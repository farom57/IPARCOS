package farom.iparcos.catalog;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stellar coordinates with methods to convert from/to String
 */
public class Coordinates {

    /**
     * Right ascension in arcsec
     */
    double ra;
    /**
     * Declination in arcsec
     */
    double de;

    /**
     * From String
     *
     * @param ra_str Right ascension string
     * @param de_str Declination string
     */
    public Coordinates(String ra_str, String de_str) {
        ra = convertRa(ra_str);
        de = convertDe(de_str);
    }

    /**
     * Convert Sexagesimal string into degrees (ie. "01 02 03.4" -> (1+2/60+3.4/3600)*15°)
     *
     * @param str RA string
     * @return degrees
     */
    static public double convertRa(String str) throws NumberFormatException {
        str = str.trim();

        Pattern p = Pattern.compile("([0-9]{1,2})[h:\\s]([0-9]{1,2})([m:'\\s]([0-9]{1,2})([,\\.]([0-9]*))?[s\"]?)?[m:'\\s]?");
        Matcher m = p.matcher(str);

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
            throw new NumberFormatException(str + " is not a valid sexagesimal string");
        }
    }

    /**
     * Convert Sexagesimal string into degrees (ie. "01 02 03.4" -> (1+2/60+3.4/3600)°)
     *
     * @param str Declination string
     * @return degrees
     */
    static public double convertDe(String str) throws NumberFormatException {
        str = str.trim();

        Pattern p = Pattern.compile("([\\+\\-]?)([0-9]{1,2})[°:\\s]([0-9]{1,2})([m:'\\s]([0-9]{1,2})([,\\.]([0-9]*))?[s\"]?)?[m:'\\s]?");
        Matcher m = p.matcher(str);

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
            throw new NumberFormatException(str + " is not a valid sexagesimal string");
        }
    }

    /**
     * Right ascension in deg
     *
     * @return degrees
     */
    public double getRa() {
        return ra;
    }

    /**
     * Declination in deg
     *
     * @return degrees
     */
    public double getDe() {
        return de;
    }

    /**
     * Return a string with the right ascension (hh:mm:ss)
     *
     * @return string
     */
    public String getRaStr() {
        int deg = (int) Math.floor(Math.abs(ra) / 15);
        int min = (int) Math.floor((Math.abs(ra) / 15 - deg) * 60);
        int sec = (int) Math.round(((Math.abs(ra) / 15 - deg) * 60 - min) * 60);
        return String.format("%02d:%02d:%02d", deg, min, sec);
    }

    /**
     * Return a string with the right ascension (hh:mm:ss)
     *
     * @return string
     */
    public String getDeStr() {
        int deg = (int) Math.floor(Math.abs(de));
        int min = (int) Math.floor((Math.abs(de) - deg) * 60);
        int sec = (int) Math.round(((Math.abs(de) - deg) * 60 - min) * 60);
        if (Math.signum(de) >= 0) {
            return String.format("+%02d:%02d:%02d", deg, min, sec);
        } else {
            return String.format("-%02d:%02d:%02d", deg, min, sec);
        }
    }

    public String toString() {
        return "RA: " + getRaStr() + " DE: " + getDeStr();
    }

