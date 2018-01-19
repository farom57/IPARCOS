package farom.iparcos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.List;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;

@SuppressWarnings("FinalizeCalledExplicitly")
public class GenericActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, INDIServerConnectionListener {

    PagerAdapter pagerAdapter;
    /**
     * The active fragment
     */
    private PrefsFragment fragment = null;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectionActivity.getInstance().registerPermanentConnectionListener(this);

        setContentView(R.layout.activity_generic);

        Toolbar toolbar = findViewById(R.id.app_toolbar);
        // toolbar.setSubtitle(R.string.title_activity_generic);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);

        // Hide the item for the current activity
        MenuItem genericItem = menu.findItem(R.id.menu_generic);
        genericItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        INDIServerConnection connection = ConnectionActivity.getConnection();
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
            pagerAdapter.tabDeviceMap.put(f, device);
            tabLayout.addTab(tabLayout.newTab().setText(device.getName()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove all the tabs
        tabLayout.removeAllTabs();
        pagerAdapter.tabDeviceMap.clear();
    }


    /**
     * open the motion activity,
     *
     * @param v
     */
    public boolean openMotionActivity(MenuItem v) {
        startActivity(new Intent(this, MotionActivity.class));
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
        // Nothing to do, already the current activity
        return false;
    }

    /**
     * open the search activity
     *
     * @param v
     * @return
     */
    public boolean openSearchActivity(MenuItem v) {
        startActivity(new Intent(this, SearchActivity.class));
        return true;
    }

    /**
     * open the connection activity
     *
     * @param v
     * @return
     */
    public boolean openConnectionActivity(MenuItem v) {
        startActivity(new Intent(this, ConnectionActivity.class));
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
    public void onTabReselected(TabLayout.Tab arg0) {
        // User selected the already selected tab. Do nothing.
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());

        // Check if the fragment is already initialized
        /*if (fragment == null) {
            fragment = new PrefsFragment();
            fragment.setDevice(pagerAdapter.tabDeviceMap.get(tab.get)); //TODO
            ft.add(android.R.id.content, fragment);

        } else {
            Log.e("GenericActivity", "error : fragment != null");
        }*/
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        /*if (fragment != null) {
            // Detach the fragment, and delete it because an other will be created
            ft.detach(fragment);
            try {
                fragment.finalize();

            } catch (Throwable e) {
                Log.e("GenericActivity", "Error in fragment.finalize(): " + e.getLocalizedMessage());
            }
            fragment = null;

        } else {
            Log.e("GenericActivity", "Error : fragment = null");
        }*/
    }
}