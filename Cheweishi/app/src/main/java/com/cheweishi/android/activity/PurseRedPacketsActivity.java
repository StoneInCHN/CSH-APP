package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.RedPacketsDetailsAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.RedPacketsInfo;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.StringUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 我的红包
 * 
 * @author XMh
 * 
 */
public class PurseRedPacketsActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {

	private static final int TELEPHONE_CODE = 0;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.tv_red_purse)
	private TextView tv_red_purse;
	@ViewInject(R.id.no_data)
	private LinearLayout no_data;

	// 上拉加载下拉刷新列表
	@ViewInject(R.id.hongbao_xlistview)
	private PullToRefreshListView mListView;
	@ViewInject(R.id.telephonemoney_linearlayout_nodata)
	private LinearLayout mLinearLayout;
	// 定义加载的页面数量
	private int page = 1;
	// item的数据
	private List<RedPacketsInfo> mList;

	private String amount = "";

	// 定义一个私有的话费详情adapter
	private RedPacketsDetailsAdapter redPacketsAdapter;
	private boolean isone = true;

	private boolean isNet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purse_integtal);
		ViewUtils.inject(this);
		init();

		// info();
		getData();
	}

	private void init() {
		title.setText(R.string.purse_certificates);
		left_action.setText(R.string.back);

		mListView.setOnRefreshListener(this);
		mListView.setMode(Mode.BOTH);

		mList = new ArrayList<RedPacketsInfo>();
		redPacketsAdapter = new RedPacketsDetailsAdapter(
				PurseRedPacketsActivity.this, mList);
		mListView.setAdapter(redPacketsAdapter);
	}

	@OnClick({ R.id.left_action, R.id.title })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:// 返回
			PurseRedPacketsActivity.this.finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 获取红包详情数据
	 */
	private void getData() {
		if (isLogined()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("pageNumber", page + "");
			params.addBodyParameter("pageSize", 20 + "");
			params.addBodyParameter("mobile", loginMessage.getMobile());
			Log.i("result", "==========uid===" + loginMessage.getUid()
					+ "==mobile==" + loginMessage.getMobile() + "==page="
					+ page);
			httpBiz = new HttpBiz(this);
			ProgrosDialog.openDialog(this);
			httpBiz.httPostData(1008, API.CSH_RED_PACKETS_DETAILS_URL, params,
					this);
		}
	}

	@Override
	public void receive(int type, String data) {
		super.receive(type, data);
		mListView.onRefreshComplete();
		ProgrosDialog.closeProgrosDialog();
		switch (type) {
		case 1008:
			parseJSONData(data);
			break;
		}
	}

	/**
	 * 解析数据
	 */
	private void parseJSONData(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(R.string.FAIL);
			return;
		}

		try {
			JSONObject jsonObject = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess,
					jsonObject.optString("state"), true)) {

				JSONObject object = jsonObject.optJSONObject("data");
				amount = object.optString("amount");
				JSONArray array = object.optJSONArray("list");
				RedPacketsInfo info = null;
				JSONObject infoObject = null;
				for (int i = 0; i < array.length(); i++) {
					infoObject = array.optJSONObject(i);
					info = new RedPacketsInfo();
					info.setId(infoObject.optInt("id"));
					info.setAdd_time(infoObject.optString("add_time"));
					info.setAdmin(infoObject.optString("admin"));
					info.setCode(infoObject.optString("code"));
					info.setEffect(infoObject.optString("effect"));
					info.setMoney(infoObject.optDouble("money"));
					info.setStatus(infoObject.optString("status"));
					info.setType(infoObject.optInt("type"));
					mList.add(info);
				}
				showData();

			} else if (StringUtil.isEquals(API.returnRelogin,
					jsonObject.optString("state"), true)) {
				ReLoginDialog.getInstance(this).showDialog(
						jsonObject.optString("message"));
			} else {
				showToast(jsonObject.optString("message"));
			}
			redPacketsAdapter.setlist(mList);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 显示数据
	 */
	private void showData() {
		if (!StringUtil.isEmpty(mList)) {
			if (!StringUtil.isEmpty(amount)) {
				tv_red_purse.setText(amount);
			} else {
				double money = 0;
				for (int i = 0; i < mList.size(); i++) {
					money += mList.get(i).getMoney();
				}
				tv_red_purse.setText("￥" + money);
			}
			redPacketsAdapter.setlist(mList);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		mList.clear();
		page = 1;
		getData();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		page++;
		getData();
	}
}
