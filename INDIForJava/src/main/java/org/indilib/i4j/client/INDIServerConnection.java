package org.indilib.i4j.client;

/*
 * #%L
 * INDI for Java Client Library
 * %%
 * Copyright (C) 2013 - 2014 indiforjava
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.indilib.i4j.protocol.DefVector;
import org.indilib.i4j.protocol.DelProperty;
import org.indilib.i4j.protocol.GetProperties;
import org.indilib.i4j.protocol.INDIProtocol;
import org.indilib.i4j.protocol.Message;
import org.indilib.i4j.protocol.SetVector;
import org.indilib.i4j.protocol.api.INDIConnection;
import org.indilib.i4j.protocol.api.INDIInputStream;
import org.indilib.i4j.protocol.url.INDIURLStreamHandler;
import org.indilib.i4j.Constants;
import org.indilib.i4j.INDIDateFormat;
import org.indilib.i4j.INDIProtocolParser;
import org.indilib.i4j.INDIProtocolReader;
import org.indilib.i4j.protocol.url.INDIURLStreamHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing a INDI Server Connection. Usually this is the entry
 * point for any INDI Client to connect to the server, retrieve all devices and
 * properties and so on.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @author Richard van Nieuwenhoven
 */
public class INDIServerConnection implements INDIProtocolParser {

    static {
        INDIURLStreamHandlerFactory.init();
    }

    /**
     * The logger to log to.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDIServerConnection.class);

    /**
     * The url of the Connection.
     */
    private final URL indiUrl;

    /**
     * The socket used in the Connection.
     */
    private INDIConnection connection = null;

    /**
     * A reader to read from the Connection.
     */
    private INDIProtocolReader reader = null;

    /**
     * The set of the devices associated to this Connection.
     */
    private final Map<String, INDIDevice> devices = new LinkedHashMap<String, INDIDevice>();

    /**
     * The list of Listeners of this Connection.
     */
    private List<INDIServerConnectionListener> listeners = new ArrayList<INDIServerConnectionListener>();

    /**
     * Constructs an instance of <code>INDIServerConnection</code>. The
     * Connection is NOT stablished.
     * 
     * @param name
     *            The elementName of the Connection.
     * @param host
     *            The host of the Connection.
     * @param port
     *            The port of the Connection.
     */
    public INDIServerConnection(String name, String host, int port) {
        this(host, port);
    }

    /**
     * Constructs an instance of <code>INDIServerConnection</code> with no
     * elementName. The Connection is NOT stablished.
     * 
     * @param host
     *            The host of the Connection.
     * @param port
     *            The port of the Connection.
     */
    public INDIServerConnection(String host, int port) {
        try {
            indiUrl = new URL(INDIURLStreamHandler.PROTOCOL, host, port, "/");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("illegal indi url", e);
        }
    }

    /**
     * Constructs an instance of <code>INDIServerConnection</code> with no
     * elementName. The parameter can take the form of an indi uri. If the URI
     * is not correct it will be used as the host of the connection. The
     * Connection is NOT stablished.
     * 
     * @param uri
     *            The INDIURI that specifies the parameters of the Connection.
     *            If the URI is not correct it will be used as the host of the
     *            connection and the default port will be used.
     */
    public INDIServerConnection(String uri) {
        this(openConnection(uri));
    }

    /**
     * open a indi connection to the specified uri.
     * 
     * @param uri
     *            the uri to parse and connect to
     * @return the initalized connection.
     */
    private static INDIConnection openConnection(String uri) {
        try {
            return (INDIConnection) new URL(uri).openConnection();
        } catch (Exception e) {
            throw new IllegalArgumentException("the specified uri is not a legal indi url", e);
        }
    }

    /**
     * create a new server connection based on a indiconnection.
     * 
     * @param connection
     *            the indi connection to base on.
     */
    public INDIServerConnection(INDIConnection connection) {
        indiUrl = connection.getURL();
        this.connection = connection;
    }

    /**
     * This function waits until a Device with a <code>deviceName</code> exists
     * in this Connection and returns it. The wait is dinamic, so it should be
     * called from a different Thread or the app will freeze until the Device
     * exists.
     * 
     * @param deviceName
     *            The deviceName of the evice to wait for.
     * @return The Device once it exists in this Connection.
     */
    public INDIDevice waitForDevice(String deviceName) {
        return waitForDevice(deviceName, Integer.MAX_VALUE);
    }

