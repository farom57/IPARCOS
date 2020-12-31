package org.indilib.i4j.iparcos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.indilib.i4j.iparcos.prop.PropUpdater;

/**
 * This fragment shows directional buttons to move a telescope. It also provides
 * buttons to change speed. To activate the buttons, the driver must provide the
 * following properties:
 * {@code TELESCOPE_MOTION_NS}, {@code TELESCOPE_MOTION_WE}, {@code TELESCOPE_ABORT_MOTION}, {@code TELESCOPE_MOTION_RATE}
 *
 * @author Romain Fafet
 */
public class MountControlFragment extends Fragment implements INDIServerConnectionListener, INDIPropertyListener,
        INDIDeviceListener, OnTouchListener, OnClickListener {

    // Properties and elements associated to the buttons
    private INDISwitchProperty telescopeMotionNSP = null;
    private INDISwitchElement telescopeMotionNE = null;
    private INDISwitchElement telescopeMotionSE = null;
    private INDISwitchProperty telescopeMotionWEP = null;
    private INDISwitchElement telescopeMotionWE = null;
    private INDISwitchElement telescopeMotionEE = null;
    private INDISwitchProperty telescopeMotionAbort = null;
    private INDISwitchElement telescopeMotionAbortE = null;
    private INDINumberProperty telescopeMotionRate = null;
    private INDISwitchProperty telescopeMotionRateLX200 = null;
    private INDISwitchProperty telescopeMotionRateEQMod = null;

    private ConnectionManager connectionManager;

    // Views
    private Button btnMoveN = null;
    private Button btnMoveS = null;
    private Button btnMoveE = null;
    private Button btnMoveW = null;
    private Button btnMoveNE = null;
    private Button btnMoveNW = null;
    private Button btnMoveSE = null;
    private Button btnMoveSW = null;
    private Button btnStop = null;
    private Button btnSpeedUp = null;
    private Button btnSpeedDown = null;
    private TextView speedText = null;

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
                        this.newProperty(device, property);
                    }
                }
            }

        } else {
            clearVars();
        }
        // Update UI
        updateBtnState();
        updateSpeedText();
    }

    private void clearVars() {
        telescopeMotionNSP = null;
        telescopeMotionNE = null;
        telescopeMotionSE = null;
        telescopeMotionWEP = null;
        telescopeMotionWE = null;
        telescopeMotionEE = null;
        telescopeMotionAbort = null;
        telescopeMotionAbortE = null;
        telescopeMotionRate = null;
        telescopeMotionRateEQMod = null;
        telescopeMotionRateLX200 = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_motion, container, false);
        // Set up the UI
        btnMoveN = rootView.findViewById(R.id.buttonN);
        btnMoveNE = rootView.findViewById(R.id.buttonNE);
        btnMoveE = rootView.findViewById(R.id.buttonE);
        btnMoveSE = rootView.findViewById(R.id.buttonSE);
        btnMoveS = rootView.findViewById(R.id.buttonS);
        btnMoveSW = rootView.findViewById(R.id.buttonSW);
        btnMoveW = rootView.findViewById(R.id.buttonW);
        btnMoveNW = rootView.findViewById(R.id.buttonNW);
        btnStop = rootView.findViewById(R.id.buttonStop);
        btnSpeedUp = rootView.findViewById(R.id.buttonSpeedUp);
        btnSpeedDown = rootView.findViewById(R.id.buttonSpeedDown);
        speedText = rootView.findViewById(R.id.speedText);
        btnMoveN.setOnTouchListener(this);
        btnMoveNE.setOnTouchListener(this);
        btnMoveE.setOnTouchListener(this);
        btnMoveSE.setOnTouchListener(this);
        btnMoveS.setOnTouchListener(this);
        btnMoveSW.setOnTouchListener(this);
        btnMoveW.setOnTouchListener(this);
        btnMoveNW.setOnTouchListener(this);
        btnStop.setOnClickListener(this);
        btnSpeedUp.setOnClickListener(this);
        btnSpeedDown.setOnClickListener(this);

        // Set up INDI connection
        connectionManager = IPARCOSApp.getConnectionManager();
        connectionManager.addListener(this);

        return rootView;
    }

    // ------ Listener functions from INDI ------

    @Override
    public void connectionLost(INDIServerConnection arg0) {
        clearVars();
        updateBtnState();
        updateSpeedText();
        // Move to the connection tab
        IPARCOSApp.goToConnectionTab();
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        // We just simply listen to this Device
        Log.i("MotionFragment", "New device: " + device.getName());
        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        // We just remove ourselves as a listener of the removed device
        Log.i("MotionFragment", "Device removed: " + device.getName());
        device.removeINDIDeviceListener(this);
    }

    @Override
    public void newMessage(INDIServerConnection arg0, Date arg1, String arg2) {

    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty<?> property) {
        // Look for certain properties
        switch (property.getName()) {
            case "TELESCOPE_MOTION_NS": {
                if (((telescopeMotionNE = (INDISwitchElement) property.getElement("MOTION_NORTH")) != null)
                        && ((telescopeMotionSE = (INDISwitchElement) property.getElement("MOTION_SOUTH")) != null)) {
                    telescopeMotionNSP = (INDISwitchProperty) property;

                } else {
                    return;
                }
                break;
            }

            case "TELESCOPE_MOTION_WE": {
                if (((telescopeMotionEE = (INDISwitchElement) property.getElement("MOTION_EAST")) != null)
                        && ((telescopeMotionWE = (INDISwitchElement) property.getElement("MOTION_WEST")) != null)) {
                    telescopeMotionWEP = (INDISwitchProperty) property;

                } else {
                    return;
                }
                break;
            }

            case "TELESCOPE_ABORT_MOTION": {
                if ((telescopeMotionAbortE = (INDISwitchElement) property.getElement("ABORT_MOTION")) != null) {
                    telescopeMotionAbort = (INDISwitchProperty) property;

                } else {
                    return;
                }
                break;
            }

            case "TELESCOPE_MOTION_RATE": {
                telescopeMotionRate = (INDINumberProperty) property;
                updateSpeedText();
                break;
            }

            case "Slew Rate": {
                telescopeMotionRateLX200 = (INDISwitchProperty) property;
                updateSpeedText();
                break;
            }

            case "SLEWMODE": {
                telescopeMotionRateEQMod = (INDISwitchProperty) property;
                updateSpeedText();
                break;
            }

            default: {
                return;
            }
        }
        property.addINDIPropertyListener(this);
        updateBtnState();
        Log.d("MotionFragment", "New Property (" + property.getName() + ") added to device " + device.getName());
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty<?> property) {
        switch (property.getName()) {
            case "TELESCOPE_MOTION_NS": {
                telescopeMotionNSP = null;
                telescopeMotionNE = null;
                telescopeMotionSE = null;
                break;
            }

            case "TELESCOPE_MOTION_WE": {
                telescopeMotionWEP = null;
                telescopeMotionWE = null;
                telescopeMotionEE = null;
                break;
            }

            case "TELESCOPE_ABORT_MOTION": {
                telescopeMotionAbort = null;
                telescopeMotionAbortE = null;
                break;
            }

            case "TELESCOPE_MOTION_RATE": {
                telescopeMotionRate = null;
                break;
            }

            case "Slew Rate": {
                telescopeMotionRateLX200 = null;
                break;
            }

            case "SLEWMODE": {
                telescopeMotionRateEQMod = null;
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
    public void propertyChanged(final INDIProperty<?> property) {
        switch (property.getName()) {
            case "TELESCOPE_MOTION_NS": {
                if (btnMoveN != null) {
                    btnMoveN.post(() -> btnMoveN.setPressed(telescopeMotionNE.getValue() == Constants.SwitchStatus.ON));
                }
                if (btnMoveS != null) {
                    btnMoveS.post(() -> btnMoveS.setPressed(telescopeMotionSE.getValue() == Constants.SwitchStatus.ON));
                }
                break;
            }

            case "TELESCOPE_MOTION_WE": {
                if (btnMoveE != null) {
                    btnMoveE.post(() -> btnMoveE.setPressed(telescopeMotionEE.getValue() == Constants.SwitchStatus.ON));
                }
                if (btnMoveW != null) {
                    btnMoveW.post(() -> btnMoveW.setPressed(telescopeMotionWE.getValue() == Constants.SwitchStatus.ON));
                }
                break;
            }

            case "TELESCOPE_MOTION_RATE":
            case "Slew Rate":
            case "SLEWMODE": {
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
        if (btnMoveE != null) {
            btnMoveE.post(() -> btnMoveE.setEnabled(telescopeMotionWEP != null));
        }
        if (btnMoveW != null) {
            btnMoveW.post(() -> btnMoveW.setEnabled(telescopeMotionWEP != null));
        }
        if (btnMoveN != null) {
            btnMoveN.post(() -> btnMoveN.setEnabled(telescopeMotionNSP != null));
        }
        if (btnMoveS != null) {
            btnMoveS.post(() -> btnMoveS.setEnabled(telescopeMotionNSP != null));
        }
        if (btnMoveNE != null) {
            btnMoveNE.post(() -> btnMoveNE.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null)));
        }
        if (btnMoveNW != null) {
            btnMoveNW.post(() -> btnMoveNW.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null)));
        }
        if (btnMoveSE != null) {
            btnMoveSE.post(() -> btnMoveSE.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null)));
        }
        if (btnMoveSW != null) {
            btnMoveSW.post(() -> btnMoveSW.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null)));
        }
        if (btnStop != null) {
            btnStop.post(() -> btnStop.setEnabled((telescopeMotionWEP != null) || (telescopeMotionNSP != null)
                    || (telescopeMotionAbort != null)));
        }
        if (btnSpeedUp != null) {
            btnSpeedUp.post(() -> btnSpeedUp.setEnabled(telescopeMotionRate != null || telescopeMotionRateEQMod != null
                    || telescopeMotionRateLX200 != null));
        }
        if (btnSpeedDown != null) {
            btnSpeedDown.post(() -> btnSpeedDown.setEnabled(telescopeMotionRate != null || telescopeMotionRateEQMod != null
                    || telescopeMotionRateLX200 != null));
        }
    }

    /**
     * Updates the speed text
     */
    public void updateSpeedText() {
        if (speedText != null) {
            speedText.post((Runnable) () -> {
                if (telescopeMotionRate != null) {
                    double speed = telescopeMotionRate.getElement("MOTION_RATE").getValue();
                    speedText.setText(String.format(Locale.ENGLISH, "%3.1fx (%3.1f '/s)", speed / 0.25, speed));

                } else if (telescopeMotionRateLX200 != null) {
                    List<INDISwitchElement> elements = telescopeMotionRateLX200.getElementsAsList();
                    int i = 0;
                    while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                            && i < elements.size() - 1) {
                        i++;
                    }
                    speedText.setText(elements.get(i).getLabel());

                } else if (telescopeMotionRateEQMod != null) {
                    List<INDISwitchElement> elements = telescopeMotionRateEQMod.getElementsAsList();
                    int i = 0;
                    while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                            && i < elements.size() - 1) {
                        i++;
                    }
                    speedText.setText(elements.get(i).getLabel());

                } else {
                    speedText.setText(R.string.default_speed);
                }
            });
        }
    }

    /**
     * Called when a directional button is pressed or released. Send the
     * corresponding order to the driver.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final Constants.SwitchStatus status, offStatus = Constants.SwitchStatus.OFF;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            status = Constants.SwitchStatus.ON;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            status = Constants.SwitchStatus.OFF;
        } else {
            return true;
        }
        int id = v.getId();
        if (id == R.id.buttonE) {
            try {
                telescopeMotionEE.setDesiredValue(status);
                telescopeMotionWE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionWEP).start();

            } catch (INDIValueException e) {
                Log.e("MotionFragment", e.getLocalizedMessage());
            }
            return true;
        } else if (id == R.id.buttonW) {
            try {
                telescopeMotionWE.setDesiredValue(status);
                telescopeMotionEE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionWEP).start();

            } catch (INDIValueException e) {
                Log.e("MotionFragment", e.getLocalizedMessage());
            }
            return true;
        } else if (id == R.id.buttonN) {
            try {
                telescopeMotionNE.setDesiredValue(status);
                telescopeMotionSE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionNSP).start();

            } catch (INDIValueException e) {
                Log.e("MotionFragment", e.getLocalizedMessage());
            }
            return true;
        } else if (id == R.id.buttonS) {
            try {
                telescopeMotionSE.setDesiredValue(status);
                telescopeMotionNE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionNSP).start();

            } catch (INDIValueException e) {
                Log.e("MotionFragment", e.getLocalizedMessage());
            }
            return true;
        } else if (id == R.id.buttonNE) {
            try {
                telescopeMotionEE.setDesiredValue(status);
                telescopeMotionWE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionWEP).start();
                telescopeMotionNE.setDesiredValue(status);
                telescopeMotionSE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionNSP).start();

            } catch (INDIValueException e) {
                Log.e("MotionFragment", e.getLocalizedMessage());
            }
            return true;
        } else if (id == R.id.buttonNW) {
            try {
                telescopeMotionWE.setDesiredValue(status);
                telescopeMotionEE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionWEP).start();
                telescopeMotionNE.setDesiredValue(status);
                telescopeMotionSE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionNSP).start();

            } catch (INDIValueException e) {
                Log.e("MotionFragment", e.getLocalizedMessage());
            }
            return true;
        } else if (id == R.id.buttonSE) {
            try {
                telescopeMotionEE.setDesiredValue(status);
                telescopeMotionWE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionWEP).start();
                telescopeMotionSE.setDesiredValue(status);
                telescopeMotionNE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionNSP).start();

            } catch (INDIValueException e) {
                Log.e("MotionFragment", e.getLocalizedMessage());
            }
            return true;
        } else if (id == R.id.buttonSW) {
            try {
                telescopeMotionWE.setDesiredValue(status);
                telescopeMotionEE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionWEP).start();
                telescopeMotionSE.setDesiredValue(status);
                telescopeMotionNE.setDesiredValue(offStatus);
                new PropUpdater(telescopeMotionNSP).start();

            } catch (INDIValueException e) {
                Log.e("MotionFragment", e.getLocalizedMessage());
            }
            return true;
        }
        return false;
    }

    /**
     * Called when one of the stop, speed up and speed down buttons is clicked.
     * Sends the corresponding order to the driver.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonStop) {
            try {
                if (telescopeMotionWEP != null) {
                    telescopeMotionWE.setDesiredValue(Constants.SwitchStatus.OFF);
                    telescopeMotionEE.setDesiredValue(Constants.SwitchStatus.OFF);
                    new PropUpdater(telescopeMotionWEP).start();
                }
                if (telescopeMotionNSP != null) {
                    telescopeMotionSE.setDesiredValue(Constants.SwitchStatus.OFF);
                    telescopeMotionNE.setDesiredValue(Constants.SwitchStatus.OFF);
                    new PropUpdater(telescopeMotionNSP).start();
                }
                if (telescopeMotionAbort != null) {
                    telescopeMotionAbortE.setDesiredValue(Constants.SwitchStatus.ON);
                    new PropUpdater(telescopeMotionAbort).start();
                }

            } catch (INDIValueException e) {
                Log.e("MotionFragment", e.getLocalizedMessage());
            }
        } else if (id == R.id.buttonSpeedUp) {
            if (telescopeMotionRate != null) {
                try {
                    INDINumberElement motionRate = telescopeMotionRate.getElement("MOTION_RATE");
                    motionRate.setDesiredValue(
                            Math.min(telescopeMotionRate.getElement("MOTION_RATE").getMax(),
                                    motionRate.getValue() * 2));
                    new PropUpdater(telescopeMotionRate).start();

                } catch (INDIValueException e) {
                    Log.e("MotionFragment", e.getLocalizedMessage());
                }

            } else if (telescopeMotionRateEQMod != null) {
                try {
                    List<INDISwitchElement> elements = telescopeMotionRateEQMod.getElementsAsList();
                    int i = 0;
                    while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                            && i < elements.size() - 2) {
                        i++;
                    }
                    elements.get(i + 1).setDesiredValue(Constants.SwitchStatus.ON);
                    new PropUpdater(telescopeMotionRateEQMod).start();

                } catch (INDIValueException e) {
                    Log.e("MotionFragment", e.getLocalizedMessage());
                }

            } else if (telescopeMotionRateLX200 != null) {
                try {
                    List<INDISwitchElement> elements = telescopeMotionRateLX200.getElementsAsList();
                    int i = 0;
                    while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                            && i < elements.size() - 1) {
                        i++;
                    }
                    if (i > 0) {
                        elements.get(i - 1).setDesiredValue(Constants.SwitchStatus.ON);
                    }
                    new PropUpdater(telescopeMotionRateLX200).start();

                } catch (INDIValueException e) {
                    Log.e("MotionFragment", e.getLocalizedMessage());
                }
            }
        } else if (id == R.id.buttonSpeedDown) {
            if (telescopeMotionRate != null) {
                try {
                    INDINumberElement motionRate = telescopeMotionRate.getElement("MOTION_RATE");
                    motionRate.setDesiredValue(Math.max(telescopeMotionRate.getElement("MOTION_RATE").getMin(),
                            motionRate.getValue() * 0.5));
                    new PropUpdater(telescopeMotionRate).start();

                } catch (INDIValueException e) {
                    Log.e("MotionFragment", e.getLocalizedMessage());
                }

            } else if (telescopeMotionRateEQMod != null) {
                try {
                    List<INDISwitchElement> elements = telescopeMotionRateEQMod.getElementsAsList();
                    int i = 0;
                    while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                            && i < elements.size() - 1) {
                        i++;
                    }
                    if (i > 0) {
                        elements.get(i - 1).setDesiredValue(Constants.SwitchStatus.ON);
                    }
                    new PropUpdater(telescopeMotionRateEQMod).start();

                } catch (INDIValueException e) {
                    Log.e("MotionFragment", e.getLocalizedMessage());
                }

            } else if (telescopeMotionRateLX200 != null) {
                try {
                    List<INDISwitchElement> elements = telescopeMotionRateLX200.getElementsAsList();
                    int i = 0;
                    while (((INDISwitchElement) elements.get(i)).getValue() == Constants.SwitchStatus.OFF
                            && i < elements.size() - 2) {
                        i++;
                    }
                    elements.get(i + 1).setDesiredValue(Constants.SwitchStatus.ON);
                    new PropUpdater(telescopeMotionRateLX200).start();

                } catch (INDIValueException e) {
                    Log.e("MotionFragment", e.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionManager.removeListener(this);
    }
}