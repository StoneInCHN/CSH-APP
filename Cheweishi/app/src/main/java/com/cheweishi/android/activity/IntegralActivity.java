package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

/***
 * 积分
 * 
 * @author lw
 * 
 */
public class IntegralActivity extends BaseActivity implements OnScrollListener,
		OnRefreshListener2<ListView> {
	private static final int INTEGRAL_CODE = 2001;
	// 头部信息
	@ViewInject(R.id.title)
	private TextView mTitle;
	// 返回的button
	@ViewInject(R.id.left_action)
	private Button mLeft;
	// 下拉加载的listview
	@ViewInject(R.id.integral_xlistview)
	private PullToRefreshListView mListView;
	// 返回顶部的button
	@ViewInject(R.id.integral_return_top)
	private Button mReturnTop;
	// 积分说明
	@ViewInject(R.id.integral_imageview_integralhelp)
	private ImageView mIntegralHelpImageView;
	// 总积分
	@ViewInject(R.id.integral_xlistview_item_textview_aggregatescore)
	private TextView integral_xlistview_item_textview_aggregatescore;
	// 今日新增积分
	@ViewInject(R.id.integral_tv_today)
	private TextView integral_tv_today;
	// 里程积分
	@ViewInject(R.id.integral_tv_today)
	private TextView integral_tv_note_jifen;
	// 签到积分
	@ViewInject(R.id.integral_tv_sign_jifen)
	private TextView integral_tv_sign_jifen;
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
		setContentView(R.layout.activity_integral);
		ViewUtils.inject(this);
		loginMessage = LoginMessageUtils.getLoginMessage(this);
		view = getLayoutInflater().inflate(R.layout.integralhaddherderview,
				null);
		ViewUtils.inject(this, view);
		mList = new ArrayList<IntegralInfo>();
		// 设置为只能上拉加载
		mListView.setMode(Mode.PULL_FROM_END);
		// 得到listview
		listView = mListView.getRefreshableView();
		// 设置上拉下拉监听
		mListView.setOnRefreshListener(this);
		// 为返回顶部按钮添加监听
		mReturnTop.setOnClickListener(listener);
		listView.setOnScrollListener(this);
		// 添加头部view并设置没有点击效果
		listView.addHeaderView(view, null, false);
		listView.setVerticalScrollBarEnabled(true);
		init();
	}

	/**
	 * 初始化方法
	 */
	private void init() {
		mLeft.setText(getResources().getString(R.string.back));
		mTitle.setText(getString(R.string.integral));
		mLeft.setOnClickListener(listener);
		mIntegralHelpImageView.setOnClickListener(listener);
		setNow();
		request();

	}

	/***
	 * 显示当前剩余积分
	 */
	private void setNow() {
		// TODO Auto-generated method stub
		if (loginMessage != null) {
			integral_xlistview_item_textview_aggregatescore.setText(""
					+ loginMessage.getScore().getNow());
		}
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.integral_imageview_integralhelp:
				Intent intent = new Intent(IntegralActivity.this,
						IntegralHelpActivity.class);
				startActivity(intent);
				break;
			case R.id.left_action:
				IntegralActivity.this.finish();
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
	protected void onDestroy() {
		super.onDestroy();
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
		integral_tv_sign_jifen.setText(sign);
		integral_tv_today.setText(total);
		integral_tv_note_jifen.setText(mile);
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

	/****
	 * 重写滑动方法，headerview隐藏时显示返回顶部按钮
	 */
	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if (arg1 > 1) {
			mReturnTop.setVisibility(View.VISIBLE);
		} else {
			mReturnTop.setVisibility(View.GONE);
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	/**
	 * 禁用下拉刷新
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	/***
	 * 上拉加载
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (!isNet) {
			page++;
			request();
		} else {
			handler.sendEmptyMessage(1);
		}

	}

	Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			mListView.onRefreshComplete();
		};

	};

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case INTEGRAL_CODE:
			mListView.onRefreshComplete();
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

}
