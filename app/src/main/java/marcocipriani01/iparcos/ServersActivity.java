package marcocipriani01.iparcos;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Activity to manage the list of servers.
 *
 * @author marcocipriani01
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
                .setPositiveButton(context.getString(R.string.ok), (dialog12, id) -> {
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
                                max = Math.max(max, Integer.parseInt(s.substring(0, s.indexOf('#'))));
                            }

                        } else {
                            serversList = new ArrayList<>();
                        }
                        serversList.add(0, (max + 1) + "#" + server);
                        // Save the list
                        Set<String> newSet = new HashSet<>(serversList);
                        preferences.edit().putStringSet(PREFERENCES_TAG, newSet).apply();
                        // Update
                        onServersReload.loadServers();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), (dialog1, id) -> dialog1.cancel()).create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        dialog.show();
    }

    public static void sortPairs(List<Pair<Long, String>> list) {
        Collections.sort(list, (o1, o2) -> (int) ((o1.first != null ? o1.first : 0) - (o2.first != null ? o2.first : 0)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

        findViewById(R.id.addServerFab).setOnClickListener(v -> {
            save();
            addServer(ServersActivity.this, ServersActivity.this);
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

    private void save() {
        // Save the new list
        List<Pair<Long, String>> list = ((ItemAdapter) serversListView.getAdapter()).getItemList();
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
            public void onItemLongClicked(final TextView view) {
                new AlertDialog.Builder(ServersActivity.this)
                        .setTitle(R.string.sure)
                        .setMessage(R.string.remove_server)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                            List<Pair<Long, String>> list = getItemList();
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).second == view.getText()) {
                                    removeItem(i);
                                    break;
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, id) -> dialog.cancel())
                        .create().show();
            }
        }, false);
    }
}