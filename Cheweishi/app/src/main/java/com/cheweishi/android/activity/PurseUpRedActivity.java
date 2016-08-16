package com.cheweishi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.IntegralAdapter;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.PurseIntegralResponse;
import com.cheweishi.android.response.BaseResponse;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.XCRoundImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *成长红包
 *
 * @author XMH
 */
public class PurseUpRedActivity extends BaseActivity implements
        OnRefreshListener2<ListView> {


    private static final int INTEGRAL_CODE = 2001;

    @ViewInject(R.id.left_action)
    private Button left_action;
    @ViewInject(R.id.title)
    private TextView title;
    @ViewInject(R.id.xcRoundImg)
    private XCRoundImageView xcRoundImg;
    // 下拉加载的listview
    @ViewInject(R.id.integral_xlistview)
    private PullToRefreshListView mListView;
    // 返回顶部的button
    @ViewInject(R.id.integral_return_top)
    private Button mReturnTop;
    //总积分
    @ViewInject(R.id.tv_balance_num)
    private TextView tv_balance_num;
    @ViewInject(R.id.ley_integral)
    private RelativeLayout ley_integral;

    // 列表
    private ListView listView;
    // 积分的适配器
    private IntegralAdapter mIntegralAdapter;


    private List<PurseIntegralResponse.MsgBean> mList;

    private int pageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purse_red);
        ViewUtils.inject(this);


        mList = new ArrayList<PurseIntegralResponse.MsgBean>();
        mIntegralAdapter = new IntegralAdapter(baseContext, mList);
        mListView.setAdapter(mIntegralAdapter);
        mListView.setMode(Mode.BOTH);
        mListView.setOnRefreshListener(this);
        init();
    }

    private void init() {
        title.setText(R.string.purse_red);
        left_action.setText(getResources().getString(R.string.back));
        left_action.setOnClickListener(listener);
        ley_integral.setOnClickListener(listener);
        // TODO 接收数据处理
        tv_balance_num.setText(getIntent().getStringExtra("score"));
        request();
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.left_action:
                    PurseUpRedActivity.this.finish();
                    break;
                case R.id.integral_return_top:
                    listView.setSelection(0);
                    break;
                case R.id.ley_integral: // 点击积分商城的时候
//                    getDuiBaUrl();
                    break;
                default:
                    break;
            }
        }
    };




    @Override
    public void receive(String data) {
        mListView.onRefreshComplete();
        ProgrosDialog.closeProgrosDialog();
        PurseIntegralResponse response = (PurseIntegralResponse) GsonUtil.getInstance().convertJsonStringToObject(data, PurseIntegralResponse.class);
        if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
            showToast(response.getDesc());
            return;
        }

        List<PurseIntegralResponse.MsgBean> temp = response.getMsg();
        if (!StringUtil.isEmpty(temp) && temp.size() > 0) {
            mList.addAll(temp);
            mIntegralAdapter.setList(mList);
        }

        loginResponse.setToken(response.getToken());
        LoginMessageUtils.saveloginmsg(baseContext, loginResponse);


    }

    @Override
    public void error(String errorMsg) {
        mListView.onRefreshComplete();
        showToast(R.string.server_link_fault);
    }


    /***
     * 显示加载完成的数据
     *
     * @param msg
     */
    private void getNotInformation(String msg) {
        // TODO Auto-generated method stub
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText(msg);
        listView.addFooterView(textView);

    }

    /***
     * 设置值
     *
     * @param dataJsonObject
     */
    protected void getValue(JSONObject dataJsonObject) {
        // TODO Auto-generated method stub
        String sign = dataJsonObject.optString("sign");
        String total = dataJsonObject.optString("total");
        String mile = dataJsonObject.optString("mile");
    }

    ;

    /**
     * 网络请求方法
     */
    private void request() {
        ProgrosDialog.openDialog(this);
        if (isLogined()) {
            String url = NetInterface.BASE_URL + NetInterface.TEMP_USER_BALANCE + NetInterface.WALLET_RECORD + NetInterface.SUFFIX;
            Map<String, Object> param = new HashMap<>();
            param.put("userId", loginResponse.getDesc());
            param.put("token", loginResponse.getToken());
            param.put("walletType", "REDPACKET"); //  红包
            param.put("walletId", getIntent().getIntExtra("walletId", 1));
            param.put("pageSize", 5);
            param.put("pageNumber", pageNumber);
            netWorkHelper.PostJson(url, param, this);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNumber = 1;
        if (!StringUtil.isEmpty(mList)) {
            mList.clear();
        }
        request();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNumber++;
        request();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CreditActivity.creditsListener = null;
    }
}
