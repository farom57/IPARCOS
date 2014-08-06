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

import java.util.ArrayList;
import java.util.List;
import laazotea.indi.ClassInstantiator;
import laazotea.indi.Constants.PropertyPermissions;
import laazotea.indi.Constants.PropertyStates;
import laazotea.indi.Constants.SwitchRules;
import laazotea.indi.Constants.SwitchStatus;
import laazotea.indi.INDIDateFormat;
import laazotea.indi.INDIException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A class representing a INDI Switch Property. <p> It implements a listener
 * mechanism to notify changes in its Elements.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.36, November 18, 2013
 */
public class INDISwitchProperty extends INDIProperty {

  /**
   * A UI component that can be used in graphical interfaces for this Switch
   * Property.
   */
  private INDIPropertyListener UIComponent;
  /**
   * The current Rule for this Switch Property.
   */
  private SwitchRules rule;

  /**
   * Constructs an instance of
   * <code>INDISwitchProperty</code>.
   * <code>INDISwitchProperty</code>s are not usually directly instantiated.
   * Usually used by
   * <code>INDIDevice</code>.
   *
   * @param xml A XML Element <code>&lt;defSwitchVector&gt;</code> describing
   * the Property.
   * @param device The <code>INDIDevice</code> to which this Property belongs.
   * @throws IllegalArgumentException if the XML Property is not well formed
   * (for example if the Elements are not well formed or if the Rule is not
   * valid).
   */
  protected INDISwitchProperty(Element xml, INDIDevice device) throws IllegalArgumentException {
    super(xml, device);

    String rul = xml.getAttribute("rule").trim();

    if (rul.compareTo("OneOfMany") == 0) {
      rule = SwitchRules.ONE_OF_MANY;
    } else if (rul.compareTo("AtMostOne") == 0) {
      rule = SwitchRules.AT_MOST_ONE;
    } else if (rul.compareTo("AnyOfMany") == 0) {
      rule = SwitchRules.ANY_OF_MANY;
    } else {
      throw new IllegalArgumentException("Illegal Rule for the Switch Property");
    }

    NodeList list = xml.getElementsByTagName("defSwitch");

    for (int i = 0 ; i < list.getLength() ; i++) {
      Element child = (Element)list.item(i);

      String name = child.getAttribute("name");

      INDIElement iel = getElement(name);

      if (iel != null) { // It already exists
      } else {  // Does not exist
        INDISwitchElement ite = new INDISwitchElement(child, this);
        addElement(ite);
      }
    }

    if (!checkCorrectValues()) {
      if (getSelectedCount() != 0) {  // Sometimes de CONFIG_PROCESS is not correct at the beginning. skip
        throw new IllegalArgumentException("Illegal initial value for Switch Property");
      }

      setState(PropertyStates.ALERT);
    }
  }

  @Override
  protected void update(Element el) {
    super.update(el, "oneSwitch");

    if (!checkCorrectValues()) {
      setState(PropertyStates.ALERT);
    }
  }

  /**
   * Gets the current Rule for this Switch Property
   *
   * @return the current Rule for this Switch Property
   */
  public SwitchRules getRule() {
    return rule;
  }

  /**
   * Sets the Permission of this Property. If set to Write Only it defaults to
   * Read Only (Switch properties cannot be Read Only).
   *
   * @param permission the new Permission for this Property.
   */
  @Override
  protected void setPermission(PropertyPermissions permission) {
    if (permission == PropertyPermissions.WO) {
      super.setPermission(PropertyPermissions.RO);
    } else {
      super.setPermission(permission);
    }
  }

