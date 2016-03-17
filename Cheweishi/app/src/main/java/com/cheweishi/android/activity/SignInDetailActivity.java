package com.cheweishi.android.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.SignInCalendarAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.entity.Score;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.CustomProgressDialog;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.WrapSizeGridView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 签到详情
 * 
 * @author zhangq
 * 
 */
public class SignInDetailActivity extends BaseActivity {
	/**
	 * 签到动作
	 */
	private static final int INDEX_SIGNIN = 1000;
	/**
	 * 获取签到详情
	 */
	private static final int INDEX_DETAIL = 1001;

	@ViewInject(R.id.title)
	private TextView tvTitle;
	@ViewInject(R.id.left_action)
	private TextView tvLeft;
	@ViewInject(R.id.right_action)
	private TextView tvRight;
	@ViewInject(R.id.tv_showdate)
	private TextView tvShowDate;// 显示时间
	@ViewInject(R.id.tv_pre)
	private TextView tvPre;
	@ViewInject(R.id.tv_next)
	private TextView tvNext;
	@ViewInject(R.id.tv_signin)
	private TextView tvSignIn;
	@ViewInject(R.id.gridview)
	private WrapSizeGridView mGridView;
	@ViewInject(R.id.llayout_kuang)
	private LinearLayout lLayoutKuang;

	private SignInCalendarAdapter gAdapter;

	//
	private int iMonthViewCurrentMonth = 0; // 当前视图月
	private int iMonthViewCurrentYear = 0; // 当前视图年

	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

