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
import com.cheweishi.android.entity.ActivityCouponResponse;
import com.cheweishi.android.utils.LogHelper;

import java.util.List;

/**
 * Created by tangce on 5/11/2016.
 */
public class CouponAdapter extends BaseAdapter {

    private List<ActivityCouponResponse.MsgBean> list;

    private Context context;

    private OnCouponClickListener listener;

    public interface OnCouponClickListener {
        public void onCouponClick(int id, int position);
    }

    public void setOnCouponClickListener(OnCouponClickListener listener) {
        this.listener = listener;
    }

    public CouponAdapter(Context context, List<ActivityCouponResponse.MsgBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setData(List<ActivityCouponResponse.MsgBean> list) {
        if (list.size() > 0) {
            this.list = list;
            this.notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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


        holder.money.setText("" + list.get(position).getAmount());
        holder.date.setText("截止时间:" + list.get(position).getOverDueTime());
        holder.desc.setText(list.get(position).getRemark());
        holder.number.setText("" + list.get(position).getRemainNum());
        if (null != list.get(position).getType() && "COMMON".equals(list.get(position).getType())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.left.setBackground(context.getResources().getDrawable(R.drawable.b_item_coupon_left));
                holder.right.setBackground(context.getResources().getDrawable(R.drawable.b_item_coupon_right));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.left.setBackground(context.getResources().getDrawable(R.drawable.b_item_coupon_pink_left));
                holder.right.setBackground(context.getResources().getDrawable(R.drawable.b_item_coupon_pink_right));
            }
        }

        if (list.get(position).isIsGet()) { // 判断是否领取
            holder.overTime.setVisibility(View.VISIBLE);
            holder.overTime.setImageResource(R.drawable.b_coupon_got);
            holder.number.setVisibility(View.GONE);
            holder.get.setVisibility(View.GONE);
        } else {
            holder.overTime.setVisibility(View.GONE);
            holder.get.setVisibility(View.VISIBLE);
            holder.number.setVisibility(View.VISIBLE);
        }

        holder.get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onCouponClick(list.get(position).getId(), position);
                }
            }
        });
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
