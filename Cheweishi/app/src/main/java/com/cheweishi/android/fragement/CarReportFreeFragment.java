package com.cheweishi.android.fragement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.CarReportActivity;
import com.cheweishi.android.activity.CarReportFreeAddActivity;
import com.cheweishi.android.adapter.CarReportFreeAdapter;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.Car;
import com.cheweishi.android.entity.CarReportFreeVO;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.http.SimpleHttpUtils;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.ShareTools;
import com.cheweishi.android.tools.TextViewTools;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.WrapContentHeightViewPager;
import com.lidroid.xutils.http.RequestParams;

/**
 * 报告费用
 * 
 * @author zhangq
 * 
 */
public class CarReportFreeFragment extends BaseFragment {
	private WrapContentHeightViewPager mViewPager;
	private LinearLayout lLayoutPoints;
	private Button ibtnClick;
	private TextView tvShow;
	private TextView tvTotal;
	private TextView tvMessage;
	private TextView tvBelow;

	private String time;
	private String id;
	private int rid;
	private String money;
	private String shareUrl;
	private String shareIcon;
	private String status;
	// item对象数组
	private List<CarReportFreeVO> datas = new ArrayList<CarReportFreeVO>();
	// 一页viewpager的适配器
	private CarReportFreeAdapter arReportFreeAdapter;
	// 一页viepager的item个数
	private int pagerCount = 6;// 每页个数
	private PagerAdapter pagerAdapter;
	private int curPager = 0;

	/**
	 * 圆点相关
	 */
	private List<ImageView> imgLists = new ArrayList<ImageView>();
	private int prePosition;
	private int curPosition;

	private int colorOriage;

