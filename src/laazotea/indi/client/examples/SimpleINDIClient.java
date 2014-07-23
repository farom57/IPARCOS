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
package laazotea.indi.client.examples;

import java.io.IOException;
import java.util.Date;
import laazotea.indi.Constants;
import laazotea.indi.client.*;

/**
 * A simple INDI Client that listens to a particular INDI Server and prints any
 * message received (listens to all possible events).
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.31, April 11, 2012
 */
public class SimpleINDIClient implements INDIServerConnectionListener, INDIDeviceListener, INDIPropertyListener {

  private INDIServerConnection connection;

  public SimpleINDIClient(String host, int port) {
    connection = new INDIServerConnection(host, port);

    connection.addINDIServerConnectionListener(this);  // We listen to all server events

    try {
      connection.connect();
      connection.askForDevices();  // Ask for all the devices.
    } catch (IOException e) {
      System.out.println("Problem with the connection: " + host + ":" + port);
      e.printStackTrace();
    }
  }

  @Override
  public void newDevice(INDIServerConnection connection, INDIDevice device) {
    // We just simply listen to this Device
    System.out.println("New device: " + device.getName());
    try {
      device.BLOBsEnable(Constants.BLOBEnables.ALSO); // Enable receiving BLOBs from this Device
    } catch (IOException e) {
    }
    device.addINDIDeviceListener(this);
  }

  @Override
  public void removeDevice(INDIServerConnection connection, INDIDevice device) {
    // We just remove ourselves as a listener of the removed device
    System.out.println("Device Removed: " + device.getName());
    device.removeINDIDeviceListener(this);
  }

  @Override
  public void connectionLost(INDIServerConnection connection) {
    System.out.println("Connection lost. Bye");

    System.exit(-1);
  }

  @Override
  public void newMessage(INDIServerConnection connection, Date timestamp, String message) {
    System.out.println("New Server Message: " + timestamp + " - " + message);
  }

  @Override
  public void newProperty(INDIDevice device, INDIProperty property) {
    // We just simply listen to this Property
    System.out.println("New Property (" + property.getName() + ") added to device " + device.getName());
    property.addINDIPropertyListener(this);
  }

  @Override
  public void removeProperty(INDIDevice device, INDIProperty property) {
    // We just remove ourselves as a listener of the removed property
    System.out.println("Property (" + property.getName() + ") removed from device " + device.getName());
    property.removeINDIPropertyListener(this);
  }

  @Override
  public void messageChanged(INDIDevice device) {
    System.out.println("New Device Message: " + device.getName() + " - " + device.getTimestamp() + " - " + device.getLastMessage());
  }

  @Override
  public void propertyChanged(INDIProperty property) {
    System.out.println("Property Changed: " + property.getNameStateAndValuesAsString());
  }

  /**
   * Parses the arguments and creates the Client if they are correct.
   *
   * @param args
   */
  public static void main(String[] args) {
    if ((args.length < 1) || (args.length > 2)) {
      printErrorMessageAndExit();
    }

    String host = args[0];
    int port = 7624;

    if (args.length > 1) {
      try {
        port = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
        printErrorMessageAndExit();
      }
    }

    SimpleINDIClient sic = new SimpleINDIClient(host, port);
  }

  private static void printErrorMessageAndExit() {
    System.out.println("The program must be called in the following way:");

    System.out.println("> java SimpleINDIClient host [port]\n  where");
    System.out.println("    host - is the INDI Server to connect to");
    System.out.println("    port - is the INDI Server port. If not present the default port (7624) will be used.\n");

    System.exit(-1);
  }
}
