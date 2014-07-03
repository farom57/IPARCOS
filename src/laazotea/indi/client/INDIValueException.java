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

import laazotea.indi.INDIException;

/**
 * A class representing an exception on the value of a
 * <code>INDIElement</code>.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.11, March 26, 2012
 */
public class INDIValueException extends INDIException {

  /**
   * The element that produced the exception.
   */
  private INDIElement element;

  /**
   * Constructs an instance of
   * <code>INDIValueException</code> with the specified detail message.
   *
   * @param element The element that produced the error.
   * @param msg the detail message.
   */
  public INDIValueException(INDIElement element, String msg) {
    super(msg);
    this.element = element;
  }

  /**
   * Gets the
   * <code>INDIElement</code> that produced the exception.
   *
   * @return the
   * <code>INDIElement</code> that produced the exception
   */
  public INDIElement getINDIElement() {
    return element;
  }
}
