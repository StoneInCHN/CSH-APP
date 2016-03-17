package com.cheweishi.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 绑定成功二级界面、不是第一次进入界面
 * 
 * @author zhangq
 * 
 */
public class CarManagerAfterBindActivity extends Activity {
	public static final String TAG = "CarManagerAfterBindActivity";
	/**
	 * 绑定二级界面
	 */
	public static final int INDEX_BIND_SECOND = 1001;
	/**
	 * 非首次绑定界面
	 */
	public static final int INDEX_NOT_FIRST = 1002;

	@ViewInject(R.id.tv_not_tips_1)
	private TextView tvNotTipsFir;
	@ViewInject(R.id.tv_not_tips_2)
	private TextView tvNotTipsSec;
	@ViewInject(R.id.btn_return)
	private Button btnReturn;
	@ViewInject(R.id.btn_go)
	private Button btnGo;
	@ViewInject(R.id.tv_tips)
	private TextView tvTips;
	@ViewInject(R.id.img_top)
	private ImageView imgTop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_after_device);
		ViewUtils.inject(this);
		initView();
	}

	private void initView() {
		Intent mIntent = getIntent();
		int index = mIntent.getIntExtra(TAG, 0);
		if (index == 0) {
			CarManagerAfterBindActivity.this.finish();
		}
		if (index == INDEX_BIND_SECOND) {
			tvNotTipsFir.setVisibility(View.INVISIBLE);
			tvNotTipsSec.setVisibility(View.INVISIBLE);
			imgTop.setImageResource(R.drawable.bangding_xuanze);
		}
		if (index == INDEX_NOT_FIRST) {
			tvTips.setVisibility(View.INVISIBLE);
			btnGo.setVisibility(View.INVISIBLE);
			imgTop.setImageResource(R.drawable.bangding_bangding);
		}

		btnGo.setOnClickListener(onClickListener);
		btnReturn.setOnClickListener(onClickListener);
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_go:
				// 跳转“我的财富”界面
				startActivity(new Intent(CarManagerAfterBindActivity.this,
						MyTreasureActivity.class));
				Constant.CURRENT_REFRESH = Constant.CAR_MANAGER_REFRESH;
				Intent mIntent = new Intent();
				mIntent.setAction(Constant.REFRESH_FLAG);
				sendBroadcast(mIntent);
				CarManagerAfterBindActivity.this.finish();
				break;
			case R.id.btn_return:
				// 返回车辆列表界面
				onBackPressed();
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onBackPressed() {
		Constant.CURRENT_REFRESH = Constant.CAR_MANAGER_REFRESH;
		Intent mIntent = new Intent();
		mIntent.setAction(Constant.REFRESH_FLAG);
		sendBroadcast(mIntent);
		this.finish();
	}
}
