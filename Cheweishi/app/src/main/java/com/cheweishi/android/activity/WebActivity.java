package com.cheweishi.android.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cheweishi.android.R;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.WebViewView;

/**
 * 网页
 *
 * @author zhangq
 */
public class WebActivity extends BaseActivity implements OnClickListener {
    private WebView mWebView;
    private Button tvLeft;
    private TextView tvTitle;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        loading = (ProgressBar) findViewById(R.id.pb_web_loading);
        tvLeft = (Button) findViewById(R.id.left_action);
        tvLeft.setOnClickListener(this);

        if (getIntent().getExtras() == null
                || StringUtil.isBlank(getIntent().getExtras().getString("url"))) {
            Log.i("Tanck", "webview" + "没有url参数！");
            showToast("url解析错误");
            finish();
            return;
        }

        initView();

        mWebView = (WebView) findViewById(R.id.webview);
        WebViewView webViewUtil = new WebViewView();
        webViewUtil.setWebView(mWebView);
        webViewUtil.setContext(WebActivity.this);
        webViewUtil.setJS(true);
        webViewUtil.setSelfAdaption();
        webViewUtil.setNoCache();
        webViewUtil.alarm404();
//        webViewUtil.showShowProgress(this);
        webViewUtil.setProgress(loading);
        webViewUtil.openUrl(getIntent().getExtras().getString("url"));

        loading.setProgress(30);

    }

    private void initView() {
        // 是否设置标题
        if (StringUtil.isBlank(getIntent().getExtras().getString("title"))) {
            return;
        }
        tvTitle = (TextView) findViewById(R.id.title);
        tvLeft = (Button) findViewById(R.id.left_action);

        tvTitle.setText(getIntent().getExtras().getString("title"));
        tvLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                WebActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        try {
            mWebView.onResume();
        }catch (Exception e){}
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            mWebView.destroy();
        }catch (Exception e){}
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        try {
            mWebView.onPause();
        }catch (Exception e){}
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        finish();
    }


    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
