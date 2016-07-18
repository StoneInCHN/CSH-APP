package com.cheweishi.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.entity.StoreListResponse;
import com.cheweishi.android.utils.StringUtil;

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
        initView();
    }

    private ImageView initView() {
        ImageView rateImage = new ImageView(context);
        rateImage.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
        rateImage.setPadding(0, 0, 2, 0);
        rateImage.setScaleType(ImageView.ScaleType.FIT_XY);
        rateImage.setImageResource(R.drawable.haoping);
        return rateImage;
    }

    public void setData(List<StoreListResponse.MsgBean> list) {
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
            convertView = View.inflate(context, R.layout.item_store_list, null);
            holder.tenantName = (TextView) convertView.findViewById(R.id.tv_store_item_tenant_name);
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_store_item);
            holder.praiseRate = (LinearLayout) convertView.findViewById(R.id.ll_store_item_rate);
            holder.rateCount = (TextView) convertView.findViewById(R.id.tv_store_item_rateCount);
            holder.rateNumber = (TextView) convertView.findViewById(R.id.tv_store_item_rate_number);
            holder.distance = (TextView) convertView.findViewById(R.id.tv_store_item_distance);
            holder.money = (TextView) convertView.findViewById(R.id.tv_store_item_money);
            holder.showMoney = (TextView) convertView.findViewById(R.id.tv_store_item_all_money);
            holder.pay = (TextView) convertView.findViewById(R.id.tv_store_item_buy);
            holder.address = (TextView) convertView.findViewById(R.id.tv_store_item_address);
            holder.serviceDesc = (TextView) convertView.findViewById(R.id.tv_store_item_wash_desc);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        XUtilsImageLoader.getxUtilsImageLoader(context, R.drawable.udesk_defalut_image_loading, R.drawable.udesk_defualt_failure, holder.icon, list.get(position).getPhoto());

        holder.tenantName.setText(list.get(position).getTenantName());
        holder.praiseRate.removeAllViews();
        // TODO 添加钻石
        for (int i = 0; i < list.get(position).getPraiseRate(); i++) {
            holder.praiseRate.addView(initView());
        }

        holder.rateNumber.setText(list.get(position).getPraiseRate() + "分");
        holder.rateCount.setText(list.get(position).getRateCounts() + "条评论");
        if (!StringUtil.isEmpty(list.get(position).getDistance())) {
            holder.distance.setVisibility(View.VISIBLE);
            holder.distance.setText(list.get(position).getDistance() + "km");
        } else {
            holder.distance.setVisibility(View.GONE);
        }
        holder.address.setText(list.get(position).getAddress());
        StoreListResponse.MsgBean.CarServiceBean carServiceBean = list.get(position).getCarService();
        if (null != carServiceBean) { // 证明是洗车服务
            holder.serviceDesc.setVisibility(View.VISIBLE);
            holder.serviceDesc.setText(list.get(position).getCarService().getServiceName());
            holder.money.setText("￥" + list.get(position).getCarService().getPrice());
            holder.showMoney.setText("￥" + list.get(position).getCarService().getPromotion_price());
            holder.pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 购买
                }
            });
        } else { // 不是洗车服务
            holder.serviceDesc.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView icon;
        private TextView tenantName;
        private LinearLayout praiseRate;
        private TextView rateCount; // 评论数量
        private TextView rateNumber;//评分
        private TextView address;
        private TextView distance;
        private TextView money;
        private TextView showMoney;
        private TextView pay;
        private TextView serviceDesc;
    }
}
