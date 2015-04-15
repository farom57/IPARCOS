package farom.iparcos.catalog;

import android.content.Context;
import android.content.res.Resources;

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
    protected String ra;
    protected String de;

    /**
     * Create the entry from a formated line
     * (ie. "ALGOL                 ALGOL; BET PER; HD19356; SAO38592                               03 08 10.131 +40 57 20.43    2.1")
     *
     * @param buf
     */
    public StarEntry(char[] buf) { // TODO change implementation -> store the complete char[] and parse it only when needed
        String data = String.valueOf(buf);

        int i = 0;

        name = data.substring(i, i + nameLength).trim();
        i += nameLength;

        names = data.substring(i, i + namesLength).trim();
        i += namesLength;

        ra = data.substring(i, i + raLength).trim();
        i += raLength;

        de = data.substring(i, i + deLength).trim();
        i += deLength;

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
        return null;
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
     * Return the object description
     *
     * @return
     */
    @Override
    public String getDescription() {
        return magnitude + " " + names;
    }

    /**
     * Return the object summary (1 line)
     *
     * @return
     */
    @Override
    public String getSummary() {
        return magnitude + " " + names;
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
