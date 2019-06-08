package com.example.pharmassist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int i) {
        switch(i) {
            case 0:
                //MainActivity.fragmentid = 0;
                return new MainActivityFragment();
            case 1:
                //MainActivity.fragmentid = 1;
                return new TestFragment();
            default:
                return null;


        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
