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

import java.util.Date;

/**
 * A interface to be notified about changes in a
 * <code>INDIServerConnection</code>
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.10, March 19, 2012
 */
public interface INDIServerConnectionListener {

  /**
   * Called when a new Device is added to the Connection.
   * @param connection The connection to which the device is added.
   * @param device The device that has been added.
   */
  public abstract void newDevice(INDIServerConnection connection, INDIDevice device);
  /**
   * Called when a device is removed from the Connection
   * @param connection The Connection from which the device is being removed.
   * @param device The device being removed.
   */
  public abstract void removeDevice(INDIServerConnection connection, INDIDevice device);
  /**
   * Called when the connection is lost (explicity or not).
   * @param connection The connection that has been lost. 
   */
  public abstract void connectionLost(INDIServerConnection connection);
  /**
   * Called when the message of the Connection is changed.
   * @param connection The Connection of whose message has changed.
   * @param timestamp The timestamp of the message.
   * @param message The message.
   */
  public abstract void newMessage(INDIServerConnection connection, Date timestamp, String message);
}
