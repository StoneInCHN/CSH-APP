package com.cheweishi.android.activity;

import java.util.List;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.PessanyAdapter;
import com.cheweishi.android.entity.PessanySearch;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 违章查询
 * 
 * @author Xiaojin
 * 
 */
@ContentView(R.layout.activity_pessany_search)
public class PessanySearchActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.lv_pessanySearch)
	private ListView lv_pessanySearch;
	private PessanyAdapter adapter;
	private List<PessanySearch> listPessanySearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		initViews();
	}

	/**
	 * 初始化视图
	 */
	private void initViews() {
		title.setText(R.string.title_activity_pessany_search);
		left_action.setText(R.string.back);
		adapter = new PessanyAdapter(this, listPessanySearch);
		lv_pessanySearch.setAdapter(adapter);
	}

	@OnClick({ R.id.left_action })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, PessanySearchResultActivity.class);
		if (!StringUtil.isEmpty(listPessanySearch)
				&& listPessanySearch.size() > arg2) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("PessanySearch", listPessanySearch.get(arg2));
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

}
