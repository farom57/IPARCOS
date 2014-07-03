/*
 *  This file is part of INDI for Java Client.
 * 
 *  INDI for Java Client is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU General Public License 
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 * 
 *  INDI for Java Client is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 *  of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with INDI for Java Client.  If not, see 
 *  <http://www.gnu.org/licenses/>.
 */
package laazotea.indi.client;

import java.util.Formatter;
import java.util.Locale;
import laazotea.indi.ClassInstantiator;
import laazotea.indi.INDIException;
import laazotea.indi.INDISexagesimalFormatter;
import org.w3c.dom.Element;

/**
 * A class representing a INDI Number Element.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.32, February 4, 2012
 */
public class INDINumberElement extends INDIElement {

  /**
   * The current value of this Number Element.
   */
  private double value;
  /**
   * The current desired value for the Element
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
  private INDIElementListener UIComponent;

  /**
   * Constructs an instance of
   * <code>INDINumberElement</code>. Usually called from a
   * <code>INDIProperty</code>.
   *
   * @param xml A XML Element
   * <code>&lt;defNumber&gt;</code> describing the Number Element.
   * @param property The
   * <code>INDIProperty</code> to which the Element belongs.
   * @throws IllegalArgumentException if the XML Element is not well formed (any
   * of the max, min, step or value are not correct numbers, if the format if
   * not correct or if the value is not within [min, max]).
   */
  protected INDINumberElement(Element xml, INDIProperty property) throws IllegalArgumentException {
    super(xml, property);

    desiredValue = null;

    String valueS = xml.getTextContent().trim();
    String minS = xml.getAttribute("min").trim();
    String maxS = xml.getAttribute("max").trim();
    String stepS = xml.getAttribute("step").trim();
    String nf = xml.getAttribute("format").trim();

    setNumberFormat(nf);

    setMin(minS);

    setMax(maxS);

    setStep(stepS);

    setValue(valueS);
  }

