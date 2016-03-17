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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.mapapi.model.LatLng;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.FindCarportListAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.MapMenssageDialog;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.Car;
import com.cheweishi.android.entity.LatlngBean;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.widget.XListView;
import com.cheweishi.android.widget.XListView.IXListViewListener;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

public class FindCarportListFragment extends BaseFragment implements
		IXListViewListener {
	private static final int FINDCARPORT_CAR_CODE = 2001;
	private static final int FINDCARPORT_CODE = 2002;
	private XListView listView;
	private LatlngBean latlngBean;
	private RadioGroup mRadioGroup;
	private RadioButton mpersonButton;

	private LinearLayout mFindMeiyouLayout;
	private List<Map<String, String>> list;
	private boolean isDraw = true;
	private FindCarportListAdapter adapter;
	private LoginMessage loginMessage;
	private int page = 0;
	private SharedPreferences sharedPreferences;
	private SharedPreferences sharedPreferences2;

	private int login_type = 0;
	private boolean loginaty = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		list = new ArrayList<Map<String, String>>();
		loginMessage = LoginMessageUtils.getLoginMessage(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_findcarportlist, null);
		latlngBean = new LatlngBean();
		init(view);
		return view;
	}

	private void init(View view) {
		initSharedpreference();
		initView(view);
		settingListview();
		initListener();
		settingSharedpreference();

	}

	private void settingSharedpreference() {
		// TODO Auto-generated method stub
		if (sharedPreferences.getBoolean("isDraw", false)) {

			isCar();
		} else {
			isPerson();
			mpersonButton.setChecked(true);
		}
	}

	private void isPerson() {
		// TODO Auto-generated method stub
		if (mContext.getSharedPreferences("resultSharedPreferences",
				Context.MODE_PRIVATE).getString("result", "") != null
				&& !mContext
						.getSharedPreferences("resultSharedPreferences",
								Context.MODE_PRIVATE).getString("result", "")
						.equals("")) {
			ansisy(mContext.getSharedPreferences("resultSharedPreferences",
					Context.MODE_PRIVATE).getString("result", ""));

		} else {
			moveToPerson();
		}

	}

	private void isCar() {
		// TODO Auto-generated method stub
//		if (mContext.getSharedPreferences("resultSharedPreferences",
//				Context.MODE_PRIVATE).getString("result", "") != null
//				&& !mContext
//						.getSharedPreferences("resultSharedPreferences",
//								Context.MODE_PRIVATE).getString("result", "")
//						.equals("")) {
//			
//			getLalng();
////			ansisy(mContext.getSharedPreferences("resultSharedPreferences",
////					Context.MODE_PRIVATE).getString("result", ""));
//		} else {
//			getLalng();
//		}
		
		getLalng();
	}

	private void initSharedpreference() {

		sharedPreferences2 = mContext.getSharedPreferences("isdraw",
				Context.MODE_PRIVATE);
		sharedPreferences = mContext.getSharedPreferences("isDraw",
				Context.MODE_PRIVATE);
		sharedPreferences2.edit().putBoolean("isdraw", isDraw).commit();

	}

	private void initListener() {
		// TODO Auto-generated method stub
		mRadioGroup.setOnCheckedChangeListener(listener);
	}

	private void settingListview() {
		// TODO Auto-generated method stub
		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(true);
		listView.setXListViewListener(this);
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		mFindMeiyouLayout = (LinearLayout) view
				.findViewById(R.id.findcar_meiyouzhaodao);
		mRadioGroup = (RadioGroup) view
				.findViewById(R.id.findcarport_list_rgroup);
		mpersonButton = (RadioButton) view
				.findViewById(R.id.findcarport_list_person_location);
		listView = (XListView) view.findViewById(R.id.carportlist_listview);
	}

	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			switch (arg1) {
			case R.id.findcarport_list_car_location:
				login_type++;
				car();
				break;
			case R.id.findcarport_list_person_location:
				person();

				break;
			default:
				break;
			}
		}
	};

	protected void moveToPerson() {
		// TODO Auto-generated method stub
		double lati = MyMapUtils.getLatitude(mContext);
		double longi = MyMapUtils.getLongitude(mContext);
		LatLng personLatLng = new LatLng(lati, longi);
		request(personLatLng);
	}

	protected void car() {
		// TODO Auto-generated method stub

		getLalng();
	}

	protected void person() {
		if (!loginaty) {
			list.clear();
			isAdapter();
			mFindMeiyouLayout.setVisibility(View.GONE);
			isDraw = false;
			sharedPreferences2.edit().putBoolean("isdraw", isDraw).commit();
			page = 0;
			moveToPerson();
		}
	}

	private void getLalng() {
		if (loginMessage != null) {
			if (getCar() != null) {
				if (getCarId() != null && !getCarId().equals("")
						&& getCar().getDevice() != null
						&& !getCar().getDevice().equals("")) {
					RequestParams params = new RequestParams();
					setParams(params);
					HttpBiz httpBiz = new HttpBiz(mContext);
					ProgrosDialog.openDialog(mContext);
					isAdapter();
					list.clear();
					isDraw = true;
					sharedPreferences2.edit().putBoolean("isdraw", isDraw)
							.commit();
					mFindMeiyouLayout.setVisibility(View.GONE);
					page = 0;
					httpBiz.httPostData(FINDCARPORT_CAR_CODE, API.CAR_DYNAMIC,
							params, this);
				} else {
					isband();
				}
			} else {
				isband();
			}
		} else {
			isLogin();

		}
	}

	private void isLogin() {
		// TODO Auto-generated method stub
		if (login_type > 1) {
			MapMenssageDialog.OpenDialog(mContext,
					getString(R.string.no_login_findcarport));
			loginaty = true;
			mpersonButton.setChecked(true);
		} else {
			mpersonButton.setChecked(true);
		}
	}

	private void isband() {
		// TODO Auto-generated method stub
		if (login_type > 1) {
			MapMenssageDialog.OpenDialog(mContext,
					getString(R.string.no_band_findcarport));
			loginaty = true;
			mpersonButton.setChecked(true);
		} else {
			mpersonButton.setChecked(true);

		}
	}

	private void setParams(RequestParams params) {
		// TODO Auto-generated method stub
		params.addBodyParameter("carId", getCarId());
		params.addBodyParameter("uid", getUid());
		params.addBodyParameter("key", getKey());
		params.addBodyParameter("isMode", 0 + "");
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
		// TODO Auto-generated method stub
		return loginMessage.getCar();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(FindCarportListFragment.this.getClass()
				.getName()); // 统计页面
	}

	/***
	 * 判断adapter是否为null
	 */
	protected void isAdapter() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		adapter = null;
	}

	protected void ansisy(String result) {
		try {
			System.out.println(result);
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.optString("operationState").equals("SUCCESS")) {

				JSONObject jsonObject2 = jsonObject.optJSONObject("data");
				JSONObject jsonObject3 = jsonObject2.optJSONObject("data");
				if (jsonObject3.optString("status").equals("Success")) {
					// Toast.makeText(mContext, result,
					// Toast.LENGTH_LONG).show();
					JSONArray jsonArray = jsonObject3.optJSONArray("pointList");
					if (jsonArray.length() > 0) {
						pson(jsonArray);
					} else {
						mFindMeiyouLayout.setVisibility(View.VISIBLE);
					}

				} else {
					mFindMeiyouLayout.setVisibility(View.VISIBLE);
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/***
	 * 解析json数组
	 * 
	 * @param jsonArray
	 */
	protected void pson(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		for (int i = 0; i < jsonArray.length(); i++) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
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
			hashMap.put("distance",
					jsonArray.optJSONObject(i).optString("distance"));
			hashMap.put("district",
					jsonArray.optJSONObject(i).optString("district"));
			list.add(hashMap);
		}
		if (adapter == null) {
			adapter = new FindCarportListAdapter(mContext, list, isDraw,
					latlngBean.getLatLng());
			System.out.println(latlngBean.getLatLng()+"--------------------------------");
			
			listView.setAdapter(adapter);
		}
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(FindCarportListFragment.this.getClass()
				.getName());
	}

	@Override
	public void onRefresh() {
		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(true);
		listView.stopLoadMore();
		listView.stopRefresh();
		page = 0;
		list.clear();
		listView.setRefreshTime(getTime());
		if (isDraw) {
			getLalng();
		} else {
			moveToPerson();
		}
		if (adapter != null) {
			adapter.setlist(list, isDraw, latlngBean.getLatLng());
		}

	}

	/****
	 * 获得当前时间
	 * 
	 * @return
	 */
	private String getTime() {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		return simpleDateFormat.format(date);
	}

	@Override
	public void onLoadMore() {
		listView.stopLoadMore();
		page++;
		if (isDraw) {
			getLalng();
		} else {
			moveToPerson();
		}
		if (adapter != null) {
			adapter.setlist(list, isDraw, latlngBean.getLatLng());
		}
	}

	/****
	 * 请求网络数据
	 * 
	 * @param latLng
	 */
	public void request(LatLng latLng) {
		RequestParams params = new RequestParams();
		setRequestParams(params, latLng);
		HttpBiz httpBiz = new HttpBiz(mContext);
		ProgrosDialog.openDialog(mContext);
		httpBiz.httPostData(FINDCARPORT_CODE, API.FINDCARPORT_URL, params, this);
//		System.out.println(API.FINDCARPORT_URL + "?latitude="
//				+ latLng.longitude + "&longitude=" + latLng.latitude
//				+ "&keyWord=停车场&size=20&page=0");
	};

	private void setRequestParams(RequestParams params, LatLng latLng) {
		// TODO Auto-generated method stub
		params.addBodyParameter("latitude", latLng.longitude + "");
		params.addBodyParameter("longitude", latLng.latitude + "");
		params.addBodyParameter("keyWord", getString(R.string.findcarport));
		params.addBodyParameter("size", 20 + "");
		params.addBodyParameter("page", page + "");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		listView = null;
		latlngBean = null;
		mRadioGroup = null;
		mpersonButton = null;
		mFindMeiyouLayout = null;
		list = null;
		adapter = null;
		loginMessage = null;
		page = 0;
		sharedPreferences = null;
		sharedPreferences2 = null;
	}

	/***
	 * 请求网络数据返回的方法
	 */
	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case FINDCARPORT_CAR_CODE:
			getcarLatlng(data);
			break;
		case FINDCARPORT_CODE:
			ansisy(data);

			break;

		default:
			break;
		}
	}

	/***
	 * 解析获得车的位置
	 * 
	 * @param data
	 */
	private void getcarLatlng(String data) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (jsonObject.optString("operationState").equals("SUCCESS")) {
				JSONObject dataJsonObject = jsonObject.optJSONObject("data");
				JSONObject dataObject = dataJsonObject.optJSONObject("data");
				JSONObject bodyJsonObject = dataObject.optJSONObject("body");
				isLatlng(getLatlng(bodyJsonObject));
			} else {
				if (jsonObject.optString("operationState").equals("RELOGIN")) {
					DialogTool.getInstance(mContext).showConflictDialog();
				} else {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/***
	 * 判断能否获取车的位置
	 * 
	 * @param latLng
	 */
	private void isLatlng(LatLng latLng) {
		// TODO Auto-generated method stub
		if (latLng != null) {
			latlngBean.setLatLng(latLng);
			request(latLng);
		} else {
			showToast(getString(R.string.gain_car_address_error));
		}
	}

	private LatLng getLatlng(JSONObject bodyJsonObject) {
		// TODO Auto-generated method stub
		double lat = bodyJsonObject.optDouble("lat");
		double lng = bodyJsonObject.optDouble("lon");
		if (lat > 0 && lng > 0) {
			LatLng latLng = new LatLng(lat, lng);
			return latLng;
		}
		return null;
	}
}
