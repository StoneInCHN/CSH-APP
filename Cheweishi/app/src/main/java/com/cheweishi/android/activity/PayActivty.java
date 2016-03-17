package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.PayUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.utils.weixinpay.WeiXinPay;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 支付界面
 * 
 * @author mingdasen
 * 
 */
@ContentView(R.layout.activity_pay_choice)
public class PayActivty extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	@ViewInject(R.id.left_action)
	private TextView left_action;

	@ViewInject(R.id.title)
	private TextView title;

	@ViewInject(R.id.btn_pay)
	private Button btn_pay;
	@ViewInject(R.id.img_weixin)
	private ImageView img_weixin;
	@ViewInject(R.id.img_alipay)
	private ImageView img_alipay;
	@ViewInject(R.id.img_upacp)
	private ImageView img_upacp;
	@ViewInject(R.id.pay_rg)
	private RadioGroup pay_rg;
	@ViewInject(R.id.rb_alipay)
	private RadioButton rb_alipay;
	@ViewInject(R.id.rb_weixin)
	private RadioButton rb_weixin;
	@ViewInject(R.id.money_rg)
	private RadioGroup money_rg;
//	@ViewInject(R.id.ll_alipay)
//	private LinearLayout ll_alipay;
//	@ViewInject(R.id.ll_weixin)
//	private LinearLayout ll_weixin;

	private float moneyAccount = 600f;// 默认支付金额
	private float testMoney = 0.01f;
	private String payment_type = "zfb";// 默认支付方式
	private MyBroadcastReceiver broad;

	/**
	 * 银联支付渠道
	 */
	private static final String CHANNEL_UPACP = "upacp";
	/**
	 * 微信支付渠道
	 */
	private static final String CHANNEL_WECHAT = "wx";
	/**
	 * 支付支付渠道
	 */
	private static final String CHANNEL_ALIPAY = "alipay";

	private static final int REQUEST_CODE_PAYMENT = 1;

	private String channel = CHANNEL_ALIPAY;

	private PayUtils payUtils;
	private String out_trade_no;
	private static final int RELOGINType = 10007;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		initView();
	}

	/**
	 * 界面初始化
	 */
	private void initView() {
		left_action.setText(R.string.back);
		title.setText(R.string.account_pay);

		payUtils = new PayUtils();
//		left_action.setOnClickListener(this);
//		btn_pay.setOnClickListener(this);
		money_rg.setOnCheckedChangeListener(this);

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

	@OnClick({ R.id.btn_pay, R.id.left_action, R.id.ll_alipay, R.id.ll_weixin,
			R.id.ll_upacp })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.btn_pay:
			btn_pay.setClickable(false);
			getPingCharge();
			// if (StringUtil.isEquals(payment_type, "zfb", true)) {
			// connectToServer();
			// } else {
			// if (WeiXinPay.getinstance(this).isWXAppInstalledAndSupported()) {
			// getWeiXinPayData();
			// }
			// }
			break;
		case R.id.ll_alipay:
			// payment_type = "zfb";
			img_alipay.setImageResource(R.drawable.dian22x);
			img_weixin.setImageResource(R.drawable.dian12x);
			img_upacp.setImageResource(R.drawable.dian12x);
			((RadioButton) pay_rg.findViewById(R.id.rb_alipay))
					.setChecked(true);
			((RadioButton) pay_rg.findViewById(R.id.rb_weixin))
					.setChecked(false);
			((RadioButton) pay_rg.findViewById(R.id.rb_upacp))
					.setChecked(false);
			channel = CHANNEL_ALIPAY;
			break;
		case R.id.ll_weixin:
			img_weixin.setImageResource(R.drawable.dian22x);
			img_alipay.setImageResource(R.drawable.dian12x);
			img_upacp.setImageResource(R.drawable.dian12x);
			// payment_type = "";
			((RadioButton) pay_rg.findViewById(R.id.rb_alipay))
					.setChecked(false);
			((RadioButton) pay_rg.findViewById(R.id.rb_weixin))
					.setChecked(true);
			((RadioButton) pay_rg.findViewById(R.id.rb_upacp))
					.setChecked(false);
			channel = CHANNEL_WECHAT;
			break;

		case R.id.ll_upacp:
			img_weixin.setImageResource(R.drawable.dian12x);
			img_alipay.setImageResource(R.drawable.dian12x);
			img_upacp.setImageResource(R.drawable.dian22x);
			((RadioButton) pay_rg.findViewById(R.id.rb_alipay))
					.setChecked(false);
			((RadioButton) pay_rg.findViewById(R.id.rb_weixin))
					.setChecked(false);
			((RadioButton) pay_rg.findViewById(R.id.rb_upacp)).setChecked(true);
			channel = CHANNEL_UPACP;
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_alipay:// 支付宝支付
			// payment_type = "zfb";
			// channel = CHANNEL_ALIPAY;
			break;

		case R.id.rb_weixin:// 微信支付
			// payment_type = "";
			// channel = CHANNEL_WECHAT;
			break;
		case R.id.rb_money_100:// 支付金额100
			moneyAccount = 100f;
			break;
		case R.id.rb_money_300:// 支付金额300
			moneyAccount = 300f;
			break;
		case R.id.rb_money_600:// 支付金额600
			moneyAccount = 600f;
			break;
		case R.id.rb_money_1000:// 支付金额1000
			moneyAccount = 1000f;
			break;

		default:
			break;
		}
	}

	/**
	 * 支付宝支付获取订单
	 */
	private void connectToServer() {
		if (!isLogined()) {
			startActivity(new Intent(PayActivty.this, LoginActivity.class));
		} else {
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			rp.addBodyParameter("total_fee", moneyAccount + "");
			rp.addBodyParameter("quantity", "1");
			rp.addBodyParameter("price", moneyAccount + "");
			rp.addBodyParameter("subject", "车生活-" + moneyAccount + "元充值");
			rp.addBodyParameter("body", "车生活-" + moneyAccount + "元充值");
			rp.addBodyParameter("payment_type", payment_type);
			httpBiz = new HttpBiz(this);
			ProgrosDialog.openDialog(this);
			httpBiz.httPostData(100001, API.GET_NO, rp, this);
		}
	}

	/**
	 * 获取微信支付订单数据
	 */
	private void getWeiXinPayData() {
		// if (!NetUtils.isNetworkAvailable(this)
		// || StringUtil.isEmpty(NetUtils.getNetIp(this))) {
		// showToast("网络不可用");
		// return;
		// }
		if (isLogined()) {
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			rp.addBodyParameter("body", "车生活-" + moneyAccount + "元充值");
			rp.addBodyParameter("total_fee", (int) (moneyAccount * 100) + "");
			rp.addBodyParameter("product_id", 1522456645 + "");
			rp.addBodyParameter("spbill_create_ip", "127.0.0.1");// NetUtils.getNetIp(this)
			httpBiz = new HttpBiz(this);
			ProgrosDialog.openDialog(this);
			Log.i("result", "====微信支付订单请求==" + API.WEIXIN_PAY_NO + "?uid="
					+ loginMessage.getUid() + "&key=" + loginMessage.getKey()
					+ "&body=" + "车生活-" + moneyAccount + "元充值" + "&total_fee="
					+ (int) (moneyAccount * 100) + "&product_id=" + 1522456645
					+ "&spbill_create_ip=" + "127.0.0.1");
			httpBiz.httPostData(100002, API.WEIXIN_PAY_NO, rp, this);
		} else {
			startActivity(new Intent(PayActivty.this, LoginActivity.class));
		}
	}

	/**
	 * 微信支付订单数据解析
	 * 
	 * @param data
	 */
	private void parseWXJSON(String data) {
		Log.i("result", "===weixin数据解析===" + data);
		ProgrosDialog.closeProgrosDialog();
		if (!StringUtil.isEmpty(data)) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(data);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnSuccess, true)) {
					String prepay_id = jsonObject.optJSONObject("data")
							.getString("prepay_id");

					String nonce_str = jsonObject.optJSONObject("data")
							.getString("nonce_str");

					WeiXinPay.getinstance(this).pay(prepay_id, nonce_str);

				} else {
					// Log.i("result", "===weixin数据解析===" +
					// jsonObject.optJSONObject("data").getString("msg"));
					showToast(jsonObject.optJSONObject("data").getString("msg"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 
	 * 支付宝订单数据解析
	 * 
	 * @param result
	 */
	private void parseJSON(String result) {
		ProgrosDialog.closeProgrosDialog();
		if (!StringUtil.isEmpty(result)) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnSuccess, true)) {
					out_trade_no = jsonObject.optJSONObject("data").getString(
							"out_trade_no");
					payUtils.setOutTradeNo(out_trade_no);
					payUtils.pay(PayActivty.this,
							"车生活-" + moneyAccount + "元充值", "车生活-"
									+ moneyAccount + "元充值", moneyAccount);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void receive(int type, String data) {
		super.receive(type, data);
		btn_pay.setClickable(true);
		switch (type) {
		case 400:
			showToast(R.string.server_link_fault);
			break;
		case 100001:
			parseJSON(data);
			break;

		case 100002:
			parseWXJSON(data);
			break;

		case 100003:
			parsePingJSON(data);
			break;
		}
	}

	/**
	 * 解析ping++数据
	 * 
	 * @param data
	 */
	private void parsePingJSON(String data) {
		ProgrosDialog.closeProgrosDialog();
		if (StringUtil.isEmpty(data)) {
			showToast(R.string.FAIL);
			return;
		}
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess,
					jsonObject.optString("state"), true)) {

				payDataDispose(jsonObject);

				// String charge =
				// jsonObject.optJSONObject("data").optString("charge");
				// Log.i("result", "===charge===" + charge);
				// pingPayment(charge);
			} else if (StringUtil.isEquals(API.returnRelogin,
					jsonObject.optString("state"), true)) {
				ReLoginDialog.getInstance(PayActivty.this).showDialog(
						jsonObject.optString("message"));
			} else {
//				ll_alipay.setClickable(true);
//				ll_weixin.setClickable(true);
				showToast(jsonObject.optString("message"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 支付数据处理
	 * 
	 * @param jsonObject
	 */
	private void payDataDispose(JSONObject jsonObject) {
		try {
			if (StringUtil.isEquals(channel, CHANNEL_ALIPAY, true)) {// 支付宝

				out_trade_no = jsonObject.optJSONObject("data").getString(
						"out_trade_no");
				Log.i("result", "=out_trade_no==支付宝==" + out_trade_no);
				payUtils.setOutTradeNo(out_trade_no);
				payUtils.pay(PayActivty.this, "车生活-" + moneyAccount + "元充值", "车生活-"
						+ moneyAccount + "元充值", moneyAccount);

			} else if (StringUtil.isEquals(channel, CHANNEL_WECHAT, true)) {// 微信

				String prepay_id = jsonObject.optJSONObject("data").getString(
						"prepay_id");

				String nonce_str = jsonObject.optJSONObject("data").getString(
						"nonce_str");
				Log.i("result", "=prepay_id==微信==" + prepay_id
						+ "===nonce_str==" + nonce_str);
				WeiXinPay.getinstance(this).pay(prepay_id, nonce_str);
			}
		} catch (JSONException e) {
		}
	}

	// /**
	// * 发起Ping++支付请求
	// *
	// * @param charge
	// */
	// private void pingPayment(String charge) {
	// Intent intent = new Intent();
	// String packageName = getPackageName();
	// ComponentName componentName = new ComponentName(packageName,
	// packageName + ".wxapi.WXPayEntryActivity");
	// intent.setComponent(componentName);
	// intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
	// startActivityForResult(intent, REQUEST_CODE_PAYMENT);
	// }

	/**
	 * 获取ping++的支付信息
	 */
	private void getPingCharge() {
		if (isLogined()) {
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("mobile", loginMessage.getMobile());// 支付通道
			rp.addBodyParameter("appid", "app_j5qbP4Dib5uHTe5C");
			rp.addBodyParameter("amount", (int) (moneyAccount * 100) + "");//
			rp.addBodyParameter("channel", channel);
			rp.addBodyParameter("currency", "cny");
			rp.addBodyParameter("subject", "车生活-" + moneyAccount + "元充值");// moneyAccount
			rp.addBodyParameter("body", "车生活-" + moneyAccount + "元充值");
			rp.addBodyParameter("red", "0");
			rp.addBodyParameter("money", 0 + "");
			rp.addBodyParameter("order_sn", "");
			rp.addBodyParameter("store_id", 2 + "");
			rp.addBodyParameter("goods_id", 1 + "");
			rp.addBodyParameter("price", (int) (moneyAccount * 100) + "");
			httpBiz = new HttpBiz(this);
			ProgrosDialog.openDialog(this);
			// Log.i("result", "====微信支付订单请求==" + API.WEIXIN_PAY_NO + "?uid="
			// + loginMessage.getUid() + "&key=" + loginMessage.getKey()
			// + "&body=" + "车生活-" + moneyAccount + "元充值" + "&total_fee="
			// + (int) (moneyAccount * 100) + "&product_id=" + 1522456645
			// + "&spbill_create_ip=" + "127.0.0.1");
			httpBiz.httPostData(100003, API.CSH_PING_CHARGE_URL, rp, this);
//			ll_alipay.setClickable(false);
//			ll_weixin.setClickable(false);
		} else {
			btn_pay.setClickable(true);
			startActivity(new Intent(PayActivty.this, LoginActivity.class));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("result", "===Ping++支付结果==" + data);
		// 支付页面返回处理
		if (requestCode == REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				String result = data.getExtras().getString("pay_result");
				/*
				 * 处理返回值 "success" - payment succeed "fail" - payment failed
				 * "cancel" - user canceld "invalid" - payment plugin not
				 * installed
				 */
				if (StringUtil.isEquals("success", result, true)) {
					Constant.CURRENT_REFRESH = Constant.PAY_REFRESH;
					Intent mIntent = new Intent();
					mIntent.setAction(Constant.REFRESH_FLAG);
					sendBroadcast(mIntent);
					showMsg("充值成功", "", "");
				} else if (StringUtil.isEquals("fail", result, true)) {
					showMsg("支付失败", "", "");
				} else if (StringUtil.isEquals("cancel", result, true)) {
					showMsg("取消支付", "", "");
				} else if (StringUtil.isEquals("invalid", result, true)) {
					showMsg("未安装付款插件", "", "");
				}
				String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
				String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
				Log.i("result", "===result==" + result + "==errorMsg=="
						+ errorMsg + "==extraMsg=" + extraMsg);
			}
		}
	}

	public void showMsg(String title, String msg1, String msg2) {
		String str = title;
		if (null != msg1 && msg1.length() != 0) {
			str += "\n" + msg1;
		}
		if (null != msg2 && msg2.length() != 0) {
			str += "\n" + msg2;
		}
		AlertDialog.Builder builder = new Builder(PayActivty.this);
		builder.setMessage(str);
		builder.setTitle("提示");
		builder.setPositiveButton("确定", null);
		builder.create().show();
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
					Constant.RECHARGE_REFRESH, true)
					|| StringUtil.isEquals(Constant.CURRENT_REFRESH,
							Constant.WEIXIN_PAY_REFRESH, true)) {
				Constant.EDIT_FLAG = true;
				// setNow();
				Log.i("result", "===========payActivity=====Receiver===========");
				finish();
			} else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.USER_NICK_EDIT_REFRESH, true)) {
				Constant.EDIT_FLAG = true;
				// initViews();
			} else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.USER_NICK_EDIT_REFRESH_OTHER, true)) {
				// connectToServer();
			}
		}
	}

}
