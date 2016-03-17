package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.http.MyHttpUtils;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 推送提示
 * 
 * @author mingdasen
 * 
 */
public class PushDialogActivity extends BaseActivity {
	private int msgType;
	private int type;// 界面表识
	private int builder_id;// 消息标识
	private Context context;
	private String title;
	private String carId = "";
	// private String device = "";
	// private String cid = "";
	private String msgContent;
	@ViewInject(R.id.title)
	private TextView tv_title;
	@ViewInject(R.id.alert_message)
	private TextView alertMessage;
	@ViewInject(R.id.btn_left)
	private Button letfButton;
	@ViewInject(R.id.btn_cancel)
	private Button rigthbutton;
	private NotificationManager mNotificationManager;
	private boolean isOK = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("result", "PushDialogActivity");
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		setContentView(R.layout.alert_dialog);
		ViewUtils.inject(PushDialogActivity.this);
		context = PushDialogActivity.this;
		msgType = getIntent().getIntExtra("msgType", -1);
		builder_id = getIntent().getIntExtra("builder_id", 0);
		type = getIntent().getIntExtra("type", -1);
		title = getIntent().getStringExtra("title");
		msgContent = getIntent().getStringExtra("msgContent");
		carId = getIntent().getStringExtra("carId");
		tv_title.setText(title);
		alertMessage.setText(msgContent);
		letfButton.setText(R.string.click_see);
		rigthbutton.setText(R.string.got_it);
		Log.i("result", "PushDialogActivity");
	}

	public void ok(View view) {
		if (msgType == 1) {
			if (type == 1) {
			} else {
				Intent intent = new Intent(context, CarDynamicActivity.class);
				intent.putExtra("carId", carId);
				startActivity(intent);
				mNotificationManager.cancel(builder_id);
				finish();
			}
		} else if (msgType == 2) {
			isOK = true;
			submitLogin();
		} else if (msgType == 3) {
			// if (type == 1) {
			// KeyDetectionActivity.instance.reconnect();
			// } else if (type == 3) {
			// ActivityControl.finishActivity(ActivityControl.getCount() - 2);
			// KeyDetectionActivity.instance.reconnect();
			// } else {
			Intent intent = new Intent(context, MainNewActivity.class);
			// intent.putExtra("device", device);
			// intent.putExtra("cid", cid);
			startActivity(intent);
			// }
			mNotificationManager.cancel(builder_id);
			finish();
		}
	}

	public void cancel(View view) {
		if (msgType == 2) {
			isOK = false;
			submitLogin();
		} else {
			mNotificationManager.cancel(builder_id);
			finish();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	private void submitLogin() {
		// TODO Auto-generated method stub
		LoginMessage loginMessage = LoginMessageUtils.getLoginMessage(context);
		if (loginMessage != null && loginMessage.getUid() != null) {
			// String phoneSystem = android.os.Build.VERSION.RELEASE;
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			MyHttpUtils myHttpUtils = new MyHttpUtils(this, rp,
					API.LOGIN_MESSAGE_RELOGIN_URL, handler);
			myHttpUtils.PostHttpUtils();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 400) {
			} else {
				String str = (String) msg.obj;
				if (str == null) {
				} else {
					System.out.println(str);
					try {
						JSONObject jsonObject = new JSONObject(str);
						if (jsonObject.optString("operationState").equals(
								"SUCCESS")) {
							// System.out.println("重新登陆==" +
							// msg.obj.toString());

							save(jsonObject);
						} else {
							showToast(jsonObject.optJSONObject("data")
									.optString("msg"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			if (isOK) {
				if (type == 1) {
					CarManagerActivity.instance.reconnect();
				} else {
					Intent intent = new Intent(context,
							CarManagerActivity.class);
					startActivity(intent);
				}
				mNotificationManager.cancel(builder_id);
				finish();
			} else {
				mNotificationManager.cancel(builder_id);
				finish();
			}
		};
	};

	protected void save(JSONObject jsonObject) {
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<LoginMessage>() {
		}.getType();
		LoginMessage loginMessage = gson.fromJson(jsonObject.optString("data"),
				type);
		LoginMessageUtils.saveProduct(loginMessage, this);
	}
}
