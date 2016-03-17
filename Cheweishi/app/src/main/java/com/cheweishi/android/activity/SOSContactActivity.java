package com.cheweishi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.tools.RegularExpressionTools;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 新增联系人
 * 
 * @author Xiaojin
 * 
 */
public class SOSContactActivity extends BaseActivity implements OnClickListener {

	@ViewInject(R.id.et_sos_contact_name)
	private EditText et_sos_contact_name;
	@ViewInject(R.id.et_sos_contact_tel)
	private EditText et_sos_contact_tel;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private TextView left_action;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	private final int ADD_TYPE = 10001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soscontact);
		ViewUtils.inject(this);
		initViews();

	}

	private void initViews() {
		httpBiz = new HttpBiz(this);
		left_action.setText(R.string.back);
		right_action.setText(R.string.button_add);
		title.setText(R.string.sos_contact_add);
		if (!StringUtil.isEmpty(getIntent().getStringExtra("name"))
				&& !StringUtil.isEmpty(getIntent().getStringExtra("tel"))) {
			et_sos_contact_name.setText(getIntent().getStringExtra("name"));
			et_sos_contact_name.setSelection(et_sos_contact_name.getText()
					.toString().length());
			et_sos_contact_tel.setText(getIntent().getStringExtra("tel"));
			right_action.setText(R.string.save);
		}
		setListeners();
	}

	private void setListeners() {
		left_action.setOnClickListener(this);
		right_action.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			SOSContactActivity.this.finish();
			break;
		case R.id.right_action:
			check();
			break;
		}
	}

	private void check() {
		String name = et_sos_contact_name.getText().toString()
				.replaceAll(" ", "");
		String tel = et_sos_contact_tel.getText().toString()
				.replaceAll(" ", "");
		if (StringUtil.isEmpty(name)) {
			showToast(R.string.sos_name_not_null);
		} else {
			if (StringUtil.isEmpty(tel)) {
				showToast(R.string.sos_tel_not_null);
			} else if (!RegularExpressionTools.isMobile(tel)) {
				showToast(R.string.tel_error);
			} else {
				Intent intent = new Intent();
				intent.putExtra("name", name);
				intent.putExtra("tel", tel);
				setResult(101, intent);
				finish();
			}
		}
		;
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		switch (type) {
		case ADD_TYPE:
			parseJSON(data);
			break;
		case 400:
			break;
		}
	}

	private void parseJSON(String result) {
		if (result == null) {
			showToast(R.string.data_fail);
		} else {

		}
	}
}
