package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.MessageCenterApdater;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.MessageCenterDialog;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.MessagCenterInfo;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.utils.ACache;
import com.cheweishi.android.utils.ActivityControl;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.SwipeMenu;
import com.cheweishi.android.widget.SwipeMenuCreator;
import com.cheweishi.android.widget.SwipeMenuItem;
import com.cheweishi.android.widget.SwipeMenuListView;
import com.cheweishi.android.widget.SwipeMenuListView.OnMenuItemClickListener;
import com.cheweishi.android.widget.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 消息中心
 * 
 * @author 胡健
 * @time 2015.6.1
 */
@SuppressLint({ "HandlerLeak", "ResourceAsColor" })
@ContentView(R.layout.activity_message_center)
public class MessagerCenterActivity extends BaseActivity {
	@ViewInject(R.id.title)
	private TextView title;

	@ViewInject(R.id.left_action)
	private Button left_action;

	@ViewInject(R.id.right_action)
	private TextView right_action;

	@ViewInject(R.id.messagerCenterListView)
	private SwipeMenuListView messagerCenterListView;

	// 总数量大小
	private List<MessagCenterInfo> textlist;
	private List<MessagCenterInfo> pagelist;
	private List<MessagCenterInfo> httpList;// 接口获取数据

	// 编辑状态下底部布局
	@ViewInject(R.id.msg_linbottom)
	private LinearLayout msg_linbottom;

	@ViewInject(R.id.msg_tv_allread)
	private TextView msg_tv_allread;

	@ViewInject(R.id.msg_tv_allclear)
	private TextView msg_tv_allclear;
	// 删除的大小
	private int deletaSize = 0;
	// 记录清除所有的大小
	private int clearAllize = 0;
	// checkBox删除的大小
	private int checkBoxDesiz = 0;

	// public static MessagerCenterActivity instance;
	private int pagei = 0;
	// 0正常状态，1编辑状态
	private int ISSHOW = 0;
	private MessageCenterApdater mMessageCenterApdater;
	// 选中的大小
	// private int deletaSeleterSize = 0;

	/** checkbox不等于0时 点击完成删除ck **/
	// private boolean isOver = false;

	// 展开
	private Animation animationOpen;
	// 关闭
	private Animation animationClose;
	// 缓存
	// private ACache mACache;
	// private LoginMessage loginMessage;

