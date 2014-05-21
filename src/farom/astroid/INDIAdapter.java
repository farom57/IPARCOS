package farom.astroid;

import java.io.IOException;
import java.util.Date;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import laazotea.indi.Constants.*;
import laazotea.indi.client.*;

public class INDIAdapter implements INDIDeviceListener, INDIPropertyListener, INDIServerConnectionListener {

	private static final INDIAdapter INSTANCE = new INDIAdapter();
	private TextView logView;
	private INDIServerConnection connection;
	private INDISwitchProperty telescopeMotionNSP;
	private INDISwitchProperty telescopeMotionWEP;
	private Button connectionButton;




	private INDIAdapter() {}
	

	public static INDIAdapter getInstance(){
		return INSTANCE;
	}

	public void connect(java.lang.String host, int port) {
		connectionButton.setText(R.string.connecting);
		log("Try to connect to "+host+":"+port);
		connection = new INDIServerConnection(host, 7624);
		connection.addINDIServerConnectionListener(this); // We listen to all
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



	

//	public void moveNorth() {
//		try {
//			telescopeMotionNSP.getElement("MOTION_NORTH").setDesiredValue(SwitchStatus.ON);
//			telescopeMotionNSP.getElement("MOTION_SOUTH").setDesiredValue(SwitchStatus.OFF);
//			telescopeMotionNSP.sendChangesToDriver();
//		} catch (INDIValueException e) {
//			log(e.getMessage());
//		} catch (IOException e) {
//			log(e.getMessage());
//		}
//	}
//
//	public void moveSouth() {
//		try {
//			telescopeMotionNSP.getElement("MOTION_NORTH").setDesiredValue(SwitchStatus.OFF);
//			telescopeMotionNSP.getElement("MOTION_SOUTH").setDesiredValue(SwitchStatus.ON);
//			telescopeMotionNSP.sendChangesToDriver();
//		} catch (INDIValueException e) {
//			log(e.getMessage());
//		} catch (IOException e) {
//			log(e.getMessage());
//		}
//	}
//
//	public void moveEast() {
//		try {
//			telescopeMotionWEP.getElement("MOTION_EAST").setDesiredValue(SwitchStatus.ON);
//			telescopeMotionWEP.getElement("MOTION_WEST").setDesiredValue(SwitchStatus.OFF);
//			telescopeMotionWEP.sendChangesToDriver();
//		} catch (INDIValueException e) {
//			log(e.getMessage());
//		} catch (IOException e) {
//			log(e.getMessage());
//		}
//	}
//
//	public void moveWest() {
//		try {
//			telescopeMotionWEP.getElement("MOTION_WEST").setDesiredValue(SwitchStatus.ON);
//			telescopeMotionWEP.getElement("MOTION_EAST").setDesiredValue(SwitchStatus.OFF);
//			telescopeMotionWEP.sendChangesToDriver();
//		} catch (INDIValueException e) {
//			log(e.getMessage());
//		} catch (IOException e) {
//			log(e.getMessage());
//		}
//	}
//
//	public void stopMove() {
//		try {
//			telescopeMotionWEP.getElement("MOTION_WEST").setDesiredValue(SwitchStatus.OFF);
//			telescopeMotionWEP.getElement("MOTION_EAST").setDesiredValue(SwitchStatus.OFF);
//			telescopeMotionNSP.getElement("MOTION_NORTH").setDesiredValue(SwitchStatus.OFF);
//			telescopeMotionNSP.getElement("MOTION_SOUTH").setDesiredValue(SwitchStatus.OFF);
//			telescopeMotionNSP.sendChangesToDriver();
//			telescopeMotionWEP.sendChangesToDriver();
//		} catch (INDIValueException e) {
//			log(e.getMessage());
//		} catch (IOException e) {
//			log(e.getMessage());
//		}
//	}

	

	/**
	 * @return the logView
	 */
	public TextView getLogView() {
		return logView;
	}


	/**
	 * @param logView the logView to set
	 */
	public void setLogView(TextView logView) {
		this.logView = logView;
		log("application started");
	}

	/**
	 * @param text The text to display in the log view
	 */
	public void log(final String text) {
		 logView.post(new Runnable() {
			 public void run() {
				 logView.append(text+"\n");
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
		 log("New Server Message: " + timestamp + " - " + message);
	}

	@Override
	public void newProperty(INDIDevice device, INDIProperty property) {
		// We just simply listen to this Property
		 log("New Property (" + property.getName() + ") added to device " +
		 device.getName());
//		if (property.getName().equals("TELESCOPE_MOTION_NS")) {
//			telescopeMotionNSP = (INDISwitchProperty) property;
//			property.addINDIPropertyListener(this);
//			log("--New Property (" + property.getName() + ") added to device " + device.getName());
//		}
//		if (property.getName().equals("TELESCOPE_MOTION_WE")) {
//			telescopeMotionWEP = (INDISwitchProperty) property;
//			property.addINDIPropertyListener(this);
//			log("--New Property (" + property.getName() + ") added to device " + device.getName());
//		}

	}

	@Override
	public void removeProperty(INDIDevice device, INDIProperty property) {
		// We just remove ourselves as a listener of the removed property
		 log("Property (" + property.getName() + ") removed from device " +
		 device.getName());
		property.removeINDIPropertyListener(this);
	}

	@Override
	public void messageChanged(INDIDevice device) {
		 log("New Device Message: " + device.getName() + " - " +
		 device.getTimestamp() + " - " + device.getLastMessage());
	}

	@Override
	public void propertyChanged(INDIProperty property) {
		 log("Property Changed: " + property.getNameStateAndValuesAsString());
	}
	
	/**
	 * @return the connectionButton
	 */
	public Button getConnectionButton() {
		return connectionButton;
	}


	/**
	 * @param connectionButton the connectionButton to set
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

}
