package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.entity.Account;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/******
 * 我的财富
 * 
 * @author lw
 * 
 */
public class MyTreasureActivity extends BaseActivity {

	@ViewInject(R.id.left_action)
	private Button mLeft;

	@ViewInject(R.id.title)
	private TextView mTitle;

	@ViewInject(R.id.mytreasure_imageview_telephonecharge_declare)
	private ImageView mytreasure_imageview_telephonecharge_declare;

	@ViewInject(R.id.mytreasure_tv_checkdetails)
	private TextView mytreasure_tv_checkdetails;

	@ViewInject(R.id.my_treasure_tv_backmoney)
	private TextView my_treasure_tv_backmoney;

	@ViewInject(R.id.mytreasure_tv_returnthemoney_help)
	private TextView mytreasure_tv_returnthemoney_help;

	@ViewInject(R.id.my_treasue_btn_sumbit)
	private Button my_treasue_btn_sumbit;

	@ViewInject(R.id.my_treasure_phone_balance)
	private TextView my_treasure_phone_balance;

	@ViewInject(R.id.mytrwasure_tv_backmoney)
	private TextView mytrwasure_tv_backmoney;

	@ViewInject(R.id.mytreasure_tv_frostmoney)
	private TextView mytreasure_tv_frostmoney;

	@ViewInject(R.id.mytreasure_tv_cash)
	private TextView mytreasure_tv_cash;
	private static final int RELOGINType = 10007;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_treasure);
		// 为注解注册创建实例
		ViewUtils.inject(this);
		if (Constant.CALL_REQUEST) {
			submitLogin();
		} else {
			init();
		}
	}

	/*
	 * 初始化
	 */
	private void init() {
		mTitle.setText("话费余额");
		mLeft.setText(getResources().getString(R.string.back));
		initlistener();
		isLogin();
	}

	/**
	 * 判断是否登陆
	 */
	private void isLogin() {
		// TODO Auto-generated method stub
		if (LoginMessageUtils.getLoginMessage(this) != null) {
			setPhonemoney();
			setBackMoney();
			setFreeze();
			setCash();
		}
	}

	/**
	 * 显示可提现金额
	 */
	private void setCash() {
		// TODO Auto-generated method stub
		if (getCash() != null && !getCash().equals("")) {
			mytreasure_tv_cash.setText(getCash());
		} else {
			mytreasure_tv_cash.setText("0");
		}
	}

	/*
	 * 显示冻结金额
	 */
	private void setFreeze() {
		// TODO Auto-generated method stub
		if (getFreeze() != null && !getFreeze().equals("")) {
			mytreasure_tv_frostmoney.setText(getFreeze());
		} else {
			mytreasure_tv_frostmoney.setText("0");
		}
	}

	/**
	 * 显示所有返费金额
	 */
	private void setBackMoney() {
		// TODO Auto-generated method stub
		if (getBackMoney() != null && !getBackMoney().equals("")) {
			mytrwasure_tv_backmoney.setText(getBackMoney());
		} else {
			mytrwasure_tv_backmoney.setText("0");
		}
	}

	/**
	 * 显示话费余额
	 */
	private void setPhonemoney() {
		// TODO Auto-generated method stub
		if (getPhonemoney() != null && !getPhonemoney().equals("")) {
			my_treasure_phone_balance.setText(getPhonemoney());
		} else {
			my_treasure_phone_balance.setText("0");
		}
	}

	/**
	 * 获得可提现金额
	 * 
	 * @return 可提现金额
	 */
	private String getCash() {
		// TODO Auto-generated method stub
		return getAccount().getCash();
	}

	/**
	 * 获得冻结金额
	 * 
	 * @return 冻结金额
	 */
	private String getFreeze() {
		// TODO Auto-generated method stub
		return getAccount().getFreeze();
	}

	/**
	 * 获得返费
	 * 
	 * @return 返回返费的钱
	 */
	private String getBackMoney() {
		// TODO Auto-generated method stub
		return getAccount().getTotal();
	}

	/**
	 * 获得话费
	 * 
	 * @return 话费
	 */
	private String getPhonemoney() {
		return getAccount().getCalling();
	}

	/**
	 * 从登陆信息里面获得account
	 * 
	 * @return account
	 */
	private Account getAccount() {
		return loginMessage.getAccount();
	}

	/***
	 * 为控件绑定事件
	 */
	private void initlistener() {
		mLeft.setOnClickListener(listener);
		mytreasure_imageview_telephonecharge_declare
				.setOnClickListener(listener);
		mytreasure_tv_checkdetails.setOnClickListener(listener);
		my_treasure_tv_backmoney.setOnClickListener(listener);
		my_treasue_btn_sumbit.setOnClickListener(listener);
		mytreasure_tv_returnthemoney_help.setOnClickListener(listener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = null;
			switch (view.getId()) {
			case R.id.left_action:
				MyTreasureActivity.this.finish();
				break;
			// 跳转到话费说明
			case R.id.mytreasure_imageview_telephonecharge_declare:
				intent = new Intent(MyTreasureActivity.this,
						TelephoneExplainActivity.class);
				startActivity(intent);
				break;
			// 跳转到话费详情
			case R.id.mytreasure_tv_checkdetails:
				intent = new Intent(MyTreasureActivity.this,
						TelephoneChargeDetilsActivity.class);
				startActivity(intent);
				break;
			// 跳转到返费详情
			case R.id.my_treasure_tv_backmoney:
				intent = new Intent(MyTreasureActivity.this,
						ReturnTheMoneyDatailsActivity.class);
				startActivity(intent);
				break;
			// 跳转到提现界面
			case R.id.my_treasue_btn_sumbit:
				// intent = new Intent(MyTreasureActivity.this,
				// WithdrawDepositActivity.class);
				// startActivity(intent);
				break;
			// 跳转到返费说明
			case R.id.mytreasure_tv_returnthemoney_help:
				intent = new Intent(MyTreasureActivity.this,
						ReturnFeeThatActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		}
	};

	private void submitLogin() {
		// TODO Auto-generated method stub
		httpBiz = new HttpBiz(this);
		if (isLogined()) {
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			httpBiz.httPostData(RELOGINType, API.LOGIN_MESSAGE_RELOGIN_URL, rp,
					this);
		}
	}

	/**
	 * 返回的json数据
	 */
	@Override
	public void receive(int type, String data) {
		if (type == 400) {
			init();
		} else {
			parseJSON(data);
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
		init();
	}

}
