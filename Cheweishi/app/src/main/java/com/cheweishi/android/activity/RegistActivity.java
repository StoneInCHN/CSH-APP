package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Config;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.APPTools;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.RegularExpressionTools;
import com.cheweishi.android.tools.SharePreferenceTools;
import com.cheweishi.android.utils.ActivityControl;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;

/**
 * 注册页面
 * 
 * @author mingdasen
 * 
 */
public class RegistActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.phonenumber)
	private EditText mPhoneNumberEditText;// 手机号码输入框
	@ViewInject(R.id.edt_code)
	private EditText mCodeEditText;// 验证吗输入框
	@ViewInject(R.id.edt_password)
	private EditText mPasswordEditText;// 密码输入框
	@ViewInject(R.id.idcard)
	private EditText mIdcardEditText;
	private TimeCount time;
	@ViewInject(R.id.linear_saoma)
	private LinearLayout mSaomaLinearLayout;
	@ViewInject(R.id.btn_getcode)
	private Button mGetcodeButton;
	@ViewInject(R.id.btn_register)
	private Button mRegisterButton;
	@ViewInject(R.id.left_action)
	private TextView leftaction;
	@ViewInject(R.id.title)
	private TextView title1;
	@ViewInject(R.id.right_action)
	private TextView rightaction;
	@ViewInject(R.id.edt_nickName)
	private EditText edt_nickName;
	private String mCode;
	@ViewInject(R.id.ll_nick)
	private LinearLayout ll_nick;
	@ViewInject(R.id.ll_phone)
	private LinearLayout ll_phone;
	@ViewInject(R.id.ll_message_remind)
	private LinearLayout ll_message_remind;
	@ViewInject(R.id.ll_vote)
	private LinearLayout ll_vote;
	@ViewInject(R.id.ll_pass)
	private LinearLayout ll_pass;
	@ViewInject(R.id.ib_password)
	private ImageButton ib_password;
	@ViewInject(R.id.tv_remind)
	private TextView tv_remind;
	@ViewInject(R.id.ll_regist_service)
	private LinearLayout ll_regist_service;
	@ViewInject(R.id.service_checkbox)
	private CheckBox service_checkbox;
	@ViewInject(R.id.tv_service)
	private TextView tv_service;
	@ViewInject(R.id.tv_notsms)
	private TextView tv_notsms;
	@ViewInject(R.id.tv_voice)
	private TextView tv_voice;
	@ResInject(id = R.string.try_try, type = ResType.String)
	private String str1;
	@ResInject(id = R.string.code_sound, type = ResType.String)
	private String str2;
	@ResInject(id = R.string.ba, type = ResType.String)
	private String str3;
	private boolean isclick = false;
	private String path = "sms";
	private boolean passwordFlag = false;
	private boolean getCodeFlag = false;
	@ViewInject(R.id.edt_confirmpassword)
	private EditText edt_confirmpassword;
	@ViewInject(R.id.tv_error)
	private TextView tv_error;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		ViewUtils.inject(this);
		initView();
	}

	private void initView() {
		httpBiz = new HttpBiz(this);
		time = new TimeCount(60000, 1000);
		leftaction.setText(getResources().getString(R.string.back));
		rightaction.setVisibility(View.GONE);
		title1.setText(getResources().getString(R.string.register));
	}

	@OnClick({ R.id.btn_getcode, R.id.left_action, R.id.btn_register,
			R.id.linear_saoma, R.id.ib_password, R.id.tv_service, R.id.tv_voice })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_getcode:
			checkCode();
			break;
		case R.id.left_action:
			cancel();
			break;
		case R.id.btn_register:
			goOn();
			break;
		case R.id.linear_saoma:
			break;
		case R.id.ib_password:
			changePasswordVisible();
			break;
		case R.id.tv_service:
			Intent intent = new Intent(RegistActivity.this,
					RegistServiceActivity.class);
			RegistActivity.this.startActivity(intent);
			break;
		case R.id.tv_voice:// 获取语音验证码
			path = "voice";
			if (time != null) {
				time.cancel();
			}
			mGetcodeButton.setClickable(false);
			tv_voice.setClickable(false);

			isclick = false;
			mGetcodeButton.setTextColor(RegistActivity.this
					.getApplicationContext().getResources()
					.getColor(R.color.btn_gray_normal));
			initColorTextView(isclick);
			getCode();
			break;
		default:
			break;
		}

	}

	/**
	 * 点击下一步
	 */
	private void goOn() {
		if (ll_phone.getVisibility() == View.VISIBLE) {// 当前显示的为手机号码获取验证码界面
			if (StringUtil.isEmpty(mPhoneNumberEditText.getText().toString()
					.replaceAll(" ", ""))) {
				showToast(R.string.tel_cannot_null);
			} else if (RegularExpressionTools.isMobile(mPhoneNumberEditText
					.getText().toString().replaceAll(" ", "")) == false) {
				showToast(R.string.tel_error);
			} else if (StringUtil.isEmpty(mCodeEditText.getText().toString()
					.replaceAll(" ", ""))) {
				showToast(R.string.please_code_enter);
			} else if (StringUtil.isEmpty(mCode)) {
				showToast(R.string.code_error);
			} else if (!mCode.equals(mCodeEditText.getText().toString())) {
				initColorTextView(true);
				tv_voice.setClickable(true);
				mGetcodeButton.setTextColor(getResources().getColor(
						R.color.orange_text_color));
				showToast(R.string.code_error);
			} else {
				if (service_checkbox.isChecked() == true) {
					tv_voice.setVisibility(View.GONE);
					tv_notsms.setVisibility(View.GONE);
					ll_message_remind.setVisibility(View.GONE);
					ll_phone.setVisibility(View.GONE);
					ll_vote.setVisibility(View.GONE);
					ll_regist_service.setVisibility(View.GONE);
					ll_pass.setVisibility(View.VISIBLE);
					tv_error.setVisibility(View.INVISIBLE);
					edt_confirmpassword.setText("");
					title1.setText(R.string.pass_setting);
					mRegisterButton.setText(R.string.idea_refer);
					// time.cancel();
					mGetcodeButton.setText(R.string.code_get);
					APPTools.closeBoard(RegistActivity.this,
							mPhoneNumberEditText);
				} else {
					showToast(R.string.service_rule);
				}
			}
		} else if (ll_pass.getVisibility() == View.VISIBLE) {// 当前显示的为设置密码界面

			if (StringUtil.isEmpty(mPasswordEditText.getText().toString())) {
				showToast(R.string.pass_cannot_null);
			} else {
				if (mPasswordEditText.getText().toString().replaceAll(" ", "")
						.length() < 6
						|| mPasswordEditText.getText().toString()
								.replaceAll(" ", "").length() > 14) {
					showToast(R.string.pass_length);
				} else {
					if (!edt_confirmpassword.getText().toString()
							.equals(mPasswordEditText.getText().toString())) {
						tv_error.setVisibility(View.VISIBLE);
					} else {
						tv_error.setVisibility(View.INVISIBLE);
						register();
						APPTools.closeBoard(RegistActivity.this, edt_nickName);
					}
				}

			}
		} else if (ll_nick.getVisibility() == View.VISIBLE) {
			if (StringUtil.isEmpty(edt_nickName.getText().toString()
					.replaceAll(" ", ""))) {
				showToast("昵称不能为空！");
			} else {
				register();
				APPTools.closeBoard(RegistActivity.this, edt_nickName);
			}
		}
	}

	/**
	 * 点击返回
	 */
	private void cancel() {
		// if (time != null) {
		// time.cancel();
		// }
		if (ll_phone.getVisibility() == View.VISIBLE) {// 当前显示的为手机号码获取验证码界面
			mPhoneNumberEditText.setText("");
			// ll_phone.setVisibility(View.GONE);
			RegistActivity.this.finish();
			// ll_nick.setVisibility(View.VISIBLE);

		} else if (ll_pass.getVisibility() == View.VISIBLE) {// 当前显示的为设置密码界面
			mRegisterButton.setText(R.string.next);
			title1.setText(R.string.register);
			tv_voice.setVisibility(View.VISIBLE);
			tv_notsms.setVisibility(View.VISIBLE);
			mPasswordEditText.setText("");
			ll_pass.setVisibility(View.GONE);
			ll_phone.setVisibility(View.VISIBLE);
			ll_message_remind.setVisibility(View.VISIBLE);
			ll_regist_service.setVisibility(View.VISIBLE);
			ll_vote.setVisibility(View.VISIBLE);
		} else {
			mRegisterButton.setText(R.string.next);
			title1.setText(R.string.register);
			ll_pass.setVisibility(View.VISIBLE);
			ll_nick.setVisibility(View.GONE);
			edt_nickName.setText("");
		}
	}

	/**
	 * 设置TextView字体颜色
	 */
	private void initColorTextView(boolean isclick) {
		SpannableString sp;
		sp = new SpannableString(str1 + str2 + str3);
		sp.setSpan(
				new ForegroundColorSpan(getResources().getColor(
						R.color.text_black_colcor)), 0, str1.length(),
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		if (isclick) {
			sp.setSpan(
					new ForegroundColorSpan(getResources().getColor(
							R.color.orange_text_color)), str1.length(),
					(str1 + str2).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		} else {
			sp.setSpan(
					new ForegroundColorSpan(getResources().getColor(
							R.color.gray_pressed)), str1.length(),
					(str1 + str2).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		}
		sp.setSpan(
				new ForegroundColorSpan(getResources().getColor(
						R.color.text_black_colcor)), (str1 + str2).length(),
				(str1 + str2 + str3).length(),
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		tv_voice.setText(sp);
	}

	/**
	 * 设置密码可见/不可见
	 */
	private void changePasswordVisible() {
		if (passwordFlag == false) {
			ib_password.setImageResource(R.drawable.yan2x);
			int sect = mPasswordEditText.getSelectionEnd();
			passwordFlag = true;
			mPasswordEditText
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
			mPasswordEditText.setSelection(sect);
		} else {
			ib_password.setImageResource(R.drawable.yan12x);
			int sect = mPasswordEditText.getSelectionEnd();
			passwordFlag = false;
			mPasswordEditText
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
			mPasswordEditText.setSelection(sect);
		}
	}

	/**
	 * 获取验证码验证
	 */
	private void checkCode() {
		isclick = true;
		path = "sms";
		mGetcodeButton.setClickable(false);
		if (mPhoneNumberEditText.getText().toString().equals("")) {
			showToast(getResources().getString(R.string.tel_cannot_null));
			mGetcodeButton.setClickable(true);
		} else {
			if (RegularExpressionTools.isMobile(mPhoneNumberEditText.getText()
					.toString())) {

				getCode();
				tv_voice.setVisibility(View.VISIBLE);
				tv_notsms.setVisibility(View.VISIBLE);
			} else {
				showToast(getResources().getString(R.string.tel_error));
				mGetcodeButton.setClickable(true);
			}
		}
		initColorTextView(isclick);
	}

	/**
	 * 获取验证码
	 */
	protected void getCode() {
		// TODO Auto-generated method stub
		String phoneNumber = mPhoneNumberEditText.getText().toString().trim();
		if (!StringUtil.isEmpty(phoneNumber)) {
			submitPhone(phoneNumber, path);
		}
	}

	/**
	 * 注册
	 */
	protected void register() {
		mRegisterButton.setClickable(false);
		String phoneNumber = mPhoneNumberEditText.getText().toString().trim();
		String password = mPasswordEditText.getText().toString().trim();
		String idCard = mIdcardEditText.getText().toString().trim();
		submitData(phoneNumber, password, idCard);
	}

	/**
	 * 注册
	 * 
	 * @param phoneNumber
	 * @param password
	 * @param idCard
	 */
	private void submitData(String phoneNumber, String password, String idCard) {
		System.out.println("SUCCESS==========" + "注册请求");
		Log.i("result", "=====注册请求===");
		getCodeFlag = false;
		String phoneDevice = getinfor();
		String phoneSystem = android.os.Build.VERSION.RELEASE;// 系统版本
		String mobileVersion = android.os.Build.MODEL;// 手机型号
		// SharedPreferences preferences = getSharedPreferences("device_token",
		// MODE_PRIVATE);
		// String str = preferences.getString("device_token", "");
		String appVersion = APPTools.getVersionName(RegistActivity.this);
		// if (StringUtil.isEmpty(str)) {
		// str = "no_device_token";
		// }
		// String urlString =
		// "http://115.28.161.11:8080/XAI/app/cws/appRegist.do"
		// + "?tel=" + phoneNumber + "&psw=" + password + "&imei=" + str
		// + "&nick=" + phoneNumber + "&phone=" + phoneDevice + "&system="
		// + phoneSystem + "&app="
		// + APPTools.getVersionName(RegistActivity.this);
		// System.out.println(urlString);
		Log.i("result", "====注册参数===userName=" + phoneNumber + "=passWord="
				+ password + "=appVersion=" + appVersion + "=mobileVersion="
				+ mobileVersion + "=mobileSystem=" + phoneSystem + "=imei="
				+ phoneDevice);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("userName", phoneNumber);
		rp.addBodyParameter("passWord", password);
		rp.addBodyParameter("appVersion", appVersion);// App版本信息
		rp.addBodyParameter("mobileVersion", mobileVersion);// 手机版本信息
		rp.addBodyParameter("mobileSystem", phoneSystem);// 手机系统版本信息
		rp.addBodyParameter("imei", phoneDevice);// 手机唯一标识符
		rp.addBodyParameter("type", "2");// 注册平台：1，IOS；2，Android；3，PC
		ProgrosDialog.openDialog(RegistActivity.this);
		httpBiz.httPostData(100002, API.CSH_REGISTER_URL, rp, this);
	}

	/**
	 * 获取验证码
	 * 
	 * @param phoneNumber
	 * @param path
	 */
	private void submitPhone(String phoneNumber, String path) {
		getCodeFlag = true;
		RequestParams params = new RequestParams();
		params.addBodyParameter("called", phoneNumber);
		params.addBodyParameter("type", "0");// 0注册， 1找回密码
		params.addBodyParameter("path", path);
		httpBiz.httPostData(100001, API.CSH_CODE_URL, params, this);
	}

	public void receive(int type, String data) {
		switch (type) {
		case 100001:
			System.out.println("获取验证码成功");
			ProgrosDialog.closeProgrosDialog();
			parseCodeJSON(data);
			break;
		case 100002:
			parseRegistJSON(data);
			break;
		case 400:
			System.out.println("获取验证码失败");
			ProgrosDialog.closeProgrosDialog();
			if (getCodeFlag == true) {
				mRegisterButton.setClickable(true);
				time.cancel();
				mGetcodeButton.setTextColor(RegistActivity.this
						.getApplicationContext().getResources()
						.getColor(R.color.orange_text_color));
				mGetcodeButton.setText(R.string.get_again);
			} else {
				initColorTextView(true);
				tv_voice.setClickable(true);
				mRegisterButton.setClickable(true);
			}
			mGetcodeButton.setClickable(true);
			break;
		}
	};

	private void parseRegistJSON(String msgString) {
		Log.i("result", "==注册数据=" + msgString);
		if (StringUtil.isEmpty(msgString)) {
			ProgrosDialog.closeProgrosDialog();
			showToast(R.string.data_fail);
		} else {
			System.out.println(msgString);
			try {
				JSONObject jsonObject = new JSONObject(msgString);
				// JSONObject jsonObject2 = jsonObject.optJSONObject("data");
				if (StringUtil.isEquals(jsonObject.optString("state"),
						"200000", true)) {
					System.out.println("SUCCESS==========" + "注册请求成功");
					// ProgrosDialog.closeProgrosDialog();
					showToast("注册成功");
					LoginMessageUtils.setLogined(this, true);
					save(jsonObject);
				} else {
					ProgrosDialog.closeProgrosDialog();
					showToast(jsonObject.optString("message"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		mRegisterButton.setClickable(true);
	}

	private void parseCodeJSON(String msgString) {
		Log.i("result", "==验证码信息=msgString==" + msgString);
		mRegisterButton.setClickable(true);
		if (StringUtil.isEmpty(msgString)) {
			mGetcodeButton.setTextColor(RegistActivity.this
					.getApplicationContext().getResources()
					.getColor(R.color.orange_text_color));
			showToast(R.string.code_error);
		} else {
			System.out.println(msgString);
			try {
				JSONObject jsonObject = new JSONObject(msgString);
				JSONObject jsonObject2 = jsonObject.optJSONObject("data");
				if (StringUtil.isEquals(jsonObject.optString("state"),
						API.returnSuccess, true)) {
					System.out.println("获取验证码成功Success");
					mGetcodeButton.setTextColor(RegistActivity.this
							.getApplicationContext().getResources()
							.getColor(R.color.btn_gray_normal));
					time.start();
					mCode = jsonObject2.optInt("code") + "";
					tv_remind.setText(mPhoneNumberEditText.getText().toString()
							.trim());
					Log.i("result", "===code==" + mCode);
					// ll_message_remind.setVisibility(View.VISIBLE);
					showToast(getResources().getString(R.string.code_success));
					// save(jsonObject);
					// startActivity(new Intent(RegistActivity.this,
					// LoginActivity.class));
				} else {
					time.cancel();
					mGetcodeButton.setTextColor(RegistActivity.this
							.getApplicationContext().getResources()
							.getColor(R.color.orange_text_color));
					mGetcodeButton.setText(R.string.code_get);
					mGetcodeButton.setClickable(true);
					initColorTextView(true);
					tv_voice.setClickable(true);
					showToast(jsonObject.optString("message"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public String getinfor() {
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String m_szImei = TelephonyMgr.getDeviceId();
		return m_szImei;
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Config.SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				String id = bundle.getString("result");
				mIdcardEditText.setText(id);
			}
			break;

		default:
			break;
		}
	};

	protected void save(JSONObject jsonObject) {
		ProgrosDialog.closeProgrosDialog();
		SharePreferenceTools.setUser(RegistActivity.this, mPhoneNumberEditText
				.getText().toString(), mPasswordEditText.getText().toString());
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<LoginMessage>() {
		}.getType();
		LoginMessage loginMessage = gson.fromJson(jsonObject.optString("data"),
				type);
		DBTools.getInstance(this).save(loginMessage);
		// saveProduct(loginMessage, RegistActivity.this);
		// LoginMessageUtils.saveProduct(loginMessage, RegistActivity.this);
		// SharePreferenceTools.saveString(RegistActivity.this, "channelId",
		// loginMessage.getNo());

		ActivityControl.removeActivityFromName(LoginActivity.class.getName());
		startActivity(new Intent(RegistActivity.this, MainNewActivity.class));
		this.finish();
		// System.out.println("SUCCESS==========" + "环信注册请求");
		// 环信登录
		// MainConstant.getInstance(RegistActivity.this).setLoginStatus("0");//
		// 设置未登录状态
		// MainConstant.getInstance(RegistActivity.this).HXlogin(
		// RegistActivity.this);
	}

	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			mGetcodeButton.setClickable(false);
			mGetcodeButton.setText(millisUntilFinished / 1000
					+ getResources().getString(R.string.time_second));
		}

		@Override
		public void onFinish() {
			mGetcodeButton.setTextColor(RegistActivity.this
					.getApplicationContext().getResources()
					.getColor(R.color.orange_text_color));
			mGetcodeButton.setText(R.string.get_again);
			mGetcodeButton.setClickable(true);
			initColorTextView(true);
			tv_voice.setClickable(true);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(RegistActivity.this.getClass().getName()); // 统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(RegistActivity.this.getClass().getName()); // 中会保存信息
		MobclickAgent.onPause(this);
	}

	/**
	 * 对Android系统返回键进行监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancel();
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (time != null) {
			time.cancel();
		}
	}

//	@Override
//	public void call() {
		// Log.i("result", "环信注册请求成功");
		// ProgrosDialog.closeProgrosDialog();
		// // Log.i("reslut","SUCCESS===" + "huanxm");
		// ActivityControl.removeActivityFromName(LoginActivity.class.getName());
		// Log.i("result", "==移除登录界面成功==");
		// startActivity(new Intent(RegistActivity.this,
		// RegistFinishActivity.class));
		// RegistActivity.this.finish();
//	}

}
