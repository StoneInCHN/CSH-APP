package com.cheweishi.android.activity;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.net.Uri;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InsuranceActivity extends BaseActivity implements OnClickListener {

	@ViewInject(R.id.tv_car_plate)
	private TextView tv_car_plate;
	@ViewInject(R.id.tv_insurance_city)
	private TextView tv_insurance_city;
	@ViewInject(R.id.tv_brandSeries)
	private TextView tv_brandSeries;
	@ViewInject(R.id.tv_insurance_car)
	private TextView tv_insurance_car;
	@ViewInject(R.id.tv_insurance_carModel)
	private TextView tv_insurance_carModel;
	@ViewInject(R.id.tv_car_plate_upload)
	private TextView tv_car_plate_upload;
	@ViewInject(R.id.btn_insurance_calculate)
	private Button btn_insurance_calculate;
	@ViewInject(R.id.btn_car_plate_upload)
	private TextView btn_car_plate_upload;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.tv_changeCar)
	private TextView tv_changeCar;
	private MyBroadcastReceiver broad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insurance);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		title.setText(R.string.title_activity_insurance);
		left_action.setText(R.string.back);
		if (isLogined() && hasCar()) {
			tv_car_plate.setText(loginMessage.getCarManager().getPlate());
			tv_brandSeries.setText(loginMessage.getCarManager().getBrand()
					.getBrandName()
					+ "-"
					+ loginMessage.getCarManager().getBrand().getSeriesName());
			tv_insurance_carModel.setText(loginMessage.getCarManager()
					.getBrand().getModuleName());
			tv_insurance_car.setText(loginMessage.getCarManager().getPlate());
		}
	}

	@OnClick({ R.id.btn_car_plate_upload, R.id.left_action, R.id.tv_changeCar,
			R.id.btn_insurance_calculate })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_car_plate_upload:
			Intent intent = new Intent(this,
					InsuranceInformationUploadActivity.class);
			startActivity(intent);
			break;
		case R.id.left_action:
			finish();
			break;
		case R.id.tv_changeCar:
			intent = new Intent(this, CarManagerActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_insurance_calculate:
			intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ getResources().getString(R.string.customerServicePhone)));
			startActivity(intent);
			// intent = new Intent(this, InsuranceCalculationActivity.class);
			// startActivity(intent);
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// 注册刷新广播
		if (broad == null) {
			broad = new MyBroadcastReceiver();
		}
		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		registerReceiver(broad, intentFilter);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			Constant.EDIT_FLAG = false;
			System.out.println("SUCCESS====main_" + Constant.CURRENT_REFRESH);
			if (!StringUtil.isEquals(intent.getAction(), Constant.REFRESH_FLAG,
					true)) {
				System.out.println("SUCCESS====" + "更新false");
				return;
			}
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.CAR_MANAGER_REFRESH, true)) {
				System.out.println("SUCCESS====" + "保险车辆切换更新" + "_"
						+ loginMessage.getCar().getPlate());
				initViews();
			}
		}
	}
}
