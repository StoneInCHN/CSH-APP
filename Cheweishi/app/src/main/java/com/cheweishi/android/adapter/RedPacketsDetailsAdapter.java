package com.cheweishi.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.entity.RedPacketsInfo;

/*****
 * 话费详情，返费详情 adapter
 * 
 * @author Administrator
 * 
 */
public class RedPacketsDetailsAdapter extends BaseAdapter {

	private List<RedPacketsInfo> list;
	private Context context;
	private XUtilsImageLoader imageLoader;

	public RedPacketsDetailsAdapter(Context context,
			List<RedPacketsInfo> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.context = context;

	}

	public void setlist(List<RedPacketsInfo> list) {
		this.list = list;
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return (list == null||list.size() == 0) ? 0 : list.size();
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
					R.layout.item_purse_hongbao, null);
			viewHolder.tv_time = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.tv_note = (TextView) convertView
					.findViewById(R.id.tv_note);
			viewHolder.img_purse_draw = (TextView) convertView.findViewById(R.id.img_purse_draw);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		RedPacketsInfo info= list.get(position);
		viewHolder.tv_time.setText(info.getAdd_time() + " - " + info.getEffect());
		viewHolder.tv_note.setText(info.getCode());
		viewHolder.img_purse_draw.setText("￥" + info.getMoney());
		return convertView;
	}

	private class ViewHolder {
		private TextView tv_time;
		private TextView tv_note;
		private TextView img_purse_draw;
		
	}
}
