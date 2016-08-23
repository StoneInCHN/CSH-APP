package com.cheweishi.android.fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheweishi.android.R;
import com.cheweishi.android.utils.LogHelper;

/**
 * Created by tangce on 8/22/2016.
 */
public class ProductParamPageFragment extends BaseFragment {
    private boolean isPrepared;

    public static final String ARG_PAGE = "ARG_PAGE";

    public static final String ARG_ID = "ARG_ID";

    private int mPage; // 当前位置

    private String currentId;//当前标示

    private boolean isRichLoaded, isParamLoaded, isCommentLoaded;//是否加载过

    public static ProductParamPageFragment newInstance(int page, String id) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(ARG_ID, id);
        ProductParamPageFragment pageFragment = new ProductParamPageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        currentId = getArguments().getString(ARG_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        switch (mPage) {
            case 0:// 图文详情
                view = inflater.inflate(R.layout.fragment_richtext, container, false);
                break;
            case 1:// 产品参数
                view = inflater.inflate(R.layout.fragment_param, container, false);
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
                    if (!isRichLoaded)
                        sendPacket(what); // TODO 发送对应的请求
                    break;
                case 1:
                    if (!isParamLoaded)
                        sendPacket(what);//TODO 发送对应的产品参数
                    break;
                case 2:
                    if (!isCommentLoaded)
                        sendPacket(what);// TODO 发送对应用户评价
                    break;
            }
        }
    }

    private void sendPacket(int what) {
        switch (what) {
            case 0:
                isRichLoaded = true;
                break;
            case 1:
                isParamLoaded = true;
                break;
            case 2:
                isCommentLoaded = true;
                break;
        }
    }

}
