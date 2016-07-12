package com.cheweishi.android.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yunjia365.android.R;
import com.cheweishi.android.activity.CouponDetailActivity;
import com.cheweishi.android.activity.PurseRedPacketsActivity;
import com.cheweishi.android.adapter.MyCouponAdapter;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.MyCouponResponse;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.StringUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangce on 6/15/2016.
 */
public class MyConpouFragment extends BaseFragment implements
        PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener {
    private PullToRefreshListView mListView;
    private List<MyCouponResponse.MsgBean> list;
    // 定义加载的页面数量
    private int page = 1;
    private MyCouponAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mycoupon, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (PullToRefreshListView) view.findViewById(R.id.xlistview_mycoupon);
        init();
    }

    private void init() {

        mListView.setOnRefreshListener(this);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        list = new ArrayList<>();
        adapter = new MyCouponAdapter(baseContext, list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        getData();
    }


    /**
     * 获取红包详情数据
     */
    private void getData() {
        if (isLogined()) {
            String url = NetInterface.BASE_URL + NetInterface.TEMP_COUPON + NetInterface.MYCOUPON + NetInterface.SUFFIX;
            Map<String, Object> param = new HashMap<>();
            param.put("userId", loginResponse.getDesc());
            param.put("token", loginResponse.getToken());
            param.put("pageSize", 5);
            param.put("pageNumber", page);
            netWorkHelper.PostJson(url, param, this);
        }
    }

    @Override
    public void receive(String data) {
        mListView.onRefreshComplete();
        ProgrosDialog.closeProgrosDialog();

        MyCouponResponse response = (MyCouponResponse) GsonUtil.getInstance().convertJsonStringToObject(data, MyCouponResponse.class);
        if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
            showToast(response.getDesc());
            return;
        }

        if (null != response.getDesc() && response.getDesc().equals("existWashing")) {
            ((PurseRedPacketsActivity) getActivity()).showTab();
        }

        if (null != response.getMsg()) {
            List<MyCouponResponse.MsgBean> temp = response.getMsg();
            list.addAll(temp);
            if (0 < list.size()) {
                if (response.getPage().getTotal() < list.size()) {
                    mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
                adapter.setData(list);
            } else {
                EmptyTools.setEmptyView(baseContext, mListView);
                EmptyTools.setImg(R.drawable.dingdanwu_icon);
                EmptyTools.setMessage("您还没有优惠券,赶快去领取吧");
                mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        } else {
            EmptyTools.setEmptyView(baseContext, mListView);
            EmptyTools.setImg(R.drawable.dingdanwu_icon);
            EmptyTools.setMessage("您还没有优惠券,赶快去领取吧");
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }


        loginResponse.setToken(response.getToken());
        LoginMessageUtils.saveloginmsg(baseContext, loginResponse);


    }


    @Override
    public void error(String errorMsg) {
        mListView.onRefreshComplete();
        ProgrosDialog.closeProgrosDialog();
        showToast(R.string.server_link_fault);
    }


    /**
     * 显示数据
     */
    private void showData() {
        if (!StringUtil.isEmpty(list)) {
            adapter.setData(list);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        list.clear();
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        page++;
        getData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(baseContext, CouponDetailActivity.class);
        intent.putExtra("COUPON_DETAIL", list.get(position - 1).getCoupon().getRemark());
        startActivity(intent);
    }


}
