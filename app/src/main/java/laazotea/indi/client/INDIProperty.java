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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import laazotea.indi.Constants;
import laazotea.indi.Constants.PropertyPermissions;
import laazotea.indi.Constants.PropertyStates;
import laazotea.indi.INDIException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

/**
 * A class representing a INDI Property. The subclasses
 * <code>INDIBLOBProperty</code>,
 * <code>INDILightProperty</code>,
 * <code>INDINumberProperty</code>,
 * <code>INDISwitchProperty</code> and
 * <code>INDITextProperty</code> define the basic Properties that a INDI Devices
 * may contain according to the INDI protocol. <p> It implements a listener
 * mechanism to notify changes in its Elements.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.32, January 27, 2013
 */
public abstract class INDIProperty {

  /**
   * The INDI Device to which this property belongs
   */
  private INDIDevice device;
  /**
   * This property name
   */
  private String name;
  /**
   * This property label
   */
  private String label;
  /**
   * The group to which this Property might be assigned
   */
  private String group;
  /**
   * The current state of this Property
   */
  private PropertyStates state;
  /**
   * The permission of this Property
   */
  private PropertyPermissions permission;
  /**
   * The timeout of this Property
   */
  private int timeout;
  /**
   * A list of elements for this Property
   */
  private LinkedHashMap<String, INDIElement> elements;
  /**
   * The list of listeners of this Property
   */
  private ArrayList<INDIPropertyListener> listeners;

  /**
   * Constructs an instance of
   * <code>INDIProperty</code>. Called by its sub-classes.
   * <code>INDIProperty</code>s are not usually directly instantiated. Usually
   * used by
   * <code>INDIDevice</code>.
   *
   * @param xml A XML Element
   * <code>&lt;defXXXVector&gt;</code> describing the Property.
   * @param device The
   * <code>INDIDevice</code> to which this Property belongs.
   * @throws IllegalArgumentException if the XML Property is not well formed
   * (does not contain a
   * <code>name</code> attribute or the permissions are not correct).
   */
  protected INDIProperty(Element xml, INDIDevice device) throws IllegalArgumentException {

    this.device = device;

    name = xml.getAttribute("name").trim();

    if (name.compareTo("") == 0) {  // If no name, ignore
      throw new IllegalArgumentException("No name for the Property");
    }

    label = xml.getAttribute("label").trim();

    if (label.compareTo("") == 0) {  // If no label copy from name
      this.label = name;
    }

    group = xml.getAttribute("group").trim();

    if (group.compareTo("") == 0) {  // If no group, create default group
      group = "Unsorted";
    }

    String sta = xml.getAttribute("state").trim();

    setState(sta);

    if ((this instanceof INDITextProperty) || (this instanceof INDINumberProperty) || (this instanceof INDISwitchProperty) || (this instanceof INDIBLOBProperty)) {

      String per = xml.getAttribute("perm").trim();

      permission = Constants.parsePropertyPermission(per);

      String to = xml.getAttribute("timeout").trim();

      if (!(to.length() == 0)) {
        setTimeout(to);
      } else {
        timeout = 0;
      }
    }

    if (this.getClass() == INDILightProperty.class) {
      timeout = 0;
      permission = PropertyPermissions.RO;
    }

    this.elements = new LinkedHashMap<String, INDIElement>();

    this.listeners = new ArrayList<INDIPropertyListener>();
  }

  /**
   * Updates the values of its elements according to some XML data. Subclasses
   * of
   * <code>INDIProperty</code> must implement this method to really do the
   * parsing and update (usually calling
   * <code>update(Element, String)</code>).
   *
   * @param xml A XML Element
   * <code>&lt;setXXXVector&gt;</code> to which the property must be updated.
   * @see #update(Element, String)
   */
  protected abstract void update(Element xml);

