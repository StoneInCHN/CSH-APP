package com.cheweishi.android.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tpl.OnLoginListener;
import cn.sharesdk.tpl.SignupPage;
import cn.sharesdk.wechat.friends.Wechat;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.APPTools;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.RegularExpressionTools;
import com.cheweishi.android.tools.SharePreferenceTools;
import com.cheweishi.android.utils.CommonUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;

/**
 * 登陆界面
 * 
 * @author mingdasen
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener,Callback, PlatformActionListener {
	@ViewInject(R.id.login_edt_phonenumber)
	private EditText mPhoneNumberEditText;// 手机号码输入框
	@ViewInject(R.id.login_edt_password)
	private EditText mPasswordEditText;// 密码输入框
	@ViewInject(R.id.login_checkbox)
	private CheckBox mCheckBox;
	@ViewInject(R.id.login_forgetpassword)
	private TextView mForgetPassword;// 忘记密码
	@ViewInject(R.id.login_btn)
	private Button mLoginButton;// 登录按钮
	@ViewInject(R.id.login_register)
	private TextView mRegisterTextView;// 注册
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	public static final int REQUEST_CODE_SETNICK = 1;
	private boolean autoLogin = false;
	@ViewInject(R.id.ll_loginLayout)
	private LinearLayout ll_loginLayout;
	@ViewInject(R.id.login_forgetpassword)
	private TextView login_forgetpassword;
	public static boolean isDevice = false;
	@ViewInject(R.id.login_top)
	private ImageView login_top;
	@ViewInject(R.id.login_delete)
	private ImageView login_delete;
	private static final int MSG_SMSSDK_CALLBACK = 1;
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR = 3;
	private static final int MSG_AUTH_COMPLETE = 4;
	private String smssdkAppkey = "76e53dba373b";// 三方登录key
	private String smssdkAppSecret = "5b0d10a9ef63766b8bcb1a8a3e739364";// 三方登录Secret
	private OnLoginListener signupListener;
	private Handler thirdHandler;
	// 短信验证的对话框
	private Dialog msgLoginDlg;
	@ViewInject(R.id.ll_login_top)
	private LinearLayout ll_loginTop;
	@ViewInject(R.id.ll_login_center)
	private LinearLayout ll_loginCenter;
	private Handler handler;
	private String loginTel = "";
	private String loginPass = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initSDK(this);
		ViewUtils.inject(this);
		thirdHandler = new Handler(this);
		init();
		setListeners();
	}

	/**
	 * 初始化视图
	 */
	private void init() {
		handler = new Handler(this);
		httpBiz = new HttpBiz(this);
		left_action.setText(R.string.back);
		title.setText(R.string.login);
		mPhoneNumberEditText.setText(SharePreferenceTools
				.getTelFromUser(LoginActivity.this));
		mPhoneNumberEditText.setSelection(mPhoneNumberEditText.getText()
				.toString().length());
		mPasswordEditText.setText(SharePreferenceTools
				.getPassFromUser(LoginActivity.this));
		if (LoginMessageUtils.showDialogFlag == true) {
			LoginMessageUtils.showDialogFlag = false;
		} else {
			showToast(R.string.before_login);
		}
		findViewById(R.id.tvWeixin).setOnClickListener(this);
		findViewById(R.id.tvQq).setOnClickListener(this);

	}

	/**
	 * 添加监听器
	 */
	private void setListeners() {
		mPasswordEditText.setOnKeyListener(onKeyListener);
	}

	/**
	 * 对系统返回键进行监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (ProgrosDialog.isProgressShowing() == false) {
				LoginActivity.this.finish();
				overridePendingTransition(R.anim.score_business_query_enter,
						R.anim.score_business_query_exit);
			}
			break;
		}
		return super.onKeyDown(keyCode, event);

	}

	/**
	 * 对手机键盘按键进行监听
	 */
	private OnKeyListener onKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_DEL) {
				mPasswordEditText.setText("");
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_ENTER
					&& event.getAction() == KeyEvent.ACTION_UP) {
				login();
				return true;
			}
			return false;
		}
	};

	/**
	 * 环信登录
	 * 
	 * @param view
	 */
	protected void HXLogin() {

		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("tag:" + "环信请求===================="
				+ sf.format(date));
		if (!CommonUtils.isNetWorkConnected(this)) {
			mLoginButton.setClickable(true);
			showToast(R.string.network_isnot_available);
			return;
		}
//		MainConstant.getInstance(LoginActivity.this).setLoginStatus("0");// 设置未登录状态
//		MainConstant.getInstance(LoginActivity.this)
//				.HXlogin(LoginActivity.this);
	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		startActivityForResult(new Intent(this, RegistActivity.class), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
	}

	private void loginSuccess2Umeng(final long start) {
		runOnUiThread(new Runnable() {
			public void run() {
				long costTime = System.currentTimeMillis() - start;
				Map<String, String> params = new HashMap<String, String>();
				params.put("status", "success");
				MobclickAgent.onEventValue(LoginActivity.this, "login1",
						params, (int) costTime);
				MobclickAgent.onEventDuration(LoginActivity.this, "login1",
						(int) costTime);
			}
		});
	}

	private void loginFailure2Umeng(final long start, final int code,
			final String message) {
		runOnUiThread(new Runnable() {
			public void run() {
				long costTime = System.currentTimeMillis() - start;
				Map<String, String> params = new HashMap<String, String>();
				params.put("status", "failure");
				params.put("error_code", code + "");
				params.put("error_description", message);
				MobclickAgent.onEventValue(LoginActivity.this, "login1",
						params, (int) costTime);
				MobclickAgent.onEventDuration(LoginActivity.this, "login1",
						(int) costTime);

			}
		});
	}

	/**
	 * 登陆验证
	 */
	protected void login() {
		mLoginButton.setClickable(false);
		String password = mPasswordEditText.getText().toString();
		if (mPhoneNumberEditText.getText().toString().length() == 0) {
			mLoginButton.setClickable(true);
			showToast(getResources().getString(R.string.enter_tel));
		} else {
			if (RegularExpressionTools.isMobile(mPhoneNumberEditText.getText()
					.toString().trim())) {
				if (!(StringUtil.isEmpty(password))) {
					submitLogin(mPhoneNumberEditText.getText().toString()
							.trim(), mPasswordEditText.getText().toString()
							.trim());
					// }
				} else {
					mLoginButton.setClickable(true);
					showToast(getResources().getString(
							R.string.pass_cannot_null));
				}
			} else {
				mLoginButton.setClickable(true);
				showToast(getResources().getString(R.string.tel_error));
			}
		}
	}

	public String getinfor() {
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String m_szImei = TelephonyMgr.getDeviceId();
		return m_szImei;
	};

	int index = 0;



	// TODO login Interface
	private void submitLogin(String phoneNumber, String password) {

		// System.out.println("tag:登录次数==================" + index);
		// index++;
		// Date date = new Date();
		// SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println("tag:" + sf.format(date));
		loginTel = phoneNumber;
		loginPass = password;
		mLoginButton.setClickable(false);
		ProgrosDialog.openDialog(LoginActivity.this);
		ProgrosDialog.CanceledOnTouchOutside(false);
		ProgrosDialog.setIsDismiss(false);
		// SharedPreferences preferences = LoginActivity.this
		// .getSharedPreferences("device_token", MODE_PRIVATE);
		// String device_token = preferences.getString("device_token", "");
		// Log.i("result", "=login=device_token=1==" + device_token);
		String phoneSystem = android.os.Build.VERSION.RELEASE;
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String m_szImei = TelephonyMgr.getDeviceId();
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("userName", phoneNumber);
		rp.addBodyParameter("passWord", password);
		// rp.addBodyParameter("loginType", 2+"");;
		rp.addBodyParameter("mobileSystem", phoneSystem);
		rp.addBodyParameter("mobileVersion", android.os.Build.MODEL);
		rp.addBodyParameter("appVersion",
				APPTools.getVersionName(LoginActivity.this));
		rp.addBodyParameter("imei", m_szImei);
		rp.addBodyParameter("type", "2");// 注册平台：1，IOS；2，Android；3，PC
		// if (StringUtil.isEmpty(device_token)) {
		// device_token = UmengRegistrar.getRegistrationId(LoginActivity.this);
		// Log.i("result", "=login=device_token=2==" + device_token);
		// if (StringUtil.isEmpty(device_token)) {
		// device_token = "no_device_token";
		// isDevice = false;
		// rp.addBodyParameter("imei", device_token);
		// } else {
		// rp.addBodyParameter("imei", device_token);
		// Editor editor = preferences.edit();
		// editor.putString("device_token", device_token);
		// editor.commit();
		// isDevice = true;
		// }
		// Log.i("result", "=login=device_token=3==" + device_token);
		// } else {
		// rp.addBodyParameter("imei", device_token);
		// isDevice = true;
		// }
		// rp.addBodyParameter("username", "test");
		// rp.addBodyParameter("password", "cws707");
		httpBiz.httPostData(10001, API.CSH_LOGIN_URL, rp, this);
	}

	private void parseLoginJSON(String str) {
		Log.i("result", "==LoginMessage==" + str);
		if (str == null) {
			mLoginButton.setClickable(true);
			// ProgrosDialog.closeProgrosDialog();
			showToast(R.string.Login_failed);
		} else {
			// System.out.println("登陆返回数据=========" + str);
			try {
				JSONObject jsonObject = new JSONObject(str);
				if (StringUtil.isEquals(jsonObject.optString("state"),
						API.returnSuccess, true)) {
					LoginMessageUtils.setLogined(this, true);
					SharePreferenceTools.setUser(this, loginTel, loginPass);
					// Log.i("result", "=车卫士服务器登录成功==");
					if (MainNewActivity.instance != null) {
						MainNewActivity.instance.finish();
					}
					// Date date = new Date();
					// SimpleDateFormat sf = new SimpleDateFormat(
					// "yyyy-MM-dd HH:mm:ss");
					// System.out.println("tag:" + "=车卫士服务器登录成功=="
					// + sf.format(date));
					this.save(jsonObject);

					Intent intent = new Intent(LoginActivity.this,
							MainNewActivity.class);
					// intent.putExtra("isDevice", isDevice);
					startActivity(intent);
					mLoginButton.setClickable(true);
					finish();

					// this.HXLogin();
				} else if (StringUtil.isEquals(API.returnRelogin,
						jsonObject.optString("state"), true)) {
					this.save(jsonObject);
					mLoginButton.setClickable(true);
					showPhoneDialog();
				} else {
					mLoginButton.setClickable(true);
					// ProgrosDialog.closeProgrosDialog();
					showToast(jsonObject.optString("message"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		// Date date = new Date();
		// SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println("tag:请求成功====================" + sf.format(date));
		switch (type) {
		case 10001:
			parseLoginJSON(data);
			break;
		case 400:
			mLoginButton.setClickable(true);
			break;
		}
	}

	/**
	 * 保存登录信息
	 * 
	 * @param jsonObject
	 */
	protected void save(JSONObject jsonObject) {
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<LoginMessage>() {
		}.getType();
		LoginMessage loginMessage = gson.fromJson(jsonObject.optString("data"),
				type);
		// 保存用户信息
		if (!StringUtil.isEmpty(loginMessage)) {
			DBTools.getInstance(this).save(loginMessage);
		}
		// saveProduct(loginMessage, LoginActivity.this);
	}

	// /**
	// * 保存登录信息
	// *
	// * @param jsonObject
	// */
	// public void saveProduct(LoginMessage pr, Context context) {
	// if (!(StringUtil.isEmpty(mCheckBox)) && mCheckBox.isChecked()) {
	// SharePreferenceTools.setUser(LoginActivity.this, pr.getMobile(),
	// mPasswordEditText.getText().toString());
	// // SharePreferenceTools.saveString(LoginActivity.this, "channelId",
	// // pr.getNo());
	// } else {
	// SharePreferenceTools.clearUser(LoginActivity.this);
	// }
	// LoginMessageUtils.saveProduct(pr, LoginActivity.this);

	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
		setContentView(R.layout.null_view);
		System.gc();
	}

	/** 填写从短信SDK应用后台注册得到的APPKEY和APPSECRET */
	public void setSMSSDKAppkey(String appkey, String appSecret) {
		smssdkAppkey = appkey;
		smssdkAppSecret = appSecret;
	}

	/** 设置授权回调，用于判断是否进入注册 */
	public void setOnLoginListener(OnLoginListener l) {
		this.signupListener = l;
	}

	@OnClick({ R.id.left_action, R.id.login_delete, R.id.login_btn,
			R.id.login_register, R.id.ll_loginLayout,
			R.id.login_forgetpassword, R.id.login_edt_phonenumber })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			LoginActivity.this.finish();
			break;
		case R.id.login_delete:
			if (ll_loginLayout.getVisibility() == View.VISIBLE) {
				LoginActivity.this.finish();
				overridePendingTransition(R.anim.score_business_query_enter,
						R.anim.score_business_query_exit);
			}
			break;
		case R.id.login_btn:
			login();
			break;
		case R.id.login_register:
			startActivity(new Intent(LoginActivity.this, RegistActivity.class));
			break;
		case R.id.ll_loginLayout:
			break;
		case R.id.login_forgetpassword:
			Intent intent = new Intent(LoginActivity.this,
					PasswordForgetActivity.class);
			intent.putExtra("tel", mPhoneNumberEditText.getText().toString());
			startActivity(intent);
			break;
		case R.id.login_edt_phonenumber:
			break;
		case R.id.tvWeixin: {
			// 微信登录
			// 测试时，需要打包签名；sample测试时，用项目里面的demokey.keystore
			// 打包签名apk,然后才能产生微信的登录
			Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
			authorize(wechat);
		}
			break;
		case R.id.tvWeibo: {
			// 新浪微博
			Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
			authorize(sina);
		}
			break;
		case R.id.tvQq: {
			// QQ空间
			Platform qzone = ShareSDK.getPlatform(QZone.NAME);
			authorize(qzone);
		}
			break;
		case R.id.tvOther: {
			// 其他登录
			authorize(null);
		}
			break;

		}
	}

	// 三方登录授权
	private void authorize(Platform plat) {
		if (plat == null) {
			return;
		}
		plat.setPlatformActionListener(this);
		// 关闭SSO授权
		plat.SSOSetting(true);
		plat.showUser(null);

	}

	public void onComplete(Platform platform, int action,
			HashMap<String, Object> res) {

		if (action == Platform.ACTION_USER_INFOR) {
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[] { platform.getName(), res };
			thirdHandler.sendMessage(msg);
		}
	}

	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			thirdHandler.sendEmptyMessage(MSG_AUTH_ERROR);
		}
		t.printStackTrace();
	}

	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			thirdHandler.sendEmptyMessage(MSG_AUTH_CANCEL);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_AUTH_CANCEL: {
			// 取消授权
			showToast(R.string.auth_cancel);
		}
			break;
		case MSG_AUTH_ERROR: {
			// 授权失败
			showToast(R.string.auth_error);
		}
			break;
		case MSG_AUTH_COMPLETE: {
			// 授权成功
			showToast(R.string.auth_complete);
			Object[] objs = (Object[]) msg.obj;
			String platform = (String) objs[0];
			HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
			if (signupListener != null
					&& signupListener.onSignin(platform, res)) {
				SignupPage signupPage = new SignupPage();
				signupPage.setOnLoginListener(signupListener);
				signupPage.setPlatform(platform);
				signupPage.show(LoginActivity.this, null);
			}
		}
			break;
		case MSG_SMSSDK_CALLBACK: {
			if (msg.arg2 == SMSSDK.RESULT_ERROR) {
				showToast("操作失败");
			} else {
				switch (msg.arg1) {
				case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE: {
					if (msgLoginDlg != null && msgLoginDlg.isShowing()) {
						msgLoginDlg.dismiss();
					}
					showToast("提交验证码成功");
					Message m = new Message();
					m.what = MSG_AUTH_COMPLETE;
					m.obj = new Object[] { "SMSSDK",
							(HashMap<String, Object>) msg.obj };
					thirdHandler.sendMessage(m);
				}
					break;
				case SMSSDK.EVENT_GET_VERIFICATION_CODE: {
					showToast("验证码已经发送");
				}
					break;
				}
			}
		}
			break;
		}
		// Toast.makeText(getContext(), "good", Toast.LENGTH_LONG).show();
		return false;
	}

	public void show(Context context) {
		initSDK(context);
		// super.show(context, null);
	}

	private void initSDK(Context context) {
		// 初始化sharesdk,具体集成步骤请看文档：
		// http://wiki.mob.com/Android_%E5%BF%AB%E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
		ShareSDK.initSDK(context);

		// 短信验证初始化，具体集成步骤看集成文档：
		// http://wiki.mob.com/Android_%E7%9F%AD%E4%BF%A1SDK%E9%9B%86%E6%88%90%E6%96%87%E6%A1%A3
		SMSSDK.initSDK(context, smssdkAppkey, smssdkAppSecret);
		EventHandler eh = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		// 注册短信验证的监听
		SMSSDK.registerEventHandler(eh);
	}

	/**
	 * 环信登录回调处理
	 */
