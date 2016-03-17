package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 取消订单
 * 
 * @author Xiaojin
 * 
 */
public class MaintainCancelActivity extends BaseActivity {

	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.tv_cancel_sumbit)
	private TextView tv_cancel_sumbit;
	@ViewInject(R.id.cbtime)
	private CheckBox cbtime;
	@ViewInject(R.id.cbserve)
	private CheckBox cbserve;
	@ViewInject(R.id.ckNone)
	private CheckBox ckNone;
	@ViewInject(R.id.et_reason)
	private EditText et_reason;
	private String reason = "等待时间过长";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cancel_order);
		ViewUtils.inject(this);
		init();
	}

	/**
	 * 初始化视图
	 */
	private void init() {
		left_action.setText("返回");
		title.setText("取消订单");
	}

	@OnClick({ R.id.left_action, R.id.tv_cancel_sumbit, R.id.cbserve,
			R.id.cbtime, R.id.ckNone })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:// 返回
			MaintainCancelActivity.this.finish();
			break;
		case R.id.tv_cancel_sumbit:
			connectToServer();
			break;
		case R.id.cbserve:
			reason = "我已经不需要此服务";
			cbserve.setChecked(true);
			cbtime.setChecked(false);
			ckNone.setChecked(false);
			break;
		case R.id.cbtime:
			reason = "等待时间过长";
			cbserve.setChecked(false);
			cbtime.setChecked(true);
			ckNone.setChecked(false);
			break;
		case R.id.ckNone:
			cbserve.setChecked(false);
			cbtime.setChecked(false);
			ckNone.setChecked(true);
			break;
		default:
			break;
		}
	}

	/**
	 * 请求服务器，取消订单
	 */
	private void connectToServer() {
		if (ckNone.isChecked()) {
			reason = et_reason.getText().toString();
		}
		if (StringUtil.isEmpty(reason)) {
			showToast("请输入取消订单原因");
			return;
		}
		ProgrosDialog.openDialog(this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("uid", loginMessage.getUid());
		rp.addBodyParameter("mobile", loginMessage.getMobile());
		rp.addBodyParameter("orderId", getIntent().getStringExtra("orderId"));
		rp.addBodyParameter("cancel_reasons", reason);
		httpBiz = new HttpBiz(this);
		httpBiz.httPostData(10002, API.CANCEL_ORDER_URL, rp, this);
	}

	/**
	 * 接收服务器返回的JSON数据
	 */
	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 400:// 服务器连接失败
			showToast(R.string.server_link_fault);
			break;
		case 10002:// 服务器连接成功
			parseJSON(data);
			break;
		}
	}

	/**
	 * 解析服务器返回的JSON数据
	 * 
	 * @param result
	 */
	private void parseJSON(String result) {
		if (StringUtil.isEmpty(result)) {// JSON数据返回错误
			return;
		}
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (StringUtil.isEquals(jsonObject.optString("state"),
					API.returnSuccess, true)) {
				sendBroadcase();
				finish();

			} else if (StringUtil.isEquals(jsonObject.optString("state"),
					API.returnRelogin, true)) {
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
	 * 发送广播，更新因为取消订单而改变的Activity
	 */
	private void sendBroadcase() {
		Constant.CURRENT_REFRESH = Constant.CANCEL_ORDER_SUCCESS_REFRESH;
		Intent mIntent = new Intent();
		mIntent.setAction(Constant.REFRESH_FLAG);
		sendBroadcast(mIntent);
	}

}
