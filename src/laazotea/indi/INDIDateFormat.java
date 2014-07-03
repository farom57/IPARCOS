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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple class to format and parse INDI timestamps.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.10, March 19, 2012
 */
public class INDIDateFormat {

  /**
   * The first possible format for INDI timestamps.
   */
  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
  /**
   * The second possible format for INDI timestamps.
   */
  private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  /**
   * Parses a timestamp expressed in the INDI format. If the timestamp does not
   * have the correct format it returns the current timestamp.
   *
   * @param time the timestamp to be parsed
   * @return the parsed timestamp or the current timestamp if the format of the
   * <code>time</code> is not correct.
   */
  public static Date parseTimestamp(String time) {
    Date timestamp = new Date();

    time = time.trim();
    
    if (!(time.length() == 0)) {
      try {
        timestamp = dateFormat.parse(time);
      } catch (ParseException e) {
        try {
          timestamp = dateFormat2.parse(time);
        } catch (ParseException ee) {
          timestamp = new Date();  // Not correct format, returning current timestamp.
        }
      }
    }

    return timestamp;
  }

  /**
   * Formats a timestamp according to the INDI format.
   *
   * @param timestamp the timestamp to be formmated
   * @return the formatted timestamp
   */
  public static String formatTimestamp(Date timestamp) {
    return dateFormat.format(timestamp);
  }

  /**
   * Gets the current timestamp in form of a String according to the INDI
   * specification.
   *
   * @return the current timestamp according to the INDI specification.
   */
  public static String getCurrentTimestamp() {
    return formatTimestamp(new Date());
  }
}
