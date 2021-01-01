package org.indilib.i4j.iparcos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
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

import org.indilib.i4j.iparcos.prop.PropUpdater;
import org.indilib.i4j.properties.INDIStandardElement;

/**
 * This fragment shows directional buttons to move a focuser.
 *
 * @author marcocipriani01
 */
public class FocuserFragment extends Fragment implements INDIServerConnectionListener, INDIPropertyListener,
        INDIDeviceListener, View.OnClickListener, CounterHandler.CounterListener, SeekBar.OnSeekBarChangeListener {

    // Properties and elements associated to the buttons
    private INDISwitchProperty directionProp = null;
    private INDISwitchElement inwardDirElem = null;
    private INDISwitchElement outwardDirElem = null;
    private INDINumberProperty relPosProp = null;
    private INDINumberElement relPosElem = null;
    private INDINumberProperty absPosProp = null;
    private INDINumberElement absPosElem = null;
    private INDINumberProperty syncPosProp = null;
    private INDINumberElement syncPosElem = null;
    private INDINumberProperty speedProp = null;
    private INDINumberElement speedElem = null;
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
    private Button syncPosButton = null;
    private TextView stepsText = null;
    private EditText positionEditText = null;
    private SeekBar speedBar = null;
    private CounterHandler stepsHandler;
    private TextView focuserName = null;

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
        enableUi();
        updateStepsText();
        updatePositionText();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_focuser, container, false);
        // Set up the UI
        inButton = rootView.findViewById(R.id.focus_in);
        outButton = rootView.findViewById(R.id.focus_out);
        speedUpButton = rootView.findViewById(R.id.focuser_faster);
        speedDownButton = rootView.findViewById(R.id.focuser_slower);
        stepsText = rootView.findViewById(R.id.focuser_steps_box);
        abortButton = rootView.findViewById(R.id.focuser_abort);
        setAbsPosButton = rootView.findViewById(R.id.fok_abs_pos_button);
        syncPosButton = rootView.findViewById(R.id.fok_sync_pos_button);
        positionEditText = rootView.findViewById(R.id.abs_pos_field);
        speedBar = rootView.findViewById(R.id.focus_speed_seekbar);
        focuserName = rootView.findViewById(R.id.focuser_name);
        stepsHandler = new CounterHandler(speedUpButton, speedDownButton, 1, 1000000, 100, 10, 100, false, this);
        new LongPressHandler(outButton, inButton, 150) {
            @Override
            protected void increment() {
                if (outwardDirElem != null && inwardDirElem != null && relPosElem != null) {
                    try {
                        outwardDirElem.setDesiredValue(Constants.SwitchStatus.ON);
                        inwardDirElem.setDesiredValue(Constants.SwitchStatus.OFF);
                        new PropUpdater(directionProp).start();
                        relPosElem.setDesiredValue((double) stepsHandler.getValue());
                        new PropUpdater(relPosProp).start();
                    } catch (INDIValueException e) {
                        Log.e("FocusFragment", e.getLocalizedMessage());
                    }
                }
            }

            @Override
            protected void decrement() {
                if (inwardDirElem != null && outwardDirElem != null && relPosElem != null) {
                    try {
                        inwardDirElem.setDesiredValue(Constants.SwitchStatus.ON);
                        outwardDirElem.setDesiredValue(Constants.SwitchStatus.OFF);
                        new PropUpdater(directionProp).start();
                        relPosElem.setDesiredValue((double) stepsHandler.getValue());
                        new PropUpdater(relPosProp).start();
                    } catch (INDIValueException e) {
                        Log.e("FocusFragment", e.getLocalizedMessage());
                    }
                }
            }
        };
        abortButton.setOnClickListener(this);
        setAbsPosButton.setOnClickListener(this);
        syncPosButton.setOnClickListener(this);
        stepsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    stepsHandler.setValue(Integer.parseInt(s.toString()));
                } catch (NumberFormatException ignored) {

                }
            }
        });
        speedBar.setOnSeekBarChangeListener(this);
        // Set up INDI connection
        connectionManager = IPARCOSApp.getConnectionManager();
        connectionManager.addListener(this);
        enableUi();
        return rootView;
    }

    private void clearVars() {
        relPosProp = null;
        relPosElem = null;
        absPosProp = null;
        absPosElem = null;
        directionProp = null;
        outwardDirElem = null;
        inwardDirElem = null;
        syncPosProp = null;
        syncPosElem = null;
        speedProp = null;
        speedElem = null;
        abortElem = null;
        abortProp = null;
    }

    // ------ Listener functions from INDI ------

    @Override
    public void connectionLost(INDIServerConnection connection) {
        clearVars();
        updateSpeedBar();
        updateStepsText();
        updatePositionText();
        enableUi();
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
        Log.d("FocusFragment", "New Property (" + property.getName() + ") added to device " + device.getName()
                + ", elements: " + Arrays.toString(property.getElementNames()));
        switch (property.getName()) {
            case "ABS_FOCUS_POSITION": {
                if ((absPosElem = (INDINumberElement) property.getElement(INDIStandardElement.FOCUS_ABSOLUTE_POSITION)) != null) {
                    absPosProp = (INDINumberProperty) property;
                }
                break;
            }
            case "REL_FOCUS_POSITION": {
                if ((relPosElem = (INDINumberElement) property.getElement(INDIStandardElement.FOCUS_RELATIVE_POSITION)) != null) {
                    relPosProp = (INDINumberProperty) property;
                    stepsHandler.setMaxValue((int) relPosElem.getMax());
                    stepsHandler.setMinValue((int) relPosElem.getMin());
                }
                break;
            }
            case "FOCUS_MOTION": {
                if (((inwardDirElem = (INDISwitchElement) property.getElement(INDIStandardElement.FOCUS_INWARD)) != null)
                        && ((outwardDirElem = (INDISwitchElement) property.getElement(INDIStandardElement.FOCUS_OUTWARD)) != null)) {
                    directionProp = (INDISwitchProperty) property;
                    focuserName.setText(device.getName());
                }
                break;
            }
            case "FOCUS_ABORT_MOTION": {
                if ((abortElem = (INDISwitchElement) property.getElement(INDIStandardElement.ABORT)) != null) {
                    abortProp = (INDISwitchProperty) property;
                }
                break;
            }
            case "FOCUS_SPEED": {
                if ((speedElem = (INDINumberElement) property.getElement(INDIStandardElement.FOCUS_SPEED_VALUE)) != null) {
                    speedProp = (INDINumberProperty) property;
                }
                break;
            }
            case "FOCUS_SYNC": {
                if ((syncPosElem = (INDINumberElement) property.getElement(INDIStandardElement.FOCUS_SYNC_VALUE)) != null) {
                    syncPosProp = (INDINumberProperty) property;
                }
                break;
            }
            default: {
                return;
            }
        }
        property.addINDIPropertyListener(this);
        enableUi();
        updateStepsText();
        updatePositionText();
        updateSpeedBar();
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty<?> property) {
        Log.d("FocusFragment", "Removed property (" + property.getName() + ") to device " + device.getName());
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
                focuserName.setText(R.string.focuser_control);
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
            case "FOCUS_SPEED": {
                speedProp = null;
                speedElem = null;
                break;
            }
            case "FOCUS_SYNC": {
                syncPosProp = null;
                syncPosElem = null;
                break;
            }
            default: {
                return;
            }
        }
        enableUi();
        updateStepsText();
        updatePositionText();
        updateSpeedBar();
    }

    @Override
    public void propertyChanged(final INDIProperty<?> property) {
        Log.d("FocusFragment",
                "Changed property (" + property.getName() + "), new value" + property.getValuesAsString());
        switch (property.getName()) {
            case "ABS_FOCUS_POSITION": {
                updatePositionText();
                break;
            }
            case "FOCUS_SPEED": {
                updateSpeedBar();
                break;
            }
        }
    }

    @Override
    public void messageChanged(INDIDevice device) {

    }

    private void updateSpeedBar() {
        if (speedBar != null) {
            speedBar.post(() -> {
                if (speedElem != null) {
                    speedBar.setOnSeekBarChangeListener(null);
                    double step = speedElem.getStep(), min = speedElem.getMin(), max = speedElem.getMax();
                    speedBar.setMax((int) ((max - min) / step));
                    speedBar.setProgress((int) ((speedElem.getValue() - min) / step));
                    speedBar.setOnSeekBarChangeListener(this);
                }
            });
        }
    }

    /**
     * Enables the buttons if the corresponding property was found
     */
    private void enableUi() {
        if (inButton != null) inButton.post(() -> inButton.setEnabled(inwardDirElem != null));
        if (outButton != null) outButton.post(() -> outButton.setEnabled(outwardDirElem != null));
        if (speedUpButton != null) speedUpButton.post(() -> speedUpButton.setEnabled(relPosElem != null));
        if (speedDownButton != null) speedDownButton.post(() -> speedDownButton.setEnabled(relPosElem != null));
        if (stepsText != null) stepsText.post(() -> stepsText.setFocusableInTouchMode(relPosElem != null));
        if (abortButton != null) abortButton.post(() -> abortButton.setEnabled(abortElem != null));
        if (setAbsPosButton != null) setAbsPosButton.post(() -> setAbsPosButton.setEnabled(absPosElem != null));
        if (syncPosButton != null) syncPosButton.post(() -> syncPosButton.setEnabled(syncPosElem != null));
        if (positionEditText != null) positionEditText.post(() -> positionEditText.setFocusableInTouchMode(absPosElem != null));
        if (speedBar != null) speedBar.post(() -> speedBar.setEnabled(speedElem != null));
    }

    /**
     * Updates the speed text
     */
    private void updateStepsText() {
        if (stepsText != null) {
            stepsText.post(() -> {
                if (relPosElem != null) {
                    int steps = (int) (double) relPosElem.getValue();
                    stepsText.setText(String.valueOf(steps));
                    stepsHandler.setValue(steps);
                } else {
                    stepsText.setText(R.string.unavailable);
                }
            });
        }
    }

    private void updatePositionText() {
        if (positionEditText != null) {
            positionEditText.post(() -> {
                if (absPosElem != null) {
                    positionEditText.setText(String.valueOf((int) (double) absPosElem.getValue()));
                } else {
                    positionEditText.setText(R.string.unavailable);
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
                    new PropUpdater(abortProp).start();
                }
            } catch (INDIValueException e) {
                Log.e("FocusFragment", e.getLocalizedMessage());
            }
        } else if (id == R.id.fok_abs_pos_button) {
            try {
                if (absPosElem != null && positionEditText != null) {
                    try {
                        absPosElem.setDesiredValue(Double.parseDouble(positionEditText.getText().toString()));
                        new PropUpdater(absPosProp).start();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Invalid absolute position!", Toast.LENGTH_SHORT).show();
                        updatePositionText();
                    }
                }
            } catch (INDIValueException e) {
                Log.e("FocusFragment", e.getLocalizedMessage());
            }
        } else if (id == R.id.fok_sync_pos_button) {
            try {
                if (syncPosElem != null && positionEditText != null) {
                    try {
                        syncPosElem.setDesiredValue(Double.parseDouble(positionEditText.getText().toString()));
                        new PropUpdater(syncPosProp).start();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Invalid absolute position!", Toast.LENGTH_SHORT).show();
                        updatePositionText();
                    }
                }
            } catch (INDIValueException e) {
                Log.e("FocusFragment", e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onIncrement(View view, int number) {
        stepsText.setText(String.valueOf(number));
    }

    @Override
    public void onDecrement(View view, int number) {
        stepsText.setText(String.valueOf(number));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (speedElem != null) {
            try {
                double step = speedElem.getStep(), min = speedElem.getMin();
                speedElem.setDesiredValue(min + (progress * step));
                new PropUpdater(speedProp).start();
            } catch (INDIValueException e) {
                Log.e("FocusFragment", e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}