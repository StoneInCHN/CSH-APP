package com.cheweishi.android.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.FindCarSearchAdapter;
import com.cheweishi.android.adapter.FindcarViewpagerAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ParkInfo;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.utils.DateUtils;
import com.cheweishi.android.utils.DisplayUtil;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.BaiduMapView;
import com.cheweishi.android.widget.FontAwesomeView;
import com.cheweishi.android.widget.GalleryViewPager;
import com.cheweishi.android.widget.ScaleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/****
 * 找车位
 * 
 * @author 刘伟
 * 
 */
public class FindcarActivity extends BaseActivity {

	private static final int FINDCARPORT_CODE = 0;
	private static final String PROVINCE = "重庆市";
	private static final int NO_DATE = -1;
	private int stutic = 0;
	@ViewInject(R.id.findcarportmap_bmapView)
	private MapView mapView;

	private BaiduMap mBaiduMap;

	private View view;

	private BaiduMapView mBaiduMapView;

	private GalleryViewPager viewPager;

	@ViewInject(R.id.right_action)
	private ImageView right_action_;
	@ViewInject(R.id.title)
	private LinearLayout title;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.title_icon)
	private FontAwesomeView title_icon;
	@ViewInject(R.id.left_action)
	private TextView left_action;
	@ViewInject(R.id.right_action)
	private ImageView right_action;
//	@ViewInject(R.id.des_icon)
//	private FontAwesomeView des_icon;

	@ViewInject(R.id.viewpager_relativelayout)
	private RelativeLayout mRelativeLayout_viewPager;

	@ViewInject(R.id.lukuangxx)
	private CheckBox mLukuangXXCheckBox;
	@ViewInject(R.id.findcar_tv_list)
	private LinearLayout findcar_tv_list;
	
	@ViewInject(R.id.listToMap)
	private LinearLayout listToMap;
	@ViewInject(R.id.LinearLayout1)
	private RelativeLayout LinearLayout1;
	@ViewInject(R.id.findcar_tv_map)
	private LinearLayout findcar_tv_map;
	@ViewInject(R.id.tv_location)
	private TextView tv_location;
	
	@ViewInject(R.id.rb_map)
	private TextView mRadioButtonMap;
	@ViewInject(R.id.rb_list)
	private TextView mRadioButtonList;

	// 搜索模块↓↓↓↓↓↓
	@ViewInject(R.id.layout_one)
	private LinearLayout layoutOne;
	@ViewInject(R.id.layout_two)
	private LinearLayout layoutTwo;
	@ViewInject(R.id.ed_search)
	private EditText edSearch;
	@ViewInject(R.id.tv_cancel)
	private TextView tvCancel;
	@ViewInject(R.id.seach_listview)
	private ListView mListView;

	private int colorBlue;
	private int colorBlack;