	private boolean isleftDelate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// mACache = ACache.get(this);
		// instance = this;
		initLintener();
		this.setRightEnbleTrue();
		initDate();

	}

	/** 初始化监听 **/
	private void initLintener() {
		this.messagerCenterListView.setXListViewListener(ixlistener);
		this.messagerCenterListView.setOnItemClickListener(onItemClickListener);
	}

	/** 初始化数据 **/
	public void initDate() {
		this.left_action.setText(R.string.back);
		this.title.setText(R.string.msg_center);
		Drawable drawable = getResources().getDrawable(
				R.drawable.message_icon2x);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		right_action.setCompoundDrawables(null, null, drawable, null); //

		// ininGetHttpData();

		httpList = new ArrayList<MessagCenterInfo>();
		httpList = (List<MessagCenterInfo>) DBTools.getInstance(this).findAll(
				MessagCenterInfo.class);
		JPushInterface.clearAllNotifications(this);
		if (!StringUtil.isEmpty(httpList) && httpList.size() > 0) {
			Collections.reverse(httpList);
			messagerCenterListView.setPullLoadEnable(false);
			messagerCenterListView.setPullRefreshEnable(false);
			messagerCenterListView.setCompileRefresh(true);
			mMessageCenterApdater = new MessageCenterApdater(httpList, this);
			messagerCenterListView.setAdapter(mMessageCenterApdater);
			addDelete();
			messagerCenterListView
					.setOnMenuItemClickListener(new myOnMenuItemClickListener());
			initAnimation();
		} else {
			// messagerCenterListView.setVisibility(View.GONE);
			// nodata.setVisibility(View.VISIBLE);
			right_action.setVisibility(View.GONE);
			EmptyTools.setEmptyView(this, messagerCenterListView);
			EmptyTools.setImg(R.drawable.message_message);
			EmptyTools.setMessage("您还没有相关消息");
		}
	}

	/**
	 * 设置右边字体不可以点击
	 */
	public void setRightEnble() {
		this.right_action.setVisibility(View.INVISIBLE);
		// this.right_action.setEnabled(false);
		// this.right_action.setTextColor(getResources().getColor(
		// R.color.gray_normal));
	}

	/**
	 * 设置右边字体可以点击
	 */
	public void setRightEnbleTrue() {
		this.right_action.setVisibility(View.VISIBLE);
		// this.right_action.setEnabled(true);
		// this.right_action.setTextColor(getResources().getColor(R.color.white));
	}

	@SuppressWarnings("unchecked")
	private void ininGetHttpData() {
		if (!isLogined()) {
			startActivity(new Intent(MessagerCenterActivity.this,
					LoginActivity.class));
			this.finish();
			return;
		}
		// if (MainActivity.msgRequest == false) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", loginMessage.getUid());
		params.addBodyParameter("mobile", loginMessage.getMobile());
		ProgrosDialog.openDialog(this);
		httpBiz = new HttpBiz(this);
		httpBiz.httPostData(10001, API.CSH_MESSAGE_LIST_URL, params, this);
		// } else {
		// ProgrosDialog.closeProgrosDialog();
		// textlist = ((ArrayList<MessagCenterInfo>) mACache
		// .getAsObject(loginMessage.getUid()));
		// if (textlist == null) {
		// textlist = new ArrayList<MessagCenterInfo>();
		// }
		// messagerCenterListView.stopRefresh();
		// initpage();
		// }
	}

	@SuppressWarnings("unchecked")
	@Override
	public void receive(int type, String data) {
		Log.i("hujian", "======data======" + data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 10001:
			parseJSONData(data);
			break;
		default:
			// textlist = ((ArrayList<MessagCenterInfo>) mACache
			// .getAsObject(loginMessage.getUid()));
			// if (textlist == null) {
			// textlist = new ArrayList<MessagCenterInfo>();
			// }
			// messagerCenterListView.stopRefresh();
			// initpage();
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private void parseJSONData(String data) {
		if (!StringUtil.isEmpty(data)) {
			try {
				JSONObject jsonObject = new JSONObject(data);
				if (StringUtil.isEquals(API.returnSuccess,
						jsonObject.optString("state"), true)) {
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<List<MessagCenterInfo>>() {
					}.getType();
					httpList = gson
							.fromJson(jsonObject.getString("data"), type);
				} else {
					showToast(jsonObject.getString("message"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		// textlist = ((ArrayList<MessagCenterInfo>) mACache
		// .getAsObject(loginMessage.getUid()));
		// if (textlist == null) {
		// textlist = new ArrayList<MessagCenterInfo>();
		// }
		// if (!"".equals(data) && data != null) {
		// ArrayList<MessagCenterInfo> messageText = new
		// ArrayList<MessagCenterInfo>();
		// JSONObject js;
		// try {
		// js = new JSONObject(data);
		// String status = js.getString("operationState");
		// if (StringUtil.isEquals("SUCCESS", status, true)) {
		// JSONObject mJsonObject = js.optJSONObject("data");
		// JSONArray array = mJsonObject.optJSONArray("data");
		// MessagCenterInfo centerInfo;
		// JSONObject item;
		// if (array.length() > 0) {
		// for (int i = 0; i < array.length(); i++) {
		// item = array.optJSONObject(i);
		// // centerInfo = new MessagCenterInfo(
		// // item.optString("title"),
		// // item.optString("time"),
		// // item.optString("content"), 0,0);
		// // messageText.add(centerInfo);
		// }
		// textlist.addAll(0, messageText);
		// }
		//
		// } else if (StringUtil.isEquals("RELOGIN", status, true)) {
		// DialogTool.getInstance(this).showConflictDialog();
		// } else {
		// js = js.getJSONObject("data");
		// showToast(js.getString("msg"));
		// }
		//
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// messagerCenterListView.stopRefresh();
		// initpage();
	}

	/**
	 * 删除点击时间
	 * 
	 * @author Administrator
	 */
	private class myOnMenuItemClickListener implements OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
			switch (index) {
			case 0:
				deletaSize++;
				httpList.remove(position);
				mMessageCenterApdater.setDeleteData(httpList);
				// mMessageCenterApdater.danHangDelete(position);
				if (StringUtil.isEmpty(httpList) || httpList.size() == 0) {
					isleftDelate = false;
					steLeftDeleta();
					msg_linbottom.setVisibility(View.GONE);
					right_action.setVisibility(View.GONE);
					EmptyTools.setEmptyView(MessagerCenterActivity.this,
							messagerCenterListView);
					EmptyTools.setImg(R.drawable.message_message);
					EmptyTools.setMessage("您还没有相关消息");
				}
				break;
			}
			return false;
		}

	}

	/** 添加删除标记 **/
	private void addDelete() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F,
						0x25)));
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				openItem.setTitle(R.string.delete);
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);
			}
		};
		messagerCenterListView.setMenuCreator(creator);

	}

	/** 如果数据小于十条，显示没有数据了 **/
	public void showNover() {
		if (getNumSize() < 10) {
			setLoadingOver();
		}
	}

	/**
	 * 下拉刷新和上拉加载更多监听
	 */
	IXListViewListener ixlistener = new IXListViewListener() {
		@Override
		public void onRefresh() {
			messagerCenterListView.setPullLoadEnable(true);
			messagerCenterListView.setPullRefreshEnable(true);
			messagerCenterListView.stopLoadMore();
			pagei = 0;
			SaveList();
			httpList.clear();
			ininGetHttpData();
		}

		@Override
		public void onLoadMore() {
			// if (httpList.size() <= 0) {
			// // messagerCenterListView.setPullLoadEnable(false);
			// showToast(R.string.no_more_data);
			// } else {
			// // initpage();
			// }
			// messagerCenterListView.stopLoadMore();
		}
	};

	private void initpage() {
		// if (!textlist.isEmpty()) {
		// Log.i("result", "==textlist===" + textlist.size());
		EmptyTools.setEmptyView(this, messagerCenterListView);
		EmptyTools.setImg(R.drawable.message_message);
		EmptyTools.setMessage("您还没有相关消息");
		// if (textlist != null) {
		// int size = textlist.size();
		// if (size <= 0) {
		// messagerCenterListView.setPullRefreshEnable(false);
		// messagerCenterListView.setPullLoadEnable(false);
		// messagerCenterListView.setVisibility(View.GONE);
		// Log.i("result", "==EmptyTools.setEmptyView==1=");
		// nodata.setVisibility(View.VISIBLE);
		// // messagerCenterListView.setEmptyView(emptyView)
		// this.setRightEnble();
		// // EmptyTools.setEmptyView(this, messagerCenterListView);
		// } else {
		// this.setRightEnbleTrue();
		// messagerCenterListView.setVisibility(View.VISIBLE);
		// messagerCenterListView.setPullRefreshEnable(true);
		// messagerCenterListView.setCompileRefresh(true);
		// }
		// ArrayList<MessagCenterInfo> childList = new
		// ArrayList<MessagCenterInfo>();
		// childList.clear();
		// for (int i = pagei; i < size; i++) {
		// if (pagei == 0) {
		// if (i < 10) {
		// childList.add(textlist.get(i));
		// }
		// } else {
		// if (i < (pagei + 10)) {
		// childList.add(textlist.get(i));
		// }
		// }
		// }
		// if (pagei == 0) {
		// if (childList.size() < 10) {
		// this.messagerCenterListView.itemXiaoyuTen(false);
		// } else {
		// this.messagerCenterListView.itemXiaoyuTen(true);
		// }
		// }
		// pagei = pagei + 10;
		// pagelist.addAll(childList);
		// mMessageCenterApdater.setDeleteData(pagelist);
		// cc.dismiss();
		ProgrosDialog.closeProgrosDialog();
		// }

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			goBack();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void goBack() {
		SaveList();
		Intent mIntent = new Intent();
		Constant.CURRENT_REFRESH = Constant.MESSAGE_CENTER_REFRESH;
		mIntent.setAction(Constant.REFRESH_FLAG);
		sendBroadcast(mIntent);
		finish();
	}

	@OnClick({ R.id.left_action, R.id.right_action, R.id.msg_tv_allread,
			R.id.msg_tv_allclear })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_action:
			if (!isleftDelate) {
				goBack();
			} else {
				// int getcheckTrueSize = this.mMessageCenterApdater
				// .getcheckTrueSize();
				// if (getcheckTrueSize > 0) {
				// // // 获取未读的数量
				// MessageCenterDialog.OpenDialog(this, 3);
				// }
				messagerCenterListView.setPullRefreshEnable(true);
				steLeftDeleta();
				colorHandler.sendEmptyMessage(0);
				startAnimation(1);
				// this.right_action.setText(R.string.car_manager_edit);
				this.isleftDelate = false;
				this.msg_linbottom.setVisibility(View.GONE);
				this.mMessageCenterApdater.setISSHOW(0, 1);
				// 设置可以下拉刷新
				this.messagerCenterListView.setCompileRefresh(true);
				this.ISSHOW = 0;
				this.right_action.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.right_action:
			if (ISSHOW == 0) {
				messagerCenterListView.setPullRefreshEnable(false);
				// this.right_action.setText(R.string.finish);
				this.msg_linbottom.setVisibility(View.VISIBLE);
				startAnimation(0);
				this.mMessageCenterApdater.setISSHOW(1, 0);
				// 设置不可以下拉刷新
				this.messagerCenterListView.setCompileRefresh(false);
				setLeftText();
				getPagelistWeiduSize();
				this.isleftDelate = true;
				ISSHOW = 1;
				this.right_action.setVisibility(View.GONE);
			} else if (ISSHOW == 1) {
				messagerCenterListView.setPullRefreshEnable(true);
				steLeftDeleta();
				colorHandler.sendEmptyMessage(0);
				startAnimation(1);
				// this.right_action.setText(R.string.car_manager_edit);
				this.isleftDelate = false;
				this.msg_linbottom.setVisibility(View.GONE);
				this.mMessageCenterApdater.setISSHOW(0, 1);
				// 设置可以下拉刷新
				this.messagerCenterListView.setCompileRefresh(true);
				this.ISSHOW = 0;
			}
			break;
		case R.id.msg_tv_allread:

			// if (getPagelistWeiduSize() > 0) {
			MessageCenterDialog.OpenDialog(this, 1);
			// this.msg_tv_allread.setTextColor(R.color.orange_text_color);
			// this.msg_tv_allread.setEnabled(true);
			// } else {
			// this.msg_tv_allread.setEnabled(false);
			// this.msg_tv_allread.setTextColor(R.color.gray_normal);
			// }
			break;
		case R.id.msg_tv_allclear:
			MessageCenterDialog.OpenDialog(this, 0);
			break;
		}
	}

	private Handler colorHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				steLeftDeleta();
				break;
			}
		};
	};

	/**
	 * 删除选中
	 */
	public void checkDe() {
		this.mMessageCenterApdater.deletaCheackAll();
		msg_tv_allread.setEnabled(false);
		msg_tv_allread.setText(R.string.delete);
		this.msg_tv_allread.setTextColor(MessagerCenterActivity.this
				.getResources().getColor(R.color.gray_normal));
		int a = httpList.size();
		System.out.println("SUCCESS==============size =" + a);
		if (a == 0) {
			isleftDelate = false;
			steLeftDeleta();
			this.msg_linbottom.setVisibility(View.GONE);
			right_action.setVisibility(View.GONE);
			EmptyTools.setEmptyView(this, messagerCenterListView);
			EmptyTools.setImg(R.drawable.message_message);
			EmptyTools.setMessage("您还没有相关消息");
		}
	}

	/**
	 * 设置左边的字体 编辑状态
	 */
	public void setLeftText() {
		int getcheckTrueSize = this.mMessageCenterApdater.getcheckTrueSize();
		left_action.setCompoundDrawables(null, null, null, null);
		if (getcheckTrueSize > 0) {
			this.left_action.setEnabled(true);
			// this.left_action.setTextColor(R.color.orange_text_color);
			this.left_action.setTextColor(MessagerCenterActivity.this
					.getResources().getColor(R.color.orange));
			this.left_action.setText(R.string.cancel);
			// left_action.setText(getText(R.string.delete) + "("
			// + getcheckTrueSize + ")");
		} else {
			// this.left_action.setTextColor(R.color.gray_normal);
			this.left_action.setText(R.string.cancel);
			this.left_action.setEnabled(true);
			this.left_action.setTextColor(getResources().getColor(
					R.color.orange));
		}
	}

	/**
	 * 设置左边的字体完成状态
	 */
	public void steLeftDeleta() {
		// top_left_large_padding
		Drawable nav_up = getResources().getDrawable(R.drawable.left);
		nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
				nav_up.getMinimumHeight());
		left_action.setCompoundDrawables(nav_up, null, null, null);

		left_action.setEnabled(true);
		this.left_action.setText(R.string.back);
		// this.left_action.setTextColor(R.color.orange_text_color);
		this.left_action.setTextColor(MessagerCenterActivity.this
				.getResources().getColor(R.color.gray_normal_6));

	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			MessagCenterInfo messagCenterInfo = httpList.get(position - 1);
			Intent intent = new Intent();
			intent.putExtra("id", messagCenterInfo.getId());
			// intent.putExtra("isread", position - 1);
			intent.setClass(MessagerCenterActivity.this,
					MessageCenterDetailsActivity.class);
			mMessageCenterApdater.flag = false;
			messagCenterInfo.setIsRead(1);
			startActivity(intent);
			httpList.remove(position - 1);
			httpList.add(position - 1, messagCenterInfo);
			mMessageCenterApdater.setData(httpList);
		}
	};

	protected void onActivityResult(int arg0, int arg1, Intent data) {
		Bundle extras = data.getExtras();
		String result = extras.getString("result");
		if ("0".equals(result)) {
			int position = extras.getInt("position");
			MessagCenterInfo messagCenterInfo = pagelist.get(position);
			messagCenterInfo.setIsRead(1);
			pagelist.remove(position);
			pagelist.add(position, messagCenterInfo);
			mMessageCenterApdater.setData(pagelist);
		}
	};

	/**
	 * 两边保持一致
	 * 
	 * @param position
	 */
	public void remevePageList(int position) {
		checkBoxDesiz++;
		DBTools.getInstance(this).deleteById(MessagCenterInfo.class,
				httpList.get(position).getId() + "");
		httpList.remove(position);
	}

	/**
	 * 刷新集合
	 */
	public void Relafresh() {
		this.mMessageCenterApdater.setDeleteData(pagelist);
	}

	// 拿到总大小
	public int getNumSize() {
		return textlist.size();

	}

	public void setLoadingOver() {
		this.messagerCenterListView.itemXiaoyuTen(false);
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	/**
	 * 获取未读数量大小
	 */
	public int getPagelistWeiduSize() {
		int weidu = 0;
		int size = httpList.size();
		for (int i = 0; i < size; i++) {
			MessagCenterInfo messagCenterInfo = httpList.get(i);
			if (messagCenterInfo.getIsRead() == 0) {
				weidu++;
			}
		}
		// if (weidu <= 0) {
		// this.msg_tv_allread.setText(R.string.all_read);
		// this.msg_tv_allread.setEnabled(false);
		// this.msg_tv_allread.setTextColor(MessagerCenterActivity.this
		// .getResources().getColor(R.color.gray_normal));
		//
		// } else {
		// this.msg_tv_allread.setEnabled(true);
		// this.msg_tv_allread.setTextColor(MessagerCenterActivity.this
		// .getResources().getColor(R.color.orange_text_color));
		// this.msg_tv_allread.setText(getText(R.string.all_read) + "("
		// + weidu + ")");
		// }
		return weidu;
	}

	/**
	 * 所有未读数量
	 */
	public int getTextlistWeiduSize(ArrayList<MessagCenterInfo> saveList) {
		int weidu = 0;
		int size = saveList.size();
		for (int i = 0; i < size; i++) {
			MessagCenterInfo messagCenterInfo = saveList.get(i);
			if (messagCenterInfo.getIsRead() == 0) {
				weidu++;
			}
		}
		return weidu;
	}

	/**
	 * 设置全部已读
	 */
	public void setAllread() {
		int size = httpList.size();
		for (int i = 0; i < size; i++) {
			MessagCenterInfo messagCenterInfo = httpList.get(i);
			if (messagCenterInfo.getIsRead() == 0) {
				messagCenterInfo.setIsRead(1);
			}
		}
		this.msg_tv_allread.setText(R.string.all_read);
		this.msg_tv_allread.setEnabled(false);
		this.msg_tv_allread.setTextColor(MessagerCenterActivity.this
				.getResources().getColor(R.color.gray_normal));
		this.mMessageCenterApdater.setDeleteData(pagelist);
	}

	/**
	 * 清空数据
	 */
	public void setAllClear() {
		clearAllize += httpList.size();
		httpList.clear();
		msg_tv_allread.setText(R.string.delete);
		left_action.setText(R.string.cancel);
		mMessageCenterApdater.setDeleteData(httpList);
		DBTools.getInstance(this).delete(MessagCenterInfo.class);
		msg_linbottom.setVisibility(View.GONE);
		steLeftDeleta();
		isleftDelate = false;
		right_action.setVisibility(View.GONE);
		EmptyTools.setEmptyView(this, messagerCenterListView);
		EmptyTools.setImg(R.drawable.message_message);
		EmptyTools.setMessage("您还没有相关消息");
		// initpage();

	}

	/**
	 * 删除数量
	 */
	public void setDeletaNum(int position) {
		if (position <= 0) {
			// isOver = false;
			this.msg_tv_allread.setText(R.string.delete);
			this.msg_tv_allread.setTextColor(MessagerCenterActivity.this
					.getResources().getColor(R.color.gray_normal));
		} else {
			this.msg_tv_allread.setEnabled(true);
			this.msg_tv_allread.setTextColor(MessagerCenterActivity.this
					.getResources().getColor(R.color.orange));
			// isOver = true;
			this.msg_tv_allread.setText(getString(R.string.delete) + "("
					+ position + ")");

		}
	}

	@Override
	public void onBackPressed() {
		SaveList();

		super.onBackPressed();
	}

	@

	Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * 清除数据的时候保存数据
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 保存数据
	 */
	public void SaveList() {
		if (textlist != null && pagelist != null) {
			int size = pagelist.size();
			int allSize = size + deletaSize + clearAllize + checkBoxDesiz;
			ArrayList<MessagCenterInfo> saveList = new ArrayList<MessagCenterInfo>();
			List<MessagCenterInfo> subList = textlist.subList(allSize,
					textlist.size());
			saveList.addAll(subList);
			saveList.addAll(pagelist);

			// Toast.makeText(this, "===00==="+getTextlistWeiduSize(saveList),
			// 1).show();
			// Log.i("hujian", "===00==="+getTextlistWeiduSize(saveList));
			setDataBack(getTextlistWeiduSize(saveList));
			deletaSize = 0;
			clearAllize = 0;
			checkBoxDesiz = 0;
			pagei = 0;
			pagelist.clear();
			textlist.clear();
			// mACache.put(loginMessage.getUid(), saveList, 360 *
			// ACache.TIME_DAY);
			saveList.clear();
		}

	}

	// 打开的动画
	public void AnimationOpen() {
		// 初始化数组
		int[] location = new int[2];
		msg_linbottom.getLocationInWindow(location);
		int x = location[0];
		int y = location[1];
		animationOpen = new TranslateAnimation(x, x, y + 500, y);
		animationOpen.setDuration(300);
	}

	// 初始化动画
	public void initAnimation() {
		AnimationOpen();
		AnimationClose();
	}

	public void startAnimation(int type) {
		removeAnimation();
		if (type == 0) {
			msg_linbottom.startAnimation(animationOpen);
		} else if (type == 1) {
			msg_linbottom.startAnimation(animationClose);
		}
	}

	// 关闭的动画
	public void AnimationClose() {
		int[] location = new int[2];
		msg_linbottom.getLocationInWindow(location);
		int x = location[0];
		int y = location[1];
		animationClose = new TranslateAnimation(x, x, y, y + 500);
		animationClose.setDuration(300);
	}

	public void removeAnimation() {
		if (msg_linbottom.getAnimation() != null) {
			msg_linbottom.clearAnimation();
			msg_linbottom.setAnimation(null);
		}
	}

	// /**
	// * toast信息
	// *
	// * @param message
	// */
	// private void toastInfo(String message) {
	// showToast(message);
	// }

	/**
	 * 设置回调
	 * 
	 * @param mMessagCenterInfo
	 */
	private void setDataBack(int positionsize) {
		// 数据是使用Intent返回
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("position", positionsize);
		intent.putExtras(bundle);
		// 设置返回数据
		MessagerCenterActivity.this.setResult(1053, intent);
	}
}
