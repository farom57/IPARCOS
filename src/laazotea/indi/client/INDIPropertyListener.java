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

/**
 * A interface to be notified about changes in a <code>INDIProperty</code>
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.10, March 19, 2012
 */
public interface INDIPropertyListener {
    /**
   * The method that will be called when a <code>INDIProperty</code> changes.
   * @param property the property that has changed.
   */
  public abstract void propertyChanged(INDIProperty property);
}
