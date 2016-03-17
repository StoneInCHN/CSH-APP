package com.cheweishi.android.fragement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.GasStationAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.JSONCallback;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.MapMenssageDialog;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.DistanceBean;
import com.cheweishi.android.entity.LatlngBean;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.XListView;
import com.cheweishi.android.widget.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/***
 * 加油站列表
 * 
 * @author 刘伟
 * 
 */
public class GasStationListFragment extends BaseFragment implements
		IXListViewListener, JSONCallback {
	private GeoCoder mGeoCoder = null;
	@ViewInject(R.id.gasstationlist_listview)
	private XListView mListView;
	private LatlngBean latlngBean;
	private List<Map<String, String>> list;
	@ViewInject(R.id.gasstationlist_list_rgroup)
	private RadioGroup mRadioGroup;
	@ViewInject(R.id.gasstationlist_person_location)
	private RadioButton mpersonButton;
	@ViewInject(R.id.gasstationlist_car_location)
	private RadioButton mCaRadioButton;
	private boolean isDraw = true;
	private GasStationAdapter adapter;
	private int page = 0;
	@ViewInject(R.id.gasstaion_meiyouzhaodao)
	private LinearLayout mFindMeiyouLayout;
	private SharedPreferences sharedPreferences;
	private SharedPreferences sharedPreferences2;

	private static final int DATA_CODE = 2001;
	private static final int CAR_CODE = 2002;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initoncreate();
	}

	/***
	 * 初始化对象
	 */
	private void initoncreate() {
		// TODO Auto-generated method stub
		mGeoCoder = GeoCoder.newInstance();
		latlngBean = new LatlngBean();
		// mGeoCoder = GeoCoder.newInstance();
		list = new ArrayList<Map<String, String>>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gasstationlist, null);
		ViewUtils.inject(this, view);
		init();
		return view;
	}

	private void init() {
		if (getActivity() != null) {
			initSharedpreferences();
			settingListview();
			initListener();
			sharedPreferences2.edit().putBoolean("isdraw", isDraw).commit();
			isCarAndPerson();
		}

	}

	/****
	 * 绑定事件监听
	 */
	private void initListener() {
		mRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
		mGeoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);
	}

	/***
	 * 设置listview的状态
	 */
	private void settingListview() {
		// TODO Auto-generated method stub
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		mListView.setXListViewListener(this);
	}

	/***
	 * 判断地图那边是人的位置还是车的位置
	 */
	private void isCarAndPerson() {
		// TODO Auto-generated method stub
		if (sharedPreferences.getBoolean("isDraw", false)) {
			mCaRadioButton.setChecked(true);
		} else {
			mpersonButton.setChecked(true);

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("reslut", "=====list=onResume====");
	}

	/***
	 * 实例化sharedPreferences
	 */
	private void initSharedpreferences() {
		// TODO Auto-generated method stub
		sharedPreferences2 = mContext.getSharedPreferences("isgasstationdraw",
				Context.MODE_PRIVATE);
		sharedPreferences = mContext.getSharedPreferences("isgasstationDraw",
				Context.MODE_PRIVATE);

	}

	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int arg1) {
			// TODO Auto-generated method stub
			switch (radioGroup.getCheckedRadioButtonId()) {
			case R.id.gasstationlist_car_location:
				isLogin();
				break;
			case R.id.gasstationlist_person_location:
				person();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onRefresh() {
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		mListView.stopLoadMore();
		mListView.stopRefresh();
		page = 0;
		list.clear();
		mListView.setRefreshTime(getTime());
		adapter = null;
		if (isDraw) {
			getLalng();
		} else {
			moveToPerson();
		}
	}

	/***
	 * 得到当前时间并格式化
	 * 
	 * @return 当前时间的字符串
	 */
	private String getTime() {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		return simpleDateFormat.format(date);
	}

	/***
	 * 没有登陆自动获取手机位置
	 */
	protected void person() {
		list.clear();
		mFindMeiyouLayout.setVisibility(View.GONE);
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		adapter = null;
		isDraw = false;
		sharedPreferences2.edit().putBoolean("isdraw", isDraw).commit();
		moveToPerson();
	}

	/****
	 * 判断是否登陆
	 */
	protected void isLogin() {
		// TODO Auto-generated method stub
		if (!isLogined()) {
			mpersonButton.setChecked(true);
		} else {
			list.clear();
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
			isDraw = true;
			adapter = null;
			sharedPreferences2.edit().putBoolean("isdraw", isDraw).commit();
			mFindMeiyouLayout.setVisibility(View.GONE);
			getLalng();
		}
	}

	/***
	 * 获得手机的位置
	 */
	protected void moveToPerson() {
		if (mContext != null) {
			LatLng personLatLng = MyMapUtils.getLatLng(mContext);
			request(personLatLng);
		}
	}

	/***
	 * 获得车
	 */
	protected void getLalng() {
		// TODO Auto-generated method stub
		if (!StringUtil.isEmpty(loginMessage)) {
			if (!StringUtil.isEmpty(loginMessage.getCarManager())
					&& !StringUtil.isEmpty(getCId())
					&& !StringUtil.isEmpty(loginMessage.getCarManager().getDevice())) {
				httputils();
			} else {
				MapMenssageDialog.OpenDialog(mContext,
						getString(R.string.no_band_gasstation));
				mpersonButton.setChecked(true);
			}
		} else {
			MapMenssageDialog.OpenDialog(mContext,
					getString(R.string.no_login_gasstation));
			mpersonButton.setChecked(true);
		}

	}

	/***
	 * 
	 * 
	 * @return 返回cid
	 */
	private String getCId() {
		return loginMessage.getCarManager().getId();
	}

	/***
	 * 请求车的位置
	 */
	private void httputils() {
		if (isLogined()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("cid", getCId());
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("mobile", loginMessage.getMobile());
			httpBiz = new HttpBiz(mContext);
			ProgrosDialog.openDialog(mContext);
			httpBiz.httPostData(CAR_CODE, API.CAR_DYNAMIC_URL, params,
					GasStationListFragment.this);
		}
	}

	@Override
	public void onLoadMore() {
		mListView.stopLoadMore();
	}

	/***
	 * 解析车的位置
	 * 
	 * @param result
	 */
	protected void analysisCarLatlng(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (StringUtil.isEquals(API.returnSuccess,
					jsonObject.optString("state"), true)) {
				JSONObject datajsonObject = jsonObject.getJSONObject("data");
				isLatlng(datajsonObject);

			} else {
				if (StringUtil.isEquals(jsonObject.optString("state"),
						API.returnRelogin, true)) {
					ReLoginDialog.getInstance(mContext).showDialog(
							jsonObject.getString("message"));
				} else {
					showToast(jsonObject.optString("message"));
					if (adapter != null) {
						adapter.notifyDataSetChanged();
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 判断服务器返回的数据是否正确
	 * 
	 * @param jsonObject
	 * @return
	 */
	private boolean isSeccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		if (jsonObject.optString("operationState").equals("SUCCESS")) {
			return true;
		}
		return false;
	}

	/***
	 * 判断是否能够获取车的位置
	 * 
	 * @param bodyJsonObject
	 */
	private void isLatlng(JSONObject bodyJsonObject) {
		// try {
		// bodyJsonObject.getString("result");
		// showToast(getString(R.string.gain_car_address_error));
		// } catch (Exception e) {
		// if (bodyJsonObject.optDouble("lat") > 0
		// && bodyJsonObject.optDouble("lon") > 0) {
		// LatLng latLng = new LatLng(bodyJsonObject.optDouble("lat"),
		// bodyJsonObject.optDouble("lon"));
		// latlngBean.setLatLng(latLng);
		// request(latLng);
		// } else {
		// showToast(getString(R.string.gain_car_address_error));
		// }
		// }

		double lat = bodyJsonObject.optDouble("lat");
		double lng = bodyJsonObject.optDouble("lon");
		if (lat != 0 && lng != 0) {
			LatLng latLng = new LatLng(lat, lng);
			latlngBean.setLatLng(latLng);
			request(latLng);
		} else {
			showToast(getString(R.string.gain_car_address_error));
		}
	}

	protected void pson(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			JSONObject additionalInformationJsonObject = jsonArray
					.optJSONObject(i).optJSONObject("additionalInformation");
			if (additionalInformationJsonObject != null) {
				hashMap.put("telephone",
						additionalInformationJsonObject.optString("telephone"));
			} else {
				hashMap.put("telephone", "");
			}
			hashMap.put("name", jsonArray.optJSONObject(i).optString("name"));
			hashMap.put("cityName",
					jsonArray.optJSONObject(i).optString("cityName"));
			hashMap.put("lat",
					jsonArray.optJSONObject(i).optJSONObject("location")
							.optString("lat"));
			hashMap.put("lng",
					jsonArray.optJSONObject(i).optJSONObject("location")
							.optString("lng"));
			hashMap.put("address",
					jsonArray.optJSONObject(i).optString("address"));
			hashMap.put("district",
					jsonArray.optJSONObject(i).optString("district"));
			list.add(hashMap);
		}
		sort();
	}

	/****
	 * 解析json
	 * 
	 * @param result
	 */
	protected void analysis(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (isSeccess(jsonObject)) {
				JSONObject dataJsonObject = jsonObject.optJSONObject("data");
				JSONObject dataObject = dataJsonObject.optJSONObject("data");
				if (dataObject.optString("status").equals("Success")) {
					JSONArray jsonArray = dataObject.optJSONArray("pointList");
					pson(jsonArray);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/***
	 * 判断是人的位置还是车的位置
	 */
	private void sort() {
		// TODO Auto-generated method stub
		if (isDraw) {
			sort(list);
		} else {
			personSort(list);
		}
	}

	/***
	 * 人的排序
	 * 
	 * @param personList
	 */
	private void personSort(List<Map<String, String>> personList) {
		// TODO Auto-generated method stub
		if (getActivity() != null) {
			List<DistanceBean> distanceBeans = new ArrayList<DistanceBean>();
			personDistance(distanceBeans, personList);
			// new 对象数组来接收 数据对象的长度
			Object[] objects = new Object[distanceBeans.size()];
			// new double数组来接收距离长度
			double[] dis = new double[distanceBeans.size()];
			// 冒泡排序
			personObject(distanceBeans, objects, dis);
			// 根据距离 顺序 排出对象的顺序
			// 得到位置通过反编译得到地址显示出来
			List<Map<String, String>> personSortList = new ArrayList<Map<String, String>>();
			calculateAddressandDistance(personSortList, objects, dis);
			setAdapter(personSortList, null);
			return;
		}

	}

	/****
	 * 通过距离给数据对象排序
	 * 
	 * @param personSortList
	 * @param objects
	 * @param dis
	 */
	private void calculateAddressandDistance(
			List<Map<String, String>> personSortList, Object[] objects,
			double[] dis) {
		for (int i = 0; i < dis.length; i++) {
			@SuppressWarnings("unchecked")
			Map<String, String> map = (Map<String, String>) objects[i];
			map.put("distance", (int) dis[i] + "");
			personSortList.add(map);
		}

	}

	/***
	 * 
	 * @param distanceBeans
	 *            手机位置到加油站的位置的距离
	 * @param objects
	 * @param dis
	 */
	private void personObject(List<DistanceBean> distanceBeans,
			Object[] objects, double[] dis) {
		// TODO Auto-generated method stub
		for (int i = 0; i < distanceBeans.size(); i++) {
			dis[i] = distanceBeans.get(i).getGetDistance();
			objects[i] = distanceBeans.get(i).getMap();

		}
		personBubblingSort(objects, dis);

	}

	/***
	 * 人的冒泡排序
	 * 
	 * @param objects
	 * @param dis
	 */
	private void personBubblingSort(Object[] objects, double[] dis) {
		// TODO Auto-generated method stub
		for (int i = 0; i < dis.length - 1; i++) {
			for (int j = 0; j < dis.length - i - 1; j++) {
				if (dis[j] > dis[j + 1]) {
					double temp = dis[j];
					dis[j] = dis[j + 1];
					dis[j + 1] = temp;
					Object tempObject = objects[j];
					objects[j] = objects[j + 1];
					objects[j + 1] = tempObject;
				}
			}
		}

	}

	/***
	 * 以人的位置进行排序
	 * 
	 * @param distanceBeans
	 * @param personList
	 */
	private void personDistance(List<DistanceBean> distanceBeans,
			List<Map<String, String>> personList) {
		// TODO Auto-generated method stub
		for (int i = 0; i < personList.size(); i++) {
			DistanceBean distanceBean = new DistanceBean();
			double lat = StringUtil.getDouble(personList.get(i).get("lat"));
			double lng = StringUtil.getDouble(personList.get(i).get("lng"));
			if (isDraw) {
				double distance = DistanceUtil.getDistance(
						new LatLng(lat, lng), latlngBean.getLatLng());
				distanceBean.setGetDistance(distance);

			} else {
				double distance = DistanceUtil.getDistance(
						new LatLng(lat, lng),
						MyMapUtils.getLatLng(getActivity()));
				distanceBean.setGetDistance(distance);

			}
			distanceBean.setMap(personList.get(i));
			distanceBeans.add(distanceBean);
		}

	}

	/***
	 * 排序
	 * 
	 * @param list2
	 */
	private void sort(List<Map<String, String>> list2) {
		if (getActivity() != null) {
			List<DistanceBean> distanceBeans = new ArrayList<DistanceBean>();
			personDistance(distanceBeans, list2);
			Object[] objects = new Object[distanceBeans.size()];
			double[] dis = new double[distanceBeans.size()];
			personObject(distanceBeans, objects, dis);
			List<Map<String, String>> sortList = new ArrayList<Map<String, String>>();
			calculateAddressandDistance(sortList, objects, dis);
			setAdapter(sortList, latlngBean.getLatLng());
		}
	}

	/***
	 * 设置adapter
	 * 
	 * @param sortList
	 * @param latLng
	 */
	private void setAdapter(List<Map<String, String>> sortList, LatLng latLng) {
		adapter = new GasStationAdapter(getActivity(), sortList, isDraw, latLng);
		mListView.setAdapter(adapter);
	}

	public void request(LatLng latLng) {
		mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		mListView = null;
	}

	OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
			if (arg0 != null && arg0.getAddressDetail() != null
					&& arg0.getAddressDetail().district != null) {
				String cityname = arg0.getAddressDetail().district;
				RequestParams params = new RequestParams();
				params.addBodyParameter("cityName", cityname);
				params.addBodyParameter("lat", arg0.getLocation().latitude + "");
				params.addBodyParameter("lon", arg0.getLocation().longitude
						+ "");
				params.addBodyParameter("keyWord",
						getString(R.string.gasstationlist_address));
				params.addBodyParameter("size", 20 + "");
				params.addBodyParameter("page", page + "");
				if (mContext != null) {
					HttpBiz myHttpUtils = new HttpBiz(mContext);
					ProgrosDialog.openDialog(mContext);
					myHttpUtils.httPostData(DATA_CODE, API.GASSTATIONLIST_URL,
							params, GasStationListFragment.this);
				}
			}
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
		}
	};

	@Override
	public void receive(int type, String data) {
		System.out.println(data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case DATA_CODE:
			analysis(data);
			break;
		case CAR_CODE:
			analysisCarLatlng(data);
			break;
		default:
			break;
		}

	};

}
