package com.cheweishi.android.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.ProductParamFragmentPagerAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangce on 8/22/2016.
 */
public class ProductParamDetailActivity extends BaseActivity {

    @ViewInject(R.id.left_action)
    private Button left_action;

    @ViewInject(R.id.title)
    private TextView title;


    @ViewInject(R.id.ll_common_title_right)
    private LinearLayout right_action; // 右边

    @ViewInject(R.id.tv_common_title_number)
    private TextView tv_common_title_number; // 购物车数量

    @ViewInject(R.id.vp_param_shop)
    private ViewPager vp_param_shop; // 滚动视图

    @ViewInject(R.id.tl_param_shop)
    private TabLayout tl_param_shop; // 滚动顶部标题

    private ProductParamFragmentPagerAdapter adapter;//整个适配器

    private List<String> list = new ArrayList<>();//标题数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_param);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        right_action.setVisibility(View.VISIBLE);
        left_action.setText(getString(R.string.back));
        title.setText(getString(R.string.detail));
        list.add("图文详情");
        list.add("产品参数");
        list.add("用户评价");
        adapter = new ProductParamFragmentPagerAdapter(getSupportFragmentManager(), baseContext, list);
        vp_param_shop.setAdapter(adapter);
        tl_param_shop.setupWithViewPager(vp_param_shop);
        vp_param_shop.setCurrentItem(getIntent().getIntExtra("currentP", 0));


        tv_common_title_number.setVisibility(View.VISIBLE);
        tv_common_title_number.setText("1");
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
