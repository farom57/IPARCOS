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
    private INDISwitchProperty focuserDirection = null;
    private INDISwitchElement inwardDirectionElement = null;
    private INDISwitchElement outwardDirectionElement = null;
    private INDINumberProperty focuserRelativePosition = null;
    private INDINumberElement focuserRelPosElement = null;
    private int speed = 0;
    private INDISwitchProperty abortProp = null;
    private INDISwitchElement abortElement = null;
    private ConnectionManager connectionManager;

    // Views
    private Button focusIn = null;
    private Button focusOut = null;
    private Button btnSpeedUp = null;
    private Button btnSpeedDown = null;
    private Button stopFocuserButton = null;
    private TextView speedText = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_focuser, container, false);

        // Set up the UI
        focusIn = rootView.findViewById(R.id.focus_in);
        focusOut = rootView.findViewById(R.id.focus_out);
        btnSpeedUp = rootView.findViewById(R.id.focuser_faster);
        btnSpeedDown = rootView.findViewById(R.id.focuser_slower);
        speedText = rootView.findViewById(R.id.focus_movement_increment);
        stopFocuserButton = rootView.findViewById(R.id.stop_focuser);
        focusIn.setOnClickListener(this);
        focusOut.setOnClickListener(this);
        btnSpeedUp.setOnClickListener(this);
        btnSpeedDown.setOnClickListener(this);
        stopFocuserButton.setOnClickListener(this);
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
        focuserDirection = null;
        focuserRelativePosition = null;
        focuserRelPosElement = null;
        outwardDirectionElement = null;
        inwardDirectionElement = null;
        updateBtnState();
        updateSpeedText();
        // Move to the connection tab
        Application.goToConnectionTab();
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        // We just simply listen to this Device
        Log.i("FocusFragment", "New device: " + device.getName());
        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        // We just remove ourselves as a listener of the removed device
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
                if ((focuserRelPosElement =
                        (INDINumberElement) property.getElement("FOCUS_RELATIVE_POSITION")) != null) {
                    focuserRelativePosition = (INDINumberProperty) property;

                } else {
                    return;
                }
                break;
            }

            case "FOCUS_MOTION": {
                if (((inwardDirectionElement = (INDISwitchElement) property.getElement("FOCUS_INWARD")) != null)
                        && ((outwardDirectionElement = (INDISwitchElement) property.getElement("FOCUS_OUTWARD")) != null)) {
                    focuserDirection = (INDISwitchProperty) property;

                } else {
                    return;
                }
                break;
            }

            case "FOCUS_ABORT_MOTION": {
                if ((abortElement = (INDISwitchElement) property.getElement("ABORT")) != null) {
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
                focuserRelPosElement = null;
                focuserRelativePosition = null;
                break;
            }

            case "FOCUS_MOTION": {
                inwardDirectionElement = null;
                outwardDirectionElement = null;
                focuserDirection = null;
                break;
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
                if (focusIn != null) {
                    focusIn.post(new Runnable() {
                        public void run() {
                            focusIn.setPressed(inwardDirectionElement.getValue() == Constants.SwitchStatus.ON);
                        }
                    });
                }
                if (focusOut != null) {
                    focusOut.post(new Runnable() {
                        public void run() {
                            focusOut.setPressed(outwardDirectionElement.getValue() == Constants.SwitchStatus.ON);
                        }
                    });
                }
                break;
            }

            case "FOCUS_ABORT_MOTION": {
                if (stopFocuserButton != null) {
                    stopFocuserButton.post(new Runnable() {
                        public void run() {
                            stopFocuserButton.setPressed(
                                    outwardDirectionElement.getValue() == Constants.SwitchStatus.ON);
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
        if (focusIn != null) {
            focusIn.post(new Runnable() {
                public void run() {
                    focusIn.setEnabled(inwardDirectionElement != null);
                }
            });
        }
        if (focusOut != null) {
            focusOut.post(new Runnable() {
                public void run() {
                    focusOut.setEnabled(outwardDirectionElement != null);
                }
            });
        }
        if (btnSpeedUp != null) {
            btnSpeedUp.post(new Runnable() {
                public void run() {
                    btnSpeedUp.setEnabled(focuserRelPosElement != null);
                }
            });
        }
        if (btnSpeedDown != null) {
            btnSpeedDown.post(new Runnable() {
                public void run() {
                    btnSpeedDown.setEnabled(focuserRelPosElement != null);
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
                    if (focuserRelPosElement != null) {
                        speed = (int) (double) focuserRelPosElement.getValue();
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
                    inwardDirectionElement.setDesiredValue(Constants.SwitchStatus.ON);
                    outwardDirectionElement.setDesiredValue(Constants.SwitchStatus.OFF);
                    new PropUpdater().execute(focuserDirection);
                    focuserRelPosElement.setDesiredValue((double) speed);
                    Log.d("FocusFragment", String.valueOf(speed));
                    new PropUpdater().execute(focuserRelativePosition);

                } catch (INDIValueException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                break;
            }

            case R.id.focus_out: {
                try {
                    outwardDirectionElement.setDesiredValue(Constants.SwitchStatus.ON);
                    inwardDirectionElement.setDesiredValue(Constants.SwitchStatus.OFF);
                    new PropUpdater().execute(focuserDirection);
                    focuserRelPosElement.setDesiredValue((double) speed);
                    Log.d("FocusFragment", String.valueOf(speed));
                    new PropUpdater().execute(focuserRelativePosition);

                } catch (INDIValueException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                break;
            }

            case R.id.stop_focuser: {
                try {
                    if (abortElement != null) {
                        abortElement.setDesiredValue(Constants.SwitchStatus.ON);
                        new PropUpdater().execute(abortProp);
                    }

                } catch (INDIValueException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                break;
            }

            case R.id.focuser_faster: {
                if (focuserRelPosElement != null) {
                    speedText.setText(String.valueOf(
                            speed = (int) Math.min(focuserRelPosElement.getMax(), speed + INCREMENT_VALUE)));
                }
                break;
            }

            case R.id.focuser_slower: {
                if (focuserRelPosElement != null) {
                    speedText.setText(String.valueOf(
                            speed = (int) Math.max(focuserRelPosElement.getMin(), speed - INCREMENT_VALUE)));
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