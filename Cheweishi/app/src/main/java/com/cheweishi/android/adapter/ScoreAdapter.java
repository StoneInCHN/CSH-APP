package com.cheweishi.android.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.CarManagerAdapter.ViewHolder;
import com.cheweishi.android.adapter.CarManagerAdapter.onRightItemClickListener;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.entity.ScoreDetail;
import com.cheweishi.android.tools.LoginMessageUtils;

public class ScoreAdapter extends BaseAdapter {
	private Context context;
	private ViewHolder viewHolder;
	private LayoutInflater mInflater;
	private List<ScoreDetail> listCarManager;

	public ScoreAdapter(Context context, List<ScoreDetail> listCarManager) {
		this.context = context;
		this.listCarManager = listCarManager;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listCarManager.size();
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

	public void setData(List<ScoreDetail> listCarManager) {
		this.listCarManager = listCarManager;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.car_manager_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_deviceId = (TextView) convertView
					.findViewById(R.id.tv_deviceId);
			viewHolder.tv_deviceId = (TextView) convertView
					.findViewById(R.id.tv_deviceId);
			viewHolder.tv_currentState = (TextView) convertView
					.findViewById(R.id.tv_currentState);
			viewHolder.tv_currentPosition = (TextView) convertView
					.findViewById(R.id.tv_currentPosition);
			viewHolder.img_carManager = (ImageView) convertView
					.findViewById(R.id.img_carManager);
			viewHolder.tv_plateCode = (TextView) convertView
					.findViewById(R.id.tv_plateCode);
			viewHolder.ll_left = (LinearLayout) convertView
					.findViewById(R.id.ll_left);
			viewHolder.item_right = (RelativeLayout) convertView
					.findViewById(R.id.item_right);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (listCarManager != null && listCarManager.size() > 0) {

		}

		return convertView;
	}

	class ViewHolder {
		ImageView img_carManager;
		RelativeLayout item_right;
		LinearLayout ll_left;
		TextView tv_plateCode;
		TextView tv_deviceId;
		TextView tv_currentState;
		TextView tv_currentPosition;
	}

	/**
	 * 单击事件监听器
	 */
	private onRightItemClickListener mListener = null;

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View v, int position);
	}
}
