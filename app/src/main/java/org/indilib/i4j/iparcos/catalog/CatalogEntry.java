package org.indilib.i4j.iparcos.catalog;

import android.content.Context;
import android.text.Spannable;

import androidx.annotation.NonNull;

/**
 * An abstract astronomical object.
 */
public abstract class CatalogEntry implements Comparable<CatalogEntry> {

    /**
     * His coordinates.
     */
    protected Coordinates coord;
    /**
     * His name.
     */
    protected String name;

    /**
     * @return the stored coordinates.
     */
    public Coordinates getCoordinates() {
        return coord;
    }

    /**
     * @return the object's name.
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
    public int compareTo(@NonNull CatalogEntry another) {
        return this.getName().compareToIgnoreCase(another.getName());
    }
}