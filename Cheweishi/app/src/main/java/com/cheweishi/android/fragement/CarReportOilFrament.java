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
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.tools.ShareTools;
import com.cheweishi.android.tools.TextViewTools;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.AvgSpeedView;
import com.cheweishi.android.widget.AvgSpeedViewY;
import com.cheweishi.android.widget.SpeedFontView;
import com.lidroid.xutils.http.RequestParams;

public class CarReportOilFrament extends BaseFragment {
	private String time;
	private FrameLayout chatLayout;
	private Button btn_share;
	private TextView tv_congestionTime;
	private String consTime = "";
	private String conRate = "";
	private String per = "";
	private String conStr = "其中高耗部分占整个行程";
	private String conStr1 = "";
	private String conStr2 = "";
	private String conStr3 = ",击败了全国";
	private String conStr4 = "的车友。";
	private Context mContext;
	private List<Speed> listSpeed = new ArrayList<Speed>();
	private List<SubSpeed> listSubSpeed = new ArrayList<SubSpeed>();
	private String avgSpeed = "0";
	private TextView tv_speed;
	private TextView tv_noSpeedData;
	private TextView tv_imgRemind;
	TextView tv;
	private TextView tv_speedFlag;
	private HorizontalScrollView noneView;
	private int rid;
	private String shareUrl = "";
	private String imgUrl;
	private ListView speedListView;
	private TextView left_zhanwei;
	private List<CarReportTimeStrInfo> array = new ArrayList<CarReportTimeStrInfo>();
	private RelativeLayout cover_layout;
	private RelativeLayout cover_relativeLayout;
	CarReportTimeAdapter adapter;
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
		speedListView = (ListView) view.findViewById(R.id.speedListView);
		cover_layout = (RelativeLayout) view.findViewById(R.id.cover_layout);
		tv_noSpeedData = (TextView) view.findViewById(R.id.tv_noSpeedData);
		conStr = mContext.getResources().getString(R.string.oil_constr1);
		conStr3 = mContext.getResources().getString(R.string.oil_constr2);
		conStr4 = mContext.getResources().getString(R.string.oil_constr3);
		ViewGroup.LayoutParams lp0 = cover_layout.getLayoutParams();
		ViewGroup.LayoutParams lp3 = tv_noSpeedData.getLayoutParams();
		ViewGroup.LayoutParams lp = speedListView.getLayoutParams();
		lp.height = (int) (ScreenTools.getScreentHeight((Activity) (mContext)) / 4 - speedListView
				.getY());
		lp3.height = (int) (ScreenTools.getScreentHeight((Activity) (mContext)) / 4 - tv_noSpeedData
				.getX());
		tv_noSpeedData.setLayoutParams(lp3);
		lp0.height = (int) (ScreenTools.getScreentHeight((Activity) (mContext)) / 4 - speedListView
				.getY()) + 4;
		cover_layout.setLayoutParams(lp0);
		speedListView.setLayoutParams(lp);
		left_zhanwei = (TextView) view.findViewById(R.id.left_zhanwei);
		ViewGroup.LayoutParams lp1 = left_zhanwei.getLayoutParams();
		lp1.height = (int) (ScreenTools.getScreentHeight((Activity) (mContext)) / 4 - left_zhanwei
				.getY());
		left_zhanwei.setLayoutParams(lp1);
		cover_relativeLayout = (RelativeLayout) view
				.findViewById(R.id.cover_relativeLayout);
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
						if (!((Activity) mContext).isFinishing()) {
							ppX = ScreenTools
									.getScreentWidth((Activity) mContext)
									/ 10
									+ (int) (listSpeed.get(0).getListSubSpeed()
											.get(0).getTime() / 3600)
									* ((ScreenTools
											.getScreentWidth((Activity) mContext) * 3 / 4 - ScreenTools
											.getScreentWidth((Activity) mContext) / 5) - ((ScreenTools
											.getScreentWidth((Activity) mContext) * 3 / 4) % (7 * 30)))
									/ (7 * 30);
							if (ppX > ScreenTools
									.getScreentWidth((Activity) mContext) * 2 / 3) {
								new Handler().postDelayed((new Runnable() {
									@Override
									public void run() {
										noneView.smoothScrollTo(
												ppX
														- (ScreenTools
																.getScreentWidth((Activity) mContext) * 3 / 8),
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
						}
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
						ppX = ScreenTools.getScreentWidth((Activity) mContext)
								/ 8
								+ (int) (listSpeed.get(arg2 - 1).getMaxTime() / 3600)
								* ((ScreenTools
										.getScreentWidth((Activity) mContext) * 4 / 3) - ((ScreenTools
										.getScreentWidth((Activity) mContext) * 4 / 3) % (7 * 30)))
								/ (7 * 30);
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
					}
					circle1.setNewData(arg2 - 1);
					font.setNewData(arg2 - 1);
				}
			}
		});
		btn_share = (Button) view.findViewById(R.id.btnShare);
		tv_speedFlag = (TextView) view.findViewById(R.id.tv_speedFlag);
		tv_speedFlag.setText(R.string.avg_oil);
		tv_imgRemind = (TextView) view.findViewById(R.id.tv_imgRemind);
		btn_share.setOnClickListener(onClickListener);
		tv_congestionTime = (TextView) view
				.findViewById(R.id.tv_congestionTime);
		noneView = (HorizontalScrollView) view.findViewById(R.id.noneView);
		tv_speed = (TextView) view.findViewById(R.id.tv_speed);
		tv_speedFlag.setVisibility(View.VISIBLE);
		// colorRemind = (LinearLayout) view.findViewById(R.id.colorRemind);
		tv = (TextView) view.findViewById(R.id.tv_app);
		tv.setText(R.string.oil_tag1);
		TextViewTools.setTextViewFontsStyle(mContext, tv);// 从assert中获取有资源，获得app的assert，采用getAserts()，通过给出在assert/下面的相对路径。在实际使用中，字体库可能存在于SD卡上，可以采用createFromFile()来替代createFromAsset。
		createMyCircle();
		initData();
		initColorTextView();
	}

