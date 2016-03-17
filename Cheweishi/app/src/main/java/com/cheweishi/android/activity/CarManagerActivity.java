package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.CarManagerAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.ButtonUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.SwipeListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Xiaojin车辆管理
 * 
 */
public class CarManagerActivity extends BaseActivity implements
		OnClickListener, CarManagerAdapter.onRightItemClickListener,
		OnItemClickListener {

	@ViewInject(R.id.listView_carManager)
	private SwipeListView listView_carManager;// 滑动列表
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	private CarManagerAdapter adapter;
	private List<CarManager> listCarManager = new ArrayList<CarManager>();
	private List<CarManager> listCarManagerTemp;
	CarManager carManagerItem = null;
	@ViewInject(R.id.ll_head)
	private LinearLayout ll_head;
	@ViewInject(R.id.listView_front)
	private LinearLayout listView_front;
	@ViewInject(R.id.ll_no_data)
	private LinearLayout ll_no_data;
	private int itemIndex;
	private MyBroadcastReceiver broad;
	public static CarManagerActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fr_list_carmanager);
		ViewUtils.inject(this);
		initViews();
		setListeners();
	}

	private void initViews() {
		title.setText("我的车辆");
		left_action.setText(R.string.back);
		right_action.setText(R.string.button_add);
		right_action.setVisibility(View.GONE);
		instance = this;
		adapter = new CarManagerAdapter(this, listCarManager,
				listView_carManager.getRightViewWidth());
		listView_carManager.setAdapter(adapter);
		adapter.setOnRightItemClickListener(this);
		listView_carManager.setSelector(new ColorDrawable(Color.TRANSPARENT));
		httpBiz = new HttpBiz(this);
		connectToServer();
	}

	private void setListeners() {
		listView_carManager.setOnItemClickListener(this);
	}

	@OnClick({ R.id.listView_front, R.id.left_action, R.id.right_action })
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.right_action:
			Intent intent = new Intent(this, AddCarActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (broad == null) {
			broad = new MyBroadcastReceiver();
		}

		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		registerReceiver(broad, intentFilter);
	}

	@Override
	public void onRightItemClick(View v, int position) {
		// TODO Auto-generated method stub
		if (ButtonUtils.isFastClick()) {
			return;
		} else {
			if (listCarManager.size() > position) {
				Intent intent = new Intent(this, AddCarActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("car", listCarManager.get(position));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}

	@OnItemClick({ R.id.listView_carManager })
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (ButtonUtils.isFastClick()) {
			return;
		} else {
			itemIndex = arg2;
			carManagerItem = listCarManager.get(itemIndex);
			// if ((carManagerItem.getFeed()) == 0) {
			ConnectItemToServer(listCarManager.get(itemIndex).getCid());
			// } else {
			// // if (StringUtil.isEmpty(listCarManager.get(itemIndex)
			// // .getDevice())) {
			// ConnectItemToServer(listCarManager.get(itemIndex).getCid());
			// // } else {
			// // Intent pageIntent = new Intent(mContext,
			// // CarManagerBindActivity.class);
			// // Bundle bundle = new Bundle();
			// // bundle.putSerializable("car", carManagerItem);
			// // pageIntent.putExtra("currentCid", carManagerItem.getCid());
			// // pageIntent.putExtra("CarManagerBindActivity", 1000);
			// // pageIntent.putExtras(bundle);
			// // startActivity(pageIntent);
			// // }
			// }
		}
	}

	/**
	 * 请求车辆列表
	 */
	private void connectToServer() {
		if (isLogined()) {
			ProgrosDialog.openDialog(this);
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("mobile", loginMessage.getMobile());
			httpBiz.httPostData(10001, API.CAR_MANAGER_URL, params, this);
		}
	}

	/**
	 * 请求设置默认车辆服务器
	 * 
	 * @param cid
	 */
	private void ConnectItemToServer(String cid) {
		ProgrosDialog.openDialog(this);
		if (isLogined()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("mobile", loginMessage.getMobile());
			params.addBodyParameter("n_cid", cid);
			params.addBodyParameter("o_cid", loginMessage.getCarManager()
					.getId());
			httpBiz.httPostData(20000, API.SET_DEDAULT_CAR_URL, params, this);
		}
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 10001:
			ProgrosDialog.closeProgrosDialog();
			parseUserDetailJSON(data);
			break;
		case 20000:
			parseJson(data);
			break;
		case 400:
			ProgrosDialog.closeProgrosDialog();
			showToast(R.string.server_link_fault);
			if (listCarManager == null || listCarManager.size() == 0) {
				EmptyTools.setEmptyView(this, listView_carManager);
				EmptyTools.setImg(R.drawable.mycar_icon);
				EmptyTools.setMessage("您还没有添加车辆");
			}
			break;
		}
	}

	/**
	 * 解析车辆列表
	 * 
	 * @param result
	 */
	private void parseUserDetailJSON(String result) {
		if (StringUtil.isEmpty(result)) {
			showToast(R.string.data_fail);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String resultStr = jsonObject.optString("state");
				if (StringUtil.isEquals(resultStr, API.returnSuccess, true)) {
					listCarManagerTemp = new ArrayList<CarManager>();
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<List<CarManager>>() {
					}.getType();
					listCarManagerTemp = gson.fromJson(
							jsonObject.optString("data"), type);
					listCarManager.clear();
					listCarManager.addAll(listCarManagerTemp);
					if (listCarManager.size() > 0) {
						ll_head.setVisibility(View.GONE);
					} else {
						EmptyTools.setEmptyView(this, listView_carManager);
						EmptyTools.setImg(R.drawable.mycar_icon);
						EmptyTools.setMessage("您还没有添加车辆");
					}
					listView_carManager.hiddenRight(null);
					listView_carManager.requestLayout();
					adapter.setData(listCarManager);
					listView_front.setVisibility(View.INVISIBLE);
					if (listCarManager.size() >= 3) {
						right_action.setVisibility(View.GONE);
					} else {
						right_action.setVisibility(View.VISIBLE);
					}
				} else if (StringUtil.isEquals(resultStr, API.returnRelogin,
						true)) {
					ReLoginDialog.getInstance(this).showDialog(
							jsonObject.optString("message"));
				} else {
					showToast(jsonObject.optString("message"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * 发送广播，更新因为车辆管理而变化的Activity
	 */
	private void judgeCurrentRefreahGoBack() {
		Constant.CURRENT_REFRESH = Constant.CAR_MANAGER_REFRESH;
		Intent mIntent = new Intent();
		mIntent.setAction(Constant.REFRESH_FLAG);
		sendBroadcast(mIntent);
	}

	/**
	 * 解析默认车辆
	 * 
	 * @param result
	 */
	private void parseJson(String result) {
		if (StringUtil.isEmpty(result)) {
			showToast(R.string.data_fail);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String resultStr = jsonObject.optString("state");
				if (StringUtil.isEquals(resultStr, API.returnSuccess, true)) {
					judgeCurrentRefreahGoBack();
					CarManager car = loginMessage.getCarManager();
					if (carManagerItem != null) {
						if (carManagerItem != null) {
							// car.setBrand(carManagerItem.getBrand());
							car.setId(carManagerItem.getId());
							car.setColor(carManagerItem.getColor());
							car.setDevice(carManagerItem.getDevice());
							car.setModule(carManagerItem.getModule());
							car.setPlate(carManagerItem.getPlate());
							car.setSeries(carManagerItem.getSeries());
							car.setVinNo(carManagerItem.getVinNo());
							car.setBrand(carManagerItem.getBrand());
						}
						BaseActivity.loginMessage.setCarManager(car);
						DBTools.getInstance(this).save(loginMessage);
						adapter.notifyDataSetChanged();
					}
				} else if (StringUtil.isEquals(resultStr, API.returnRelogin,
						true)) {
					DialogTool.getInstance(this).showConflictDialog();
				} else {
					showToast(jsonObject.optString("message"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * 重新链接
	 */
	public void reconnect() {
		listView_front.setVisibility(View.VISIBLE);
		if (listCarManagerTemp != null) {
			listCarManagerTemp.clear();
		}
		connectToServer();
	}

	public void setListVisible(boolean flag) {
		if (flag == false) {
			listView_carManager.setVisibility(View.GONE);
		} else {
			listView_carManager.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (listCarManager != null) {
			listCarManager.clear();
		}
		if (listCarManagerTemp != null) {
			listCarManagerTemp.clear();
		}
		unregisterReceiver(broad);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.CAR_MANAGER_REFRESH, true)) {
				reconnect();

			}
		}
	}

	@Override
	public void dealCallBackFromAdapter(int pos, Object obj) {
		// TODO Auto-generated method stub
		super.dealCallBackFromAdapter(pos, obj);
		if (ButtonUtils.isFastClick()) {
			return;
		} else {

			itemIndex = pos;
			carManagerItem = listCarManager.get(itemIndex);
			ConnectItemToServer(listCarManager.get(itemIndex).getId());

		}
	}
}
