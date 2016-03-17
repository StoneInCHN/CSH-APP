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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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
import com.cheweishi.android.adapter.MyStallAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ParkInfo;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.utils.DateUtils;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.FontAwesomeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 我身边的车位
 * 
 * @author mingdasen
 * 
 */
@ContentView(R.layout.activity_mystall)
public class MyStallctivity extends BaseActivity implements OnClickListener {
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
	// listview加载数据
	@ViewInject(R.id.mycar_xlistview)
	private ListView mycar_xlistview;
	@ViewInject(R.id.findcar_tv_map)
	private LinearLayout findcar_tv_map;
	@ViewInject(R.id.no_data)
	private LinearLayout nodata;
	
	@ViewInject(R.id.listToMap)
	private LinearLayout listToMap;
	@ViewInject(R.id.LinearLayout1)
	private RelativeLayout LinearLayout1;
	
	@ViewInject(R.id.ll_list)
	private LinearLayout ll_list;
//	@ViewInject(R.id.ll_map)
//	private LinearLayout ll_map;
	
	@ViewInject(R.id.rb_map)
	private TextView mRadioButtonMap;
	@ViewInject(R.id.rb_list)
	private TextView mRadioButtonList;

	private MyStallAdapter adapter;
	private List<ParkInfo> list;
	private String lon = "";// 经度
	private String lat = "";// 纬度

	// 搜索部分↓↓↓↓
	@ViewInject(R.id.layout_one)
	private LinearLayout layoutOne;
	@ViewInject(R.id.layout_two)
	private LinearLayout layoutTwo;
	// @ViewInject(R.id.tv_search)
	// private TextView tvSearch;
	@ViewInject(R.id.ed_search)
	private EditText edSearch;
	@ViewInject(R.id.tv_cancel)
	private TextView tvCancel;
	@ViewInject(R.id.seach_listview)
	private ListView mListView;
	@ViewInject(R.id.tv_location)
	private TextView tv_location;
	

