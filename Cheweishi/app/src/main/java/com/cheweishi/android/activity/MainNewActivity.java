package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.ImgAdapter;
import com.cheweishi.android.adapter.MainGridViewAdapter;
import com.cheweishi.android.adapter.MainListViewAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ADInfo;
import com.cheweishi.android.entity.Brand;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.entity.MainGridInfo;
import com.cheweishi.android.entity.MainSellerInfo;
import com.cheweishi.android.entity.MainSellerServiceInfo;
import com.cheweishi.android.tools.APPTools;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.ButtonUtils;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.utils.mapUtils.LocationUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.CustomDialog.Builder;
import com.cheweishi.android.widget.MyGallery;
import com.cheweishi.android.widget.UnSlidingListView;
import com.cheweishi.android.widget.UnslidingGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

/**
 * 商城版首页
 *
 * @author mingdasen
 */
@ContentView(R.layout.activity_main2)
public class MainNewActivity extends BaseActivity {

    @ViewInject(R.id.ibtn_user)
    // title左边按钮
    private ImageButton ibtn_user;

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
        // setJpush();
    }

    /**
     * 极光推送设置
     */
    private void setJpush() {
        // 获取极光推送
        String alias = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
        JPushInterface.setAlias(getApplicationContext(), alias, mTagsCallback);
        String JPushId = JPushInterface.getRegistrationID(this);
        Log.i("result", "=JPushRegistrationID==" + JPushId + "==alias=" + alias);
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
                    Log.i("result", logs);
                    if (!StringUtil.isEmpty(alias)) {
                        Log.i("result", "==alias=" + alias);
                    }

                    if (!StringUtil.isEmpty(tags)) {
                        Log.i("result", "==tags=" + tags.toString());
                    }
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i("result", logs);
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
                    Log.e("result", logs);
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
                    Log.d("result", "Set alias in handler.");
                    JPushInterface.setAlias(getApplicationContext(),
                            (String) msg.obj, mTagsCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d("result", "Set tags in handler.");
                    JPushInterface.setTags(getApplicationContext(),
                            (Set<String>) msg.obj, mTagsCallback);
                    break;

                default:
                    Log.i("result", "Unhandled msg - " + msg.what);
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
                new GetDataTask().execute();
                getMainData();
            }

        });
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(50);
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

        setJpush();
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
            Log.i("result", "=========engineInitSuccess=========");
        }

        public void engineInitStart() {
            Log.i("result", "=========engineInitStart=========");
        }

        public void engineInitFail() {
            Log.i("result", "=========engineInitFail=========");
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

        gridInfos = new ArrayList<MainGridInfo>();
        // adList = new ArrayList<Integer>();
        for (int i = 0; i < 12; i++) {
            MainGridInfo gridInfo = new MainGridInfo();
            gridInfo.setName(name[i]);
            gridInfo.setImgId(icon[i]);
            gridInfo.setImgUrl("asdasdas");
            gridInfos.add(gridInfo);
            // adList.add(R.drawable.fuwu_bj);
        }

        gridViewAdapter = new MainGridViewAdapter(this, gridInfos);
        gv_service.setAdapter(gridViewAdapter);
        initLocation();
        getMainData();
        // }
    }

    /**
     * 显示数据
     */
    private void showData() {
        setTitleLeft();
        setJpushTags();
        listViewAdapter = new MainListViewAdapter(this, sellerInfos);
        list_business.setAdapter(listViewAdapter);

        InitFocusIndicatorContainer();
        imgAdapter = new ImgAdapter(MainNewActivity.this, adInfos, 0);
        mygallery.setAdapter(imgAdapter);
        mygallery.setFocusable(true);
        mygallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int selIndex, long arg3) {
                if (adInfos != null && adInfos.size() > 0) {
                    selIndex = selIndex % adInfos.size();
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
     * 获取主界面的数据
     */
    private void getMainData() {
        RequestParams params = new RequestParams();
        if (isLogined()) {
            params.addBodyParameter("uid", loginMessage.getUid());
            params.addBodyParameter("mobile", loginMessage.getMobile());
        }
        params.addBodyParameter("lat", MyMapUtils.getLatitude(this) + "");
        params.addBodyParameter("lon", MyMapUtils.getLongitude(this) + "");
        params.addBodyParameter("app", APPTools.getVersionName(this));
        httpBiz = new HttpBiz(this);
        ProgrosDialog.openDialog(this);
        // Log.i("result",
        // "===主界面数据请求参数==uid=" + loginMessage.getUid() + "==mobile="
        // + loginMessage.getMobile() + "==lat="
        // + MyMapUtils.getLatitude(this) + "==lon="
        // + MyMapUtils.getLongitude(this));
        if (isLogined()) {
            Log.i("result", "===首页数据请求参数===uid=" + loginMessage.getUid() + "_" + loginMessage.getMobile() + "_" + MyMapUtils.getLatitude(this) + "_" + MyMapUtils.getLongitude(this) + "_" + APPTools.getVersionName(this));
        } else {
            Log.i("result", "===首页数据请求参数==lat=" + MyMapUtils.getLatitude(this) + "_" + MyMapUtils.getLongitude(this) + "_" + APPTools.getVersionName(this));
        }
        httpBiz.httPostData(10001, API.CSH_MAIN_DATA_URL, params, this);
    }

    @Override
    public void receive(int type, String data) {
        super.receive(type, data);
        ProgrosDialog.closeProgrosDialog();
        switch (type) {
            case 10001:
                parseJsonData(data);
                break;

            default:
                showToast(R.string.FAIL);
                break;
        }
    }

    private String app_new_download_url;
    private String compel;

    /**
     * //TODO 数据解析
     *
     * @param data
     */
    private void parseJsonData(String data) {
        if (StringUtil.isEmpty(data)) {
            showToast(R.string.FAIL);
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(data);
            if (StringUtil.isEquals(API.returnSuccess,
                    jsonObject.optString("state"), true)) {
                // 数据处理
                processingData(jsonObject);
            } else if (StringUtil.isEquals(API.returnRelogin,
                    jsonObject.optString("state"), true)) {
                ReLoginDialog.getInstance(this).showDialog(
                        jsonObject.optString("message"));
            } else if (StringUtil.isEquals("200010",
                    jsonObject.optString("state"), true)) {
                app_new_download_url = jsonObject.optJSONObject("data")
                        .optString("url_android");
                compel = jsonObject.optJSONObject("data").optString("compel");
                // 版本更新处理
                showVersionDialog(jsonObject.optString("message"));

            } else {
                showToast(jsonObject.optString("message"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据处理
     *
     * @param jsonObject
     */
    private void processingData(JSONObject jsonObject) {
        JSONObject object = jsonObject.optJSONObject("data");
        if (!StringUtil.isEmpty(object)) {
            // 获取图片的base_url
            API.CSH_GET_IMG_BASE_URL = object.optString("baseUrl");
            // 获取推送标签
            Constant.JPUSH_TAGS = object.optString("tags");
            // 用户信息
            if (!StringUtil.isEmpty(object.optJSONObject("user"))) {
                loginMessage.setNick_name(object.optJSONObject("user")
                        .optString("nick_name"));
                loginMessage.setIcon(object.optJSONObject("user").optString(
                        "icon"));
                loginMessage.setSignature(object.optJSONObject("user")
                        .optString("signature"));
            }

            // 车辆信息
            if (!StringUtil.isEmpty(object.optJSONObject("vehicle"))) {
                CarManager carManager = new CarManager();
                carManager.setId(object.optJSONObject("vehicle").optInt("id")
                        + "");
                carManager.setSeries(object.optJSONObject("vehicle").optString(
                        "series"));
                carManager.setModule(object.optJSONObject("vehicle").optString(
                        "module"));
                carManager.setDevice(object.optJSONObject("vehicle").optString(
                        "device"));
                carManager.setPlate(object.optJSONObject("vehicle").optString(
                        "plate"));
                JSONObject brandObject = object.optJSONObject("vehicle")
                        .optJSONObject("brand");
                if (!StringUtil.isEmpty(brandObject)) {
                    Brand brand = new Brand();
                    brand.setSeries(brandObject.optString("series"));
                    brand.setBrandIcon(brandObject.optString("brandIcon"));
                    brand.setModule(brandObject.optString("module"));
                    brand.setSeriesName(brandObject.optString("seriesName"));
                    brand.setModuleName(brandObject.optString("moduleName"));
                    brand.setBrand(brandObject.optString("brand"));
                    brand.setBrandName(brandObject.optString("brandName"));
                    carManager.setBrand(brand);
                }
                carManager.setDriving_license(object.optJSONObject("vehicle")
                        .optString("driving_license"));
                loginMessage.setCarManager(carManager);
            }
            // 保存用户信息
            if (!StringUtil.isEmpty(loginMessage)) {
                DBTools.getInstance(this).save(loginMessage);
            }

            // 广告信息
            adInfos = new ArrayList<ADInfo>();
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<List<ADInfo>>() {
            }.getType();
            adInfos = gson.fromJson(object.optString("adv"), type);
            for (ADInfo adInfo : adInfos) {
                adInfo.setWidth(320);
                adInfo.setHeight(80);
                adInfo.setDefaultImg(R.drawable.ad_default_back);
            }
            // 店铺信息
            JSONArray array = object.optJSONArray("store");
            sellerInfos = new ArrayList<MainSellerInfo>();
            JSONObject sellerObject = null;
            MainSellerInfo sellerInfo = null;
            for (int i = 0; i < array.length(); i++) {
                sellerObject = array.optJSONObject(i);
                sellerInfo = new MainSellerInfo();
                sellerInfo.setName(sellerObject.optString("store_name"));
                sellerInfo.setAddress(sellerObject.optString("address"));
                sellerInfo.setImgUrl(sellerObject.optString("image_1"));//
                sellerInfo.setEvaluateImg(sellerObject.optInt("evaluateImg"));// ?
                sellerInfo.setEvaluate(sellerObject.optString("evaluate"));// ?
                String distance = String.format("%.2f",
                        sellerObject.optDouble("distance") / 1000);
                sellerInfo.setDistance(distance + "km");
                sellerInfo.setId(sellerObject.optString("id"));
                sellerInfo.setAppoint(sellerObject.optString("is_appoint"));
                // sellerInfo.setLon(jsonObject.getDouble("lon"));

                JSONArray array2 = sellerObject.optJSONArray("commodity");
                JSONObject serviceObject = null;
                List<MainSellerServiceInfo> sellerServiceInfos = new ArrayList<MainSellerServiceInfo>();
                MainSellerServiceInfo serviceInfo = null;
                if (!StringUtil.isEmpty(array2)) {
                    for (int j = 0; j < array2.length(); j++) {
                        serviceInfo = new MainSellerServiceInfo();
                        serviceObject = array2.optJSONObject(j);
                        serviceInfo.setName(serviceObject
                                .optString("goods_name"));
                        serviceInfo.setfPrice(serviceObject
                                .optString("discount_price"));
                        serviceInfo.setPrice(serviceObject.optString("price"));
                        serviceInfo.setId(serviceObject.optString("id"));
                        serviceInfo.setIsFPrice(serviceObject
                                .optInt("is_discount__price"));
                        serviceInfo.setIsRed(serviceObject
                                .optInt("support_red") + "");
                        serviceInfo.setCate_id_2(serviceObject
                                .optString("cate_id_2"));
                        sellerServiceInfos.add(serviceInfo);
                    }
                }
                // Log.i("result", "===serviceInfos==" + serviceInfos.size());
                sellerInfo.setServices(sellerServiceInfos);
                sellerInfos.add(sellerInfo);
            }
            showData();
        } else {
            showToast(jsonObject.optString("message"));
        }
    }

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
                    API.CSH_GET_IMG_BASE_URL
                            + loginMessage.getCarManager().getBrand()
                            .getBrandIcon());
            // DisplayImageOptions options = new DisplayImageOptions.Builder()
            // .cacheInMemory(true).cacheOnDisk(true)
            // .showImageForEmptyUri(R.drawable.tianjiacar_img2x)
            // .showImageOnFail(R.drawable.tianjiacar_img2x)
            // .showImageOnLoading(R.drawable.tianjiacar_img2x)
            // .bitmapConfig(Bitmap.Config.RGB_565).build();
            //
            // ImageLoader imageLoader = ImageLoader.getInstance();
            // imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            // imageLoader.getInstance().displayImage(
            // API.CSH_GET_IMG_BASE_URL
            // + loginMessage.getCarManager().getBrand()
            // .getBrandIcon(), ibtn_user, options);
            // ibtn_user.setImageResource(R.drawable.tianjiacar_img2x);
        }
    }

    /**
     * 设置小圆点
     */
    private void InitFocusIndicatorContainer() {
        portImg = new ArrayList<ImageView>();
        portImg.clear();
        this.ll_focus_indicator_container.removeAllViews();
        if (!StringUtil.isEmpty(adInfos)) {
            for (int i = 0; i < adInfos.size(); i++) {
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

    @OnClick({R.id.ibtn_user, R.id.ibtn_search, R.id.btn_scanning,
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
            case R.id.ibtn_search:// 搜索
                break;
            case R.id.btn_scanning:// 扫一扫
                PackageManager pkm = getPackageManager();
                boolean has_permission = (PackageManager.PERMISSION_GRANTED == pkm
                        .checkPermission("android.permission.CAMERA", baseContext.getPackageName()));//"packageName"));
                if (has_permission) {
                    intent.setClass(MainNewActivity.this,
                            MipcaActivityCapture.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            case R.id.btn_my_wallet:// 我的钱包
                isLogin(PurseActivity.class);// PurseActivity
                break;
            case R.id.btn_my_order:// 我的订单
                isLogin(MyorderActivity.class);
                break;
            case R.id.rl_activity_area:// 活动专区
                // intent.setClass(MainNewActivity.this,
                // InformationSecondListActivity.class);
                // startActivity(intent);
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
                isLoginOrHasCar(InsuranceActivity.class);
                break;
            case 1:// 洗车
                isLogin(WashcarListActivity.class);
                break;
            case 2:// 紧急救援
                isLoginOrHasCar(SoSActivity.class);
                break;
            case 3:// 保养
                isLogin(MaintainListActivity.class);
                break;
            case 4:// 找加油站
                isLogin(GasStationActivity.class);
                break;
            case 5:// logo
                break;
            case 6:// 隐藏了
                break;
            case 7:// 美容
                isLogin(BeautyListActivity.class);
                break;
            case 8:// 车辆动态
                isLoginOrHasCar(CarDynamicActivity.class);
                break;
            case 9:// 一键检测
                isLoginOrHasCar(CarDetectionActivity.class);
                break;
            case 10:// 违章代办
                isLoginOrHasCar(PessanySearchActivity.class);
                break;
            case 11:// 找车位
                isLogin(FindParkingSpaceActivity.class);
                break;
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
            showCustomDialog();
        } else {
            intent.setClass(MainNewActivity.this, cls);
            startActivity(intent);
        }
    }

    /**
     * 绑定车辆提示dialog
     */
    private void showCustomDialog() {
        Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(getString(R.string.home_no_device));
        builder.setTitle(getString(R.string.remind));
        builder.setPositiveButton(getString(R.string.home_goto_bind),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(MainNewActivity.this,
                                AddCarActivity.class));
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
            if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
                    Constant.CAR_MANAGER_REFRESH, true)) {
                Constant.EDIT_FLAG = true;
                getMainData();
                Log.i("result", "===========MainNewActivity================");
            } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
                    Constant.USER_NICK_EDIT_REFRESH, true)) {
                Constant.EDIT_FLAG = true;
                // initViews();
            } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
                    Constant.USER_NICK_EDIT_REFRESH_OTHER, true)) {
                // connectToServer();
            }
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
            builder.setMessage(message);
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
