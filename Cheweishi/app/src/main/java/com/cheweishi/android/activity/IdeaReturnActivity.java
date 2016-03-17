package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.tools.RegularExpressionTools;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author zhangQ
 * @date 创建时间：2015-12-2 下午5:38:48
 * @version 1.0
 * @Description:
 */
public class IdeaReturnActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.idea_ed)
	private EditText idea_ed;
	@ViewInject(R.id.idea_btn)
	private Button idea_btn;
	private HttpBiz httpBiz;
	private String submit = "";
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.tv_idea_count)
	private TextView tv_idea_count;
	private boolean resetText;
	private int cursorPos;
	// 输入表情前EditText中的文本
	private String tmp;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.idea_return);
		ViewUtils.inject(this);
		init();
		idea_ed.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				tv_idea_count.setText(arg0.length() + "/100");
				CharSequence input = null;
				if (!resetText) {
					if (arg3 >= 2) {
						// 提取输入的长度大于2的文本

						try {
							input = arg0.subSequence(cursorPos, cursorPos
									+ arg3);
						} catch (Exception e) {
							try {
								input = arg0.subSequence(cursorPos - 1,
										cursorPos + arg3 - 1);
							} catch (Exception e1) {

							}
						}
						// 正则匹配是否是表情符号
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
								idea_ed.setText(tmp);
								tv_idea_count.setText(tmp.length() + "/100");
								idea_ed.invalidate();
								idea_ed.setSelection(idea_ed.getText()
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
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				if (!resetText) {
					cursorPos = idea_ed.getSelectionEnd();
					tmp = arg0.toString();// 这里用s.toString()而不直接用s是因为如果用s，那么，tmp和s在内存中指向的是同一个地址，s改变了，tmp也就改变了，那么表情过滤就失败了
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void init() {
		title.setText(R.string.feed_back);
		left_action.setText(R.string.back);
		// goToLoginFirst();
		if (!isLogined()) {
			Intent intent = new Intent();
			intent.setClass(IdeaReturnActivity.this, LoginActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.score_business_query_enter,
					R.anim.score_business_query_exit);
		}
	}

	@OnClick({ R.id.idea_btn, R.id.left_action })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.idea_btn:
			submit = idea_ed.getText().toString();
			httpBiz = new HttpBiz(IdeaReturnActivity.this);
			RequestParams mRequestParams = new RequestParams();
			// mRequestParams.addBodyParameter("carwashId",
			// intent.getStringExtra("userId"));
			// mRequestParams.addBodyParameter("orderNumber",
			// intent.getStringExtra("orderId"));
			if (StringUtil.isEmpty(submit)) {
				showToast("输入不能为空！");
				return;

			} else {
				ProgrosDialog.openDialog(this);
				mRequestParams.addBodyParameter("uid", loginMessage.getUid());
				mRequestParams.addBodyParameter("body", submit);
				httpBiz.httPostData(10001, API.CSH_RETUR_ORDER_URL,
						mRequestParams, this);
			}
			break;
		case R.id.left_action:
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		Log.i("=======", data);
		switch (type) {
		case 400:
			Log.i("idea", "result===400");
			break;
		case 10001:
			IdeaReturnJSON(data);
			break;

		default:
			break;
		}

	}

	private void IdeaReturnJSON(String results) {
		if (StringUtil.isEmpty(results)) {
			Log.i("idea", "result===null");
			return;
		}
		Log.i("result============", results);
		try {
			JSONObject jsonObject = new JSONObject(results);
			if (StringUtil.isEquals(API.returnSuccess,
					jsonObject.optString("state"), true)) {
				IdeaReturnActivity.this.finish();

			} else if (StringUtil.isEquals(API.returnRelogin,
					jsonObject.optString("state"), true)) {
				ReLoginDialog.getInstance(this).showDialog(
						jsonObject.optString("message"));
			} else {
				showToast(jsonObject.optString("message"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
