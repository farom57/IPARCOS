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
 * Created by farom on 15/04/15.
 */
public class StarEntry extends CatalogEntry {
    private final static int resource = R.raw.stars;
    private final static int entryLength = 120;
    private final static int nameLength = 22;
    private final static int namesLength = 64;
    private final static int raLength = 12;
    private final static int deLength = 12;
    private final static int magnitudeLength = 7;

    protected String name;
    protected String names;
    protected String magnitude;
    protected Coordinates coord;

    /**
     * Create the entry from a formated line
     * (ie. "ALGOL                 ALGOL; BET PER; HD19356; SAO38592                               03 08 10.131 +40 57 20.43    2.1")
     *
     * @param buf
     */
    public StarEntry(char[] buf) {
        String data = String.valueOf(buf);

        int i = 0;

        name = data.substring(i, i + nameLength).trim();
        i += nameLength;

        names = data.substring(i, i + namesLength).trim();
        i += namesLength;

        String ra_str = data.substring(i, i + raLength).trim();
        i += raLength+1;

        String de_str = data.substring(i, i + deLength).trim();
        i += deLength+1;

        coord = new Coordinates(ra_str,de_str);

        magnitude = data.substring(i, i + magnitudeLength).trim();
        i += magnitudeLength;
    }

    /**
     * Return the object coordinates
     *
     * @return
     */
    @Override
    public Coordinates getCoordinates() {
        return coord;
    }

    /**
     * Return the object name
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Create the description rich-text string
     *
     * @param ctx
     * @return
     */
    @Override
    public Spannable createDescription(Context ctx) {
        Resources r = ctx.getResources();
        String str =  "<b>" + r.getString(R.string.entry_names) + r.getString(R.string.colon_with_spaces) + "</b>" + names + "<br/>";
        str += "<b>" + r.getString(R.string.entry_type) + r.getString(R.string.colon_with_spaces) + "</b>" + r.getString(R.string.entry_star) + "<br/>";
        str += "<b>" + r.getString(R.string.entry_magnitude) + r.getString(R.string.colon_with_spaces) + "</b>" + magnitude + "<br/>";
        str += "<b>" + r.getString(R.string.entry_RA) + r.getString(R.string.colon_with_spaces) + "</b>" + coord.getRaStr() + "<br/>";
        str += "<b>" + r.getString(R.string.entry_DE) + r.getString(R.string.colon_with_spaces) + "</b>" + coord.getDeStr();

        return new SpannableString(Html.fromHtml(str));
    }

    /**
     * Create the summary rich-text string (1 line)
     *
     * @param ctx
     * @return
     */
    @Override
    public Spannable createSummary(Context ctx) {
        Resources r = ctx.getResources();
        String str =  "<b>" + r.getString(R.string.entry_star) + "</b> " + r.getString(R.string.entry_mag) + "=" + magnitude;
        return new SpannableString(Html.fromHtml(str));
    }


    /**
     * Create the list of DSO entries
     *
     * @param context
     * @return
     */
    public static ArrayList<StarEntry> createList(Context context) {
        ArrayList<StarEntry> entries = new ArrayList<StarEntry>();

        // Open and read the catalog file
        try {
            final Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(resource);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), entryLength);
            char[] buf = new char[entryLength];

            while (reader.read(buf, 0, entryLength) > 0) {
                entries.add(new StarEntry(buf));
            }

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return entries;
    }

}
