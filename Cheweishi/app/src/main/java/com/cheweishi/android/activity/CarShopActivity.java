package com.cheweishi.android.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.cheweishi.android.adapter.CarShopAdapter;
import com.cheweishi.android.R;
import com.cheweishi.android.adapter.NewsFragmentPagerAdapter;
import com.cheweishi.android.adapter.ShopFragmentPagerAdapter;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.NewsTypeResponse;
import com.cheweishi.android.fragement.ShopFragment;
import com.cheweishi.android.utils.GsonUtil;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/21.
 */
public class CarShopActivity extends BaseActivity {

    private ShopFragment shopFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_shop);
        init();
    }

    private void init() {
        shopFragment = new ShopFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_shop_content, shopFragment);
        transaction.commit();
    }

}
