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

import java.io.InputStream;
import org.w3c.dom.Document;

/**
 * A interface representing a generic INDI Protocol Parser
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.2, April 1, 2012
 */
public interface INDIProtocolParser {

  /**
   * Parses a XML Document.
   *
   * @param doc The XML document to parse. It presumably consists on INDI
   * Protocol messages.
   */
  public void parseXML(Document doc);

  /**
   * Gets the input stream from where the messages will be read.
   *
   * @return The input stream from where the messages will be read.
   */
  public InputStream getInputStream();

  /**
   * Called when the reader finishes the readings (communications broken /
   * stopped).
   */
  public void finishReader();
}
