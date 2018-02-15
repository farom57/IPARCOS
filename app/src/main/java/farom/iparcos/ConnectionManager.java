package farom.iparcos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIDeviceListener;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;

/**
 * @author SquareBoot
 */
public class ConnectionManager implements INDIServerConnectionListener, INDIDeviceListener {

    /**
     * The connection to the INDI server.
     */
    private INDIServerConnection connection;
    /**
     * A list to re-add the listener when the connection is destroyed and recreated.
     */
    private ArrayList<INDIServerConnectionListener> permanentConnectionListeners;

    /**
     * Class constructor.
     */
    public ConnectionManager() {
        permanentConnectionListeners = new ArrayList<>();
    }

    /**
     * @return the current state of this connection manager (connected or not).
     */
    public boolean isConnected() {
        return (connection != null) && (connection.isConnected());
    }

    /**
     * @return the connection. May be {@code null} if the connection doesn't exist.
     */
    public INDIServerConnection getConnection() {
        return connection;
    }

    /**
     * Connects to the driver
     *
     * @param host the host / IP address of the INDI server
     * @param port the port of the INDI server
     */
    public void connect(String host, int port) {
        if (!isConnected()) {
            Application.setState(Application.getContext().getResources().getString(R.string.connecting));
            Application.log(Application.getContext().getResources().getString(R.string.try_to_connect) + host + ":" + port);

            System.out.println("Trying to connect to " + host + ":" + port);
            connection = new INDIServerConnection(host, port);
            // Listen to all
            connection.addINDIServerConnectionListener(this);
            for (INDIServerConnectionListener permanentConnectionListener : permanentConnectionListeners) {
                connection.addINDIServerConnectionListener(permanentConnectionListener);
            }

            new Thread(new Runnable() {
                public void run() {
                    try {
                        connection.connect();
                        // Ask for all the devices
                        connection.askForDevices();
                        Application.log(Application.getContext().getResources().getString(R.string.connected));
                        Application.setState(Application.getContext().getResources().getString(R.string.disconnect));

                    } catch (IOException e) {
                        Application.log(Application.getContext().getResources().getString(R.string.connection_pb));
                        Application.log(e.getLocalizedMessage());
                        Application.setState(Application.getContext().getResources().getString(R.string.connect));
                    }

                }
            }).start();

        } else {
            Application.log("Already connected!");
        }
    }

    /**
     * Breaks the connection.
     */
    public void disconnect() {
        if (isConnected()) {
            connection.disconnect();
            Application.setState(Application.getContext().getResources().getString(R.string.connect));

        } else {
            Application.log("Not connected!");
        }
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        device.addINDIDeviceListener(this);
        Application.log(Application.getContext().getResources().getString(R.string.new_device) + device.getName());
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        device.removeINDIDeviceListener(this);
        Application.log(Application.getContext().getResources().getString(R.string.device_removed) + device.getName());
    }

    @Override
    public void connectionLost(INDIServerConnection connection) {
        Application.log(Application.getContext().getResources().getString(R.string.connection_lost));
        Application.setState(Application.getContext().getResources().getString(R.string.connect));

        // Move to the connection tab
        Application.goToConnectionTab();
    }

    @Override
    public void newMessage(INDIServerConnection connection, Date timestamp, String message) {
        Application.log(message);
    }

    /**
     * Add a INDIServerConnectionListener to the connection. If the connection
     * is re-created, the listener will be re-installed
     *
     * @param arg the listener
     */
    public void registerPermanentConnectionListener(INDIServerConnectionListener arg) {
        permanentConnectionListeners.add(arg);
        if (connection != null) {
            connection.addINDIServerConnectionListener(arg);
        }
    }

    /**
     * Removes the given listener
     *
     * @param arg the listener
     */
    public void unRegisterPermanentConnectionListener(INDIServerConnectionListener arg) {
        permanentConnectionListeners.remove(arg);
        if (connection != null) {
            connection.removeINDIServerConnectionListener(arg);
        }
    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty property) {

    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty property) {

    }

    @Override
    public void messageChanged(INDIDevice device) {
        Application.log(device.getName() + ": " + device.getLastMessage());
    }
}