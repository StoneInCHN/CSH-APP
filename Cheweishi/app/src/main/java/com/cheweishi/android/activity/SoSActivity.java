package com.cheweishi.android.activity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 紧急救援
 */
public class SoSActivity extends BaseActivity implements OnClickListener,
		BDLocationListener, OnMarkerClickListener {
	@ViewInject(R.id.Sos_map)
	private MapView Sos_map;
	@ViewInject(R.id.Sos_address)
	private TextView Sos_address;
	@ViewInject(R.id.SosPhone)
	private TextView SosPhone;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.ll_locate)
	private LinearLayout ll_locate;
	private CustomDialog.Builder builder;
	private CustomDialog sosDialog;
	// 声明定位模式
	private LocationClient locationClient;
	// 声明定位服务客户端
	private LocationClientOption Option;
	private BaiduMap baiduMap;
	BitmapDescriptor bitmapDescriptor;
	private String city = "";
	private String district = "";
	private String key = "";
	private double latitude = 0.0;
	private double longitude = 0.0;
	private boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_sos);
		ViewUtils.inject(this);
		init();
	}

	/**
	 * 初始化视图
	 */
	private void init() {
		title.setText(R.string.fuwu_emergency);
		// 获取地图控制器
		baiduMap = Sos_map.getMap();
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		locationClient = new LocationClient(getApplicationContext());
		Option = new LocationClientOption();
		Option.setCoorType("bd0911");// 设置返回定位坐标
		Option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		Option.setIsNeedAddress(true);// 是否需要地址信息
		Option.setNeedDeviceDirect(true);// 是否需要设备方向
		Option.setScanSpan(5000);// 设置扫描（毫秒）
		locationClient.setLocOption(Option);
		// 定位监听事件
		locationClient.registerLocationListener(this);
		baiduMap.setOnMarkerClickListener(this);
	}

	@Override
	protected void onStart() {
		locationClient.start();
		baiduMap.setMyLocationEnabled(true);
		super.onStart();
	}

	@Override
	protected void onStop() {
		locationClient.stop();
		baiduMap.setMyLocationEnabled(false);
		super.onStop();
	}

	@OnClick({ R.id.SosPhone, R.id.left_action, R.id.ll_locate })
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.SosPhone:
			showSOSDialog();
			break;
		case R.id.left_action:
			finish();
			break;
		case R.id.ll_locate:
			searchPlace();
			break;

		default:
			break;
		}
	}

	/**
	 * 紧急救援搜索
	 */
	private void searchPlace() {
		Intent intent = new Intent();
		intent.setClass(SoSActivity.this, SearchActivity.class);
		intent.putExtra("type", "SOS");
		intent.putExtra("hint", "紧急救援搜索");
		startActivityForResult(intent, 1000);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		Log.i("result", "===argo==" + arg0 + "====arg1==" + arg1);
		if (arg1 == RESULT_OK) {
			switch (arg0) {
			case 1000:
				baiduMap.clear();
				if (!StringUtil.isEmpty(arg2)) {
					if (!StringUtil.isEmpty(arg2.getStringExtra("city"))) {
						city = arg2.getStringExtra("city");
					}
					if (!StringUtil.isEmpty(arg2.getStringExtra("district"))) {
						district = arg2.getStringExtra("district");
					}
					if (!StringUtil.isEmpty(arg2.getStringExtra("keyword"))) {
						key = arg2.getStringExtra("keyword");
					}
					if (!StringUtil.isEmpty(arg2.getStringExtra("lat"))
							&& !StringUtil.isEmpty(arg2.getStringExtra("lon"))) {
						latitude = StringUtil.getDouble(arg2
								.getStringExtra("lat"));
						longitude = StringUtil.getDouble(arg2
								.getStringExtra("lon"));
					}
					String stradd = city + district + key;
					setData(latitude, longitude, stradd);
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 弹出呼叫对话框
	 */
	private void showSOSDialog() {
		builder = new CustomDialog.Builder(this);
		builder.setTitle(R.string.customerServicePhone);
		builder.setPositiveButton(R.string.call_out,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:"
										+ getResources().getString(
												R.string.customerServicePhone)));
						startActivity(intent);

					}
				});

		builder.setNegativeButton(R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// setRadioButtonLight();
					}
				});
		sosDialog = builder.create();
		sosDialog.show();

	}

	/**
	 * 添加覆盖物
	 * 
	 * @param Latitude
	 * @param Longitude
	 * @param string
	 */
	private void setData(double Latitude, double Longitude, String string) {
		LatLng latLng = new LatLng(Latitude, Longitude);
		baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
		bitmapDescriptor = BitmapDescriptorFactory
				.fromResource(R.drawable.jiuyuan_chepaihao2x);
		OverlayOptions ooA = new MarkerOptions().position(latLng)
				.icon(bitmapDescriptor).zIndex(9).draggable(true);
		baiduMap.addOverlay(ooA);
		Sos_address.setText(string);
		// 当前位置的详细信息
	}

	/**
	 * 定位当前位置监听
	 */
	@Override
	public void onReceiveLocation(BDLocation arg0) {
		// TODO Auto-generated method stub
		if (flag == false) {
			flag = true;
			setData(arg0.getLatitude(), arg0.getLongitude(), arg0.getAddrStr());

		}
	}

	/**
	 * 覆盖物点击监听
	 */
	@Override
	public boolean onMarkerClick(Marker arg0) {
		InfoWindow mInfoWindow;
		TextView location = new TextView(SoSActivity.this);
		location.setBackgroundResource(R.drawable.jiuyuan_kuang);// location_tips

		if (isLogined() && hasCar()) {
			location.setText(loginMessage.getCarManager().getPlate());
		} else {
			location.setText(Sos_address.getText().toString());
		}
		final LatLng ll = arg0.getPosition();
		Point p = baiduMap.getProjection().toScreenLocation(ll);
		p.y -= 60;
		LatLng llInfo = baiduMap.getProjection().fromScreenLocation(p);
		mInfoWindow = new InfoWindow(
				BitmapDescriptorFactory.fromView(location), llInfo, 10,
				new OnInfoWindowClickListener() {

					@Override
					public void onInfoWindowClick() {
						Log.i("===========", "==================");
						baiduMap.hideInfoWindow();
					}
				});
		// 显示InfoWindow
		baiduMap.showInfoWindow(mInfoWindow);
		return false;
	}

}
