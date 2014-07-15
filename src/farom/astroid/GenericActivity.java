package farom.astroid;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.os.Build;
import android.preference.SwitchPreference;

public class GenericActivity extends Activity implements TabListener, INDIServerConnectionListener {

	/**
	 * The active fragment
	 */
	private PrefsFragment fragment = null;

	/**
	 * Retains the association between the tab and the device
	 */
	private HashMap<Tab, INDIDevice> tabDeviceMap;


	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ConnectionActivity.getInstance().registerPermanentConnectionListener(this);

		setContentView(R.layout.activity_generic);
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);

		tabDeviceMap = new HashMap<ActionBar.Tab, INDIDevice>();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.global, menu);

		// hide the item for the current activity
		MenuItem genericItem = menu.findItem(R.id.menu_generic);
		genericItem.setVisible(false);

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		INDIServerConnection connection = ConnectionActivity.getConnection();
		if(connection==null){
			return;
		}
		
		List<INDIDevice> list = connection.getDevicesAsList();
		if(list==null){
			return;
		}
		
		if (list.size() > 0) {

			// Recreate tabs
			for (Iterator<INDIDevice> it = list.iterator(); it.hasNext();) {
				INDIDevice device = it.next();

				Tab tab = actionBar.newTab();

				tabDeviceMap.put(tab, device);

				tab.setText(device.getName());
				tab.setTabListener(this);
				actionBar.addTab(tab);

			}

		}

	}

	@Override
	protected void onPause() {
		super.onPause();

		// Remove the tabs
		actionBar.removeAllTabs();
		tabDeviceMap.clear();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * open the motion activity,
	 * 
	 * @param v
	 */
	public boolean openMotionActivity(MenuItem v) {
		Intent intent = new Intent(this, MotionActivity.class);
		startActivity(intent);
		return true;
	}

	/**
	 * open the settings activity
	 * 
	 * @param v
	 * @return
	 */
	public boolean openSettingsActivity(MenuItem v) {
		// TODO
		return false;
	}

	/**
	 * open the generic activity
	 * 
	 * @param v
	 * @return
	 */
	public boolean openGenericActivity(MenuItem v) {
		// nothing to do, already the current activity
		return false;
	}

	/**
	 * open the connection activity
	 * 
	 * @param v
	 * @return
	 */
	public boolean openConnectionActivity(MenuItem v) {
		Intent intent = new Intent(this, ConnectionActivity.class);
		startActivity(intent);
		return true;
	}

	@Override
	public void connectionLost(INDIServerConnection arg0) {
		openConnectionActivity(null);
	}

	@Override
	public void newDevice(INDIServerConnection arg0, INDIDevice arg1) {

	}

	@Override
	public void newMessage(INDIServerConnection arg0, Date arg1, String arg2) {

	}

	@Override
	public void removeDevice(INDIServerConnection arg0, INDIDevice arg1) {
		openConnectionActivity(null);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// User selected the already selected tab. Usually do nothing.
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// Check if the fragment is already initialized
		if (fragment == null) {
			fragment = new PrefsFragment();
			fragment.setDevice(tabDeviceMap.get(tab));
			ft.add(android.R.id.content, fragment);
		}else{
			Log.e("GenericActivity","error : fragment!=null");
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (fragment != null) {
			// Detach the fragment, and delete it because an other will be
			// created
			ft.detach(fragment);
			try {
				fragment.finalize();
			} catch (Throwable e) {
				Log.e("GenericActivity","error fragment.finalize() : "+e.getMessage());
			}
			fragment=null;
			
		}else{
			Log.e("GenericActivity","error : fragment==null");
		}
	}

}
