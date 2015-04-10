package farom.iparcos.catalog;

import android.content.Context;

import farom.iparcos.catalog.CatalogEntry;

/**
 * A catalog of astronomical objcets
 */
public abstract class Catalog {

    //
    // Fields
    //

    /**
     * Size of the entry in the catalog text file (not counting the ne line character)
     */
    protected int entryLength;
    /**
     * The maximum size of the name in the catalog
     */
    protected int entryNameLength;
    /**
     * Resource id of the catalog text file
     */
    protected int catalogRawResource;
    /**
     * Application context to access the resources
     */
    protected Context context;


    //
    // Constructors
    //

    /**
     * Constructor
     * @param entryLength   Size of the entry in the catalog text file (not counting the ne line character)
     * @param entryNameLength   The maximum size of the name in the catalog
     * @param catalogRawResource    Resource id of the catalog text file
     * @param context   Application context to access the resources
     */
    public Catalog (int entryLength, int entryNameLength, int catalogRawResource, Context context) {
        this.entryLength=entryLength;
        this.entryNameLength=entryNameLength;
        this.catalogRawResource=catalogRawResource;
        this.context=context;
    }

    //
    // Methods
    //


    /**
     * Search objects in the catalog whose first characters matches the query. Return
     * the entry indexes.
     * @return       int [*]
     * @param        query
     */
    public int[] search(String query)
    {
        // TODO
        return null;
    }


    /**
     * Return the i-th entry in the catalog
     * @return       CatalogEntry
     * @param        i
     */
    public CatalogEntry getEntry(int i)
    {
        // TODO
        return null;
    }


    /**
     * @return       CatalogEntry
     * @param        entryRawData The raw data of the entry in the catalog (the line in
     * the catalog file)
     */
    protected abstract CatalogEntry readEntry(char[] entryRawData);


}
