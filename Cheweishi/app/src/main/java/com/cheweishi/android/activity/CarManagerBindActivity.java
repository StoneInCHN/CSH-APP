package com.cheweishi.android.activity;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.ActivityControl;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.CustomDialog.Builder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 绑定成功、添加没有设备界面
 * 
 * @author zhangq
 * 
 */
public class CarManagerBindActivity extends BaseActivity {
	public static final String Tag = "CarManagerBindActivity";
	/**
	 * 第一次绑定界面
	 */
	public static final int INDEX_BIND_FIRST = 1000;
	/**
	 * 没有设备界面
	 */
	public static final int INDEX_NO_DEVICE = 1001;
	/**
	 * 用户选择话费
	 */
	public static final int SELECT_HUAFEI = 101;
	/**
	 * 用户选择反费
	 */
	public static final int SELECT_FANFEI = 102;
	@ViewInject(R.id.llayout_first)
	private LinearLayout lLayoutBindFirst;
	@ViewInject(R.id.btn_huafei)
	private Button btnHuaFei;// 话费
	@ViewInject(R.id.btn_fanfei)
	private Button btnFanFei;// 反费
	@ViewInject(R.id.btn_about_huafei)
	private Button btnAboutHuaFei;// 关于话费
	@ViewInject(R.id.btn_about_fanfei)
	private Button btnAboutFanFei;// /关于返费

	@ViewInject(R.id.tv_first)
	private TextView tvFirst;
	@ViewInject(R.id.tv_second)
	private TextView tvSecond;

	@ViewInject(R.id.rlayout_no_device)
	private RelativeLayout rLayoutNoDevice;
	@ViewInject(R.id.btn_return)
	private Button btnReturn;// 返回
	@ViewInject(R.id.btn_go)
	private Button btnGo;// 去看看

	private int index;
	private CarManager carManagerTemp;
	private String currentCid;

