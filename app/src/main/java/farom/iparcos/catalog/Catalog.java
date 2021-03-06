package farom.iparcos.catalog;

import android.content.Context;
import android.text.Spannable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A catalog of astronomical objects.
 *
 * @see DSOEntry
 * @see StarEntry
 */
public class Catalog {

    /**
     * Catalog objects.
     */
    private ArrayList<CatalogEntry> entries;
    /**
     * {@code true} if the catalog is fully initialized.
     */
    private boolean ready = false;

    /**
     * Class constructor. Loads the catalog from the resources and initializes it.
     *
     * @param context Application context to access the resources
     */
    public Catalog(Context context) {
        Log.i("CatalogManager", "Loading DSO...");
        entries = new ArrayList<CatalogEntry>(DSOEntry.createList(context));
        Log.i("CatalogManager", "Loading stars...");
        entries.addAll(StarEntry.createList(context));
        Collections.sort(entries);
        ready = true;
    }

    /**
     * @return {@code true} if the catalog is fully initialized.
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * @return an {@link ArrayList} containing all the entries of this catalog.
     */
    public ArrayList<CatalogEntry> getEntries() {
        if (isReady()) {
            return entries;

        } else {
            return null;
        }
    }

    /**
     * Performs a search in the entries.
     *
     * @param query what to look for.
     * @return the first index corresponding to the given query.
     */
    public int searchIndex(final String query) {
        int index = Collections.binarySearch(entries, new CatalogEntry() {
            @Override
            public Coordinates getCoordinates() {
                return null;
            }

            @Override
            public String getName() {
                return query;
            }

            @Override
            public Spannable createDescription(Context ctx) {
                return null;
            }

            @Override
            public Spannable createSummary(Context ctx) {
                return null;
            }
        });
        if (index < 0) {
            index = -index - 1;
        }
        return index;
    }
}