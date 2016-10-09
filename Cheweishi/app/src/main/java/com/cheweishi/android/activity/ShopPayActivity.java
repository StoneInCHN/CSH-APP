package com.cheweishi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * Created by Tanck on 9/28/2016.
 * <p/>
 * Describe: 商品支付界面
 */
public class ShopPayActivity extends BaseActivity {

    private String[] orderId; // 订单数组 : 可能是多个商家的商品一起下单的

    private String money = "0";//暂时的价钱

    private final String ALIPAY = "ALIPAY";// 阿里支付

    private final String WECHAT = "WECHAT";//微信支付

    private final String WALLET = "WALLET";//钱包支付

    private String paymentType = ALIPAY;// 支付类型

    @ViewInject(R.id.tv_shop_money)
    private TextView tv_shop_money;//显示价格

    @ViewInject(R.id.left_action)
    private Button left_action;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.img_alipay)
    private ImageView img_alipay;

    @ViewInject(R.id.img_weixin)
    private ImageView img_weixin;

    @ViewInject(R.id.img_wallet_pay)
    private ImageView img_wallet_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_pro_pay);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        left_action.setText(R.string.back);
        title.setText(R.string.wash_pay);
        String temp = getIntent().getStringExtra("orderId");
        if (StringUtil.isEmpty(temp)) {
            showToast(R.string.pay_error_notify);
//            finish();
            return;
        }
        orderId = temp.split(",");
        money = getIntent().getStringExtra("money");
        tv_shop_money.setText("￥" + money);
    }

    @OnClick({R.id.tv_shop_affirm, R.id.left_action, R.id.ll_alipay, R.id.ll_weixin, R.id.ll_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_shop_affirm: // 确认支付
                break;
            case R.id.left_action: // 返回
                finish();
                break;
            case R.id.ll_alipay: // 支付宝
                paymentType = ALIPAY;
                img_alipay.setImageResource(R.drawable.dian22x);
                img_weixin.setImageResource(R.drawable.dian12x);
                img_wallet_pay.setImageResource(R.drawable.dian12x);
                break;
            case R.id.ll_weixin: // 微信
                paymentType = WECHAT;
                img_alipay.setImageResource(R.drawable.dian12x);
                img_weixin.setImageResource(R.drawable.dian22x);
                img_wallet_pay.setImageResource(R.drawable.dian12x);
                break;
            case R.id.ll_wallet: // 钱包
                paymentType = WALLET;
                img_alipay.setImageResource(R.drawable.dian12x);
                img_weixin.setImageResource(R.drawable.dian12x);
                img_wallet_pay.setImageResource(R.drawable.dian22x);
                break;
        }
    }
}
