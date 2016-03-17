package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.tools.APPTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.RegularExpressionTools;
import com.cheweishi.android.tools.ReturnBackDialogRemindTools;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 
 * @author Xiaojin个人中心-个性签名修改
 * 
 */
public class UserSignActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.et_userSign)
	private EditText et_userSign;
	private String userNote = "";
	private boolean resetText;
	// 输入表情前的光标位置
	private int cursorPos;
	// 输入表情前EditText中的文本
	private String tmp;
	private String pre = "";
	@ViewInject(R.id.currentFontSize)
	private TextView currentFontSize;
	int countFont = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_sign);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		// closeBoard();
		httpBiz = new HttpBiz(this);
		title.setText(R.string.individualSignature);
		left_action.setText(R.string.back);
		right_action.setText(R.string.finish);
		userNote = getIntent().getStringExtra("sign");
		if (!StringUtil.isEmpty(userNote)) {
			et_userSign.setText(userNote);
			et_userSign.setSelection(userNote.length());
			currentFontSize.setText(et_userSign.length() + "");
		}
		pre = loginMessage.getSignature();
		et_userSign.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				currentFontSize.setText(s.length() + "");
				CharSequence input = null;
				if (!resetText) {
					if (count >= 2) {
						// 提取输入的长度大于3的文本

						try {
							input = s.subSequence(cursorPos, cursorPos + count);
						} catch (Exception e) {
							try {
								input = s.subSequence(cursorPos - 1, cursorPos
										+ count - 1);
							} catch (Exception e1) {

							}
						}
						// 正则匹配是否是表情符号
						// Matcher matcher = pattern.matcher(input.toString());
						if (input != null
								&& !RegularExpressionTools
										.isFacingExpression(input.toString())) {
							if (RegularExpressionTools.isChinese(input
									.toString())) {
							} else if (RegularExpressionTools.isAllChar(input
									.toString())) {

							} else {
								resetText = true;
								// 是表情符号就将文本还原为输入表情符号之前的内容
								et_userSign.setText(tmp);
								currentFontSize.setText(tmp.length() + "");
								et_userSign.invalidate();
								et_userSign.setSelection(et_userSign.getText()
										.toString().length());
								showToast(R.string.expression_notSupport);
							}
						}
					}
				} else {
					resetText = false;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				if (!resetText) {
					cursorPos = et_userSign.getSelectionEnd();
					tmp = s.toString();// 这里用s.toString()而不直接用s是因为如果用s，那么，tmp和s在内存中指向的是同一个地址，s改变了，tmp也就改变了，那么表情过滤就失败了
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@OnClick({ R.id.left_action, R.id.right_action })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			if (!et_userSign.getText().toString().equals(pre)) {
				ReturnBackDialogRemindTools.getInstance(UserSignActivity.this)
						.show();
			} else {
				UserSignActivity.this.finish();
			}
			break;
		case R.id.right_action:
			connectToServer();
			break;
		}
	}

	/**
	 * 个性签名提交服务器
	 */
	private void connectToServer() {
		if (isLogined()) {
			ProgrosDialog.openDialog(this);
			right_action.setClickable(false);
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("signature", et_userSign.getText().toString());
			httpBiz.httPostData(10000, API.CSH_UPDATE_USER_SIGN_URL, params, this);
		}
	}

	/**
	 * 接受网络请求回调参数并判断
	 */
	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 10000:
			parseUserDetailJSON(data);
			break;
		case 400:
			right_action.setClickable(true);
			break;
		}
	}

	/**
	 * 解析服务返回的Json数据
	 * 
	 * @param result
	 */
	private void parseUserDetailJSON(String result) {
		if (StringUtil.isEmpty(result)) {
			showToast(R.string.data_fail);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (StringUtil.isEquals(jsonObject.optString("state"),
						API.returnSuccess, true)) {
					et_userSign.setFocusable(false);
					showToast(R.string.individualSignature_setting_success);
					loginMessage.setSignature(et_userSign.getText().toString());
					LoginMessageUtils.saveProduct(loginMessage, this);
					Constant.CURRENT_REFRESH = Constant.SPECIAL_SIGN_REFRESH;
					Intent mIntent = new Intent();
					mIntent.setAction(Constant.REFRESH_FLAG);
					sendBroadcast(mIntent);
					finish();
				} else {
					right_action.setClickable(true);
					showToast(jsonObject.optString("message"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * 控制Android系统返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (!et_userSign.getText().toString().equals(pre)) {
				ReturnBackDialogRemindTools.getInstance(this).show();
			} else {
				APPTools.closeBoard(this, et_userSign);
				UserSignActivity.this.finish();
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
