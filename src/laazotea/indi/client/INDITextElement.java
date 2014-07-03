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

import laazotea.indi.ClassInstantiator;
import laazotea.indi.INDIException;
import org.w3c.dom.Element;

/**
 * A class representing a INDI Text Element.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.32, February 4, 2012
 */
public class INDITextElement extends INDIElement {

  /**
   * The current value of the Text Element
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
  private INDIElementListener UIComponent;

  /**
   * Constructs an instance of
   * <code>INDITextElement</code>. Usually called from a
   * <code>INDIProperty</code>.
   *
   * @param xml A XML Element
   * <code>&lt;defText&gt;</code> describing the Text Element.
   * @param property The
   * <code>INDIProperty</code> to which the Element belongs.
   * @throws IllegalArgumentException if the XML Element is not well formed.
   */
  protected INDITextElement(Element xml, INDIProperty property) throws IllegalArgumentException {
    super(xml, property);

    desiredValue = null;

    value = xml.getTextContent().trim();
  }

  @Override
  public String getValue() {
    return value;
  }

  /**
   * Sets the current value of this Text Element. It is assummed that the XML
   * Element is really describing the new value for this particular Text
   * Element. <p> This method will notify the change of the value to the
   * listeners.
   *
   * @param xml A XML Element &lt;oneText&gt; describing the Element.
   * @throws IllegalArgumentException if the
   * <code>xml</code> is not well formed.
   */
  @Override
  protected void setValue(Element xml) throws IllegalArgumentException {
    value = xml.getTextContent().trim();

    notifyListeners();
  }

  @Override
  public INDIElementListener getDefaultUIComponent() throws INDIException {
    if (UIComponent != null) {
      removeINDIElementListener(UIComponent);
    }


    Object[] arguments = new Object[]{this, getProperty().getPermission()};
    String[] possibleUIClassNames = new String[]{"laazotea.indi.client.ui.INDITextElementPanel", "laazotea.indi.androidui.INDITextElementView"};

    try {
      UIComponent = (INDIElementListener) ClassInstantiator.instantiate(possibleUIClassNames, arguments);
    } catch (ClassCastException e) {
      throw new INDIException("The UI component is not a valid INDIElementListener. Probably a incorrect library in the classpath.");
    }

    addINDIElementListener(UIComponent);

    return UIComponent;
  }

  /**
   * Checks if a desired value would be correct to be applied to the Text
   * Element.
   *
   * @param desiredValue The value to be checked.
   * @return
   * <code>true</code> if the
   * <code>desiredValue</code> is a
   * <code>String</code>.
   * <code>false</code> otherwise.
   * @throws INDIValueException if
   * <code>desiredValue</code> is
   * <code>null</code>.
   */
  @Override
  public boolean checkCorrectValue(Object desiredValue) throws INDIValueException {
    if (desiredValue == null) {
      throw new INDIValueException(this, "null value");
    }

    if (desiredValue instanceof String) {
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
    if (desiredValue != null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the XML code &lt;oneText&gt; representing this Text Element with a
   * new desired value (a
   * <code>String</code>). Resets the desired value.
   *
   * @return the XML code
   * <code>&lt;oneText&gt;</code> representing the Text Element with a new
   * value.
   * @see #setDesiredValue
   */
  @Override
  protected String getXMLOneElementNewValue() {
    String xml = "<oneText name=\"" + this.getName() + "\">" + desiredValue + "</oneText>";

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
