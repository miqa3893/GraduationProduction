package com.example.miqa3893.LineManager;

import android.support.v4.app.*;
import android.support.v4.app.Fragment;

//TotalLines:38

public class MyFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomeFragment.newInstance(android.R.color.transparent);
            case 1:
                return HomeFragment.newInstance(android.R.color.transparent);
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "生産ライン" + (position + 1);
    }

}
