package farom.astroid;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.os.Build;

public class ConnectionActivity extends Activity {

	private INDIAdapter indiAdapter;
	private List<String> serverList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_connection);

		serverList = new Vector<String>();
		serverList.add(getResources().getString(R.string.hostauto));
		serverList.add(getResources().getString(R.string.hostadd));
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				serverList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) findViewById(R.id.spinnerHost)).setAdapter(dataAdapter);

		indiAdapter = INDIAdapter.getInstance();
		indiAdapter.setLogView((TextView) findViewById(R.id.logTextBox));

		((ToggleButton) findViewById(R.id.connectionButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String host = String.valueOf(((Spinner) findViewById(R.id.spinnerHost)).getSelectedItem());
				String portStr = ((EditText) findViewById(R.id.editTextPort)).getText().toString();
				int port;
				try {
					port = Integer.parseInt(portStr);
				} catch (NumberFormatException e) {
					port = 7624;
				}

				indiAdapter.connect(host, port);
			}
		});

		((Spinner) findViewById(R.id.spinnerHost)).setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (parent.getItemAtPosition(pos).toString().equals(getResources().getString(R.string.hostadd))) {
					addServer();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

	}

	protected void addServer(String ip) {
		serverList.add(0,ip);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				serverList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) findViewById(R.id.spinnerHost)).setAdapter(dataAdapter);
	}

	protected void addServer() {
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.ip_prompt, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				addServer(userInput.getText().toString());
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connection, menu);
		return true;
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	// if (id == R.id.action_settings) {
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

}
