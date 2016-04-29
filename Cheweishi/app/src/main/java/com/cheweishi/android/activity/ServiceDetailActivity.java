package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.biz.XUtilsImageLoader;

/**
 * Created by Tanck on 2016/4/28.
 */
public class ServiceDetailActivity extends BaseActivity implements View.OnClickListener {

    private Button left_action;

    private ImageView iv_service_detail_img;

    private TextView tv_service_detail_desc;

    private TextView title, tv_service_detail_title;
    private LinearLayout ll_service_detail_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        left_action = (Button) findViewById(R.id.left_action);
        iv_service_detail_img = (ImageView) findViewById(R.id.iv_service_detail_img);
        tv_service_detail_desc = (TextView) findViewById(R.id.tv_service_detail_desc);
        tv_service_detail_title = (TextView) findViewById(R.id.tv_service_detail_title);
        ll_service_detail_no = (LinearLayout) findViewById(R.id.ll_service_detail_no);
        title = (TextView) findViewById(R.id.title);
        title.setText("服务详情");
        left_action.setOnClickListener(this);

        initView();
    }

    private void initView() {
        String imgPath = getIntent().getStringExtra("img");
        String desc = getIntent().getStringExtra("desc");

        XUtilsImageLoader.getxUtilsImageLoader(this, R.drawable.zhaochewei_img,
                iv_service_detail_img, imgPath);
        if (null == desc || "".equals(desc)) {
            tv_service_detail_title.setVisibility(View.GONE);
            ll_service_detail_no.setVisibility(View.VISIBLE);
        } else
            tv_service_detail_desc.setText(desc);
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