  /**
   * Updates the values of its elements according to some XML data. Subclasses
   * of
   * <code>INDIProperty</code> usually call this method from
   * <code>update(Element)</code> to really do the parsing and update.
   *
   * @param xml A XML Element
   * <code>&lt;setXXXVector&gt;</code> to which the property must be updated.
   * @param childNodesType The real XML type of
   * <code>xml</code>, that is, one of
   * <code>&lt;setBLOBVector&gt;</code>,
   * <code>&lt;setLightVector&gt;</code>,
   * <code>&lt;setNumberVector&gt;</code>,
   * <code>&lt;setSwitchVector&gt;</code> or
   * <code>&lt;setTextVector&gt;</code>.
   * @see #update(Element)
   */
  protected void update(Element xml, String childNodesType) {
    try {
      String sta = xml.getAttribute("state").trim();

      if (!(sta.length() == 0)) {
        setState(sta);
      }

      String to = xml.getAttribute("timeout").trim();

      if (!(to.length() == 0)) {
        setTimeout(to);
      }

      NodeList list = xml.getElementsByTagName(childNodesType);

      for (int i = 0; i < list.getLength(); i++) {
        Element child = (Element) list.item(i);

        String ename = child.getAttribute("name");

        INDIElement iel = getElement(ename);

//          System.out.println(child + " - " + name + " - " + iel);
        if (iel != null) { // It already exists else ignore
          iel.setValue(child);
        }
      }
    } catch (IllegalArgumentException e) {  // If there was some poblem parsing then set to alert
      state = PropertyStates.ALERT;
    }

    notifyListeners();
  }

  /**
   * Sets the state of this property.
   *
   * @param newState The new state for this Property in form of a String:
   * <code>Idle</code>,
   * <code>Ok</code>,
   * <code>Busy</code> or
   * <code>Alert</code>.
   * @throws IllegalArgumentException if the
   * <code>newState</code> is not a valid one.
   */
  private void setState(String newState) throws IllegalArgumentException {
    state = Constants.parsePropertyState(newState);
  }

