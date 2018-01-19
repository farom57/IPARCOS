package farom.iparcos.catalog;

import android.content.Context;
import android.text.Spannable;


/**
 * An astronomical object
 */
public abstract class CatalogEntry implements Comparable<CatalogEntry> {

    protected Coordinates coord;
    protected String name;

    /**
     * Return the object coordinates
     *
     * @return coordinates
     */
    public Coordinates getCoordinates() {
        return coord;
    }

    /**
     * Return the object name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Create the description rich-text string
     *
     * @param ctx Context (to access resource strings)
     * @return description Spannable
     */
    public abstract Spannable createDescription(Context ctx);

    /**
     * Create the summary rich-text string (1 line)
     *
     * @param ctx Context (to access resource strings)
     * @return summary Spannable
     */
    public abstract Spannable createSummary(Context ctx);

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(CatalogEntry another) {
        return this.getName().compareToIgnoreCase(another.getName());
    }
}