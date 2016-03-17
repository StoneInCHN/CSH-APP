package com.cheweishi.android.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.BaskOrderActivity;
import com.cheweishi.android.activity.CancelOrderActivity;
import com.cheweishi.android.activity.MyorderActivity;
import com.cheweishi.android.activity.OrderDetailsActivity;
import com.cheweishi.android.activity.WashCarPayActivity;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.MyOrderBean;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyorderAdapter extends BaseAdapter {
	private List<MyOrderBean> mData;
	private Context mContext;
	private int colorGray;
	private XUtilsImageLoader imageLoader;
	private int colorGrayNormal;
	private MyOrderBean bean;
	private LayoutInflater mInflater;
	private int mPosition;

	public MyorderAdapter(List<MyOrderBean> mData, Context mContext) {
		super();
		this.mData = mData;
		this.mContext = mContext;
		mInflater = LayoutInflater.from(mContext);
	}

	public void setData(List<MyOrderBean> mData) {
		this.mData = mData;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return mData == null ? 0 : mData.size();
	}

	@Override
	public Object getItem(int arg0) {

		return mData == null ? null : mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;
		if (arg1 == null) {
			holder = new ViewHolder();
			arg1 = mInflater.inflate(R.layout.item_my_order, null);

			holder.btn_order_comment = (TextView) arg1
					.findViewById(R.id.btn_order_comment);
			holder.btn_order_detail = (TextView) arg1
					.findViewById(R.id.btn_order_detail);
			holder.img_order = (ImageView) arg1.findViewById(R.id.img_order);
			holder.tv_order_class = (TextView) arg1
					.findViewById(R.id.tv_order_class);
			holder.tv_order_class_name = (TextView) arg1
					.findViewById(R.id.tv_order_class_name);
			holder.tv_shopping = (TextView) arg1.findViewById(R.id.tv_shopping);
			holder.tv_order_owner_name = (TextView) arg1
					.findViewById(R.id.tv_order_owner_name);
			holder.tv_money = (TextView) arg1.findViewById(R.id.tv_money);
			holder.tv_order_price = (TextView) arg1
					.findViewById(R.id.tv_order_price);
			holder.tv_time = (TextView) arg1.findViewById(R.id.tv_time);
			holder.tv_order_time = (TextView) arg1
					.findViewById(R.id.tv_order_time);
			arg1.setTag(holder);

		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		// 0订单完成1订单进行中2已取消3NO
		if (mData.get(arg0).getStatus().equals("0")) {// 订单已取消
			holder.tv_order_class_name.setText("已取消");
			holder.tv_time.setText("取消时间");
			holder.tv_order_time.setText(""
					+ mData.get(arg0).getFinished_time());
			holder.tv_order_class_name.setTextColor(mContext.getResources()
					.getColor(R.color.gray_pressed));
			holder.btn_order_comment.setVisibility(View.GONE);
			 holder.btn_order_detail.setVisibility(View.GONE);

			// holder.tv_order_class.setText("现代朗动保养");
			holder.btn_order_detail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(mContext, CancelOrderActivity.class);
					mContext.startActivity(intent);

				}
			});
		} else if (mData.get(arg0).getStatus().equals("12")) {// 订单进行中
			holder.tv_time.setText("下单时间");

			holder.tv_order_class_name.setText("进行中");
			holder.tv_order_time.setText("" + mData.get(arg0).getAdd_time());
			holder.tv_order_class_name.setTextColor(mContext.getResources()
					.getColor(R.color.green_deep));
			holder.btn_order_comment.setVisibility(View.GONE);
			 holder.btn_order_detail.setVisibility(View.GONE);
			holder.btn_order_detail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(mContext, OrderDetailsActivity.class);
					mContext.startActivity(intent);

				}
			});
		} else if (mData.get(arg0).getStatus().equals("1")) {// 确认付款
			holder.tv_order_class_name.setText("未付款");
			holder.tv_time.setText("下单时间");
			holder.tv_order_time
					.setText("" + mData.get(arg0).getAdd_time());
			holder.tv_order_class_name.setTextColor(mContext.getResources()
					.getColor(R.color.red));
			holder.btn_order_comment.setVisibility(View.VISIBLE);
			holder.btn_order_detail.setVisibility(View.GONE);
			holder.btn_order_comment.setText("确认付款");
			holder.btn_order_comment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg1) {
					Intent intent = new Intent();
					intent.putExtra("seller", mData.get(arg0).getSeller_name());
					intent.putExtra("service", mData.get(arg0).getGoods_name());
					intent.putExtra("price", mData.get(arg0).getPrice());
					if (mData.get(arg0).getCate_id_2().equals("30")) {
						intent.putExtra("type", "px");
					} else {
						intent.putExtra("type", "npx");
					}
					intent.putExtra("order_sn", mData.get(arg0).getOrder_sn());
					intent.putExtra("seller_id", mData.get(arg0).getSeller_id());
					intent.setClass(mContext, WashCarPayActivity.class);
					mContext.startActivity(intent);
				}
			});
		} else if (mData.get(arg0).getStatus().equals("3")) {// 订单完成
			holder.tv_order_class_name.setText("已完成");
			holder.tv_time.setText("支付时间");
			holder.tv_order_time.setText(""
					+ mData.get(arg0).getFinished_time());
			holder.tv_order_class_name.setTextColor(mContext.getResources()
					.getColor(R.color.main_orange));
			holder.btn_order_comment.setVisibility(View.GONE);
			holder.btn_order_detail.setVisibility(View.GONE);
			holder.btn_order_comment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(mContext, BaskOrderActivity.class);
					mContext.startActivity(intent);
				}
			});
		} else {// 过期
			holder.tv_order_class_name.setText("已过期");
			holder.tv_time.setText("过期时间");
			holder.tv_order_time.setText(""
					+ mData.get(arg0).getFinished_time());
			holder.tv_order_class_name.setTextColor(mContext.getResources()
					.getColor(R.color.gray_pressed));
			holder.btn_order_comment.setVisibility(View.GONE);
			holder.btn_order_detail.setVisibility(View.GONE);
			holder.btn_order_comment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(mContext, BaskOrderActivity.class);
					mContext.startActivity(intent);
				}
			});
		}
		if (mData.get(arg0).getCate_id_2().equals("30")
				|| mData.get(arg0).getCate_id_2().equals("31")) {// 洗车
			holder.img_order.setBackgroundResource(R.drawable.xiche);
		} else if (mData.get(arg0).getCate_id_2().equals("25")) {// 保养
			holder.img_order.setBackgroundResource(R.drawable.baoyang);
		} else if (mData.get(arg0).getCate_id_2().equals("23")) {// 美容
			holder.img_order.setBackgroundResource(R.drawable.meirong);
		}
		holder.tv_order_owner_name.setText(mData.get(arg0).getSeller_name());
		holder.tv_order_price.setText("￥" + mData.get(arg0).getPrice());
		holder.tv_order_class.setText(mData.get(arg0).getGoods_name());

		return arg1;

	}

	class ViewHolder {
		@ViewInject(R.id.img_order)
		private ImageView img_order;
		@ViewInject(R.id.tv_order_class)
		private TextView tv_order_class;
		@ViewInject(R.id.tv_order_class_name)
		private TextView tv_order_class_name;
		@ViewInject(R.id.tv_order_owner_name)
		private TextView tv_order_owner_name;
		@ViewInject(R.id.tv_order_price)
		private TextView tv_order_price;
		@ViewInject(R.id.tv_order_time)
		private TextView tv_order_time;
		@ViewInject(R.id.tv_shopping)
		private TextView tv_shopping;
		@ViewInject(R.id.tv_money)
		private TextView tv_money;
		@ViewInject(R.id.tv_time)
		private TextView tv_time;
		private TextView tv_order_class_name_overdue;
		@ViewInject(R.id.btn_order_comment)
		private TextView btn_order_comment;
		@ViewInject(R.id.btn_order_detail)
		private TextView btn_order_detail;
	}

}
