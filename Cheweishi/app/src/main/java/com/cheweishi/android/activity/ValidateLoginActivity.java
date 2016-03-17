package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.RegularExpressionTools;
import com.cheweishi.android.utils.ActivityControl;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.ClearEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 验证码登录
 * 
 * @author mingdasen
 * 
 */
@ContentView(R.layout.activity_validate_login)
public class ValidateLoginActivity extends BaseActivity {
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private TextView left_action;

	@ViewInject(R.id.et_phonenumber)
	private TextView et_phonenumber;
	@ViewInject(R.id.edt_code)
	private EditText edt_code;
	@ViewInject(R.id.btn_getcode)
	private Button btn_getcode;
	@ViewInject(R.id.tv_notsms)
	private TextView tv_notsms;
	@ViewInject(R.id.tv_voice)
	private TextView tv_voice;
	@ViewInject(R.id.btn_next)
	private Button btn_next;
	
	@ResInject(id = R.string.try_try, type = ResType.String)
	private String str1;
	@ResInject(id = R.string.code_sound, type = ResType.String)
	private String str2;
	@ResInject(id = R.string.ba, type = ResType.String)
	private String str3;
	private String path = "sms";
	private String mCode;
	
	private TimeCount time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		init();
	}

	private void init() {
		left_action.setText(R.string.back);
		title.setText("验证登陆");
		if (!StringUtil.isEmpty(loginMessage)) {
			et_phonenumber.setText(loginMessage.getMobile());
		}
		time = new TimeCount(60000, 1000);// 设置倒计时为一分钟		
	}

	@OnClick({ R.id.left_action, R.id.btn_getcode, R.id.tv_voice, R.id.btn_next })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			if (time != null) {
				time.cancel();
			}
			DBTools.getInstance(this).delete(LoginMessage.class);
			this.finish();
			break;
		case R.id.btn_getcode:
			checkCode();
			break;
		case R.id.tv_voice:
			initColorTextView(false);
			tv_voice.setClickable(false);
			checkVoiceCode();
			break;
		case R.id.btn_next:
			validateLogin();
			break;

		default:
			break;
		}
	}
	
	
	/**
	 * 点击确认登录
	 */
	private void validateLogin() {
		String phoneNum = et_phonenumber.getText().toString()
				.replaceAll(" ", "");
		String code = edt_code.getText().toString()
				.replaceAll(" ", "");
		if (StringUtil.isEmpty(phoneNum)) {
			showToast(R.string.tel_cannot_null);
		} else if (!RegularExpressionTools.isMobile(phoneNum)) {
			showToast(R.string.tel_error);
		} else if (StringUtil.isEmpty(code)) {
			showToast(R.string.please_code_enter);
		} else if (!StringUtil.isEquals(code, mCode, true)) {
			showToast(R.string.code_error);
		} else {
			postLogin();
		}
	}
	
	private void postLogin() {
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String m_szImei = TelephonyMgr.getDeviceId();
		if (isLogined()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("imei", m_szImei);
			params.addBodyParameter("type", "2");
			httpBiz = new HttpBiz(this);
			ProgrosDialog.openDialog(this);
			ProgrosDialog.CanceledOnTouchOutside(false);
			ProgrosDialog.setIsDismiss(false);
			httpBiz.httPostData(1002, API.CSH_VALIDATE_LOGIN_URL, params, this);
		}
	}

	/**
	 * 获取语音验证码
	 */
	private void checkVoiceCode() {
		String phoneNum = et_phonenumber.getText().toString();
		if (StringUtil.isEmpty(phoneNum)) {
			showToast(R.string.tel_cannot_null);
			time.cancel();
			btn_next.setText(R.string.next);
			btn_next.setClickable(true);
			btn_getcode.setTextColor(ValidateLoginActivity.this
					.getApplicationContext().getResources()
					.getColor(R.color.orange_text_color));
		} else if (!RegularExpressionTools.isMobile(phoneNum)) {
			showToast(R.string.tel_error);
			time.cancel();
			btn_next.setText(R.string.code_get);
			btn_next.setClickable(true);
			btn_getcode.setTextColor(ValidateLoginActivity.this
					.getApplicationContext().getResources()
					.getColor(R.color.orange_text_color));
		} else {
			path = "voice";

			btn_getcode.setTextColor(ValidateLoginActivity.this
					.getApplicationContext().getResources()
					.getColor(R.color.btn_gray_normal));
			submitPhone(phoneNum, path);

		}
	}
	
	/**
	 * 获取短信验证码
	 */
	private void checkCode() {
		String phoneNum = et_phonenumber.getText().toString();
		if (StringUtil.isEmpty(phoneNum)) {
			showToast(R.string.tel_cannot_null);
			initColorTextView(true);
		} else if (!RegularExpressionTools.isMobile(phoneNum)) {
			showToast(R.string.tel_error);
			initColorTextView(true);
		} else {
			path = "sms";
			tv_notsms.setVisibility(View.VISIBLE);
			tv_voice.setVisibility(View.VISIBLE);
			initColorTextView(true);
			tv_voice.setClickable(true);
			time.start();
			btn_getcode.setTextColor(ValidateLoginActivity.this
					.getApplicationContext().getResources()
					.getColor(R.color.btn_gray_normal));
			submitPhone(phoneNum, path);

		}
	}
	
	/**
	 * 请求验证码
	 * 
	 * @param phoneNumber
	 * @param path
	 */
	private void submitPhone(String phoneNumber, String path) {
		Log.i("result", "==submitPhone==" + phoneNumber + "==path=" + path);
		RequestParams params = new RequestParams();
		params.addBodyParameter("called", phoneNumber);
		params.addBodyParameter("type", "2");
		params.addBodyParameter("path", path);
		httpBiz = new HttpBiz(this);
		ProgrosDialog.openDialog(this);
		httpBiz.httPostData(10001, API.CSH_CODE_URL, params, this);
	}

	
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 10001:
			parseCodeJSON(data);
			break;
		case 1002:
			parseLoginJSON(data);
			break;
		case 400:
			showToast(R.string.server_link_fault);
			btn_getcode.setClickable(true);
			btn_next.setClickable(true);
			time.cancel();
			btn_getcode.setText(R.string.get_again);
			btn_getcode.setTextColor(ValidateLoginActivity.this
					.getApplicationContext().getResources()
					.getColor(R.color.orange_text_color));
			btn_next.setClickable(true);
			tv_voice.setClickable(true);
			initColorTextView(true);
			break;
		}
	};
	
	private void parseLoginJSON(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(R.string.FAIL);
			return;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess, jsonObject.optString("state"), true)) {
				if (MainNewActivity.instance != null) {
					MainNewActivity.instance.finish();
				}
				
//				this.save(jsonObject);
				ActivityControl.removeActivityFromName(LoginActivity.class.getName());
				Intent intent = new Intent(ValidateLoginActivity.this,
						MainNewActivity.class);
				startActivity(intent);
				this.finish();
				
				
			} else {
				showToast(jsonObject.optString("message"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		DBTools.getInstance(this).save(loginMessage);
		// saveProduct(loginMessage, LoginActivity.this);
	}

	/**
	 * 对获取到的验证码Json数据解析
	 * 
	 * @param msgString
	 */
	private void parseCodeJSON(String msgString) {
		btn_next.setClickable(true);
		System.out.println(msgString);
		if (StringUtil.isEmpty(msgString)) {
			showToast(R.string.data_fail);
			btn_next.setText(R.string.next);
			btn_next.setClickable(true);
			btn_getcode.setTextColor(ValidateLoginActivity.this
					.getApplicationContext().getResources()
					.getColor(R.color.orange_text_color));
			tv_voice.setClickable(true);
			initColorTextView(true);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(msgString);
				JSONObject jsonObject2 = jsonObject.optJSONObject("data");
				if (StringUtil.isEquals(jsonObject.optString("state"),
						API.returnSuccess, true)) {
					showToast(getResources().getString(R.string.code_success));
					time.start();
					mCode = jsonObject2.optInt("code") + "";
				} else {
					time.cancel();
					btn_next.setText(R.string.code_get);
					btn_next.setClickable(true);
					tv_voice.setClickable(true);
					initColorTextView(true);
					btn_getcode.setClickable(true);
					btn_getcode.setText(R.string.code_get);
					showToast(jsonObject.optString("message"));
					btn_getcode
							.setTextColor(ValidateLoginActivity.this
									.getApplicationContext().getResources()
									.getColor(R.color.orange_text_color));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			btn_getcode.setClickable(false);
			btn_getcode.setText(millisUntilFinished / 1000
					+ getResources().getString(R.string.time_second));
		}

		@Override
		public void onFinish() {
			btn_getcode.setText(R.string.get_again);
			btn_getcode.setClickable(true);
			tv_voice.setClickable(true);
			initColorTextView(true);
			btn_getcode.setTextColor(ValidateLoginActivity.this
					.getApplicationContext().getResources()
					.getColor(R.color.orange_text_color));
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
	
	@Override
	public void onBackPressed() {
		DBTools.getInstance(this).delete(LoginMessage.class);
		super.onBackPressed();
	}
}
