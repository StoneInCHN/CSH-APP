package com.cheweishi.android.activity;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 车辆检测
 * 
 * @author mingdasen
 * 
 */
@ContentView(R.layout.activity_car_detection)
public class CarDetectionActivity extends BaseActivity {

	@ViewInject(R.id.left_action)
	private Button left_action;

	@ViewInject(R.id.title)
	private TextView title;

	@ViewInject(R.id.right_action)
	private TextView right_action;

	@ViewInject(R.id.tv_detdction_plate)
	private TextView tv_detdction_plate;// 车牌号

	@ViewInject(R.id.img_car_logo)
	private ImageView img_car_logo;// 车辆图标

	@ViewInject(R.id.tv_car_state)
	private TextView tv_car_state;// 车辆行驶总里程和总时间

	@ViewInject(R.id.btn_security_scan)
	private TextView btn_security_scan;// 安全扫描按钮

	@ViewInject(R.id.tv_trip_date)
	private TextView tv_trip_date;// 报告时间

	@ViewInject(R.id.oil_wear)
	private TextView oil_wear;// 当日油耗

	@ViewInject(R.id.tv_speed)
	private TextView tv_speed;// 平均速度

	@ViewInject(R.id.tv_average_iol)
	private TextView tv_average_iol;// 平均油耗

	@ViewInject(R.id.tv_mile)
	private TextView tv_mile;// 里程

	@ViewInject(R.id.tv_date)
	private TextView tv_date;// 时间

	private DeteBroadcastReceiver broad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		httpBiz = new HttpBiz(this);
		initView();
	}

	private void initView() {
		left_action.setText(R.string.back);
		title.setText(R.string.mycar);
		right_action.setText(R.string.mycar_list);

		initData();
	}

	private void initData() {
		if (isLogined() && hasDevice()) {
			tv_detdction_plate.setText(loginMessage.getCarManager().getPlate());
			if (hasBrandIcon()) {
				XUtilsImageLoader.getxUtilsImageLoader(this,
						R.drawable.tianjiacar_img2x, img_car_logo,
						API.CSH_GET_IMG_BASE_URL
								+ loginMessage.getCarManager().getBrand()
										.getBrandIcon());
			} else {
				img_car_logo.setImageResource(R.drawable.main_logo);
			}
		}

		getCarReport();
	}

	/**
	 * 获取车辆报告
	 */
	private void getCarReport() {
		if (hasDevice()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("mobile", loginMessage.getMobile());
			params.addBodyParameter("cid", loginMessage.getCarManager().getId());
			params.addBodyParameter("type", "day");
			// 下次年检时间yyyy-MM-dd
			params.addBodyParameter("time",
					StringUtil.getDate(StringUtil.getLastDate(), "yyyy-MM-dd"));
			ProgrosDialog.openDialog(this);
			httpBiz.httPostData(10000, API.REPORT_MAIN, params, this);
		}
	}

	@Override
	public void receive(int type, String data) {
		super.receive(type, data);
		Log.i("result", "====报告数据===" + data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 10000:
			parseReport(data);
			break;
		case 400:
			showToast(R.string.FAIL);
			break;
		}
	}

	/**
	 * 报告数据解析
	 * 
	 * @param data
	 */
	private void parseReport(String data) {
		try {
			JSONObject json = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess, json.optString("state"),
					true)) {
				json = json.getJSONObject("data");

				String avgSpeed = json.optString("avgSpeed");
				String oil = json.optString("oil");
				String licheng = json.optString("mile");

				String time = json.optString("feeTime");
				String fee = json.optString("fee");
				String avgOil = json.optString("avgOil");
				Log.i("zzqq", "carreport===good1");
				String drivingScore = json.optString("drivingScore");
				Log.i("zzqq", "carreport===good2");
				// rid = json.optInt("rid");
				Log.i("zzqq", "carreport===good3");
				String status = json.optString("status");

				if (StringUtil.isEmpty(oil)) {
					oil_wear.setText("--");
				} else {
					oil_wear.setText(oil);
				}
				if (StringUtil.isEmpty(avgOil)) {
					tv_average_iol.setText("--" + "升/百公里");
				} else {
					tv_average_iol.setText(avgOil + "升/百公里");
				}
				if (StringUtil.isEmpty(avgSpeed)) {
					tv_speed.setText("--");
				} else {
					tv_speed.setText(avgSpeed);
				}
				if (StringUtil.isEmpty(time)) {
					tv_date.setText("--");
				} else {
					tv_date.setText(time + "分钟");
				}
				if (StringUtil.isEmpty(licheng)) {
					tv_mile.setText("--");
				} else {
					tv_mile.setText(licheng + "公里");
				}
				tv_trip_date.setText(StringUtil.getDate(
						StringUtil.getLastDate(), "yyyy-MM-dd"));

			} else if (StringUtil.isEquals(API.returnRelogin,
					json.optString("state"), true)) {
				ReLoginDialog.getInstance(this).showDialog(
						json.optString("message"));
			} else {
				showToast(json.optString("message"));
			}
		} catch (Exception e) {
		}
	}

	@OnClick({ R.id.left_action, R.id.right_action, R.id.btn_security_scan,
			R.id.rl_trip_date })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:// 返回
			finish();
			break;
		case R.id.right_action:// 车辆列表
			startActivity(new Intent(CarDetectionActivity.this,
					CarManagerActivity.class));
			break;

		case R.id.btn_security_scan:// 安全扫描
			if (hasDevice()) {
				startActivity(new Intent(CarDetectionActivity.this,
						SecurityScanActivity.class));
			}else{
				showToast("未绑定设备");
			}
			break;
		case R.id.rl_trip_date:// 车辆报告
			startActivity(new Intent(CarDetectionActivity.this,
					CarReportActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (broad == null) {
			broad = new DeteBroadcastReceiver();
		}

		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		registerReceiver(broad, intentFilter);
	}

	private class DeteBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.CAR_MANAGER_REFRESH, true)) {
				System.out.println("SUCCESS====" + "默认车辆更新");
				initView();
			}
		}

	}

}
