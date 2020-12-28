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

import org.indilib.i4j.INDIException;

import java.util.ArrayList;

import org.indilib.i4j.protocol.DefElement;
import org.indilib.i4j.protocol.OneElement;

/**
 * A class representing a INDI Element. The subclasses
 * <code>INDIBLOBElement</code>, <code>INDILightElement</code>,
 * <code>INDINumberElement</code>, <code>INDISwitchElement</code> and
 * <code>INDITextElement</code> define the basic Elements that a INDI Property
 * may contain according to the INDI protocol.
 * <p>
 * It implements a listener mechanism to notify changes in its value.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public abstract class INDIElement {

    /**
     * The property to which this Element belongs.
     */
    private INDIProperty<?> property;

    /**
     * The name of the Element.
     */
    private String name;

    /**
     * The label of the Element.
     */
    private String label;

    /**
     * The list of listeners of this Element.
     */
    private ArrayList<INDIElementListener> listeners;

    /**
     * Constructs an instance of <code>INDIElement</code>. Called by its
     * sub-classes. <code>INDIElement</code>s are not usually directly
     * instantiated. Usually used by <code>INDIProperty</code>. Throws
     * IllegalArgumentException if the XML Element is not well formed (does not
     * contain a <code>name</code> attribute).
     * 
     * @param xml
     *            A XML Element <code>&lt;defXXX&gt;</code> describing the
     *            Element.
     * @param property
     *            The <code>INDIProperty</code> to which this Element belongs.
     */
    protected INDIElement(DefElement<?> xml, INDIProperty<?> property) {
        this.property = property;

        name = xml.getName();

        if (name.length() == 0) {
            throw new IllegalArgumentException("No name for Element");
        }

        label = xml.getLabel();

        if (label.length() == 0) { // If empty we use the name
            label = name;
        }

        listeners = new ArrayList<INDIElementListener>();
    }

    /**
     * Adds a new listener that will be notified on this Element value changes.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addINDIElementListener(INDIElementListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from the listeners list. This listener will no longer
     * be notified of changes of this Element.
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeINDIElementListener(INDIElementListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies the listeners about changes of the value of the Element.
     */
    protected void notifyListeners() {
        for (INDIElementListener l : new ArrayList<>(listeners)) {
            l.elementChanged(this);
        }
    }

    /**
     * Gets the label of the Element.
     * 
     * @return The label of the Element.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the name of the Element.
     * 
     * @return The name of the Element.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the Property to which this Element belongs.
     * 
     * @return The property to which this Element belongs.
     */
    public INDIProperty<?> getProperty() {
        return property;
    }

    /**
     * Gets the current value of the Element.
     * 
     * @return The current value of the Element.
     */
    public abstract Object getValue();

    /**
     * Gets the current value of the Element as a String.
     * 
     * @return The current value of the Element as a String.
     */
    public abstract String getValueAsString();

    /**
     * Sets the current value of the Element. It is assummed that the XML
     * Element is really describing the new value for this particular Element.
     * <p>
     * This method will notify the change of the value to the listeners.
     * 
     * @param xml
     *            A XML Element &lt;oneXXX&gt; describing the Element.
     */
    protected abstract void setValue(OneElement<?> xml);

    /**
     * Gets the desired value of the Element.
     * 
     * @return The current desiredvalue of the Element. <code>null</code> if it
     *         is not setted.
     */
    public abstract Object getDesiredValue();

    /**
     * Sets the desired value of the Element to <code>desiredValue</code>.
     * 
     * @param desiredValue
     *            The desired value for the property.
     * @throws INDIValueException
     *             if the <code>desiredValue</code> is not of the correct type
     *             for the Element.
     */
    public abstract void setDesiredValue(Object desiredValue) throws INDIValueException;

    /**
     * Returns <code>true</code> if the <code>desiredValue</code> has been
     * setted (and thus if it should be send to the Driver).
     * 
     * @return <code>true</code> if the desiredValue has been setted.
     *         <code>false</code> otherwise.
     */
    public abstract boolean isChanged();

    /**
     * Gets a default UI component to handle the repesentation and control of
     * this Element. The panel is registered as a listener of this Element.
     * Please note that the UI class must implement INDIElementListener. The
     * component will be chosen depending on the loaded UI libraries
     * (I4JClientUI, I4JAndroid, etc). Note that a casting of the returned value
     * must be done. If a previous UI element has been asked, it will be
     * discarded and de-registered as listener. So, only one default UI element
     * will be active.
     * 
     * @return A UI component that handles this Element.
     * @throws INDIException
     *             if there is a problem instantiating the Component.
     */
    public abstract INDIElementListener getDefaultUIComponent() throws INDIException;

    /**
     * Checks if a desired value would be correct to be applied to the Element
     * according to its definition and limits.
     * 
     * @param desiredValue
     *            The value to be checked.
     * @return <code>true</code> if the <code>desiredValue</code> would be
     *         acceptable to be applied to the element. <code>false</code>
     *         otherwise.
     * @throws INDIValueException
     *             if the desiredValue is not correct.
     */
    public abstract boolean checkCorrectValue(Object desiredValue) throws INDIValueException;

    /**
     * Returns the XML code &lt;oneXXX&gt; representing this Element with a new
     * desired value). The desired value is reseted.
     * 
     * @return the XML code <code>&lt;oneXXX&gt;</code> representing the Element
     *         with a new value.
     * @see #setDesiredValue
     */
    protected abstract OneElement<?> getXMLOneElementNewValue();

    /**
     * Gets the name of the element and its current value.
     * 
     * @return a String with the name of the Element and Its Value
     */
    public abstract String getNameAndValueAsString();
}
