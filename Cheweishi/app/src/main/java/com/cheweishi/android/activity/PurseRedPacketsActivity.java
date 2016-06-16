package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.CouponAdapter;
import com.cheweishi.android.adapter.MyCouponAdapter;
import com.cheweishi.android.adapter.RedPacketsDetailsAdapter;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ActivityCouponResponse;
import com.cheweishi.android.entity.MyCouponResponse;
import com.cheweishi.android.entity.RedPacketsInfo;
import com.cheweishi.android.entity.ChargeResponse;
import com.cheweishi.android.fragement.MyCarCouponFragment;
import com.cheweishi.android.fragement.MyConpouFragment;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.StringUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 我的红包
 *
 * @author XMh
 */
public class PurseRedPacketsActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.left_action)
    private Button left_action;
    @ViewInject(R.id.title)
    private TextView title;
    @ViewInject(R.id.ll_mycoupon_tab)
    private LinearLayout ll_mycoupon_tab; // 选项卡
    @ViewInject(R.id.btn_coupon)
    private Button btn_coupon; // 优惠券
    @ViewInject(R.id.btn_washcar_coupon)
    private Button btn_washcar_coupon;
    private MyConpouFragment myConpouFragment;
    private MyCarCouponFragment myCarCouponFragment;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purse_integtal);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        title.setText(R.string.purse_certificates);
        left_action.setText(R.string.back);
        addFragment();
    }

    private void addFragment() {
        myConpouFragment = new MyConpouFragment();
        myCarCouponFragment = new MyCarCouponFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_coupon_content, myCarCouponFragment).add(R.id.fl_coupon_content, myConpouFragment).hide(myCarCouponFragment).show(myConpouFragment);
        transaction.commit();
    }

    private void showFragment(Fragment show, Fragment hide) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(hide).show(show);
        transaction.commit();
    }

    @OnClick({R.id.left_action, R.id.ll_mycoupon_tab, R.id.btn_coupon, R.id.btn_washcar_coupon})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_action:// 返回
                this.finish();
                break;
            case R.id.btn_coupon:
                if (0 == currentIndex)
                    return;
                btn_coupon.setBackgroundResource(R.drawable.baike_btn_trans_left_f_96);
                btn_washcar_coupon.setBackgroundResource(R.drawable.baike_btn_pink_right_f_96);
                showFragment(myCarCouponFragment, myConpouFragment);
                currentIndex = 1;
                break;
            case R.id.btn_washcar_coupon:
                if (1 == currentIndex)
                    return;
                btn_coupon.setBackgroundResource(R.drawable.baike_btn_pink_left_f_96);
                btn_washcar_coupon.setBackgroundResource(R.drawable.baike_btn_trans_right_f_96);
                showFragment(myConpouFragment, myCarCouponFragment);
                currentIndex = 0;
                break;
            default:
                break;
        }
    }

    public void showTab() {
        ll_mycoupon_tab.setVisibility(View.VISIBLE);
    }
}
