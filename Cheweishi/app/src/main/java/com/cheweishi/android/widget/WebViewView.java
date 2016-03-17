package com.cheweishi.android.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;

/**
 * WebView
 * 
 * eg:
 * 
 * WebViewView webViewUtil = new WebViewView();
 * webViewUtil.setWebView(mWebView); webViewUtil.setContext(mContext);
 * webViewUtil.setJS(true); webViewUtil.setSelfAdaption();
 * webViewUtil.setNoCache();
 * webViewUtil.openUrl(getIntent().getExtras().getString("url"));
 * webViewUtil.alarm404();
 * 
 * @author WG
 * 
 */

public class WebViewView {
	protected static final String TAG = "WebViewView";
	private Context mContext;
	private WebView mWebView;

	/**
	 * 设置webview
	 * 
	 * @param mContext
	 */

	public void setWebView(WebView webView) {
		mWebView = webView;
	}

	/**
	 * 打开URL
	 * 
	 * @param url
	 */
	public void openUrl(String url) {
		mWebView.loadUrl(url);
	}

	/**
	 * 网页提醒
	 */
	public void alarm404() {
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {

				// ToastUtil.showS(mContext, "Oh no!" + description);
				Log.e(TAG, "Oh no!" + description);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});
	}

	/**
	 * 显示进度
	 */

	public void showShowProgress() {
		// getActivity().getWindow().requestFeature(Window.FEATURE_PROGRESS);
	}

	/**
	 * 设置进度
	 */

	public void setProgress() {
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {

				((Activity) mContext).setProgress(newProgress);
				super.onProgressChanged(view, newProgress);
			}

		});
	}

	/**
	 * 启动JAVASCRIPT
	 * 
	 * @param flag
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public void setJS(boolean flag) {
		mWebView.getSettings().setJavaScriptEnabled(flag);
	}

	/**
	 * 禁止缓存
	 * 
	 * @param flag
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public void setNoCache() {
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
	}

	/**
	 * 加载进来的页面自适应手机屏幕
	 * 
	 * @param flag
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public void setSelfAdaption() {
		mWebView.getSettings()
				.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.setInitialScale(25);
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

}
