package farom.astroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import laazotea.indi.Constants.*;
import laazotea.indi.client.*;

public class INDIAdapter implements INDIDeviceListener, INDIPropertyListener, INDIServerConnectionListener,
		OnTouchListener, OnClickListener {

	private static final INDIAdapter INSTANCE = new INDIAdapter();

	private INDIServerConnection connection;

	private INDISwitchProperty telescopeMotionNSP = null;
	private INDISwitchElement telescopeMotionNE = null;
	private INDISwitchElement telescopeMotionSE = null;
	private INDISwitchProperty telescopeMotionWEP = null;
	private INDISwitchElement telescopeMotionWE = null;
	private INDISwitchElement telescopeMotionEE = null;
	private INDISwitchProperty telescopeMotionAbort = null;
	private INDISwitchElement telescopeMotionAbortE = null;
	private INDINumberProperty telescopeMotionRate = null;

	private TextView logView;
	private Button connectionButton = null;
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
	
	private ArrayList<INDIServerConnectionListener> permanentConnectionListeners;

	private INDIAdapter() {
		permanentConnectionListeners = new ArrayList<INDIServerConnectionListener>();
	}

	public static INDIAdapter getInstance() {
		return INSTANCE;
	}

	public List<INDIDevice> getDevices() {
		if(connection!=null){
		return connection.getDevicesAsList();
		}else{
			return new ArrayList<INDIDevice>();
		}
	}

	public void registerPermanentConnectionListener(INDIServerConnectionListener arg){
		permanentConnectionListeners.add(arg);
		if(connection!=null){
			connection.addINDIServerConnectionListener(arg);
		}
	}
	
	public void unRegisterPermanentConnectionListener(INDIServerConnectionListener arg){
		permanentConnectionListeners.remove(arg);
		if(connection!=null){
			connection.removeINDIServerConnectionListener(arg);
		}
	}
	
	public void connect(java.lang.String host, int port) {
		connectionButton.setText(R.string.connecting);
		log("Try to connect to " + host + ":" + port);
		connection = new INDIServerConnection(host, port);
		
		connection.addINDIServerConnectionListener(this); // We listen to all
		for(Iterator<INDIServerConnectionListener> it = permanentConnectionListeners.iterator(); it.hasNext();){
			connection.addINDIServerConnectionListener(it.next());
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
					connection.connect();
					connection.askForDevices(); // Ask for all the devices.
					log("connection ok");
					connectionButton.post(new Runnable() {
						public void run() {
							connectionButton.setText(R.string.disconnect);
						}
					});
				} catch (IOException e) {
					log("Problem with the connection");
					log(e.getMessage());
					connectionButton.post(new Runnable() {
						public void run() {
							connectionButton.setText(R.string.connect);
						}
					});
				}

			}
		}).start();
	}

	/**
	 * @return the logView
	 */
	public TextView getLogView() {
		return logView;
	}

	/**
	 * @param logView
	 *            the logView to set
	 */
	public void setLogView(TextView logView) {
		this.logView = logView;
		log("application started");
	}


	/**
	 * @param text
	 *            The text to display in the log view
	 */
	public void log(final String text) {
		logView.post(new Runnable() {
			public void run() {
				logView.append(text + "\n");
				((ScrollView)logView.getParent()).fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	@Override
	public void newDevice(INDIServerConnection connection, INDIDevice device) {
		// We just simply listen to this Device
		log("New device: " + device.getName());

		device.addINDIDeviceListener(this);
	}

	@Override
	public void removeDevice(INDIServerConnection connection, INDIDevice device) {
		// We just remove ourselves as a listener of the removed device
		log("Device Removed: " + device.getName());
		device.removeINDIDeviceListener(this);
	}

	@Override
	public void connectionLost(INDIServerConnection connection) {
		log("Connection lost. Bye");
		connectionButton.post(new Runnable() {
			public void run() {
				connectionButton.setText(R.string.connect);
			}
		});
	}

	@Override
	public void newMessage(INDIServerConnection connection, Date timestamp, String message) {
		// log("New Server Message: " + timestamp + " - " + message);
	}

	@Override
	public void newProperty(INDIDevice device, INDIProperty property) {
		// We just simply listen to this Property
		log("New Property (" + property.getName() + ") added to device " + device.getName());
		property.addINDIPropertyListener(this);
		
		if (property.getName().equals("TELESCOPE_MOTION_NS")) {
			if (((telescopeMotionNE = (INDISwitchElement) property.getElement("MOTION_NORTH")) != null)
					&& ((telescopeMotionSE = (INDISwitchElement) property.getElement("MOTION_SOUTH")) != null)) {
				telescopeMotionNSP = (INDISwitchProperty) property;
				log("--New Property (" + property.getName() + ") added to device " + device.getName());
				updateBtnState();
			}
		}
		if (property.getName().equals("TELESCOPE_MOTION_WE")) {
			if (((telescopeMotionEE = (INDISwitchElement) property.getElement("MOTION_EAST")) != null)
					&& ((telescopeMotionWE = (INDISwitchElement) property.getElement("MOTION_WEST")) != null)) {
				telescopeMotionWEP = (INDISwitchProperty) property;
				log("--New Property (" + property.getName() + ") added to device " + device.getName());
				updateBtnState();
			}
		}
		if (property.getName().equals("TELESCOPE_ABORT_MOTION")) {
			if ((telescopeMotionAbortE = (INDISwitchElement) property.getElement("ABORT_MOTION")) != null){
				telescopeMotionAbort = (INDISwitchProperty) property;
				log("--New Property (" + property.getName() + ") added to device " + device.getName());
				updateBtnState();
			}
		}
		if (property.getName().equals("TELESCOPE_MOTION_RATE")) {
			telescopeMotionRate = (INDINumberProperty) property;
			log("--New Property (" + property.getName() + ") added to device " + device.getName());
			updateBtnState();
			updateSpeedText();
		}

		

	}

	@Override
	public void removeProperty(INDIDevice device, INDIProperty property) {
		log("removed Property (" + property.getName() + ") of the device " + device.getName());
		property.addINDIPropertyListener(this);
		if (property.getName().equals("TELESCOPE_MOTION_NS")) {
			telescopeMotionNSP = null;
			telescopeMotionNE = null;
			telescopeMotionSE = null;
			updateBtnState();
		}
		if (property.getName().equals("TELESCOPE_MOTION_WE")) {
			telescopeMotionWEP = null;
			telescopeMotionWE = null;
			telescopeMotionEE = null;
			updateBtnState();
		}
		if (property.getName().equals("TELESCOPE_ABORT_MOTION")) {
			telescopeMotionAbort = null;
			telescopeMotionAbortE = null;
			updateBtnState();
		}
		if (property.getName().equals("TELESCOPE_MOTION_RATE")) {
			telescopeMotionRate = null;
			updateSpeedText();
		}
	}

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
					btnSpeedUp.setEnabled(telescopeMotionRate != null);
				}
			});
		}
		if (btnSpeedDown != null) {
			btnSpeedDown.post(new Runnable() {
				public void run() {
					btnSpeedDown.setEnabled(telescopeMotionRate != null);
				}
			});
		}
	}

	public void updateSpeedText() {
		if (speedText != null) {
			speedText.post(new Runnable() {
				@Override
				public void run() {
					if (telescopeMotionRate != null) {
						double speed = telescopeMotionRate.getElement("MOTION_RATE").getValue();
						speedText.setText(String.format("%3.1fx (%3.1f '/s)", speed / 0.25, speed));
					} else {
						speedText.setText(R.string.default_speed);
					}
				}
			});
		}
	}

	@Override
	public void messageChanged(INDIDevice device) {
		// log("New Device Message: " + device.getName() + " - " +
		// device.getTimestamp() + " - " + device.getLastMessage());
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
		if (property.getName().equals("TELESCOPE_MOTION_RATE")) {
			updateSpeedText();
		}
	}

	/**
	 * @return the connectionButton
	 */
	public Button getConnectionButton() {
		return connectionButton;
	}

	/**
	 * @param connectionButton
	 *            the connectionButton to set
	 */
	public void setConnectionButton(Button connectionButton) {
		this.connectionButton = connectionButton;
	}

	/**
	 * Breaks the connection
	 */
	public void disconnect() {
		connection.disconnect();
	}

	/**
	 * @param btnMoveN
	 *            the btnMoveN to set
	 */
	public void setBtnMoveN(Button btnMoveN) {
		this.btnMoveN = btnMoveN;
		this.btnMoveN.setOnTouchListener(this);
	}

	/**
	 * @param btnMoveS
	 *            the btnMoveS to set
	 */
	public void setBtnMoveS(Button btnMoveS) {
		this.btnMoveS = btnMoveS;
		this.btnMoveS.setOnTouchListener(this);
	}

	/**
	 * @param btnMoveE
	 *            the btnMoveE to set
	 */
	public void setBtnMoveE(Button btnMoveE) {
		this.btnMoveE = btnMoveE;
		this.btnMoveE.setOnTouchListener(this);
	}

	/**
	 * @param btnMoveW
	 *            the btnMoveW to set
	 */
	public void setBtnMoveW(Button btnMoveW) {
		this.btnMoveW = btnMoveW;
		this.btnMoveW.setOnTouchListener(this);
	}

	/**
	 * @param btnMoveNE
	 *            the btnMoveNE to set
	 */
	public void setBtnMoveNE(Button btnMoveNE) {
		this.btnMoveNE = btnMoveNE;
		this.btnMoveNE.setOnTouchListener(this);
	}

	/**
	 * @param btnMoveNW
	 *            the btnMoveNW to set
	 */
	public void setBtnMoveNW(Button btnMoveNW) {
		this.btnMoveNW = btnMoveNW;
		this.btnMoveNW.setOnTouchListener(this);
	}

	/**
	 * @param btnMoveSE
	 *            the btnMoveSE to set
	 */
	public void setBtnMoveSE(Button btnMoveSE) {
		this.btnMoveSE = btnMoveSE;
		this.btnMoveSE.setOnTouchListener(this);
	}

	/**
	 * @param btnMoveSW
	 *            the btnMoveSW to set
	 */
	public void setBtnMoveSW(Button btnMoveSW) {
		this.btnMoveSW = btnMoveSW;
		this.btnMoveSW.setOnTouchListener(this);
	}

	/**
	 * @param btnStop
	 *            the btnStop to set
	 */
	public void setBtnStop(Button btnStop) {
		this.btnStop = btnStop;
		this.btnStop.setOnClickListener(this);
	}

	/**
	 * @param btnSpeedUp
	 *            the btnSpeedUp to set
	 */
	public void setBtnSpeedUp(Button btnSpeedUp) {
		this.btnSpeedUp = btnSpeedUp;
		this.btnSpeedUp.setOnClickListener(this);
	}

	/**
	 * @param btnSpeedDown
	 *            the btnSpeedDown to set
	 */
	public void setBtnSpeedDown(Button btnSpeedDown) {
		this.btnSpeedDown = btnSpeedDown;
		this.btnSpeedDown.setOnClickListener(this);
	}

	/**
	 * @param speedText
	 *            the speedText to set
	 */
	public void setSpeedText(TextView speedText) {
		this.speedText = speedText;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		SwitchStatus status,negStatus;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			status = SwitchStatus.ON;
			negStatus = SwitchStatus.OFF;
			//log("button pressed");
			// v.setPressed(true);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			status = SwitchStatus.OFF;
			negStatus = SwitchStatus.OFF;
			//log("button released");
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
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
			}
			return true;
		case R.id.buttonW:
			try {
				telescopeMotionWE.setDesiredValue(status);
				telescopeMotionEE.setDesiredValue(negStatus);
				telescopeMotionWEP.sendChangesToDriver();
			} catch (INDIValueException e) {
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
			}
			return true;
		case R.id.buttonN:
			try {
				telescopeMotionNE.setDesiredValue(status);
				telescopeMotionSE.setDesiredValue(negStatus);
				telescopeMotionNSP.sendChangesToDriver();
			} catch (INDIValueException e) {
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
			}
			return true;
		case R.id.buttonS:
			try {
				telescopeMotionSE.setDesiredValue(status);
				telescopeMotionNE.setDesiredValue(negStatus);
				telescopeMotionNSP.sendChangesToDriver();
			} catch (INDIValueException e) {
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
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
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
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
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
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
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
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
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
			}
			return true;
		default:
			log("unknown view");
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonStop:
			try {
				if(telescopeMotionWEP!=null){
					telescopeMotionWE.setDesiredValue(SwitchStatus.OFF);
					telescopeMotionEE.setDesiredValue(SwitchStatus.OFF);
					telescopeMotionWEP.sendChangesToDriver();
				}
				if(telescopeMotionNSP!=null){
					telescopeMotionSE.setDesiredValue(SwitchStatus.OFF);
					telescopeMotionNE.setDesiredValue(SwitchStatus.OFF);
					telescopeMotionNSP.sendChangesToDriver();
				}
				if(telescopeMotionAbort!=null){
					telescopeMotionAbortE.setDesiredValue(SwitchStatus.ON);
					telescopeMotionAbort.sendChangesToDriver();
				}
			} catch (INDIValueException e) {
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
			}
			break;
		case R.id.buttonSpeedUp:
			try {
				double speed = telescopeMotionRate.getElement("MOTION_RATE").getValue();
				double maxSpeed = telescopeMotionRate.getElement("MOTION_RATE").getMax();
				speed = Math.min(maxSpeed, speed * 2);
				telescopeMotionRate.getElement("MOTION_RATE").setDesiredValue(speed);
				telescopeMotionRate.sendChangesToDriver();
			} catch (INDIValueException e) {
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
			}
			break;
		case R.id.buttonSpeedDown:
			try {
				double speed = telescopeMotionRate.getElement("MOTION_RATE").getValue();
				double minSpeed = telescopeMotionRate.getElement("MOTION_RATE").getMin();
				speed = Math.max(minSpeed, speed * 0.5);
				telescopeMotionRate.getElement("MOTION_RATE").setDesiredValue(speed);
				telescopeMotionRate.sendChangesToDriver();
			} catch (INDIValueException e) {
				log(e.getMessage());
			} catch (IOException e) {
				log(e.getMessage());
			}
			break;
		default:
			log("unknown view");
		}

	}

}