    /**
     * This function waits until a Device with a <code>deviceName</code> exists
     * in this Connection and returns it. The wait is dinamic, so it should be
     * called from a different Thread or the app will freeze until the Device
     * exists or the <code>maxWait</code> number of seconds have elapsed.
     * 
     * @param deviceName
     *            The deviceName of the device to wait for.
     * @param maxWait
     *            Maximum number of seconds to wait for the Device
     * @return The Device once it exists in this Connection or <code>null</code>
     *         if the maximum wait is achieved.
     */
    public INDIDevice waitForDevice(String deviceName, int maxWait) {
        INDIDevice d = null;

        long startTime = new Date().getTime();
        boolean timeElapsed = false;

        while (d == null && !timeElapsed) {
            d = this.getDevice(deviceName);

            if (d == null) {
                try {
                    Thread.sleep(Constants.WAITING_INTERVAL);
                } catch (InterruptedException e) {
                    LOG.warn("sleep interrupted", e);
                }
            }

            long endTime = new Date().getTime();

            if ((endTime - startTime) / Constants.MILLISECONDS_IN_A_SECOND > maxWait) {
                timeElapsed = true;
            }
        }

        return d;
    }

    /**
     * Connects to the INDI Server.
     * 
     * @throws IOException
     *             if there is some problem connecting to the Server.
     */
    public void connect() throws IOException {
        if (connection == null) {
            connection = (INDIConnection) indiUrl.openConnection();
        }
        if (reader == null) {
            reader = new INDIProtocolReader(this, "client reader " + connection.getURL());
            reader.start();
        }
    }

    /**
     * If connected, disconnects from the INDI Server and notifies the
     * listeners.
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                LOG.error("error durill connection close", e);
            }

            devices.clear();
            notifyListenersConnectionLost();
            connection = null;
        }
    }

    /**
     * Determines if the Connection is stablished of not.
     * 
     * @return <code>true</code> if the Connection is stablished.
     *         <code>false</code> otherwise.
     */
    public boolean isConnected() {
        if (connection == null) {
            return false;
        }

        return true;
    }

    /**
     * Sends the appropriate message to the INDI Server to be notified about the
     * Devices of the Server.
     * 
     * @throws IOException
     *             if there is some problem with the Connection.
     */
    public void askForDevices() throws IOException {
        sendMessageToServer(new GetProperties().setVersion("1.7"));
    }

    /**
     * Sends the appropriate message to the INDI Server to be notified about a
     * particular Device of the Server.
     * 
     * @param device
     *            the Device elementName that is asked for.
     * @throws IOException
     *             if there is some problem with the Connection.
     */
    public void askForDevices(String device) throws IOException {
        sendMessageToServer(new GetProperties().setVersion("1.7").setDevice(device));
    }

    /**
     * Sends the appropriate message to the INDI Server to be notified about a
     * particular Property of a particular Device of the Server.
     * 
     * @param device
     *            the Device elementName of whose property is asked for.
     * @param propertyName
     *            the Property elementName that is asked for.
     * @throws IOException
     *             if there is some problem with the Connection.
     */
    public void askForDevices(String device, String propertyName) throws IOException {
        sendMessageToServer(new GetProperties().setVersion("1.7").setDevice(device).setName(propertyName));
    }

    /**
     * Sends a xmlMessage message to the server.
     * 
     * @param xmlMessage
     *            the message to be sent.
     * @throws IOException
     *             if there is some problem with the Connection.
     */
    protected void sendMessageToServer(INDIProtocol<?> xmlMessage) throws IOException {
        connection.getINDIOutputStream().writeObject(xmlMessage);
    }

    @Override
    public void finishReader() {
        disconnect(); // If there has been a problem with reading the port
                      // really disconnect and notify listeners
    }

    /**
     * Adds a new Device to this Connection and notifies the listeners.
     * 
     * @param device
     *            the device to be added.
     */
    private void addDevice(INDIDevice device) {
        devices.put(device.getName(), device);

        notifyListenersNewDevice(device);
    }

    /**
     * Gets a particular Device by its elementName.
     * 
     * @param deviceName
     *            the deviceName of the Device
     * @return the Device with the <code>deviceName</code> or <code>null</code>
     *         if there is no Device with that deviceName.
     */
    public INDIDevice getDevice(String deviceName) {
        return devices.get(deviceName);
    }

