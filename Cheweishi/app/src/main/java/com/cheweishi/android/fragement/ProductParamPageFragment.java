package com.cheweishi.android.fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.ProductParamAdapter;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ProductDetailResponse;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.UnSlidingListView;
import com.zzhoujay.richtext.ImageFixCallback;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;

/**
 * Created by tangce on 8/22/2016.
 */
public class ProductParamPageFragment extends BaseFragment implements ImageFixCallback {
    private boolean isPrepared;

    public static final String ARG_PAGE = "ARG_PAGE";

    public static final String ARG_ID = "ARG_ID";

    public static final String ARG_DATA = "ARG_DATA";

    private int mPage; // 当前位置

    private String currentId;//当前标示

    private boolean isRichLoaded, isParamLoaded, isCommentLoaded;//是否加载过

    private TextView rt_param;//富文本

    private UnSlidingListView usl_param;//参数List

    private ProductParamAdapter adapter;

    private ProductDetailResponse response;

    private RichText richText;

    public static ProductParamPageFragment newInstance(int page, String id, ProductDetailResponse data) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(ARG_ID, id);
        args.putSerializable(ARG_DATA, data);
        ProductParamPageFragment pageFragment = new ProductParamPageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        currentId = getArguments().getString(ARG_ID);
        response = (ProductDetailResponse) getArguments().getSerializable(ARG_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        switch (mPage) {
            case 0:// 图文详情
                view = inflater.inflate(R.layout.fragment_richtext, container, false);
                rt_param = (TextView) view.findViewById(R.id.rt_param);
                break;
            case 1:// 产品参数
                view = inflater.inflate(R.layout.fragment_param, container, false);
                usl_param = (UnSlidingListView) view.findViewById(R.id.usl_param);
                break;
            case 2://用户评价
                view = inflater.inflate(R.layout.fragment_comment, container, false);
                break;
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPrepared = true;
        onVisible();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if (!isPrepared || !isVisible) {//|| isLoaded
            return;
        }
//        LogHelper.d("onVisible:" + isVisible);
        loading.sendEmptyMessage(0x616);
    }

    @Override
    public void onDataLoading(int what) {
        if (0x616 == what) {
            switch (mPage) {
                case 0:
//                    if (!isRichLoaded)
                        sendPacket(0); // TODO 发送对应的请求
                    break;
                case 1:
                    if (!isParamLoaded)
                        sendPacket(1);//TODO 发送对应的产品参数
                    break;
                case 2:
                    if (!isCommentLoaded)
                        sendPacket(2);// TODO 发送对应用户评价
                    break;
            }
        }
    }

    private void sendPacket(int what) {
        switch (what) {
            case 0:
//                isRichLoaded = true;
                if (null != response) {
                    String temp = response.getMsg().getIntroduction();
                    if (StringUtil.isEmpty(temp))
                        temp = "";
                    richText = RichText.from(temp);
                    richText.autoFix(false).fix(this).into(rt_param);
                }
                break;
            case 1:
                isParamLoaded = true;
                if (null != response) {
                    if (0 == response.getMsg().getProductParam().size()) {
                        EmptyTools.setEmptyView(baseContext, usl_param);
                        EmptyTools.setImg(R.drawable.mycar_icon);
                        EmptyTools.setMessage("当前暂时没有任何商品,赶快去添加吧");
                        return;
                    }
                    adapter = new ProductParamAdapter(baseContext, response.getMsg().getProductParam());
                    usl_param.setAdapter(adapter);
                }
                break;
            case 2:
                isCommentLoaded = true;
                break;
        }
    }

    private void sendCommentPacket(int type) {
        if (0 == type)
            ProgrosDialog.openDialog(baseContext);
//        String url = NetInterface.BASE_URL+NetInterface
    }

    @Override
    public void onFix(ImageHolder holder, boolean imageReady) {
        if (holder.getWidth() > 500 && holder.getHeight() > 500) {
            holder.setAutoFix(true);
        }
    }
}
