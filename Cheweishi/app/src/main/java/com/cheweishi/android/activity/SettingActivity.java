package com.cheweishi.android.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.SharePreferenceTools;
import com.cheweishi.android.utils.ActivityControl;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/***
 * 设置
 * 
 * @author mingdasen
 * 
 */
public class SettingActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.bt_report_setting)
	private Button bt_report_setting;

	@ViewInject(R.id.bt_sos_setting)
	private Button bt_sos_setting;

	@ViewInject(R.id.bt_signDetail_setting)
	private Button bt_signDetail_setting;// 签到详情

	@ViewInject(R.id.bt_message_push)
	private Button bt_message_push;// 推送设置

	@ViewInject(R.id.bt_aboutUs_setting)
	private Button bt_aboutUs_setting;// 关于我们

	@ViewInject(R.id.bt_logout_setting)
	private Button bt_logout_setting;// 退出、登陆
	private CustomDialog.Builder builder;
	private LoginMessage loginMessage;

	@ViewInject(R.id.title)
	private TextView title;

	@ViewInject(R.id.left_action)
	private Button left_action;

	private Intent intent;
	private MyBroadcastReceiver broad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ViewUtils.inject(this);
		initViews();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (broad == null) {
			broad = new MyBroadcastReceiver();
			IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
			registerReceiver(broad, intentFilter);
		}
	}

	/**
	 * 初始化布局
	 */
	private void initViews() {
//		loginMessage = LoginMessageUtils.getLoginMessage(this);
		title.setText(R.string.set);
		left_action.setText(R.string.back);
		if (isLogined()) {
			bt_logout_setting.setText(R.string.logout);
		} else {
			bt_logout_setting.setText(R.string.login);
		}
	}

	/**
	 * 点击事件
	 */
	@OnClick({ R.id.bt_logout_setting, R.id.bt_signDetail_setting,
			R.id.bt_aboutUs_setting, R.id.bt_message_push, R.id.left_action,
			R.id.bt_report_setting, R.id.bt_sos_setting })
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bt_logout_setting:// 退出、登陆按钮
			logoutSet();
			break;
		case R.id.bt_signDetail_setting:// 签到详情按钮
			signDetailSet();
			break;
		case R.id.bt_aboutUs_setting:// 关于我们
			intent = new Intent(SettingActivity.this, AboutUsActivity.class);
			startActivity(intent);
			break;
		case R.id.bt_message_push:// 推送设置
			setPush();
			break;
		case R.id.left_action:// 返回
			SettingActivity.this.finish();
			break;

		case R.id.bt_report_setting:// 报警设置\
			reportSet();
			break;
		case R.id.bt_sos_setting:// SOS设置
			setSOS();
			break;
		}
	}

	private void setPush() {
		if (!isLogined()) {
			intent = new Intent(SettingActivity.this, LoginActivity.class);
			startActivity(intent);
			SettingActivity.this.overridePendingTransition(
					R.anim.score_business_query_enter,
					R.anim.score_business_query_exit);
		} else {
			intent = new Intent(SettingActivity.this, PushDetailsActivity.class);
			startActivity(intent);
		}
	}

	private void setSOS() {
		if (!isLogined()) {
			intent = new Intent(SettingActivity.this, LoginActivity.class);
			startActivity(intent);
			SettingActivity.this.overridePendingTransition(
					R.anim.score_business_query_enter,
					R.anim.score_business_query_exit);
		} else {
			intent = new Intent(SettingActivity.this, SetSOSActivity.class);
			startActivity(intent);
		}
	}

	private void reportSet() {
		if (!isLogined()) {
			intent = new Intent(SettingActivity.this, LoginActivity.class);
			startActivity(intent);
			SettingActivity.this.overridePendingTransition(
					R.anim.score_business_query_enter,
					R.anim.score_business_query_exit);
		} else {
			intent = new Intent(SettingActivity.this, SetReportActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 签到详情按钮点击操作
	 */
	private void signDetailSet() {

		if (!isLogined()) {
			intent = new Intent(SettingActivity.this, LoginActivity.class);
			startActivity(intent);
			SettingActivity.this.overridePendingTransition(
					R.anim.score_business_query_enter,
					R.anim.score_business_query_exit);
		} else {
			intent = new Intent(SettingActivity.this,
					SignInDetailActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 退出、登录按钮设置、操作
	 */
	private void logoutSet() {
		if (!isLogined()) {
			intent = new Intent(SettingActivity.this, LoginActivity.class);
			LoginMessageUtils.showDialogFlag = true;
			startActivity(intent);
			SettingActivity.this.overridePendingTransition(
					R.anim.score_business_query_enter,
					R.anim.score_business_query_exit);
		} else {
			LoginMessageUtils.showDialogFlag = true;
			LogoutDialog();
		}
	}

	/**
	 * 环信退出操作
	 */
//	private void HXlogout() {
//		// show网络请求时的dialog
//		ProgrosDialog.openDialog(SettingActivity.this);
//		// 环信退出使用带回调的退出方法
//		if (EMChat.getInstance().isLoggedIn()) {
//
//			EMChatManager.getInstance().logout(new EMCallBack() {
//
//				@Override
//				public void onSuccess() {
//					runOnUiThread(new Runnable() {
//						public void run() {
//							// 只有环信退出成功才能进行自己系统退出操作
//							MainConstant.getInstance(SettingActivity.this)
//									.setPassword(null);
//							Log.i("result", "=====环信退出成功===");
//							logoutConnectToServer();
//						}
//					});
//				}
//
//				@Override
//				public void onProgress(int progress, String status) {
//				}
//
//				@Override
//				public void onError(int code, String message) {
//					// 环信退出失败，关闭dialog、并作出相应提示
//					ProgrosDialog.closeProgrosDialog();
//					Log.i("result", "=====环信退出失败===");
//					// 提示连接失败，请重试
//					// Toast.makeText(SettingActivity.this,
//					// R.string.server_link_fault, Toast.LENGTH_LONG).show();
//					logoutConnectToServer();
//				}
//			});
//		} else {
//			Log.i("result", "=====环信没有登陆===");
//			logoutConnectToServer();
//		}
//	}

	/**
	 * 退出车生活系统
	 */
	private void logoutConnectToServer() {
		// 清楚登陆时保存的登陆密码
		SharePreferenceTools.clearPassFromUser(SettingActivity.this);
		if (!isLogined()) {
			// 退出系统
			Log.i("result", "===没有调退出接口===");
			clearData();
		} else {
			// 请求退出接口
			ProgrosDialog.openDialog(this);
			RequestParams params = new RequestParams();
			params.addBodyParameter("id", loginMessage.getId());
			HttpBiz httpBiz = new HttpBiz(SettingActivity.this);
			Log.i("result", "===调退出接口===");
			httpBiz.httPostData(10008, API.CSH_LOGOUT_URL, params, this);
		}
	}

	/**
	 * 数据返回处理
	 */
	@Override
	public void receive(int type, String data) {
		super.receive(type, data);
		Log.i("result", "===退出数据===" + data);
		clearData();
	}

	/**
	 * 退出提示
	 */
	private void LogoutDialog() {
		builder = new CustomDialog.Builder(this);
		builder.setMessage(R.string.logout_dialog);
		builder.setTitle(R.string.remind);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						logoutConnectToServer();
					}
				});

		builder.setNegativeButton(R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();

	}

	/**
	 * 退出操作后清除数据
	 */
	private void clearData() {
		ProgrosDialog.closeProgrosDialog();
		LoginMessageUtils.setLogined(this, false);
		bt_logout_setting.setText(R.string.login);
		DBTools.getInstance(this).delete(LoginMessage.class);
//		LoginMessageUtils.deleteLoginMessage(SettingActivity.this);
		// if (ProgrosDialog.isProgressShowing()) {
		// ProgrosDialog.closeProgrosDialog();
		// ActivityControl.finishProgrom();
		// }else {
		ActivityControl.finishProgrom();
		// }
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {

			if (!StringUtil.isEquals(intent.getAction(), Constant.REFRESH_FLAG,
					true)) {
				System.out
						.println("SUCCESS====" + "haha0" + intent.getAction());
				return;
			}
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.LOGIN_REFRESH, true)) {
				System.out.println("SUCCESS====" + "haha2");
				initViews();
			}
		}
	}
}
