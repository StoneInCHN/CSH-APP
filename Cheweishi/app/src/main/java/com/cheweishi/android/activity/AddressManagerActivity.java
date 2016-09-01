package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.entity.MyRecevieAddressResponse;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * Created by tangce on 8/31/2016.
 */
public class AddressManagerActivity extends BaseActivity {

    @ViewInject(R.id.left_action)
    private Button left_action; // 左边标题

    @ViewInject(R.id.title)
    private TextView title; // 标题

    @ViewInject(R.id.et_adm_name)
    private EditText et_adm_name;//收货人名字

    @ViewInject(R.id.et_adm_phone)
    private EditText et_adm_phone;//收货人电话

    @ViewInject(R.id.tv_adm_area)
    private TextView tv_adm_area;//收货人地区

    @ViewInject(R.id.cb_adm_def)
    private CheckBox cb_adm_def;//是否默认

    @ViewInject(R.id.et_adm_detail_address)
    private EditText et_adm_detail_address;//收货人详细地址

    private MyRecevieAddressResponse.MsgBean data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        left_action.setText(R.string.back);

        data = (MyRecevieAddressResponse.MsgBean) getIntent().getSerializableExtra("data");
        if (null != data) {
            title.setText(R.string.edit_address);
            et_adm_name.setText(data.getConsignee());
            et_adm_phone.setText(data.getPhone());
            et_adm_detail_address.setText(data.getAddress());
            tv_adm_area.setText(data.getAreaName());
            if (data.isIsDefault())
                cb_adm_def.setChecked(true);
        } else {
            title.setText(R.string.add_new_address);
        }
    }

    @OnClick({R.id.left_action, R.id.tv_adm_area})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_action:
                finish();
                break;
            case R.id.tv_adm_area: // 地区修改
                break;
        }
    }
}
