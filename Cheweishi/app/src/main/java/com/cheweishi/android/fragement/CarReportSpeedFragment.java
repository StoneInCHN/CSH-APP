package com.cheweishi.android.fragement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.CarReportActivity;
import com.cheweishi.android.activity.LoginActivity;
import com.cheweishi.android.adapter.CarReportTimeAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.entity.CarReportTimeStrInfo;
import com.cheweishi.android.entity.Speed;
import com.cheweishi.android.entity.SubSpeed;
import com.cheweishi.android.interfaces.CarReportListener;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.tools.ShareTools;
import com.cheweishi.android.tools.TextViewTools;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.AvgSpeedView;
import com.cheweishi.android.widget.AvgSpeedViewY;
import com.cheweishi.android.widget.CarReportSpeedViewPager;
import com.cheweishi.android.widget.SpeedFontView;
import com.lidroid.xutils.http.RequestParams;

/***/
public class CarReportSpeedFragment extends BaseFragment {
	private CarReportListener reportListener;
	private CarReportSpeedViewPager viewPagerBelow;
	private String time;
	private FrameLayout chatLayout;
	private Button btn_share;
	private TextView tv_congestionTime;
	private String consTime = "";
	private String conRate = "";
	private String per = "";
	private String conStr = "拥堵状态下的行驶时间为";
	private String conStr1 = "分钟,";
	private String conStr2 = "拥堵系数";
	private String conStr3 = ",击败了全国";
	private String conStr4 = "的车友。";
	private HorizontalScrollView noneView;
	private Context mContext;
	private String avgSpeed = "0";
	private List<Speed> listSpeed = new ArrayList<Speed>();
	private List<SubSpeed> listSubSpeed = new ArrayList<SubSpeed>();
	private TextView tv_speed;
	private TextView tv_noSpeedData;
	private TextView tv_imgRemind;
	private TextView tv_speedFlag;
	private RelativeLayout cover_layout;
	TextView tv;
	private int rid;
	private String shareUrl = "";
	private String imgUrl;
	private ListView speedListView;
	private List<CarReportTimeStrInfo> array = new ArrayList<CarReportTimeStrInfo>();
	private TextView left_zhanwei;
	CarReportTimeAdapter adapter;
	private RelativeLayout cover_relativeLayout;
	public static float XLength;
	public static float XScale;
	public static float RATIO;
	private boolean nullFlag = false;
	private MyBroadcastReceiver broad;
	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		time = getArguments().getString("time");
		rid = getArguments().getInt("rid");

	}

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_carreport_speed, container,
				false);

		initViews();
		return view;
	}

	private void initViews() {
		mContext = getActivity();
		httpBiz = new HttpBiz(mContext);
		chatLayout = (FrameLayout) view.findViewById(R.id.chatLayout);
		tv_noSpeedData = (TextView) view.findViewById(R.id.tv_noSpeedData);
		tv_imgRemind = (TextView) view.findViewById(R.id.tv_imgRemind);
		speedListView = (ListView) view.findViewById(R.id.speedListView);
		cover_layout = (RelativeLayout) view.findViewById(R.id.cover_layout);
		cover_relativeLayout = (RelativeLayout) view
				.findViewById(R.id.cover_relativeLayout);
		conStr = mContext.getResources().getString(R.string.speed_constr1);
		conStr1 = mContext.getResources().getString(R.string.speed_constr2);
		conStr2 = mContext.getResources().getString(R.string.speed_constr3);
		conStr3 = mContext.getResources().getString(R.string.speed_constr4);
		conStr4 = mContext.getResources().getString(R.string.speed_constr5);
		cover_relativeLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		speedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub
				adapter.setSelectItem(arg2);
				if (arg2 == 0) {
					tv_imgRemind.setVisibility(View.INVISIBLE);
					if (nullFlag == false) {
						tv_imgRemind.setVisibility(View.INVISIBLE);
						cover_relativeLayout
								.setOnTouchListener(new OnTouchListener() {

									@Override
									public boolean onTouch(View arg0,
											MotionEvent arg1) {
										// TODO Auto-generated method stub
										return true;
									}
								});

						new Handler().postDelayed((new Runnable() {
							@Override
							public void run() {
								noneView.smoothScrollTo(0, 0);

							}
						}), 5);
						createMyCircle();
					}
				} else {
					tv_imgRemind.setVisibility(View.VISIBLE);
					cover_relativeLayout
							.setOnTouchListener(new OnTouchListener() {

								@Override
								public boolean onTouch(View arg0,
										MotionEvent arg1) {
									// TODO Auto-generated method stub
									return false;
								}
							});
					tv_imgRemind.setVisibility(View.VISIBLE);
					if (!((Activity) mContext).isFinishing()) {
						if ((listSpeed.get(arg2 - 1).getStart() + (listSpeed
								.get(arg2 - 1).getEnd() - listSpeed.get(
								arg2 - 1).getStart()) / 2) > 6 * 3600) {
							new Handler().postDelayed((new Runnable() {
								@Override
								public void run() {

									noneView.smoothScrollTo(
											(int) ((listSpeed.get(arg2 - 1)
													.getStart() + (listSpeed
													.get(arg2 - 1).getEnd() - listSpeed
													.get(arg2 - 1).getStart()) / 2)
													* XScale - 6 * 3600 * XScale),
											0);
								}
							}), 5);
						} else {
							new Handler().postDelayed((new Runnable() {
								@Override
								public void run() {
									noneView.smoothScrollTo(0, 0);
								}
							}), 5);
						}
					} else {
						new Handler().postDelayed((new Runnable() {
							@Override
							public void run() {
								noneView.smoothScrollTo(0, 0);

							}
						}), 5);
					}
					// }
					circle1.setNewData(arg2 - 1);
					font.setNewData(arg2 - 1);
				}
			}
		});
		noneView = (HorizontalScrollView) view.findViewById(R.id.noneView);
		noneView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
				}
				if (MotionEvent.ACTION_MOVE == event.getAction()) {

				}

				return false;
			}
		});
		btn_share = (Button) view.findViewById(R.id.btnShare);
		tv_speedFlag = (TextView) view.findViewById(R.id.tv_speedFlag);
		tv_speedFlag.setText(R.string.avg_speed);
		tv_speedFlag.setVisibility(View.VISIBLE);
		int screenWidth = ScreenTools.getScreentWidth((Activity) mContext);
		int screenHeight = ScreenTools.getScreentHeight((Activity) mContext);
		float ratioWidth = (float) screenWidth / 720;
		float ratioHeight = (float) screenHeight / 1280;
		float RATIO = Math.min(ratioWidth, ratioHeight);
		ViewGroup.LayoutParams lp0 = cover_layout.getLayoutParams();
		ViewGroup.LayoutParams lp1 = tv_noSpeedData.getLayoutParams();
		ViewGroup.LayoutParams lp = speedListView.getLayoutParams();
		lp.height = (int) (ScreenTools.getScreentHeight((Activity) (mContext)) / 4 - speedListView
				.getY());
		lp0.height = (int) (ScreenTools.getScreentHeight((Activity) (mContext))
				/ 4 - speedListView.getY() + 4 * RATIO);
		cover_layout.setLayoutParams(lp0);
		lp1.height = (int) (ScreenTools.getScreentHeight((Activity) (mContext)) / 4 - tv_noSpeedData
				.getX());
		tv_noSpeedData.setLayoutParams(lp1);
		left_zhanwei = (TextView) view.findViewById(R.id.left_zhanwei);
		ViewGroup.LayoutParams lp2 = left_zhanwei.getLayoutParams();
		lp2.height = (int) (ScreenTools.getScreentHeight((Activity) (mContext)) / 4 - left_zhanwei
				.getY());
		left_zhanwei.setLayoutParams(lp2);
		speedListView.setLayoutParams(lp);
		btn_share.setOnClickListener(onClickListener);
		tv_speed = (TextView) view.findViewById(R.id.tv_speed);
		tv_congestionTime = (TextView) view
				.findViewById(R.id.tv_congestionTime);
		tv = (TextView) view.findViewById(R.id.tv_app);
		createMyCircle();
		TextViewTools.setTextViewFontsStyle(mContext, tv);
		initData();
	}

	private int status;
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.btnShare:
				if (status != 0) {
					ShareTools.showShare(mContext, mContext.getResources()
							.getString(R.string.share_title), tv_speedFlag
							.getText().toString()
							+ tv_speed.getText().toString()
							+ tv_congestionTime.getText().toString()
							+ "\n"
							+ tv.getText().toString(), shareUrl, imgUrl);
				}
				break;
			}

		}

	};

	private void initData() {
		if (!isLogined()) {
			Intent intent = new Intent(mContext, LoginActivity.class);
			mContext.startActivity(intent);
		} else {
			if (!hasCar()) {
				showToast(R.string.noCar);
			} else {
				RequestParams rp = new RequestParams();
				rp.addBodyParameter("uid", loginMessage.getUid());
				rp.addBodyParameter("key", loginMessage.getKey());
				rp.addBodyParameter("cid", loginMessage.getCar().getCid());
				rp.addBodyParameter("type", "speed");
				rp.addBodyParameter("time", time);
				rp.addBodyParameter("rid", rid + "");

				httpBiz.httPostData(
						10001,
						"http://115.28.161.11:8080/XAI/app/t_cws/appGainReportDes.do?",
						rp, this);
			}
		}
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
			speedListView.setBackgroundResource(R.color.gray_backgroud);
			tv_noSpeedData.setVisibility(View.VISIBLE);
			tv_speed.setText("0");
			tv_congestionTime.setVisibility(View.INVISIBLE);
			tv_imgRemind.setVisibility(View.INVISIBLE);
			tv.setVisibility(View.INVISIBLE);
			btn_share.setBackgroundResource(R.drawable.shaiyishai_nodata);
			btn_share.setTextColor(mContext.getResources().getColor(
					R.color.gray_normal));
			break;
		}
	}

	DecimalFormat df = new DecimalFormat("0.00");
	int ppX;

	private void parseJson(String result) {
		if (result == null) {
			showToast(R.string.data_fail);
		} else {

			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.optString("operationState").equals("SUCCESS")) {

					JSONObject jsonObjectData = jsonObject
							.optJSONObject("data");
					this.status = jsonObjectData.optInt("status");
					if (jsonObjectData.optInt("status") == 0) {
						speedListView
								.setBackgroundResource(R.color.gray_backgroud);
						tv_noSpeedData.setVisibility(View.VISIBLE);
						tv_speed.setText("0");
						tv_congestionTime.setVisibility(View.INVISIBLE);
						tv_imgRemind.setVisibility(View.INVISIBLE);
						tv.setVisibility(View.INVISIBLE);
						btn_share
								.setBackgroundResource(R.drawable.shaiyishai_nodata);
						btn_share.setTextColor(mContext.getResources()
								.getColor(R.color.gray_normal));

					} else {
						consTime = jsonObjectData.optString("jamTime");
						conRate = jsonObjectData.optString("jamPercent");
						per = jsonObjectData.optString("percent");
						avgSpeed = jsonObjectData.optString("avgDay");
						shareUrl = jsonObjectData.optString("shareUrl");
						imgUrl = jsonObjectData.optString("shareIcon");
						if (Float
								.parseFloat(per.substring(0, per.length() - 1)) / 100f <= 0.2) {
							tv.setText(R.string.speed_tag1);
						} else if (Float.parseFloat(per.substring(0,
								per.length() - 1)) / 100f <= 0.55) {
							tv.setText(R.string.speed_tag2);
						} else {
							tv.setText(R.string.speed_tag3);
						}
						JSONArray jsonArray = jsonObjectData
								.optJSONArray("list");
						if (jsonArray != null && jsonArray.length() > 0) {

							for (int i = (jsonArray.length() - 1); i >= 0; i--) {

								Speed speed = new Speed();
								JSONObject jsonObjectIndex = jsonArray
										.optJSONObject(i);
								speed.setAvgCurrent((float) (jsonObjectIndex
										.optDouble("avgCurrent")));
								speed.setMaxCurrent((float) (jsonObjectIndex
										.optDouble("maxCurrent")));
								speed.setStart(jsonObjectIndex.optLong("start"));
								speed.setEnd(jsonObjectIndex.optLong("end"));
								speed.setStartTime(jsonObjectIndex
										.optString("startTime"));
								speed.setMaxTime(jsonObjectIndex
										.optLong("maxTime"));
								speed.setEndTime(jsonObjectIndex
										.optString("endTime"));
								speed.setStartTime(jsonObjectIndex
										.optString("startTime"));
								speed.setEndTime(jsonObjectIndex
										.optString("endTime"));
								CarReportTimeStrInfo carReportTimeStrInfo = new CarReportTimeStrInfo();
								carReportTimeStrInfo.setStart(speed
										.getStartTime());
								carReportTimeStrInfo.setEnd(speed.getEndTime());
								array.add(carReportTimeStrInfo);
								JSONArray jsonArray1 = jsonObjectIndex
										.optJSONArray("list");

								if (jsonArray1 != null
										&& jsonArray1.length() > 0) {
									listSubSpeed = new ArrayList<SubSpeed>();
									for (int j = 0; j < jsonArray1.length(); j++) {
										JSONObject jsonObjectIndex1 = jsonArray1
												.optJSONObject(j);
										SubSpeed ss = new SubSpeed();
										ss.setSpeed((float) (jsonObjectIndex1
												.optDouble("speed")));
										ss.setTime(jsonObjectIndex1
												.optLong("time"));
										ss.setStatus(jsonObjectIndex1
												.optInt("status"));
										listSubSpeed.add(ss);
									}
									speed.setListSubSpeed(listSubSpeed);
								}
								listSpeed.add(speed);
							}
							tv.setVisibility(View.VISIBLE);
							tv_speed.setText(avgSpeed + "km/h");
							initColorTextView();
							tv.setVisibility(View.VISIBLE);
							btn_share
									.setBackgroundResource(R.drawable.shaiyishai);
							btn_share.setTextColor(mContext.getResources()
									.getColor(R.color.orange_text_color));
							adapter = new CarReportTimeAdapter(mContext, array);
							adapter.setSelectItem(0);
							speedListView.setAdapter(adapter);
						} else {
							nullFlag = true;
							CarReportTimeStrInfo CarReportTimeStrInfo = new CarReportTimeStrInfo();
							CarReportTimeStrInfo.setStart("");
							CarReportTimeStrInfo.setEnd("");
							array.add(CarReportTimeStrInfo);
							adapter = new CarReportTimeAdapter(mContext, array);
							adapter.setSelectItem(0);
							speedListView.setAdapter(adapter);
							tv_speed.setText("0" + "km/h");
						}
					}
				} else if (jsonObject.optString("operationState").equals(
						"RELOGIN")) {
					DialogTool.getInstance(mContext).showConflictDialog();
				} else if (jsonObject.optString("operationState")
						.equals("FAIL")) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			createMyCircle();
		}
	}

	private void initColorTextView() {
		if (isAdded()) {
			SpannableString sp;// = new SpannableString("拥堵状态下的行驶时间为");
			sp = new SpannableString(conStr + consTime + conStr1 + conStr2
					+ conRate + conStr3 + per + conStr4);
			sp.setSpan(
					new ForegroundColorSpan(this.getResources().getColor(
							R.color.orange_text_color)), conStr.length(),
					(conStr + consTime).length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			sp.setSpan(
					new ForegroundColorSpan(this.getResources().getColor(
							R.color.orange_text_color)), (conStr + consTime
							+ conStr1 + conStr2).length(), (conStr + consTime
							+ conStr1 + conStr2 + conRate).length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			sp.setSpan(
					new ForegroundColorSpan(this.getResources().getColor(
							R.color.orange_text_color)),
					(conStr + consTime + conStr1 + conStr2 + conRate + conStr3)
							.length(),
					(conStr + consTime + conStr1 + conStr2 + conRate + conStr3 + per)
							.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			tv_congestionTime.setText(sp);
		}
	}

	private List<View> itemViews = new ArrayList<View>();
	AvgSpeedView circle1;
	AvgSpeedViewY circle2;
	SpeedFontView font;

	private void createMyCircle() {
		circle1 = new AvgSpeedView(mContext, listSpeed, false, speedListView,
				left_zhanwei, tv_imgRemind);
		circle2 = new AvgSpeedViewY(mContext, listSpeed, false);
		font = new SpeedFontView(mContext, listSpeed, false);
		circle1.setBackgroundColor(mContext.getResources().getColor(
				R.color.white));
		chatLayout.removeAllViews();
		chatLayout.addView(circle1, 0);
		chatLayout.addView(font, 1);
	}

	public void updateData(String date) {
		array.clear();
		listSubSpeed.clear();
		listSpeed.clear();
		tv_noSpeedData.setVisibility(View.GONE);
		tv_congestionTime.setVisibility(View.VISIBLE);
		time = date;
		new Handler().postDelayed((new Runnable() {
			@Override
			public void run() {
				noneView.smoothScrollTo(0, 0);
			}
		}), 5);
		initData();
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
