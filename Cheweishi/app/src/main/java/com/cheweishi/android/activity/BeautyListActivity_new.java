package com.cheweishi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.cheweishi.android.R;
import com.cheweishi.android.adapter.MainListViewAdapter;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.MainSellerInfo;
import com.cheweishi.android.entity.MainSellerServiceInfo;
import com.cheweishi.android.entity.ServiceListResponse;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangq
 */
@ContentView(R.layout.activity_washcar_list)
public class BeautyListActivity_new extends BaseActivity implements
        OnRefreshListener2<ListView> {
    @ViewInject(R.id.title)
    private TextView tvTitle;
    @ViewInject(R.id.left_action)
    private Button btnLeft;
    @ViewInject(R.id.listview)
    private PullToRefreshListView mListView;
    @ViewInject(R.id.lay_wash_beauty)
    private LinearLayout lay_wash_beauty;
    // private WashcarListAdapter mListViewAdapter;
    private MainListViewAdapter listViewAdapter;
    // private int count = 0;
    private List<ServiceListResponse.MsgBean> washcarList;
    private List<LatLng> positionList;

    private int page = 1;

    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		/* init tools */
        ViewUtils.inject(this);

		/**/
        initView();
        getDataFromIntent();
    }

    private void getDataFromIntent() {
        if (isLogined()) {

            ProgrosDialog.openDialog(this);
            String url = NetInterface.BASE_URL + NetInterface.TEMP_HOME_URL + NetInterface.LIST + NetInterface.SUFFIX;
            Map<String, Object> param = new HashMap<>();
            param.put("userId", loginResponse.getDesc());
            LogHelper.d("----send:" + loginResponse.getToken());
            param.put("token", loginResponse.getToken());
            param.put("latitude", MyMapUtils.getLatitude(this));//维度
//            param.put("latitude", "10");//维度
            param.put("longitude", MyMapUtils.getLongitude(this));//经度
//            param.put("longitude", "10");//经度
            /**
             * 1保养
             2	洗车
             3	维修
             4	紧急救援
             5	美容
             */
            param.put("serviceCategoryId", 5); // TODO 目前只有一种
            param.put("pageSize", 5);
            param.put("pageNumber", page);
            param.put(Constant.PARAMETER_TAG, NetInterface.LIST);
            netWorkHelper.PostJson(url, param, this);
        }
    }

    @Override
    public void receive(String TAG, String data) {
        ProgrosDialog.closeProgrosDialog();
        mListView.onRefreshComplete();
        switch (TAG) {
            case NetInterface.LIST:
                ServiceListResponse response = (ServiceListResponse) GsonUtil.getInstance().convertJsonStringToObject(data, ServiceListResponse.class);
                if (response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    // TODO 成功
                    if (null != response.getMsg()) {
                        washcarList.addAll(0 <= (washcarList.size() - 1) ? (washcarList.size() - 1) : 0, response.getMsg());
                    }

                    if (0 == washcarList.size()) {
                        EmptyTools.setEmptyView(baseContext, mListView);
                        EmptyTools.setImg(R.drawable.mycar_icon);
                        EmptyTools.setMessage("当前没有美容信息");
                    } else {

                        listViewAdapter.setData(washcarList);
                        total = response.getPage().getTotal();
                        if (total < 5) {
                            mListView.setMode(Mode.PULL_FROM_START);
                        }
                    }
                    loginResponse.setToken(response.getToken());
//                    LoginMessageUtils.saveloginmsg(baseContext,loginResponse);
                } else if (response.getCode().equals(NetInterface.RESPONSE_TOKEN)) {
                    // TODO 超时
                    Intent intent = new Intent(BeautyListActivity_new.this, LoginActivity.class);
                    intent.putExtra(Constant.AUTO_LOGIN, true);
                    startActivity(intent);
                    this.finish();
                    overridePendingTransition(R.anim.score_business_query_enter,
                            R.anim.score_business_query_exit);
                }
                break;
        }


    }

    @Override
    public void error(String errorMsg) {
        ProgrosDialog.closeProgrosDialog();
        showToast(R.string.FAIL);
    }

    @Override
    public void receive(int type, String data) {
        ProgrosDialog.closeProgrosDialog();
        mListView.onRefreshComplete();
        switch (type) {
            case 1001:
                doAboutWashcar(data);
                break;
            default:
                break;
        }
    }

    private void initView() {
        tvTitle.setText("到店美容");
        btnLeft.setText(R.string.back);
        mListView.setOnRefreshListener(this);
        mListView.setMode(Mode.BOTH);

        washcarList = new ArrayList<ServiceListResponse.MsgBean>();
//		serviceInfos = new ArrayList<MainSellerServiceInfo>();
        // TODO 修改主页而注释
        listViewAdapter = new MainListViewAdapter(this, washcarList);
        mListView.setAdapter(listViewAdapter);
        // lay_wash_beauty.setVisibility(View.GONE);
        // mListViewAdapter = new MainListViewAdapter(mListViewLists, this);
        // listViewAdapter = new MainListViewAdapter(this, washcarList);
        // mListView.setAdapter(listViewAdapter);
    }

    @OnClick({R.id.left_action, R.id.tvHistory})
    public void finishActivity(View v) {
        switch (v.getId()) {
            case R.id.left_action:
                onBackPressed();
                break;
            case R.id.tvHistory:
                Intent intent = new Intent(this, WashcarHistoryActivity.class);
                // intent.putExtra("count", count);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 解析洗车店列表数据
     *
     * @param data
     */
    private void doAboutWashcar(String data) {
        if (StringUtil.isEmpty(data)) {
            showToast(R.string.FAIL);
            return;
        }
        try {
            JSONObject json = new JSONObject(data);
            if (StringUtil.isEquals(API.returnSuccess, json.optString("state"),
                    true)) {
                JSONArray array = json.optJSONArray("data");
                JSONObject jsonObject = null;
                MainSellerInfo sellerInfo = null;
                for (int i = 0; i < array.length(); i++) {
                    jsonObject = array.optJSONObject(i);
                    sellerInfo = new MainSellerInfo();
                    sellerInfo.setName(jsonObject.optString("store_name"));
                    sellerInfo.setAddress(jsonObject.optString("address"));
                    sellerInfo.setImgUrl(jsonObject.optString("image_1"));//
                    sellerInfo.setEvaluateImg(jsonObject.optInt("evaluateImg"));// ?
                    sellerInfo.setEvaluate(jsonObject.optString("evaluate"));// ?
                    String distance = String.format("%.2f",
                            jsonObject.optDouble("distance") / 1000);
                    sellerInfo.setDistance(distance + "km");
                    sellerInfo.setId(jsonObject.optString("id"));
                    sellerInfo.setAppoint(jsonObject.optString("is_appoint"));
                    // sellerInfo.setLon(jsonObject.getDouble("lon"));

                    JSONArray array2 = jsonObject.optJSONArray("commodity");
                    JSONObject object = null;
                    MainSellerServiceInfo serviceInfo = null;
                    List<MainSellerServiceInfo> serviceInfos = new ArrayList<MainSellerServiceInfo>();
                    for (int j = 0; j < array2.length(); j++) {
                        serviceInfo = new MainSellerServiceInfo();
                        object = array2.optJSONObject(j);
                        serviceInfo.setName(object.optString("goods_name"));
                        serviceInfo.setfPrice(object
                                .optString("discount_price"));
                        serviceInfo.setPrice(object.optString("price"));
                        serviceInfo.setId(object.optString("id"));
                        serviceInfo.setIsFPrice(object
                                .optInt("is_discount__price"));
                        serviceInfo.setIsRed(object.optInt("support_red") + "");
                        serviceInfo.setCate_id_2(object.optString("cate_id_2"));
                        serviceInfos.add(serviceInfo);
                    }
                    // Log.i("result", "===serviceInfos==" +
                    // serviceInfos.size());
                    sellerInfo.setServices(serviceInfos);
//					washcarList.add(sellerInfo);
                }
//				Log.i("result", "===washcarList==" + washcarList.size());
                if (StringUtil.isEmpty(washcarList) || washcarList.size() <= 0) {
                    EmptyTools.setEmptyView(this, mListView);
                    EmptyTools.setImg(R.drawable.dingdanwu_icon);
                    EmptyTools.setMessage("亲，暂无相关数据");
                } else {
                    // TODO 修改主页而注释
//					listViewAdapter.setData(washcarList);
                }

            } else if (StringUtil.isEquals(API.returnRelogin,
                    json.optString("state"), true)) {
                ReLoginDialog.getInstance(this).showDialog(
                        json.optString("message"));
            } else {
                showToast(json.optString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // @OnCompoundButtonCheckedChange(R.id.cbox_map)
    // public void turnToMap(View v, boolean isChecked) {
    // Intent intent = new Intent(this, WashCarActivity.class);
    // intent.putExtra("index", WashCarActivity.INDEX_FROM_LIST);
    // intent.putParcelableArrayListExtra("list",
    // (ArrayList<? extends Parcelable>) mListViewLists);
    // startActivity(intent);
    // finish();
    // }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        washcarList.clear();
//		serviceInfos.clear();
        page = 1;
        getDataFromIntent();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (null != washcarList && total <= washcarList.size()) {
            showToast("没有更多记录了");
            return;
        }
        page++;
        getDataFromIntent();
    }
}
