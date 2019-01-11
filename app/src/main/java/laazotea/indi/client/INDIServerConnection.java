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
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import laazotea.indi.INDIDateFormat;
import laazotea.indi.INDIProtocolParser;
import laazotea.indi.INDIProtocolReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class representing a INDI Server Connection. Usually this is the entry
 * point for any INDI Client to connect to the server, retrieve all devices and
 * properties and so on.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.32, January 27, 2013
 */
public class INDIServerConnection implements INDIProtocolParser {

  /**
   * The name of the Connection.
   */
  private String name;
  /**
   * The host of the Connection.
   */
  private String host;
  /**
   * The port of the Connection.
   */
  private int port;
  /**
   * The sockect used in the Connection.
   */
  private Socket socket = null;
  /**
   * The output writer of the Connection.
   */
  private PrintWriter out = null;
  /**
   * A reader to read from the Connection.
   */
  INDIProtocolReader reader;
  /**
   * The set of the devices associated to this Connection.
   */
  private LinkedHashMap<String, INDIDevice> devices;
  /**
   * The list of Listeners of this Connection.
   */
  private ArrayList<INDIServerConnectionListener> listeners;

  /**
   * Constructs an instance of
   * <code>INDIServerConnection</code>. The Connection is NOT stablished.
   *
   * @param name The name of the Connection.
   * @param host The host of the Connection.
   * @param port The port of the Connection.
   */
  public INDIServerConnection(String name, String host, int port) {
    init(name, host, port);
  }

  /**
   * Constructs an instance of
   * <code>INDIServerConnection</code> with no name. The Connection is NOT
   * stablished.
   *
   * @param host The host of the Connection.
   * @param port The port of the Connection.
   */
  public INDIServerConnection(String host, int port) {
    init("", host, port);
  }

  /**
   * Constructs an instance of
   * <code>INDIServerConnection</code> with no name and using the default port
   * (7624). The Connection is NOT stablished.
   *
   * @param host The host of the Connection.
   */
  public INDIServerConnection(String host) {
    init("", host, 7624);
  }

  /**
   * Initilizes the Connection
   *
   * @param name The name of the Connection.
   * @param host The host of the Connection.
   * @param port The port of the Connection.
   */
  private void init(String name, String host, int port) {
    this.name = name;
    this.host = host;
    this.port = port;
    this.socket = null;
    this.out = null;
    this.reader = null;

    devices = new LinkedHashMap<String, INDIDevice>();

    listeners = new ArrayList<INDIServerConnectionListener>();
  }

  /**
   * This function waits until a Device with a
   * <code>name</code> exists in this Connection and returns it. The wait is
   * dinamic, so it should be called from a different Thread or the app will
   * freeze until the Device exists.
   *
   * @param name The name of the evice to wait for.
   * @return The Device once it exists in this Connection.
   */
  public INDIDevice waitForDevice(String name) {
    return waitForDevice(name, Integer.MAX_VALUE);
  }

