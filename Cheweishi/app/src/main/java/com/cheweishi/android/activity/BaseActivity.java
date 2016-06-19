package com.cheweishi.android.activity;

import java.io.File;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheweishi.android.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.JSONCallback;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.entity.LoginResponse;
import com.cheweishi.android.http.NetWorkHelper;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.ActivityControl;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.ResponseInfo;
import com.umeng.analytics.MobclickAgent;
import com.zzhoujay.richtext.ImageFixCallback;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;

/**
 * 所有Activity的父类
 *
 * @author zhangq
 */
public abstract class BaseActivity extends FragmentActivity implements
        JSONCallback {
    public static final int MY_CAMEAR_PREMESSION = 0; // 拍照
    public static final int MY_LOCATION_PREMESSION = 1; // 定位
    /**
     * 上下文 当进入activity时必须 mContext = this 方可使用，否则会报空指针
     */
    protected NotificationManager notificationManager;
    private static long startedActivityCount = 0l;
    public ProgressDialog progress;
    // 环信账号在别处登录
    // public static boolean isConflict = false;
    // // 环信账号被移除
    // public static boolean isCurrentAccountRemoved = false;
    public static boolean isConflictDialogShow;
    public static boolean isAccountRemovedDialogShow;
    private Toast mToast;
    public HttpBiz httpBiz;
    public static LoginMessage loginMessage;
    public static LoginResponse loginResponse;
    public static List<LoginMessage> loginMessages;
    public static List<LoginResponse> loginResponses;
    public static Context baseContext;
    protected NetWorkHelper netWorkHelper;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (0x471 == msg.what) {
                PullToRefreshBase<ListView> pullToRefreshListView = (PullToRefreshBase<ListView>) msg.obj;
                pullToRefreshListView.onRefreshComplete();
                pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    };

    /**
     * Activity的回调函数。当application进入前台时，该函数会被自动调用。
     */
    protected void applicationDidEnterForeground() {
        // submitLLogin();
    }

    /**
     * Activity的回调函数。当application进入后台时，该函数会被自动调用。
     */
    protected void applicationDidEnterBackground() {
        // showToast("wowo");
    }

    /**
     * // * 检查当前用户是否被删除 //
     */
    // public static boolean getCurrentAccountRemoved() {
    // return isCurrentAccountRemoved;
    // }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        baseContext = this;
        isLogined();

        netWorkHelper = NetWorkHelper.getInstance(baseContext);
        ActivityControl.addActivity(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		PushAgent.getInstance(this).onAppStart();
        if (getIntent() == null) {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(
                            getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startedActivityCount++;

        if (1 == startedActivityCount) {
            applicationDidEnterForeground();
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    public void setTitle(String desc) {
        if (null != desc && !"".equals(desc)) {
            MainNewActivity.tv_home_title.setText(desc);
        }
    }

    /**
     * 重回前台显示调用
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Toast.makeText(this, "good", Toast.LENGTH_LONG).show();
        // onresume时，取消notification显示

//		EMChatManager.getInstance().activityResumed();
        MobclickAgent.onPageStart(getClass().getName()); // 统计页面
        MobclickAgent.onResume(this); // 统计时长

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progress != null) {
            progress.dismiss();
        }
        MobclickAgent.onPageEnd(getClass().getName()); // 中会保存信息
        MobclickAgent.onPause(this);

        if (null != mToast) {
            mToast.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progress != null) {
            progress.dismiss();
        }

        startedActivityCount--;
        if (0 == startedActivityCount) {
            applicationDidEnterBackground();
        }
    }

    /**
     * Activity销毁，关闭加载效果
     */
    @Override
    protected void onDestroy() {
        ActivityControl.removeActivity(this);
        super.onDestroy();
        if (progress != null) {
            progress.dismiss();
        }
    }

    public void refreshLoginMessage() {

    }

    /**
     * 消息队列方式显示进度
     */
    public Handler progressHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (progress != null) {
                progress.dismiss();
            }
            progress = new ProgressDialog(BaseActivity.this);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setMessage(msg.obj.toString());
            progress.setCancelable(false);
            progress.show();
        }
    };

    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });
        }
        // if (mToast == null) {
        // mToast = Toast.makeText(getApplicationContext(), text,
        // Toast.LENGTH_SHORT);
        // } else {
        // mToast.setText(text);
        // }
        //
        // }
        //
        // mToast.show();
    }

    public void showToast(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(
                            BaseActivity.this.getApplicationContext(),
                            resourceId, Toast.LENGTH_LONG);
                } else {
                    mToast.setText(resourceId);
                }
                mToast.show();
            }
        });
    }

    /**
     * 隐藏消息队列进度的显示
     */
    public Handler disProgressHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (progress != null) {
                progress.dismiss();
            }
        }
    };

    /**
     * 返回的json数据
     */
    @Override
    public void receive(int type, String data) {
        // TODO Auto-generated method stub

    }


    /**
     * 返回的json数据
     *
     * @param TAG
     * @param data
     */
    @Override
    public void receive(String TAG, String data) {

    }

    /**
     * 返回的json数据
     *
     * @param data
     */
    @Override
    public void receive(String data) {

    }

    /**
     * 失败数据
     *
     * @param errorMsg
     */
    @Override
    public void error(String errorMsg) {
        ProgrosDialog.closeProgrosDialog();
        showToast(R.string.server_link_fault);
    }

    /**
     * 返回文件下载回调
     */
    @Override
    public void downFile(int type, ResponseInfo<File> arg0) {
        // TODO Auto-generated method stub

    }

    static int feed;

    @SuppressWarnings("unchecked")
    public boolean isLogined() {
        if (StringUtil.isEmpty(loginResponse)) {
//            loginResponses = (List<LoginResponse>) DBTools.getInstance(this)
//                    .findAll(LoginResponse.class);
            loginResponse = LoginMessageUtils.getLoginResponse(baseContext);
//            if (!StringUtil.isEmpty(loginResponses) && loginResponses.size() > 0) {
//                loginResponse = loginResponses.get(0);
//            }
        }
        if (StringUtil.isEmpty(BaseActivity.loginResponse)
                || StringUtil.isEmpty(loginResponse.getDesc())) {
            return false;
        }
//        Log.i("result", "===uid==" + loginResponse.getMsg().getId() + "===mobile=="
//                + loginResponse.getMsg().getUserName());
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean isLogined_old() {
        if (StringUtil.isEmpty(loginMessage)) {
            loginMessages = (List<LoginMessage>) DBTools.getInstance(this)
                    .findAll(LoginMessage.class);
            if (!StringUtil.isEmpty(loginMessages) && loginMessages.size() > 0) {
                loginMessage = loginMessages.get(0);
            }
        }
        Log.i("result", "===loginMessage==" + String.valueOf(loginMessage));
        if (StringUtil.isEmpty(BaseActivity.loginMessage)
                || StringUtil.isEmpty(loginMessage.getUid())) {
            return false;
        }
        Log.i("result", "===uid==" + loginMessage.getUid() + "===mobile=="
                + loginMessage.getMobile());
        return true;
    }

    /**
     * 判断环信账号
     *
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
    public boolean hasCar() {
        if (isLogined()) {
            if (!StringUtil.isEmpty(loginResponse.getMsg())
                    && !StringUtil.isEmpty(loginResponse.getMsg().getDefaultVehicle())) {
                return true;
            }

        }
        return false;
    }

    /**
     * 判断是否有设备
     *
     * @return
     */
    public boolean hasDevice() {
        if (hasCar() && !StringUtil.isEmpty(loginResponse.getMsg().getDefaultVehicle()) && !StringUtil.isEmpty(loginResponse.getMsg().getDefaultDeviceNo())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断积分
     *
     * @return
     */
    public boolean hasScore() {
//        if (isLogined()) {
//            if (!StringUtil.isEmpty(loginMessage.getScore())
//                    && !StringUtil.isEmpty(loginMessage.getScore().getCid())
//                    && !StringUtil.isEmpty(loginMessage.getScore().getNow())) {
//                return true;
//            }
//        }
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
            if (!StringUtil.isEmpty(loginResponse.getMsg().getPhoto())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBrandName() {
        if (isLogined()) {
            if (!StringUtil.isEmpty(loginResponse.getMsg().getDefaultVehicle())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBrandIcon() {
        if (isLogined()) {
            if (!StringUtil.isEmpty(loginResponse.getMsg().getDefaultVehicleIcon())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNick() {
        if (isLogined()) {
            if (!StringUtil.isEmpty(loginResponse.getMsg().getNickName())) {
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
            if (!StringUtil.isEmpty(loginResponse.getMsg().getUserName())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNote() {
        if (isLogined()) {
            if (!StringUtil.isEmpty(loginResponse.getMsg().getSignature())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户没有登录，去登录
     */
    public boolean goToLoginFirst() {
        if (StringUtil.isEmpty(loginResponse)
                || StringUtil.isEmpty(loginResponse.getDesc())) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.score_business_query_enter,
                    R.anim.score_business_query_exit);
            return false;
        } else {
            return true;
        }
    }

    public void dealCallBackFromAdapter(int pos, Object obj) {

    }


    public String getUserId() {
        if (null != loginResponse)
            return loginResponse.getDesc();
        return null;
    }

    public String getToken() {
        if (null != loginResponse)
            return loginResponse.getToken();
        return null;
    }

    public void setUserToken(String token) {
        if (null != token && null != loginResponse) {
            loginResponse.setToken(token);
            LoginMessageUtils.saveloginmsg(baseContext, loginResponse);
        }
    }

    public void onRefreshOver(PullToRefreshBase<ListView> refreshView) {
        Message msg = Message.obtain();
        msg.what = 0x471;
        msg.obj = refreshView;
        handler.sendMessage(msg);
    }


    public boolean applyAdmin(String permission, int code) {

        if (!hasPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {//Manifest.permission.CAMERA
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        code);
            }
            return true;
        }

        return false;
    }

    private boolean hasPermission() {
        int permissionCheck = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0

            permissionCheck = ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA);
        }
        return permissionCheck == PackageManager.PERMISSION_GRANTED ? true : false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_CAMEAR_PREMESSION:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限同一了.
                } else {
                    //权限被拒绝了
                    showToast("拍照权限已经被拒绝,你可以在权限管理重新添加该权限");
                }

                break;
            case MY_LOCATION_PREMESSION:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限同一了.
                } else {
                    //权限被拒绝了
                    showToast("定位权限已经被拒绝,你可以在权限管理重新添加该权限");
                }

                break;
        }
    }

    public void setRitchText(String text, TextView textView) {
        RichText.from(text).autoFix(false).fix(new ImageFixCallback() {
            @Override
            public void onFix(ImageHolder holder, boolean imageReady) {
                if (holder.getWidth() > 500 && holder.getHeight() > 500) {
                    holder.setAutoFix(true);
                }
            }
        }).into(textView);
    }

    /**
     * @param msg
     * @param buttonMsg
     * @param type      0 表示添加车辆,1表示绑定
     */
    public void showCustomDialog(String msg, String buttonMsg, final int type) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(getString(R.string.remind));

        builder.setPositiveButton(buttonMsg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (0 == type) { // 添加车辆
                            Intent intent = new Intent(baseContext, AddCarActivity.class);
                            startActivity(intent);
                        } else { // 绑定设备
                            Intent intent = new Intent(baseContext, DevicesListActivity.class);
                            intent.putExtra("cid", loginResponse.getMsg().getDefaultVehicleId());
                            startActivity(intent);
                        }
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    /**
     * @param msg
     * @param buttonMsg
     * @param type      0 表示添加车辆,1表示绑定
     */
    public void showCustomDialog(String msg, String buttonMsg, final int type, final Activity activity) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(getString(R.string.remind));

        builder.setPositiveButton(buttonMsg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (0 == type) {
                            Intent intent = new Intent(baseContext, AddCarActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(baseContext, DevicesListActivity.class);
                            startActivity(intent);
                        }
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.finish();
                    }
                });

        builder.create().show();
    }


    /**
     * 打开相机 为了购买
     */
    public void OpenCamera() {
        applyAdmin(Manifest.permission.CAMERA, MY_CAMEAR_PREMESSION);
        PackageManager pkm = getPackageManager();
        boolean has_permission = (PackageManager.PERMISSION_GRANTED == pkm
                .checkPermission("android.permission.CAMERA", baseContext.getPackageName()));//"packageName"));
        if (has_permission) {
            Intent intent = new Intent(baseContext,
                    MipcaActivityCapture.class);
            intent.putExtra("PAY_TYPE", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            showToast("请为该应用添加打开相机权限");
        }
    }
}
