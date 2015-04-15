package farom.iparcos.catalog;

import android.content.Context;
import android.content.res.Resources;
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
     * Return the object description
     * @return
     */
    public abstract String getDescription();

    /**
     * Return the object summary (1 line)
     * @return
     */
    public abstract String getSummary();


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


