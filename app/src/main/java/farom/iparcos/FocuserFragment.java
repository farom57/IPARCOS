package farom.iparcos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import laazotea.indi.Constants;
import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIDeviceListener;
import laazotea.indi.client.INDIElement;
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
        INDIDeviceListener, View.OnTouchListener, View.OnClickListener {

    // Properties and elements associated to the buttons
    private INDISwitchProperty focuserDirection = null;
    private INDINumberProperty focuserRelativePosition = null;
    private ConnectionManager connectionManager;

    // Views
    private Button focusIn = null;
    private Button focusOut = null;
    private Button btnSpeedUp = null;
    private Button btnSpeedDown = null;
    private TextView speedText = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_focuser, container, false);

        // Set up the UI
        focusIn = rootView.findViewById(R.id.focus_in);
        focusOut = rootView.findViewById(R.id.focus_out);
        btnSpeedUp = rootView.findViewById(R.id.focus_interval_more);
        btnSpeedDown = rootView.findViewById(R.id.focus_interval_less);
        speedText = rootView.findViewById(R.id.focus_movement_increment);
        focusIn.setOnTouchListener(this);
        focusOut.setOnTouchListener(this);
        btnSpeedUp.setOnClickListener(this);
        btnSpeedDown.setOnClickListener(this);

        // Set up INDI connection
        connectionManager = Application.getConnectionManager();
        connectionManager.registerPermanentConnectionListener(this);

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
        updateBtnState();
        updateSpeedText();
        // Move to the connection tab
        Application.goToConnectionTab();
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        // We just simply listen to this Device
        Log.i("FocusFragment", getString(R.string.new_device) + device.getName());
        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        // We just remove ourselves as a listener of the removed device
        Log.i("FocusFragment", getString(R.string.device_removed) + device.getName());
        device.removeINDIDeviceListener(this);
    }

    @Override
    public void newMessage(INDIServerConnection arg0, Date arg1, String arg2) {

    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty property) {
        // Look for certain properties
        /*if (property.getName().equals("TELESCOPE_MOTION_NS")) {
            if (((telescopeMotionNE = (INDISwitchElement) property.getElement("MOTION_NORTH")) != null)
                    && ((telescopeMotionSE = (INDISwitchElement) property.getElement("MOTION_SOUTH")) != null)) {
                property.addINDIPropertyListener(this);
                telescopeMotionNSP = (INDISwitchProperty) property;
                Log.i("FocusFragment",
                        "--New Property (" + property.getName() + ") added to device " + device.getName());
                updateBtnState();
            }
        }*/
        Log.d("FocusFragment", "New Property (" + property.getName() + ") added to device " + device.getName());
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty property) {
        /*if (property.getName().equals("TELESCOPE_MOTION_NS")) {
            telescopeMotionNSP = null;
            telescopeMotionNE = null;
            telescopeMotionSE = null;
        }*/

        updateBtnState();
        updateSpeedText();

        Log.d("FocusFragment", "Removed property (" + property.getName() + ") to device " + device.getName());
    }

    @Override
    public void propertyChanged(final INDIProperty property) {
        Log.d("FocusFragment", "Changed property (" + property.getName() + "), new value" + property.getValuesAsString());
        /*if (property.getName().equals("TELESCOPE_MOTION_NS")) {
            if (btnMoveN != null) {
                btnMoveN.post(new Runnable() {
                    public void run() {
                        btnMoveN.setPressed(telescopeMotionNE.getValue() == Constants.SwitchStatus.ON);
                    }
                });
            }
            if (btnMoveS != null) {
                btnMoveS.post(new Runnable() {
                    public void run() {
                        btnMoveS.setPressed(telescopeMotionSE.getValue() == Constants.SwitchStatus.ON);
                    }
                });
            }
        }
        if (property.getName().equals("TELESCOPE_MOTION_WE")) {
            if (btnMoveE != null) {
                btnMoveE.post(new Runnable() {
                    public void run() {
                        btnMoveE.setPressed(telescopeMotionEE.getValue() == Constants.SwitchStatus.ON);
                    }
                });
            }
            if (btnMoveW != null) {
                btnMoveW.post(new Runnable() {
                    public void run() {
                        btnMoveW.setPressed(telescopeMotionWE.getValue() == Constants.SwitchStatus.ON);
                    }
                });
            }
        }
        if (property.getName().equals("TELESCOPE_MOTION_RATE") || property.getName().equals("Slew Rate")
                || property.getName().equals("SLEWMODE")) {
            updateSpeedText();
        }*/
    }

    @Override
    public void messageChanged(INDIDevice device) {

    }

    // ------ UI functions ------

    /**
     * Enables the buttons if the corresponding property was found
     */
    public void updateBtnState() {
        /*if (focusIn != null) {
            focusIn.post(new Runnable() {
                public void run() {
                    focusIn.setEnabled( != null);
                }
            });
        }
        if (focusOut != null) {
            focusOut.post(new Runnable() {
                public void run() {
                    focusOut.setEnabled( != null);
                }
            });
        }
        if (btnSpeedUp != null) {
            btnSpeedUp.post(new Runnable() {
                public void run() {
                    btnSpeedUp.setEnabled( != null);
                }
            });
        }
        if (btnSpeedDown != null) {
            btnSpeedDown.post(new Runnable() {
                public void run() {
                    btnSpeedDown.setEnabled( != null);
                }
            });
        }*/
    }

    /**
     * Updates the speed text
     */
    public void updateSpeedText() {
        /*if (speedText != null) {
            speedText.post(new Runnable() {
                @Override
                public void run() {
                    if (telescopeMotionRate != null) {
                        double speed = telescopeMotionRate.getElement("MOTION_RATE").getValue();
                        speedText.setText(String.format("%3.1fx (%3.1f '/s)", speed / 0.25, speed));

                    } else if (telescopeMotionRateLX200 != null) {
                        ArrayList<INDIElement> elements = telescopeMotionRateLX200.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                                && i < elements.size() - 1) {
                            i++;
                        }
                        speedText.setText(elements.get(i).getLabel());

                    } else if (telescopeMotionRateEQMod != null) {
                        ArrayList<INDIElement> elements = telescopeMotionRateEQMod.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                                && i < elements.size() - 1) {
                            i++;
                        }
                        speedText.setText(elements.get(i).getLabel());

                    } else {
                        speedText.setText(R.string.default_speed);
                    }
                }
            });
        }*/
    }

    /**
     * Called when a directional button is pressed or released. Send the
     * corresponding order to the driver.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /*Constants.SwitchStatus status, negStatus;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            status = Constants.SwitchStatus.ON;
            negStatus = Constants.SwitchStatus.OFF;
            // log("button pressed");
            // v.setPressed(true);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            status = Constants.SwitchStatus.OFF;
            negStatus = Constants.SwitchStatus.OFF;
            // log("button released");
            // v.setPressed(false);

        } else {
            return true;
        }

        switch (v.getId()) {
            case R.id.buttonE: {
                try {
                    telescopeMotionEE.setDesiredValue(status);
                    telescopeMotionWE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();

                } catch (INDIValueException | IOException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                return true;

            }

            case R.id.buttonW: {
                try {
                    telescopeMotionWE.setDesiredValue(status);
                    telescopeMotionEE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();

                } catch (INDIValueException | IOException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                return true;
            }

            case R.id.buttonN: {
                try {
                    telescopeMotionNE.setDesiredValue(status);
                    telescopeMotionSE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();

                } catch (INDIValueException | IOException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                return true;
            }

            case R.id.buttonS: {
                try {
                    telescopeMotionSE.setDesiredValue(status);
                    telescopeMotionNE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();

                } catch (INDIValueException | IOException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                return true;
            }

            case R.id.buttonNE: {
                try {
                    telescopeMotionEE.setDesiredValue(status);
                    telescopeMotionWE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();
                    telescopeMotionNE.setDesiredValue(status);
                    telescopeMotionSE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();

                } catch (INDIValueException | IOException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                return true;
            }

            case R.id.buttonNW: {
                try {
                    telescopeMotionWE.setDesiredValue(status);
                    telescopeMotionEE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();
                    telescopeMotionNE.setDesiredValue(status);
                    telescopeMotionSE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();

                } catch (INDIValueException | IOException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                return true;
            }

            case R.id.buttonSE: {
                try {
                    telescopeMotionEE.setDesiredValue(status);
                    telescopeMotionWE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();
                    telescopeMotionSE.setDesiredValue(status);
                    telescopeMotionNE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();

                } catch (INDIValueException | IOException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                return true;
            }

            case R.id.buttonSW: {
                try {
                    telescopeMotionWE.setDesiredValue(status);
                    telescopeMotionEE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();
                    telescopeMotionSE.setDesiredValue(status);
                    telescopeMotionNE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();

                } catch (INDIValueException | IOException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                return true;
            }

            default: {
                Log.e("FocusFragment", "Unknown view");
            }
        }*/
        return false;
    }

    /**
     * Called when one of the stop, speed up and speed down buttons is clicked.
     * Sends the corresponding order to the driver.
     */
    @Override
    public void onClick(View v) {/*
        switch (v.getId()) {
            case R.id.buttonStop: {
                try {
                    if (telescopeMotionWEP != null) {
                        telescopeMotionWE.setDesiredValue(Constants.SwitchStatus.OFF);
                        telescopeMotionEE.setDesiredValue(Constants.SwitchStatus.OFF);
                        telescopeMotionWEP.sendChangesToDriver();
                    }
                    if (telescopeMotionNSP != null) {
                        telescopeMotionSE.setDesiredValue(Constants.SwitchStatus.OFF);
                        telescopeMotionNE.setDesiredValue(Constants.SwitchStatus.OFF);
                        telescopeMotionNSP.sendChangesToDriver();
                    }
                    if (telescopeMotionAbort != null) {
                        telescopeMotionAbortE.setDesiredValue(Constants.SwitchStatus.ON);
                        telescopeMotionAbort.sendChangesToDriver();
                    }

                } catch (INDIValueException | IOException e) {
                    Log.e("FocusFragment", e.getLocalizedMessage());
                }
                break;
            }

            case R.id.buttonSpeedUp: {
                if (telescopeMotionRate != null) {
                    try {
                        double speed = telescopeMotionRate.getElement("MOTION_RATE").getValue();
                        double maxSpeed = telescopeMotionRate.getElement("MOTION_RATE").getMax();
                        speed = Math.min(maxSpeed, speed * 2);
                        telescopeMotionRate.getElement("MOTION_RATE").setDesiredValue(speed);
                        telescopeMotionRate.sendChangesToDriver();

                    } catch (INDIValueException | IOException e) {
                        Log.e("FocusFragment", e.getLocalizedMessage());
                    }

                } else if (telescopeMotionRateEQMod != null) {
                    try {
                        ArrayList<INDIElement> elements = telescopeMotionRateEQMod.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                                && i < elements.size() - 2) {
                            i++;
                        }
                        elements.get(i + 1).setDesiredValue(Constants.SwitchStatus.ON);
                        telescopeMotionRateEQMod.sendChangesToDriver();

                    } catch (INDIValueException | IOException e) {
                        Log.e("FocusFragment", e.getLocalizedMessage());
                    }

                } else if (telescopeMotionRateLX200 != null) {
                    try {
                        ArrayList<INDIElement> elements = telescopeMotionRateLX200.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                                && i < elements.size() - 1) {
                            i++;
                        }
                        if (i > 0) {
                            elements.get(i - 1).setDesiredValue(Constants.SwitchStatus.ON);
                        }
                        telescopeMotionRateLX200.sendChangesToDriver();

                    } catch (INDIValueException | IOException e) {
                        Log.e("FocusFragment", e.getLocalizedMessage());
                    }
                }
                break;
            }

            case R.id.buttonSpeedDown: {
                if (telescopeMotionRate != null) {
                    try {
                        double speed = telescopeMotionRate.getElement("MOTION_RATE").getValue();
                        double minSpeed = telescopeMotionRate.getElement("MOTION_RATE").getMin();
                        speed = Math.max(minSpeed, speed * 0.5);
                        telescopeMotionRate.getElement("MOTION_RATE").setDesiredValue(speed);
                        telescopeMotionRate.sendChangesToDriver();

                    } catch (INDIValueException | IOException e) {
                        Log.e("FocusFragment", e.getLocalizedMessage());
                    }

                } else if (telescopeMotionRateEQMod != null) {
                    try {
                        ArrayList<INDIElement> elements = telescopeMotionRateEQMod.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                                && i < elements.size() - 1) {
                            i++;
                        }
                        if (i > 0) {
                            elements.get(i - 1).setDesiredValue(Constants.SwitchStatus.ON);
                        }
                        telescopeMotionRateEQMod.sendChangesToDriver();

                    } catch (INDIValueException | IOException e) {
                        Log.e("FocusFragment", e.getLocalizedMessage());
                    }

                } else if (telescopeMotionRateLX200 != null) {
                    try {
                        ArrayList<INDIElement> elements = telescopeMotionRateLX200.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                                && i < elements.size() - 2) {
                            i++;
                        }
                        elements.get(i + 1).setDesiredValue(Constants.SwitchStatus.ON);
                        telescopeMotionRateLX200.sendChangesToDriver();

                    } catch (INDIValueException | IOException e) {
                        Log.e("FocusFragment", e.getLocalizedMessage());
                    }
                }
                break;
            }

            default: {
                Log.e("FocusFragment", "unknown view");
            }
        }
*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionManager.unRegisterPermanentConnectionListener(this);
    }
}