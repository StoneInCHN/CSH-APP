package com.cheweishi.android.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.interfaces.ScrollViewListener;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.widget.MyScrollView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangce on 8/12/2016.
 */
public class ProductDetailActivity extends BaseActivity implements ScrollViewListener, ViewPager.OnPageChangeListener {

    @ViewInject(R.id.left_action)
    private Button left_action; // 左标题

    @ViewInject(R.id.title)
    private TextView title; // 标题

    @ViewInject(R.id.iv_common_title_right)
    private ImageView right_action; // 右边

    @ViewInject(R.id.sv_product_detail)
    private MyScrollView sv_product_detail;

    @ViewInject(R.id.ll_shop_detail_title)
    private LinearLayout ll_common_title;

    @ViewInject(R.id.ll_product_detail_title)
    private LinearLayout ll_product_detail_title; // 黑色返回

    @ViewInject(R.id.iv_product_detail_point)
    private ImageView iv_product_detail_point;// 图片指示灯

    @ViewInject(R.id.vp_product_detail)
    private ViewPager vp_product_detail;//滑动图片

    @ViewInject(R.id.ll_shop_buy)
    private LinearLayout ll_shop_buy; // 滑动出来的购物车

    private int headerHeight; // 顶部标题的高度

    private List<String> list = new ArrayList<>();// 图片url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ViewUtils.inject(this);
        init();

    }

    private void init() {
        right_action.setVisibility(View.VISIBLE);
        ll_common_title.setVisibility(View.GONE);
        title.setText(getString(R.string.shop_detail));
        left_action.setText(getString(R.string.back));
        headerHeight = getHeadHeight(ll_common_title);
        sv_product_detail.setScrollViewListener(this);
        vp_product_detail.setOnPageChangeListener(this);
        for (int i = 0; i < 4; i++) {
            list.add("");
        }
        drawPoint(0);
    }

    @OnClick({R.id.left_action})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_action:
                finish();
                break;
        }
    }


    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (0 != y) { // 向上滚动
            ll_common_title.setVisibility(View.VISIBLE);
            float alpha = y * 1.0f / headerHeight;
            if (1.0f < alpha)
                alpha = 1.0f;
            else if (0.0f > alpha)
                alpha = 0.0f;
            else if (0.0f < alpha && 1.0f > alpha) {
                ll_product_detail_title.setVisibility(View.GONE);
                ll_shop_buy.setVisibility(View.GONE);
            }
            ViewHelper.setAlpha(ll_common_title, alpha);
        } else if (0 == y) {
            ll_common_title.setVisibility(View.GONE);
            ll_product_detail_title.setVisibility(View.VISIBLE);
            ll_shop_buy.setVisibility(View.VISIBLE);
        }

    }

    private void drawPoint(int position) {
        int radius = 10; // 半径
        int spacing = 50; // 点之间间隔
        Bitmap points = Bitmap.createBitmap(radius * 2 + spacing * (list.size() - 1), radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(points);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置画笔为无锯齿
        paint.setStyle(Paint.Style.FILL); // 实心
        for (int i = 0; i < list.size(); i++) {
            paint.setColor(Color.GRAY);
            if (position == i) // 设置选中项为白色
                paint.setColor(getResources().getColor(R.color.deep_orange));
            canvas.drawCircle(radius + spacing * i, radius, radius, paint);
        }
        iv_product_detail_point.setImageBitmap(points);
    }


    private int getHeadHeight(LinearLayout view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        drawPoint(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
