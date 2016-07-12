package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.yunjia365.android.R;
import com.cheweishi.android.adapter.TelephonEchargeDetailsAdapter;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ReturnTheMoneyInfo;
import com.cheweishi.android.entity.ChargeResponse;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.XCRoundImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 我的余额
 *
 * @author XMh
 */
public class PurseBalanceActivity extends BaseActivity implements
        OnRefreshListener2<ListView> {
    private static final int INSURANCE_CODE = 100001;
    @ViewInject(R.id.left_action)
    private Button left_action;
    @ViewInject(R.id.title)
    private TextView title;
    @ViewInject(R.id.ll_purse_balance_pay)
    private LinearLayout ll_purse_balance_pay;
    @ViewInject(R.id.ll_purse_balance_device)
    private LinearLayout ll_purse_balance_device;
    @ViewInject(R.id.xcRoundImg)
    private XCRoundImageView xcRoundImg;
    @ViewInject(R.id.tv_balance_num)
    private TextView tv_balance_num;
    private MyBroadcastReceiver broad;
    // 上拉加载下拉刷新列表
    @ViewInject(R.id.telephonechargedetils_listview)
    private PullToRefreshListView telephonechargedetils_listview;
    @ViewInject(R.id.telephonemoney_linearlayout_nodata)
    private LinearLayout mLinearLayout;
    // item的数据
    private List<ChargeResponse.MsgBean> list = new ArrayList<ChargeResponse.MsgBean>();
    private List<ChargeResponse.MsgBean> mList = new ArrayList<ChargeResponse.MsgBean>();
    // 定义一个私有的余额详情adapter
    private TelephonEchargeDetailsAdapter telephonEchargeDetailsAdapter;
    private Intent intent;
    private int pageNumber = 1;
    private int pageSize = 10;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purse_balance);
        ViewUtils.inject(this);
        init();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(broad);
    }

    /**
     * 初始化视图
     */
    private void init() {
        title.setText(R.string.purse_my_balance);
        left_action.setText(R.string.back);
        telephonechargedetils_listview.setOnRefreshListener(this);
        telephonechargedetils_listview.setMode(Mode.BOTH);
        mListView = telephonechargedetils_listview.getRefreshableView();
        telephonEchargeDetailsAdapter = new TelephonEchargeDetailsAdapter(this,
                list);
        mListView.setAdapter(telephonEchargeDetailsAdapter);
        String money = getIntent().getStringExtra("money");
        if (StringUtil.isEmpty(money)) {
            tv_balance_num.setText("0.00");
        } else {
            tv_balance_num.setText(money);
        }
        request();
    }

    @OnClick({R.id.left_action, R.id.title, R.id.ll_purse_balance_pay, R.id.ll_purse_balance_device})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_action:// 返回
                PurseBalanceActivity.this.finish();
                break;
            case R.id.ll_purse_balance_pay:
                intent = new Intent(PurseBalanceActivity.this, PayActivty.class);
                startActivity(intent);
                break;
            case R.id.ll_purse_balance_device:
//                intent = new Intent(PurseBalanceActivity.this, PayActivty.class);
//                intent.putExtra("PAY_TYPE", true);
//                startActivity(intent);
                OpenCamera();
                break;
            default:
                break;
        }
    }

