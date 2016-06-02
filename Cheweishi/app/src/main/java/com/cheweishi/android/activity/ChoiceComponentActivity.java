package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.ChoiceComponentAdapter;
import com.cheweishi.android.entity.ComponentServiceResponse;
import com.cheweishi.android.widget.UnSlidingExpandableListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;

/**
 * Created by tangce on 6/2/2016.
 */
public class ChoiceComponentActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.left_action)
    private Button left_action;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.tv_choice_service)
    private TextView tv_choice_service; // 服务名字

    @ViewInject(R.id.usl_choice_content)
    private UnSlidingExpandableListView usl_choice_content; // 主要的Item

    @ViewInject(R.id.tv_choice_total_money)
    private TextView tv_choice_total_money; // 总价格

    @ViewInject(R.id.tv_choice_ok)
    private TextView tv_choice_ok;// 确认

    private ChoiceComponentAdapter adapter;

    private ComponentServiceResponse response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_component);
        ViewUtils.inject(this);

        init();
    }

    private void init() {
        title.setText(R.string.back);

        if (null != response) {
            adapter = new ChoiceComponentAdapter(baseContext, response);
            usl_choice_content.setAdapter(adapter);
        }
    }


    @OnClick({R.id.left_action})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_action:
                finish();
                break;
        }
    }
}
