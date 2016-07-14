package com.cheweishi.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.entity.StoreListResponse;

import java.util.List;

/**
 * Created by tangce on 7/14/2016.
 */
public class StoreListAdapter extends BaseAdapter {

    private Context context;

    private List<StoreListResponse.MsgBean> list;


    public StoreListAdapter(Context context, List<StoreListResponse.MsgBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    private class ViewHolder {
        private ImageView icon;
        private TextView tenantName;
        private LinearLayout praiseRate;
        private TextView rateCount;
        private TextView address;
        private TextView distance;
        private TextView money;
        private TextView showMoney;
        private TextView pay;
    }
}
