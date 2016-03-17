package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.entity.NetPhone;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 车之语网络电话
 * 
 * @author Xiaojin
 * 
 */
public class WebPhoneActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.imgBtn_download)
	private ImageButton imgBtn_download;// 下载
	@ViewInject(R.id.tv_card_number)
	private TextView tv_card_number;// 卡号
	@ViewInject(R.id.tv_password)
	private TextView tv_password;// 密码
	@ViewInject(R.id.tv_web_phone_communicate)
	private TextView tv_web_phone_communicate;
	NetPhone netPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ip_phone);
		ViewUtils.inject(this);
		init();
	}

	/**
	 * 初始化视图
	 */
	private void init() {
		left_action.setText(R.string.back);
		title.setText(R.string.wireless_communications);
		connectToServer();
	}

	private void connectToServer() {
		httpBiz = new HttpBiz(this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("uid", loginMessage.getUid());
		rp.addBodyParameter("mobile", loginMessage.getMobile());
		httpBiz.httPostData(10001, API.CSH_WEB_PHONE_URL, rp, this);
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		switch (type) {
		case 400:
			showToast(R.string.server_link_fault);
			break;
		case 10001:
			parseJSON(data);
			break;
		}
	}

	private void parseJSON(String result) {
		if (StringUtil.isEmpty(result)) {
			return;
		}
		System.out.println("网络电话====" + result);
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (StringUtil.isEquals(API.returnSuccess,
					jsonObject.optString("state"), true)) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<NetPhone>() {
				}.getType();
				netPhone = gson.fromJson(jsonObject.optString("data"), type);
				setValues();
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

	private void setValues() {
		if (!StringUtil.isEmpty(netPhone)) {
			tv_card_number.setText(netPhone.getCode());
			tv_password.setText(netPhone.getPass());
		}
	}

	@OnClick({ R.id.left_action, R.id.imgBtn_download,
			R.id.tv_web_phone_communicate })
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tv_web_phone_communicate:
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ getResources().getString(R.string.customerServicePhone)));
			startActivity(intent);
			break;
		case R.id.left_action:
			WebPhoneActivity.this.finish();
			break;
		case R.id.imgBtn_download:
			try {
				Uri uri = Uri.parse("http://139.129.9.175:9999/czy/");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			} catch (Exception e) {// 手机上未安装浏览器
				showToast(R.string.hint);
			}
			break;

		default:
			break;
		}
	}
}
