package farom.iparcos;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.SearchManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import farom.iparcos.catalog.Catalog;
import farom.iparcos.catalog.CatalogEntry;


/**
 * Allow the user to search for an astronomical object and display the result.
 */
public class SearchActivity extends ListActivity implements MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    ArrayAdapter<CatalogEntry> adapter;
    private ArrayList<CatalogEntry> entries;
    private Catalog catalog;

    /**
     * Called at the activity creation. Disable opening animation and load default content.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        entries = new ArrayList<CatalogEntry>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, entries) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(entries.get(position).getName());
                text2.setText(entries.get(position).createSummary(getContext()));
                return view;
            }
        };
        setListAdapter(adapter);


        final Context act = this;
        new Thread(new Runnable(){
            @Override
            public void run() {
                catalog = new Catalog(act);
                entries.addAll(catalog.getEntries());

            }
        }).start(); // TODO : faire plus propre avec Cursor et Loader

        getListView().setOnItemClickListener(this);
    }

    /**
     * perform the search
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
        if(catalog!=null){
            if(catalog.isReady()) {
                getListView().setSelection(catalog.searchIndex(query));
            }
        }
    }

    /**
     * Initiate the menu (only the search view in fact).
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
        searchItem.expandActionView();              // force expand the view at the beginning (the searchView.setIconifiedByDefault is not working)
        searchItem.setOnActionExpandListener(this); // recover the collapse event to quit the activity

        return true;
    }

    /**
     * (from OnActionExpandListener) Called when the search menu is expanded. It can only happens at the menu initialisation. Nothing to do.
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    /**
     * (from OnActionExpandListener) Called when the user closes the search menu. It shall kill the activity.
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
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage(entries.get(position).createDescription(this))
                .setTitle(entries.get(position).getName());
        builder.setPositiveButton(R.string.GOTO,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNeutralButton(R.string.sync,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}


