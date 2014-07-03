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
import laazotea.indi.INDIBLOBValue;
import laazotea.indi.INDIException;
import org.w3c.dom.Element;

/**
 * A class representing a INDI BLOB Element.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.32, February 4, 2012
 */
public class INDIBLOBElement extends INDIElement {

  /**
   * The current value of the BLOB Element
   */
  private INDIBLOBValue value;
  /**
   * The current desired value of the BLOB Element
   */
  private INDIBLOBValue desiredValue;
  /**
   * A UI component that can be used in graphical interfaces for this BLOB
   * Element.
   */
  private INDIElementListener UIComponent;

  /**
   * Constructs an instance of
   * <code>INDIBLOBElement</code>. Usually called from a
   * <code>INDIProperty</code>.
   *
   * @param xml A XML Element
   * <code>&lt;defBLOB&gt;</code> describing the BLOB Element.
   * @param property The
   * <code>INDIProperty</code> to which the Element belongs.
   * @throws IllegalArgumentException if the XML Element is not well formed.
   */
  protected INDIBLOBElement(Element xml, INDIProperty property) throws IllegalArgumentException {
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
   * Element. <p> This method will notify the change of the value to the
   * listeners.
   *
   * @param xml A XML Element &lt;oneBLOB&gt; describing the Element.
   * @throws IllegalArgumentException if the
   * <code>xml</code> is not well formed (no size, no format or incorrectly
   * coded data
   */
  @Override
  public void setValue(Element xml) throws IllegalArgumentException {
    value = new INDIBLOBValue(xml);

    notifyListeners();
  }

  @Override
  public INDIElementListener getDefaultUIComponent() throws INDIException {
    if (UIComponent != null) {
      removeINDIElementListener(UIComponent);
    }

    Object[] arguments = new Object[]{this, getProperty().getPermission()};
    String[] possibleUIClassNames = new String[]{"laazotea.indi.client.ui.INDIBLOBElementPanel", "laazotea.indi.androidui.INDIBLOBElementView"};

    try {
      UIComponent = (INDIElementListener) ClassInstantiator.instantiate(possibleUIClassNames, arguments);
    } catch (ClassCastException e) {
      throw new INDIException("The UI component is not a valid INDIElementListener. Probably a incorrect library in the classpath.");
    }

    addINDIElementListener(UIComponent);

    return UIComponent;
  }

  /**
   * Checks if a desired value would be correct to be applied to the BLOB
   * Element.
   *
   * @param desiredValue The value to be checked.
   * @return
   * <code>true</code> if the
   * <code>desiredValue</code> is a
   * <code>INDIBLOBValue</code>.
   * <code>false</code> otherwise
   * @throws INDIValueException if
   * <code>desiredValue</code> is
   * <code>null</code>.
   */
  @Override
  public boolean checkCorrectValue(Object desiredValue) throws INDIValueException {
    if (desiredValue == null) {
      throw new IllegalArgumentException("null value");
    }

    if (!(desiredValue instanceof INDIBLOBValue)) {
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
    if (desiredValue != null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the XML code &lt;oneBLOB&gt; representing this BLOB Element with a
   * new desired value (a
   * <code>INDIBLOBValue</code>). Resets the desired value.
   *
   * @return the XML code
   * <code>&lt;oneBLOB&gt;</code> representing the BLOB Element with a new
   * value.
   * @see #setDesiredValue
   */
  @Override
  protected String getXMLOneElementNewValue() {
    INDIBLOBValue ibv = (INDIBLOBValue) desiredValue;
    int size = ibv.getSize();

    String data = value.getBase64BLOBData();

    String xml = "<oneBLOB name=\"" + this.getName() + "\" size=\"" + size + "\" format=\"" + ibv.getFormat() + "\">" + data + "</oneBLOB>";

    desiredValue = null;

    return xml;
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
