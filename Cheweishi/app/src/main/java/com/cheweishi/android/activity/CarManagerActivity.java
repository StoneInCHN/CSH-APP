package com.cheweishi.android.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.adapter.CarManagerAdapter;
import com.cheweishi.android.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.entity.LoginUserInfoResponse;
import com.cheweishi.android.entity.MyCarManagerResponse;
import com.cheweishi.android.response.BaseResponse;
import com.cheweishi.android.tools.DBTools;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.ButtonUtils;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.SwipeListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xiaojin车辆管理
 */
public class CarManagerActivity extends BaseActivity implements
        OnClickListener,
        OnItemClickListener {

    private SwipeListView listView_carManager;// 滑动列表
    @ViewInject(R.id.left_action)
    private Button left_action;
    @ViewInject(R.id.title)
    private TextView title;
    @ViewInject(R.id.right_action)
    private TextView right_action;
    private CarManagerAdapter adapter;
    private List<MyCarManagerResponse.MsgBean> listCarManager = new ArrayList<MyCarManagerResponse.MsgBean>();
    private List<MyCarManagerResponse.MsgBean> listCarManagerTemp;
    MyCarManagerResponse.MsgBean carManagerItem = null;
    @ViewInject(R.id.ll_head)
    private LinearLayout ll_head;
    private int itemIndex;
    private int currentDefaultIndex;
    private MyBroadcastReceiver broad;
    //    public static CarManagerActivity instance;
    private String DefaultName = "";
    private String DefaultPlate = "";
    private String DefaultIcon = "";
    private String DefaultNo = "";

    @ViewInject(R.id.vp_car_manager)
    private ViewPager vp_car_manager;
    private static final float DEFAULT_MIN_ALPHA = 0.5f;
    private float mMinAlpha = DEFAULT_MIN_ALPHA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fr_list_carmanager);
        ViewUtils.inject(this);
//        initViews();
//        setListeners();

        CarManagerAdapter adapter = new CarManagerAdapter(baseContext, null);
        vp_car_manager.setAdapter(adapter);
        vp_car_manager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                if (position < -1) {
                    view = view.findViewById(R.id.ll_car_manager_item_head);
                    ViewHelper.setAlpha(view, mMinAlpha);
                    ViewHelper.setX(view, view.getWidth() / 2);
                } else if (position <= 1) { // [-1,1]

                    if (position < 0) //[0，-1]
                    {
                        float factor = mMinAlpha + (1 - mMinAlpha) * (1 + position);
                        view = view.findViewById(R.id.ll_car_manager_item_head);
                        ViewHelper.setAlpha(view, factor);
                        ViewHelper.setScaleX(view, factor);
                        ViewHelper.setScaleY(view, factor);
                        ViewHelper.setX(view, factor);
                    } else//[1，0]
                    {
                        float factor = mMinAlpha + (1 - mMinAlpha) * (1 - position);
                        view = view.findViewById(R.id.ll_car_manager_item_head);
                        ViewHelper.setAlpha(view, factor);
                        ViewHelper.setScaleX(view, factor);
                        ViewHelper.setScaleY(view, factor);
                        ViewHelper.setX(view, factor);
                    }
                } else { // (1,+Infinity]
                    int wid = view.getWidth();
                    view = view.findViewById(R.id.ll_car_manager_item_head);
                    ViewHelper.setAlpha(view, mMinAlpha);
                    ViewHelper.setX(view, wid - view.getWidth() / 2);
                }
            }
        });

    }

    private void initViews() {
        title.setText("我的车辆");
        left_action.setText(R.string.back);
        right_action.setText(R.string.button_add);
        right_action.setVisibility(View.GONE);
//        instance = this;
//        adapter = new CarManagerAdapter(this, listCarManager,
//                listView_carManager.getRightViewWidth());
//        listView_carManager.setAdapter(adapter);
//        adapter.setOnRightItemClickListener(this);
        listView_carManager.setSelector(new ColorDrawable(Color.TRANSPARENT));
        connectToServer();
    }

    private void setListeners() {
        listView_carManager.setOnItemClickListener(this);
    }

    //    @OnClick({R.id.listView_front, R.id.left_action, R.id.right_action})
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.left_action:
                finish();
                break;
            case R.id.right_action:
                Intent intent = new Intent(this, AddCarActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (broad == null) {
            broad = new MyBroadcastReceiver();
        } else {
            reconnect();
        }

        IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
        registerReceiver(broad, intentFilter);
    }

