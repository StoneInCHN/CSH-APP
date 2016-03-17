package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.IntegralAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.IntegralInfo;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.XCRoundImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;


/**
 * 我的积分
 * @author XMH
 *
 */
public class PurseIntegralActivity extends BaseActivity {

	
	private static final int INTEGRAL_CODE = 2001;
	
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.xcRoundImg)
	private XCRoundImageView xcRoundImg;
	// 下拉加载的listview
	@ViewInject(R.id.integral_xlistview)
	private ListView mListView;
	// 返回顶部的button
	@ViewInject(R.id.integral_return_top)
	private Button mReturnTop;
	//总积分
	@ViewInject(R.id.tv_balance_num)
	private TextView tv_balance_num;
	
	// 列表
	private ListView listView;
	// 积分的适配器
	private IntegralAdapter mIntegralAdapter;

	private LoginMessage loginMessage;
	// 装数据的容器
	private List<IntegralInfo> list;

	private List<IntegralInfo> mList;
	// 页数
	private int page = 1;
	// 每页的条数
	private int size = 10;

	private boolean isTwo;

	private boolean isNet = false;
	// 定义viewholer的view
	private View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purse_certificates);
		ViewUtils.inject(this);
		
		
		loginMessage = LoginMessageUtils.getLoginMessage(this);
		mList = new ArrayList<IntegralInfo>();
		
		init();
	}
	
	private void init(){
		title.setText(R.string.purse_my_integral);
		left_action.setText(getResources().getString(R.string.back));
		left_action.setOnClickListener(listener);
		setNow();
//		request();
	}
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.left_action:
				PurseIntegralActivity.this.finish();
				break;
			case R.id.integral_return_top:
				listView.setSelection(0);
				break;
			default:
				break;
			}
		}
	};



	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case INTEGRAL_CODE:
			try {
				JSONObject jsonObject = new JSONObject(data);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnSuccess, true)) {
					JSONObject dataJsonObject = jsonObject
							.optJSONObject("data");
					if (page == 1) {
						getValue(dataJsonObject);
					}
					analysis(dataJsonObject);

				} else {
					if (StringUtil.isEquals(
							jsonObject.optString("operationState"),
							API.returnRelogin, true)) {
						DialogTool.getInstance(this).showConflictDialog();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	
	/***
	 * 解析json数据
	 * 
	 * @param dataJsonObject
	 */
	protected void analysis(JSONObject dataJsonObject) {
		// TODO Auto-generated method stub
		JSONArray historyJsonArray = dataJsonObject.optJSONArray("list");
		if (historyJsonArray.length() == 0) {
			if (page == 1) {
				getNotInformation(getString(R.string.no_data));
				IntegralAdapter integralAdapter = new IntegralAdapter(this,
						list);
				listView.setAdapter(integralAdapter);

			}
			if (!isTwo) {
				getNotInformation(getString(R.string.load_full));
				isNet = true;
				isTwo = true;
			}
		} else {
			Gson gson = new Gson();
			java.lang.reflect.Type type = new TypeToken<List<IntegralInfo>>() {
			}.getType();
			list = gson.fromJson(dataJsonObject.optString("list"), type);
			mList.addAll(list);
			if (mIntegralAdapter == null) {
				mIntegralAdapter = new IntegralAdapter(this, mList);
				mListView.setAdapter(mIntegralAdapter);
			} else {
				mIntegralAdapter.setList(mList);
			}
		}
	}

	/***
	 * 显示加载完成的数据
	 * 
	 * @param msg
	 */
	private void getNotInformation(String msg) {
		// TODO Auto-generated method stub
		TextView textView = new TextView(this);
		textView.setGravity(Gravity.CENTER);
		textView.setText(msg);
		listView.addFooterView(textView);

	}

	/***
	 * 设置值
	 * 
	 * @param dataJsonObject
	 */
	protected void getValue(JSONObject dataJsonObject) {
		// TODO Auto-generated method stub
		String sign = dataJsonObject.optString("sign");
		String total = dataJsonObject.optString("total");
		String mile = dataJsonObject.optString("mile");
	};

	/**
	 * 网络请求方法
	 */
	private void request() {
		RequestParams params = new RequestParams(HTTP.UTF_8);
		if (LoginMessageUtils.getLoginMessage(this) != null
				&& LoginMessageUtils.getLoginMessage(this).getUid() != null
				&& !LoginMessageUtils.getLoginMessage(this).getUid().equals("")) {
			setParams(params);
			httpBiz = new HttpBiz(this);

			ProgrosDialog.openDialog(this);
			httpBiz.httPostData(INTEGRAL_CODE, API.INTEGRALURL, params, this);
		}
	}

	/**
	 * set参数
	 * 
	 * @param params
	 */
	private void setParams(RequestParams params) {
		// TODO Auto-generated method stub
		params.addBodyParameter("uid", loginMessage.getUid());
		params.addBodyParameter("key", loginMessage.getKey());
		params.addBodyParameter("score", loginMessage.getScore().getCid());
		params.addBodyParameter("page", page + "");
		params.addBodyParameter("size", size + "");
	}

	/***
	 * 显示当前剩余积分
	 */
	private void setNow() {
		// TODO Auto-generated method stub
		if (loginMessage != null) {
			tv_balance_num.setText(getIntent().getStringExtra("score"));
		}
	}
}
