package com.cheweishi.android.fragement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.CarReportActivity;
import com.cheweishi.android.adapter.CarReportTimeAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CarReportTimeInfo;
import com.cheweishi.android.entity.CarReportTimeStrInfo;
import com.cheweishi.android.http.MyHttpUtils;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.tools.ShareTools;
import com.cheweishi.android.tools.TextViewTools;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CarReportTimeView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 驾驶时间
 * 
 * @author mingdasen
 * 
 */
public class CarReportDriveTimeFragment extends BaseFragment {
	private String time;
	private int rid;
	@ViewInject(R.id.horScrollview)
	private HorizontalScrollView scrollView;
	@ViewInject(R.id.linearLayout)
	private LinearLayout linearLayout;
	@ViewInject(R.id.ll_listView)
	private LinearLayout ll_listView;
	private List<CarReportTimeInfo> infos;
	private List<CarReportTimeInfo> listInfos;

	@ViewInject(R.id.tv_speedFlag)
	private TextView tv_mileFalg;
	@ViewInject(R.id.tv_speed)
	private TextView tv_dayMile;
	@ViewInject(R.id.tv_congestionTime)
	private TextView tv_congestion;

	@ViewInject(R.id.tv_app)
	private TextView tv_Evaluation;
	@ViewInject(R.id.tv_prompt)
	private TextView tv_prompt;

	@ViewInject(R.id.listView)
	private ListView listView;

	@ViewInject(R.id.img_kuang)
	private ImageView img_kuang;
	@ViewInject(R.id.tv_noData)
	private TextView tv_noData;

