package com.cheweishi.android.activity;

import java.util.List;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.PessanySearchResultAdpater;
import com.cheweishi.android.entity.PessanySearchResult;
import com.cheweishi.android.tools.EmptyTools;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 违章查询结果
 * 
 * @author Xiaojin
 * 
 */
public class PessanySearchResultActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.lv_pessany_search_result)
	private ListView lv_pessany_search_result;
	@ViewInject(R.id.btn_peccany_pay)
	private Button btn_peccany_pay;
	private List<PessanySearchResult> listPessanySearchResult;
	private PessanySearchResultAdpater adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pessany_search_result);
		ViewUtils.inject(this);
		initViews();
	}

	/**
	 * 初始化视图
	 */
	private void initViews() {
		title.setText(R.string.title_activity_pessany_search);
		left_action.setText(R.string.back);
		adapter = new PessanySearchResultAdpater(this, listPessanySearchResult);
		lv_pessany_search_result.setAdapter(adapter);
		EmptyTools.setEmptyView(this, lv_pessany_search_result);
		EmptyTools.setImg(R.drawable.weizhang_jilu);
		EmptyTools.setMessage("恭喜您，没有违章记录");
	}

	@OnClick({ R.id.left_action, R.id.btn_peccany_pay,
			R.id.img_peccany_result_refresh })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.btn_peccany_pay:
			goToPayPeccany();
			break;
		case R.id.img_peccany_result_refresh:
			break;
		}
	}

	/**
	 * 违章缴费
	 */
	private void goToPayPeccany() {
		Intent intent = new Intent(this, PayPessanyActivity.class);
		startActivity(intent);
	}
}
