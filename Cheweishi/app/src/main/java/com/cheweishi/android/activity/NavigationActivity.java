package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.FindCarSearchAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CarDynamicVO;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.CustomProgressDialog;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.utils.mapUtils.BaseMapUtil;
import com.cheweishi.android.utils.mapUtils.LocationUtil;
import com.cheweishi.android.utils.mapUtils.MapSearchUtil;
import com.cheweishi.android.utils.mapUtils.NavigationUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.CustomDialog.Builder;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/***
 * 导航界面
 * 
 * @author zhangq
 * 
 */
public class NavigationActivity extends BaseActivity {
	public static final String TAG = "NavigationActivity";
	public static final int INTENT_CAR_DYNAMIC = 1001;
	public static final int INTENT_CHAT = 1002;

	@ViewInject(R.id.left_action)
	private TextView mLeftAction;// 左边动作
	@ViewInject(R.id.title)
	private TextView mTitle;// 中间标题
	@ViewInject(R.id.right_action)
	private ImageButton ibtnRight;// 右边动作
	@ViewInject(R.id.navigation_bmapView)
	private MapView mMapView;
	@ViewInject(R.id.navigation_edit_message)
	private EditText searchView;// 自动完成输入
	@ViewInject(R.id.tv_start_navigation)
	private TextView tvStartNavigation;// 开始导航
	@ViewInject(R.id.navigation_location_addess)
	private TextView tvLocation;
	@ViewInject(R.id.tv_target)
	private TextView tvTarget;
	@ViewInject(R.id.llayout_search)
	private LinearLayout lLayoutSearch;// 搜索框
	@ViewInject(R.id.cbox_traffic)
	private CheckBox cBoxTraffic;// 交通路况
	@ViewInject(R.id.cbox_car_location)
	private RadioButton rbtnCarLocation;// 车的位置
	@ViewInject(R.id.cbox_person_location)
	private RadioButton rbtnPersonLocation;// 人的位置
	@ViewInject(R.id.llayout_below)
	private LinearLayout lLayoutBelow;
	@ViewInject(R.id.ibtn_increse)
	private ImageView imgIncrese;//
	@ViewInject(R.id.ibtn_decrese)
	private ImageView imgDecrese;
	@ViewInject(R.id.map_list)
	private ListView mapListView;

	// private String carId;
	private String cid;
	private String uid;
	private String key;// 登陆key
	private boolean isLogined = false;// 判断是否登陆 true登陆
	private boolean hasCar = false;// 是否有车
	private int mIndex;// 设置intent来自标志
	private LatLng selectLatLng;// 目的地信息
	private LatLng carLatLng;// 车位置
	private LatLng personLatLng;// 人位置
	private String poiStr;// 搜索关键字
	private DrivingRouteOverlay overlay;// 路线规划层
	private ArrayList<HashMap<String, String>> searchDatas = new ArrayList<HashMap<String, String>>();
	private FindCarSearchAdapter searchAdapter;
	private CustomProgressDialog progressDialog;
	private boolean isDataChanged = true;// autocompletetextview的数据是否加载
	/**
	 * 标志按下“完成”发起搜索
	 */
	private boolean FLAG_DONE = false;

