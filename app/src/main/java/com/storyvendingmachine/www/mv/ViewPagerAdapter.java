package com.storyvendingmachine.www.mv;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * Created by Administrator on 2018-03-06.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("item pointer", Integer.toString(position));

            if (position == 0) {
                return mainFragment.newInstance();
            } else if (position == 1) {
                return listFragment.newInstance();
            } else if (position == 2) {
                return writingFragment.newInstance();
            } else {
                return null;
            }

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }



}