	private LoginMessage mess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		ViewUtils.inject(this);
		httpBiz = new HttpBiz(this);
		init();
	}

	private void init() {
		tvRight.setVisibility(View.INVISIBLE);
		tvLeft.setText(this.getString(R.string.back));
		tvTitle.setText(this.getString(R.string.signin_title));

		tvSignIn.setOnClickListener(clickListener);
		tvLeft.setOnClickListener(clickListener);
		tvPre.setOnClickListener(clickListener);
		tvNext.setOnClickListener(clickListener);

		setGirdView();
		mess = LoginMessageUtils.getLoginMessage(this);
		getMonthSignIn();

	}

	/**
	 * 获得日历详情
	 */
	private void getMonthSignIn() {
		// if (!MyHXSDKHelper.getInstance().isLogined()) {
		// Toast.makeText(SignInDetailActivity.this, "账号未登录",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		String str = sdf.format(calStartDate.getTime());
		tvShowDate.setText(str);

		RequestParams params = new RequestParams();
		String uid = mess.getUid();
		String key = mess.getKey();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//
		int year = calStartDate.get(Calendar.YEAR);
		int month = calStartDate.get(Calendar.MONTH);

		Calendar calendarStart = Calendar.getInstance();
		Calendar calendarEnd = Calendar.getInstance();

		calendarStart.set(Calendar.YEAR, year);
		calendarStart.set(Calendar.MONTH, month);
		calendarStart.set(Calendar.DAY_OF_MONTH, 1);
		if (month >= 11) {
			calendarEnd.set(Calendar.YEAR, year + 1);
			calendarEnd.set(Calendar.MONTH, 0);
			calendarEnd.set(Calendar.DAY_OF_MONTH, 1);
		}
		int today = calendarEnd.get(Calendar.DAY_OF_MONTH);
		calendarEnd.set(Calendar.DAY_OF_MONTH, today + 1);

		params.addBodyParameter("uid", uid);
		params.addBodyParameter("key", key);
		params.addBodyParameter("start", sdf.format(calendarStart.getTime()));
		params.addBodyParameter("end", sdf.format(calendarEnd.getTime()));

		httpBiz.getPostData(INDEX_DETAIL, API.SIGNIN_DETAIL, params, this,
				httpBiz.POSTNUM, this);
	}

	// 根据改变的日期更新日历
	// 填充日历控件用
	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);// 得到当前日历显示的年

		// 星期一是2 星期天是1 填充剩余天数
		int iDay = 0;
		int iFirstDayOfWeek = Calendar.MONDAY;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

	}

	private void showMonth() {
		getMonthSignIn();
	}

	private void parseJsonDetail(String data) {

		lists = new ArrayList<Date>();
		try {
			JSONObject json = new JSONObject(data);
			if ("SUCCESS".equalsIgnoreCase(json.getString("operationState"))) {
				json = json.getJSONObject("data");
				JSONArray array = json.getJSONArray("sign");
				for (int i = 0; i < array.length(); i++) {
					JSONObject time = array.getJSONObject(i);
					long lTime = StringUtil.getLong(time.getString("time"));
					lists.add(new Date(lTime));
				}
				setGirdView();
				if (isTodaySigned()) {
					tvSignIn.setBackgroundResource(R.drawable.sign_button2);
					tvSignIn.setText("已签到");
					tvSignIn.setClickable(false);
				} else {
					tvSignIn.setBackgroundResource(R.drawable.qingdaoxingqing_button);
					tvSignIn.setText("今日签到");
				}
			} else if ("RELOGIN".equalsIgnoreCase(json
					.getString("operationState"))) {
				DialogTool.getInstance(SignInDetailActivity.this)
						.showConflictDialog();
			} else {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
			}
		} catch (Exception e) {
			// toastMessage(getString(R.string.no_result));
		}

		dissPDialog();
	}

	private void parseJsonSignIn(String data) {
		Score score = new Score();

		try {
			JSONObject json = new JSONObject(data);
			if ("SUCCESS".equalsIgnoreCase(json.getString("operationState"))) {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));

				try {
					Double scoreNow = json.getDouble("score");
					score.setNow(scoreNow + "");
					mess.setScore(score);
					mess.setSign(1);
					LoginMessageUtils.saveProduct(mess,
							SignInDetailActivity.this);
				} catch (Exception e) {
				}
				calStartDate = Calendar.getInstance();
				getMonthSignIn();

				Intent mIntent = new Intent();
				Constant.CURRENT_REFRESH = Constant.SIGN_REFRESH;
				mIntent.setAction(Constant.REFRESH_FLAG);
				sendBroadcast(mIntent);
			} else if ("RELOGIN".equalsIgnoreCase(json
					.getString("operationState"))) {
				DialogTool.getInstance(SignInDetailActivity.this)
						.showConflictDialog();
			} else {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
			}
		} catch (Exception e) {
		}

	}

	private ArrayList<Date> lists;

	private void setGirdView() {
		Calendar tempSelected = Calendar.getInstance(); // 临时
		tempSelected.setTime(calStartDate.getTime());

		// tempSelected.add(Calendar.MONTH, -1);
		gAdapter = new SignInCalendarAdapter(this, tempSelected, lists);
		mGridView.setAdapter(gAdapter);// 设置菜单Adapter
	}

	// 上一个月
	private void setPrevViewItem() {
		UpdateStartDateForMonth();
		iMonthViewCurrentMonth--;// 当前选择月--
		// 如果当前月为负数的话显示上一年
		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); // 设置月
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear); // 设置年
		showMonth();
	}

	// 下一个月
	private void setNextViewItem() {
		UpdateStartDateForMonth();
		iMonthViewCurrentMonth++;
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		showMonth();
	}

	private void signIn() {
		String uid = mess.getUid();
		String key = mess.getKey();
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", uid);
		params.addBodyParameter("key", key);
		httpBiz.getPostData(INDEX_SIGNIN, API.SIGN_URL, params, this,
				httpBiz.POSTNUM, this);
	}

	@Override
	public void receive(int type, String data) {
		switch (type) {
		case INDEX_DETAIL:
			parseJsonDetail(data);
			break;
		case INDEX_SIGNIN:
			parseJsonSignIn(data);
			break;

		default:
			break;
		}
	}

	private CustomProgressDialog pDialog;

	private void dissPDialog() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.left_action:
				SignInDetailActivity.this.finish();
				break;
			case R.id.tv_signin:
				signIn();
				break;
			case R.id.tv_pre:
				setPrevViewItem();
				break;
			case R.id.tv_next:
				setNextViewItem();
				break;

			default:
				break;
			}
		}

	};

	private boolean isTodaySigned() {
		if (lists == null) {
			return false;
		}
		Date date = Calendar.getInstance().getTime();
		for (Date d : lists) {
			if (d.getYear() == date.getYear()
					&& d.getMonth() == date.getMonth()
					&& d.getDate() == date.getDate()) {
				return true;
			}
		}
		return false;
	}

	public void mesureWidth() {
		Log.i("zzqq",
				"left=" + mGridView.getLeft() + "\nright="
						+ mGridView.getRight());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mesureWidth();
	}

}