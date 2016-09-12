package com.james.li.rxjavaexample.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by jyj-lsy on 9/12/16 in zsl-tech.
 */
public class MyFragmentAdapter extends FragmentPagerAdapter{

    List<Fragment> fragmentList = null;

    public MyFragmentAdapter(List<Fragment> fragments, FragmentManager fm) {
        super(fm);
        this.fragmentList = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
