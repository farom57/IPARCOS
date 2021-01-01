package org.indilib.i4j.iparcos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.indilib.i4j.client.INDIDevice;
import org.indilib.i4j.client.INDIServerConnection;
import org.indilib.i4j.client.INDIServerConnectionListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControlPanelFragment extends Fragment implements TabLayout.OnTabSelectedListener, INDIServerConnectionListener {

    /**
     * Manages the connection with the INDI server.
     *
     * @see ConnectionManager
     */
    private ConnectionManager connectionManager;
    // Views
    private ViewPager viewPager;
    private DevicesPagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    /**
     * Used to create an unique tag for each tab.
     */
    private int c = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control_panel, container, false);

        connectionManager = IPARCOSApp.getConnectionManager();
        connectionManager.addListener(this);

        tabLayout = rootView.findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = rootView.findViewById(R.id.pager);
        pagerAdapter = new DevicesPagerAdapter(getChildFragmentManager(), new ArrayList<>());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!connectionManager.isConnected()) {
            removeAllTabs();
            pagerAdapter.add(new SimplePageDescriptor("NoDevices" + c, getString(R.string.error_no_devices)));
            return;
        }
        List<INDIDevice> list = connectionManager.getConnection().getDevicesAsList();
        if ((list == null) || (list.size() == 0)) {
            removeAllTabs();
            pagerAdapter.add(new SimplePageDescriptor("NoDevices" + c, getString(R.string.error_no_devices)));
            return;
        }
        // Create / recreate tabs
        for (INDIDevice device : list) {
            pagerAdapter.add(new DevicePageDescriptor(device));
            tabLayout.addTab(tabLayout.newTab().setText(device.getName()));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        removeAllTabs();
    }

    private void removeAllTabs() {
        tabLayout.removeAllTabs();
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            pagerAdapter.remove(i);
        }
        pagerAdapter = new DevicesPagerAdapter(getChildFragmentManager(), new ArrayList<>());
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionManager.removeListener(this);
    }

    @Override
    public void connectionLost(INDIServerConnection arg0) {
        // Move to the connection tab
        IPARCOSApp.goToConnectionTab();
    }

    @Override
    public void newDevice(INDIServerConnection arg0, INDIDevice arg1) {

    }

    @Override
    public void newMessage(INDIServerConnection arg0, Date arg1, String arg2) {

    }

    @Override
    public void removeDevice(INDIServerConnection arg0, INDIDevice arg1) {
        // Move to the connection tab
        IPARCOSApp.goToConnectionTab();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    /**
     * @author marcocipriani01
     */
    public static class NoDevicesFragment extends Fragment {
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.no_devices_fragment, container, false);
        }
    }

    /**
     * Page adapter. Creates the {@link PrefsFragment} corresponding to the specified {@link PageDescriptor}.
     *
     * @author marcocipriani01
     */
    private class DevicesPagerAdapter extends ArrayPagerAdapter<Fragment> {

        private DevicesPagerAdapter(FragmentManager fragmentManager, List<PageDescriptor> descriptors) {
            super(fragmentManager, descriptors);
        }

        @Override
        protected Fragment createFragment(PageDescriptor desc) {
            if (desc instanceof DevicePageDescriptor) {
                PrefsFragment fragment = new PrefsFragment();
                fragment.setDevice(((DevicePageDescriptor) desc).getDevice());
                return fragment;
            } else {
                return new NoDevicesFragment();
            }
        }
    }

    /**
     * A descriptor for each tab. It also retains the association between the tab and the correspondent device.
     *
     * @author marcocipriani01
     */
    private class DevicePageDescriptor extends SimplePageDescriptor {

        private final INDIDevice device;

        private DevicePageDescriptor(INDIDevice device) {
            super(device.getName() + c, device.getName());
            c++;
            this.device = device;
        }

        private INDIDevice getDevice() {
            return device;
        }
    }
}