package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.InsuranceBusinessAdapter;
import com.cheweishi.android.entity.InsuranceCalculation;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_insurance_calculation)
public class InsuranceCalculationActivity extends BaseActivity implements
		OnClickListener {

	@ViewInject(R.id.lv_business_insurance)
	private ListView lv_business_insurance;
	@ViewInject(R.id.tv_bussiness_insurance_name)
	private TextView tv_bussiness_insurance_name;
	@ViewInject(R.id.tv_bussiness_insurance_time)
	private TextView tv_bussiness_insurance_time;
	private CheckBox cb_bussiness_insurance;
	private TextView tv_bussiness_insurance_price;
	@ViewInject(R.id.tv_bussiness_insurance_total_price)
	private TextView tv_bussiness_insurance_total_price;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.btn_insurance_buy)
	private Button btn_insurance_buy;
	@ViewInject(R.id.ll_business_insurance)
	private LinearLayout ll_business_insurance;
	int[] location = new int[2];
	int[] location2 = new int[2];
	private List<InsuranceCalculation> listInsuranceCalculation;
	String[] array = { "aaaaaaaaaaa", "bbbbbbbbb", "ccccccccc", "ddddd",
			"eeeee", "fffffffff", "bbbbbbbbb", "ccccccccc", "ddddd", "eeeee",
			"fffffffff", "bbbbbbbbb", "ccccccccc", "ddddd", "eeeee",
			"fffffffff" };
	private InsuranceBusinessAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		listInsuranceCalculation = new ArrayList<InsuranceCalculation>();
		for (int i = 0; i < 10; i++) {
			InsuranceCalculation InsuranceCalculation = new InsuranceCalculation();
			String[] array = { "10万", "20万", "30万", "40万", "50万" };
			InsuranceCalculation.setInsuranceRange(array);
			InsuranceCalculation.setInsuranceCurrentPrice(array[0]);
			InsuranceCalculation.setInsuranceName("商业险");
			InsuranceCalculation.setInsuranceType("商业险");
			InsuranceCalculation.setInsurancePayPrice("1024");
			listInsuranceCalculation.add(InsuranceCalculation);
		}
		adapter = new InsuranceBusinessAdapter(this, listInsuranceCalculation);
		if (isLogined() && hasCar()) {
			title.setText(loginMessage.getCar().getPlate());
		}
		left_action.setText(R.string.back);
		View headview = LayoutInflater.from(this).inflate(
				R.layout.head_insurance, null);
		View headview1 = LayoutInflater.from(this).inflate(
				R.layout.item_suspend_view, null);
		// ViewUtils.inject(this, headview);
		// ViewUtils.inject(this, headview1);
		lv_business_insurance.addHeaderView(headview);
		lv_business_insurance.addHeaderView(headview1);
		tv_bussiness_insurance_price = (TextView) headview1
				.findViewById(R.id.tv_bussiness_insurance_price);
		cb_bussiness_insurance = (CheckBox) headview1
				.findViewById(R.id.cb_bussiness_insurance);
		lv_business_insurance.setAdapter(adapter);
		lv_business_insurance.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem >= 1) {
					ll_business_insurance.setVisibility(View.VISIBLE);
				} else {

					ll_business_insurance.setVisibility(View.GONE);
				}
			}
		});
		dealCallBackFromAdapter(0, null);
	}

	@OnClick({ R.id.left_action, R.id.btn_insurance_buy })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.btn_insurance_buy:
			break;
		}
	}

	public void dealCallBackFromAdapter(int position, Object obj) { // tv_bussiness_insurance_price.setText(
																	// "1000");
		List<InsuranceCalculation> temp = (List<InsuranceCalculation>) obj;
		if (StringUtil.isEmpty(temp)) {
			tv_bussiness_insurance_total_price.setText("1060");
			tv_bussiness_insurance_price.setText("0");
			((TextView) ll_business_insurance
					.findViewById(R.id.tv_bussiness_insurance_price))
					.setText("0");
		} else {
			int price = 1060;
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).isChooseFlag() == true) {
					price += Integer.parseInt(temp.get(i)
							.getInsurancePayPrice());
				}
			}
			if (price > 1060) {
				cb_bussiness_insurance.setChecked(true);
				((CheckBox) ll_business_insurance
						.findViewById(R.id.cb_bussiness_insurance))
						.setChecked(true);
				tv_bussiness_insurance_price.setText((price - 1060) + "");
				((TextView) ll_business_insurance
						.findViewById(R.id.tv_bussiness_insurance_price))
						.setText((price - 1060) + "");
			} else {

				((TextView) ll_business_insurance
						.findViewById(R.id.tv_bussiness_insurance_price))
						.setText("0");
				tv_bussiness_insurance_price.setText("0");
				cb_bussiness_insurance.setChecked(false);
				((CheckBox) ll_business_insurance
						.findViewById(R.id.cb_bussiness_insurance))
						.setChecked(false);
			}
			tv_bussiness_insurance_total_price.setText(price + "");
		}
	}
}