  /**
   * This function waits until a Device with a
   * <code>name</code> exists in this Connection and returns it. The wait is
   * dinamic, so it should be called from a different Thread or the app will
   * freeze until the Device exists or the
   * <code>maxWait</code> number of seconds have elapsed.
   *
   * @param name The name of the evice to wait for.
   * @param maxWait Maximum number of seconds to wait for the Device
   * @return The Device once it exists in this Connection or
   * <code>null</code> if the maximum wait is achieved.
   */
  public INDIDevice waitForDevice(String name, int maxWait) {
    INDIDevice d = null;

    long startTime = (new Date()).getTime();
    boolean timeElapsed = false;

    while ((d == null) && (!timeElapsed)) {
      d = this.getDevice(name);

      if (d == null) {
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

    return d;
  }

  /**
   * Changes the name, host and port of the Connection if it is not connected.
   *
   * @param name The new name of the Connection.
   * @param host The new host of the Connection.
   * @param port The new port of the Connection.
   */
  public void setData(String name, String host, int port) {
    if (!isConnected()) {
      this.name = name;
      this.host = host;
      this.port = port;
    }
  }

  /**
   * Gets the host of the Connection.
   *
   * @return the host of the Connection.
   */
  public String getHost() {
    return host;
  }

  /**
   * Gets the name of the Connection.
   *
   * @return the name of the Connection.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the port of the Connection.
   *
   * @return the port of the Connection.
   */
  public int getPort() {
    return port;
  }

  /**
   * Connects to the INDI Server.
   *
   * @throws IOException if there is some problem connecting to the Server.
   */
  public void connect() throws IOException {
    if (socket == null) {
      socket = new Socket();

      try {
        socket.connect(new InetSocketAddress(host, port), 20000);
      } catch (IOException e) {
        socket = null;
        throw e;
      }

      out = new PrintWriter(socket.getOutputStream(), true);

      reader = new INDIProtocolReader(this);
      reader.start();
    }
  }

  /**
   * If connected, disconnects from the INDI Server and notifies the listeners.
   *
   */
  public void disconnect() {
    if (socket != null) {
      try {
        socket.shutdownInput();
        out.close();
//        socket.close();
      } catch (IOException e) {
      }

      socket = null;
      devices.clear();
      notifyListenersConnectionLost();
    }
  }

  /**
   * Determines if the Connection is stablished of not.
   *
   * @return
   * <code>true</code> if the Connection is stablished.
   * <code>false</code> otherwise.
   */
  public boolean isConnected() {
    if (socket == null) {
      return false;
    }

    return true;
  }

  /**
   * Sends the appropriate message to the INDI Server to be notified about the
   * Devices of the Server.
   *
   * @throws IOException if there is some problem with the Connection.
   */
  public void askForDevices() throws IOException {
    String message = "<getProperties version=\"1.7\" />";

    sendMessageToServer(message);
  }

  /**
   * Sends the appropriate message to the INDI Server to be notified about a
   * particular Device of the Server.
   *
   * @param device the Device name that is asked for.
   * @throws IOException if there is some problem with the Connection.
   */
  public void askForDevices(String device) throws IOException {
    String message = "<getProperties version=\"1.7\" device=\"" + device + "\"/>";

    sendMessageToServer(message);
  }

  /**
   * Sends the appropriate message to the INDI Server to be notified about a
   * particular Property of a particular Device of the Server.
   *
   * @param device the Device name of whose property is asked for.
   * @param propertyName the Property name that is asked for.
   * @throws IOException if there is some problem with the Connection.
   */
  public void askForDevices(String device, String propertyName) throws IOException {
    String message = "<getProperties version=\"1.7\" device=\"" + device + "\" name=\"" + propertyName + "\"/>";

    sendMessageToServer(message);
  }

  /**
   * Sends a XML message to the server.
   *
   * @param XML the message to be sent.
   * @throws IOException if there is some problem with the Connection.
   */
  protected void sendMessageToServer(String XML) throws IOException {
    out.print(XML);
    out.flush();
  }

  @Override
  public void finishReader() {
    disconnect();  // If there has been a problem with reading the port really disconnect and notify listeners
  }

  /**
   * Adds a new Device to this Connection and notifies the listeners.
   *
   * @param device the device to be added.
   */
  private void addDevice(INDIDevice device) {
    devices.put(device.getName(), device);

    notifyListenersNewDevice(device);
  }

  /**
   * Gets a particular Device by its name.
   *
   * @param name the name of the Device
   * @return the Device with the
   * <code>name</code> or
   * <code>null</code> if there is no Device with that name.
   */
  public INDIDevice getDevice(String name) {
    return devices.get(name);
  }

  /**
   * A convenience method to get the Property of a Device by specifiying their
   * names.
   *
   * @param deviceName the name of the Device.
   * @param propertyName the name of the Property.
   * @return the Property with
   * <code>propertyName</code> as name of the device with
   * <code>deviceName</code> as name.
   */
  public INDIProperty getProperty(String deviceName, String propertyName) {
    INDIDevice d = getDevice(deviceName);

    if (d == null) {
      return null;
    }

    return d.getProperty(propertyName);
  }

  /**
   * A convenience method to get the Element of a Property of a Device by
   * specifiying their names.
   *
   * @param deviceName the name of the Property.
   * @param propertyName the name of the Element.
   * @param elementName the name of the Element.
   * @return the Element with a
   * <code>elementName</code> as a name of a Property with
   * <code>propertyName</code> as name of the device with
   * <code>deviceName</code> as name.
   */
  public INDIElement getElement(String deviceName, String propertyName, String elementName) {
    INDIDevice d = getDevice(deviceName);

    if (d == null) {
      return null;
    }

    return d.getElement(propertyName, elementName);
  }

  /**
   * Parses the XML messages.
   *
   * @param doc the messages to be parsed.
   */
  @Override
  public void parseXML(Document doc) {
    Element el = doc.getDocumentElement();

    if (el.getNodeName().compareTo("INDI") != 0) {
      return;
    }

    NodeList nodes = el.getChildNodes();

    for (int i = 0; i < nodes.getLength(); i++) {
      Node n = nodes.item(i);

      if (n instanceof Element) {
        Element child = (Element) n;

        String name = child.getNodeName();

        if ((name.equals("defTextVector")) || (name.equals("defNumberVector")) || (name.equals("defSwitchVector")) || (name.equals("defLightVector")) || (name.equals("defBLOBVector"))) {
          addProperty(child);
        } else if ((name.equals("setTextVector")) || (name.equals("setNumberVector")) || (name.equals("setSwitchVector")) || (name.equals("setLightVector")) || (name.equals("setBLOBVector"))) {
          updateProperty(child);
        } else if (name.equals("message")) {
          messageReceived(child);
        } else if (name.equals("delProperty")) {
          deleteProperty(child);
        }
      }
    }
  }

  /**
   * Parses a XML &lt;delProperty&gt; element and notifies the listeners.
   *
   * @param xml The element to be parsed.
   */
  private void deleteProperty(Element xml) {
    if (xml.hasAttribute("device")) {
      String deviceName = xml.getAttribute("device").trim();

      INDIDevice d = getDevice(deviceName);

      if (d != null) {
        String propertyName = xml.getAttribute("name").trim();

        if (!(propertyName.length() == 0)) {
          d.deleteProperty(xml);
        } else {
          deleteDevice(d);
        }
      } else {
        deleteAllDevices();
      }
    }
  }

  /**
   * Deletes all the Devices from the Connection and notifies the listeners.
   */
  private void deleteAllDevices() {
    Iterator<INDIDevice> devs = devices.values().iterator();

    while (!devs.hasNext()) {
      deleteDevice(devs.next());
    }
  }

  /**
   * Deletes a Device from the Connection and notifies the listeners.
   *
   * @param device the Device to be removed.
   */
  private void deleteDevice(INDIDevice device) {
    devices.remove(device.getName());

    notifyListenersRemoveDevice(device);
  }

  /**
   * Parses a XML &lt;message&gt; element and notifies the listeners if
   * appropriate.
   *
   * @param xml The XML to be parsed.
   */
  private void messageReceived(Element xml) {
    if (xml.hasAttribute("device")) {
      String deviceName = xml.getAttribute("device").trim();

      INDIDevice d = getDevice(deviceName);

      if (d != null) {
        d.messageReceived(xml);
      }
    } else {  // Global message from server
      if (xml.hasAttribute("message")) {
        String time = xml.getAttribute("timestamp").trim();

        Date timestamp = INDIDateFormat.parseTimestamp(time);

        String message = xml.getAttribute("message").trim();

        notifyListenersNewMessage(timestamp, message);
      }
    }
  }

  /**
   * Parses a XML &lt;defXXXVector&gt; element.
   *
   * @param xml the element to be parsed.
   */
  private void addProperty(Element xml) {
    String deviceName = xml.getAttribute("device").trim();

    INDIDevice d = getDevice(deviceName);

    if (d == null) {
      d = new INDIDevice(deviceName, this);
      addDevice(d);
    }

    d.addProperty(xml);
  }

  /**
   * Parses a XML &lt;setXXXVector&gt; element.
   *
   * @param xml the element to be parsed.
   */
  private void updateProperty(Element el) {
    String deviceName = el.getAttribute("device").trim();

    INDIDevice d = getDevice(deviceName);

    if (d != null) {  // If device does no exist ignore
      d.updateProperty(el);
    }
  }

  /**
   * Adds a new listener to this Connection.
   *
   * @param listener the listener to be added.
   */
  public void addINDIServerConnectionListener(INDIServerConnectionListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a listener from this Connection.
   *
   * @param listener the listener to be removed.
   */
  public void removeINDIServerConnectionListener(INDIServerConnectionListener listener) {
    listeners.remove(listener);
  }

  /**
   * Adds a listener to all the Devices from this Connection
   *
   * @param listener the listener to add
   */
  public void addINDIDeviceListenerToAllDevices(INDIDeviceListener listener) {
    List<INDIDevice> l = getDevicesAsList();

    for (int i = 0; i < l.size(); i++) {
      INDIDevice d = l.get(i);

      d.addINDIDeviceListener(listener);
    }
  }

  /**
   * Removes a listener from all the Devices from this Connection
   *
   * @param listener the listener to remove
   */
  public void removeINDIDeviceListenerFromAllDevices(INDIDeviceListener listener) {
    List<INDIDevice> l = getDevicesAsList();

    for (int i = 0; i < l.size(); i++) {
      INDIDevice d = l.get(i);

      d.removeINDIDeviceListener(listener);
    }
  }

  /**
   * Gets the names of the Devices of this Connection.
   *
   * @return the names of the Devices of this Connection.
   */
  public String[] getDeviceNames() {
    List<INDIDevice> l = getDevicesAsList();

    String[] names = new String[l.size()];

    for (int i = 0; i < l.size(); i++) {
      names[i] = l.get(i).getName();
    }

    return names;
  }

  /**
   * Gets a
   * <code>List</code> with all the Devices of this Connection.
   *
   * @return the
   * <code>List</code> of Devices belonging to this Connection.
   */
  public List<INDIDevice> getDevicesAsList() {
    return new ArrayList<INDIDevice>(devices.values());
  }

  /**
   * A convenience method to add a listener to a Device (identified by its name)
   * of this Connection.
   *
   * @param deviceName the Device name to which add the listener
   * @param listener the listener to add
   */
  public void addINDIDeviceListener(String deviceName, INDIDeviceListener listener) {
    INDIDevice d = getDevice(deviceName);

    if (d == null) {
      return;
    }

    d.addINDIDeviceListener(listener);
  }

  /**
   * A convenience method to remove a listener from a Device (identified by its
   * name) of this Connection.
   *
   * @param deviceName the Device name to which remove the listener
   * @param listener the listener to remove
   */
  public void removeINDIDeviceListener(String deviceName, INDIDeviceListener listener) {
    INDIDevice d = getDevice(deviceName);

    if (d == null) {
      return;
    }

    d.removeINDIDeviceListener(listener);
  }

  /**
   * Notifies the listeners about a new Device.
   *
   * @param device the new Device.
   */
  private void notifyListenersNewDevice(INDIDevice device) {
    ArrayList<INDIServerConnectionListener> lCopy = (ArrayList<INDIServerConnectionListener>) listeners.clone();

    for (int i = 0; i < lCopy.size(); i++) {
      INDIServerConnectionListener l = lCopy.get(i);

      l.newDevice(this, device);
    }
  }

  /**
   * Notifies the listeners about a Device that is removed.
   *
   * @param device the removed device.
   */
  private void notifyListenersRemoveDevice(INDIDevice device) {
    ArrayList<INDIServerConnectionListener> lCopy = (ArrayList<INDIServerConnectionListener>) listeners.clone();

    for (int i = 0; i < lCopy.size(); i++) {
      INDIServerConnectionListener l = lCopy.get(i);

      l.removeDevice(this, device);
    }
  }

  /**
   * Notifies the listeners when the Connection is lost.
   */
  private void notifyListenersConnectionLost() {
    ArrayList<INDIServerConnectionListener> lCopy = (ArrayList<INDIServerConnectionListener>) listeners.clone();

    for (int i = 0; i < lCopy.size(); i++) {
      INDIServerConnectionListener l = lCopy.get(i);
      l.connectionLost(this);
    }
  }

  /**
   * Notifies the listeners about a new Server message.
   *
   * @param timestamp the timestamp of the message.
   * @param message the message.
   */
  protected void notifyListenersNewMessage(Date timestamp, String message) {
    ArrayList<INDIServerConnectionListener> lCopy = (ArrayList<INDIServerConnectionListener>) listeners.clone();

    for (int i = 0; i < lCopy.size(); i++) {
      INDIServerConnectionListener l = lCopy.get(i);

      l.newMessage(this, timestamp, message);
    }
  }

  /**
   * Gets the input stream of this Connection.
   *
   * @return The input stream of this Connection.
   */
  @Override
  public InputStream getInputStream() {
    try {
      return socket.getInputStream();
    } catch (IOException e) {
      return null;
    }
  }
}
