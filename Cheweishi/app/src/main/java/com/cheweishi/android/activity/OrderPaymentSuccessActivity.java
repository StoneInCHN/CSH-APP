package com.cheweishi.android.activity;

import com.cheweishi.android.cheweishi.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 订单支付成功
 * 
 * @author mingdasen
 * 
 */
@ContentView(R.layout.activity_order_payment_success)
public class OrderPaymentSuccessActivity extends BaseActivity {
	@ViewInject(R.id.left_action)
	private Button left_action;

	@ViewInject(R.id.title)
	private TextView title;

	@ViewInject(R.id.tv_store_name)
	private TextView tv_store_name;
	@ViewInject(R.id.tv_goods_name)
	private TextView tv_goods_name;

	@ViewInject(R.id.tv_price)
	private TextView tv_price;
	@ViewInject(R.id.tv_order_sn)
	private TextView tv_order_sn;
	@ViewInject(R.id.tv_effective_time)
	private TextView tv_effective_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		initView();
	}

	private void initView() {
		left_action.setText(R.string.back);
		title.setText("支付成功");
		tv_store_name.setText(getIntent().getStringExtra("store_name"));
		tv_goods_name.setText(getIntent().getStringExtra("goods_name"));
		tv_price.setText("￥" + getIntent().getStringExtra("price") + "元");
		tv_order_sn.setText(getIntent().getStringExtra("order_sn"));
		tv_effective_time.setText(getIntent().getStringExtra("effectiveTime"));
	}

	@OnClick({ R.id.tv_see_details, R.id.left_action })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_see_details:
			Intent intent = new Intent(OrderPaymentSuccessActivity.this, OrderDetailsActivity.class);
//			intent.putExtra("order_sn", getIntent().getStringExtra("order_sn"));
			intent.putExtra("order_id", getIntent().getStringExtra("order_id"));
			Log.i("result", "====order_id====" + getIntent().getStringExtra("order_id"));
			startActivity(intent);
			finish();
			break;
		case R.id.left_action:
			finish();
			break;
		default:
			break;
		}
	}
}
