package farom.iparcos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;

public class GenericActivity extends AppCompatActivity implements ActionBar.TabListener, INDIServerConnectionListener {

    /**
     * The active fragment
     */
    private PrefsFragment fragment = null;

    /**
     * Retains the association between the tab and the device
     */
    private HashMap<ActionBar.Tab, INDIDevice> tabDeviceMap;


    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectionActivity.getInstance().registerPermanentConnectionListener(this);

        setContentView(R.layout.activity_generic);

        Toolbar toolbar = findViewById(R.id.app_toolbar);
        // toolbar.setSubtitle(R.string.title_activity_generic);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        tabDeviceMap = new HashMap<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);

        // hide the item for the current activity
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

        if (list.size() > 0) {
            // Recreate tabs
            for (INDIDevice device : list) {
                ActionBar.Tab tab = actionBar.newTab();

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
     * open the search activity
     *
     * @param v
     * @return
     */
    public boolean openSearchActivity(MenuItem v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        return true;
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
    public void onTabReselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
        // User selected the already selected tab. Usually do nothing.
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Check if the fragment is already initialized
        if (fragment == null) {
            fragment = new PrefsFragment();
            fragment.setDevice(tabDeviceMap.get(tab));
            // TODO(squareboot)
            //ft.add(android.R.id.content, fragment);

        } else {
            Log.e("GenericActivity", "error : fragment!=null");
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment != null) {
            // Detach the fragment, and delete it because an other will be created
            // TODO(squareboot)
            //ft.detach(fragment);
            try {
                fragment.finalize();

            } catch (Throwable e) {
                //e.printStackTrace();
                Log.e("GenericActivity", "error fragment.finalize() : " + e.getLocalizedMessage());
            }
            fragment = null;

        } else {
            Log.e("GenericActivity", "error : fragment==null");
        }
    }
}