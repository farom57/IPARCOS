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

import org.indilib.i4j.INDIBLOBValue;
import org.indilib.i4j.INDIException;
import org.indilib.i4j.protocol.DefBlob;
import org.indilib.i4j.protocol.OneBlob;
import org.indilib.i4j.protocol.OneElement;

/**
 * A class representing a INDI BLOB Element.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDIBLOBElement extends INDIElement {

    /**
     * The current value of the BLOB Element.
     */
    private INDIBLOBValue value;

    /**
     * The current desired value of the BLOB Element.
     */
    private INDIBLOBValue desiredValue;

    /**
     * A UI component that can be used in graphical interfaces for this BLOB
     * Element.
     */
    private INDIElementListener uiComponent;

    /**
     * Constructs an instance of <code>INDIBLOBElement</code>. Usually called
     * from a <code>INDIProperty</code>.
     * 
     * @param xml
     *            A XML Element <code>&lt;defBLOB&gt;</code> describing the BLOB
     *            Element.
     * @param property
     *            The <code>INDIProperty</code> to which the Element belongs.
     */
    protected INDIBLOBElement(DefBlob xml, INDIBLOBProperty property) {
        super(xml, property);

        desiredValue = null;

        value = new INDIBLOBValue(new byte[0], "");
    }

    @Override
    public INDIBLOBValue getValue() {
        return value;
    }

    /**
     * Sets the current value of this BLOB Element. It is assummed that the XML
     * Element is really describing the new value for this particular BLOB
     * Element.
     * <p>
     * This method will notify the change of the value to the listeners.
     * <p>
     * Throws IllegalArgumentException if the <code>xml</code> is not well
     * formed (no size, no format or incorrectly coded data
     * 
     * @param xml
     *            A XML Element &lt;oneBLOB&gt; describing the Element.
     */
    @Override
    public void setValue(OneElement<?> xml) {
        value = new INDIBLOBValue((OneBlob) xml);

        notifyListeners();
    }

    @Override
    public INDIElementListener getDefaultUIComponent() throws INDIException {
        if (uiComponent != null) {
            removeINDIElementListener(uiComponent);
        }
        uiComponent = INDIViewCreator.getDefault().createBlobElementView(this, getProperty().getPermission());
        addINDIElementListener(uiComponent);
        return uiComponent;
    }

    /**
     * Checks if a desired value would be correct to be applied to the BLOB
     * Element.
     * 
     * @param valueToCheck
     *            The value to be checked.
     * @return <code>true</code> if the <code>valueToCheck</code> is a
     *         <code>INDIBLOBValue</code>. <code>false</code> otherwise
     * @throws INDIValueException
     *             if <code>valueToCheck</code> is <code>null</code>.
     */
    @Override
    public boolean checkCorrectValue(Object valueToCheck) throws INDIValueException {
        if (valueToCheck == null) {
            throw new IllegalArgumentException("null value");
        }
        if (!(valueToCheck instanceof INDIBLOBValue)) {
            return false;
        }
        return true;
    }

    @Override
    public String getNameAndValueAsString() {
        return getName() + " - BLOB format: " + this.getValue().getFormat() + " - BLOB Size: " + this.getValue().getSize();
    }

    @Override
    public INDIBLOBValue getDesiredValue() {
        return desiredValue;
    }

    @Override
    public void setDesiredValue(Object desiredValue) throws INDIValueException {
        INDIBLOBValue b = null;
        try {
            b = (INDIBLOBValue) desiredValue;
        } catch (ClassCastException e) {
            throw new INDIValueException(this, "Value for a BLOB Element must be a INDIBLOBValue");
        }

        this.desiredValue = b;
    }

    @Override
    public boolean isChanged() {
        return desiredValue != null;
    }

    /**
     * Returns the XML code &lt;oneBLOB&gt; representing this BLOB Element with
     * a new desired value (a <code>INDIBLOBValue</code>). Resets the desired
     * value.
     * 
     * @return the XML code <code>&lt;oneBLOB&gt;</code> representing the BLOB
     *         Element with a new value.
     * @see #setDesiredValue
     */
    @Override
    protected OneElement<?> getXMLOneElementNewValue() {
        INDIBLOBValue ibv = desiredValue;

        OneBlob result = new OneBlob().setName(getName()).setByteContent(value.getBlobData()).setFormat(ibv.getFormat());

        desiredValue = null;

        return result;
    }

    @Override
    public String toString() {
        if (this.getValue().getSize() > 0) {
            return this.getValue().getFormat() + " (" + this.getValue().getSize() + " bytes)";
        }

        return "";
    }

    @Override
    public String getValueAsString() {
        return "BLOB format: " + this.getValue().getFormat() + " - BLOB Size: " + this.getValue().getSize();
    }
}
