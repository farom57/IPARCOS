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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A class that reads from a input stream and sends the read messages to a parser.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.2, April 1, 2012
 */
public class INDIProtocolReader extends Thread {
  /**
   * The parser to which the messages will be sent.
   */
  private INDIProtocolParser parser;
  /**
   * Used to friendly stop the reader.
   */
  private boolean stop;
  
  /**
   * Creates the reader.
   * @param parser The parser to which the readed messages will be sent.
   */
  public INDIProtocolReader(INDIProtocolParser parser) {
    this.parser = parser;
  }
  
  /**
   * The main body of the reader.
   */
  @Override
  public void run() {
    DocumentBuilderFactory docBuilderFactory;

    DocumentBuilder docBuilder;
    try {
      docBuilderFactory = DocumentBuilderFactory.newInstance();
      docBuilder = docBuilderFactory.newDocumentBuilder();
      docBuilder.setErrorHandler(new ErrorHandler() {

        @Override
        public void warning(SAXParseException e) throws SAXException {
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    int BUFFER_SIZE = 1000000;

    StringBuffer bufferedInput = new StringBuffer();

    char[] buffer = new char[BUFFER_SIZE];

    stop = false;

    BufferedReader in = new BufferedReader(new InputStreamReader(parser.getInputStream()));

    try {
      while (!stop) {
        int nReaded = in.read(buffer, 0, BUFFER_SIZE);

        if (nReaded != -1) {
          bufferedInput.append(buffer, 0, nReaded);  // Appending to the buffer

          boolean errorParsing = false;

          try {
            String d = "<INDI>" + bufferedInput + "</INDI>";
            //System.err.println(d);
            d = d.replaceAll("\\<\\?xml version='...'\\?\\>", "");
            d = d.replaceAll("\\<\\?xml version=\"...\"\\?\\>", "");
            //    System.err.println(d);

            Document doc = docBuilder.parse(new InputSource(new StringReader(d)));

            parser.parseXML(doc);
          } catch (SAXException e) {
            errorParsing = true;
          }

          if (!errorParsing) {
            bufferedInput.setLength(0);  // Empty the buffer because it has been already parsed
          }
        } else {  // If -1 readed, end
          stop = true;
        }
      }
    } catch (IOException e) {
      //   e.printStackTrace();
    }

    parser.finishReader();
  }
  
  /**
   * Sets the stop parameter. If set to <code>true</code> the reader will gracefully stop after the next read.
   * @param stop
   */
  public void setStop(boolean stop) {
    this.stop = stop; 
  }
}
