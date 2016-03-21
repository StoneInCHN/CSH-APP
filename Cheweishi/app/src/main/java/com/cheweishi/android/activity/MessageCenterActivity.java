package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;

import com.cheweishi.android.cheweishi.R;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * Created by tangce on 3/21/2016.
 */

public class MessageCenterActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center_new);
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
