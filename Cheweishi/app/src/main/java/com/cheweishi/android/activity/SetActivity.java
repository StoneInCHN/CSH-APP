package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.SharePreferenceTools;
import com.cheweishi.android.utils.ActivityControl;
import com.cheweishi.android.utils.FileSizeUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * @author zhangQ
 * @date 创建时间：2015-12-3 上午11:07:21
 * @version 1.0
 * @Description:
 */
public class SetActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	@ViewInject(R.id.cb_call_police)
	private CheckBox cb_call_police;// 报警
	// @ViewInject(R.id.cb_sound)
	// private CheckBox cb_sound;// 声音
	// @ViewInject(R.id.cb_shake)
	
	// private CheckBox cb_shake;// 震动
	@ViewInject(R.id.cb_push)
	private CheckBox cb_push;// 极光推送

	@ViewInject(R.id.ll_good_reputation)//五星好评
	private LinearLayout ll_good_reputation;
	@ViewInject(R.id.ll_about_my)
	private LinearLayout ll_about_my;//关于我们
	@ViewInject(R.id.ll_clear_cache)
	private LinearLayout ll_clear_cache;

	@ViewInject(R.id.tv_version)
	private TextView tv_version;
	@ViewInject(R.id.btn_setout)
	private Button btn_setout;
	@ViewInject(R.id.left_action)
	private Button left_action;

	@ViewInject(R.id.title)
	private TextView title;

	private Intent intent;
	private CustomDialog.Builder builder;
	private MyBroadcastReceiver broad;
	private String status = "";
	private int type = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		ViewUtils.inject(this);
		initViews();
	}

	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 1006:
			parseMessageJSON(data);
			break;
		case 10008:
			pareLogoutData(data);
			break;
		case 100:
			pareJSONGet(data);
			Log.i("result", "=====设置111111===");
			break;
		case 101:
			// pareJSONSet(data);
		default:
			break;
		}
	}

	/**
	 * 退出操作处理
	 * 
	 * @param data
	 */
	private void pareLogoutData(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(getString(R.string.FAIL));
			return;
		}
		System.out.println("退出======" + data);
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess,
					jsonObject.optString("state"), true)) {
				showToast(jsonObject.optString("message"));
				clearData();
			} else {
				showToast(jsonObject.optString("message"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

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
		title.setText(R.string.set);
		left_action.setText(R.string.back);
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			// 当前版本
			tv_version.setText(pi.versionName);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		if (isLogined()) {
			btn_setout.setText(R.string.logout);
		} else {
			btn_setout.setText(R.string.login);
		}

		// if (isLogined()) {
		// if (loginMessage.getIsPush() == 0) {
		// cb_push.setChecked(false);
		// } else {
		// cb_push.setChecked(true);
		// }
		// }
		// // 判断用户是否登陆 ，没有登陆就不能选择推送
		// if (isLogined()) {
		// cb_push.setOnCheckedChangeListener(listener);
		// } else {
		// cb_push.setEnabled(false);
		// }
		// getReportSetInfo();
	}

	/**
	 * 获取警告设置信息
	 */
	private void getReportSetInfo() {
		Log.i("resultto", "=====设置设置设置设置===");
		if (isLogined()) {
			ProgrosDialog.openDialog(SetActivity.this);
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			httpBiz = new HttpBiz(SetActivity.this);
			httpBiz.httPostData(100, API.SOS_LIST_URL, rp, this);
		} else {
			startActivity(new Intent(SetActivity.this, LoginActivity.class));
		}
	}

	OnCheckedChangeListener listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if (arg1) {
				messageConnectToServer("1");
				loginMessage.setIsPush(1);
			} else {
				messageConnectToServer("0");
				loginMessage.setIsPush(0);
			}
			LoginMessageUtils.saveProduct(loginMessage, SetActivity.this);

		}
	};

	// 通知后台更改推送状态
	private void messageConnectToServer(String type) {
		if (!isLogined()) {
			Intent intent = new Intent(SetActivity.this, LoginActivity.class);
			this.startActivity(intent);
			this.finish();
		} else {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("key", loginMessage.getKey());
			params.addBodyParameter("type", type);
			httpBiz = new HttpBiz(SetActivity.this);
			httpBiz.httPostData(1006, API.ACCEPT_MESSAGE, params, this);
		}
	}

	/**
	 * 设置checkbox状态
	 * 
	 * @param type
	 * @param status
	 */
	// private void setCheckBox(int type, String status) {
	// switch (type) {
	// case 0:
	// if (status.equals("0")) {
	// cb_call_police.setChecked(false);
	// } else {
	// cb_call_police.setChecked(true);
	// }
	// break;
	// case 1:
	// if (status.equals("0")) {
	// cb_sound.setChecked(false);
	// } else {
	// cb_sound.setChecked(true);
	// }
	// break;
	// case 2:
	// if (status.equals("0")) {
	// cb_shake.setChecked(false);
	// } else {
	// cb_shake.setChecked(true);
	// }
	// break;
	// case 3:
	// if (status.equals("0")) {
	// cb_push.setChecked(false);
	// } else {
	// cb_push.setChecked(true);
	// }
	// break;
	//
	// default:
	// break;
	// }
	// }

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
					DialogTool.getInstance(SetActivity.this)
							.showConflictDialog();
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

	private void setPushFail() {
		if (cb_push.isChecked()) {
			cb_push.setChecked(false);
		} else {
			cb_push.setChecked(true);
		}
	}

	@OnClick({ R.id.ll_about_my, R.id.btn_setout, R.id.left_action,
			R.id.ll_clear_cache, R.id.ll_good_reputation })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_about_my:// 关于我们
			intent = new Intent(SetActivity.this, AboutUsNewActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_setout:// 退出登录
			logoutSet();
			break;
		case R.id.left_action:
			finish();
			break;
		case R.id.ll_clear_cache:// 清除缓存
			clearPhoneCache();
			break;
		case R.id.ll_good_reputation:// 五星好评
			// Intent startintent = new Intent("android.intent.action.MAIN");
			// startintent.addCategory("android.intent.category.APP_MARKET");
			// startintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// startActivity(startintent);
			try {
				String str = "market://details?id=" + getPackageName();
				Intent localIntent = new Intent("android.intent.action.VIEW");
				localIntent.setData(Uri.parse(str));
				startActivity(localIntent);
			} catch (Exception e) {
				showToast("请安装浏览器");
			}

			break;

		default:
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.cb_call_police:
			if (isChecked) {
				// 报警设置

			} else {

			}
			break;
		// case R.id.cb_sound:
		// if (isChecked) {
		// // 声音提醒
		// } else {
		//
		// }
		//
		// break;
		// case R.id.cb_shake:
		// // 震动
		// if (isChecked) {
		// status = "1";
		// } else {
		// status = "0";
		// }
		// type = 0;
		// setReportStatus(type, status);
		// break;
		case R.id.cb_push:
			// 推送
			// if (isChecked) {
			// messageConnectToServer("1");
			// loginMessage.setIsPush(1);
			// } else {
			// messageConnectToServer("0");
			// loginMessage.setIsPush(0);
			// }
			LoginMessageUtils.saveProduct(loginMessage, SetActivity.this);

			break;

		default:
			break;
		}

	}

	private void logoutSet() {
		if (!isLogined()) {
			intent = new Intent(SetActivity.this, LoginActivity.class);
			LoginMessageUtils.showDialogFlag = true;
			startActivity(intent);
			SetActivity.this.overridePendingTransition(
					R.anim.score_business_query_enter,
					R.anim.score_business_query_exit);
		} else {
			LoginMessageUtils.showDialogFlag = true;
			LogoutDialog();
		}
	}

	private void LogoutDialog() {
		builder = new CustomDialog.Builder(this);
		builder.setMessage(R.string.logout_dialog);
		builder.setTitle(R.string.remind);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// HXlogout();
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

//	private void HXlogout() {
//		// show网络请求时的dialog
//		ProgrosDialog.openDialog(SetActivity.this);
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
//							MainConstant.getInstance(SetActivity.this)
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

	private void logoutConnectToServer() {
		// 清楚登陆时保存的登陆密码
		SharePreferenceTools.clearPassFromUser(SetActivity.this);
		if (!isLogined()) {
			// 退出系统
			clearData();
		} else {
			// 请求退出接口
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			HttpBiz httpBiz = new HttpBiz(SetActivity.this);
			ProgrosDialog.openDialog(this);
			httpBiz.httPostData(10008, API.CSH_LOGOUT_URL, params, this);
		}
	}

	/**
	 * 退出操作后清除数据
	 */
	private void clearData() {
		LoginMessageUtils.setLogined(this, false);
		btn_setout.setText(R.string.login);
		// LoginMessageUtils.deleteLoginMessage(SetActivity.this);
		DBTools.getInstance(this).delete(LoginMessage.class);
		// Log.i("result", "===uid==" + loginMessage.getUid() + "_"
		// +loginMessage.getMobile());
		ActivityControl.finishProgrom();
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			Log.i("result", "=====ooooo清除成功===");
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

	private void setReportStatus(int type, String status) {

		if (isLogined()) {
			ProgrosDialog.openDialog(SetActivity.this);
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			rp.addBodyParameter("type", type + "");
			rp.addBodyParameter("status", status);
			httpBiz = new HttpBiz(SetActivity.this);
			httpBiz.httPostData(101, API.SOS_STATUS_URL, rp, this);
		} else {
			startActivity(new Intent(SetActivity.this, LoginActivity.class));
		}
	}

	// private void pareJSONSet(String data) {
	// if (data == null) {
	// showToast(R.string.get_failed_please_check);
	// setStatucFail();
	// } else {
	// try {
	// JSONObject jsonObject = new JSONObject(data);
	// String status = jsonObject.optString("operationState");
	// if (StringUtil.isEquals("SUCCESS", status, true)) {
	// setStatucSuccess();
	// } else if (StringUtil.isEquals("FAIL", status, true)) {
	// setStatucFail();
	// showToast(jsonObject.optJSONObject("data").optString("msg"));
	// } else if (StringUtil.isEquals("RELOGIN", status, true)) {
	// DialogTool.getInstance(SetActivity.this)
	// .showConflictDialog();
	// } else if (StringUtil.isEquals("DEFAULT", status, true)) {
	// setStatucFail();
	// showToast(jsonObject.optJSONObject("data").optString("msg"));
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }

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
					// initCheckBox(status);
				} else if (StringUtil.isEquals("FAIL", statu, true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (StringUtil.isEquals("RELOGIN", statu, true)) {
					DialogTool.getInstance(SetActivity.this)
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
	// private void initCheckBox(String status) {
	// if (status.substring(0, 1).equals("0")) {
	// cb_call_police.setChecked(false);
	// } else {
	// cb_call_police.setChecked(true);
	// }
	// if (status.substring(1, 2).equals("0")) {
	// cb_sound.setChecked(false);
	// } else {
	// cb_sound.setChecked(true);
	// }
	// if (status.substring(2, 3).equals("0")) {
	// cb_shake.setChecked(false);
	// } else {
	// cb_shake.setChecked(true);
	// }
	// if (status.substring(3, 4).equals("0")) {
	// cb_push.setChecked(false);
	// } else {
	// cb_push.setChecked(true);
	// }
	//
	// }
	/**
	 * 清除缓存
	 * 
	 * @param status
	 */
	private void clearPhoneCache() {

		Double d = FileSizeUtils.getFileOrFilesSize(Constant.BASE_IMG_CATCH_DIR
				+ "cws", 3);

		showToast("清除缓存" + d + "M");
		XUtilsImageLoader.clearCache();
	}

}
