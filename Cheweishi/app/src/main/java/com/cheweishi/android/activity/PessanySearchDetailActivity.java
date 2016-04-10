package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.PessanyResponse;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * Created by Administrator on 2016/4/10.
 */
public class PessanySearchDetailActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.left_action)
    private Button left_action;
    @ViewInject(R.id.tv_pessany_detail_plate)
    private TextView tv_pessany_detail_plate;
    @ViewInject(R.id.tv_pessany_detail_score)
    private TextView tv_pessany_detail_score;
    @ViewInject(R.id.tv_pessany_detail_money)
    private TextView tv_pessany_detail_money;
    @ViewInject(R.id.tv_pessany_detail_content)
    private TextView tv_pessany_detail_content;
    @ViewInject(R.id.tv_pessany_detail_address)
    private TextView tv_pessany_detail_address;
    @ViewInject(R.id.tv_pessany_detail_site)
    private TextView tv_pessany_detail_site;
    @ViewInject(R.id.tv_pessany_detail_date)
    private TextView tv_pessany_detail_date;

    private PessanyResponse.MsgBean data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        setContentView(R.layout.activity_pessany_detail);
        data = (PessanyResponse.MsgBean) getIntent().getSerializableExtra("PessanySearch");
        if (null == data) {
            showToast("页面初始化失败,请重试");
            return;
        }
        left_action.setText(R.string.back);
        setdata(data);
    }

    private void setdata(PessanyResponse.MsgBean data) {
        tv_pessany_detail_plate.setText(data.getPlate());
        tv_pessany_detail_score.setText(data.getScore());
        tv_pessany_detail_money.setText(data.getFinesAmount());
        tv_pessany_detail_address.setText(data.getIllegalAddress());
        tv_pessany_detail_content.setText(data.getIllegalContent());
        tv_pessany_detail_date.setText(data.getIllegalDate());
        tv_pessany_detail_site.setText(data.getProcessingSite());
    }

    @OnClick({R.id.left_action})
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.left_action:
                finish();
                break;
        }
    }
}