//    /**
//     * 打开相机
//     */
//    private void OpenCamera() {
//        applyAdmin(Manifest.permission.CAMERA, MY_CAMEAR_PREMESSION);
//        PackageManager pkm = getPackageManager();
//        boolean has_permission = (PackageManager.PERMISSION_GRANTED == pkm
//                .checkPermission("android.permission.CAMERA", baseContext.getPackageName()));//"packageName"));
//        if (has_permission) {
//            Intent intent = new Intent(baseContext,
//                    MipcaActivityCapture.class);
//            intent.putExtra("PAY_TYPE", true);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        } else {
//            showToast("请为该应用添加打开相机权限");
//        }
//    }

    // 网络请求方法
    private void request() {
        ProgrosDialog.openDialog(baseContext);
        // 判断是否登陆
        if (isLogined()) {
            String url = NetInterface.BASE_URL + NetInterface.TEMP_USER_BALANCE + NetInterface.WALLET_RECORD + NetInterface.SUFFIX;
            Map<String, Object> param = new HashMap<>();
            param.put("userId", loginResponse.getDesc());
            param.put("token", loginResponse.getToken());
            param.put("walletType", "MONEY"); //  余额
            param.put("walletId", getIntent().getIntExtra("walletId", 1));
            param.put("pageSize", pageSize);
            param.put("pageNumber", pageNumber);
            netWorkHelper.PostJson(url, param, this);

//			params_insurance.addBodyParameter("uid", loginMessage.getUid());
//			params_insurance.addBodyParameter("mobile",
//					loginMessage.getMobile());
//			params_insurance.addBodyParameter("pageSize", pageSize + "");
//			params_insurance.addBodyParameter("pageNumber", pageNumber + "");
//			httpBiz.httPostData(INSURANCE_CODE, API.CSH_REST_OF_MONEY_LIST_URL,
//					params_insurance, this);
        }

    }

    @Override
    public void receive(String data) {
        telephonechargedetils_listview.onRefreshComplete();
        ProgrosDialog.closeProgrosDialog();
        ChargeResponse response = (ChargeResponse) GsonUtil.getInstance().convertJsonStringToObject(data, ChargeResponse.class);
        if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
            showToast(response.getDesc());
            return;
        }

        mList = response.getMsg();
        if (!StringUtil.isEmpty(mList) && mList.size() > 0) {
            list.addAll(mList);
            telephonEchargeDetailsAdapter.setlist(list);
        }

        tv_balance_num.setText(response.getDesc());
        loginResponse.setToken(response.getToken());
        LoginMessageUtils.saveloginmsg(baseContext, loginResponse);
    }

    @Override
    public void error(String errorMsg) {
        telephonechargedetils_listview.onRefreshComplete();
        ProgrosDialog.closeProgrosDialog();
    }

    /**
     * 接受服务器返回的JSON字符串
     */
    @Override
    public void receive(int code, String data) {
        super.receive(code, data);
        ProgrosDialog.closeProgrosDialog();
        telephonechargedetils_listview.onRefreshComplete();
        switch (code) {
            case 400:
                showToast(R.string.server_link_fault);
                break;
            case INSURANCE_CODE:
                parseInsuranceJSON(data);
                break;
            default:
                break;
        }
    }

    /**
     * 解析JSON字符串
     *
     * @param result
     */
    private void parseInsuranceJSON(String result) {
        if (StringUtil.isEmpty(result)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (StringUtil.isEquals(jsonObject.optString("state"),
                    API.returnSuccess, true)) {
                String balance = jsonObject.optJSONObject("data").optString(
                        "balance");
                if (StringUtil.isEmpty(balance)) {
                    tv_balance_num.setText("0.00");
                } else {
                    tv_balance_num.setText(balance);
                }
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<List<ReturnTheMoneyInfo>>() {
                }.getType();
                mList = gson.fromJson(jsonObject.optJSONObject("data")
                        .optString("walletRecordList"), type);
                if (!StringUtil.isEmpty(mList) && mList.size() > 0) {
                    list.addAll(mList);
                    telephonEchargeDetailsAdapter.setlist(list);
                }
            } else if (StringUtil.isEquals(jsonObject.optString("state"),
                    API.returnRelogin, true)) {
                ReLoginDialog.getInstance(this).showDialog(
                        jsonObject.optString("message"));
            } else {
                showToast(jsonObject.optString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        // TODO Auto-generated method stub
        pageNumber = 1;
        if (!StringUtil.isEmpty(list)) {
            list.clear();
        }
        if (!StringUtil.isEmpty(mList)) {
            mList.clear();
        }
        request();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        // TODO Auto-generated method stub
        pageNumber++;
        request();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broad == null) {
            broad = new MyBroadcastReceiver();
        }
        IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
        registerReceiver(broad, intentFilter);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (!StringUtil.isEquals(intent.getAction(), Constant.REFRESH_FLAG,
                    true)) {
                return;
            }
            if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
                    Constant.RECHARGE_REFRESH, true)) {
                Constant.EDIT_FLAG = true;

                list.clear();
                pageNumber = 1;
                request();
            } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
                    Constant.WEIXIN_PAY_REFRESH, true)) {
                Constant.EDIT_FLAG = true;

                list.clear();
                pageNumber = 1;
                request();
            } else if (StringUtil.isEquals(Constant.CURRENT_REFRESH, Constant.PAY_REFRESH, true)) {
                Constant.EDIT_FLAG = true;

                list.clear();
                pageNumber = 1;
                request();
            }
        }
    }
}
