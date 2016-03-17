package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.FindcarViewpagerAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.dialog.WashCarDateDialog;
import com.cheweishi.android.dialog.WashCarDateDialog.Builder;
import com.cheweishi.android.entity.WashcarVO;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.utils.CustomProgressDialog;
import com.cheweishi.android.utils.DisplayUtil;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.utils.mapUtils.BaseMapUtil;
import com.cheweishi.android.utils.mapUtils.LocationUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnCompoundButtonCheckedChange;

/**
 * 保养
 * 
 * @author XMh
 * 
 */
@ContentView(R.layout.activity_washcar)
public class AppointmentMaintainActivity extends BaseActivity {
	public static final int INDEX_FROM_LIST = 1001;
	@ViewInject(R.id.mapview)
	private MapView mMapView;
	@ViewInject(R.id.container)
	private RelativeLayout container;
	@ViewInject(R.id.title)
	private TextView tvTitle;
	@ViewInject(R.id.left_action)
	private Button btnLeft;
	private ViewPager mViewPager;
	@ViewInject(R.id.img_location)
	private ImageView imgLocation;

	private BaseMapUtil mBaseMapUtil;
	private LocationUtil mLocationUtil;
	private List<View> viewLists;
	private FindcarViewpagerAdapter viewPagerAdapter;
	private List<WashcarVO> washcarList;
	private WashcarVO selectVO;

	private List<LatLng> positionList;
	private HashMap<LatLng, Marker> markerMap;
	private LatLng personLatLng;
	private WashCarDateDialog dateDialog;
	private boolean isFirst;
	// private int count = 0;// 已预约的次数
	private CustomDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* 1 */
		ViewUtils.inject(this);
		httpBiz = new HttpBiz(this);

		/* 2 */
		initView();

		/* 3 */
		initMap();

		/* 4 */
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		int index = intent.getIntExtra("index", 0);
		if (index == INDEX_FROM_LIST) {
			ProgrosDialog.openDialog(this);
			washcarList = intent.getParcelableArrayListExtra("list");
			handler.sendEmptyMessage(0);
			return;
		}
//		Log.i("result", "==initData==" + loginMessage.getCar().getCid());
		if (!isLogined()) {
			startActivity(new Intent(AppointmentMaintainActivity.this, LoginActivity.class));
			this.finish();
			return;
		}else if(StringUtil.isEmpty(loginMessage.getCar())
				|| StringUtil.isEmpty(loginMessage.getCar().getPlate()) || StringUtil.isEmpty(loginMessage.getCar().getCid())){
			showToast("您还未绑定车辆");
			startActivity(new Intent(AppointmentMaintainActivity.this, CarManagerActivity.class));
			this.finish();
			return;
		}
		ProgrosDialog.openDialog(this);
		RequestParams mRequestParams = new RequestParams();
		mRequestParams.addBodyParameter("uid", loginMessage.getUid());
		mRequestParams.addBodyParameter("key", loginMessage.getKey());
		mRequestParams.addBodyParameter("cid", loginMessage.getCar().getCid());
		mRequestParams.addBodyParameter("lat", MyMapUtils.getLatitude(this)
				+ "");
		mRequestParams.addBodyParameter("lon", MyMapUtils.getLongitude(this)
				+ "");
		Log.i("zqtest", "uid:" + loginMessage.getUid() + "--key--:"
				+ loginMessage.getKey());
		httpBiz.httPostData(1001, API.WASHCHAR_LIST_DATA, mRequestParams, this);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			positionList = new ArrayList<LatLng>();

