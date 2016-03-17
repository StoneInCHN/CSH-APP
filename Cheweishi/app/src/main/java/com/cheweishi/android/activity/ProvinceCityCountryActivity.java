package com.cheweishi.android.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.CarTypeCityListingAdapter;
import com.cheweishi.android.adapter.CarTypeProvinceListingAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ProvinceListing;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.http.RequestParams;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author Xiaojin城市选择
 * 
 */
public class ProvinceCityCountryActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private static final int CODE_PROVINCE = 1001;
	private static final int CODE_CITY = 1002;
	private static final int CODE_COUNTRY = 1003;
	private DrawerLayout mDrawerLayout = null;
	private TextView ibtnBack;
	private TextView ibtnRefresh;
	private TextView ititletext;
	private ListView provinceListView;
	private ListView cityListView;
	private ListView countryListView;
	private TextView tv_getLocationAgain;
	private TextView tv_location;
	private CarTypeProvinceListingAdapter provinceListingAdapter;
	private CarTypeCityListingAdapter cityListingAdapter;
	private List<ProvinceListing> provinceList = new ArrayList<ProvinceListing>();
	private List<ProvinceListing> cityList;
	private List<ProvinceListing> countryList;
	private String ab;
	private int provinceId, cityId, countryId;
	private String provinceName, cityName;
	private MyHandler handler;
	String currentProvince;
	String currentcity;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		provinceList.clear();
		if (cityList != null) {
			cityList.clear();
		}
		if (countryList != null) {
			countryList.clear();
		}
		handler.removeCallbacksAndMessages(null);
		setContentView(R.layout.null_view);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_province_city_country);
		handler = new MyHandler(this);
		init();
	}

	private void getCity() {
		currentProvince = MyMapUtils
				.getProvince(ProvinceCityCountryActivity.this);
		currentcity = MyMapUtils
				.getHistoryCity(ProvinceCityCountryActivity.this);
		if (currentcity != null && (!currentcity.equals("null"))) {
			tv_location.setTextColor(getResources().getColor(R.color.black));
			tv_location.setText(currentcity);
			tv_location.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// getCity();
					connectToServerFromCityName();
				}
			});
			tv_getLocationAgain.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// getCity();
					connectToServerFromCityName();
				}
			});
			tv_getLocationAgain.setText(R.string.GPSLocation);
		} else {
			tv_location.setText(R.string.location_fail);
			tv_getLocationAgain.setText(R.string.GPSLocationAgain);

			tv_location.setTextColor(getResources().getColor(
					R.color.gray_normal));
			tv_getLocationAgain.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					getCity();
				}
			});
		}
	}

	private void connectToServerFromCityName() {
		RequestParams rp = new RequestParams();
		if (currentcity.contains("-")) {
			rp.addBodyParameter("province", currentcity.split("-")[0]);
			rp.addBodyParameter("district", currentcity.split("-")[1]);
			httpBiz.httPostData(1, API.GET_CITY_ID_URL, rp, this);
		}

	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		switch (type) {
		case 1:
			parseNameJSON(data);
			break;
		case 2:
			parseCityJSON(data);
			break;
		}
	}

	private void parseNameJSON(String result) {
		System.out.println("城市信息====" + result);
		if (StringUtil.isEmpty(result)) {
			showToast(R.string.data_fail);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnSuccess, true)) {
					JSONObject jsonObjectData = jsonObject
							.optJSONObject("data");
					Intent intent = new Intent();
					intent.putExtra("provinceId", "");
					intent.putExtra("area", "");
					intent.putExtra("cityId", jsonObjectData.optString("msg"));
					intent.putExtra("countryId", "");

					intent.putExtra("ab", "");
					intent.putExtra("cityName", currentcity.split("-")[1]);
					if (currentcity.split("-")[0].contains("省")) {
						intent.putExtra("name", currentcity.split("-")[1]);
					} else {
						intent.putExtra("name", currentcity.split("-")[0]);
					}
					setResult(RESULT_OK, intent);
					finish();
				} else if (jsonObject.optString("operationState")
						.equals("FAIL")) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (jsonObject.optString("operationState").equals(
						"DEFAULT")) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 对抽屉进行监听
	 */
	private DrawerListener onDrawerListener = new DrawerListener() {

		@Override
		public void onDrawerStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onDrawerSlide(View arg0, float arg1) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onDrawerOpened(View arg0) {
			// TODO Auto-generated method stub
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		}

		@Override
		public void onDrawerClosed(View arg0) {
			mDrawerLayout
					.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		}
	};

	/**
	 * 初始化控件
	 */
	private void init() {
		httpBiz = new HttpBiz(this);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout1);
		mDrawerLayout.setDrawerListener(onDrawerListener);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		ibtnBack = (TextView) findViewById(R.id.left_action);
		ibtnBack.setText(R.string.back);
		ibtnRefresh = (TextView) findViewById(R.id.right_action);
		ititletext = (TextView) findViewById(R.id.title);
		ititletext.setText(R.string.distinct);
		ibtnBack.setOnClickListener(this);
		ibtnRefresh.setOnClickListener(this);
		ititletext.setText(R.string.city_choose);
		LayoutInflater ll = LayoutInflater.from(this);
		View view = ll.inflate(R.layout.head_city, null);
		tv_location = (TextView) view.findViewById(R.id.tv_location);
		tv_getLocationAgain = (TextView) view
				.findViewById(R.id.tv_getLocationAgain);
		getCity();
		provinceListView = (ListView) findViewById(R.id.lv_provincelisting1);
		provinceListView.addHeaderView(view);
		cityListView = (ListView) findViewById(R.id.lv_citylisting1);
		countryListView = (ListView) findViewById(R.id.lv_countrylisting1);
		provinceListView.setSelector(R.drawable.more_pressed);
		cityListView.setSelector(R.drawable.more_pressed);
		countryListView.setSelector(R.drawable.more_pressed);
		provinceListView.setOnItemClickListener(this);
		cityListView.setOnItemClickListener(this);
		countryListView.setOnItemClickListener(this);
		ProgrosDialog.openDialog(this);
		getdata("1", "0");

	}

	/**
	 * 获取省份或者城市数据
	 * 
	 * @param grade
	 * @param parent
	 */
	private void getdata(String grade, String parent) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("grade", grade);
		params.addBodyParameter("parent", parent);
		httpBiz.httPostData(2, API.GET_CITY_URL, params, this);

	}

	private void parseCityJSON(String result) {
		ProgrosDialog.closeProgrosDialog();
		System.out.println("城市信息====" + result);
		if (StringUtil.isEmpty(result)) {
			showToast(R.string.data_fail);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnSuccess, true)) {
					JSONObject jsonObjectData = jsonObject
							.optJSONObject("data");
					JSONArray jsonArray = jsonObjectData.optJSONArray("city");
					if (jsonArray != null && jsonArray.length() > 0) {
						if (provinceFlag == true) {
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObjectIndex = jsonArray
										.optJSONObject(i);
								ProvinceListing provinceListing = new ProvinceListing();
								provinceListing.setId(jsonObjectIndex
										.optInt("cid"));
								provinceListing.setName(jsonObjectIndex
										.optString("name"));
								provinceListing.setClassno(jsonObjectIndex
										.optString("grade"));
								provinceListing.setParent(true);
								provinceList.add(provinceListing);
								provinceListingAdapter = new CarTypeProvinceListingAdapter(
										ProvinceCityCountryActivity.this,
										provinceList, false);
								provinceListView
										.setAdapter(provinceListingAdapter);
							}
							provinceFlag = false;
						} else {
							if (cityList != null) {
								cityList.clear();
							} else {
								cityList = new ArrayList<ProvinceListing>();
							}
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObjectIndex = jsonArray
										.optJSONObject(i);
								ProvinceListing provinceListing = new ProvinceListing();
								provinceListing.setId(jsonObjectIndex
										.optInt("cid"));
								provinceListing.setName(jsonObjectIndex
										.optString("name"));
								provinceListing.setClassno(jsonObjectIndex
										.optString("grade"));
								provinceListing.setParent(true);
								cityList.add(provinceListing);
								cityListingAdapter = new CarTypeCityListingAdapter(
										ProvinceCityCountryActivity.this,
										cityList);
								cityListView.setAdapter(cityListingAdapter);
								ProgrosDialog.closeProgrosDialog();
							}
						}
					}
					if (provinceFlag == true) {

					} else {

					}
				} else if (jsonObject.optString("operationState")
						.equals("FAIL")) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (jsonObject.optString("operationState").equals(
						"DEFAULT")) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * handler更新ui
	 */
	private static class MyHandler extends Handler {
		// WeakReference to the outer class's instance.
		private WeakReference<ProvinceCityCountryActivity> mOuter;

		public MyHandler(ProvinceCityCountryActivity activity) {
			mOuter = new WeakReference<ProvinceCityCountryActivity>(activity);
		}

		public void handleMessage(android.os.Message msg) {
			ProvinceCityCountryActivity outer = mOuter.get();
			switch (msg.what) {
			case CODE_PROVINCE:
				ProgrosDialog.closeProgrosDialog();
				break;
			case CODE_CITY:
				outer.cityListingAdapter = new CarTypeCityListingAdapter(outer,
						outer.cityList);
				outer.cityListView.setAdapter(outer.cityListingAdapter);
				ProgrosDialog.closeProgrosDialog();
				break;

			case CODE_COUNTRY:
				Intent intent = new Intent();
				intent.putExtra("provinceId", outer.provinceId);
				intent.putExtra("area", outer.provinceName);
				intent.putExtra("cityId", outer.cityId + "");
				intent.putExtra("countryId", outer.countryId);
				intent.putExtra("provinceName", outer.provinceName);
				intent.putExtra("ab", outer.ab);
				intent.putExtra("cityName", outer.cityName);
				outer.setResult(RESULT_OK, intent);
				if (outer.provinceName.contains("省")) {
					intent.putExtra("name", outer.cityName);
				} else {
					intent.putExtra("name", outer.provinceName);
				}
				outer.finish();
				break;
			default:
				break;
			}
		}
	}

	// public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// ProvinceCityCountryActivity.this.finish();
	// }
	// return super.onKeyDown(keyCode, event);
	// };

	/**
	 * back，refresh按钮事件
	 * 
	 * @param
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.right_action:
			getdata("1", "0");
			break;
		default:
			break;
		}
	}

	private boolean provinceFlag = true;

	/**
	 * 省份和城市选择事件 onItemClick
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.lv_provincelisting1:
			if (position > 0) {
				provinceName = provinceList.get(position - 1).getName();
				ab = provinceList.get(position - 1).getShort_name();
				mDrawerLayout.openDrawer(Gravity.RIGHT);
				provinceId = (int) id;
				ProgrosDialog.openDialog(this);
				final int parentId = (int) provinceList.get(position - 1)
						.getId();
				getdata("2", parentId + "");
			}
			break;
		case R.id.lv_citylisting1:
			cityId = (int) id;
			cityName = cityList.get(position).getName();
			handler.sendEmptyMessage(CODE_COUNTRY);
			break;

		default:
			break;
		}
	}
}
