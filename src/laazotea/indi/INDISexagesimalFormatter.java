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

import java.util.Formatter;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * A class to format and parse numbers in sexagesimal format.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.10, March 19, 2012
 */
public class INDISexagesimalFormatter {

  private String format;
  private int length;
  private int fractionLength;

  /**
   * Constructs an instance of
   * <code>INDISexagesimalFormatter</code> with a particular format.
   *
   * @param format The desired format
   * @throws IllegalArgumentException if the format is not correct: begins with
   * %, ends with m and specifies a length and fractionLength in the form
   * length.fractionLength. Valid fractionLengths are 3, 5, 6, 8 and 9. For
   * example %5.3m.
   */
  public INDISexagesimalFormatter(String format) throws IllegalArgumentException {
    this.format = format;

    checkFormat();
  }

  /**
   * Gets the format of this formatter.
   *
   * @return the format of this formatter.
   */
  public String getFormat() {
    return format;
  }

  /**
   * Checks the specified format string.
   *
   * @throws IllegalArgumentException if the format string is not valid: begins
   * with %, ends with m and specifies a length and fractionLength in the form
   * length.fractionLength. Valid fractionLengths are 3, 5, 6, 8 and 9. For
   * example %5.3m.
   */
  private void checkFormat() throws IllegalArgumentException {
    if (!format.startsWith("%")) {
      throw new IllegalArgumentException("Number format not starting with %");
    }

    if (!format.endsWith("m")) {
      throw new IllegalArgumentException("Sexagesimal format not recognized (not ending m)");
    }

    String remaining = format.substring(1, format.length() - 1);

    int dotPos = remaining.indexOf(".");

    if (dotPos == -1) {
      throw new IllegalArgumentException("Sexagesimal format not correct (no dot)");
    }

    String l = remaining.substring(0, dotPos);
    String frLength = remaining.substring(dotPos + 1);

    try {
      length = Integer.parseInt(l);
      fractionLength = Integer.parseInt(frLength);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Illegal sexagesimal length or fraction length");
    }

    if ((fractionLength != 3) && (fractionLength != 5) && (fractionLength != 6) && (fractionLength != 8) && (fractionLength != 9)) {
      throw new IllegalArgumentException("Illegal sexagesimal fraction length");
    }
  }

  /**
   * Parses a sexagesimal number. DO NOT USE IT. THIS IS A PRELIMINARY VERSION
   * AND DOES NOT WORK AS EXPECTED. THIS METHOD WILL DISAPEAR IN FUTURE VERSIONS
   * OF THE CLASS.
   *
   * @param number NOT USED
   * @return NOT USED
   * @throws IllegalArgumentException
   * @deprecated
   */
  @Deprecated
  public double parseSexagesimal2(String number) throws IllegalArgumentException {
    number = number.replace(' ', ':');
    number = number.replace(';', ':');

//    System.out.println(" ->" + number + " ; " + format);
//    System.out.flush();

    if (number.indexOf(":") == -1) {  // If there are no separators maybe they have sent just a single double
      try {
        double n = Double.parseDouble(number);

        return n;
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Sexagesimal number format not correct (not even single number)");
      }
    }

    StringTokenizer st = new StringTokenizer(number, ":", false);

    double degrees = 0;
    double minutes = 0;
    double seconds = 0;

    try {
      String aux = st.nextToken().trim();

      if (aux.length() > 0) {
        degrees = Double.parseDouble(aux);
      }

      aux = st.nextToken().trim();

      if (aux.length() > 0) {
        minutes = Double.parseDouble(aux);
      }

      if (fractionLength > 5) {
        aux = st.nextToken().trim();

        if (aux.length() > 0) {
          seconds = Double.parseDouble(aux);
        }
      }

    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException("Sexagesimal number format not correct");
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Sexagesimal number component not correct");
    }

    double res = degrees;
    if (degrees > 0) {
      res += (minutes / 60.0) + (seconds / 3600.0);
    } else {
      res -= (minutes / 60.0) + (seconds / 3600.0);
    }

    return res;
  }

