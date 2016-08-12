package com.cheweishi.android.fragement;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baidu.navisdk.util.common.ScreenUtil;
import com.cheweishi.android.R;
import com.cheweishi.android.activity.CarShopActivity;
import com.cheweishi.android.activity.MainNewActivity;
import com.cheweishi.android.adapter.ShopCateGoryAdapter;
import com.cheweishi.android.adapter.ShopFragmentPagerAdapter;
import com.cheweishi.android.adapter.StoreCateGoryAdapter;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.NewsTypeResponse;
import com.cheweishi.android.entity.ShopTypeResponse;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.ScreenUtils;
import com.cheweishi.android.widget.BackgroundDarkPopupWindow;
import com.cheweishi.android.widget.UnSlidingListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangce on 8/10/2016.
 */
public class ShopFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ViewPager vp_shops;//滚动

    private TabLayout tl_shop;//顶部导航

    private ShopFragmentPagerAdapter adapter;//适配器

    private LinearLayout ll_shop_top_down_array; // 顶部选择

    private BackgroundDarkPopupWindow popupWindow;//弹出PopuWindow

    private View categoryView;//分类

    private ListView lv_shop_category;// 分类列表

    private ShopCateGoryAdapter cateGoryAdapter;//下拉分类适配

    private ShopTypeResponse response;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        vp_shops = (ViewPager) view.findViewById(R.id.vp_shop);

        tl_shop = (TabLayout) view.findViewById(R.id.tl_shop);

        categoryView = View.inflate(baseContext, R.layout.shop_service_category, null);

        lv_shop_category = (ListView) categoryView.findViewById(R.id.lv_shop_category);

        cateGoryAdapter = new ShopCateGoryAdapter(baseContext, null);

        lv_shop_category.setAdapter(cateGoryAdapter);

        lv_shop_category.setOnItemClickListener(this);

        ll_shop_top_down_array = (LinearLayout) view.findViewById(R.id.ll_shop_top_down_array);

        ll_shop_top_down_array.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendPacket();
    }

    @Override
    public void receive(String data) {
        response = (ShopTypeResponse) GsonUtil.getInstance().convertJsonStringToObject(data, ShopTypeResponse.class);
        if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
            ProgrosDialog.closeProgrosDialog();
            showToast(response.getDesc());
            return;
        }
        if (null == response.getMsg() || 0 >= response.getMsg().size()) {
            ProgrosDialog.closeProgrosDialog();
            return;
        }
        ll_shop_top_down_array.setVisibility(View.VISIBLE);
        adapter = new ShopFragmentPagerAdapter(((CarShopActivity) baseContext).getSupportFragmentManager(), baseContext, response.getMsg());
        vp_shops.setAdapter(adapter);
        if (5 <= response.getMsg().size())
            tl_shop.setTabMode(TabLayout.MODE_SCROLLABLE);
        tl_shop.setupWithViewPager(vp_shops);
        loginResponse.setToken(response.getToken());
    }

    private void sendPacket() {
        ProgrosDialog.openDialog(baseContext);
        //TODO send packet
        String url = NetInterface.BASE_URL + NetInterface.TEMP_NEWS + NetInterface.GET_NEWS_TYPES + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        param.put("token", loginResponse.getToken());
        netWorkHelper.PostJson(url, param, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_shop_top_down_array:
                showPopup(tl_shop, response.getMsg());
                break;
        }
    }

    private void showPopup(View belowView, List<ShopTypeResponse.MsgBean> data) {
        cateGoryAdapter.setData(data);
        if (null != popupWindow) {
            if (!popupWindow.isShowing()) // 不是正在展示的,重新展示
                popupWindow.showAsDropDown(belowView, 0, 1);
            return;
        }
        measureView(categoryView);
        int height = data.size() * categoryView.getMeasuredHeight() > ScreenUtils.getScreenHeight(baseContext.getApplicationContext()) / 2 ? ScreenUtils.getScreenHeight(baseContext.getApplicationContext()) / 2 : data.size() * categoryView.getMeasuredHeight();
        popupWindow = new BackgroundDarkPopupWindow(categoryView, ScreenUtils.getScreenWidth(baseContext.getApplicationContext()), height, 0);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setDarkStyle(-1);
        popupWindow.setDarkColor(Color.parseColor("#a0000000"));
        popupWindow.darkFillScreen();
        popupWindow.darkBelow(belowView);
        popupWindow.showAsDropDown(belowView, 0, 1);
    }

    private void dismissPopupWindow() {
        if (null != popupWindow && popupWindow.isShowing())
            popupWindow.dismiss();
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        //headerView的宽度信息
        int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int childMeasureHeight;
        if (lp.height > 0) {
            childMeasureHeight = View.MeasureSpec.makeMeasureSpec(lp.height, View.MeasureSpec.EXACTLY);
            //最后一个参数表示：适合、匹配
        } else {
            childMeasureHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);//未指定
        }
        //将宽和高设置给child
        child.measure(childMeasureWidth, childMeasureHeight);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissPopupWindow();
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissPopupWindow();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != response && position <= (response.getMsg().size() - 1)) {
            vp_shops.setCurrentItem(position);
        }
        dismissPopupWindow();
    }
}
