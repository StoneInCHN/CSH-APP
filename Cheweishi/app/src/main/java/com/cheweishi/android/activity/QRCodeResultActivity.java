package com.cheweishi.android.activity;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.tools.RegularExpressionTools;
import com.cheweishi.android.tools.TextViewTools;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 二维码扫描结果显示
 *
 * @author mingdasen
 */
@ContentView(R.layout.activity_qrcode_result)
public class QRCodeResultActivity extends BaseActivity {
    @ViewInject(R.id.left_action)
    private TextView left_action;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.tv_result_content)
    private TextView tv_result_content;

    @ViewInject(R.id.btn_result)
    private Button btn_result;

    private String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        initView();
    }

    // TODO 获取返回结果
    private void initView() {
        left_action.setText(R.string.back);
        title.setText("扫描结果");

        Bundle bundle = getIntent().getExtras();
        result = bundle.getString("result");
        tv_result_content.setText(TextViewTools.ToDBC(result));
    }

    @OnClick({R.id.left_action, R.id.btn_result})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_action:
                finish();
                break;
            case R.id.btn_result:
                if (RegularExpressionTools.isUrl(result)) {
                    visitUrl();
                } else {
                    showToast("扫描结果处理");
                }
                break;
        }
    }

    /**
     * 访问Url // TODO 访问接口
     */
    private void visitUrl() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(result);
        intent.setData(content_url);
        startActivity(intent);
    }


    @Override
    public void receive(String TAG, String data) {
        super.receive(TAG, data);
        // TODO succes 需要更新ui
    }


    @Override
    public void error(String errorMsg) {
        super.error(errorMsg);

        // TODO fail 需要retry或者更新UI
    }
}
