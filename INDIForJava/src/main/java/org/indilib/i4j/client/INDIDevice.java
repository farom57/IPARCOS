package org.indilib.i4j.client;

/*
 * #%L INDI for Java Client Library %% Copyright (C) 2013 - 2014 indiforjava %%
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Lesser Public License for more details. You should have received a copy of
 * the GNU General Lesser Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. #L%
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.indilib.i4j.protocol.DefBlobVector;
import org.indilib.i4j.protocol.DefLightVector;
import org.indilib.i4j.protocol.DefNumberVector;
import org.indilib.i4j.protocol.DefSwitchVector;
import org.indilib.i4j.protocol.DefTextVector;
import org.indilib.i4j.protocol.DefVector;
import org.indilib.i4j.protocol.DelProperty;
import org.indilib.i4j.protocol.EnableBLOB;
import org.indilib.i4j.protocol.INDIProtocol;
import org.indilib.i4j.protocol.SetBlobVector;
import org.indilib.i4j.protocol.SetLightVector;
import org.indilib.i4j.protocol.SetNumberVector;
import org.indilib.i4j.protocol.SetSwitchVector;
import org.indilib.i4j.protocol.SetTextVector;
import org.indilib.i4j.protocol.SetVector;
import org.indilib.i4j.Constants;
import org.indilib.i4j.INDIDateFormat;
import org.indilib.i4j.INDIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing a INDI Device.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @author Richard van Nieuwenhoven
 */
public class INDIDevice {

    /**
     * A logger for the errors.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDIDevice.class);

    /**
     * The name of the Device.
     */
    private String name;

    /**
     * The Server Connection to which this Device Belongs.
     */
    private INDIServerConnection server;

    /**
     * The collection of properties for this Device.
     */
    private Map<String, INDIProperty<?>> properties;

    /**
     * The list of Listeners of this Device.
     */
    private List<INDIDeviceListener> listeners;

    /**
     * A UI component that can be used in graphical interfaces for this Device.
     */
    private INDIDeviceListener uiComponent;

    /**
     * The timestamp for the last message.
     */
    private Date timestamp;

    /**
     * The last message of this Device.
     */
    private String message;

    /**
     * The number of <code>BLOBProperties</code> in this Device.
     */
    private int blobCount;

    /**
     * Constructs an instance of <code>INDIDevice</code>. Usually called from a
     * <code>INDIServerConnection</code>.
     * 
     * @param name
     *            the name of this Device
     * @param server
     *            the Server Connection of this Device
     * @see INDIServerConnection
     */
    protected INDIDevice(String name, INDIServerConnection server) {
        this.name = name;
        this.server = server;

        properties = new LinkedHashMap<String, INDIProperty<?>>();

        listeners = new ArrayList<INDIDeviceListener>();

        timestamp = new Date();
        message = "";
        blobCount = 0;
    }

