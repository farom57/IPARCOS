/*
 *  This file is part of INDI for Java.
 * 
 *  INDI for Java is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU General Public License 
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 * 
 *  INDI for Java is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 *  of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with INDI for Java.  If not, see 
 *  <http://www.gnu.org/licenses/>.
 */
package laazotea.indi;

/**
 * A class representing a generic INDI Exception
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.11, March 26, 2012
 */
public class INDIException extends Exception {
  public INDIException(String msg) {
    super(msg); 
  }
}