//	@Override
//	public void call() {
		// String chat_login = SharePreferenceTools.getStringValue(
		// LoginActivity.this, "chat_login");
		// ProgrosDialog.closeProgrosDialog();
		// // 判断登陆成功与否，1表示成功，0表示失败HX
		// // if (chat_login != null && chat_login.equals("1")) {
		//
		// Date date = new Date();
		// SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println("tag:" + "=环信服务器登录成功==" + sf.format(date)
		// + " ______" + EMChat.getInstance().isLoggedIn());
		// Log.i("result", "chat_login = " + chat_login);

		// // ActivityControl.removeActivityFromName(SettingActivity.class
		// // .getName());
		// Intent mIntent = new Intent(this, MainActivity.class);
		// mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
		// | Intent.FLAG_ACTIVITY_NEW_TASK);
		// // startActivity(intent);
		// // Constant.CURRENT_REFRESH = Constant.LOGIN_REFRESH;
		// // mIntent.setAction(Constant.REFRESH_FLAG);
		// // mIntent.putExtra("isDevice", isDevice);
		// // sendBroadcast(mIntent);
		//
		// mLoginButton.setClickable(true);
		//
		// startActivity(mIntent);
		// finish();
		// 进入主页面
//		Intent intent = new Intent(LoginActivity.this, MainNewActivity.class);
//		// intent.putExtra("isDevice", isDevice);
//		startActivity(intent);
//		mLoginButton.setClickable(true);
//		finish();

		// } else {
		// mLoginButton.setClickable(true);
		// Log.i("result", "chat_login = " + 0);
		// showToast(R.string.Login_failed);
		// // 清除登陆信息
		// LoginMessageUtils.deleteLoginMessage(LoginActivity.this);
		// }
//	}

	private CustomDialog.Builder builder;
	private CustomDialog phoneDialog;

	private void showPhoneDialog() {
		if (!StringUtil.isEmpty(this)) {
			builder = new CustomDialog.Builder(this);
			builder.setMessage("您的手机在已另一台手机上登录");
			builder.setTitle(R.string.remind);
			builder.setPositiveButton("验证登录",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Intent intent = new Intent(LoginActivity.this,
									ValidateLoginActivity.class);
							startActivity(intent);

						}
					});

			builder.setNegativeButton(R.string.cancel,
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							DBTools.getInstance(LoginActivity.this).delete(
									LoginMessage.class);
							// setRadioButtonLight();
						}
					});
			phoneDialog = builder.create();
			phoneDialog.show();
		}
	}
}
