package com.cheweishi.android.activity;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;
import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.webkit.JsResult;
import android.webkit.GeolocationPermissions.Callback;

public class SCActivity extends BaseActivity {
	private SharedPreferences preferences;
	@ViewInject(R.id.scWeb)
	private WebView scWeb;
	private WebSettings settings;
	private boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_sc);
		ViewUtils.inject(this);
		connectToServer();
	}

	private void initViews() {
		settingsWeb();
		scWeb.loadUrl("http://pay.chcws.com/wap/");
	}

	/**
	 * webview设置不跳转到其他浏览器上
	 */
	WebViewClient client = new WebViewClient() {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// loadHistoryUrls.add(url);
			// view.loadUrl(url);
			return false;

		}

		public void onPageFinished(WebView view, String url) {
			ProgrosDialog.closeProgrosDialog();
			// CookieManager cookieManager = CookieManager.getInstance();
			// String CookieStr = cookieManager.getCookie(url);
			// String jsString =
			// "function addcookie(name,value,expireHours){var cookieString=name+\"=\"+escape(value)+\"; path=/\";if(expireHours>0){var date=new Date();date.setTime(date.getTime+expireHours*3600*1000);cookieString=cookieString+\"; expire=\"+date.toGMTString();}document.cookie=cookieString;}";
			if (!flag) {
				scWeb.loadUrl("javascript:addcookie('username','" + username
						+ "')");
				scWeb.loadUrl("javascript:addcookie('key','" + key + "')");
				// scWeb.reload();
				flag = true;
			}
			// scWeb.loadUrl("http://182.92.82.119/wap/");
		};

		public void onPageStarted(WebView view, String url,
				android.graphics.Bitmap favicon) {
			ProgrosDialog.openDialog(SCActivity.this);

		};
	};

	private void settingsWeb() {
		// if (settings == null) {
		// settings = this.scWeb.getSettings();
		// }
		settings = this.scWeb.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		// settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		if (Build.VERSION.SDK_INT >= 11)
			this.scWeb.getSettings().setDisplayZoomControls(false);
		while (true) {
			settings.setAllowFileAccess(true);
			settings.setCacheMode(1);
			settings.setDomStorageEnabled(true);
			settings.setDatabaseEnabled(true);
			// web.setWebChromeClient(new WebChromeClient());
			cachedata(this.scWeb);
			break;
		}
		// // 启用数据库
		// settings.setDatabaseEnabled(true);
		// String dir = this.getApplicationContext()
		// .getDir("database", Context.MODE_PRIVATE).getPath();
		// // // 启用地理定位
		// settings.setGeolocationEnabled(true);
		// // 设置定位的数据库路径
		// settings.setGeolocationDatabasePath(dir);
		// // 最重要的方法，一定要设置，这就是出不来的主要原因
		// settings.setDomStorageEnabled(true);
		scWeb.setWebViewClient(client);
		webChromeClient();
	}

	private void cachedata(View paramView) {
		try {
			Field localField = WebView.class
					.getDeclaredField("mZoomButtonsController");
			localField.setAccessible(true);
			ZoomButtonsController localZoomButtonsController = new ZoomButtonsController(
					paramView);
			localZoomButtonsController.getZoomControls().setVisibility(8);
			localField.set(paramView, localZoomButtonsController);
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	private void webChromeClient() {
		scWeb.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					ProgrosDialog.closeProgrosDialog();
				} else {
					ProgrosDialog.openDialog(SCActivity.this);
				}

			}

			// 处理javascript中的confirm
			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				Builder builder = new Builder(SCActivity.this);
				builder.setTitle("来自网页的消息");
				builder.setMessage(message);
				builder.setPositiveButton(android.R.string.ok,
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								result.confirm();
							}
						});
				builder.setNegativeButton(android.R.string.cancel,
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								result.cancel();
							}
						});
				builder.setCancelable(false);
				builder.create();

				if (SCActivity.this.isFinishing()) {
					builder = null;
				} else {
					builder.show();
				}
				return true;
			}

			@Override
			public void onGeolocationPermissionsHidePrompt() {
				super.onGeolocationPermissionsHidePrompt();
				// / Log.i(LOGTAG, "onGeolocationPermissionsHidePrompt");
			}

			@Override
			public void onGeolocationPermissionsShowPrompt(final String origin,
					final Callback callback) {
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);

			}
		});
	}

	private void connectToServer() {
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("username", loginMessage.getTel());
		rp.addBodyParameter("password", "123123");
		rp.addBodyParameter("client", "wap");
		httpBiz = new HttpBiz(this);
		httpBiz.httPostData(10001,
				"http://pay.chcws.com/mobile/index.php?act=login", rp, this);
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		switch (type) {
		case 400:
			showToast(R.string.server_link_fault);
			break;
		case 10001:
			parseJSON(data);
			break;
		}
	}

	String username;
	String key;

	private void parseJSON(String result) {
		if (StringUtil.isEmpty(result)) {
			showToast(R.string.data_fail);
		} else {
			System.out.println("SUCCESS============" + result);
			try {
				JSONObject json = new JSONObject(result);
				username = json.optJSONObject("datas").optString("username");
				key = json.optJSONObject("datas").optString("key");
				// 从assets目录下面的加载html
				System.out.println("cookie=====================" + username
						+ "========" + key);

				initViews();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// /**
	// * cookie处理
	// *
	// * @param context
	// * @param url
	// */
	// public void synCookies(Context context, String url, String userName,
	// String key) {
	// String jsString =
	// "function addcookie(name,value,expireHours){var cookieString=name+\"=\"+escape(value)+\"; path=/\";if(expireHours>0){var date=new Date();date.setTime(date.getTime+expireHours*3600*1000);cookieString=cookieString+\"; expire=\"+date.toGMTString();}document.cookie=cookieString;}";
	// scWeb.loadUrl("javascript:" + jsString);
	// preferences = getSharedPreferences("cookie" + "username=" + userName
	// + ";path=/", MODE_PRIVATE);
	// preferences = getSharedPreferences("cookie" + "key=" + key + ";path=/",
	// MODE_PRIVATE);
	// CookieSyncManager cookieSyncManager = CookieSyncManager
	// .createInstance(context);
	// cookieSyncManager.sync();
	// CookieManager cookieManager = CookieManager.getInstance();
	// // cookieManager.removeAllCookie();
	// cookieManager.setCookie(url, preferences.getString("cookie", ""));
	// CookieSyncManager.getInstance().sync();
	// }

	// public void test() {
	//
	// ScriptEngineManager manager = new ScriptEngineManager();
	//
	// ScriptEngine engine = manager.getEngineByName("javascript");
	//
	// try {
	//
	// engine.eval("var a=3; var b=4;print (a+b);");
	//
	// // engine.eval("alert(\"js alert\");"); // 不能调用浏览器中定义的js函数 //
	// // 错误，会抛出alert引用不存在的异常
	//
	// } catch (ScriptException e) {
	//
	// e.printStackTrace();
	// }
	// }
}
