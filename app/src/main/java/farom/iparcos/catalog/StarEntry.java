package farom.iparcos.catalog;

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

import farom.iparcos.R;

/**
 * A star
 */
public class StarEntry extends CatalogEntry {

    /**
     * Resource file.
     */
    private final static int RESOURCE = R.raw.stars;
    /**
     * The length of each line in the resource file.
     */
    private final static int ENTRY_LENGTH = 118;
    /**
     * The length of the name in each line.
     */
    private final static int NAME_LENGTH = 22;
    /**
     * The length of the string containing all the other names in each line.
     */
    private final static int NAMES_LENGTH = 64;
    /**
     * The length of the RA coordinate in each line.
     */
    private final static int RA_LENGTH = 13;
    /**
     * The length of the DEC coordinate in each line.
     */
    private final static int DEC_LENGTH = 12;
    /**
     * The length of the magnitude in each line.
     */
    private final static int MAGNITUDE_LENGTH = 7;

    protected String names;
    protected String magnitude;

    /**
     * Create the entry from a formatted line
     * (ie. "ALGOL                 ALGOL; BET PER; HD19356; SAO38592                               03 08 10.131 +40 57 20.43    2.1")
     *
     * @param buf formatted line
     */
    public StarEntry(char[] buf) {
        String data = String.valueOf(buf);

        int i = 0;

        name = data.substring(i, i + NAME_LENGTH).trim();
        i += NAME_LENGTH;

        names = data.substring(i, i + NAMES_LENGTH).trim();
        i += NAMES_LENGTH;

        String raString = data.substring(i, i + RA_LENGTH).trim();
        i += RA_LENGTH;

        String decString = data.substring(i, i + DEC_LENGTH).trim();
        i += DEC_LENGTH;

        coord = new Coordinates(raString, decString);

        magnitude = data.substring(i, ENTRY_LENGTH).trim();
    }

    /**
     * Create the list of star entries
     *
     * @param context Context to access the catalog file
     * @return A list of stars
     */
    public static ArrayList<StarEntry> createList(Context context) {
        ArrayList<StarEntry> entries = new ArrayList<>();
        // Open and read the catalog file
        try {
            final Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(RESOURCE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), ENTRY_LENGTH);
            char[] buf = new char[ENTRY_LENGTH];

            while (reader.read(buf, 0, ENTRY_LENGTH) > 0) {
                entries.add(new StarEntry(buf));
                // Skip new line "\n"
                reader.skip(1);
            }

            inputStream.close();

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return entries;
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
        String str = "<b>" + r.getString(R.string.entry_names) + r.getString(R.string.colon_with_spaces) + "</b>" + names + "<br/>";
        str += "<b>" + r.getString(R.string.entry_type) + r.getString(R.string.colon_with_spaces) + "</b>" + r.getString(R.string.entry_star) + "<br/>";
        str += "<b>" + r.getString(R.string.entry_magnitude) + r.getString(R.string.colon_with_spaces) + "</b>" + magnitude + "<br/>";
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
        String str = "<b>" + r.getString(R.string.entry_star) + "</b> " + r.getString(R.string.entry_mag) + "=" + magnitude;
        return new SpannableString(Html.fromHtml(str));
    }
}