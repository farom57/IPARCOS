package org.indilib.i4j.iparcos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import org.indilib.i4j.client.INDIDevice;
import org.indilib.i4j.client.INDIDeviceListener;
import org.indilib.i4j.client.INDIProperty;

import java.util.HashMap;
import java.util.List;

import org.indilib.i4j.iparcos.prop.PropPref;

/**
 * A placeholder fragment containing a simple view.
 */
public class PrefsFragment extends PreferenceFragmentCompat implements INDIDeviceListener {

    private INDIDevice device = null;
    private PreferenceScreen prefScreen;
    private HashMap<INDIProperty<?>, PropPref<?>> map;
    private HashMap<String, PreferenceCategory> groups;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
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
                List<INDIProperty<?>> props = device.getPropertiesOfGroup(group);
                if (props.size() > 0) {
                    PreferenceCategory prefGroup = new PreferenceCategory(context);
                    prefGroup.setIconSpaceReserved(false);
                    groups.put(group, prefGroup);
                    prefGroup.setTitle(group);
                    prefScreen.addPreference(prefGroup);
                    for (INDIProperty<?> prop : props) {
                        PropPref<?> pref = PropPref.create(context, prop);
                        if (pref != null) {
                            pref.setIconSpaceReserved(false);
                            map.put(prop, pref);
                            prefGroup.addPreference(pref);
                        }
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
    public void newProperty(INDIDevice device, final INDIProperty<?> property) {
        ((Activity) context).runOnUiThread(() -> {
            String group = property.getGroup();
            PreferenceCategory prefGroup = groups.get(group);
            if (prefGroup == null) {
                prefGroup = new PreferenceCategory(context);
                prefGroup.setIconSpaceReserved(false);
                groups.put(group, prefGroup);
                prefGroup.setTitle(group);
                prefScreen.addPreference(prefGroup);
            }
            PropPref<?> pref = PropPref.create(context, property);
            if (pref != null) {
                pref.setIconSpaceReserved(false);
                map.put(property, pref);
                prefGroup.addPreference(pref);
            }
        });
    }

    @Override
    public void removeProperty(INDIDevice device, INDIProperty<?> property) {
        PropPref<?> pref = map.get(property);
        if (pref != null) {
            String group = property.getGroup();
            PreferenceCategory prefGroup = groups.get(group);
            if (prefGroup != null) {
                prefGroup.removePreference(pref);
                if (prefGroup.getPreferenceCount() == 0) {
                    prefScreen.removePreference(prefGroup);
                    groups.remove(group);
                }
            }
            map.remove(property);
        }
    }
}