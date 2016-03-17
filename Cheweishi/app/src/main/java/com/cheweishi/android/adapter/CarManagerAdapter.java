package com.cheweishi.android.adapter;

import java.util.List;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.widget.FontAwesomeView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CarManagerAdapter extends BaseAdapter {
	private BaseActivity context;
	private ViewHolder viewHolder;
	private LayoutInflater mInflater;
	private List<CarManager> listCarManager;
	private int mRightWidth = 0;

	public CarManagerAdapter(BaseActivity context,
			List<CarManager> listCarManager, int rightWidth) {
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
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setData(List<CarManager> listCarManager) {
		if (listCarManager.size() > 0) {
			this.listCarManager = listCarManager;
			this.notifyDataSetChanged();
		}
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
			viewHolder.car_item_default_img = (FontAwesomeView) convertView
					.findViewById(R.id.car_item_default_img);
			viewHolder.tv_device_state = (TextView) convertView
					.findViewById(R.id.tv_device_state);
			viewHolder.tv_default_car = (TextView) convertView
					.findViewById(R.id.tv_default_car);
			viewHolder.tv_brandName = (TextView) convertView
					.findViewById(R.id.tv_brandName);
			viewHolder.tv_serielsName = (TextView) convertView
					.findViewById(R.id.tv_serielsName);
			viewHolder.cb_default_car = (CheckBox) convertView
					.findViewById(R.id.cb_default_car);
			viewHolder.ll_default_car = (LinearLayout) convertView
					.findViewById(R.id.ll_default_car);
			viewHolder.ll_default_car.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					context.dealCallBackFromAdapter(position, null);
				}
			});

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (listCarManager != null && listCarManager.size() > 0
				&& listCarManager.get(position).getPlate() != null) {
//			System.out.println("车辆管理" + BaseActivity.loginMessage.getCarManager().getId()+ "_" +listCarManager
//					.get(position)
//					.getId());
			if (BaseActivity.loginMessage.getCarManager() != null
					&& BaseActivity.loginMessage.getCarManager().getId() != null
					&& listCarManager
							.get(position)
							.getId()
							.equals(BaseActivity.loginMessage.getCarManager()
									.getId())) {
				viewHolder.tv_plateCode.setText(listCarManager.get(position)
						.getPlate() + "");

				viewHolder.car_item_default_img.setVisibility(View.GONE);
				// viewHolder.tv_plateCode.setTextColor(context.getResources()
				// .getColor(R.color.orange_text_color));
				viewHolder.cb_default_car.setChecked(true);

				viewHolder.tv_default_car.setVisibility(View.VISIBLE);
			} else {
				viewHolder.cb_default_car.setClickable(true);
				viewHolder.cb_default_car.setChecked(false);
				viewHolder.car_item_default_img.setVisibility(View.GONE);
				viewHolder.tv_plateCode.setTextColor(context.getResources()
						.getColor(R.color.black));
				viewHolder.tv_plateCode.setText(listCarManager.get(position)
						.getPlate());
				viewHolder.tv_default_car.setVisibility(View.INVISIBLE);
			}
			viewHolder.tv_brandName.setText(listCarManager.get(position)
					.getBrand().getBrandName());
			viewHolder.tv_serielsName.setText(listCarManager.get(position)
					.getBrand().getSeriesName());
			if (listCarManager.get(position).getDevice() == null
					|| listCarManager.get(position).getDevice().equals("")) {
				viewHolder.tv_device_state
						.setText(R.string.car_manager_no_device);
			} else {
				viewHolder.tv_device_state.setText("");
				// if (listCarManager.get(position).getFeed() == 0) {
				// viewHolder.tv_device_state.setText("");
				// } else {
				// viewHolder.tv_device_state
				// .setText(R.string.car_manager_select_returnStyle);
				// }
			}
			// if (position == 0) {
			// LinearLayout.LayoutParams lp2 = new LayoutParams(0,
			// LayoutParams.MATCH_PARENT);
			// viewHolder.item_right.setLayoutParams(lp2);
			//
			// } else {
			LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth,
					LayoutParams.MATCH_PARENT);
			viewHolder.item_right.setLayoutParams(lp2);

			viewHolder.item_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onRightItemClick(v, position);
					}
				}
			});

			XUtilsImageLoader.getxUtilsImageLoader(context,
					R.drawable.repaire_img,viewHolder.img_carManager,
					API.DOWN_IMAGE_URL
					+ listCarManager.get(position).getBoundJson());
			// if(viewHolder.cb_default_car.isChecked()) {
			viewHolder.cb_default_car.setClickable(false);
			// } else{
			// viewHolder.cb_default_car.setClickable(true);
			// }
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
		FontAwesomeView car_item_default_img;
		TextView tv_device_state;
		CheckBox cb_default_car;
		TextView tv_default_car;
		TextView tv_brandName;
		TextView tv_serielsName;
		LinearLayout ll_default_car;
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
