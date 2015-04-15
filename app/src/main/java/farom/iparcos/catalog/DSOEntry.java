package farom.iparcos.catalog;

import android.content.Context;
import android.content.res.Resources;

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
     * Return the object description
     *
     * @return
     */
    @Override
    public String getDescription() {
        return type+", "+size+", "+magnitude;
    }

    /**
     * Return the object summary (1 line)
     *
     * @return
     */
    @Override
    public String getSummary() {
        return type+", "+size+", "+magnitude;
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
