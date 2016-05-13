package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.CouponAdapter;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ActivityCouponResponse;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.GsonUtil;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangce on 5/11/2016.
 */
@ContentView(R.layout.activity_coupon)
public class CouponActivity extends BaseActivity {

    @ViewInject(R.id.pl_list)
    private PullToRefreshListView pullListView;

    @ViewInject(R.id.left_action)
    private Button left_action;

    @ViewInject(R.id.title)
    private TextView title;

    private List<ActivityCouponResponse.MsgBean> list = new ArrayList<>();

    private CouponAdapter adapter;

    private int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        init();
    }

    private void init() {
        left_action.setText("返回");
        title.setText("优惠券中心");
        adapter = new CouponAdapter(baseContext, list);
        pullListView.setAdapter(adapter);

        getServerData();
    }

    private void getServerData() {
        ProgrosDialog.openDialog(baseContext);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_COUPON + NetInterface.GETLISTCOUPON + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        param.put("token", loginResponse.getToken());
        param.put("pageSize", 10);
        param.put("pageNumber", page);
        param.put(Constant.PARAMETER_TAG, NetInterface.GETLISTCOUPON);
        netWorkHelper.PostJson(url, param, this);
    }

    @Override
    public void receive(String TAG, String data) {
        ProgrosDialog.closeProgrosDialog();
        switch (TAG) {
            case NetInterface.GETLISTCOUPON:

                ActivityCouponResponse couponResponse = (ActivityCouponResponse) GsonUtil.getInstance().convertJsonStringToObject(data, ActivityCouponResponse.class);
                if (!couponResponse.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    showToast(couponResponse.getDesc());
                    return;
                }


                List<ActivityCouponResponse.MsgBean> temp = couponResponse.getMsg();

                if (null != temp)
                    list.addAll(temp);
                if (0 == list.size()) {
                    EmptyTools.setEmptyView(baseContext, pullListView);
                    EmptyTools.setImg(R.drawable.dingdanwu_icon);
                    EmptyTools.setMessage("您还没有优惠券");
                } else {
                    adapter.setData(list);
                }


                loginResponse.setToken(couponResponse.getToken());
                LoginMessageUtils.saveloginmsg(baseContext, loginResponse);

                break;
        }


    }

    @OnClick({R.id.left_action})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.left_action:
                finish();
                break;
        }

    }
}