    /**
     * A convenience method to get the Property of a Device by specifiying their
     * names.
     * 
     * @param deviceName
     *            the elementName of the Device.
     * @param propertyName
     *            the elementName of the Property.
     * @return the Property with <code>propertyName</code> as elementName of the
     *         device with <code>deviceName</code> as elementName.
     */
    public INDIProperty<?> getProperty(String deviceName, String propertyName) {
        INDIDevice d = getDevice(deviceName);
        if (d == null) {
            return null;
        }
        return d.getProperty(propertyName);
    }

    /**
     * A convenience method to get the Element of a Property of a Device by
     * specifiying their names.
     * 
     * @param deviceName
     *            the elementName of the Property.
     * @param propertyName
     *            the elementName of the Element.
     * @param elementName
     *            the elementName of the Element.
     * @return the Element with a <code>elementName</code> as a elementName of a
     *         Property with <code>propertyName</code> as elementName of the
     *         device with <code>deviceName</code> as elementName.
     */
    public INDIElement getElement(String deviceName, String propertyName, String elementName) {
        INDIDevice d = getDevice(deviceName);

        if (d == null) {
            return null;
        }

        return d.getElement(propertyName, elementName);
    }

    @Override
    public void processProtokolMessage(INDIProtocol<?> xml) {
        if (xml instanceof DefVector<?>) {
            addProperty((DefVector<?>) xml);
        } else if (xml instanceof SetVector<?>) {
            updateProperty((SetVector<?>) xml);
        } else if (xml instanceof Message) {
            messageReceived((Message) xml);
        } else if (xml instanceof DelProperty) {
            deleteProperty((DelProperty) xml);
        }
    }

    /**
     * Parses a XML &lt;delProperty&gt; element and notifies the listeners.
     * 
     * @param xml
     *            The element to be parsed.
     */
    private void deleteProperty(DelProperty xml) {
        if (xml.hasDevice()) {
            String deviceName = xml.getDevice();

            INDIDevice d = getDevice(deviceName);

            if (d != null) {
                String propertyName = xml.getName();

                if (propertyName != null && !propertyName.isEmpty()) {
                    d.deleteProperty(xml);
                } else {
                    deleteDevice(d);
                }
            } else {
                deleteAllDevices();
            }
        }
    }

    /**
     * Deletes all the Devices from the Connection and notifies the listeners.
     */
    private void deleteAllDevices() {
        Iterator<INDIDevice> devs = devices.values().iterator();

        while (!devs.hasNext()) {
            deleteDevice(devs.next());
        }
    }

    /**
     * Deletes a Device from the Connection and notifies the listeners.
     * 
     * @param device
     *            the Device to be removed.
     */
    private void deleteDevice(INDIDevice device) {
        devices.remove(device.getName());

        notifyListenersRemoveDevice(device);
    }

    /**
     * Parses a XML &lt;message&gt; element and notifies the listeners if
     * appropriate.
     * 
     * @param xml
     *            The XML to be parsed.
     */
    private void messageReceived(Message xml) {
        if (xml.hasDevice()) {
            String deviceName = xml.getDevice();
            INDIDevice d = getDevice(deviceName);
            if (d != null) {
                d.messageReceived(xml);
            }
        } else { // Global message from server
            if (xml.hasMessage()) {
                Date timestamp = INDIDateFormat.dateFormat().parseTimestamp(xml.getTimestamp());
                notifyListenersNewMessage(timestamp, xml.getMessage());
            }
        }
    }

    /**
     * Parses a XML &lt;defXXXVector&gt; element.
     * 
     * @param xml
     *            the element to be parsed.
     */
    private void addProperty(DefVector<?> xml) {
        String deviceName = xml.getDevice();

        INDIDevice d = getDevice(deviceName);

        if (d == null) {
            d = new INDIDevice(deviceName, this);
            addDevice(d);
        }

        d.addProperty(xml);
    }

    /**
     * Parses a XML &lt;setXXXVector&gt; element.
     * 
     * @param el
     *            the element to be parsed.
     */
    private void updateProperty(SetVector<?> el) {
        String deviceName = el.getDevice();

        INDIDevice d = getDevice(deviceName);

        if (d != null) { // If device does no exist ignore
            d.updateProperty(el);
        }
    }

