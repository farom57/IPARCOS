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


public class INDIAdapter implements INDIServerConnectionListener {

	private static final INDIAdapter INSTANCE = new INDIAdapter();

	private INDIServerConnection connection;



	private TextView logView;
	private Button connectionButton = null;

	
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


	@Override
	public void newDevice(INDIServerConnection connection, INDIDevice device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDevice(INDIServerConnection connection, INDIDevice device) {
		// TODO Auto-generated method stub
		
	}

	

	

}