	AvgSpeedView circle1;
	AvgSpeedViewY circle2;
	SpeedFontView font;
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
				rp.addBodyParameter("type", "avgOil");
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
						conRate = jsonObjectData.optString("highPercent");
						per = jsonObjectData.optString("percent");
						avgSpeed = jsonObjectData.optString("avgDay");
						shareUrl = jsonObjectData.optString("shareUrl");
						imgUrl = jsonObjectData.optString("shareIcon");
						if (Float.parseFloat(avgSpeed) <= 8.0f) {
							tv.setText(R.string.oil_tag2);
						} else if (Float.parseFloat(avgSpeed) <= 13.0f) {
							tv.setText(R.string.oil_tag3);
						} else {
							tv.setText(R.string.oil_tag1);
						}
						JSONArray jsonArray = jsonObjectData
								.optJSONArray("list");
						if (jsonArray != null && jsonArray.length() > 0) {

							for (int i = (jsonArray.length() - 1); i >= 0; i--) {
								Speed speed = new Speed();
								JSONObject jsonObjectIndex = jsonArray
										.optJSONObject(i);
								speed.setAvgCurrent((float) (jsonObjectIndex
										.optDouble("avgCurrent")) * 6);
								speed.setMaxCurrent((float) (jsonObjectIndex
										.optDouble("maxCurrent")) * 6);
								speed.setStart(jsonObjectIndex.optLong("start"));
								speed.setEnd(jsonObjectIndex.optLong("end"));
								speed.setStartTime(jsonObjectIndex
										.optString("startTime"));
								speed.setMaxTime(jsonObjectIndex
										.optLong("maxTime") * 10);
								speed.setEndTime(jsonObjectIndex
										.optString("endTime"));
								speed.setStartTime(jsonObjectIndex
										.optString("startTime"));
								speed.setEndTime(jsonObjectIndex
										.optString("endTime"));
								speed.setStart(jsonObjectIndex.optLong("start"));
								speed.setEnd(jsonObjectIndex.optLong("end"));
								JSONArray jsonArray1 = jsonObjectIndex
										.optJSONArray("list");
								listSubSpeed = new ArrayList<SubSpeed>();
								CarReportTimeStrInfo carReportTimeStrInfo = new CarReportTimeStrInfo();
								carReportTimeStrInfo.setStart(speed
										.getStartTime());
								carReportTimeStrInfo.setEnd(speed.getEndTime());
								array.add(carReportTimeStrInfo);
								if (jsonArray1 != null
										&& jsonArray1.length() > 0) {
									for (int j = 0; j < jsonArray1.length(); j++) {
										JSONObject jsonObjectIndex1 = jsonArray1
												.optJSONObject(j);
										SubSpeed ss = new SubSpeed();
										ss.setSpeed((float) (jsonObjectIndex1
												.optDouble("oil")) * 6);
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
							if (!((Activity) mContext).isFinishing()) {
								ppX = ScreenTools
										.getScreentWidth((Activity) mContext)
										/ 10
										+ (int) (listSpeed.get(0)
												.getListSubSpeed().get(0)
												.getTime() / 3600)
										* ((ScreenTools
												.getScreentWidth((Activity) mContext) * 3 / 4 - ScreenTools
												.getScreentWidth((Activity) mContext) / 5) - ((ScreenTools
												.getScreentWidth((Activity) mContext) * 3 / 4) % (7 * 30)))
										/ (7 * 30);
								if (ppX > ScreenTools
										.getScreentWidth((Activity) mContext) * 2 / 3) {
									new Handler().postDelayed((new Runnable() {
										@Override
										public void run() {
											noneView.smoothScrollTo(
													ppX
															- (ScreenTools
																	.getScreentWidth((Activity) mContext) * 3 / 8),
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
							}
							tv_congestionTime.setVisibility(View.VISIBLE);
							tv_speed.setText(avgSpeed
									+ mContext.getResources().getString(
											R.string.oil_unit));
							initColorTextView();
							tv.setVisibility(View.VISIBLE);
							btn_share
									.setBackgroundResource(R.drawable.shaiyishai);
							btn_share.setTextColor(mContext.getResources()
									.getColor(R.color.orange_text_color));
							// 新建一个数组适配器ArrayAdapter绑定数据，参数(当前的Activity，布局文件，数据源)
							adapter = new CarReportTimeAdapter(mContext, array);
							speedListView.setAdapter(adapter);
						} else {
							nullFlag = true;
							CarReportTimeStrInfo CarReportTimeStrInfo = new CarReportTimeStrInfo();
							CarReportTimeStrInfo.setStart("");
							CarReportTimeStrInfo.setEnd("");
							array.add(CarReportTimeStrInfo);
							tv_speed.setText(avgSpeed
									+ mContext.getResources().getString(
											R.string.oil_unit));
							initColorTextView();
							tv.setVisibility(View.VISIBLE);
							btn_share
									.setBackgroundResource(R.drawable.shaiyishai);
							btn_share.setTextColor(mContext.getResources()
									.getColor(R.color.orange_text_color));
							tv_imgRemind.setVisibility(View.VISIBLE);
							// 新建一个数组适配器ArrayAdapter绑定数据，参数(当前的Activity，布局文件，数据源)
							adapter = new CarReportTimeAdapter(mContext, array);
							speedListView.setAdapter(adapter);
							adapter.setSelectItem(0);
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

	int ppX;

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

	private void createMyCircle() {
		circle1 = new AvgSpeedView(mContext, listSpeed, true, speedListView,
				left_zhanwei, tv_imgRemind);
		circle2 = new AvgSpeedViewY(mContext, listSpeed, true);
		font = new SpeedFontView(mContext, listSpeed, true);
		circle1.setBackgroundColor(mContext.getResources().getColor(
				R.color.white));
		chatLayout.removeAllViews();
		chatLayout.addView(circle1);
		chatLayout.addView(font);
	}

	public void updateData(String date) {
		listSubSpeed.clear();
		listSpeed.clear();
		array.clear();
		tv_noSpeedData.setVisibility(View.GONE);
		time = date;
		initData();
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
