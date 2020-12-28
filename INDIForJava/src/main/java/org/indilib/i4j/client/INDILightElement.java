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

import org.indilib.i4j.protocol.DefLight;
import org.indilib.i4j.protocol.OneElement;
import org.indilib.i4j.Constants;
import org.indilib.i4j.INDIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing a INDI Light Element.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDILightElement extends INDIElement {

    /**
     * A logger for the errors.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDILightElement.class);

    /**
     * Current State value for this Light Element.
     */
    private Constants.LightStates state;

    /**
     * A UI component that can be used in graphical interfaces for this Light
     * Element.
     */
    private INDIElementListener uiComponent;

    /**
     * Constructs an instance of <code>INDILightElement</code>. Usually called
     * from a <code>INDIProperty</code>. Throws IllegalArgumentException if the
     * XML Element is not well formed or the value is not a valid one.
     * 
     * @param xml
     *            A XML Element <code>&lt;defLight&gt;</code> describing the
     *            Light Element.
     * @param property
     *            The <code>INDIProperty</code> to which the Element belongs.
     */
    protected INDILightElement(DefLight xml, INDILightProperty property) {
        super(xml, property);
        setValue(xml.getTextContent());
    }

    @Override
    public Constants.LightStates getValue() {
        return state;
    }

    /**
     * Sets the current value of this Light Element. It is assummed that the XML
     * Element is really describing the new value for this particular Light
     * Element. Throws IllegalArgumentException if the <code>xml</code> is not
     * well formed (the light status is not correct).
     * <p>
     * This method will notify the change of the value to the listeners.
     * 
     * @param xml
     *            A XML Element &lt;oneLight&gt; describing the Element.
     */
    @Override
    protected void setValue(OneElement<?> xml) {
        setValue(xml.getTextContent());
        notifyListeners();
    }

    /**
     * Sets the state of the Light Element. Throws IllegalArgumentException if
     * the new state is not correct ("Idle" or "Ok" or "Busy" or "Alert").
     * 
     * @param newState
     *            The new state of the Light Element
     */
    private void setValue(String newState) {
        if (newState.compareTo("Idle") == 0) {
            state = Constants.LightStates.IDLE;
        } else if (newState.compareTo("Ok") == 0) {
            state = Constants.LightStates.OK;
        } else if (newState.compareTo("Busy") == 0) {
            state = Constants.LightStates.BUSY;
        } else if (newState.compareTo("Alert") == 0) {
            state = Constants.LightStates.ALERT;
        } else {
            throw new IllegalArgumentException("Illegal Light Status");
        }
    }

    @Override
    public INDIElementListener getDefaultUIComponent() throws INDIException {
        if (uiComponent != null) {
            removeINDIElementListener(uiComponent);
        }
        uiComponent = INDIViewCreator.getDefault().createLightElementView(this, getProperty().getPermission());

        addINDIElementListener(uiComponent);

        return uiComponent;
    }

    /**
     * Always returns true. This method should never be called as lights cannot
     * be setted by a client.
     * 
     * @param desiredValue
     *            DO NOT USE
     * @return true
     * @throws INDIValueException
     *             NEVER THROWN
     */
    @Override
    public boolean checkCorrectValue(Object desiredValue) throws INDIValueException {
        return true; // Nothing to check
    }

    @Override
    public String getNameAndValueAsString() {
        return getName() + " - " + getValue();
    }

    @Override
    public Object getDesiredValue() {
        throw new UnsupportedOperationException("Lights have no desired value");
    }

    @Override
    public void setDesiredValue(Object desiredValue) throws INDIValueException {
        throw new INDIValueException(this, "Lights cannot be set.");
    }

    @Override
    public boolean isChanged() {
        return false; // Lights cannot be changed
    }

    /**
     * Always returns an empty "" <code>String</code>. This method should never
     * be called as lights cannot be setted by a client.
     * 
     * @return "";
     */
    @Override
    protected OneElement<?> getXMLOneElementNewValue() {
        LOG.error("changed but not possible, it should not be possible to change a light!");
        return null;
    }

    @Override
    public String toString() {
        return getName() + ": " + getValue();
    }

    @Override
    public String getValueAsString() {
        return getValue() + "";
    }
}
