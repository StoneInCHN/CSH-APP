package com.cheweishi.android.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cheweishi.android.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.config.Constant;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付返回数据处理
 *
 * @author mingdasen
 */
//com.cheweishi.android.wxapi.WXPayEntryActivity
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
//	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weixinpay_result_activity);

        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i("Tanck", "onPayFinish, errCode = " + resp.errCode);
        Log.i("Tanck", "onPayFinish, getType = " + resp.getType());
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			Intent intent;
            if (resp.errCode == 0) {
                Intent mIntent = new Intent();
                Constant.CURRENT_REFRESH = Constant.WEIXIN_PAY_REFRESH;
                mIntent.setAction(Constant.REFRESH_FLAG);
                Log.i("Tanck", "===发送微信支付成功广播==");
                sendBroadcast(mIntent);
//				mIntent = new Intent(WXPayEntryActivity.this, RechargeActivity.class);
                Log.i("Tanck", "===微信支付成功==");
                showToast("支付成功");
                finish();
//				startActivity(mIntent);
            } else {
                Log.i("Tanck", "===微信支付失败==");
                Intent mIntent = new Intent();
                Constant.CURRENT_REFRESH = Constant.WEIXIN_PAY_FAIL_REFRESH;
                mIntent.setAction(Constant.REFRESH_FLAG);
                Log.i("Tanck", "===发送微信支付成功广播==");
                sendBroadcast(mIntent);
                showToast("支付失败");
                WXPayEntryActivity.this.finish();
            }
        }
    }

//	void submitLogin() {
//		Log.i("result", "===发送登录信息确认请求==");
//		if (isLogined()) {
//			Log.i("result", "===发送登录信息确认请求=112212=" + isLogined());
//			RequestParams rp = new RequestParams();
//			rp.addBodyParameter("uid", loginMessage.getUid());
//			rp.addBodyParameter("key", loginMessage.getKey());
//			httpBiz.httPostData(1000008, API.LOGIN_MESSAGE_RELOGIN_URL, rp,
//					this);
//		}
//	}
//	@Override
//	public void receive(int type, String data) {
//		// TODO Auto-generated method stub
////		Log.i("result", "=0==获取登录信息确认数据=type=" + type);
////		super.receive(type, data);
//		Log.i("result", "=1==获取登录信息确认数据=type=" + type);
//		switch (type) {
//		case 1000008:
//			Log.i("result", "=2==获取登录信息确认数据=data=" + data);
//			parseLogin(data);
//			break;
//		}
//	}
//
//	protected void save(String jsonObject) {
//		Gson gson = new Gson();
//		java.lang.reflect.Type type = new TypeToken<LoginMessage>() {
//		}.getType();
//		loginMessage = gson.fromJson(jsonObject, type);
//		LoginMessageUtils.saveProduct(loginMessage, this);
//		System.out.println("SUCCESS=============" + "success");
////		initViews();
//		Intent mIntent = new Intent();
//		Constant.CURRENT_REFRESH = Constant.LOGIN_REFRESH;
//		mIntent.setAction(Constant.REFRESH_FLAG);
//		Log.i("result", "===发送微信支付成功广播==");
//		sendBroadcast(mIntent);
//		mIntent = new Intent(WXPayEntryActivity.this, RechargeActivity.class);
//		Log.i("result", "===微信支付成功==");
//		startActivity(mIntent);
//		WXPayEntryActivity.this.finish();
//	}
//
//	private void parseLogin(String data) {
//		if (StringUtil.isEmpty(data)) {
//			return;
//		}
//		System.out.println(data);
//		try {
//			JSONObject jsonObject = new JSONObject(data);
//			String resultStr = jsonObject.optString("operationState");
//			String resultMsg = jsonObject.optJSONObject("data")
//					.optString("msg");
//			if (StringUtil.isEquals(resultStr, API.returnSuccess, true)) {
//				save(jsonObject.optString("data"));
//			} else {
//				if (StringUtil.isEquals(resultStr, API.returnRelogin, true)) {
//					DialogTool.getInstance(this).showConflictDialog();
//				} else {
//					showToast(resultMsg);
//				}
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//	}

//	@Override
//	public void refreshLoginMessage() {
//		super.refreshLoginMessage();
//		
//	}
}