	private int colorBlue;
	private int colorBlack;
	private TextView tvHead;
	private LinearLayout layoutFoot;
	private LatLng selectLatLng;
	private boolean hasHead;
	private FindCarSearchAdapter searchAdapter;
	private ArrayList<HashMap<String, String>> datas;
	private ArrayList<HashMap<String, String>> historyDatas;
	/**
	 * 标志通过输入键“DONE”发起的搜索
	 */
	private boolean FLAG_DONE = false;
	private GeoCoder coder;
	// 搜索部分↑↑↑↑
	private int stutic = 0;// 0标识我身边、1表示目的地
	private String poiStr = "";// 关键字
	private String type = "cq";// cq表示重庆，qt表示其他
	private Drawable wo;//
	private Drawable sousuo;
	private Drawable phone;
	private FontAwesomeView favFoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		initData();
		initSearchPart();
	}

	/**
	 * 搜索相关设置
	 */
	private void initSearchPart() {
		edSearch.setOnEditorActionListener(onEditorActionListener);
		edSearch.addTextChangedListener(watcher);

		mListView.setOnItemClickListener(onItemClickListener);
		int px10 = DateUtils.dip2Px(this, 10);
		int px5 = DateUtils.dip2Px(this, 5);
		int textSize = 17;

//		tvHead = new TextView(this);
//		tvHead.setText(R.string.lately_search);
//		tvHead.setId(4000);
//		tvHead.setTextSize(textSize);
//		tvHead.setBackgroundColor(getResources().getColor(
//				R.color.gray_backgroud));
//		tvHead.setPadding(px10, px5, px10, px5);

		layoutFoot = new LinearLayout(this);
		layoutFoot.setOrientation(LinearLayout.HORIZONTAL);
		layoutFoot.setId(5000);
		favFoot = new FontAwesomeView(this);
		favFoot.setText(getString(R.string.icon_delete));
		favFoot.setTextSize(13);

		colorBlue = getResources().getColor(R.color.main_blue);
		colorBlack = getResources().getColor(R.color.black);
		tvFoot = new TextView(this);
		tvFoot.setText(R.string.clear_history);
		tvFoot.setTextSize(textSize);
		tvFoot.setPadding(px5, 0, 0, 0);
		tvFoot.setTextColor(getResources().getColor(R.color.main_blue));

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
				FLAG_DONE = true;
				getSugLocation(poiStr);
				return true;
			}
			return false;
		}
	};

	/**
	 * 发起建议点搜索
	 * 
	 * @param s
	 */
	private void getSugLocation(String s) {
		String sCity = MyMapUtils.getCity(MyStallctivity.this);
		if (sCity == null) {
			return;
		}
		SuggestionSearch mSearch = SuggestionSearch.newInstance();
		mSearch.setOnGetSuggestionResultListener(sugResultListener);
		SuggestionSearchOption option = new SuggestionSearchOption()
				.city(sCity).keyword(s.toString());
		mSearch.requestSuggestion(option);
	}

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

	/**
	 * get local datas
	 * 
	 * @return
	 */
	private void getLocalDatas() {
		// TODO get local datas
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
		File file = new File(Environment.getExternalStorageDirectory(),
				"cheweishi");
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(file, "FindCarSearch.txt");

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

	/**
	 * “切换”原始界面
	 */
	private void showOriginal() {
		layoutOne.setVisibility(View.VISIBLE);
		layoutTwo.setVisibility(View.GONE);
		mListView.setVisibility(View.GONE);
		listToMap.setVisibility(View.VISIBLE);
		LinearLayout1.setVisibility(View.VISIBLE);
		// title.setClickable(true);
		// tvSearch.setText(sKey);
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
		layoutOne.setVisibility(View.GONE);
		nodata.setVisibility(View.GONE);
		listToMap.setVisibility(View.GONE);
		LinearLayout1.setVisibility(View.GONE);
		layoutTwo.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.VISIBLE);
		tv_location.setText(MyMapUtils.getAddress(this));
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
//		Log.i("result", "====mListView==" + mListView + "==searchAdapter===" + searchAdapter);
		mListView.setAdapter(searchAdapter);

		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.showSoftInput(edSearch, InputMethodManager.SHOW_IMPLICIT);
	}

	// geo搜索结果
	OnGetGeoCoderResultListener onGetGeoCodeResultListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			String province = result.getAddressDetail().province;
			if (getText(R.string.chongqing).equals(province)) {
				type = "cq";
			} else {
				type = "qt";
			}
			getParkData();
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
			searchAdapter = new FindCarSearchAdapter(MyStallctivity.this, datas);

			if (hasHead) {
				hasHead = false;
//				mListView.removeHeaderView(tvHead);
				mListView.removeFooterView(layoutFoot);
			}
			mListView.setAdapter(searchAdapter);
		}
	};
	private TextView tvFoot;

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

	// /**
	// * 初始化监听
	// */
	// private void initLisiner() {
	// this.left_action.setOnClickListener(this);
	// right_action.setOnClickListener(this);
	// findcar_tv_map.setOnClickListener(this);
	// }

	/**
	 * 初始化数据
	 */
	private void initData() {
		getIntentData();
		getResData();
		Log.i("result", "==status==" + stutic);
		if (stutic == 0) {
			initStatut0();
		} else {
			initStatus1();
		}
		if (list == null || list.size() == 0) {
			nodata.setVisibility(View.VISIBLE);
			mycar_xlistview.setVisibility(View.GONE);
		} else {
			nodata.setVisibility(View.GONE);
			mycar_xlistview.setVisibility(View.VISIBLE);
			adapter = new MyStallAdapter(this, list);
			mycar_xlistview.setAdapter(adapter);
		}
	}

	/**
	 * 初始化status为1时的数据
	 */
	private void initStatus1() {
		lon = getIntent().getDoubleExtra("lon", 0) + "";
		lat = getIntent().getDoubleExtra("lat", 0) + "";
		Log.i("result", "==lon==" + lon + "==lat=" + lat);
		setStatus1TitleRight();
	}

	/**
	 * 当status为1时title和right_action的显示信息
	 */
	private void setStatus1TitleRight() {
		right_action.setImageResource(R.drawable.shan2x);
//		des_icon.setText(R.string.icon_mySide);
		// right_action.setCompoundDrawables(wo, null, null, null); // 设置左图标
		left_action.setText("");
		// tv_title.setCompoundDrawables(sousuo, null, null, null); // 设置左图标
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
	}

	/**
	 * 获取资源数据
	 */
	private void getResData() {
		wo = getResources().getDrawable(R.drawable.haochewei_wo);
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		wo.setBounds(0, 0, wo.getMinimumWidth(), wo.getMinimumHeight());
		sousuo = getResources().getDrawable(R.drawable.zhaochewei_sousuo);
		sousuo.setBounds(0, 0, sousuo.getMinimumWidth(),
				sousuo.getMinimumHeight());
		phone = getResources().getDrawable(R.drawable.zhaochewei_mudidi);
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		phone.setBounds(0, 0, phone.getMinimumWidth(), phone.getMinimumHeight());
	}

	/**
	 * statuc为0时的布局初始化
	 */
	private void initStatut0() {
		lon = MyMapUtils.getLongitude(this) + "";
		lat = MyMapUtils.getLatitude(this) + "";
		this.left_action.setText(R.string.back);
		title_icon.setVisibility(View.GONE);
		this.tv_title.setText(R.string.title_my_side);
		this.right_action.setImageResource(R.drawable.zhaochewei_sousuo);
	}

	/**
	 * 接收intent数据
	 */
	private void getIntentData() {
		list = new ArrayList<ParkInfo>();
		stutic = getIntent().getIntExtra("stutic", 0);
		list = getIntent().getParcelableArrayListExtra("list");
		poiStr = getIntent().getStringExtra("key");
	}

	/**
	 * 获取车位列表数据
	 */
	private void getParkData() {
		ProgrosDialog.openDialog(MyStallctivity.this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("lon", lon);
		rp.addBodyParameter("lat", lat);
		rp.addBodyParameter("type", type);
		httpBiz = new HttpBiz(MyStallctivity.this);
		httpBiz.httPostData(10006, API.FINDCARPORT_LIST_URL, rp, this);
	}

	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 10006:
			parseParkJSON(data);
			break;
		case 400:
			EmptyTools.setEmptyView(this, mycar_xlistview);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * 数据解析
	 * 
	 * @param data
	 */
	private void parseParkJSON(String data) {
		if (data == null) {
			nodata.setVisibility(View.VISIBLE);
			mycar_xlistview.setVisibility(View.GONE);
		} else {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(data);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						"SUCCESS", true)) {
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<List<ParkInfo>>() {
					}.getType();
					list.clear();
					list = gson.fromJson(jsonObject.optJSONObject("data")
							.optJSONObject("data").optString("parkInfoList"),
							type);
					refreshData();
				} else {
					nodata.setVisibility(View.VISIBLE);
					mycar_xlistview.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新数据
	 */
	private void refreshData() {
		if (list == null || list.size() == 0) {
			nodata.setVisibility(View.VISIBLE);
			mycar_xlistview.setVisibility(View.GONE);
		} else {
			nodata.setVisibility(View.GONE);
			mycar_xlistview.setVisibility(View.VISIBLE);
			adapter.refreshData(list);
		}
	}

	@OnClick({ R.id.left_action, R.id.right_action, R.id.findcar_tv_map,
			R.id.title, R.id.tv_cancel })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			this.finish();
			break;
		case R.id.right_action:
			setTitile();
			break;
		case R.id.findcar_tv_map:
			jumpFindcar();
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
		}
	}

	/**
	 * 跳转车位地图界面
	 */
	private void jumpFindcar() {
		setListToMap();
		Intent intent = new Intent(MyStallctivity.this, FindcarActivity.class);
		intent.putExtra("stutic", stutic);// 状态：0表示我身边，1表示目的地
		intent.putExtra("key", poiStr);// 关键字，有关键字是显示关键字，没有关键字传"",显示hint。
		if (list == null || list.size() == 0) {
			intent.putParcelableArrayListExtra("list", null);
		} else {
			intent.putParcelableArrayListExtra("list",
					(ArrayList<? extends Parcelable>) list);
		}
		intent.putExtra("lon", lon);// 有搜索传索搜出来的，没有就传我身边的
		intent.putExtra("lat", lat);// 同lon.
		intent.putExtra("type", type);
		startActivity(intent);
		this.finish();
	}
	
	/**
	 * 设置切换按钮颜色
	 */
	private void setListToMap() {
		findcar_tv_map.setBackgroundResource(R.drawable.chewei_bj2);
		Drawable img_on = getResources().getDrawable(R.drawable.chewei_map_click);
		img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
		mRadioButtonMap.setCompoundDrawables(img_on, null, null, null); //设置左图标
		mRadioButtonMap.setTextColor(getResources().getColor(R.color.orange));
		
		ll_list.setBackgroundResource(R.drawable.chewei_bj1);
		Drawable img_off = getResources().getDrawable(R.drawable.chewei_list);
		img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
		mRadioButtonList.setCompoundDrawables(img_off, null, null, null); //设置左图标
		mRadioButtonList.setTextColor(getResources().getColor(R.color.text_black_colcor));		
	}

	/**
	 * 切换title
	 */
	private void setTitile() {
		if (stutic == 0) {
			showSearch();
			stutic = 1;
		} else {
			right_action.setImageResource(R.drawable.zhaochewei_sousuo);
//			des_icon.setText(R.string.icon_destination);
//			 right_action.setCompoundDrawables(phone, null, null, null); //
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
			if (getText(R.string.chongqing).equals(
					MyMapUtils.getProvince(MyStallctivity.this))) {
				type = "cq";
			} else {
				type = "qt";
			}
			if ((MyMapUtils.getLongitude(MyStallctivity.this) + "").equals(lon)
					&& (MyMapUtils.getLatitude(MyStallctivity.this) + "")
							.equals(lat)) {
			} else {
				lon = MyMapUtils.getLongitude(this) + "";
				lat = MyMapUtils.getLatitude(this) + "";
				getParkData();
			}
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
				R.color.text_black_colcor));
//		tv_title.setHint(R.string.search_destination_hint);
		tv_title.setTextSize(14);
		stutic = 1;
	}

	@Override
	protected void onPause() {
		super.onPause();
		ProgrosDialog.closeProgrosDialog();
	}
	
//	@OnItemClick({R.id.mycar_xlistview})
//	private void onItemClick(AdapterView<?> parent, View view, int position,
//			long id){
//		Intent intent = new Intent(MyStallctivity.this, ParkDetailsActivity.class);
//		intent.putExtra("parkInfo", list.get(position));
//		startActivity(intent);
//	}
}
