package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.RechargeDialog;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.PayUtils;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class RechargeActivity extends BaseActivity implements OnClickListener {

	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private TextView left_action;
	@ViewInject(R.id.tv_recharge)
	private TextView tv_recharge;
	@ViewInject(R.id.bt_recharge)
	private Button bt_recharge;
	private int accountNum = 100;
	@ViewInject(R.id.tv_rechargeDetail)
	private TextView tv_rechargeDetail;
	@ViewInject(R.id.tv_netPhone_detail)
	private TextView tv_netPhone_detail;
	@ViewInject(R.id.tv_freezeMoney)
	private TextView tv_freezeMoney;
	@ViewInject(R.id.tv_totalMoney)
	private TextView tv_totalMoney;
	@ViewInject(R.id.tv_netPhone_balance)
	private TextView tv_netPhone_balance;
	@ViewInject(R.id.tv_rechargeProtocol)
	private TextView tv_rechargeProtocol;
	private static final int RELOGINType = 10007;
	private PayUtils payUtils;
	@ViewInject(R.id.tv_insurance_protected)
	private TextView tv_insurance_protected;
	@ViewInject(R.id.img_insurance_flag)
	private ImageView img_insurance_flag;
	private RechargeDialog.Builder builder;
	private RechargeDialog dialog;
	private String out_trade_no;
	private MyBroadcastReceiver broad;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 注册刷新广播
		if (broad == null) {
			broad = new MyBroadcastReceiver();
		}

		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		registerReceiver(broad, intentFilter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
//		builder = new RechargeDialog.Builder(this);
//
//		builder.setPositiveButton("充值", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//				connectToServer();
//			}
//
//		});
//		builder.setNegativeButton(R.string.cancel,
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface arg0, int arg1) {
//						// TODO Auto-generated method stub
//						dialog.dismiss();
//					}
//
//				});
//		dialog = builder.create();
//		payUtils = new PayUtils();
		title.setText("我的财富");
		left_action.setText(R.string.back);
		tv_recharge.setText(loginMessage.getAccount().getCash() + "");
		// tv_freezeMoney.setText(loginMessage.getAccount().getFreeze() + "");
		tv_netPhone_balance
				.setText(loginMessage.getAccount().getCalling() + "");
		Log.i("result", "===账户余额==" + loginMessage.getAccount().getTotal() + "");
		tv_totalMoney.setText(loginMessage.getAccount().getTotal() + "");
		tv_freezeMoney.setText(loginMessage.getAccount().getFreeze() + "");
		if (loginMessage.getAccount().getInsurance() == 0) {
			tv_insurance_protected.setText(R.string.insurance_danger);
		} else if (loginMessage.getAccount().getInsurance() == 1) {
			tv_insurance_protected.setText(R.string.insurance_protected);

		}

	}

	@OnClick({ R.id.bt_recharge, R.id.left_action,
			R.id.integral_xlistview_item_relativelayout,
			R.id.tv_netPhone_detail, R.id.tv_rechargeProtocol,
			R.id.rl_insurance_protected })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_recharge:
			Intent intent = new Intent(this, PayActivty.class);
			startActivity(intent);
			
//			dialog.show();
			break;
		case R.id.tv_rechargeProtocol:
			intent = new Intent(this, RegistServiceActivity.class);
			intent.putExtra("recharge", true);
			startActivity(intent);
			break;
		case R.id.left_action:
			finish();
			break;
		case R.id.integral_xlistview_item_relativelayout:
			intent = new Intent(this, ReturnTheMoneyDatailsActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_insurance_protected:
			intent = new Intent(this, ReturnTheMoneyDatailsActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_netPhone_detail:
			intent = new Intent(this, TelephoneChargeDetilsActivity.class);
			startActivity(intent);
			break;
		}
	}

	private void connectToServer() {
		// RequestParams rp = new RequestParams();
		// rp.addBodyParameter("uid", loginMessage.getUid());
		// rp.addBodyParameter("key", loginMessage.getKey());
		// rp.addBodyParameter("total_fee", RechargeDialog.moneyAccount + "");
		// rp.addBodyParameter("quantity", "1");
		// rp.addBodyParameter("price", RechargeDialog.moneyAccount + "");
		// rp.addBodyParameter("subject", "车生活-" + RechargeDialog.moneyAccount
		// + "元充值");
		// rp.addBodyParameter("body", "车生活-" + RechargeDialog.moneyAccount
		// + "元充值");
		// rp.addBodyParameter("payment_type", "zfb");
		// httpBiz = new HttpBiz(this);
		// httpBiz.httPostData(100001, API.GET_NO, rp, this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("uid", loginMessage.getUid());
		rp.addBodyParameter("key", loginMessage.getKey());
		rp.addBodyParameter("total_fee", RechargeDialog.moneyAccount + "");
		rp.addBodyParameter("quantity", "1");
		rp.addBodyParameter("price", RechargeDialog.moneyAccount + "");
		rp.addBodyParameter("subject", "车生活-" + RechargeDialog.moneyAccount
				+ "元充值");
		rp.addBodyParameter("body", "车生活-" + RechargeDialog.moneyAccount
				+ "元充值");
		rp.addBodyParameter("payment_type", "zfb");
		httpBiz = new HttpBiz(this);
		httpBiz.httPostData(100001, API.GET_NO, rp, this);
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		switch (type) {
		case 400:
			showToast(R.string.server_link_fault);
			break;
		case 100001:
			parseJSON(data);
			break;
		case RELOGINType:
			parseLogin(data);
			break;
		}
	}

	private void parseJSON(String result) {
		if (!StringUtil.isEmpty(result)) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnSuccess, true)) {
					out_trade_no = jsonObject.optJSONObject("data").getString(
							"out_trade_no");
					payUtils.setOutTradeNo(out_trade_no);
					// payUtils.pay(RechargeActivity.this, "车生活-"
					// + RechargeDialog.moneyAccount + "元充值", "车生活-"
					// + RechargeDialog.moneyAccount + "元充值",
					// RechargeDialog.moneyAccount);
					payUtils.pay(RechargeActivity.this, "车生活-"
							+ RechargeDialog.moneyAccount + "元充值", "车生活-"
							+ RechargeDialog.moneyAccount + "元充值",
							RechargeDialog.moneyAccount);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	void submitLogin() {
		// TODO Auto-generated method stub

		if (isLogined()) {
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			httpBiz = new HttpBiz(this);
			httpBiz.httPostData(RELOGINType, API.LOGIN_MESSAGE_RELOGIN_URL, rp,
					RechargeActivity.this);
		}
	}

	protected void save(String jsonObject) {
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<LoginMessage>() {
		}.getType();
		loginMessage = gson.fromJson(jsonObject, type);
		LoginMessageUtils.saveProduct(loginMessage, this);
		System.out.println("SUCCESS=============" + "success");
		initViews();
		Intent mIntent = new Intent();
		Constant.CURRENT_REFRESH = Constant.LOGIN_REFRESH;
		mIntent.setAction(Constant.REFRESH_FLAG);
		sendBroadcast(mIntent);
	}

	private void parseLogin(String data) {
		if (StringUtil.isEmpty(data)) {
			return;
		}
		System.out.println(data);
		try {
			JSONObject jsonObject = new JSONObject(data);
			String resultStr = jsonObject.optString("operationState");
			String resultMsg = jsonObject.optJSONObject("data")
					.optString("msg");
			if (StringUtil.isEquals(resultStr, API.returnSuccess, true)) {
				save(jsonObject.optString("data"));
			} else {
				if (StringUtil.isEquals(resultStr, API.returnRelogin, true)) {
					DialogTool.getInstance(this).showConflictDialog();
				} else {
					showToast(resultMsg);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void refreshLoginMessage() {
		// TODO Auto-generated method stub
		super.refreshLoginMessage();
		submitLogin();

	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			Constant.EDIT_FLAG = false;
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.INSURANCE_REFRESH, true)) {
				System.out.println("SUCCESS====" + "保险更新");
				initViews();
			}else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.LOGIN_REFRESH, true)) {
				Log.i("result", "SUCCESS====" + "充值更新");
//				refreshLoginMessage();
				initViews();
			}else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.PAY_REFRESH, true)) {
				Log.i("result", "SUCCESS====" + "微信充值更新");
				refreshLoginMessage();
//				initViews();
			}

		}
	}
}
