package com.cheweishi.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.Account;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 提现
 * 
 * @author 刘伟
 * 
 */
public class WithdrawDepositActivity extends BaseActivity {

	@ViewInject(R.id.left_action)
	private Button mLeft;
	@ViewInject(R.id.treasureLayout)
	private LinearLayout treasureLayout;
	@ViewInject(R.id.title)
	private TextView mTitle;
	@ViewInject(R.id.requestFocusLayout)
	private LinearLayout requestFocusLayout;
	@ViewInject(R.id.withdraw_deposit_tv_can_money)
	private TextView withdraw_deposit_tv_can_money;

	@ViewInject(R.id.withdraw_deposit_et_money)
	private EditText withdraw_deposit_et_money;

	@ViewInject(R.id.withdraw_deposit_btn_sumbit)
	private Button withdraw_deposit_btn_sumbit;

	@ViewInject(R.id.withdraw_deposit_tv_chongxingshuru)
	private TextView withdraw_deposit_tv_chongxingshuru;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_withdraw_deposit);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		mTitle.setText(getString(R.string.withdraw_deposit));
		mLeft.setText(getResources().getString(R.string.back));
		withdraw_deposit_et_money.clearFocus();
		initListener();
		isLogin();
		setPricePoint(withdraw_deposit_et_money);
	}

	/***
	 * 
	 * 为控件绑定事件监听
	 */
	private void initListener() {
		mLeft.setOnClickListener(listener);
		treasureLayout.setOnClickListener(listener);
		withdraw_deposit_et_money.setOnClickListener(listener);
		withdraw_deposit_btn_sumbit.setOnClickListener(listener);
		withdraw_deposit_et_money.addTextChangedListener(textWatcher);
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			withdraw_deposit_tv_chongxingshuru.setVisibility(View.INVISIBLE);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 判断是否登陆
	 */
	private void isLogin() {
		// TODO Auto-generated method stub
		if (isLogined()) {
			setCash();
		}

	}

	/***
	 * 显示可提现金额
	 */
	private void setCash() {
		// TODO Auto-generated method stub
		if (getCash() != null && !getCash().equals("")) {
			withdraw_deposit_tv_can_money.setText(getCash());
		} else {
			withdraw_deposit_tv_can_money.setText("0");
		}
	}

	/***
	 * 获得可提现金额
	 * 
	 * @return 可提现金额
	 */
	private String getCash() {
		// TODO Auto-generated method stub
		return getAccount().getCash();
	}

	/**
	 * 得到返费的account
	 * 
	 * @return account
	 */
	private Account getAccount() {
		// TODO Auto-generated method stub
		return loginMessage.getAccount();
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.withdraw_deposit_et_money:
				break;
			case R.id.treasureLayout:
				requestFocusLayout.setFocusable(true);
				requestFocusLayout.setFocusableInTouchMode(true);
				requestFocusLayout.requestFocus();
				getMoney();
				break;
			case R.id.left_action:
				WithdrawDepositActivity.this.finish();
				break;

			case R.id.withdraw_deposit_btn_sumbit:
				break;

			default:
				break;
			}
		}
	};

	public void closeBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(withdraw_deposit_et_money.getWindowToken(),
				0);
	}

	/**
	 * 判断是否可以提现
	 */
	protected void getMoney() {
		// TODO Auto-generated method stub
		String money = withdraw_deposit_et_money.getText().toString().trim();
		if (money != null && !money.equals("") && !money.equals("0")
				&& getCash() != null && !getCash().equals("")) {
			try {
				double money_int = StringUtil.getDouble(money);
				double cash_int = StringUtil.getDouble(getCash());
				if (money_int > cash_int) {
					withdraw_deposit_tv_chongxingshuru
							.setVisibility(View.VISIBLE);
				} else if (money_int == 0.0) {
					withdraw_deposit_tv_chongxingshuru
							.setVisibility(View.VISIBLE);

				} else if (money_int == cash_int) {
					withdraw_deposit_btn_sumbit
							.setBackgroundResource(R.drawable.logout_red);
				} else {
					withdraw_deposit_tv_chongxingshuru
							.setVisibility(View.VISIBLE);

				}
			} catch (Exception e) {
				// TODO: handle exception
				showToast(getString(R.string.input_number));
			}
		} else {
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			lostFocus();
			return true;
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE) {
			lostFocus();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 隐藏键盘时失去焦点
	 */
	private void lostFocus() {
		// TODO Auto-generated method stub
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager.isActive()) {
			inputMethodManager.hideSoftInputFromWindow(
					WithdrawDepositActivity.this.getCurrentFocus()
							.getWindowToken(), 0);
		}
		withdraw_deposit_et_money.clearFocus();
		getMoney();
	}

	public void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}

		});

	}

}
