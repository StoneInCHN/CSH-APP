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
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.InsuranceInfo;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.NoUnderlineSpan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InsuranceBuyActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.tv_insurance_desc)
	private TextView tv_insurance_desc;
	@ViewInject(R.id.tv_insurance_time)
	private TextView tv_insurance_time;
	@ViewInject(R.id.tv_insurance_num)
	private TextView tv_insurance_num;
	@ViewInject(R.id.tv_insurance_total)
	private TextView tv_insurance_total;
	private static final int INSURANCE_CODE = 100001;
	private static final int INSURANCE_BUY_CODE = 100002;
	private static final int RELOGINType = 10007;
	private CustomDialog.Builder builder;
	private CustomDialog phoneDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insurance_buy);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		title.setText(R.string.insurance_buy_title);
		left_action.setText(R.string.back);
		httpBiz = new HttpBiz(this);
		// connectToServer();
		InsuranceInfo insuranceInfo = (InsuranceInfo) getIntent().getExtras()
				.getSerializable("insuranceInfo");
		if (!StringUtil.isEmpty(insuranceInfo)) {
			tv_insurance_desc.setText(insuranceInfo.getDesc());
			tv_insurance_num.setText(insuranceInfo.getNum());
			tv_insurance_time.setText(insuranceInfo.getTime());
			tv_insurance_total.setText(insuranceInfo.getTotal());
			SpannableString sp = new SpannableString(insuranceInfo.getTotal());
			sp.setSpan(
					new ForegroundColorSpan(this.getResources().getColor(
							R.color.gray_normal)), insuranceInfo.getTotal()
							.length() - 1, insuranceInfo.getTotal().length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			tv_insurance_total.setText(sp);
		}
	}

	public void payInsurance(View view) {

	}

	public void turnToProtocol1() {
		Intent intent = new Intent(this, RegistServiceActivity.class);
		intent.putExtra("recharge", true);
		startActivity(intent);
	}

	@OnClick({ R.id.left_action, R.id.bt_insurance_buy, R.id.tv_turnToProtocol1 })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.bt_insurance_buy:
			showPhoneDialog();
			break;
		case R.id.tv_turnToProtocol1:
			turnToProtocol1();
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

	private void connectToByInsuranceServer() {
		
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("uid", loginMessage.getUid());
		rp.addBodyParameter("key", loginMessage.getKey());
		ProgrosDialog.openDialog(this);
		httpBiz.httPostData(INSURANCE_BUY_CODE, API.INSURANCE_BUY_URL, rp, this);
	}

	@Override
	public void receive(int code, String data) {
		super.receive(code, data);
		ProgrosDialog.closeProgrosDialog();
		switch (code) {
		case 400:
			showToast(R.string.server_link_fault);
			break;
		// case INSURANCE_CODE:
		// parseInsuranceJSON(data);
		// break;
		case INSURANCE_BUY_CODE:
			parseBuyInsuranceJSON(data);
			break;
		case RELOGINType:
			parseJSON(data);
			break;
		}
	}

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
	// .getTotal());
	// tv_insurance_num.setText(insuranceInfo.getNum());
	// tv_insurance_time.setText(insuranceInfo.getTime());
	// tv_insurance_total.setText(insuranceInfo.getTotal());
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

	private void parseBuyInsuranceJSON(String result) {
		if (StringUtil.isEmpty(result)) {
			return;
		}
		System.out.println(result);
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (StringUtil.isEquals(jsonObject.optString("operationState"),
					API.returnSuccess, true)) {
				buySuccess();
			} else if (StringUtil.isEquals(
					jsonObject.optString("operationState"), API.returnFail,
					true)) {
				showToast(jsonObject.optJSONObject("data").optString("msg"));
			} else if (StringUtil.isEquals(
					jsonObject.optString("operationState"), API.returnRelogin,
					true)) {
				DialogTool.getInstance(this).showConflictDialog();
			} else if (StringUtil.isEquals(
					jsonObject.optString("operationState"), API.returnDefault,
					true)) {
				showToast(jsonObject.optJSONObject("data").optString("msg"));
			}
			showToast(jsonObject.optJSONObject("data").optString("msg"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void buySuccess() {
		submitLogin();

	}

	private void submitLogin() {
		// TODO Auto-generated method stub

		if (isLogined()) {
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			httpBiz.httPostData(RELOGINType, API.LOGIN_MESSAGE_RELOGIN_URL, rp,
					this);
		}
	}

	private void parseJSON(String data) {
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
				// judgeJSONParse(type, jsonObject.optString("data"));
				save(jsonObject.optString("data"));
			} else {
				if (StringUtil.isEquals(resultStr, API.returnRelogin, true)) {
					DialogTool.getInstance(this).showConflictDialog();
				} else {
					showToast(resultMsg);
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void save(String jsonObject) {
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<LoginMessage>() {
		}.getType();
		loginMessage = gson.fromJson(jsonObject, type);
		LoginMessageUtils.saveProduct(loginMessage, this);
		Intent mIntent = new Intent();
		Constant.CURRENT_REFRESH = Constant.INSURANCE_REFRESH;
		mIntent.setAction(Constant.REFRESH_FLAG);
		sendBroadcast(mIntent);
		finish();
	}

	/**
	 * 联系客服对话框
	 */
	private void showPhoneDialog() {
		builder = new CustomDialog.Builder(this);
		builder.setMessage(R.string.insurance_message_title);
		builder.setTitle(R.string.remind);
		builder.setPositiveButton(R.string.insurance_buy_sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						connectToByInsuranceServer();

					}
				});

		builder.setNegativeButton(R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// setRadioButtonLight();
					}
				});
		phoneDialog = builder.create();
		phoneDialog.show();
	}
}
