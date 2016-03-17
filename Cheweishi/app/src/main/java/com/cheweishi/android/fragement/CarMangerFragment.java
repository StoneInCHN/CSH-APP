package com.cheweishi.android.fragement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Config;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.http.SimpleHttpUtils;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.utils.mapUtils.BaseMapUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.RequestParams;

public class CarMangerFragment extends BaseFragment {

	private MapView mapView;
	private List<CarManager> listCarManagerTemp;
	int page = 1;
	View view;
	private LatLng carLatLng;
	private BaseMapUtil mBaseMapUtil;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.car_manger_fragment, container, false);
		mapView = (MapView) view.findViewById(R.id.carmanger_bmapView);
		mBaseMapUtil = new BaseMapUtil(mapView.getMap());
		mBaseMapUtil.setUI();
		mBaseMapUtil.setMapStatus();

		init(view);
		return view;
	}

	public void reconnect() {
		if (mapView != null) {
			mapView = (MapView) view.findViewById(R.id.carmanger_bmapView);
			request();
		}
	}

	private void init(View view) {
		mapView.showZoomControls(false);
		request();
	}

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 400) {
				showToast(Config.ERROR);
			} else {
				String result = (String) msg.obj;
				if (result != null) {
					analysis(result);
				}
			}
		};
	};

	protected void analysis(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.optString("operationState").equals("SUCCESS")) {
				JSONObject jsonObject2 = jsonObject.getJSONObject("data");

				JSONObject jsonObject3 = jsonObject2.getJSONObject("data");
				JSONObject jsonObject4 = jsonObject3.getJSONObject("body");
				getLatlng(jsonObject4);
			} else if ("RELOGIN".equalsIgnoreCase(jsonObject
					.getString("operationState"))) {
			} else {
				jsonObject = jsonObject.getJSONObject("data");
				showToast(jsonObject.getString("msg"));
			}
		} catch (JSONException e) {
			showToast("无数据");
		}

	}

	private void getLatlng(JSONObject jsonObject4) throws JSONException {
		double lat = jsonObject4.getDouble("lat");
		double lng = jsonObject4.getDouble("lon");
		carLatLng = new LatLng(lat, lng);
		getMycar(carLatLng);
	}

	private void getMycar(LatLng latLng) {
		mBaseMapUtil.setMarkerOverlayer(latLng, R.drawable.chedongtai_car2x);
		mBaseMapUtil.moveTo(latLng, false);
	}

	public void request() {
		LoginMessage loginMessage = LoginMessageUtils.getLoginMessage(mContext);
		if (loginMessage == null) {
			return;
		}
		if (loginMessage.getCar() == null) {
			return;
		}
		if (StringUtil.isEmpty(loginMessage.getCar().getDevice())) {
			return;
		}
		String cid = loginMessage.getCar().getCid();

		String url = API.CAR_DYNAMIC + "?uid=" + loginMessage.getUid()
				+ "&key=" + loginMessage.getKey() + "&cid=" + cid;
		SimpleHttpUtils myHttpUtils = new SimpleHttpUtils(mContext, null, url,
				handler);
		myHttpUtils.PostHttpUtils();
	}

	public void connectToServer() {
		LoginMessage loginMessage = LoginMessageUtils.getLoginMessage(mContext);
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", loginMessage.getUid());
		params.addBodyParameter("key", loginMessage.getKey());
		params.addBodyParameter("page", page + "");
		SimpleHttpUtils MyHttpUtils = new SimpleHttpUtils(mContext, params,
				API.CAR_MANAGER_URL, handlercarid);
		MyHttpUtils.PostHttpUtils();
	}

	private Handler handlercarid = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 404:
				Toast.makeText(mContext, R.string.server_link_fault,
						Toast.LENGTH_LONG).show();
				break;
			default:
				parseUserDetailJSON((String) msg.obj);
				break;
			}
		};
	};

	protected void parseUserDetailJSON(String result) {
		if (result == null || result.equals("")) {
			Toast.makeText(mContext, R.string.no_result, Toast.LENGTH_LONG)
					.show();
		} else {
			try {
				System.out.println(result);
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.optString("operationState").equals("SUCCESS")) {
					JSONArray jsonArray = jsonObject.optJSONObject("data")
							.optJSONArray("cars");
					listCarManagerTemp = new ArrayList<CarManager>();
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							Gson gson = new Gson();
							java.lang.reflect.Type type = new TypeToken<CarManager>() {
							}.getType();
							CarManager carManager = gson.fromJson(
									jsonArray.optString(i), type);
							listCarManagerTemp.add(carManager);
						}
					}

				} else if (jsonObject.optString("operationState")
						.equals("FAIL")) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (jsonObject.optString("operationState").equals(
						"RELOGIN")) {
					DialogTool.getInstance(mContext).showConflictDialog();
				} else if (jsonObject.optString("operationState").equals(
						"DEFAULT")) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			} catch (JSONException e) {
				Toast.makeText(mContext,
						mContext.getString(R.string.location_failed),
						Toast.LENGTH_LONG).show();
			}

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mBaseMapUtil.onDestory();
		mBaseMapUtil = null;
	}

}
