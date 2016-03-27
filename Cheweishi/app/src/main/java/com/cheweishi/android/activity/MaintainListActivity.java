package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.MaintainListNewAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.MainSellerInfo;
import com.cheweishi.android.entity.MainSellerServiceInfo;
import com.cheweishi.android.entity.MaintainResponse;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnCompoundButtonCheckedChange;

/**
 * 保养列表
 *
 * @author mingdasen
 */
public class MaintainListActivity extends BaseActivity implements OnRefreshListener2<ListView> {

    @ViewInject(R.id.title)
    private TextView tvTitle;
    @ViewInject(R.id.left_action)
    private Button btnLeft;
    @ViewInject(R.id.listview)
    private PullToRefreshListView mListView;
    @ViewInject(R.id.lay_wash_beauty)
    private LinearLayout lay_wash_beauty;
    private List<MainSellerInfo> maintainList;
    //	private List<LatLng> positionList;
    private MaintainListNewAdapter mListViewAdapter;
    //	private List<WashcarVO> mListViewLists;
    @ViewInject(R.id.tv_maintain_myCar)
    private TextView tv_maintain_myCar;
    private MyBroadcastReceiver broad;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_beauty_list);
        ViewUtils.inject(this);
        /**/
        initView();
        getDataFromIntent();
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

    private void getDataFromIntent() {
        // mListViewLists = getIntent().getParcelableArrayListExtra("list");
        if (isLogined()) {
            ProgrosDialog.openDialog(this);
            String url = NetInterface.BASE_URL + NetInterface.TEMP_HOME_URL + NetInterface.MAINTAIN + NetInterface.SUFFIX;
            Map<String,Object> param = new HashMap<>();
            param.put("userId",loginResponse.getDesc());
            param.put("token",loginResponse.getToken());
            param.put("serviceCategoryId",1); // 保养
            netWorkHelper.PostJson(url,param,this);
        }
    }

    @Override
    public void receive(String data) {
        ProgrosDialog.closeProgrosDialog();
        mListView.onRefreshComplete();
        MaintainResponse response = (MaintainResponse) GsonUtil.getInstance().convertJsonStringToObject(data,MaintainResponse.class);
        if(!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)){
            showToast(response.getDesc());
            return;
        }



//        maintainList = response.getMsg().getCarServices()
        if (StringUtil.isEmpty(maintainList) || maintainList.size() <= 0) {
            EmptyTools.setEmptyView(this, mListView);
            EmptyTools.setImg(R.drawable.dingdanwu_icon);
            EmptyTools.setMessage("亲，暂无相关数据");
        } else {
            mListViewAdapter.setData(maintainList);
        }
        loginResponse.setToken(response.getToken());
        LoginMessageUtils.saveloginmsg(baseContext,loginResponse);
    }

    @Override
    public void receive(int type, String data) {
        ProgrosDialog.closeProgrosDialog();
        mListView.onRefreshComplete();
        switch (type) {
            case 1001:
                doAboutMaintain(data);
                break;

            default:
                break;
        }
    }

    /**
     * 解析保养列表数据
     *
     * @param data
     */
    private void doAboutMaintain(String data) {
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
                    List<MainSellerServiceInfo> serviceInfos = new ArrayList<MainSellerServiceInfo>();
                    MainSellerServiceInfo serviceInfo = null;
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
                        serviceInfo.setDescribe(object.optString("description"));
                        serviceInfo.setCate_id_2(object.optString("cate_id_2"));
                        serviceInfos.add(serviceInfo);
                    }
                    // Log.i("result", "===serviceInfos==" +
                    // serviceInfos.size());
                    sellerInfo.setServices(serviceInfos);
                    maintainList.add(sellerInfo);
                }
                if (StringUtil.isEmpty(maintainList) || maintainList.size() <= 0) {
                    EmptyTools.setEmptyView(this, mListView);
                    EmptyTools.setImg(R.drawable.dingdanwu_icon);
                    EmptyTools.setMessage("亲，暂无相关数据");
                } else {
                    mListViewAdapter.setData(maintainList);
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

    private void initView() {
        tvTitle.setText("预约保养");
        btnLeft.setText(R.string.back);
        if (hasCar()) {

            tv_maintain_myCar.setText(loginResponse.getMsg().getDefaultVehicle());
            //  TODO 原本有系类
//			tv_maintain_myCar.setText(loginMessage.getCarManager().getBrand().getBrandName()+
//					" "+ loginMessage.getCarManager().getBrand().getSeriesName());
        }
        mListView.setMode(Mode.BOTH);
        mListView.setOnRefreshListener(this);
        maintainList = new ArrayList<MainSellerInfo>();
        mListViewAdapter = new MaintainListNewAdapter(this, maintainList);
        mListView.setAdapter(mListViewAdapter);
    }

    @OnClick({R.id.left_action, R.id.tvHistory, R.id.lay_wash_beauty})
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
            case R.id.lay_wash_beauty:
                startActivity(new Intent(MaintainListActivity.this, CarManagerActivity.class));
                break;
            default:
                break;
        }
    }

//	@OnCompoundButtonCheckedChange(R.id.cbox_map)
//	public void turnToMap(View v, boolean isChecked) {
//		Intent intent = new Intent(this, WashCarActivity.class);
//		intent.putExtra("index", WashCarActivity.INDEX_FROM_LIST);
//		intent.putParcelableArrayListExtra("list",
//				(ArrayList<? extends Parcelable>) mListViewLists);
//		startActivity(intent);
//		finish();
//	}

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (!StringUtil.isEquals(intent.getAction(), Constant.REFRESH_FLAG,
                    true)) {
                return;
            }
            if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
                    Constant.CAR_MANAGER_REFRESH, true)) {
                Log.i("result", "===========MaintainListActivity保养保养保养保养保养保养保养保养================" + loginMessage.getCarManager().getBrand() + "_" + loginMessage.getCarManager().getSeriesName());
                Constant.EDIT_FLAG = true;
                initView();
                getDataFromIntent();


//			} else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
//					Constant.USER_NICK_EDIT_REFRESH, true)) {
//				Constant.EDIT_FLAG = true;
//				// initViews();
//			} else if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
//					Constant.USER_NICK_EDIT_REFRESH_OTHER, true)) {
//				// connectToServer();
            }
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        maintainList.clear();
//		serviceInfos.clear();
        page = 1;
        getDataFromIntent();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        page++;
        getDataFromIntent();
    }
}
