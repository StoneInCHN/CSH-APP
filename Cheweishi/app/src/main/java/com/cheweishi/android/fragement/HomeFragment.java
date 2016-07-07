package com.cheweishi.android.fragement;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BaiduNaviManager;
import com.cheweishi.android.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.activity.BeautyListActivity_new;
import com.cheweishi.android.activity.CarDetectionActivity;
import com.cheweishi.android.activity.CarDynamicActivity;
import com.cheweishi.android.activity.CouponActivity;
import com.cheweishi.android.activity.CreditActivity;
import com.cheweishi.android.activity.FindParkingSpaceActivity;
import com.cheweishi.android.activity.GasStationActivity;
import com.cheweishi.android.activity.LoginActivity;
import com.cheweishi.android.activity.MainNewActivity;
import com.cheweishi.android.activity.MaintainListActivity_new;
import com.cheweishi.android.activity.PessanySearchActivity;
import com.cheweishi.android.activity.SoSActivity;
import com.cheweishi.android.activity.WashcarListActivity;
import com.cheweishi.android.activity.WebActivity;
import com.cheweishi.android.adapter.ImgAdapter;
import com.cheweishi.android.adapter.MainGridViewAdapter;
import com.cheweishi.android.adapter.MainListViewAdapter;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.AdvResponse;
import com.cheweishi.android.entity.LoginResponse;
import com.cheweishi.android.entity.MainGridInfo;
import com.cheweishi.android.entity.PushResponse;
import com.cheweishi.android.entity.ServiceListResponse;
import com.cheweishi.android.response.BaseResponse;
import com.cheweishi.android.tools.APPTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.SharePreferenceTools;
import com.cheweishi.android.utils.ButtonUtils;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.ScreenUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.MyGallery;
import com.cheweishi.android.widget.UnSlidingListView;
import com.cheweishi.android.widget.UnslidingGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by tangce on 7/6/2016.
 */
public class HomeFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public static TextView tv_home_title;

    // 服务模块gridview
    private UnslidingGridView gv_service;

    // 滚动广告模块
    private MyGallery mygallery;

//    // 活动专区图片
//    private ImageView img_activity_area;
//
//    // 活动专区name
//    private TextView tv_activity_area;
//
//    // 活动专区内容
//    private TextView tv_area_content;
//
//    // 积分商城图片
//    private ImageView img_integral_mall;
//
//    // 积分商城name
//    private TextView tv_integral_mall;
//
//    // 积分商城内容
//    private TextView tv_integral_mall_content;

    //活动专区
    private RelativeLayout rl_activity_area;

    // 积分商城
    private RelativeLayout rl_integral_mall;

    // 商家列表
    private UnSlidingListView list_business;

    // 小圆点容器
    private LinearLayout ll_focus_indicator_container;

    // 可下拉刷新的scrollview
    private PullToRefreshScrollView refresh_scrollview;

    private ImageView iv_home_hascoupon;// 活动中心按钮

    private MainGridViewAdapter gridViewAdapter;// gv_service适配器
    private MainListViewAdapter listViewAdapter;// list_business适配器
    private ImgAdapter imgAdapter;// mygrallery适配器
    private List<MainGridInfo> gridInfos;

    private ArrayList<ImageView> portImg;


    private String app_new_download_url = "";
    private String compel;

    private Intent intent = new Intent();


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        tv_home_title = (TextView) view.findViewById(R.id.tv_home_title);

        gv_service = (UnslidingGridView) view.findViewById(R.id.gv_service);

        mygallery = (MyGallery) view.findViewById(R.id.mygallery);

