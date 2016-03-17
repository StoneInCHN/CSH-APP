package com.cheweishi.android.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.PhotoTools;
import com.cheweishi.android.utils.BitmapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 违章缴费
 * 
 * @author Xiaojin
 * 
 */
@ContentView(R.layout.activity_pay_pessany)
public class PayPessanyActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView right_action;
	@ViewInject(R.id.ll_peddany_upload)
	private LinearLayout ll_peddany_upload;
	@ViewInject(R.id.ll_pay_pessany_car_code)
	private LinearLayout ll_pay_pessany_car_code;
	private final int UPLOAD_IMG_TYPE = 10003;
	private String mAlbumPicturePath = null;
	public static final int TAKE_A_PICTURE = 10;
	private Dialog dialog1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_pessany);
		ViewUtils.inject(this);
		initViews();
	}

	/**
	 * 初始化视图
	 */
	private void initViews() {
		title.setText(R.string.title_activity_pay_pessany);
		left_action.setText(R.string.back);
	}

	@OnClick({ R.id.left_action, R.id.ll_peddany_upload, R.id.xiangji,
			R.id.xiangce, R.id.quxiao })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			finish();
			break;
		case R.id.ll_peddany_upload:
			showImgDialog();
			break;
		// 调用手机相机
		case R.id.xiangji:
			PhotoTools.init();
			dialog1.dismiss();
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
					PhotoTools.IMGPATH, PhotoTools.IMAGE_FILE_NAME)));
			startActivityForResult(intent, TAKE_A_PICTURE);
			break;
		// 调用手机相册
		case R.id.xiangce:
			PhotoTools.init();
			dialog1.dismiss();
			if (PhotoTools.mIsKitKat) {
				PhotoTools.selectImageUriAfterKikat(PayPessanyActivity.this);
			} else {
				PhotoTools.cropImageUri(PayPessanyActivity.this);
			}
			break;
		case R.id.quxiao:
			dialog1.dismiss();
			break;
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
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, data);
		Bitmap bitmap;
		if (data != null) {
			super.onActivityResult(arg0, arg1, data);
			switch (arg0) {
			case PhotoTools.SELECT_A_PICTURE:
				if (arg1 == RESULT_OK && null != data) {
					mAlbumPicturePath = PhotoTools.getPath(
							getApplicationContext(), data.getData());
					bitmap = PhotoTools.decodeUriAsBitmap(
							Uri.fromFile(new File(mAlbumPicturePath)), this);
					judgeBitmap(bitmap);
				} else if (arg1 == RESULT_CANCELED) {
					showToast("取消驾驶证设置");
				}
				break;
			case PhotoTools.SELECET_A_PICTURE_AFTER_KIKAT:
				if (arg1 == RESULT_OK && null != data) {
					mAlbumPicturePath = PhotoTools.getPath(
							getApplicationContext(), data.getData());
					bitmap = PhotoTools.decodeUriAsBitmap(
							Uri.fromFile(new File(mAlbumPicturePath)), this);
					judgeBitmap(bitmap);
				} else if (arg1 == RESULT_CANCELED) {
					showToast("取消驾驶证设置");
				}
				break;
			case TAKE_A_PICTURE:
				Log.i("zou", "TAKE_A_PICTURE-resultCode:" + arg1);
				if (arg1 == RESULT_OK) {
					bitmap = PhotoTools.decodeUriAsBitmap(Uri
							.fromFile(new File(PhotoTools.IMGPATH,
									PhotoTools.IMAGE_FILE_NAME)), this);
					judgeBitmap(bitmap);
				}
				break;
			default:
				break;
			}
		}
	}

	private void judgeBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			showToast("取消驾驶证设置");
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
					showToast("驾驶证设置失败，请重试...");
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

	/**
	 * 上传头像
	 * 
	 * @param pathString
	 * @throws Exception
	 */
	private void setImageView(String pathString) {
		File file = new File(pathString);
		RequestParams params = new RequestParams();
		params.addBodyParameter("file", file);
		params.addBodyParameter("uid", loginMessage.getUid());
		params.addBodyParameter("key", loginMessage.getKey());
		// httpBiz.httPostData(RELOGIN_TYPE, API.LOGIN_MESSAGE_RELOGIN_URL,
		// params, this);
		httpBiz = new HttpBiz(this);
		httpBiz.uploadMethod(UPLOAD_IMG_TYPE, params, API.UPLOAD_IMG_URL, this,
				this);
		ProgrosDialog.openDialog(this);
	}

	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		switch (type) {

		case UPLOAD_IMG_TYPE:
			parseImgJSON(data);
			break;
		case 400:
			break;
		default:
			break;
		}
	}

	private void parseImgJSON(String result) {

		if (!StringUtil.isEmpty(result)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String resultStr = jsonObject.optString("title");
				if (StringUtil.isEquals(resultStr, API.returnSuccess, true)) {
					Constant.CURRENT_REFRESH = Constant.LOGIN_REFRESH;
					// String path = jsonObject.optJSONObject("data").optString(
					// "file");
				} else if (StringUtil.isEquals(resultStr, "FAIL", true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (StringUtil.isEquals(resultStr, "RELOGIN", true)) {
					DialogTool.getInstance(PayPessanyActivity.this)
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

}