//    @Override
//    public void onRightItemClick(View v, int position) {
//        if (ButtonUtils.isFastClick()) {
//            return;
//        } else {
//            if (listCarManager.size() > position) {
//                Intent intent = new Intent(this, AddCarActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("car", listCarManager.get(position));
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        }
//    }

    //    @OnItemClick({R.id.listView_carManager})
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        LogHelper.d(arg2 + "----" + currentDefaultIndex);
        if (ButtonUtils.isFastClick()) {
            return;
        } else if (currentDefaultIndex != arg2) {
            itemIndex = arg2;
            carManagerItem = listCarManager.get(itemIndex);
            // if ((carManagerItem.getFeed()) == 0) {
            ConnectItemToServer(listCarManager.get(itemIndex));
            // } else {
            // // if (StringUtil.isEmpty(listCarManager.get(itemIndex)
            // // .getDevice())) {
            // ConnectItemToServer(listCarManager.get(itemIndex).getCid());
            // // } else {
            // // Intent pageIntent = new Intent(mContext,
            // // CarManagerBindActivity.class);
            // // Bundle bundle = new Bundle();
            // // bundle.putSerializable("car", carManagerItem);
            // // pageIntent.putExtra("currentCid", carManagerItem.getCid());
            // // pageIntent.putExtra("CarManagerBindActivity", 1000);
            // // pageIntent.putExtras(bundle);
            // // startActivity(pageIntent);
            // // }
            // }
        }
    }

    /**
     * 请求车辆列表
     */
    private void connectToServer() {
        if (isLogined()) {
            ProgrosDialog.openDialog(baseContext);
            String url = NetInterface.BASE_URL + NetInterface.TEMP_CAR_URL + NetInterface.LIST + NetInterface.SUFFIX;
            Map<String, Object> param = new HashMap<>();
            param.put("userId", loginResponse.getDesc());
            param.put("token", loginResponse.getToken());
            param.put(Constant.PARAMETER_TAG, NetInterface.LIST);
            netWorkHelper.PostJson(url, param, this);
        }
    }

    /**
     * 请求设置默认车辆服务器
     */
    private void ConnectItemToServer(MyCarManagerResponse.MsgBean msgBean) {
        ProgrosDialog.openDialog(this);
        if ((isLogined())) {

            //  TODO 暂时没有判断
//                ||
//
//                 null == loginResponse.getMsg().getDefaultVehiclePlate())
//                || !loginResponse.getMsg().getDefaultVehiclePlate().equals(msgBean.getPlate())
//                || !loginResponse.getMsg().getDefaultVehicle().equals(msgBean.getVehicleFullBrand())) {

            ProgrosDialog.openDialog(baseContext);
            String url = NetInterface.BASE_URL + NetInterface.TEMP_CAR_URL + NetInterface.SET_DEFAULT_DEVICE + NetInterface.SUFFIX;
            Map<String, Object> param = new HashMap<>();
            param.put("userId", loginResponse.getDesc());
            param.put("token", loginResponse.getToken());
            param.put("vehicleId", msgBean.getId());
            param.put(Constant.PARAMETER_TAG, NetInterface.SET_DEFAULT_DEVICE);
            netWorkHelper.PostJson(url, param, this);

            // TODO 更新UI和缓存.
            DefaultName = msgBean.getVehicleFullBrand();
            DefaultPlate = msgBean.getPlate();
            DefaultIcon = msgBean.getBrandIcon();
            DefaultNo = msgBean.getDeviceNo();
        } else {
            ProgrosDialog.closeProgrosDialog();
        }
    }


    @Override
    public void receive(String TAG, String data) {
        ProgrosDialog.closeProgrosDialog();
        switch (TAG) {
            case NetInterface.LIST:
                MyCarManagerResponse response = (MyCarManagerResponse) GsonUtil.getInstance().convertJsonStringToObject(data, MyCarManagerResponse.class);
                if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    showToast(response.getDesc());
                    return;
                }
                listCarManagerTemp = response.getMsg();
                listCarManager.clear();
                listCarManager.addAll(listCarManagerTemp);
                if (listCarManager.size() > 0) {
                    ll_head.setVisibility(View.GONE);
                } else {
                    EmptyTools.setEmptyView(this, listView_carManager);
                    EmptyTools.setImg(R.drawable.mycar_icon);
                    EmptyTools.setMessage("您还没有添加车辆");
                }
                listView_carManager.hiddenRight(null);
                listView_carManager.requestLayout();

                for (int i = 0; i < listCarManager.size(); i++) { // 寻找默认车辆的索引
                    if (listCarManager.get(i).isDefault()) {
                        currentDefaultIndex = i;
                        setMainIcon(listCarManager.get(i).getBrandIcon());
                        break;
                    }
                }
//                adapter.setData(listCarManager);
//                listView_front.setVisibility(View.INVISIBLE);
                if (listCarManager.size() >= 3) {
                    right_action.setVisibility(View.GONE);
                } else {
                    right_action.setVisibility(View.VISIBLE);
                }

                loginResponse.setToken(response.getToken());
                LoginMessageUtils.saveloginmsg(baseContext, loginResponse);
                break;
            case NetInterface.SET_DEFAULT_DEVICE:

                MyCarManagerResponse baseResponse = (MyCarManagerResponse) GsonUtil.getInstance().convertJsonStringToObject(data, MyCarManagerResponse.class);
                if (!baseResponse.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    showToast(baseResponse.getDesc());
                    return;
                }

                for (int i = 0; i < listCarManager.size(); i++) {
                    listCarManager.get(i).setDefault(false);
                }
                currentDefaultIndex = itemIndex;
                listCarManager.get(itemIndex).setDefault(true);
                String temp = baseResponse.getDesc();
                if (null != temp && !"".equals(temp)) {
                    // 设置主页标题
                    setTitle(baseResponse.getDesc());
                } else {
                    // 设置主页标题
                    setTitle("车生活");
                }
                //设置主页车辆图标
                setMainIcon(DefaultIcon);
//                adapter.setData(listCarManager);
//                listView_front.setVisibility(View.INVISIBLE);
                if (listCarManager.size() >= 3) {
                    right_action.setVisibility(View.GONE);
                } else {
                    right_action.setVisibility(View.VISIBLE);
                }
                loginResponse.getMsg().setDefaultVehicle(DefaultName);
                loginResponse.getMsg().setDefaultVehicleIcon(DefaultIcon);
                loginResponse.getMsg().setDefaultVehiclePlate(DefaultPlate);
                loginResponse.getMsg().setDefaultDeviceNo(DefaultNo);
                loginResponse.getMsg().setDefaultVehicleId("" + baseResponse.getMsg().get(currentDefaultIndex).getId());
                loginResponse.setToken(baseResponse.getToken());
                LoginMessageUtils.saveloginmsg(baseContext, loginResponse);


                Intent intent = new Intent();
                intent.setAction(Constant.REFRESH_FLAG);
                Constant.CURRENT_REFRESH = Constant.CAR_MANAGER_REFRESH;
                sendBroadcast(intent);
                break;
        }
    }

    private void setMainIcon(String defaultIcon) {
        XUtilsImageLoader.getxUtilsImageLoader(this,
                R.drawable.tianjiacar_img2x, MainNewActivity.ibtn_user,
                defaultIcon);
    }

    @Override
    public void error(String errorMsg) {
        ProgrosDialog.closeProgrosDialog();
        showToast(R.string.server_link_fault);
    }


    /**
     * 发送广播，更新因为车辆管理而变化的Activity
     */
    private void judgeCurrentRefreahGoBack() {
        Constant.CURRENT_REFRESH = Constant.CAR_MANAGER_REFRESH;
        Intent mIntent = new Intent();
        mIntent.setAction(Constant.REFRESH_FLAG);
        sendBroadcast(mIntent);
    }


    /**
     * 重新链接
     */
    public void reconnect() {
//        listView_front.setVisibility(View.VISIBLE);
        if (listCarManagerTemp != null) {
            listCarManagerTemp.clear();
//            adapter.setNullUpdateUi(listCarManagerTemp);
        }
        connectToServer();
    }

    public void setListVisible(boolean flag) {
        if (flag == false) {
            listView_carManager.setVisibility(View.GONE);
        } else {
            listView_carManager.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listCarManager != null) {
            listCarManager.clear();
        }
        if (listCarManagerTemp != null) {
            listCarManagerTemp.clear();
        }
        unregisterReceiver(broad);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
                    Constant.CAR_MANAGER_REFRESH, true)) {
//                reconnect();

            }
        }
    }

    @Override
    public void dealCallBackFromAdapter(int pos, Object obj) {
        super.dealCallBackFromAdapter(pos, obj);
        if (ButtonUtils.isFastClick()) {
            return;
        } else if (currentDefaultIndex != pos) {

            itemIndex = pos;
            carManagerItem = listCarManager.get(itemIndex);
            ConnectItemToServer(listCarManager.get(itemIndex));

        }
    }
}
