package farom.iparcos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.SearchManager;
import android.content.Intent;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * Allow the user to search for an astronomical object and display the result.
 */
public class SearchActivity extends Activity implements MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener {


    /**
     * Called at the activity creation. Disable opening animation and load default content.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_search);

    }

    /**
     * perform the search
     * @param query
     */
    private void doMySearch(String query) {
//        while(catalog==null){}
//        while(!catalog.isReady()){}
//        catalog.search(query);
        TextView text = (TextView) findViewById(R.id.textViewtest);
        text.setText(query);
        Log.d("GLOBALLOG", "Search for " + query);
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
}


