package com.cheweishi.android.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.DrvingBehaviorDetail;

public class DrivingBehaviorAdapter extends BaseAdapter {
	private ViewHolder viewHolder;
	private LayoutInflater mInflater;
	private List<DrvingBehaviorDetail> listRepairInfo;

	public DrivingBehaviorAdapter(Context context,
			List<DrvingBehaviorDetail> listRepairInfo) {
		this.listRepairInfo = listRepairInfo;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return 10;
		return (listRepairInfo == null || listRepairInfo.size() == 0) ? 0
				: listRepairInfo.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.drving_behavior_item, null);
			viewHolder = new ViewHolder();

			viewHolder.tv_repair_name = (TextView) convertView
					.findViewById(R.id.drvingTime);
			viewHolder.tv_repair_address = (TextView) convertView
					.findViewById(R.id.drvingAddress);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (listRepairInfo != null && listRepairInfo.size() > 0) {
			viewHolder.tv_repair_name.setText(listRepairInfo.get(position)
					.getTime());
			viewHolder.tv_repair_address.setText(listRepairInfo.get(position)
					.getAddr());

		}

		return convertView;
	}

	class ViewHolder {
		ImageView img_repair_call;
		ImageView img_repair_location;
		ImageView img_repair_img;
		TextView tv_repair_name;
		TextView tv_repair_address;
		TextView tv_repair_distance;
	}

	public void setData(List<DrvingBehaviorDetail> listRepairInfo) {
		this.listRepairInfo = listRepairInfo;
		this.notifyDataSetChanged();
	}
}
