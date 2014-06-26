package farom.astroid;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIDeviceListener;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

public class GenericActivity extends Activity implements INDIServerConnectionListener {

	private INDIAdapter indiAdapter;
	private PrefsFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		indiAdapter = INDIAdapter.getInstance();
		indiAdapter.registerPermanentConnectionListener(this);
		setContentView(R.layout.activity_generic);
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
	protected void onResume(){
		super.onResume();
		if ((fragment == null) && (indiAdapter.getDevices().size() > 0)) {
			fragment = new PrefsFragment();
			fragment.setDevice(indiAdapter.getDevices().get(0));
			getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if ((fragment != null)) {
			getFragmentManager().beginTransaction().remove(fragment).commit();
			fragment.finalize();
			fragment=null;
		}
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PrefsFragment extends PreferenceFragment implements INDIDeviceListener {
		private INDIAdapter indiAdapter = null;
		private INDIDevice device = null;
		private PreferenceScreen prefScreen;
		private HashMap<INDIProperty, PropPref> map;
		private HashMap<String, PreferenceCategory> groups;

		public void setDevice(INDIDevice dev) {
			indiAdapter = INDIAdapter.getInstance();
			device = dev;
			device.addINDIDeviceListener(this);
			map = new HashMap<INDIProperty, PropPref>();
			groups = new HashMap<String, PreferenceCategory>();
		}
		
		public void finalize(){
			device.removeINDIDeviceListener(this);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.empty_preferences);

		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			prefScreen = getPreferenceScreen();
			if (device != null) {
				for (Iterator<String> it = device.getGroupNames().iterator(); it.hasNext();) {
					String group = it.next();
					addPrefGroup(group);
				}
			}
		}

		private void addPrefGroup(String group) {
			ArrayList<INDIProperty> props = device.getPropertiesOfGroup(group);
			if (props.size() > 0) {
				PreferenceCategory prefGroup = new PreferenceCategory(getActivity());
				groups.put(group, prefGroup);
				prefGroup.setTitle(group);
				prefScreen.addPreference(prefGroup);
				for (Iterator<INDIProperty> it = props.iterator(); it.hasNext();) {
					INDIProperty prop = it.next();
					PropPref pref = new PropPref(getActivity(), prop);
					map.put(prop, pref);
					prefGroup.addPreference(pref);
				}
			}
		}

		@Override
		public void messageChanged(INDIDevice arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void newProperty(INDIDevice arg0, INDIProperty prop) {
			String group = prop.getGroup();
			PreferenceCategory prefGroup = groups.get(group);
			if (prefGroup == null) {
				prefGroup = new PreferenceCategory(getActivity());
				groups.put(group, prefGroup);
				prefGroup.setTitle(group);
				prefScreen.addPreference(prefGroup);
			}
			PropPref pref = new PropPref(getActivity(), prop);
			map.put(prop, pref);
			prefGroup.addPreference(pref);
		}

		@Override
		public void removeProperty(INDIDevice arg0, INDIProperty prop) {
			PropPref pref = map.get(prop);
			if (pref != null) {
				String group = prop.getGroup();
				PreferenceCategory prefGroup = groups.get(group);
				prefGroup.removePreference(pref);
				if (prefGroup.getPreferenceCount() == 0) {
					prefScreen.removePreference(prefGroup);
					groups.remove(group);
				}
				map.remove(prop);
			}
		}

	}

}
