package com.tominc.mirror.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tominc.mirror.fragments.CalenderFragment;
import com.tominc.mirror.fragments.NewsFragment;
import com.tominc.mirror.fragments.WeatherFragment;

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
                return new WeatherFragment();
            case 1:
                return new NewsFragment();
            case 2:
                return new CalenderFragment();
            default:
                return new WeatherFragment();
        }
    }

    @Override
    public int getCount() {
        return this.numPages;
    }
}
