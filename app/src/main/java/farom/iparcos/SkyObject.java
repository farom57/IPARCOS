package farom.iparcos;

import android.util.Log;

/**
 * @author farom57
 */
public class SkyObject {

    String[] identifiers;
    String ra;
    String de;

    private SkyObject(String ra2, String de2, String[] identifiers2) {
        ra = ra2;
        de = de2;
        identifiers = identifiers2;
    }

    /**
     * Create the SkyObject from the line in the catalog file
     *
     * @param line
     */
    public static SkyObject fromLine(String line) {
        String[] tokens = line.split("\\|");
        if (tokens.length < 2) {
            Log.w("CATALOG", "Can't parse the line: " + line);
            return null;
        }


        String[] coords = tokens[0].trim().split("[ ]+");
        if (coords.length < 2) {
            Log.w("CATALOG", "Can't parse the coordinates: " + tokens[0].trim());
            return null;
        }

        String ra = coords[0].trim();
        String de = coords[1].trim();
        String[] identifiers = tokens[1].trim().split(",");

        // Verifying data
        if (!ra.matches("^[\\+-]?[0-9]+(:[0-9]+){0,2}(\\.[0-9]+)?$")) {
            Log.e("CATALOG", "ra = " + ra + " does not match the std format");
        }
        if (!de.matches("^[\\+-]?[0-9]+(:[0-9]+){0,2}(\\.[0-9]+)?$")) {
            Log.e("CATALOG", "de = " + de + " does not match the std format");
        }

        for (int i = 1; i < identifiers.length; i++) {
            if (identifiers[i].matches("NAME")) {
                Log.e("CATALOG", identifiers[i]);
                String[] temp = identifiers[i].split("^NAME");
                Log.e("CATALOG", "temp[0]=" + temp[0]);
                Log.e("CATALOG", "temp[1]=" + temp[1]);
                identifiers[i] = temp[0];

            } else {
                identifiers[i] = identifiers[i].replaceAll("\\s", "");
            }
        }

        return new SkyObject(ra, de, identifiers);

    }

    public String getRA() {
        return ra;
    }

    public String getDE() {
        return de;
    }

    public String[] getIdentifiers() {
        return identifiers;
    }

    public String getIdentifiersStr() {
        StringBuilder result = new StringBuilder(identifiers[0]);
        for (int i = 1; i < identifiers.length; i++) {
            result.append(", ").append(identifiers[i]);
        }
        return result.toString();
    }
}