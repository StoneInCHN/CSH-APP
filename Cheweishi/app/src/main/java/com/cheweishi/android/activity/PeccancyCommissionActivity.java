package com.cheweishi.android.activity;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.cheweishi.R.array;
import com.cheweishi.android.cheweishi.R.id;
import com.cheweishi.android.cheweishi.R.layout;
import com.cheweishi.android.cheweishi.R.string;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ImgDialog;
import com.cheweishi.android.tools.AllCapTransformationMethod;
import com.cheweishi.android.tools.RegularExpressionTools;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

@ContentView(R.layout.activity_peccancy_commission)
public class PeccancyCommissionActivity extends BaseActivity implements
		OnClickListener {
	private ImgDialog.Builder builder;
	private ImgDialog imgDialog;
	@ViewInject(R.id.tv_peccancy_brand)
	private TextView tv_peccancy_brand;
	@ViewInject(R.id.et_peccancy_car_plate)
	private EditText et_peccancy_car_plate;
	@ViewInject(R.id.tv_peccancy_city)
	private TextView tv_peccancy_city;
	@ViewInject(R.id.et_peccancy_vin)
	private EditText et_peccancy_vin;
	@ViewInject(R.id.ll_peccancy_brand)
	private LinearLayout ll_peccancy_brand;
	@ViewInject(R.id.ll_peccancy_car_plate)
	private LinearLayout ll_peccancy_car_plate;
	@ViewInject(R.id.ll_peccancy_city)
	private LinearLayout ll_peccancy_city;
	@ViewInject(R.id.bt_saveAndSearch)
	private Button bt_saveAndSearch;
	@ViewInject(R.id.bt_peccancy_province)
	private TextView bt_peccancy_province;
	@ViewInject(R.id.bt_peccancy_char)
	private TextView bt_peccancy_char;
	@ViewInject(R.id.img_vin_desc)
	private ImageView img_vin_desc;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	private String longPro[];
	private String shortPro[];
	private String brandId = null;
	private String carModelUrl = null;
	private String brandName;
	private String modelName;
	private String modelId = null;
	private String styleId = null;
	private String cityId;// 城市
	private String cityName;// 城市

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		left_action.setText(R.string.back);
		title.setText(R.string.title_activity_peccancy_commission);
		longPro = this.getResources().getStringArray(R.array.province_item);
		shortPro = this.getResources().getStringArray(
				R.array.province_short_item);
		String pro = MyMapUtils.getProvince(PeccancyCommissionActivity.this);
		boolean falg = false;
		for (int i = 0; i < longPro.length; i++) {
			if (pro.contains(longPro[i])) {
				falg = true;
				bt_peccancy_province.setText(shortPro[i]);
				break;
			}
		}
		if (falg == true) {
			bt_peccancy_province.setText("渝");
		} else {
			bt_peccancy_province.setText(R.string.choose);
		}
		bt_peccancy_char.setText("A");
		et_peccancy_car_plate.setTransformationMethod(new AllCapTransformationMethod());
		et_peccancy_car_plate.setSelection(et_peccancy_car_plate.getText().toString().length());
	}

	@OnClick({ R.id.ll_peccancy_brand, R.id.ll_peccancy_city,
			R.id.bt_saveAndSearch, R.id.bt_peccancy_province,
			R.id.bt_peccancy_char, R.id.left_action, R.id.img_vin_desc })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.ll_peccancy_brand:
			goToSelectBrandSerials();
			break;
		case R.id.ll_peccancy_city:
			goToSelectCity();
			break;
		case R.id.bt_saveAndSearch:
			goToSearch();
			break;
		case R.id.bt_peccancy_province:
			goToSelectShortProvince();
			break;
		case R.id.bt_peccancy_char:
			goToSelectCharProvince();
			break;
		case R.id.img_vin_desc:
			showPhoneDialog();
			break;
		case R.id.left_action:
			finish();
			break;
		}
	}

	private void goToSearch() {
		if (check() == true) {
			Intent intent = new Intent(PeccancyCommissionActivity.this,
					PessanySearchResultActivity.class);
			startActivityForResult(intent, 20015);
		}
	}

	private boolean check() {
		boolean flag = false;
		if (StringUtil.isEmpty(brandName) || StringUtil.isEmpty(modelName)) {
			showToast(R.string.car_model_choose_not_yet);
		} else if (StringUtil.isEmpty(et_peccancy_car_plate.getText()
				.toString())) {
			showToast(R.string.car_plate_choose_not_yet);
		} else if (!RegularExpressionTools.isCarPlate((bt_peccancy_province
				.getText().toString()
				+ bt_peccancy_char.getText().toString()
				+ et_peccancy_car_plate.getText().toString()).toUpperCase())) {
			showToast("请输入正确的车牌号");
		} else if (StringUtil.isEmpty(cityName)) {
			showToast("请选择查询城市");
		} else if (StringUtil.isEmpty(et_peccancy_vin.getText().toString())
				|| et_peccancy_vin.getText().toString().length() != 4) {
			showToast("请输入车架号码后4位");
		} else {
			flag = true;
		}
		return flag;
	}

	private void goToSelectCity() {

		Intent intent = new Intent(PeccancyCommissionActivity.this,
				ProvinceCityCountryActivity.class);
		startActivityForResult(intent, 20015);

	}

	private void goToSelectBrandSerials() {
		Intent intent = new Intent(PeccancyCommissionActivity.this,
				CarTypeCarBrandModelActivity.class);
		startActivityForResult(intent, 1000);
	}

	private void goToSelectShortProvince() {
		Intent intent = new Intent(PeccancyCommissionActivity.this,
				ProvinceAndCharGridActivity.class);
		intent.putExtra("flag", true);
		startActivityForResult(intent, 10001);
	}

	private void goToSelectCharProvince() {
		Intent intent = new Intent(PeccancyCommissionActivity.this,
				ProvinceAndCharGridActivity.class);
		intent.putExtra("flag", false);
		startActivityForResult(intent, 10002);
	}

	private void goToVinDesc() {
		Intent intent = new Intent(PeccancyCommissionActivity.this,
				AddCarExtraActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1000:
			if (!StringUtil.isEmpty(data)) {

				brandId = data.getStringExtra("mResultFirstId");
				modelId = data.getStringExtra("mResultLastId");
				styleId = data.getStringExtra("styleId");
				carModelUrl = data.getStringExtra("carLogoUrl");
				brandName = data.getStringExtra("mResultFirstName");
				modelName = data.getStringExtra("mResultLastName");
				tv_peccancy_brand.setText(brandName + "-" + modelName);

			}
			break;
		case 10001:
			System.out.println("item========haha");
			if (!StringUtil.isEmpty(data)
					&& !StringUtil.isEmpty(data.getStringExtra("item"))) {
				String itemData = data.getStringExtra("item");
				System.out.println("item========" + itemData);
				bt_peccancy_province.setText(itemData);
			}
			break;
		case 10002:
			if (data != null
					&& !StringUtil.isEmpty(data.getStringExtra("item"))) {
				String itemData = data.getStringExtra("item");
				System.out.println("item========" + itemData);
				bt_peccancy_char.setText(itemData);
			}
			break;
		case 20015:// 城市
			if (data != null) {
				if (StringUtil.isEmpty(data.getStringExtra("cityName"))
						&& StringUtil.isEmpty(data.getStringExtra("cityId"))) {
					tv_peccancy_city.setText("");
					cityId = "";
				} else {
					cityName = data.getStringExtra("cityName");
					tv_peccancy_city.setText(data.getStringExtra("cityName"));
					cityId = data.getStringExtra("cityId");
				}
			}
			break;
		}
	}

	/**
	 * 联系客服对话框
	 */
	private void showPhoneDialog() {
		builder = new ImgDialog.Builder(this);
		builder.setMessage(R.string.phone_msg);
		builder.setTitle(R.string.contact_customer_service);
		builder.setPositiveButton(R.string.customerServiceCall,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});

		imgDialog = builder.create();
		imgDialog.show();
	}

}
