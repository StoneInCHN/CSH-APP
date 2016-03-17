package com.cheweishi.android.fragement;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapDoubleClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.RechargeActivity.MyBroadcastReceiver;
import com.cheweishi.android.adapter.FindcarViewpagerAdapter;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.entity.ParkInfo;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.utils.DisplayUtil;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.BaiduMapView;
import com.cheweishi.android.widget.GalleryViewPager;
import com.cheweishi.android.widget.ScaleView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 找车位地图
 * 
 * @author 大森
 * 
 */
public class FindParkingSpaceMapFragment extends BaseFragment {

	@ViewInject(R.id.findcarportmap_bmapView)
	private MapView mapView;// 百度地图

	@ViewInject(R.id.lukuangxx)
	private CheckBox lukuangxx;// 路况

	@ViewInject(R.id.findcarport_decrese)
	private ImageButton findcarport_decrese;// -

	@ViewInject(R.id.findcarport_increse)
	private ImageButton findcarport_increse;// +

	@ViewInject(R.id.findcar_location_icon)
	private ImageView findcar_location_icon;// 定位

	@ViewInject(R.id.viewpager_relativelayout)
	private RelativeLayout viewpager_relativelayout;// 数据信息显示

	@ViewInject(R.id.linearlayout_scale)
	private LinearLayout linearlayout_scale;// 比例尺

	private BaiduMap mBaiduMap;

	private BaiduMapView mBaiduMapView;

	private ScaleView mScaleView;

	private GalleryViewPager viewPager;

	private View view;

	private FindcarViewpagerAdapter adatper;

	private List<ParkInfo> listmMaps;// 地图数据
	private Bundle bundle;

	private List<Marker> markersList;
	private List<View> lists;
	private List<LatLng> latlngList;

