package farom.iparcos.catalog;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * An astronomical object
 */
public abstract class CatalogEntry implements Comparable<CatalogEntry> {



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
     * Create the description rich-text string
     * @return
     */
    public abstract Spannable createDescription(Context ctx);

    /**
     * Create the summary rich-text string (1 line)
     * @return
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
        return getName().compareToIgnoreCase(another.getName());
    }
}


