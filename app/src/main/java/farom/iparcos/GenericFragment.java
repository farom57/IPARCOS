package farom.iparcos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;

@SuppressWarnings("FinalizeCalledExplicitly")
public class GenericFragment extends Fragment implements TabLayout.OnTabSelectedListener, INDIServerConnectionListener {

    /**
     * Retains the association between the tab and the device
     */
    HashMap<PrefsFragment, INDIDevice> tabDeviceMap;

    // Views
    private View rootView;
    private ViewPager viewPager;
    private ArrayPagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_generic, container, false);

        ConnectionFragment.getInstance().registerPermanentConnectionListener(this);

        tabDeviceMap = new HashMap<>();

        tabLayout = rootView.findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = rootView.findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getChildFragmentManager(), new ArrayList<PageDescriptor>());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        INDIServerConnection connection = ConnectionFragment.getConnection();
        if (connection == null) {
            return;
        }
        List<INDIDevice> list = connection.getDevicesAsList();
        if (list == null) {
            return;
        }
        // Recreate tabs
        for (INDIDevice device : list) {
            PrefsFragment f = new PrefsFragment();
            f.setDevice(device);
            tabDeviceMap.put(f, device);
            pagerAdapter.add(new DeviceDescriptor(device));
            tabLayout.addTab(tabLayout.newTab().setText(device.getName()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove all the tabs
        tabLayout.removeAllTabs();
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            pagerAdapter.remove(i);
        }
        tabDeviceMap.clear();
    }

    @Override
    public void connectionLost(INDIServerConnection arg0) {
        //TODO openConnectionActivity(null);
    }

    @Override
    public void newDevice(INDIServerConnection arg0, INDIDevice arg1) {

    }

    @Override
    public void newMessage(INDIServerConnection arg0, Date arg1, String arg2) {

    }

    @Override
    public void removeDevice(INDIServerConnection arg0, INDIDevice arg1) {
        //TODO openConnectionActivity(null);
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

    private class PagerAdapter extends ArrayPagerAdapter<Fragment> {

        PagerAdapter(FragmentManager fragmentManager, List<PageDescriptor> descriptors) {
            super(fragmentManager, descriptors);
        }

        @Override
        protected Fragment createFragment(PageDescriptor desc) {
            PrefsFragment fragment = new PrefsFragment();
            fragment.setDevice(((DeviceDescriptor) desc).getDevice());
            return fragment;
        }
    }

    private class DeviceDescriptor extends SimplePageDescriptor {

        private INDIDevice device;

        DeviceDescriptor(INDIDevice device) {
            super(device.getName(), device.getName());
            this.device = device;
        }

        INDIDevice getDevice() {
            return device;
        }
    }
}