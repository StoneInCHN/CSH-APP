package com.cheweishi.android.activity;

import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.R;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.interfaces.InsuranceListener;
import com.cheweishi.android.tools.PhotoTools;
import com.cheweishi.android.tools.ReturnBackDialogRemindTools;
import com.cheweishi.android.utils.BitmapUtils;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomCheckDialog;
import com.cheweishi.android.widget.CustomDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InsuranceActivity extends BaseActivity implements OnClickListener, InsuranceListener {

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
    @ViewInject(R.id.iv_driver_license_type)
    private ImageView iv_driver_license_type;// 行驶证图标
    @ViewInject(R.id.iv_id_type)
    private ImageView iv_id_type;//身份证图标
    @ViewInject(R.id.tv_insurance_type)
    private TextView tv_insurance_type;// 保险
    private Dialog dialog1;


    public static final int TAKE_A_PICTURE = 10;
    private String mAlbumPicturePath;
    private boolean IMG_FLAG;
    private String mCarDirverImg;
    private String mPeopleIdImg;

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
        tv_daikuan_type.setOnClickListener(this);
        tv_contact_sex.setOnClickListener(this);
        tv_driver_license_type.setOnClickListener(this);
        iv_driver_license_type.setOnClickListener(this);
        iv_id_type.setOnClickListener(this);
        tv_insurance_type.setOnClickListener(this);
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
            R.id.btn_insurance_calculate, R.id.tv_guohu_type, R.id.xiangji,
            R.id.xiangce, R.id.quxiao, R.id.tv_id_type})
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
                CustomCheckDialog.Builder guohu = new CustomCheckDialog.Builder(baseContext);
                showDialog(R.id.tv_guohu_type, guohu.YES_NO, guohu);
                break;

            case R.id.tv_daikuan_type://贷款
                CustomCheckDialog.Builder daikuan = new CustomCheckDialog.Builder(baseContext);
                showDialog(R.id.tv_daikuan_type, daikuan.YES_NO, daikuan);
                break;

            case R.id.tv_contact_sex:// 性别
                CustomCheckDialog.Builder sex = new CustomCheckDialog.Builder(baseContext);
                showDialog(R.id.tv_contact_sex, sex.SEX, sex);
                break;

            case R.id.iv_driver_license_type:
            case R.id.tv_driver_license_type: // 行驶证
                IMG_FLAG = true;
                showImgDialog();
                break;

            case R.id.iv_id_type:
            case R.id.tv_id_type: // 身份证
                IMG_FLAG = false;
                showImgDialog();
                break;

            case R.id.tv_insurance_type: // 保险
                CustomCheckDialog.Builder insurance = new CustomCheckDialog.Builder(baseContext);
                showDialog(R.id.tv_insurance_type, insurance.LIST, insurance);
                break;

            // 调用手机相机
            case R.id.xiangji:
                PhotoTools.init();
                dialog1.dismiss();
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                        PhotoTools.IMGPATH, PhotoTools.IMAGE_FILE_NAME)));
                startActivityForResult(intent, TAKE_A_PICTURE);
                break;
            // 调用手机相册
            case R.id.xiangce:
                PhotoTools.init();
                dialog1.dismiss();
                if (PhotoTools.mIsKitKat) {
                    PhotoTools.selectImageUriAfterKikat(this);
                } else {
                    PhotoTools.cropImageUri(this);
                }
                break;
            case R.id.quxiao:
                dialog1.dismiss();
                break;
        }
    }

    private void showDialog(int flag, int value, CustomCheckDialog.Builder builder) {
        if (null == builder)
            return;
        builder.setMyOnClickListener(this);
        builder.setContentFlag(flag, value);
        CustomCheckDialog customDialog = builder.create();
        customDialog.show();
    }

    @Override
    public void getResult(int tag, String result) {
        switch (tag) {
            case R.id.tv_guohu_type: // 过户
                tv_guohu_type.setText(result);
                break;
            case R.id.tv_daikuan_type:// 贷款
                tv_daikuan_type.setText(result);
                break;
            case R.id.tv_contact_sex: //性别
                tv_contact_sex.setText(result);
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


        Bitmap bitmap;

        switch (requestCode) {
            case PhotoTools.SELECT_A_PICTURE:
                if (resultCode == RESULT_OK && null != data) {
                    mAlbumPicturePath = PhotoTools.getPath(getApplicationContext(),
                            data.getData());
                    bitmap = PhotoTools.decodeUriAsBitmap(
                            Uri.fromFile(new File(mAlbumPicturePath)), this);
                    judgeBitmap(bitmap);
                } else if (resultCode == RESULT_CANCELED) {
                    showToast("取消头像设置");
                }
                break;
            case PhotoTools.SELECET_A_PICTURE_AFTER_KIKAT:
                if (resultCode == RESULT_OK && null != data) {
                    mAlbumPicturePath = PhotoTools.getPath(getApplicationContext(),
                            data.getData());
                    bitmap = PhotoTools.decodeUriAsBitmap(
                            Uri.fromFile(new File(mAlbumPicturePath)), this);
                    judgeBitmap(bitmap);
                } else if (resultCode == RESULT_CANCELED) {
                    showToast("取消头像设置");
                }
                break;
            case TAKE_A_PICTURE:
                Log.i("Tanck", "TAKE_A_PICTURE-resultCode:" + resultCode);
                if (resultCode == RESULT_OK) {
                    bitmap = PhotoTools.decodeUriAsBitmap(Uri.fromFile(new File(
                            PhotoTools.IMGPATH, PhotoTools.IMAGE_FILE_NAME)), this);
                    judgeBitmap(bitmap);
                }
            default:
                break;
        }

    }


    private void judgeBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            showToast("取消头像设置");
        } else {
            saveBitmap(bitmap);
        }
    }

    @SuppressWarnings("static-access")
    private void saveBitmap(Bitmap bitmap) {
        new DateFormat();
        bitmap = new BitmapUtils().zoomBitmap(bitmap);
        FileOutputStream fileOutputStream = null;
        FileOutputStream fOutputStream = null;
        File file = new File(PhotoTools.IMGPATH);

        file.mkdirs();// 创建文件夹
        String fileName = PhotoTools.IMGPATH + PhotoTools.IMAGE_FILE_NAME;
        File file1 = new File(PhotoTools.ACCOUNT_DIR);// 如果缓存目录存在的话，删除缓存目录
        file1.setReadable(false);
        try {
            fileOutputStream = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);// 把数据写入文件
            Bitmap bitmap1 = PhotoTools.getimage(fileName, this);
            fOutputStream = new FileOutputStream(fileName);
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            setImageView(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!StringUtil.isEmpty(fileOutputStream)) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } else {
                    showToast("头像设置失败，请重试...");
                }
                if (!StringUtil.isEmpty(fOutputStream)) {
                    fOutputStream.flush();
                    fOutputStream.close();
                } else {
                    showToast("头像设置失败，请重试...");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void setImageView(String fileName) {
        LogHelper.d("图片地址:" + fileName);
        if (IMG_FLAG) { // 行驶证
            mCarDirverImg = fileName;
            tv_driver_license_type.setVisibility(View.GONE);
            iv_driver_license_type.setVisibility(View.VISIBLE);
            XUtilsImageLoader.getxUtilsLocalImageLoader(this, R.drawable.zhaochewei_img,
                    iv_driver_license_type, fileName);
        } else { //身份证
            mPeopleIdImg = fileName;
            tv_id_type.setVisibility(View.GONE);
            iv_id_type.setVisibility(View.VISIBLE);
            XUtilsImageLoader.getxUtilsLocalImageLoader(this, R.drawable.zhaochewei_img,
                    iv_id_type, fileName);
        }
        // TODO 需要更新UI.

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
            if (!StringUtil.isEquals(intent.getAction(), Constant.REFRESH_FLAG,
                    true)) {
                return;
            }
            if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
                    Constant.CAR_MANAGER_REFRESH, true)) {
                initViews();
            }
        }
    }


    /**
     * @return void 返回类型
     * @throws
     * @Title: showDialog
     * @Description: TODO(dialog弹出和显示的样式)
     */
    @SuppressWarnings("deprecation")
    private void showImgDialog() {
        View view = getLayoutInflater().inflate(R.layout.person_seting_dialog,
                null);
        ViewUtils.inject(this, view);
        dialog1 = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog1.setContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog1.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        dialog1.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog1.setCanceledOnTouchOutside(true);
        dialog1.show();
    }
}
