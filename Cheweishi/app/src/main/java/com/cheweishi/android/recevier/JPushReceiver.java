package com.cheweishi.android.recevier;

import java.io.File;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.activity.LoginActivity;
import com.cheweishi.android.activity.MessageCenterDetailsActivity;
import com.cheweishi.android.activity.MessagerCenterActivity;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.JSONCallback;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.entity.MessagCenterInfo;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.ActivityControl;
import com.cheweishi.android.utils.DateUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;

/**
 * 极光推送自定义接收器 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 * 
 * @author mingdasen
 * 
 */
public class JPushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	private static CustomDialog.Builder builder;
	private static CustomDialog phoneDialog;
	private static int NOTIFICATION_ID;
	private static Context mContext;
	

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		this.mContext = context;
		Log.i("result", "===Jpush=bundle==" + bundle.getString(JPushInterface.EXTRA_EXTRA));
//		Log.d("result", "[JPushReceiver] onReceive - " + intent.getAction()
//				+ ", extras: " + printBundle(bundle));
		
//		processCustomMessage(context, printBundle(bundle));
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[JPushReceiver]接收Registration Id : " + regId);
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			// processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
			// 打开自定义的Activity
			JSONObject json;
			try {
				json = new JSONObject(
						bundle.getString(JPushInterface.EXTRA_EXTRA));
				Intent i = new Intent(context, MessageCenterDetailsActivity.class);
				i.putExtra("id",json.optInt("id"));
				// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.w(TAG, "[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
		
		printBundle(bundle);
	}

	// send msg to MainActivity
	private void processCustomMessage(Context context, String bundle) {

		// if (PushDialogActivity.isForeground) {
		// String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		// String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		// Constant.CURRENT_REFRESH = Constant.JPUSH_REFRESH;
		// Intent msgIntent = new Intent();
		// msgIntent.setAction(Constant.CURRENT_REFRESH);
		// msgIntent.putExtra("bundle", bundle);
		// context.sendBroadcast(msgIntent);
		// Toast.makeText(context, bundle, Toast.LENGTH_LONG).show();
		// if (!ExampleUtil.isEmpty(extras)) {
		// try {
		// JSONObject extraJson = new JSONObject(extras);
		// if (null != extraJson && extraJson.length() > 0) {
		// msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
		// }
		// } catch (JSONException e) {
		Log.i("result", " ===发送推送显示广播== ");
		//
		// }
		// }

		// }
	}

	/**
	 * 打印所有的 intent extra 数据
	 * 
	 * @param bundle
	 * @return
	 */
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(
							bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it = json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" + myKey + " - "
								+ json.optString(myKey) + "]");
						if (StringUtil.isEquals(json.optString("type"), "10",
								true)) {
//							 ReLoginDialog.getInstance(
//							 ActivityControl.getActivity(0)).showDialog(
//							 "您的手机在已另一台手机上登录");
							LoginMessageUtils.setLogined(ActivityControl.getActivity(0), false);
							DBTools.getInstance(ActivityControl.getActivity(0)).delete(LoginMessage.class);
							showPhoneDialog(bundle);
						}else {
//							MessagCenterInfo centerInfo;
							if (NOTIFICATION_ID != bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)) {
								NOTIFICATION_ID = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
								getMessageData(json.optInt("id"));
//								centerInfo = new MessagCenterInfo();
//								centerInfo.setId(json.optInt("id"));
//								DBTools.getInstance(ActivityControl.getActivity(0)).save(centerInfo);
//								if (ActivityControl.getActivity(0).equals(MessagerCenterActivity.class)) {
//									MessagerCenterActivity.instance.initDate();
//								}
							}
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	private static void showPhoneDialog(final Bundle bundle) {
		if (!StringUtil.isEmpty(ActivityControl.getActivity(0))) {
			builder = new CustomDialog.Builder(ActivityControl.getActivity(0));
			builder.setMessage("您的手机在已另一台手机上登录");
			builder.setTitle(R.string.remind);
			builder.setPositiveButton("验证登录",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							JPushInterface.clearNotificationById(ActivityControl.getActivity(0), bundle
									.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
							Intent intent = new Intent(ActivityControl.getActivity(0),
									LoginActivity.class);
							ActivityControl.getActivity(0).startActivity(intent);
						}
					});

			builder.setNegativeButton(R.string.cancel,
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							JPushInterface.clearNotificationById(ActivityControl.getActivity(0), bundle
									.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
							DBTools.getInstance(ActivityControl.getActivity(0)).delete(LoginMessage.class);
							// setRadioButtonLight();
						}
					});
			if (StringUtil.isEmpty(phoneDialog)) {
				phoneDialog = builder.create();
				phoneDialog.show();
			}else if(!phoneDialog.isShowing()){
//				phoneDialog.dismiss();
				
				phoneDialog.show();
			}
		}
	}
	
	private static void getMessageData(int id){
		if (!StringUtil.isEmpty(BaseActivity.loginMessage) && !StringUtil.isEmpty(BaseActivity.loginMessage.getUid())) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", BaseActivity.loginMessage.getUid());
			params.addBodyParameter("mobile", BaseActivity.loginMessage.getMobile());
			params.addBodyParameter("id", id + "");
			Log.i("result", "==uid==" + BaseActivity.loginMessage.getUid() + "_" + BaseActivity.loginMessage.getMobile() + "_" + id);
			HttpBiz httpBiz = new HttpBiz(mContext);
			httpBiz.httPostData(1001, API.CSH_MESSAGE_LIST_URL, params, callback);
		}
	}

	private static JSONCallback callback = new JSONCallback() {
		
		@Override
		public void receive(int type, String data) {
			switch (type) {
			case 1001:
				parseJsonData(data);
				break;
			default:
				break;
			}
		}
		
		@Override
		public void downFile(int type, ResponseInfo<File> arg0) {
		}
	};

	
	protected static void parseJsonData(String data) {
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess, jsonObject.optString("state"),true)) {
				MessagCenterInfo centerInfo = new MessagCenterInfo();
				JSONObject object = jsonObject.optJSONObject("data");
				centerInfo.setId(object.optInt("id"));
				centerInfo.setAdd_time(object.getString("add_time"));
				centerInfo.setContent(object.getString("content"));
				centerInfo.setUid(object.getInt("uid"));
				centerInfo.setBody(object.getString("body"));
				centerInfo.setIcon(object.getString("icon"));
				centerInfo.setTitle(object.getString("title"));
				centerInfo.setIsRead(0);
				centerInfo.setType(object.getString("type"));
				if (StringUtil.isEmpty(DBTools.getInstance(ActivityControl.getActivity(0)).findFirst(MessagCenterInfo.class, "id", object.optInt("id") + ""))) {
					DBTools.getInstance(ActivityControl.getActivity(0)).save(centerInfo);
				}
			}else {
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
