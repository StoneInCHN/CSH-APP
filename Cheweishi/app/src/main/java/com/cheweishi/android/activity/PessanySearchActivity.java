package com.cheweishi.android.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.PessanyAdapter;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.PessanyResponse;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.GsonUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 违章查询
 *
 * @author Xiaojin
 */
@ContentView(R.layout.activity_pessany_search)
public class PessanySearchActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener {
    @ViewInject(R.id.left_action)
    private Button left_action;
    @ViewInject(R.id.title)
    private TextView title;
    @ViewInject(R.id.right_action)
    private TextView right_action;
    @ViewInject(R.id.lv_pessanySearch)
    private ListView lv_pessanySearch;
    private PessanyAdapter adapter;
    private List<PessanyResponse.MsgBean> listPessanySearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        initViews();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        title.setText(R.string.title_activity_pessany_search);
        left_action.setText(R.string.back);
        getData();
    }

    private void getData() {
        ProgrosDialog.openDialog(baseContext);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_RECORD + NetInterface.ILLEGAL_RECORD + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        param.put("token", loginResponse.getToken());
        param.put("plate", loginResponse.getMsg().getDefaultVehiclePlate());
        netWorkHelper.PostJson(url, param, this);
    }

    @Override
    public void receive(String data) {
        ProgrosDialog.closeProgrosDialog();
        PessanyResponse response = (PessanyResponse) GsonUtil.getInstance().convertJsonStringToObject(data, PessanyResponse.class);
        if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
            showToast(response.getDesc());
            return;
        }

        // 填充数据
        if (null != response.getMsg() && 0 < response.getMsg().size()) {
            listPessanySearch = response.getMsg();
            adapter = new PessanyAdapter(this, listPessanySearch);
            lv_pessanySearch.setAdapter(adapter);
        } else {
            EmptyTools.setEmptyView(baseContext, lv_pessanySearch);
            EmptyTools.setImg(R.drawable.mycar_icon);
            EmptyTools.setMessage("您还有没有违章记录");
        }
        loginResponse.setToken(response.getToken());
        LoginMessageUtils.saveloginmsg(baseContext, loginResponse);
    }

    @OnClick({R.id.left_action})
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.left_action:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent(this, PessanySearchDetailActivity.class);
        intent.putExtra("PessanySearch", listPessanySearch.get(arg2));
        startActivity(intent);
    }

}
