package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.R;

/**
 * Created by tangce on 5/18/2016.
 */
public class CouponDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_coupon_detail;

    private TextView title;

    private Button left_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);

        init();

    }

    private void init() {
        tv_coupon_detail = (TextView) findViewById(R.id.tv_coupon_detail);
        left_action = (Button) findViewById(R.id.left_action);
        left_action.setText("返回");
        title = (TextView) findViewById(R.id.title);
        left_action.setOnClickListener(this);
        title.setText("优惠券详情");
        tv_coupon_detail.setText(getIntent().getStringExtra("COUPON_DETAIL"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_action:
                finish();
                break;
        }
    }
}
