package farom.iparcos.catalog;

import android.util.Log;

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
     * @param ra_str
     * @param de_str
     */
    public Coordinates(String ra_str, String de_str){
        // TODO
    }

    /**
     * Convert Sexagesimal string into degrees (ie. "01 02 03.4" -> (1+2/60+3.4/3600)*15°)
     * @param str
     * @return
     */
    static public double convertRa(String str) throws NumberFormatException{
        str = str.trim();

        Pattern p = Pattern.compile("([0-9]{1,2})[h:\\s]([0-9]{1,2})([m:\\s]([0-9]{1,2})([,\\.]([0-9]*))?)?");
        Matcher m = p.matcher(str);

        double value = 0;
        if(m.matches()){
            for(int i=0; i <= m.groupCount(); i++) {
                Log.d("Coordinates", "Groupe " + i +"/" + m.groupCount() + " : " + m.group(i));
            }
            switch(m.groupCount()){
                case 6:
                    for(int i = 0; i<m.group(6).length(); ++i){
                        value += 15./3600.*(m.group(6).charAt(i)-'0')*Math.pow(0.1,i+1);
                    }
                    // do not break but continue
                case 4:
                    for(int i = 0; i<m.group(4).length(); ++i){
                        value += 15./3600.*(m.group(4).charAt(i)-'0')*Math.pow(10,m.group(4).length()-i-1);
                    }
                    // do not break but continue
                case 2:
                    for(int i = 0; i<m.group(2).length(); ++i){
                        value += 15./60.*(m.group(2).charAt(i)-'0')*Math.pow(10,m.group(2).length()-i-1);
                    }
                    for(int i = 0; i<m.group(1).length(); ++i){
                        value += 15.*(m.group(1).charAt(i)-'0')*Math.pow(10,m.group(1).length()-i-1);
                    }
                    return value;
                default:
                    throw new NumberFormatException(str + "is not a valid sexagesimal string");
            }
        }else{
            throw new NumberFormatException(str + "is not a valid sexagesimal string");
        }
    }

    /**
     * Convert Sexagesimal string into degrees (ie. "-01 02 03.4" -> (1+2/60+3.4/3600)*-1°)
     * @param str
     * @return
     */
    static public double convertDe(String str) throws NumberFormatException{
        str = str.trim();

        Pattern p = Pattern.compile("([0-9]{1,2})[h:\\s]([0-9]{1,2})([m:,\\s]([0-9]{1,2})([,\\.]([0-9]*))?)?s?");
        Matcher m = p.matcher(str);

        double value = 0;
        if(m.matches()){
            for(int i=0; i <= m.groupCount(); i++) {
                Log.d("Coordinates", "Groupe " + i +"/" + m.groupCount() + " : " + m.group(i));
            }
            switch(m.groupCount()){
                case 6:
                    for(int i = 0; i<m.group(6).length(); ++i){
                        value += 15./3600.*(m.group(6).charAt(i)-'0')*Math.pow(0.1,i+1);
                    }
                    // do not break but continue
                case 4:
                    for(int i = 0; i<m.group(4).length(); ++i){
                        value += 15./3600.*(m.group(4).charAt(i)-'0')*Math.pow(10,m.group(4).length()-i-1);
                    }
                    // do not break but continue
                case 2:
                    for(int i = 0; i<m.group(2).length(); ++i){
                        value += 15./60.*(m.group(2).charAt(i)-'0')*Math.pow(10,m.group(2).length()-i-1);
                    }
                    for(int i = 0; i<m.group(1).length(); ++i){
                        value += 15.*(m.group(1).charAt(i)-'0')*Math.pow(10,m.group(1).length()-i-1);
                    }
                    return value;
                default:
                    throw new NumberFormatException(str + " is not a valid sexagesimal string (1)");
            }
        }else{
            throw new NumberFormatException(str + " is not a valid sexagesimal string (2)");
        }
    }

    static void test(){
        Log.d("Coordinates_Test","\"00 00:01.000\" -> " + convertRa("00 00:01.000") + "(true value = 0.00416666666)");
        Log.d("Coordinates_Test","\"10:00 00.000\" -> " + convertRa("10:00 00.000") + "(true value = 150)");
        Log.d("Coordinates_Test","\"21:00m00.000\" -> " + convertRa("21:00m00.000") + "(true value = 315)");
        Log.d("Coordinates_Test","\"21:00,10.000\" -> " + convertRa("21:00,10.000") + "(true value = ?)");
        //Log.d("Coordinates_Test","\"21:00m00\"\" -> " + convertRa("21:00m00\"") + "(true value = 315)");
        Log.d("Coordinates_Test", "\"12:34:56.789\" -> " + convertRa("12:34:56.789") + "(true value = 188.736620833)");
        Log.d("Coordinates_Test","\"  1:2:3.04 \" -> " + convertRa("  1:2:3.04 ") + "(true value = 15.5126666667)");
        Log.d("Coordinates_Test","\"  1:2:3. \" -> " + convertRa("  1:2:3. ") + "(true value = 15.5125)");
        Log.d("Coordinates_Test","\"  1:2:3s\" -> " + convertRa("  1:2:3s") + "(true value = 15.5125)");
        try {
            Log.d("Coordinates_Test", "\"  1a:2:3. \" -> " + convertRa("  1a:2:3. ") + "(wrong)");
        }catch(NumberFormatException e) {
            Log.d("Coordinates_Test",e.getMessage());
        }
        try{
            Log.d("Coordinates_Test","\"10m2:3.\" -> " + convertRa("10m2:3.") + "(wrong)");
        }catch(NumberFormatException e) {
            Log.d("Coordinates_Test",e.getMessage());
        }
        try{
            Log.d("Coordinates_Test","\"-10:02:34.1 \" -> " + convertRa("-10:02:34.1 ") + "(wrong)");
        }catch(NumberFormatException e) {
            Log.d("Coordinates_Test",e.getMessage());
        }
        try{
            Log.d("Coordinates_Test","\"110:02:34.1 \" -> " + convertRa("110:02:34.1 ") + "(wrong)");
        }catch(NumberFormatException e) {
            Log.d("Coordinates_Test",e.getMessage());
        }
        try{
            Log.d("Coordinates_Test","\"10:012:34.1 \" -> " + convertRa("10:012:34.1 ") + "(wrong)");
        }catch(NumberFormatException e) {
            Log.d("Coordinates_Test",e.getMessage());
        }        try{
            Log.d("Coordinates_Test","\"10::34.1 \" -> " + convertRa("10::34.1 ") + "(wrong)");
        }catch(NumberFormatException e) {
            Log.d("Coordinates_Test",e.getMessage());
        }

    }

}
