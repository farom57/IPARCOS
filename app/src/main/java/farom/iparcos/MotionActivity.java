package farom.iparcos;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import laazotea.indi.Constants.SwitchStatus;
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
 * The activity display directional buttons to move a telescope. It also provide
 * buttons to change speed. To activate the buttons, the driver must provide the
 * following properties:
 * TELESCOPE_MOTION_NS,TELESCOPE_MOTION_WE,TELESCOPE_ABORT_MOTION
 * ,TELESCOPE_MOTION_RATE
 *
 * @author Romain Fafet
 */
public class MotionActivity extends Activity implements INDIServerConnectionListener, INDIPropertyListener,
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the UI
        setContentView(R.layout.activity_motion);
        btnMoveN = (Button) findViewById(R.id.buttonN);
        btnMoveNE = (Button) findViewById(R.id.buttonNE);
        btnMoveE = (Button) findViewById(R.id.buttonE);
        btnMoveSE = (Button) findViewById(R.id.buttonSE);
        btnMoveS = (Button) findViewById(R.id.buttonS);
        btnMoveSW = (Button) findViewById(R.id.buttonSW);
        btnMoveW = (Button) findViewById(R.id.buttonW);
        btnMoveNW = (Button) findViewById(R.id.buttonNW);
        btnStop = (Button) findViewById(R.id.buttonStop);
        btnSpeedUp = (Button) findViewById(R.id.buttonSpeedUp);
        btnSpeedDown = (Button) findViewById(R.id.buttonSpeedDown);
        speedText = (TextView) findViewById(R.id.speedText);
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
        ConnectionActivity.getInstance().registerPermanentConnectionListener(this);

        // Enumerate existing properties
        INDIServerConnection connection = ConnectionActivity.getConnection();
        if (connection != null) {
            List<INDIDevice> list = connection.getDevicesAsList();
            if (list != null) {
                for (Iterator<INDIDevice> it = list.iterator(); it.hasNext(); ) {
                    INDIDevice device = it.next();
                    device.addINDIDeviceListener(this);
                    List<INDIProperty> properties = device.getPropertiesAsList();
                    for (Iterator<INDIProperty> it2 = properties.iterator(); it2.hasNext(); ) {
                        this.newProperty(device, it2.next());
                    }
                }
            }
        }

        // Update UI
        updateBtnState();
        updateSpeedText();

    }

    // ------ Action bar menu ------ //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);

        // hide the menu item for the current activity
        MenuItem motionItem = menu.findItem(R.id.menu_move);
        motionItem.setVisible(false);

        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.title_activity_motion);

        return true;
    }

    /**
     * open the motion activity,
     *
     * @param v
     */
    public boolean openMotionActivity(MenuItem v) {
        // nothing to do, already the current activity
        return false;
    }

    /**
     * open the settings activity
     *
     * @param v
     * @return
     */
    public boolean openSettingsActivity(MenuItem v) {
        // TODO
        return false;
    }

    /**
     * open the generic activity
     *
     * @param v
     * @return
     */
    public boolean openGenericActivity(MenuItem v) {
        Intent intent = new Intent(this, GenericActivity.class);
        startActivity(intent);
        return true;
    }


    /**
     * open the search activity
     *
     * @param v
     * @return
     */
    public boolean openSearchActivity(MenuItem v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        return true;
    }

    /**
     * open the connection activity
     *
     * @param v
     * @return
     */
    public boolean openConnectionActivity(MenuItem v) {
        Intent intent = new Intent(this, ConnectionActivity.class);
        startActivity(intent);
        return true;
    }

    // ------ Listener functions from INDI ------

    @Override
    public void connectionLost(INDIServerConnection arg0) {
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
        updateBtnState();
        updateSpeedText();
        openConnectionActivity(null);
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        // We just simply listen to this Device
        Log.i("MotionActivity", getString(R.string.new_device) + device.getName());

        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        // We just remove ourselves as a listener of the removed device
        Log.i("MotionActivity", getString(R.string.device_removed) + device.getName());
        device.removeINDIDeviceListener(this);
    }

    @Override
    public void newMessage(INDIServerConnection arg0, Date arg1, String arg2) {
        // nothing to do
    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty property) {

        // Look for certain properties

        if (property.getName().equals("TELESCOPE_MOTION_NS")) {
            if (((telescopeMotionNE = (INDISwitchElement) property.getElement("MOTION_NORTH")) != null)
                    && ((telescopeMotionSE = (INDISwitchElement) property.getElement("MOTION_SOUTH")) != null)) {
                property.addINDIPropertyListener(this);
                telescopeMotionNSP = (INDISwitchProperty) property;
                Log.i("MotionActivity",
                        "--New Property (" + property.getName() + ") added to device " + device.getName());
                updateBtnState();
            }
        }

        if (property.getName().equals("TELESCOPE_MOTION_WE")) {
            if (((telescopeMotionEE = (INDISwitchElement) property.getElement("MOTION_EAST")) != null)
                    && ((telescopeMotionWE = (INDISwitchElement) property.getElement("MOTION_WEST")) != null)) {
                property.addINDIPropertyListener(this);
                telescopeMotionWEP = (INDISwitchProperty) property;
                Log.i("MotionActivity",
                        "--New Property (" + property.getName() + ") added to device " + device.getName());
                updateBtnState();
            }
        }

        if (property.getName().equals("TELESCOPE_ABORT_MOTION")) {
            if ((telescopeMotionAbortE = (INDISwitchElement) property.getElement("ABORT_MOTION")) != null) {
                property.addINDIPropertyListener(this);
                telescopeMotionAbort = (INDISwitchProperty) property;
                Log.i("MotionActivity",
                        "--New Property (" + property.getName() + ") added to device " + device.getName());
                updateBtnState();
            }
        }

        if (property.getName().equals("TELESCOPE_MOTION_RATE")) {
            property.addINDIPropertyListener(this);
            telescopeMotionRate = (INDINumberProperty) property;
            Log.i("MotionActivity", "--New Property (" + property.getName() + ") added to device " + device.getName());
            updateBtnState();
            updateSpeedText();
        }

        if (property.getName().equals("Slew Rate")) {
            property.addINDIPropertyListener(this);
            telescopeMotionRateLX200 = (INDISwitchProperty) property;
            Log.i("MotionActivity", "--New Property (" + property.getName() + ") added to device " + device.getName());
            updateBtnState();
            updateSpeedText();
        }

        if (property.getName().equals("SLEWMODE")) {
            property.addINDIPropertyListener(this);
            telescopeMotionRateEQMod = (INDISwitchProperty) property;
            Log.i("MotionActivity", "--New Property (" + property.getName() + ") added to device " + device.getName());
            updateBtnState();
            updateSpeedText();
        }
        Log.d("MotionActivity", "New Property (" + property.getName() + ") added to device " + device.getName());
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty property) {

        if (property.getName().equals("TELESCOPE_MOTION_NS")) {
            telescopeMotionNSP = null;
            telescopeMotionNE = null;
            telescopeMotionSE = null;
        }
        if (property.getName().equals("TELESCOPE_MOTION_WE")) {
            telescopeMotionWEP = null;
            telescopeMotionWE = null;
            telescopeMotionEE = null;
        }
        if (property.getName().equals("TELESCOPE_ABORT_MOTION")) {
            telescopeMotionAbort = null;
            telescopeMotionAbortE = null;
        }
        if (property.getName().equals("TELESCOPE_MOTION_RATE")) {
            telescopeMotionRate = null;
        }

        if (property.getName().equals("Slew Rate")) {
            telescopeMotionRateLX200 = null;
        }

        if (property.getName().equals("SLEWMODE")) {
            telescopeMotionRateEQMod = null;
        }

        updateBtnState();
        updateSpeedText();

        Log.d("MotionActivity", "Removed property (" + property.getName() + ") to device " + device.getName());
    }

    @Override
    public void propertyChanged(final INDIProperty property) {
        if (property.getName().equals("TELESCOPE_MOTION_NS")) {
            if (btnMoveN != null) {
                btnMoveN.post(new Runnable() {
                    public void run() {
                        btnMoveN.setPressed(telescopeMotionNE.getValue() == SwitchStatus.ON);
                    }
                });
            }
            if (btnMoveS != null) {
                btnMoveS.post(new Runnable() {
                    public void run() {
                        btnMoveS.setPressed(telescopeMotionSE.getValue() == SwitchStatus.ON);
                    }
                });
            }
        }
        if (property.getName().equals("TELESCOPE_MOTION_WE")) {
            if (btnMoveE != null) {
                btnMoveE.post(new Runnable() {
                    public void run() {
                        btnMoveE.setPressed(telescopeMotionEE.getValue() == SwitchStatus.ON);
                    }
                });
            }
            if (btnMoveW != null) {
                btnMoveW.post(new Runnable() {
                    public void run() {
                        btnMoveW.setPressed(telescopeMotionWE.getValue() == SwitchStatus.ON);
                    }
                });
            }
        }
        if (property.getName().equals("TELESCOPE_MOTION_RATE") || property.getName().equals("Slew Rate")
                || property.getName().equals("SLEWMODE")) {
            updateSpeedText();
        }
    }

    @Override
    public void messageChanged(INDIDevice device) {
        // TODO Auto-generated method stub

    }

    // ------ UI functions ------

    /**
     * Enable the buttons if the corresponding property was found
     */
    public void updateBtnState() {
        if (btnMoveE != null) {
            btnMoveE.post(new Runnable() {
                public void run() {
                    btnMoveE.setEnabled(telescopeMotionWEP != null);
                }
            });
        }
        if (btnMoveW != null) {
            btnMoveW.post(new Runnable() {
                public void run() {
                    btnMoveW.setEnabled(telescopeMotionWEP != null);
                }
            });
        }
        if (btnMoveN != null) {
            btnMoveN.post(new Runnable() {
                public void run() {
                    btnMoveN.setEnabled(telescopeMotionNSP != null);
                }
            });
        }
        if (btnMoveS != null) {
            btnMoveS.post(new Runnable() {
                public void run() {
                    btnMoveS.setEnabled(telescopeMotionNSP != null);
                }
            });
        }
        if (btnMoveNE != null) {
            btnMoveNE.post(new Runnable() {
                public void run() {
                    btnMoveNE.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null));
                }
            });
        }
        if (btnMoveNW != null) {
            btnMoveNW.post(new Runnable() {
                public void run() {
                    btnMoveNW.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null));
                }
            });
        }
        if (btnMoveSE != null) {
            btnMoveSE.post(new Runnable() {
                public void run() {
                    btnMoveSE.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null));
                }
            });
        }
        if (btnMoveSW != null) {
            btnMoveSW.post(new Runnable() {
                public void run() {
                    btnMoveSW.setEnabled((telescopeMotionWEP != null) && (telescopeMotionNSP != null));
                }
            });
        }
        if (btnStop != null) {
            btnStop.post(new Runnable() {
                public void run() {
                    btnStop.setEnabled((telescopeMotionWEP != null) || (telescopeMotionNSP != null)
                            || (telescopeMotionAbort != null));
                }
            });
        }
        if (btnSpeedUp != null) {
            btnSpeedUp.post(new Runnable() {
                public void run() {
                    btnSpeedUp.setEnabled(telescopeMotionRate != null || telescopeMotionRateEQMod != null
                            || telescopeMotionRateLX200 != null);
                }
            });
        }
        if (btnSpeedDown != null) {
            btnSpeedDown.post(new Runnable() {
                public void run() {
                    btnSpeedDown.setEnabled(telescopeMotionRate != null || telescopeMotionRateEQMod != null
                            || telescopeMotionRateLX200 != null);
                }
            });
        }
    }

    /**
     * Update the speed text
     */
    public void updateSpeedText() {
        if (speedText != null) {
            speedText.post(new Runnable() {
                @Override
                public void run() {
                    if (telescopeMotionRate != null) {
                        double speed = telescopeMotionRate.getElement("MOTION_RATE").getValue();
                        speedText.setText(String.format("%3.1fx (%3.1f '/s)", speed / 0.25, speed));
                    } else if (telescopeMotionRateLX200 != null) {
                        ArrayList<INDIElement> elements = telescopeMotionRateLX200.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == SwitchStatus.OFF
                                && i < elements.size() - 1) {
                            i++;
                        }
                        speedText.setText(elements.get(i).getLabel());
                    } else if (telescopeMotionRateEQMod != null) {
                        ArrayList<INDIElement> elements = telescopeMotionRateEQMod.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == SwitchStatus.OFF
                                && i < elements.size() - 1) {
                            i++;
                        }
                        speedText.setText(elements.get(i).getLabel());
                    } else {
                        speedText.setText(R.string.default_speed);
                    }
                }
            });

        }
    }

    // Called when a directional button is pressed or released, send the
    // corresponding order to the driver
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        SwitchStatus status, negStatus;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            status = SwitchStatus.ON;
            negStatus = SwitchStatus.OFF;
            // log("button pressed");
            // v.setPressed(true);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            status = SwitchStatus.OFF;
            negStatus = SwitchStatus.OFF;
            // log("button released");
            // v.setPressed(false);
        } else {
            return true;
        }

        switch (v.getId()) {
            case R.id.buttonE:
                try {
                    telescopeMotionEE.setDesiredValue(status);
                    telescopeMotionWE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();
                } catch (INDIValueException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                }
                return true;
            case R.id.buttonW:
                try {
                    telescopeMotionWE.setDesiredValue(status);
                    telescopeMotionEE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();
                } catch (INDIValueException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                }
                return true;
            case R.id.buttonN:
                try {
                    telescopeMotionNE.setDesiredValue(status);
                    telescopeMotionSE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();
                } catch (INDIValueException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                }
                return true;
            case R.id.buttonS:
                try {
                    telescopeMotionSE.setDesiredValue(status);
                    telescopeMotionNE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();
                } catch (INDIValueException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                }
                return true;
            case R.id.buttonNE:
                try {
                    telescopeMotionEE.setDesiredValue(status);
                    telescopeMotionWE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();
                    telescopeMotionNE.setDesiredValue(status);
                    telescopeMotionSE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();
                } catch (INDIValueException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                }
                return true;
            case R.id.buttonNW:
                try {
                    telescopeMotionWE.setDesiredValue(status);
                    telescopeMotionEE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();
                    telescopeMotionNE.setDesiredValue(status);
                    telescopeMotionSE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();
                } catch (INDIValueException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                }
                return true;
            case R.id.buttonSE:
                try {
                    telescopeMotionEE.setDesiredValue(status);
                    telescopeMotionWE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();
                    telescopeMotionSE.setDesiredValue(status);
                    telescopeMotionNE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();
                } catch (INDIValueException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                }
                return true;
            case R.id.buttonSW:
                try {
                    telescopeMotionWE.setDesiredValue(status);
                    telescopeMotionEE.setDesiredValue(negStatus);
                    telescopeMotionWEP.sendChangesToDriver();
                    telescopeMotionSE.setDesiredValue(status);
                    telescopeMotionNE.setDesiredValue(negStatus);
                    telescopeMotionNSP.sendChangesToDriver();
                } catch (INDIValueException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                }
                return true;
            default:
                Log.e("MotionActivity", "unknown view");
        }
        return false;
    }

    // Called when one of the stop, speed up and speed dow buttons is clcked.
    // Send the corresponding order to the driver
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStop:
                try {
                    if (telescopeMotionWEP != null) {
                        telescopeMotionWE.setDesiredValue(SwitchStatus.OFF);
                        telescopeMotionEE.setDesiredValue(SwitchStatus.OFF);
                        telescopeMotionWEP.sendChangesToDriver();
                    }
                    if (telescopeMotionNSP != null) {
                        telescopeMotionSE.setDesiredValue(SwitchStatus.OFF);
                        telescopeMotionNE.setDesiredValue(SwitchStatus.OFF);
                        telescopeMotionNSP.sendChangesToDriver();
                    }
                    if (telescopeMotionAbort != null) {
                        telescopeMotionAbortE.setDesiredValue(SwitchStatus.ON);
                        telescopeMotionAbort.sendChangesToDriver();
                    }
                } catch (INDIValueException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("MotionActivity", e.getLocalizedMessage());
                }
                break;
            case R.id.buttonSpeedUp:
                if (telescopeMotionRate != null) {
                    try {
                        double speed = telescopeMotionRate.getElement("MOTION_RATE").getValue();
                        double maxSpeed = telescopeMotionRate.getElement("MOTION_RATE").getMax();
                        speed = Math.min(maxSpeed, speed * 2);
                        telescopeMotionRate.getElement("MOTION_RATE").setDesiredValue(speed);
                        telescopeMotionRate.sendChangesToDriver();
                    } catch (INDIValueException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    } catch (IOException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    }
                } else if (telescopeMotionRateEQMod != null) {
                    try {
                        ArrayList<INDIElement> elements = telescopeMotionRateEQMod.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == SwitchStatus.OFF
                                && i < elements.size() - 2) {
                            i++;
                        }
                        elements.get(i + 1).setDesiredValue(SwitchStatus.ON);
                        telescopeMotionRateEQMod.sendChangesToDriver();
                    } catch (INDIValueException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    } catch (IOException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    }
                } else if (telescopeMotionRateLX200 != null) {
                    try {
                        ArrayList<INDIElement> elements = telescopeMotionRateLX200.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == SwitchStatus.OFF
                                && i < elements.size() - 1) {
                            i++;
                        }
                        if (i > 0) {
                            elements.get(i - 1).setDesiredValue(SwitchStatus.ON);
                        }
                        telescopeMotionRateLX200.sendChangesToDriver();
                    } catch (INDIValueException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    } catch (IOException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    }
                }
                break;
            case R.id.buttonSpeedDown:
                if (telescopeMotionRate != null) {
                    try {
                        double speed = telescopeMotionRate.getElement("MOTION_RATE").getValue();
                        double minSpeed = telescopeMotionRate.getElement("MOTION_RATE").getMin();
                        speed = Math.max(minSpeed, speed * 0.5);
                        telescopeMotionRate.getElement("MOTION_RATE").setDesiredValue(speed);
                        telescopeMotionRate.sendChangesToDriver();
                    } catch (INDIValueException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    } catch (IOException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    }
                } else if (telescopeMotionRateEQMod != null) {
                    try {
                        ArrayList<INDIElement> elements = telescopeMotionRateEQMod.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == SwitchStatus.OFF
                                && i < elements.size() - 1) {
                            i++;
                        }
                        if (i > 0) {
                            elements.get(i - 1).setDesiredValue(SwitchStatus.ON);
                        }
                        telescopeMotionRateEQMod.sendChangesToDriver();
                    } catch (INDIValueException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    } catch (IOException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    }
                } else if (telescopeMotionRateLX200 != null) {
                    try {
                        ArrayList<INDIElement> elements = telescopeMotionRateLX200.getElementsAsList();
                        int i = 0;
                        while (((INDISwitchElement) elements.get(i)).getValue() == SwitchStatus.OFF
                                && i < elements.size() - 2) {
                            i++;
                        }
                        elements.get(i + 1).setDesiredValue(SwitchStatus.ON);
                        telescopeMotionRateLX200.sendChangesToDriver();
                    } catch (INDIValueException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    } catch (IOException e) {
                        Log.e("MotionActivity", e.getLocalizedMessage());
                    }
                }
                break;
            default:
                Log.e("MotionActivity", "unknown view");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionActivity.getInstance().unRegisterPermanentConnectionListener(this);
    }

}
