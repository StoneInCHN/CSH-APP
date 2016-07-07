package com.cheweishi.android.fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.widget.XCRoundImageView;

/**
 * Created by tangce on 7/6/2016.
 */
public class MyFragment extends BaseFragment {

    // 头像
    private XCRoundImageView iv_myAccountUserIcon;

    // 用户名
    private TextView tv_my_name;

    //电话号码
    private TextView tv_my_phone;

    // 设置
    private TextView tv_setting;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

    }
}
