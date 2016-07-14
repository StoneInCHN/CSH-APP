package com.cheweishi.android.fragement;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.activity.MainNewActivity;
import com.cheweishi.android.adapter.StoreCateGoryAdapter;
import com.cheweishi.android.utils.DisplayUtil;
import com.cheweishi.android.utils.ScreenUtils;
import com.cheweishi.android.widget.BackgroundDarkPopupWindow;
import com.cheweishi.android.widget.UnSlidingListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangce on 7/6/2016.
 */
public class StoreFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private boolean isLoaded = false;

    private BackgroundDarkPopupWindow popupWindow;//弹框

    private View categoryView;//分类

    private UnSlidingListView usl_store_category;// 分类列表

    private StoreCateGoryAdapter cateGoryAdapter; // 分类适配器

    private LinearLayout ll_store_top_category; // 顶部分类

    private TextView tv_store_category_service, tv_store_category_sort;// 服务类别和排序

    private List<String> serviceData = new ArrayList<>();// 服务

    private List<String> sortData = new ArrayList<>();// 排序服务

    private PullToRefreshListView prl_store; // 租户列表

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ll_store_top_category = (LinearLayout) view.findViewById(R.id.ll_store_top_category);

        tv_store_category_service = (TextView) view.findViewById(R.id.tv_store_category_service);

        tv_store_category_sort = (TextView) view.findViewById(R.id.tv_store_category_sort);

        prl_store = (PullToRefreshListView) view.findViewById(R.id.prl_store);


        categoryView = View.inflate(baseContext, R.layout.store_service_category, null);
        usl_store_category = (UnSlidingListView) categoryView.findViewById(R.id.usl_store_category);


        tv_store_category_service.setOnClickListener(this);
        tv_store_category_sort.setOnClickListener(this);
        cateGoryAdapter = new StoreCateGoryAdapter(baseContext, null, 1);
        usl_store_category.setAdapter(cateGoryAdapter);
        usl_store_category.setOnItemClickListener(this);
    }

    private void onLoad() {
        isLoaded = true;
        initData();
    }

    private void initData() {
        serviceData.add("洗车服务");
        serviceData.add("保养服务");
        serviceData.add("美容服务");
        sortData.add("距离优先");
        sortData.add("好评优先");
        sortData.add("价格优先");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public void onDataLoading(int what) {
        if (0x2 == what) {
            onLoad();
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !isLoaded)
            loading.sendEmptyMessage(0x2);
    }


    private void showPopup(View belowView, int type, List<String> data) {
        cateGoryAdapter.setData(data, type);
        if (null != popupWindow) {
            if (!popupWindow.isShowing()) // 不是正在展示的,重新展示
                popupWindow.showAsDropDown(belowView, 0, 1);
            return;
        }
        measureView(categoryView);
        popupWindow = new BackgroundDarkPopupWindow(categoryView, ScreenUtils.getScreenWidth(baseContext.getApplicationContext()), categoryView.getMeasuredHeight(), ((MainNewActivity) baseContext).getBottomHeight());
//        popupWindow.setFocusable(true);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_store_category_service: // 分类服务
                showPopup(ll_store_top_category, 1, serviceData);
                break;
            case R.id.tv_store_category_sort:// 排序
                showPopup(ll_store_top_category, 0, sortData);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissPopupWindow();
    }


}
