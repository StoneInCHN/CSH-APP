package com.cheweishi.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.interfaces.ScrollViewListener;
import com.cheweishi.android.thirdpart.adapter.CBPageAdapter;
import com.cheweishi.android.thirdpart.holder.CBViewHolderCreator;
import com.cheweishi.android.thirdpart.holder.CommonNetWorkImgHolder;
import com.cheweishi.android.thirdpart.view.CBLoopViewPager;
import com.cheweishi.android.utils.StringUtil;
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
public class ProductDetailActivity extends BaseActivity implements ScrollViewListener, ViewPager.OnPageChangeListener, TextWatcher {

    @ViewInject(R.id.left_action)
    private Button left_action; // 左标题

    @ViewInject(R.id.title)
    private TextView title; // 标题

    @ViewInject(R.id.ll_common_title_right)
    private LinearLayout right_action; // 右边

    @ViewInject(R.id.tv_common_title_number)
    private TextView tv_common_title_number; // 购物车数量

    @ViewInject(R.id.tv_product_cart_number)
    private TextView tv_product_cart_number;//购物车数量

    @ViewInject(R.id.sv_product_detail)
    private MyScrollView sv_product_detail;

    @ViewInject(R.id.ll_shop_detail_title)
    private LinearLayout ll_common_title;

    @ViewInject(R.id.ll_product_detail_title)
    private LinearLayout ll_product_detail_title; // 黑色返回

    @ViewInject(R.id.iv_product_detail_point)
    private ImageView iv_product_detail_point;// 图片指示灯

    @ViewInject(R.id.vp_product_detail)
    private CBLoopViewPager vp_product_detail;//滑动图片

    @ViewInject(R.id.ll_shop_buy)
    private FrameLayout ll_shop_buy; // 滑动出来的购物车

    @ViewInject(R.id.ll_product_img_txt_detail)
    private LinearLayout ll_product_img_txt_detail;//图文详情

    @ViewInject(R.id.ll_product_detail_param)
    private LinearLayout ll_product_detail_param; // 产品参数

    @ViewInject(R.id.tv_product_detail_right_more)
    private TextView tv_product_detail_right_more;//更多或暂无评价

    @ViewInject(R.id.et_product_detail_num)
    private EditText et_product_detail_num;//购买数量


    @ViewInject(R.id.iv_product_detail_num_add)
    private ImageView num_add;//增加

    @ViewInject(R.id.iv_product_detail_num_les)
    private ImageView num_les;//减少

    private int headerHeight; // 顶部标题的高度

    private List<String> list = new ArrayList<>();// 图片url

    private CBPageAdapter adapter; // 图片适配器

    private int mBuynumber = 1; // 购买数量

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
        et_product_detail_num.addTextChangedListener(this);
        sv_product_detail.setScrollViewListener(this);
        vp_product_detail.setOnPageChangeListener(this);
        for (int i = 0; i < 1; i++) {
            list.add("");
        }
        adapter = new CBPageAdapter(new MyHolder(), list, this);
        vp_product_detail.setAdapter(adapter, true);
        drawPoint(0);


        tv_product_cart_number.setVisibility(View.VISIBLE);
        tv_product_cart_number.setText("1");
        tv_common_title_number.setVisibility(View.VISIBLE);
        tv_common_title_number.setText("1");
    }

    @OnClick({R.id.left_action, R.id.ll_product_img_txt_detail, R.id.ll_product_detail_param, R.id.rl_product_detail_more
            , R.id.tv_product_detail_right_more, R.id.iv_product_detail_num_les, R.id.iv_product_detail_num_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_action:
                finish();
                break;
            case R.id.ll_product_img_txt_detail: // 图文详情
                Intent intent = new Intent(baseContext, ProductParamDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_product_detail_param: // 产品参数
                Intent detail = new Intent(baseContext, ProductParamDetailActivity.class);
                detail.putExtra("currentP", 1);
                startActivity(detail);
                break;
            case R.id.tv_product_detail_right_more://评价
            case R.id.rl_product_detail_more: // 更多评论
                Intent common = new Intent(baseContext, ProductParamDetailActivity.class);
                common.putExtra("currentP", 2);
                startActivity(common);
                break;
            case R.id.iv_product_detail_num_les: // 减少
                --mBuynumber;
                et_product_detail_num.setText(String.valueOf(0 >= mBuynumber ? 1 : mBuynumber));
                break;
            case R.id.iv_product_detail_num_add: // 增加
                ++mBuynumber;
                et_product_detail_num.setText(String.valueOf(99 < mBuynumber ? 99 : mBuynumber));
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (0 >= s.length()) {
            mBuynumber = 1;
            et_product_detail_num.setText(String.valueOf(mBuynumber));
        }
        mBuynumber = StringUtil.isEmpty(s.toString()) ? 1 : Integer.valueOf(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    private class MyHolder implements CBViewHolderCreator<CommonNetWorkImgHolder> {

        @Override
        public CommonNetWorkImgHolder createHolder() {
            return new CommonNetWorkImgHolder();
        }
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
