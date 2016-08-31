package com.cheweishi.android.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.entity.MyRecevieAddressResponse;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by tangce on 8/31/2016.
 */
public class AddressManagerActivity extends BaseActivity {

    @ViewInject(R.id.left_action)
    private Button left_action; // 左边标题

    @ViewInject(R.id.title)
    private TextView title; // 标题

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
        } else {
            title.setText(R.string.add_new_address);
        }
    }
}
