package com.cheweishi.android.adapter;

import java.util.List;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CarType;
import com.cheweishi.android.entity.Carobject;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CarTypeCarBrandExpandableListViewAdapter extends
		BaseExpandableListAdapter {

	private Context context;
	private List<Carobject> list;

	public CarTypeCarBrandExpandableListViewAdapter(Context context,
			List<Carobject> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getGroupCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return list.get(groupPosition).getList() == null ? 0 : list
				.get(groupPosition).getList().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return list.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return list.get(groupPosition).getList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(
				R.layout.cartype_carbrandmodel_group_listitem, null);
		TextView groupName = (TextView) convertView
				.findViewById(R.id.tv_groupname);
		groupName.setText(list.get(groupPosition).getType());
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		Log.i("zhfy", "getChildView  groupPosition = " + groupPosition
				+ " childPosition = " + childPosition);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.cartype_carbrand_child_listitem, null);
			holder = new ViewHolder();
			holder.carLogo = (ImageView) convertView
					.findViewById(R.id.img_caricon);
			holder.carName = (TextView) convertView
					.findViewById(R.id.tv_carname);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CarType carType = list.get(groupPosition).getList().get(childPosition);
		holder.carName.setText(carType.getName());
		XUtilsImageLoader.getxUtilsImageLoader(context, R.drawable.repaire_img,
				holder.carLogo, API.DOWN_IMAGE_URL + carType.getLogo());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private ViewHolder holder;

	class ViewHolder {
		private ImageView carLogo;
		private TextView carName;
	}
}
