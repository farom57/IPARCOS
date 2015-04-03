package farom.iparcos;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Created by farom on 08/02/15.
 */
public class Catalog {
    ArrayList<SkyObjectKeyRef> catalog;
    private final Context mHelperContext;
    private boolean ready = false;

    public Catalog(Context context){
        mHelperContext = context;
        loadCatalog();
    }

    /**
     * Starts a thread to load the database table with words
     */
    private void loadCatalog() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadSkyObjects();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * Read the catalog
     */
    private void loadSkyObjects() throws IOException{
        Log.d("CATALOG", "Loading objects...");
        final Resources resources = mHelperContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.catalog);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        catalog = new ArrayList<SkyObjectKeyRef>();
        int nb_objects=0;
        int nb_identifiers=0;
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                SkyObject obj = SkyObject.fromLine(line);
                if(obj!=null) {
                    String[] ids = obj.getIdentifiers();
                    nb_objects++;
                    for (int i = 1; i < ids.length; i++) {
                        catalog.add(new SkyObjectKeyRef(ids[i], obj));
                        nb_identifiers++;
                    }
                }
            }
        } finally {
            reader.close();
        }
        Log.d("CATALOG", "DONE loading objects.");
        Log.d("CATALOG", "" + nb_identifiers + " identifiers in " + nb_objects + " objects.");

        Collections.sort(catalog);

        Log.d("CATALOG", "DONE sorting catalog");

        ready = true;
    }


    /**
     * Search the identifiers which start with the given string
     * @param query
     * @return a list of matching objects
     */
    public ArrayList<SkyObject> search(String query){
        query=query.replaceAll("\\s","").toLowerCase();
        Log.d("CATALOG", "Searching for " + query);



        Comparator<SkyObjectKeyRef> comparator = new Comparator<SkyObjectKeyRef>() {
            public int compare(SkyObjectKeyRef currentItem, SkyObjectKeyRef key) {
                return currentItem.identifier.compareTo(key.identifier);
            }
        };

        int idx = Arrays.binarySearch(catalog.toArray(), new SkyObjectKeyRef(query,null), null);
        idx = Math.abs(idx);

        Log.d("CATALOG", "Must be around idx=" + idx + " - " + catalog.get(idx).identifier);

        for(int i=idx-10; i<idx+10; i++){
            Log.d("CATALOG", "i=" + i + " - " + catalog.get(i).identifier);
        }

        ArrayList<SkyObject> result = new ArrayList<SkyObject>();
        int nb_results = 0;
        while(catalog.get(idx+nb_results).identifier.toLowerCase().startsWith(query)){
            result.add(catalog.get(idx).object);
            Log.d("CATALOG", nb_results+" ("+(idx+nb_results)+"): " +catalog.get(idx+nb_results).identifier+".");
            nb_results++;

        }


        return null;
    }

    public boolean isReady() {
        return ready;
    }

    public class SkyObjectKeyRef implements Comparable{

        public String identifier;
        public SkyObject object;

        public SkyObjectKeyRef(String identifier2, SkyObject object2){
            identifier=identifier2;
            object=object2;
        }

        @Override
        public int compareTo(Object another) {
            return this.identifier.compareToIgnoreCase(((SkyObjectKeyRef)another).identifier);
        }
    }
}
