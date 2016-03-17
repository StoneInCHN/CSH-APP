package com.cheweishi.android.fragement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.CarReportActivity;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.http.SimpleHttpUtils;
import com.cheweishi.android.interfaces.CarReportListener;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CarReportViewPager;
import com.cheweishi.android.widget.ReportCircleView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;

public class CarReportMainFragment extends BaseFragment {
	private CarReportViewPager viewPagerBelow;
	private ReportCircleView circleView;// 选中的circleView
	private String time;
	// 圆圈位置状态
	private int offsetDegree;

	// String url = "http://115.28.161.11:8080/XAI/app/t_cws/appGainReport.do";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// time = getArguments().getString("time");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		time = ((CarReportActivity) mContext).getSTime();
		View view = inflater.inflate(R.layout.fragment_carreport_main,
				container, false);
		viewPagerBelow = (CarReportViewPager) view
				.findViewById(R.id.viewpager_below);
		viewPagerBelow.setOnPageChangeListener(onPagerChangeListener);
		createMyCircle();
		initData();
		return view;
	}

	/**
	 * 网络请求
	 */
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
		Log.i("zzqq", "time==" + time);
		// ?uid=30&key=bed3eb2d-3dda-4240-92ce-8ffe97a06a55&cid=274&type=day&time=2015-05-18
		SimpleHttpUtils utils = new SimpleHttpUtils(mContext, params,
				API.REPORT_MAIN, handler);
		// API
		utils.PostHttpUtils();
	}

	/**
	 * 填入ReportCircleView的默认数组
	 */
	private String[] values = { "", "", "", "", "", "", "" };
	DecimalFormat df = new DecimalFormat("0.00");

	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			((CarReportActivity) mContext).disMissCustomDialog();
			int rid = 0;
			if (msg.what == 400) {
				((CarReportActivity) mContext).setInfoFromMain(false, -1);
				toastMessage(mContext.getString(R.string.server_link_fault));
				((CarReportActivity) mContext).setInfoFromMain(true, rid);
				circleView.setValues(values);
				return;
			}
			String str = (String) msg.obj;
			Log.i("zzqq", "carreport===" + str);
			try {
				JSONObject json = new JSONObject(str);
				String resultStr = json.optString("state");
				if (API.returnSuccess.equalsIgnoreCase(json.optString("state"))) {
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
					rid = json.optInt("rid");
					Log.i("zzqq", "carreport===good3");
					String status = json.optString("status");
					if ("0".equals(status)) {
						throw new RuntimeException("break try{}");
					}

					values[0] = isStringNull(oil);
					values[1] = isStringNull(avgOil);
					values[2] = isStringNull(avgSpeed);
					values[3] = isStringNull(time);
					values[4] = isStringNull(licheng);
					values[5] = isStringNull(fee);
					values[6] = isStringNull(drivingScore);
				} else if (StringUtil.isEquals(resultStr, API.returnRelogin,
						true)) {
					DialogTool.getInstance(mContext).showConflictDialog();
				} else {
					showToast(json.optString("message"));
				}
				Log.i("zzqq", "carreport===good");
			} catch (Exception e) {
				for (int i = 0; i < 7; i++) {
					values[i] = "";
				}
				// toastMessage(mContext.getString(R.string.no_result));
			}
			((CarReportActivity) mContext).setInfoFromMain(true, rid);
			circleView.setValues(values);
		}
	};

	private void toastMessage(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}

	private String isStringNull(String str) {
		if (str == null || "".equals(str) || "null".equalsIgnoreCase(str)
				|| "nan".equalsIgnoreCase(str)) {
			return "0";
		}
		return str;
	}

	private List<View> itemViews = new ArrayList<View>();

	private void createMyCircle() {
		Log.i("zzqq", "-----createMyCircle------");
		if (circleView != null) {
			offsetDegree = (int) circleView.overDegree;
		}
		ReportCircleView circle1 = new ReportCircleView(mContext, offsetDegree);
		ReportCircleView circle2 = new ReportCircleView(mContext, offsetDegree);
		ReportCircleView circle3 = new ReportCircleView(mContext, offsetDegree);

		if (itemViews.size() > 0) {
			itemViews.clear();
		}
		itemViews.add(circle1);
		itemViews.add(circle2);
		itemViews.add(circle3);

		viewPagerBelow.setAdapter(pagerAdapter);
		viewPagerBelow.setCurrentItem(1);
		circleView = circle2;
	}

	/**
	 * 跟新数据
	 */
	@Override
	public void updateData(String date) {
		time = date;
		createMyCircle();
		if (isAfterToday(date)) {
			return;
		}
		initData();
	}

	/**
	 * 当前日期是否在今天之后
	 * 
	 * @param sDate
	 * @return
	 */
	private boolean isAfterToday(String sDate) {
		Date d = StringUtil.getDate(sDate, "yyyy-MM-dd");
		Date today = new Date();
		return d.after(today);
	}

	PagerAdapter pagerAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View imgCircle = itemViews.get(position);
			container.addView(imgCircle);
			viewPagerBelow.setObjectForPosition(imgCircle, position);
			return imgCircle;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(itemViews.get(position));
		}

		@Override
		public int getCount() {
			return itemViews == null ? 0 : itemViews.size();
		}
	};

	OnPageChangeListener onPagerChangeListener = new OnPageChangeListener() {
		private int itemPosition;

		@Override
		public void onPageSelected(int position) {
			itemPosition = position;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int status) {
			Log.i("zzqq", "--itemPosition---" + itemPosition);
			if (itemPosition == 0 && status == 0) {
				reportListener.changeDate(CarReportListener.INDEX_RIGHT);
			} else if (itemPosition == 2 && status == 0) {
				reportListener.changeDate(CarReportListener.INDEX_LEFT);
			}

		}
	};

	public void setCarReportListener(CarReportListener listener) {
		this.reportListener = listener;
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("zzqq", "---onPause----");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("zzqq", "------onResume------");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
		Log.i("zzqq", "------onDestroy------");
	}

}
