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
import laazotea.indi.INDIDateFormat;
import laazotea.indi.INDIException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A class representing a INDI Text Property.<p> It implements a listener
 * mechanism to notify changes in its Elements.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.36, November 17, 2013
 */
public class INDITextProperty extends INDIProperty {

  /**
   * A UI component that can be used in graphical interfaces for this Text
   * Property.
   */
  private INDIPropertyListener UIComponent;

  /**
   * Constructs an instance of
   * <code>INDITextProperty</code>.
   * <code>INDITextProperty</code>s are not usually directly instantiated.
   * Usually used by
   * <code>INDIDevice</code>.
   *
   * @param xml A XML Element <code>&lt;defTextVector&gt;</code> describing the
   * Property.
   * @param device The <code>INDIDevice</code> to which this Property belongs.
   * @throws IllegalArgumentException if the XML Property is not well formed
   * (for example if the Elements are not well formed).
   */
  protected INDITextProperty(Element xml, INDIDevice device) throws IllegalArgumentException {
    super(xml, device);

    NodeList list = xml.getElementsByTagName("defText");

    for (int i = 0 ; i < list.getLength() ; i++) {
      Element child = (Element)list.item(i);

      String name = child.getAttribute("name");

      INDIElement iel = getElement(name);

      if (iel != null) { // It already exists
      } else {  // Does not exist
        INDITextElement ite = new INDITextElement(child, this);
        addElement(ite);
      }
    }
  }

  @Override
  protected void update(Element el) {
    super.update(el, "oneText");
  }

  /**
   * Gets the opening XML Element &lt;newTextVector&gt; for this Property.
   *
   * @return the opening XML Element &lt;newTextVector&gt; for this Property.
   */
  @Override
  protected String getXMLPropertyChangeInit() {
    String xml = "<newTextVector device=\"" + getDevice().getName() + "\" name=\"" + getName() + "\" timestamp=\"" + INDIDateFormat.getCurrentTimestamp() + "\">";

    return xml;
  }

  /**
   * Gets the closing XML Element &lt;/newTextVector&gt; for this Property.
   *
   * @return the closing XML Element &lt;/newTextVector&gt; for this Property.
   */
  @Override
  protected String getXMLPropertyChangeEnd() {
    String xml = "</newTextVector>";

    return xml;
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
  public final INDITextElement getElement(String name) {
    return (INDITextElement)super.getElement(name);
  }
}
