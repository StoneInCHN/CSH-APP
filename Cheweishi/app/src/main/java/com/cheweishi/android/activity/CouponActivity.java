package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.CouponAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnChildClick;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

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

    private CouponAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        init();
    }

    private void init() {
        left_action.setText("返回");
        title.setText("优惠券中心");


        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("");
        }
        adapter = new CouponAdapter(baseContext, list);
        pullListView.setAdapter(adapter);
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
