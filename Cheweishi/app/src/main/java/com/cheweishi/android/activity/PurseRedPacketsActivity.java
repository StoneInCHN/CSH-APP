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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
public class PurseRedPacketsActivity extends BaseActivity implements
        OnRefreshListener2<ListView>, AdapterView.OnItemClickListener {

    private static final int TELEPHONE_CODE = 0;
    @ViewInject(R.id.left_action)
    private Button left_action;
    @ViewInject(R.id.title)
    private TextView title;
    @ViewInject(R.id.tv_red_purse)
    private TextView tv_red_purse;
    @ViewInject(R.id.no_data)
    private LinearLayout no_data;

    // 上拉加载下拉刷新列表
    @ViewInject(R.id.hongbao_xlistview)
    private PullToRefreshListView mListView;
    @ViewInject(R.id.telephonemoney_linearlayout_nodata)
    private LinearLayout mLinearLayout;
    // 定义加载的页面数量
    private int page = 1;
    // item的数据
    private List<ChargeResponse.MsgBean> mList;

    private String amount = "";

    // 定义一个私有的话费详情adapter
    private RedPacketsDetailsAdapter redPacketsAdapter;
    private boolean isone = true;

    private boolean isNet = false;


    private List<MyCouponResponse.MsgBean> list;

    private MyCouponAdapter adapter;

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

        mListView.setOnRefreshListener(this);
        mListView.setMode(Mode.PULL_FROM_START);

        list = new ArrayList<>();
        adapter = new MyCouponAdapter(baseContext, list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        getData();


//        mList = new ArrayList<ChargeResponse.MsgBean>();
//        for (int i = 0; i < 10; i++) {
//            ChargeResponse.MsgBean msgBean = new ChargeResponse.MsgBean();
//            msgBean.setRemark("1111");
//            msgBean.setRedPacket(10);
//            mList.add(msgBean);
//        }
//        redPacketsAdapter = new RedPacketsDetailsAdapter(
//                PurseRedPacketsActivity.this, mList);
//        mListView.setAdapter(redPacketsAdapter);
//
//
//        amount = getIntent().getStringExtra("redPacket");
//        if (!StringUtil.isEmpty(amount)) {
//            tv_red_purse.setText(amount);
//        } else {
//            double money = 0;
//            for (int i = 0; i < mList.size(); i++) {
//                money += mList.get(i).getMoney();
//            }
//            tv_red_purse.setText("￥" + money);
//        }


    }

    @OnClick({R.id.left_action, R.id.title})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_action:// 返回
                PurseRedPacketsActivity.this.finish();
                break;

            default:
                break;
        }
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
