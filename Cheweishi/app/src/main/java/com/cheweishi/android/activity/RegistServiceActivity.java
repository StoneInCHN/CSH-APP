package com.cheweishi.android.activity;

import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.InsuranceInfo;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author Xiaojin注册服务条款
 * 
 */
public class RegistServiceActivity extends BaseActivity {
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.service_content)
	private TextView service_content;
	private static final int INSURANCE_CODE = 100001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist_service);
		ViewUtils.inject(this);
		httpBiz = new HttpBiz(this);
		left_action.setText(R.string.back);
		if (getIntent().getBooleanExtra("recharge", false) == true) {
			title.setText(R.string.insurance_protocol_title);
			// service_content.setText(R.string.recharge_protocol);
			service_content.setText("");
		} else {
			service_content.setVisibility(View.VISIBLE);
			title.setText(R.string.carLift_customerRule);
		}
		left_action.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RegistServiceActivity.this.finish();
			}
		});
		service_content
				.setMovementMethod(ScrollingMovementMethod.getInstance());
		connectToServer();
	}

	private void connectToServer() {
		RequestParams rp = new RequestParams();
		ProgrosDialog.openDialog(this);
		httpBiz.httPostData(INSURANCE_CODE, API.PROTOCAL_Insurance_URL, rp,
				this);
	}

	@Override
	public void receive(int code, String data) {
		super.receive(code, data);
		ProgrosDialog.closeProgrosDialog();
		switch (code) {
		case 400:
			showToast(R.string.server_link_fault);
			break;
		case INSURANCE_CODE:
			parseInsuranceJSON(data);
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
				String dataResult = new JSONObject(result)
						.optJSONObject("data").optString("content");
				service_content.setText(dataResult);
				service_content.setVisibility(View.VISIBLE);
			} else {
				showToast(jsonObject.optJSONObject("data").optString("msg"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
