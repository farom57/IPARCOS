package farom.iparcos.catalog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores equatorial coordinates and contains some utilities to convert decimal degrees to strings and vice-versa.
 */
public class Coordinates {

    /**
     * Right ascension in arcsec
     */
    private double ra;
    /**
     * Declination in arcsec
     */
    private double dec;

    /**
     * Class constructor. Processes the input strings and calculates right ascension and declination.
     *
     * @param ra  Right ascension (string).
     * @param dec Declination (string).
     */
    public Coordinates(String ra, String dec) {
        this.ra = convertRa(ra.trim());
        this.dec = convertDec(dec.trim());
    }

    /**
     * Class constructor.
     *
     * @param ra  Right ascension (decimal degrees).
     * @param dec Declination (decimal degrees).
     */
    public Coordinates(double ra, double dec) {
        this.ra = ra;
        this.dec = dec;
    }

    /**
     * Converts the sexagesimal degrees contained in the input string into degrees (ie. "01 02 03.4" → (1+2/60+3.4/3600)*15°)
     *
     * @param string an input string (right ascension)
     * @return the right ascension converted in decimal degrees.
     */
    private static double convertRa(String string) throws NumberFormatException {
        Pattern p = Pattern.compile("([0-9]{1,2})[h:\\s]([0-9]{1,2})([m:'\\s]([0-9]{1,2})([,.]([0-9]*))?[s\"]?)?[m:'\\s]?");
        Matcher m = p.matcher(string);

        double value = 0;
        if (m.matches()) {

            if (m.group(6) != null) {
                for (int i = 0; i < m.group(6).length(); ++i) {
                    value += 15. / 3600. * (m.group(6).charAt(i) - '0') * Math.pow(0.1, i + 1);
                }
            }
            if (m.group(4) != null) {
                for (int i = 0; i < m.group(4).length(); ++i) {
                    value += 15. / 3600. * (m.group(4).charAt(i) - '0') * Math.pow(10, m.group(4).length() - i - 1);
                }
            }
            if (m.group(2) != null) {
                for (int i = 0; i < m.group(2).length(); ++i) {
                    value += 15. / 60. * (m.group(2).charAt(i) - '0') * Math.pow(10, m.group(2).length() - i - 1);
                }
            }

            if (m.group(1) != null) {
                for (int i = 0; i < m.group(1).length(); ++i) {
                    value += 15. * (m.group(1).charAt(i) - '0') * Math.pow(10, m.group(1).length() - i - 1);
                }
            }

            return value;

        } else {
            throw new NumberFormatException(string + " is not a valid sexagesimal string");
        }
    }

    /**
     * Converts the sexagesimal degrees contained in the input string into degrees (ie. "01 02 03.4" → (1+2/60+3.4/3600)*15°)
     *
     * @param string an input string (declination)
     * @return the declination converted in decimal degrees.
     */
    private static double convertDec(String string) throws NumberFormatException {
        Pattern p = Pattern.compile("([+\\-]?)([0-9]{1,2})[°:\\s]([0-9]{1,2})([m:'\\s]([0-9]{1,2})([,.]([0-9]*))?[s\"]?)?[m:'\\s]?");
        Matcher m = p.matcher(string);

        double value = 0;
        if (m.matches()) {

            if (m.group(7) != null) {
                for (int i = 0; i < m.group(7).length(); ++i) {
                    value += 1. / 3600. * (m.group(7).charAt(i) - '0') * Math.pow(0.1, i + 1);
                }
            }
            if (m.group(5) != null) {
                for (int i = 0; i < m.group(5).length(); ++i) {
                    value += 1. / 3600. * (m.group(5).charAt(i) - '0') * Math.pow(10, m.group(5).length() - i - 1);
                }
            }
            if (m.group(3) != null) {
                for (int i = 0; i < m.group(3).length(); ++i) {
                    value += 1. / 60. * (m.group(3).charAt(i) - '0') * Math.pow(10, m.group(3).length() - i - 1);
                }
            }
            if (m.group(2) != null) {
                for (int i = 0; i < m.group(2).length(); ++i) {
                    value += (m.group(2).charAt(i) - '0') * Math.pow(10, m.group(2).length() - i - 1);
                }
            }
            if (m.group(1) != null) {
                if (m.group(1).equals("-")) {
                    value = -value;
                }
            }

            return value;

        } else {
            throw new NumberFormatException(string + " is not a valid sexagesimal string");
        }
    }

    /**
     * @return the right ascension in decimal degrees.
     */
    public double getRa() {
        return ra;
    }

    /**
     * @return the declination in decimal degrees.
     */
    public double getDec() {
        return dec;
    }

    /**
     * @return a string containing the right ascension (hh:mm:ss)
     */
    public String getRaStr() {
        int deg = (int) Math.floor(Math.abs(ra) / 15);
        int min = (int) Math.floor((Math.abs(ra) / 15 - deg) * 60);
        int sec = (int) Math.round(((Math.abs(ra) / 15 - deg) * 60 - min) * 60);
        return String.format("%02d:%02d:%02d", deg, min, sec);
    }

    /**
     * @return a string containing the declination (hh:mm:ss)
     */
    public String getDeStr() {
        int deg = (int) Math.floor(Math.abs(dec));
        int min = (int) Math.floor((Math.abs(dec) - deg) * 60);
        int sec = (int) Math.round(((Math.abs(dec) - deg) * 60 - min) * 60);
        if (Math.signum(dec) >= 0) {
            return String.format("+%02d:%02d:%02d", deg, min, sec);

        } else {
            return String.format("-%02d:%02d:%02d", deg, min, sec);
        }
    }

    public String toString() {
        return "RA: " + getRaStr() + " Dec: " + getDeStr();
    }
}