package com.cheweishi.android.activity;

import java.lang.reflect.Type;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.http.RequestParams;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.SosAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.SOS;
import com.cheweishi.android.entity.SortModel;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.utils.Utility;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.SwipeListView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * SOS设置
 * 
 * @author mingdasen
 * 
 */
public class SetSOSActivity extends BaseActivity {

	@ViewInject(R.id.listView_sos)
	private SwipeListView listView_sos;
	private SosAdapter adapter;
	private List<SOS> listSOS;
	private final int listType = 10001;
	private final int deleteType = 10002;
	private final int statusType = 10003;
	private final int chooseType = 10004;
	private final int addType = 10005;
	@ViewInject(R.id.ll_sos_contact_add)
	private LinearLayout ll_sos_contact_add;
	@ViewInject(R.id.ll_sos_crashNotification)
	private LinearLayout ll_sos_crashNotification;
	@ViewInject(R.id.ll_crash_notification)
	private LinearLayout ll_crash_notification;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private TextView left_action;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.tb_crash_notification)
	private ToggleButton tb_crash_notification;
	@ViewInject(R.id.tv_crash_notification)
	private TextView tv_crash_notification;
	private boolean checkFlag = false;
	private int crashLevel = 0;
	private String status;
	private int listIndex = 0;
	private CustomDialog.Builder builder;
	private CustomDialog phoneDialog;
	private String addOrEdit = "Add";
	private String sid = "";
	@ViewInject(R.id.tv_sos_contact_remind)
	private TextView tv_sos_contact_remind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_sos);
		ViewUtils.inject(this);
		
		initViews();

	}

	private void initViews() {
		title.setText(R.string.SOS_setting);
		left_action.setText(R.string.back);
		httpBiz = new HttpBiz(this);
		adapter = new SosAdapter(this, listSOS,
				listView_sos.getRightViewWidth());
		listView_sos.setAdapter(adapter);
		listView_sos.setOnItemClickListener(onItemClickListener);
		listView_sos.setOverScrollMode(View.OVER_SCROLL_NEVER);
		Utility.setListViewHeightBasedOnChildren(listView_sos);
		adapter.setOnRightItemClickListener(onRightItemClickListener);
		setListeners();
		connectToServerList();
	}

	private void setListeners() {
		ll_sos_contact_add.setOnClickListener(onClickListener);
		ll_sos_crashNotification.setOnClickListener(onClickListener);
		left_action.setOnClickListener(onClickListener);
		tb_crash_notification
				.setOnCheckedChangeListener(onCheckedChangeListener);
	}

	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if (checkFlag == true) {
				checkFlag = false;
			} else {
				if (arg1 == true) {
					connectToToggleButtonServer("5", "1");
				} else {

					connectToToggleButtonServer("5", "0");
				}

			}
		}

	};

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			addOrEdit = "Edit";
			Intent intent = new Intent(SetSOSActivity.this,
					SOSContactActivity.class);
			intent.putExtra("name", ((SOS) (adapter.getItem(arg2))).getName());
			intent.putExtra("tel", ((SOS) (adapter.getItem(arg2))).getTel());
			sid = ((SOS) (adapter.getItem(arg2))).getId();
			startActivityForResult(intent, 1001);
		}

	};

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.left_action:
				finish();
				break;
			case R.id.ll_sos_contact_add:
				addOrEdit = "Add";
				Intent intent = new Intent(SetSOSActivity.this,
						SOSContactActivity.class);
				startActivityForResult(intent, 1001);
				break;
			case R.id.ll_sos_crashNotification:
				intent = new Intent(SetSOSActivity.this,
						SOSCrashLevelActivity.class);
				intent.putExtra("crashLevel", crashLevel);
				startActivityForResult(intent, 1002);
				break;
			}
		}
	};

	private SosAdapter.onRightItemClickListener onRightItemClickListener = new SosAdapter.onRightItemClickListener() {

		@Override
		public void onRightItemClick(View v, int position) {
			showPhoneDialog(((SOS) adapter.getItem(position)).getId());

		}

	};

	/**
	 * 是否通知以及碰撞等级
	 * 
	 * @param type
	 * @param status
	 */
	private void connectToToggleButtonServer(String type, String status) {
		if (isLogined()) {
			ProgrosDialog.openDialog(this);
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			rp.addBodyParameter("type", type);
			rp.addBodyParameter("status", status);
			httpBiz.httPostData(statusType, API.SOS_STATUS_URL, rp,
					SetSOSActivity.this);
		}

	}

	/**
	 * 添加联系人
	 * 
	 * @param name
	 * @param tel
	 */
	private void connectToAddContact(String name, String tel) {
		// showToast("haha");

		if (isLogined()) {
			ProgrosDialog.openDialog(this);
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			rp.addBodyParameter("name", name);
			rp.addBodyParameter("tel", tel);
			if (StringUtil.isEquals(addOrEdit, "Edit", true)) {
				rp.addBodyParameter("sid", sid);
				httpBiz.httPostData(addType, API.SOS_EDIT_URL, rp,
						SetSOSActivity.this);
			} else {
				httpBiz.httPostData(addType, API.SOS_ADD_URL, rp,
						SetSOSActivity.this);
			}

		}
	}

	/**
	 * 获取联系人列表
	 */
	private void connectToServerList() {

		if (isLogined()) {
			ProgrosDialog.openDialog(this);
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			httpBiz.httPostData(listType, API.SOS_LIST_URL, rp,
					SetSOSActivity.this);
		}
	}

	/**
	 * 删除联系人
	 * 
	 * @param id
	 */
	private void connectToServerDelete(String id) {
		if (isLogined()) {
			ProgrosDialog.openDialog(this);
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("uid", loginMessage.getUid());
			rp.addBodyParameter("key", loginMessage.getKey());
			rp.addBodyParameter("sid", id);
			httpBiz.httPostData(deleteType, API.SOS_DELETE_URL, rp,
					SetSOSActivity.this);
		}
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		ProgrosDialog.closeProgrosDialog();
		super.receive(type, data);
		switch (type) {
		case 400:
			break;
		case listType:
			parseListJSON(data);
			break;
		case deleteType:
			parseAddJSON(data);
			break;
		case chooseType:
			break;

		case statusType:
			parseStatusJSON(data);
			break;
		case addType:
			parseAddJSON(data);
			break;

		}
	}

	private void parseStatusJSON(String result) {

		if (!StringUtil.isEmpty(result)) {
			System.out.println("haha" + result);
			try {
				if (StringUtil.isEquals(
						new JSONObject(result).optString("operationState"),
						API.returnSuccess, true)) {
					status = new JSONObject(result).optJSONObject("data")
							.optString("status");
					judgeLevel();
				} else {
					showToast(new JSONObject(result).optJSONObject("data")
							.optString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void parseAddJSON(String result) {

		if (!StringUtil.isEmpty(result)) {
			System.out.println("haha" + result);
			try {
				if (StringUtil.isEquals(
						new JSONObject(result).optString("operationState"),
						API.returnSuccess, true)) {
					listSOS.clear();
					connectToServerList();
				} else {
					showToast(new JSONObject(result).optJSONObject("data")
							.optString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parseListJSON(String result) {

		if (!StringUtil.isEmpty(result)) {

			System.out.println("联系人列表==========" + result);
			Gson gson = new Gson();
			Type type = new TypeToken<List<SOS>>() {
			}.getType();
			try {
				if (StringUtil.isEquals(
						new JSONObject(result).optString("operationState"),
						API.returnSuccess, true)) {

					listSOS = (List<SOS>) gson.fromJson(new JSONObject(result)
							.optJSONObject("data").optString("list"), type);
					adapter.setData(listSOS);
					Utility.setListViewHeightBasedOnChildren(listView_sos);
					status = new JSONObject(result).optJSONObject("data")
							.optString("status");
					judgeLevel();

					if (listSOS != null && listSOS.size() >= 3) {
						ll_sos_contact_add.setVisibility(View.GONE);
					} else {
						ll_sos_contact_add.setVisibility(View.VISIBLE);
					}
				} else {
					showToast(new JSONObject(result).optJSONObject("data")
							.optString("msg"));
				}
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据状态设置togglebutton的状态和碰撞等级的显示
	 */
	private void judgeLevel() {

		if (status.charAt(5) == '0') {
			if (listIndex == 0) {
				checkFlag = false;
			}
			tb_crash_notification.setChecked(false);
			ll_crash_notification.setVisibility(View.GONE);
			tv_sos_contact_remind.setVisibility(View.VISIBLE);
		} else {
			if (listIndex == 0) {
				checkFlag = true;
			}
			tb_crash_notification.setChecked(true);
			ll_crash_notification.setVisibility(View.VISIBLE);
			tv_sos_contact_remind.setVisibility(View.GONE);
		}
		listIndex++;
		if (status.charAt(4) == '1') {
			crashLevel = 1;
			tv_crash_notification.setText(R.string.crash_level_oneCrash);
		} else if (status.charAt(4) == '2') {
			crashLevel = 2;
			tv_crash_notification.setText(R.string.crash_level_twoCrash);
		} else {
			crashLevel = 3;
			tv_crash_notification.setText(R.string.crash_level_threeCrash);
		}
	}

	// /**
	// *
	// * @Title: showDialog
	// * @Description: TODO(dialog弹出和显示的样式)
	// * @param 设定文件
	// * @return void 返回类型
	// * @throws
	// */
	// private void showImgDialog() {
	// View view = getLayoutInflater().inflate(R.layout.person_seting_dialog,
	// null);
	// dialog1 = new Dialog(this, R.style.transparentFrameWindowStyle);
	//
	// dialog1.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
	// LayoutParams.WRAP_CONTENT));
	// origionImgBtn = (Button) view.findViewById(R.id.origionImgBtn);
	// camera = (Button) view.findViewById(R.id.xiangji);
	// camera.setVisibility(View.GONE);
	// cancel = (Button) view.findViewById(R.id.quxiao);
	// photo = (Button) view.findViewById(R.id.xiangce);
	// photo.setText(R.string.sos_contact_add);
	// // photo.setVisibility(View.GONE);
	// origionImgBtn.setText(R.string.select_from_tel_book);
	// origionImgBtn.setOnClickListener(myListener);
	// camera.setOnClickListener(myListener);
	// cancel.setOnClickListener(myListener);
	// photo.setOnClickListener(myListener);
	//
	// Window window = dialog1.getWindow();
	// // 设置显示动画
	// window.setWindowAnimations(R.style.main_menu_animstyle);
	// WindowManager.LayoutParams wl = window.getAttributes();
	// wl.x = 0;
	// wl.y = getWindowManager().getDefaultDisplay().getHeight();
	// // 以下这两句是为了保证按钮可以水平满屏
	// wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
	// wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
	//
	// // 设置显示位置
	// dialog1.onWindowAttributesChanged(wl);
	// // 设置点击外围解散
	// dialog1.setCanceledOnTouchOutside(true);
	// dialog1.show();
	// }
	//
	// OnClickListener myListener = new OnClickListener() {
	//
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.left_action:
	// Intent intent1 = new Intent();
	// setResult(1111, intent1);
	// finish();
	// break;
	// case R.id.origionImgBtn:
	// Constant.SettingOrPhone = "Setting";
	// dialog1.dismiss();
	// Intent intent = new Intent(SetSOSActivity.this,
	// ContactActivity.class);
	// startActivityForResult(intent, 1000);
	// break;
	// case R.id.xiangji:
	//
	// dialog1.dismiss();
	// break;
	// // 调用手机相册
	// case R.id.xiangce:
	// dialog1.dismiss();
	// intent = new Intent(SetSOSActivity.this,
	// SOSContactActivity.class);
	// startActivityForResult(intent, 1001);
	// break;
	// case R.id.quxiao:
	// dialog1.dismiss();
	// break;
	// }
	//
	// }
	// };

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1000:
			if (data != null && data.getExtras() != null) {
				SortModel sm = (SortModel) data.getExtras().getSerializable(
						"sm");
				connectToAddContact(sm.getName(), sm.getPhone());
			}

			break;
		case 1001:
			if (data != null && data.getStringExtra("name") != null
					&& data.getStringExtra("tel") != null) {
				connectToAddContact(data.getStringExtra("name"),
						data.getStringExtra("tel"));
			}
			break;
		case 1002:
			if (data != null) {
				int level = data.getIntExtra("crashLevel", 0);
				connectToToggleButtonServer("4", level + "");
			}
			break;
		}
	};

	/**
	 * 删除对话框
	 */
	private void showPhoneDialog(final String id) {
		builder = new CustomDialog.Builder(this);
		builder.setMessage(R.string.report_insure_delete);
		builder.setTitle(R.string.remind);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						listView_sos.hideView();
						connectToServerDelete(id);
					}
				});

		builder.setNegativeButton(R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		phoneDialog = builder.create();
		phoneDialog.show();
	}

}
