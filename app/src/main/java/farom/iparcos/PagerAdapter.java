package farom.iparcos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.HashMap;

import laazotea.indi.client.INDIDevice;

/**
 * @author SquareBoot
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class PagerAdapter extends FragmentStatePagerAdapter {

    /**
     * Retains the association between the tab and the device
     */
    HashMap<PrefsFragment, INDIDevice> tabDeviceMap;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        tabDeviceMap = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        return (Fragment) tabDeviceMap.keySet().toArray()[position];
    }

    @Override
    public int getCount() {
        return tabDeviceMap.size();
    }
}