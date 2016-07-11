package com.cheweishi.android.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.cheweishi.android.R;
import com.cheweishi.android.adapter.ImgAdapter;
import com.cheweishi.android.adapter.MainGridViewAdapter;
import com.cheweishi.android.adapter.MainListViewAdapter;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.config.StatusBarCompat2;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ADInfo;
import com.cheweishi.android.entity.AdvResponse;
import com.cheweishi.android.entity.LoginResponse;
import com.cheweishi.android.entity.MainGridInfo;
import com.cheweishi.android.entity.MainSellerInfo;
import com.cheweishi.android.entity.PushResponse;
import com.cheweishi.android.entity.ServiceListResponse;
import com.cheweishi.android.fragement.HomeFragment;
import com.cheweishi.android.fragement.MyFragment;
import com.cheweishi.android.fragement.NewsFragment;
import com.cheweishi.android.fragement.StoreFragment;
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

    public static String bindTitle = "车生活";

    @ViewInject(R.id.ibtn_user)
    // title左边按钮
    public static ImageView ibtn_user;

    @ViewInject(R.id.tv_msg_center_num)
    public static TextView tv_msg_center_num;//消息数量

    @ViewInject(R.id.ll_home_header)
    private LinearLayout ll_home_header;//actionbar

    @ViewInject(R.id.ll_right_msg)
    private LinearLayout ll_right_msg; // 右边消息

    //首页
    @ViewInject(R.id.ll_home_bottom_index)
    private LinearLayout ll_home_bottom_index;

    //门店
    @ViewInject(R.id.ll_home_bottom_store)
    private LinearLayout ll_home_bottom_store;

    //蓝调
    @ViewInject(R.id.ll_home_bottom_news)
    private LinearLayout ll_home_bottom_news;

    //我的
    @ViewInject(R.id.ll_home_bottom_my)
    private LinearLayout ll_home_bottom_my;


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


    private HomeFragment home;
    private StoreFragment store;
    private NewsFragment news;
    private MyFragment my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        StatusBarCompat2.setStatusBarColor(this);
        instance = this;
        initLocation();

        initContent();

    }

    private void initContent() {
        home = new HomeFragment();
        store = new StoreFragment();
        news = new NewsFragment();
        my = new MyFragment();
        InitHomeFragment(R.id.fl_home_content, home, store, news, my);
    }


    @Override
    protected void onResume() {
        super.onResume();
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
            Log.i("Tanck", "location:" + historyCity);
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


    @Override
    public void error(String errorMsg) {
        ProgrosDialog.closeProgrosDialog();
        showToast(R.string.FAIL);
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


    private Intent intent;

    @OnClick({R.id.ibtn_user, R.id.ll_right_msg,
            R.id.ll_home_bottom_index, R.id.ll_home_bottom_store,
            R.id.ll_home_bottom_news, R.id.ll_home_bottom_my})
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
            case R.id.ll_home_bottom_index: //首页
                ll_right_msg.setVisibility(View.VISIBLE);
                setTitle(bindTitle);
                ll_home_header.setVisibility(View.VISIBLE);
                ChangeFragment(0, home, store, news, my);
                break;
            case R.id.ll_home_bottom_store: // 门店
                ll_right_msg.setVisibility(View.INVISIBLE);
                setTitle("门店");
                ll_home_header.setVisibility(View.VISIBLE);
                ChangeFragment(1, home, store, news, my);
                break;
            case R.id.ll_home_bottom_news: // 新闻
                ll_right_msg.setVisibility(View.INVISIBLE);
                setTitle("车蓝调");
                ll_home_header.setVisibility(View.VISIBLE);
                ChangeFragment(2, home, store, news, my);
                break;
            case R.id.ll_home_bottom_my: // 我的
                ll_right_msg.setVisibility(View.INVISIBLE);
                setTitle("我的");
                ll_home_header.setVisibility(View.GONE);
                ChangeFragment(3, home, store, news, my);
                break;
//            case R.id.btn_scanning:// 扫一扫
//                OpenCamera(false);
//                break;
//            case R.id.btn_my_wallet:// 我的钱包
//                isLogin(PurseActivity.class);// PurseActivity
//                break;
//            case R.id.btn_my_order:// 我的订单
//                isLogin(MyorderActivity.class);
//                break;
//            case R.id.rl_activity_area:// 活动专区
////                 intent.setClass(MainNewActivity.this,
////                 InformationSecondListActivity.class);
////                 startActivity(intent);
//                iv_home_hascoupon.setVisibility(View.GONE);
//                intent.setClass(MainNewActivity.this, CouponActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.rl_integral_mall:// 积分商城
////                 intent.setClass(MainNewActivity.this, SCActivity.class);
////                 startActivity(intent);
//                getDuiBaUrl();
//                break;
            default:
                break;
        }
    }

    public void setTitle(String desc) {
        if (null != desc && !"".equals(desc)) {
            tv_home_title.setText(desc);
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
            overridePendingTransition(R.anim.score_business_query_enter,
                    R.anim.score_business_query_exit);
        } else {
            intent.setClass(baseContext, cls);
            startActivity(intent);
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
        instance = null;
    }

    private long exitTime = 0;

    public void ExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showToast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            ExitApp();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
