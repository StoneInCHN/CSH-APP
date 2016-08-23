package com.cheweishi.android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cheweishi.android.fragement.ProductParamPageFragment;

import java.util.List;

/**
 * Created by tanck on 2016/08/07.
 */
public class ProductParamFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    private List<String> list;

    public ProductParamFragmentPagerAdapter(FragmentManager fm, Context context, List<String> list) {
        super(fm);
        this.context = context;
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return ProductParamPageFragment.newInstance(position, list.get(position));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);
    }

}
