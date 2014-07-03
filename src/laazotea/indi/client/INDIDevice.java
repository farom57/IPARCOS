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
import java.util.*;
import laazotea.indi.ClassInstantiator;
import laazotea.indi.Constants;
import laazotea.indi.Constants.BLOBEnables;
import laazotea.indi.INDIDateFormat;
import laazotea.indi.INDIException;
import org.w3c.dom.Element;

/**
 * A class representing a INDI Device.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.32, January 27, 2013
 */
public class INDIDevice {

  /**
   * The name of the Device.
   */
  private String name;
  /**
   * The Server Connection to which this Device Belongs
   */
  private INDIServerConnection server;
  /**
   * The collection of properties for this Device
   */
  private LinkedHashMap<String, INDIProperty> properties;
  /**
   * The list of Listeners of this Device.
   */
  private ArrayList<INDIDeviceListener> listeners;
  /**
   * A UI component that can be used in graphical interfaces for this Device.
   */
  private INDIDeviceListener UIComponent;
  /**
   * The timestamp for the last message.
   */
  private Date timestamp;
  /**
   * The last message of this Device.
   */
  private String message;
  /**
   * The number of
   * <code>BLOBProperties</code> in this Device.
   */
  private int blobCount;

  /**
   * Constructs an instance of
   * <code>INDIDevice</code>. Usually called from a
   * <code>INDIServerConnection</code>.
   *
   * @param name the name of this Device
   * @param server the Server Connection of this Device
   * @see INDIServerConnection
   */
  protected INDIDevice(String name, INDIServerConnection server) {
    this.name = name;
    this.server = server;

    this.properties = new LinkedHashMap<String, INDIProperty>();

    this.listeners = new ArrayList<INDIDeviceListener>();

    timestamp = new Date();
    message = "";
    blobCount = 0;
  }

