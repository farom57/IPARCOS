package farom.iparcos;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Activity to manage the list of servers.
 *
 * @author SquareBoot
 */
public class ServersActivity extends AppCompatActivity {

    private DragListView serversListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);
        getSupportActionBar().setHomeButtonEnabled(true);

        final LoadServersRunnable loader = new LoadServersRunnable();

        findViewById(R.id.addServerFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionFragment.addServer(ServersActivity.this, loader);
            }
        });

        serversListView = findViewById(R.id.serversList);
        serversListView.setLayoutManager(new LinearLayoutManager(this));
        serversListView.setCanDragHorizontally(false);
        loader.run();

        Toast.makeText(this, R.string.servers_list_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onDestroy() {
        super.onDestroy();

        // Save the new list
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> newSet = new HashSet<>();
        List<Pair<Long, String>> list = serversListView.getAdapter().getItemList();
        for (Pair<Long, String> pair : list) {
            newSet.add(pair.second);
        }
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
            ArrayList<Pair<Long, String>> serversList;
            if (set != null) {
                serversList = new ArrayList<>();
                String[] strings = set.toArray(new String[set.size()]);
                for (int i = 0; i < set.size(); i++) {
                    serversList.add(new Pair<>((long) i, strings[i]));
                }

            } else {
                serversList = new ArrayList<>();
            }
            final ItemAdapter listAdapter = new ItemAdapter(serversList, R.layout.list_item, R.id.listview_drag, false) {
                @Override
                public void onItemClicked(TextView view) {

                }

                @Override
                @SuppressWarnings("unchecked")
                public void onItemLongClicked(final TextView view) {
                    new AlertDialog.Builder(ServersActivity.this)
                            .setTitle(R.string.sure)
                            .setMessage(R.string.remove_server)
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DragItemAdapter adapter = serversListView.getAdapter();
                                    List<Pair<Long, String>> list = adapter.getItemList();
                                    for (int i = 0; i < list.size(); i++) {
                                        if (list.get(i).second == view.getText()) {
                                            adapter.removeItem(i);
                                        }
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .create().show();
                }
            };
            serversListView.setAdapter(listAdapter, false);
        }
    }
}