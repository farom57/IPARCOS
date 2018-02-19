package farom.iparcos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Activity to manage the list of servers.
 *
 * @author SquareBoot
 */
public class ServersActivity extends AppCompatActivity {

    private FloatingActionButton addServerFab;
    private DynamicListView serversListView;
    /**
     * The original position of the floating action button.
     */
    private int fabPosY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);
        getSupportActionBar().setHomeButtonEnabled(true);

        final LoadServersRunnable loader = new LoadServersRunnable();

        addServerFab = findViewById(R.id.addServerFab);
        addServerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionFragment.addServer(ServersActivity.this, loader);
            }
        });
        fabPosY = addServerFab.getScrollY();

        serversListView = findViewById(R.id.serversList);
        serversListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= visibleItemCount) {
                    addServerFab.animate().cancel();
                    addServerFab.animate().translationYBy(150);

                } else {
                    addServerFab.animate().cancel();
                    addServerFab.animate().translationY(fabPosY);
                }
            }
        });
        serversListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        serversListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                serversListView.list.remove(position);
            }
        });
        loader.run();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Save the new list
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> newSet = new HashSet<>();
        newSet.addAll(serversListView.list);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("SERVER_SET", newSet);
        editor.apply();
    }

    /**
     * Loads and updates the servers list.
     */
    protected class LoadServersRunnable implements Runnable {

        @Override
        public void run() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ServersActivity.this);
            Set<String> set = preferences.getStringSet("SERVER_SET", null);
            List<String> serversList;
            if (set != null) {
                serversList = new Vector<>(set);

            } else {
                serversList = new Vector<>();
            }
            StableArrayAdapter adapter = new StableArrayAdapter(ServersActivity.this, android.R.layout.simple_list_item_1, serversList);
            serversListView.setList(serversList);
            serversListView.setAdapter(adapter);
        }
    }
}