//    static void test(){
//        Log.d("Coordinates_Test"," --- RA --- ");
//        Log.d("Coordinates_Test","\"00 00:01.000\" -> " + convertRa("00 00:01.000") + "(true value = 0.00416666666)");
//        Log.d("Coordinates_Test","\"10:00 00.000\" -> " + convertRa("10:00 00.000") + "(true value = 150)");
//        Log.d("Coordinates_Test","\"21:00m00.000\" -> " + convertRa("21:00m00.000") + "(true value = 315)");
//        Log.d("Coordinates_Test","\"21:00'20.000\" -> " + convertRa("21:00'20.000") + "(true value = ?)");
//        Log.d("Coordinates_Test","\"21:00m00\"\" -> " + convertRa("21:00m00\"") + "(true value = 315)");
//        Log.d("Coordinates_Test", "\"12:34:56.789\" -> " + convertRa("12:34:56.789") + "(true value = 188.736620833)");
//        Log.d("Coordinates_Test","\"  1:2:3.04 \" -> " + convertRa("  1:2:3.04 ") + "(true value = 15.5126666667)");
//        Log.d("Coordinates_Test","\"  1:2:3. \" -> " + convertRa("  1:2:3. ") + "(true value = 15.5125)");
//        Log.d("Coordinates_Test","\"  1:2:3s\" -> " + convertRa("  1:2:3s") + "(true value = 15.5125)");
//        Log.d("Coordinates_Test","\"  1h2\" -> " + convertRa("  1h2") + "(true value = 15.5...)");
//        Log.d("Coordinates_Test","\"  1h2m \" -> " + convertRa("  1h2m ") + "(true value = 15.5...)");
//        Log.d("Coordinates_Test","\"  1h02m10s\" -> " + convertRa("  1h02m10s") + "(true value = 15.5...)");
//        try {
//            Log.d("Coordinates_Test", "\"  1a:2:3. \" -> " + convertRa("  1a:2:3. ") + "(wrong)");
//        }catch(NumberFormatException e) {
//            Log.d("Coordinates_Test",e.getMessage());
//        }
//        try{
//            Log.d("Coordinates_Test","\"10m2:3.\" -> " + convertRa("10m2:3.") + "(wrong)");
//        }catch(NumberFormatException e) {
//            Log.d("Coordinates_Test",e.getMessage());
//        }
//        try{
//            Log.d("Coordinates_Test","\"-10:02:34.1 \" -> " + convertRa("-10:02:34.1 ") + "(wrong)");
//        }catch(NumberFormatException e) {
//            Log.d("Coordinates_Test",e.getMessage());
//        }
//        try{
//            Log.d("Coordinates_Test","\"110:02:34.1 \" -> " + convertRa("110:02:34.1 ") + "(wrong)");
//        }catch(NumberFormatException e) {
//            Log.d("Coordinates_Test",e.getMessage());
//        }
//        try{
//            Log.d("Coordinates_Test","\"10:012:34.1 \" -> " + convertRa("10:012:34.1 ") + "(wrong)");
//        }catch(NumberFormatException e) {
//            Log.d("Coordinates_Test",e.getMessage());
//        }        try{
//            Log.d("Coordinates_Test","\"10::34.1 \" -> " + convertRa("10::34.1 ") + "(wrong)");
//        }catch(NumberFormatException e) {
//            Log.d("Coordinates_Test",e.getMessage());
//        }
//
//        Log.d("Coordinates_Test"," --- DE --- ");
//        Log.d("Coordinates_Test","\"+00 00:01.000\" -> " + convertDe("+00 00:01.000") + "(true value = 0.000277777)");
//        Log.d("Coordinates_Test","\"10:00 00.000\" -> " + convertDe("10:00 00.000") + "(true value = 10)");
//        Log.d("Coordinates_Test","\"-21:00m00.000\" -> " + convertDe("-21:00m00.000") + "(true value = -21)");
//        Log.d("Coordinates_Test","\"21:00'20.000\" -> " + convertDe("21:00'20.000") + "(true value = 21.0055555556)");
//        Log.d("Coordinates_Test","\"21:00m00\"\" -> " + convertDe("21:00m00\"") + "(true value = 21)");
//        Log.d("Coordinates_Test", "\"-12:34:56.789\" -> " + convertDe("-12:34:56.789") + "(true value = -12.5824413889)");
//        Log.d("Coordinates_Test","\"  +1:2:3.04 \" -> " + convertDe("  +1:2:3.04 ") + "(true value = 1.03417777778)");
//
//        try {
//            Log.d("Coordinates_Test", "\"+ 00 00:01.000\" -> " + convertDe("+ 00 00:01.000") + "(wrong)");
//        }catch(NumberFormatException e) {
//            Log.d("Coordinates_Test",e.getMessage());
//        }
//
//        Log.d("Coordinates_Test",(new Coordinates("12:34:56.789","-12:34:56.789")).toString());
//        Log.d("Coordinates_Test",(new Coordinates("1h02m10s","+1:2:3.04")).toString());
//
//
//    }
}