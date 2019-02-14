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
                return YoutubeFragment.newInstance(null, null);
            } else if (position == 1) {
                return BoxOfficeFragment.newInstance(null, null);
            } else if (position == 2) {
                return mainFragment.newInstance();
            } else if (position == 3) {
                return listFragment.newInstance();
            }else{
                return writingFragment.newInstance();
            }

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 5;
    }



}