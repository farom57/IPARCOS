package farom.iparcos;

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

    private INDIServerConnection connection;
    // A list to re-add the listener when the connection is destroyed and recreated
    private ArrayList<INDIServerConnectionListener> permanentConnectionListeners;

    /**
     * Class constructor.
     */
    public ConnectionManager() {
        permanentConnectionListeners = new ArrayList<>();
    }

    /**
     * @return the connection. May be {@code null} if the connection doesn't exist.
     */
    public INDIServerConnection getConnection() {
        return connection;
    }

    /**
     * Breaks the connection
     */
    public void disconnect() {
        connection.disconnect();
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        device.addINDIDeviceListener(this);
        appendLog(getString(R.string.new_device) + device.getName());
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        device.removeINDIDeviceListener(this);
        appendLog(getString(R.string.device_removed) + device.getName());
    }

    @Override
    public void connectionLost(INDIServerConnection connection) {
        appendLog(getString(R.string.connection_lost));
        connectionButton.post(new Runnable() {
            public void run() {
                connectionButton.setText(R.string.connect);
                //TODO invalidateOptionsMenu();
            }
        });

        // Open the connection activity TODO
        //Intent intent = new Intent(this, ConnectionFragment.class);
        //startActivity(intent);
    }

    @Override
    public void newMessage(INDIServerConnection connection, Date timestamp, String message) {
        appendLog(message);
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
        appendLog(device.getName() + ": " + device.getLastMessage());
    }
}