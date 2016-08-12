package com.cheweishi.android.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/21.
 */
public class CarShopActivity extends BaseActivity {

    private ShopFragment shopFragment;

    @ViewInject(R.id.left_action)
    private Button left_action;

    @ViewInject(R.id.title)
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_shop);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        title.setText(getString(R.string.car_shop));
        left_action.setText(getString(R.string.back));

        shopFragment = new ShopFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_shop_content, shopFragment);
        transaction.commit();
    }

    @OnClick({R.id.left_action})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_action:
                finish();
                break;
        }
    }

}
