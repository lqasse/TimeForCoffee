package no.lqasse.timeforcoffee.Main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by lassedrevland on 02.01.16.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private static final int LIST = 0;
    private static final int SETTINGS = 1;
    private SettingsFragment settingsFragment;
    private ListFragment listFragment;
    private MainActivity main;

    public PagerAdapter(FragmentManager fm, MainActivity main) {
        super(fm);
        this.main = main;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == LIST){
            return listFragment.newInstance(main);
        } else if (position == SETTINGS){
            return settingsFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