  /**
   * Parses a sexagesimal number. The input
   * <code>String</code> is formatted as a maximum of three doubles separated by
   * : ; or a blank space. The first number represents the number of degrees,
   * the second is the number of minutes and the third is the number of seconds.
   *
   * @param number The number to be parsed.
   * @return The parsed double.
   * @throws IllegalArgumentException if the number format is not correct.
   */
  public double parseSexagesimal(String number) throws IllegalArgumentException {
    number = number.trim();

    if (number.length() == 0) {
      throw new IllegalArgumentException("Empty number");
    }

    number = number.replace(' ', ':');
    number = number.replace(';', ':');

    int charCount = number.length() - number.replaceAll(":", "").length();

    if (charCount > 2) {
      throw new IllegalArgumentException("Too many components for the sexagesimal formatter");
    }

    double degrees = 0;
    double minutes = 0;
    double seconds = 0;

    StringTokenizer st = new StringTokenizer(number, ":", false);

    String d = st.nextToken().trim();

    try {
      degrees = Double.parseDouble(d);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Number format incorrect");
    }

    if (st.hasMoreTokens()) {
      String m = st.nextToken().trim();

      try {
        minutes = Double.parseDouble(m);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Minutes format incorrect");
      }

      if (minutes < 0) {
        throw new IllegalArgumentException("Minutes cannot be negative");
      }

      if (st.hasMoreTokens()) {
        String s = st.nextToken().trim();

        try {
          seconds = Double.parseDouble(s);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Seconds format incorrect");
        }

        if (seconds < 0) {
          throw new IllegalArgumentException("Seconds cannot be negative");
        }
      }
    }

    double res = degrees;
    if (Double.valueOf(degrees).compareTo(-0.) > 0) {
      res += (minutes / 60.0) + (seconds / 3600.0);
    } else {
      res -= (minutes / 60.0) + (seconds / 3600.0);
    }

    return res;
  }

  /**
   * Fomats a number according to the number format os this formatter.
   *
   * @param number the number to be formatted.
   * @return The formatted number as a <code>String</code>.
   */
  public String format(Double number) {
    int sign = 1;
    if (number < 0) {
      sign = -1;
    }

    number = Math.abs(number);

    String fractionalPart = ":";

    int integerPart;

    integerPart = ((int)Math.floor(number));

    double fractional = Math.abs(number - integerPart);

    if (fractionLength < 6) {
      double minutes = fractional * 60;

      String form = "%02.0f";
      if (fractionLength == 5) {
        form = "%04.1f";
      }

      Formatter formatter = new Formatter(Locale.US);
      String newMinutes = formatter.format(form, minutes).toString();

      if (Double.parseDouble(newMinutes) >= 60.0) {
        minutes = 0.0;
        integerPart++;
      }

      formatter = new Formatter(Locale.US);
      fractionalPart += formatter.format(form, minutes);
    } else {
      double minutes = Math.floor(fractional * 60);

      double rest = fractional - ((double)minutes / 60.0);

      double seconds = rest * 3600;

      String form = "%02.0f";
      if (fractionLength == 8) {
        form = "%04.1f";
      } else if (fractionLength == 9) {
        form = "%05.2f";
      }

      Formatter formatter = new Formatter(Locale.US);
      String newSeconds = formatter.format(form, seconds).toString();

      if (Double.parseDouble(newSeconds) >= 60.0) {
        seconds = 0.0;
        minutes++;
      }

      formatter = new Formatter(Locale.US);
      String newMinutes = formatter.format("%02.0f", minutes).toString();

      if (Double.parseDouble(newMinutes) >= 60.0) {
        minutes = 0.0;
        integerPart++;
      }

      formatter = new Formatter(Locale.US);
      fractionalPart += formatter.format("%02.0f:" + form, minutes, seconds);
    }

    String res = integerPart + fractionalPart;

    if (sign < 0) {
      res = "-" + res;
    }

    res = padLeft(res, length);

    return res;
  }

  /**
   * Pads a String to the left with spaces.
   *
   * @param s The <code>String</code> to be padded.
   * @param n The maximum size of the padded <code>String</code>.
   * @return The padded <code>String</code>
   */
  private String padLeft(String s, int n) {
    if (s.length() >= n) {
      return s;
    }
    String spaces = "";

    for (int i = 0 ; i < n - s.length() ; i++) {
      spaces += " ";
    }

    return spaces + s;
  }
}