  /**
   * Checks if the Rule of this Switch property holds.
   *
   * @return <code>true</code> if the values of the Elements of this Property
   * comply with the Rule. <code>false</code> otherwise.
   */
  private boolean checkCorrectValues() {
    if (getState() == PropertyStates.OK) {

      int selectedCount = getSelectedCount();

      if ((rule == SwitchRules.ONE_OF_MANY) && (selectedCount != 1)) {
        return false;
      }

      if ((rule == SwitchRules.AT_MOST_ONE) && (selectedCount > 1)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Gets the number of selected Switch Elements.
   *
   * @return the number of selected Elements.
   */
  private int getSelectedCount() {
    int selectedCount = 0;

    List<INDIElement> list = getElementsAsList();

    for (int i = 0 ; i < list.size() ; i++) {
      INDISwitchElement el = (INDISwitchElement)list.get(i);
      //     System.out.println("-->" + el.getName() + el.getValue());
      if (el.getValue() == SwitchStatus.ON) {
        selectedCount++;
      }
    }

    return selectedCount;
  }

  /**
   * Gets the opening XML Element &lt;newSwitchVector&gt; for this Property.
   *
   * @return the opening XML Element &lt;newSwitchVector&gt; for this Property.
   */
  @Override
  protected String getXMLPropertyChangeInit() {
    String xml = "<newSwitchVector device=\"" + getDevice().getName() + "\" name=\"" + getName() + "\" timestamp=\"" + INDIDateFormat.getCurrentTimestamp() + "\">";

    return xml;
  }

  /**
   * Gets the closing XML Element &lt;/newSwitchVector&gt; for this Property.
   *
   * @return the closing XML Element &lt;/newSwitchVector&gt; for this Property.
   */
  @Override
  protected String getXMLPropertyChangeEnd() {
    String xml = "</newSwitchVector>";

    return xml;
  }

  @Override
  public INDIPropertyListener getDefaultUIComponent() throws INDIException {
    if (UIComponent != null) {
      removeINDIPropertyListener(UIComponent);
    }

    Object[] arguments = new Object[]{this};
    String[] possibleUIClassNames;

    if (getName().equals("CONNECTION")) {
      possibleUIClassNames = new String[]{"laazotea.indi.client.ui.INDIConnectionPropertyPanel", "laazotea.indi.client.ui.INDIDefaultPropertyPanel", "laazotea.indi.androidui.INDIDefaultPropertyView"};
    } else if (rule == SwitchRules.ONE_OF_MANY) {
      possibleUIClassNames = new String[]{"laazotea.indi.client.ui.INDISwitchOneOfManyPropertyPanel", "laazotea.indi.androidui.INDIDefaultPropertyView"};
    } else {
      possibleUIClassNames = new String[]{"laazotea.indi.client.ui.INDIDefaultPropertyPanel", "laazotea.indi.androidui.INDIDefaultPropertyView"};
    }

    try {
      UIComponent = (INDIPropertyListener)ClassInstantiator.instantiate(possibleUIClassNames, arguments);
    } catch (ClassCastException e) {
      throw new INDIException("The UI component is not a valid INDIPropertyListener. Probably a incorrect library in the classpath.");
    }

    addINDIPropertyListener(UIComponent);

    return UIComponent;
  }

  /**
   * Gets a particular Element of this Property by its name.
   *
   * @param name The name of the Element to be returned
   * @return The Element of this Property with the given <code>name</code>.
   * <code>null</code> if there is no Element with that <code>name</code>.
   */
  @Override
  public final INDISwitchElement getElement(String name) {
    return (INDISwitchElement)super.getElement(name);
  }

  /**
   * Gets the values of the Property as a String.
   *
   * @return A String representation of the value of the Property.
   */
  @Override
  public String getValuesAsString() {
    String aux = "";

    ArrayList<INDIElement> l = this.getElementsAsList();
    int n = 0;

    for (int i = 0 ; i < l.size() ; i++) {
      if (l.get(i).getValue() == SwitchStatus.ON) {
        if (n == 0) {
          aux += l.get(i).getLabel();
          n++;
        } else {
          aux += ", " + l.get(i).getLabel();
          n++;
        }
      }
    }

    if (n > 1) {
      return "[" + aux + "]";
    }

    return aux;
  }
}


