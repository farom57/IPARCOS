package farom.iparcos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.empty_preferences);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefScreen = getPreferenceScreen();
        if (device != null) {
            for (String group : device.getGroupNames()) {
                ArrayList<INDIProperty> props = device.getPropertiesOfGroup(group);
                if (props.size() > 0) {
                    PreferenceCategory prefGroup = new PreferenceCategory(context);
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
        }
    }

    public void setDevice(INDIDevice device) {
        this.device = device;
        this.device.addINDIDeviceListener(this);
        map = new HashMap<>();
        groups = new HashMap<>();
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        device.removeINDIDeviceListener(this);
    }

    @Override
    public void messageChanged(INDIDevice device) {

    }

    @Override
    public void newProperty(INDIDevice device, final INDIProperty property) {
        ((Activity) context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String group = property.getGroup();
                PreferenceCategory prefGroup = groups.get(group);
                if (prefGroup == null) {
                    prefGroup = new PreferenceCategory(context);
                    groups.put(group, prefGroup);
                    prefGroup.setTitle(group);
                    prefScreen.addPreference(prefGroup);
                }
                PropPref pref = PropPref.create(getActivity(), property);
                map.put(property, pref);
                prefGroup.addPreference(pref);
            }
        });
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty property) {
        PropPref pref = map.get(property);
        if (pref != null) {
            String group = property.getGroup();
            PreferenceCategory prefGroup = groups.get(group);
            prefGroup.removePreference(pref);
            if (prefGroup.getPreferenceCount() == 0) {
                prefScreen.removePreference(prefGroup);
                groups.remove(group);
            }
            map.remove(property);
        }
    }
}