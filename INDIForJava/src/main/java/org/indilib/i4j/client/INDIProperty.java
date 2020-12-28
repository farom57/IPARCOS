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

import org.indilib.i4j.Constants;
import org.indilib.i4j.INDIDateFormat;
import org.indilib.i4j.INDIException;
import org.indilib.i4j.properties.INDIStandardElement;
import org.indilib.i4j.properties.INDIStandardProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.indilib.i4j.protocol.DefVector;
import org.indilib.i4j.protocol.NewVector;
import org.indilib.i4j.protocol.OneElement;
import org.indilib.i4j.protocol.SetVector;

/**
 * A class representing a INDI Property. The subclasses
 * <code>INDIBLOBProperty</code>, <code>INDILightProperty</code>,
 * <code>INDINumberProperty</code>, <code>INDISwitchProperty</code> and
 * <code>INDITextProperty</code> define the basic Properties that a INDI Devices
 * may contain according to the INDI protocol.
 * <p>
 * It implements a listener mechanism to notify changes in its Elements.
 * 
 * @param <Element>
 *            the elements that occure in the property.
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public abstract class INDIProperty<Element extends INDIElement> implements Iterable<Element> {

    /**
     * The INDI Device to which this property belongs.
     */
    private INDIDevice device;

    /**
     * This property name.
     */
    private String name;

    /**
     * This property label.
     */
    private String label;

    /**
     * The group to which this Property might be assigned.
     */
    private String group;

    /**
     * The current state of this Property.
     */
    private Constants.PropertyStates state;

    /**
     * The permission of this Property.
     */
    private Constants.PropertyPermissions permission;

    /**
     * The timeout of this Property.
     */
    private int timeout;

    /**
     * A list of elements for this Property.
     */
    private Map<String, Element> elements;

    /**
     * The list of listeners of this Property.
     */
    private List<INDIPropertyListener> listeners;

    /**
     * Constructs an instance of <code>INDIProperty</code>. Called by its
     * sub-classes. <code>INDIProperty</code>s are not usually directly
     * instantiated. Usually used by <code>INDIDevice</code>. Throws
     * IllegalArgumentException if the XML Property is not well formed (does not
     * contain a <code>name</code> attribute or the permissions are not
     * correct).
     * 
     * @param xml
     *            A XML Element <code>&lt;defXXXVector&gt;</code> describing the
     *            Property.
     * @param device
     *            The <code>INDIDevice</code> to which this Property belongs.
     */
    protected INDIProperty(DefVector<?> xml, INDIDevice device) {

        this.device = device;
        name = xml.getName();
        if (name.isEmpty()) { // If no name, ignore
            throw new IllegalArgumentException("No name for the Property");
        }
        label = xml.getLabel();
        if (label.isEmpty()) { // If no label copy from name
            this.label = name;
        }
        group = xml.getGroup();
        if (group.isEmpty()) { // If no group, create default group
            group = "Unsorted";
        }
        String sta = xml.getState();
        setState(sta);
        if (this instanceof INDITextProperty || this instanceof INDINumberProperty //
                || this instanceof INDISwitchProperty || this instanceof INDIBLOBProperty) {

            permission = Constants.parsePropertyPermission(xml.getPerm());

            String to = xml.getTimeout();

            if (!to.isEmpty()) {
                setTimeout(to);
            } else {
                timeout = 0;
            }
        }

        if (this.getClass() == INDILightProperty.class) {
            timeout = 0;
            permission = Constants.PropertyPermissions.RO;
        }

        this.elements = new LinkedHashMap<String, Element>();

        this.listeners = new ArrayList<INDIPropertyListener>();
    }

    /**
     * Updates the values of its elements according to some XML data. Subclasses
     * of <code>INDIProperty</code> must implement this method to really do the
     * parsing and update (usually calling <code>update(Element, String)</code>
     * ).
     * 
     * @param xml
     *            A XML Element <code>&lt;setXXXVector&gt;</code> to which the
     *            property must be updated.
     */
    protected abstract void update(SetVector<?> xml);

    /**
     * Updates the values of its elements according to some XML data. Subclasses
     * of <code>INDIProperty</code> usually call this method from
     * <code>update(Element)</code> to really do the parsing and update.
     * 
     * @param xml
     *            A XML Element <code>&lt;setXXXVector&gt;</code> to which the
     *            property must be updated.
     * @param childNodesType
     *            The real XML type of <code>xml</code>, that is, one of
     *            <code>&lt;setBLOBVector&gt;</code>,
     *            <code>&lt;setLightVector&gt;</code>,
     *            <code>&lt;setNumberVector&gt;</code>,
     *            <code>&lt;setSwitchVector&gt;</code> or
     *            <code>&lt;setTextVector&gt;</code>.
     */
    protected void update(SetVector<?> xml, Class<?> childNodesType) {
        try {
            String sta = xml.getState();
            if (!(sta.length() == 0)) {
                setState(sta);
            }
            String to = xml.getTimeout();
            if ((to != null) && (to.length() != 0)) {
                setTimeout(to);
            }
            for (OneElement<?> element : xml.getElements()) {
                if (childNodesType.isAssignableFrom(element.getClass())) {
                    String ename = element.getName();
                    Element iel = getElement(ename);
                    if (iel != null) {
                        // It already exists else ignore
                        iel.setValue(element);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            // If there was some problem parsing then set to alert
            state = Constants.PropertyStates.ALERT;
        }
        notifyListeners();
    }

    /**
     * Sets the state of this property.
     * 
     * @param newState
     *            The new state for this Property in form of a String:
     *            <code>Idle</code>, <code>Ok</code>, <code>Busy</code> or
     *            <code>Alert</code>.
     */
    private void setState(String newState) {
        state = Constants.parsePropertyState(newState);
    }

    /**
     * Sets the current timeout for this Property. Throws
     * IllegalArgumentException if the format of the timeout is not correct (a
     * positive integer).
     * 
     * @param newTimeout
     *            The new current timeout.
     */
    private void setTimeout(String newTimeout) {
        try {
            timeout = Integer.parseInt(newTimeout);

            if (timeout < 0) {
                throw new IllegalArgumentException("Illegal timeout for the Property");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegal timeout for the Property");
        }
    }

    /**
     * Gets the Device that owns this Property.
     * 
     * @return the Device that owns this Property
     */
    public INDIDevice getDevice() {
        return device;
    }

    /**
     * Gets the Group to which this property might be assigned.
     * 
     * @return the group to which this property might be assigned.
     */
    public String getGroup() {
        return group;
    }

    /**
     * Gets the timeout for this Property.
     * 
     * @return the timeout for this Property.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Gets the label for this Property.
     * 
     * @return the label for this Property.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the name of this Property.
     * 
     * @return the name of this Property
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the number of Elements in this Property.
     * 
     * @return the number of Elements in this Property.
     */
    public int getElementCount() {
        return elements.size();
    }

    /**
     * Gets the Permission of this Property.
     * 
     * @return the Permission of this Property.
     */
    public Constants.PropertyPermissions getPermission() {
        return permission;
    }

    /**
     * Gets the State of this Property.
     * 
     * @return the State of this Property.
     */
    public Constants.PropertyStates getState() {
        return state;
    }

    /**
     * Sets the timeout of this property.
     * 
     * @param timeout
     *            the new timeout for this Property.
     */
    protected void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Sets the State of this Property. The listeners are notified of this
     * change.
     * 
     * @param state
     *            the new State of this Property.
     */
    protected void setState(Constants.PropertyStates state) {
        this.state = state;

        notifyListeners();
    }

    /**
     * Sets the Permission of this Property.
     * 
     * @param permission
     *            the new Permission for this Property.
     */
    protected void setPermission(Constants.PropertyPermissions permission) {
        this.permission = permission;
    }

    /**
     * Adds a new Element to this Property.
     * 
     * @param element
     *            the Element to be added.
     */
    protected void addElement(Element element) {
        elements.put(element.getName(), element);
    }

    /**
     * Gets a particular Element of this Property by its elementName.
     * 
     * @param elementName
     *            The elementName of the Element to be returned
     * @return The Element of this Property with the given
     *         <code>elementName</code>. <code>null</code> if there is no
     *         Element with that <code>elementName</code>.
     */
    public Element getElement(String elementName) {
        return elements.get(elementName);
    }

    /**
     * Gets a particular Element of this Property by its elementName.
     * 
     * @param elementName
     *            The elementName of the Element to be returned
     * @return The Element of this Property with the given
     *         <code>elementName</code>. <code>null</code> if there is no
     *         Element with that <code>elementName</code>.
     */
    public Element getElement(INDIStandardElement elementName) {
        return elements.get(elementName.name());
    }

    /**
     * Gets a particular Element of this Property by its elementName.
     * 
     * @param elementName
     *            The elementName of the Element to be returned
     * @return The Element of this Property with the given
     *         <code>elementName</code>. <code>null</code> if there is no
     *         Element with that <code>elementName</code>.
     */
    public Element getElement(INDIStandardProperty elementName) {
        return elements.get(elementName.name());
    }

    /**
     * Gets a <code>ArrayList</code> with all the Elements of this Property.
     * 
     * @return the <code>ArrayList</code> of Elements belonging to this
     *         Property.
     */
    public List<Element> getElementsAsList() {
        return new ArrayList<Element>(elements.values());
    }

    /**
     * Tests and changes the desired values of the the Elements of this
     * Property. If there are new desired values for any Elements the XML code
     * to produce the change is sent to the INDI Driver. If communication is
     * successful the state of the property is set to "Busy".
     * 
     * @throws INDIValueException
     *             if some of the desired values are not correct or if the
     *             Property is Read Only.
     * @throws IOException
     *             if there is some communication problem with the INDI driver
     *             connection.
     */
    public void sendChangesToDriver() throws INDIValueException, IOException {
        if (permission == Constants.PropertyPermissions.RO) {
            throw new INDIValueException(null, "The property is read only");
        }
        int changedElements = 0;
        NewVector<?> xml = getXMLPropertyChangeInit();
        for (Element el : this) {
            if (el.isChanged()) {
                changedElements++;
                xml.getElements().add(el.getXMLOneElementNewValue());
            }
        }
        if (changedElements > 0) {
            setState(Constants.PropertyStates.BUSY);
            xml.setDevice(getDevice().getName());
            xml.setName(getName());
            xml.setTimestamp(INDIDateFormat.dateFormat().getCurrentTimestamp());
            device.sendMessageToServer(xml);
        }
    }

    /**
     * Adds a new listener to this Property.
     * 
     * @param listener
     *            the listener to be added.
     */
    public void addINDIPropertyListener(INDIPropertyListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from this Property.
     * 
     * @param listener
     *            the listener to be removed.
     */
    public void removeINDIPropertyListener(INDIPropertyListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all the listeners about the changes in the Property.
     */
    private void notifyListeners() {
        for (INDIPropertyListener l : new ArrayList<INDIPropertyListener>(listeners)) {
            l.propertyChanged(this);
        }
    }

    /**
     * Gets the opening XML Element &lt;newXXXVector&gt; for this Property.
     * 
     * @return the opening XML Element &lt;newXXXVector&gt; for this Property.
     */
    protected abstract NewVector<?> getXMLPropertyChangeInit();

    /**
     * Gets a default UI component to handle the repesentation and control of
     * this Property. The panel is registered as a listener of this Property.
     * Please note that the UI class must implement INDIPropertyListener. The
     * component will be chosen depending on the loaded UI libraries
     * (I4JClientUI, I4JAndroid, etc). Note that a casting of the returned value
     * must be done. If a previous default component has been requested, the
     * previous one will be deregistered. So, only one default component will
     * listen for the property.
     * 
     * @return A UI component that handles this Property.
     * @throws INDIException
     *             if there is a problem instantiating an UI component for a
     *             Property.
     */
    public abstract INDIPropertyListener getDefaultUIComponent() throws INDIException;

    /**
     * Gets the names of the Elements of this Property.
     * 
     * @return the names of the Elements of this Property.
     */
    public String[] getElementNames() {
        List<String> names = new ArrayList<>();
        for (Element l : this) {
            names.add(l.getName());
        }
        return names.toArray(new String[names.size()]);
    }

    /**
     * Returns a String with the name of the Property, its state and its
     * elements and values.
     * 
     * @return a String representation of the property and its values.
     */
    public String getNameStateAndValuesAsString() {
        StringBuffer aux = new StringBuffer(getName()).append(" - ").append(getState()).append("\n");
        for (Element l : this) {
            aux.append("  ").append(l.getNameAndValueAsString()).append("\n");
        }
        return aux.toString();
    }

    /**
     * Gets the values of the Property as a String.
     * 
     * @return A String representation of the value of the Property.
     */
    public String getValuesAsString() {
        StringBuffer aux = new StringBuffer("[");
        for (Element l : this) {
            if (aux.length() > 1) {
                aux.append(',');
            }
            aux.append(l);
        }
        return aux.append("]").toString();
    }

    @Override
    public Iterator<Element> iterator() {
        return elements.values().iterator();
    }
}
