package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.DrivingBehaviorAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.DrvingBehaviorDetail;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 
 * @author Xiaojin车辆报告-驾驶行为详情
 * 
 */
public class DrvingBehaviorActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.drvingBehaviorListView)
	private ListView drvingBehaviorListView;
	private List<DrvingBehaviorDetail> listDrvingBehaviorDetail = new ArrayList<DrvingBehaviorDetail>();
	private List<DrvingBehaviorDetail> listDrvingBehaviorDetailTemp = new ArrayList<DrvingBehaviorDetail>();
	private DrivingBehaviorAdapter adapter;
	@ViewInject(R.id.left_action)
	private TextView left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.drvingBehaviorTitle)
	private TextView drvingBehaviorTitle;
	@ViewInject(R.id.drvingBehaviorContent)
	private TextView drvingBehaviorContent;
	private int type;
	@ViewInject(R.id.drving_img)
	private ImageView drving_img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drving_behavior);
		ViewUtils.inject(this);
		initViews();
		connectToServer();
	}

	private void initViews() {
		httpBiz = new HttpBiz(this);
		adapter = new DrivingBehaviorAdapter(this, listDrvingBehaviorDetail);
		drvingBehaviorListView.setAdapter(adapter);
		drvingBehaviorListView.setSelector(android.R.color.transparent);
		drvingBehaviorListView.setOnItemClickListener(onItemClickListener);
		left_action.setText(R.string.back);
		title.setText(R.string.behavior_detail);
		Intent intent = getIntent();
		int pageIndex = intent.getIntExtra("pageIndex", 0);
		judgePage(pageIndex);
	}

	/**
	 * 判断是急加速、急转弯、急刹车还是疲劳驾驶
	 * 
	 * @param pageIndex
	 */
	private void judgePage(int pageIndex) {
		switch (pageIndex) {
		case 0:
			drving_img.setImageResource(R.drawable.xiangqing_jijiasu2x);
			type = 1;
			drvingBehaviorTitle.setText(R.string.behavior_jijiasu);
			drvingBehaviorContent.setText(R.string.behavior_jijiasu_detail);
			break;
		case 1:
			drving_img.setImageResource(R.drawable.xiangqing_jishache2x);
			drvingBehaviorTitle.setText(R.string.behavior_jishache);
			drvingBehaviorContent.setText(R.string.behavior_jishache_detail);
			type = 3;
			break;
		case 2:
			drving_img.setImageResource(R.drawable.xiangqing_jizhuanwan2x);
			type = 2;
			drvingBehaviorTitle.setText(R.string.behavior_jizhuanwan);
			drvingBehaviorContent.setText(R.string.behavior_jizhuanwan_detail);
			break;
		case 3:
			drving_img.setImageResource(R.drawable.xiangqing_pilaojiashi2x);
			drvingBehaviorTitle.setText(R.string.behavior_jpiliaojiashi);
			type = 4;
			drvingBehaviorContent
					.setText(R.string.behavior_jpiliaojiashi_detail);
			break;
		default:
			drving_img.setImageResource(R.drawable.xiangqing_jijiasu2x);
			type = 1;
			drvingBehaviorTitle.setText(R.string.behavior_jijiasu);
			drvingBehaviorContent.setText(R.string.behavior_jijiasu_detail);
			break;
		}
	}

	@OnClick({ R.id.left_action })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			DrvingBehaviorActivity.this.finish();
			break;
		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(DrvingBehaviorActivity.this,
					FootmarkMapActivity.class);
			if (!StringUtil.isEmpty(listDrvingBehaviorDetail)
					&& listDrvingBehaviorDetail.size() > 0) {
				intent.putExtra("DrivingBehavior", "DrivingBehavior");
				intent.putExtra("latlon", listDrvingBehaviorDetail.get(arg2)
						.getLon()
						+ ","
						+ listDrvingBehaviorDetail.get(arg2).getLat());
			}
			DrvingBehaviorActivity.this.startActivity(intent);
		}

	};

	/**
	 * 请求服务器
	 */
	private void connectToServer() {

		if (isLogined()) {
			ProgrosDialog.openDialog(this);
			listDrvingBehaviorDetailTemp.clear();
			if (hasDevice()) {
				RequestParams rp = new RequestParams();
				rp.addBodyParameter("uid", loginMessage.getUid());
				rp.addBodyParameter("key", loginMessage.getKey());
				rp.addBodyParameter("time", getIntent().getStringExtra("time"));
				rp.addBodyParameter("type", type + "");
				rp.addBodyParameter("cid", loginMessage.getCar().getCid());
				httpBiz.httPostData(1, API.DRIVING_BEHAVIOR, rp, this);
			} else {
				showToast(R.string.not_add_car);
				Intent intent = new Intent(this, AddCarActivity.class);
				this.startActivity(intent);
			}
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			this.startActivity(intent);
		}

	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 1:
			parseRepairJSON(data);
			break;
		}
	}

	/**
	 * 对服务器返回数据进行解析
	 * 
	 * @param result
	 */
	private void parseRepairJSON(String result) {
		if (StringUtil.isEmpty(result)) {
			showToast(R.string.data_fail);
		} else {
			System.out.println("驾驶行为=================" + result);
			try {
				JSONObject jsonObject1 = new JSONObject(result);
				String resultStr = jsonObject1.optString("operationState");
				if (StringUtil.isEquals(resultStr, API.returnSuccess, true)) {
					JSONObject jsonObject = new JSONObject(result)
							.optJSONObject("data");
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<List<DrvingBehaviorDetail>>() {
					}.getType();
					listDrvingBehaviorDetail = gson.fromJson(
							jsonObject.optString("list"), type);
					adapter.setData(listDrvingBehaviorDetail);
				} else if (StringUtil.isEquals(resultStr, API.returnRelogin,
						true)) {
					DialogTool.getInstance(this).showConflictDialog();
				} else if (StringUtil.isEquals(resultStr, API.returnFail, true)) {
					showToast(jsonObject1.optJSONObject("data")
							.optString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DialogTool.getInstance(this).dismissConflictDialog();
	}
}
