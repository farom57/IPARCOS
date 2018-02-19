package farom.iparcos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import farom.iparcos.catalog.Catalog;
import farom.iparcos.catalog.CatalogEntry;
import farom.iparcos.catalog.Coordinates;
import laazotea.indi.Constants;
import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIDeviceListener;
import laazotea.indi.client.INDINumberElement;
import laazotea.indi.client.INDINumberProperty;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIPropertyListener;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;
import laazotea.indi.client.INDISwitchElement;
import laazotea.indi.client.INDISwitchProperty;

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
    // INDI properties
    private INDINumberProperty telescopeCoordP = null;
    private INDINumberElement telescopeCoordRA = null;
    private INDINumberElement telescopeCoordDE = null;
    private INDISwitchProperty telescopeOnCoordSetP = null;
    private INDISwitchElement telescopeOnCoordSetSync = null;
    private INDISwitchElement telescopeOnCoordSetSlew = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getString(R.string.empty_catalog));

        setHasOptionsMenu(true);

        if ((catalog == null) || (!catalog.isReady())) {
            catalogEntries = new ArrayList<>();
            entriesAdapter = new ArrayAdapter<CatalogEntry>(getContext(),
                    android.R.layout.simple_list_item_2, android.R.id.text1, catalogEntries) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ((TextView) view.findViewById(android.R.id.text1))
                            .setText(catalogEntries.get(position).getName());
                    ((TextView) view.findViewById(android.R.id.text2))
                            .setText(catalogEntries.get(position).createSummary(getContext()));
                    return view;
                }
            };
            setListAdapter(entriesAdapter);
            // List loading
            setListShown(false);
            getLoaderManager().initLoader(0, null, this).forceLoad();

        } else {
            setListAdapter(entriesAdapter);
        }

        // Set up INDI connection
        ConnectionManager connectionManager = Application.getConnectionManager();
        connectionManager.registerPermanentConnectionListener(this);

        // Enumerate existing properties
        INDIServerConnection connection = connectionManager.getConnection();
        if (connection != null) {
            List<INDIDevice> list = connection.getDevicesAsList();
            if (list != null) {
                for (INDIDevice device : list) {
                    device.addINDIDeviceListener(this);
                    List<INDIProperty> properties = device.getPropertiesAsList();
                    for (INDIProperty property : properties) {
                        this.newProperty(device, property);
                    }
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        System.out.println("Creating menu");
        MenuItem item = menu.add(R.string.menu_search);
        item.setIcon(R.drawable.ic_action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        SearchView searchView = new SearchView(getActivity());
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
        if ((catalog != null) && (catalog.isReady())) {
            setSelection(catalog.searchIndex(newText));
        }
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Context ctx = l.getContext();
        final Coordinates coord = catalogEntries.get(position).getCoordinates();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(catalogEntries.get(position).createDescription(getContext()))
                .setTitle(catalogEntries.get(position).getName());

        // Only display buttons if the telescope is ready
        if (telescopeCoordP != null && telescopeOnCoordSetP != null) {
            builder.setPositiveButton(R.string.GOTO, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        telescopeOnCoordSetSlew.setDesiredValue(Constants.SwitchStatus.ON);
                        telescopeOnCoordSetSync.setDesiredValue(Constants.SwitchStatus.OFF);
                        telescopeOnCoordSetP.sendChangesToDriver();
                        telescopeCoordRA.setDesiredValue(coord.getRaStr());
                        telescopeCoordDE.setDesiredValue(coord.getDeStr());
                        telescopeCoordP.sendChangesToDriver();
                        Toast toast = Toast.makeText(ctx, ctx.getString(R.string.slew_ok), Toast.LENGTH_LONG);
                        toast.show();

                    } catch (Exception e) {
                        Toast toast = Toast.makeText(ctx, ctx.getString(R.string.sync_slew_error), Toast.LENGTH_LONG);
                        toast.show();
                    }

                }
            });
            builder.setNeutralButton(R.string.sync, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        telescopeOnCoordSetSync.setDesiredValue(Constants.SwitchStatus.ON);
                        telescopeOnCoordSetSlew.setDesiredValue(Constants.SwitchStatus.OFF);
                        telescopeOnCoordSetP.sendChangesToDriver();
                        telescopeCoordRA.setDesiredValue(coord.getRaStr());
                        telescopeCoordDE.setDesiredValue(coord.getDeStr());
                        telescopeCoordP.sendChangesToDriver();
                        Toast toast = Toast.makeText(ctx, ctx.getString(R.string.sync_ok), Toast.LENGTH_LONG);
                        toast.show();

                    } catch (Exception e) {
                        Toast toast = Toast.makeText(ctx, ctx.getString(R.string.sync_slew_error), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        }

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * @return a catalog loader.
     * @see CatalogLoader
     */
    public Loader<Catalog> onCreateLoader(int id, Bundle args) {
        return new CatalogLoader(getContext());
    }

    /**
     * Binds the given catalog, loaded using {@link CatalogLoader}, to the Fragment's ListView.
     *
     * @param loader the loader. Ignored.
     * @param data   the new catalog.
     */
    public void onLoadFinished(Loader<Catalog> loader, Catalog data) {
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
        Log.i("CatalogManager", "Catalog binded.");
    }

    /**
     * Here the application should remove any references it has to the {@link CatalogLoader}'s data.
     *
     * @param loader the loader to unbind.
     */
    public void onLoaderReset(Loader<Catalog> loader) {

    }

    @Override
    public void newProperty(INDIDevice device, INDIProperty property) {
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

            if (telescopeCoordDE != null && telescopeCoordRA != null) {
                property.addINDIPropertyListener(this);
                telescopeCoordP = (INDINumberProperty) property;
                Log.i("SearchFragment", "New Property (" + property.getName() + ") added to device " + device.getName());

            } else {
                Log.w("SearchFragment", "Bad property: " + property.getName() + ", device: " + device.getName());
            }
        }
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty property) {
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
    public void propertyChanged(INDIProperty property) {

    }

    @Override
    public void newDevice(INDIServerConnection connection, INDIDevice device) {
        // We just simply listen to this Device
        Log.i("SearchFragment", getString(R.string.new_device) + device.getName());
        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        // We just remove ourselves as a listener of the removed device
        Log.i("SearchFragment", getString(R.string.device_removed) + device.getName());
        device.removeINDIDeviceListener(this);
    }

    @Override
    public void connectionLost(INDIServerConnection connection) {
        telescopeCoordP = null;
        telescopeCoordRA = null;
        telescopeCoordDE = null;
        telescopeOnCoordSetP = null;
        telescopeOnCoordSetSlew = null;
        telescopeOnCoordSetSync = null;
    }

    @Override
    public void newMessage(INDIServerConnection connection, Date timestamp, String message) {

    }

    /**
     * Catalog loader.
     *
     * @author SquareBoot
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