package farom.astroid;

import java.io.IOException;
import java.util.Date;

import laazotea.indi.Constants;
import laazotea.indi.Constants.SwitchStatus;
import laazotea.indi.client.*;

public class Scope implements INDIServerConnectionListener, INDIDeviceListener, INDIPropertyListener {

	protected int speed = 1;
	protected LogTextBox mLogBox;
	protected static Scope lastInstance = null;
	private INDIServerConnection connection;
	protected INDISwitchProperty telescopeMotionNSP;
	protected INDISwitchProperty telescopeMotionWEP;

	public Scope() {
		lastInstance = this;

		connection = new INDIServerConnection("192.168.43.71", 7624);

		connection.addINDIServerConnectionListener(this); // We listen to all
															// server events

	}

	public static Scope getLastInstance() {
		if (lastInstance == null)
			new Scope();
		return lastInstance;
	}

	public void setLogBox(LogTextBox logBox) {
		mLogBox = logBox;
		log("Log ");
		log("Type : Scope");
	}

	public void connect() {
		log("Connection ...");

//		new Thread() {
//			@Override
//			public void run() {
//				try {
//					connection.connect();
//					connection.askForDevices(); // Ask for all the devices.
//					log("connection ok");
//				} catch (IOException e) {
//					log("Problem with the connection");
//					log(e.getMessage());
//				}
//			}
//		}.start();

		new Thread(new Runnable() {
			public void run() {
				try {
					connection.connect();
					connection.askForDevices(); // Ask for all the devices.
					log("connection ok");
				} catch (IOException e) {
							log("Problem with the connection");
							log(e.getMessage());
				}

			}
		}).start();

	}

	public void moveNorth() {
		try {
			telescopeMotionNSP.getElement("MOTION_NORTH").setDesiredValue(SwitchStatus.ON);
			telescopeMotionNSP.getElement("MOTION_SOUTH").setDesiredValue(SwitchStatus.OFF);
			telescopeMotionNSP.sendChangesToDriver();
		} catch (INDIValueException e) {
			log(e.getMessage());
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	public void moveSouth() {
		try {
			telescopeMotionNSP.getElement("MOTION_NORTH").setDesiredValue(SwitchStatus.OFF);
			telescopeMotionNSP.getElement("MOTION_SOUTH").setDesiredValue(SwitchStatus.ON);
			telescopeMotionNSP.sendChangesToDriver();
		} catch (INDIValueException e) {
			log(e.getMessage());
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	public void moveEast() {
		try {
			telescopeMotionWEP.getElement("MOTION_EAST").setDesiredValue(SwitchStatus.ON);
			telescopeMotionWEP.getElement("MOTION_WEST").setDesiredValue(SwitchStatus.OFF);
			telescopeMotionWEP.sendChangesToDriver();
		} catch (INDIValueException e) {
			log(e.getMessage());
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	public void moveWest() {
		try {
			telescopeMotionWEP.getElement("MOTION_WEST").setDesiredValue(SwitchStatus.ON);
			telescopeMotionWEP.getElement("MOTION_EAST").setDesiredValue(SwitchStatus.OFF);
			telescopeMotionWEP.sendChangesToDriver();
		} catch (INDIValueException e) {
			log(e.getMessage());
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	public void stopMove() {
		try {
			telescopeMotionWEP.getElement("MOTION_WEST").setDesiredValue(SwitchStatus.OFF);
			telescopeMotionWEP.getElement("MOTION_EAST").setDesiredValue(SwitchStatus.OFF);
			telescopeMotionNSP.getElement("MOTION_NORTH").setDesiredValue(SwitchStatus.OFF);
			telescopeMotionNSP.getElement("MOTION_SOUTH").setDesiredValue(SwitchStatus.OFF);
			telescopeMotionNSP.sendChangesToDriver();
			telescopeMotionWEP.sendChangesToDriver();
		} catch (INDIValueException e) {
			log(e.getMessage());
		} catch (IOException e) {
			log(e.getMessage());
		}
	}

	public void speedUp() {
		if (speed < 4)
			speed++;
		setSpeed();
	}

	public void speedDown() {
		if (speed > 1)
			speed--;
		setSpeed();
	}

	protected void setSpeed() {
		switch (speed) {
		case 1:
			sendCommand(":RG#");
			break;
		case 2:
			sendCommand(":RC#");
			break;
		case 3:
			sendCommand(":RM#");
			break;
		case 4:
			sendCommand(":RS#");
			break;
		}
	}

	public String getSpeedName() {
		switch (speed) {
		case 1:
			return "Guidage";
		case 2:
			return "Centrage";
		case 3:
			return "Recherche";
		case 4:
			return "Max";
		}
		return "???";
	}

	protected void sendCommand(String command) {
		log("Send : " + command);

	}

	public void log(final String text) {
		mLogBox.post(new Runnable() {
			public void run() {
				mLogBox.append(text);
			}
		});
	}

	@Override
	public void newDevice(INDIServerConnection connection, INDIDevice device) {
		// We just simply listen to this Device
		log("New device: " + device.getName());
		try {
			device.BLOBsEnable(Constants.BLOBEnables.ALSO); // Enable receiving
															// BLOBs from this
															// Device
		} catch (IOException e) {
		}
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

	}

	@Override
	public void newMessage(INDIServerConnection connection, Date timestamp, String message) {
		//log("New Server Message: " + timestamp + " - " + message);
	}

	@Override
	public void newProperty(INDIDevice device, INDIProperty property) {
		// We just simply listen to this Property
		//log("New Property (" + property.getName() + ") added to device " + device.getName());
		if(property.getName().equals("TELESCOPE_MOTION_NS")){
			telescopeMotionNSP=(INDISwitchProperty)property;
			property.addINDIPropertyListener(this);
			log("--New Property (" + property.getName() + ") added to device " + device.getName());
		}
		if(property.getName().equals("TELESCOPE_MOTION_WE")){
			telescopeMotionWEP=(INDISwitchProperty)property;
			property.addINDIPropertyListener(this);
			log("--New Property (" + property.getName() + ") added to device " + device.getName());
		}

	}

	@Override
	public void removeProperty(INDIDevice device, INDIProperty property) {
		// We just remove ourselves as a listener of the removed property
		//log("Property (" + property.getName() + ") removed from device " + device.getName());
		property.removeINDIPropertyListener(this);
	}

	@Override
	public void messageChanged(INDIDevice device) {
		//log("New Device Message: " + device.getName() + " - " + device.getTimestamp() + " - " + device.getLastMessage());
	}

	@Override
	public void propertyChanged(INDIProperty property) {
		//log("Property Changed: " + property.getNameStateAndValuesAsString());
	}

}
