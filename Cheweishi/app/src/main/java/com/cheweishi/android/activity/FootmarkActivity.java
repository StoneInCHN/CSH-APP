package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.FootmarkAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.TrackMessageBean;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.utils.BitmapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.BaiduMapView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

public class FootmarkActivity extends BaseActivity implements
		OnRefreshListener2<ExpandableListView>, OnScrollListener {

	private static final int AS_CODE = 0;
	@ViewInject(R.id.left_action_footmark)
	private Button mLeftAction;
	@ViewInject(R.id.footmark_listview)
	private PullToRefreshExpandableListView listView;
	@ViewInject(R.id.relativelayout_footmark)
	private RelativeLayout relativelayout_footmark;
	private List<String> mGroup = new ArrayList<String>();
	private List<List<TrackMessageBean>> lwChild = new ArrayList<List<TrackMessageBean>>();
	private List<List<TrackMessageBean>> childA = new ArrayList<List<TrackMessageBean>>();
	private List<TrackMessageBean> childTemp = new ArrayList<TrackMessageBean>();
	private List<TrackMessageBean> list;
	private List<TrackMessageBean> mList = new ArrayList<TrackMessageBean>();
	private FootmarkAdapter footmarkAdapter;

	private TextView cityTextView;
	private TextView stopPoint;
	private View view;
	private int page = 1;
	private ExpandableListView mListView;
	private MapView mapView;
	private BaiduMap mBaiduMap;
	private BaiduMapView mBaiduMapView;

	private BitmapDescriptor bitmap;// = BitmapDescriptorFactory
	// .fromResource(R.drawable.zuji_dian);
	private String temp;
	private int indexTemp = 0;
	private Button map_top_null;
	@ViewInject(R.id.relativelayout_footmark)
	private RelativeLayout relativeLayout_T_footmark;

	@ViewInject(R.id.tr_title)
	private TextView tr_title;

	@ViewInject(R.id.tr_right_action)
	private TextView tr_right_action;

	@ViewInject(R.id.tr_left_action)
	private Button tr_left_action;

	@ViewInject(R.id.footmark_linearlayout)
	private LinearLayout footmark_linearlayout;

	// private MyBroadcastReceiver broad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_footmark);
		ViewUtils.inject(this);
		loginMessage = LoginMessageUtils.getLoginMessage(this);
		setTitle();
		relativelayout_footmark.setVisibility(View.GONE);
		baiduMapSetting();
		setHight();

		initBaidu();

		initListview();

		footmark_linearlayout.addView(view);

		initIndicator();
		init();
		initListener();

	}

	private void setTitle() {
		// TODO Auto-generated method stub
		mLeftAction.setText(getString(R.string.back));
		tr_title.setText(R.string.footmark);
		tr_right_action.setText(R.string.setting);
	}

	private void setHight() {
		// TODO Auto-generated method stub
		android.view.ViewGroup.LayoutParams lp = mapView.getLayoutParams();
		int height = (int) (ScreenTools.getScreentHeight(this) * 0.3);
		int width = ScreenTools.getScreentWidth(this);
		lp.width = width;
		lp.height = height;
	}

	private void initBaidu() {

		float f = mBaiduMap.getMaxZoomLevel();// 19.0 最小比例尺
		LatLng latLng = new LatLng(35.3349, 103.2319);
		mBaiduMapView.moveLatLng(latLng, f - 14);

	}

	private void initListview() {
		// TODO Auto-generated method stub
		mListView = listView.getRefreshableView();
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setGroupIndicator(null);
		mListView.setDividerHeight(0);
		listView.setMode(Mode.PULL_FROM_END);
	}

	private void baiduMapSetting() {
		view = LayoutInflater.from(this).inflate(R.layout.headerview, null);
		cityTextView = (TextView) view.findViewById(R.id.cityPoint);
		stopPoint = (TextView) view.findViewById(R.id.stoppoint);
		mapView = (MapView) view
				.findViewById(R.id.footmark_headerview_baidumap);
		map_top_null = (Button) view.findViewById(R.id.map_top_null);

		mapView.showZoomControls(false);
		mapView.setOnTouchListener(null);
		mapView.setOnClickListener(null);
		mapView.removeViewAt(1);
		mapView.setFocusable(false);
		mapView.setEnabled(false);
		mBaiduMap = mapView.getMap();
		mBaiduMapView = new BaiduMapView(mapView, this);
		mBaiduMap.setTrafficEnabled(false);
		mapView.showScaleControl(false);
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		mBaiduMap.setBuildingsEnabled(false);
		mBaiduMap.setOnMapTouchListener(null);
		mBaiduMap.setOnMapClickListener(null);

	}

	private void initListener() {
		// TODO Auto-generated method stub
		tr_left_action.setOnClickListener(listener);
		tr_right_action.setOnClickListener(listener);
		listView.setOnRefreshListener(this);
		mLeftAction.setOnClickListener(listener);
		mListView.setOnScrollListener(this);
		map_top_null.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}
		});
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {

			switch (arg0.getId()) {
			case R.id.tr_left_action:
				FootmarkActivity.this.finish();
				break;
			case R.id.tr_right_action:
				Intent intent = new Intent(FootmarkActivity.this,
						FootmarkSettingActivity.class);
				// startActivity(intent);
				break;

			default:
				break;
			}

		}
	};

	private void init() {
		request();
		mListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});

		mListView.setOnChildClickListener(onChildClickListener);
		int group = mListView.getCount();
		for (int i = 0; i < group; i++) {
			mListView.expandGroup(i);
		}
	}

	private OnChildClickListener onChildClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView arg0, View arg1,
				int groupPosition, int childPosition, long id) {

			String latlon = lwChild.get(groupPosition).get(childPosition)
					.getLnglat();
			Intent intent = new Intent(FootmarkActivity.this,
					FootmarkMapActivity.class);
			intent.putExtra("latlon", latlon);
			startActivity(intent);
			return true;
		}
	};

	protected void pson(JSONObject jsonObject) {
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<List<TrackMessageBean>>() {
		}.getType();
		list = gson.fromJson(
				jsonObject.optJSONObject("data").optString("list"), type);
		mList.addAll(list);
		if (mList != null) {
			for (int i = 0; i < list.size(); i++) {
				if (mList.get(i).getType().equals("0")) {
					String latlon = mList.get(i).getLnglat();
					String[] latlonsStrings = latlon.split(",");
					double lng = StringUtil.getDouble(latlonsStrings[0]);
					double lat = StringUtil.getDouble(latlonsStrings[1]);
					LatLng latLng = new LatLng(lat, lng);
					if (StringUtil.isEmpty(bitmap)) {
						bitmap = BitmapDescriptorFactory
								.fromResource(R.drawable.zuji_dian);
					}
					try {
						mBaiduMapView.moveLatlng(latLng, bitmap, 5);
					} catch (Exception e) {
						bitmap = BitmapDescriptorFactory
								.fromResource(R.drawable.zuji_dian);
						try {
							mBaiduMapView.moveLatlng(latLng, bitmap, 5);
						} catch (Exception e2) {
						}
						
					}
				}
			}
		} else {
			throw new NullPointerException("list no instantiation");
		}

		setGroup();
		if (footmarkAdapter == null) {
			footmarkAdapter = new FootmarkAdapter(FootmarkActivity.this,
					mGroup, lwChild);
			mListView.setAdapter(footmarkAdapter);
		} else {
			footmarkAdapter.setlist(mGroup, lwChild);
		}
		for (int i = 0; i < footmarkAdapter.getGroupCount(); i++) {
			mListView.expandGroup(i);
		}

	}

	private void setGroup() {
		if (temp == null || temp.equals("") && mList.get(0).getTime() != null) {
			temp = mList.get(0).getTime();
			indexTemp = 0;
			mGroup.add(temp);
			childTemp.add(mList.get(0));
			if (mList.size() > 1) {
				for (int i = 1; i < mList.size(); i++) {
					if (temp.equals(mList.get(i).getTime())) {
						childTemp.add(mList.get(i));
						if (i == mList.size() - 1) {
							lwChild.add(childTemp);
							temp = mList.get(mList.size() - 1).getTime();
						}
					} else {

						lwChild.add(childTemp);
						childTemp = new ArrayList<TrackMessageBean>();
						temp = mList.get(i).getTime();
						mGroup.add(temp);
						indexTemp = i;
						childTemp.add(mList.get(i));
						if (indexTemp == mList.size() - 1) {
							lwChild.add(childTemp);
							temp = mList.get(mList.size() - 1).getTime();
						}
					}
				}
			} else {
				if (mList.size() == 1) {
					lwChild.add(childTemp);
				}
			}
		} else {
			if (indexTemp < mList.size() - 1) {
				for (int i = (page - 1) * 10; i < mList.size(); i++) {
					if (mList.get(i).getTime().equals(temp)) {

						childTemp.add(mList.get(i));
						if (i == mList.size() - 1) {
							if (lwChild
									.get(lwChild.size() - 1)
									.get(lwChild.get(lwChild.size() - 1).size() - 1)
									.getTime()
									.equals(childTemp.get(0).getTime())) {
								lwChild.set(lwChild.size() - 1, childTemp);
							} else {
								lwChild.add(childTemp);
							}
							temp = mList.get(mList.size() - 1).getTime();
						}
					} else {
						if (lwChild
								.get(lwChild.size() - 1)
								.get(lwChild.get(lwChild.size() - 1).size() - 1)
								.getTime().equals(childTemp.get(0).getTime())) {
							lwChild.set(lwChild.size() - 1, childTemp);
						} else {
							lwChild.add(childTemp);
						}
						indexTemp = i;
						childTemp = new ArrayList<TrackMessageBean>();

						temp = mList.get(i).getTime();
						mGroup.add(temp);
						indexTemp = i;
						childTemp.add(mList.get(i));
						if (indexTemp == mList.size() - 1) {
							lwChild.add(childTemp);
							indexTemp = mList.size() - 1;
						}
					}
				}
			}

		}
	}

	public void request() {
		RequestParams requestParams = new RequestParams(HTTP.UTF_8);

		String uid = loginMessage.getUid();
		String key = loginMessage.getKey();
		String device = loginMessage.getCar().getDevice();
		if (isLogined()) {
			if (device != null && !StringUtil.isEquals(device, "", true)) {
				requestParams.addBodyParameter("key", key);
				requestParams.addBodyParameter("device", device);
				requestParams.addBodyParameter("uid", uid);
				requestParams.addBodyParameter("page", "" + page);
				requestParams.addBodyParameter("size", "10");
				httpBiz = new HttpBiz(FootmarkActivity.this);
				ProgrosDialog.openDialog(this);
				httpBiz.httPostData(AS_CODE, API.FOOTMARK_URL, requestParams,
						this);
			} else {
				showToast(R.string.mile_toast_nodivice);
			}
		} else {
			showToast(R.string.information_no_login);
		}

	}

	@Override
	public void onPullDownToRefresh(
			PullToRefreshBase<ExpandableListView> refreshView) {
		temp = null;
		indexTemp = 0;
		mGroup.clear();
		lwChild.clear();
		childA.clear();
		childTemp.clear();
		list.clear();
		page = 1;
		request();
		listView.onRefreshComplete();

	}

	@Override
	public void onPullUpToRefresh(
			PullToRefreshBase<ExpandableListView> refreshView) {
		page++;
		request();
		listView.onRefreshComplete();
	}

	private void initIndicator() {
		ILoadingLayout endLabels = listView.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel(this.getResources().getString(
				R.string.Pull_Up_To_Refresh_pull_lable));// 刚下拉时，显示的提示
		endLabels.setRefreshingLabel(getResources().getString(
				R.string.Pull_Up_To_Refresh_release_lable));// 刷新时
		endLabels.setReleaseLabel(getResources().getString(
				R.string.Pull_Up_To_Refresh_refreshing_lable));// 下来达到一定距离时，显示的提示
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mBaiduMap.clear();
		mBaiduMap = null;
		mapView.onDestroy();
		mapView = null;
		view = null;
		listener = null;
		listView = null;
		mListView = null;
		mGroup.clear();
		mGroup = null;
		childA.clear();
		childA = null;
		lwChild.clear();
		lwChild = null;
		childTemp.clear();
		childTemp = null;
		list = null;
		footmarkAdapter = null;
		if (!StringUtil.isEmpty(bitmap)) {
			bitmap.recycle();
		}
		
		temp = null;
		indexTemp = 0;
		page = 0;
		stopPoint = null;
		mLeftAction = null;
		cityTextView = null;
	}

	// 滑动监听
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem >= 1) {
			mLeftAction.setVisibility(View.GONE);
		} else {
			mLeftAction.setVisibility(View.VISIBLE);
		}
		if (firstVisibleItem >= 1) {
			relativelayout_footmark.setVisibility(View.VISIBLE);
		} else {
			relativelayout_footmark.setVisibility(View.GONE);
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case AS_CODE:
			analysis(data);
			break;

		default:
			break;
		}
	}

	private void analysis(String data) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(jsonObject.optString("operationState"),
					API.returnSuccess, true)) {
				JSONObject jsonObject2 = jsonObject.optJSONObject("data");
				int city = jsonObject2.optInt("city");
				if (!StringUtil.isEmpty(city)) {
					if (!StringUtil.isEmpty(cityTextView)) {
						cityTextView.setText(city + "个城市点");
					}
				}

				int point = jsonObject2.optInt("point");
				if (!StringUtil.isEmpty(point)) {
					if (stopPoint != null) {
						stopPoint.setText(point + "个足迹点");
					}
				}
				if (page == 1) {
					Bitmap bitmap = BitmapUtils.getViewBitmap(view);
					ImageView imageView = new ImageView(FootmarkActivity.this);
					imageView.setImageBitmap(bitmap);
					mListView.addHeaderView(imageView);
				}
				if (jsonObject2.optJSONArray("list").length() == 0) {
					if (page == 1) {
						footmarkAdapter = new FootmarkAdapter(
								FootmarkActivity.this, mGroup, lwChild);
						mListView.setAdapter(footmarkAdapter);
						mListView.setCacheColorHint(0);
						mListView.setSelector(android.R.color.transparent);
						// EmptyTools.setEmptyView(this, mListView);
					}

				} else {
					pson(jsonObject);
				}
			} else {
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnRelogin, true)) {
					DialogTool.getInstance(this).showConflictDialog();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}

	}

	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// if (broad == null) {
	// broad = new MyBroadcastReceiver();
	// }
	//
	// IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
	// registerReceiver(broad, intentFilter);
	// }
	//
	// public class MyBroadcastReceiver extends BroadcastReceiver {
	//
	// public void onReceive(Context context, Intent intent) {
	// Constant.EDIT_FLAG = false;
	// System.out.println("SUCCESS====main_" + Constant.CURRENT_REFRESH);
	// if (!StringUtil.isEquals(intent.getAction(), Constant.REFRESH_FLAG,
	// true)) {
	// System.out.println("SUCCESS====" + "更新false");
	// return;
	// }
	// if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
	// Constant.LOGIN_REFRESH, true)) {
	// System.out.println("SUCCESS====" + "登录信息更新Main");
	//
	//
	// } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
	// Constant.CAR_MANAGER_REFRESH, true)) {
	// System.out.println("SUCCESS====" + "车辆信息更新");
	// } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
	// Constant.MESSAGE_CENTER_REFRESH, true)) {
	// System.out.println("SUCCESS====" + "消息中心更新");
	// } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
	// Constant.SPECIAL_SIGN_REFRESH, true)) {
	// System.out.println("SUCCESS====" + "个性签名更新");
	// } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
	// Constant.USER_CENTER_REFRESH, true)) {
	// System.out.println("SUCCESS====" + "个人中心更新");
	// } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
	// Constant.USER_NICK_EDIT_REFRESH, true)) {
	//
	// System.out.println("SUCCESS====" + "个人编辑更新");
	//
	// }
	//
	// }
	// }
}
