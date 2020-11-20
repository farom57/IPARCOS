package marcocipriani01.iparcos.catalog;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import marcocipriani01.iparcos.R;

/**
 * Represents a deep sky object. This class also contains a loader to fetch DSO from the app's catalog.
 */
public class DSOEntry extends CatalogEntry {

    /**
     * Resource file.
     */
    private final static int RESOURCE = R.raw.ngc_ic_b;
    /**
     * The length of each line in the resource file.
     */
    private final static int ENTRY_LENGTH = 54;
    /**
     * The length of the name in each line.
     */
    private final static int NAME_LENGTH = 25;
    /**
     * The length of the magnitude in each line.
     */
    private final static int MAGNITUDE_LENGTH = 2;
    /**
     * The length of the type string in each line.
     */
    private final static int TYPE_LENGTH = 3;
    /**
     * The length of the object size in each line.
     */
    private final static int SIZE_LENGTH = 5;
    /**
     * The length of the RA coordinate in each line.
     */
    private final static int RA_LENGTH = 10;
    /**
     * The length of the DEC coordinate in each line.
     */
    //private final static int DEC_LENGTH = 9;

    private final String type;
    private final String size;
    private final String magnitude;

    /**
     * Create the entry from a formatted line
     * (ie. "Dumbbell nebula          8 Pl 15.2 19 59 36.1+22 43 00")
     *
     * @param buf formatted line
     */
    private DSOEntry(char[] buf) {
        String data = String.valueOf(buf);

        int i = 0;

        name = data.substring(i, i + NAME_LENGTH).trim();
        i += NAME_LENGTH;

        magnitude = data.substring(i, i + MAGNITUDE_LENGTH).trim();
        i += MAGNITUDE_LENGTH;

        type = data.substring(i, i + TYPE_LENGTH).trim();
        i += TYPE_LENGTH;

        size = data.substring(i, i + SIZE_LENGTH).trim();
        i += SIZE_LENGTH;

        String raString = data.substring(i, i + RA_LENGTH).trim();
        i += RA_LENGTH;

        String decString = data.substring(i, ENTRY_LENGTH).trim();

        coord = new Coordinates(raString, decString);
    }

    /**
     * Create the list of DSO entries
     *
     * @param context Context to access the catalog file
     * @return A list of stars
     */
    public static ArrayList<DSOEntry> createList(Context context) {
        ArrayList<DSOEntry> entries = new ArrayList<>();
        // Open and read the catalog file
        try {
            final Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(RESOURCE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), ENTRY_LENGTH);
            char[] buf = new char[ENTRY_LENGTH];

            while (reader.read(buf, 0, ENTRY_LENGTH) > 0) {
                entries.add(new DSOEntry(buf));
                // Skip new line "\n"
                //noinspection ResultOfMethodCallIgnored
                reader.skip(1);
            }

            inputStream.close();

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return entries;
    }

    /**
     * Return the string resource which correspond to the type acronym
     *
     * @return text
     */
    private int getTypeStringResource() {
        switch (type) {
            case "Gx":
                return R.string.entry_Gx;

            case "OC":
                return R.string.entry_OC;

            case "Gb":
                return R.string.entry_Gb;

            case "Nb":
                return R.string.entry_Nb;

            case "Pl":
                return R.string.entry_Pl;

            case "C+N":
                return R.string.entry_cluster_nebulosity;

            case "Ast":
                return R.string.entry_Ast;

            case "Kt":
                return R.string.entry_Kt;

            case "***":
                return R.string.entry_triStar;

            case "D*":
                return R.string.entry_doubleStar;

            case "*":
                return R.string.entry_star;

            case "?":
                return R.string.entry_uncertain;

            case "":
                return R.string.entry_blank;

            case "-":
                return R.string.entry_minus;

            case "PD":
                return R.string.entry_PD;

            default:
                return R.string.entry_blank;
        }
    }

    /**
     * Return the string resource which correspond to the type acronym
     *
     * @return short text
     */
    private int getTypeShortStringResource() {
        switch (type) {
            case "Gx":
                return R.string.entry_short_Gx;

            case "OC":
                return R.string.entry_short_OC;

            case "Gb":
                return R.string.entry_short_Gb;

            case "Nb":
                return R.string.entry_short_Nb;

            case "Pl":
                return R.string.entry_short_Pl;

            case "C+N":
                return R.string.entry_short_cluster_nebula;

            case "Ast":
                return R.string.entry_short_Ast;

            case "Kt":
                return R.string.entry_short_Kt;

            case "***":
                return R.string.entry_short_triStar;

            case "D*":
                return R.string.entry_short_doubleStar;

            case "*":
                return R.string.entry_short_star;

            case "?":
                return R.string.entry_short_uncertain;

            case "":
                return R.string.entry_short_blank;

            case "-":
                return R.string.entry_short_minus;

            case "PD":
                return R.string.entry_short_PD;

            default:
                return R.string.entry_short_blank;
        }
    }

    /**
     * Create the description rich-text string
     *
     * @param ctx Context (to access resource strings)
     * @return description Spannable
     */
    @Override
    public Spannable createDescription(Context ctx) {
        Resources r = ctx.getResources();
        String str = "<b>" + r.getString(R.string.entry_type) + r.getString(R.string.colon_with_spaces) + "</b>" + r.getString(getTypeStringResource()) + "<br/>";
        if (!magnitude.equals("")) {
            str += "<b>" + r.getString(R.string.entry_magnitude) + r.getString(R.string.colon_with_spaces) + "</b>" + magnitude + "<br/>";
        }
        if (!size.equals("")) {
            str += "<b>" + r.getString(R.string.entry_size) + r.getString(R.string.colon_with_spaces) + "</b>" + size + " " + r.getString(R.string.arcmin) + "<br/>";
        }
        str += "<b>" + r.getString(R.string.entry_RA) + r.getString(R.string.colon_with_spaces) + "</b>" + coord.getRaStr() + "<br/>";
        str += "<b>" + r.getString(R.string.entry_DE) + r.getString(R.string.colon_with_spaces) + "</b>" + coord.getDeStr();
        return new SpannableString(Html.fromHtml(str));
    }

    /**
     * Create the summary rich-text string (1 line)
     *
     * @param ctx Context (to access resource strings)
     * @return summary Spannable
     */
    @Override
    public Spannable createSummary(Context ctx) {
        Resources r = ctx.getResources();
        String str = "<b>" + r.getString(getTypeShortStringResource()) + "</b> ";
        if (!magnitude.equals("")) {
            str += r.getString(R.string.entry_mag) + ": " + magnitude + " ";
        }
        if (!size.equals("")) {
            str += r.getString(R.string.entry_size).toLowerCase() + ": " + size + r.getString(R.string.arcmin);
        }
        return new SpannableString(Html.fromHtml(str));
    }
}