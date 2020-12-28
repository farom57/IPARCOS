package org.indilib.i4j;

/*
 * #%L
 * INDI for Java Base Library
 * %%
 * Copyright (C) 2013 - 2014 indiforjava
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
import java.io.Serializable;
import java.util.Formatter;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * A class to format and parse numbers in sexagesimal format.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public class INDISexagesimalFormatter implements Serializable {

    /**
     * the serial version id.
     */
    private static final long serialVersionUID = -3904216502728630808L;

    /**
     * The format to be used. It must begin with %, end with m and specifies a
     * length and fractionLength in the form length.fractionLength. Valid
     * fractionLengths are 3, 5, 6, 8 and 9. For example %5.3m.
     */
    private String format;

    /**
     * The length of the format.
     */
    private int length;

    /**
     * The fraction length.
     */
    private int fractionLength;

    /**
     * Fraction Length 3.
     */
    private static final int FL3 = 3;

    /**
     * Fraction Length 5.
     */
    private static final int FL5 = 5;

    /**
     * Fraction Length 6.
     */
    private static final int FL6 = 6;

    /**
     * Fraction Length 8.
     */
    private static final int FL8 = 8;

    /**
     * Fraction Length 9.
     */
    private static final int FL9 = 9;

    /**
     * Minutes in a hour.
     */
    private static final double MINUTES_PER_HOUR = 60.0;

    /**
     * Seconds in a hour.
     */
    private static final double SECONDS_PER_HOUR = 3600.0;

    /**
     * Seconds in a minute.
     */
    private static final double SECONDS_PER_MINUTE = 60.0;

    /**
     * Zero Negative.
     */
    private static final double ZERO_NEG = -0.;

    /**
     * Constructs an instance of <code>INDISexagesimalFormatter</code> with a
     * particular format. Throws IllegalArgumentException if the format is not
     * correct: begins with %, ends with m and specifies a length and
     * fractionLength in the form length.fractionLength. Valid fractionLengths
     * are 3, 5, 6, 8 and 9. For example %5.3m.
     * 
     * @param format
     *            The desired format
     */
    public INDISexagesimalFormatter(final String format) {
        this.format = format;

        checkFormat();
    }

    /**
     * Gets the format of this formatter.
     * 
     * @return the format of this formatter.
     */
    public final String getFormat() {
        return format;
    }

    /**
     * Checks the specified format string. Throws IllegalArgumentException if
     * the format string is not valid: begins with %, ends with m and specifies
     * a length and fractionLength in the form length.fractionLength. Valid
     * fractionLengths are 3, 5, 6, 8 and 9. For example %5.3m.
     */
    private void checkFormat() {
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

        if (fractionLength != FL3 && fractionLength != FL5 && fractionLength != FL6 && fractionLength != FL8 && fractionLength != FL9) {
            throw new IllegalArgumentException("Illegal sexagesimal fraction length");
        }
    }

    /**
     * Parses a sexagesimal newNumber. DO NOT USE IT. THIS IS A PRELIMINARY
     * VERSION AND DOES NOT WORK AS EXPECTED. THIS METHOD WILL DISAPEAR IN
     * FUTURE VERSIONS OF THE CLASS.
     * 
     * @param number
     *            NOT USED
     * @return NOT USED
     * @deprecated
     */
    @Deprecated
    public final double parseSexagesimal2(final String number) {
        String newNumber = number.replace(' ', ':');
        newNumber = newNumber.replace(';', ':');

        if (newNumber.indexOf(":") == -1) { // If there are no separators maybe
            // they have sent just a single
            // double
            try {
                double n = Double.parseDouble(newNumber);

                return n;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Sexagesimal number format not correct (not even single number)");
            }
        }

        StringTokenizer st = new StringTokenizer(newNumber, ":", false);

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

            if (fractionLength > FL5) {
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
            res += minutes / MINUTES_PER_HOUR + seconds / SECONDS_PER_HOUR;
        } else {
            res -= minutes / MINUTES_PER_HOUR + seconds / SECONDS_PER_HOUR;
        }

        return res;
    }

    /**
     * Parses a sexagesimal newNumber. The input <code>String</code> is
     * formatted as a maximum of three doubles separated by : ; or a blank
     * space. The first newNumber represents the newNumber of degrees, the
     * second is the newNumber of minutes and the third is the newNumber of
     * seconds.
     * 
     * @param number
     *            The newNumber to be parsed.
     * @return The parsed double.
     */
    public final double parseSexagesimal(final String number) {
        String newNumber = number.trim();

        if (newNumber.isEmpty()) {
            throw new IllegalArgumentException("Empty number");
        }

        newNumber = newNumber.replace(' ', ':');
        newNumber = newNumber.replace(';', ':');

        int charCount = newNumber.length() - newNumber.replaceAll(":", "").length();

        if (charCount > 2) {
            throw new IllegalArgumentException("Too many components for the sexagesimal formatter");
        }

        double degrees = 0;
        double minutes = 0;
        double seconds = 0;

        StringTokenizer st = new StringTokenizer(newNumber, ":", false);

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
        if (Double.valueOf(degrees).compareTo(ZERO_NEG) > 0) {
            res += minutes / MINUTES_PER_HOUR + seconds / SECONDS_PER_HOUR;
        } else {
            res -= minutes / MINUTES_PER_HOUR + seconds / SECONDS_PER_HOUR;
        }

        return res;
    }

    /**
     * Fomats a newNumber according to the newNumber format os this formatter.
     * 
     * @param number
     *            the newNumber to be formatted.
     * @return The formatted newNumber as a <code>String</code>.
     */
    public final String format(final Double number) {
        int sign = 1;
        if (number < 0) {
            sign = -1;
        }

        double newNumber = Math.abs(number);

        String fractionalPart = ":";

        int integerPart;

        integerPart = (int) Math.floor(newNumber);

        double fractional = Math.abs(newNumber - integerPart);

        if (fractionLength < FL6) {
            double minutes = fractional * SECONDS_PER_MINUTE;

            String form = "%02.0f";
            if (fractionLength == FL5) {
                form = "%04.1f";
            }

            Formatter formatter = new Formatter(Locale.US);
            String newMinutes = formatter.format(form, minutes).toString();

            if (Double.parseDouble(newMinutes) >= MINUTES_PER_HOUR) {
                minutes = 0.0;
                integerPart++;
            }

            formatter = new Formatter(Locale.US);
            fractionalPart += formatter.format(form, minutes);
        } else {
            double minutes = Math.floor(fractional * MINUTES_PER_HOUR);

            double rest = fractional - minutes / SECONDS_PER_MINUTE;

            double seconds = rest * SECONDS_PER_HOUR;

            String form = "%02.0f";
            if (fractionLength == FL8) {
                form = "%04.1f";
            } else if (fractionLength == FL9) {
                form = "%05.2f";
            }

            Formatter formatter = new Formatter(Locale.US);
            String newSeconds = formatter.format(form, seconds).toString();

            if (Double.parseDouble(newSeconds) >= SECONDS_PER_MINUTE) {
                seconds = 0.0;
                minutes++;
            }

            formatter = new Formatter(Locale.US);
            String newMinutes = formatter.format("%02.0f", minutes).toString();

            if (Double.parseDouble(newMinutes) >= MINUTES_PER_HOUR) {
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
     * @param s
     *            The <code>String</code> to be padded.
     * @param n
     *            The maximum size of the padded <code>String</code>.
     * @return The padded <code>String</code>
     */
    private String padLeft(final String s, final int n) {
        if (s.length() >= n) {
            return s;
        }

        int nSpaces = n - s.length();

        if (nSpaces <= 0) {
            return s;
        }

        StringBuilder spacesBuffer = new StringBuilder(nSpaces);
        for (int i = 0; i < nSpaces; i++) {
            spacesBuffer.append(" ");
        }
        String spaces = spacesBuffer.toString();

        return spaces + s;
    }
}
