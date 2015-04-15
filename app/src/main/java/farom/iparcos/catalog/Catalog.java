package farom.iparcos.catalog;


import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A catalog of astronomical objects
 */
public class Catalog {

    /**
     * Application context to access the resources
     */
    protected Context context;

    /**
     * Catalog objects
     */
    protected ArrayList<CatalogEntry> entries;



    private boolean ready = false;

    /**
     * Constructor
     *
     * @param context Application context to access the resources
     */
    public Catalog(Context context) {
        this.context=context;
        init();

    }

    private void init() {
        entries = new ArrayList<CatalogEntry>(DSOEntry.createList(context));
        entries.addAll(StarEntry.createList(context));
        Collections.sort(entries);
        ready = true;
    }

    /**
     * @return true if the catalog is fully initialized
     */
    public boolean isReady(){
        return ready;
    }

    public ArrayList<CatalogEntry> getEntries(){
        if(isReady()){
            return entries;
        }else{
            return null;
        }
    }

    public int searchIndex(final String query){

        CatalogEntry fakeEntry = new CatalogEntry() {
            @Override
            public Coordinates getCoordinates() {
                return null;
            }

            @Override
            public String getName() {
                return query;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public String getSummary() {
                return null;
            }
        };
        int index = Collections.binarySearch(entries,fakeEntry);

        if(index<0){
            index = -index - 1;
        }

        return index;
    }
}


