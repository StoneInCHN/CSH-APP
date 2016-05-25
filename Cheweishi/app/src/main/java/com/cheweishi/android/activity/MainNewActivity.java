package com.cheweishi.android.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.cheweishi.android.adapter.ImgAdapter;
import com.cheweishi.android.adapter.MainGridViewAdapter;
import com.cheweishi.android.adapter.MainListViewAdapter;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.R;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ADInfo;
import com.cheweishi.android.entity.AdvResponse;
import com.cheweishi.android.entity.LoginResponse;
import com.cheweishi.android.entity.MainGridInfo;
import com.cheweishi.android.entity.MainSellerInfo;
import com.cheweishi.android.entity.PushResponse;
import com.cheweishi.android.entity.ServiceListResponse;
import com.cheweishi.android.tools.APPTools;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.ButtonUtils;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.utils.mapUtils.LocationUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.CustomDialog.Builder;
import com.cheweishi.android.widget.MyGallery;
import com.cheweishi.android.widget.UnSlidingListView;
import com.cheweishi.android.widget.UnslidingGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 商城版首页
 *
 * @author mingdasen
 */
@ContentView(R.layout.activity_main2)
public class MainNewActivity extends BaseActivity {

    @ViewInject(R.id.tv_home_title)
    public static TextView tv_home_title;

    @ViewInject(R.id.ibtn_user)
    // title左边按钮
    public static ImageButton ibtn_user;

    @ViewInject(R.id.gv_service)
    // 服务模块gridview
    private UnslidingGridView gv_service;

    @ViewInject(R.id.mygallery)
    // 滚动广告模块
    private MyGallery mygallery;

    @ViewInject(R.id.img_activity_area)
    // 活动专区图片
    private ImageView img_activity_area;

    @ViewInject(R.id.tv_activity_area)
    // 活动专区name
    private TextView tv_activity_area;

    @ViewInject(R.id.tv_area_content)
    // 活动专区内容
    private TextView tv_area_content;

    @ViewInject(R.id.img_integral_mall)
    // 积分商城图片
    private ImageView img_integral_mall;

    @ViewInject(R.id.tv_integral_mall)
    // 积分商城name
    private TextView tv_integral_mall;

    @ViewInject(R.id.tv_integral_mall_content)
    // 积分商城内容
    private TextView tv_integral_mall_content;

    @ViewInject(R.id.list_business)
    // 商家列表
    private UnSlidingListView list_business;

    @ViewInject(R.id.ll_focus_indicator_container)
    // 小圆点容器
    private LinearLayout ll_focus_indicator_container;

    @ViewInject(R.id.tv_msg_center_num)
    public static TextView tv_msg_center_num;//消息数量

    // 可下拉刷新的scrollview
    @ViewInject(R.id.refresh_scrollview)
    private PullToRefreshScrollView refresh_scrollview;

    private MainGridViewAdapter gridViewAdapter;// gv_service适配器
    private MainListViewAdapter listViewAdapter;// list_business适配器
    private ImgAdapter imgAdapter;// mygrallery适配器
    private List<MainGridInfo> gridInfos;

    // private List<Integer> adList;
    private ArrayList<ImageView> portImg;
    private List<MainSellerInfo> sellerInfos;

    private List<ADInfo> adInfos;// 广告数据

    /**
     * 定位工具
     */
    private LocationUtil mLocationUtil;
    private SharedPreferences spLocation;
    private String historyCity;

    private String specialLcoationChongqing;
    private String specialLcoationBeijing;
    private String specialLcoationTianjin;
    private String specialLcoationShanghai;
    private String specialLcoationHongKong;
    private String specialLcoationAomen;

    public static MainNewActivity instance;

    private MyBroadcastReceiver broad;

    private int preSelImgIndex = 0;

    private String[] name = {"买车险", "洗车", "紧急救援", "保养", "找加油站", "", "", "美容",
            "车辆动态", "一键检测", "违章查询", "找车位"};
    private int[] icon = {R.drawable.xian, R.drawable.xiche,
            R.drawable.jinjijiuyuan, R.drawable.baoyang, R.drawable.jiayouzhan,
            0, 0, R.drawable.meirong, R.drawable.dongtai, R.drawable.jiance,
            R.drawable.weizhang, R.drawable.chewei};

    private CustomDialog.Builder builder;
    private CustomDialog versionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        instance = this;

