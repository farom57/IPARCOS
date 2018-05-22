package farom.iparcos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import farom.iparcos.prop.PropUpdater;
import laazotea.indi.Constants;
import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIDeviceListener;
import laazotea.indi.client.INDINumberElement;
import laazotea.indi.client.INDINumberProperty;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIPropertyListener;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;
import laazotea.indi.client.INDISwitchElement;
import laazotea.indi.client.INDISwitchProperty;
import laazotea.indi.client.INDIValueException;

/**
 * This fragment shows directional buttons to move a focuser. It also provides
 * buttons to change speed.
 *
 * @author SquareBoot
 */
public class FocuserFragment extends Fragment implements INDIServerConnectionListener, INDIPropertyListener,
        INDIDeviceListener, View.OnClickListener {

    private static final double INCREMENT_VALUE = 500.0;

    // Properties and elements associated to the buttons
    private INDISwitchProperty directionProp = null;
    private INDISwitchElement inwardDirElem = null;
    private INDISwitchElement outwardDirElem = null;
    private INDINumberProperty relPosProp = null;
    private INDINumberElement relPosElem = null;
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
    private TextView speedText = null;

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
        inButton.setOnClickListener(this);
        outButton.setOnClickListener(this);
        speedUpButton.setOnClickListener(this);
        speedDownButton.setOnClickListener(this);
        abortButton.setOnClickListener(this);
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
                    speed = Integer.valueOf(s.toString());

                } catch (NumberFormatException ignored) {

                }
            }
        });

        // Set up INDI connection
        connectionManager = Application.getConnectionManager();
        connectionManager.addListener(this);

        // Enumerate existing properties
        INDIServerConnection connection = connectionManager.getConnection();
        if (connection != null) {
            List<INDIDevice> list = connection.getDevicesAsList();
            if (list != null) {
                for (INDIDevice device : list) {
                    device.addINDIDeviceListener(this);
                    List<INDIProperty> properties = device.getPropertiesAsList();
                    for (INDIProperty property : properties) {
                        this.newProperty(device, property);
                    }
                }
            }
        }

        // Update UI
        updateBtnState();
        updateSpeedText();

        return rootView;
    }

    // ------ Listener functions from INDI ------

    @Override
    public void connectionLost(INDIServerConnection arg0) {
        directionProp = null;
        relPosProp = null;
        relPosElem = null;
        outwardDirElem = null;
        inwardDirElem = null;
        updateBtnState();
        updateSpeedText();
        // Move to the connection tab
        Application.goToConnectionTab();
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
    public void newMessage(INDIServerConnection arg0, Date arg1, String arg2) {

    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty property) {
        // Look for certain properties
        switch (property.getName()) {
            case "REL_FOCUS_POSITION": {
                if ((relPosElem =
                        (INDINumberElement) property.getElement("FOCUS_RELATIVE_POSITION")) != null) {
                    relPosProp = (INDINumberProperty) property;

                } else {
                    return;
                }
                break;
            }

            case "FOCUS_MOTION": {
                if (((inwardDirElem = (INDISwitchElement) property.getElement("FOCUS_INWARD")) != null)
                        && ((outwardDirElem = (INDISwitchElement) property.getElement("FOCUS_OUTWARD")) != null)) {
                    directionProp = (INDISwitchProperty) property;

                } else {
                    return;
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
        Log.d("FocusFragment", "New Property (" + property.getName() + ") added to device " + device.getName());
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty property) {
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
            }

            default: {
                return;
            }
        }
        updateBtnState();
        updateSpeedText();
        Log.d("MotionFragment", "Removed property (" + property.getName() + ") to device " + device.getName());
    }

    @Override
    public void propertyChanged(final INDIProperty property) {
        Log.d("FocusFragment",
                "Changed property (" + property.getName() + "), new value" + property.getValuesAsString());
        switch (property.getName()) {
            case "FOCUS_MOTION": {
                if (inButton != null) {
                    inButton.post(new Runnable() {
                        public void run() {
                            inButton.setPressed(inwardDirElem.getValue() == Constants.SwitchStatus.ON);
                        }
                    });
                }
                if (outButton != null) {
                    outButton.post(new Runnable() {
                        public void run() {
                            outButton.setPressed(outwardDirElem.getValue() == Constants.SwitchStatus.ON);
                        }
                    });
                }
                break;
            }

            case "FOCUS_ABORT_MOTION": {
                if (abortButton != null) {
                    abortButton.post(new Runnable() {
                        public void run() {
                            abortButton.setPressed(
                                    outwardDirElem.getValue() == Constants.SwitchStatus.ON);
                        }
                    });
                }
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
            inButton.post(new Runnable() {
                public void run() {
                    inButton.setEnabled(inwardDirElem != null);
                }
            });
        }
        if (outButton != null) {
            outButton.post(new Runnable() {
                public void run() {
                    outButton.setEnabled(outwardDirElem != null);
                }
            });
        }
        if (speedUpButton != null) {
            speedUpButton.post(new Runnable() {
                public void run() {
                    speedUpButton.setEnabled(relPosElem != null);
                }
            });
        }
        if (speedDownButton != null) {
            speedDownButton.post(new Runnable() {
                public void run() {
                    speedDownButton.setEnabled(relPosElem != null);
                }
            });
        }
        if (abortButton != null) {
            abortButton.post(new Runnable() {
                public void run() {
                    abortButton.setEnabled(abortElem != null);
                }
            });
        }
    }

    /**
     * Updates the speed text
     */
    public void updateSpeedText() {
        if (speedText != null) {
            speedText.post(new Runnable() {
                @Override
                public void run() {
                    if (relPosElem != null) {
                        speed = (int) (double) relPosElem.getValue();
                        speedText.setText(String.valueOf(speed));

                    } else {
                        speedText.setText(R.string.default_speed);
                        speed = 0;
                    }
                }
            });
        }
    }

    /**
     * Called when one of the stop, speed up and speed down buttons is clicked.
     * Sends the corresponding order to the driver.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.focus_in: {
                try {
                    inwardDirElem.setDesiredValue(Constants.SwitchStatus.ON);
                    outwardDirElem.setDesiredValue(Constants.SwitchStatus.OFF);
                    new PropUpdater().execute(directionProp);
                    relPosElem.setDesiredValue((double) speed);
                    Log.d("FocusFragment", String.valueOf(speed));
                    new PropUpdater().execute(relPosProp);

                } catch (INDIValueException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                break;
            }

            case R.id.focus_out: {
                try {
                    outwardDirElem.setDesiredValue(Constants.SwitchStatus.ON);
                    inwardDirElem.setDesiredValue(Constants.SwitchStatus.OFF);
                    new PropUpdater().execute(directionProp);
                    relPosElem.setDesiredValue((double) speed);
                    Log.d("FocusFragment", String.valueOf(speed));
                    new PropUpdater().execute(relPosProp);

                } catch (INDIValueException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                break;
            }

            case R.id.focuser_abort: {
                try {
                    if (abortElem != null) {
                        abortElem.setDesiredValue(Constants.SwitchStatus.ON);
                        new PropUpdater().execute(abortProp);
                    }

                } catch (INDIValueException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                break;
            }

            case R.id.focuser_faster: {
                if (relPosElem != null) {
                    speedText.setText(String.valueOf(speed = (int) Math.min(relPosElem.getMax(), speed + INCREMENT_VALUE)));
                }
                break;
            }

            case R.id.focuser_slower: {
                if (relPosElem != null) {
                    speedText.setText(String.valueOf(speed = (int) Math.max(relPosElem.getMin(), speed - INCREMENT_VALUE)));
                }
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionManager.removeListener(this);
    }
}