//        img_activity_area = (ImageView) view.findViewById(R.id.img_activity_area);
//
//        tv_activity_area = (TextView) view.findViewById(R.id.tv_activity_area);
//
//        tv_area_content = (TextView) view.findViewById(R.id.tv_area_content);
//
//        img_integral_mall = (ImageView) view.findViewById(R.id.img_integral_mall);
//
//        tv_integral_mall = (TextView) view.findViewById(R.id.tv_integral_mall);
//
//        tv_integral_mall_content = (TextView) view.findViewById(R.id.tv_integral_mall_content);

        list_business = (UnSlidingListView) view.findViewById(R.id.list_business);

        ll_focus_indicator_container = (LinearLayout) view.findViewById(R.id.ll_focus_indicator_container);

        rl_activity_area = (RelativeLayout) view.findViewById(R.id.rl_activity_area);

        rl_integral_mall = (RelativeLayout) view.findViewById(R.id.rl_integral_mall);

        // 可下拉刷新的scrollview
        refresh_scrollview = (PullToRefreshScrollView) view.findViewById(R.id.refresh_scrollview);


        iv_home_hascoupon = (ImageView) view.findViewById(R.id.iv_home_hascoupon);

        rl_activity_area.setOnClickListener(this);
        rl_integral_mall.setOnClickListener(this);


        initScrollView();
        iniBaiduNavi();
        initData();
    }

    /**
     * 加载数据
     */
    private void initData() {

        gridInfos = new ArrayList<MainGridInfo>();
        for (int i = 0; i < 12; i++) {
            MainGridInfo gridInfo = new MainGridInfo();
            gridInfo.setName(name[i]);
            gridInfo.setImgId(icon[i]);
//            gridInfo.setImgUrl("asdasdas");// TODO:暂无可配图片地址
            gridInfos.add(gridInfo);
        }
        gridViewAdapter = new MainGridViewAdapter(baseContext, gridInfos);
        gv_service.setAdapter(gridViewAdapter);
        gv_service.setOnItemClickListener(this);
        getMainData();
    }


    /**
     * 初始化百度导航
     */
    private void iniBaiduNavi() {
        // 初始化导航引擎
        BaiduNaviManager.getInstance().initEngine(getActivity(),

                getSdcardDir(), mNaviEngineInitListener, new LBSAuthManagerListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                    }
                });
    }


    private BNaviEngineManager.NaviEngineInitListener mNaviEngineInitListener = new BNaviEngineManager.NaviEngineInitListener() {
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

    /**
     * 初始化ScrollView
     */
    @SuppressWarnings("unchecked")
    private void initScrollView() {

        // 上拉、下拉设定
        // refresh_scrollview.setMode(Mode.MANUAL_REFRESH_ONLY);

        refresh_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshBase refreshView) {

                // 执行刷新函数
//                new GetDataTask().execute();
                getMainData();
                refresh_scrollview.onRefreshComplete();
            }

        });
    }

    /**
     * // TODO 发送请求
     * 获取主界面的数据
     */
    private void getMainData() {

        if (!isLogined()) {
            Intent intent = new Intent(baseContext, LoginActivity.class);
            startActivity(intent);
            return;
        }

        ((BaseActivity) getActivity()).applyAdmin(Manifest.permission.ACCESS_FINE_LOCATION, ((BaseActivity) getActivity()).MY_LOCATION_PREMESSION);
        ((BaseActivity) getActivity()).applyAdmin(Manifest.permission.ACCESS_COARSE_LOCATION, ((BaseActivity) getActivity()).MY_LOCATION_PREMESSION);
        ProgrosDialog.openDialog(baseContext);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_HOME_URL + NetInterface.LIST + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        LogHelper.d("----send:" + loginResponse.getToken());
        param.put("token", loginResponse.getToken());
        param.put("latitude", MyMapUtils.getLatitude(baseContext.getApplicationContext()));//维度
//        param.put("latitude", "10");//维度
        param.put("longitude", MyMapUtils.getLongitude(baseContext.getApplicationContext()));//经度
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

    private void requestAdv() {
        String url = NetInterface.BASE_URL + NetInterface.TEMP_ADV_URL + NetInterface.HOME_ADV + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        param.put("token", loginResponse.getToken());
        param.put(Constant.PARAMETER_TAG, NetInterface.HOME_ADV);
        netWorkHelper.PostJson(url, param, this);
    }


    /**
     * 极光推送设置
     */
    private void setJpush() {
        if (LoginMessageUtils.getPush(baseContext))
            JPushInterface.resumePush(baseContext);
        // 获取极光推送
        String alias = ((TelephonyManager) baseContext.getSystemService(baseContext.TELEPHONY_SERVICE))
                .getDeviceId();
        JPushInterface.setAlias(baseContext.getApplicationContext(), alias, mTagsCallback);
        String JPushId = JPushInterface.getRegistrationID(baseContext);
        LogHelper.d("=JPushRegistrationID==" + JPushId + "==alias=" + alias);

        if (null != JPushId && !"".equals(JPushId)) {
            String url = NetInterface.BASE_URL + NetInterface.TEMP_JPUSH + NetInterface.SET_ID + NetInterface.SUFFIX;
            Map<String, Object> param = new HashMap<>();
            param.put("userId", loginResponse.getDesc());
            param.put("token", loginResponse.getToken());
            param.put("piWidth", ScreenUtils.getScreenWidth(baseContext.getApplicationContext()));
            param.put("piHeight", ScreenUtils.getScreenHeight(baseContext.getApplicationContext()));
            param.put("appPlatform", "ANDROID"); // TODO 暂时不加
            param.put("regId", JPushId);
            param.put("versionCode", APPTools.getVersionCode(baseContext));
            param.put(Constant.PARAMETER_TAG, NetInterface.SET_ID);
            netWorkHelper.PostJson(url, param, this);
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

    private void goDuiba(String url) {
        Intent intent = new Intent();
        intent.setClass(baseContext, CreditActivity.class);
        intent.putExtra("navColor", "#FFFFFF");    //配置导航条的背景颜色，请用#ffffff长格式。
        intent.putExtra("titleColor", "#484848");    //配置导航条标题的颜色，请用#ffffff长格式。
        intent.putExtra("url", url);    //配置自动登陆地址，每次需服务端动态生成。
        startActivity(intent);
    }

    @Override
    public void
    receive(String TAG, String data) {
        switch (TAG) {
            case NetInterface.LIST + "HOME":
                ServiceListResponse response = (ServiceListResponse) GsonUtil.getInstance().convertJsonStringToObject(data, ServiceListResponse.class);
                if (null == response)
                    return;
                requestAdv();
                if (response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    // TODO 成功
                    setTitle(response.getDesc());
                    showData(response);
                } else if (response.getCode().equals(NetInterface.RESPONSE_TOKEN)) {
                    // TODO 超时
                    Intent intent = new Intent(baseContext, LoginActivity.class);
                    intent.putExtra(Constant.AUTO_LOGIN, true);
                    startActivity(intent);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.score_business_query_enter,
                            R.anim.score_business_query_exit);
                    return;
                }

//                loginResponse.setToken(response.getToken());
//                LoginMessageUtils.saveloginmsg(baseContext, loginResponse);
                break;

            case NetInterface.HOME_ADV:
                AdvResponse advResponse = (AdvResponse) GsonUtil.getInstance().convertJsonStringToObject(data, AdvResponse.class);
                if (null == advResponse)
                    return;
                setJpush();
                if (!advResponse.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    showToast(advResponse.getDesc());
                    return;
                }

                // TODO 更新消息UI
                if (null != advResponse.getDesc() && !"".equals(advResponse.getDesc())) {
                    int number = 0;
                    try { // 屏蔽高并发的时候
                        number = Integer.valueOf(advResponse.getDesc());
                    } catch (Exception e) {
                        return;
                    }
                    if (0 < number) {
                        MainNewActivity.tv_msg_center_num.setVisibility(View.VISIBLE);
                        if (number <= 99)
                            MainNewActivity.tv_msg_center_num.setText(advResponse.getDesc());
                        else
                            MainNewActivity.tv_msg_center_num.setText("99+");
                    } else {
                        MainNewActivity.tv_msg_center_num.setText("0");
                        MainNewActivity.tv_msg_center_num.setVisibility(View.GONE);
                    }
                }
                showData(advResponse);
//                loginResponse.setToken(advResponse.getToken());
//                LoginMessageUtils.saveloginmsg(baseContext, loginResponse);


//                setTitleLeft();
//                setJpush();
                break;

            case NetInterface.SET_ID:
                PushResponse baseResponse = (PushResponse) GsonUtil.getInstance().convertJsonStringToObject(data, PushResponse.class);
                if (null == baseResponse)
                    return;
                if (!baseResponse.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    showToast(baseResponse.getDesc());
                    return;
                }

//                if (null != baseResponse.getMsg().getHomeAdvUrl() && !"".equals(baseResponse.getMsg().getHomeAdvUrl())) {
                SharePreferenceTools.setPhoneUrl(baseContext, baseResponse.getMsg().getHomeAdvUrl());
//                }

                if (null != baseResponse.getMsg()) {
                    if (null != baseResponse.getMsg().getApkPath() && !"".equals(baseResponse.getMsg().getApkPath())) {
                        app_new_download_url = baseResponse.getMsg().getApkPath();
                        if (baseResponse.getMsg().isForced())
                            compel = "0";
                        showVersionDialog(baseResponse.getMsg().getUpdateContent());
                    }
                    if (baseResponse.getMsg().isHasCoupon()) { // 是否有优惠券可领取
                        iv_home_hascoupon.setVisibility(View.VISIBLE);
                    }
                }

                loginResponse.setToken(baseResponse.getToken());
                ProgrosDialog.closeProgrosDialog();
                break;

            case NetInterface.GET_DUIBA_LOGIN_URL: // 获取兑吧url
                ProgrosDialog.closeProgrosDialog();
                BaseResponse duibaResponse = (BaseResponse) GsonUtil.getInstance().convertJsonStringToObject(data, BaseResponse.class);
                if (!duibaResponse.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    showToast(duibaResponse.getDesc());
                    return;
                }

                if (null != duibaResponse.getDesc() && !"".equals(duibaResponse.getDesc())) {
//                    Intent duiba = new Intent(baseContext, WebActivity.class);
//                    duiba.putExtra("url", duibaResponse.getDesc());
//                    startActivity(duiba);
                    goDuiba(duibaResponse.getDesc());
                }

                loginResponse.setToken(duibaResponse.getToken());
                LoginMessageUtils.saveloginmsg(baseContext, loginResponse);

                break;
            case "SOS": // 紧急救援
                ProgrosDialog.closeProgrosDialog();
                LoginResponse sos = (LoginResponse) GsonUtil.getInstance().convertJsonStringToObject(data, LoginResponse.class);
                loginResponse = sos;
                LoginMessageUtils.saveloginmsg(baseContext, sos);
                isLoginOrHasCar(SoSActivity.class);
                break;
            case "CAR_DYNAMIC":// 车辆动态
                ProgrosDialog.closeProgrosDialog();
                LoginResponse carDynamic = (LoginResponse) GsonUtil.getInstance().convertJsonStringToObject(data, LoginResponse.class);
                loginResponse = carDynamic;
                LoginMessageUtils.saveloginmsg(baseContext, carDynamic);
                isLoginOrHasCar(CarDynamicActivity.class);
                break;
            case "CAR_DETECTION":// 一键检测
                ProgrosDialog.closeProgrosDialog();
                LoginResponse carDetection = (LoginResponse) GsonUtil.getInstance().convertJsonStringToObject(data, LoginResponse.class);
                loginResponse = carDetection;
                LoginMessageUtils.saveloginmsg(baseContext, carDetection);
                isLoginOrHasCar(CarDetectionActivity.class);
                break;
            case "PESSANY":// 违章查询
                ProgrosDialog.closeProgrosDialog();
                LoginResponse pessany = (LoginResponse) GsonUtil.getInstance().convertJsonStringToObject(data, LoginResponse.class);
                loginResponse = pessany;
                LoginMessageUtils.saveloginmsg(baseContext, pessany);
                isLoginOrHasCar_New(PessanySearchActivity.class);
                break;
        }
    }

    public void setTitle(String desc) {
        if (null != desc && !"".equals(desc)) {
            MainNewActivity.tv_home_title.setText(desc);
        }
    }

    private void showData(ServiceListResponse response) {
        setJpushTags();
        listViewAdapter = new MainListViewAdapter(baseContext, response.getMsg());
        list_business.setAdapter(listViewAdapter);
    }

    /**
     * 显示数据
     */
    private void showData(final AdvResponse advResponse) {

        // TODO 更新广告
        InitFocusIndicatorContainer(advResponse);
        imgAdapter = new ImgAdapter((BaseActivity) getActivity(), advResponse, -1);
        mygallery.setAdapter(imgAdapter);
        mygallery.setFocusable(true);
        mygallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
     * 设置小圆点
     */
    private void InitFocusIndicatorContainer(AdvResponse advResponse) {
        portImg = new ArrayList<ImageView>();
        portImg.clear();
        this.ll_focus_indicator_container.removeAllViews();
        if (!StringUtil.isEmpty(advResponse.getMsg())) {
            for (int i = 0; i < advResponse.getMsg().size(); i++) {
                ImageView localImageView = new ImageView(baseContext);
                localImageView.setId(i);
                ImageView.ScaleType localScaleType = ImageView.ScaleType.FIT_XY;
                localImageView.setScaleType(localScaleType);
                LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                localImageView.setLayoutParams(localLayoutParams);
                localImageView.setPadding(5, 5, 5, 5);
                localImageView.setImageResource(R.drawable.lunbo_dian);
                portImg.add(localImageView);
                this.ll_focus_indicator_container.addView(localImageView);
            }
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
            JPushInterface.setTags(baseContext.getApplicationContext(), tagSet,
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
                    JPushInterface.setAlias(baseContext.getApplicationContext(),
                            (String) msg.obj, mTagsCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d("Tanck", "Set tags in handler.");
                    JPushInterface.setTags(baseContext.getApplicationContext(),
                            (Set<String>) msg.obj, mTagsCallback);
                    break;

                default:
                    Log.i("Tanck", "Unhandled msg - " + msg.what);
            }
        }
    };

    /**
     * 设置头部左边的图片状态
     */
    private void setTitleLeft() {
        if (!((BaseActivity) getActivity()).hasBrandIcon()) {
            MainNewActivity.ibtn_user.setImageResource(R.drawable.tianjiacar_img2x);
        } else {
            XUtilsImageLoader.getxUtilsImageLoader(baseContext,
                    R.drawable.tianjiacar_img2x, MainNewActivity.ibtn_user,
                    loginResponse.getMsg().getDefaultVehicleIcon());
        }
    }


    /**
     * 更新对话框
     */
    private void showVersionDialog(String message) {

        builder = new CustomDialog.Builder(baseContext);

        builder.setTitle(R.string.remind);
        builder.setPositiveButton(R.string.banben_updata_remind,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!StringUtil.isEquals(compel, "0", true)) {
                            dialog.dismiss();
                        }
                        try {
                            Uri uri = Uri.parse(app_new_download_url);
                            Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(it);
                        } catch (Exception e) {// 手机上未安装浏览器
                            showToast(R.string.hint);
                        }
                    }
                });

        if (!StringUtil.isEquals(compel, "0", true)) { // 非强制更新
            builder.setMessage(message, 1);
            builder.setNegativeButton(R.string.cancel,
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            versionDialog = builder.create();
        } else { // 强制更新
            builder.setMessage(message, 1);
            versionDialog = builder.create();
            versionDialog.setCanceledOnTouchOutside(false);
            versionDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (StringUtil.isEquals(compel, "0", true)) {
                                getActivity().finish();
                            }
                            break;
                    }
                    return true;
                }
            });
