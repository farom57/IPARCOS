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
import android.util.Log;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIDeviceListener;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;

/**
 * The main activity of the application, which manages the connection.
 *
 * @author Romain Fafet
 */
public class ConnectionFragment extends Fragment {

    private static ConnectionFragment instance = null;

    // Views
    private View rootView;
    private TextView logView;
    private Button connectionButton;

    /**
     * @return the instance of the activity
     */
    public static ConnectionFragment getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;

        rootView = inflater.inflate(R.layout.activity_connection, container, false);

        loadServerList();

        logView = rootView.findViewById(R.id.logTextBox); // TODO : correct
        logView.setMovementMethod(new ScrollingMovementMethod());

        connectionButton = rootView.findViewById(R.id.connectionButton);

        ((Spinner) rootView.findViewById(R.id.spinnerHost)).setOnItemSelectedListener(new OnItemSelectedListener() {
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

        rootView.findViewById(R.id.connectionButton).setOnClickListener(new View.OnClickListener() {
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

                // Connect (or disconnect)
                if (connectionButton.getText().equals(getResources().getString(R.string.connect))) {
                    if (host.equals(getResources().getString(R.string.hostadd))) {
                        addServer();

                    } else {
                        connect(host, port);
                    }

                } else if (connectionButton.getText().equals(getResources().getString(R.string.disconnect))) {
                    disconnect();
                }
            }
        });

        return rootView;
    }

    /**
     * load and update the server list
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
        ((Spinner) rootView.findViewById(R.id.spinnerHost)).setAdapter(dataAdapter);
    }

    /**
     * Add the server address, save the server list and update the spinner
     *
     * @param ip
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
     * Ask to the user to add a new server
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

    /**
     * Connect to the driver
     *
     * @param host
     * @param port
     */
    private void connect(java.lang.String host, int port) {
        connectionButton.setText(R.string.connecting);
        appendLog(getString(R.string.try_to_connect) + host + ":" + port);
        connection = new INDIServerConnection(host, port);

        // Listen to all
        connection.addINDIServerConnectionListener(this);
        for (INDIServerConnectionListener permanentConnectionListener : permanentConnectionListeners) {
            connection.addINDIServerConnectionListener(permanentConnectionListener);
        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    connection.connect();
                    // Ask for all the devices
                    connection.askForDevices();
                    appendLog(getString(R.string.connected));
                    connectionButton.post(new Runnable() {
                        public void run() {
                            connectionButton.setText(R.string.disconnect);
                            //TODO invalidateOptionsMenu();
                        }
                    });

                } catch (IOException e) {
                    appendLog(getString(R.string.connection_pb));
                    appendLog(e.getLocalizedMessage());
                    connectionButton.post(new Runnable() {
                        public void run() {
                            connectionButton.setText(R.string.connect);
                        }
                    });
                }

            }
        }).start();
    }
}