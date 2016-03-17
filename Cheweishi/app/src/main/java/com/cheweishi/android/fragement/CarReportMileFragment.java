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
import com.cheweishi.android.entity.CarReportMileInfo;
import com.cheweishi.android.entity.CarReportTimeStrInfo;
import com.cheweishi.android.http.MyHttpUtils;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.tools.ShareTools;
import com.cheweishi.android.tools.TextViewTools;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CarReportMileView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 驾驶里程报告
 * 
 * @author mingdasen
 * 
 */
public class CarReportMileFragment extends BaseFragment {
	private String time;
	@ViewInject(R.id.horScrollview)
	private HorizontalScrollView scrollView;
	@ViewInject(R.id.linearLayout)
	private LinearLayout linearLayout;
	@ViewInject(R.id.ll_listView)
	private LinearLayout ll_listView;
	private List<CarReportMileInfo> infos;
	private List<CarReportMileInfo> listInfos;
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
	private CarReportMileView mileView;
	private int rid;
	private String hisMile = "";
	private String total = "";
	private float maxMile = 0;
	private float minMile = 0;
	private String percent = "";
	private String str1 = "";// "其中最长连续驾驶";
	private String str2 = "";// "km,击败了全国";
	private String str3 = "";// "的车友";
	private String status = "";
	private String recordDate = "";
	private String shareUrl = "";
	private String shareIcon = "";
	private String shareTitle = "";// "嘿~你的车联网了么";
	private String shareContent = "";
	private String max = "0.00";
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
		str1 = mContext.getResources().getString(R.string.mile_hint1);
		str2 = mContext.getResources().getString(R.string.mile_hint2);
		str3 = mContext.getResources().getString(R.string.mile_hint3);
		shareTitle = mContext.getResources().getString(
				R.string.mile_share_title);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.carreport_mile_fragment,
				container, false);
		ViewUtils.inject(this, view);
		intitView(view);
		initData();
		return view;
	}

	/**
	 * 布局初始化
	 * 
	 * @param view
	 */
	private void intitView(View view) {
		list = new ArrayList<CarReportTimeStrInfo>();
		mileView = new CarReportMileView(mContext);
		if (linearLayout.getChildCount() > 0) {
			linearLayout.removeView(mileView);
			linearLayout.addView(mileView);
		} else {
			linearLayout.addView(mileView);
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
		tv_mileFalg.setText(R.string.mile_sameday);
		tv_dayMile.setText("0" + mContext.getString(R.string.mile_unit));
		tv_congestion.setText(str1 + str2 + str3);
		tv_Evaluation.setText(R.string.mile_evaluation1);
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

	/**
	 * 获取数据
	 */
	private void initData() {
		if (!MyHttpUtils.isNetworkAvailable(mContext)) {
			((CarReportActivity) mContext).disMissCustomDialog();
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
			params.addBodyParameter("type", "mile");
			params.addBodyParameter("time", time);
			httpBiz = new HttpBiz(mContext);
			httpBiz.httPostData(10008, API.DRIVING_MILE_URL, params, this);
		}
	}

	public void receive(int type, String data) {
		Log.i("result", "=="+data);
		((CarReportActivity) mContext).disMissCustomDialog();
		switch (type) {
		case 10008:
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
	 * 数据解析处理
	 * 
	 * @param data
	 */
	private void parseJSONData(String data) {
		if (null == data || "".equals(data)) {
			status = "0";
			showToast(R.string.server_link_fault);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(data);
				String statu = jsonObject.optString("operationState");
				if (StringUtil.isEquals("SUCCESS", statu, true)) {
					JSONObject object = jsonObject.optJSONObject("data");
					total = object.optString("total");
					percent = object.optString("percent");
					max = object.optString("max");
					maxMile = StringUtil.getFloat(max);
					minMile = StringUtil.getFloat(object.optString("min"));
					hisMile = object.optString("mile");
					status = object.optString("status");
					recordDate = object.optString("recordDate");
					shareUrl = object.optString("shareUrl");
					shareIcon = object.optString("shareIcon");
					JSONArray jsonArray = object.optJSONArray("list");
					CarReportMileInfo mileInfo;
					infos = new ArrayList<CarReportMileInfo>();
					JSONObject object2;
					for (int i = 0; i < jsonArray.length(); i++) {
						mileInfo = new CarReportMileInfo();
						object2 = jsonArray.optJSONObject(i);
						mileInfo.setStart(StringUtil.getInt(object2
								.optString("start")));
						mileInfo.setDriverMile(StringUtil.getFloat(object2
								.optString("driverMile")));
						mileInfo.setEnd(StringUtil.getInt(object2
								.optString("end")));
						infos.add(mileInfo);
					}
				} else if (StringUtil.isEquals("FAIL", statu, true)) {
					status = "0";
					showToast(jsonObject.optJSONObject("data").optString("msg"));
					// Toast.makeText(
					// mContext,
					// , Toast.LENGTH_LONG).show();
				} else if (StringUtil.isEquals("RELOGIN", statu, true)) {
					status = "0";
					// showDialog();
					DialogTool.getInstance(mContext).showConflictDialog();
				} else if (StringUtil.isEquals("DEFAULT", statu, true)) {
					status = "0";
					// dialog.dismiss();
					showToast(jsonObject.optJSONObject("data").optString("msg"));
					// Toast.makeText(
					// mContext,
					// , Toast.LENGTH_LONG).show();
				}

			} catch (JSONException e) {
				status = "0";
				e.printStackTrace();
			}
		}
		setNetData();
	}

	/**
	 * 网络数据加载完成处理
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

		listInfos = new ArrayList<CarReportMileInfo>();
		if (infos != null && infos.size() > 0) {
			listInfos.clear();
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
		bundle.putString("hisMile", hisMile);
		bundle.putFloat("maxMile", maxMile);
		bundle.putFloat("minMile", minMile);
		bundle.putString("date", recordDate);
		bundle.putString("status", status);
		bundle.putInt("type", type);
		mileView.setInvalidate(bundle, listInfos);
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
				// scrollView.scrollTo(mileView.getFirstRect(), 0);
				scrollView.smoothScrollTo(mileView.getFirstRect(), 0);
			}
		}), 0);
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
			// tv_mileFalg.setText("您当日驾驶里程为");
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
			// tv_mileFalg.setText("您当日驾驶里程为");
			tv_dayMile.setText(total + mContext.getString(R.string.mile_unit));
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
	 * 添加listView数据
	 */
	protected void addlistView() {
		list.clear();
		CarReportTimeStrInfo timeStrInfo;
		for (int i = 0; i < listInfos.size(); i++) {
			timeStrInfo = new CarReportTimeStrInfo();
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
			timeStrInfo.setStart(startTime);
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
			timeStrInfo.setEnd(endTime);
			list.add(timeStrInfo);
		}
		adapter.setList(list);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 设置评价
	 */
	private void setEvaluation() {
		float totalT = 0;
		if (total != null && !total.equals("")) {
			totalT = StringUtil.getFloat(total);
		}
		if (totalT >= 0 && totalT < 10) {
			tv_Evaluation.setText(R.string.mile_evaluation1);
		} else if (totalT >= 10 && totalT < 60) {
			tv_Evaluation.setText(R.string.mile_evaluation2);
		} else if (totalT >= 60 && totalT < 200) {
			tv_Evaluation.setText(R.string.mile_evaluation3);
		} else if (totalT >= 200) {
			tv_Evaluation.setText(R.string.mile_evaluation4);
		}
		TextViewTools.setTextViewFontsStyle(mContext, tv_Evaluation);
	}

	/**
	 * 设置TextView字体颜色
	 */
	private void initColorTextView() {
		SpannableString sp;
		sp = new SpannableString(str1 + max + str2 + percent + str3);
		sp.setSpan(
				new ForegroundColorSpan(mContext.getResources().getColor(
						R.color.orange_text_color)), str1.length(),
				(str1 + max).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		sp.setSpan(
				new ForegroundColorSpan(mContext.getResources().getColor(
						R.color.orange_text_color)),
				(str1 + max + str2).length(),
				(str1 + max + str2 + percent).length(),
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		tv_congestion.setText(sp);
	}

	public void updateData(String date) {
		time = date;
		type = 0;
		initData();
	}
}
