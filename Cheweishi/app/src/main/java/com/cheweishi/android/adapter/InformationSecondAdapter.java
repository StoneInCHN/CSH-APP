package com.cheweishi.android.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.CircleInformationDetailActivity;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CircleInformation;
import com.cheweishi.android.tools.ScreenTools;

public class InformationSecondAdapter extends BaseAdapter {
	private Context context;
	private ViewHolder viewHolder;
	private LayoutInflater mInflater;
	private List<CircleInformation> listCarManager;

	public InformationSecondAdapter(Context context,
			List<CircleInformation> listCarManager) {
		this.context = context;
		this.listCarManager = listCarManager;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listCarManager == null ? 0 : listCarManager.size();
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

	public void setData(List<CircleInformation> listCarManager) {
		this.listCarManager = listCarManager;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.information_second_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.img_infomation_item_title = (ImageView) convertView
					.findViewById(R.id.img_infomation_item_title);
			viewHolder.tv_infomation_item_title = (TextView) convertView
					.findViewById(R.id.tv_infomation_item_title);
			viewHolder.btn_infomation_item_title = (TextView) convertView
					.findViewById(R.id.btn_infomation_item_title);
			viewHolder.tv_informationTime = (TextView) convertView
					.findViewById(R.id.tv_informationTime);
			viewHolder.tv_infomation_item_details = (TextView) convertView
					.findViewById(R.id.tv_infomation_item_details);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (listCarManager != null && listCarManager.size() > 0) {
			viewHolder.tv_infomation_item_title.setText(listCarManager.get(
					position).getTitle());
			CircleInformation circleInformation = listCarManager.get(position);
			ViewGroup.LayoutParams lp = viewHolder.img_infomation_item_title
					.getLayoutParams();
			if (circleInformation.getW() != 0 && circleInformation.getH() != 0) {
				lp.height = (ScreenTools.getScreentWidth((Activity) context) - (int) context
						.getResources().getDimension(R.dimen.normal_padding) * 2)
						* circleInformation.getH() / circleInformation.getW();
			} else {
				lp.height = 0;
			}
			viewHolder.img_infomation_item_title.setLayoutParams(lp);

			XUtilsImageLoader.getxUtilsImageLoader(context,
					R.drawable.service_default,viewHolder.img_infomation_item_title,
					API.DOWN_IMAGE_URL + circleInformation.getPic());
			Date date = new Date(Long.parseLong(circleInformation.getTime()));
			SimpleDateFormat sf = new SimpleDateFormat("MM-dd HH:mm");
			viewHolder.tv_informationTime.setText(sf.format(date));

		}

		return convertView;
	}

	class ViewHolder {
		ImageView img_infomation_item_title;
		TextView tv_infomation_item_title;
		TextView btn_infomation_item_title;
		TextView tv_informationTime;
		TextView tv_infomation_item_details;
	}

}
