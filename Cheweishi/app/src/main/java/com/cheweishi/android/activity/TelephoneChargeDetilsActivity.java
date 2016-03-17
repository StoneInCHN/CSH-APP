package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.TelephonEchargeDetailsAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ParkInfo;
import com.cheweishi.android.entity.ReturnTheMoneyInfo;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/*****
 * 话费详情
 * 
 * @author lw
 * 
 */

public class TelephoneChargeDetilsActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {

	private static final int TELEPHONE_CODE = 0;
	// 头部标题
	@ViewInject(R.id.title)
	private TextView mTitle;
	// 头部返回
	@ViewInject(R.id.left_action)
	private Button mLeft;
	// 上拉加载下拉刷新列表
	@ViewInject(R.id.telephonechargedetils_refteshlistview)
	private PullToRefreshListView pullToRefreshListView;
	@ViewInject(R.id.telephonemoney_linearlayout_nodata)
	private LinearLayout mLinearLayout;
	// 定义加载的页面数量
	private int page = 1;
	private ListView mListView;
	// item的数据
	private List<ReturnTheMoneyInfo> list;
	private List<ReturnTheMoneyInfo> mList;

	private TextView textView;
	// 定义一个私有的话费详情adapter
	private TelephonEchargeDetailsAdapter telephonEchargeDetailsAdapter;
	private boolean isone = true;

	private boolean isNet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_telephone_charge_detils);
		ViewUtils.inject(this);
		// 设置上拉加载下拉刷新监听
		pullToRefreshListView.setOnRefreshListener(this);
		// 设置listview的上拉加载下拉刷新
		pullToRefreshListView.setMode(Mode.BOTH);
		// 通过pullToRefreshListView得到listview
		mListView = pullToRefreshListView.getRefreshableView();
		// 给list实例化对象
		mList = new ArrayList<ReturnTheMoneyInfo>();
		init();
	}

	// 初始化方法
	private void init() {
		// TODO Auto-generated method stub
		mTitle.setText(getString(R.string.telephonechargedetils));
		mLeft.setText(getResources().getString(R.string.back));
		// 返回监听方法
		mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				TelephoneChargeDetilsActivity.this.finish();
			}
		});
		request();
	}

	// 下拉刷新方法
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		page = 1;
		if (!StringUtil.isEmpty(list)) {
			list.clear();
		}
		isone = true;
		mListView.removeFooterView(textView);
		isNet = false;
		if (!StringUtil.isEmpty(mList)) {
			mList.clear();
		}
		request();
	}

	// 上拉加载更多方法
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isNet) {
			handler.sendEmptyMessage(1);
		} else {
			page++;
			request();
		}
	}

	Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			pullToRefreshListView.onRefreshComplete();
		};

	};

	// 網絡請求方法
	private void request() {
		RequestParams params = new RequestParams(HTTP.UTF_8);
		if (LoginMessageUtils.getLoginMessage(this) != null) {
			params.addBodyParameter("uid",
					LoginMessageUtils.getLoginMessage(this).getUid());
			params.addBodyParameter("key",
					LoginMessageUtils.getLoginMessage(this).getKey());
			params.addBodyParameter("account", LoginMessageUtils
					.getLoginMessage(this).getAccount().getAid());
			params.addBodyParameter("type", "call");
			params.addBodyParameter("page", "" + page);
			params.addBodyParameter("size", "10");
			HttpBiz httpBiz = new HttpBiz(this);
			ProgrosDialog.openDialog(this);
			httpBiz.httPostData(TELEPHONE_CODE, API.MyMoney_URL, params, this);

		}
	}

	@Override
	public void receive(int type, String data) {
		super.receive(type, data);
		pullToRefreshListView.onRefreshComplete();
		System.out.println(data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case TELEPHONE_CODE:
			json(data);
			break;
		default:
			break;
		}
	}

	private void json(String data) {
		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(jsonObject.optString("operationState"),
					API.returnSuccess, true)) {
				JSONObject dataJsonObject = jsonObject.optJSONObject("data");
				JSONArray jsonArray = dataJsonObject.optJSONArray("list");
				if (jsonArray != null && jsonArray.length() == 0) {
					if (page == 1) {
						mLinearLayout.setVisibility(View.VISIBLE);
					} else {
						if (isone) {
							textView = new TextView(this);
							textView.setText(getString(R.string.no_more));
							textView.setTextColor(Color.BLACK);
							textView.setPadding(10, 30, 10, 30);
							textView.setGravity(Gravity.CENTER_HORIZONTAL);
							mListView.addFooterView(textView);
							isone = false;
							isNet = true;
						}
					}
				} else {
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<List<ReturnTheMoneyInfo>>() {
					}.getType();
					list = gson
							.fromJson(dataJsonObject.getString("list"), type);

					mList.addAll(list);
					// 把数据添加到adapter中
					if (telephonEchargeDetailsAdapter == null) {
						telephonEchargeDetailsAdapter = new TelephonEchargeDetailsAdapter(
								TelephoneChargeDetilsActivity.this, mList);
						mListView.setAdapter(telephonEchargeDetailsAdapter);
					} else {
						// 上拉加载后的数据填充
						telephonEchargeDetailsAdapter.setlist(mList);
					}
				}
			} else {
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnRelogin, true)) {
					DialogTool.getInstance(this).showConflictDialog();
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
