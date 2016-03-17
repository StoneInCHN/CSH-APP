package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 报警设置
 * 
 * @author mingdasen
 * 
 */
public class SetReportActivity extends BaseActivity {

	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.checkbox1)
	private CheckBox box1;
	@ViewInject(R.id.checkbox2)
	private CheckBox box2;
	@ViewInject(R.id.checkbox3)
	private CheckBox box3;
	@ViewInject(R.id.checkbox4)
	private CheckBox box4;
	@ViewInject(R.id.checkbox5)
	private CheckBox box5;
	@ViewInject(R.id.tv_1)
	private TextView tv_1;
	@ViewInject(R.id.tv_2)
	private TextView tv_2;
	@ViewInject(R.id.tv_3)
	private TextView tv_3;
	@ViewInject(R.id.tv_4)
	private TextView tv_4;
	@ViewInject(R.id.tv_5)
	private TextView tv_5;
	private String status = "";
	private int type = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setreprot);
		ViewUtils.inject(this);
		left_action.setText(R.string.back);
		title.setText(R.string.warning_title);
		setListener();
		getReportSetInfo();
	}

	/**
	 * 获取警告设置信息
	 */
	private void getReportSetInfo() {
		if (isLogined()) {
			ProgrosDialog.openDialog(SetReportActivity.this);
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			httpBiz = new HttpBiz(SetReportActivity.this);
			httpBiz.httPostData(100, API.SOS_LIST_URL, rp, this);
		} else {
			startActivity(new Intent(SetReportActivity.this,
					LoginActivity.class));
		}
	}

	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 100:
			pareJSONGet(data);
			break;
		case 101:
			pareJSONSet(data);
			break;
		default:
			break;
		}
	}

	/**
	 * 设置结果解析
	 * 
	 * @param data
	 */
	private void pareJSONSet(String data) {
		if (data == null) {
			showToast(R.string.get_failed_please_check);
			setStatucFail();
		} else {
			try {
				JSONObject jsonObject = new JSONObject(data);
				String status = jsonObject.optString("operationState");
				if (StringUtil.isEquals("SUCCESS", status, true)) {
					setStatucSuccess();
				} else if (StringUtil.isEquals("FAIL", status, true)) {
					setStatucFail();
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (StringUtil.isEquals("RELOGIN", status, true)) {
					DialogTool.getInstance(SetReportActivity.this)
							.showConflictDialog();
				} else if (StringUtil.isEquals("DEFAULT", status, true)) {
					setStatucFail();
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 设置成功
	 */
	private void setStatucSuccess() {
		if (status.equals("0")) {
			setCheckBox(type, "0");
		} else {
			setCheckBox(type, "1");
		}
	}
	
	/**
	 * 设置失败
	 */
	private void setStatucFail() {
		if (status.equals("0")) {
			setCheckBox(type, "1");
		} else {
			setCheckBox(type, "0");
		}
	}
	
	
	/**
	 * 设置checkbox状态
	 * @param type
	 * @param status
	 */
	private void setCheckBox(int type, String status) {
		switch (type) {
		case 0:
			if (status.equals("0")) {
				box1.setChecked(false);
				tv_1.setVisibility(View.VISIBLE);
			} else {
				box1.setChecked(true);
				tv_1.setVisibility(View.INVISIBLE);
			}
			break;
		case 1:
			if (status.equals("0")) {
				box2.setChecked(false);
				tv_2.setVisibility(View.VISIBLE);
			} else {
				box2.setChecked(true);
				tv_2.setVisibility(View.INVISIBLE);
			}
			break;
		case 2:
			if (status.equals("0")) {
				box3.setChecked(false);
				tv_3.setVisibility(View.VISIBLE);
			} else {
				box3.setChecked(true);
				tv_3.setVisibility(View.INVISIBLE);
			}
			break;
		case 3:
			if (status.equals("0")) {
				box4.setChecked(false);
				tv_4.setVisibility(View.VISIBLE);
			} else {
				box4.setChecked(true);
				tv_4.setVisibility(View.INVISIBLE);
			}
			break;
			
		case 6:
			if (status.equals("0")) {
				box5.setChecked(false);
				tv_5.setVisibility(View.VISIBLE);
			} else {
				box5.setChecked(true);
				tv_5.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 获取数据
	 * 
	 * @param data
	 */
	private void pareJSONGet(String data) {
		if (data == null) {
			showToast(R.string.get_failed_please_check);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(data);
				String statu = jsonObject.optString("operationState");
				if (StringUtil.isEquals("SUCCESS", statu, true)) {
					String status = jsonObject.optJSONObject("data").optString(
							"status");
					initCheckBox(status);
				} else if (StringUtil.isEquals("FAIL", statu, true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (StringUtil.isEquals("RELOGIN", statu, true)) {
					DialogTool.getInstance(SetReportActivity.this)
							.showConflictDialog();
				} else if (StringUtil.isEquals("DEFAULT", statu, true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化设置状态
	 * 
	 * @param status
	 */
	private void initCheckBox(String status) {
		if (status.substring(0, 1).equals("0")) {
			box1.setChecked(false);
			tv_1.setVisibility(View.VISIBLE);
		} else {
			box1.setChecked(true);
			tv_1.setVisibility(View.INVISIBLE);
		}
		if (status.substring(1, 2).equals("0")) {
			box2.setChecked(false);
			tv_2.setVisibility(View.VISIBLE);
		} else {
			box2.setChecked(true);
			tv_2.setVisibility(View.INVISIBLE);
		}
		if (status.substring(2, 3).equals("0")) {
			box3.setChecked(false);
			tv_3.setVisibility(View.VISIBLE);
		} else {
			box3.setChecked(true);
			tv_3.setVisibility(View.INVISIBLE);
		}
		if (status.substring(3, 4).equals("0")) {
			box4.setChecked(false);
			tv_4.setVisibility(View.VISIBLE);
		} else {
			box4.setChecked(true);
			tv_4.setVisibility(View.INVISIBLE);
		}
		if (status.substring(6, 7).equals("0")) {
			box5.setChecked(false);
			tv_5.setVisibility(View.VISIBLE);
		} else {
			box5.setChecked(true);
			tv_5.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		box1.setOnCheckedChangeListener(listener);
		box2.setOnCheckedChangeListener(listener);
		box3.setOnCheckedChangeListener(listener);
		box4.setOnCheckedChangeListener(listener);
		box5.setOnCheckedChangeListener(listener);
		left_action.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SetReportActivity.this.finish();
			}
		});
	}

	OnCheckedChangeListener listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton btn, boolean arg1) {
			switch (btn.getId()) {
			case R.id.checkbox1:
				if (arg1) {
					status = "1";
					tv_1.setVisibility(View.INVISIBLE);
				} else {
					status = "0";
					tv_1.setVisibility(View.VISIBLE);
				}
				type = 0;
				setReportStatus(type, status);
				break;
			case R.id.checkbox2:
				if (arg1) {
					status = "1";
					tv_2.setVisibility(View.INVISIBLE);
				} else {
					status = "0";
					tv_2.setVisibility(View.VISIBLE);
				}
				type = 1;
				setReportStatus(type, status);
				break;
			case R.id.checkbox3:
				if (arg1) {
					status = "1";
					tv_3.setVisibility(View.INVISIBLE);
				} else {
					status = "0";
					tv_3.setVisibility(View.VISIBLE);
				}
				type = 2;
				setReportStatus(type, status);
				break;
			case R.id.checkbox4:
				if (arg1) {
					status = "1";
					tv_4.setVisibility(View.INVISIBLE);
				} else {
					status = "0";
					tv_4.setVisibility(View.VISIBLE);
				}
				type = 3;
				setReportStatus(type, status);
				break;
			case R.id.checkbox5:
				if (arg1) {
					status = "1";
					tv_5.setVisibility(View.INVISIBLE);
				} else {
					status = "0";
					tv_5.setVisibility(View.VISIBLE);
				}
				type = 6;
				setReportStatus(type, status);
				break;
			default:
				break;
			}
		}
	};

	private void setReportStatus(int type, String status) {
		if (isLogined()) {
			ProgrosDialog.openDialog(SetReportActivity.this);
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			rp.addBodyParameter("type", type + "");
			rp.addBodyParameter("status", status);
			httpBiz = new HttpBiz(SetReportActivity.this);
			httpBiz.httPostData(101, API.SOS_STATUS_URL, rp, this);
		} else {
			startActivity(new Intent(SetReportActivity.this,
					LoginActivity.class));
		}
	}
}
