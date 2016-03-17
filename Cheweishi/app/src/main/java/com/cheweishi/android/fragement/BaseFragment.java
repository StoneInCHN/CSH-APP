package com.cheweishi.android.fragement;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.JSONCallback;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.interfaces.CarReportListener;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.http.ResponseInfo;
import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends Fragment implements JSONCallback {
	protected Context mContext;
	protected LayoutInflater inflater;
	/**
	 * 这个某些需要用到
	 */
	protected CarReportListener reportListener;
	public HttpBiz httpBiz;
	private Toast mToast;
	public static LoginMessage loginMessage;
	public static Context baseContext;
	public static List<LoginMessage> loginMessages;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		baseContext = getActivity();
		isLogined();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		baseContext = getActivity();
		isLogined();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (httpBiz != null) {
			httpBiz.removeAllHandler();
		}
	}

	/**
	 * 跟新fragment
	 * 
	 * @param date
	 */
	public void updateData(String date) {

	}

	public void setCarReportListener(CarReportListener listener) {
		this.reportListener = listener;
	}

	@Override
	public void onResume() {
		super.onResume();
		/**
		 * 友盟页面统计
		 */
		MobclickAgent.onPageStart(getClass().getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		/**
		 * 友盟页面统计
		 */
		MobclickAgent.onPageEnd(getClass().getName());

	}

	/**
	 * 返回的json数据
	 */
	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
	}

	/**
	 * 返回文件下载回调
	 */
	@Override
	public void downFile(int type, ResponseInfo<File> arg0) {
		// TODO Auto-generated method stub

	}

	public void showToast(final String msg) {
		if (!TextUtils.isEmpty(msg)) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(
								mContext.getApplicationContext(), msg,
								Toast.LENGTH_LONG);
					} else {
						mToast.setText(msg);
					}
					mToast.show();
				}
			});
		}
	}

	public void showToast(final int msg) {
		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mToast == null) {
					mToast = Toast.makeText(mContext.getApplicationContext(),
							msg, Toast.LENGTH_LONG);
				} else {
					mToast.setText(msg);
				}
				mToast.show();
			}
		});
	}

	public boolean isLogined() {
		// if (StringUtil.isEmpty(loginMessage)) {
		// // loginMessage = LoginMessageUtils.getLoginMessage(baseContext);
		// // loginMessages = (List<LoginMessage>)
		// DBTools.getInstance(baseContext)
		// // .findAll(LoginMessage.class);
		// // if (!StringUtil.isEmpty(loginMessages)) {
		// // loginMessage = loginMessages.get(0);
		// // }
		//
		// }
		loginMessage = BaseActivity.loginMessage;
		if (StringUtil.isEmpty(loginMessage)
				|| StringUtil.isEmpty(loginMessage.getUid())) {
			return false;
		}
		return true;
	}

	/**
	 * 判断环信账号
	 * 
	 * @param context
	 * @return
	 */
	public boolean hasNo() {
		if (isLogined()) {
			if (!StringUtil.isEmpty(loginMessage.getNo())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否绑定车辆
	 * 
	 * @return
	 */
	/**
	 * 判断是否绑定车辆
	 * 
	 * @return
	 */
	public boolean hasCar() {
		if (isLogined()) {
			if (!StringUtil.isEmpty(loginMessage.getCarManager())
					&& !StringUtil.isEmpty(loginMessage.getCarManager()
							.getDevice())
					&& !StringUtil.isEmpty(loginMessage.getCarManager()
							.getPlate())
					&& !StringUtil
							.isEmpty(loginMessage.getCarManager().getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断积分
	 * 
	 * @param context
	 * @return
	 */
	public boolean hasScore() {
		if (isLogined()) {
			if (!StringUtil.isEmpty(loginMessage.getScore())
					&& !StringUtil.isEmpty(loginMessage.getScore().getCid())
					&& !StringUtil.isEmpty(loginMessage.getScore().getNow())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasAccount() {
		if (isLogined()) {
			if (!StringUtil.isEmpty(loginMessage.getAccount())
					&& !StringUtil.isEmpty(loginMessage.getAccount().getAid())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPhoto() {
		if (isLogined()) {
			if (!StringUtil.isEmpty(loginMessage.getPhoto())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasBrandName() {
		if (isLogined()) {
			if (!StringUtil.isEmpty(loginMessage.getCar())
					&& !StringUtil.isEmpty(loginMessage.getCarManager()
							.getBrandName())
					&& !StringUtil.isEmpty(loginMessage.getCarManager()
							.getSeriesName())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasNick() {
		if (isLogined()) {
			if (!StringUtil.isEmpty(loginMessage.getNick_name())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasSign() {
		if (isLogined()) {
			if (BaseActivity.loginMessage.getSign() == 1) {
				return true;
			}
		}
		return false;
	}

	public boolean hasTel() {
		if (isLogined()) {
			if (!StringUtil.isEmpty(loginMessage.getMobile())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasNote() {
		if (isLogined()) {
			if (!StringUtil.isEmpty(loginMessage.getSignature())) {
				return true;
			}
		}
		return false;
	}

}
