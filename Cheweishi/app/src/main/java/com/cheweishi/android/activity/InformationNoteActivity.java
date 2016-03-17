package com.cheweishi.android.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.WashcarHistoryAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.JsonObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author zhangQ
 * @date 创建时间：2015-11-26 上午11:53:09
 * @version 1.0
 * @Description:
 */
public class InformationNoteActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.information_no_car_wash)
	private CheckBox information_no_car_wash;
	@ViewInject(R.id.information_else)
	private CheckBox information_else;
	@ViewInject(R.id.information_ed_else)
	private EditText information_ed_else;
	@ViewInject(R.id.tv_wash_submit)
	private Button tv_wash_submit;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	private int box;
	private HttpBiz httpBiz;
	private String desc = "";

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_note);
		ViewUtils.inject(this);
		left_action.setText("返回");
		title.setText("情况说明");

	}

	@OnClick({ R.id.information_no_car_wash, R.id.information_else,
			R.id.tv_wash_submit, R.id.left_action, })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.information_no_car_wash:
			box = 1;
			information_else.setChecked(false);
			information_ed_else.setVisibility(View.GONE);
			break;
		case R.id.information_else:
			box = 2;
			information_no_car_wash.setChecked(false);
			information_ed_else.setVisibility(View.VISIBLE);
			break;
		case R.id.tv_wash_submit:
			httpBiz = new HttpBiz(InformationNoteActivity.this);
			Intent intent = getIntent();
			RequestParams mRequestParams = new RequestParams();
			mRequestParams.addBodyParameter("uid",
					loginMessage.getUid());
			mRequestParams.addBodyParameter("key",
					loginMessage.getKey());
			mRequestParams.addBodyParameter("carwashId",intent.getStringExtra("userId"));
			mRequestParams.addBodyParameter("orderNumber",intent.getStringExtra("orderId"));
			if(box == 1){
				desc = (String) getText(R.string.wash_no);
			}else if(box == 2){
				desc = information_ed_else.getText().toString();
				if(desc.equals("")){
					showToast("输入不能为空！");
					return;
				}else{
					mRequestParams.addBodyParameter("desc",
							desc);
				}
			}
			httpBiz.httPostData(10001, API.CONFIRM_AN_ORDER_cause,
					mRequestParams, InformationNoteActivity.this);
			break;
		case R.id.left_action:
			finish();
		default:
			break;
		}

	}
	
	@Override
	public void receive(int type, String data) {
		switch (type) {
		case 400:
			Log.i("washcarorder", "result===400");
			break;
		case 10001:
			parseWashCarOrderCauseJSON(data);
			break;
		}
	}
	

	private void parseWashCarOrderCauseJSON(String results) {
		if (StringUtil.isEmpty(results)) {
			Log.i("washcarorder", "result===null");
			return;
		}
		Log.i("result============",results);
		try {
			JSONObject jsonObject = new JSONObject(results);
			if("SUCCESS".equals(jsonObject.get("operationState"))){
				showToast(jsonObject.optJSONObject("data").optString("msg"));
				InformationNoteActivity.this.finish();
			}else if("RELOGIN".equalsIgnoreCase(jsonObject
					.optString("operationState"))){
				DialogTool.getInstance(InformationNoteActivity.this).showConflictDialog();
			}else if("FAIL".equals(jsonObject.opt("operationState"))){
				Toast.makeText(InformationNoteActivity.this, jsonObject.optJSONObject("data").optString("msg"), Toast.LENGTH_SHORT).show();
			}else if("DEFAULT".equals(jsonObject.opt("operationState"))){
				Toast.makeText(InformationNoteActivity.this, jsonObject.optJSONObject("data").optString("msg"), Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
