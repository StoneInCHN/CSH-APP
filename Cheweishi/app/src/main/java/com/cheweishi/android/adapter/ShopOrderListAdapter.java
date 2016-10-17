package com.cheweishi.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.entity.NewsListResponse;
import com.cheweishi.android.entity.ShopOrderListResponse;
import com.cheweishi.android.entity.ShopPayOrderNative;
import com.cheweishi.android.widget.SimpleTagImageView;
import com.cheweishi.android.widget.UnSlidingListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tangce on 7/19/2016.
 */
public class ShopOrderListAdapter extends BaseAdapter {

    private Context context;

    private List<ShopOrderListResponse.MsgBean> list;

    private List<ShopPayOrderListAdapter> childAdapters;


    public ShopOrderListAdapter(Context context, List<ShopOrderListResponse.MsgBean> list) {
        this.context = context;
        this.list = list;
        initChildAdapter(list);
    }

    public void setData(List<ShopOrderListResponse.MsgBean> list) {
        this.list = list;
        initChildAdapter(list);
        notifyDataSetChanged();
    }


    private void initChildAdapter(List<ShopOrderListResponse.MsgBean> parents) {
        if (null == parents || 0 == parents.size())
            return;
        childAdapters = new ArrayList<>();
        for (int i = 0; i < parents.size(); i++) {
            ShopOrderListResponse.MsgBean temp = parents.get(i);
            List<ShopPayOrderNative> orderNatives = new ArrayList<>();
            for (int j = 0; j < temp.getOrderItem().size(); j++) {
                ShopPayOrderNative orderNative = new ShopPayOrderNative();
                orderNative.setId(temp.getOrderItem().get(j).getId());
                orderNative.setNumber(temp.getOrderItem().get(j).getQuantity());
                orderNative.setMoney(temp.getOrderItem().get(j).getPrice());
                orderNative.setName(temp.getOrderItem().get(j).getName());
                orderNative.setIcon(temp.getOrderItem().get(j).getThumbnail());
                orderNatives.add(orderNative);
            }
            childAdapters.add(new ShopPayOrderListAdapter(context, orderNatives));
        }
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
            convertView = View.inflate(context, R.layout.item_shop_order_list, null);

            holder.time = (TextView) convertView.findViewById(R.id.tv_item_order_time);
            holder.status = (TextView) convertView.findViewById(R.id.tv_item_order_status);
            holder.content = (UnSlidingListView) convertView.findViewById(R.id.ulv_item_order_content);
            holder.money = (TextView) convertView.findViewById(R.id.tv_item_order_money);
            holder.cancel = (TextView) convertView.findViewById(R.id.tv_item_order_cancel);
            holder.pay = (TextView) convertView.findViewById(R.id.tv_item_order_pay);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.content.setAdapter(childAdapters.get(position));
        holder.time.setText(getDate(list.get(position).getCreateDate()));
//        holder.time.setText(list.get(position).getOrderItem().get(position).ge);

        return convertView;
    }

    private class ViewHolder {
        private TextView time;
        private TextView status;
        private UnSlidingListView content;
        private TextView money;
        private TextView cancel;
        private TextView pay;
    }


    private String getDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(time);// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
}
