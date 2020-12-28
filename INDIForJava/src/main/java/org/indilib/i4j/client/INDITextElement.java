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

import org.indilib.i4j.INDIException;
import org.indilib.i4j.protocol.DefText;
import org.indilib.i4j.protocol.OneElement;
import org.indilib.i4j.protocol.OneText;

/**
 * A class representing a INDI Text Element.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDITextElement extends INDIElement {

    /**
     * The current value of the Text Element.
     */
    private String value;

    /**
     * The current desired value for the Text Element.
     */
    private String desiredValue;

    /**
     * A UI component that can be used in graphical interfaces for this Text
     * Element.
     */
    private INDIElementListener uiComponent;

    /**
     * Constructs an instance of <code>INDITextElement</code>. Usually called
     * from a <code>INDIProperty</code>.
     * 
     * @param xml
     *            A XML Element <code>&lt;defText&gt;</code> describing the Text
     *            Element.
     * @param property
     *            The <code>INDIProperty</code> to which the Element belongs.
     */
    protected INDITextElement(DefText xml, INDITextProperty property) {
        super(xml, property);
        desiredValue = null;
        value = xml.getTextContent();
    }

    @Override
    public String getValue() {
        return value;
    }

    /**
     * Sets the current value of this Text Element. It is assummed that the XML
     * Element is really describing the new value for this particular Text
     * Element.
     * <p>
     * This method will notify the change of the value to the listeners.
     * 
     * @param xml
     *            A XML Element &lt;oneText&gt; describing the Element.
     */
    @Override
    protected void setValue(OneElement<?> xml) {
        value = xml.getTextContent();
        notifyListeners();
    }

    @Override
    public INDIElementListener getDefaultUIComponent() throws INDIException {
        if (uiComponent != null) {
            removeINDIElementListener(uiComponent);
        }

        uiComponent = INDIViewCreator.getDefault().createTextElementView(this, getProperty().getPermission());
        addINDIElementListener(uiComponent);

        return uiComponent;
    }

    /**
     * Checks if a desired value would be correct to be applied to the Text
     * Element.
     * 
     * @param valueToCheck
     *            The value to be checked.
     * @return <code>true</code> if the <code>valueToCheck</code> is a
     *         <code>String</code>. <code>false</code> otherwise.
     * @throws INDIValueException
     *             if <code>valueToCheck</code> is <code>null</code>.
     */
    @Override
    public boolean checkCorrectValue(Object valueToCheck) throws INDIValueException {
        if (valueToCheck == null) {
            throw new INDIValueException(this, "null value");
        }

        if (valueToCheck instanceof String) {
            return true;
        }

        return false;
    }

    @Override
    public String getNameAndValueAsString() {
        return getName() + " - " + getValue();
    }

    @Override
    public String getDesiredValue() {
        return desiredValue;
    }

    @Override
    public void setDesiredValue(Object desiredValue) throws INDIValueException {
        String v = null;

        try {
            v = (String) desiredValue;
        } catch (ClassCastException e) {
            throw new INDIValueException(this, "Value for a Text Element must be a String");
        }

        this.desiredValue = v;
    }

    @Override
    public boolean isChanged() {
        return desiredValue != null;
    }

    /**
     * Returns the XML code &lt;oneText&gt; representing this Text Element with
     * a new desired value (a <code>String</code>). Resets the desired value.
     * 
     * @return the XML code <code>&lt;oneText&gt;</code> representing the Text
     *         Element with a new value.
     * @see #setDesiredValue
     */
    @Override
    protected OneElement<?> getXMLOneElementNewValue() {
        OneText xml = new OneText().setName(getName()).setTextContent(desiredValue);

        desiredValue = null;

        return xml;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return getValue();
    }
}
