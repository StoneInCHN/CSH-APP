package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.MyorderAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.MyOrderBean;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MyorderActivity extends BaseActivity implements OnClickListener,
		OnRefreshListener2<ListView>, OnItemClickListener {
	@ViewInject(R.id.title)
	private TextView tvTitle;
	@ViewInject(R.id.left_action)
	private Button btnLeft;
	// @ViewInject(R.id.right_action)
	// private ImageButton right_action;
	@ViewInject(R.id.lv_myOrder)
	private PullToRefreshListView lv_myOrder;
	private ListView mListView;
	// @ViewInject(R.id.layoutNoData)
	// private LinearLayout layoutNoData;
	private ArrayList<MyOrderBean> list = new ArrayList<MyOrderBean>();
	private ArrayList<MyOrderBean> listTemp;
	private MyorderAdapter adapter;
	private int pageNumber = 1;
	private CustomDialog.Builder builder;
	private CustomDialog deleteDialog;

	// WashcarHistoryActivity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_washcar_history);
		ViewUtils.inject(this);
		init();

	}

	private void init() {
		tvTitle.setText(R.string.myorder);
		btnLeft.setText(R.string.back);
		// right_action.setImageResource(R.drawable.sousuo_icon);
		lv_myOrder.setOnRefreshListener(this);
		// 设置listview的上拉加载下拉刷新
		lv_myOrder.setMode(Mode.BOTH);
		lv_myOrder.setOnItemClickListener(this);
		// 通过pullToRefreshListView得到listview
		mListView = lv_myOrder.getRefreshableView();
		EmptyTools.setEmptyView(this, mListView);
		
		list = new ArrayList<MyOrderBean>();
		adapter = new MyorderAdapter(list, this);
		mListView.setAdapter(adapter);

		httpBiz = new HttpBiz(this);
		connectToServer();
	}

	@Override
	public void receive(int type, String data) {
		super.receive(type, data);
		lv_myOrder.onRefreshComplete();

		ProgrosDialog.closeProgrosDialog();
		System.out.println("订单====" + data);
		switch (type) {
		case 400:
			showToast(R.string.server_link_fault);
			break;
		case 10002:
			parseJSON(data);
			break;
		case 10003:
			break;
		}
	}

	@OnClick({ R.id.left_action, R.id.right_action })
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.right_action:
			showDeleteDialog();
			break;

		default:
			break;
		}

	}

	private void showDeleteDialog() {
		builder = new CustomDialog.Builder(this);
		builder.setMessage(R.string.order_delete_remind);
		builder.setTitle(R.string.remind);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						connectToDeleteServer();

					}
				});

		builder.setNegativeButton(R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// setRadioButtonLight();
					}
				});
		deleteDialog = builder.create();
		deleteDialog.show();
	}

	private void connectToDeleteServer() {
		ProgrosDialog.openDialog(this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("uid", loginMessage.getUid());
		httpBiz.httPostData(10003, API.CSH_MYORDER_LIST_DELETE_URL, rp, this);
	}

	private void connectToServer() {
		ProgrosDialog.openDialog(this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("uid", loginMessage.getUid());
		rp.addBodyParameter("mobile", loginMessage.getMobile());
		rp.addBodyParameter("pageNumber", pageNumber + "");
		rp.addBodyParameter("pageSize", "20");
		httpBiz.httPostData(10002, API.CSH_MYORDER_LIST_URL, rp, this);
	}

	private void parseJSON(String result) {
		if (StringUtil.isEmpty(result)) {
			return;
		}

		try {
			JSONObject jsonObject = new JSONObject(result);
			if (StringUtil.isEquals(jsonObject.optString("state"),
					API.returnSuccess, true)) {
				System.out.println("订单====" + result);
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<List<MyOrderBean>>() {
				}.getType();
				listTemp = gson.fromJson(jsonObject.optString("data"), type);
				if (listTemp.size() == 0 && list.size() == 0) {
					// layoutNoData.setVisibility(View.VISIBLE);
					EmptyTools.setImg(R.drawable.dingdanwu_icon);
					EmptyTools.setMessage("您还没有订单");
				} else if (!(StringUtil.isEmpty(listTemp) || listTemp.size() == 0)) {
					list.addAll(listTemp);
				} else {
					showToast(R.string.no_more);
				}
				System.out.println("订单=====" + list.size());
				adapter.setData(list);
			} else if (StringUtil.isEquals(jsonObject.optString("state"),
					API.returnRelogin, true)) {
				ReLoginDialog.getInstance(this).showDialog(
						jsonObject.optString("message"));
			} else {
				showToast(jsonObject.optString("message") + "haha");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		pageNumber = 1;
		list.clear();
		connectToServer();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (!(StringUtil.isEmpty(listTemp) && listTemp.size() == 20)) {
			pageNumber++;
			connectToServer();
		} else {
			showToast(R.string.no_more);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		System.out.println("onItem===");
		Intent intent = new Intent(this, OrderDetailsActivity.class);
		intent.putExtra("order_id", list.get(arg2 - 1).getId());
		startActivity(intent);
	}

}
