package com.cheweishi.android.fragement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.AddCarActivity;
import com.cheweishi.android.activity.CarDynamicActivity;
import com.cheweishi.android.activity.CarInformationActivity;
import com.cheweishi.android.activity.CarReportActivity;
import com.cheweishi.android.activity.DetactionInfoActivity;
import com.cheweishi.android.activity.FindcarActivity;
import com.cheweishi.android.activity.FootmarkActivity;
import com.cheweishi.android.activity.GasStationActivity;
import com.cheweishi.android.activity.LoginActivity;
import com.cheweishi.android.activity.MainNewActivity;
import com.cheweishi.android.activity.NavigationActivity;
import com.cheweishi.android.activity.WashCarActivity;
import com.cheweishi.android.activity.WashcarDetailsActivity;
import com.cheweishi.android.activity.WeatherActivity;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.entity.AkeyTextAllInfo;
import com.cheweishi.android.entity.AkeytestInfo;
import com.cheweishi.android.entity.Car;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.entity.WashcarVO;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.ButtonUtils;
import com.cheweishi.android.utils.CustomProgressDialog;
import com.cheweishi.android.utils.DisplayUtil;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.ScreenUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.utils.mapUtils.LocationUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.CustomDialog.Builder;
import com.cheweishi.android.widget.FontAwesomeView;
import com.cheweishi.android.widget.LeiDaView;
import com.cheweishi.android.widget.Panel;
import com.cheweishi.android.widget.Panel.OnPanelListener;
import com.cheweishi.android.widget.TwoAnimationLayout;
import com.cheweishi.android.widget.XCRoundImageView;
import com.lidroid.xutils.http.RequestParams;

/**
 * 我的车（主界面）
 * 
 * @author zhangq
 * 
 */
@SuppressLint({ "InflateParams", "ResourceAsColor" })
public class MyCarFragment extends BaseFragment implements OnPanelListener {
	private XCRoundImageView imgUser;// 用户头像
	private LinearLayout lLayoutUser;
	private LinearLayout tvReturn;// 返回
	private LinearLayout tvMore;// 更多
	private TextView tvMoreSub;
	private TextView tvTitle;// 昵称
	private ImageView imgVip;// vip图标
	private ImageView imgWeather;// 天气图标
	private RelativeLayout rLayoutMessage;// 信息图标
	private TextView tvMessage;// 信息数目
	// private TextView tvTips;// 提示“正在监测”
	private RelativeLayout rLayoutTip;
	private TextView tvPhone;// 电话
	private ImageView imgPhone;// 电话图标
	private LinearLayout tvCarDynamic;// 车动态
	private LinearLayout tvNavigation;// 导航
	private LinearLayout tvFindParkingLot;// 找车位
	// private LinearLayout tvCheckViolation;// 查违章
	private LinearLayout tvFootPrint;// 足迹
	private LinearLayout tvCarReport;// 用车报告
	private LinearLayout tvServiceHome;// 找车位
	private LinearLayout tvCarConsult;// 车咨询
	private LinearLayout tvFindGasStation;// 找加油站
	private Panel mPanel;
	private RelativeLayout ll_myCar;
	private LinearLayout ll_main_rightBottom;
	private FontAwesomeView img_go;

	/**
	 * 定位工具
	 */
	private LocationUtil mLocationUtil;
	private SharedPreferences spLocation;
	private String historyCity;
	private boolean isLogined = false;// 判断是否登陆 true登陆
	private boolean hasCar = false;// 是否有车
	private boolean isFirst = true;// 判断是否第一次登陆

	private String specialLcoationChongqing;
	private String specialLcoationBeijing;
	private String specialLcoationTianjin;
	private String specialLcoationShanghai;
	private String specialLcoationHongKong;
	private String specialLcoationAomen;

	private RelativeLayout one_relayout;// 第一个动画的外层布局
	private RelativeLayout two_relayout;// 第二个动画的外层布局
	private LinearLayout there_relayout;// 第三个动画的布局

	// 1
	private ImageView main_iv_check;
	private LeiDaView search_device_view;

	// 2
	private ImageView main_iv_car;// 汽车
	private TwoAnimationLayout mian_rel_two;// 表层布局
	private Animation caranimation;// 车的动画
	private Animation main_two_reanimation;// 外层布局动画
	private TextView main_tv_checkstatus;// 检测项
	private TextView mian_tv_num;// 检测数量

	// 3
	private LoginMessage message;

	private LinearLayout left_xian;
	private LinearLayout main_lin_xian1;
	private LinearLayout main_lin_xian2;
	private SlidingPaneLayout layout;
	private TextView main_tv_baifenbi;
	/**
	 * 正在检测
	 */
	private TextView tv1;

	/**
	 * 获取外层布局的高度
	 */
	private RelativeLayout main_relayout;
	/**
	 * 那个圆
	 */
	private RelativeLayout main_re_yuan;
	/**
	 * 检测是数组
	 */
	private String[] data = { "总里程", "空燃比系数", "蓄电池电压", "气节门开度", "OBD时间",
			"发动机负荷", "发动机运行时间", "百公里油耗", "剩余油量", "发动机转速", "车速", "环境温度", "水温",
			"距下次年检", "距离上次保养", "故障码" };

	private void initView(View view) {
		ll_main_rightBottom = (LinearLayout) view
				.findViewById(R.id.ll_main_rightBottom);
		img_go = (FontAwesomeView) view.findViewById(R.id.img_go);
		this.one_relayout = (RelativeLayout) view
				.findViewById(R.id.one_relayout);
		this.two_relayout = (RelativeLayout) view
				.findViewById(R.id.two_relayout);
		this.there_relayout = (LinearLayout) view
				.findViewById(R.id.there_relayout);

		this.main_iv_car = (ImageView) view.findViewById(R.id.main_iv_car);
		this.mian_rel_two = (TwoAnimationLayout) view
				.findViewById(R.id.mian_rel_two);

		this.main_iv_car = (ImageView) view.findViewById(R.id.main_iv_car);
		this.mian_rel_two = (TwoAnimationLayout) view
				.findViewById(R.id.mian_rel_two);
		this.two_relayout = (RelativeLayout) view
				.findViewById(R.id.two_relayout);

		this.main_tv_checkstatus = (TextView) view
				.findViewById(R.id.main_tv_checkstatus);

		this.mian_tv_num = (TextView) view.findViewById(R.id.mian_tv_num);

		this.left_xian = (LinearLayout) view.findViewById(R.id.left_xian);
		this.main_lin_xian1 = (LinearLayout) view
				.findViewById(R.id.main_lin_xian5);
		this.main_lin_xian2 = (LinearLayout) view
				.findViewById(R.id.main_lin_xian2);

		this.tv1 = (TextView) view.findViewById(R.id.tv1);
		this.main_tv_baifenbi = (TextView) view
				.findViewById(R.id.main_tv_baifenbi);

		main_relayout = (RelativeLayout) view.findViewById(R.id.main_relayout);

		this.there_four = (LinearLayout) view.findViewById(R.id.there_four);

	}

