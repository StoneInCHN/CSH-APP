package com.cheweishi.android.activity;

import android.os.Bundle;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.CouponAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangce on 5/11/2016.
 */
@ContentView(R.layout.activity_coupon)
public class CouponActivity extends BaseActivity {

    @ViewInject(R.id.pl_list)
    private PullToRefreshListView pullListView;

    private CouponAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        init();
    }

    private void init() {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("");
        }
        adapter = new CouponAdapter(baseContext, list);
        pullListView.setAdapter(adapter);
    }
}
