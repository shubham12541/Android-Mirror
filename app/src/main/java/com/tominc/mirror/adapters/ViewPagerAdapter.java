package com.tominc.mirror.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tominc.mirror.fragments.CalenderFragment;
import com.tominc.mirror.fragments.MusicFragment;
import com.tominc.mirror.fragments.NewsFragment;
import com.tominc.mirror.fragments.WeatherFragment;
import com.tominc.mirror.models.Weather;

/**
 * Created by shubham on 08/07/17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int numPages;

    public ViewPagerAdapter(FragmentManager fm, int numPages) {
        super(fm);
        this.numPages = numPages;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return WeatherFragment.newInstance();
            case 1:
                return NewsFragment.newInstance();
            case 2:
                return CalenderFragment.newInstance();
            case 3:
                return MusicFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.numPages;
    }
}
