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


public class SearchActivity extends Activity implements MenuItem.OnActionExpandListener {
    private String query;
    private MenuItem search_item;
    private SearchView searchView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);

    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
            if(search_item!=null){
                search_item.expandActionView();
                searchView.setQuery(query,false);
            }
        }
    }



    private void doMySearch(String query) {
//        while(catalog==null){}
//        while(!catalog.isReady()){}
//        catalog.search(query);
        TextView text=(TextView)findViewById(R.id.textViewtest);
        text.setText(query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        search_item=menu.findItem(R.id.menu_search);
        search_item.expandActionView();
        searchView.setQuery(query,false);
        search_item.setOnActionExpandListener(this);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        TextView text = (TextView) findViewById(R.id.textViewtest);
        text.setText("Collapse");
        Log.d("GLOBALLOG", "Collapse");
        finish();
        return true;
    }
}