  /**
   * Set the number format for this Number Element.
   *
   * @param newNumberFormat The new number format.
   * @throws IllegalArgumentException if the number format is not correct.
   */
  private void setNumberFormat(String newNumberFormat) throws IllegalArgumentException {
    newNumberFormat = newNumberFormat.trim();

    if (!newNumberFormat.startsWith("%")) {
      throw new IllegalArgumentException("Number format not starting with %\n");
    }

    if ((!newNumberFormat.endsWith("f")) && (!newNumberFormat.endsWith("e")) && (!newNumberFormat.endsWith("E")) && (!newNumberFormat.endsWith("g")) && (!newNumberFormat.endsWith("G")) && (!newNumberFormat.endsWith("m"))) {
      throw new IllegalArgumentException("Number format not recognized%\n");
    }

    if (newNumberFormat.endsWith("m")) {
      sFormatter = new INDISexagesimalFormatter(newNumberFormat);
    }

    this.numberFormat = newNumberFormat;
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
   * Gets the maximum for this Number Element formated as a String according to
   * the number format.
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
   * Gets the minimum for this Number Element formated as a String according to
   * the number format.
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
   * Gets the step for this Number Element formated as a String according to the
   * number format.
   *
   * @return The step for this Number Element formatted as a String.
   */
  public String getStepAsString() {
    return getNumberAsString(getStep());
  }

  /**
   * Gets the value of this Number Element formated as a String according to the
   * number format.
   *
   * @return The value of this Number Element formatted as a String.
   */
  @Override
  public String getValueAsString() {
    return getNumberAsString((Double) getValue());
  }

  /**
   * Returns a number formatted according to the Number Format of this Number
   * Element.
   *
   * @param number the number to be formatted.
   * @return the number formatted according to the Number Format of this Number
   * Element.
   */
  private String getNumberAsString(double number) {
    String aux;

    if (numberFormat.endsWith("m")) {
      aux = sFormatter.format(number);
    } else {
      Formatter formatter = new Formatter(Locale.US);
      aux = formatter.format(getNumberFormat(), number).toString();
    }

    return aux;
  }

  @Override
  public Double getValue() {
    return value;
  }

  /**
   * Sets the current value of this Number Element. It is assummed that the XML
   * Element is really describing the new value for this particular Number
   * Element. <p> This method will notify the change of the value to the
   * listeners.
   *
   * @param xml A XML Element &lt;oneNumber&gt; describing the Element.
   * @throws IllegalArgumentException if the
   * <code>xml</code> is not well formed (the value is not a correct number or
   * it is not in the [min, max] range).
   */
  @Override
  protected void setValue(Element xml) throws IllegalArgumentException {
    String valueS = xml.getTextContent().trim();

    setValue(valueS);

    notifyListeners();
  }

  /**
   * Sets the maximum of this Number Element
   *
   * @param maxS A String with the maximum value of this Number Element
   */
  private void setMax(String maxS) {
    max = parseNumber(maxS);
  }

  /**
   * Sets the minimum of this Number Element
   *
   * @param maxS A String with the minimum value of this Number Element
   */
  private void setMin(String minS) {
    min = parseNumber(minS);
  }

  /**
   * Sets the step of this Number Element
   *
   * @param maxS A String with the step value of this Number Element
   */
  private void setStep(String stepS) {
    step = parseNumber(stepS);
  }

  /**
   * Sets the value of this Number Element
   *
   * @param valueS A String with the new value of this Number Element
   * @throws IllegalArgumentException the value is not a correct number or it is
   * not in the [min, max] range.
   */
  private void setValue(String valueS) throws IllegalArgumentException {
    value = parseNumber(valueS);

    if ((value < min) || (value > max)) {
      throw new IllegalArgumentException(this.getProperty().getName() + " ; " + getName() + " ; " + "Number (" + valueS + ") not in range [" + min + ", " + max + "]");
    }
  }

  /**
   * Parses a number according to the Number Format of this Number Element.
   *
   * @param number The number to be parsed.
   * @return the parsed number @throw IllegalArgumentException if the
   * <code>number</code> is not correctly formatted.
   */
  private double parseNumber(String number) throws IllegalArgumentException {
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
    if (UIComponent != null) {
      removeINDIElementListener(UIComponent);
    }

    Object[] arguments = new Object[]{this, getProperty().getPermission()};
    String[] possibleUIClassNames = new String[]{"laazotea.indi.client.ui.INDINumberElementPanel", "laazotea.indi.androidui.INDINumberElementView"};

    try {
      UIComponent = (INDIElementListener) ClassInstantiator.instantiate(possibleUIClassNames, arguments);
    } catch (ClassCastException e) {
      throw new INDIException("The UI component is not a valid INDIElementListener. Probably a incorrect library in the classpath.");
    }

    addINDIElementListener(UIComponent);

    return UIComponent;
  }

  /**
   * Checks if a desired desiredValue would be correct to be applied to the
   * Number Element.
   *
   * @param desiredValue The desiredValue to be checked (usually a String, but
   * can be a Double).
   * @return
   * <code>true</code> if the
   * <code>desiredValue</code> is a valid Double or a correct String according
   * to the Number Format.
   * <code>false</code> otherwise.
   * @throws INDIValueException if
   * <code>desiredValue</code> is
   * <code>null</code> or if it is not a Double or a correctly formatted
   * <code>String</code>.
   */
  @Override
  public boolean checkCorrectValue(Object desiredValue) throws INDIValueException {
    if (desiredValue == null) {
      throw new INDIValueException(this, "null value");
    }

    double d;

    if (desiredValue instanceof Double) {
      d = ((Double) desiredValue).doubleValue();
    } else {
      if (desiredValue instanceof String) {
        String val;

        val = ((String) desiredValue).trim();
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
   * @param desiredValue The new desired Value
   * @throws IllegalArgumentException if the desired value not in range
   */
  private void setDesiredValueAsString(String desiredValue) throws INDIValueException {
    double dd = parseNumber(desiredValue);

    if ((dd < min) || (dd > max)) {
      throw new INDIValueException(this, getName() + " ; " + "Number (" + desiredValue + ") not in range [" + min + ", " + max + "]");
    }

    this.desiredValue = new Double(dd);
  }

  /**
   * Sets the desired value from a double.
   *
   * @param desiredValue The new desired Value
   * @throws IllegalArgumentException if the desired value not in range
   */
  private void setDesiredValueAsdouble(double desiredValue) throws INDIValueException {
    double dd = desiredValue;

    if ((dd < min) || (dd > max)) {
      throw new INDIValueException(this, getName() + " ; " + "Number (" + value + ") not in range [" + min + ", " + max + "]");
    }

    this.desiredValue = new Double(dd);
  }

  @Override
  public boolean isChanged() {
    if (desiredValue != null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the XML code &lt;oneNumber&gt; representing this Number Element
   * with a new desired value. Resets the desired value.
   *
   * @return the XML code
   * <code>&lt;oneNumber&gt;</code> representing this Number Element with a new
   * value.
   * @see #setDesiredValue
   */
  @Override
  protected String getXMLOneElementNewValue() {
    String xml = "<oneNumber name=\"" + this.getName() + "\">" + desiredValue.doubleValue() + "</oneNumber>";

    desiredValue = null;

    return xml;
  }

  @Override
  public String toString() {
    return this.getNumberAsString(value).trim();
  }
}
