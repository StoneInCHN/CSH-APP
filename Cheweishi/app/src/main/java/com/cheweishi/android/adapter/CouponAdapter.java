package com.cheweishi.android.adapter;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.R;

import java.util.List;

/**
 * Created by tangce on 5/11/2016.
 */
public class CouponAdapter extends BaseAdapter {

    private List<String> list;

    private Context context;

    public CouponAdapter(Context context, List<String> list) {
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
        ViewHolder holder = null;

        if (null == convertView) {
            holder = new ViewHolder();

            convertView = View.inflate(context, R.layout.item_coupon, null);

            holder.money = (TextView) convertView.findViewById(R.id.tv_coupon_money);
            holder.desc = (TextView) convertView.findViewById(R.id.tv_coupon_desc);
            holder.date = (TextView) convertView.findViewById(R.id.tv_coupon_date);
            holder.number = (TextView) convertView.findViewById(R.id.tv_coupon_number);
            holder.overTime = (ImageView) convertView.findViewById(R.id.iv_coupon_overtime);
            holder.get = (TextView) convertView.findViewById(R.id.tv_coupon_get);
            holder.left = (LinearLayout) convertView.findViewById(R.id.ll_coupon_left);
            holder.right = (LinearLayout) convertView.findViewById(R.id.ll_coupon_right);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // TODO 根据不同类型加载

        if (position % 2 == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.left.setBackground(context.getResources().getDrawable(R.drawable.b_item_coupon_pink_left));
                holder.right.setBackground(context.getResources().getDrawable(R.drawable.b_item_coupon_pink_right));
            }
//            holder.left.setBackgroundResource(R.drawable.b_item_coupon_pink_left);
//            holder.right.setBackgroundResource(R.drawable.b_item_coupon_pink_left);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.left.setBackground(context.getResources().getDrawable(R.drawable.b_item_coupon_left));
                holder.right.setBackground(context.getResources().getDrawable(R.drawable.b_item_coupon_right));
            }
//            holder.left.setBackgroundResource(R.drawable.b_item_coupon_left);
//            holder.right.setBackgroundResource(R.drawable.b_item_coupon_left);
        }


        return convertView;
    }

    private class ViewHolder {
        private TextView money;
        private TextView desc;
        private TextView date;
        private ImageView overTime;
        private TextView number;
        private TextView get;
        private LinearLayout left;
        private LinearLayout right;
    }
}
