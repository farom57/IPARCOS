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
import laazotea.indi.Constants.LightStates;
import laazotea.indi.INDIException;
import org.w3c.dom.Element;

/**
 * A class representing a INDI Light Element.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.32, February 4, 2012
 */
public class INDILightElement extends INDIElement {

  /**
   * Current State value for this Light Element.
   */
  private LightStates state;
  /**
   * A UI component that can be used in graphical interfaces for this Light
   * Element.
   */
  private INDIElementListener UIComponent;

  /**
   * Constructs an instance of
   * <code>INDILightElement</code>. Usually called from a
   * <code>INDIProperty</code>.
   *
   * @param xml A XML Element
   * <code>&lt;defLight&gt;</code> describing the Light Element.
   * @param property The
   * <code>INDIProperty</code> to which the Element belongs.
   * @throws IllegalArgumentException if the XML Element is not well formed or
   * the value is not a valid one.
   */
  protected INDILightElement(Element xml, INDIProperty property) throws IllegalArgumentException {
    super(xml, property);

    String sta = xml.getTextContent().trim();

    setValue(sta);
  }

  @Override
  public LightStates getValue() {
    return state;
  }

  /**
   * Sets the current value of this Light Element. It is assummed that the XML
   * Element is really describing the new value for this particular Light
   * Element. <p> This method will notify the change of the value to the
   * listeners.
   *
   * @param xml A XML Element &lt;oneLight&gt; describing the Element.
   * @throws IllegalArgumentException if the
   * <code>xml</code> is not well formed (the light status is not correct).
   */
  @Override
  protected void setValue(Element xml) throws IllegalArgumentException {
    String sta = xml.getTextContent().trim();

    setValue(sta);

    notifyListeners();
  }

  /**
   * Sets the state of the Light Element.
   *
   * @param newState The new state of the Light Element
   * @throws IllegalArgumentException if the new state is not correct ("Idle" or
   * "Ok" or "Busy" or "Alert").
   */
  private void setValue(String newState) throws IllegalArgumentException {
    if (newState.compareTo("Idle") == 0) {
      state = LightStates.IDLE;
    } else if (newState.compareTo("Ok") == 0) {
      state = LightStates.OK;
    } else if (newState.compareTo("Busy") == 0) {
      state = LightStates.BUSY;
    } else if (newState.compareTo("Alert") == 0) {
      state = LightStates.ALERT;
    } else {
      throw new IllegalArgumentException("Illegal Light Status");
    }
  }

  @Override
  public INDIElementListener getDefaultUIComponent() throws INDIException {
    if (UIComponent != null) {
      removeINDIElementListener(UIComponent);
    }

    Object[] arguments = new Object[]{this};
    String[] possibleUIClassNames = new String[]{"laazotea.indi.client.ui.INDILightElementPanel", "laazotea.indi.androidui.INDILightElementView"};

    try {
      UIComponent = (INDIElementListener) ClassInstantiator.instantiate(possibleUIClassNames, arguments);
    } catch (ClassCastException e) {
      throw new INDIException("The UI component is not a valid INDIElementListener. Probably a incorrect library in the classpath.");
    }

    addINDIElementListener(UIComponent);

    return UIComponent;
  }

  /**
   * Always returns true. This method should never be called as lights cannot be
   * setted by a client.
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
   * Always returns an empty ""
   * <code>String</code>. This method should never be called as lights cannot be
   * setted by a client.
   *
   * @return "";
   */
  @Override
  protected String getXMLOneElementNewValue() {
    return "";  // No XML for a light: it cannot be changed
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
