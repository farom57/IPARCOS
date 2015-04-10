package farom.iparcos.catalog;

/**
 * An astronomical object
 */
public abstract class CatalogEntry {



    /**
     * Return the object coordinates
     * @return
     */
    public abstract Coordinates getCoordinates();


    /**
     * Return the object name
     * @return
     */
    public abstract String getName();


    /**
     * Return the object description
     * @return
     */
    public abstract String getDescription();


}
