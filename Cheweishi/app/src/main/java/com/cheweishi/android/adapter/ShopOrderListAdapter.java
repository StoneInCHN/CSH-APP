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
import com.cheweishi.android.widget.SimpleTagImageView;
import com.cheweishi.android.widget.UnSlidingListView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by tangce on 7/19/2016.
 */
public class ShopOrderListAdapter extends BaseAdapter {

    private Context context;

    private List<ShopOrderListResponse.MsgBean> list;


    public ShopOrderListAdapter(Context context, List<ShopOrderListResponse.MsgBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setData(List<ShopOrderListResponse.MsgBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    private void initChildAdapter() {

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
    public int getViewTypeCount() {
        return 2;//2种类型
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
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

}
