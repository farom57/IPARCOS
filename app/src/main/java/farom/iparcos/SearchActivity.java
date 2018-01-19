package farom.iparcos;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
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
 * Allows the user to search for an astronomical object and displays the result.
 */
public class SearchActivity extends ListActivity
        implements MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener,
        AdapterView.OnItemClickListener, INDIServerConnectionListener, INDIPropertyListener, INDIDeviceListener {

    ArrayAdapter adapter;
    private ArrayList<CatalogEntry> entries;
    private Catalog catalog;

    // INDI properties
    private INDINumberProperty telescopeCoordP = null;
    private INDINumberElement telescopeCoordRA = null;
    private INDINumberElement telescopeCoordDE = null;
    private INDISwitchProperty telescopeOnCoordSetP = null;
    private INDISwitchElement telescopeOnCoordSetSync = null;
    private INDISwitchElement telescopeOnCoordSetSlew = null;

    /**
     * Called at the activity creation. Disable opening animation and load default content.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        // list setup
        entries = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, entries) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text1.setText(entries.get(position).getName());
                text2.setText(entries.get(position).createSummary(getContext()));
                return view;
            }
        };
        setListAdapter(adapter);

        // List loading
        final Context act = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                catalog = new Catalog(act);
                entries.addAll(catalog.getEntries());

            }
        }).start(); // TODO : faire plus propre avec Cursor et Loader

        // Set up INDI connection
        ConnectionActivity.getInstance().registerPermanentConnectionListener(this);

        // Enumerate existing properties
        INDIServerConnection connection = ConnectionActivity.getConnection();
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

        getListView().setOnItemClickListener(this);
    }

    /**
     * perform the search
     *
     * @param query
     */
    private void doMySearch(String query) {
//        TextView text = (TextView) findViewById(R.id.textViewtest);
//        text.setText(query);
//        Log.d("GLOBALLOG", "Search for " + query);
//        entries.clear();
//        entries.addAll(dsoCatalog.search(query));
//        entries.add(new DSOEntry("48 Tuc                   4 Gb 30.9 00 24 06.1-72 05 00X"));
//        Log.d("GLOBALLOG", "entries.size() = " + entries.size());
//        adapter.notifyDataSetChanged();
        if (catalog != null) {
            if (catalog.isReady()) {
                getListView().setSelection(catalog.searchIndex(query));
            }
        }
    }

    /**
     * Initiate the menu (only the search view in fact).
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);    // expand the view at the beginning
        searchView.setSubmitButtonEnabled(false);   // disable the submit button (the search is done every time the user change the text in the searchView)
        searchView.setOnQueryTextListener(this);    // to recover text change

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        // Force expand the view at the beginning (searchView.setIconifiedByDefault is not working)
        searchItem.expandActionView();
        // Recover the collapse event to quit the activity
        searchItem.setOnActionExpandListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * (from OnActionExpandListener) Called when the search menu is expanded. It can only happens at the menu initialisation. Nothing to do.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    /**
     * (from OnActionExpandListener) Called when the user closes the search menu. It shall kill the activity.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        finish();
        overridePendingTransition(0, 0); // Disable the swipe animation for the activity end.
        return true;
    }

    /**
     * (from OnQueryTextListener) Called when the user changes the search string
     *
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        doMySearch(newText);
        return false;
    }

    /**
     * (from OnQueryTextListener) Called when the user submits the query. Nothing to do since it is done in onQueryTextChange
     *
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Context ctx = view.getContext();
        final Coordinates coord = entries.get(position).getCoordinates();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(entries.get(position).createDescription(this))
                .setTitle(entries.get(position).getName());

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
                Log.i("SearchActivity", "New Property (" + property.getName() + ") added to device " + device.getName());

            } else {
                Log.w("SearchActivity", "Bad property: " + property.getName() + ", device: " + device.getName());
            }
        }

        if (property.getName().equals("EQUATORIAL_COORD") || property.getName().equals("EQUATORIAL_EOD_COORD")) {
            telescopeCoordRA = (INDINumberElement) property.getElement("RA");
            telescopeCoordDE = (INDINumberElement) property.getElement("DEC");

            if (telescopeCoordDE != null && telescopeCoordRA != null) {
                property.addINDIPropertyListener(this);
                telescopeCoordP = (INDINumberProperty) property;
                Log.i("SearchActivity", "New Property (" + property.getName() + ") added to device " + device.getName());
            } else {
                Log.w("SearchActivity", "Bad property: " + property.getName() + ", device: " + device.getName());
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
        Log.d("SearchActivity", "Removed property (" + property.getName() + ") to device " + device.getName());
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
        Log.i("SearchActivity", getString(R.string.new_device) + device.getName());
        device.addINDIDeviceListener(this);
    }

    @Override
    public void removeDevice(INDIServerConnection connection, INDIDevice device) {
        // We just remove ourselves as a listener of the removed device
        Log.i("SearchActivity", getString(R.string.device_removed) + device.getName());
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
}