package com.cheweishi.android.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cheweishi.android.R;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ReadNewsResponse;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.WebViewView;

import java.util.HashMap;
import java.util.Map;

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
    private String newsId;// 新闻id
    private String url;//web url
    private String title;//标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        tvTitle = (TextView) findViewById(R.id.web_title);
        loading = (ProgressBar) findViewById(R.id.pb_web_loading);
        tvLeft = (Button) findViewById(R.id.left_action);

        tvLeft.setOnClickListener(this);
        tvLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                WebActivity.this.finish();
            }
        });

        newsId = getIntent().getStringExtra("id");
        url = getIntent().getExtras().getString("url");
        title = getIntent().getExtras().getString("title");
        // 是否设置标题
        if (!StringUtil.isBlank(title)) {
            tvTitle.setText(title);
        }


        if (StringUtil.isEmpty(newsId)) {
            if (StringUtil.isBlank(url)) { // &&!url.contains("http")
                finish();
                return;
            } else {
                openWebView(url);
            }
        } else { // 为新闻
            sendPacketForUrl();
        }
    }

    private void sendPacketForUrl() {
        ProgrosDialog.openDialog(baseContext);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_NEWS + NetInterface.READ_NEWS + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        param.put("token", loginResponse.getToken());
        param.put("newsId", newsId);
        param.put(Constant.PARAMETER_TAG, NetInterface.READ_NEWS);
        netWorkHelper.PostJson(url, param, this);
    }

    @Override
    public void receive(String TAG, String data) {
        ProgrosDialog.closeProgrosDialog();
        switch (TAG) {
            case NetInterface.READ_NEWS: // 阅读新闻
                ReadNewsResponse newsResponse = (ReadNewsResponse) GsonUtil.getInstance().convertJsonStringToObject(data, ReadNewsResponse.class);
                if (!newsResponse.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    showToast(newsResponse.getDesc());
                    return;
                }

                title = newsResponse.getMsg().getTitle();
                title = StringUtil.isEmpty(title) ? getResources().getString(R.string.see_details) : title;
                tvTitle.setText(title);
                url = newsResponse.getMsg().getContentUrl();
                if (StringUtil.isEmpty(url))
                    finish();
                openWebView(url);

                loginResponse.setToken(newsResponse.getToken());
                break;
        }
    }

    private void openWebView(String url) {
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
        webViewUtil.openUrl(url);
        loading.setProgress(30);
    }


    @Override
    protected void onResume() {
        try {
            mWebView.onResume();
        } catch (Exception e) {
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            mWebView.destroy();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        try {
            mWebView.onPause();
        } catch (Exception e) {
        }
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
