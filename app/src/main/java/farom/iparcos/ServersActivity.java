package farom.iparcos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Activity to manage the list of servers.
 *
 * @author SquareBoot
 */
public class ServersActivity extends AppCompatActivity implements ServersReloadListener {

    public static final String PREFERENCES_TAG = "SERVERS_LIST";
    private DragListView serversListView;

    /**
     * Asks the user to add a new server.
     */
    static void addServer(final Context context, final ServersReloadListener onServersReload) {
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        Dialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.host_prompt_text).setView(input).setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String server = input.getText().toString();
                        if (!server.equals("")) {
                            // Retrieve the list
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            Set<String> set = preferences.getStringSet(PREFERENCES_TAG, null);
                            List<String> serversList;
                            int max = -1;
                            if (set != null) {
                                serversList = new ArrayList<>(set);
                                for (String s : serversList) {
                                    max = Math.max(max, Integer.valueOf(s.substring(0, s.indexOf('#'))));
                                }

                            } else {
                                serversList = new ArrayList<>();
                            }
                            serversList.add((max + 1) + "#" + server);
                            // Save the list
                            Set<String> newSet = new HashSet<>();
                            newSet.addAll(serversList);
                            preferences.edit().putStringSet(PREFERENCES_TAG, newSet).apply();
                            // Update
                            onServersReload.loadServers();
                        }
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        dialog.show();
    }

    public static void sortPairs(List<Pair<Long, String>> list) {
        Collections.sort(list, new Comparator<Pair<Long, String>>() {
            @Override
            public int compare(Pair<Long, String> o1, Pair<Long, String> o2) {
                return (int) ((o1.first != null ? o1.first : 0) - (o2.first != null ? o2.first : 0));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

        findViewById(R.id.addServerFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                addServer(ServersActivity.this, ServersActivity.this);
            }
        });

        serversListView = findViewById(R.id.serversList);
        serversListView.setLayoutManager(new LinearLayoutManager(this));
        serversListView.setCanDragHorizontally(false);
        loadServers();

        Toast.makeText(this, R.string.servers_list_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    @SuppressLint("ApplySharedPref")
    @SuppressWarnings("unchecked")
    private void save() {
        // Save the new list
        List<Pair<Long, String>> list = serversListView.getAdapter().getItemList();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i).second;
            set.add(i + "#" + s);
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit().putStringSet(PREFERENCES_TAG, set).apply();
    }

    @Override
    public void loadServers() {
        Set<String> set = PreferenceManager.getDefaultSharedPreferences(ServersActivity.this)
                .getStringSet(PREFERENCES_TAG, null);
        ArrayList<Pair<Long, String>> serversList = new ArrayList<>();
        if (set != null) {
            for (String s : set) {
                int index = s.indexOf('#');
                serversList.add(new Pair<>(Long.valueOf(s.substring(0, index)), s.substring(index + 1)));
            }
            sortPairs(serversList);
        }
        serversListView.setAdapter(new ItemAdapter(serversList, R.layout.list_item, R.id.listview_drag, false) {
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
        }, false);
    }
}