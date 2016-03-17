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
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CircleInformation;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.XCRoundImageView;

/**
 * 车资讯适配器
 * 
 * @author mingdasen
 * 
 */
public class CarInfoAdapter extends BaseAdapter {
	private Context context;
	private List<CircleInformation> list;

	public CarInfoAdapter(Context context, List<CircleInformation> list) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View conterView, ViewGroup parent) {
		HoderView hoder;
		if (conterView == null) {
			hoder = new HoderView();
			conterView = LayoutInflater.from(context).inflate(
					R.layout.car_information_list_item, null);
			hoder.img = (XCRoundImageView) conterView.findViewById(R.id.img);
			hoder.tv_name = (TextView) conterView
					.findViewById(R.id.tv_title_name);
			hoder.tv_time = (TextView) conterView.findViewById(R.id.tv_time);
			hoder.tv_content = (TextView) conterView
					.findViewById(R.id.tv_info_content);
			conterView.setTag(hoder);
		} else {
			hoder = (HoderView) conterView.getTag();
		}
		if (list != null) {
			XUtilsImageLoader.getxUtilsImageLoader(context,
					R.drawable.zhaochewei_img,hoder.img,
					API.DOWN_IMAGE_URL+ list.get(position).getLogo());
			hoder.tv_name.setText(list.get(position).getType());
			String date = StringUtil.getDateForFormat2(list
					.get(position).getTime(), "MM-dd HH:mm");
			if (date == null) {
				hoder.tv_time.setText("");
			}else {
				hoder.tv_time.setText(date);
			}
			hoder.tv_content.setText(list.get(position).getContent());
		}
		return conterView;
	}

	class HoderView {
		private TextView tv_name;
		private XCRoundImageView img;
		private TextView tv_time;
		private TextView tv_content;
	}

}