	private String strBindFirst1;
	private String strBindFirst2;
	private String strNoDevice1;
	private String strNoDevice2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_device);
		ViewUtils.inject(this);
		httpBiz = new HttpBiz(this);
		initView();
	}

	private void initView() {
		Intent mIntent = getIntent();

		index = mIntent.getIntExtra(Tag, 0);
		ActivityControl.removeActivityFromName(SettingActivity.class.getName());
		if (index == 0) {
			CarManagerBindActivity.this.finish();
		}
		strBindFirst1 = getString(R.string.bind_no_device1);
		strBindFirst2 = getString(R.string.bind_no_device2);
		strNoDevice1 = getString(R.string.bind_no_device1);
		strNoDevice2 = getString(R.string.bind_no_device2);

		// if (index == INDEX_BIND_FIRST || ) {
		// changeContainer(lLayoutBindFirst, rLayoutNoDevice);
		// tvFirst.setText(strBindFirst1);
		// tvSecond.setText(strBindFirst2);
		// currentCid = mIntent.getStringExtra("currentCid");
		// }

		if (index == INDEX_NO_DEVICE || index == INDEX_BIND_FIRST) {
			changeContainer(rLayoutNoDevice, lLayoutBindFirst);
			tvFirst.setText(strNoDevice1);
			tvSecond.setText(strNoDevice2);
			Intent intent = getIntent();
			// Toast.makeText(this, "haha1", Toast.LENGTH_LONG).show();
			if (intent != null) {

				Bundle bundle = intent.getExtras();
				if (bundle != null && bundle.getSerializable("car") != null) {
					// Toast.makeText(this, "haha", Toast.LENGTH_LONG).show();
					carManagerTemp = (CarManager) bundle.getSerializable("car");
				}
			}
		}
	}

	/**
	 * 切换内容
	 * 
	 * @param showView
	 *            显示控件
	 * @param goneView
	 *            隐藏控件
	 */
	private void changeContainer(View showView, View goneView) {
		if (showView.getVisibility() == View.GONE) {
			showView.setVisibility(View.VISIBLE);
		}
		if (goneView.getVisibility() == View.VISIBLE) {
			goneView.setVisibility(View.GONE);
		}
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.btn_huafei:
			showCustomDialog(SELECT_HUAFEI);
			break;
		case R.id.btn_fanfei:
			showCustomDialog(SELECT_FANFEI);
			break;
		case R.id.btn_about_huafei:
			// 跳转话费说明
			startActivity(new Intent(CarManagerBindActivity.this,
					TelephoneExplainActivity.class));
			// CarManagerBindActivity.this.finish();
			break;
		case R.id.btn_about_fanfei:
			// 跳转反费说明
			startActivity(new Intent(CarManagerBindActivity.this,
					ReturnFeeThatActivity.class));
			// CarManagerBindActivity.this.finish();
			break;
		case R.id.btn_go:
			Intent intent = new Intent(CarManagerBindActivity.this,
					AddCarActivity.class);
			intent.putExtra("AddCarActivity", "AddCarActivity");
			Bundle bundle = new Bundle();
			bundle.putSerializable("car", carManagerTemp);
			intent.putExtras(bundle);
			startActivity(intent);
			CarManagerBindActivity.this.finish();
			break;
		case R.id.btn_return:
			// judgeCurrentRefreahGoBack();
			Constant.CURRENT_REFRESH = Constant.CAR_MANAGER_REFRESH;
			Intent mIntent = new Intent();
			mIntent.setAction(Constant.REFRESH_FLAG);
			sendBroadcast(mIntent);
			CarManagerBindActivity.this.finish();
			break;
		default:
			break;
		}
	}

	private void postInternet(int type) {
		LoginMessage loginMessage = LoginMessageUtils.getLoginMessage(this);
		if (loginMessage == null) {
			return;
		}
		if (loginMessage.getCar() == null) {
			return;
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", loginMessage.getUid());
		params.addBodyParameter("key", loginMessage.getKey());
		params.addBodyParameter("cid", currentCid);
		params.addBodyParameter("type", String.valueOf(type));
		httpBiz.getPostData(1000, API.CHOOSE_FEED, params, this,
				httpBiz.POSTNUM, this);

	}

	private void judgeCurrentRefreahGoBack() {
		System.out.println("SUCCESS========" + "判断");

		Constant.CURRENT_REFRESH = Constant.CAR_MANAGER_REFRESH;
		Intent mIntent = new Intent();
		mIntent.setAction(Constant.REFRESH_FLAG);
		sendBroadcast(mIntent);
	}

	@Override
	public void receive(int type, String data) {
		try {
			JSONObject js = new JSONObject(data);
			if ("SUCCESS".equalsIgnoreCase(js.getString("operationState"))) {
				judgeCurrentRefreahGoBack();
				js = js.getJSONObject("data");

				Intent i = new Intent(CarManagerBindActivity.this,
						CarManagerAfterBindActivity.class);
				i.putExtra(CarManagerAfterBindActivity.TAG,
						CarManagerAfterBindActivity.INDEX_BIND_SECOND);
				try {
					startActivity(i);

				} catch (Exception e) {
					startActivity(i);
				}
				setResult(5);
				CarManagerBindActivity.this.finish();
			} else if ("RELOGIN".equalsIgnoreCase(js
					.getString("operationState"))) {
				DialogTool.getInstance(CarManagerBindActivity.this)
						.showConflictDialog();
			} else {
				js = js.getJSONObject("data");
				showToast(js.getString("msg"));
			}
		} catch (Exception e) {

		}

	}

	private CustomDialog.Builder mBuilder;

	private void showCustomDialog(final int select) {
		if (mBuilder == null) {
			mBuilder = new Builder(this);
		}
		String message = "";
		if (select == SELECT_HUAFEI) {
			message = this.getString(R.string.bind_huafei);
		}
		if (select == SELECT_FANFEI) {
			message = this.getString(R.string.bind_fanfei);
		}
		String s1 = this.getString(R.string.bind_you_sure) + message + "?";
		String s2 = this.getString(R.string.bind_cant_change);
		int len1 = s1.length();
		int len2 = s2.length();
		SpannableString ss = new SpannableString(s1 + s2);
		ss.setSpan(new ForegroundColorSpan(Color.rgb(255, 0, 0)), len1, len1
				+ len2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		mBuilder.setMessage(ss);
		mBuilder.setPositiveButton(this.getString(R.string.sure),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						int type = 0;
						if (select == SELECT_FANFEI) {
							type = 2;
						} else if (select == SELECT_HUAFEI) {
							type = 1;
						}
						postInternet(type);
					}

				});
		mBuilder.setNegativeButton(this.getString(R.string.cancel),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				});
		mBuilder.create().show();
	}

	@Override
	public void onBackPressed() {
		Constant.CURRENT_REFRESH = Constant.CAR_MANAGER_REFRESH;
		Intent mIntent = new Intent();
		mIntent.setAction(Constant.REFRESH_FLAG);
		sendBroadcast(mIntent);
		if (index == INDEX_BIND_FIRST) {
			showToast(this.getString(R.string.bind_not_choose));
		} else {
			super.onBackPressed();
		}
		
	}

}
