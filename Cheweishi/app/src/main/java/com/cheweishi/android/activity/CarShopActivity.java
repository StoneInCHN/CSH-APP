package com.cheweishi.android.activity;

import android.os.Bundle;

import com.cheweishi.android.adapter.CarShopAdapter;
import com.yunjia365.android.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Administrator on 2016/3/21.
 */
public class CarShopActivity extends BaseActivity {

    private CarShopAdapter adapter;

    private PullToRefreshListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_shop);
        listView = (PullToRefreshListView) findViewById(R.id.pl_car_shop);
        adapter = new CarShopAdapter(baseContext,null);
        listView.setAdapter(adapter);
    }
}
