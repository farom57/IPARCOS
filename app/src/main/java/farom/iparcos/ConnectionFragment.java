package farom.iparcos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * The main screen of the application, which manages the connection.
 *
 * @author Romain Fafet
 * @author SquareBoot
 */
public class ConnectionFragment extends Fragment {

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

    // Views
    private View rootView;
    private Button connectionButton;
    private Spinner serversSpinner;
    private LoadServersRunnable loadServersRunnable;
    /**
     * The original position of the floating action button.
     */
    private int fabPosY;

    /**
     * Asks the user to add a new server.
     */
    protected static void addServer(final Activity activity, final Runnable onServerReload) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.host_prompt_text);

        final EditText input = new EditText(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setCancelable(false)
                .setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String server = input.getText().toString();
                        if (!server.equals("")) {
                            // Retrieve the list
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                            Set<String> set = preferences.getStringSet("SERVER_SET", null);
                            List<String> serverList;
                            if (set != null) {
                                serverList = new Vector<>(set);

                            } else {
                                serverList = new Vector<>();
                            }
                            serverList.add(0, server);

                            // Save the list
                            Set<String> newSet = new HashSet<>();
                            newSet.addAll(serverList);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putStringSet("SERVER_SET", newSet);
                            editor.apply();

                            // Update
                            if (onServerReload != null) {
                                onServerReload.run();
                            }
                        }
                    }
                })
                .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        Dialog dialog = builder.create();
        try {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        } catch (NullPointerException ignored) {

        }
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            loadServersRunnable.run();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_connection, container, false);

        final ListView logsList = rootView.findViewById(R.id.logsList);
        final LogAdapter logAdapter = new LogAdapter(getContext(), logs);
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

        loadServersRunnable = new LoadServersRunnable();
        serversSpinner = rootView.findViewById(R.id.spinnerHost);
        serversSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                if (selected.equals(getResources().getString(R.string.host_add))) {
                    addServer(getActivity(), loadServersRunnable);

                } else if (selected.equals(getResources().getString(R.string.host_manage))) {
                    startActivityForResult(new Intent(getContext(), ServersActivity.class), 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadServersRunnable.run();

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
                        addServer(getActivity(), loadServersRunnable);

                    } else if (host.equals(getResources().getString(R.string.host_manage))) {
                        startActivityForResult(new Intent(getContext(), ServersActivity.class), 1);

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
    public void onDestroy() {
        super.onDestroy();
        buttonText = connectionButton.getText().toString();
        spinnerItem = serversSpinner.getSelectedItemPosition();
    }

    /**
     * Loads and updates the servers list.
     */
    protected class LoadServersRunnable implements Runnable {

        @Override
        public void run() {
            // Get the preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            Set<String> set = preferences.getStringSet("SERVER_SET", null);
            List<String> serversList;
            if (set != null) {
                serversList = new Vector<>(set);

            } else {
                serversList = new Vector<>();
            }
            // Update the display
            serversList.add(getResources().getString(R.string.host_add));
            serversList.add(getResources().getString(R.string.host_manage));
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, serversList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            serversSpinner.setAdapter(dataAdapter);
        }
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
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            holder.log.setText(item.getLog());
            holder.timestamp.setText(item.getTimestamp());
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

        LogItem(String log, String timestamp) {
            this.log = log;
            this.timestamp = timestamp;
        }

        String getLog() {
            return log;
        }

        void setLog(String log) {
            this.log = log;
        }

        String getTimestamp() {
            return timestamp;
        }

        void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}