  /**
   * Sets the current timeout for this Property
   *
   * @param newTimeout The new current timeout.
   * @throws IllegalArgumentException if the format of the timeout is not
   * correct (a positive integer).
   */
  private void setTimeout(String newTimeout) throws IllegalArgumentException {
    try {
      timeout = Integer.parseInt(newTimeout);

      if (timeout < 0) {
        throw new IllegalArgumentException("Illegal timeout for the Property");
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Illegal timeout for the Property");
    }
  }

  /**
   * Gets the Device that owns this Property.
   *
   * @return the Device that owns this Property
   */
  public INDIDevice getDevice() {
    return device;
  }

  /**
   * Gets the Group to which this property might be assigned.
   *
   * @return the group to which this property might be assigned.
   */
  public String getGroup() {
    return group;
  }

  /**
   * Gets the timeout for this Property.
   *
   * @return the timeout for this Property.
   */
  public int getTimeout() {
    return timeout;
  }

  /**
   * Gets the label for this Property.
   *
   * @return the label for this Property.
   */
  public String getLabel() {
    return label;
  }

  /**
   * Gets the name of this Property
   *
   * @return the name of this Property
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the number of Elements in this Property
   *
   * @return the number of Elements in this Property.
   */
  public int getElementCount() {
    return elements.size();
  }

  /**
   * Gets the Permission of this Property.
   *
   * @return the Permission of this Property.
   */
  public PropertyPermissions getPermission() {
    return permission;
  }

  /**
   * Gets the State of this Property.
   *
   * @return the State of this Property.
   */
  public PropertyStates getState() {
    return state;
  }

  /**
   * Sets the timeout of this property.
   *
   * @param timeout the new timeout for this Property.
   */
  protected void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  /**
   * Sets the State of this Property. The listeners are notified of this change.
   *
   * @param state the new State of this Property.
   */
  protected void setState(PropertyStates state) {
    this.state = state;

    notifyListeners();
  }

  /**
   * Sets the Permission of this Property.
   *
   * @param permission the new Permission for this Property.
   */
  protected void setPermission(PropertyPermissions permission) {
    this.permission = permission;
  }

  /**
   * Adds a new Element to this Property.
   *
   * @param element the Element to be added.
   */
  protected void addElement(INDIElement element) {
    elements.put(element.getName(), element);
  }

  /**
   * Gets a particular Element of this Property by its name.
   *
   * @param name The name of the Element to be returned
   * @return The Element of this Property with the given
   * <code>name</code>.
   * <code>null</code> if there is no Element with that
   * <code>name</code>.
   */
  public INDIElement getElement(String name) {
    return elements.get(name);
  }

  /**
   * Gets a
   * <code>ArrayList</code> with all the Elements of this Property.
   *
   * @return the
   * <code>ArrayList</code> of Elements belonging to this Property.
   */
  public ArrayList<INDIElement> getElementsAsList() {
    return new ArrayList<INDIElement>(elements.values());
  }

  /**
   * Tests and changes the desired values of the the Elements of this Property.
   * If there are new desired values for any Elements the XML code to produce
   * the change is sent to the INDI Driver. If communication is successful the
   * state of the property is set to "Busy".
   *
   * @throws INDIValueException if some of the desired values are not correct or
   * if the Property is Read Only.
   * @throws IOException if there is some communication problem with the INDI
   * driver connection.
   */
  public void sendChangesToDriver() throws INDIValueException, IOException {
    if (permission == PropertyPermissions.RO) {
      throw new INDIValueException(null, "The property is read only");
    }

    ArrayList<INDIElement> elems = getElementsAsList();

    int changedElements = 0;
    String xml = "";
    for (int i = 0; i < elems.size(); i++) {
      INDIElement el = elems.get(i);

      if (el.isChanged()) {
        changedElements++;

        xml += el.getXMLOneElementNewValue();
      }
    }

    if (changedElements > 0) {
      setState(PropertyStates.BUSY);

      xml = getXMLPropertyChangeInit() + xml + getXMLPropertyChangeEnd();

      Log.w("INDI",xml);
      device.sendMessageToServer(xml);
    }
  }

  /**
   * Adds a new listener to this Property.
   *
   * @param listener the listener to be added.
   */
  public void addINDIPropertyListener(INDIPropertyListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a listener from this Property.
   *
   * @param listener the listener to be removed.
   */
  public void removeINDIPropertyListener(INDIPropertyListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notifies all the listeners about the changes in the Property.
   */
  private void notifyListeners() {
    ArrayList<INDIPropertyListener> lCopy = (ArrayList<INDIPropertyListener>)listeners.clone();

    for (int i = 0; i < lCopy.size(); i++) {
      INDIPropertyListener l = lCopy.get(i);

      l.propertyChanged(this);
    }
  }

  /**
   * Gets the opening XML Element &lt;newXXXVector&gt; for this Property.
   *
   * @return the opening XML Element &lt;newXXXVector&gt; for this Property.
   */
  protected abstract String getXMLPropertyChangeInit();

  /**
   * Gets the closing XML Element &lt;/newXXXVector&gt; for this Property.
   *
   * @return the closing XML Element &lt;/newXXXVector&gt; for this Property.
   */
  protected abstract String getXMLPropertyChangeEnd();

  /**
   * Gets a default UI component to handle the repesentation and control of this
   * Property. The panel is registered as a listener of this Property. Please
   * note that the UI class must implement INDIPropertyListener. The component
   * will be chosen depending on the loaded UI libraries (I4JClientUI,
   * I4JAndroid, etc). Note that a casting of the returned value must be done.
   *
   * If a previous default component has been requested, the previous one will
   * be deregistered. So, only one default component will listen for the
   * property.
   *
   * @return A UI component that handles this Property.
   */
  public abstract INDIPropertyListener getDefaultUIComponent() throws INDIException;

  /**
   * Gets the names of the Elements of this Property.
   *
   * @return the names of the Elements of this Property.
   */
  public String[] getElementNames() {
    List<INDIElement> l = getElementsAsList();

    String[] names = new String[l.size()];

    for (int i = 0; i < l.size(); i++) {
      names[i] = l.get(i).getName();
    }

    return names;
  }

  /**
   * Returns a String with the name of the Property, its state and its elements
   * and values.
   *
   * @return a String representation of the property and its values.
   */
  public String getNameStateAndValuesAsString() {
    String aux = getName() + " - " + getState() + "\n";
    List<INDIElement> l = getElementsAsList();

    for (int i = 0; i < l.size(); i++) {
      aux += "  " + l.get(i).getNameAndValueAsString() + "\n";
    }

    return aux;
  }

  /**
   * Gets the values of the Property as a String. 
   *
   * @return A String representation of the value of the Property.
   */
  public String getValuesAsString() {
    String aux = "[";

    ArrayList<INDIElement> l = this.getElementsAsList();

    for (int i = 0; i < l.size(); i++) {
      if (i == 0) {
        aux += l.get(i).toString();
      } else {
        aux += ", " + l.get(i).toString();
      }
    }

    return aux + "]";
  }
}