	// private LinearLayout.LayoutParams lp;
	private boolean isLsftSize = true;
	/**
	 * 线的外层布局
	 */
	private RelativeLayout.LayoutParams lp;
	/**
	 * 线布局的副边距
	 */
	private int leftsize;

	/**
	 * 启动第二个动画
	 */
	private void initTwoAnimation() {

		disableSubView(false);
		initData();
		setImagePhoneGone();
		leftsize = DisplayUtil.dip2px(mContext, 60);
		one_relayout.setVisibility(View.GONE);
		mian_rel_two.setVisibility(View.VISIBLE);
		this.main_iv_car.setVisibility(View.VISIBLE);
		/** 获取车的宽度 ***/
		this.getCarwideth();
		this.layout();
		// 车的动画
		caranimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.caranimation);
		// 外层布局的动画
		main_two_reanimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.main_two_reanimation);
		// 设置查值器
		main_two_reanimation
				.setInterpolator(new AnticipateOvershootInterpolator());
		caranimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				setTwoTextVisible();
				/** 设置默认的参数 **/
				setMoren();
				/** 启动左边的动画 **/
				leftCarDistanceAnimation();

			}

		});
		this.main_iv_car.startAnimation(caranimation);
	}

	/**
	 * 布局的处理
	 */
	public void layout() {
		lp = new RelativeLayout.LayoutParams(leftsize,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(-leftsize, 0, 0, 0);
		left_xian.setLayoutParams(lp);
		left_xian.setGravity(Gravity.CENTER);
		left_xian.setBackgroundColor(Color.WHITE);
		left_xian.getBackground().setAlpha(0);
		LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 0.9f);
		LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 0.1f);
		main_lin_xian1.setLayoutParams(params1);
		main_lin_xian2.setLayoutParams(params2);
		main_lin_xian1.setBackgroundColor(Color.WHITE);
		main_lin_xian1.getBackground().setAlpha(0);
		main_lin_xian2.setBackgroundColor(Color.WHITE);
		main_lin_xian2.getBackground().setAlpha(0);
	}

	private Animation animation2;
	private Animation animation3;

	/**
	 * 到左边的动画
	 */
	public void leftCarDistanceAnimation() {

		if (mian_rel_two.getAnimation() != null
				&& left_xian.getAnimation() != null) {
			mian_rel_two.clearAnimation();
			left_xian.clearAnimation();
			mian_rel_two.setAnimation(null);
			left_xian.setAnimation(null);
		}
		mian_rel_two.startAnimation(animation3);
		left_xian.startAnimation(animation2);
		animation2.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// setzCarQianXY();
				main_tv_baifenbi.setVisibility(View.VISIBLE);
				startSetMaginleft();
			}
		});

	}

	private int[] time = { 200, 300, 40, 500, 600, };
	/**
	 * 存放时间的集合
	 */
	private int[] allTime;
	/**
	 * 存放进度的集合
	 */
	private int jinduNun;

	/**
	 * 开始设置坐标聚
	 */
	public void startSetMaginleft() {
		getTime();
		jinduNun = 0;
		int time1 = allTime[0];
		/** 第一次的速度 **/
		Ssudu = (oneDistance / time1) * 10;
		/** 执行中间的动画 **/
		excuteUpdater();
	}

	/**
	 * 获取时间
	 */
	public void getTime() {
		Random random = new Random();
		getCarwideth();
		allTime = new int[16];
		for (int i = 0; i < 16; i++) {
			int nextInt = random.nextInt(5);
			int oneTimime = time[nextInt];
			allTime[i] = oneTimime;
		}
	}

	/**
	 * 第二次清空数据
	 */
	public void setNull() {
		allTime = null;
		leftCarDistanceJilu = leftCarDistance;
		jiluTime = 0;
		dierboS = 0;
		left_xian.setX(-leftsize);
		mian_rel_two.setX(0);
		layout();
	}

	/**
	 * 已经执行过的时间
	 */
	private int jiluTime = 0;
	/**
	 * 单位速度
	 */
	private float Ssudu = 0;

	/**
	 * 第二波动画移动距离
	 */
	private float dierboS;

	/**
	 * 更新Ui刷新事项
	 */
	private void excuteUpdater() {
		timerexcute = new Timer();
		TimerTask taskexcute = new TimerTask() {
			@Override
			public void run() {
				jiluTime += 10;
				leftCarDistanceJilu += Ssudu;
				dierboS += Ssudu;
				Bundle bundle = new Bundle();
				bundle.putFloat("leftCarDistance", leftCarDistanceJilu);
				bundle.putInt("jinduNun", jinduNun);
				bundle.putFloat("dierboS", dierboS);

				if (jinduNun < 16) {
					if (jiluTime >= getZhixingshijian(jinduNun)) {
						if (jinduNun <= 14) {
							jinduNun += 1;
							int time2 = allTime[jinduNun];
							/** 第一次的速度 **/
							Ssudu = (oneDistance / time2) * 10;
						} else {
							Ssudu = (rightCarDistance / 500) * 10;
						}

					}
				}
				Message message = new Message();
				message.obj = bundle;
				message.what = 2;
				handler1.sendMessage(message);
			}
		};
		timerexcute.schedule(taskexcute, 0, 10);
	}

	/**
	 * 计算执行到第几个时间的总和
	 * 
	 * @param num
	 */
	public int getZhixingshijian(int num) {
		int allzhixingtime = 0;
		for (int i = 0; i <= num; i++) {
			allzhixingtime = allTime[i] + allzhixingtime;
		}
		return allzhixingtime;
	}

	@SuppressLint("HandlerLeak")
	Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				break;
			case 2:
				Bundle bundle = (Bundle) msg.obj;
				float int1 = bundle.getFloat("leftCarDistance");
				left_xian.setX((int1 - (leftsize / 2)));
				mian_rel_two.setX(int1);
				if (int1 >= scressWidth) {
					timerexcute.cancel();
					/** 启动第三个动画 **/
					startAnitionThere();
				}
				float float1 = bundle.getFloat("dierboS");
				DecimalFormat formater = new DecimalFormat("#0.##");
				float bi = float1 / cartDistance;
				String format = formater.format(bi);
				double baifenbi = Double.parseDouble(format);
				int sbaifenbi = (int) (baifenbi * 100);
				if (sbaifenbi > 100) {
					main_tv_baifenbi.setText(100 + "%");
					main_tv_baifenbi.setVisibility(View.INVISIBLE);
				} else {
					main_tv_baifenbi.setText(sbaifenbi + "%");
				}

				int int2 = bundle.getInt("jinduNun");
				if (int2 < 16) {
					main_tv_checkstatus.setText(data[int2]);
					mian_tv_num.setText("(" + (int2 + 1) + "/" + 16 + ")");
				}
				break;
			}
		}
	};

	/**
	 * 屏幕宽度
	 */
	@SuppressWarnings("unused")
	private float scressWidth;
	/**
	 * 车左边的距离
	 */
	private float leftCarDistance;

	/**
	 * 车左边的距离记录，更新坐标
	 */
	private float leftCarDistanceJilu;
	/**
	 * 车的距离
	 */
	private float cartDistance;

	/**
	 * 车尾后边的距离
	 */
	private float rightCarDistance;

	/**
	 * 16、1份的距离
	 */
	private float oneDistance;

	/**
	 * 车加左边距
	 */
	// private float leftallDistance;

	/**
	 * 获取车的宽度
	 */
	public void getCarwideth() {
		/**
		 * 回调拿到坐标，，会一直执行
		 */
		ViewTreeObserver vto = main_iv_car.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (isLsftSize) {
					cartDistance = main_iv_car.getWidth();
					scressWidth = ScreenUtils.getScressWidth(mContext);
					leftCarDistance = (scressWidth - cartDistance) / 2;
					rightCarDistance = (scressWidth - cartDistance) / 2;
					oneDistance = cartDistance / 16;
					// leftallDistance = leftCarDistance + cartDistance;
					leftCarDistanceJilu = (scressWidth - cartDistance) / 2;
					isLsftSize = false;
					animation2 = new TranslateAnimation((leftsize / 2),
							(leftCarDistance + (leftsize / 2)), 0, 0);
					animation3 = new TranslateAnimation(0, leftCarDistance, 0,
							0);
					animation2.setDuration(500);
					animation3.setDuration(500);
				}
				return true;
			}
		});
	}

	// 动画布局的总大小
	// private float outLength;

	/**
	 * 获取动画布局
	 */
	public void getMainAnimationideth() {
		/**
		 * 回调拿到坐标，，会一直执行
		 */
		ViewTreeObserver vto = main_relayout.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (isLsftSize) {
					// outLength = main_relayout.getWidth();
				}
				return true;
			}
		});
	}

	/**
	 * 初始话第三部分
	 */
	public void startAnitionThere() {

		mian_rel_two.setVisibility(View.GONE);
		/** 打开第三个动画 **/
		OpenThereAnimation();
	}

	// private int num = 0;

	private void setMoren() {
		this.main_tv_checkstatus.setText(data[0]);
		this.mian_tv_num.setText("(" + 1 + "/" + 16 + ")");
		// this.num = 0;
	}

	private Timer timerexcute;

	//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		specialLcoationChongqing = mContext
				.getString(R.string.special_location_chongqing);
		specialLcoationBeijing = mContext
				.getString(R.string.special_location_beijing);
		specialLcoationTianjin = mContext
				.getString(R.string.special_location_tianjin);
		specialLcoationShanghai = mContext
				.getString(R.string.special_location_shanghai);
		specialLcoationHongKong = mContext
				.getString(R.string.special_location_hongkong);
		specialLcoationAomen = mContext
				.getString(R.string.special_location_aomen);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_main, container, false);

		httpBiz = new HttpBiz(mContext);
		init(view);
		return view;
	}

	// private OnMenuListener onMenuListener = new OnMenuListener() {
	//
	// @Override
	// public void openMenu() {
	// imgPhone.setImageResource(R.drawable.chedongtai_youce22x);
	// }
	//
	// @Override
	// public void closeMenu() {
	// imgPhone.setImageResource(R.drawable.chedongtai_youce12x);
	// }
	// };

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 判断账号是否被T或是在别处登陆
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false))
			return;

	}

	private void init(View view) {
		message = LoginMessageUtils.getLoginMessage(mContext);
		imgUser = (XCRoundImageView) view.findViewById(R.id.img_user);
		lLayoutUser = (LinearLayout) view.findViewById(R.id.llayout_user);
		tvMore = (LinearLayout) view.findViewById(R.id.panelHandle);
		tvMoreSub = (TextView) view.findViewById(R.id.tv_more_sub);
		this.main_re_yuan = (RelativeLayout) view
				.findViewById(R.id.main_re_yuan);
		tvReturn = (LinearLayout) view.findViewById(R.id.tv_return);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		imgWeather = (ImageView) view.findViewById(R.id.img_weather);
		rLayoutMessage = (RelativeLayout) view.findViewById(R.id.img_message);
		tvMessage = (TextView) view.findViewById(R.id.tv_message);

		// tvTips = (TextView) view.findViewById(R.id.tv_tip);
		rLayoutTip = (RelativeLayout) view.findViewById(R.id.rlayout_tip);

		tvPhone = (TextView) view.findViewById(R.id.tv_phone);
		imgPhone = (ImageView) view.findViewById(R.id.img_phone);

		imgVip = (ImageView) view.findViewById(R.id.img_vip);

		tvCarDynamic = (LinearLayout) view.findViewById(R.id.tv_car_dynamic);
		tvNavigation = (LinearLayout) view.findViewById(R.id.tv_navigation);
		tvFindParkingLot = (LinearLayout) view
				.findViewById(R.id.tv_find_parkinglot);
		// tvCheckViolation = (LinearLayout) view
		// .findViewById(R.id.mycar_check_violation);
		tvFootPrint = (LinearLayout) view.findViewById(R.id.mycar_my_footprint);
		tvCarReport = (LinearLayout) view.findViewById(R.id.mycar_report);
		tvServiceHome = (LinearLayout) view.findViewById(R.id.service_car);
		tvCarConsult = (LinearLayout) view.findViewById(R.id.mycar_car_consult);
		tvFindGasStation = (LinearLayout) view
				.findViewById(R.id.mycar_find_gasstation);
		mPanel = (Panel) view.findViewById(R.id.bottomPanel);
		ll_myCar = (RelativeLayout) view.findViewById(R.id.ll_myCar);
		ll_myCar.setOnClickListener(onClickListener);
		lLayoutUser.setOnClickListener(onClickListener);
		tvMore.setOnClickListener(onClickListener);
		tvReturn.setOnClickListener(onClickListener);
		imgWeather.setOnClickListener(onClickListener);
		rLayoutMessage.setOnClickListener(onClickListener);
		tvPhone.setOnClickListener(onClickListener);
		imgPhone.setOnClickListener(onClickListener);
		imgPhone.setOnClickListener(onClickListener);
		tvCarDynamic.setOnClickListener(onClickListener);
		tvNavigation.setOnClickListener(onClickListener);
		tvFindParkingLot.setOnClickListener(onClickListener);
		// tvCheckViolation.setOnClickListener(onClickListener);
		tvFootPrint.setOnClickListener(onClickListener);
		tvCarReport.setOnClickListener(onClickListener);
		tvServiceHome.setOnClickListener(onClickListener);
		tvCarConsult.setOnClickListener(onClickListener);
		tvFindGasStation.setOnClickListener(onClickListener);
		mPanel.setOnPanelListener(this);
		this.main_iv_check = (ImageView) view.findViewById(R.id.main_iv_check);
		search_device_view = (LeiDaView) view
				.findViewById(R.id.search_device_view);
		// this.main_iv_check = (ImageView)
		// view.findViewById(R.id.main_iv_check);
		// search_device_view = (LeiDaView) view
		// .findViewById(R.id.search_device_view);
		search_device_view.setWillNotDraw(false);
		this.main_iv_check.setOnClickListener(onClickListener);

		spLocation = mContext.getSharedPreferences(
				MyMapUtils.LOCATION_PREFERENCES_NAME, Context.MODE_PRIVATE);

		// 清空位置信息

		// 动画
		initAnimation();
		tvReturn.setAnimation(animationShow);
		tvMore.setAnimation(animationShow);
		tvMoreSub.setAnimation(animationShow);

		// 判断是否登陆，并设置头像
		setUserImage();

//		setResideMenu();

		// 开启地位功能，生命周期与此fragment相同
		// /***第二个动画***/
		/*** 第二个动画 ***/
		initView(view);
		initThereAnimationView(view);
	}

	/**
	 * 判断是否登陆并设置头像(刷新操作)
	 */
	public void setUserImage() {
		isLogined = false;
		hasCar = false;
		message = LoginMessageUtils.getLoginMessage(mContext);
		if (message == null) {
			imgUser.setImageResource(R.drawable.info_touxiang_moren);
			imgVip.setVisibility(View.INVISIBLE);
			tvTitle.setText(mContext.getString(R.string.information_no_login));
			setLeiDaGone();
			return;
		}

		if (StringUtil.isEmpty(message.getUid())) {
			imgUser.setImageResource(R.drawable.info_touxiang_moren);
			imgVip.setVisibility(View.INVISIBLE);
			tvTitle.setText(mContext.getString(R.string.information_no_login));
			setLeiDaGone();

			return;
		}
		isLogined = true;// uid不为null，判断为已登陆

		Car car = message.getCar();
		if (!StringUtil.isEmpty(car) && !StringUtil.isEmpty(car.getDevice())) {
			hasCar = true;// carid不为Null,判断为绑定有车
		}
		XUtilsImageLoader.getxUtilsImageLoader(mContext,
				R.drawable.info_touxiang_moren,imgUser,
				API.DOWN_IMAGE_URL + message.getPhoto());
		tvTitle.setText(message.getNick());
		imgVip.setVisibility(View.VISIBLE);
		setLeiDaVisible();

	}

	/**
	 * 设置消息数目
	 * 
	 * @param number
	 */
	public void setMessageNumber(String number) {
		if (tvMessage == null) {
			return;
		}
		if (number == null || "0".equals(number)
				|| "null".equalsIgnoreCase(number) || "".equals(number)) {
			tvMessage.setVisibility(View.GONE);
			return;
		}
		tvMessage.setVisibility(View.VISIBLE);
		tvMessage.setText(number);
	}

	/**
	 * 显示头部用户信息
	 */
	public void setHeadShow() {
		lLayoutUser.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏头部用户信息
	 */
	public void setHeadHide() {
		lLayoutUser.setVisibility(View.INVISIBLE);
	}

	public void setHasCar(boolean hasCar) {
		this.hasCar = hasCar;
	}

	BDLocationListener locationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// 重庆市-渝中区
			historyCity = location.getProvince();
			Log.i("zzq", "location:" + historyCity);
			boolean cityChangeFlag = false;
			if (historyCity != null
					&& (historyCity.contains(specialLcoationChongqing)
							|| historyCity.contains(specialLcoationBeijing)
							|| historyCity.contains(specialLcoationHongKong)
							|| historyCity.contains(specialLcoationTianjin)
							|| historyCity.contains(specialLcoationAomen) || historyCity
								.contains(specialLcoationShanghai))) {
				historyCity = location.getCity() + "-" + location.getDistrict();
			} else if (historyCity != null) {
				historyCity = location.getProvince() + "-" + location.getCity();
			}
			String preHisCity = spLocation.getString("historyCity", null);
			if (preHisCity != null && preHisCity.equals(historyCity)) {
				cityChangeFlag = true;
			}

			spLocation.edit()
					.putString("latitude", location.getLatitude() + "")
					.putString("longitude", location.getLongitude() + "")
					.putString("address", location.getAddrStr())
					.putString("province", location.getProvince())
					.putString("district", location.getDistrict())
					.putString("historyCity", historyCity)
					.putBoolean("cityChangeFlag", cityChangeFlag)
					.putFloat("radius", location.getRadius())
					.putString("city", location.getCity()).commit();

		}
	};

	private void setResideMenu() {
		MainNewActivity parentActivity = (MainNewActivity) mContext;
//		layout = parentActivity.getLayout();
		// try {
		// MainActivity parentActivity = (MainActivity) mContext;
		// resideMenu = parentActivity.getResideMenu();
		// // add gesture operation's ignored views
		// // resideMenu.addIgnoredView(gallery);
		// // resideMenu.setMenuListener(onMenuListener);
		// } catch (Exception e) {
		// resideMenu = null;
		// }
	}

	private void showCustomDialog() {
		Builder builder = new CustomDialog.Builder(mContext);
		builder.setMessage(mContext.getString(R.string.home_no_device));
		builder.setTitle(mContext.getString(R.string.remind));
		builder.setPositiveButton(mContext.getString(R.string.home_goto_bind),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivity(new Intent(mContext, AddCarActivity.class));
					}
				});
		builder.setNegativeButton(mContext.getString(R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	/**
	 * 对是否登陆和是否有车处理
	 * 
	 * @param cls
	 */
	private void isLoginOrHasCar(Class<?> cls) {
		if (!isLogined) {
			intent.setClass(mContext, LoginActivity.class);
		} else if (!hasCar) {
			showCustomDialog();
		} else {
			intent.setClass(mContext, cls);
		}
	}

	/**
	 * 判断是否登陆
	 * 
	 * @param cls
	 */
	private void isLogin(Class<?> cls) {
		if (!isLogined) {
			intent.setClass(mContext, LoginActivity.class);
		} else {
			intent.setClass(mContext, cls);
		}
	}

	OnPageChangeListener pageChangelistener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {

		}

		@Override
		public void onPageScrolled(int position1, float arg1, int position2) {

		}

		@Override
		public void onPageScrollStateChanged(int position) {

		}
	};

	/**
	 * 主界面所有控件点击开关
	 * 
	 * @param isClicked
	 */
	public void disableSubView(boolean isClicked) {
		Log.i("zzqq", "--disableSubView--" + isClicked);
//		if (isClicked) {
//			MainActivity.instance.showLLeftSide();
//		} else {
//			MainActivity.instance.closeLeftSide();
//		}

		rLayoutMessage.setClickable(isClicked);
		rLayoutMessage.setEnabled(isClicked);
		lLayoutUser.setClickable(isClicked);
		lLayoutUser.setEnabled(isClicked);
		tvCarDynamic.setClickable(isClicked);
		tvCarDynamic.setEnabled(isClicked);
		tvNavigation.setClickable(isClicked);
		tvNavigation.setEnabled(isClicked);
		tvFindParkingLot.setClickable(isClicked);
		tvFindParkingLot.setEnabled(isClicked);
		tvFootPrint.setClickable(isClicked);
		tvFootPrint.setEnabled(isClicked);
		tvCarReport.setClickable(isClicked);
		tvCarReport.setEnabled(isClicked);
		tvServiceHome.setClickable(isClicked);
		tvServiceHome.setEnabled(isClicked);
		tvMore.setClickable(isClicked);
	}

	private Intent intent;
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			/**
			 * 快速点击忽略处理
			 */
			if (ButtonUtils.isFastClick()) {
				return;
			}
			intent = new Intent();
			switch (view.getId()) {
			case R.id.ll_myCar:
				break;
			case R.id.llayout_user:
				if (!isLogined) {
					LoginMessageUtils.showDialogFlag = true;
					intent.setClass(mContext, LoginActivity.class);
				} else {
					layout.openPane();

				}
				break;
			case R.id.img_weather:
				intent.setClass(mContext, WeatherActivity.class);
				break;
			case R.id.img_message:

//				((MainActivity) mContext).startMessageCenter();

				break;
			case R.id.panelHandle:
				tvMore.setVisibility(View.GONE);
				mPanel.setOpen(true, true);
				break;
			case R.id.tv_return:
				tvReturn.setVisibility(View.GONE);
				tvMore.setVisibility(View.VISIBLE);
				tvMoreSub.setVisibility(View.INVISIBLE);
				tvMoreSub.startAnimation(animationShow);
				mPanel.setOpen(false, true);
				break;
			case R.id.img_phone:
			case R.id.tv_phone:
				if (!isLogined) {
					intent.setClass(mContext, LoginActivity.class);
				} else {
					// turnToWashCar();
					if (!hasCar()) {
						showCustomDialog();
					} else {
						intent.setClass(mContext, WashCarActivity.class);
					}
				}
				break;
			case R.id.tv_car_dynamic:
				isLoginOrHasCar(CarDynamicActivity.class);
				break;
			case R.id.tv_navigation:
				intent.setClass(mContext, NavigationActivity.class);
				intent.putExtra("isLogined", isLogined);
				intent.putExtra("hasCar", hasCar);
				break;
			case R.id.tv_find_parkinglot:
				intent.setClass(mContext, FindcarActivity.class);
				break;
			// case R.id.mycar_check_violation:
			// // TODO 查违章
			// intent.setClass(mContext, QueryIllegalActivity.class);
			// break;
			case R.id.mycar_my_footprint:
				isLoginOrHasCar(FootmarkActivity.class);
				break;
			case R.id.mycar_report:
				isLoginOrHasCar(CarReportActivity.class);
				break;
			case R.id.mycar_car_consult:
				intent.setClass(mContext, CarInformationActivity.class);
				break;
			case R.id.mycar_find_gasstation:
				intent.setClass(mContext, GasStationActivity.class);
				break;
			case R.id.main_iv_check:
				message = LoginMessageUtils.getLoginMessage(mContext);
				if (message == null || message.getUid() == null) {
					intent = new Intent();
					intent.setClass(mContext, LoginActivity.class);
				} else {
					if (message.getCar().getCid() == null
							|| message.getCar().getDevice() == null
							|| "".equals(message.getCar().getCid())
							|| "".equals(message.getCar().getDevice())) {
						showToast(R.string.mile_toast_nodivice);
						intent = new Intent(getActivity(), AddCarActivity.class);
						// 发送广播 刷新主界面界面
						Intent mIntent = new Intent();
						Constant.CURRENT_REFRESH = Constant.LOGIN_REFRESH;
						mIntent.setAction(Constant.REFRESH_FLAG);
						mContext.sendBroadcast(mIntent);
					} else {
						/** 执行第二个动画 ***/
						openTwoAnimation();
					}
				}

				break;

			case R.id.main_btn_retesting:// 重新检测
				retesting();
				break;
			case R.id.main_btn_seedetails:// 查看检测详情
				seeDetectDetails();
				break;
			case R.id.main_iv_close:// 关闭检查
				closeDetect();
				break;
			case R.id.main_iv_close1:// 关闭检测失败
				closeDetailFail();
				break;
			default:
				break;
			}
			try {
				startActivity(intent);
			} catch (Exception e) {
				// intent没有setClass，忽略处理
			}
		}

	};

	/**
	 * 跳转到洗车店
	 */
	private void turnToWashCar() {
		RequestParams mRequestParams = new RequestParams();
		if (!isLogined()) {
			return;
		}
		if (!hasCar()) {
			showCustomDialog();
		}
		showProgressDialog();
		mRequestParams.addBodyParameter("uid", loginMessage.getUid());
		mRequestParams.addBodyParameter("key", loginMessage.getKey());
		mRequestParams.addBodyParameter("cid", loginMessage.getCar().getCid());
		Log.i("zqtest", "uid:" + loginMessage.getUid() + "--key--:"
				+ loginMessage.getKey() + "--cid--:"
				+ loginMessage.getCar().getCid());
		httpBiz.httPostData(1001, API.WASHCAR_CHECKORDER, mRequestParams, this);
	}

	/**
	 * 请求数据
	 */
	private void initData() {
		if (isOhterLogin) {
			String uid = message.getUid();
			String key = message.getKey();
			String device = message.getCar().getDevice();
			String cid = message.getCar().getCid();
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", uid);
			params.addBodyParameter("key", key);
			params.addBodyParameter("device", device);
			params.addBodyParameter("cid", cid);
			Log.i("result", "uid = " + uid + "   key = " + key + "   device="
					+ device + "   cid = " + cid);
			httpBiz.httPostData(20000, API.REAL_TIME_CONDITION_URL, params,
					MyCarFragment.this);
		}
	}

	private ArrayList<String> infoList;// 检测信息
	private ArrayList<AkeytestInfo> Akeyone;// 第一个
	private ArrayList<AkeytestInfo> Akeytwo;// 第二个
	private ArrayList<AkeytestInfo> Akeythere;// 第三个
	private AkeyTextAllInfo mAkeyTextAllInfo;// 一键检测所有信息
	private boolean isOhterLogin = true;// 其他地方登录

	@Override
	public void receive(int type, String data) {
		disMissProgressDialog();
		if (type == 20000) {
			doAboutCheck(data);
		} else if (type == 1001) {
			Log.i("zqtest", data);
			doAboutWashcar(data);
		} else if (type == 400) {
		} else if (type == 401) {
			showToast(R.string.network_isnot_available);
		}
	}

	/**
	 * 处理洗车店
	 * 
	 * @param data
	 */
	private void doAboutWashcar(String data) {
		try {
			JSONObject json = new JSONObject(data);
			if ("SUCCESS".equals(json.get("operationState"))) {
				json = json.getJSONObject("data");
				json = json.getJSONObject("data");

				String state = json.getString("state");

				if ("0".equals(state)) {
					Intent intent = new Intent(mContext,
							WashcarDetailsActivity.class).putExtra("index",
							WashcarDetailsActivity.INDEX_ORDER_DETAIL);
					WashcarVO vo = new WashcarVO();
					vo.setAddress(json.getString("address"));
					vo.setCwId(json.getString("cw_id"));
					vo.setDes(json.getString("des"));
					vo.setLat(json.getString("lat"));
					vo.setLon(json.getString("lon"));
					vo.setDate(json.getString("date"));
					vo.setName(json.getString("name"));
					vo.setPic(json.getString("pic"));
					vo.setTel(json.getString("tel"));
					vo.setUno(json.getString("uno"));
					vo.setTime(json.getString("time"));
					intent.putExtra("vo", vo);
					startActivity(intent);
				} else {
					startActivity(new Intent(mContext, WashCarActivity.class));
				}
			} else if ("RELOGIN".equalsIgnoreCase(json
					.getString("operationState"))) {
				// DialogTool.getInstance(mContext).showConflictDialog();
				startActivity(new Intent(mContext, WashCarActivity.class));
			} else {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理一键检测
	 * 
	 * @param data
	 */
	private void doAboutCheck(String data) {
		infoList = new ArrayList<String>();
		Akeyone = new ArrayList<AkeytestInfo>();
		Akeytwo = new ArrayList<AkeytestInfo>();
		Akeythere = new ArrayList<AkeytestInfo>();
		AkeytestInfo mAkeytestInfo;
		String unit;
		String name;
		String value;
		String fault;
		String typeItem;
		if (data != null && !"".equals(data)) {
			try {
				JSONObject jsondata = new JSONObject(data);

				if ("SUCCESS".equals(jsondata.getString("operationState"))) {
					JSONObject jsonObject = jsondata.optJSONObject("data");
					String carState = jsonObject.optString("carState");
					String logo = jsonObject.optString("logo");
					String maintainState = jsonObject
							.optString("maintainState");
					String plate = jsonObject.optString("plate");
					String dtc = jsonObject.optString("dtc");
					String synthesis = jsonObject.optString("synthesis");
					JSONArray list = jsonObject.optJSONArray("list");
					JSONObject item;
					for (int i = 0; i < list.length(); i++) {

						item = list.optJSONObject(i);
						unit = item.optString("unit");
						name = item.optString("name");
						value = item.optString("value");
						fault = item.optString("fault");
						typeItem = item.optString("type");
						infoList.add(name);
						mAkeytestInfo = new AkeytestInfo(unit, name, value,
								fault, typeItem);
						if ("1".equals(typeItem)) {
							Akeyone.add(mAkeytestInfo);
						}
						if ("2".equals(typeItem)) {
							Akeytwo.add(mAkeytestInfo);
						}
						if ("3".equals(typeItem)) {
							Akeythere.add(mAkeytestInfo);
						}
					}
					mAkeyTextAllInfo = new AkeyTextAllInfo(carState, logo,
							maintainState, plate, dtc, synthesis, Akeyone,
							Akeytwo, Akeythere);
				}
				// 帐号其他地方登录
				else if ("RELOGIN".equals(jsondata.getString("operationState"))) {
					DialogTool.getInstance(mContext).showConflictDialog();
					isOhterLogin = false;
				} else {
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 开始第二个动画
	 */
	public void openTwoAnimation() {
		search_device_view.setSearching(false);
		two_relayout.setVisibility(View.VISIBLE);
		initTwoAnimation();
	}

	private Animation animationShow;

	private void initAnimation() {
		animationShow = AnimationUtils.loadAnimation(mContext,
				R.anim.home_tv_show);
	}

	@Override
	public void onPanelClosed(Panel panel) {
		tvMoreSub.setVisibility(View.VISIBLE);
		tvMoreSub.startAnimation(animationShow);
	}

	@Override
	public void onPanelOpened(Panel panel) {
		tvReturn.setVisibility(View.VISIBLE);
		tvReturn.startAnimation(animationShow);
		tvMoreSub.setVisibility(View.GONE);
	}

	private ImageView main_iv_close;// 关闭
	private TextView main_tv_leixing;// 类型
	private TextView main_tv_vehicle;// 车辆状态
	private TextView main_tv_yinfo;// 养护状态

	private TextView main_tv_fault;// 故障检测
	private ImageView main_iv_checkstatus;// 综合
	private TextView main_btn_retesting;// 重新检测

	private ImageView main_iv_carlogo;// 车的logo

	private TextView main_btn_seedetails;// 查看详情

	private Animation ainimation;

	private ImageView iv_pb_oneD;// 第一个底部的进度条

	private ImageView iv_pb_oneD2;// 第二个底部的进度条
	/** 让动画只执行一次 **/
	private boolean donghua;

	public void OnenFourAnimation() {

		ainimation = AnimationUtils.loadAnimation(mContext,
				R.anim.main_threr_in);
	}

	/**
	 * 初始化第三个动画
	 */
	private void OpenThereAnimation() {
		disableSubView(true);
		this.setTwoTextGone();
		getPBwideth();
		ainimation = AnimationUtils.loadAnimation(mContext,
				R.anim.main_threr_in);
		if (there_relayout.getAnimation() != null) {
			there_relayout.clearAnimation();
			there_relayout.setAnimation(null);
		}
		if (there_four.getAnimation() != null) {
			there_four.clearAnimation();
			there_four.setAnimation(null);
		}
		ainimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (donghua) {
					if (mAkeyTextAllInfo != null) {
						startPbAnimation(Integer.parseInt(mAkeyTextAllInfo
								.getCarState()), Integer
								.parseInt(mAkeyTextAllInfo.getMaintainState()));
						setReselt();
					}
					donghua = false;
				}

			}
		});
		if (mAkeyTextAllInfo != null) {
			donghua = true;
			there_relayout.setVisibility(View.VISIBLE);
			there_relayout.startAnimation(ainimation);

		} else {
			donghua = false;
			there_four.setVisibility(View.VISIBLE);
			there_four.startAnimation(ainimation);
		}

	}

	/**
	 * 第四哥布局
	 */
	private LinearLayout there_four;
	/**
	 * 第四个的关闭
	 */
	private ImageView main_iv_close1;

	/**
	 * 赋值结果
	 */
	public void setReselt() {

		if (mAkeyTextAllInfo != null) {
			main_tv_leixing.setText(mAkeyTextAllInfo.getPlate());

			String carState = mAkeyTextAllInfo.getCarState();
			String maintainState = mAkeyTextAllInfo.getMaintainState();

			main_tv_vehicle.setText(carState
					+ mContext.getString(R.string.score));

			main_tv_yinfo.setText(maintainState
					+ mContext.getString(R.string.score));

			int IntcarState = Integer.parseInt(carState);
			int IntmaintainState = Integer.parseInt(maintainState);

			if (IntcarState >= 80) {
				main_tv_vehicle.setTextColor(getResources().getColor(
						R.color.main_text_blue));
			} else if (IntcarState >= 60 && IntcarState < 80) {
				main_tv_vehicle.setTextColor(getResources().getColor(
						R.color.main_text_cengse));
			} else if (IntcarState < 60) {
				main_tv_vehicle.setTextColor(getResources().getColor(
						R.color.main_text_red));
			}

			if (IntmaintainState >= 80) {
				main_tv_yinfo.setTextColor(getResources().getColor(
						R.color.main_text_blue));
			} else if (IntmaintainState >= 60 && IntmaintainState < 80) {
				main_tv_yinfo.setTextColor(getResources().getColor(
						R.color.main_text_cengse));
			} else if (IntmaintainState < 60) {
				main_tv_yinfo.setTextColor(getResources().getColor(
						R.color.main_text_red));
			}

			String dtc = mAkeyTextAllInfo.getDtc();
			main_tv_fault.setText(dtc);
			if ("未发现故障码".equals(dtc)) {
				main_tv_fault.setTextColor(getResources().getColor(
						R.color.main_text4));
			} else {
				main_tv_fault
						.setTextColor(getResources().getColor(R.color.red));
			}

			String synthesis = mAkeyTextAllInfo.getSynthesis();

			if ("优秀".equals(synthesis)) {
				main_iv_checkstatus
						.setBackgroundResource(R.drawable.home_jiance_youxiu);
			} else if ("良好".equals(synthesis)) {
				main_iv_checkstatus
						.setBackgroundResource(R.drawable.home_jiance_lianghao);
			} else if ("一般".equals(synthesis)) {
				main_iv_checkstatus
						.setBackgroundResource(R.drawable.home_jiance_yiban);
			} else if ("较差".equals(synthesis)) {
				main_iv_checkstatus
						.setBackgroundResource(R.drawable.home_jiance_jiaocha);
			} else if ("危险".equals(synthesis)) {
				main_iv_checkstatus
						.setBackgroundResource(R.drawable.home_jiance_weixian);
			}

			XUtilsImageLoader.getxUtilsImageLoader(mContext,
					R.drawable.car_default,main_iv_carlogo,
					API.DOWN_IMAGE_URL + mAkeyTextAllInfo.getLogo());
		}
	}

	/**
	 * 清空结果
	 */
	public void setReseltNull() {
		main_tv_leixing.setText(null);
		main_tv_vehicle.setText(null);
		main_tv_yinfo.setText(null);
		main_tv_fault.setText(null);
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void initThereAnimationView(View view) {
		main_iv_close = (ImageView) view.findViewById(R.id.main_iv_close);
		main_tv_leixing = (TextView) view.findViewById(R.id.main_tv_leixing1);

		main_tv_vehicle = (TextView) view.findViewById(R.id.main_tv_vehicle);
		main_tv_yinfo = (TextView) view.findViewById(R.id.main_tv_yinfo);

		main_tv_fault = (TextView) view.findViewById(R.id.main_tv_fault);
		main_iv_checkstatus = (ImageView) view
				.findViewById(R.id.main_iv_checkstatus);
		main_btn_retesting = (TextView) view
				.findViewById(R.id.main_btn_retesting);
		main_btn_seedetails = (TextView) view
				.findViewById(R.id.main_btn_seedetails);

		iv_pb_oneD = (ImageView) view.findViewById(R.id.iv_pb_oneD);
		iv_pb_oneD2 = (ImageView) view.findViewById(R.id.iv_pb_oneD2);

		main_iv_carlogo = (ImageView) view.findViewById(R.id.main_iv_carlogo);
		main_iv_close1 = (ImageView) view.findViewById(R.id.main_iv_close1);
		initlisiner();
	}

	/**
	 * 获取进度条的不耳，只执行一次
	 */
	private boolean di = true;
	/**
	 * 进度条的宽度
	 */
	private float pbwidth;
	/**
	 * 第一个进度条距离
	 */
	private float pbOneLength;

	/**
	 * 第二个进度条距离
	 */
	private float pbTwoLength;

	/**
	 * 进度的宽度
	 */
	public void getPBwideth() {
		/**
		 * 回调拿到坐标，，会一直执行
		 */
		ViewTreeObserver vto = iv_pb_oneD.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (di) {
					pbwidth = iv_pb_oneD.getWidth();
					int[] location = new int[2];
					iv_pb_oneD.getLocationOnScreen(location);
					// pbX = location[0];
					di = false;
				}
				return true;
			}
		});
	}

	/**
	 * 第一个进度条动画
	 */
	private Animation animationPB;
	/**
	 * 第er个进度条动画
	 */
	private Animation animationPB2;

	/**
	 * 进度条的动画
	 * 
	 * @param num
	 */
	public void startPbAnimation(int num1, int num2) {

		jisuanJuLi(num1, num2);
		iv_pb_oneD.setVisibility(View.VISIBLE);
		iv_pb_oneD2.setVisibility(View.VISIBLE);
		if (iv_pb_oneD.getAnimation() != null
				&& iv_pb_oneD2.getAnimation() != null) {
			iv_pb_oneD.clearAnimation();
			iv_pb_oneD.setAnimation(null);

			iv_pb_oneD2.clearAnimation();
			iv_pb_oneD2.setAnimation(null);
		}

		animationPB = new TranslateAnimation(-pbwidth, -pbOneLength, 0, 0);
		animationPB.setFillAfter(true);
		animationPB.setDuration(500);
		iv_pb_oneD.startAnimation(animationPB);

		animationPB2 = new TranslateAnimation(-pbwidth, -pbTwoLength, 0, 0);
		animationPB2.setFillAfter(true);
		animationPB2.setDuration(500);
		iv_pb_oneD2.startAnimation(animationPB2);
	}

	/**
	 * 计算距离
	 */
	public void jisuanJuLi(int num1, int num2) {
		float one = (float) (num1 / 100.0);
		float two = (float) (num2 / 100.0);
		/** 占有位置 **/
		float onedistance = pbwidth * one;
		/** 占有位置 **/
		float twodistance = pbwidth * two;
		/** 实际位置为宽度减去占有位置的负值 ***/
		pbOneLength = pbwidth - onedistance;
		pbTwoLength = pbwidth - twodistance;

	}

	// 跳转到第二个动画
	private int type;

	/**
	 * 重置数据
	 */
	public void setReSet() {
		setMoren();
		closeAnimation();
		donghua = true;
		/** 滞空第二个的数据 **/
		setNull();
		if (animationPB != null) {
			animationPB.setFillAfter(false);
			animationPB2.setFillAfter(false);
		}
		iv_pb_oneD.setX(0);
		iv_pb_oneD2.setX(0);
		iv_pb_oneD2.setVisibility(View.INVISIBLE);
		mAkeyTextAllInfo = null;
		iv_pb_oneD.setVisibility(View.INVISIBLE);
		setReseltNull();
	}

	/**
	 * 初始化监听
	 */
	private void initlisiner() {
		main_btn_retesting.setOnClickListener(onClickListener);
		main_btn_seedetails.setOnClickListener(onClickListener);
		main_iv_close.setOnClickListener(onClickListener);
		main_iv_close1.setOnClickListener(onClickListener);
	}

	/**
	 * 重新检查
	 */
	private void retesting() {
		mian_rel_two.setVisibility(View.VISIBLE);
		main_iv_car.setVisibility(View.GONE);
		there_relayout.setVisibility(View.GONE);
		type = 1;
		setReSet();
	}

	/**
	 * 查看详情
	 */
	private void seeDetectDetails() {
		if (mAkeyTextAllInfo != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("list", mAkeyTextAllInfo);
			Intent intent = new Intent();
			intent.putExtras(bundle);
			intent.setClass(mContext, DetactionInfoActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 关闭检测
	 */
	private void closeDetect() {
		two_relayout.setVisibility(View.GONE);
		there_relayout.setVisibility(View.GONE);
		one_relayout.setVisibility(View.VISIBLE);
		type = 0;
		setReSet();
		setImagePhoneVisible();
	}

	/**
	 * 关闭检测失败提示
	 */
	private void closeDetailFail() {
		two_relayout.setVisibility(View.GONE);
		there_relayout.setVisibility(View.GONE);
		there_four.setVisibility(View.GONE);
		one_relayout.setVisibility(View.VISIBLE);
		type = 0;
		setReSet();
		setImagePhoneVisible();
	}

	/**
	 * 第三个动画关闭
	 */
	public void closeAnimation() {
		ainimation = AnimationUtils.loadAnimation(mContext,
				R.anim.main_threr_out);
		if (there_relayout.getAnimation() != null) {
			there_relayout.clearAnimation();
			there_relayout.setAnimation(null);
		}
		if (there_four.getAnimation() != null) {
			there_four.clearAnimation();
			there_four.setAnimation(null);
		}
		ainimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (type == 1) {
					initTwoAnimation();
					there_relayout.setVisibility(View.GONE);
					type = 0;
				}
			}
		});
		if (mAkeyTextAllInfo != null) {
			there_relayout.startAnimation(ainimation);
		} else {
			there_four.startAnimation(ainimation);
		}

		search_device_view.setSearching(true);
	}

	/**
	 * 设置显示
	 */
	public void setTwoTextVisible() {
		this.tv1.setVisibility(View.VISIBLE);
		this.main_tv_checkstatus.setVisibility(View.VISIBLE);
		this.mian_tv_num.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置隐藏
	 */
	public void setTwoTextGone() {
		this.tv1.setVisibility(View.INVISIBLE);
		this.main_tv_checkstatus.setVisibility(View.INVISIBLE);
		this.mian_tv_num.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设置打电话影藏
	 */
	public void setImagePhoneGone() {
		ll_main_rightBottom.setVisibility(View.GONE);
		img_go.setVisibility(View.GONE);
		tvPhone.setVisibility(View.GONE);
		imgPhone.setVisibility(View.GONE);
		tvMore.setVisibility(View.INVISIBLE);
		disableSubView(false);
	}

	/**
	 * 设置打电话显示
	 */
	public void setImagePhoneVisible() {
		ll_main_rightBottom.setVisibility(View.VISIBLE);
		img_go.setVisibility(View.VISIBLE);
		tvPhone.setVisibility(View.VISIBLE);
		imgPhone.setVisibility(View.VISIBLE);
		tvMore.setVisibility(View.VISIBLE);
		disableSubView(true);
	}

	/**
	 * 设置雷达影藏
	 */
	public void setLeiDaGone() {
		main_re_yuan.setVisibility(View.VISIBLE);
		this.search_device_view.setVisibility(View.INVISIBLE);
		rLayoutTip.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设置雷达显示
	 */
	public void setLeiDaVisible() {
		main_re_yuan.setVisibility(View.GONE);
		this.search_device_view.setVisibility(View.VISIBLE);
		rLayoutTip.setVisibility(View.VISIBLE);
	}

	private CustomProgressDialog progressDialog;

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.getInstance(mContext);
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}

	private void disMissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	private int getLeiDaHeight() {
		Resources res = mContext.getResources();
		int i1 = res.getDimensionPixelSize(R.dimen.marging_four_dp);
		int i2 = res.getDimensionPixelSize(R.dimen.car_main_item_margin) * 2;
		int i3 = res.getDimensionPixelSize(R.dimen.car_main_item_width) * 2;
		int i4 = res.getDimensionPixelSize(R.dimen.car_main_below_padding) * 2;
		return i1 + i2 + i3 + i4;
	}

	/**
	 * 调用刷新数据
	 */
	@Override
	public void onResume() {
		super.onResume();
		setUserImage();
	}

	@Override
	public void onStart() {
		super.onStart();

		mLocationUtil = new LocationUtil(mContext,
				LocationUtil.SCANSPAN_TYPE_LONG, locationListener);
		mLocationUtil.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onStop() {
		super.onStop();
		mLocationUtil.onStop();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationUtil.onDestory();
		mLocationUtil = null;
	}

}
