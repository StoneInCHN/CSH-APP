package com.cheweishi.android.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.WebViewView;

/**
 * 网页
 * 
 * @author zhangq
 * 
 */
public class WebActivity extends BaseActivity {
	private WebView mWebView;
	private Button tvLeft;
	private TextView tvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);

		if (getIntent().getExtras() == null
				|| StringUtil.isBlank(getIntent().getExtras().getString("url"))) {
			Log.i("zzqq", "webview" + "没有url参数！");
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
		webViewUtil.openUrl(getIntent().getExtras().getString("url"));
		webViewUtil.alarm404();

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
		mWebView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mWebView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mWebView.onPause();
		super.onPause();
	}

}
