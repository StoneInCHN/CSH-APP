package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.OrderExLvAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.UnSlidingOrderExpandapleListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
/**
 * 取消订单
 * 
 * @author Xiaojin
 * 
 */
public class CancelOrderActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.lv_order)
	private UnSlidingOrderExpandapleListView listView;
	private OrderExLvAdapter adapter;
	@ViewInject(R.id.imgphone)
	private ImageView imgphone;// 商家电话
	@ViewInject(R.id.imgMap)
	private ImageView imgMap;// 商家地址
	private CustomDialog.Builder builder;
	private CustomDialog phoneDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cancel_orderone);
		ViewUtils.inject(this);
		init();
		listView = (UnSlidingOrderExpandapleListView) findViewById(R.id.lv_order);
		// adapter = new OrderExLvAdapter(this);
		// listView.setAdapter(adapter);
		listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {

				return false;
			}
		});

		listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				Log.e("hefeng",
						"ExpandableListView ChildClickListener groupPosition="
								+ arg2 + "||childPosition=" + arg3);
				return false;
			}
		});
	}

	private void init() {
		title.setText("订单详情");
		left_action.setText(R.string.back);
		right_action.setVisibility(View.GONE);
		connectToServer();
	}

	private void connectToServer() {
		ProgrosDialog.openDialog(this);
		RequestParams rp = new RequestParams();
		httpBiz = new HttpBiz(this);

	}

	@Override
	public void receive(int type, String data) {

		super.receive(type, data);
		switch (type) {
		case 100001:
			parseJSON(data);
			break;

		default:
			break;
		}
	}

	private void parseJSON(String data) {
		if (StringUtil.isEmpty(data)) {
			return;
		}
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(jsonObject.optString("state"),
					API.returnSuccess, true)) {

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@OnClick({ R.id.left_action, R.id.imgphone, R.id.imgMap })
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.imgphone:// 商家电话
			showPhoneDialog();
			break;
		case R.id.imgMap:// 商家地址

			break;

		default:
			break;
		}
	}

	/**
	 * 联系商家对话框
	 */
	private void showPhoneDialog() {
		builder = new CustomDialog.Builder(this);
		builder.setMessage(R.string.phone_msg);
		builder.setPositiveButton(R.string.customerServiceCall,
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
