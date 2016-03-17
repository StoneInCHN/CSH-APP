package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;


/****
 * 消息详情
 * @author lw
 *
 */
public class PushDetailsActivity extends BaseActivity {

	@ViewInject(R.id.left_action)
	private TextView mleft;

	@ViewInject(R.id.title)
	private TextView mtitle;

	@ViewInject(R.id.push_to_checkbox)
	private CheckBox mType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_details);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		mtitle.setText(R.string.msg_push_set);
		mleft.setText(getResources().getString(R.string.back));

		mleft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				PushDetailsActivity.this.finish();

			}
		});
		if (isLogined()) {
			if (loginMessage.getIsPush() == 0) {
				mType.setChecked(false);
			} else {
				mType.setChecked(true);
			}
		}
		//判断用户是否登陆 ，没有登陆就不能选择推送
		if (isLogined()) {
			mType.setOnCheckedChangeListener(listener);
		}else {
			mType.setEnabled(false);
		}

	}

	//改变推送消息的监听
	OnCheckedChangeListener listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				messageConnectToServer("1");
				loginMessage.setIsPush(1);
			} else {
				messageConnectToServer("0");
				loginMessage.setIsPush(0);
			}
			LoginMessageUtils.saveProduct(loginMessage,
					PushDetailsActivity.this);
		}
	};

	//通知后台更改推送状态
	private void messageConnectToServer(String type) {

		if (!isLogined()) {
			Intent intent = new Intent(PushDetailsActivity.this,
					LoginActivity.class);
			this.startActivity(intent);
			this.finish();
		} else {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("key", loginMessage.getKey());
			params.addBodyParameter("type", type);
			httpBiz = new HttpBiz(PushDetailsActivity.this);
			httpBiz.httPostData(1006, API.ACCEPT_MESSAGE, params, this);
		}
	}

	@Override
	public void receive(int type, String data) {
		switch (type) {
		case 1006:
			parseMessageJSON(data);
			break;

		default:
			break;
		}
	}

	private void parseMessageJSON(String result) {
		System.out.println("用户退出====" + result);
		if (result == null || result.equals("")) {
			showToast(R.string.no_result);
			setPushFail();
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String status = jsonObject.optString("operationState");
				if (StringUtil.isEquals(status, "SUCCESS", true)) {
					Log.i("result", "==push=type==推送=" + result);
					System.out.println("消息推送===========" + result);
				} else if (StringUtil.isEquals(status, "FAIL", true)) {
					setPushFail();
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (StringUtil.isEquals(status, "RELOGIN", true)) {
					DialogTool.getInstance(PushDetailsActivity.this).showConflictDialog();
				} else if (StringUtil.isEquals(status, "DEFAULT", true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
					setPushFail();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				setPushFail();
			}
		}
	}
	
	private void setPushFail(){
		if (mType.isChecked()) {
			mType.setChecked(false);
		} else {
			mType.setChecked(true);
		}
	}
}
