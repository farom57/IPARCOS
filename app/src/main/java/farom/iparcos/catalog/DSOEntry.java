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
import java.util.List;

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

    protected String name;
    protected String type;
    protected String size;
    protected String magnitude;
    protected String ra;
    protected String de;


    /**
     * Create the entry from a formated line
     * (ie. "Dumbbell nebula          8 Pl 15.2 19 59 36.1+22 43 00")
     * @param buf
     */
    public DSOEntry(char[] buf){
        String data = String.valueOf(buf);

        int i=0;

        name = data.substring(i, i+nameLength).trim();
        i+=nameLength;

        magnitude = data.substring(i, i+magnitudeLength).trim();
        i+=magnitudeLength;

        type = data.substring(i, i+typeLength).trim();
        i+=typeLength;

        size = data.substring(i, i+sizeLength).trim();
        i+=sizeLength;

        ra = data.substring(i, i+raLength).trim();
        i+=raLength;

        de = data.substring(i, i+deLength).trim();
    }

    /**
     * Return the object coordinates     *
     * @return
     */
    @Override
    public Coordinates getCoordinates() {
        return null;
    }

    /**
     * Return the string resource which correspond to the type acronym
     * @return
     */
    private int getTypeStringResource(){
        if(type.equals("Gx")) {
            return R.string.entry_Gx;
        }else if(type.equals("OC")) {
            return R.string.entry_OC;
        }else if(type.equals("Gb")) {
            return R.string.entry_Gb;
        }else if(type.equals("Nb")) {
            return R.string.entry_Nb;
        }else if(type.equals("Pl")) {
            return R.string.entry_Pl;
        }else if(type.equals("C+N")) {
            return R.string.entry_CplusN;
        }else if(type.equals("Ast")) {
            return R.string.entry_Ast;
        }else if(type.equals("Kt")) {
            return R.string.entry_Kt;
        }else if(type.equals("***")) {
            return R.string.entry_triStar;
        }else if(type.equals("D*")) {
            return R.string.entry_doubleStar;
        }else if(type.equals("*")) {
            return R.string.entry_star;
        }else if(type.equals("?")) {
            return R.string.entry_uncertain;
        }else if(type.equals("")) {
            return R.string.entry_blank;
        }else if(type.equals("-")) {
            return R.string.entry_minus;
        }else if(type.equals("PD")) {
            return R.string.entry_PD;
        }else{
            return R.string.entry_blank;
        }
    }

    /**
     * Return the string resource which correspond to the type acronym
     * @return
     */
    private int getTypeShortStringResource(){
        if(type.equals("Gx")) {
            return R.string.entry_short_Gx;
        }else if(type.equals("OC")) {
            return R.string.entry_short_OC;
        }else if(type.equals("Gb")) {
            return R.string.entry_short_Gb;
        }else if(type.equals("Nb")) {
            return R.string.entry_short_Nb;
        }else if(type.equals("Pl")) {
            return R.string.entry_short_Pl;
        }else if(type.equals("C+N")) {
            return R.string.entry_short_CplusN;
        }else if(type.equals("Ast")) {
            return R.string.entry_short_Ast;
        }else if(type.equals("Kt")) {
            return R.string.entry_short_Kt;
        }else if(type.equals("***")) {
            return R.string.entry_short_triStar;
        }else if(type.equals("D*")) {
            return R.string.entry_short_doubleStar;
        }else if(type.equals("*")) {
            return R.string.entry_short_star;
        }else if(type.equals("?")) {
            return R.string.entry_short_uncertain;
        }else if(type.equals("")) {
            return R.string.entry_short_blank;
        }else if(type.equals("-")) {
            return R.string.entry_short_minus;
        }else if(type.equals("PD")) {
            return R.string.entry_short_PD;
        }else{
            return R.string.entry_short_blank;
        }
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
        String str = "<b>" + r.getString(R.string.entry_type) + r.getString(R.string.colon_with_spaces) + "</b>" + r.getString(getTypeStringResource()) + "<br/>";
        if(!magnitude.equals("")){
            str += "<b>" + r.getString(R.string.entry_magnitude) + r.getString(R.string.colon_with_spaces) + "</b>" + magnitude + "<br/>";
        }
        if(!size.equals("")){
            str += "<b>" + r.getString(R.string.entry_size) + r.getString(R.string.colon_with_spaces) + "</b>" + size + " " + r.getString(R.string.arcmin) + "<br/>";
        }
        str += "<b>" + r.getString(R.string.entry_RA) + r.getString(R.string.colon_with_spaces) + "</b>" + ra + "<br/>";
        str += "<b>" + r.getString(R.string.entry_DE) + r.getString(R.string.colon_with_spaces) + "</b>" + de;
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
        String str =  "<b>" + r.getString(getTypeShortStringResource()) + "</b> ";
        if(!magnitude.equals("")){
            str += r.getString(R.string.entry_mag) + "=" + magnitude + " ";
        }
        if(!size.equals("")){
            str += r.getString(R.string.entry_size).toLowerCase() + "=" + size  + r.getString(R.string.arcmin);
        }
        return new SpannableString(Html.fromHtml(str));
    }

    /**
     * Return the object name
     * @return
     */
    @Override
    public String getName(){
        return name;
    }

    /**
     * Create the list of DSO entries
     * @param context
     * @return
     */
    public static ArrayList<DSOEntry> createList(Context context){
        ArrayList<DSOEntry> entries = new ArrayList<DSOEntry>();

        // Open and read the catalog file
        try{
            final Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(resource);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream),entryLength);
            char[] buf = new char[entryLength];

            while(reader.read(buf,0,entryLength)>0){
                entries.add(new DSOEntry(buf));
            }

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return entries;
    }


}
