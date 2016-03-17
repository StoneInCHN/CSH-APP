package com.cheweishi.android.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.PhotoTools;
import com.cheweishi.android.utils.BitmapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 
 * @author Xiaojin行驶证上传
 * 
 */
public class InsuranceInformationUploadActivity extends BaseActivity implements
		OnClickListener {

	@ViewInject(R.id.btn_front_driver_licence_take_pic)
	private Button btn_front_driver_licence_take_pic;
	@ViewInject(R.id.btn_front_driver_licence_select)
	private Button btn_front_driver_licence_select;
	@ViewInject(R.id.btn_back_driver_licence_take_pic)
	private Button btn_back_driver_licence_take_pic;
	@ViewInject(R.id.btn_back_driver_licence_select)
	private Button btn_back_driver_licence_select;
	@ViewInject(R.id.btn_car_driving_licence_commit)
	private Button btn_car_driving_licence_commit;
	private String mAlbumPicturePath = null;
	public static final int TAKE_A_PICTURE = 10;
	private boolean frontChoosedFlag = false;
	private final int UPLOAD_IMG_TYPE = 10003;
	private String frontPath;
	private String backPath;
	@ViewInject(R.id.img_front_driver_licence)
	private ImageView img_front_driver_licence;
	@ViewInject(R.id.img_back_driver_licence)
	private ImageView img_back_driver_licence;
	@ViewInject(R.id.left_action)
	private Button left_action;
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.right_action)
	private TextView right_action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insurance_information_upload);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		title.setText(R.string.title_activity_insurance_information_upload);
		left_action.setText(R.string.back);
	}

	@OnClick({ R.id.btn_front_driver_licence_take_pic,
			R.id.btn_front_driver_licence_select,
			R.id.btn_back_driver_licence_take_pic,
			R.id.btn_back_driver_licence_select,
			R.id.btn_car_driving_licence_commit, R.id.left_action })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_front_driver_licence_take_pic:
			frontChoosedFlag = true;
			goToTakeAPic();
			break;
		case R.id.btn_front_driver_licence_select:
			frontChoosedFlag = true;
			selectFromGallery();
			break;
		case R.id.btn_back_driver_licence_take_pic:
			frontChoosedFlag = false;
			goToTakeAPic();
			break;
		case R.id.btn_back_driver_licence_select:
			frontChoosedFlag = false;
			selectFromGallery();
			break;
		case R.id.btn_car_driving_licence_commit:
			break;
		case R.id.left_action:
			finish();
			break;
		}
	}

	private void goToTakeAPic() {
		PhotoTools.init();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				PhotoTools.IMGPATH, PhotoTools.IMAGE_FILE_NAME)));
		startActivityForResult(intent, TAKE_A_PICTURE);
	}

	private void selectFromGallery() {
		PhotoTools.init();
		if (PhotoTools.mIsKitKat) {
			PhotoTools.selectImageUriAfterKikat(this);
		} else {
			PhotoTools.cropImageUri(this);
		}
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
		}
	}

	private void judgeBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			showToast("图片获取失败");
		} else {
			saveBitmap(bitmap);
		}
	}

	@SuppressWarnings("static-access")
	private void saveBitmap(Bitmap bitmap) {
		if (frontChoosedFlag == true) {
			img_front_driver_licence.setImageBitmap(bitmap);
		} else {
			img_back_driver_licence.setImageBitmap(bitmap);
		}
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
		httpBiz.uploadMethod(UPLOAD_IMG_TYPE, params, API.UPLOAD_IMG_URL, this,
				this);
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
		case UPLOAD_IMG_TYPE:
			parseImgJSON(data);
			break;
		}
	}

	/**
	 * 解析上传图片之后返回的JSON数据
	 * 
	 * @param result
	 */
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
					if (frontChoosedFlag == true) {
						frontPath = path;
						XUtilsImageLoader.getxUtilsImageLoader(this,
								R.drawable.home_button,img_front_driver_licence, frontPath);
					} else {
						backPath = path;
						XUtilsImageLoader.getxUtilsImageLoader(this,
								R.drawable.home_button,img_back_driver_licence, backPath);
					}
				} else if (StringUtil.isEquals(resultStr, "FAIL", true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				} else if (StringUtil.isEquals(resultStr, "RELOGIN", true)) {
					DialogTool.getInstance(this).showConflictDialog();
				} else if (StringUtil.isEquals(resultStr, "DEFAULT", true)) {
					showToast(jsonObject.optJSONObject("data").optString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * 提交行驶证
	 */
	private void connectToServer() {

	}
}
