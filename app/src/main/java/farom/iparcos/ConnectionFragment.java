package farom.iparcos;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * The main activity of the application, which manages the connection.
 *
 * @author Romain Fafet
 */
public class ConnectionFragment extends Fragment {

    private static boolean restore = false;
    private static String logsText;
    private static String buttonText;

    // Views
    private View rootView;
    private Button connectionButton;
    private TextView logView;
    private Spinner serversSpinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_connection, container, false);

        logView = rootView.findViewById(R.id.logTextBox);

        connectionButton = rootView.findViewById(R.id.connectionButton);

        serversSpinner = rootView.findViewById(R.id.spinnerHost);
        serversSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (parent.getItemAtPosition(pos).toString().equals(getResources().getString(R.string.hostadd))) {
                    addServer();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadServerList();

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
                    if (host.equals(getResources().getString(R.string.hostadd))) {
                        addServer();

                    } else {
                        Application.getConnectionManager().connect(host, port);
                    }

                } else if (connectionButton.getText().equals(getResources().getString(R.string.disconnect))) {
                    Application.getConnectionManager().disconnect();
                }
            }
        });

        if (restore) {
            logView.setText(logsText);
            connectionButton.setText(buttonText);

        } else {
            logsText = "";
            buttonText = getResources().getString(R.string.connect);
        }

        Application.setUiUpdater(new Application.UIUpdater() {
            @Override
            public void appendLog(final String msg) {
                logView.post(new Runnable() {
                    public void run() {
                        logView.append(msg);
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
        restore = true;
        logsText = logView.getText().toString();
        buttonText = connectionButton.getText().toString();
    }

    /**
     * load and update the servers list.
     */
    protected void loadServerList() {
        // Get the preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> set = preferences.getStringSet("SERVER_SET", null);
        List<String> serverList;
        if (set != null) {
            serverList = new Vector<>(set);

        } else {
            serverList = new Vector<>();
        }
        // Update the display
        serverList.add(getResources().getString(R.string.hostadd));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, serverList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serversSpinner.setAdapter(dataAdapter);
    }

    /**
     * Adds the server address, saves the servers list and updates the spinner.
     *
     * @param ip the IP address of the new Server
     */
    protected void addServer(String ip) {
        // Retrieve the list
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> set = preferences.getStringSet("SERVER_SET", null);
        List<String> serverList;
        if (set != null) {
            serverList = new Vector<>(set);

        } else {
            serverList = new Vector<>();
        }
        serverList.add(0, ip);

        // Save the list
        Set<String> newSet = new HashSet<>();
        newSet.addAll(serverList);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("SERVER_SET", newSet);
        editor.apply();

        // Update
        loadServerList();
    }

    /**
     * Asks the user to add a new server.
     */
    @SuppressLint("InflateParams")
    protected void addServer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.host_prompt_text);

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String server = input.getText().toString();
                        if (!server.equals("")) {
                            addServer(server);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }
}