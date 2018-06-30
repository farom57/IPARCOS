package farom.iparcos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static farom.iparcos.ServersActivity.PREFERENCES_TAG;

/**
 * The main screen of the application, which manages the connection.
 *
 * @author Romain Fafet
 * @author SquareBoot
 */
public class ConnectionFragment extends Fragment implements ServersReloadListener {

    /**
     * All the logs.
     */
    private final static ArrayList<LogItem> logs = new ArrayList<>();
    /**
     * The last text of the button (to restore the Fragment's state)
     */
    private static String buttonText = null;
    /**
     * The last position of the spinner (to restore the Fragment's state)
     */
    private static int spinnerItem = -1;
    private Context context;
    // Views
    private View rootView;
    private Button connectionButton;
    private Spinner serversSpinner;
    /**
     * The original position of the floating action button.
     */
    private int fabPosY;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            loadServers();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_connection, container, false);

        final ListView logsList = rootView.findViewById(R.id.logsList);
        final LogAdapter logAdapter = new LogAdapter(context, logs);
        logsList.setAdapter(logAdapter);

        final FloatingActionButton clearLogsButton = rootView.findViewById(R.id.clearLogsButton);
        clearLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logs.clear();
                logAdapter.notifyDataSetChanged();
                clearLogsButton.animate().translationY(250);
            }
        });

        fabPosY = clearLogsButton.getScrollY();

        logsList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= visibleItemCount) {
                    clearLogsButton.animate().cancel();
                    clearLogsButton.animate().translationYBy(250);

                } else {
                    clearLogsButton.animate().cancel();
                    clearLogsButton.animate().translationY(fabPosY);
                }
            }
        });

        connectionButton = rootView.findViewById(R.id.connectionButton);

        serversSpinner = rootView.findViewById(R.id.spinnerHost);
        serversSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                if (selected.equals(getResources().getString(R.string.host_add))) {
                    ServersActivity.addServer(context, ConnectionFragment.this);

                } else if (selected.equals(getResources().getString(R.string.host_manage))) {
                    startActivityForResult(new Intent(context, ServersActivity.class), 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadServers();

        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve Hostname and port number
                String host = String.valueOf(((Spinner) rootView.findViewById(R.id.spinnerHost)).getSelectedItem());
                String portStr = ((EditText) rootView.findViewById(R.id.editTextPort)).getText().toString();
                int port;
                try {
                    port = Integer.parseInt(portStr);

                } catch (NumberFormatException e) {
                    port = 7624;
                }

                // Connect or disconnect
                if (connectionButton.getText().equals(getResources().getString(R.string.connect))) {
                    if (host.equals(getResources().getString(R.string.host_add))) {
                        ServersActivity.addServer(context, ConnectionFragment.this);

                    } else if (host.equals(getResources().getString(R.string.host_manage))) {
                        startActivityForResult(new Intent(context, ServersActivity.class), 1);

                    } else {
                        Application.getConnectionManager().connect(host, port);
                    }

                } else if (connectionButton.getText().equals(getResources().getString(R.string.disconnect))) {
                    Application.getConnectionManager().disconnect();
                }
            }
        });

        if (spinnerItem != -1) {
            serversSpinner.setSelection(spinnerItem);
            connectionButton.setText(buttonText);

        } else {
            spinnerItem = 0;
            buttonText = getResources().getString(R.string.connect);
        }

        Application.setUiUpdater(new Application.UIUpdater() {
            @Override
            public void appendLog(final String msg, final String timestamp) {
                logsList.post(new Runnable() {
                    @Override
                    public void run() {
                        logs.add(new LogItem(msg, timestamp));
                        logAdapter.notifyDataSetChanged();
                        if (logs.size() == 1) {
                            clearLogsButton.animate().cancel();
                            clearLogsButton.animate().translationY(fabPosY);
                        }
                    }
                });
            }

            @Override
            public void setConnectionState(final String state) {
                connectionButton.post(new Runnable() {
                    @Override
                    public void run() {
                        connectionButton.setText(state);
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        buttonText = connectionButton.getText().toString();
        spinnerItem = serversSpinner.getSelectedItemPosition();
    }

    @Override
    public void loadServers() {
        Set<String> set = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(PREFERENCES_TAG, null);
        List<String> serversList = new ArrayList<>();
        if (set != null) {
            ArrayList<Pair<Long, String>> pairsList = new ArrayList<>();
            for (String s : set) {
                int index = s.indexOf('#');
                pairsList.add(new Pair<>(Long.valueOf(s.substring(0, index)), s.substring(index + 1)));
            }
            ServersActivity.sortPairs(pairsList);
            for (Pair<Long, String> pair : pairsList) {
                serversList.add(pair.second);
            }
        }
        serversList.add(getResources().getString(R.string.host_add));
        serversList.add(getResources().getString(R.string.host_manage));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, serversList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serversSpinner.setAdapter(dataAdapter);
    }

    /**
     * {@code ArrayAdapter} for logs.
     *
     * @author SquareBoot
     */
    private class LogAdapter extends ArrayAdapter<LogItem> {

        LayoutInflater inflater;

        LogAdapter(Context context, List<LogItem> objects) {
            super(context, android.R.layout.simple_list_item_2, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
                holder = new ViewHolder();
                holder.log = convertView.findViewById(android.R.id.text1);
                holder.timestamp = convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LogItem item = getItem(position);
            if (item != null) {
                holder.log.setText(item.getLog());
                holder.timestamp.setText(item.getTimestamp());
            }
            return convertView;
        }
    }

    /**
     * {@code ViewHolder} for the {@code ListView} that stores logs.
     *
     * @author SquareBoot
     */
    private class ViewHolder {
        TextView log, timestamp;
    }

    /**
     * Represents a single log with its timestamp.
     *
     * @author SquareBoot
     */
    private class LogItem {

        private String log;
        private String timestamp;

        /**
         * Class constructor.
         */
        LogItem(@NonNull String log, @NonNull String timestamp) {
            this.log = log;
            this.timestamp = timestamp;
        }

        /**
         * @return the log text.
         */
        String getLog() {
            return log;
        }

        /**
         * @return the timestamp string.
         */
        String getTimestamp() {
            return timestamp;
        }
    }
}