    /**
     * Adds a new listener to this Device.
     * 
     * @param listener
     *            the listener to be added.
     */
    public void addINDIDeviceListener(INDIDeviceListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from this Device.
     * 
     * @param listener
     *            the listener to be removed.
     */
    public void removeINDIDeviceListener(INDIDeviceListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies the listeners of a new <code>INDIProperty</code>.
     * 
     * @param property
     *            The new property
     */
    private void notifyListenersNewProperty(INDIProperty<?> property) {
        for (INDIDeviceListener l : new ArrayList<>(listeners)) {
            l.newProperty(this, property);
        }
    }

    /**
     * Notifies the listeners of a removed <code>INDIProperty</code>.
     * 
     * @param property
     *            The removed property
     */
    private void notifyListenersDeleteProperty(INDIProperty<?> property) {
        for (INDIDeviceListener l : new ArrayList<>(listeners)) {

            l.removeProperty(this, property);
        }
    }

    /**
     * Notifies the listeners that the message of this Device has changed.
     */
    private void notifyListenersMessageChanged() {
        for (INDIDeviceListener l : new ArrayList<>(listeners)) {
            l.messageChanged(this);
        }
    }

    /**
     * Sends the appropriate message to the Server to establish a particular
     * BLOB policy (BLOBEnable) for the Device.
     * 
     * @param enable
     *            The BLOB policy.
     * @throws IOException
     *             if there is some problem sending the message.
     */
    public void blobsEnable(Constants.BLOBEnables enable) throws IOException {
        sendMessageToServer(new EnableBLOB().setDevice(getName()).setTextContent(Constants.getBLOBEnableAsString(enable)));
    }

    /**
     * Sends the appropriate message to the Server to establish a particular
     * BLOB policy (BLOBEnable) for the Device and a particular Property.
     * 
     * @param enable
     *            The BLOB policy.
     * @param property
     *            The Property of the Device to listen to.
     * @throws IOException
     *             if there is some problem sending the message.
     */
    public void blobsEnable(Constants.BLOBEnables enable, INDIProperty<?> property) throws IOException {
        if (properties.containsValue(property) && property instanceof INDIBLOBProperty) {
            sendMessageToServer(new EnableBLOB().setDevice(getName()).setName(property.getName()).setTextContent(Constants.getBLOBEnableAsString(enable)));
        }
    }

    /**
     * Sends the appropriate message to the Server to disallow the receipt of
     * BLOB property changes.
     * 
     * @throws IOException
     *             if there is some problem sending the message.
     * @deprecated Replaced by
     *             {@link #blobsEnable(Constants.BLOBEnables)}
     */
    @Deprecated
    public void blobsEnableNever() throws IOException {
        sendMessageToServer(new EnableBLOB().setDevice(getName()).setTextContent("Never"));
    }

    /**
     * Sends the appropriate message to the Server to allow the receipt of BLOB
     * property changes along with any other property types.
     * 
     * @throws IOException
     *             if there is some problem sending the message.
     * @deprecated Replaced by
     *             {@link #blobsEnable(Constants.BLOBEnables)}
     */
    @Deprecated
    public void blobsEnableAlso() throws IOException {
        sendMessageToServer(new EnableBLOB().setDevice(getName()).setTextContent("Also"));
    }

    /**
     * Sends the appropriate message to the Server to allow the receipt of just
     * BLOB property changes.
     * 
     * @throws IOException
     *             if there is some problem sending the message.
     * @deprecated Replaced by
     *             {@link #blobsEnable(Constants.BLOBEnables)}
     */
    @Deprecated
    public void blobsEnableOnly() throws IOException {
        sendMessageToServer(new EnableBLOB().setDevice(getName()).setTextContent("Only"));
    }

    /**
     * Sends the appropriate message to the Server to disallow the receipt of a
     * particular BLOB property changes.
     * 
     * @param property
     *            the BLOB property
     * @throws IOException
     *             if there is some problem sending the message.
     * @deprecated Replaced by
     *             {@link #blobsEnable(Constants.BLOBEnables, INDIProperty)}
     */
    @Deprecated
    public void blobsEnableNever(INDIBLOBProperty property) throws IOException {
        sendMessageToServer(new EnableBLOB().setDevice(getName()).setName(property.getName()).setTextContent("Never"));
    }

    /**
     * Sends the appropriate message to the Server to allow the receipt of a
     * particular BLOB property changes along with any other property types.
     * 
     * @param property
     *            the BLOB property
     * @throws IOException
     *             if there is some problem sending the message.
     * @deprecated Replaced by
     *             {@link #blobsEnable(Constants.BLOBEnables, INDIProperty)}
     */
    @Deprecated
    public void blobsEnableAlso(INDIBLOBProperty property) throws IOException {
        sendMessageToServer(new EnableBLOB().setDevice(getName()).setName(property.getName()).setTextContent("Also"));
    }

    /**
     * Sends the appropriate message to the Server to allow the receipt of just
     * a particular BLOB property changes.
     * 
     * @param property
     *            the BLOB property
     * @throws IOException
     *             if there is some problem sending the message.
     * @deprecated Replaced by
     *             {@link #blobsEnable(Constants.BLOBEnables, INDIProperty)}
     */
    @Deprecated
    public void blobsEnableOnly(INDIBLOBProperty property) throws IOException {
        sendMessageToServer(new EnableBLOB().setDevice(getName()).setName(property.getName()).setTextContent("Only"));
    }

    /**
     * Gets the last message received from the Device.
     * 
     * @return the last message received.
     */
    public String getLastMessage() {
        return message;
    }

    /**
     * Gets the timestamp of the last received message.
     * 
     * @return the timestamp of the last received message.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the name of the Device.
     * 
     * @return the name of the Device.
     */
    public String getName() {
        return name;
    }

    /**
     * Processes a XML message received for this Device and stores and notifies
     * the listeners if there is some message attribute in them.
     * 
     * @param xml
     *            the XML message to be processed.
     */
    protected void messageReceived(INDIProtocol<?> xml) {
        if (xml.hasMessage()) {
            String time = xml.getTimestamp();

            timestamp = INDIDateFormat.dateFormat().parseTimestamp(time);

            message = xml.getMessage();

            notifyListenersMessageChanged();
        }
    }

    /**
     * Processes a XML &lt;delProperty&gt;. It removes the appropriate Property
     * from the list of Properties.
     * 
     * @param xml
     *            the XML message to be processed.
     */
    protected void deleteProperty(DelProperty xml) {
        String propertyName = xml.getName();

        if (!(propertyName.length() == 0)) {
            messageReceived(xml);

            INDIProperty<?> p = getProperty(propertyName);

            if (p != null) {
                removeProperty(p);
            }
        }
    }

    /**
     * This function waits until a Property with a <code>propertyName</code>
     * exists in this device and returns it. The wait is dinamic, so it should
     * be called from a different Thread or the app will freeze until the
     * property exists.
     * 
     * @param propertyName
     *            The propertyName of the Property to wait for.
     * @return The Property once it exists in this device.
     */
    public INDIProperty<?> waitForProperty(String propertyName) {
        return waitForProperty(propertyName, Integer.MAX_VALUE);
    }

    /**
     * This function waits until a Property with a <code>propertyName</code>
     * exists in this device and returns it. The wait is dinamic, so it should
     * be called from a different Thread or the app will freeze until the
     * property exists or the <code>maxWait</code> number of seconds have
     * elapsed.
     * 
     * @param propertyName
     *            The propertyName of the Property to wait for.
     * @param maxWait
     *            Maximum number of seconds to wait for the Property
     * @return The Property once it exists in this Device or <code>null</code>
     *         if the maximum wait is achieved.
     */
    public INDIProperty<?> waitForProperty(String propertyName, int maxWait) {
        INDIProperty<?> p = null;

        long startTime = new Date().getTime();
        boolean timeElapsed = false;

        while (p == null && !timeElapsed) {
            p = this.getProperty(propertyName);

            if (p == null) {
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

        return p;
    }

    /**
     * Processes a XML &lt;setXXXVector&gt;. It updates the appropiate Property.
     * 
     * @param xml
     *            the XML message to be processed.
     */
    protected void updateProperty(SetVector<?> xml) {
        String propertyName = xml.getName();

        if (!propertyName.isEmpty()) {
            // check message
            messageReceived(xml);

            INDIProperty<?> p = getProperty(propertyName);

            if (p != null) { // If it does not exist else ignore
                if (p instanceof INDITextProperty && xml instanceof SetTextVector) {
                    p.update(xml);
                } else if (p instanceof INDINumberProperty && xml instanceof SetNumberVector) {
                    p.update(xml);
                } else if (p instanceof INDISwitchProperty && xml instanceof SetSwitchVector) {
                    p.update(xml);
                } else if (p instanceof INDILightProperty && xml instanceof SetLightVector) {
                    p.update(xml);
                } else if (p instanceof INDIBLOBProperty && xml instanceof SetBlobVector) {
                    p.update(xml);
                }
            }
        }
    }

    /**
     * Processes a XML &lt;newXXXVector&gt;. It creates and adds the appropiate
     * Property.
     * 
     * @param xml
     *            The XML message to be processed.
     */
    protected void addProperty(DefVector<?> xml) {
        String propertyName = xml.getName();

        if (!propertyName.isEmpty()) {
            messageReceived(xml);

            INDIProperty<?> p = getProperty(propertyName);

            if (p == null) { // If it does not exist
                try {
                    if (xml instanceof DefSwitchVector) {
                        INDISwitchProperty sp = new INDISwitchProperty((DefSwitchVector) xml, this);
                        addProperty(sp);
                        notifyListenersNewProperty(sp);
                    } else if (xml instanceof DefTextVector) {
                        INDITextProperty tp = new INDITextProperty((DefTextVector) xml, this);
                        addProperty(tp);
                        notifyListenersNewProperty(tp);
                    } else if (xml instanceof DefNumberVector) {
                        INDINumberProperty np = new INDINumberProperty((DefNumberVector) xml, this);
                        addProperty(np);
                        notifyListenersNewProperty(np);
                    } else if (xml instanceof DefLightVector) {
                        INDILightProperty lp = new INDILightProperty((DefLightVector) xml, this);
                        addProperty(lp);
                        notifyListenersNewProperty(lp);
                    } else if (xml instanceof DefBlobVector) {
                        INDIBLOBProperty bp = new INDIBLOBProperty((DefBlobVector) xml, this);
                        addProperty(bp);
                        notifyListenersNewProperty(bp);
                    }
                } catch (IllegalArgumentException e) {
                    LOG.error("Some problem with the parameters", e);
                }
            }
        }
    }

    /**
     * Gets the number of BLOB properties in this Device.
     * 
     * @return the number of BLOB properties in this Device.
     */
    public int getBLOBCount() {
        return blobCount;
    }

    /**
     * Adds a Property to the properties list and updates the BLOB count if
     * necessary.
     * 
     * @param property
     *            The property to be added.
     */
    private void addProperty(INDIProperty<?> property) {
        properties.put(property.getName(), property);

        if (property instanceof INDIBLOBProperty) {
            blobCount++;
        }
    }

    /**
     * Removes a Property from the properties list and updates the BLOB count if
     * necessary.
     * 
     * @param property
     *            The property to be removed.
     */
    private void removeProperty(INDIProperty<?> property) {
        properties.remove(property.getName());

        if (property instanceof INDIBLOBProperty) {
            blobCount--;
        }

        notifyListenersDeleteProperty(property);
    }

    /**
     * Gets the Server Connection of this Device.
     * 
     * @return the Server Connection of this Device.
     */
    public INDIServerConnection getServer() {
        return server;
    }

    /**
     * Gets a Property by its propertyName.
     * 
     * @param propertyName
     *            the propertyName of the Property to be retrieved.
     * @return the Property with the <code>propertyName</code> or
     *         <code>null</code> if there is no Property with that propertyName.
     */
    public INDIProperty<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    /**
     * Gets a list of group names for all the properties.
     * 
     * @return the list of group names for all the properties of the device.
     */
    public List<String> getGroupNames() {
        ArrayList<String> groupNames = new ArrayList<String>();
        for (INDIProperty<?> p : properties.values()) {
            String groupName = p.getGroup();

            if (!groupNames.contains(groupName)) {
                groupNames.add(groupName);
            }
        }
        return groupNames;
    }

    /**
     * Gets a list of all the properties of the device.
     * 
     * @return the list of Properties belonging to the device
     */
    public List<INDIProperty<?>> getAllProperties() {
        return new ArrayList<INDIProperty<?>>(properties.values());
    }

    /**
     * Gets a list of properties belonging to a group.
     * 
     * @param groupName
     *            the name of the group
     * @return the list of Properties belonging to the group
     */
    public List<INDIProperty<?>> getPropertiesOfGroup(String groupName) {
        ArrayList<INDIProperty<?>> props = new ArrayList<>();
        for (INDIProperty<?> p : properties.values()) {
            if (p.getGroup().compareTo(groupName) == 0) {
                props.add(p);
            }
        }
        return props;
    }

    /**
     * A convenience method to get the Element of a Property by specifiying
     * their names.
     * 
     * @param propertyName
     *            the name of the Property.
     * @param elementName
     *            the name of the Element.
     * @return the Element with <code>elementName</code> as name of the property
     *         with <code>propertyName</code> as name.
     */
    public INDIElement getElement(String propertyName, String elementName) {
        INDIProperty<?> p = getProperty(propertyName);

        if (p == null) {
            return null;
        }

        return p.getElement(elementName);
    }

    /**
     * Sends a XML message to the Server.
     * 
     * @param xmlMessage
     *            the message to be sent.
     * @throws IOException
     *             if there is some problem with the connection to the server.
     */
    protected void sendMessageToServer(INDIProtocol<?> xmlMessage) throws IOException {
        server.sendMessageToServer(xmlMessage);
    }

    /**
     * Gets a default UI component to handle the repesentation and control of
     * this Device. The panel is registered as a listener of this Device. Please
     * note that the UI class must implement INDIDeviceListener. The component
     * will be chosen depending on the loaded UI libraries (I4JClientUI,
     * I4JAndroid, etc). Note that a casting of the returned value must be done.
     * If a previous component has been asked, it will be dregistered as a
     * listener. So, only one default component will listen to the device.
     * 
     * @return A UI component that handles this Device.
     * @throws INDIException
     *             if no uiComponent is found in the classpath.
     */
    public INDIDeviceListener getDefaultUIComponent() throws INDIException {
        if (uiComponent != null) {
            removeINDIDeviceListener(uiComponent);
        }
        uiComponent = INDIViewCreator.getDefault().createDeviceView(this);
        addINDIDeviceListener(uiComponent);
        return uiComponent;
    }

    /**
     * Gets a <code>List</code> with all the Properties of this Device.
     * 
     * @return the <code>List</code> of Properties belonging to this Device.
     */
    public List<INDIProperty<?>> getPropertiesAsList() {
        return new ArrayList<INDIProperty<?>>(properties.values());
    }

    /**
     * Gets the names of the Properties of this Device.
     * 
     * @return the names of the Properties of this Device.
     */
    public String[] getPropertyNames() {
        List<String> names = new ArrayList<>();
        for (INDIProperty<?> indiProperty : properties.values()) {
            names.add(indiProperty.getName());
        }
        return names.toArray(new String[names.size()]);
    }
}
