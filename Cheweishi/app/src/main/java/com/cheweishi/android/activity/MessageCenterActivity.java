package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yunjia365.android.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * Created by tangce on 3/21/2016.
 */



public class MessageCenterActivity extends BaseActivity implements View.OnClickListener {


    @ViewInject(R.id.left_action)
    private Button left_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center_new);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.left_action)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.left_action:
                finish();
                break;
        }
    }
}
