package com.cheweishi.android.fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheweishi.android.R;
import com.cheweishi.android.activity.CarShopActivity;
import com.cheweishi.android.adapter.ShopFragmentPagerAdapter;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.NewsTypeResponse;
import com.cheweishi.android.utils.GsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangce on 8/10/2016.
 */
public class ShopFragment extends BaseFragment {

    private ViewPager vp_shops;//滚动

    private TabLayout tl_shop;//顶部导航

    private ShopFragmentPagerAdapter adapter;//适配器

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        vp_shops = (ViewPager) view.findViewById(R.id.vp_shop);
        tl_shop = (TabLayout) view.findViewById(R.id.tl_shop);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendPacket();
    }

    @Override
    public void receive(String data) {
        NewsTypeResponse response = (NewsTypeResponse) GsonUtil.getInstance().convertJsonStringToObject(data, NewsTypeResponse.class);
        if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
            ProgrosDialog.closeProgrosDialog();
            showToast(response.getDesc());
            return;
        }
        if (null == response.getMsg() || 0 >= response.getMsg().size()) {
            ProgrosDialog.closeProgrosDialog();
            return;
        }

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
}
