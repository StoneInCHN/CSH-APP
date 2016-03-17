package com.cheweishi.android.activity;

import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.cheweishi.R.id;
import com.cheweishi.android.cheweishi.R.layout;
import com.cheweishi.android.cheweishi.R.string;
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
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InsurancePolicyActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private Button left_action;
	private static final int INSURANCE_CODE = 100001;
	@ViewInject(R.id.tv_insurance_name)
	private TextView tv_insurance_name;
	@ViewInject(R.id.tv_insurance_no)
	private TextView tv_insurance_no;
	@ViewInject(R.id.tv_insurance_startTime)
	private TextView tv_insurance_startTime;
	@ViewInject(R.id.tv_insurance_endTime)
	private TextView tv_insurance_endTime;
	@ViewInject(R.id.tv_insurance_desc)
	private TextView tv_insurance_desc;
	@ViewInject(R.id.tv_insurance_num)
	private TextView tv_insurance_num;
	@ViewInject(R.id.tv_insurance_total)
	private TextView tv_insurance_total;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insurance_policy);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		httpBiz = new HttpBiz(this);
		title.setText(R.string.insurance_policy);
		left_action.setText(R.string.back);
		InsuranceInfo insuranceInfo = (InsuranceInfo) getIntent().getExtras()
				.getSerializable("insuranceInfo");
		if (!StringUtil.isEmpty(insuranceInfo)) {
			tv_insurance_desc.setText(insuranceInfo.getDesc());
			tv_insurance_num.setText(insuranceInfo.getNum());
			tv_insurance_total.setText(insuranceInfo.getTotal());
			tv_insurance_startTime.setText(insuranceInfo.getStart());
			tv_insurance_endTime.setText(insuranceInfo.getEnd());
			tv_insurance_name.setText(insuranceInfo.getName());
			tv_insurance_no.setText(insuranceInfo.getPolicy_no());
		}
		// connectToServer();
	}

	public void turnToProtocol() {
		Intent intent = new Intent(this, RegistServiceActivity.class);
		intent.putExtra("recharge", true);
		startActivity(intent);
	}

	@OnClick({ R.id.left_action, R.id.tv_turnToProtocal })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.tv_turnToProtocal:
			turnToProtocol();
			break;
		}
	}

	// private void connectToServer() {
	// RequestParams rp = new RequestParams();
	// rp.addBodyParameter("uid", loginMessage.getUid());
	// rp.addBodyParameter("key", loginMessage.getKey());
	// rp.addBodyParameter("aid", loginMessage.getAccount().getAid());
	// rp.addBodyParameter("type", loginMessage.getAccount().getInsurance()
	// + "");
	// httpBiz.httPostData(INSURANCE_CODE, API.MyMoney_Insurance_URL, rp, this);
	// }
	//
	// @Override
	// public void receive(int code, String data) {
	// super.receive(code, data);
	// ProgrosDialog.closeProgrosDialog();
	// switch (code) {
	// case 400:
	// break;
	// case INSURANCE_CODE:
	// parseInsuranceJSON(data);
	// break;
	// }
	// }
	//
	// private void parseInsuranceJSON(String result) {
	// if (StringUtil.isEmpty(result)) {
	// return;
	// }
	// System.out.println(result);
	// try {
	// JSONObject jsonObject = new JSONObject(result);
	// if (StringUtil.isEquals(jsonObject.optString("operationState"),
	// API.returnSuccess, true)) {
	// String dataResult = new JSONObject(result).optString("data");
	// Gson gson = new Gson();
	// Type type = new TypeToken<InsuranceInfo>() {
	// }.getType();
	// InsuranceInfo insuranceInfo = gson.fromJson(dataResult, type);
	// if (!StringUtil.isEmpty(insuranceInfo)) {
	// tv_insurance_desc.setText(loginMessage.getAccount()
	// .getTotal() + "元");
	// tv_insurance_num.setText(insuranceInfo.getNum());
	// tv_insurance_total.setText(insuranceInfo.getTotal());
	// tv_insurance_startTime.setText(insuranceInfo.getStart());
	// tv_insurance_endTime.setText(insuranceInfo.getEnd());
	// tv_insurance_name.setText(insuranceInfo.getName());
	// } else {
	// }
	//
	// } else {
	// showToast(jsonObject.optJSONObject("data").optString("msg"));
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
