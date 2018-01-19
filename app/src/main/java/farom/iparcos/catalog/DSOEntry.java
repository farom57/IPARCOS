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
 * Deep sky object
 */
public class DSOEntry extends CatalogEntry {

    private final static int resource = R.raw.ngc_ic_b;
    private final static int entryLength = 56;
    private final static int nameLength = 25;
    private final static int magnitudeLength = 2;
    private final static int typeLength = 3;
    private final static int sizeLength = 5;
    private final static int raLength = 10;
    private final static int deLength = 10;

    protected String type;
    protected String size;
    protected String magnitude;

    /**
     * Create the entry from a formatted line
     * (ie. "Dumbbell nebula          8 Pl 15.2 19 59 36.1+22 43 00")
     *
     * @param buf formatted line
     */
    public DSOEntry(char[] buf) {
        String data = String.valueOf(buf);

        int i = 0;

        name = data.substring(i, i + nameLength).trim();
        i += nameLength;

        magnitude = data.substring(i, i + magnitudeLength).trim();
        i += magnitudeLength;

        type = data.substring(i, i + typeLength).trim();
        i += typeLength;

        size = data.substring(i, i + sizeLength).trim();
        i += sizeLength;

        String ra_str = data.substring(i, i + raLength).trim();
        i += raLength + 1;

        String de_str = data.substring(i, i + deLength).trim();

        coord = new Coordinates(ra_str, de_str);
    }

    /**
     * Create the list of DSO entries
     *
     * @param context Context to access the catalog file
     * @return A list of stars
     */
    public static ArrayList<DSOEntry> createList(Context context) {
        ArrayList<DSOEntry> entries = new ArrayList<DSOEntry>();

        // Open and read the catalog file
        try {
            final Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(resource);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), entryLength);
            char[] buf = new char[entryLength];

            while (reader.read(buf, 0, entryLength) > 0) {
                entries.add(new DSOEntry(buf));
            }

            inputStream.close();

        } catch (IOException e) {
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
        if (type.equals("Gx")) {
            return R.string.entry_Gx;

        } else if (type.equals("OC")) {
            return R.string.entry_OC;

        } else if (type.equals("Gb")) {
            return R.string.entry_Gb;

        } else if (type.equals("Nb")) {
            return R.string.entry_Nb;

        } else if (type.equals("Pl")) {
            return R.string.entry_Pl;

        } else if (type.equals("C+N")) {
            return R.string.entry_CplusN;

        } else if (type.equals("Ast")) {
            return R.string.entry_Ast;

        } else if (type.equals("Kt")) {
            return R.string.entry_Kt;

        } else if (type.equals("***")) {
            return R.string.entry_triStar;

        } else if (type.equals("D*")) {
            return R.string.entry_doubleStar;

        } else if (type.equals("*")) {
            return R.string.entry_star;

        } else if (type.equals("?")) {
            return R.string.entry_uncertain;

        } else if (type.equals("")) {
            return R.string.entry_blank;

        } else if (type.equals("-")) {
            return R.string.entry_minus;

        } else if (type.equals("PD")) {
            return R.string.entry_PD;

        } else {
            return R.string.entry_blank;
        }
    }

    /**
     * Return the string resource which correspond to the type acronym
     *
     * @return short text
     */
    private int getTypeShortStringResource() {
        if (type.equals("Gx")) {
            return R.string.entry_short_Gx;

        } else if (type.equals("OC")) {
            return R.string.entry_short_OC;

        } else if (type.equals("Gb")) {
            return R.string.entry_short_Gb;

        } else if (type.equals("Nb")) {
            return R.string.entry_short_Nb;

        } else if (type.equals("Pl")) {
            return R.string.entry_short_Pl;

        } else if (type.equals("C+N")) {
            return R.string.entry_short_CplusN;

        } else if (type.equals("Ast")) {
            return R.string.entry_short_Ast;

        } else if (type.equals("Kt")) {
            return R.string.entry_short_Kt;

        } else if (type.equals("***")) {
            return R.string.entry_short_triStar;

        } else if (type.equals("D*")) {
            return R.string.entry_short_doubleStar;

        } else if (type.equals("*")) {
            return R.string.entry_short_star;

        } else if (type.equals("?")) {
            return R.string.entry_short_uncertain;

        } else if (type.equals("")) {
            return R.string.entry_short_blank;

        } else if (type.equals("-")) {
            return R.string.entry_short_minus;

        } else if (type.equals("PD")) {
            return R.string.entry_short_PD;

        } else {
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
            str += r.getString(R.string.entry_mag) + "=" + magnitude + " ";
        }
        if (!size.equals("")) {
            str += r.getString(R.string.entry_size).toLowerCase() + "=" + size + r.getString(R.string.arcmin);
        }
        return new SpannableString(Html.fromHtml(str));
    }
}