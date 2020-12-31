package org.indilib.i4j.iparcos;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import org.indilib.i4j.Constants;
import org.indilib.i4j.client.INDIDevice;
import org.indilib.i4j.client.INDIDeviceListener;
import org.indilib.i4j.client.INDINumberElement;
import org.indilib.i4j.client.INDINumberProperty;
import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDIPropertyListener;
import org.indilib.i4j.client.INDIServerConnection;
import org.indilib.i4j.client.INDIServerConnectionListener;
import org.indilib.i4j.client.INDISwitchElement;
import org.indilib.i4j.client.INDISwitchProperty;
import org.indilib.i4j.client.INDIValueException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.indilib.i4j.iparcos.catalog.Catalog;
import org.indilib.i4j.iparcos.catalog.CatalogEntry;
import org.indilib.i4j.iparcos.catalog.Coordinates;
import org.indilib.i4j.iparcos.prop.PropUpdater;

/**
 * Allows the user to look for an astronomical object and slew the telescope.
 */
public class SearchFragment extends ListFragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Catalog>,
        INDIServerConnectionListener, INDIPropertyListener, INDIDeviceListener {

    // ListView stuff
    private static ArrayList<CatalogEntry> catalogEntries = new ArrayList<>();
    private static ArrayAdapter<CatalogEntry> entriesAdapter;
    private static Catalog catalog;
    private ConnectionManager connectionManager;
    // INDI properties
    private INDINumberProperty telescopeCoordP = null;
    private INDINumberElement telescopeCoordRA = null;
    private INDINumberElement telescopeCoordDE = null;
    private INDISwitchProperty telescopeOnCoordSetP = null;
    private INDISwitchElement telescopeOnCoordSetSync = null;
    private INDISwitchElement telescopeOnCoordSetSlew = null;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Enumerate existing properties
        if (connectionManager.isConnected()) {
            List<INDIDevice> list = connectionManager.getConnection().getDevicesAsList();
            if (list != null) {
                for (INDIDevice device : list) {
                    device.addINDIDeviceListener(this);
                    for (INDIProperty<?> property : device.getPropertiesAsList()) {
                        newProperty(device, property);
                    }
                }
            }

        } else {
            clearVars();
        }
    }

    private void clearVars() {
        telescopeCoordP = null;
        telescopeCoordRA = null;
        telescopeCoordDE = null;
        telescopeOnCoordSetP = null;
        telescopeOnCoordSetSlew = null;
        telescopeOnCoordSetSync = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.empty_catalog));
        setHasOptionsMenu(true);

        if (catalog == null) {
            catalogEntries = new ArrayList<>();
            entriesAdapter = new ArrayAdapter<CatalogEntry>(context,
                    android.R.layout.simple_list_item_2, android.R.id.text1, catalogEntries) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ((TextView) view.findViewById(android.R.id.text1))
                            .setText(catalogEntries.get(position).getName());
                    ((TextView) view.findViewById(android.R.id.text2))
                            .setText(catalogEntries.get(position).createSummary(context));
                    return view;
                }
            };
            setListAdapter(entriesAdapter);
            // List loading
            setListShown(false);
            LoaderManager.getInstance(this).initLoader(0, null, this).forceLoad();

        } else {
            setListAdapter(entriesAdapter);
        }

        // Set up INDI connection
        connectionManager = IPARCOSApp.getConnectionManager();
        connectionManager.addListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        MenuItem item = menu.add(R.string.menu_search);
        item.setIcon(R.drawable.search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = new SearchView(context);
        searchView.setOnQueryTextListener(this);
        item.setActionView(searchView);
    }

    /**
     * Called when the user changes the search string.
     *
     * @param newText the new query.
     * @return {@code false}, because the action is being handled by this listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (catalog != null) setSelection(catalog.searchIndex(newText));
        return false;
    }

    /**
     * Called when the user submits the query. Nothing to do since it is done in {@link #onQueryTextChange(String)}
     *
     * @param query the new query. Ignored.
     * @return always {@code false}.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onListItemClick(ListView l, @NonNull View v, int position, long id) {
        final Context context = l.getContext();
        final Coordinates coord = catalogEntries.get(position).getCoordinates();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(catalogEntries.get(position).createDescription(context)).setTitle(catalogEntries.get(position).getName());
        // Only display buttons if the telescope is ready
        if (telescopeCoordP != null && telescopeOnCoordSetP != null) {
            builder.setPositiveButton(R.string.GOTO, (dialog, which) -> {
                try {
                    telescopeOnCoordSetSlew.setDesiredValue(Constants.SwitchStatus.ON);
                    telescopeOnCoordSetSync.setDesiredValue(Constants.SwitchStatus.OFF);
                    new PropUpdater(telescopeOnCoordSetP).start();
                    telescopeCoordRA.setDesiredValue(coord.getRaStr());
                    telescopeCoordDE.setDesiredValue(coord.getDeStr());
                    new PropUpdater(telescopeCoordP).start();
                    Toast.makeText(context, context.getString(R.string.slew_ok), Toast.LENGTH_LONG).show();

                } catch (INDIValueException e) {
                    Toast.makeText(context, context.getString(R.string.sync_slew_error), Toast.LENGTH_LONG).show();
                }
            });
            builder.setNeutralButton(R.string.sync, (dialog, which) -> {
                try {
                    telescopeOnCoordSetSync.setDesiredValue(Constants.SwitchStatus.ON);
                    telescopeOnCoordSetSlew.setDesiredValue(Constants.SwitchStatus.OFF);
                    new PropUpdater(telescopeOnCoordSetP).start();
                    telescopeCoordRA.setDesiredValue(coord.getRaStr());
                    telescopeCoordDE.setDesiredValue(coord.getDeStr());
                    new PropUpdater(telescopeCoordP).start();
                    Toast toast = Toast.makeText(context, context.getString(R.string.sync_ok), Toast.LENGTH_LONG);
                    toast.show();

                } catch (INDIValueException e) {
                    Toast toast = Toast.makeText(context, context.getString(R.string.sync_slew_error), Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {

        });
        builder.create().show();
    }

    /**
     * @return a catalog loader.
     * @see CatalogLoader
     */
    @NonNull
    public Loader<Catalog> onCreateLoader(int id, Bundle args) {
        return new CatalogLoader(context);
    }

    /**
     * Binds the given catalog, loaded using {@link CatalogLoader}, to the Fragment's ListView.
     *
     * @param loader the loader. Ignored.
     * @param data   the new catalog.
     */
    public void onLoadFinished(@NonNull Loader<Catalog> loader, Catalog data) {
        Log.i("CatalogManager", "Catalog loaded. Binding data...");
        catalog = data;
        if (catalogEntries.size() != 0) {
            catalogEntries.clear();
        }
        catalogEntries.addAll(catalog.getEntries());
        entriesAdapter.notifyDataSetChanged();
        if (isResumed()) {
            setListShown(true);

        } else {
            setListShownNoAnimation(true);
        }
        Log.i("CatalogManager", "Catalog bound.");
    }

    /**
     * Here the application should remove any references it has to the {@link CatalogLoader}'s data.
     *
     * @param loader the loader to unbind.
     */
    public void onLoaderReset(@NonNull Loader<Catalog> loader) {

    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty<?> property) {
        // Look for properties
        if (property.getName().equals("ON_COORD_SET")) {
            telescopeOnCoordSetSlew = (INDISwitchElement) property.getElement("TRACK");
            if (telescopeOnCoordSetSlew == null) {
                telescopeOnCoordSetSync = (INDISwitchElement) property.getElement("SLEW");
            }
            telescopeOnCoordSetSync = (INDISwitchElement) property.getElement("SYNC");

            if (telescopeOnCoordSetSlew != null && telescopeOnCoordSetSync != null) {
                property.addINDIPropertyListener(this);
                telescopeOnCoordSetP = (INDISwitchProperty) property;
                Log.i("SearchFragment", "New Property (" + property.getName() + ") added to device " + device.getName());

            } else {
                Log.w("SearchFragment", "Bad property: " + property.getName() + ", device: " + device.getName());
            }
        }

        if (property.getName().equals("EQUATORIAL_COORD") || property.getName().equals("EQUATORIAL_EOD_COORD")) {
            telescopeCoordRA = (INDINumberElement) property.getElement("RA");
            telescopeCoordDE = (INDINumberElement) property.getElement("DEC");

            if (telescopeCoordDE != null && telescopeCoordRA != null && property instanceof INDINumberProperty) {
                property.addINDIPropertyListener(this);
                telescopeCoordP = (INDINumberProperty) property;
                Log.i("SearchFragment", "New Property (" + property.getName() + ") added to device " + device.getName());

            } else {
                Log.w("SearchFragment", "Bad property: " + property.getName() + ", device: " + device.getName());
            }
        }
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty<?> property) {
        if (property.getName().equals("ON_COORD_SET")) {
            telescopeCoordP = null;
            telescopeCoordRA = null;
            telescopeCoordDE = null;
        }
        if (property.getName().equals("EQUATORIAL_COORD")) {
            telescopeOnCoordSetP = null;
            telescopeOnCoordSetSlew = null;
            telescopeOnCoordSetSync = null;
        }
        Log.d("SearchFragment", "Removed property (" + property.getName() + ") to device " + device.getName());
    }

    @Override
    public void messageChanged(INDIDevice device) {

    }

    @Override
    public void propertyChanged(INDIProperty<?> property) {

    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        Log.i("SearchFragment", "New device: " + device.getName());
        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        Log.i("SearchFragment", "Device removed: " + device.getName());
        device.removeINDIDeviceListener(this);
    }

    @Override
    public void connectionLost(INDIServerConnection connection) {
        clearVars();
    }

    @Override
    public void newMessage(INDIServerConnection connection, Date timestamp, String message) {

    }

    /**
     * Catalog loader.
     *
     * @author marcocipriani01
     */
    private static class CatalogLoader extends AsyncTaskLoader<Catalog> {

        /**
         * Class constructor.
         *
         * @param context the context that will be used to fetch entries.
         */
        CatalogLoader(@NonNull Context context) {
            super(context);
        }

        /**
         * Calls {@link #CatalogLoader(Context)} to load the entire catalog.
         *
         * @return the complete catalog.
         */
        @Nullable
        @Override
        public Catalog loadInBackground() {
            Log.i("CatalogManager", "Loading catalog...");
            return new Catalog(getContext());
        }
    }
}