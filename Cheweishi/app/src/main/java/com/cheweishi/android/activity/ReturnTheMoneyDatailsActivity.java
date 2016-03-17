package com.cheweishi.android.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.RechargeActivity.MyBroadcastReceiver;
import com.cheweishi.android.adapter.TelephonEchargeDetailsAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.InsuranceInfo;
import com.cheweishi.android.entity.ReturnTheMoneyInfo;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.utils.CommonUtil;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/*****
 * 返费详情
 * 
 * @author Administrator
 * 
 */

public class ReturnTheMoneyDatailsActivity extends BaseActivity implements
		OnRefreshListener2<ListView>, OnClickListener {

	private static final int RETURNT_CODE = 20001;
	private static final int INSURANCE_CODE = 100001;
	// 获得title控件
	@ViewInject(R.id.title)
	private TextView mTitle;
	// 获得返回控件
	@ViewInject(R.id.left_action)
	private Button mLeft;

	// 获得刷新加载的listview
	@ViewInject(R.id.returnthemoney_refteshlistview)
	private PullToRefreshListView pullToRefreshListView;
	private ListView listView;
	private List<ReturnTheMoneyInfo> list;
	private List<ReturnTheMoneyInfo> mList;
	// 话费详情adapter
	private TelephonEchargeDetailsAdapter telephonEchargeDetailsAdapter;

	@ViewInject(R.id.returnmoney_linearlayout_nodata)
	private LinearLayout mLinearLayout;
	private TextView textView;
	// 加載的页面数量
	private int page = 1;

	private int isone = 0;

	@ViewInject(R.id.bt_insurance_buy)
	private Button bt_insurance_buy;
	@ViewInject(R.id.tv_insurance_name)
	private TextView tv_insurance_name;
	@ViewInject(R.id.tv_insurance_desc)
	private TextView tv_insurance_desc;
	private View headView;
	private MyBroadcastReceiver broad;
	private boolean isNet = false;
	private boolean addFlag = false;
	InsuranceInfo insuranceInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_return_the_money_datails);
		headView = LayoutInflater.from(this).inflate(R.layout.insurance_buy,
				null);
		ViewUtils.inject(this, headView);
		ViewUtils.inject(this);
		initListView();

	}

	private void initListView() {
		// TODO Auto-generated method stub
		pullToRefreshListView.setOnRefreshListener(this);

		listView = pullToRefreshListView.getRefreshableView();
		if (addFlag == false) {
			addFlag = true;
			listView.addHeaderView(headView);
		}
		listView.setDividerHeight(0);
		if (loginMessage.getAccount().getInsurance() == 0) {
			bt_insurance_buy.setText(R.string.insurance_buy);
			bt_insurance_buy.setTextColor(getResources().getColor(
					R.color.main_blue));
			bt_insurance_buy
					.setBackgroundResource(R.drawable.insurance_buy_now);
		} else if (loginMessage.getAccount().getInsurance() == 1) {
			bt_insurance_buy.setText(R.string.insurance_still_protected);
			// bt_insurance_buy.setBackgroundResource(R.color.green);
			bt_insurance_buy.setTextColor(getResources()
					.getColor(R.color.white));
			bt_insurance_buy
					.setBackgroundResource(R.drawable.btn_insurance_pressed);
		} else {
			headView.setVisibility(View.GONE);
			listView.removeHeaderView(headView);
		}
		init();
	}

	// 初始化内容
	private void init() {
		// TODO Auto-generated method stub
		mTitle.setText("余额详情");
		mLeft.setText(getString(R.string.back));
		mList = new ArrayList<ReturnTheMoneyInfo>();
		mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ReturnTheMoneyDatailsActivity.this.finish();
			}
		});

		request();
	}

	/***
	 * 下拉刷新
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (CommonUtil.getNowNetWorkState(this)) {
			page = 1;
			if (list != null) {
				list.clear();
			}
			if (mList != null) {
				mList.clear();
			}
			isone = 0;
			isNet = false;
			listView.removeFooterView(textView);
			request();
		} else {
			showToast(R.string.the_current_network);
			pullToRefreshListView.onRefreshComplete();
		}
	}

	/***
	 * 上拉加载更多
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (CommonUtil.getNowNetWorkState(this)) {
			if (!isNet) {
				page++;
				request();
			} else {
				handler.sendEmptyMessage(1);
			}
		} else {
			showToast(R.string.the_current_network);
			pullToRefreshListView.onRefreshComplete();
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			pullToRefreshListView.onRefreshComplete();
		};

	};

	// 网络请求方法
	private void request() {
		RequestParams params = new RequestParams(HTTP.UTF_8);
		RequestParams params_insurance = new RequestParams(HTTP.UTF_8);

		// 判断是否登陆
		if (isLogined()) {
			setDate(params);
			params_insurance.addBodyParameter("uid", loginMessage.getUid());
			params_insurance.addBodyParameter("key", loginMessage.getKey());
			params_insurance.addBodyParameter("aid", loginMessage.getAccount()
					.getAid());
			params_insurance.addBodyParameter("type", loginMessage.getAccount()
					.getInsurance() + "");
			HttpBiz httpBiz = new HttpBiz(this);
			ProgrosDialog.openDialog(this);
			httpBiz.httPostData(RETURNT_CODE, API.MyMoney_URL, params, this);
			httpBiz.httPostData(INSURANCE_CODE, API.MyMoney_Insurance_URL,
					params_insurance, this);
		}

	}

	// 设置提交参数
	private void setDate(RequestParams params) {
		// TODO Auto-generated method stub
		params.addBodyParameter("uid", loginMessage.getUid());
		params.addBodyParameter("key", loginMessage.getKey());
		params.addBodyParameter("account", loginMessage.getAccount().getAid());
		params.addBodyParameter("type", "fee");
		params.addBodyParameter("page", "" + page);
		params.addBodyParameter("size", "10");
	}

	@Override
	public void receive(int code, String data) {
		super.receive(code, data);
		ProgrosDialog.closeProgrosDialog();
		switch (code) {
		case 400:
			System.out.println("SUCCESS================haha1");
			headView.setVisibility(View.GONE);
			listView.removeHeaderView(headView);
			showToast(R.string.server_link_fault);
			break;
		case INSURANCE_CODE:
			parseInsuranceJSON(data);
			break;
		case RETURNT_CODE:
			System.out.println("SUCCESS================haha");
			System.out.println(data);
			pullToRefreshListView.onRefreshComplete();
			try {
				JSONObject jsonObject = new JSONObject(data);
				// 判斷数据的正确性
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnSuccess, true)) {
					JSONObject dataJsonObject = jsonObject
							.optJSONObject("data");
					JSONArray jsonArray = dataJsonObject.optJSONArray("list");
					if (jsonArray != null && jsonArray.length() == 0) {
						// EmptyTools.setEmptyView(this, listView);
						if (page == 1) {
							mLinearLayout.setVisibility(View.VISIBLE);
							pullToRefreshListView.setMode(Mode.DISABLED);
						} else {
							pullToRefreshListView.setMode(Mode.PULL_FROM_END);
							if (isone == 0) {
								textView = new TextView(this);
								textView.setText(getString(R.string.no_more));
								textView.setTextColor(Color.BLACK);
								textView.setPadding(10, 30, 10, 30);
								textView.setGravity(Gravity.CENTER_HORIZONTAL);
								listView.addFooterView(textView);
								isone++;
								isNet = true;
							}
						}
					} else {
						pullToRefreshListView.setMode(Mode.PULL_FROM_END);
						Gson gson = new Gson();
						java.lang.reflect.Type type = new TypeToken<List<ReturnTheMoneyInfo>>() {
						}.getType();
						list = gson.fromJson(dataJsonObject.getString("list"),
								type);
						mList.addAll(list);

					}
					if (telephonEchargeDetailsAdapter == null) {
						telephonEchargeDetailsAdapter = new TelephonEchargeDetailsAdapter(
								ReturnTheMoneyDatailsActivity.this, mList);
						listView.setAdapter(telephonEchargeDetailsAdapter);
					} else {
						telephonEchargeDetailsAdapter.setlist(mList);
					}
				} else {
					if (StringUtil.isEquals(
							jsonObject.optString("operationState"),
							API.returnRelogin, true)) {
						DialogTool.getInstance(this).showConflictDialog();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		default:
			break;
		}
	}

	private void parseInsuranceJSON(String result) {
		if (StringUtil.isEmpty(result)) {
			return;
		}
		System.out.println(result);
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (StringUtil.isEquals(jsonObject.optString("operationState"),
					API.returnSuccess, true)) {
				String dataResult = jsonObject.optString("data");
				Gson gson = new Gson();
				Type type = new TypeToken<InsuranceInfo>() {
				}.getType();
				insuranceInfo = gson.fromJson(dataResult, type);
				if (!StringUtil.isEmpty(insuranceInfo)) {
					if (loginMessage.getAccount().getInsurance() == 0) {
						System.out.println("SUCCESS==========haha");

						tv_insurance_desc.setText("保当前金额" + "|"
								+ insuranceInfo.getNum() + "|"
								+ insuranceInfo.getTotal() + "/年");
					} else {
						tv_insurance_desc.setText(insuranceInfo.getEnd());
					}
					tv_insurance_name.setText(insuranceInfo.getName());
				} else {
					System.out.println("SUCCESS==========haha1");
					headView.setVisibility(View.GONE);
					listView.removeHeaderView(headView);
				}
			} else if (StringUtil.isEquals(
					jsonObject.optString("operationState"), API.returnFail,
					true)) {
				showToast(jsonObject.optJSONObject("data").optString("msg"));
			} else if (StringUtil.isEquals(
					jsonObject.optString("operationState"), API.returnRelogin,
					true)) {
				DialogTool.getInstance(this).showConflictDialog();
			} else if (StringUtil.isEquals(
					jsonObject.optString("operationState"), API.returnDefault,
					true)) {
				showToast(jsonObject.optJSONObject("data").optString("msg"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnClick({ R.id.bt_insurance_buy, R.id.ll_insurance_buy })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_insurance_buy:
			if (loginMessage.getAccount().getInsurance() == 0) {
				turnToInsuranceBuy();
			} else {
				turnToInsurancePolicy();
			}
			break;
		case R.id.ll_insurance_buy:
			if (loginMessage.getAccount().getInsurance() == 1) {
				turnToInsurancePolicy();
			} else {
				// turnToInsuranceBuy();
			}
			break;
		}
	}

	private void turnToInsuranceBuy() {
		Intent intent = new Intent(this, InsuranceBuyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("insuranceInfo", insuranceInfo);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void turnToInsurancePolicy() {
		Intent intent = new Intent(this, InsurancePolicyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("insuranceInfo", insuranceInfo);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (broad == null) {
			broad = new MyBroadcastReceiver();
		}

		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		registerReceiver(broad, intentFilter);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			Constant.EDIT_FLAG = false;
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.INSURANCE_REFRESH, true)) {
				System.out.println("SUCCESS====" + "保险更新");
				list.clear();
				mList.clear();
				page = 1;
				initListView();
			}

		}
	}
}