	// viewpager内容适配
	private List<GridView> lists = new ArrayList<GridView>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("zzqq", "--free time--" + System.currentTimeMillis());
		time = getArguments().getString("time");
		rid = getArguments().getInt("rid");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_report_free, container,
				false);

		mViewPager = (WrapContentHeightViewPager) view
				.findViewById(R.id.viewpager);
		/**
		 * 根据手机实际密度设置viewpager行数
		 */
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Activity.WINDOW_SERVICE);
		Display dp = wm.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		dp.getMetrics(outMetrics);
		if (outMetrics.heightPixels <= 840) {
			mViewPager.setRowNumber(1);
			pagerCount = 3;
		}

		lLayoutPoints = (LinearLayout) view.findViewById(R.id.lLayout_points);
		tvTotal = (TextView) view.findViewById(R.id.tv_speed);
		tvMessage = (TextView) view.findViewById(R.id.tv_congestionTime);
		tvBelow = (TextView) view.findViewById(R.id.tv_app);
		tvShow = (TextView) view.findViewById(R.id.tv_speedFlag);
		ibtnClick = (Button) view.findViewById(R.id.btnShare);

		tvShow.setText(mContext.getString(R.string.report_your_day_free));
		tvShow.setVisibility(View.VISIBLE);
		TextViewTools.setTextViewFontsStyle(mContext, tvBelow);
		ibtnClick.setOnClickListener(onClickLister);
		mViewPager.setOnPageChangeListener(onPagerChangeListener);

		colorOriage = mContext.getResources().getColor(
				R.color.orange_text_color);
		initData();
		return view;
	}

	OnPageChangeListener onPagerChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			curPosition = position;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == 0 && curPosition != prePosition) {
				imgLists.get(curPosition).setImageResource(
						R.drawable.point_orange);
				imgLists.get(prePosition).setImageResource(
						R.drawable.point_gray);
				prePosition = curPosition;
			}

		}
	};

	OnClickListener onClickLister = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnShare:
				// tvShow;
				String content = mContext
						.getString(R.string.report_your_day_free)
						+ tvTotal.getText().toString()
						+ tvMessage.getText().toString()
						+ tvBelow.getText().toString();
				ShareTools.showShare(mContext, mContext
						.getString(R.string.report_are_your_car_connect),
						content, shareUrl, shareIcon);
				break;

			default:
				break;
			}
		}
	};

	private void initView() {
		int itemNum = datas.size();
		if (lists.size() > 0) {
			lists.clear();
		}

		int pager = itemNum / pagerCount;
		// pager = pager > 0 ? pager : 0;
		int rest = itemNum % pagerCount;
		for (int i = 0; i <= pager; i++) {
			List<CarReportFreeVO> subList = null;
			if (i < pager) {
				subList = datas.subList(i * pagerCount, i * pagerCount
						+ pagerCount);
			} else {
				subList = datas.subList(i * pagerCount, i * pagerCount + rest);
			}

			if (subList != null && subList.size() > 0) {
				GridView mGridView = new GridView(mContext);
				mGridView.setNumColumns(3);
				mGridView.setGravity(Gravity.CENTER);
				mGridView.setSelector(new ColorDrawable(
						android.R.color.transparent));
				LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				mGridView.setLayoutParams(lp);
				mGridView.setId(5000 + i);
				mGridView.setOnItemClickListener(onItemClickListener);

				arReportFreeAdapter = new CarReportFreeAdapter(subList,
						mContext);
				mGridView.setAdapter(arReportFreeAdapter);
				lists.add(mGridView);
			}
		}

		pagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View view, Object obj) {
				return view == obj;
			}

			@Override
			public int getCount() {
				return lists.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				try {
					container.removeView(lists.get(position));
				} catch (Exception e) {
				}
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View g = lists.get(position);
				container.addView(g);
				return g;
			}
		};
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(curPager);

		/**
		 * 圆点
		 */
		pager = rest == 0 ? pager - 1 : pager;
		addPoints(pager);
	}

	/**
	 * viewpager item点击事件
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {
			/**
			 * <p>
			 * parent 是gridview 自定义id为5000+pager
			 * </p>
			 * position是item在gridview里面的位置
			 */
			int clickPosition = (parent.getId() - 5000) * pagerCount + position;
			curPager = clickPosition / pagerCount;
			CarReportFreeVO vo = datas.get(clickPosition);

			int len = datas.size();
			Intent intent = new Intent(mContext, CarReportFreeAddActivity.class);
			if (clickPosition == len - 1) {
				// 点击最后一项
				intent.putExtra("index", "add");
				intent.putExtra("rid", rid);
			} else if (clickPosition == 0) {
				// 点击第一项
				intent.putExtra("index", "oil");
				intent.putExtra("money", money);
			} else {
				// 点击中间项
				intent.putExtra("index", "edit");
				intent.putExtra("itemId", vo.getId());
				intent.putExtra("vo", vo);
			}
			intent.putExtra("id", id);
			intent.putExtra("time", time);
			startActivityForResult(intent, 1000);
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			initData();
		}
	}

	private void addPoints(int pager) {
		prePosition = curPager;
		curPosition = 0;
		if (imgLists.size() > 0) {
			imgLists.clear();
			lLayoutPoints.removeAllViews();
		}

		for (int i = 0; i <= pager; i++) {
			ImageView imgPoint = new ImageView(mContext);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			imgPoint.setPadding(5, 0, 5, 0);
			if (i == curPager) {
				imgPoint.setImageResource(R.drawable.point_orange);
			} else {
				imgPoint.setImageResource(R.drawable.point_gray);
			}
			imgPoint.setLayoutParams(params);
			lLayoutPoints.addView(imgPoint);
			imgLists.add(imgPoint);
		}

		if (pager == 0) {
			imgLists.get(0).setVisibility(View.INVISIBLE);
		}
	}

	private void initData() {
		// if (!MyHXSDKHelper.getInstance().isLogined()) {
		// Toast.makeText(mContext, "当前未登录", Toast.LENGTH_SHORT).show();
		// return;
		// }
		LoginMessage message = LoginMessageUtils.getLoginMessage(mContext);
		Car car = message.getCar();
		if (car == null) {
			return;
		}

		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", message.getUid());
		params.addBodyParameter("key", message.getKey());
		params.addBodyParameter("cid", car.getCid());
		params.addBodyParameter("type", "fee");
		// 下次年检时间yyyy-MM-dd time
		params.addBodyParameter("time", time);
		params.addBodyParameter("rid", String.valueOf(rid));
		// String url = API.REPORT_FREE_URL + "?uid=" + message.getUid() +
		// "&key="
		// + message.getKey() + "&cid=" + car.getCid() + "&type=" + "fee"
		// + "&time=" + time + "&rid=" + rid;
		// Log.i("zzqq", "---free url----" + url);
		SimpleHttpUtils utils = new SimpleHttpUtils(mContext, params,
				API.REPORT_FREE_URL, handler);
		utils.PostHttpUtils();
	}

	private String total;
	private String maxType;
	private String percent;
	private String max;
	private String oil = "0";

	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			((CarReportActivity) mContext).disMissCustomDialog();
			if (datas.size() > 0) {
				datas.clear();
			}

			// 第一项 燃油项
			CarReportFreeVO vo = new CarReportFreeVO();
			vo.setsType("oil");
			vo.setMoney("¥" + oil);
			datas.add(vo);
			// 最后一个item 添加项
			vo = new CarReportFreeVO();
			vo.setsType("add");
			datas.add(vo);

			if (msg.what == 400) {
				toastMessage(mContext.getString(R.string.server_link_fault));
				setViewAfterNet();
				return;
			}
			String str = (String) msg.obj;
			Log.i("zzqq", "--report free---" + str);

			try {
				JSONObject json = new JSONObject(str);

				if ("SUCCESS"
						.equalsIgnoreCase(json.getString("operationState"))) {

					json = json.getJSONObject("data");
					total = json.getString("total");
					id = json.getString("id");
					percent = json.getString("percent");
					max = json.getString("max");
					oil = json.getString("oil");
					status = json.getString("status");
					money = oil + "";
					maxType = json.getString("maxType");
					shareUrl = json.getString("shareUrl");
					shareIcon = json.getString("shareIcon");

					//
					datas.get(0).setMoney("¥" + oil);

					// 中间item项
					List<CarReportFreeVO> subDatas = new ArrayList<CarReportFreeVO>();
					JSONArray array = json.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						json = array.getJSONObject(i);
						vo = new CarReportFreeVO();
						vo.setAddr(json.getString("addr"));
						vo.setFid(json.getInt("fid"));
						vo.setId(json.getInt("id"));
						vo.setLatlng(json.getString("latlng"));
						vo.setMoney("¥" + json.getDouble("value"));
						vo.setsType(json.getString("type"));
						vo.setTime(json.getString("time"));
						subDatas.add(vo);
					}

					datas.addAll(1, subDatas);

					tvMessage.setText(getMessage(maxType, max, percent));
					tvBelow.setText(getBelowText(total));
				} else if ("RELOGIN".equalsIgnoreCase(json
						.getString("operationState"))) {
					DialogTool.getInstance(mContext).showConflictDialog();
				} else {
					json = json.getJSONObject("data");
					toastMessage(json.getString("msg"));
				}
			} catch (Exception e) {
				// toastMessage(mContext.getString(R.string.no_result));
			}

			setViewAfterNet();
		}

		private void setViewAfterNet() {
			tvTotal.setText(getValue(total));
			if (!"1".equals(status)) {
				ibtnClick.setBackgroundResource(R.drawable.shaiyishai_nodata);
				ibtnClick.setClickable(false);
				ibtnClick.setTextColor(mContext.getResources().getColor(
						R.color.huise));
			}

			initView();
		}

	};

	private void toastMessage(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}

	private SpannableString getMessage(String type, String money, String percent) {
		String s1 = mContext.getString(R.string.report_most_free)
				+ getType(type);
		String s2 = getValue(money);
		String s3 = mContext.getString(R.string.report_beat_country);
		String s4 = getPercent(percent);
		String s5 = mContext.getString(R.string.report_car_friends);
		int point1 = s1.length();
		int point2 = point1 + s2.length();
		int point3 = point2 + s3.length();
		int point4 = point3 + s4.length();
		SpannableString builder = new SpannableString(s1 + s2 + s3 + s4 + s5);

		builder.setSpan(new ForegroundColorSpan(colorOriage), point1, point2,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(new ForegroundColorSpan(colorOriage), point3, point4,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return builder;
	}

	private String getType(String type) {
		// park、clean、road、maintain、insurance、fine
		if ("park".equals(type)) {
			return mContext.getString(R.string.report_park);
		} else if ("clean".equals(type)) {
			return mContext.getString(R.string.report_wash);
		} else if ("road".equals(type)) {
			return mContext.getString(R.string.report_road);
		} else if ("maintain".equals(type)) {
			return mContext.getString(R.string.report_maintenance);
		} else if ("insurance".equals(type)) {
			return mContext.getString(R.string.report_insure);
		} else if ("fine".equals(type)) {
			return mContext.getString(R.string.report_fine);
		} else if ("oil".equals(type)) {
			return mContext.getString(R.string.report_oil);
		}
		return mContext.getString(R.string.report_oil);
	}

	private String getBelowText(String total) {
		double cost = StringUtil.getDouble(total.replace(",", "").trim());
		String value = mContext.getString(R.string.report_myroad_myplace);
		if (50 <= cost && cost < 300) {
			value = mContext.getString(R.string.report_time_is_money);
		} else if (cost >= 300) {
			value = mContext.getString(R.string.report_hao_you_hu);
		}
		return value;
	}

	private String getValue(String value) {
		if (StringUtil.isEmpty(value)) {
			return "¥0";
		}
		return "¥" + value;
	}

	private String getPercent(String value) {
		if (StringUtil.isEmpty(value)) {
			return "0%";
		}
		return value;
	}

	public void updateData(String date) {
		time = date;
		initData();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}

}
