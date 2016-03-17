package com.cheweishi.android.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.UserInfo;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.PhotoTools;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.BitmapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.XCRoundImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class UserDetailActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.ll_userTop)
	private RelativeLayout ll_userTop;
	@ViewInject(R.id.tv_user_detail_nickName)
	private TextView tv_user_detail_nickName;
	@ViewInject(R.id.tv_user_detail_sex)
	private TextView tv_user_detail_sex;
	@ViewInject(R.id.tv_user_detail_age)
	private TextView tv_user_detail_age;
	@ViewInject(R.id.ll_img)
	private RelativeLayout ll_img;
	@ViewInject(R.id.xcRoundImg)
	private XCRoundImageView xcRoundImg;
	@ViewInject(R.id.tv_userDetail_sign)
	private TextView tv_userDetail_sign;
	@ViewInject(R.id.tv_userDetail_city)
	private TextView tv_userDetail_city;
	@ViewInject(R.id.img_userDetail_top_img)
	private ImageView img_userDetail_top_img;
	@ViewInject(R.id.ll_userDetail_edit)
	private LinearLayout ll_userDetail_edit;
	@ViewInject(R.id.tv_userDetail_balance)
	private TextView tv_userDetail_balance;
	private boolean hasMeasured = false;
	@ViewInject(R.id.tr_left_action)
	private Button left_action;
	private UserInfo userInfo;
	private Dialog dialog1;
	@ViewInject(R.id.xiangji)
	private Button camera;
	@ViewInject(R.id.xiangce)
	private Button photo;
	@ViewInject(R.id.quxiao)
	private Button cancel;
	@ViewInject(R.id.btn_layout)
	private LinearLayout btn_layout;
	@ViewInject(R.id.origionalImg)
	private ImageView origionalImg;
	@ViewInject(R.id.bt_scoreRanking)
	private Button bt_scoreRanking;
	@ViewInject(R.id.bt_mileRanking)
	private Button bt_mileRanking;
	@ViewInject(R.id.tv_userDetail_calling)
	private TextView tv_userDetail_calling;
	@ViewInject(R.id.tv_userDetail_score)
	private TextView tv_userDetail_score;
	private String mAlbumPicturePath = null;
	@ViewInject(R.id.tv_userDetail_tel)
	private TextView tv_userDetail_tel;
	@ViewInject(R.id.tv_userDetail_car)
	private TextView tv_userDetail_car;
	@ViewInject(R.id.tv_userDetail_car_age)
	private TextView tv_userDetail_car_age;
	@ViewInject(R.id.tv_user_car_name)
	private TextView tv_user_car_name;
	@ViewInject(R.id.tv_userDetail_car_mile)
	private TextView tv_userDetail_car_mile;
	@ViewInject(R.id.ll_userDetail_car)
	private LinearLayout ll_userDetail_car;
	@ViewInject(R.id.ll_userDetail_city)
	private LinearLayout ll_userDetail_city;
	@ViewInject(R.id.ll_userDetail_score)
	private LinearLayout ll_userDetail_score;
	@ViewInject(R.id.img_user_detail_sex)
	private ImageView img_user_detail_sex;
	@ViewInject(R.id.ll_center)
	private LinearLayout ll_center;
	@ViewInject(R.id.ll_userDetail_tel)
	private LinearLayout ll_userDetail_tel;
	@ViewInject(R.id.tv_user_car_img)
	private ImageView tv_user_car_img;
	private final int RELOGIN_TYPE = 10001;
	private final int GET_USER_DETAIL_TYPE = 10002;
	private final int UPLOAD_IMG_TYPE = 10003;
	MyBroadcastReceiver broad;
	@ViewInject(R.id.tr_right_action)
	private TextView tr_right_action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user_detail);
		ViewUtils.inject(this);
		initViews();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (broad == null) {
			broad = new MyBroadcastReceiver();
		}
		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		registerReceiver(broad, intentFilter);
	}

	private void initViews() {
		tr_right_action.setVisibility(View.GONE);
		httpBiz = new HttpBiz(this);
		PhotoTools.deleteFile();
		// 设置头像所处的位置
		ViewTreeObserver vto = ll_userTop.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					int height = ll_userTop.getMeasuredHeight();
					ViewGroup.LayoutParams imgLp = xcRoundImg.getLayoutParams();
					xcRoundImg.setY(height - imgLp.height * 2 / 3);
					ll_center.setY(height - imgLp.height * 2 / 3);
					hasMeasured = true;
				}
				return true;
			}
		});
		left_action.setText(R.string.back);
		XUtilsImageLoader.getxUtilsImageLoader(this,
				R.drawable.info_touxiang_moren,xcRoundImg,
				API.DOWN_IMAGE_URL + loginMessage.getPhoto());
		if (isLogined() && hasCar() && hasBrandName()) {
			tv_user_car_name.setText("认证座驾:"
					+ loginMessage.getCar().getBrandName() + "-"
					+ loginMessage.getCar().getSeriesName());
		} else {
			tv_user_car_img.setVisibility(View.GONE);
			tv_user_car_name.setText("未获取到认证座驾");
		}
		if (isLogined() && hasScore()) {
			tv_userDetail_score.setText(loginMessage.getScore().getNow());
			tv_user_detail_nickName.setText(loginMessage.getNick());
			tv_userDetail_tel.setText(loginMessage.getTel());
		}
		connectToServer();

	}

	public static final int TAKE_A_PICTURE = 10;

	@OnClick({ R.id.ll_userDetail_car, R.id.ll_userDetail_city,
			R.id.ll_userDetail_score, R.id.ll_userDetail_edit,
			R.id.tv_userDetail_balance, R.id.tr_left_action,
			R.id.tv_userDetail_city, R.id.tv_userDetail_sign, R.id.xcRoundImg,
			R.id.tv_user_detail_sex, R.id.origionalImg, R.id.origionImgBtn,
			R.id.xiangji, R.id.xiangce, R.id.quxiao })
	@Override
	public void onClick(View arg0) {
		Intent intent;
		switch (arg0.getId()) {
		case R.id.ll_userDetail_car:
			intent = new Intent(UserDetailActivity.this,
					CarManagerActivity.class);
			UserDetailActivity.this.startActivityForResult(intent, 1002);
			break;
		case R.id.tv_userDetail_city:
		case R.id.ll_userDetail_city:
			if (loginMessage != null && loginMessage.getCar() != null
					&& loginMessage.getCar().getDevice() != null
					&& (!loginMessage.getCar().getDevice().equals(""))) {
				intent = new Intent(UserDetailActivity.this,
						FootmarkActivity.class);
				UserDetailActivity.this.startActivity(intent);
			} else {
				showToast("您还未绑定设备");
			}
			break;
		case R.id.ll_userDetail_score:
			intent = new Intent(UserDetailActivity.this, IntegralActivity.class);
			UserDetailActivity.this.startActivity(intent);
			break;
		case R.id.ll_userDetail_edit:
			intent = new Intent(UserDetailActivity.this,
					UserInfoEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("userInfo", userInfo);
			intent.putExtras(bundle);
			UserDetailActivity.this.startActivityForResult(intent, 1000);
			break;
		case R.id.tv_userDetail_balance:
			break;
		case R.id.tr_left_action:
			goBack();
			break;
		case R.id.tv_userDetail_sign:
			// showNodeDialog();
			break;
		case R.id.tv_user_detail_sex:
			break;
		case R.id.xcRoundImg:
			showImgDialog();
			break;
		case R.id.origionalImg:
			dialog1.dismiss();
			break;
		case R.id.origionImgBtn:
			btn_layout.setVisibility(View.INVISIBLE);
			origionalImg.setVisibility(View.VISIBLE);
			final Animation animation1 = AnimationUtils.loadAnimation(
					UserDetailActivity.this, R.anim.score_business_query_enter);
			origionalImg.startAnimation(animation1);
			XUtilsImageLoader.getxUtilsImageLoader(UserDetailActivity.this,
					R.drawable.info_touxiang_moren,origionalImg,
					API.DOWN_IMAGE_URL + loginMessage.getPhoto());
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
				PhotoTools.selectImageUriAfterKikat(UserDetailActivity.this);
			} else {
				PhotoTools.cropImageUri(UserDetailActivity.this);
			}
			break;
		case R.id.quxiao:
			dialog1.dismiss();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				goBack();
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void goBack() {
		if (!Constant.EDIT_FLAG) {
			Constant.CURRENT_REFRESH = "";
		}
		Intent mIntent = new Intent();
		mIntent.setAction(Constant.REFRESH_FLAG);
		sendBroadcast(mIntent);

		finish();

	}

	/**
	 * 请求个人中心信息
	 */
	private void connectToServer() {

		if (isLogined()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", loginMessage.getUid());
			params.addBodyParameter("key", loginMessage.getKey());
			httpBiz = new HttpBiz(this);
			httpBiz.httPostData(GET_USER_DETAIL_TYPE, API.USER_DETAIL_URL,
					params, this);
		}

	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		super.receive(type, data);
		PhotoTools.deleteFile();
		switch (type) {
		case 400:
			showToast(R.string.server_link_fault);
			break;
		case GET_USER_DETAIL_TYPE:
			parseUserDetailJSON(data);
			break;
		case UPLOAD_IMG_TYPE:
			parseImgJSON(data);
			break;
		}
	}

	/**
	 * 上传头像
	 * 
	 * @param pathString
	 * @throws Exception
	 */
	private void setImageView(String pathString) throws Exception {
		ProgrosDialog.openDialog(this);
		File file = new File(pathString);
		RequestParams params = new RequestParams();
		params.addBodyParameter("file", file);
		params.addBodyParameter("uid", loginMessage.getUid());
		params.addBodyParameter("key", loginMessage.getKey());
		httpBiz.httPostData(RELOGIN_TYPE, API.LOGIN_MESSAGE_RELOGIN_URL,
				params, this);
		httpBiz.uploadMethod(UPLOAD_IMG_TYPE, params, API.UPLOAD_IMG_URL, this,
				this);

	}

	private void parseUserDetailJSON(String result) {
		System.out.println("用户信息haha====" + result);
		if (!StringUtil.isEmpty(result)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String resultStr = jsonObject.optString("operationState");
				if (StringUtil.isEquals(resultStr, API.returnSuccess, true)) {
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<UserInfo>() {
					}.getType();
					userInfo = gson
							.fromJson(jsonObject.optString("data"), type);
					setValues(userInfo);
				} else if (StringUtil.isEquals(resultStr, "FAIL", true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (StringUtil.isEquals(resultStr, "RELOGIN", true)) {
					ReLoginDialog.getInstance(this).showDialog(
							jsonObject.optString("message"));
				} else if (StringUtil.isEquals(resultStr, "DEFAULT", true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void setValues(UserInfo userInfo) {
		XUtilsImageLoader.getxUtilsImageLoader(this,
				R.drawable.info_touxiang_moren,xcRoundImg,
				API.DOWN_IMAGE_URL + loginMessage.getPhoto());
		if (StringUtil.isEquals(userInfo.getSex(), "0", true)) {
			tv_user_detail_sex.setText("未设置");
		} else if (StringUtil.isEquals(userInfo.getSex(), "1", true)) {
			img_user_detail_sex.setImageResource(R.drawable.bianji_boy2x);
			img_user_detail_sex.setVisibility(View.VISIBLE);
		} else if (StringUtil.isEquals(userInfo.getSex(), "2", true)) {
			img_user_detail_sex.setImageResource(R.drawable.bianji_girl2x);
			img_user_detail_sex.setVisibility(View.VISIBLE);
		}
		tv_user_detail_age.setText(userInfo.getAge() + "岁");
		tv_userDetail_sign.setText(userInfo.getNote());
		tv_user_detail_sex.setText(userInfo.getCity());
		tv_userDetail_car_age.setText(userInfo.getCarAge() + " 年");
		tv_userDetail_car.setText(userInfo.getCarNum() + " 辆");
		tv_userDetail_car_mile.setText(userInfo.getCarMile() + " 公里");
		if (!StringUtil.isEmpty(userInfo.getAddr())) {
			tv_userDetail_city.setText(userInfo.getAddr() + " 附近");
		} else {
			// if (!StringUtil.isEmpty(userInfo.getCity())) {
			// tv_userDetail_city.setText(userInfo.getCity() + " 附近");
			// }
		}

	}

	private void parseImgJSON(String result) {
		System.out.println("修改信息====" + result);
		if (!StringUtil.isEmpty(result)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String resultStr = jsonObject.optString("title");
				if (StringUtil.isEquals(resultStr, API.returnSuccess, true)) {
					Constant.CURRENT_REFRESH = Constant.LOGIN_REFRESH;
					String path = jsonObject.optJSONObject("data").optString(
							"file");
					loginMessage.setPhoto(path);
					userInfo.setPhoto(path);
					LoginMessageUtils.saveProduct(loginMessage,
							UserDetailActivity.this);
					XUtilsImageLoader.getxUtilsImageLoader(
							UserDetailActivity.this,
							R.drawable.info_touxiang_moren,xcRoundImg,
							API.DOWN_IMAGE_URL + loginMessage.getPhoto());
					XUtilsImageLoader.getxUtilsImageLoader(
							UserDetailActivity.this,
							R.drawable.info_touxiang_moren,origionalImg,
							API.DOWN_IMAGE_URL + loginMessage.getPhoto());
					// if (!Constant.EDIT_FLAG) {
					Constant.CURRENT_REFRESH = Constant.LOGIN_REFRESH;
					// }
					Intent mIntent = new Intent();
					mIntent.setAction(Constant.REFRESH_FLAG);
					sendBroadcast(mIntent);
					showToast("头像设置成功");

				} else if (StringUtil.isEquals(resultStr, "FAIL", true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (StringUtil.isEquals(resultStr, "RELOGIN", true)) {
					DialogTool.getInstance(UserDetailActivity.this)
							.showConflictDialog();
				} else if (StringUtil.isEquals(resultStr, "DEFAULT", true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("NewApi")
	public long getBitmapsize(Bitmap bitmap) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		return baos.toByteArray().length / 1024;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
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
			Log.i("zou", "TAKE_A_PICTURE-resultCode:" + resultCode);
			if (resultCode == RESULT_OK) {
				bitmap = PhotoTools.decodeUriAsBitmap(Uri.fromFile(new File(
						PhotoTools.IMGPATH, PhotoTools.IMAGE_FILE_NAME)), this);
				judgeBitmap(bitmap);
			}
			break;
		case 2012:
			if (!StringUtil.isEmpty(data)) {
				showToast("取消头像设置");
			}
			break;
		case 1001:
			initViews();
			connectToServer();
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

	/**
	 * 
	 * @Title: showDialog
	 * @Description: TODO(dialog弹出和显示的样式)
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	private void showImgDialog() {
		View view = getLayoutInflater().inflate(R.layout.person_seting_dialog,
				null);
		ViewUtils.inject(this, view);
		dialog1 = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog1.setContentView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		PhotoTools.deleteFile();
		unregisterReceiver(broad);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			System.out.println("SUCCESS====" + "haha1" + intent.getAction()
					+ "_" + Constant.CURRENT_REFRESH + "_"
					+ Constant.LOGIN_REFRESH);
			if (!StringUtil.isEquals(intent.getAction(), Constant.REFRESH_FLAG,
					true)) {
				System.out
						.println("SUCCESS====" + "haha0" + intent.getAction());
				return;
			}
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.CAR_MANAGER_REFRESH, true)) {
				Constant.EDIT_FLAG = true;
				connectToServer();
			} else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.USER_NICK_EDIT_REFRESH, true)) {
				Constant.EDIT_FLAG = true;
				System.out.println("SUCCESS=========个人中心昵称更新");
				initViews();
			} else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.USER_NICK_EDIT_REFRESH_OTHER, true)) {
				System.out.println("SUCCESS=========个人中心编辑更新");
				connectToServer();
			} else {
				Constant.CURRENT_REFRESH = "";
			}
		}
	}
}
