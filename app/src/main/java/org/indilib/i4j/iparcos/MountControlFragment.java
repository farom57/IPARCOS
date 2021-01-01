package org.indilib.i4j.iparcos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.indilib.i4j.Constants;
import org.indilib.i4j.client.INDIDevice;
import org.indilib.i4j.client.INDIDeviceListener;
import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDIPropertyListener;
import org.indilib.i4j.client.INDIServerConnection;
import org.indilib.i4j.client.INDIServerConnectionListener;
import org.indilib.i4j.client.INDISwitchElement;
import org.indilib.i4j.client.INDISwitchProperty;
import org.indilib.i4j.client.INDIValueException;
import org.indilib.i4j.iparcos.prop.PropUpdater;
import org.indilib.i4j.properties.INDIStandardElement;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    private final SpinnerInteractionListener spinnerListener = new SpinnerInteractionListener();
    private ConnectionManager connectionManager;
    // Properties and elements associated to the buttons
    private INDISwitchProperty telescopeMotionNSP = null;
    private INDISwitchElement telescopeMotionNE = null;
    private INDISwitchElement telescopeMotionSE = null;
    private INDISwitchProperty telescopeMotionWEP = null;
    private INDISwitchElement telescopeMotionWE = null;
    private INDISwitchElement telescopeMotionEE = null;
    private INDISwitchProperty telescopeMotionAbort = null;
    private INDISwitchElement telescopeMotionAbortE = null;
    private INDISwitchProperty telescopeSlewRate = null;
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
    private Spinner slewRateSpinner = null;
    private TextView mountName = null;

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
        enableUi();
        initSlewRate();
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
        slewRateSpinner = rootView.findViewById(R.id.mount_slew_rate);
        mountName = rootView.findViewById(R.id.mount_name);
        btnMoveN.setOnTouchListener(this);
        btnMoveNE.setOnTouchListener(this);
        btnMoveE.setOnTouchListener(this);
        btnMoveSE.setOnTouchListener(this);
        btnMoveS.setOnTouchListener(this);
        btnMoveSW.setOnTouchListener(this);
        btnMoveW.setOnTouchListener(this);
        btnMoveNW.setOnTouchListener(this);
        btnStop.setOnClickListener(this);
        slewRateSpinner.setOnTouchListener(spinnerListener);
        // Set up INDI connection
        connectionManager = IPARCOSApp.getConnectionManager();
        connectionManager.addListener(this);
        return rootView;
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
        telescopeSlewRate = null;
    }

    private void initSlewRate() {
        if (slewRateSpinner != null) {
            slewRateSpinner.post(() -> {
                Context context = getContext();
                if (context != null) {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    int selectedItem = 0;
                    if (telescopeSlewRate == null) {
                        arrayAdapter.add(getString(R.string.unavailable));
                    } else {
                        int count = 0;
                        for (INDISwitchElement element : telescopeSlewRate.getElementsAsList()) {
                            String label = element.getLabel();
                            if (label.contains("x")) {
                                arrayAdapter.add(label);
                                if (element.getValue() == Constants.SwitchStatus.ON)
                                    selectedItem = count;
                                count++;
                            }
                        }
                        if (count == 0) arrayAdapter.add(getString(R.string.unavailable));
                    }
                    slewRateSpinner.setAdapter(arrayAdapter);
                    slewRateSpinner.setOnItemSelectedListener(null);
                    slewRateSpinner.setSelection(selectedItem);
                    slewRateSpinner.setOnItemSelectedListener(spinnerListener);
                }
            });
        }
    }

    /**
     * Enables the buttons if the corresponding property was found
     */
    public void enableUi() {
        if (btnMoveE != null) btnMoveE.post(() -> btnMoveE.setEnabled(telescopeMotionWEP != null));
        if (btnMoveW != null) btnMoveW.post(() -> btnMoveW.setEnabled(telescopeMotionWEP != null));
        if (btnMoveN != null) btnMoveN.post(() -> btnMoveN.setEnabled(telescopeMotionNSP != null));
        if (btnMoveS != null) btnMoveS.post(() -> btnMoveS.setEnabled(telescopeMotionNSP != null));
        if (btnMoveNE != null)
            btnMoveNE.post(() -> btnMoveNE.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null)));
        if (btnMoveNW != null)
            btnMoveNW.post(() -> btnMoveNW.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null)));
        if (btnMoveSE != null)
            btnMoveSE.post(() -> btnMoveSE.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null)));
        if (btnMoveSW != null)
            btnMoveSW.post(() -> btnMoveSW.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null)));
        if (btnStop != null)
            btnStop.post(() -> btnStop.setEnabled((telescopeMotionWEP != null) || (telescopeMotionNSP != null)
                    || (telescopeMotionAbort != null)));
        if (slewRateSpinner != null)
            slewRateSpinner.post(() -> slewRateSpinner.setEnabled(telescopeSlewRate != null));
    }

    @Override
    public void connectionLost(INDIServerConnection arg0) {
        clearVars();
        enableUi();
        initSlewRate();
        // Move to the connection tab
        IPARCOSApp.goToConnectionTab();
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        Log.i("MotionFragment", "New device: " + device.getName());
        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        Log.i("MotionFragment", "Device removed: " + device.getName());
        device.removeINDIDeviceListener(this);
    }

    @Override
    public void newMessage(INDIServerConnection arg0, Date arg1, String arg2) {

    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty<?> property) {
        Log.d("MotionFragment", "New Property (" + property.getName() + ") added to device " + device.getName()
                + ", elements: " + Arrays.toString(property.getElementNames()));
        switch (property.getName()) {
            case "TELESCOPE_MOTION_NS": {
                if (((telescopeMotionNE = (INDISwitchElement) property.getElement(INDIStandardElement.MOTION_NORTH)) != null)
                        && ((telescopeMotionSE = (INDISwitchElement) property.getElement(INDIStandardElement.MOTION_SOUTH)) != null)) {
                    telescopeMotionNSP = (INDISwitchProperty) property;
                    mountName.setText(device.getName());
                }
                break;
            }
            case "TELESCOPE_MOTION_WE": {
                if (((telescopeMotionEE = (INDISwitchElement) property.getElement(INDIStandardElement.MOTION_EAST)) != null)
                        && ((telescopeMotionWE = (INDISwitchElement) property.getElement(INDIStandardElement.MOTION_WEST)) != null)) {
                    telescopeMotionWEP = (INDISwitchProperty) property;
                }
                break;
            }
            case "TELESCOPE_ABORT_MOTION": {
                if ((telescopeMotionAbortE = (INDISwitchElement) property.getElement(INDIStandardElement.ABORT_MOTION)) != null) {
                    telescopeMotionAbort = (INDISwitchProperty) property;
                }
                break;
            }
            case "TELESCOPE_SLEW_RATE": {
                telescopeSlewRate = (INDISwitchProperty) property;
                initSlewRate();
                break;
            }
            default: {
                return;
            }
        }
        property.addINDIPropertyListener(this);
        enableUi();
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty<?> property) {
        Log.d("MotionFragment", "Removed property (" + property.getName() + ") to device " + device.getName());
        switch (property.getName()) {
            case "TELESCOPE_MOTION_NS": {
                telescopeMotionNSP = null;
                telescopeMotionNE = null;
                telescopeMotionSE = null;
                mountName.setText(R.string.mount_control);
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
            case "TELESCOPE_SLEW_RATE": {
                telescopeSlewRate = null;
                initSlewRate();
                break;
            }
            default: {
                return;
            }
        }
        enableUi();
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
            case "TELESCOPE_SLEW_RATE": {
                if (slewRateSpinner != null) {
                    slewRateSpinner.post(() -> {
                        String selected = null;
                        for (INDISwitchElement element : telescopeSlewRate) {
                            if (element.getValue() == Constants.SwitchStatus.ON)
                                selected = element.getLabel();
                        }
                        if (selected != null) {
                            SpinnerAdapter adapter = slewRateSpinner.getAdapter();
                            int i;
                            for (i = 0; i < adapter.getCount(); i++) {
                                if (((String) adapter.getItem(i)).equals(selected)) break;
                            }
                            slewRateSpinner.setOnItemSelectedListener(null);
                            slewRateSpinner.setSelection(i);
                            slewRateSpinner.setOnItemSelectedListener(spinnerListener);
                        }
                    });
                }
                break;
            }
        }
    }

    @Override
    public void messageChanged(INDIDevice device) {

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
        if (v.getId() == R.id.buttonStop) {
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionManager.removeListener(this);
    }

    /**
     * @author Andres Q.
     * @author marcocipriani01
     * @see <a href="https://stackoverflow.com/a/28466764">Spinner onItemSelected called multiple times after screen rotation</a>
     */
    private class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        private boolean userSelect = false;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                try {
                    String selected = ((String) slewRateSpinner.getAdapter().getItem(position));
                    if ((selected != null) && (!selected.equals(getString(R.string.unavailable)))) {
                        for (INDISwitchElement element : telescopeSlewRate.getElementsAsList()) {
                            String label = element.getLabel();
                            if (label.equals(selected)) {
                                element.setDesiredValue(Constants.SwitchStatus.ON);
                            } else {
                                element.setDesiredValue(Constants.SwitchStatus.OFF);
                            }
                        }
                        new PropUpdater(telescopeSlewRate).start();
                    }
                } catch (Exception e) {
                    Log.d("MotionFragment", "Slew rate error!");
                }
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}