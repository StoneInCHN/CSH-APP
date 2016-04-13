package com.cheweishi.android.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.cheweishi.android.adapter.OrderExLvAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.OrderDetail;
import com.cheweishi.android.entity.OrderDetailResponse;
import com.cheweishi.android.entity.OrderGoods;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.QRImageUtil;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.BaiduMapView;
import com.cheweishi.android.widget.UnSlidingOrderExpandapleListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;

/**
 * 订单详情/生成订单
 *
 * @author Xiaojin
 */

public class OrderDetailsActivity extends BaseActivity implements
        OnClickListener {

    @ViewInject(R.id.lv_order)
    private UnSlidingOrderExpandapleListView listView;
    private OrderExLvAdapter adapter;
    @ViewInject(R.id.left_action)
    private Button left_action;
    @ViewInject(R.id.title)
    private TextView title;
    @ViewInject(R.id.right_action)
    private TextView right_action;
    @ViewInject(R.id.ll_succeed_name)
    private LinearLayout ll_succeed_name;
    @ViewInject(R.id.car_tv_car_iv_location)
    private TextView car_tv_car_iv_location;
    @ViewInject(R.id.imgphone)
    private ImageView imgphone;
    @ViewInject(R.id.img_nav)
    private ImageView img_nav;
    @ViewInject(R.id.car_xlocation)
    private TextView car_xlocation;
    @ViewInject(R.id.tv_reputation)
    private TextView tv_reputation;
    @ViewInject(R.id.tv_order_car)
    private TextView tv_order_car;
    private String store_id;
    private String goods_id;
    private String price;
    private MyBroadcastReceiver broad;
    @ViewInject(R.id.rl_order)
    private LinearLayout rl_order;
    @ViewInject(R.id.car_iv_location)
    private ImageView car_iv_location;
    @ViewInject(R.id.rl_success_progress)
    private RelativeLayout rl_success_progress;
    private boolean flag = false;
    @ViewInject(R.id.tv_time1_first)
    private TextView tv_time1_first;
    @ViewInject(R.id.tv_time1_second)
    private TextView tv_time1_second;
    @ViewInject(R.id.tv_order_sn)
    private TextView tv_order_sn;
    @ViewInject(R.id.img_erweima)
    private ImageView img_erweima;
    private boolean cancelFlag = false;
    @ViewInject(R.id.ll_erweima)
    private LinearLayout ll_erweima;
    @ViewInject(R.id.img_order)
    private ImageView img_order;
    @ViewInject(R.id.tv_order_class)
    private TextView tv_order_class;
    @ViewInject(R.id.img_yuyue)
    private ImageView img_yuyue;
    @ViewInject(R.id.tv_yuyue)
    private TextView tv_yuyue;
    @ViewInject(R.id.img_daodian)
    private ImageView img_daodian;
    @ViewInject(R.id.tv_daodian)
    private TextView tv_daodian;
    @ViewInject(R.id.ll_order_detail)
    private LinearLayout ll_order_detail;
    @ViewInject(R.id.tv_line_left)
    private TextView tv_line_left;
    @ViewInject(R.id.rel_ok)
    private RelativeLayout rel_ok;
    @ViewInject(R.id.img_ok)
    private ImageView img_ok;
    @ViewInject(R.id.tv_ok)
    private TextView tv_ok;
    private Bitmap qrBitmap;
    private OrderDetailResponse response;
    @ViewInject(R.id.lv_order_detail_icon)
    private LinearLayout lv_order_detail_icon;
    @ViewInject(R.id.tv_order_ok)
    private TextView tv_order_ok;
    @ViewInject(R.id.img_order_ok)
    private ImageView img_order_ok;
    @ViewInject(R.id.tv_order_paid)
    private TextView tv_order_paid; // 确认付款
    @ViewInject(R.id.tv_order_complete)
    private TextView tv_order_complete;// 订单完成
    private String chargeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ViewUtils.inject(this);
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        title.setText(R.string.order_details);
        left_action.setText(R.string.back);
        right_action.setVisibility(View.GONE);
        right_action.setText(R.string.cancel_Order);
        if (hasCar()) {
            tv_order_car.setText(loginResponse.getMsg().getDefaultVehicle());
        }

        if (!StringUtil.isEmpty(getIntent().getBundleExtra("bundle"))) {// 生成订单
            Bundle bundle = getIntent().getBundleExtra("bundle");
            store_id = bundle.getString("store_id");
            goods_id = bundle.getString("goods_id");
            price = bundle.getString("price");
            flag = true;
        } else {// 订单详情
            flag = false;
        }

        connectToServer();
    }

    @OnClick({R.id.left_action, R.id.right_action, R.id.img_nav, R.id.imgphone})
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.left_action:
                finish();
                break;
            case R.id.right_action:// 取消订单
                cancelOrder();
                break;
            case R.id.img_nav:// 导航
                turnToNav();
                break;
            case R.id.imgphone:// 拨打电话
                turnToPhone();
                break;
            default:
                break;
        }
    }

    /**
     * 拨打电话
     */
    public void turnToPhone() {
        Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                + response.getMsg().getTenantInfo().getContactPhone()));
        tel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(tel);
    }

    /**
     * 导航
     */
    private void turnToNav() {
        BaiduMapView baiduMapView = new BaiduMapView();
        baiduMapView.initMap(this);
        baiduMapView.baiduNavigation(MyMapUtils.getLatitude(this),
                MyMapUtils.getLongitude(this), MyMapUtils.getAddress(this),
                response.getMsg().getTenantInfo().getLatitude(), response.getMsg().getTenantInfo().getLongitude(),
                response.getMsg().getTenantInfo().getAddress());
    }

    /**
     * 取消订单
     */
    private void cancelOrder() {
        Intent intent = new Intent();
        intent.setClass(OrderDetailsActivity.this, MaintainCancelActivity.class);
//        intent.putExtra("orderId", orderDetail.getOrderId());
        startActivity(intent);
    }

    /**
     * 请求服务器
     */
    private void connectToServer() {
        String recordId = getIntent().getStringExtra("recordId");
        if (null != recordId) {
            ProgrosDialog.openDialog(this);
            String url = NetInterface.BASE_URL + NetInterface.TEMP_ORDER + NetInterface.ORDER_DETIAL + NetInterface.SUFFIX;
            LogHelper.d("url" + url);
            Map<String, Object> param = new HashMap<>();
            param.put("userId", loginResponse.getDesc());
            param.put("token", loginResponse.getToken());
            param.put("recordId", recordId);
            netWorkHelper.PostJson(url, param, this);
        }

    }


    @Override
    public void receive(String data) {
        ProgrosDialog.closeProgrosDialog();
        response = (OrderDetailResponse) GsonUtil.getInstance().convertJsonStringToObject(data, OrderDetailResponse.class);
        if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
            showToast(response.getDesc());
            return;
        }

        setValues();
        loginResponse.setToken(response.getToken());
        LoginMessageUtils.saveloginmsg(baseContext, loginResponse);
    }

    @Override
    public void error(String errorMsg) {
        ProgrosDialog.closeProgrosDialog();
        showToast(R.string.server_link_fault);
    }

    /**
     * 接收服务器返回数据
     */
    @Override
    public void receive(int type, String data) {
        // TODO Auto-generated method stub
        super.receive(type, data);
        ProgrosDialog.closeProgrosDialog();
        switch (type) {
            case 10001:// 服务器连接成功
                parseJSON(data);
                break;
            case 400:// 服务器连接失败
                showToast(R.string.server_link_fault);
                break;
        }
    }

    private void parseJSON(String result) {
        if (StringUtil.isEmpty(result)) {// 返回数据出错
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (StringUtil.isEquals(jsonObject.optString("state"),
                    API.returnSuccess, true)) {
                rl_order.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<OrderDetail>() {
                }.getType();
//                orderDetail = gson.fromJson(jsonObject.optString("data"), type);
//                setValues();
            } else if (StringUtil.isEquals(jsonObject.optString("state"),
                    API.returnRelogin, true)) {
                ReLoginDialog.getInstance(this).showDialog(
                        jsonObject.optString("message"));
            } else {
                showToast(jsonObject.optString("message"));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 格式化日期
     *
     * @param dateStr
     * @return
     */
    private String formateDate(String dateStr) {
        if (!dateStr.equals("null")) {
            DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.CHINA);
            Date date = null;
            try {
                date = sf.parse(dateStr);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            DateFormat sf1 = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
            if (null != date) {
                String str = sf1.format(date);
                return str;
            }
        }
        return "";
    }

    private void setValues() {
        car_tv_car_iv_location.setText(response.getMsg().getTenantInfo().getTenantName());
        car_xlocation.setText(response.getMsg().getTenantInfo().getAddress());
        // tv_reputation.setText(orderDetail.getReputation());
        tv_reputation.setText("");
        tv_order_sn.setText(response.getMsg().getRecordNo());
        XUtilsImageLoader.getxUtilsImageLoader(this, R.drawable.zhaochewei_img,
                car_iv_location, response.getMsg().getTenantInfo().getPhoto());


        ll_order_detail.removeAllViews();
        // TODO 添加钻石
        for (int i = 0; i < response.getMsg().getTenantInfo().getPraiseRate(); i++) {
            ImageView imageView = new ImageView(baseContext);
            imageView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            imageView.setPadding(0, 0, 2, 0);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource(R.drawable.haoping);
            ll_order_detail.addView(imageView);
        }

        chargeStatus = response.getMsg().getTenantInfo().getChargeStatus();
        progress_pay_statue(chargeStatus);
        adapter = new OrderExLvAdapter(this, response);

        // TODO 条形码先不管.

//        if (!StringUtil.isEmpty(orderDetail.getBarcodes())) {
//            ll_erweima.setVisibility(View.VISIBLE);
//            qrBitmap = QRImageUtil.createQRImage(orderDetail.getBarcodes(),
//                    OrderDetailsActivity.this);
//            img_erweima.setImageBitmap(qrBitmap);
//        }
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (null != qrBitmap) {
            // 回收并且置为null
            qrBitmap.recycle();
            qrBitmap = null;
        }
        System.gc();
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

    public class MyBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
                    Constant.CANCEL_ORDER_SUCCESS_REFRESH, true)) {
                cancelFlag = true;
                connectToServer();

            }
        }
    }

    /**
     * 进行中、已完成
     *
     * @param res_str
     */
    private void green_img_order(int res_str) {
        img_order.setImageResource(R.drawable.dingdanxiangqing_chenggong2x);
        tv_order_class.setText(res_str);
    }

    private void green_img_order(String res_str) {
        img_order.setImageResource(R.drawable.dingdanxiangqing_chenggong2x);
        tv_order_class.setText(res_str);
    }

    private void red_img_order(int res_str) {
        img_order.setImageResource(R.drawable.dingdanxiangqing_cancel2x);
        tv_order_class.setText(res_str);
    }


    private void red_img_order(String res_str) {
        img_order.setImageResource(R.drawable.dingdanxiangqing_cancel2x);
        tv_order_class.setText(res_str);
    }

    private void progress_pay_statue(String str) {
        if (null == str) {
            str = getIntent().getStringExtra("chargeStatus");
            if (null == str)
                return;
        }
        /*** 预约中
         RESERVATION,
         预约成功
         RESERVATION_SUCCESS,
         预约失败
         RESERVATION_FAIL,
         未支付
         UNPAID,
         已支付
         PAID,
         完成
         FINISH,
         过期
         OVERDUE,
         */

        lv_order_detail_icon.setVerticalGravity(View.VISIBLE);
        if (1 == response.getMsg().getServiceFlag()) {
            switch (str) {
                case "RESERVATION": // 预约
                    img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
                    img_daodian.setImageResource(R.drawable.dingdanxiangqing_baoyang2x);
                    tv_yuyue.setText("预约下单");
                    tv_daodian.setText("到店服务");
                    green_img_order(R.string.order_win);
                    tv_daodian.setTextColor(getResources().getColor(R.color.gray));
                    tv_time1_first.setText(formateDate(String.valueOf(response.getMsg().getCreateDate())));
                    tv_time1_second.setText("");
                    break;
                case "RESERVATION_SUCCESS": // 预约成功
                    img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
                    img_daodian.setImageResource(R.drawable.dingdanxiangqing_quxiao2x);
                    tv_yuyue.setText("预约成功");
                    tv_daodian.setText("到店服务");
                    green_img_order(R.string.order_details_re);
                    tv_daodian.setTextColor(getResources().getColor(R.color.gray));
                    tv_time1_first.setText(formateDate(formateDate(String.valueOf(response.getMsg().getCreateDate()))));
                    tv_time1_second.setText("");
                    break;

                case "RESERVATION_FAIL":// 预约失败
                    img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
                    img_daodian.setImageResource(R.drawable.dingdanxiangqing_quxiao2x);
                    tv_yuyue.setText("预约下单");
                    tv_daodian.setText("订单取消");
                    red_img_order("预约失败,您可以选择新的时间段进行重新预约");
                    tv_daodian.setTextColor(getResources().getColor(R.color.order_dr));
                    tv_time1_first.setText(formateDate(formateDate(String.valueOf(response.getMsg().getCreateDate()))));
                    tv_time1_second.setText("");
                    break;
                case "UNPAID": // 未支付
                    img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
                    img_daodian.setImageResource(R.drawable.dingdanxiangqing_baoyang1);
                    tv_yuyue.setText("预约成功");
                    tv_daodian.setText("完成服务");
                    green_img_order("您已完成服务,请确认支付信息");
                    tv_daodian.setTextColor(getResources().getColor(R.color.order_dr));
                    tv_time1_first.setText(formateDate(formateDate(String.valueOf(response.getMsg().getCreateDate()))));
                    tv_time1_second.setVisibility(View.VISIBLE);
                    tv_time1_second.setText(formateDate(formateDate(String.valueOf(response.getMsg().getSubscribeDate()))));
                    break;
                case "PAID":  //已支付
                    img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
                    img_daodian.setImageResource(R.drawable.dingdanxiangqing_baoyang1);
                    img_ok.setImageResource(R.drawable.dingdanxiangqing_pay1);
                    tv_ok.setTextColor(getResources().getColor(R.color.order_dr));
                    tv_yuyue.setText("预约成功");
                    tv_daodian.setText("完成服务");
                    green_img_order(R.string.order_paid);
                    tv_daodian.setTextColor(getResources().getColor(R.color.order_dr));
                    tv_time1_first.setText(formateDate(String.valueOf(response.getMsg().getCreateDate())));
                    tv_time1_second.setVisibility(View.VISIBLE);
                    tv_time1_second.setText(formateDate(String.valueOf(response.getMsg().getSubscribeDate())));
                    tv_order_paid.setVisibility(View.VISIBLE);
                    tv_order_paid.setText(formateDate(String.valueOf(response.getMsg().getPayDate())));
                    break;

                case "FINISH": // 订单完成
                    img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
                    img_daodian.setImageResource(R.drawable.dingdanxiangqing_baoyang1);
                    img_ok.setImageResource(R.drawable.dingdanxiangqing_pay1);
                    img_order_ok.setImageResource(R.drawable.dingdanxiangqing_pay1);
                    tv_ok.setTextColor(getResources().getColor(R.color.order_dr));
                    tv_order_ok.setTextColor(getResources().getColor(R.color.order_dr));
                    tv_yuyue.setText("预约成功");
                    tv_daodian.setText("完成服务");
                    green_img_order(R.string.order_win_complete);
                    tv_daodian.setTextColor(getResources().getColor(R.color.order_dr));
                    tv_time1_first.setText(formateDate(String.valueOf(response.getMsg().getCreateDate())));
                    tv_time1_second.setVisibility(View.VISIBLE);
                    tv_time1_second.setText(formateDate(String.valueOf(response.getMsg().getSubscribeDate())));
                    tv_order_paid.setVisibility(View.VISIBLE);
                    tv_order_paid.setText(formateDate(String.valueOf(response.getMsg().getPayDate())));
                    tv_order_complete.setVisibility(View.VISIBLE);
                    tv_order_complete.setText(formateDate(String.valueOf(response.getMsg().getFinishDate())));
                    break;
            }
        } else {
            // TODO 非预约类型
            LogHelper.d("非预约类型");
        }


//        if (StringUtil.isEquals(str, "0", true)) {// 订单已取消(支付流程)
//            img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
//            img_daodian.setImageResource(R.drawable.dingdanxiangqing_quxiao2x);
//            tv_yuyue.setText("预约下单");
//            tv_daodian.setText("已取消");
//            red_img_order(R.string.order_cancel_oen);
//            tv_daodian.setTextColor(getResources().getColor(R.color.order_dr));
//            tv_time1_first.setText(formateDate(orderDetail.getAdd_time()));
//            tv_time1_second
//                    .setText(formateDate(orderDetail.getFinished_time()));
//        } else if (StringUtil.isEquals(str, "1", true)) {// 确认付款(支付流程)
//            img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
//            img_daodian.setImageResource(R.drawable.dingdanxiangqing_baoyang2x);
//            tv_yuyue.setText("预约下单");
//            tv_daodian.setText("到店洗车");
//            green_img_order(R.string.order_not_pay);
//            tv_daodian.setTextColor(getResources().getColor(R.color.gray));
//            tv_time1_first.setText(formateDate(orderDetail.getAdd_time()));
//            tv_time1_second.setText("");
//        } else if (StringUtil.isEquals(str, "12", true)) {// 订单进行中(支付流程)
//            img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
//            img_daodian.setImageResource(R.drawable.dingdanxiangqing_baoyang2x);
//            tv_yuyue.setText("预约下单");
//            tv_daodian.setText("到店洗车");
//            green_img_order(R.string.order_win_middle);
//            tv_daodian.setTextColor(getResources().getColor(R.color.gray));
//            tv_time1_first.setText(formateDate(orderDetail.getAdd_time()));
//            tv_time1_second.setText("");
//        } else if (StringUtil.isEquals(str, "3", true)) {// 订单完成(支付流程)
//            img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
//            img_daodian.setImageResource(R.drawable.dingdanxiangqing_baoyang1);
//            tv_yuyue.setText("预约下单");
//            tv_daodian.setText("到店洗车");
//            green_img_order(R.string.order_win_complete);
//            tv_daodian.setTextColor(getResources().getColor(R.color.order_dr));
//            tv_time1_first.setText(formateDate(orderDetail.getAdd_time()));
//            tv_time1_second
//                    .setText(formateDate(orderDetail.getFinished_time()));
//        } else if (StringUtil.isEquals(str, "4", true)) {// 过期(支付流程)
//            img_yuyue.setImageResource(R.drawable.dingdanxiangqing_timexxx2xx);
//            img_daodian.setImageResource(R.drawable.dingdanxiangqing_quxiao2x);
//            tv_yuyue.setText("预约下单");
//            tv_daodian.setText("已过期");
//            red_img_order(R.string.order_out_of_dateline);
//            tv_daodian.setTextColor(getResources().getColor(R.color.order_dr));
//            tv_time1_first.setText(formateDate(orderDetail.getAdd_time()));
//            tv_time1_second
//                    .setText(formateDate(orderDetail.getFinished_time()));
//        }
    }

}
