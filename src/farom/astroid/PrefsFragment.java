package farom.astroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIDeviceListener;
import laazotea.indi.client.INDIProperty;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;

/**
 * A placeholder fragment containing a simple view.
 */
public class PrefsFragment extends PreferenceFragment implements INDIDeviceListener {
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

	public void finalize() throws Throwable {
		super.finalize();
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
				PropPref pref = PropPref.create(getActivity(), prop);
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
		PropPref pref = PropPref.create(getActivity(), prop);
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