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
import laazotea.indi.Constants.PropertyPermissions;
import laazotea.indi.INDIException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A class representing a INDI Light Property.<p> It implements a listener
 * mechanism to notify changes in its Elements.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.36, November 17, 2013
 */
public class INDILightProperty extends INDIProperty {

  /**
   * A UI component that can be used in graphical interfaces for this Light
   * Property.
   */
  private INDIPropertyListener UIComponent;

  /**
   * Constructs an instance of
   * <code>INDILightProperty</code>.
   * <code>INDILightProperty</code>s are not usually directly instantiated.
   * Usually used by
   * <code>INDIDevice</code>.
   *
   * @param xml A XML Element <code>&lt;defLightVector&gt;</code> describing the
   * Property.
   * @param device The <code>INDIDevice</code> to which this Property belongs.
   * @throws IllegalArgumentException if the XML Property is not well formed
   * (for example if the Elements are not well formed).
   */
  protected INDILightProperty(Element xml, INDIDevice device) throws IllegalArgumentException {
    super(xml, device);

    NodeList list = xml.getElementsByTagName("defLight");

    for (int i = 0 ; i < list.getLength() ; i++) {
      Element child = (Element)list.item(i);

      String name = child.getAttribute("name");

      INDIElement iel = getElement(name);

      if (iel != null) { // It already exists
      } else {  // Does not exist
        INDILightElement ite = new INDILightElement(child, this);
        addElement(ite);
      }
    }
  }

  @Override
  protected void update(Element el) {
    super.update(el, "oneLight");
  }

  /**
   * Always sets the permission to Read Only as lights may not change.
   *
   * @param permission ignored.
   */
  @Override
  protected void setPermission(PropertyPermissions permission) {
    super.setPermission(PropertyPermissions.RO);
  }

  /**
   * Sets the timeout to 0 as lights may not change.
   *
   * @param timeout ignored.
   */
  @Override
  protected void setTimeout(int timeout) {
    super.setTimeout(0);
  }

  /**
   * Gets an empty
   * <code>String</code> as Light Properties cannot be changed by clients.
   *
   * @return "" a empty <code>String</code>
   */
  @Override
  protected String getXMLPropertyChangeInit() {
    return "";  // A light cannot change
  }

  /**
   * Gets an empty
   * <code>String</code> as Light Properties cannot be changed by clients.
   *
   * @return "" a empty <code>String</code>
   */
  @Override
  protected String getXMLPropertyChangeEnd() {
    return "";  // A light cannot change
  }

  @Override
  public INDIPropertyListener getDefaultUIComponent() throws INDIException {
    if (UIComponent != null) {
      removeINDIPropertyListener(UIComponent);
    }

    Object[] arguments = new Object[]{this};
    String[] possibleUIClassNames = new String[]{"laazotea.indi.client.ui.INDIDefaultPropertyPanel", "laazotea.indi.androidui.INDIDefaultPropertyView"};

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
  public INDILightElement getElement(String name) {
    return (INDILightElement)super.getElement(name);
  }
}
