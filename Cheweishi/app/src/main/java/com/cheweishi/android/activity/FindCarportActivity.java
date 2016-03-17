package com.cheweishi.android.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.fragement.FindCarportListFragment;
import com.cheweishi.android.fragement.FindCarportMapFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/****
 * 
 * 找车位
 * 
 * @author 刘伟
 * 
 */

public class FindCarportActivity extends BaseActivity {

	private FragmentManager manager;
	private FragmentTransaction transaction;
	@ViewInject(R.id.listToMap)
	private RadioGroup mRadioGroup;
	FindCarportMapFragment findCarportMapFragment;
	FindCarportListFragment findCarportListFragment;

	// 头部返回建
	@ViewInject(R.id.left_action)
	private Button mLeftAction;

	// 头部的列表
	@ViewInject(R.id.rb_list)
	private RadioButton radioButtonList;

	// 头部的地圖
	@ViewInject(R.id.rb_map)
	private RadioButton mRadioButtonMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_carport);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		ViewUtils.inject(this);
		init();
		mLeftAction.setText(getResources().getString(R.string.back));
		
	}

	/***
	 * 初始化
	 */
	private void init() {
		mLeftAction.setOnClickListener(listener);
		// 设置地图为选中状态
		mRadioButtonMap.setChecked(true);
		// 获得fragment管理器
		manager = getSupportFragmentManager();
		// 实例化fragment
		initFragment();
		// 获得fragmenttransaction的实例
		initTransaction();
		// 判断是否创建
		isAdded(findCarportMapFragment);
		// 显示地图fragment，并提交事务
		showFragment();
		// 切换地图或列表监听
		mRadioGroup.setOnCheckedChangeListener(checkedChangeListener);
	}

	private void initTransaction() {
		transaction = manager.beginTransaction();
	}

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int id) {

			
			System.out.println(radioGroup.getCheckedRadioButtonId());
			switch (radioGroup.getCheckedRadioButtonId()) {
			// 选中地图
			case R.id.rb_map:
				changeFragment(findCarportMapFragment);
				break;
			// 选中列表
			case R.id.rb_list:
				changeFragment(findCarportListFragment);
				break;
			default:
				break;
			}
		}
	};

	private void showFragment() {
		// TODO Auto-generated method stub
		// 显示地图fragment
		transaction.show(findCarportMapFragment);
		// 提交事务
		transaction.commit();
	}

	private void initFragment() {
		// 实例化地图fragment
		findCarportMapFragment = new FindCarportMapFragment();
		// 实例化列表fragment
		findCarportListFragment = new FindCarportListFragment();
	}

	public void changeFragment(Fragment fragment) {
		initTransaction();
		isAdded(fragment);
		if (fragment == findCarportListFragment) {
			showhide(fragment, findCarportMapFragment);
		}
		if (fragment == findCarportMapFragment) {
			showhide(fragment, findCarportListFragment);
		}

	}

	/***
	 * 显示和隐藏fragement
	 * 
	 * @param fragment
	 *            显示的fragment
	 * @param fragment2
	 *            隐藏的fragment
	 */
	private void showhide(Fragment fragment, Fragment fragment2) {
		transaction.show(fragment);
		transaction.hide(fragment2);
		transaction.commitAllowingStateLoss();
	}

	/***
	 * 判断fragment 是否被创建
	 * 
	 * @param fragment
	 *            显示的fragment
	 */
	private void isAdded(Fragment fragment) {
		// TODO Auto-generated method stub
		if (!fragment.isAdded()) {
			transaction.add(R.id.findcar_framelayout, fragment);
		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			// 返回监听
			case R.id.left_action:
				FindCarportActivity.this.finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		manager = null;
		transaction = null;
		mRadioGroup.removeAllViews();
		mRadioGroup = null;
		findCarportMapFragment = null;
		findCarportListFragment = null;
		getSharedPreferences("isindex",
				Context.MODE_PRIVATE).edit().clear().commit();
		getSharedPreferences("isDraw",
				Context.MODE_PRIVATE).edit().clear().commit();
		getSharedPreferences("isdraw",
				Context.MODE_PRIVATE).edit().clear().commit();
		getSharedPreferences("resultSharedPreferences",
				Context.MODE_PRIVATE).edit().clear().commit();
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
