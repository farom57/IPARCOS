package marcocipriani01.iparcos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.indilib.i4j.Constants;
import org.indilib.i4j.client.INDIDevice;
import org.indilib.i4j.client.INDIDeviceListener;
import org.indilib.i4j.client.INDINumberElement;
import org.indilib.i4j.client.INDINumberProperty;
import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDIPropertyListener;
import org.indilib.i4j.client.INDIServerConnection;
import org.indilib.i4j.client.INDIServerConnectionListener;
import org.indilib.i4j.client.INDISwitchElement;
import org.indilib.i4j.client.INDISwitchProperty;
import org.indilib.i4j.client.INDIValueException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import marcocipriani01.iparcos.prop.PropUpdater;

/**
 * This fragment shows directional buttons to move a focuser. It also provides
 * buttons to change speed.
 *
 * @author marcocipriani01
 */
public class FocuserFragment extends Fragment implements INDIServerConnectionListener, INDIPropertyListener, INDIDeviceListener, View.OnClickListener, CounterHandler.CounterListener {

    private static final int INCREMENT_VALUE = 10;

    // Properties and elements associated to the buttons
    private INDISwitchProperty directionProp = null;
    private INDISwitchElement inwardDirElem = null;
    private INDISwitchElement outwardDirElem = null;
    private INDINumberProperty relPosProp = null;
    private INDINumberElement relPosElem = null;
    private INDINumberProperty absPosProp = null;
    private INDINumberElement absPosElem = null;
    private int speed = 0;
    private INDISwitchProperty abortProp = null;
    private INDISwitchElement abortElem = null;
    private ConnectionManager connectionManager;
    // Views
    private Button inButton = null;
    private Button outButton = null;
    private Button speedUpButton = null;
    private Button speedDownButton = null;
    private Button abortButton = null;
    private Button setAbsPosButton = null;
    private TextView speedText = null;
    private TextView absPosText = null;
    private CounterHandler speedHandler;

    @Override
    public void onStart() {
        super.onStart();
        if (connectionManager.isConnected()) {
            List<INDIDevice> list = connectionManager.getConnection().getDevicesAsList();
            if (list != null) {
                for (INDIDevice device : list) {
                    device.addINDIDeviceListener(this);
                    List<INDIProperty<?>> properties = device.getPropertiesAsList();
                    for (INDIProperty<?> property : properties) {
                        newProperty(device, property);
                    }
                }
            }

        } else {
            clearVars();
        }
        // Update UI
        updateBtnState();
        updateSpeedText();
        updateAbsPosText();
    }

