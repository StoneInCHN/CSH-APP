package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.TelephonEchargeDetailsAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ReturnTheMoneyInfo;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.XCRoundImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 我的余额
 * 
 * @author XMh
 * 
 */
public class PurseBalanceActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {
	private static final int INSURANCE_CODE = 100001;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.ley_top_up)
	private LinearLayout ley_top_up;
	@ViewInject(R.id.xcRoundImg)
	private XCRoundImageView xcRoundImg;
	@ViewInject(R.id.tv_balance_num)
	private TextView tv_balance_num;
	private MyBroadcastReceiver broad;
	// 上拉加载下拉刷新列表
	@ViewInject(R.id.telephonechargedetils_listview)
	private PullToRefreshListView telephonechargedetils_listview;
	@ViewInject(R.id.telephonemoney_linearlayout_nodata)
	private LinearLayout mLinearLayout;
	// item的数据
	private List<ReturnTheMoneyInfo> list = new ArrayList<ReturnTheMoneyInfo>();
	private List<ReturnTheMoneyInfo> mList = new ArrayList<ReturnTheMoneyInfo>();
	// 定义一个私有的余额详情adapter
	private TelephonEchargeDetailsAdapter telephonEchargeDetailsAdapter;
	private Intent intent;
	private int pageNumber = 1;
	private int pageSize = 20;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purse_balance);
		ViewUtils.inject(this);
		init();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broad);
	}

	/**
	 * 初始化视图
	 */
	private void init() {
		title.setText(R.string.purse_my_balance);
		left_action.setText(R.string.back);
		// tv_balance_num.setText(getIntent().getStringExtra("money"));
		telephonechargedetils_listview.setOnRefreshListener(this);
		telephonechargedetils_listview.setMode(Mode.BOTH);
		mListView = telephonechargedetils_listview.getRefreshableView();
		telephonEchargeDetailsAdapter = new TelephonEchargeDetailsAdapter(this,
				list);
		mListView.setAdapter(telephonEchargeDetailsAdapter);
		httpBiz = new HttpBiz(this);
		request();
	}

	@OnClick({ R.id.left_action, R.id.title, R.id.ley_top_up })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:// 返回
			PurseBalanceActivity.this.finish();
			break;
		case R.id.ley_top_up:
			intent = new Intent(PurseBalanceActivity.this, PayActivty.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	// 网络请求方法
	private void request() {
		// ProgrosDialog.openDialog(this);
		RequestParams params_insurance = new RequestParams(HTTP.UTF_8);
		// 判断是否登陆
		if (isLogined()) {
			params_insurance.addBodyParameter("uid", loginMessage.getUid());
			params_insurance.addBodyParameter("mobile",
					loginMessage.getMobile());
			params_insurance.addBodyParameter("pageSize", pageSize + "");
			params_insurance.addBodyParameter("pageNumber", pageNumber + "");
			httpBiz.httPostData(INSURANCE_CODE, API.CSH_REST_OF_MONEY_LIST_URL,
					params_insurance, this);
		}

	}

	/**
	 * 接受服务器返回的JSON字符串
	 */
	@Override
	public void receive(int code, String data) {
		super.receive(code, data);
		ProgrosDialog.closeProgrosDialog();
		telephonechargedetils_listview.onRefreshComplete();
		switch (code) {
		case 400:
			showToast(R.string.server_link_fault);
			break;
		case INSURANCE_CODE:
			parseInsuranceJSON(data);
			break;
		default:
			break;
		}
	}

	/**
	 * 解析JSON字符串
	 * 
	 * @param result
	 */
	private void parseInsuranceJSON(String result) {
		if (StringUtil.isEmpty(result)) {
			return;
		}
		System.out.println("余额========" + result);
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (StringUtil.isEquals(jsonObject.optString("state"),
					API.returnSuccess, true)) {
				String balance = jsonObject.optJSONObject("data").optString(
						"balance");
				if (StringUtil.isEmpty(balance)) {
					tv_balance_num.setText("0.00");
				} else {
					tv_balance_num.setText(balance);
				}
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<List<ReturnTheMoneyInfo>>() {
				}.getType();
				mList = gson.fromJson(jsonObject.optJSONObject("data")
						.optString("walletRecordList"), type);
				if (!StringUtil.isEmpty(mList) && mList.size() > 0) {
					list.addAll(mList);
					telephonEchargeDetailsAdapter.setlist(list);
				}
			} else if (StringUtil.isEquals(jsonObject.optString("state"),
					API.returnRelogin, true)) {
				ReLoginDialog.getInstance(this).showDialog(
						jsonObject.optString("message"));
			} else {
				showToast(jsonObject.optString("message"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		pageNumber = 1;
		if (!StringUtil.isEmpty(list)) {
			list.clear();
		}
		if (!StringUtil.isEmpty(mList)) {
			mList.clear();
		}
		request();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		pageNumber++;
		request();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (broad == null) {
			broad = new MyBroadcastReceiver();
		}
		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		registerReceiver(broad, intentFilter);
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			if (!StringUtil.isEquals(intent.getAction(), Constant.REFRESH_FLAG,
					true)) {
				return;
			}
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.RECHARGE_REFRESH, true)) {
				Constant.EDIT_FLAG = true;

				list.clear();
				pageNumber = 1;
				request();
			} else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.WEIXIN_PAY_REFRESH, true)) {
				Constant.EDIT_FLAG = true;

				list.clear();
				pageNumber = 1;
				request();
			}
		}
	}
}
