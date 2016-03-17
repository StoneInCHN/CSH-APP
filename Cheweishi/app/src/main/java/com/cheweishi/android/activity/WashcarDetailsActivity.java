package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ExpandableListView.OnGroupClickListener;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.ExpandableListViewAdapter;
import com.cheweishi.android.adapter.WashCarCommentAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.UserComment;
import com.cheweishi.android.entity.WashCar;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.BaiduMapView;
import com.cheweishi.android.widget.UnSlidingExpandableListView;
import com.cheweishi.android.widget.UnSlidingListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 商家详情
 * 
 * @author Xiaojin
 * 
 */
@ContentView(R.layout.activity_washcar_malldetails)
public class WashcarDetailsActivity extends BaseActivity implements
		android.view.View.OnClickListener {
	public static final String TAG = "WashcarDetailsActivity";
	/**
	 * 详情界面
	 */
	public static final int INDEX_DETAIL = 1001;
	/**
	 * 订单详情
	 */
	public static final int INDEX_ORDER_DETAIL = 1002;

	/**
	 * 来自地图
	 */
	public static final int INDEX_FROM_MAP = 1003;

	/**
	 * 来自列表
	 */
	public static final int INDEX_FROM_LIST = 1004;
	/**
	 * 来自历史记录
	 */
	public static final int INDEX_FROM_HISTORY = 1005;

	/**
	 * 来自我的订单列表预约中
	 */
	public static final int INDEX_FROM_ING = 1006;
	@ViewInject(R.id.title)
	private TextView tvTitle;
	@ViewInject(R.id.left_action)
	private Button btnLeft;
	@ViewInject(R.id.car_iv_location)
	private ImageView car_iv_location;
	@ViewInject(R.id.tv_time)
	private TextView tv_time;
	@ViewInject(R.id.tv_time_interval)
	private TextView tv_time_interval;
	@ViewInject(R.id.car_xlocation)
	private TextView car_xlocation;
	@ViewInject(R.id.img_maintain_phone)
	private ImageView img_maintain_phone;
	@ViewInject(R.id.img_maintain_ditu)
	private ImageView img_maintain_ditu;
	@ViewInject(R.id.lv_washcar_pinglun)
	private UnSlidingListView lv_washcar_pinglun;
	@ViewInject(R.id.lv_washcar_detils)
	private UnSlidingExpandableListView lv_washcar_detils;
	@ViewInject(R.id.rel_user_comm)
	private RelativeLayout rel_user_comm;
	@ViewInject(R.id.car_tv_car_iv_location)
	private TextView car_tv_car_iv_location;
	@ViewInject(R.id.tv_phone)
	private TextView tv_phone;
	private List<UserComment> comments;
	private WashCarCommentAdapter commentAdapter;
	private ExpandableListViewAdapter exListAdapter;
	String id = "";
	WashCar washCar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		init();
	}

	/**
	 * 初始化视图
	 */
	private void init() {
		comments = new ArrayList<UserComment>();
		// for (int i = 0; i < 3; i++) {
		// UserComment comm = new UserComment();
		// comm.setUser_name("x******1");
		// comm.setTime("2015-12-28");
		// comm.setUserMsg("东西很好，物流很快，很满意！！！");
		// comments.add(comm);
		// }
		commentAdapter = new WashCarCommentAdapter(this, comments);
		lv_washcar_pinglun.setAdapter(commentAdapter);
		httpBiz = new HttpBiz(this);
		tvTitle.setText("商家详情");
		btnLeft.setText("返回");
		id = getIntent().getStringExtra("id");
		getHistoryData();

	}

	// 设置品牌group点击无事件，让expandableListView可以保持一直打开的状态
	private OnGroupClickListener onGroupClickListener = new OnGroupClickListener() {
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			return true;
		}
	};

	/**
	 * 请求服务器，获取商家详情
	 */
	private void getHistoryData() {
		if (!isLogined()) {
			return;
		}
		ProgrosDialog.openDialog(this);
		RequestParams mRequestParams = new RequestParams();
		mRequestParams.addBodyParameter("id", id);
		httpBiz.httPostData(1003, API.CSH_WASHCARDETAILS_URL, mRequestParams,
				this);
	}

	/**
	 * 获取从服务器获取的json数据
	 */
	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 1003:
			receiveHistory(data);
			break;
		default:
			break;
		}
	}

	/**
	 * 解析商家详情
	 * 
	 * @param data
	 */
	private void receiveHistory(String data) {
		Log.i("result", "===订单详情==" + data);
		if (StringUtil.isEmpty(data)) {
			showToast(R.string.FAIL);
			return;
		}
		try {
			JSONObject json = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess, json.optString("state"),
					true)) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<WashCar>() {
				}.getType();
				washCar = gson.fromJson(json.optString("data"), type);
				setData();
			} else if (API.returnRelogin.equalsIgnoreCase(json
					.getString("operationState"))) {
				ReLoginDialog.getInstance(this).showDialog(
						json.optString("message"));
			} else {
				showToast(json.getString("message"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@OnClick(R.id.left_action)
	public void finishActivity(View v) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	/**
	 * 获取从服务器解析出的数据对象并填充视图
	 * 
	 * @param washCar
	 */
	private void setData() {
		XUtilsImageLoader.getxUtilsImageLoader(this, R.drawable.zhaochewei_img,
				car_iv_location, API.CSH_BASE_URL + washCar.getImage_1());
		car_tv_car_iv_location.setText(washCar.getStore_name());
		tv_time_interval.setText(washCar.getBusiness_time());
		car_xlocation.setText(washCar.getAddress());
		exListAdapter = new ExpandableListViewAdapter(this, washCar.getType(),
				washCar);
		lv_washcar_detils.setOnGroupClickListener(onGroupClickListener);
		lv_washcar_detils.setAdapter(exListAdapter);
		for (int i = 0; i < washCar.getType().size(); i++) {
			lv_washcar_detils.expandGroup(i);
		}
	}

	@OnClick({ R.id.left_action, R.id.rel_user_comm, R.id.img_maintain_phone,
			R.id.img_maintain_ditu })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rel_user_comm:// 评论
			Intent intent = new Intent(WashcarDetailsActivity.this,
					AllCommentActivity.class);
			startActivity(intent);
			break;
		case R.id.left_action:
			finish();
			break;
		case R.id.img_maintain_phone:// 拨打电话
			turnToPhone();
			break;
		case R.id.img_maintain_ditu:// 导航
			turnToNav();
			break;
		default:
			break;
		}
	}

	/**
	 * 导航
	 */
	private void turnToNav() {
		BaiduMapView baiduMapView = new BaiduMapView();
		baiduMapView.initMap(this);
		baiduMapView.baiduNavigation(MyMapUtils.getLatitude(this),
				MyMapUtils.getLongitude(this), MyMapUtils.getAddress(this),
				washCar.getIm_lat(), washCar.getIm_lng(), washCar.getAddress());
	}

	/**
	 * 拨打电话
	 */
	public void turnToPhone() {
		Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ washCar.getTel()));
		tel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(tel);
	}
}
