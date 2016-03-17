package com.cheweishi.android.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.WashcarHistoryAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.WashcarHistoryVO;
import com.cheweishi.android.entity.WashcarVO;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.utils.DateUtils;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 预约洗车订单列表
 * 
 * @author zhangq
 * 
 */
@ContentView(R.layout.activity_washcar_history)
public class WashcarHistoryActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {
	// http://app.chcws.com:8080/XAI/app/up/appGainWashList.do?uid=1&key=1&page=1&size=10
	public static final String TAG = "WashcarHistoryActivity";
	@ViewInject(R.id.title)
	private TextView tvTitle;
	@ViewInject(R.id.left_action)
	private Button btnLeft;
	@ViewInject(R.id.lv_myOrder)
	private PullToRefreshListView lv_myOrder;
	private ListView mListView;
//	@ViewInject(R.id.layoutNoData)
//	private LinearLayout layoutNoData;
	// @ViewInject(R.id.car_order)
	// private Button car_order;
	// @ViewInject(R.id.shopp_order)
	// private Button shopp_order;
	// private int count = -1;
	List<WashcarHistoryVO> lists = null;
	// private int count = -1;

	private WashcarHistoryAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ViewUtils.inject(this);

		initView();

		initData();
	}

	private void initView() {
		tvTitle.setText(R.string.my_order);
		// count = getIntent().getIntExtra("count", -1);
		int height = DateUtils.dip2Px(this, 5);
		TextView tvHead = new TextView(this);
		tvHead.setLayoutParams(new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT, height));
		lv_myOrder.setOnRefreshListener(this);
		// 设置listview的上拉加载下拉刷新
		lv_myOrder.setMode(Mode.BOTH);
		// 通过pullToRefreshListView得到listview
		mListView = lv_myOrder.getRefreshableView();
		mListView.addHeaderView(tvHead);
		mListView.addFooterView(tvHead);

		mListView.setOnItemClickListener(itemClickListener);
	}

	private void initData() {
		if (!isLogined()) {
			return;
		}

		ProgrosDialog.openDialog(this);

		httpBiz = new HttpBiz(this);
		RequestParams mRequestParams = new RequestParams();
		mRequestParams.addBodyParameter("uid", loginMessage.getUid());
		mRequestParams.addBodyParameter("key", loginMessage.getKey());
		mRequestParams.addBodyParameter("page", 1 + "");
		mRequestParams.addBodyParameter("size", 10 + "");

		httpBiz.httPostData(1000, API.WASHCAR_HISTORY, mRequestParams, this);
	}

	@Override
	public void receive(int type, String data) {
		Log.i(TAG, data);
		ProgrosDialog.closeProgrosDialog();
		try {
			JSONObject json = new JSONObject(data);
			if ("SUCCESS".equals(json.get("operationState"))) {

				Gson gson = new Gson();
				Type typeToken = new TypeToken<List<WashcarHistoryVO>>() {
				}.getType();

				json = json.getJSONObject("data");
				JSONArray arr = json.getJSONArray("list");
				lists = gson.fromJson(arr.toString(), typeToken);
				Log.i("result", "==list.size==" + lists.size());// lists.size();
			} else if ("RELOGIN".equalsIgnoreCase(json
					.getString("operationState"))) {
				DialogTool.getInstance(this).showConflictDialog();
			} else {
				json = json.getJSONObject("data");
				showToast(json.getString("msg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (lists == null || lists.size() == 0) {
			//layoutNoData.setVisibility(View.VISIBLE);
		} else {
			mAdapter = new WashcarHistoryAdapter(lists, this);
			mListView.setAdapter(mAdapter);
		}
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			WashcarHistoryVO vo = (WashcarHistoryVO) parent
					.getItemAtPosition(position);
			Intent intent;
			if ("1".equals(vo.getStatus())) {
				intent = new Intent(WashcarHistoryActivity.this,
						WashcarDetailsActivity.class);
				intent.putExtra("index", WashcarDetailsActivity.INDEX_FROM_ING);
				intent.putExtra("uno", vo.getUno());
			} else {
				intent = new Intent(WashcarHistoryActivity.this,
						WashcarDetailsActivity.class);
				intent.putExtra("index",
						WashcarDetailsActivity.INDEX_FROM_HISTORY);
				intent.putExtra("uno", vo.getUno());
				if (!StringUtil.isEmpty(vo)) {
					if (!StringUtil.isEmpty(vo.getStatus())
							&& "1".equals(vo.getStatus())) {
						intent = new Intent(WashcarHistoryActivity.this,
								WashcarDetailsActivity.class);
						intent.putExtra("index",
								WashcarDetailsActivity.INDEX_FROM_ING);
						intent.putExtra("uno", vo.getUno());
					} else {
						intent = new Intent(WashcarHistoryActivity.this,
								WashcarDetailsActivity.class);
						intent.putExtra("index",
								WashcarDetailsActivity.INDEX_FROM_HISTORY);
						intent.putExtra("uno", vo.getUno());
					}
					// intent.putExtra("count", count);
					startActivity(intent);
				}
				// intent.putExtra("count", count);
				startActivity(intent);
			}
		}
	};

	@OnClick(R.id.left_action)
	public void btnClick(View v) {
		finish();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	// @OnClick({ R.id.car_order, R.id.shopp_order })
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.car_order:
	// initData();
	// layoutNoData.setVisibility(View.GONE);
	// break;
	// case R.id.shopp_order:
	// // 清空list，并刷新
	// lists.clear();
	// mAdapter.notifyDataSetChanged();
	// layoutNoData.setVisibility(View.VISIBLE);
	// break;
	//
	// default:
	// break;
	// }
	//
	// }

}
