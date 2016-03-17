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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
/**
 * 预约支付
 * @author mingdasen
 *
 */
public class WashCarPayActivity extends BaseActivity {
	@ViewInject(R.id.tv_wash_pay_num)
	private TextView tv_wash_pay_num;// 价钱
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.tv_wash_affirm)
	private TextView tv_wash_affirm;
	@ViewInject(R.id.pay_rg)
	private RadioGroup pay_rg;
	@ViewInject(R.id.rb_alipay)
	private RadioButton rb_alipay;
	@ViewInject(R.id.rb_weixin)
	private RadioButton rb_weixin;
	@ViewInject(R.id.img_weixin)
	private ImageView img_weixin;
	@ViewInject(R.id.img_alipay)
	private ImageView img_alipay;
	@ViewInject(R.id.tv_pay_name)
	// 商户名称
	private TextView tv_pay_name;
	@ViewInject(R.id.tv_pay_service_name)
	private TextView tv_pay_service_name;// 服务名称
	@ViewInject(R.id.tv_wash_money)
	private TextView tv_wash_money;
	@ViewInject(R.id.img_upacp)
	private ImageView img_upacp;
	@ViewInject(R.id.cb_red)
	private CheckBox cb_red;
	@ViewInject(R.id.tv_red_hint)
	private TextView tv_red_hint;

	@ViewInject(R.id.rl_balance)
	private RelativeLayout rl_balance;
	@ViewInject(R.id.rl_red)
	private RelativeLayout rl_red;

	@ViewInject(R.id.cb_balance)
	private CheckBox cb_balance;
	@ViewInject(R.id.tv_balance_hint)
	private TextView tv_balance_hint;

	@ViewInject(R.id.ll_pay)
	private LinearLayout ll_pay;

	// private Intent intent = new Intent();
	// private String num = "0.01";
	// private String out_trade_no;
	// private PayUtils payUtils;
	// private String payment_type = "zfb";// 默认支付方式
	private double red = 0.0;// 使用的红包
	private double balance = 0.0;// 支付金额
	private double amount = 0.0;// 订单金额
	private double remainder = 0.0;// 使用的账户余额

	private String pay_type = "";
	private String order_sn = "";
	private int mScore = 0; // 我的积分
	private double mMoney = 0.0; // 我的余额
	private double mRed = 0.0;// 我的红包
	private int red_status = 0;// 0使用红包，1表示不使用红包
	private int balance_status = 0;// 0表示使用余额，1表示不使用余额
	private String seller_id = "";//商户id
	private String service_id = "";//服务id
	
	private PayUtils payUtils;
	private String out_trade_no;

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
	
	private MyBroadcastReceiver broad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_washcar_pay);
		ViewUtils.inject(this);
		httpBiz = new HttpBiz(this);
		init();

	}

	private void init() {
		left_action.setText(R.string.back);
		title.setText("支付");
		tv_pay_name.setText(getIntent().getStringExtra("seller"));
		tv_pay_service_name.setText(getIntent().getStringExtra("service"));
		tv_wash_pay_num
				.setText("￥" + getIntent().getStringExtra("price") + "元");
		tv_wash_money.setText("￥" + getIntent().getStringExtra("price") + "元");
		pay_type = getIntent().getStringExtra("type");
		order_sn = getIntent().getStringExtra("order_sn");
		seller_id = getIntent().getStringExtra("seller_id");
		service_id = getIntent().getStringExtra("service_id");
		String price = getIntent().getStringExtra("price");
		if (!StringUtil.isEmpty(price)) {
			amount = StringUtil.getDouble(price);
		}
		getRedData();
		// payUtils = new PayUtils();
		// num = intent.getStringExtra("pay_num");
		// tv_wash_pay_num.setText("￥"+num);

		cb_red.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				balance = amount;
				if (cb_red.isChecked()) {
					red_status = 0;
					redCompute(mRed, mMoney, mScore);
				} else {
					red_status = 1;
					red = 0.0;
					// if (remainder == 0) {
					// balance = amount;
					// }else {
					// balance = amount - remainder;
					// }
					// tv_wash_money.setText("￥" + balance + "元");
					tv_red_hint.setText("使用红包抵用");
					redCompute(mRed, mMoney, mScore);
				}
			}
		});

		cb_balance
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						balance = amount;
						if (cb_balance.isChecked()) {
							balance_status = 0;
							redCompute(mRed, mMoney, mScore);
						} else {
							balance_status = 1;
							remainder = 0.0;
							// if (red == 0) {
							// balance = amount;
							// }else {
							// balance = amount - red;
							// }
							// tv_wash_money.setText("￥" + balance + "元");
							tv_balance_hint.setText("使用余额支付");
							redCompute(mRed, mMoney, mScore);
						}
					}
				});

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

	/**
	 * 获取红包信息
	 */
	protected void getRedData() {
		if (isLogined()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("mobile", loginMessage.getMobile());
			ProgrosDialog.openDialog(this);
			httpBiz.httPostData(10008, API.CSH_QUERY_WALLET_URL, params, this);
		} else {
			startActivity(new Intent(WashCarPayActivity.this,
					LoginActivity.class));
			overridePendingTransition(R.anim.score_business_query_enter,
					R.anim.score_business_query_exit);
		}
	}

	@OnClick({ R.id.left_action, R.id.rb_alipay, R.id.rb_weixin,
			R.id.tv_wash_affirm, R.id.ll_alipay, R.id.ll_weixin, R.id.ll_upacp,
			R.id.cb_red })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.tv_wash_affirm:
			// if (StringUtil.isEquals(payment_type, "zfb", true)) {
			// connectToServer();
			// } else {
			// if (WeiXinPay.getinstance(this).isWXAppInstalledAndSupported()) {
			// getWeiXinPayData();
			// }
			// }
			tv_wash_affirm.setClickable(false);
			getPingCharge();
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

	/**
	 * 获取ping++的支付信息
	 */
	private void getPingCharge() {
		if (isLogined()) {
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("mobile", loginMessage.getMobile());// 支付通道
			rp.addBodyParameter("appid", "app_j5qbP4Dib5uHTe5C");
			rp.addBodyParameter("amount", (int) (balance * 100) + "");//
			rp.addBodyParameter("channel", channel);
			rp.addBodyParameter("currency", "cny");
			rp.addBodyParameter("subject", tv_pay_name.getText() + "");
			rp.addBodyParameter("body", tv_pay_service_name.getText() + "");
			rp.addBodyParameter("red", (int) (red * 100) + "");
			rp.addBodyParameter("money", (int) (remainder * 100) + "");
			rp.addBodyParameter("order_sn", order_sn);
			rp.addBodyParameter("store_id", seller_id);
			rp.addBodyParameter("goods_id", service_id);
			rp.addBodyParameter("price", (int)(amount * 100) + "");
			ProgrosDialog.openDialog(this);
			httpBiz.httPostData(100003, API.CSH_ORDER_CHARGE_URL, rp, this);
		} else {
			tv_wash_affirm.setClickable(true);
			startActivity(new Intent(WashCarPayActivity.this,
					LoginActivity.class));
			overridePendingTransition(R.anim.score_business_query_enter,
					R.anim.score_business_query_exit);
		}
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		ProgrosDialog.closeProgrosDialog();
		tv_wash_affirm.setClickable(true);
		super.receive(type, data);
		switch (type) {
		case 400:
			showToast(R.string.server_link_fault);
			break;
		case 100003:
			parsePingJSON(data);
			break;
		case 10008:
			parseJsonData(data);
			break;
		case 1008:
			parsePayOrderJSON(data);
			break;
		}
	}
	
	/**
	 * 解析支付成功订单数据
	 * @param data
	 */
	private void parsePayOrderJSON(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(R.string.FAIL);
			return;
		}
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess, jsonObject.optString("state"), true)) {
				paymentDone(jsonObject.optJSONObject("data").optJSONObject("return"));
			}else if(StringUtil.isEquals(API.returnRelogin, jsonObject.optString("state"), true)){
				ReLoginDialog.getInstance(this).showDialog(jsonObject.optString("message"));
			}else {
				showToast(jsonObject.optString("message"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

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
					// tv_purse_balance.setText(object.optString("money"));
					String red = object.optString("red");
					if (StringUtil.isEmpty(red) || StringUtil.isEquals("null", red, true)) {
						mRed = 0;
					} else {
						mRed = StringUtil.getDouble(red);
					}
					String money = object.optString("money");
					if (StringUtil.isEmpty(money) || StringUtil.isEquals("null", money, true)) {
						mMoney = 0;
					} else {
						mMoney = StringUtil.getDouble(money);
					}
					String score = object.optString("score");
					if (StringUtil.isEmpty(score) || StringUtil.isEquals("null", score, true)) {
						mScore = 0;
					} else {
						mScore = StringUtil.getInt(score);
					}
					// if (!StringUtil.isEmpty(red) ||
					// !StringUtil.isEquals("null", red, true)) {
					balance = amount;
					redCompute(mRed, mMoney, mScore);
					// }else{
					// cb_red.setChecked(false);
					// tv_red_hint.setText("红包金额不足");
					// showToast("红包金额不足");
					// }
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

	/**
	 * 支付计算
	 */
	private void redCompute(double red, double remainder, int score) {
		// 是否显示红包抵用
		if (red <= 0.0) {
			rl_red.setVisibility(View.GONE);
		} else if (red > 0.0 && red_status == 0) {
			rl_red.setVisibility(View.VISIBLE);
			// 红包计算
			redCount(red);
			double temp = balance - this.red;
			balance = convert(temp);
			tv_red_hint.setText("可使用红包抵用" + this.red + "元");
		}
		// 是否显示余额支付
		if (remainder <= 0.0) {
			rl_balance.setVisibility(View.GONE);
		} else if (remainder > 0.0 && balance_status == 0) {
			rl_balance.setVisibility(View.VISIBLE);
			// 余额计算
			balanceCount(remainder);
			double temp = balance - this.remainder;
			balance = convert(temp);
			tv_balance_hint.setText("可使用余额支付" + this.remainder + "元");
		}
		
		tv_wash_money.setText("￥" + balance + "元");
		showPay();
	}

	/**
	 * 保留两位小数
	 * @param value
	 * @return
	 */
	private double convert(double value){
		long l1 = Math.round(value*100); //四舍五入
		double ret = l1/100.0; //注意：使用 100.0 而不是 100
		return ret;
		}
	
	/**
	 * 显示在线支付
	 */
	private void showPay() {
		// 是否显示在线支付
		if (balance > 0) {
			ll_pay.setVisibility(View.VISIBLE);
		} else {
			ll_pay.setVisibility(View.GONE);
		}
	}

	/**
	 * 余额计算
	 * 
	 * @param remainder
	 */
	private void balanceCount(double remainder) {
		if (remainder >= balance) {
			this.remainder = convert(balance);
		} else {
			this.remainder = 0.0;
		}
	}

	/**
	 * 红包的计算
	 * 
	 * @param red
	 * @return
	 */
	private void redCount(double red) {
		if (StringUtil.isEquals("px", pay_type, true)) {
			if (red >= balance) {
				this.red = convert(balance);
			} else {
				this.red = convert(red);
			}
		} else {
			if (red >= balance * 0.15) {
				this.red = convert(balance);
			} else {
				this.red = convert(red);
			}
		}
	}
	private String pay_order_sn;
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
				String type = jsonObject.optJSONObject("data")
						.optString("type");
				if (StringUtil.isEquals("1", type, true)) {
					JSONObject result = jsonObject.optJSONObject("data")
							.optJSONObject("return");
					paymentDone(result);
				} else if (StringUtil.isEquals("2", type, true)) {
//					String charge = jsonObject.optJSONObject("data").optString(
//							"charge");
//					pay_order_sn = jsonObject.optJSONObject("data").optString(
//							"order_sn");
////					pingPayment(charge);
					payDataDispose(jsonObject);
				}
			} else if (StringUtil.isEquals(API.returnRelogin,
					jsonObject.optString("state"), true)) {
				ReLoginDialog.getInstance(WashCarPayActivity.this).showDialog(
						jsonObject.optString("message"));
			} else {
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
				payUtils = new PayUtils();
				payUtils.setOutTradeNo(out_trade_no);
				payUtils.pay(WashCarPayActivity.this, tv_pay_name.getText() + "", tv_pay_service_name.getText() + "", balance);

			} else if (StringUtil.isEquals(channel, CHANNEL_WECHAT, true)) {// 微信
				out_trade_no = jsonObject.optJSONObject("data").getString(
						"out_trade_no");
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

	/**
	 * 红包、余额支付完成
	 * 
	 * @param result
	 */
	private void paymentDone(JSONObject result) {
		String store_name = result.optString("store_name");
		String price = result.optString("price");
		String goods_name = result.optString("goods_name");
		String order_sn = result.optString("order_sn");
		String effectiveTime = result.optString("effectiveTime");
		String order_id = result.optString("order_id");
		Intent intent = new Intent(WashCarPayActivity.this,
				OrderPaymentSuccessActivity.class);
		intent.putExtra("store_name", store_name);
		intent.putExtra("price", price);
		intent.putExtra("goods_name", goods_name);
		intent.putExtra("order_sn", order_sn);
		intent.putExtra("effectiveTime", effectiveTime);
		intent.putExtra("order_id", order_id);
		
		startActivity(intent);
		this.finish();
	}

//	/**
//	 * 发起Ping++支付请求
//	 * 
//	 * @param charge
//	 */
//	private void pingPayment(String charge) {
//		Intent intent = new Intent();
//		String packageName = getPackageName();
//		ComponentName componentName = new ComponentName(packageName,
//				packageName + ".wxapi.WXPayEntryActivity");
//		intent.setComponent(componentName);
//		intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
//		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		Log.i("result", "===Ping++支付结果==" + data);
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
					getPaySuccessData();
					showToast("支付成功");
				}else {
					
					if (StringUtil.isEquals("fail", result, true)) {
						showMsg("支付失败","","");
					} else if(StringUtil.isEquals("cancel", result, true)){
						showMsg("取消支付","","");
					}else if(StringUtil.isEquals("invalid", result, true)){
						showMsg("未安装付款插件", "", "");
					}
					String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
					String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
					Log.i("result", "===result==" + result + "==errorMsg=="
							+ errorMsg + "==extraMsg=" + extraMsg);
				}
			}
		}
	}

	/**
	 * 获取在线支付成功后的订单数据
	 */
	private void getPaySuccessData() {
		if (isLogined()) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", loginMessage.getUid());
		params.addBodyParameter("order_sn", out_trade_no);
		ProgrosDialog.openDialog(this);
		httpBiz.httPostData(1008, API.CSH_GET_PAY_SUCCESS_ORDER_URL, params, this);
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
		AlertDialog.Builder builder = new Builder(WashCarPayActivity.this);
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
				getPaySuccessData();
				Log.i("result", "===========WashCarPayActivity=====Receiver===========");
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