  /**
   * Adds a new listener to this Device.
   *
   * @param listener the listener to be added.
   */
  public void addINDIDeviceListener(INDIDeviceListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a listener from this Device.
   *
   * @param listener the listener to be removed.
   */
  public void removeINDIDeviceListener(INDIDeviceListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notifies the listeners of a new
   * <code>INDIProperty</code>
   *
   * @param property The new property
   */
  private void notifyListenersNewProperty(INDIProperty property) {
    ArrayList<INDIDeviceListener> lCopy = (ArrayList<INDIDeviceListener>) listeners.clone();

    for (int i = 0; i < lCopy.size(); i++) {
      INDIDeviceListener l = lCopy.get(i);

      l.newProperty(this, property);
    }
  }

  /**
   * Notifies the listeners of a removed
   * <code>INDIProperty</code>
   *
   * @param property The removed property
   */
  private void notifyListenersDeleteProperty(INDIProperty property) {
    ArrayList<INDIDeviceListener> lCopy = (ArrayList<INDIDeviceListener>) listeners.clone();

    for (int i = 0; i < lCopy.size(); i++) {
      INDIDeviceListener l = lCopy.get(i);

      l.removeProperty(this, property);
    }
  }

  /**
   * Notifies the listeners that the message of this Device has changed.
   */
  private void notifyListenersMessageChanged() {
    ArrayList<INDIDeviceListener> lCopy = (ArrayList<INDIDeviceListener>) listeners.clone();

    for (int i = 0; i < lCopy.size(); i++) {
      INDIDeviceListener l = lCopy.get(i);

      l.messageChanged(this);
    }
  }

  /**
   * Sends the appropriate message to the Server to establish a particular BLOB
   * policy (BLOBEnable) for the Device.
   *
   * @param enable The BLOB policy.
   * @throws IOException if there is some problem sending the message.
   */
  public void BLOBsEnable(BLOBEnables enable) throws IOException {
    String xml = "<enableBLOB device=\"" + getName() + "\">" + Constants.getBLOBEnableAsString(enable) + "</enableBLOB>";

    sendMessageToServer(xml);
  }

  /**
   * Sends the appropriate message to the Server to establish a particular BLOB
   * policy (BLOBEnable) for the Device and a particular Property.
   *
   * @param enable The BLOB policy.
   * @param property The Property of the Device to listen to.
   * @throws IOException if there is some problem sending the message.
   */
  public void BLOBsEnable(BLOBEnables enable, INDIProperty property) throws IOException {
    if ((properties.containsValue(property)) && (property instanceof INDIBLOBProperty)) {
      String xml = "<enableBLOB device=\"" + getName() + "\" name=\"" + property.getName() + "\">" + Constants.getBLOBEnableAsString(enable) + "</enableBLOB>";

      sendMessageToServer(xml);
    }
  }

  /**
   * Sends the appropriate message to the Server to disallow the receipt of BLOB
   * property changes.
   *
   * @throws IOException if there is some problem sending the message.
   * @deprecated Replaced by {@link #BLOBsEnable(laazotea.indi.Constants.BLOBEnables)}
   */
  @Deprecated
  public void BLOBsEnableNever() throws IOException {
    String xml = "<enableBLOB device=\"" + getName() + "\">Never</enableBLOB>";

    sendMessageToServer(xml);
  }

  /**
   * Sends the appropriate message to the Server to allow the receipt of BLOB
   * property changes along with any other property types.
   *
   * @throws IOException if there is some problem sending the message.
   * @deprecated Replaced by {@link #BLOBsEnable(laazotea.indi.Constants.BLOBEnables)}
   */
  @Deprecated
  public void BLOBsEnableAlso() throws IOException {
    String xml = "<enableBLOB device=\"" + getName() + "\">Also</enableBLOB>";

    sendMessageToServer(xml);
  }

  /**
   * Sends the appropriate message to the Server to allow the receipt of just
   * BLOB property changes.
   *
   * @throws IOException if there is some problem sending the message.
   * @deprecated Replaced by {@link #BLOBsEnable(laazotea.indi.Constants.BLOBEnables)}
   */
  @Deprecated
  public void BLOBsEnableOnly() throws IOException {
    String xml = "<enableBLOB device=\"" + getName() + "\">Only</enableBLOB>";

    sendMessageToServer(xml);
  }

  /**
   * Sends the appropriate message to the Server to disallow the receipt of a
   * particular BLOB property changes.
   *
   * @param property the BLOB property
   * @throws IOException if there is some problem sending the message.
   * @deprecated Replaced by {@link #BLOBsEnable(laazotea.indi.Constants.BLOBEnables, INDIProperty)}
   */
  @Deprecated
  public void BLOBsEnableNever(INDIBLOBProperty property) throws IOException {
    String xml = "<enableBLOB device=\"" + getName() + "\" name=\"" + property.getName() + "\">Never</enableBLOB>";

    sendMessageToServer(xml);
  }

  /**
   * Sends the appropriate message to the Server to allow the receipt of a
   * particular BLOB property changes along with any other property types.
   *
   * @param property the BLOB property
   * @throws IOException if there is some problem sending the message.
   * @deprecated Replaced by {@link #BLOBsEnable(laazotea.indi.Constants.BLOBEnables, INDIProperty)}
   */
  @Deprecated
  public void BLOBsEnableAlso(INDIBLOBProperty property) throws IOException {
    String xml = "<enableBLOB device=\"" + getName() + "\" name=\"" + property.getName() + "\">Also</enableBLOB>";

    sendMessageToServer(xml);
  }

  /**
   * Sends the appropriate message to the Server to allow the receipt of just a
   * particular BLOB property changes.
   *
   * @param property the BLOB property
   * @throws IOException if there is some problem sending the message.
   * @deprecated Replaced by {@link #BLOBsEnable(laazotea.indi.Constants.BLOBEnables, INDIProperty)}
   */
  @Deprecated
  public void BLOBsEnableOnly(INDIBLOBProperty property) throws IOException {
    String xml = "<enableBLOB device=\"" + getName() + "\" name=\"" + property.getName() + "\">Only</enableBLOB>";

    sendMessageToServer(xml);
  }

  /**
   * Gets the last message received from the Device.
   *
   * @return the last message received.
   */
  public String getLastMessage() {
    return message;
  }

  /**
   * Gets the timestamp of the last received message.
   *
   * @return the timestamp of the last received message.
   */
  public Date getTimestamp() {
    return timestamp;
  }

  /**
   * Gets the name of the Device.
   *
   * @return the name of the Device.
   */
  public String getName() {
    return name;
  }

  /**
   * Processes a XML message received for this Device and stores and notifies
   * the listeners if there is some message attribute in them.
   *
   * @param xml the XML message to be processed.
   */
  protected void messageReceived(Element xml) {
    if (xml.hasAttribute("message")) {
      String time = xml.getAttribute("timestamp").trim();

      timestamp = INDIDateFormat.parseTimestamp(time);

      message = xml.getAttribute("message").trim();

      notifyListenersMessageChanged();
    }
  }

  /**
   * Processes a XML &lt;delProperty&gt;. It removes the appropriate Property
   * from the list of Properties.
   *
   * @param xml the XML message to be processed.
   */
  protected void deleteProperty(Element xml) {
    String propertyName = xml.getAttribute("name").trim();

    if (!(propertyName.length() == 0)) {
      messageReceived(xml);

      INDIProperty p = getProperty(propertyName);

      if (p != null) {
        removeProperty(p);
      }
    }
  }

  /**
   * This function waits until a Property with a
   * <code>name</code> exists in this device and returns it. The wait is
   * dinamic, so it should be called from a different Thread or the app will
   * freeze until the property exists.
   *
   * @param name The name of the Property to wait for.
   * @return The Property once it exists in this device.
   */
  public INDIProperty waitForProperty(String name) {
    return waitForProperty(name, Integer.MAX_VALUE);
  }

  /**
   * This function waits until a Property with a
   * <code>name</code> exists in this device and returns it. The wait is
   * dinamic, so it should be called from a different Thread or the app will
   * freeze until the property exists or the
   * <code>maxWait</code> number of seconds have elapsed.
   *
   * @param name The name of the Property to wait for.
   * @param maxWait Maximum number of seconds to wait for the Property
   * @return The Property once it exists in this Device or
   * <code>null</code> if the maximum wait is achieved.
   */
  public INDIProperty waitForProperty(String name, int maxWait) {
    INDIProperty p = null;

    long startTime = (new Date()).getTime();
    boolean timeElapsed = false;

    while ((p == null) && (!timeElapsed)) {
      p = this.getProperty(name);

      if (p == null) {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
        }
      }

      long endTime = (new Date()).getTime();

      if (((endTime - startTime) / 1000) > maxWait) {
        timeElapsed = true;
      }
    }

    return p;
  }

  /**
   * Processes a XML &lt;setXXXVector&gt;. It updates the appropiate Property.
   *
   * @param xml the XML message to be processed.
   */
  protected void updateProperty(Element xml) {
    String propertyName = xml.getAttribute("name").trim();

    if (!(propertyName.length() == 0)) {
      // check message 
      messageReceived(xml);

      INDIProperty p = getProperty(propertyName);

      if (p != null) {  // If it does not exist else ignore
        if ((p.getClass() == INDITextProperty.class) && (xml.getTagName().compareTo("setTextVector") == 0)) {  // If types coincide
          p.update(xml);
        }

        if ((p.getClass() == INDINumberProperty.class) && (xml.getTagName().compareTo("setNumberVector") == 0)) {  // If types coincide
          p.update(xml);
        }

        if ((p.getClass() == INDISwitchProperty.class) && (xml.getTagName().compareTo("setSwitchVector") == 0)) {  // If types coincide
          p.update(xml);
        }

        if ((p.getClass() == INDILightProperty.class) && (xml.getTagName().compareTo("setLightVector") == 0)) {  // If types coincide
          p.update(xml);
        }

        if ((p.getClass() == INDIBLOBProperty.class) && (xml.getTagName().compareTo("setBLOBVector") == 0)) {  // If types coincide
          p.update(xml);
        }
      }
    }
  }

  /**
   * Processes a XML &lt;newXXXVector&gt;. It creates and adds the appropiate
   * Property.
   *
   * @param xml The XML message to be processed.
   */
  protected void addProperty(Element xml) {
    String propertyName = xml.getAttribute("name").trim();

    if (!(propertyName.length() == 0)) {
      messageReceived(xml);

      INDIProperty p = getProperty(propertyName);

      if (p == null) {  // If it does not exist
        try {
          if (xml.getTagName().compareTo("defSwitchVector") == 0) {
            INDISwitchProperty sp = new INDISwitchProperty(xml, this);

            addProperty(sp);

            notifyListenersNewProperty(sp);
          } else if (xml.getTagName().compareTo("defTextVector") == 0) {
            INDITextProperty tp = new INDITextProperty(xml, this);

            addProperty(tp);

            notifyListenersNewProperty(tp);
          } else if (xml.getTagName().compareTo("defNumberVector") == 0) {
            INDINumberProperty np = new INDINumberProperty(xml, this);

            addProperty(np);

            notifyListenersNewProperty(np);
          } else if (xml.getTagName().compareTo("defLightVector") == 0) {
            INDILightProperty lp = new INDILightProperty(xml, this);

            addProperty(lp);

            notifyListenersNewProperty(lp);
          } else if (xml.getTagName().compareTo("defBLOBVector") == 0) {
            INDIBLOBProperty bp = new INDIBLOBProperty(xml, this);

            addProperty(bp);

            notifyListenersNewProperty(bp);
          }
        } catch (IllegalArgumentException e) {  // Some problem with the parameters
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Gets the number of BLOB properties in this Device.
   *
   * @return the number of BLOB properties in this Device.
   */
  public int getBLOBCount() {
    return blobCount;
  }

  /**
   * Adds a Property to the properties list and updates the BLOB count if
   * necessary.
   *
   * @param property
   */
  private void addProperty(INDIProperty property) {
    properties.put(property.getName(), property);

    if (property instanceof INDIBLOBProperty) {
      blobCount++;
    }
  }

  /**
   * Removes a Property from the properties list and updates the BLOB count if
   * necessary.
   *
   * @param property
   */
  private void removeProperty(INDIProperty property) {
    properties.remove(property.getName());

    if (property instanceof INDIBLOBProperty) {
      blobCount--;
    }

    notifyListenersDeleteProperty(property);
  }

  /**
   * Gets the Server Connection of this Device.
   *
   * @return the Server Connection of this Device.
   */
  public INDIServerConnection getServer() {
    return server;
  }

  /**
   * Gets a Property by its name.
   *
   * @param name the name of the Property to be retrieved.
   * @return the Property with the
   * <code>name</code> or
   * <code>null</code> if there is no Property with that name.
   */
  public INDIProperty getProperty(String name) {
    return properties.get(name);
  }

  /**
   * Gets a list of group names for all the properties.
   *
   * @return the list of group names for all the properties of the device.
   */
  public ArrayList<String> getGroupNames() {
    ArrayList<String> groupNames = new ArrayList<String>();

    Collection c = properties.values();
    Iterator itr = c.iterator();

    while (itr.hasNext()) {
      INDIProperty p = (INDIProperty) itr.next();

      String groupName = p.getGroup();

      if (!groupNames.contains(groupName)) {
        groupNames.add(groupName);
      }
    }

    return groupNames;
  }

  /**
   * Gets a list of all the properties of the device.
   *
   * @return the list of Properties belonging to the device
   */
  public ArrayList<INDIProperty> getAllProperties() {
    ArrayList<INDIProperty> props = new ArrayList<INDIProperty>();

    Collection c = properties.values();
    Iterator itr = c.iterator();

    while (itr.hasNext()) {
      INDIProperty p = (INDIProperty) itr.next();

      props.add(p);
    }

    return props;
  }

  /**
   * Gets a list of properties belonging to a group.
   *
   * @param groupName the name of the group
   * @return the list of Properties belonging to the group
   */
  public ArrayList<INDIProperty> getPropertiesOfGroup(String groupName) {
    ArrayList<INDIProperty> props = new ArrayList<INDIProperty>();

    Collection c = properties.values();
    Iterator itr = c.iterator();

    while (itr.hasNext()) {
      INDIProperty p = (INDIProperty) itr.next();
      if (p.getGroup().compareTo(groupName) == 0) {
        props.add(p);
      }
    }

    return props;
  }

  /**
   * A convenience method to get the Element of a Property by specifiying their
   * names.
   *
   * @param propertyName the name of the Property.
   * @param elementName the name of the Element.
   * @return the Element with
   * <code>elementName</code> as name of the property with
   * <code>propertyName</code> as name.
   */
  public INDIElement getElement(String propertyName, String elementName) {
    INDIProperty p = getProperty(propertyName);

    if (p == null) {
      return null;
    }

    return p.getElement(elementName);
  }

  /**
   * Sends a XML message to the Server.
   *
   * @param XMLMessage the message to be sent.
   * @throws IOException if there is some problem with the connection to the
   * server.
   */
  protected void sendMessageToServer(String XMLMessage) throws IOException {
    server.sendMessageToServer(XMLMessage);
  }

  /**
   * Gets a default UI component to handle the repesentation and control of this
   * Device. The panel is registered as a listener of this Device. Please note
   * that the UI class must implement INDIDeviceListener. The component will be
   * chosen depending on the loaded UI libraries (I4JClientUI, I4JAndroid, etc).
   * Note that a casting of the returned value must be done.
   *
   * If a previous component has been asked, it will be dregistered as a
   * listener. So, only one default component will listen to the device.
   *
   * @return A UI component that handles this Device.
   * @throws INDIException if no UIComponent is found in the classpath.
   */
  public INDIDeviceListener getDefaultUIComponent() throws INDIException {
    if (UIComponent != null) {
      removeINDIDeviceListener(UIComponent);
    }

    Object[] arguments = new Object[]{this};
    String[] possibleUIClassNames = new String[]{"laazotea.indi.client.ui.INDIDevicePanel", "laazotea.indi.androidui.INDIDeviceView"};
    try {
      UIComponent = (INDIDeviceListener) ClassInstantiator.instantiate(possibleUIClassNames, arguments);
    } catch (ClassCastException e) {
      throw new INDIException("The UI component is not a valid INDIDeviceListener. Probably a incorrect library in the classpath.");
    }

    addINDIDeviceListener(UIComponent);

    return UIComponent;
  }

  /**
   * Gets a
   * <code>List</code> with all the Properties of this Device.
   *
   * @return the
   * <code>List</code> of Properties belonging to this Device.
   */
  public List<INDIProperty> getPropertiesAsList() {
    return new ArrayList<INDIProperty>(properties.values());
  }

  /**
   * Gets the names of the Properties of this Device.
   *
   * @return the names of the Properties of this Device.
   */
  public String[] getPropertyNames() {
    List<INDIProperty> l = getPropertiesAsList();

    String[] names = new String[l.size()];

    for (int i = 0; i < l.size(); i++) {
      names[i] = l.get(i).getName();
    }

    return names;
  }
}
