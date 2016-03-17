package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.InformationSecondAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.CircleInformation;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author Xiaojin车资讯二级
 * 
 */
public class InformationSecondListActivity extends BaseActivity {

	@ViewInject(R.id.listView_information)
	private ListView listView_information;
	private InformationSecondAdapter adapter;
	private List<CircleInformation> listCircleInformation = new ArrayList<CircleInformation>();
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private Button left_action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information_second_list);
		ViewUtils.inject(this);
		httpBiz = new HttpBiz(this);
		title.setText(R.string.car_life_new_dynamic);
		left_action.setText(R.string.back);
		adapter = new InformationSecondAdapter(
				InformationSecondListActivity.this, listCircleInformation);
		listView_information.setAdapter(adapter);
		listView_information.setSelector(R.drawable.more_pressed);
		left_action.setOnClickListener(onClickListener);
		listView_information.setOnItemClickListener(onItemClickListener);
		connectToServer();
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.left_action:
				InformationSecondListActivity.this.finish();
				break;
			}
		}
	};

	private void connectToServer() {
		ProgrosDialog.openDialog(this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("type", "车生活管家");
		httpBiz.httPostData(1, API.INFORMATION1_URL, rp, this);
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		switch (type) {
		case 1:
			parseJSON(data);
			break;
		}
	}

	private void parseJSON(String result) {
		Log.i("========", result);
		ProgrosDialog.closeProgrosDialog();
		if (StringUtil.isEmpty(result)) {

		} else {
			System.out.println("chezixun" + result);
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnSuccess, true)) {
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<List<CircleInformation>>() {
					}.getType();
					listCircleInformation = gson.fromJson(jsonObject
							.optJSONObject("data").optString("list"), type);
					adapter = new InformationSecondAdapter(
							InformationSecondListActivity.this,
							listCircleInformation);
					listView_information.setAdapter(adapter);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(InformationSecondListActivity.this,
					CircleInformationDetailActivity.class);
			intent.putExtra("id", listCircleInformation.get(arg2).getId());
			InformationSecondListActivity.this.startActivity(intent);
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ProgrosDialog.closeProgrosDialog();
	}

}