			if (!StringUtil.isEmpty(washcarList)) {
				for (WashcarVO vo : washcarList) {
					LatLng latLng = new LatLng(
							StringUtil.getDouble(vo.getLat()),
							StringUtil.getDouble(vo.getLon()));
					positionList.add(latLng);
				}
				fillInViewpager();
				ProgrosDialog.closeProgrosDialog();
			}
		}
	};

	/**
	 * 
	 */
	private void initMap() {
		mMapView.showZoomControls(false);

		mLocationUtil = new LocationUtil(this,
				LocationUtil.SCANSPAN_TYPE_SHORT, locationListener);

		mBaseMapUtil = new BaseMapUtil(mMapView.getMap());
		mBaseMapUtil.setUI();
		mBaseMapUtil.setMapStatus();
		mBaseMapUtil.setMyLocationEnable(true, R.drawable.chedongtai_person);
		mBaseMapUtil.setOnMapLoadedCallBack(mapLoadedCallBack);
	}

	private OnMapLoadedCallback mapLoadedCallBack = new OnMapLoadedCallback() {

		@Override
		public void onMapLoaded() {
			int headHeight = mMapView.getTop();
			int x = imgLocation.getRight() + 4;
			int y = imgLocation.getBottom()
					- mMapView.getChildAt(1).getHeight() - headHeight;
			mMapView.setScaleControlPosition(new Point(x, y));
		}
	};

	/**
	 * baidu localtion listener
	 */
	BDLocationListener locationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (mBaseMapUtil == null) {
				return;
			}
			if (!isFirst) {
				isFirst = true;
				mBaseMapUtil.moveTo(
						new LatLng(location.getLatitude(), location
								.getLongitude()), false);
			}
			personLatLng = new LatLng(location.getLatitude(),
					location.getLongitude());
			mBaseMapUtil.setMylocationData(location.getLatitude(),
					location.getLongitude(), location.getRadius(),
					location.getRadius());
		}
	};

	private void initView() {
		tvTitle.setText("预约保养");
		btnLeft.setText("返回");
	}

	OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		private int prePosition = 0;

		@Override
		public void onPageSelected(int position) {
			int size = positionList.size();
			position = position % size;

			/* 相同不变 */
			if (prePosition == position) {
				return;
			}
			LatLng latLng = positionList.get(position);
			LatLng preLatLng = positionList.get(prePosition);

			changeMarkers(preLatLng, latLng);
			prePosition = position;
			mBaseMapUtil.moveTo(latLng, true);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if (container != null) {
				container.invalidate();
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	/**
	 * 解析子控件
	 * 
	 * @param inflater
	 */
	private void inflateChildView() {
		LayoutInflater inflater = LayoutInflater.from(this);
		viewLists = new ArrayList<View>();
		int size = washcarList.size();
		for (int i = 0; i < size; i++) {
			WashcarVO vo = washcarList.get(i);

			View view = inflater.inflate(R.layout.item_viewpager_washcar, null);
			view.setId(5000 + i);
			LinearLayout layoutOrder = (LinearLayout) view
					.findViewById(R.id.findcarport_linearlayout_notive);
			layoutOrder.setId(6000 + i);
			TextView tvName = (TextView) view
					.findViewById(R.id.findcarportviewpager_map_item_name);
			tvName.setText(vo.getName());

			TextView tvAddress = (TextView) view
					.findViewById(R.id.findcarportviewpager_map_item_address);
			tvAddress.setText(vo.getAddress());

			TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
			tvTime.setText(vo.getDate());

			view.setOnClickListener(clickListener);
			layoutOrder.setOnClickListener(clickListener);
			viewLists.add(view);
		}
	}

	/**
	 * 
	 */
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			/* click parent */
			if (5000 <= id && id < 6000) {
				selectVO = washcarList.get(id - 5000);
				clickParentView();
			}

			/* click child */
			if (6000 <= id && id < 7000) {
				selectVO = washcarList.get(id - 6000);
				// clickChildView();
				clickParentView();
			}
		}

	};

	/**
	 * click viewpager's item's childview
	 */
	private void clickChildView() {
		if (Constant.WASHCAR_COUNT == 4) {
			showNoDialog();
		} else {
			showDateDiaglog();
		}
	}

	/**
	 * 不能进行预约提示
	 */
	private void showNoDialog() {
		builder = new CustomDialog.Builder(this);
		builder.setMessage("您本月免费次数已用完。亲~下个月再来吧!");
		builder.setTitle("提示");
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/**
	 * click viewpager's item
	 */
	private void clickParentView() {
		Intent intent = new Intent(this, WashcarDetailsActivity.class);
		intent.putExtra("index", WashcarDetailsActivity.INDEX_DETAIL);
		intent.putExtra("return", WashcarDetailsActivity.INDEX_FROM_MAP);
		intent.putParcelableArrayListExtra("list",
				(ArrayList<? extends Parcelable>) washcarList);
		intent.putExtra("vo", selectVO);
		// intent.putExtra("count", Constant.WASHCAR_COUNT);
		startActivity(intent);
		// finish();
	}

	@OnClick(R.id.left_action)
	public void finishActivity(View view) {
		onBackPressed();
	}

	@OnCompoundButtonCheckedChange(R.id.cbox_list)
	public void turnToList(View v, boolean isChecked) {
		Intent intent = new Intent(this, MaintainListActivity.class);
		intent.putParcelableArrayListExtra("list",
				(ArrayList<? extends Parcelable>) washcarList);
		intent.putExtra("test", "hello");
		// intent.putExtra("count", Constant.WASHCAR_COUNT);
		startActivity(intent);
		finish();
	}

	@OnCompoundButtonCheckedChange(R.id.cbox_traffic)
	public void controlTraffic(CompoundButton v, boolean isChecked) {
		mBaseMapUtil.setTrafficEnable(isChecked);
		if (isChecked) {
			showToast(getString(R.string.TrafficEnabled_open));
		} else {
			showToast(getString(R.string.TrafficEnabled_close));
		}
	}

	@OnClick(R.id.ibtn_increse)
	public void mapIncrese(View v) {
		mBaseMapUtil.zoomTo(mBaseMapUtil.getZoom() + 1);
	}

	@OnClick(R.id.ibtn_decrese)
	public void mapDecrese(View v) {
		mBaseMapUtil.zoomTo(mBaseMapUtil.getZoom() - 1);
	}

	@OnClick(R.id.img_location)
	public void moveToPerson(View v) {
		mBaseMapUtil.moveTo(personLatLng, true);
	}

	@OnClick(R.id.tvHistory)
	public void btnClick(View v) {

		Intent intent = new Intent(this, WashcarHistoryActivity.class);
		// intent.putExtra("count", Constant.WASHCAR_COUNT);
		startActivity(intent);
	}

	@Override
	public void receive(int type, String data) {
		Log.i("zqtest", data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 1001:
			doAboutWashcar(data);
			break;
		case 1002:
			doAboutOrder(data);
			break;
		default:
			break;
		}
	}

	private void doAboutOrder(String data) {
		try {
			JSONObject json = new JSONObject(data);
			if ("SUCCESS".equals(json.get("operationState"))) {
				json = json.getJSONObject("data");
				json.getString("status");

				Intent intent = new Intent(this, MaintainDetilsActivity.class);
				intent.putExtra("index",
						WashcarDetailsActivity.INDEX_ORDER_DETAIL);
				selectVO.setUno(json.getString("uno"));
				selectVO.setTime(json.getString("time"));
				selectVO.setCarNumber(json.getString("plate"));
				intent.putExtra("vo", selectVO);
				startActivity(intent);
				this.finish();
			} else if ("RELOGIN".equalsIgnoreCase(json
					.getString("operationState"))) {
				DialogTool.getInstance(this).showConflictDialog();
			} else {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析保养列表数据
	 * 
	 * @param data
	 */
	private void doAboutWashcar(String data) {
		try {
			JSONObject json = new JSONObject(data);
			if ("SUCCESS".equals(json.get("operationState"))) {
				json = json.getJSONObject("data");

				Constant.WASHCAR_COUNT = Integer.parseInt(json
						.optString("count"));

				JSONArray arr = json.getJSONArray("data");
				washcarList = new ArrayList<WashcarVO>();
				positionList = new ArrayList<LatLng>();
				for (int i = 0; i < arr.length(); i++) {
					WashcarVO vo = new WashcarVO();
					json = arr.getJSONObject(i);
					vo = new WashcarVO();
					vo.setCwId(json.getString("cw_id"));
					vo.setDes(json.getString("des"));
					vo.setLon(json.getString("lon"));
					vo.setAddress(json.getString("address"));
					vo.setTel(json.getString("tel"));
					vo.setName(json.getString("name"));
					vo.setMile(json.getString("mile") + "米");
					vo.setPic(json.getString("pic"));
					vo.setLat(json.getString("lat"));
					vo.setDate(json.getString("date"));
					washcarList.add(vo);

					double lat = StringUtil.getDouble(json.getString("lat"));
					double lng = StringUtil.getDouble(json.getString("lon"));

					positionList.add(new LatLng(lat, lng));
				}

				/* 填充地图和adpter */
				if (!StringUtil.isEmpty(positionList)) {
					fillInViewpager();
				}
				
			} else if ("RELOGIN".equalsIgnoreCase(json
					.getString("operationState"))) {
				DialogTool.getInstance(this).showConflictDialog();
			} else {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 填充地图和adapter
	 */
	private void fillInViewpager() {
		/* 解析子控件 */
		inflateChildView();

		mViewPager = new ViewPager(this);
		mViewPager.setClipChildren(false);
		mViewPager.setOnPageChangeListener(pageChangeListener);
		viewPagerAdapter = new FindcarViewpagerAdapter(this, viewLists);

		mViewPager.setAdapter(viewPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setPageMargin(DisplayUtil.dip2px(this, 10));
		mViewPager.setCurrentItem(100);

		LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
		params.width = ScreenTools.getScreentWidth(this) * 9 / 10;
		mViewPager.setLayoutParams(params);

		container.addView(mViewPager);
		/* 第一个洗车店 */
		LatLng latLng = null;
		markerMap = new HashMap<LatLng, Marker>();
		int size = positionList.size();
		for (int i = size - 1; i >= 0; i--) {
			Marker marker = null;
			latLng = positionList.get(i);
			if (i == 0 && !StringUtil.isEmpty(latLng)) {
				marker = mBaseMapUtil.setMarkerOverlayer(latLng,
						R.drawable.xiche_dian_click, i + "");
			} else if(!StringUtil.isEmpty(latLng)){
				marker = mBaseMapUtil.setMarkerOverlayer(latLng,
						R.drawable.xiche_dian, i + "");
			}

			markerMap.put(latLng, marker);
		}
		mBaseMapUtil.setMarkerListener(markerClickListener);
	}

	private void changeMarkers(LatLng pre, LatLng now) {
		markerMap.get(pre).setIcon(
				BitmapDescriptorFactory.fromResource(R.drawable.xiche_dian));
		Marker marker = markerMap.get(now);
		marker.setIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.xiche_dian_click));

		marker.setToTop();
	}

	OnMarkerClickListener markerClickListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			int position = StringUtil.getInt(marker.getTitle());
			if (position > 9) {
				mViewPager.setCurrentItem(position + 80);
			} else {
				mViewPager.setCurrentItem(position + 100);
			}
			return true;
		}
	};

	private void showDateDiaglog() {
		if (dateDialog == null) {
			final Builder builder = new WashCarDateDialog.Builder(this);
			builder.setPositiveButton("",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String date = builder.getSelectPosition();
							String selectedDate = date.substring(1,
									date.length() - 1);
							orderTheShop(selectedDate);
							dateDialog.dismiss();
						}
					});
			builder.setNegativeButton("",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dateDialog.dismiss();

						}
					});
			builder.setDate(Calendar.getInstance().getTime());

			dateDialog = builder.create();
			builder.setCount("本月剩余次数为" + (4 - Constant.WASHCAR_COUNT) + "次");
		}
		if (!dateDialog.isShowing()) {
			dateDialog.show();
		}
	}

	/**
	 * 预定保养
	 * 
	 * @param selectedDate
	 */
	private void orderTheShop(String selectedDate) {
		RequestParams mRequestParams = new RequestParams();
		if (!isLogined()) {
			return;
		}
		ProgrosDialog.openDialog(this);
		mRequestParams.addBodyParameter("uid", loginMessage.getUid());
		mRequestParams.addBodyParameter("key", loginMessage.getKey());
		mRequestParams.addBodyParameter("cid", loginMessage.getCar().getCid());

		mRequestParams.addBodyParameter("cw_id", selectVO.getCwId());
		mRequestParams.addBodyParameter("time", selectedDate);
		httpBiz.httPostData(1002, API.WASHCAR_ORDER_SHOP, mRequestParams, this);
	}

	private CustomProgressDialog progressDialog;

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
		if (mBaseMapUtil != null) {
			mBaseMapUtil.onDestory();
			mBaseMapUtil = null;
		}
		mLocationUtil.onDestory();
		mLocationUtil = null;

		viewLists = null;
	}
}