    /**
     * Adds a new listener to this Connection.
     * 
     * @param listener
     *            the listener to be added.
     */
    public void addINDIServerConnectionListener(INDIServerConnectionListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from this Connection.
     * 
     * @param listener
     *            the listener to be removed.
     */
    public void removeINDIServerConnectionListener(INDIServerConnectionListener listener) {
        listeners.remove(listener);
    }

    /**
     * Adds a listener to all the Devices from this Connection.
     * 
     * @param listener
     *            the listener to add
     */
    public void addINDIDeviceListenerToAllDevices(INDIDeviceListener listener) {
        List<INDIDevice> l = getDevicesAsList();

        for (int i = 0; i < l.size(); i++) {
            INDIDevice d = l.get(i);

            d.addINDIDeviceListener(listener);
        }
    }

    /**
     * Removes a listener from all the Devices from this Connection.
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeINDIDeviceListenerFromAllDevices(INDIDeviceListener listener) {
        List<INDIDevice> l = getDevicesAsList();

        for (int i = 0; i < l.size(); i++) {
            INDIDevice d = l.get(i);

            d.removeINDIDeviceListener(listener);
        }
    }

    /**
     * Gets the names of the Devices of this Connection.
     * 
     * @return the names of the Devices of this Connection.
     */
    public String[] getDeviceNames() {
        List<INDIDevice> l = getDevicesAsList();

        String[] names = new String[l.size()];

        for (int i = 0; i < l.size(); i++) {
            names[i] = l.get(i).getName();
        }

        return names;
    }

    /**
     * Gets a <code>List</code> with all the Devices of this Connection.
     * 
     * @return the <code>List</code> of Devices belonging to this Connection.
     */
    public List<INDIDevice> getDevicesAsList() {
        return new ArrayList<INDIDevice>(devices.values());
    }

    /**
     * A convenience method to add a listener to a Device (identified by its
     * elementName) of this Connection.
     * 
     * @param deviceName
     *            the Device elementName to which add the listener
     * @param listener
     *            the listener to add
     */
    public void addINDIDeviceListener(String deviceName, INDIDeviceListener listener) {
        INDIDevice d = getDevice(deviceName);

        if (d == null) {
            return;
        }

        d.addINDIDeviceListener(listener);
    }

    /**
     * A convenience method to remove a listener from a Device (identified by
     * its elementName) of this Connection.
     * 
     * @param deviceName
     *            the Device elementName to which remove the listener
     * @param listener
     *            the listener to remove
     */
    public void removeINDIDeviceListener(String deviceName, INDIDeviceListener listener) {
        INDIDevice d = getDevice(deviceName);

        if (d == null) {
            return;
        }

        d.removeINDIDeviceListener(listener);
    }

    /**
     * Notifies the listeners about a new Device.
     * 
     * @param device
     *            the new Device.
     */
    private void notifyListenersNewDevice(INDIDevice device) {
        ArrayList<INDIServerConnectionListener> lCopy = new ArrayList<>(listeners);

        for (int i = 0; i < lCopy.size(); i++) {
            INDIServerConnectionListener l = lCopy.get(i);

            l.newDevice(this, device);
        }
    }

    /**
     * Notifies the listeners about a Device that is removed.
     * 
     * @param device
     *            the removed device.
     */
    private void notifyListenersRemoveDevice(INDIDevice device) {
        ArrayList<INDIServerConnectionListener> lCopy = new ArrayList<>(listeners);

        for (int i = 0; i < lCopy.size(); i++) {
            INDIServerConnectionListener l = lCopy.get(i);

            l.removeDevice(this, device);
        }
    }

    /**
     * Notifies the listeners when the Connection is lost.
     */
    private void notifyListenersConnectionLost() {
        ArrayList<INDIServerConnectionListener> lCopy = new ArrayList<>(listeners);

        for (int i = 0; i < lCopy.size(); i++) {
            INDIServerConnectionListener l = lCopy.get(i);
            l.connectionLost(this);
        }
    }

    /**
     * Notifies the listeners about a new Server message.
     * 
     * @param timestamp
     *            the timestamp of the message.
     * @param message
     *            the message.
     */
    protected void notifyListenersNewMessage(Date timestamp, String message) {
        ArrayList<INDIServerConnectionListener> lCopy = new ArrayList<>(listeners);

        for (int i = 0; i < lCopy.size(); i++) {
            INDIServerConnectionListener l = lCopy.get(i);

            l.newMessage(this, timestamp, message);
        }
    }

    /**
     * Gets the input stream of this Connection.
     * 
     * @return The input stream of this Connection.
     */
    @Override
    public INDIInputStream getInputStream() {
        try {
            return connection.getINDIInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + indiUrl + ")";
    }

    /**
     * @return the url behind the server connection.
     */
    public URL getURL() {
        return indiUrl;
    }
}
