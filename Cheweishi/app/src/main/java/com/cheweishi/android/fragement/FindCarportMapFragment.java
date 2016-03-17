package com.cheweishi.android.fragement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.JSONCallback;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.MapMenssageDialog;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.Car;
import com.cheweishi.android.entity.LatlngBean;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.BaiduMapView;
import com.lidroid.xutils.http.RequestParams;

public class FindCarportMapFragment extends BaseFragment implements
		JSONCallback {

	private static final int FINDCAR_CODE = 2001;
	private static final int FINDCARPORT_CODE = 2002;
	private MapView mMapView;
	private LatlngBean latlngBean;
	private List<Marker> markerList;
	private TextView mPortAddress;
	private boolean isFirst = true;
	private BaiduMapView mBaiduMapView;
	private LoginMessage loginMessage;
	private TextView mNameTextView;
	private BaiduMap mBaiduMap;
	private boolean isDraw = true;
	private double lat;
	private double lng;
	private GeoCoder mSearch = null;
	private GeoCoder mCoder;
	private List<Map<String, String>> listmMaps;
	private List<LatLng> list = new ArrayList<LatLng>();
	private RadioButton mpersonButton;
	private RadioButton mCaRadioButton;
	private String address;
	private LinearLayout mNetiveLayout;
	private SharedPreferences sharedPreferences;
	private SharedPreferences sharedPreferences2;
	private SharedPreferences sharedPreferencesis;
	private BitmapDescriptor bitmap = BitmapDescriptorFactory
			.fromResource(R.drawable.chewei_location);
	private TextView mfindcarportlistview_map_item_distance;
	private LinearLayout mTishijulikuang;
	private LinearLayout findcarport_map_linearlayout_tishikuang;
	private int isF = 0;
	private CheckBox checkBox;
	private ImageButton mGass_decrese;
	private ImageButton mGass_increse;
	private RadioGroup mRadioGroup;
	private int login_type = 0;

	private boolean loginaty = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		init();

	}

	private void init() {
		loginMessage = LoginMessageUtils.getLoginMessage(mContext);
		markerList = new ArrayList<Marker>();
		latlngBean = new LatlngBean();
		mSearch = GeoCoder.newInstance();
		mCoder = GeoCoder.newInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_findcarport_map, null);
		init(view);
		return view;
	}

