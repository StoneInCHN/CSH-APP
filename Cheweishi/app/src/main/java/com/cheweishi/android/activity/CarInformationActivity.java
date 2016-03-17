package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.CarInfoAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.CircleInformation;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 车资讯
 */
public class CarInformationActivity extends BaseActivity {

	@ViewInject(R.id.left_action)
	private Button btn_left;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView tv_rigth;
	@ViewInject(R.id.carinfo_list)
	private ListView listView;
	private CarInfoAdapter adapter;
	private List<CircleInformation> list = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_carinfo_layout);
		ViewUtils.inject(this);
		list = new ArrayList<CircleInformation>();
		tv_rigth.setVisibility(View.INVISIBLE);
		title.setText(R.string.information_car);
		btn_left.setText(R.string.back);
		// 加载数据
		getCarInfomation();
		listView.setSelector(R.drawable.more_pressed);
		listView.setOnItemClickListener(listener);

		btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	/**
	 * item点击监听
	 */
	OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(CarInformationActivity.this,
					InformationSecondListActivity.class);
			intent.putExtra("type", list.get(arg2).getType());
			Log.i("=======", list.get(arg2).getType());
			startActivity(intent);
		}
	};

	/**
	 * 加载车资讯数据
	 */
	private void getCarInfomation() {
		ProgrosDialog.openDialog(CarInformationActivity.this);
		httpBiz = new HttpBiz(CarInformationActivity.this);
		httpBiz.httPostData(101, API.INFORMATION_URL, null, this);
	}

	/**
	 * 接收数据
	 */
	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		if (type == 101) {
			parseData(data);
		} else if (type == 400) {
			EmptyTools.setEmptyView(this, listView);
		}

	}

	/**
	 * 数据解析
	 * 
	 * @param data
	 */
	private void parseData(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(R.string.data_fail);
		} else {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(data);
				String status = jsonObject.optString("operationState");
				if (StringUtil.isEquals(status, "SUCCESS", true)) {
					Gson gson = new Gson();
					java.lang.reflect.Type jsonType = new TypeToken<List<CircleInformation>>() {
					}.getType();
					list = gson.fromJson(jsonObject.optJSONObject("data")
							.optString("list"), jsonType);
					Log.i("========", list + "");
					adapter = new CarInfoAdapter(this, list);
					listView.setAdapter(adapter);
				} else {
					EmptyTools.setEmptyView(this, listView);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
