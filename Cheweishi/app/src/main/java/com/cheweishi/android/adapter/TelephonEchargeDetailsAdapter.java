package com.cheweishi.android.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.ReturnTheMoneyInfo;
import com.cheweishi.android.utils.StringUtil;

/*****
 * 话费详情，返费详情 adapter
 * 
 * @author Administrator
 * 
 */
public class TelephonEchargeDetailsAdapter extends BaseAdapter {

	private List<ReturnTheMoneyInfo> list;
	private Context context;

	public TelephonEchargeDetailsAdapter(Context context,
			List<ReturnTheMoneyInfo> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.context = context;

	}

	public void setlist(List<ReturnTheMoneyInfo> list) {
		this.list = list;
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return (list == null || list.size() == 0) ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub

		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.telephoneecharge_pull_listview_item, null);
			viewHolder.MoldMoney = (TextView) convertView
					.findViewById(R.id.telephone_pulltorefresh_tv_moldmoney);
			viewHolder.Money = (TextView) convertView
					.findViewById(R.id.telephone_pulltorefresh_tv_money);
			viewHolder.Time = (TextView) convertView
					.findViewById(R.id.telephone_pulltorefresh_tv_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ReturnTheMoneyInfo info = list.get(position);
		viewHolder.Time.setText(info.getAdd_time());

		if (info.getType().equals("1")) {
			viewHolder.Money.setTextColor(context.getResources().getColor(
					R.color.green));
			viewHolder.Money.setText("-" + info.getMoney());

		} else if (info.getType().equals("0")) {

			viewHolder.Money.setTextColor(Color.RED);
			if (!StringUtil.isEmpty(info.getPayment_type())
					&& info.getPayment_type().equals("2")) {
				viewHolder.Money.setText("+" + info.getRed());
			} else {
				viewHolder.Money.setText("+" + info.getMoney());
			}
		}
		viewHolder.MoldMoney.setText(info.getDescribe());
		return convertView;
	}

	private class ViewHolder {
		TextView MoldMoney;
		TextView Time;
		TextView Money;
	}
}