        initScrollView();
        iniBaiduNavi();
        initData();


//        PoiSearch mPoiSearch = PoiSearch.newInstance();
//        mPoiSearch.searchInCity((new PoiCitySearchOption())
//                .city("成都")
//                .keyword("加油站")
//                .pageNum(10));
//        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
//            @Override
//            public void onGetPoiResult(PoiResult poiResult) {
//                List<PoiInfo> allPoi = poiResult.getAllPoi();
//                for (int i = 0; i < allPoi.size(); i++) {
//                    PoiInfo poiInfo = allPoi.get(i);
//                    LogHelper.d("---" + poiInfo.address + "--" + poiInfo.city + "---" + poiInfo.type);
//                }
//            }
//
//            @Override
//            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
//
//            }
//        });
        // setJpush();
    }


    /**
     * 极光推送设置
     */
    private void setJpush() {
        if (LoginMessageUtils.getPush(baseContext))
            JPushInterface.resumePush(baseContext);
        // 获取极光推送
        String alias = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
        JPushInterface.setAlias(getApplicationContext(), alias, mTagsCallback);
        String JPushId = JPushInterface.getRegistrationID(this);
        LogHelper.d("=JPushRegistrationID==" + JPushId + "==alias=" + alias);

        if (null != JPushId && !"".equals(JPushId)) {
            String url = NetInterface.BASE_URL + NetInterface.TEMP_JPUSH + NetInterface.SET_ID + NetInterface.SUFFIX;
            Map<String, Object> param = new HashMap<>();
            param.put("userId", loginResponse.getDesc());
            param.put("token", loginResponse.getToken());
            param.put("appPlatform", "ANDROID"); // TODO 暂时不加
            param.put("regId", JPushId);
            param.put("versionCode", APPTools.getVersionCode(baseContext));
            param.put(Constant.PARAMETER_TAG, NetInterface.SET_ID);
            netWorkHelper.PostJson(url, param, this);
        }
    }

    /**
     * 设置极光推送标签
     */
    private void setJpushTags() {
        if (!StringUtil.isEmpty(Constant.JPUSH_TAGS)) {
            Set<String> tagSet = new LinkedHashSet<String>();
            String[] sArray = Constant.JPUSH_TAGS.split(",");
            for (String sTagItme : sArray) {
                tagSet.add(sTagItme);
            }
            JPushInterface.setTags(getApplicationContext(), tagSet,
                    mTagsCallback);
        }
    }

    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i("Tanck", logs);
                    if (!StringUtil.isEmpty(alias)) {
                        LogHelper.d("==alias=" + alias);
                    }

                    if (!StringUtil.isEmpty(tags)) {
                        LogHelper.d("==tags=" + tags.toString());
                    }
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    LogHelper.d(logs);
                    if (!StringUtil.isEmpty(alias)) {
                        mHandler.sendMessageDelayed(
                                mHandler.obtainMessage(MSG_SET_ALIAS, alias),
                                1000 * 60);
                    }
                    if (!StringUtil.isEmpty(tags)) {
                        mHandler.sendMessageDelayed(
                                mHandler.obtainMessage(MSG_SET_TAGS, tags),
                                1000 * 60);
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    LogHelper.d(logs);
            }
        }
    };

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d("Tanck", "Set alias in handler.");
                    JPushInterface.setAlias(getApplicationContext(),
                            (String) msg.obj, mTagsCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d("Tanck", "Set tags in handler.");
                    JPushInterface.setTags(getApplicationContext(),
                            (Set<String>) msg.obj, mTagsCallback);
                    break;

                default:
                    Log.i("Tanck", "Unhandled msg - " + msg.what);
            }
        }
    };

    /**
     * 初始化ScrollView
     */
    @SuppressWarnings("unchecked")
    private void initScrollView() {

        // 上拉、下拉设定
        // refresh_scrollview.setMode(Mode.MANUAL_REFRESH_ONLY);

        refresh_scrollview.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshBase refreshView) {

                // 执行刷新函数
//                new GetDataTask().execute();
                getMainData();
                refresh_scrollview.onRefreshComplete();
            }

        });
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some stuff here

            // Call onRefreshComplete when the list has been refreshed.
            refresh_scrollview.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broad == null) {
            broad = new MyBroadcastReceiver();
        }
        IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
        registerReceiver(broad, intentFilter);

