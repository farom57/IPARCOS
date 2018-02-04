package farom.iparcos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;

public class GenericFragment extends Fragment implements TabLayout.OnTabSelectedListener, INDIServerConnectionListener {

    private ConnectionManager connectionManager;

    // Views
    private ViewPager viewPager;
    private ArrayPagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    /**
     * Used to create an unique tag for each tab.
     */
    private int c = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_generic, container, false);

        connectionManager = Application.getConnectionManager();
        System.out.println("Register");
        connectionManager.registerPermanentConnectionListener(this);
        System.out.println("Done");

        tabLayout = rootView.findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = rootView.findViewById(R.id.pager);
        pagerAdapter = new DevicesPagerAdapter(getChildFragmentManager(), new ArrayList<PageDescriptor>());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        INDIServerConnection connection = connectionManager.getConnection();
        if (connection == null) {
            return;
        }
        List<INDIDevice> list = connection.getDevicesAsList();
        if (list == null) {
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
        // Remove all the tabs
        tabLayout.removeAllTabs();
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            pagerAdapter.remove(i);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionManager.unRegisterPermanentConnectionListener(this);
    }

    @Override
    public void connectionLost(INDIServerConnection arg0) {
        // Move to the connection tab
        Application.goToConnectionTab();
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
        Application.goToConnectionTab();
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
     * Page adapter. Creates the {@link PrefsFragment} corresponding to the specified {@link PageDescriptor}.
     *
     * @author SquareBoot
     */
    private class DevicesPagerAdapter extends ArrayPagerAdapter<Fragment> {

        DevicesPagerAdapter(FragmentManager fragmentManager, List<PageDescriptor> descriptors) {
            super(fragmentManager, descriptors);
        }

        @Override
        protected Fragment createFragment(PageDescriptor desc) {
            PrefsFragment fragment = new PrefsFragment();
            fragment.setDevice(((DevicePageDescriptor) desc).getDevice());
            return fragment;
        }
    }

    /**
     * A descriptor for each tab. It also retains the association between the tab and the correspondent device.
     *
     * @author SquareBoot
     */
    private class DevicePageDescriptor extends SimplePageDescriptor {

        private INDIDevice device;

        DevicePageDescriptor(INDIDevice device) {
            super(device.getName() + c, device.getName());
            c++;
            this.device = device;
        }

        INDIDevice getDevice() {
            return device;
        }
    }
}