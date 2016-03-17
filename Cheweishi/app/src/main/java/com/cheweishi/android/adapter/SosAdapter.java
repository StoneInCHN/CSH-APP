package com.cheweishi.android.adapter;

import java.util.List;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.entity.SOS;
import com.cheweishi.android.tools.LoginMessageUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SosAdapter extends BaseAdapter {
	private Context context;
	private ViewHolder viewHolder;
	private LayoutInflater mInflater;
	private List<SOS> listCarManager;
	private int mRightWidth = 0;

	public SosAdapter(Context context, List<SOS> listCarManager, int rightWidth) {
		this.context = context;
		this.listCarManager = listCarManager;
		mRightWidth = rightWidth;
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
		return listCarManager.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setData(List<SOS> listCarManager) {
		if (listCarManager != null && listCarManager.size() > 0) {
			this.listCarManager = listCarManager;
			this.notifyDataSetChanged();
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_sos, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_sosName = (TextView) convertView
					.findViewById(R.id.tv_sosName);
			viewHolder.tv_sosTel = (TextView) convertView
					.findViewById(R.id.tv_sosTel);
			viewHolder.ll_leftSos = (LinearLayout) convertView
					.findViewById(R.id.ll_leftSos);
			viewHolder.item_rightSos = (RelativeLayout) convertView
					.findViewById(R.id.item_rightSos);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth,
				LayoutParams.MATCH_PARENT);
		viewHolder.item_rightSos.setLayoutParams(lp2);
		viewHolder.item_rightSos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightItemClick(v, position);
				}
			}
		});
		if (listCarManager != null && listCarManager.size() > 0) {
			SOS sos = listCarManager.get(position);
			viewHolder.tv_sosName.setText(sos.getName());
			viewHolder.tv_sosTel.setText(sos.getTel());
		}

		return convertView;
	}

	class ViewHolder {
		RelativeLayout item_rightSos;
		LinearLayout ll_leftSos;
		TextView tv_sosName;
		TextView tv_sosTel;
	}

	/**
	 * 单击事件监听器
	 */
	private onRightItemClickListener mListener = null;

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		// LinearLayout.LayoutParams lp2 = new LayoutParams(0,
		// LayoutParams.MATCH_PARENT);
		// viewHolder.item_right.setLayoutParams(lp2);
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View v, int position);
	}
}
