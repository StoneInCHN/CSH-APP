package com.cheweishi.android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cheweishi.android.entity.NewsTypeResponse;
import com.cheweishi.android.fragement.NewsPageFragment;
import com.cheweishi.android.fragement.ShopPageFragment;

import java.util.List;

/**
 * Created by huangzx on 2015/8/27.
 */
public class ShopFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    private List<NewsTypeResponse.MsgBean> list;

    public ShopFragmentPagerAdapter(FragmentManager fm, Context context, List<NewsTypeResponse.MsgBean> list) {
        super(fm);
        this.context = context;
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return ShopPageFragment.newInstance(position,list.get(position).getId());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).getName();
    }

}
