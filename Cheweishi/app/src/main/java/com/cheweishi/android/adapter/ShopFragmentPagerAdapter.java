package com.cheweishi.android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cheweishi.android.entity.NewsTypeResponse;
import com.cheweishi.android.entity.ShopTypeResponse;
import com.cheweishi.android.fragement.NewsPageFragment;
import com.cheweishi.android.fragement.ShopPageFragment;

import java.util.List;

/**
 * Created by tanck on 2016/08/07.
 */
public class ShopFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    private List<ShopTypeResponse.MsgBean> list;

    public ShopFragmentPagerAdapter(FragmentManager fm, Context context, List<ShopTypeResponse.MsgBean> list) {
        super(fm);
        this.context = context;
        this.list = list;
    }

    public void setData(List<ShopTypeResponse.MsgBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return ShopPageFragment.newInstance(position, list.get(position).getId(),list.get(position).getName());
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
