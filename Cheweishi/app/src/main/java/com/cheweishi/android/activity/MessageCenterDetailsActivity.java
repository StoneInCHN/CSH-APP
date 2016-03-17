package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.MessagCenterInfo;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.utils.ActivityControl;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_message_center_details)
public class MessageCenterDetailsActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.msg_datails_info)
	private TextView msg_datails_info;
	@ViewInject(R.id.detail_tv_type)
	private TextView detail_tv_type;
	@ViewInject(R.id.detail_tv_time)
	private TextView detail_tv_time;

	@ViewInject(R.id.tv_centent_details_body)
	private TextView tv_centent_details_body;
	@ViewInject(R.id.tv_centent_details_title)
	private TextView tv_centent_details_title;
	@ViewInject(R.id.tv_centent_time)
	private TextView tv_centent_time;
	@ViewInject(R.id.tv_centent_details_content)
	private TextView tv_centent_details;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		httpBiz = new HttpBiz(this);
		initData();
		// getMessageDetails();

	}

	private void getMessageData(int id) {
		if (isLogined()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("mobile",loginMessage.getMobile());
			params.addBodyParameter("id", id + "");
			Log.i("result", "==uid==" + loginMessage.getUid() + "_" + loginMessage.getMobile() + "_" + id);
			httpBiz.httPostData(1001, API.CSH_MESSAGE_LIST_URL, params, this);
		}
	}

	private void getMessageDetails() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", "1");
		ProgrosDialog.openDialog(this);
		httpBiz.httPostData(100000, API.CSH_MESSAGE_DETAILS_URL, params, this);
	}

	@Override
	public void receive(int type, String data) {
		super.receive(type, data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 1001:
			// paresJsonData(data);
			parseJsonData(data);
			break;

		default:
			showToast(R.string.FAIL);
			break;
		}
	}

	private void paresJsonData(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast("获取数据失败");
			return;
		}

		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess,
					jsonObject.optString("state"), true)) {
				tv_centent_details_title.setText(jsonObject.optJSONObject(
						"data").optString("title"));
				tv_centent_time.setText(jsonObject.optJSONObject("data")
						.optString("add_time"));
				tv_centent_details.setText(jsonObject.optJSONObject("data")
						.optString("content"));
				tv_centent_details_body.setText(jsonObject
						.optJSONObject("data").optString("body"));
			} else {
				showToast(jsonObject.optString("message"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void parseJsonData(String data) {
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess,
					jsonObject.optString("state"), true)) {
				MessagCenterInfo centerInfo = new MessagCenterInfo();
				JSONObject object = jsonObject.optJSONObject("data");
				int id = object.optInt("id");
				centerInfo.setId(id);
				centerInfo.setAdd_time(object.getString("add_time"));
				centerInfo.setContent(object.getString("content"));
				centerInfo.setUid(object.getInt("uid"));
				centerInfo.setBody(object.getString("body"));
				centerInfo.setIcon(object.getString("icon"));
				centerInfo.setIsRead(1);
				centerInfo.setTitle(object.getString("title"));
				centerInfo.setType(object.getString("type"));
				if (StringUtil.isEmpty(DBTools.getInstance(this).findFirst(MessagCenterInfo.class, "id", id + ""))) {
					DBTools.getInstance(this).save(centerInfo);
				}else {
					DBTools.getInstance(this).deleteById(MessagCenterInfo.class, id + "");
					DBTools.getInstance(this).save(centerInfo);
				}
				tv_centent_details_title.setText(centerInfo.getTitle());
				tv_centent_time.setText(centerInfo.getAdd_time());
				tv_centent_details.setText(centerInfo.getContent());
			} else {
				showToast(jsonObject.optString("message"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initData() {
		this.title.setText(R.string.msg_details);
		this.left_action.setText(R.string.back);
		int id = getIntent().getIntExtra("id", 0);
		
		MessagCenterInfo centerInfo;
		
		centerInfo = (MessagCenterInfo) DBTools.getInstance(this).findFirst(
				MessagCenterInfo.class, "id", id + "");
		if (StringUtil.isEmpty(centerInfo)) {
			getMessageData(id);
		}else {
			tv_centent_details_title.setText(centerInfo.getTitle());
			tv_centent_time.setText(centerInfo.getAdd_time());
			tv_centent_details.setText(centerInfo.getContent());
		}


		// MessagCenterInfo mMessagCenterInfo = (MessagCenterInfo) getIntent()
		// .getExtras().getSerializable("list");
		// this.setDataBack(mMessagCenterInfo);
		// String type = mMessagCenterInfo.getType();
		// detail_tv_type.setText(type);
		// if("1".equals(type)){
		// detail_tv_type.setText("非法启动");
		// }
		// else if("2".equals(type)){
		// detail_tv_type.setText("非法振动");
		// }
		// else if("3".equals(type)){
		// detail_tv_type.setText("obd故障报警");
		// }
		// else if("4".equals(type)){
		// detail_tv_type.setText("水温异常报警");
		// }
		// else if("5".equals(type)){
		// detail_tv_type.setText("超速报警");
		// }
		// else if("6".equals(type)){
		// detail_tv_type.setText("碰撞告警");
		// }
		// else if("7".equals(type)){
		// detail_tv_type.setText("侧翻告警");
		// }
		// else if("8".equals(type)){
		// detail_tv_type.setText("车辆启动");
		// }
		// else if("9".equals(type)){
		// detail_tv_type.setText("车辆熄火");
		// }
		// else if("10".equals(type)){
		// detail_tv_type.setText("解绑通知");
		// }
		// else if("11".equals(type)){
		// detail_tv_type.setText("保养通知");
		// }
		// this.msg_datails_info.setText(mMessagCenterInfo.getInfo());
		// this.detail_tv_time.setText(mMessagCenterInfo.getTime().substring(10,
		// mMessagCenterInfo.getTime().length()));
	}

	@OnClick({ R.id.left_action })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			this.finish();
			break;
		}
	}

	/**
	 * 设置回调
	 * 
	 * @param mMessagCenterInfo
	 */
	private void setDataBack(MessagCenterInfo mMessagCenterInfo) {
		// 数据是使用Intent返回
		Intent intent = new Intent();
		int position = getIntent().getExtras().getInt("isread");
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		/** 消息已读未读处理 0 未读 1已读 **/
		int isRead = mMessagCenterInfo.getIsRead();
		if (isRead == 0) {
			// 把返回数据存入Intent
			bundle.putString("result", "0");
		} else if (isRead == 1) {
			// 把返回数据存入Intent
			bundle.putString("result", "0");
		}
		intent.putExtras(bundle);
		// 设置返回数据
		MessageCenterDetailsActivity.this.setResult(RESULT_OK, intent);
	}
}