//	private GetLocationListener getLocationListener = new GetLocationListener() {
//		@Override
//		public void getLoation(BDLocation location) {
//			if (location == null || mMapView == null || isDraw)
//				return;
//			double lati = location.getLatitude();
//			double longi = location.getLongitude();
//			new MyLocationData.Builder().accuracy(location.getRadius())
//					.direction(location.getDirection()).latitude(lati)
//					.longitude(longi).build();
//		}
//	};

	private void init(View view) {
		initSharedPreference();
		initView(view);
		initBaiduMap();
		initListener();
		isCarAndperson();
	}

	private void initBaiduMap() {
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(false);
		mBaiduMap = mMapView.getMap();
		mBaiduMapView = new BaiduMapView(mMapView, mContext);
	}

	private void isCarAndperson() {
		System.out.println("------------1---------"
				+ sharedPreferencesis.getBoolean("isdex", false));
		if (sharedPreferencesis.getBoolean("isdex", false)) {
			System.out.println("---------2------------"
					+ sharedPreferencesis.getBoolean("isdex", false));
			sharedPreferences.edit().putBoolean("isDraw", isDraw).commit();

			System.out.println("---------11------------"
					+ sharedPreferences2.getBoolean("isdraw", false));
			if (sharedPreferences2.getBoolean("isdraw", false)) {
				mCaRadioButton.setChecked(true);
			} else {
				mpersonButton.setChecked(true);
				isDraw = false;
				sharedPreferences.edit().putBoolean("isDraw", isDraw).commit();
			}
		} else {
			System.out.println("----------3-----------"
					+ sharedPreferencesis.getBoolean("isdex", false));
			sharedPreferences.edit().putBoolean("isDraw", isDraw).commit();
			mCaRadioButton.setChecked(true);
		}
		sharedPreferencesis.edit().putBoolean("isdex", true).commit();
	}

	private void initSharedPreference() {
		sharedPreferencesis = mContext.getSharedPreferences("isindex",
				Context.MODE_PRIVATE);
		sharedPreferences = mContext.getSharedPreferences("isDraw",
				Context.MODE_PRIVATE);
		sharedPreferences2 = mContext.getSharedPreferences("isdraw",
				Context.MODE_PRIVATE);
	}

	private void initView(View view) {
		mRadioGroup = (RadioGroup) view.findViewById(R.id.findcarport_rgroup);
		mGass_decrese = (ImageButton) view
				.findViewById(R.id.findcarport_decrese);
		mGass_increse = (ImageButton) view
				.findViewById(R.id.findcarport_increse);
		checkBox = (CheckBox) view.findViewById(R.id.findcarport_cbox_traffic);

		findcarport_map_linearlayout_tishikuang = (LinearLayout) view
				.findViewById(R.id.findcarport_map_linearlayout_tishikuang);
		mTishijulikuang = (LinearLayout) view
				.findViewById(R.id.tishijulidaohang);
		mfindcarportlistview_map_item_distance = (TextView) view
				.findViewById(R.id.findcarportlistview_map_item_distance);
		mpersonButton = (RadioButton) view
				.findViewById(R.id.findcarport_cbox_person_location);
		mCaRadioButton = (RadioButton) view
				.findViewById(R.id.findcarport_cbox_car_location);
		mNameTextView = (TextView) view
				.findViewById(R.id.findcarportlistview_map_item_name);
		mNetiveLayout = (LinearLayout) view
				.findViewById(R.id.findcarport_linearlayout_notive);
		mPortAddress = (TextView) view
				.findViewById(R.id.findcarportlistview_map_item_address);
		mMapView = (MapView) view.findViewById(R.id.findcarportmap_bmapView);
	}

	private android.widget.CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new android.widget.CompoundButton.OnCheckedChangeListener() {

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

	private OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {

			for (int i = 0; i < markerList.size(); i++) {
				markerList.get(i).setIcon(bitmap);
			}
			Bitmap bitmaps = BitmapFactory.decodeResource(getResources(),
					R.drawable.chewei_locationi_click);
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
					.fromBitmap(bitmaps);
			mNameTextView.setText(marker.getTitle());
			marker.setIcon(bitmapDescriptor);
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(marker
					.getPosition()));
			lat = marker.getPosition().latitude;
			lng = marker.getPosition().longitude;
			return true;
		}
	};

	private void initListener() {
		mRadioGroup.setOnCheckedChangeListener(checkedChangeListener);
		mGass_decrese.setOnClickListener(listener);
		mGass_increse.setOnClickListener(listener);
		mSearch.setOnGetGeoCodeResultListener(mSearchCoderResultListener);
		mCoder.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
		mNetiveLayout.setOnClickListener(listener);
		mBaiduMap.setOnMarkerClickListener(onMarkerClickListener);
		checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
	}

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int arg1) {
			// TODO Auto-generated method stub
			if (radioGroup.getCheckedRadioButtonId() == R.id.findcarport_cbox_car_location) {
				login_type++;
				car();
			} else if (radioGroup.getCheckedRadioButtonId() == R.id.findcarport_cbox_person_location) {
				person();
			}

		}

	};

	private void pson(String result) {
		if (result == null) {
			Toast.makeText(mContext, "null", Toast.LENGTH_LONG).show();
		} else {
			mContext.getSharedPreferences("resultSharedPreferences",
					Context.MODE_PRIVATE).edit().putString("result", result)
					.commit();
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.optString("operationState").equals("SUCCESS")) {

					JSONObject jsonObject2 = jsonObject.optJSONObject("data");
					JSONObject jsonObject3 = jsonObject2.optJSONObject("data");
					if (jsonObject3.optString("status").equals("Success")) {
						mTishijulikuang.setVisibility(View.VISIBLE);
						findcarport_map_linearlayout_tishikuang
								.setVisibility(View.GONE);
						JSONArray jsonArray = jsonObject3
								.optJSONArray("pointList");
						if (jsonArray.length() == 1) {
							showToast(getString(R.string.no_data));
							mTishijulikuang.setVisibility(View.GONE);
							findcarport_map_linearlayout_tishikuang
									.setVisibility(View.VISIBLE);
						} else {
							listmMaps = new ArrayList<Map<String, String>>();
							for (int i = 0; i < jsonArray.length(); i++) {
								HashMap<String, String> hashMap = new HashMap<String, String>();
								String name = jsonArray.optJSONObject(i)
										.optString("name");
								String cityName = jsonArray.optJSONObject(i)
										.optString("cityName");
								String lat = jsonArray.optJSONObject(i)
										.optJSONObject("location")
										.optString("lat");
								String lng = jsonArray.optJSONObject(i)
										.optJSONObject("location")
										.optString("lng");
								String address = jsonArray.optJSONObject(i)
										.optString("address");
								String distance = jsonArray.optJSONObject(i)
										.optString("distance");
								String district = jsonArray.optJSONObject(i)
										.optString("district");
								hashMap.put("name", name);
								hashMap.put("cityName", cityName);
								hashMap.put("lat", lat);
								hashMap.put("lng", lng);
								hashMap.put("address", address);
								hashMap.put("distance", distance);
								hashMap.put("district", district);
								listmMaps.add(hashMap);
							}
							for (int i = 0; i < listmMaps.size(); i++) {
								double lat = Double.parseDouble(listmMaps
										.get(i).get("lat").toString());
								double lng = Double.parseDouble(listmMaps
										.get(i).get("lng").toString());
								LatLng latlng = new LatLng(lat, lng);
								moveLatLng(latlng, i);
								list.add(latlng);
							}
							if (isFirst) {
								double lat = StringUtil.getDouble(listmMaps
										.get(0).get("lat"));
								double lng = StringUtil.getDouble(listmMaps
										.get(0).get("lng"));
								LatLng latLng = new LatLng(lat, lng);
								mCoder.reverseGeoCode(new ReverseGeoCodeOption()
										.location(latLng));
								isFirst = false;
							}
						}
					}
				} else {
					showToast(getString(R.string.no_data));
					mTishijulikuang.setVisibility(View.GONE);
					findcarport_map_linearlayout_tishikuang
							.setVisibility(View.VISIBLE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	protected void person() {
		if (!loginaty) {
			baiduMapclear();
			isFirst();
			isF = 0;
			isDraw = false;
			sharedPreferences.edit().putBoolean("isDraw", isDraw).commit();
			setTextNull();
			moveToPerson();
		} else {
			// loginaty = false;
		}
	}

	protected void car() {
		moveToCar();
	}

	private void baiduMapclear() {
		mBaiduMap.clear();
	}

	protected void isFirst() {
		if (!isFirst) {
			isFirst = true;
		}
	}

	/***
	 * 清空数据
	 */
	protected void setTextNull() {
		mfindcarportlistview_map_item_distance.setText("");
		mNameTextView.setText("");
		mPortAddress.setText("");
	}

	protected void moveToPerson() {

		LatLng personLatLng = MyMapUtils.getLatLng(mContext);
		mBaiduMap.setMyLocationEnabled(true);

		mBaiduMapView.updateOritentation(personLatLng,
				R.drawable.chedongtai_person, 20, 20);
		mBaiduMapView.zoomTo(16);
		mBaiduMapView.moveLatLng(personLatLng);
		request(personLatLng);
	}

	protected void moveToCar() {
		getLalngRequest();
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.findcarport_linearlayout_notive:
				if (isDraw) {
					setCarNavigation();

				} else {
					setPersonNavigation();
				}
				break;
			case R.id.findcarport_decrese:
				mBaiduMapView.zoomTo((int) mBaiduMap.getMapStatus().zoom - 1);
				break;
			case R.id.findcarport_increse:
				mBaiduMapView.zoomTo((int) mBaiduMap.getMapStatus().zoom + 1);
				break;
			default:
				break;
			}

		}
	};

	/***
	 * 选中之后的变色以及更改下面的显示内容
	 * 
	 * @param latlng
	 *            经纬度对象
	 * @param i
	 *            选中的是哪个
	 */
	private void moveLatLng(LatLng latlng, int i) {
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.chewei_location);
		OverlayOptions option = new MarkerOptions().position(latlng).icon(
				bitmap);
		Marker marker = (Marker) mBaiduMap.addOverlay(option);
		if (isF == 0) {
			Bitmap bitmaps = BitmapFactory.decodeResource(getResources(),
					R.drawable.chewei_locationi_click);
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
					.fromBitmap(bitmaps);
			marker.setIcon(bitmapDescriptor);
			++isF;
		}
		marker.setTitle(listmMaps.get(i).get("name"));
		markerList.add(marker);
	}

	/***
	 * 以车的位置导航
	 */
	protected void setCarNavigation() {
		if (latlngBean != null) {
			BaiduMapView baiduMapView = new BaiduMapView();
			baiduMapView.initMap(mContext);
			if (latlngBean!=null) {
				baiduMapView.baiduNavigation(latlngBean.getLatLng().latitude,
						latlngBean.getLatLng().longitude, null, lat, lng, address);	
			}
			
		}
	}

	/***
	 * 以人的位置导航
	 */
	protected void setPersonNavigation() {
		if (lat != 0 && lng != 0) {
			BaiduMapView baiduMapView = new BaiduMapView();
			baiduMapView.initMap(mContext);
			baiduMapView.baiduNavigation(
					MyMapUtils.getLatLng(mContext).latitude,
					MyMapUtils.getLatLng(mContext).longitude,
					MyMapUtils.getAddress(mContext), lat, lng, address);
		}
	}

	private OnGetGeoCoderResultListener mSearchCoderResultListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
			address = arg0.getAddress();
			mPortAddress.setText(address);
			if (isDraw) {
				double distance1 = DistanceUtil.getDistance(
						latlngBean.getLatLng(), arg0.getLocation());
				mfindcarportlistview_map_item_distance.setText((int) distance1
						+ "");
			} else {
				LatLng latLng = MyMapUtils.getLatLng(getActivity());
				double distance1 = DistanceUtil.getDistance(latLng,
						arg0.getLocation());
				mfindcarportlistview_map_item_distance.setText((int) distance1
						+ "");
			}
		}
	};

	private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

			if (mNameTextView == null) {
				return;
			}
			setText(result);
			lat = result.getLocation().latitude;
			lng = result.getLocation().longitude;
			if (isDraw) {
				double distance1 = DistanceUtil.getDistance(
						latlngBean.getLatLng(), result.getLocation());
				mfindcarportlistview_map_item_distance.setText((int) distance1
						+ "");
			} else {
				LatLng latLng = new LatLng(MyMapUtils.getLatitude(mContext),
						MyMapUtils.getLongitude(mContext));
				double distance1 = DistanceUtil.getDistance(latLng,
						result.getLocation());
				mfindcarportlistview_map_item_distance.setText((int) distance1
						+ "");
			}
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {

		}
	};

	/****
	 * 在地图上标记车的位置
	 * 
	 * @param latLng
	 */
	private void getMycar(LatLng latLng) {

		float f = mBaiduMap.getMaxZoomLevel();// 19.0 最小比例尺
		MapStatusUpdate u1 = MapStatusUpdateFactory
				.newLatLngZoom(latLng, f - 3);
		mBaiduMap.animateMapStatus(u1);
		BitmapDescriptor myBitmapDescriptor = BitmapDescriptorFactory
				.fromResource(R.drawable.chedongtai_car2x);
		OverlayOptions option = new MarkerOptions().position(latLng).icon(
				myBitmapDescriptor);
		mBaiduMap.addOverlay(option);
	}

	/*****
	 * 设置地址和名称
	 * 
	 * @param result
	 */
	protected void setText(ReverseGeoCodeResult result) {
		mNameTextView.setText(listmMaps.get(0).get("name") + "");
		address = result.getAddress();
		mPortAddress.setText(address);
	}

	/***
	 * 请求网络返回车的位置
	 */
	private void getLalngRequest() {
		if (loginMessage != null) {
			mTishijulikuang.setVisibility(View.VISIBLE);
			if (getCar() != null) {
				if (getCarId() != null && !getCarId().equals("")) {
					if (loginMessage.getCar().getDevice() != null
							&& !loginMessage.getCar().getDevice().equals("")) {
						RequestParams params = new RequestParams();
						setparams(params);
						HttpBiz httpBiz = new HttpBiz(mContext);
						ProgrosDialog.openDialog(mContext);
						baiduMapclear();
						setTextNull();
						isDraw = true;
						isFirst();
						isF = 0;
						sharedPreferences.edit().putBoolean("isDraw", isDraw)
								.commit();
						httpBiz.httPostData(FINDCAR_CODE, API.CAR_DYNAMIC,
								params, this);
						System.out.println(API.CAR_DYNAMIC + "?carId="
								+ getCarId() + "&uid=" + getUid() + "&key="
								+ getKey() + "&isMode=0");
					} else {
						isBand();
					}
				} else {
					isBand();

				}
			} else {
				isBand();
			}
		} else {
			isLogin();
		}
	}

	/***
	 * 没登陆
	 */
	private void isLogin() {
		if (login_type > 1) {
			MapMenssageDialog.OpenDialog(mContext,
					getString(R.string.no_login_findcarport));
			loginaty = true;
			mpersonButton.setChecked(true);
		} else {
			mTishijulikuang.setVisibility(View.GONE);
			mpersonButton.setChecked(true);
		}
	}

	/***
	 * 没绑定设备
	 */
	private void isBand() {
		if (login_type > 1) {
			MapMenssageDialog.OpenDialog(mContext,
					getString(R.string.no_band_findcarport));
			loginaty = true;
			mpersonButton.setChecked(true);
		} else {
			mTishijulikuang.setVisibility(View.GONE);
			mpersonButton.setChecked(true);
		}
	}

	private void setparams(RequestParams params) {
		params.addBodyParameter("carId", getCarId());
		params.addBodyParameter("uid", getUid());
		params.addBodyParameter("key", getKey());
		params.addBodyParameter("isMode", "0");
	}

	private String getKey() {
		// TODO Auto-generated method stub
		return loginMessage.getKey();
	}

	private String getUid() {
		// TODO Auto-generated method stub
		return loginMessage.getUid();
	}

	private String getCarId() {
		// TODO Auto-generated method stub
		return getCar().getCarId();
	}

	private Car getCar() {
		return loginMessage.getCar();
	}

	/****
	 * 网络请求获取停车场
	 * 
	 * @param latLng
	 */
	public void request(LatLng latLng) {
		double lat = latLng.latitude;
		double lng = latLng.longitude;
		RequestParams params = new RequestParams();
		params.addBodyParameter("latitude", lng + "");
		params.addBodyParameter("longitude", lat + "");
		params.addBodyParameter("keyWord", getString(R.string.findcarport) + "");
		params.addBodyParameter("size", 20 + "");
		params.addBodyParameter("page", 0 + "");
		HttpBiz httpBiz = new HttpBiz(mContext);
		ProgrosDialog.openDialog(mContext);
		httpBiz.httPostData(FINDCARPORT_CODE, API.FINDCARPORT_URL, params, this);

		System.out.println(API.FINDCARPORT_URL + "?latitude=" + lat
				+ "&longitude=" + lng + "&keyWord=停车场&size=20&page=0");
	};

	@Override
	public void onResume() {
		super.onResume();
//		MyCarFragment.setGetLocationListener(getLocationListener);
		mMapView.onResume();
	}

	public void onPause() {
		super.onPause();
//		MyCarFragment.setGetLocationListener(null);
	}

	@Override
	public void onDestroy() {
		mBaiduMap.setMyLocationEnabled(false);
		if (mBaiduMap != null) {
			mBaiduMap.clear();
		}
		mBaiduMapView.onDestory();
		mMapView.onDestroy();
		if (markerList != null) {
			markerList.clear();
		}
		if (list != null) {
			list.clear();
		}
		if (bitmap != null) {
			bitmap.recycle();
		}
		if (listmMaps != null) {
			listmMaps.clear();
		}
		onGetGeoCoderResultListener = null;
		mSearchCoderResultListener = null;
		mSearch.destroy();
		mCoder.destroy();
		list.clear();
		markerList.clear();
		if (listmMaps != null) {
			listmMaps.clear();
		}
		bitmap = null;
		markerList = null;
		latlngBean = null;
		listmMaps = null;
		mBaiduMap = null;
		mNameTextView = null;
		list = null;
		mCaRadioButton = null;
		mBaiduMapView = null;
		super.onDestroy();
	}

	@Override
	public void receive(int type, String data) {
		super.receive(type, data);
		System.out.println(data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case FINDCAR_CODE:
			analsis(data);
			break;
		case FINDCARPORT_CODE:
			pson(data);
			break;

		default:
			break;
		}
	}

	/***
	 * 解析得到车的位置
	 * 
	 * @param data
	 *            json
	 */
	private void analsis(String data) {
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (jsonObject.optString("operationState").equals("SUCCESS")) {
				JSONObject jsonObject2 = jsonObject.optJSONObject("data");
				JSONObject jsonObject3 = jsonObject2.optJSONObject("data");
				JSONObject jsonObject4 = jsonObject3.optJSONObject("body");
				try {
					jsonObject4.getString("result");
					mTishijulikuang.setVisibility(View.GONE);
					findcarport_map_linearlayout_tishikuang
							.setVisibility(View.VISIBLE);
					showToast(getString(R.string.gain_car_address_error));
				} catch (Exception e) {
					mTishijulikuang.setVisibility(View.VISIBLE);
					findcarport_map_linearlayout_tishikuang
							.setVisibility(View.GONE);
					getLatLng(jsonObject4);
				}
			} else {
				if (jsonObject.optString("operationState").equals("RELOGIN")) {
					DialogTool.getInstance(mContext).showConflictDialog();
				} else {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 获得车的经纬度
	 * 
	 * @param jsonObject
	 * @throws JSONException
	 */
	private void getLatLng(JSONObject jsonObject) throws JSONException {
		double lat = jsonObject.optDouble("lat");
		double lng = jsonObject.optDouble("lon");
		if (lat > 0 && lng > 0) {
			LatLng latLng = new LatLng(lat, lng);
			latlngBean.setLatLng(latLng);
			getMycar(latLng);
			request(latLng);
		} else {
			showToast(getString(R.string.gain_car_address_error));
		}
	}

}
