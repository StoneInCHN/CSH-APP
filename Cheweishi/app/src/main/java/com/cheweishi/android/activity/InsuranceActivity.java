package com.cheweishi.android.activity;

import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.tools.ReturnBackDialogRemindTools;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.net.Uri;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InsuranceActivity extends BaseActivity implements OnClickListener {

    @ViewInject(R.id.tv_car_plate)
    private TextView tv_car_plate;
    @ViewInject(R.id.tv_insurance_city)
    private TextView tv_insurance_city;
    @ViewInject(R.id.tv_brandSeries)
    private TextView tv_brandSeries;
    @ViewInject(R.id.tv_insurance_car)
    private TextView tv_insurance_car;
    @ViewInject(R.id.tv_insurance_carModel)
    private TextView tv_insurance_carModel;
    @ViewInject(R.id.tv_car_plate_upload)
    private TextView tv_car_plate_upload;
    @ViewInject(R.id.btn_insurance_calculate)
    private Button btn_insurance_calculate;
    @ViewInject(R.id.btn_car_plate_upload)
    private TextView btn_car_plate_upload;
    @ViewInject(R.id.left_action)
    private Button left_action;
    @ViewInject(R.id.title)
    private TextView title;
    @ViewInject(R.id.right_action)
    private TextView right_action;
    @ViewInject(R.id.tv_changeCar)
    private TextView tv_changeCar;
    private MyBroadcastReceiver broad;

    @ViewInject(R.id.bt_province)
    private TextView bt_province; // 省份
    @ViewInject(R.id.bt_char)
    private TextView bt_char; // 城市标示
    @ViewInject(R.id.tv_guohu_type)
    private TextView tv_guohu_type; // 过户
    @ViewInject(R.id.tv_daikuan_type)
    private TextView tv_daikuan_type;//贷款
    @ViewInject(R.id.tv_driver_license_type)
    private TextView tv_driver_license_type;//行驶证
    @ViewInject(R.id.tv_id_type)
    private TextView tv_id_type;// 身份证
    @ViewInject(R.id.tv_contact_sex)
    private TextView tv_contact_sex;//性别
    @ViewInject(R.id.ed_contact_name)
    private TextView ed_contact_name;//联系人姓名
    @ViewInject(R.id.ed_contact_phone_number)
    private TextView ed_contact_phone_number;// 联系人投保手机号码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance);
        ViewUtils.inject(this);
        initViews();
        setListener();

        //ReturnBackDialogRemindTools.getInstance(this).show();
    }

    private void setListener() {
        bt_province.setOnClickListener(this);
        bt_char.setOnClickListener(this);
    }

    private void initViews() {
        title.setText(R.string.title_activity_insurance);
        left_action.setText(R.string.back);
        if (isLogined() && hasCar()) {
            tv_car_plate.setText(loginResponse.getMsg().getDefaultVehiclePlate());
            tv_brandSeries.setText(loginResponse.getMsg().getDefaultVehicle());
        }
    }

    @OnClick({R.id.btn_car_plate_upload, R.id.left_action, R.id.tv_changeCar,
            R.id.btn_insurance_calculate, R.id.tv_guohu_type})
    @Override
    public void onClick(View arg0) {
        Intent intent;
        switch (arg0.getId()) {
            case R.id.btn_car_plate_upload:
                intent = new Intent(this,
                        InsuranceInformationUploadActivity.class);
                startActivity(intent);
                break;
            case R.id.left_action:
                finish();
                break;
            case R.id.tv_changeCar:
                intent = new Intent(this, CarManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_insurance_calculate:
                // TODO 发包购买.
//			intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
//					+ getResources().getString(R.string.customerServicePhone)));
//			startActivity(intent);
                // intent = new Intent(this, InsuranceCalculationActivity.class);
                // startActivity(intent);
                break;


            case R.id.bt_province: // 车牌省份
                intent = new Intent(InsuranceActivity.this,
                        ProvinceAndCharGridActivity.class);
                intent.putExtra("flag", true);
                startActivityForResult(intent, 10001);
                break;

            case R.id.bt_char: // 车牌城市
                intent = new Intent(InsuranceActivity.this,
                        ProvinceAndCharGridActivity.class);
                intent.putExtra("flag", false);
                startActivityForResult(intent, 10002);
                break;

            case R.id.tv_guohu_type:// 过户
                ReturnBackDialogRemindTools.getInstance(this).show();
                break;
        }
    }

    /**
     * 对车型、车牌的回调
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 10001:
                if (!StringUtil.isEmpty(data)
                        && !StringUtil.isEmpty(data.getStringExtra("item"))) {
                    String itemData = data.getStringExtra("item");
                    bt_province.setText(itemData);
                }
                break;
            case 10002:
                if (data != null
                        && !StringUtil.isEmpty(data.getStringExtra("item"))) {
                    String itemData = data.getStringExtra("item");
                    bt_char.setText(itemData);
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // 注册刷新广播
        if (broad == null) {
            broad = new MyBroadcastReceiver();
        }
        IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
        registerReceiver(broad, intentFilter);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            Constant.EDIT_FLAG = false;
            System.out.println("SUCCESS====main_" + Constant.CURRENT_REFRESH);
            if (!StringUtil.isEquals(intent.getAction(), Constant.REFRESH_FLAG,
                    true)) {
                System.out.println("SUCCESS====" + "更新false");
                return;
            }
            if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
                    Constant.CAR_MANAGER_REFRESH, true)) {
                System.out.println("SUCCESS====" + "保险车辆切换更新" + "_"
                        + loginMessage.getCar().getPlate());
                initViews();
            }
        }
    }
}