	@ViewInject(R.id.btnShare)
	private Button btn_share;
	private CarReportTimeView timeView;
	private String hisMinute = "";
	private String total = "";
	private float maxTime = 0;
	private String percent = "";
	private String str1 = "";
	private String str2 = "";
	private String str3 = "";
	private String status = "";
	private String recordDate = "";
	private String shareUrl = "";
	private String shareTitle = "";
	private String shareContent = "";
	private String shareIcon = "";
	public Dialog dialog;
	private CarReportTimeAdapter adapter;
	private List<CarReportTimeStrInfo> list;
	private String startTime;
	private String endTime;
	private int type = 0;
	private LayoutParams lp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		time = getArguments().getString("time");
		rid = getArguments().getInt("rid");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.carreport_mile_fragment,
				container, false);
		ViewUtils.inject(this, view);
		intitView(view);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		getResData();
		initData();
	}
	/**
	 * 获取资源数据
	 */
	private void getResData() {
		str1 = mContext.getResources().getString(
				R.string.drive_time_results_str1);
		str2 = mContext.getResources().getString(
				R.string.drive_time_results_str2);
		str3 = mContext.getResources().getString(
				R.string.drive_time_results_str3);
		shareTitle = mContext.getResources().getString(R.string.share_title);
	}

	/**
	 * 加载设置布局
	 * 
	 * @param view
	 */
	private void intitView(View view) {
		list = new ArrayList<CarReportTimeStrInfo>();

		timeView = new CarReportTimeView(mContext);
		if (linearLayout.getChildCount() > 0) {
			linearLayout.removeView(timeView);
			linearLayout.addView(timeView);
		} else {
			linearLayout.addView(timeView);
		}

		lp = new RelativeLayout.LayoutParams(
				(int) ((ScreenTools.getScreentWidth((Activity) mContext)) * 0.25),
				(int) ((ScreenTools.getScreentHeight((Activity) mContext)) * 0.28f));
		lp.setMargins(
				(int) ((ScreenTools.getScreentWidth((Activity) mContext)) * 0.70),
				0, 0, 0);
		ll_listView.setLayoutParams(lp);
		lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.setMargins(
				0,
				(int) ((ScreenTools.getScreentHeight((Activity) mContext)) * 0.28f / 2 - 20),
				0, 0);
		tv_noData.setLayoutParams(lp);
		lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp.setMargins(
				(int) ((ScreenTools.getScreentWidth((Activity) mContext)) * 0.15),
				10, 0, 0);
		tv_prompt.setLayoutParams(lp);
		lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				(int) ((ScreenTools.getScreentHeight((Activity) mContext)) * 0.28f));
		img_kuang.setLayoutParams(lp);
		adapter = new CarReportTimeAdapter(mContext, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClick);
		setViewData();
	}

	/**
	 * 初始化界面数据
	 */
	private void setViewData() {
		tv_noData.setVisibility(View.GONE);
		ll_listView.setVisibility(View.VISIBLE);
		tv_mileFalg.setVisibility(View.VISIBLE);
		tv_congestion.setVisibility(View.VISIBLE);
		tv_Evaluation.setVisibility(View.VISIBLE);
		tv_mileFalg.setText(R.string.drive_time_sameday);
		tv_dayMile.setText("0" + mContext.getString(R.string.drive_time_unit));
		tv_congestion.setText(str1 + str2 + str3);
		tv_Evaluation.setText(R.string.drive_time_evaluation1);
		btn_share.setBackgroundResource(R.drawable.shaiyishai_nodata);
		btn_share.setTextColor(mContext.getResources().getColor(R.color.huise));
		btn_share.setEnabled(false);
	}

	/**
	 * 列表item点击事件
	 */
	OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			type = position;
			adapter.setSelectItem(type);
			setDrawView();
		}
	};

	private void initData() {
		if (!MyHttpUtils.isNetworkAvailable(mContext)) {
			showToast(R.string.network_isnot_available);
			status = "0";
			setMileView();
			return;
		}
		if (isLogined() && hasCar()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("key", loginMessage.getKey());
			params.addBodyParameter("cid", loginMessage.getCar().getCid());
			params.addBodyParameter("rid", rid + "");
			params.addBodyParameter("type", "time");
			params.addBodyParameter("time", time);
			httpBiz = new HttpBiz(mContext);
			httpBiz.httPostData(10009, API.DRIVING_MILE_URL, params, this);
		}
	}

	public void receive(int type, String data) {
		Log.i("result", "=="+data);
		((CarReportActivity) mContext).disMissCustomDialog();
		switch (type) {
		case 10009:
			parseJSONData(data);
			break;
		case 400:
			status = "0";
			showToast(R.string.server_link_fault);
			setNetData();
			break;
		default:
			break;
		}
	};

	/**
	 * 数据解析
	 * 
	 * @param data
	 */
	private void parseJSONData(String data) {
		if (data == null || data.equals("")) {
			status = "0";
			showToast(R.string.no_result);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(data);
				String statu = jsonObject.optString("operationState");
				if (StringUtil.isEquals("SUCCESS", statu, true)) {
					JSONObject object = jsonObject.optJSONObject("data");
					total = object.optString("total");
					percent = object.optString("percent");
					maxTime = StringUtil.getFloat(object.optString("max"));
					hisMinute = object.optString("minute");
					status = object.optString("status");
					recordDate = object.optString("recordDate");
					shareUrl = object.optString("shareUrl");
					shareIcon = object.optString("shareIcon");
					JSONArray jsonArray = object.optJSONArray("list");
					CarReportTimeInfo timeInfo;
					infos = new ArrayList<CarReportTimeInfo>();
					JSONObject object2;
					for (int i = 0; i < jsonArray.length(); i++) {
						timeInfo = new CarReportTimeInfo();
						object2 = jsonArray.optJSONObject(i);
						timeInfo.setStart(StringUtil.getInt(object2
								.optString("start")));
						timeInfo.setFeetime(StringUtil.getInt(object2
								.optString("feetime")));
						timeInfo.setEnd(StringUtil.getInt(object2
								.optString("end")));
						infos.add(timeInfo);
					}
				} else if (StringUtil.isEquals("FAIL", statu, true)) {
					status = "0";
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (StringUtil.isEquals("RELOGIN", statu, true)) {
					status = "0";
					DialogTool.getInstance(mContext).showConflictDialog();
				} else if (StringUtil.isEquals("DEFAULT", statu, true)) {
					// dialog.dismiss();
					status = "0";
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}

			} catch (JSONException e) {
				status = "0";
				e.printStackTrace();
			}
		}
		setNetData();
	}

	/**
	 * 显示网络请求数据
	 */
	private void setNetData() {
		lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		if (status.equals("0")) {
			lp.setMargins(0, 0, 0, 0);
		} else {
			lp.setMargins(
					0,
					0,
					(int) ((ScreenTools.getScreentWidth((Activity) mContext)) * 0.245),
					0);
		}
		scrollView.setLayoutParams(lp);

		listInfos = new ArrayList<CarReportTimeInfo>();
		listInfos.clear();
		if (infos != null && infos.size() > 0) {
			for (int i = infos.size() - 1; i >= 0; i--) {
				listInfos.add(infos.get(i));
			}
//			adapter.setSelectItem(type);
			addlistView();
		} else {
			list.clear();
			CarReportTimeStrInfo timeStrInfo = new CarReportTimeStrInfo();
			timeStrInfo.setEnd("");
			timeStrInfo.setStart("");
			list.add(timeStrInfo);
//			adapter.setSelectItem(type);
			adapter.setList(list);
			adapter.notifyDataSetChanged();
		}
		setMileView();
	}

	private void setDrawView() {
		Bundle bundle = new Bundle();
		bundle.putString("hisTime", hisMinute);
		bundle.putString("date", recordDate);
		bundle.putString("status", status);
		bundle.putInt("type", type);
		if (type == 0) {
			tv_prompt.setVisibility(View.GONE);
			scrollView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					return true;
				}
			});
		} else {
			tv_prompt.setVisibility(View.VISIBLE);
			scrollView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					return false;
				}
			});
		}
		// 滚动默认位置
		new Handler().postDelayed((new Runnable() {
			@Override
			public void run() {
				// scrollView.scrollTo(timeView.getFirstRect(), 0);
				// scrollView.smoothScrollBy(timeView.getFirstRect(), 0);
				scrollView.smoothScrollTo(timeView.getFirstRect(), 0);
			}
		}), 5);
		timeView.setInvalidate(bundle, listInfos);
	}

	protected void addlistView() {
		list.clear();
		CarReportTimeStrInfo timeInfo;
		for (int i = 0; i < listInfos.size(); i++) {
			timeInfo = new CarReportTimeStrInfo();
			int hour = listInfos.get(i).getStart() / 3600;
			String strhour = "";
			if (hour > 9) {
				strhour = hour + "";
			} else {
				strhour = "0" + hour;
			}
			int Minute = listInfos.get(i).getStart() % 3600 / 60;
			String strminute = "";
			if (Minute > 9) {
				strminute = "" + Minute;
			} else {
				strminute = "0" + Minute;
			}
			startTime = strhour + ":" + strminute;
			timeInfo.setStart(startTime);
			hour = listInfos.get(i).getEnd() / 3600;
			if (hour > 9) {
				strhour = hour + "";
			} else {
				strhour = "0" + hour;
			}
			Minute = listInfos.get(i).getEnd() % 3600 / 60;
			if (Minute > 9) {
				strminute = "" + Minute;
			} else {
				strminute = "0" + Minute;
			}
			endTime = strhour + ":" + strminute;
			timeInfo.setEnd(endTime);
			list.add(timeInfo);
		}
		adapter.setList(list);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 显示数据
	 */
	protected void setMileView() {
		if (status.equals("0")) {
			listView.setVisibility(View.GONE);
			tv_noData.setVisibility(View.VISIBLE);
			ll_listView.setVisibility(View.GONE);
			setDrawView();
			tv_mileFalg.setVisibility(View.VISIBLE);
			// tv_mileFalg.setText("您当日驾驶时长为");
			tv_dayMile.setText("0");
			tv_congestion.setVisibility(View.INVISIBLE);
			tv_Evaluation.setVisibility(View.INVISIBLE);
			btn_share.setBackgroundResource(R.drawable.shaiyishai_nodata);
			btn_share.setTextColor(mContext.getResources().getColor(
					R.color.huise));
			btn_share.setEnabled(false);
		} else {
			listView.setVisibility(View.VISIBLE);
			tv_noData.setVisibility(View.GONE);
			ll_listView.setVisibility(View.VISIBLE);
			setDrawView();
			tv_mileFalg.setVisibility(View.VISIBLE);
			tv_congestion.setVisibility(View.VISIBLE);
			tv_Evaluation.setVisibility(View.VISIBLE);
			// tv_mileFalg.setText("您当日驾驶时长为");
			int hour = StringUtil.getInt(total) / 60;
			int minute = StringUtil.getInt(total) % 60;
			if (hour == 0) {
				tv_dayMile.setText(minute + ""
						+ mContext.getString(R.string.drive_time_unit));
			} else {
				tv_dayMile.setText("" + hour
						+ mContext.getString(R.string.drive_time_hour) + minute
						+ mContext.getString(R.string.drive_time_minute));
			}
			initColorTextView();
			setEvaluation();
			btn_share.setBackgroundResource(R.drawable.shaiyishai);
			btn_share.setTextColor(mContext.getResources().getColor(
					R.color.orange_text_color));
			btn_share.setEnabled(true);
			shareContent = tv_mileFalg.getText().toString()
					+ tv_dayMile.getText().toString()
					+ tv_congestion.getText().toString()
					+ tv_Evaluation.getText().toString();
			btn_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					ShareTools.showShare(mContext, shareTitle, shareContent,
							shareUrl, shareIcon);
				}
			});
		}
	}

	/**
	 * 设置评价
	 */
	private void setEvaluation() {
		int totalT = 0;
		if (total != null && !total.equals("")) {
			totalT = StringUtil.getInt(total);
		}
		if (totalT >= 0 && totalT < 15) {
			tv_Evaluation.setText(R.string.drive_time_evaluation1);
		} else if (totalT >= 15 && totalT < 60) {
			tv_Evaluation.setText(R.string.drive_time_evaluation2);
		} else if (totalT >= 60 && totalT < 240) {
			tv_Evaluation.setText(R.string.drive_time_evaluation3);
		} else if (totalT >= 240) {
			tv_Evaluation.setText(R.string.drive_time_evaluation4);
		}
		TextViewTools.setTextViewFontsStyle(mContext, tv_Evaluation);
	}

	/**
	 * 设置TextView字体颜色
	 */
	private void initColorTextView() {
		SpannableString sp;
		String time;
		int hour = (int) (maxTime / 60);
		int minute = (int) (maxTime % 60);
		if (hour == 0) {
			time = "" + minute + mContext.getString(R.string.drive_time_unit);
		} else {
			time = "" + hour + mContext.getString(R.string.drive_time_hour) + minute
					+ mContext.getString(R.string.drive_time_minute);
		}
		sp = new SpannableString(str1 + time + str2 + percent + str3);
		sp.setSpan(
				new ForegroundColorSpan(mContext.getResources().getColor(
						R.color.orange_text_color)), str1.length(),
				(str1 + time).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		sp.setSpan(
				new ForegroundColorSpan(mContext.getResources().getColor(
						R.color.orange_text_color)),
				(str1 + time + str2).length(),
				(str1 + time + str2 + percent).length(),
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		tv_congestion.setText(sp);
	}

	/**
	 * 变更数据
	 */
	public void updateData(String date) {
		time = date;
		type = 0;
		initData();
	}
}
