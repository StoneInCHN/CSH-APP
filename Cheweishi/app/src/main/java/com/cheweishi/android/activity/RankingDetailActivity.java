package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.cheweishi.R.layout;
import com.cheweishi.android.cheweishi.R.menu;
import com.cheweishi.android.adapter.ParentAdapter;
import com.cheweishi.android.adapter.ScoreAdapter;
import com.cheweishi.android.adapter.ParentAdapter.OnChildTreeViewClickListener;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.ChildEntity;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.entity.ParentEntity;
import com.cheweishi.android.entity.ScoreDetail;
import com.cheweishi.android.entity.UserInfo;
import com.cheweishi.android.http.MyHttpUtils;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.widget.CustomDialog;
import com.cheweishi.android.widget.XListView;
import com.cheweishi.android.widget.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class RankingDetailActivity extends BaseActivity {
	@ViewInject(R.id.eList)
	private XListView eList;
	private List<ScoreDetail> listScoreDetail = new ArrayList<ScoreDetail>();
	private CustomDialog.Builder builder;
	private LoginMessage loginMessage;
	private int page = 1;
	private ScoreAdapter adapter;
	private List<ScoreDetail> listScoreDetailTemp = new ArrayList<ScoreDetail>();
	@ViewInject(R.id.ranking_layout)
	private RelativeLayout ranking_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ranking_detail);
		ViewUtils.inject(this);
		ranking_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RankingDetailActivity.this.finish();
			}

		});
		// loginMessage = LoginMessageUtils
		// .getLoginMessage(RankingDetailActivity.this);
		// eList.setPullLoadEnable(true);
		// eList.setPullRefreshEnable(true);
		// adapter = new ScoreAdapter(this, listScoreDetail);
		// eList.setAdapter(adapter);
		// connectToServer();
	}

	private void connectToServer() {
		if (loginMessage == null || loginMessage.getUid() == null) {

		} else {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("cid", loginMessage.getCar().getCid());
			params.addBodyParameter("key", loginMessage.getKey());
			params.addBodyParameter("page", page + "");
			MyHttpUtils myHttpUtils = new MyHttpUtils(
					RankingDetailActivity.this, params, API.CHANGE_PERSON_URL,
					handler);
			myHttpUtils.PostHttpUtils();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 400:
				Toast.makeText(RankingDetailActivity.this,
						R.string.server_link_fault, Toast.LENGTH_LONG).show();
				break;
			default:
				parseJSON((String) msg.obj);
				break;
			}
		}
	};

	private void parseJSON(String result) {
		System.out.println("用户信息====" + result);
		if (result == null || result.equals("")) {
			Toast.makeText(RankingDetailActivity.this, R.string.no_result,
					Toast.LENGTH_LONG).show();
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.optString("operationState").equals("SUCCESS")) {
					JSONArray jsonArray = jsonObject.optJSONArray("data");
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							Gson gson = new Gson();
							java.lang.reflect.Type type = new TypeToken<ScoreDetail>() {
							}.getType();
							ScoreDetail userInfo = gson.fromJson(
									jsonArray.optString(i), type);
							listScoreDetailTemp.add(userInfo);
						}
						listScoreDetail.addAll(listScoreDetailTemp);
						adapter.setData(listScoreDetail);
						if (listScoreDetailTemp.size() < 20) {

						} else {
							page++;
							connectToServer();
						}

					}

				} else if (jsonObject.optString("operationState")
						.equals("FAIL")) {
					Toast.makeText(RankingDetailActivity.this,
							jsonObject.optJSONObject("data").optString("msg"),
							Toast.LENGTH_LONG).show();
				} else if (jsonObject.optString("operationState").equals(
						"RELOGIN")) {
					showDialog();
				} else if (jsonObject.optString("operationState").equals(
						"DEFAULT")) {
					Toast.makeText(RankingDetailActivity.this,
							jsonObject.optJSONObject("data").optString("msg"),
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void showDialog() {
		builder = new CustomDialog.Builder(RankingDetailActivity.this);
		builder.setMessage(R.string.message_reLogin);
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent1 = new Intent(RankingDetailActivity.this,
						LoginActivity.class);
				RankingDetailActivity.this
						.startActivityForResult(intent1, 1001);// 设置你的操作事项

			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	IXListViewListener ixlistener = new IXListViewListener() {

		@Override
		public void onRefresh() {
			eList.setPullLoadEnable(true);
			eList.setPullRefreshEnable(true);
			eList.stopLoadMore();
			eList.stopRefresh();
			listScoreDetail.clear();
			listScoreDetailTemp.clear();
			page = 1;
			connectToServer();

		}

		@Override
		public void onLoadMore() {
			eList.stopLoadMore();
			if (listScoreDetailTemp.size() < 20) {
				Toast.makeText(RankingDetailActivity.this, "数据已经加载完！",
						Toast.LENGTH_LONG).show();
			} else {
				page++;
				connectToServer();
			}
		}
	};
}
