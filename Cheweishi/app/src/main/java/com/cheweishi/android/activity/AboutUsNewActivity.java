package com.cheweishi.android.activity;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.widget.CustomDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author zhangQ
 * @date 创建时间：2015-12-3 下午4:18:39
 * @version 1.0
 * @Description:
 */
public class AboutUsNewActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.phone_about_us)
	private TextView phone_about_us;
	@ViewInject(R.id.URL_about_us)
	private TextView URL_about_us;

	private CustomDialog.Builder builder;
	private CustomDialog phoneDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us_new);
		ViewUtils.inject(this);
		initViews();
	}

	/**
	 * 初始化布局
	 */
	private void initViews() {
		left_action.setText(R.string.back);
		loginMessage = LoginMessageUtils.getLoginMessage(this);
		title.setText(R.string.about_us);
		phone_about_us.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		URL_about_us.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

	}

	@OnClick({ R.id.left_action, R.id.phone_about_us, R.id.URL_about_us })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.URL_about_us:
			Uri uri = Uri.parse("http://www.chcws.com");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			break;
		case R.id.phone_about_us:
			showPhoneDialog();
			break;

		default:
			break;
		}

	}

	private void showPhoneDialog() {
		builder = new CustomDialog.Builder(this);
		builder.setTitle(R.string.customerServicePhone);
		builder.setPositiveButton(R.string.call_out,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:"
										+ getResources().getString(
												R.string.customerServicePhone)));
						startActivity(intent);

					}
				});

		builder.setNegativeButton(R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// setRadioButtonLight();
					}
				});
		phoneDialog = builder.create();
		phoneDialog.show();

	}
}