    private void clearVars() {
        relPosProp = null;
        relPosElem = null;
        absPosProp = null;
        absPosElem = null;
        directionProp = null;
        outwardDirElem = null;
        inwardDirElem = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_focuser, container, false);
        // Set up the UI
        inButton = rootView.findViewById(R.id.focus_in);
        outButton = rootView.findViewById(R.id.focus_out);
        speedUpButton = rootView.findViewById(R.id.focuser_faster);
        speedDownButton = rootView.findViewById(R.id.focuser_slower);
        speedText = rootView.findViewById(R.id.focuser_steps_box);
        abortButton = rootView.findViewById(R.id.focuser_abort);
        setAbsPosButton = rootView.findViewById(R.id.abs_pos_button);
        absPosText = rootView.findViewById(R.id.abs_pos_field);
        new CounterHandler(outButton, inButton, -1, -1, 0, 1, 150, false, this, false);
        speedHandler = new CounterHandler(speedUpButton, speedDownButton, 0, 10, 0,
                INCREMENT_VALUE, 100, false, this);
        abortButton.setOnClickListener(this);
        setAbsPosButton.setOnClickListener(this);
        speedText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    speed = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {
                }
            }
        });

        // Set up INDI connection
        connectionManager = IPARCOSApp.getConnectionManager();
        connectionManager.addListener(this);

        return rootView;
    }

    // ------ Listener functions from INDI ------

    @Override
    public void connectionLost(INDIServerConnection connection) {
        clearVars();
        updateBtnState();
        updateSpeedText();
        updateAbsPosText();
        // Move to the connection tab
        IPARCOSApp.goToConnectionTab();
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        Log.i("FocusFragment", "New device: " + device.getName());
        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        Log.i("FocusFragment", "Device removed: " + device.getName());
        device.removeINDIDeviceListener(this);
    }

    @Override
    public void newMessage(INDIServerConnection connection, Date timestamp, String message) {

    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty<?> property) {
        // Look for certain properties
        switch (property.getName()) {
            case "ABS_FOCUS_POSITION": {
                if ((absPosElem = (INDINumberElement) property.getElement("FOCUS_ABSOLUTE_POSITION")) != null) {
                    absPosProp = (INDINumberProperty) property;
                }
                break;
            }

            case "REL_FOCUS_POSITION": {
                if ((relPosElem = (INDINumberElement) property.getElement("FOCUS_RELATIVE_POSITION")) != null) {
                    relPosProp = (INDINumberProperty) property;
                    speedHandler.setMaxValue((int) relPosElem.getMax());
                    speedHandler.setMinValue((int) relPosElem.getMin());
                }
                break;
            }

            case "FOCUS_MOTION": {
                if (((inwardDirElem = (INDISwitchElement) property.getElement("FOCUS_INWARD")) != null)
                        && ((outwardDirElem = (INDISwitchElement) property.getElement("FOCUS_OUTWARD")) != null)) {
                    directionProp = (INDISwitchProperty) property;
                }
                break;
            }

            case "FOCUS_ABORT_MOTION": {
                if ((abortElem = (INDISwitchElement) property.getElement("ABORT")) != null) {
                    abortProp = (INDISwitchProperty) property;
                }
                break;
            }

            default: {
                return;
            }
        }
        property.addINDIPropertyListener(this);
        updateBtnState();
        Log.d("FocusFragment", "New Property (" + property.getName() + ") added to device " + device.getName()
                + ", elements: " + Arrays.toString(property.getElementNames()));
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty<?> property) {
        switch (property.getName()) {
            case "REL_FOCUS_POSITION": {
                relPosElem = null;
                relPosProp = null;
                break;
            }

            case "FOCUS_MOTION": {
                inwardDirElem = null;
                outwardDirElem = null;
                directionProp = null;
                break;
            }

            case "FOCUS_ABORT_MOTION": {
                abortElem = null;
                abortProp = null;
                break;
            }

            case "ABS_FOCUS_POSITION": {
                absPosProp = null;
                absPosElem = null;
                break;
            }

            default: {
                return;
            }
        }
        updateBtnState();
        updateSpeedText();
        updateAbsPosText();
        Log.d("MotionFragment", "Removed property (" + property.getName() + ") to device " + device.getName());
    }

    @Override
    public void propertyChanged(final INDIProperty<?> property) {
        Log.d("FocusFragment",
                "Changed property (" + property.getName() + "), new value" + property.getValuesAsString());
        switch (property.getName()) {
            case "ABS_FOCUS_POSITION": {
                updateAbsPosText();
                break;
            }

            case "FOCUS_ABORT_MOTION": {
                if (abortButton != null) {
                    abortButton.post(() -> abortButton.setPressed(outwardDirElem.getValue() == Constants.SwitchStatus.ON));
                }
                break;
            }

            case "REL_FOCUS_POSITION": {
                updateSpeedText();
                break;
            }
        }
    }

    @Override
    public void messageChanged(INDIDevice device) {

    }

    // ------ UI functions ------

    /**
     * Enables the buttons if the corresponding property was found
     */
    public void updateBtnState() {
        if (inButton != null) {
            inButton.post(() -> inButton.setEnabled(inwardDirElem != null));
        }
        if (outButton != null) {
            outButton.post(() -> outButton.setEnabled(outwardDirElem != null));
        }
        if (speedUpButton != null) {
            speedUpButton.post(() -> speedUpButton.setEnabled(relPosElem != null));
        }
        if (speedDownButton != null) {
            speedDownButton.post(() -> speedDownButton.setEnabled(relPosElem != null));
        }
        if (speedText != null) {
            speedText.post(() -> speedText.setFocusableInTouchMode(relPosElem != null));
        }
        if (abortButton != null) {
            abortButton.post(() -> abortButton.setEnabled(abortElem != null));
        }
        if (setAbsPosButton != null) {
            setAbsPosButton.post(() -> setAbsPosButton.setEnabled(absPosElem != null));
        }
        if (absPosText != null) {
            absPosText.post(() -> absPosText.setFocusableInTouchMode(absPosElem != null));
        }
    }

    /**
     * Updates the speed text
     */
    public void updateSpeedText() {
        if (speedText != null) {
            speedText.post(() -> {
                if (relPosElem != null) {
                    speed = (int) (double) relPosElem.getValue();
                    speedText.setText(String.valueOf(speed));

                } else {
                    speedText.setText(R.string.default_speed);
                }
            });
        }
    }

    public void updateAbsPosText() {
        if (absPosText != null) {
            absPosText.post(() -> {
                if (absPosElem != null) {
                    absPosText.setText(String.valueOf((int) (double) absPosElem.getValue()));

                } else {
                    absPosText.setText(R.string.default_speed);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionManager.removeListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.focuser_abort) {
            try {
                if (abortElem != null) {
                    abortElem.setDesiredValue(Constants.SwitchStatus.ON);
                    new PropUpdater().execute(abortProp);
                }

            } catch (INDIValueException e) {
                Log.e("FocusFragment", e.getLocalizedMessage());
            }
        } else if (id == R.id.abs_pos_button) {
            try {
                if (absPosElem != null && absPosText != null) {
                    try {
                        absPosElem.setDesiredValue(Double.parseDouble(absPosText.getText().toString()));
                        new PropUpdater().execute(absPosProp);

                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Invalid absolute position!", Toast.LENGTH_SHORT).show();
                        updateAbsPosText();
                    }
                }

            } catch (INDIValueException e) {
                Log.e("FocusFragment", e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onIncrement(View view, int number) {
        int id = view.getId();
        if (id == R.id.focuser_faster) {
            speed = number;
            speedText.setText(String.valueOf(speed));
        } else if (id == R.id.focus_out) {
            if (outwardDirElem != null && inwardDirElem != null && relPosElem != null) {
                try {
                    outwardDirElem.setDesiredValue(Constants.SwitchStatus.ON);
                    inwardDirElem.setDesiredValue(Constants.SwitchStatus.OFF);
                    new PropUpdater().execute(directionProp);
                    relPosElem.setDesiredValue((double) speed);
                    new PropUpdater().execute(relPosProp);

                } catch (INDIValueException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public void onDecrement(View view, int number) {
        int id = view.getId();
        if (id == R.id.focuser_slower) {
            speed = number;
            speedText.setText(String.valueOf(speed));
        } else if (id == R.id.focus_in) {
            if (inwardDirElem != null && outwardDirElem != null && relPosElem != null) {
                try {
                    inwardDirElem.setDesiredValue(Constants.SwitchStatus.ON);
                    outwardDirElem.setDesiredValue(Constants.SwitchStatus.OFF);
                    new PropUpdater().execute(directionProp);
                    relPosElem.setDesiredValue((double) speed);
                    new PropUpdater().execute(relPosProp);

                } catch (INDIValueException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
            }
        }
    }
}