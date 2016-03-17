package com.cheweishi.android.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.TrackMessageBean;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.widget.FontAwesomeView;

public class FootmarkAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> group;
	List<List<TrackMessageBean>> child;

	public FootmarkAdapter(Context context, List<String> group,
			List<List<TrackMessageBean>> child) {
		this.context = context;
		this.group = group;
		this.child = child;
	}

	public void setlist(List<String> group, List<List<TrackMessageBean>> child) {
		// if (this.group != null) {
		// this.group.clear();
		// }
		// if (this.child != null) {
		// this.child.clear();
		// }
		this.child = child;
		this.group = group;
		this.notifyDataSetChanged();
	}

	@Override
	public Object getChild(int group, int position) {
		// TODO Auto-generated method stub
		if (child != null && child.get(group) != null
				&& child.get(group).get(position) != null) {
			return child.get(group).get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int arg0, int point, boolean arg2, View view,
			ViewGroup arg4) {
		if (child.get(arg0).size() > point) {
			if (child.get(arg0).get(point).getType().equals("0")) {
				view = LayoutInflater.from(context).inflate(
						R.layout.footmark_listview_park_item, null);

				TextView footmark_tv_initialaddress_point = (TextView) view
						.findViewById(R.id.footmark_tv_initialaddress_point);
				TextView footmark_tv_starttime_point = (TextView) view
						.findViewById(R.id.footmark_tv_starttime_point);
				String address = child.get(arg0).get(point).getAddr_start();
				String endTime = child.get(arg0).get(point).getDate_end();
				footmark_tv_initialaddress_point.setText(address);
				footmark_tv_starttime_point.setText(endTime);

			} else if (child.get(arg0).get(point).getType().equals("1")) {
				view = LayoutInflater.from(context).inflate(
						R.layout.footmark_listview_item, null);
				TextView footmark_tv_initialaddress = (TextView) view
						.findViewById(R.id.footmark_tv_initialaddress);
				TextView footmark_tv_overaddress = (TextView) view
						.findViewById(R.id.footmark_tv_overaddress);
				TextView footmark_tv_starttime = (TextView) view
						.findViewById(R.id.footmark_tv_starttime);
				TextView footmark_tv_journey = (TextView) view
						.findViewById(R.id.footmark_tv_journey);
				TextView footmark_tv_time = (TextView) view
						.findViewById(R.id.footmark_tv_time);
				TrackMessageBean listbBean = child.get(arg0).get(point);
				footmark_tv_initialaddress.setText(listbBean.getAddr_start());
				footmark_tv_journey.setText(listbBean.getMile() + "km");
				footmark_tv_overaddress.setText(listbBean.getAddr_end());
				footmark_tv_starttime.setText(listbBean.getDate_start().substring(10, 16));
				footmark_tv_time.setText(listbBean.getMinute() + "分钟");
			} else {

			}
		}
		return view;
	}

	@Override
	public int getChildrenCount(int group) {
		if (child == null) {
			return 0;
		} else {
			if (group < child.size()) {
				return child.get(group).size();
			} else {
				return 0;
			}
		}
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		if (group == null || group.size() == 0) {
			return 1;
		} else {
			return group.size();
		}

	}

	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}

	@Override
	public View getGroupView(int arg0, boolean arg1, View view, ViewGroup arg3) {
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.group_list_item_tag, null);
		}

		TextView mTime = (TextView) view
				.findViewById(R.id.group_list_item_text);
		if (group != null && group.size() > arg0) {
			String time = group.get(arg0);
			mTime.setText(time);
		}
		if (group.size() == 0) {
			view = LayoutInflater.from(context).inflate(
					R.layout.no_data, null);
			FontAwesomeView fav = (FontAwesomeView) view.findViewById(R.id.fav_img);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			params.setMargins(0, (int) (ScreenTools.getScreentHeight((Activity)context) * 0.6)/2 - 110, 0, 0);
			fav.setLayoutParams(params);
		}
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

}