//        setJpush();
        setJpushTags();
    }

    /**
     * 初始化百度导航
     */
    private void iniBaiduNavi() {
        // 初始化导航引擎
        BaiduNaviManager.getInstance().initEngine(this,

                getSdcardDir(), mNaviEngineInitListener, new LBSAuthManagerListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                    }
                });
    }

    private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
        public void engineInitSuccess() {
            Log.i("Tanck", "=========engineInitSuccess=========");
        }

        public void engineInitStart() {
            Log.i("Tanck", "=========engineInitStart=========");
        }

        public void engineInitFail() {
            Log.i("Tanck", "=========engineInitFail=========");
        }
    };

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationUtil = new LocationUtil(this, LocationUtil.SCANSPAN_TYPE_LONG,
                locationListener);
        mLocationUtil.onStart();
    }

    /**
     * 百度定位监听
     */
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

    /**
     * 加载数据
     */
    private void initData() {

        setTitleLeft();
        gridInfos = new ArrayList<MainGridInfo>();
        for (int i = 0; i < 12; i++) {
            MainGridInfo gridInfo = new MainGridInfo();
            gridInfo.setName(name[i]);
            gridInfo.setImgId(icon[i]);
            gridInfo.setImgUrl("asdasdas");
            gridInfos.add(gridInfo);
        }
        gridViewAdapter = new MainGridViewAdapter(this, gridInfos);
        gv_service.setAdapter(gridViewAdapter);
        initLocation();
        getMainData();
    }


    private void showData(ServiceListResponse response) {
        setJpushTags();
        listViewAdapter = new MainListViewAdapter(this, response.getMsg());
        list_business.setAdapter(listViewAdapter);
    }

    /**
     * 显示数据
     */
    private void showData(final AdvResponse advResponse) {

        // TODO 更新广告
        InitFocusIndicatorContainer(advResponse);
        imgAdapter = new ImgAdapter(MainNewActivity.this, advResponse, -1);
        mygallery.setAdapter(imgAdapter);
        mygallery.setFocusable(true);
        mygallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int selIndex, long arg3) {
//                LogHelper.d("arg3:" + arg3 + "current:" + preSelImgIndex + "---selIndex:" + selIndex + "---size:" + advResponse.getMsg().size());
                if (advResponse.getMsg() != null && advResponse.getMsg().size() > 0) {
                    selIndex = selIndex % advResponse.getMsg().size();
                    portImg.get(preSelImgIndex).setImageResource(
                            R.drawable.lunbo_dian);
                    portImg.get(selIndex).setImageResource(
                            R.drawable.lunbo_dian_click);
                    preSelImgIndex = selIndex;
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    /**
     * // TODO 发送请求
     * 获取主界面的数据
     */
    private void getMainData() {
        if (!isLogined()) {
            Intent intent = new Intent(MainNewActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        ProgrosDialog.openDialog(this);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_HOME_URL + NetInterface.LIST + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        LogHelper.d("----send:" + loginResponse.getToken());
        param.put("token", loginResponse.getToken());
        param.put("latitude", MyMapUtils.getLatitude(this));//维度
//        param.put("latitude", "10");//维度
        param.put("longitude", MyMapUtils.getLongitude(this));//经度
//        param.put("longitude", "10");//经度
        /**
         * 1保养
         2	洗车
         3	维修
         4	紧急救援
         5	美容
         */
        param.put("serviceCategoryId", 2); // TODO 目前只有一种
        param.put("pageSize", 5);
        param.put("pageNumber", 1);
        param.put(Constant.PARAMETER_TAG, NetInterface.LIST + "HOME");
        netWorkHelper.PostJson(url, param, this);
    }

    @Override
    public void
    receive(String TAG, String data) {
        ProgrosDialog.closeProgrosDialog();
        switch (TAG) {
            case NetInterface.LIST + "HOME":
                ServiceListResponse response = (ServiceListResponse) GsonUtil.getInstance().convertJsonStringToObject(data, ServiceListResponse.class);
                if (response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    // TODO 成功
                    setTitle(response.getDesc());
                    showData(response);
                    loginResponse.setToken(response.getToken());
                    DBTools.getInstance(baseContext).save(loginResponse);
                } else if (response.getCode().equals(NetInterface.RESPONSE_TOKEN)) {
                    // TODO 超时
                    Intent intent = new Intent(MainNewActivity.this, LoginActivity.class);
                    intent.putExtra(Constant.AUTO_LOGIN, true);
                    startActivity(intent);
                    this.finish();
                    overridePendingTransition(R.anim.score_business_query_enter,
                            R.anim.score_business_query_exit);
                    return;
                }

                requestAdv();
                break;

            case NetInterface.HOME_ADV:

                AdvResponse advResponse = (AdvResponse) GsonUtil.getInstance().convertJsonStringToObject(data, AdvResponse.class);
                if (!advResponse.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    showToast(advResponse.getDesc());
                    return;
                }

                // TODO 更新消息UI
                if (null != advResponse.getDesc() && !"".equals(advResponse.getDesc())) {
                    int number = Integer.valueOf(advResponse.getDesc());
                    if (0 < number) {
                        tv_msg_center_num.setVisibility(View.VISIBLE);
                        if (number <= 99)
                            tv_msg_center_num.setText(advResponse.getDesc());
                        else
                            tv_msg_center_num.setText("99+");
                    } else {
                        tv_msg_center_num.setText("0");
                        tv_msg_center_num.setVisibility(View.GONE);
                    }
                }
                showData(advResponse);
                loginResponse.setToken(advResponse.getToken());
                LoginMessageUtils.saveloginmsg(baseContext, loginResponse);


                setJpush();
                break;

            case NetInterface.SET_ID:
                PushResponse baseResponse = (PushResponse) GsonUtil.getInstance().convertJsonStringToObject(data, PushResponse.class);
                if (!baseResponse.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    showToast(baseResponse.getDesc());
                    return;
                }

                // TODO 版本更新
                if (null != baseResponse.getMsg()) {
                    app_new_download_url = baseResponse.getMsg().getApkPath();
                    showVersionDialog(baseResponse.getMsg().getUpdateContent());
                }

                loginResponse.setToken(baseResponse.getToken());
                LoginMessageUtils.saveloginmsg(baseContext, loginResponse);
                break;
            case "SOS": // 紧急救援
                LoginResponse sos = (LoginResponse) GsonUtil.getInstance().convertJsonStringToObject(data, LoginResponse.class);
                loginResponse = sos;
                LoginMessageUtils.saveloginmsg(baseContext, sos);
                isLoginOrHasCar(SoSActivity.class);
                break;
            case "CAR_DYNAMIC":// 车辆动态
                LoginResponse carDynamic = (LoginResponse) GsonUtil.getInstance().convertJsonStringToObject(data, LoginResponse.class);
                loginResponse = carDynamic;
                LoginMessageUtils.saveloginmsg(baseContext, carDynamic);
                isLoginOrHasCar(CarDynamicActivity.class);
                break;
            case "CAR_DETECTION":// 一键检测
                LoginResponse carDetection = (LoginResponse) GsonUtil.getInstance().convertJsonStringToObject(data, LoginResponse.class);
                loginResponse = carDetection;
                LoginMessageUtils.saveloginmsg(baseContext, carDetection);
                isLoginOrHasCar(CarDetectionActivity.class);
                break;
            case "PESSANY":// 违章查询
                LoginResponse pessany = (LoginResponse) GsonUtil.getInstance().convertJsonStringToObject(data, LoginResponse.class);
                loginResponse = pessany;
                LoginMessageUtils.saveloginmsg(baseContext, pessany);
                isLoginOrHasCar_New(PessanySearchActivity.class);
                break;
        }


    }


    private void requestAdv() {
        String url = NetInterface.BASE_URL + NetInterface.TEMP_ADV_URL + NetInterface.HOME_ADV + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        param.put("token", loginResponse.getToken());
        param.put(Constant.PARAMETER_TAG, NetInterface.HOME_ADV);
        netWorkHelper.PostJson(url, param, this);
    }

    @Override
    public void error(String errorMsg) {
        ProgrosDialog.closeProgrosDialog();
        showToast(R.string.FAIL);
    }

    private String app_new_download_url = "";
    private String compel;


    /**
     * 定位相关初始化
     */
    private void initLocation() {
        spLocation = getSharedPreferences(MyMapUtils.LOCATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        specialLcoationChongqing = this
                .getString(R.string.special_location_chongqing);
        specialLcoationBeijing = this
                .getString(R.string.special_location_beijing);
        specialLcoationTianjin = this
                .getString(R.string.special_location_tianjin);
        specialLcoationShanghai = this
                .getString(R.string.special_location_shanghai);
        specialLcoationHongKong = this
                .getString(R.string.special_location_hongkong);
        specialLcoationAomen = this.getString(R.string.special_location_aomen);
    }

    /**
     * 设置头部左边的图片状态
     */
    private void setTitleLeft() {
        if (!hasBrandIcon()) {
            ibtn_user.setImageResource(R.drawable.tianjiacar_img2x);
        } else {
            XUtilsImageLoader.getxUtilsImageLoader(this,
                    R.drawable.tianjiacar_img2x, ibtn_user,
                    loginResponse.getMsg().getDefaultVehicleIcon());
        }
    }

    /**
     * 设置小圆点
     */
    private void InitFocusIndicatorContainer(AdvResponse advResponse) {
        portImg = new ArrayList<ImageView>();
        portImg.clear();
        this.ll_focus_indicator_container.removeAllViews();
        if (!StringUtil.isEmpty(advResponse.getMsg())) {
            for (int i = 0; i < advResponse.getMsg().size(); i++) {
                ImageView localImageView = new ImageView(this);
                localImageView.setId(i);
                ImageView.ScaleType localScaleType = ImageView.ScaleType.FIT_XY;
                localImageView.setScaleType(localScaleType);
                LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                localImageView.setLayoutParams(localLayoutParams);
                localImageView.setPadding(5, 5, 5, 5);
                localImageView.setImageResource(R.drawable.lunbo_dian);
                portImg.add(localImageView);
                this.ll_focus_indicator_container.addView(localImageView);
            }
        }
    }

    @OnClick({R.id.ibtn_user, R.id.ll_right_msg, R.id.btn_scanning,
            R.id.btn_my_wallet, R.id.btn_my_order, R.id.rl_activity_area,
            R.id.rl_integral_mall})
    public void onClick(View v) {
        /**
         * 快速点击忽略处理
         */
        if (ButtonUtils.isFastClick()) {
            return;
        }
        intent = new Intent();
        switch (v.getId()) {
            case R.id.ibtn_user:// 个人中心
                isLogin(MyAccountActivity.class);
                break;
            case R.id.ll_right_msg:// 消息中心
                isLogin(MessagerCenterActivity.class);
                break;
            case R.id.btn_scanning:// 扫一扫
                OpenCamera(null);
                break;
            case R.id.btn_my_wallet:// 我的钱包
                isLogin(PurseActivity.class);// PurseActivity
                break;
            case R.id.btn_my_order:// 我的订单
                isLogin(MyorderActivity.class);
                break;
            case R.id.rl_activity_area:// 活动专区
//                 intent.setClass(MainNewActivity.this,
//                 InformationSecondListActivity.class);
//                 startActivity(intent);
                intent.setClass(MainNewActivity.this, CouponActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_integral_mall:// 积分商城
                // intent.setClass(MainNewActivity.this, SCActivity.class);
                // startActivity(intent);
                break;
            default:
                break;
        }
    }

    private Intent intent;

    @OnItemClick({R.id.gv_service})
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        /**
         * 快速点击忽略处理
         */
        if (ButtonUtils.isFastClick()) {
            return;
        }
        intent = new Intent();
        switch (position) {
            case 0:// 买车险
                showToast("此功能正在开发中,敬请期待...");
//                Intent intent = new Intent(baseContext, InsuranceActivity.class);
//                startActivity(intent);
//                isLoginOrHasCar(InsuranceActivity.class);
                break;
            case 1:// 洗车
                isLogin(WashcarListActivity.class);
                break;
            case 2:// 紧急救援
                updateCache("SOS");
//                isLoginOrHasCar(SoSActivity.class);
                break;
            case 3:// 保养
//                isLogin(MaintainListActivity.class); // TODO 详情
//                isLogin(MaintainDetailsActivity.class);
                isLogin(MaintainListActivity_new.class);
                break;
            case 4:// 找加油站
                isLogin(GasStationActivity.class);
                break;
            case 5:// logo
                break;
            case 6:// 隐藏了
                break;
            case 7:// 美容
//                isLogin(BeautyListActivity.class); // TODO 详情
//                isLogin(BeautyDetailsActivity.class);
                isLogin(BeautyListActivity_new.class);
                break;
            case 8:// 车辆动态
                updateCache("CAR_DYNAMIC");
//                isLoginOrHasCar(CarDynamicActivity.class);
                break;
            case 9:// 一键检测
                updateCache("CAR_DETECTION");
//                isLoginOrHasCar(CarDetectionActivity.class);
                break;
            case 10:// 违章代办
                updateCache("PESSANY");
//                isLoginOrHasCar(PessanySearchActivity.class);
                break;
            case 11:// 找车位
                isLogin(FindParkingSpaceActivity.class);
                break;
        }
    }


    private void updateCache(String tag) {
        if (isLogined()) {
            ProgrosDialog.openDialog(baseContext);
            String url = NetInterface.HEADER_ALL + NetInterface.UPDATE_CACHE + NetInterface.SUFFIX;
            Map<String, Object> param = new HashMap<>();
            param.put("userId", loginResponse.getDesc());
            param.put("token", loginResponse.getToken());
            param.put(Constant.PARAMETER_TAG, tag);
            netWorkHelper.PostJson(url, param, this);
        }
    }

    /**
     * 登陆才能跳转
     *
     * @param cls
     */
    private void isLogin(Class<?> cls) {
        if (!isLogined()) {
            intent.setClass(MainNewActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.score_business_query_enter,
                    R.anim.score_business_query_exit);
        } else {
            intent.setClass(MainNewActivity.this, cls);
            startActivity(intent);
        }
    }

    /**
     * 对是否登陆和是否有车处理
     *
     * @param cls
     */
    private void isLoginOrHasCar(Class<?> cls) {
        if (!isLogined()) {
            intent.setClass(MainNewActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.score_business_query_enter,
                    R.anim.score_business_query_exit);
        } else if (!hasDevice()) {
            showCustomDialog(getString(R.string.home_no_device));
        } else {
            intent.setClass(MainNewActivity.this, cls);
            startActivity(intent);
        }
    }


    /**
     * 是否有车新判断
     *
     * @param cls
     */
    private void isLoginOrHasCar_New(Class<?> cls) {
        if (!isLogined()) {
            intent.setClass(MainNewActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.score_business_query_enter,
                    R.anim.score_business_query_exit);
        } else if (!hasCar()) {
            showCustomDialog("你还没有添加车辆");
        } else {
            intent.setClass(MainNewActivity.this, cls);
            startActivity(intent);
        }
    }

    /**
     * 绑定车辆提示dialog
     */
    private void showCustomDialog(String msg) {
        Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(getString(R.string.remind));
        builder.setPositiveButton(getString(R.string.home_goto_bind),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        OpenCamera(loginResponse.getMsg().getDefaultVehicleId());
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }


    /**
     * 打开相机
     *
     * @param cid
     */
    private void OpenCamera(String cid) {
        PackageManager pkm = getPackageManager();
        boolean has_permission = (PackageManager.PERMISSION_GRANTED == pkm
                .checkPermission("android.permission.CAMERA", baseContext.getPackageName()));//"packageName"));
        if (has_permission) {
            Intent intent = new Intent(baseContext,
                    MipcaActivityCapture.class);
            intent.putExtra("cid", cid);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            showToast("请为该应用添加打开相机权限");
        }
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
        if (!StringUtil.isEmpty(broad)) {
            unregisterReceiver(broad);
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (!StringUtil.isEquals(intent.getAction(), Constant.REFRESH_FLAG,
                    true)) {
                return;
            }
//            if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
//                    Constant.CAR_MANAGER_REFRESH, true)) {
//                Constant.EDIT_FLAG = true;
//                getMainData();
//                Log.i("result", "===========MainNewActivity================");
//            } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
//                    Constant.USER_NICK_EDIT_REFRESH, true)) {
//                Constant.EDIT_FLAG = true;
//                // initViews();
//            } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
//                    Constant.USER_NICK_EDIT_REFRESH_OTHER, true)) {
//                // connectToServer();
//            }
        }
    }

    /**
     * 删除对话框
     */
    private void showVersionDialog(String message) {

        builder = new CustomDialog.Builder(this);

        builder.setTitle(R.string.remind);
        builder.setPositiveButton(R.string.banben_updata_remind,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            Uri uri = Uri.parse(app_new_download_url);
                            Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(it);
                        } catch (Exception e) {// 手机上未安装浏览器
                            showToast(R.string.hint);
                        }
                    }
                });

        if (!StringUtil.isEquals(compel, "0", true)) {
            builder.setMessage(message, 1);
            builder.setNegativeButton(R.string.cancel,
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            builder.setMessage("当前版本过低，请更新到最新版本");
        }
        versionDialog = builder.create();
        versionDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (StringUtil.isEquals(compel, "0", true)) {
                    finish();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);

    }

}
