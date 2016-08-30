package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.ShopPayOrderListAdapter;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.DefaultAddressResponse;
import com.cheweishi.android.entity.ShopPayOrderNative;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.widget.ScrollListView;
import com.cheweishi.android.widget.UnSlidingListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangce on 8/29/2016.
 */
public class ShopPayOrderActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @ViewInject(R.id.left_action)
    private Button left_action; // 左边标题

    @ViewInject(R.id.title)
    private TextView title; // 标题

    @ViewInject(R.id.usl_sp_order)
    private ScrollListView usl_sp_order; // 商品列表

    @ViewInject(R.id.cb_sp_order_invoice)
    private CheckBox cb_sp_order_invoice;//发票

    @ViewInject(R.id.tv_sp_order_invoice_notice)
    private TextView tv_sp_order_invoice_notice;//发票提示

    @ViewInject(R.id.ll_sp_order_invoice)
    private LinearLayout ll_sp_order_invoice;//发票内容

    @ViewInject(R.id.tv_sp_total_shop_item)
    private TextView tv_sp_total_shop_item;//商品总数

    @ViewInject(R.id.tv_sp_order_total_money)
    private TextView tv_sp_order_total_money;//总价钱

    @ViewInject(R.id.tv_sp_order_consignee)
    private TextView tv_sp_order_consignee;//收货人

    @ViewInject(R.id.tv_sp_order_consignee_phone)
    private TextView tv_sp_order_consignee_phone;//收货人电话

    @ViewInject(R.id.tv_sp_order_consignee_address)
    private TextView tv_sp_order_consignee_address;//收货人地址

    @ViewInject(R.id.tv_sp_money)
    private TextView tv_sp_money;//总价钱

    private ShopPayOrderListAdapter adapter;

    private List<ShopPayOrderNative> list;


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
        list = (List<ShopPayOrderNative>) getIntent().getSerializableExtra("data");

        if (null != list && 0 < list.size()) {
            usl_sp_order.setFocusable(false);
            adapter = new ShopPayOrderListAdapter(baseContext, list);
            usl_sp_order.setAdapter(adapter);

            tv_sp_total_shop_item.setText("共计" + calcNumber() + "件商品");
            tv_sp_money.setText(String.valueOf(calcMoney()));
            tv_sp_order_total_money.setText("￥" + tv_sp_money.getText());
        }

        cb_sp_order_invoice.setOnCheckedChangeListener(this);

        getDefaultAddress();
    }

    private long calcMoney() {
        long temp = 0;
        for (int i = 0; i < list.size(); i++) {
            temp += Integer.valueOf(list.get(i).getMoney()) * Integer.valueOf(list.get(i).getNumber());
        }
        return temp;
    }

    private int calcNumber() {
        int temp = 0;
        for (int i = 0; i < list.size(); i++) {
            temp += Integer.valueOf(list.get(i).getNumber());
        }
        return temp;
    }

    @OnClick({R.id.left_action,R.id.rl_sp_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_action:
                finish();
                break;
            case R.id.rl_sp_order: // 地址

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

                if (null != addressResponse.getMsg()) { // 有地址
                    tv_sp_order_consignee.setText(addressResponse.getMsg().getConsignee());
                    tv_sp_order_consignee_address.setText(addressResponse.getMsg().getAreaName() + addressResponse.getMsg().getAddress());
                    tv_sp_order_consignee_phone.setText(addressResponse.getMsg().getPhone());
                }

                loginResponse.setToken(addressResponse.getToken());
                break;
        }

        ProgrosDialog.closeProgrosDialog();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            tv_sp_order_invoice_notice.setVisibility(View.VISIBLE);
            ll_sp_order_invoice.setVisibility(View.VISIBLE);
        } else {
            tv_sp_order_invoice_notice.setVisibility(View.GONE);
            ll_sp_order_invoice.setVisibility(View.GONE);
        }
    }
}
