package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.DefaultAddressResponse;
import com.cheweishi.android.utils.GsonUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangce on 8/29/2016.
 */
public class ShopPayOrderActivity extends BaseActivity {

    @ViewInject(R.id.left_action)
    private Button left_action; // 左边标题

    @ViewInject(R.id.title)
    private TextView title; // 标题


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_pay_order);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        left_action.setText(R.string.back);
        title.setText(R.string.confrim_order);
        getDefaultAddress();
    }

    @OnClick({R.id.left_action})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_action:
                finish();
                break;
        }
    }

    private void getDefaultAddress() {
        ProgrosDialog.openDialog(baseContext);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_SHOP_ADD + NetInterface.GET_DEFAULT_ADDRESS + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        param.put("token", loginResponse.getToken());
        param.put(Constant.PARAMETER_TAG, NetInterface.GET_DEFAULT_ADDRESS);
        netWorkHelper.PostJson(url, param, this);
    }


    @Override
    public void receive(String TAG, String data) {

        switch (TAG) {
            case NetInterface.GET_DEFAULT_ADDRESS:
                DefaultAddressResponse addressResponse = (DefaultAddressResponse) GsonUtil.getInstance().convertJsonStringToObject(data, DefaultAddressResponse.class);
                if (!addressResponse.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    ProgrosDialog.closeProgrosDialog();
                    showToast(addressResponse.getDesc());
                    return;
                }


                loginResponse.setToken(addressResponse.getToken());
                break;
        }

        ProgrosDialog.closeProgrosDialog();
    }
}