	private List<TextView> listMarkers;
	private List<TextView> listMarkersNo;
	private List<BitmapDescriptor> listBitmapDescriptorsNo;
	private List<BitmapDescriptor> listBitmapDescriptors;
	private BitmapDescriptor bitmapDescriptorsmile = BitmapDescriptorFactory
			.fromResource(R.drawable.zhaochewei_location);
	private static final int NO_DATE = -1;
	private LatLng latLng;// 经纬度
	private FindParkBroadcastReceiver broad;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_parking_space_map, null);
		ViewUtils.inject(this, view);
		initBaiduview();
		moveToMelocation();
		if (!StringUtil.isEmpty(getArguments())) {
			listmMaps = getArguments().getParcelableArrayList("data");
			latLng = new LatLng(getArguments().getDouble("lat"),getArguments().getDouble("lng"));
		}
		
		initView();
		return view;
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
//		Log.i("result", "=============map=onstart======" + listmMaps.size());
	}

	@Override
	public void onResume() {
		super.onResume();
		// bundle = getArguments().getBundle("data");
		// listmMaps = (List<ParkInfo>) bundle.get("data");
		Log.i("result", "=============map=onResume======");
		// 注册刷新广播
		if (broad == null) {
			broad = new FindParkBroadcastReceiver();
		}
		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		mContext.registerReceiver(broad, intentFilter);
		// initView();
		// moveTolocation(latLng);
	}

	/**
	 * 初始化数据
	 */
	private void initView() {
		// latLng = mContext.getL
		// if (!StringUtil.isEmpty(getArguments())) {
		// bundle = getArguments();
		// listmMaps = (List<ParkInfo>) bundle.get("data");
		if (!StringUtil.isEmpty(listmMaps) && listmMaps.size() > 0) {
			viewpager_relativelayout.setVisibility(View.VISIBLE);
			analList(listmMaps);
		} else {
			viewpager_relativelayout.setVisibility(View.INVISIBLE);
			// }
		}
	}

	private void analList(List<ParkInfo> listmMaps) {
		settext();
		init();
		for (int i = 0; i < listmMaps.size(); i++) {
			double lat = StringUtil.getDouble(listmMaps.get(i).getLatitude()
					.toString());
			double lng = StringUtil.getDouble(listmMaps.get(i).getLongitude()
					.toString());
			LatLng latlng = new LatLng(lat, lng);
			moveLatLng(latlng, i);
			latlngList.add(latlng);
		}
		// 把第一个marker置顶
		markersList.get(0).setToTop();
	}

	private void moveLatLng(LatLng latlng, int i) {

		OverlayOptions option = null;
		if (i == 0) {
			option = new MarkerOptions().position(latlng).icon(
					listBitmapDescriptors.get(i));
		} else if (i < 10) {
			option = new MarkerOptions().position(latlng).icon(
					listBitmapDescriptorsNo.get(i));
		} else if (i > 9 && i < 20) {
			option = new MarkerOptions().position(latlng).icon(
					bitmapDescriptorsmile);
		}
		Marker marker = (Marker) mBaiduMap.addOverlay(option);
		marker.setTitle(i + "");
		markersList.add(marker);
	}

	private void settext() {
		initTextview();
	}

	private void initTextview() {
		if (!StringUtil.isEmpty(lists)) {
			int count = 10;
			if (listmMaps.size() <= 10) {
				count = listmMaps.size();
			}
			for (int i = 0; i < count; i++) {
				((TextView) lists.get(i).findViewById(
						R.id.findcarportviewpager_map_item_address))
						.setText(listmMaps.get(i).getAddr());
				((TextView) lists.get(i).findViewById(
						R.id.findcarportviewpager_map_item_distance))
						.setText(listmMaps.get(i).getDistance() + "米");

				if (listmMaps.get(i).getLeftNum() == NO_DATE) {
					((TextView) lists.get(i).findViewById(
							R.id.findcarportviewpager_map_item_surplusCarport))
							.setText("--");
				} else {
					((TextView) lists.get(i).findViewById(
							R.id.findcarportviewpager_map_item_surplusCarport))
							.setText(listmMaps.get(i).getLeftNum() + "");
				}
				((TextView) lists.get(i).findViewById(
						R.id.findcarportviewpager_map_item_costTextView))
						.setText(listmMaps.get(i).getPrice());
				((TextView) lists.get(i).findViewById(
						R.id.findcarport_viewpager_tv_notive))
						.setOnClickListener(new NotiveOnclickListener(i));
				((LinearLayout) lists.get(i)
						.findViewById(R.id.tishijulidaohang))
						.setOnClickListener(new TiaozhuanOnclickListener(i));
				((TextView) lists.get(i).findViewById(
						R.id.findcarportviewpager_map_item_name))
						.setText(listmMaps.get(i).getName());
				// if (listmMaps.get(i).getLeftNum() == NO_DATE) {
				// listMarkers.get(i).setText("--");
				// listMarkersNo.get(i).setText("--");
				// } else {
				// listMarkers.get(i).setText(
				// listmMaps.get(i).getLeftNum() + "");
				// listMarkersNo.get(i).setText(
				// listmMaps.get(i).getLeftNum() + "");
				// }
				listBitmapDescriptorsNo.add(BitmapDescriptorFactory
						.fromView(listMarkersNo.get(i)));
				listBitmapDescriptors.add(BitmapDescriptorFactory
						.fromView(listMarkers.get(i)));
			}
		}
	}

	class TiaozhuanOnclickListener implements OnClickListener {

		int position = 0;

		public TiaozhuanOnclickListener(int i) {
			position = i;
		}

		@Override
		public void onClick(View arg0) {
			// Intent intent = new Intent(FindcarActivity.this,
			// ParkDetailsActivity.class);
			// intent.putExtra("parkInfo", listmMaps.get(position));
			// FindcarActivity.this.startActivity(intent);
		}

	}

	class NotiveOnclickListener implements OnClickListener {

		int index = 0;

		public NotiveOnclickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View arg0) {
			double latitude = StringUtil.getDouble(listmMaps.get(index)
					.getLatitude());
			double longitude = StringUtil.getDouble(listmMaps.get(index)
					.getLongitude());
			String addr = listmMaps.get(index).getAddr();
			mBaiduMapView.baiduNavigation(MyMapUtils.getLatitude(mContext),
					MyMapUtils.getLongitude(mContext),
					MyMapUtils.getAddress(mContext), latitude, longitude, addr);
		}
	}

	private void init() {
		// left_action.setOnClickListener(listener);
		refreshScaleAndZoomControl();
		viewPager = new GalleryViewPager(mContext);
		viewPager.setClipChildren(false);
		adatper = new FindcarViewpagerAdapter(mContext, lists);
		viewPager.setAdapter(adatper);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setPageMargin(DisplayUtil.dip2px(mContext, 10));
		viewPager.setCurrentItem(100);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
		lp.width = ScreenTools.getScreentWidth((Activity) mContext) * 4 / 5;
		viewPager.setLayoutParams(lp);
		viewpager_relativelayout.addView(viewPager);
		initListener();
	}

	private void initListener() {
		// listTextView.setOnClickListener(listener);
		lukuangxx.setOnCheckedChangeListener(onCheckedChangeListener);
		mBaiduMap.setOnMapDoubleClickListener(onMapDoubleClickListener);
		findcarport_increse.setOnClickListener(listener);
		findcarport_decrese.setOnClickListener(listener);
		mBaiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
		mBaiduMap.setOnMarkerClickListener(onMarkerClickListener);
		findcar_location_icon.setOnClickListener(listener);
		// right_action.setOnClickListener(listener)
		// title.setOnClickListener(listener);
		viewPager.setOnPageChangeListener(pageChangeListener);
		// right_action.setOnClickListener(listener);

	}

	/****
	 * 修改比例尺的显示长度和数字
	 */
	private void refreshScaleAndZoomControl() {
		mScaleView.refreshScaleView(Math.round(mBaiduMap.getMapStatus().zoom));
	}

	/**
	 * 初始化地图
	 */
	private void initBaiduview() {
		mapView.showZoomControls(false);
		mBaiduMapView = new BaiduMapView(mapView, mContext);
		mBaiduMap = mapView.getMap();
		mScaleView = new ScaleView(mContext);
		mScaleView.setMapView(mBaiduMap);
		linearlayout_scale.addView(mScaleView);
		initbaidu();
	}

	private void initbaidu() {
		listMarkers = new ArrayList<TextView>();
		listMarkersNo = new ArrayList<TextView>();
		listBitmapDescriptorsNo = new ArrayList<BitmapDescriptor>();
		listBitmapDescriptors = new ArrayList<BitmapDescriptor>();
		for (int i = 0; i < 10; i++) {
			listMarkers.add((TextView) LayoutInflater.from(mContext)
					.inflate(R.layout.marker_bitmap, null)
					.findViewById(R.id.marker_tv_bitmap));
			listMarkersNo.add((TextView) LayoutInflater.from(mContext)
					.inflate(R.layout.marker_tv_bitmap_noxuanzhong, null)
					.findViewById(R.id.marker_tv_bitmap_no));
		}
		lists = new ArrayList<View>();
		initList();
		latlngList = new ArrayList<LatLng>();
		markersList = new ArrayList<Marker>();
	};

	private void initList() {
		for (int i = 0; i < 10; i++) {
			lists.add(LayoutInflater.from(mContext).inflate(
					R.layout.findcar_viewpager_item, null));
		}

	}

	private void moveToMelocation() {
		latLng = MyMapUtils.getLatLng(mContext);
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMapView.updateOritentation(latLng, R.drawable.chedongtai_person,
				20, 20);
		mBaiduMapView.moveLatLng(latLng, 16);
	};

	private void moveTolocation(LatLng latLng) {
		mBaiduMapView.setMarker(latLng, R.drawable.zhaochewei_weizhi);
		mBaiduMapView.moveLatLng(latLng, 16);
	};

	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if (arg1) {
				mBaiduMap.setTrafficEnabled(true);
				showToast(getString(R.string.TrafficEnabled_open));
			} else {
				mBaiduMap.setTrafficEnabled(false);
				showToast(getString(R.string.TrafficEnabled_close));
			}
		}
	};

	private OnMapDoubleClickListener onMapDoubleClickListener = new OnMapDoubleClickListener() {

		@Override
		public void onMapDoubleClick(LatLng arg0) {
			refreshScaleAndZoomControl();
		}
	};

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.findcarport_increse:
				if (mBaiduMap.getMapStatus().zoom == mBaiduMap
						.getMaxZoomLevel()) {
					findcarport_increse
							.setImageResource(R.drawable.zhaochewei_fangda_max_up);
				} else {
					if (mBaiduMap.getMapStatus().zoom == mBaiduMap
							.getMinZoomLevel()) {
						findcarport_decrese
								.setImageResource(R.drawable.ibtn_map_decrese);
					}
					mBaiduMapView
							.zoomTo((int) mBaiduMap.getMapStatus().zoom + 1);
					refreshScaleAndZoomControl();
				}
				break;
			case R.id.findcarport_decrese:
				if (mBaiduMap.getMapStatus().zoom == mBaiduMap
						.getMinZoomLevel()) {
					findcarport_decrese
							.setImageResource(R.drawable.zhaochewei_fangda_max_down);
				} else {
					if (mBaiduMap.getMapStatus().zoom == mBaiduMap
							.getMaxZoomLevel()) {
						findcarport_increse
								.setImageResource(R.drawable.ibtn_map_increse);
					}
					mBaiduMapView
							.zoomTo((int) mBaiduMap.getMapStatus().zoom - 1);
					refreshScaleAndZoomControl();
				}
				break;
			case R.id.findcar_location_icon:
				mBaiduMapView.moveLatLng(MyMapUtils.getLatLng(mContext));
				break;
			default:
				break;
			}
		}
	};

	private OnMapStatusChangeListener onMapStatusChangeListener = new OnMapStatusChangeListener() {

		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {

		}

		@Override
		public void onMapStatusChangeFinish(MapStatus arg0) {

		}

		@Override
		public void onMapStatusChange(MapStatus arg0) {
			refreshScaleAndZoomControl();
		}
	};

	private OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			int i = Integer.parseInt(marker.getTitle());
			if (i < 10) {
				if (viewPager.getVisibility() == View.GONE) {
					if (view != null) {
						view.setVisibility(View.GONE);
					}
					viewPager.setVisibility(View.VISIBLE);
				}
				if (i < 6) {
					viewPager.setCurrentItem(100 + i, true);
				} else {
					viewPager.setCurrentItem(100 - (10 - i), true);
				}

			} else {
				for (int j = 0; j < markersList.size(); j++) {
					if (j < 10) {
						markersList.get(j).setIcon(
								listBitmapDescriptorsNo.get(j));
					} else {
						markersList.get(j).setIcon(bitmapDescriptorsmile);
					}
				}
				Bitmap bitmap = android.graphics.BitmapFactory.decodeResource(
						getResources(), R.drawable.zhaochewei_location_click);
				BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
						.fromBitmap(bitmap);
				marker.setIcon(bitmapDescriptor);
				if (view == null) {
					view = LayoutInflater.from(mContext).inflate(
							R.layout.findcar_viewpager_item, null);
				} else {
					viewpager_relativelayout.removeView(view);
					if (view.getVisibility() == View.GONE) {
						view.setVisibility(View.VISIBLE);
					}
				}
				TextView address = (TextView) view
						.findViewById(R.id.findcarportviewpager_map_item_address);
				TextView distance = (TextView) view
						.findViewById(R.id.findcarportviewpager_map_item_distance);
				TextView surplusCarport = (TextView) view
						.findViewById(R.id.findcarportviewpager_map_item_surplusCarport);
				TextView costTextView = (TextView) view
						.findViewById(R.id.findcarportviewpager_map_item_costTextView);
				TextView notive = (TextView) view
						.findViewById(R.id.findcarport_viewpager_tv_notive);
				TextView name = (TextView) view
						.findViewById(R.id.findcarportviewpager_map_item_name);
				LinearLayout tishijulidaohang = (LinearLayout) view
						.findViewById(R.id.tishijulidaohang);

				address.setText(listmMaps.get(i).getAddr());
				distance.setText(listmMaps.get(i).getDistance() + "米");
				surplusCarport.setText(listmMaps.get(i).getLeftNum() + "");
				costTextView.setText(listmMaps.get(i).getPrice() + "");
				name.setText(listmMaps.get(i).getName());
				notive.setOnClickListener(new NotiveOnclickListener(i));
				tishijulidaohang
						.setOnClickListener(new TiaozhuanOnclickListener(i));
				view.setPadding(30, 0, 30, 20);
				viewpager_relativelayout.addView(view);

				viewPager.setVisibility(View.GONE);

			}
			return true;
		}
	};

	OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {

			String string = String.valueOf(arg0);
			String str = string.substring(string.length() - 1);
			for (int i = 0; i < markersList.size(); i++) {
				if (i < 10) {
					markersList.get(i).setIcon(listBitmapDescriptorsNo.get(i));
				} else {
					markersList.get(i).setIcon(bitmapDescriptorsmile);
				}
			}
			markersList.get(Integer.parseInt(str)).setToTop();
			markersList.get(Integer.parseInt(str)).setIcon(
					listBitmapDescriptors.get(Integer.parseInt(str)));
			mBaiduMapView.moveLatLng(latlngList.get(Integer.parseInt(str)));
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if (viewpager_relativelayout != null) {
				viewpager_relativelayout.invalidate();
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	public class FindParkBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.FIND_PARK_REFRESH, true)) {
				Log.i("result", "=================找车位更新===============");
				listmMaps = intent.getParcelableArrayListExtra("data");
				latLng = new LatLng(intent.getDoubleExtra("lat", 0),
						intent.getDoubleExtra("lng", 0));
				moveTolocation(latLng);
				initView();
			}
		}
	}

}
