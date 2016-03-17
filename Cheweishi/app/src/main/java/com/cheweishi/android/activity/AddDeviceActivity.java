package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 绑定设备
 * 
 * @author Xiaojin
 * 
 */
public class AddDeviceActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.tv_car_device)
	private EditText tv_car_device;
	@ViewInject(R.id.bt_addCar)
	private Button bt_addCar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device);
		ViewUtils.inject(this);
		initViews();
	}

	/**
	 * 初始化视图
	 */
	private void initViews() {
		title.setText(R.string.title_activity_add_device);
		left_action.setText(R.string.back);
		httpBiz = new HttpBiz(this);
	}

	@OnClick({ R.id.left_action, R.id.bt_addDevice })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.bt_addDevice:
			check();
			break;
		}
	}

	private void check() {
		if (StringUtil.isEmpty(tv_car_device.getText().toString())) {
			showToast("您还未填写设备号");
		} else {
			connectToServer();
		}
	}

	/**
	 * 请求服务器
	 */
	private void connectToServer() {
		ProgrosDialog.openDialog(this);
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", loginMessage.getUid());
		params.addBodyParameter("mobile", loginMessage.getMobile());
		params.addBodyParameter("cid", getIntent().getStringExtra("cid"));
		params.addBodyParameter("device", tv_car_device.getText().toString());
		httpBiz.httPostData(10001, API.ADD_DEVICE_URL, params, this);
	}

	/**
	 * 接受服务器返回JSON数据
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
		case 10001:// 服务器连接成功
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
		if (StringUtil.isEmpty(result)) {
			showToast(R.string.data_fail);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (StringUtil.isEquals(jsonObject.optString("state"),
						API.returnSuccess, true)) {
					Constant.CURRENT_REFRESH = Constant.CAR_MANAGER_REFRESH;
					Intent mIntent = new Intent();
					mIntent.setAction(Constant.REFRESH_FLAG);
					sendBroadcast(mIntent);// 发送广播，更新所有应为设备需要更新的Activity
					this.finish();
				} else {
					showToast(jsonObject.optString("message"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}
}
