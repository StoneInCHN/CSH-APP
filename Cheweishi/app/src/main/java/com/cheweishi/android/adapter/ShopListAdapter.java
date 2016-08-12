package com.cheweishi.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.entity.NewsListResponse;
import com.cheweishi.android.entity.ShopListResponse;
import com.cheweishi.android.widget.SimpleTagImageView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by tangce on 7/19/2016.
 */
public class ShopListAdapter extends BaseAdapter {

    private Context context;

    private List<ShopListResponse.MsgBean> list;


    public ShopListAdapter(Context context, List<ShopListResponse.MsgBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setData(List<ShopListResponse.MsgBean> list) {
        this.list = list;
        notifyDataSetChanged();
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

        ViewHolder holder = null;

        if (null == convertView) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_shop_list, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_shop_item_img);
            holder.desc = (TextView) convertView.findViewById(R.id.tv_shop_item_desc);
            holder.buyCount = (TextView) convertView.findViewById(R.id.tv_shop_item_buy_count);
            holder.money = (TextView) convertView.findViewById(R.id.tv_shop_item_money);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder.desc.setText();

        return convertView;
    }

    private class ViewHolder {
        private ImageView icon;
        private TextView desc;
        private TextView money;
        private TextView buyCount;
    }

}
