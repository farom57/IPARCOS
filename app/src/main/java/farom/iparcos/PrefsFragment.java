package farom.iparcos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

import farom.iparcos.prop.PropPref;
import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIDeviceListener;
import laazotea.indi.client.INDIProperty;

/**
 * A placeholder fragment containing a simple view.
 */
public class PrefsFragment extends PreferenceFragmentCompat implements INDIDeviceListener {

    private INDIDevice device = null;
    private PreferenceScreen prefScreen;
    private HashMap<INDIProperty, PropPref> map;
    private HashMap<String, PreferenceCategory> groups;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.empty_preferences);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefScreen = getPreferenceScreen();
        if (device != null) {
            for (String group : device.getGroupNames()) {
                addPrefGroup(group);
            }
        }
    }

    public void setDevice(INDIDevice dev) {
        device = dev;
        device.addINDIDeviceListener(this);
        map = new HashMap<>();
        groups = new HashMap<>();
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        device.removeINDIDeviceListener(this);
    }

    private void addPrefGroup(String group) {
        ArrayList<INDIProperty> props = device.getPropertiesOfGroup(group);
        if (props.size() > 0) {
            PreferenceCategory prefGroup = new PreferenceCategory(getActivity());
            groups.put(group, prefGroup);
            prefGroup.setTitle(group);
            prefScreen.addPreference(prefGroup);
            for (INDIProperty prop : props) {
                PropPref pref = PropPref.create(getActivity(), prop);
                map.put(prop, pref);
                prefGroup.addPreference(pref);
            }
        }
    }

    @Override
    public void messageChanged(INDIDevice arg0) {

    }

    @Override
    public void newProperty(INDIDevice arg0, final INDIProperty prop) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
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
        });
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