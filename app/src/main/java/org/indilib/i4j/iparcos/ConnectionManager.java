package org.indilib.i4j.iparcos;

import android.content.res.Resources;

import org.indilib.i4j.client.INDIDevice;
import org.indilib.i4j.client.INDIDeviceListener;
import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDIServerConnection;
import org.indilib.i4j.client.INDIServerConnectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


/**
 * Manages an {@link INDIServerConnection} object, listens to INDI messages and notifies listeners.
 *
 * @author marcocipriani01
 */
public class ConnectionManager implements INDIServerConnectionListener, INDIDeviceListener {

    /**
     * A list to re-add the listener when the connection is destroyed and recreated.
     */
    private final ArrayList<INDIServerConnectionListener> listeners;
    /**
     * The connection to the INDI server.
     */
    private INDIServerConnection connection;

    /**
     * Class constructor.
     */
    public ConnectionManager() {
        listeners = new ArrayList<>();
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
        final Resources resources = IPARCOSApp.getAppResources();
        if (!isConnected()) {
            IPARCOSApp.setState(IPARCOSApp.ConnectionState.CONNECTING);
            IPARCOSApp.log(resources.getString(R.string.try_to_connect) + host + ":" + port);
            new Thread(() -> {
                try {
                    connection = new INDIServerConnection(host, port);
                    connection.addINDIServerConnectionListener(this);
                    for (INDIServerConnectionListener l : listeners) {
                        connection.addINDIServerConnectionListener(l);
                    }
                    connection.connect();
                    connection.askForDevices();
                    IPARCOSApp.log(resources.getString(R.string.connected));
                    IPARCOSApp.setState(IPARCOSApp.ConnectionState.CONNECTED);
                } catch (IOException e) {
                    IPARCOSApp.log(e.getLocalizedMessage());
                    IPARCOSApp.setState(IPARCOSApp.ConnectionState.DISCONNECTED);
                }
            }).start();
        } else {
            IPARCOSApp.log("Already connected!");
        }
    }

    /**
     * Breaks the connection.
     */
    public void disconnect() {
        if (isConnected()) {
            new Thread(() -> connection.disconnect()).start();
        } else {
            IPARCOSApp.log("Not connected!");
        }
    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        device.addINDIDeviceListener(this);
        IPARCOSApp.log("New device: " + device.getName());
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        device.removeINDIDeviceListener(this);
        IPARCOSApp.log("Device removed: " + device.getName());
    }

    @Override
    public void connectionLost(INDIServerConnection connection) {
        Resources resources = IPARCOSApp.getAppResources();
        IPARCOSApp.log(resources.getString(R.string.connection_lost));
        IPARCOSApp.setState(IPARCOSApp.ConnectionState.DISCONNECTED);
        IPARCOSApp.goToConnectionTab();
    }

    @Override
    public void newMessage(INDIServerConnection connection, Date timestamp, String message) {
        IPARCOSApp.log(message);
    }

    /**
     * Add a INDIServerConnectionListener to the connection. If the connection
     * is re-created, the listener will be re-installed
     *
     * @param arg the listener
     */
    public void addListener(INDIServerConnectionListener arg) {
        if (connection != null) {
            listeners.add(arg);
            connection.addINDIServerConnectionListener(arg);
        }
    }

    /**
     * Removes the given listener
     *
     * @param arg the listener
     */
    public void removeListener(INDIServerConnectionListener arg) {
        if (connection != null) {
            listeners.remove(arg);
            connection.removeINDIServerConnectionListener(arg);
        }
    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty<?> property) {

    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty<?> property) {

    }

    @Override
    public void messageChanged(INDIDevice device) {
        IPARCOSApp.log(device.getName() + ": " + device.getLastMessage());
    }
}