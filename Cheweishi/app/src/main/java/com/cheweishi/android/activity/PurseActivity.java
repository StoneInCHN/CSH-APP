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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 我的钱包
 * 
 * @author XMH
 * 
 */
public class PurseActivity extends BaseActivity {

	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.rel_purse_certificates)
	// 卡券
	private RelativeLayout rel_purse_certificates;
	@ViewInject(R.id.rel_purse_integral)
	// 积分
	private RelativeLayout rel_purse_integral;
	@ViewInject(R.id.rel_purse_balance)
	// 账户余额
	private RelativeLayout rel_purse_balance;
	@ViewInject(R.id.rel_purse_phone)
	// 话费余额
	private RelativeLayout rel_purse_phone;
	@ViewInject(R.id.tv_purse_integral)
	// 积分Text
	private TextView tv_purse_integral;

	@ViewInject(R.id.tv_purse_certificates)
	// 红包text
	private TextView tv_purse_certificates;
	@ViewInject(R.id.tv_purse_balance)
	// 余额text
	private TextView tv_purse_balance;
	@ViewInject(R.id.tv_purse_fee_balance)
	// 花费text
	private TextView tv_purse_fee_balance;

	private static final int URL_TYPE = 100000;
	private Intent intent;
	private String money = "";
	private String score;
	private MyBroadcastReceiver broad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purse);
		ViewUtils.inject(this);

		init();
	}

	private void init() {
		title.setText(R.string.purse);
		left_action.setText(R.string.back);
		setNow();
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

	@OnClick({ R.id.left_action, R.id.title, R.id.rel_purse_certificates,
			R.id.rel_purse_integral, R.id.rel_purse_balance,
			R.id.rel_purse_phone })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:// 返回
			PurseActivity.this.finish();
			break;
		case R.id.rel_purse_certificates:// 红包
			intent = new Intent(PurseActivity.this,
					PurseRedPacketsActivity.class);
			startActivity(intent);
			break;
		case R.id.rel_purse_integral:// 积分
			intent = new Intent(PurseActivity.this, PurseIntegralActivity.class);
			intent.putExtra("score", score);
			startActivity(intent);
			break;
		case R.id.rel_purse_balance:// 账户余额
			intent = new Intent(PurseActivity.this, PurseBalanceActivity.class);
			intent.putExtra("money", money);
			startActivity(intent);
			break;
		case R.id.rel_purse_phone:// 话费余额
			intent = new Intent(PurseActivity.this, WebPhoneActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/***
	 * 获取钱包数据信息
	 */
	private void setNow() {
		if (isLogined()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("mobile", loginMessage.getMobile());

			httpBiz = new HttpBiz(this);
			ProgrosDialog.openDialog(this);
			httpBiz.httPostData(URL_TYPE, API.CSH_QUERY_WALLET_URL, params,
					this);
		} else {
			startActivity(new Intent(PurseActivity.this, LoginActivity.class));
			overridePendingTransition(R.anim.score_business_query_enter,
					R.anim.score_business_query_exit);
			finish();
		}
		// if (loginMessage != null) {
		// tv_purse_integral.setText(""
		// + loginMessage.getScore().getNow());
		// }
	}

	@Override
	public void receive(int type, String data) {
		super.receive(type, data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case URL_TYPE:
			parseJsonData(data);
			break;

		default:
			showToast(R.string.FAIL);
			break;
		}
	}

	/**
	 * 数据解析
	 * 
	 * @param data
	 */
	private void parseJsonData(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(R.string.FAIL);
			return;
		}
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess,
					jsonObject.optString("state"), true)) {
				JSONObject object = jsonObject.optJSONObject("data");
				if (!StringUtil.isEmpty(object)) {
					String red = object.optString("red");
					if (StringUtil.isEmpty(red)
							|| StringUtil.isEquals("null", red, true)) {
						tv_purse_certificates.setText(0 + "");
					} else {
						tv_purse_certificates.setText(red);
					}
					String money = object.optString("money");
					if (StringUtil.isEmpty(money)
							|| StringUtil.isEquals("null", money, true)) {
						tv_purse_balance.setText(0 + "");
						this.money = 0 + "";
					} else {
						tv_purse_balance.setText(money);
						this.money = money;
					}
					String score = object.optString("score");
					if (StringUtil.isEmpty(score)
							|| StringUtil.isEquals("null", score, true)) {
						tv_purse_integral.setText(0 + "");
						this.score = 0 + "";
					} else {
						tv_purse_integral.setText(score);
						this.score = score;
					}
				}
			} else if (StringUtil.isEquals(API.returnRelogin,
					jsonObject.optString("state"), true)) {
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
	protected void onDestroy() {
		super.onDestroy();
		if (!StringUtil.isEmpty(broad)) {
			unregisterReceiver(broad);
		}
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
				setNow();
				Log.i("result", "===========PurseActivity====Receiver============");
			} else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.WEIXIN_PAY_REFRESH, true)) {
				Constant.EDIT_FLAG = true;
				setNow();
			} else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.USER_NICK_EDIT_REFRESH_OTHER, true)) {
				// connectToServer();
			}
		}
	}

}
