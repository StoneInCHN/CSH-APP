package com.cheweishi.android.fragement;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.CarReportActivity;
import com.cheweishi.android.activity.DrvingBehaviorActivity;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.entity.DrvingScore;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.RequestParams;

public class DrivingBehaviorFragment extends BaseFragment {
	private String time;
	private TextView tv_drivingMile;
	private TextView tv_drivingTime;
	private TextView tv_drivingScore;
	private TextView rapidAcceleration;
	private TextView tv_brokenOn;
	private TextView tv_suddenTurn;
	private TextView tv_fatigueDriving;
	private LinearLayout ll_rapidAcceleration;
	private LinearLayout ll_brokenOn;
	private LinearLayout ll_suddenTurn;
	private LinearLayout ll_fatigueDriving;
	private Context mContext;
	private DrvingScore drvingScore;
	private int rid;
	private MyBroadcastReceiver broad;
	private View view;

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (broad == null) {
			broad = new MyBroadcastReceiver();
		}

		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		if (!StringUtil.isEmpty(mContext)) {
			mContext.registerReceiver(broad, intentFilter);
		}
	}

	private void initViews() {
		httpBiz = new HttpBiz(mContext);
		tv_drivingMile = (TextView) view.findViewById(R.id.tv_drivingMile);
		tv_drivingTime = (TextView) view.findViewById(R.id.tv_drivingTime);
		tv_drivingScore = (TextView) view.findViewById(R.id.tv_drivingScore);
		rapidAcceleration = (TextView) view
				.findViewById(R.id.rapidAcceleration);
		tv_brokenOn = (TextView) view.findViewById(R.id.tv_brokenOn);
		tv_suddenTurn = (TextView) view.findViewById(R.id.tv_suddenTurn);
		tv_fatigueDriving = (TextView) view
				.findViewById(R.id.tv_fatigueDriving);
		ll_rapidAcceleration = (LinearLayout) view
				.findViewById(R.id.ll_rapidAcceleration);
		ll_brokenOn = (LinearLayout) view.findViewById(R.id.ll_brokenOn);
		ll_suddenTurn = (LinearLayout) view.findViewById(R.id.ll_suddenTurn);
		ll_fatigueDriving = (LinearLayout) view
				.findViewById(R.id.ll_fatigueDriving);
	}

	private void setListeners() {
		// ll_rapidAcceleration.setOnClickListener(onClickListener);
		// ll_brokenOn.setOnClickListener(onClickListener);
		// ll_suddenTurn.setOnClickListener(onClickListener);
		// ll_fatigueDriving.setOnClickListener(onClickListener);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		time = getArguments().getString("time");
		rid = getArguments().getInt("rid");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_driving, container, false);

		mContext = getActivity();
		initViews();
		setListeners();
		initData();
		return view;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(mContext, DrvingBehaviorActivity.class);
			switch (arg0.getId()) {
			case R.id.ll_rapidAcceleration:
				intent.putExtra("pageIndex", 0);
				break;
			case R.id.ll_brokenOn:
				intent.putExtra("pageIndex", 1);
				break;
			case R.id.ll_suddenTurn:
				intent.putExtra("pageIndex", 2);
				break;
			case R.id.ll_fatigueDriving:
				intent.putExtra("pageIndex", 3);
				break;
			default:
				intent.putExtra("pageIndex", 0);
				break;
			}
			intent.putExtra("time", time);
			startActivity(intent);

		}

	};

	private void initData() {
		System.out.println("报告===================" + time);
		if (!isLogined()) {

			return;
		}
		CarManager car = loginMessage.getCarManager();
		if (hasCar() == false) {
			System.out.println("报告===================" + car.getId() + "_"
					+ car.getDevice() + "_" + car.getPlate());
			return;
		}
		System.out.println("报告======" + time);
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", loginMessage.getUid());
		params.addBodyParameter("mobile", loginMessage.getMobile());
		params.addBodyParameter("cid", car.getId());
		params.addBodyParameter("type", "day");
		// 下次年检时间yyyy-MM-dd
		params.addBodyParameter("time", time);
		httpBiz.httPostData(10001, API.REPORT_MAIN, params, this);
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		((CarReportActivity) mContext).disMissCustomDialog();
		switch (type) {
		case 10001:
			parseJson(data);
			break;
		case 400:
			setNull();
			break;
		}
	}

	DecimalFormat df = new DecimalFormat("0.00");

	private void parseJson(String result) {
		System.out.println("驾驶行为=====" + result);
		if (result == null) {
			setNull();
			showToast(R.string.data_fail);
		} else {

			try {
				JSONObject jsonObject = new JSONObject(result);
				String resultStr = jsonObject.optString("state");
				if (resultStr.equals(API.returnSuccess)) {
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<DrvingScore>() {
					}.getType();
					drvingScore = gson.fromJson(jsonObject.optString("data"),
							type);
					tv_drivingMile.setText(drvingScore.getMile() + "km");
					tv_drivingTime.setText(((int) drvingScore.getFeeTime())
							/ 60
							+ mContext.getResources().getString(R.string.hour)
							+ (((int) drvingScore.getFeeTime()) % 60)
							+ mContext.getResources()
									.getString(R.string.minute));
					tv_drivingScore
							.setText(drvingScore.getDrivingScore()
									+ mContext.getResources().getString(
											R.string.score));
					rapidAcceleration.setText(drvingScore
							.getRapidAcceleration() + "");
					tv_brokenOn.setText(drvingScore.getBrokenOn() + "");
					tv_suddenTurn.setText(drvingScore.getSuddenTurn() + "");
					tv_fatigueDriving.setText(drvingScore.getFatigueDriving()
							+ "");

				} else if (StringUtil.isEquals(resultStr, API.returnRelogin,
						true)) {
					DialogTool.getInstance(mContext).showConflictDialog();
				} else {
					showToast(jsonObject.optString("message"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void updateData(String date) {
		// setNull();
		time = date;
		initData();
	}

	private void setNull() {
		tv_drivingMile.setText("0km/h");
		tv_drivingTime.setText("0"
				+ mContext.getResources().getString(R.string.hour));
		tv_drivingScore.setText("0"
				+ mContext.getResources().getString(R.string.minute));
		rapidAcceleration.setText("0");
		tv_brokenOn.setText("0");
		tv_suddenTurn.setText("0");
		tv_fatigueDriving.setText("0");
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			System.out.println("SUCCESS========" + "判断列表" + "_"
					+ Constant.CURRENT_REFRESH);
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.LOGIN_REFRESH, true)) {
				System.out.println("SUCCESS====" + "列表更新");
				initViews();

			}
		}
	}
}
