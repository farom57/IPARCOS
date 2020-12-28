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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.indilib.i4j.Constants;
import org.indilib.i4j.INDIException;
import org.indilib.i4j.protocol.DefSwitch;
import org.indilib.i4j.protocol.OneElement;
import org.indilib.i4j.protocol.OneSwitch;

/**
 * A class representing a INDI Switch Element.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDISwitchElement extends INDIElement {

    /**
     * A UI component that can be used in graphical interfaces for this Switch
     * Element.
     */
    private INDIElementListener uiComponent;

    /**
     * Current Status value for this Switch Element.
     */
    private Constants.SwitchStatus status;

    /**
     * Current desired status value for this Switch Element.
     */
    private Constants.SwitchStatus desiredStatus;

    /**
     * Constructs an instance of <code>INDISwitchElement</code>. Usually called
     * from a <code>INDIProperty</code>. Throws IllegalArgumentException if the
     * XML Element is not well formed (switch value not correct).
     * 
     * @param xml
     *            A XML Element <code>&lt;defSwitch&gt;</code> describing the
     *            Switch Element.
     * @param property
     *            The <code>INDIProperty</code> to which the Element belongs.
     */
    protected INDISwitchElement(DefSwitch xml, INDISwitchProperty property) {
        super(xml, property);
        desiredStatus = null;
        setValue(xml.getTextContent());
    }

    @Override
    public Constants.SwitchStatus getValue() {
        return status;
    }

    /**
     * Sets the current value of this Switch Element. It is assummed that the
     * XML Element is really describing the new value for this particular Switch
     * Element.
     * <p>
     * This method will notify the change of the value to the listeners.
     * 
     * @param xml
     *            A XML Element &lt;oneSwitch&gt; describing the Element.
     */
    @Override
    protected void setValue(OneElement<?> xml) {
        setValue(xml.getTextContent());

        notifyListeners();
    }

    /**
     * Sets the value of the Switch Property. Throws IllegalArgumentException if
     * the new status is not a correct one ("On" or "Off")
     * 
     * @param newStatus
     *            the new status of the property
     */
    private void setValue(String newStatus) {
        if (newStatus.compareTo("Off") == 0) {
            status = Constants.SwitchStatus.OFF;
        } else if (newStatus.compareTo("On") == 0) {
            status = Constants.SwitchStatus.ON;
        } else {
            throw new IllegalArgumentException("Illegal Switch Status");
        }
    }

    @Override
    public INDIElementListener getDefaultUIComponent() throws INDIException {
        if (uiComponent != null) {
            removeINDIElementListener(uiComponent);
        }
        uiComponent = INDIViewCreator.getDefault().createSwitchElementView(this, getProperty().getPermission());
        addINDIElementListener(uiComponent);
        return uiComponent;
    }

    /**
     * Checks if a desired value would be correct to be applied to the Switch
     * Element, that is a <code>SwitchStatus</code> object.
     * 
     * @param desiredValue
     *            The value to be checked.
     * @return <code>true</code> if the <code>desiredValue</code> is a
     *         <code>SwitchStatus</code>. <code>false</code> otherwise.
     * @throws INDIValueException
     *             if <code>desiredValue</code> is <code>null</code>.
     */
    @Override
    public boolean checkCorrectValue(Object desiredValue) throws INDIValueException {
        if (desiredValue == null) {
            throw new INDIValueException(this, "null value");
        }

        if (desiredValue instanceof Constants.SwitchStatus) {
            return true;
        }

        return false;
    }

    @Override
    public String getNameAndValueAsString() {
        return getName() + " - " + getValue();
    }

    @Override
    public Constants.SwitchStatus getDesiredValue() {
        if (desiredStatus == null) { // Maybe there is no desired status, but
                                     // should be sent
            return status;
        }

        return desiredStatus;
    }

    @Override
    public void setDesiredValue(Object desiredValue) throws INDIValueException {
        Constants.SwitchStatus ss = null;
        try {
            ss = (Constants.SwitchStatus) desiredValue;
        } catch (ClassCastException e) {
            throw new INDIValueException(this, "Value for a Switch Element must be a SwitchStatus");
        }

        desiredStatus = ss;
    }

    @Override
    public boolean isChanged() {
        return true; // Always true to send all the elements in a switch
                     // property
    }

    /**
     * Returns the XML code &lt;oneSwitch&gt; representing this Switch Element
     * with a new value (a <code>SwitchStatus</code>). Resets the desired
     * status.
     * 
     * @return the XML code <code>&lt;oneSwitch&gt;</code> representing this
     *         Switch Element with a new value.
     * @see #setDesiredValue
     */
    @Override
    protected OneElement<?> getXMLOneElementNewValue() {
        OneSwitch xml = new OneSwitch().setName(getName()).setTextContent(Constants.getSwitchStatusAsString(desiredStatus));

        desiredStatus = null;

        return xml;
    }

    @Override
    public String getValueAsString() {
        return getValue() + "";
    }
}
