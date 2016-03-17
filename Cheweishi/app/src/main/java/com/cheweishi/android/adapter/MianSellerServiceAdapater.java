package com.cheweishi.android.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.WashCarPayActivity;
import com.cheweishi.android.entity.MainSellerInfo;
import com.cheweishi.android.entity.MainSellerServiceInfo;
import com.cheweishi.android.utils.StringUtil;

/**
 * 新版首页商家服务列表适配器
 * 
 * @author mingdasen
 * 
 */
public class MianSellerServiceAdapater extends BaseAdapter {

	private Context mContext;
	private List<MainSellerServiceInfo> list;
	private MainSellerInfo mainSellerInfo;

	public MianSellerServiceAdapater(Context mContext,
			MainSellerInfo mainSellerInfo) {
		this.mContext = mContext;
		this.mainSellerInfo = mainSellerInfo;
		this.list = mainSellerInfo.getServices();
	}

	@Override
	public int getCount() {
		return StringUtil.isEmpty(list) ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_seller_service, null);
			holder.tv_service_name = (TextView) convertView
					.findViewById(R.id.tv_service_name);
			holder.tv_service_fPrice_name = (TextView) convertView
					.findViewById(R.id.tv_service_fPrice_name);
			holder.tv_service_fPrice = (TextView) convertView
					.findViewById(R.id.tv_service_fPrice);
			holder.tv_service_price = (TextView) convertView
					.findViewById(R.id.tv_service_price);
			holder.tv_red_packets = (TextView) convertView.findViewById(R.id.tv_time);
			holder.btn_pay = (TextView) convertView.findViewById(R.id.btn_pay);
			
			holder.btn_pay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, WashCarPayActivity.class);
					intent.putExtra("seller", mainSellerInfo.getName());
					intent.putExtra("seller_id", mainSellerInfo.getId());
					intent.putExtra("service", list.get(position).getName());
					intent.putExtra("service_id", list.get(position).getId());
					if (StringUtil.isEmpty(list.get(position).getfPrice()) || StringUtil.isEquals("null", list.get(position).getfPrice(), true)) {
						intent.putExtra("price", list.get(position).getPrice());
					} else {
						intent.putExtra("price", list.get(position).getfPrice());
					}
					if (StringUtil.isEquals(list.get(position).getCate_id_2(), "30", true)) {
						intent.putExtra("type", "px");
					} else {
						intent.putExtra("type", "");
					}
					mContext.startActivity(intent);
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!StringUtil.isEmpty(list) && list.size() > position) {
			holder.tv_service_name.setText(list.get(position).getName());
			Log.i("result", "=======Price=======" + list.get(position).getPrice() + "===fPrice==" + list.get(position).getfPrice());
			if (StringUtil.isEmpty(list.get(position).getfPrice()) || StringUtil.isEquals("null", list.get(position).getfPrice(), true)) {
				holder.tv_service_fPrice.setText("￥"+list.get(position).getPrice());
			} else {
				holder.tv_service_fPrice.setText("￥"+list.get(position).getfPrice());
			}
			holder.tv_service_price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
			holder.tv_service_price.setText("￥"+list.get(position).getPrice());
			if (StringUtil.isEquals("0", list.get(position).getIsRed(), true)) {
				holder.tv_red_packets.setVisibility(View.VISIBLE);
			}else {
				holder.tv_red_packets.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

	class ViewHolder {
		private TextView tv_service_name;
		private TextView tv_service_fPrice_name;
		private TextView tv_service_fPrice;
		private TextView tv_service_price;
		private TextView btn_pay;
		private TextView tv_red_packets;
	}
}
