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

import java.util.Formatter;
import java.util.Locale;

import org.indilib.i4j.protocol.DefNumber;
import org.indilib.i4j.protocol.OneElement;
import org.indilib.i4j.protocol.OneNumber;
import org.indilib.i4j.INDIException;
import org.indilib.i4j.INDISexagesimalFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing a INDI Number Element.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDINumberElement extends INDIElement {

    /**
     * A logger for the errors.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INDINumberElement.class);

    /**
     * The current value of this Number Element.
     */
    private double value;

    /**
     * The current desired value for the Element.
     */
    private Double desiredValue;

    /**
     * The number format of this Number Element.
     */
    private String numberFormat;

    /**
     * The minimum value for this Number Element.
     */
    private double min;

    /**
     * The maximum value for this Number Element.
     */
    private double max;

    /**
     * The step for this Number Element.
     */
    private double step;

    /**
     * A formatter used to parse and format the values.
     */
    private INDISexagesimalFormatter sFormatter;

    /**
     * A UI component that can be used in graphical interfaces for this Number
     * Element.
     */
    private INDIElementListener uiComponent;

    /**
     * Constructs an instance of <code>INDINumberElement</code>. Usually called
     * from a <code>INDIProperty</code>. Throws IllegalArgumentException if the
     * XML Element is not well formed (any of the max, min, step or value are
     * not correct numbers, if the format if not correct or if the value is not
     * within [min, max]).
     * 
     * @param xml
     *            A XML Element <code>&lt;defNumber&gt;</code> describing the
     *            Number Element.
     * @param property
     *            The <code>INDIProperty</code> to which the Element belongs.
     */
    protected INDINumberElement(DefNumber xml, INDINumberProperty property) {
        super(xml, property);
        desiredValue = null;
        setNumberFormat(xml.getFormat());
        setMin(xml.getMin());
        setMax(xml.getMax());
        setStep(xml.getStep());
        setValue(xml.getTextContent());
    }

    /**
     * Set the number format for this Number Element.
     * 
     * @param newNumberFormat
     *            The new number format.
     */
    private void setNumberFormat(String newNumberFormat) {

        if (!newNumberFormat.startsWith("%")) {
            throw new IllegalArgumentException("Number format not starting with %\n");
        }

        if (!newNumberFormat.endsWith("f") && !newNumberFormat.endsWith("e") && !newNumberFormat.endsWith("E") && !newNumberFormat.endsWith("g")
                && !newNumberFormat.endsWith("G") && !newNumberFormat.endsWith("m")) {
            throw new IllegalArgumentException("Number format not recognized%\n");
        }

        if (newNumberFormat.endsWith("m")) {
            sFormatter = new INDISexagesimalFormatter(newNumberFormat);
        }

        if (newNumberFormat.equals("%0.f") || newNumberFormat.equals("%.f")) {
            newNumberFormat = "%.0f";
        }

        numberFormat = newNumberFormat;
    }

    /**
     * Gets the maximum for this Number Element.
     * 
     * @return The maximum for this Number Element.
     */
    public double getMax() {
        return max;
    }

    /**
     * Gets the maximum for this Number Element formated as a String according
     * to the number format.
     * 
     * @return The maximum for this Number Element formatted as a String.
     */
    public String getMaxAsString() {
        return getNumberAsString(getMax());
    }

    /**
     * Gets the minimum for this Number Element.
     * 
     * @return The minimum for this Number Element.
     */
    public double getMin() {
        return min;
    }

    /**
     * Gets the minimum for this Number Element formated as a String according
     * to the number format.
     * 
     * @return The minimum for this Number Element formatted as a String.
     */
    public String getMinAsString() {
        return getNumberAsString(getMin());
    }

    /**
     * Gets the number format of this Number Element.
     * 
     * @return the number format of this Number Element.
     */
    public String getNumberFormat() {
        return numberFormat;
    }

    /**
     * Gets the step for this Number Element.
     * 
     * @return The step for this Number Element.
     */
    public double getStep() {
        return step;
    }

    /**
     * Gets the step for this Number Element formated as a String according to
     * the number format.
     * 
     * @return The step for this Number Element formatted as a String.
     */
    public String getStepAsString() {
        return getNumberAsString(getStep());
    }

    /**
     * Gets the value of this Number Element formated as a String according to
     * the number format.
     *
     * @return The value of this Number Element formatted as a String.
     */
    @Override
    public String getValueAsString() {
        return getNumberAsString(getValue());
    }

    /**
     * Returns a number formatted according to the Number Format of this Number
     * Element.
     * 
     * @param number
     *            the number to be formatted.
     * @return the number formatted according to the Number Format of this
     *         Number Element.
     */
    private String getNumberAsString(double number) {
        String aux;

        if (numberFormat.endsWith("m")) {
            aux = sFormatter.format(number);
        } else {
            Formatter formatter = new Formatter(Locale.US);
            aux = formatter.format(getNumberFormat(), number).toString();
            formatter.close();
        }

        return aux;
    }

    @Override
    public Double getValue() {
        return value;
    }

    /**
     * Sets the current value of this Number Element. It is assummed that the
     * XML Element is really describing the new value for this particular Number
     * Element.
     * <p>
     * This method will notify the change of the value to the listeners.
     * <p>
     * Throws IllegalArgumentException if the <code>xml</code> is not well
     * formed (the value is not a correct number or it is not in the [min, max]
     * range).
     * 
     * @param xml
     *            A XML Element &lt;oneNumber&gt; describing the Element.
     */
    @Override
    protected void setValue(OneElement<?> xml) {
        setValue(xml.getTextContent());
        notifyListeners();
    }

    /**
     * Sets the maximum of this Number Element.
     * 
     * @param maxS
     *            A String with the maximum value of this Number Element
     */
    private void setMax(String maxS) {
        max = parseNumber(maxS);
    }

    /**
     * Sets the minimum of this Number Element.
     * 
     * @param minS
     *            A String with the minimum value of this Number Element
     */
    private void setMin(String minS) {
        min = parseNumber(minS);
    }

    /**
     * Sets the step of this Number Element.
     * 
     * @param stepS
     *            A String with the step value of this Number Element
     */
    private void setStep(String stepS) {
        step = parseNumber(stepS);
    }

    /**
     * Sets the value of this Number Element. Initially it threw
     * IllegalArgumentException the value is not a correct number or it is not
     * in the [min, max] range. However some existing drivers send values
     * outside the [min, max] range. Therefore it does not throw the exception
     * when the value is outside the range. HERE WE SHOULD LOG THIS PROBLEM IN
     * ORDER TO TELL DRIVER PROGRAMERS TO AVOID THESE INCORRECT VALUES.
     * 
     * @param valueS
     *            A String with the new value of this Number Element
     */
    private void setValue(String valueS) {
        value = parseNumber(valueS);

        if (value < min || value > max) {
            String message = this.getProperty().getName() + " ; " + getName() + " ; " + "Number (" + valueS + ") not in range [" + min + ", " + max + "]";
            LOG.error(message);
            // throw new IllegalArgumentException();
        }
    }

    /**
     * Parses a number according to the Number Format of this Number Element.
     * Throws IllegalArgumentException if the <code>number</code> is not
     * correctly formatted.
     * 
     * @param number
     *            The number to be parsed.
     * @return the parsed number
     */
    private double parseNumber(String number) {
        double res;

        if (numberFormat.endsWith("m")) {
            res = sFormatter.parseSexagesimal(number);
        } else {
            try {
                res = Double.parseDouble(number);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Number value not correct");
            }
        }

        return res;
    }

    @Override
    public INDIElementListener getDefaultUIComponent() throws INDIException {
        if (uiComponent != null) {
            removeINDIElementListener(uiComponent);
        }
        uiComponent = INDIViewCreator.getDefault().createNumberElementView(this, getProperty().getPermission());
        addINDIElementListener(uiComponent);

        return uiComponent;
    }

    /**
     * Checks if a desired valueToCheck would be correct to be applied to the
     * Number Element.
     * 
     * @param valueToCheck
     *            The valueToCheck to be checked (usually a String, but can be a
     *            Double).
     * @return <code>true</code> if the <code>valueToCheck</code> is a valid
     *         Double or a correct String according to the Number Format.
     *         <code>false</code> otherwise.
     * @throws INDIValueException
     *             if <code>valueToCheck</code> is <code>null</code> or if it is
     *             not a Double or a correctly formatted <code>String</code>.
     */
    @Override
    public boolean checkCorrectValue(Object valueToCheck) throws INDIValueException {
        if (valueToCheck == null) {
            throw new INDIValueException(this, "null value");
        }

        double d;

        if (valueToCheck instanceof Double) {
            d = ((Double) valueToCheck).doubleValue();
        } else {
            if (valueToCheck instanceof String) {
                String val = (String) valueToCheck;
                try {
                    d = parseNumber(val);
                } catch (IllegalArgumentException e) {
                    throw new INDIValueException(this, e.getMessage());
                }
            } else {
                throw new INDIValueException(this, "The number value is not correct (not Double nor String)");
            }
        }

        if (d < min) {
            throw new INDIValueException(this, "Number less than minimum (" + getMinAsString() + ")");
        }

        if (d > max) {
            throw new INDIValueException(this, "Number greater than maximum (" + getMaxAsString() + ")");
        }

        return true;
    }

    @Override
    public String getNameAndValueAsString() {
        return getName() + " - " + this.getValueAsString();
    }

    @Override
    public Double getDesiredValue() {
        return desiredValue;
    }

    @Override
    public void setDesiredValue(Object desiredValue) throws INDIValueException {
        if (desiredValue instanceof String) {
            setDesiredValueAsString((String) desiredValue);
        } else if (desiredValue instanceof Double) {
            setDesiredValueAsdouble(((Double) desiredValue).doubleValue());
        } else {
            throw new INDIValueException(this, "Value for a Number Element must be a String or a Double");
        }
    }

    /**
     * Sets the desired value from a String.
     * 
     * @param newDesiredValue
     *            The new desired Value
     * @throws INDIValueException
     *             if the desired value not in range
     */
    private void setDesiredValueAsString(String newDesiredValue) throws INDIValueException {
        double dd = parseNumber(newDesiredValue);

        if (dd < min || dd > max) {
            throw new INDIValueException(this, getName() + " ; " + "Number (" + newDesiredValue + ") not in range [" + min + ", " + max + "]");
        }

        desiredValue = new Double(dd);
    }

    /**
     * Sets the desired value from a double.
     * 
     * @param newDesiredValue
     *            The new desired Value
     * @throws INDIValueException
     *             if the desired value not in range
     */
    private void setDesiredValueAsdouble(double newDesiredValue) throws INDIValueException {
        double dd = newDesiredValue;

        if (dd < min || dd > max) {
            throw new INDIValueException(this, getName() + " ; " + "Number (" + value + ") not in range [" + min + ", " + max + "]");
        }

        desiredValue = new Double(dd);
    }

    @Override
    public boolean isChanged() {
        return desiredValue != null;
    }

    /**
     * Returns the XML code &lt;oneNumber&gt; representing this Number Element
     * with a new desired value. Resets the desired value.
     * 
     * @return the XML code <code>&lt;oneNumber&gt;</code> representing this
     *         Number Element with a new value.
     * @see #setDesiredValue
     */
    @Override
    protected OneNumber getXMLOneElementNewValue() {
        OneNumber xml = new OneNumber().setName(getName()).setTextContent(Double.toString(desiredValue.doubleValue()));

        desiredValue = null;

        return xml;
    }

    @Override
    public String toString() {
        return this.getNumberAsString(value).trim();
    }
}