//            versionDialog.setCancelable(false);
        }
        versionDialog.show();
    }


    /**
     * 对是否登陆和是否有车处理
     *
     * @param cls
     */
    private void isLoginOrHasCar(Class<?> cls) {
        if (!isLogined()) {
            intent.setClass(baseContext, LoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.score_business_query_enter,
                    R.anim.score_business_query_exit);
        } else if (!hasCar()) {
            ((BaseActivity) getActivity()).showCustomDialog("你还没有添加车辆", "添加车辆", 0);
            return;
        } else if (!((BaseActivity) getActivity()).hasDevice()) {
            ((BaseActivity) getActivity()).showCustomDialog(getString(R.string.home_no_device), "前往绑定", 1);
            return;
        } else {
            intent.setClass(baseContext, cls);
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
            intent.setClass(baseContext, LoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.score_business_query_enter,
                    R.anim.score_business_query_exit);
        } else if (!hasCar()) {
            ((BaseActivity) getActivity()).showCustomDialog("你还没有添加车辆", "添加车辆", 0);
            return;
        } else {
            intent.setClass(baseContext, cls);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /**
         * 快速点击忽略处理
         */
        if (ButtonUtils.isFastClick()) {
            return;
        }
        switch (position) {
            case 0:// 买车险
//                showToast("此功能正在开发中,敬请期待...");
//                Intent intent = new Intent(baseContext, InsuranceActivity.class);
//                startActivity(intent);
//                isLoginOrHasCar(InsuranceActivity.class);
                Intent intent = new Intent(baseContext, WebActivity.class);
                intent.putExtra("url", NetInterface.INSURANCE);
                startActivity(intent);
                break;
            case 1:// 洗车
                isLogin(WashcarListActivity.class);
                break;
            case 2:// 紧急救援
//                updateCache("SOS");
                Intent sos = new Intent(baseContext, SoSActivity.class);
                startActivity(sos);
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


    /**
     * 登陆才能跳转
     *
     * @param cls
     */
    private void isLogin(Class<?> cls) {
        if (!isLogined()) {
            intent.setClass(baseContext, LoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.score_business_query_enter,
                    R.anim.score_business_query_exit);
        } else {
            intent.setClass(baseContext, cls);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginMessageUtils.saveloginmsg(baseContext, loginResponse);
        mygallery.destroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_activity_area:// 活动专区
                iv_home_hascoupon.setVisibility(View.GONE);
                intent.setClass(baseContext, CouponActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_integral_mall:// 积分商城
                getDuiBaUrl();
                break;
        }
    }

    private void getDuiBaUrl() {
        ProgrosDialog.openDialog(baseContext);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_DUIBA + NetInterface.GET_DUIBA_LOGIN_URL + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        param.put("token", loginResponse.getToken());
        param.put(Constant.PARAMETER_TAG, NetInterface.GET_DUIBA_LOGIN_URL);
        netWorkHelper.PostJson(url, param, this);
    }
}
