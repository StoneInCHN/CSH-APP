package com.cheweishi.android.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.ComponentServiceAdapter;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ComponentServiceResponse;
import com.cheweishi.android.entity.ComponentServiceShowResponse;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.widget.UnSlidingListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangce on 5/31/2016.
 */
public class MaintainComponentActivity extends BaseActivity {

    @ViewInject(R.id.left_action)
    private Button left_action;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.iv_maintain_icon)
    private ImageView iv_maintain_icon; // 图标

    @ViewInject(R.id.tv_maintain_car_mode)
    private TextView tv_maintain_car_mode; // 车型名字

    @ViewInject(R.id.tv_maintain_car_mode_name)
    private TextView tv_maintain_car_mode_name;// 具体款式名字

    @ViewInject(R.id.tv_maintain_service_name)
    private TextView tv_maintain_service_name;//服务名字

    @ViewInject(R.id.tv_maintain_choice_component)
    private TextView tv_maintain_choice_component;//选配件

    @ViewInject(R.id.usl_maintain_content)
    private UnSlidingListView usl_maintain_content;// 选配件内容

    @ViewInject(R.id.ll_maintain_component_content)
    private LinearLayout ll_maintain_component_content;// 配件内容头

    private ComponentServiceAdapter adapter;

    private List<ComponentServiceShowResponse> showData; // 组合界面需要展示的数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_component);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        left_action.setText(R.string.back);
        title.setText(R.string.car_maintain);
        XUtilsImageLoader.getxUtilsImageLoader(baseContext,
                R.drawable.home_color_car, iv_maintain_icon,
                loginResponse.getMsg().getDefaultVehicleIcon());
        tv_maintain_car_mode.setText(loginResponse.getMsg().getDefaultVehicle());
        showData = new ArrayList<>();
        adapter = new ComponentServiceAdapter(baseContext, showData);
        usl_maintain_content.setAdapter(adapter);
//        getData();
    }

    private void getData(int id) {
        ProgrosDialog.openDialog(baseContext);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_HOME_URL + NetInterface.GETSERVICEBYID + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        param.put("token", loginResponse.getToken());
        param.put("serviceId", id);
        param.put(Constant.PARAMETER_TAG, NetInterface.GETSERVICEBYID);
        netWorkHelper.PostJson(url, param, this);
    }

    @Override
    public void receive(String TAG, String data) {
        ProgrosDialog.closeProgrosDialog();
        switch (TAG) {
            case NetInterface.GETSERVICEBYID: // 获取保养的组合套餐
                ComponentServiceResponse response = (ComponentServiceResponse) GsonUtil.getInstance().convertJsonStringToObject(data, ComponentServiceResponse.class);
                if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    showToast(response.getDesc());
                    return;
                }


                loginResponse.setToken(response.getToken());
                LoginMessageUtils.saveloginmsg(baseContext, loginResponse);
                break;
        }
    }
}
