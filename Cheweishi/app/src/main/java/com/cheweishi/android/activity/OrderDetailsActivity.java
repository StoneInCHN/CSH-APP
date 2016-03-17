package com.cheweishi.android.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import com.cheweishi.android.adapter.OrderExLvAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.OrderDetail;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.QRImageUtil;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.BaiduMapView;
import com.cheweishi.android.widget.UnSlidingOrderExpandapleListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cheweishi.android.cheweishi.R;

/**
 * 订单详情/生成订单
 * 
 * @author Xiaojin
 * 
 */

public class OrderDetailsActivity extends BaseActivity implements
		OnClickListener {

	@ViewInject(R.id.lv_order)
	private UnSlidingOrderExpandapleListView listView;
	private OrderExLvAdapter adapter;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.ll_succeed_name)
	private LinearLayout ll_succeed_name;
	@ViewInject(R.id.car_tv_car_iv_location)
	private TextView car_tv_car_iv_location;
	@ViewInject(R.id.imgphone)
	private ImageView imgphone;
	@ViewInject(R.id.img_nav)
	private ImageView img_nav;
	@ViewInject(R.id.car_xlocation)
	private TextView car_xlocation;
	@ViewInject(R.id.tv_reputation)
	private TextView tv_reputation;
	@ViewInject(R.id.tv_order_car)
	private TextView tv_order_car;
	private String store_id;
	private String goods_id;
	private String price;
	private OrderDetail orderDetail;
	private MyBroadcastReceiver broad;
	@ViewInject(R.id.rl_order)
	private LinearLayout rl_order;
	@ViewInject(R.id.car_iv_location)
	private ImageView car_iv_location;
	@ViewInject(R.id.rl_success_progress)
	private RelativeLayout rl_success_progress;
	private boolean flag = false;
	@ViewInject(R.id.tv_time1_first)
	private TextView tv_time1_first;
	@ViewInject(R.id.tv_time1_second)
	private TextView tv_time1_second;
	@ViewInject(R.id.tv_order_sn)
	private TextView tv_order_sn;
	@ViewInject(R.id.img_erweima)
	private ImageView img_erweima;
	private boolean cancelFlag = false;
	@ViewInject(R.id.ll_erweima)
	private LinearLayout ll_erweima;
	@ViewInject(R.id.img_order)
	private ImageView img_order;
	@ViewInject(R.id.tv_order_class)
	private TextView tv_order_class;
	@ViewInject(R.id.img_yuyue)
	private ImageView img_yuyue;
	@ViewInject(R.id.tv_yuyue)
	private TextView tv_yuyue;
	@ViewInject(R.id.img_daodian)
	private ImageView img_daodian;
	@ViewInject(R.id.tv_daodian)
	private TextView tv_daodian;
	private Bitmap qrBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_details);
		ViewUtils.inject(this);
		init();
	}

	/**
	 * 初始化视图
	 */
	private void init() {
		title.setText(R.string.order_details);
		left_action.setText(R.string.back);
		right_action.setVisibility(View.GONE);
		right_action.setText(R.string.cancel_Order);
		if (hasCar()) {
			tv_order_car.setText(loginMessage.getCarManager().getBrand()
					.getBrandName()
					+ "-"
					+ loginMessage.getCarManager().getBrand().getSeriesName());
		}

		if (!StringUtil.isEmpty(getIntent().getBundleExtra("bundle"))) {// 生成订单
			Bundle bundle = getIntent().getBundleExtra("bundle");
			store_id = bundle.getString("store_id");
			goods_id = bundle.getString("goods_id");
			price = bundle.getString("price");
			flag = true;
		} else {// 订单详情
			flag = false;
		}

		connectToServer();
	}

	@OnClick({ R.id.left_action, R.id.right_action, R.id.img_nav, R.id.imgphone })
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.right_action:// 取消订单
			cancelOrder();
			break;
		case R.id.img_nav:// 导航
			turnToNav();
			break;
		case R.id.imgphone:// 拨打电话
			turnToPhone();
			break;
		default:
			break;
		}
	}

	/**
	 * 拨打电话
	 */
	public void turnToPhone() {
		Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ orderDetail.getMobile()));
		tel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(tel);
	}

	/**
	 * 导航
	 */
	private void turnToNav() {
		BaiduMapView baiduMapView = new BaiduMapView();
		baiduMapView.initMap(this);
		baiduMapView.baiduNavigation(MyMapUtils.getLatitude(this),
				MyMapUtils.getLongitude(this), MyMapUtils.getAddress(this),
				orderDetail.getIm_lat(), orderDetail.getIm_lng(),
				orderDetail.getAddress());
	}

	/**
	 * 取消订单
	 */
	private void cancelOrder() {
		Intent intent = new Intent();
		intent.setClass(OrderDetailsActivity.this, MaintainCancelActivity.class);
		intent.putExtra("orderId", orderDetail.getOrderId());
		startActivity(intent);
	}

	/**
	 * 请求服务器
	 */
	private void connectToServer() {
		RequestParams rp = new RequestParams();
		ProgrosDialog.openDialog(this);
		httpBiz = new HttpBiz(this);
		rp.addBodyParameter("uid", loginMessage.getUid());
		rp.addBodyParameter("mobile", loginMessage.getMobile());
		if (flag == true) {// 生成订单网络请求
			rp.addBodyParameter("store_id", store_id);
			rp.addBodyParameter("goods_id", goods_id);
			rp.addBodyParameter("price", price);
			httpBiz.httPostData(10001, API.CSH_MAKE_ORDER_URL, rp, this);
		} else {// 订单详情请求
			if (cancelFlag == false) {
				rp.addBodyParameter("orderId",
						getIntent().getStringExtra("order_id"));
			} else {
				rp.addBodyParameter("orderId", orderDetail.getOrderId());
			}
			httpBiz.httPostData(10001, API.ORDER_DETAIL_URL, rp, this);
		}
	}

	/**
	 * 接收服务器返回数据
	 */
	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 10001:// 服务器连接成功
			parseJSON(data);
			break;
		case 400:// 服务器连接失败
			showToast(R.string.server_link_fault);
			break;
		}
	}

	private void parseJSON(String result) {
		if (StringUtil.isEmpty(result)) {// 返回数据出错
			return;
		}
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (StringUtil.isEquals(jsonObject.optString("state"),
					API.returnSuccess, true)) {
				rl_order.setVisibility(View.VISIBLE);
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<OrderDetail>() {
				}.getType();
				orderDetail = gson.fromJson(jsonObject.optString("data"), type);
				setValues();
			} else if (StringUtil.isEquals(jsonObject.optString("state"),
					API.returnRelogin, true)) {
				ReLoginDialog.getInstance(this).showDialog(
						jsonObject.optString("message"));
			} else {
				showToast(jsonObject.optString("message"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 格式化日期
	 * 
	 * @param dateStr
	 * @return
	 */
	private String formateDate(String dateStr) {
		if (!dateStr.equals("null")) {
			DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.CHINA);
			Date date = null;
			try {
				date = sf.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DateFormat sf1 = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
			String str = sf1.format(date);
			return str;
		}
		return "";
	}

	private void setValues() {
		car_tv_car_iv_location.setText(orderDetail.getStore_name());
		car_xlocation.setText(orderDetail.getAddress());
		// tv_reputation.setText(orderDetail.getReputation());
		tv_reputation.setText("");
		tv_order_sn.setText(orderDetail.getOrder_sn());
		XUtilsImageLoader.getxUtilsImageLoader(this, R.drawable.zhaochewei_img,
				car_iv_location, API.CSH_BASE_URL + orderDetail.getImage_1());
		progress_pay_statue(orderDetail.getStatus());
		adapter = new OrderExLvAdapter(this, orderDetail.getOrderGoodsList());
		if (!StringUtil.isEmpty(orderDetail.getBarcodes())) {
			ll_erweima.setVisibility(View.VISIBLE);
			qrBitmap = QRImageUtil.createQRImage(orderDetail.getBarcodes(),
					OrderDetailsActivity.this);
			img_erweima.setImageBitmap(qrBitmap);
		}
		listView.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (null != qrBitmap) {
			// 回收并且置为null
			qrBitmap.recycle();
			qrBitmap = null;
		}
		System.gc();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (broad == null) {
			broad = new MyBroadcastReceiver();
		}

		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		registerReceiver(broad, intentFilter);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.CANCEL_ORDER_SUCCESS_REFRESH, true)) {
				cancelFlag = true;
				connectToServer();

			}
		}
	}

	/**
	 * 进行中、已完成
	 * 
	 * @param res_str
	 */
	private void green_img_order(int res_str) {
		img_order.setImageResource(R.drawable.dingdanxiangqing_chenggong2x);
		tv_order_class.setText(res_str);
	}

	private void red_img_order(int res_str) {
		img_order.setImageResource(R.drawable.dingdanxiangqing_cancel2x);
		tv_order_class.setText(res_str);
	}

	private void progress_pay_statue(String str) {
		if (StringUtil.isEquals(str, "0", true)) {// 订单已取消(支付流程)
			img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
			img_daodian.setImageResource(R.drawable.dingdanxiangqing_quxiao2x);
			tv_yuyue.setText("预约下单");
			tv_daodian.setText("已取消");
			red_img_order(R.string.order_cancel_oen);
			tv_daodian.setTextColor(getResources().getColor(R.color.order_dr));
			tv_time1_first.setText(formateDate(orderDetail.getAdd_time()));
			tv_time1_second
					.setText(formateDate(orderDetail.getFinished_time()));
		} else if (StringUtil.isEquals(str, "1", true)) {// 确认付款(支付流程)
			img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
			img_daodian.setImageResource(R.drawable.dingdanxiangqing_baoyang2x);
			tv_yuyue.setText("预约下单");
			tv_daodian.setText("到店洗车");
			green_img_order(R.string.order_not_pay);
			tv_daodian.setTextColor(getResources().getColor(R.color.gray));
			tv_time1_first.setText(formateDate(orderDetail.getAdd_time()));
			tv_time1_second.setText("");
		} else if (StringUtil.isEquals(str, "12", true)) {// 订单进行中(支付流程)
			img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
			img_daodian.setImageResource(R.drawable.dingdanxiangqing_baoyang2x);
			tv_yuyue.setText("预约下单");
			tv_daodian.setText("到店洗车");
			green_img_order(R.string.order_win_middle);
			tv_daodian.setTextColor(getResources().getColor(R.color.gray));
			tv_time1_first.setText(formateDate(orderDetail.getAdd_time()));
			tv_time1_second.setText("");
		} else if (StringUtil.isEquals(str, "3", true)) {// 订单完成(支付流程)
			img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
			img_daodian.setImageResource(R.drawable.dingdanxiangqing_baoyang1);
			tv_yuyue.setText("预约下单");
			tv_daodian.setText("到店洗车");
			green_img_order(R.string.order_win_complete);
			tv_daodian.setTextColor(getResources().getColor(R.color.order_dr));
			tv_time1_first.setText(formateDate(orderDetail.getAdd_time()));
			tv_time1_second
					.setText(formateDate(orderDetail.getFinished_time()));
		} else if (StringUtil.isEquals(str, "4", true)) {// 过期(支付流程)
			img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
			img_daodian.setImageResource(R.drawable.dingdanxiangqing_quxiao2x);
			tv_yuyue.setText("预约下单");
			tv_daodian.setText("已过期");
			red_img_order(R.string.order_out_of_dateline);
			tv_daodian.setTextColor(getResources().getColor(R.color.order_dr));
			tv_time1_first.setText(formateDate(orderDetail.getAdd_time()));
			tv_time1_second
					.setText(formateDate(orderDetail.getFinished_time()));
		}
	}

}
