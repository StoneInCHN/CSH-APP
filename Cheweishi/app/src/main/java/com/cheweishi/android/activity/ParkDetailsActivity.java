package com.cheweishi.android.activity;

import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ParkInfo;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.BaiduMapView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ParkDetailsActivity extends BaseActivity implements
		OnClickListener {

	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.car_detailes_iv_location)
	private ImageView park_img;
	@ViewInject(R.id.tvcar_detaileslocation)
	private TextView park_name;
	@ViewInject(R.id.car_detailes_xlocation)
	private TextView park_addr;
	@ViewInject(R.id.car_detailes_tv_length)
	private TextView park_distance;
	@ViewInject(R.id.tvcar_detailes_num)
	private TextView park_num;
	@ViewInject(R.id.tvcar_detailes_text)
	private TextView park_price;
	@ViewInject(R.id.car_detailes_lin_yuyue)
	private LinearLayout call;
	@ViewInject(R.id.car_detailes_lin_daozhequ)
	private LinearLayout go;
	private String parkID = "";
	private ParkInfo parkInfo;
	private String lat = "";
	private String lon = "";
	// private String addr = "";
	private int distance = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parking_detailes);
		ViewUtils.inject(this);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		parkInfo = getIntent().getParcelableExtra("parkInfo");
		parkID = parkInfo.getParkID();
		lat = parkInfo.getLatitude();
		lon = parkInfo.getLongitude();
		distance = parkInfo.getDistance();
		left_action.setText(R.string.back);
		title.setText(R.string.park_details_title);
		// left_action.setOnClickListener(this);
		// call.setOnClickListener(this);
		// go.setOnClickListener(this);
		// park_img.setOnClickListener(this);
		if ("baidu".equals(parkInfo.getParkID())) {
			setTextData();
		} else {
			getParkDetailsData();
		}
	}

	private Dialog dialog1;
	private Button origionImgBtn;
	private LinearLayout btn_layout;
	private ImageView origionalImg;

	/**
	 * 
	 * @Title: showDialog
	 * @Description: TODO(dialog弹出和显示的样式)
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void showImgDialog() {
		View view = getLayoutInflater().inflate(R.layout.person_seting_dialog,
				null);
		dialog1 = new Dialog(this, R.style.transparentFrameWindowStyle);

		dialog1.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		 origionImgBtn = (Button) view.findViewById(R.id.origionImgBtn);
		btn_layout = (LinearLayout) view.findViewById(R.id.btn_layout);
		origionalImg = (ImageView) view.findViewById(R.id.origionalImg);
		origionImgBtn.setOnClickListener(myListener);
		origionalImg.setOnClickListener(myListener);
		btn_layout.setVisibility(View.INVISIBLE);
		origionalImg.setVisibility(View.VISIBLE);
		Window window = dialog1.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog1.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog1.setCanceledOnTouchOutside(true);

		final Animation animation1 = AnimationUtils.loadAnimation(this,
				R.anim.score_business_query_enter);
		origionalImg.startAnimation(animation1);
		XUtilsImageLoader.getxUtilsImageLoader(this, R.drawable.zhaochewei_img,origionalImg, parkInfo.getPicUrl());
		dialog1.show();
	}

	OnClickListener myListener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.origionalImg:
				dialog1.dismiss();
				break;
			case R.id.origionImgBtn:
				btn_layout.setVisibility(View.INVISIBLE);
				origionalImg.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

	/**
	 * 获取停车场详情数据
	 */
	private void getParkDetailsData() {
		ProgrosDialog.openDialog(ParkDetailsActivity.this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("parkID", parkID);
		httpBiz = new HttpBiz(ParkDetailsActivity.this);
		httpBiz.httPostData(10005, API.FINDCARPORT_DETAILES_URL, rp, this);
	}

	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 10005:
			pareJsonData(data);
			break;
		case 400:
			showToast(R.string.server_link_fault);
			break;
		default:
			break;
		}
	}

	/**
	 * 数据解析
	 * 
	 * @param data
	 */
	private void pareJsonData(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(R.string.data_fail);
		} else {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(data);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						"SUCCESS", true)) {
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<ParkInfo>() {
					}.getType();
					parkInfo = gson.fromJson(jsonObject.optJSONObject("data")
							.optJSONObject("data").optString("parkInfo"), type);
					setTextData();
				} else {
					showToast(jsonObject.optJSONObject("data")
							.optJSONObject("data").optString("message"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 填充数据
	 */
	private void setTextData() {
		XUtilsImageLoader.getxUtilsImageLoader(this, R.drawable.zhaochewei_img,park_img, parkInfo.getPicUrl());
		park_name.setText(parkInfo.getName());
		park_addr.setText(parkInfo.getAddr());
		park_distance.setText(distance + "" + getText(R.string.distance_unit));
		if (parkInfo.getLeftNum() == -1) {
			park_num.setText("--" + "/" + "--");
		} else {
			park_num.setText(parkInfo.getLeftNum() + "/" + parkInfo.getTotal());
		}
		if (null == parkInfo.getPrice() || "--".equals(parkInfo.getPrice())) {
			park_price.setText("--");
		} else {
			park_price.setText(QBchange(parkInfo.getPriceDesc()));
		}
	}

	// 全角转半角
	public String QBchange(String QJstr) {
		String outStr = "";
		String Tstr = "";
		byte[] b = null;

		for (int i = 0; i < QJstr.length(); i++) {
			try {
				Tstr = QJstr.substring(i, i + 1);
				b = Tstr.getBytes("unicode");
			} catch (java.io.UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (b[3] == -1) {
				b[2] = (byte) (b[2] + 32);
				b[3] = 0;
				try {
					outStr = outStr + new String(b, "unicode");
				} catch (java.io.UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else
				outStr = outStr + Tstr;
		}
		return outStr;
	}

	@OnClick({ R.id.left_action, R.id.car_detailes_lin_yuyue,
			R.id.car_detailes_lin_daozhequ, R.id.car_detailes_iv_location })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			this.finish();
			break;
		case R.id.car_detailes_lin_yuyue:
			break;
		case R.id.car_detailes_lin_daozhequ:
			goToPark();
			break;
		case R.id.car_detailes_iv_location:
			if (!StringUtil.isEquals("", parkInfo.getPicUrl(), true)) {
				showImgDialog();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 跳转到导航
	 */
	private void goToPark() {
		BaiduMapView bb = new BaiduMapView();
		bb.initMap(this);
		bb.baiduNavigation(MyMapUtils.getLatitude(this),
				MyMapUtils.getLongitude(this), MyMapUtils.getAddress(this),
				StringUtil.getDouble(lat), StringUtil.getDouble(lon),
				parkInfo.getAddr());
	}
}