	private LocationUtil mLocationUtil;
	private BaseMapUtil mBaseMapUtil;
	private MapSearchUtil mMapSearchUtil;
	private NavigationUtil mNavigationUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		ViewUtils.inject(this);
		httpBiz = new HttpBiz(this);
		init();
	}

	private void init() {
		// 判断intent来源
		Intent mIntent = getIntent();
		mIndex = mIntent.getIntExtra(TAG, 0);
		switch (mIndex) {
		case INTENT_CHAT:
			mTitle.setText("位置信息");
			ibtnRight.setOnClickListener(clickListener);
			break;
		default:
			mTitle.setText("导    航");
			ibtnRight.setVisibility(View.INVISIBLE);
			isLogined = mIntent.getBooleanExtra("isLogined", false);
			hasCar = mIntent.getBooleanExtra("hasCar", false);
			break;
		}

		// 绑定监听
		mLeftAction.setOnClickListener(clickListener);
		tvStartNavigation.setOnClickListener(clickListener);
		lLayoutBelow.setOnClickListener(clickListener);
		cBoxTraffic.setOnCheckedChangeListener(checkedChangeListener);
		rbtnCarLocation.setOnCheckedChangeListener(checkedChangeListener);
		rbtnPersonLocation.setOnCheckedChangeListener(checkedChangeListener);
		rbtnPersonLocation.setOnClickListener(clickListener);
		imgIncrese.setOnClickListener(clickListener);
		imgDecrese.setOnClickListener(clickListener);

		searchAdapter = new FindCarSearchAdapter(this, null,
				FindCarSearchAdapter.FLAG_LOCATION);
		searchView.addTextChangedListener(watcher);
		searchView.setOnEditorActionListener(onEditorActionListener);
		mapListView.setAdapter(searchAdapter);
		mapListView.setOnItemClickListener(itemClickListener);

		// 设置地图信息
		setMapInfo();

		// 默认选择为人
		rbtnPersonLocation.setChecked(true);

		// 没车跳过下面处理
		if (!isLogined || !hasCar) {
			return;
		}
		getInfo();
	}

	/**
	 * 输入监听
	 */
	OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				FLAG_DONE = true;
				poiStr = searchView.getText() == null ? null : searchView
						.getText().toString();
				if (!StringUtil.isEmpty(poiStr)) {
					mMapSearchUtil.startSuggetSearch(
							MyMapUtils.getCity(NavigationActivity.this),
							poiStr, onGetSuggestionResultListener);
				}
				return true;
			}
			return false;
		}
	};

	/**
	 * 建议点搜索
	 */
	private OnGetSuggestionResultListener onGetSuggestionResultListener = new OnGetSuggestionResultListener() {

		@Override
		public void onGetSuggestionResult(SuggestionResult result) {
			if (result == null || result.getAllSuggestions() == null) {
				return;
			}

			if (FLAG_DONE) {
				FLAG_DONE = false;
				dealAfterDone(result.getAllSuggestions());
				return;
			}
			searchDatas.clear();
			for (SuggestionResult.SuggestionInfo info : result
					.getAllSuggestions()) {
				if (info.pt != null) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("city", info.city);
					map.put("district", info.district);
					map.put("key", info.key);
					map.put("latitude", info.pt.latitude + "");
					map.put("longitude", info.pt.longitude + "");
					searchDatas.add(map);
				}
			}
			searchAdapter = new FindCarSearchAdapter(NavigationActivity.this,
					searchDatas, FindCarSearchAdapter.FLAG_LOCATION);
			mapListView.setAdapter(searchAdapter);
			if (mapListView.getVisibility() == View.INVISIBLE) {
				mapListView.setVisibility(View.VISIBLE);
			}
		}
	};

	/**
	 * 是否登陆、有车辆提示dialog
	 */
	private void showCustomDialog(String str) {
		Builder builder = new CustomDialog.Builder(this);
		builder.setMessage(R.string.message_reLogin);
		builder.setTitle(R.string.remind);
		builder.setMessage(str);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog1, int which) {

						dialog1.dismiss();

					}
				});
		CustomDialog dia = builder.create();
		dia.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				rbtnPersonLocation.setChecked(true);
			}
		});
		dia.show();
	}

	/**
	 * 判断carid不为空
	 * 
	 * @return
	 */
	private boolean getInfo() {
		try {
			LoginMessage message = LoginMessageUtils.getLoginMessage(this);
			cid = message.getCar().getCid();
			uid = message.getUid();
			key = message.getKey();
			if (!StringUtil.isEmpty(cid)) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 初始化地图设置
	 */
	private void setMapInfo() {
		/* 不显示默认的缩放按键 */
		mMapView.showZoomControls(false);
		mMapSearchUtil = new MapSearchUtil();
		mBaseMapUtil = new BaseMapUtil(mMapView.getMap());
		mNavigationUtil = new NavigationUtil(this);
		mLocationUtil = new LocationUtil(this,
				LocationUtil.SCANSPAN_TYPE_SHORT, locationListener);

		mBaseMapUtil.setMapStatus();
		mBaseMapUtil.setUI();
		mBaseMapUtil.setMyLocationEnable(true, R.drawable.chedongtai_person);

	}

	/**
	 * 定位监听
	 */
	private BDLocationListener locationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (mBaseMapUtil == null) {
				return;
			}
			Log.i("zzqq", "location:" + location.getAddrStr());
			mBaseMapUtil.setMylocationData(location.getLatitude(),
					location.getLongitude(), location.getRadius(),
					location.getDirection());
		}
	};

	/**
	 * 开启导航
	 */
	private void startNavigation() {
		if (selectLatLng == null) {
			return;
		}
		// TODO 修改人的位置
		LatLng latLng = MyMapUtils.getLatLng(this);
		double sLatitude = latLng.latitude;
		double sLongitude = latLng.longitude;

		String sAddress = MyMapUtils.getAddress(this);
		showPdialog();
		mNavigationUtil.startNavigation(sLatitude, sLongitude, sAddress,
				selectLatLng.latitude, selectLatLng.longitude, poiStr);
	}

	/**
	 * 移动到人
	 */
	private void moveToPerson() {
		personLatLng = MyMapUtils.getLatLng(this);
		mBaseMapUtil.zoomTo(BaseMapUtil.DEFAULT_ZOOM_LEVEL);
		mBaseMapUtil.moveTo(personLatLng, true);

		String str = MyMapUtils.getAddress(this);
		str = StringUtil.isEmpty(str) ? " " : str;
		tvTarget.setText("当前位置：");
		tvLocation.setText(str);
	}

	/**
	 * 移动到车
	 */
	private void moveToCar() {
		showPdialog();
		RequestParams params = new RequestParams();
		params.addBodyParameter("cid", cid);
		params.addBodyParameter("uid", uid);
		params.addBodyParameter("key", key);
		httpBiz.httPostData(1000, API.CAR_DYNAMIC, params, this);
	}

	/**
	 * 处理车辆信息
	 */

	@Override
	public void receive(int type, String data) {
		try {
			JSONObject js = new JSONObject(data);
			if ("SUCCESS".equalsIgnoreCase(js.getString("operationState"))) {
				js = js.getJSONObject("data");
				js = js.getJSONObject("data");
				js = js.getJSONObject("body");
				Gson gson = new Gson();
				CarDynamicVO vo = gson.fromJson(js.toString(),
						CarDynamicVO.class);
				carLatLng = new LatLng(StringUtil.getDouble(vo.getLat()),
						StringUtil.getDouble(vo.getLon()));
				if (StringUtil.isEmpty(vo.getStatus())) {
					throw new RuntimeException("jsoo");
				}
				mMapSearchUtil.startReverseGeoCode(carLatLng,
						onGetGeoCoderResultListener);
				mBaseMapUtil.removeAllMarkers();

				selectLatLng = carLatLng;
				// %u7184%u706b “熄火” unicode
				if ("%u7184%u706b".equals(vo.getStatus())) {
					mBaseMapUtil.setMarkerOverlayer(carLatLng,
							R.drawable.chedongtai_carstop);
				} else {
					mBaseMapUtil.setMarkerOverlayer(carLatLng,
							R.drawable.chedongtai_car2x);

				}

				mMapSearchUtil.startRoutePlan(MapSearchUtil.ROUTEPLAN_DRIVE,
						personLatLng, carLatLng, onGetRoutePlanResultListener);
			} else if ("RELOGIN".equalsIgnoreCase(js
					.getString("operationState"))) {
				DialogTool.getInstance(NavigationActivity.this)
						.showConflictDialog();
			} else {
				js = js.getJSONObject("data");
				showToast(js.getString("msg"));
			}
		} catch (Exception e) {
			showToast(getString(R.string.no_result));
			try {
				rbtnPersonLocation.setChecked(true);
			} catch (Exception e1) {
			}
			disMissPDialog();
		}
	}

	/**
	 * 算路监听
	 */
	private OnGetRoutePlanResultListener onGetRoutePlanResultListener = new OnGetRoutePlanResultListener() {

		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult arg0) {

		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult arg0) {

		}

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(NavigationActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
				// result.getSuggestAddrInfo();
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				if (overlay != null) {
					overlay.removeFromMap();
				}
				if (result.getRouteLines().get(0).getWayPoints().size() < 2) {
					showToast("请输入终点");
				} else if (result.getRouteLines().get(0).getWayPoints().size() > 10000) {
					showToast("途经点过多，无法进行路线规划");
				} else {
					mMapView.getMap().clear();
					overlay = new DrivingRouteOverlay(mMapView.getMap());
					overlay.setData(result.getRouteLines().get(0));
					overlay.addToMap();

					overlay.zoomToSpan();
					// overlay.addToMap();
				}
			}
			disMissPDialog();
		}
	};

	/**
	 * 地理位置编译监听
	 */
	private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			String carString = result.getAddress();
			tvTarget.setText("车辆位置：");
			poiStr = carString;
			if (lLayoutBelow.getVisibility() == View.INVISIBLE) {
				lLayoutBelow.setVisibility(View.VISIBLE);
			}
			tvLocation.setText(carString);
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {

		}
	};

	/**
	 * 显示加载dialog
	 */
	private void showPdialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.getInstance(this);
		}
		progressDialog.show();
	}

	/**
	 * 去掉dialog
	 */
	private void disMissPDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * 点击监听
	 */
	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.left_action:
				NavigationActivity.this.finish();
				break;
			case R.id.tv_start_navigation:
				startNavigation();
				break;
			case R.id.cbox_person_location:
				if (lLayoutBelow.getVisibility() == View.VISIBLE) {
					lLayoutBelow.setVisibility(View.INVISIBLE);
				}
				if (overlay != null) {
					overlay.removeFromMap();
					overlay = null;
				}

				moveToPerson();
				break;
			case R.id.cbox_car_location:
				moveToCar();
				break;
			case R.id.right_action:
				setResult(RESULT_OK, null);
				finish();
				break;
			case R.id.ibtn_increse:
				mBaseMapUtil.zoomTo(mBaseMapUtil.getZoom() + 1);
				break;
			case R.id.ibtn_decrese:
				mBaseMapUtil.zoomTo(mBaseMapUtil.getZoom() - 1);
				break;
			default:
				break;
			}

		}

	};

	/**
	 * 选中监听
	 */
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton cButton, boolean isChecked) {
			switch (cButton.getId()) {
			case R.id.cbox_traffic:
				mBaseMapUtil.setTrafficEnable(isChecked);
				if (isChecked) {
					showToast(getString(R.string.TrafficEnabled_open));
				} else {
					showToast(getString(R.string.TrafficEnabled_close));
				}
				break;
			case R.id.cbox_car_location:
				if (isChecked) {
					if (!isLogined) {
						showCustomDialog("登录后才能找到您的车噢！");
						return;
					}
					if (!hasCar) {
						showCustomDialog("没有绑定车辆");
						return;
					}
					moveToCar();
				}
				break;
			case R.id.cbox_person_location:
				if (isChecked) {
					if (lLayoutBelow.getVisibility() == View.VISIBLE) {
						lLayoutBelow.setVisibility(View.INVISIBLE);
					}
					if (overlay != null) {
						overlay.removeFromMap();
						overlay = null;
					}
					moveToPerson();
				}
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 下拉列表子项监听
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View convertView,
				int position, long arg3) {
			showPdialog();

			@SuppressWarnings("unchecked")
			HashMap<String, String> items = (HashMap<String, String>) parent
					.getItemAtPosition(position);
			poiStr = items.get("key");

			if (mapListView.getVisibility() == View.VISIBLE) {
				mapListView.setVisibility(View.INVISIBLE);
				isDataChanged = false;
				searchView.setText(poiStr);
			}

			double lati = StringUtil.getDouble(items.get("latitude"));
			double longi = StringUtil.getDouble(items.get("longitude"));
			selectLatLng = new LatLng(lati, longi);

			mBaseMapUtil.setMarkerOverlayer(selectLatLng,
					R.drawable.weixiu_location2x);
			tvTarget.setText("目的地：");
			tvLocation.setText(poiStr);
			if (lLayoutBelow.getVisibility() == View.INVISIBLE) {
				lLayoutBelow.setVisibility(View.VISIBLE);
			}

			mMapSearchUtil.startRoutePlan(MapSearchUtil.ROUTEPLAN_DRIVE,
					personLatLng, selectLatLng, onGetRoutePlanResultListener);

		}

	};

	/**
	 * 自动完成监听
	 */
	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (StringUtil.isEmpty(s)) {
				return;
			}
			if (!isDataChanged) {
				isDataChanged = true;
				return;
			}
			mMapSearchUtil.startSuggetSearch(
					MyMapUtils.getCity(NavigationActivity.this), s.toString(),
					onGetSuggestionResultListener);
		}
	};

	/**
	 * 按下“完成”接收到数据
	 */
	private void dealAfterDone(List<SuggestionInfo> list) {
		int size = list.size();
		SuggestionInfo info = null;
		for (int i = 0; i < size; i++) {
			info = list.get(i);
			if (info != null && info.pt != null) {
				break;
			}
		}

		if (info == null || info.pt == null) {
			return;
		}
		if (mapListView.getVisibility() == View.VISIBLE) {
			mapListView.setVisibility(View.INVISIBLE);
			isDataChanged = false;
			searchView.setText(poiStr);
		}

		showPdialog();
		double lati = info.pt.latitude;
		double longi = info.pt.longitude;
		selectLatLng = new LatLng(lati, longi);

		mBaseMapUtil.setMarkerOverlayer(selectLatLng,
				R.drawable.weixiu_location2x);
		tvTarget.setText("目的地：");
		tvLocation.setText(poiStr);
		if (lLayoutBelow.getVisibility() == View.INVISIBLE) {
			lLayoutBelow.setVisibility(View.VISIBLE);
		}

		mMapSearchUtil.startRoutePlan(MapSearchUtil.ROUTEPLAN_DRIVE,
				personLatLng, selectLatLng, onGetRoutePlanResultListener);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		progressDialog = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationUtil.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationUtil.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mMapSearchUtil != null) {
			mMapSearchUtil.onDestory();
			mMapSearchUtil = null;
		}
		if (mBaseMapUtil != null) {
			mBaseMapUtil.onDestory();
			mBaseMapUtil = null;
		}
		mLocationUtil.onDestory();
		mLocationUtil = null;
		if (mNavigationUtil != null) {
			mNavigationUtil = null;
		}
		overlay = null;
		searchAdapter = null;
		searchDatas = null;
		mMapView.onDestroy();
		System.gc();
	}

}
