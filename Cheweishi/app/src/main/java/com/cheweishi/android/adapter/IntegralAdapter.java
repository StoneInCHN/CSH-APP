package com.cheweishi.android.adapter;

import java.util.List;
import java.util.Map;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.IntegralInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IntegralAdapter extends BaseAdapter {

	private Context mContext;
	private List<IntegralInfo> mList;
	private LayoutInflater mLayoutInflater;

	public IntegralAdapter(Context context, List<IntegralInfo> list) {
		mContext = context;
		mList = list;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	public void setList(List<IntegralInfo> list) {
		mList = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList == null ? 1 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(
					R.layout.integral_xlistview_item, null);
			viewHolder.sigeAndmileageTextView = (TextView) convertView
					.findViewById(R.id.sigeAndmileage);
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.integral_time);
			viewHolder.integralTextView = (TextView) convertView
					.findViewById(R.id.integral_textview_number);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(mList != null && mList.size() > position){
			IntegralInfo info= mList.get(position);
			String type = info.type;
			String total = info.total;
			String time = info.time;
			String rule=info.rule;
			viewHolder.sigeAndmileageTextView.setText(type);
			viewHolder.timeTextView.setText(time);
			if (rule.equals("0")) {
				viewHolder.integralTextView.setText("+" + total+"分");
				
			}else if (rule.equals("1")) {
				viewHolder.integralTextView.setText("-" + total);
				
			}
		}
		
		return convertView;
	}

	private class ViewHolder {
		private TextView sigeAndmileageTextView;
		private TextView timeTextView;
		private TextView integralTextView;

	}

}