//	private TextView tvHead;
	private TextView tvFoot;
	private LinearLayout layoutFoot;
	private LatLng selectLatLng;
	private boolean hasHead;
	private FindCarSearchAdapter searchAdapter;
	private ArrayList<HashMap<String, String>> datas;
	private ArrayList<HashMap<String, String>> historyDatas;
	/**
	 * 标志通过输入键“完成”发起的搜索
	 */
	private boolean FLAG_DONE = false;
	// ↑↑↑↑↑↑

	private List<View> lists;

	@ViewInject(R.id.findcar_tv_list)
	private LinearLayout listTextView;

	@ViewInject(R.id.findcarport_increse)
	private ImageButton increseImageButton;

	@ViewInject(R.id.findcarport_decrese)
	private ImageButton decreseImageButton;

	private List<Marker> markersList;
	private List<ParkInfo> listmMaps;
	private List<LatLng> latlngList;
	@ViewInject(R.id.linearlayout_scale)
	private LinearLayout mLinearLayout;
	private LatLng personLatLng;

	@ViewInject(R.id.findcar_location_icon)
	private ImageView mLocation;

	private ScaleView mScaleView;
	private FindcarViewpagerAdapter adatper;
	private List<TextView> listMarkers;
	private List<TextView> listMarkersNo;
	private List<BitmapDescriptor> listBitmapDescriptorsNo;
	private List<BitmapDescriptor> listBitmapDescriptors;
	private BitmapDescriptor bitmapDescriptorsmile = BitmapDescriptorFactory
			.fromResource(R.drawable.zhaochewei_location);
	private String lat;
	private String lon;
	private String poiStr;
	private boolean isDefult = false;
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findcar);
		ViewUtils.inject(this);
		initSearchPart();
		initBaiduview();
		getListDate();
	}

	private void getListDate() {
		Intent intent = getIntent();
		if (intent.getParcelableArrayListExtra("list") != null) {
			if (intent.getIntExtra("stutic", 0) == 0) {
				this.left_action.setText(R.string.back);
				title_icon.setVisibility(View.GONE);
				this.tv_title.setText(R.string.title_my_side);
				this.right_action.setImageResource(R.drawable.zhaochewei_sousuo);
				moveTolocation();
			} else {
				lon = getIntent().getStringExtra("lon");
				lat = getIntent().getStringExtra("lat");
				poiStr = getIntent().getStringExtra("key");

				right_action.setImageResource(R.drawable.shan2x);
//				des_icon.setText(R.string.icon_mySide);
				// right_action.setCompoundDrawables(wo, null, null, null); //
				// 设置左图标
				left_action.setText("");
				// tv_title.setCompoundDrawables(sousuo, null, null, null); //
				// 设置左图标
				title_icon.setVisibility(View.VISIBLE);
				title.setBackgroundResource(R.drawable.zhaochewei_kuang_white);
				title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
				tv_title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
				if (poiStr == null || poiStr.equals("")) {
					tv_title.setText("");
					tv_title.setHint(R.string.search_destination_hint);
					tv_title.setTextColor(getResources().getColor(R.color.gray));
				} else {
					tv_title.setText(poiStr);
					tv_title.setTextColor(getResources().getColor(
							R.color.text_black_colcor));
				}
				tv_title.setTextSize(14);
				LatLng latLng = new LatLng(StringUtil.getDouble(lat),
						StringUtil.getDouble(lon));
				selectLatLng = latLng;
				moveTolocation(latLng);
			}
			stutic = intent.getIntExtra("stutic", 0);
			listmMaps = intent.getParcelableArrayListExtra("list");
			analList(listmMaps);
		} else {
			moveToPerson();
		}

	}

	private void initBaiduview() {
		// TODO Auto-generated method stub
		mapView.showZoomControls(false);
		mBaiduMapView = new BaiduMapView(mapView, this);
		mBaiduMap = mapView.getMap();
		mScaleView = new ScaleView(this);
		mScaleView.setMapView(mBaiduMap);
		mLinearLayout.addView(mScaleView);
		initbaidu();
	}

	private void initbaidu() {
		listMarkers = new ArrayList<TextView>();
		listMarkersNo = new ArrayList<TextView>();
		listBitmapDescriptorsNo = new ArrayList<BitmapDescriptor>();
		listBitmapDescriptors = new ArrayList<BitmapDescriptor>();
		for (int i = 0; i < 10; i++) {
			listMarkers.add((TextView) getLayoutInflater().inflate(
					R.layout.marker_bitmap, null).findViewById(
					R.id.marker_tv_bitmap));
			listMarkersNo.add((TextView) getLayoutInflater().inflate(
					R.layout.marker_tv_bitmap_noxuanzhong, null).findViewById(
					R.id.marker_tv_bitmap_no));
		}
		lists = new ArrayList<View>();
		initList();
		latlngList = new ArrayList<LatLng>();
		markersList = new ArrayList<Marker>();
	};

	private void moveTolocation() {
		personLatLng = MyMapUtils.getLatLng(this);
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMapView.updateOritentation(personLatLng,
				R.drawable.chedongtai_person, 20, 20);
		mBaiduMapView.moveLatLng(personLatLng, 16);
	};

	private void moveTolocation(LatLng latLng) {
		mBaiduMapView.setMarker(latLng, R.drawable.zhaochewei_weizhi);
		mBaiduMapView.moveLatLng(latLng, 16);
	};

	protected void moveToPerson() {
		isDefult = false;
		moveTolocation();
		if (MyMapUtils.getProvince(this) != null
				&& MyMapUtils.getProvince(this).equals(PROVINCE)) {
			type = "cq";
		} else {
			type = "qt";
		}
		request();
	}

	public void request() {
		RequestParams params = new RequestParams();
//		Log.i("result", "==lat==" + selectLatLng.latitude + "==lon=" + selectLatLng.longitude);
		Log.i("result", "==lat==" + personLatLng.latitude + "==lon=" + personLatLng.longitude);
		if (isDefult) {
			params.addBodyParameter("lat", selectLatLng.latitude + "");
			params.addBodyParameter("lon", selectLatLng.longitude + "");
		} else {
			params.addBodyParameter("lat", personLatLng.latitude + "");
			params.addBodyParameter("lon", personLatLng.longitude + "");
		}
		
		params.addBodyParameter("type", type);
		httpBiz = new HttpBiz(this);
		ProgrosDialog.openDialog(this);
		httpBiz.httPostData(FINDCARPORT_CODE, API.FINDCAR_URL, params, this);
	};

	private void init() {
		left_action.setOnClickListener(listener);
		refreshScaleAndZoomControl();
		viewPager = new GalleryViewPager(this);
		viewPager.setClipChildren(false);
		adatper = new FindcarViewpagerAdapter(this, lists);
		viewPager.setAdapter(adatper);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setPageMargin(DisplayUtil.dip2px(this, 10));
		viewPager.setCurrentItem(100);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
		lp.width = ScreenTools.getScreentWidth(this) * 4 / 5;
		viewPager.setLayoutParams(lp);
		mRelativeLayout_viewPager.addView(viewPager);
		initListener();
	}

	private void initListener() {
		listTextView.setOnClickListener(listener);
		mLukuangXXCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
		mBaiduMap.setOnMapDoubleClickListener(onMapDoubleClickListener);
		decreseImageButton.setOnClickListener(listener);
		increseImageButton.setOnClickListener(listener);
		mBaiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
		mBaiduMap.setOnMarkerClickListener(onMarkerClickListener);
		mLocation.setOnClickListener(listener);
		// right_action.setOnClickListener(listener)
		title.setOnClickListener(listener);
		viewPager.setOnPageChangeListener(pageChangeListener);
		right_action_.setOnClickListener(listener);

	}

	private OnMapDoubleClickListener onMapDoubleClickListener = new OnMapDoubleClickListener() {

		@Override
		public void onMapDoubleClick(LatLng arg0) {
			refreshScaleAndZoomControl();
		}
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

	/**
	 * 搜索相关设置
	 */
	private void initSearchPart() {
		tvCancel.setOnClickListener(listener);
		edSearch.setOnEditorActionListener(onEditorActionListener);
		edSearch.addTextChangedListener(watcher);
		mListView.setOnItemClickListener(onItemClickListener);
		int px10 = DateUtils.dip2Px(FindcarActivity.this, 10);
		int px5 = DateUtils.dip2Px(FindcarActivity.this, 5);
		int textSize = 17;

//		tvHead = new TextView(FindcarActivity.this);
//		tvHead.setText("最近搜过");
//		tvHead.setId(4000);
//		tvHead.setTextSize(textSize);
//		tvHead.setBackgroundColor(FindcarActivity.this.getResources().getColor(
//				R.color.gray_backgroud));
//		tvHead.setPadding(px10, px5, px10, px5);

		layoutFoot = new LinearLayout(FindcarActivity.this);
		layoutFoot.setOrientation(LinearLayout.HORIZONTAL);
		layoutFoot.setId(5000);
		favFoot = new FontAwesomeView(this);
		favFoot.setText(getString(R.string.icon_delete));
		favFoot.setTextSize(13);
		tvFoot = new TextView(FindcarActivity.this);
		tvFoot.setText("清除历史记录");
		tvFoot.setTextSize(textSize);
		tvFoot.setPadding(px5, 0, 0, 0);
		colorBlue = FindcarActivity.this.getResources().getColor(
				R.color.main_blue);
		colorBlack = FindcarActivity.this.getResources()
				.getColor(R.color.black);

		layoutFoot.addView(favFoot);
		layoutFoot.addView(tvFoot);
		layoutFoot.setPadding(px10, px10, px10, px10);
		layoutFoot.setGravity(Gravity.CENTER);

		coder = GeoCoder.newInstance();
		coder.setOnGetGeoCodeResultListener(onGetGeoCodeResultListener);
	}

	private void setFooterViewColor(int color) {
		tvFoot.setTextColor(color);
		favFoot.setTextColor(color);
	}

	OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				String poiStr = edSearch.getText() == null ? null : edSearch
						.getText().toString();
				if (historyDatas == null) {
					historyDatas = new ArrayList<HashMap<String, String>>();
				}
				HashMap<String, String> items = new HashMap<String, String>();
				items.put("key", poiStr);
				historyDatas.add(items);
				saveLocalDatas();
				stutic = 0;
				showOriginal();
				FLAG_DONE = true;
				getSugLocation(poiStr);
				return true;
			}
			return false;
		}
	};

	/**
	 * get local datas
	 * 
	 * @return
	 */
	private void getLocalDatas() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return;
		}
		File file = Environment.getExternalStorageDirectory();
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(file, "cheweishi/FindCarSearch.txt");
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			char[] buffer = new char[1024];
			StringBuilder sb = new StringBuilder();
			while (fr.read(buffer, 0, buffer.length) > 0) {
				sb.append(buffer);
			}
			String json = sb.toString();
			JSONArray jsonArray = new JSONArray(json);
			historyDatas = new ArrayList<HashMap<String, String>>();
			for (int i = jsonArray.length() - 1; i >= 0; i--) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("city", jsonObject.getString("city"));
				map.put("district", jsonObject.getString("district"));
				map.put("key", jsonObject.getString("key"));
				map.put("latitude", jsonObject.getString("latitude"));
				map.put("longitude", jsonObject.getString("longitude"));
				historyDatas.add(map);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * save datas to local
	 * 
	 * @return
	 */
	private void saveLocalDatas() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return;
		}
		File file = Environment.getExternalStorageDirectory();
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(file, "cheweishi/FindCarSearch.txt");
		FileOutputStream fos = null;
		PrintStream ps = null;
		try {
			fos = new FileOutputStream(file);
			ps = new PrintStream(fos);

			Gson gson = new Gson();
			String json = gson.toJson(historyDatas, ArrayList.class);
			ps.println(json);
		} catch (FileNotFoundException e) {
		} finally {
			if (ps != null) {
				ps.flush();
				ps.close();
			}
		}
	}

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
					view = LayoutInflater.from(FindcarActivity.this).inflate(
							R.layout.findcar_viewpager_item, null);
				} else {
					mRelativeLayout_viewPager.removeView(view);
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
				mRelativeLayout_viewPager.addView(view);

				viewPager.setVisibility(View.GONE);

			}
			return true;
		}
	};

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
//				if (listmMaps.get(i).getLeftNum() == NO_DATE) {
//					listMarkers.get(i).setText("--");
//					listMarkersNo.get(i).setText("--");
//				} else {
//					listMarkers.get(i).setText(
//							listmMaps.get(i).getLeftNum() + "");
//					listMarkersNo.get(i).setText(
//							listmMaps.get(i).getLeftNum() + "");
//				}
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
//			Intent intent = new Intent(FindcarActivity.this,
//					ParkDetailsActivity.class);
//			intent.putExtra("parkInfo", listmMaps.get(position));
//			FindcarActivity.this.startActivity(intent);
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
			mBaiduMapView.baiduNavigation(
					MyMapUtils.getLatitude(FindcarActivity.this),
					MyMapUtils.getLongitude(FindcarActivity.this),
					MyMapUtils.getAddress(FindcarActivity.this), latitude,
					longitude, addr);
		}
	}

	private void initList() {
		for (int i = 0; i < 10; i++) {
			lists.add(getLayoutInflater().inflate(
					R.layout.findcar_viewpager_item, null));
		}

	}

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
			if (mRelativeLayout_viewPager != null) {
				mRelativeLayout_viewPager.invalidate();
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};
	
	/**
	 * 设置切换按钮颜色
	 */
	private void setMapToList() {
		findcar_tv_list.setBackgroundResource(R.drawable.chewei_bj2);
		Drawable img_on = getResources().getDrawable(R.drawable.chewei_map_click);
		img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
		mRadioButtonMap.setCompoundDrawables(img_on, null, null, null); //设置左图标
		mRadioButtonMap.setTextColor(getResources().getColor(R.color.orange));
		
		findcar_tv_map.setBackgroundResource(R.drawable.chewei_bj1);
		Drawable img_off = getResources().getDrawable(R.drawable.chewei_list);
		img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
		mRadioButtonList.setCompoundDrawables(img_off, null, null, null); //设置左图标
		mRadioButtonList.setTextColor(getResources().getColor(R.color.text_black_colcor));		
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.left_action:
				FindcarActivity.this.finish();
				break;
			case R.id.findcar_tv_list:
				setMapToList();
				
				Intent intent = new Intent(FindcarActivity.this,
						MyStallctivity.class);
				intent.putExtra("stutic", stutic);
				intent.putParcelableArrayListExtra("list",
						(ArrayList<? extends Parcelable>) listmMaps);
				if (stutic == 1) {
					if (selectLatLng != null) {
						intent.putExtra("lon", selectLatLng.longitude);
						intent.putExtra("lat", selectLatLng.latitude);
					} else {
						intent.putExtra("lon", "");
						intent.putExtra("lat", "");
					}

				} else {
					intent.putExtra("lon", personLatLng.longitude);// 有搜索传索搜出来的，没有就传我身边的
					intent.putExtra("lat", personLatLng.latitude);// 同lon.
				}
				intent.putExtra("key", poiStr);
				startActivity(intent);
				FindcarActivity.this.finish();
				break;
			case R.id.findcarport_increse:
				if (mBaiduMap.getMapStatus().zoom == mBaiduMap
						.getMaxZoomLevel()) {
					increseImageButton
							.setImageResource(R.drawable.zhaochewei_fangda_max_up);
				} else {
					if (mBaiduMap.getMapStatus().zoom == mBaiduMap
							.getMinZoomLevel()) {
						decreseImageButton
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
					decreseImageButton
							.setImageResource(R.drawable.zhaochewei_fangda_max_down);
				} else {
					if (mBaiduMap.getMapStatus().zoom == mBaiduMap
							.getMaxZoomLevel()) {
						increseImageButton
								.setImageResource(R.drawable.ibtn_map_increse);
					}
					mBaiduMapView
							.zoomTo((int) mBaiduMap.getMapStatus().zoom - 1);
					refreshScaleAndZoomControl();
				}
				break;
			case R.id.title:
				if (getText(R.string.title_my_side).equals(tv_title.getText())) {
				} else {
					showSearch();
				}
				break;
			case R.id.tv_cancel:
				stutic = 1;
				showOriginal();
				break;
			case R.id.findcar_location_icon:
				mBaiduMapView.moveLatLng(MyMapUtils
						.getLatLng(FindcarActivity.this));
				break;
			case R.id.right_action:
//				stutic = 1;
				setTitile();
				break;
			default:
				break;
			}
		}
	};
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			HashMap<String, String> items = (HashMap<String, String>) parent
					.getItemAtPosition(position);

			if (items != null) {
				poiStr = items.get("key");
				title_icon.setVisibility(View.VISIBLE);
				tv_title.setText(poiStr);

				lat = items.get("latitude");
				lon = items.get("longitude");
				selectLatLng = new LatLng(StringUtil.getDouble(lat),
						StringUtil.getDouble(lon));
				ReverseGeoCodeOption options = new ReverseGeoCodeOption();
				options.location(selectLatLng);
				coder.reverseGeoCode(options);

				if (historyDatas == null) {
					historyDatas = new ArrayList<HashMap<String, String>>();
				}
				historyDatas.add(items);
				saveLocalDatas();
				stutic = 0;
				showOriginal();
			} else if (position != 0) {
				// 清除历史记录
				historyDatas = null;
				searchAdapter.clearData();
				saveLocalDatas();

				setFooterViewColor(colorBlack);
				layoutFoot.setClickable(true);
			}

		}
	};

	/****
	 * 清除数据
	 */
	private void clear() {
		if (mBaiduMap != null) {
			mBaiduMap.clear();
		}
		if (lists != null) {
			lists.clear();
			lists = null;
		}
		if (listMarkers != null) {
			listMarkers.clear();
			listMarkers = null;
		}
		if (listMarkersNo != null) {
			listMarkersNo.clear();
			listMarkersNo = null;
		}
		if (listmMaps != null) {
			listmMaps.clear();
			listmMaps = null;
		}
		if (listBitmapDescriptors != null) {
			listBitmapDescriptors.clear();
			listBitmapDescriptors = null;
		}
		if (listBitmapDescriptorsNo != null) {
			listBitmapDescriptorsNo.clear();
			listBitmapDescriptorsNo = null;
		}
	}

	// geo搜索结果
	OnGetGeoCoderResultListener onGetGeoCodeResultListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			String province = result.getAddressDetail().province;
			clear();
			initbaidu();
			mBaiduMapView.setMarker(selectLatLng, R.drawable.zhaochewei_weizhi);
			mBaiduMapView.moveLatLng(selectLatLng);
			isDefult = true;
			if (province.equals(PROVINCE)) {
				type = "cq";
			} else {
				type = "qt";
			}
			request();

		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {

		}
	};

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
			if (s == null) {
				return;
			}
			getSugLocation(s.toString());
		}

	};

	/**
	 * 发起建议点搜索
	 * 
	 * @param s
	 */
	private void getSugLocation(String s) {
		String sCity = MyMapUtils.getCity(FindcarActivity.this);
		if (sCity == null) {
			return;
		}
		SuggestionSearch mSearch = SuggestionSearch.newInstance();
		mSearch.setOnGetSuggestionResultListener(sugResultListener);
		SuggestionSearchOption option = new SuggestionSearchOption()
				.city(sCity).keyword(s.toString());
		mSearch.requestSuggestion(option);
	}

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

	/**
	 * 建议搜索监听
	 */
	private OnGetSuggestionResultListener sugResultListener = new OnGetSuggestionResultListener() {

		@Override
		public void onGetSuggestionResult(SuggestionResult result) {
			if (result == null) {
				return;
			}
			List<SuggestionInfo> list = result.getAllSuggestions();
			datas = new ArrayList<HashMap<String, String>>();
			if (list == null || list.size() == 0) {
				return;
			}

			if (FLAG_DONE) {
				FLAG_DONE = false;
				dealInfoAfterGo(list);
				return;
			}
			for (SuggestionInfo info : list) {
				// 过滤出有用数据
				if (info.pt != null) {
					HashMap<String, String> items = new HashMap<String, String>();
					items.put("city", info.city);
					items.put("district", info.district);
					items.put("key", info.key);
					items.put("latitude", info.pt.latitude + "");
					items.put("longitude", info.pt.longitude + "");
					datas.add(items);
				}
			}
			searchAdapter = new FindCarSearchAdapter(FindcarActivity.this,
					datas);

			if (hasHead) {
				hasHead = false;
//				mListView.removeHeaderView(tvHead);
				mListView.removeFooterView(layoutFoot);
			}
			mListView.setAdapter(searchAdapter);
		}

	};

	/**
	 * 按下“go”，接收到数据后的操作
	 * 
	 * @param list
	 */
	private void dealInfoAfterGo(List<SuggestionInfo> list) {
		SuggestionInfo info = null;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			info = list.get(i);
			if (info != null && info.pt != null) {
				break;
			}

		}
		if (info == null || info.pt == null) {
			return;
		}

		poiStr = info.key;
		title_icon.setVisibility(View.VISIBLE);
		tv_title.setText(poiStr);

		lat = info.pt.latitude + "";
		lon = info.pt.longitude + "";
		selectLatLng = new LatLng(info.pt.latitude, info.pt.longitude);
		ReverseGeoCodeOption options = new ReverseGeoCodeOption();
		options.location(selectLatLng);
		coder.reverseGeoCode(options);

		if (historyDatas == null) {
			historyDatas = new ArrayList<HashMap<String, String>>();
			return;
		}
		HashMap<String, String> items = new HashMap<String, String>();
		items.put("city", info.city);
		items.put("district", info.district);
		items.put("key", info.key);
		items.put("latitude", info.pt.latitude + "");
		items.put("longitude", info.pt.longitude + "");
		historyDatas.add(items);
		saveLocalDatas();
		stutic = 0;
		showOriginal();
	}

	private GeoCoder coder;
	private FontAwesomeView favFoot;

	/**
	 * “切换”原始界面
	 */
	private void showOriginal() {
		layoutOne.setVisibility(View.VISIBLE);
		layoutTwo.setVisibility(View.GONE);
		mListView.setVisibility(View.GONE);
		listToMap.setVisibility(View.VISIBLE);
		LinearLayout1.setVisibility(View.VISIBLE);
		edSearch.setText(null);
		datas = null;
		mListView.setAdapter(null);
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edSearch.getWindowToken(), 0);
		
		if (stutic == 0) {
			showSearchTitle();
		} else {
			setTitile();
		}
	}

	/**
	 * “切换”搜索界面
	 */
	private void showSearch() {
		tv_location.setText(MyMapUtils.getAddress(this));
		layoutOne.setVisibility(View.GONE);
		listToMap.setVisibility(View.GONE);
		LinearLayout1.setVisibility(View.GONE);
		layoutTwo.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.VISIBLE);
		edSearch.setHint(R.string.search_park_hint);
		edSearch.requestFocus();
		if (!hasHead) {
			hasHead = true;
//			mListView.addHeaderView(tvHead);
			mListView.addFooterView(layoutFoot);
		}

		getLocalDatas();

		if (historyDatas != null && historyDatas.size() != 0) {
			setFooterViewColor(colorBlue);
			layoutFoot.setClickable(false);
		} else {
			setFooterViewColor(colorBlack);
			layoutFoot.setClickable(true);
		}
		searchAdapter = new FindCarSearchAdapter(this, historyDatas);
		mListView.setAdapter(searchAdapter);

		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.showSoftInput(edSearch, InputMethodManager.SHOW_IMPLICIT);
	}

	/****
	 * 修改比例尺的显示长度和数字
	 */
	private void refreshScaleAndZoomControl() {
		mScaleView.refreshScaleView(Math.round(mBaiduMap.getMapStatus().zoom));
	}

	@Override
	public void receive(int type, String data) {
		super.receive(type, data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case FINDCARPORT_CODE:
			pson(data);
			break;
		default:
			break;
		}
	}

	private void pson(String result) {
		if (StringUtil.isEmpty(result)) {
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(result);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						"SUCCESS", true)) {
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<List<ParkInfo>>() {
					}.getType();
					listmMaps = gson.fromJson(jsonObject.optJSONObject("data")
							.optJSONObject("data").optString("parkInfoList"),
							type);
					if (listmMaps.size() > 0) {
						mRelativeLayout_viewPager.setVisibility(View.VISIBLE);
						analList(listmMaps);
					} else {
						mRelativeLayout_viewPager.setVisibility(View.INVISIBLE);
					}
				} else {
					showToast(getString(R.string.no_data));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
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

	/**
	 * 切换title
	 */
	private void setTitile() {
		if (stutic == 0) {
			showSearch();
//			right_action.setImageResource(R.drawable.);
////			des_icon.setText(R.string.icon_mySide);
//			// right_action.setCompoundDrawables(wo, null, null, null); // 设置左图标
//			left_action.setText("");
//			// tv_title.setCompoundDrawables(sousuo, null, null, null); // 设置左图标
//			title_icon.setVisibility(View.VISIBLE);
//			title.setBackgroundResource(R.drawable.zhaochewei_kuang_white);
//			title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//			tv_title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//			tv_title.setText("");
//			tv_title.setTextColor(getResources().getColor(
//					R.color.text_black_colcor));
//			tv_title.setHint(R.string.search_destination_hint);
//			tv_title.setTextSize(14);
			stutic = 1;
		} else {
			right_action.setImageResource(R.drawable.zhaochewei_sousuo);
//			des_icon.setText(R.string.icon_destination);
			// right_action.setCompoundDrawables(phone, null, null, null); //
			// 设置左图标
			left_action.setText(R.string.back);
			// tv_title.setCompoundDrawables(null, null, null, null); // 设置左图标
			title.setBackgroundColor(getResources().getColor(R.color.white));
			title.setGravity(Gravity.CENTER);
			tv_title.setGravity(Gravity.CENTER);
			title_icon.setVisibility(View.GONE);
			tv_title.setText(R.string.title_my_side);
			tv_title.setTextColor(getResources().getColor(R.color.text_black_colcor));
			tv_title.setHint("");
			tv_title.setTextSize(20);
			stutic = 0;
			clear();
			initbaidu();
			isDefult = false;
			moveToPerson();
		}
	}
	
	/**
	 * 搜索返回title显示
	 */
	private void showSearchTitle() {
		right_action.setImageResource(R.drawable.shan2x);
//		des_icon.setText(R.string.icon_mySide);
		// right_action.setCompoundDrawables(wo, null, null, null); // 设置左图标
		left_action.setText("");
		// tv_title.setCompoundDrawables(sousuo, null, null, null); // 设置左图标
		title_icon.setVisibility(View.VISIBLE);
		title.setBackgroundResource(R.drawable.zhaochewei_kuang_white);
		title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		tv_title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//		tv_title.setText("");
		tv_title.setTextColor(getResources().getColor(
				R.color.gray_pressed));
//		tv_title.setHint(R.string.search_destination_hint);
		tv_title.setTextSize(14);
		stutic = 1;
	}

	@Override
	protected void onDestroy() {
		clear();
		mapView.onDestroy();
		mBaiduMapView.onDestory();
		super.onDestroy();
	}
}
