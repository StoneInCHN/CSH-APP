package com.cheweishi.android.fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.ShopListAdapter;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;


public class ShopPageFragment extends BaseFragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    public static final String ARG_ID = "ARG_ID";

    private int mPage;

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    private boolean isLoaded;//是否已经联网加载过数据了

    private PullToRefreshGridView gridView;

    private int currentPage = 1;

    private int currentId;

    private List<String> list = new ArrayList<>();

    private ShopListAdapter adapter;

    private int total;

    private boolean isHeadRefresh = false;
    private boolean isEmpty;

    public static ShopPageFragment newInstance(int page, int id) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putInt(ARG_ID, id);
        ShopPageFragment pageFragment = new ShopPageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        currentId = getArguments().getInt(ARG_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_page, container, false);
        gridView = (PullToRefreshGridView) view.findViewById(R.id.prg_shops);
        isPrepared = true;
        onVisible();
        return view;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if (!isPrepared || !isVisible) {//|| isLoaded
            return;
        }
        for (int i = 0; i < 50; i++) {
            list.add("haha");
        }
        adapter = new ShopListAdapter(baseContext, list);
        gridView.setAdapter(adapter);
        gridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//        gridView.setOnRefreshListener(this);
//        gridView.setOnItemClickListener(this);
        loading.sendEmptyMessageDelayed(0x10, 500);
    }

    @Override
    public void onDataLoading(int what) {
//        if (isLoaded) {
//        if (0 == list.size()) {
//            EmptyTools.setEmptyView(baseContext, listView);
//            EmptyTools.setImg(R.drawable.mycar_icon);
//            EmptyTools.setMessage("当前列表没有新闻信息");
//        }
//            return;
//        }

//        if (0x10 == what && !isLoaded) {
//            isLoaded = true;
//            sendPacket(0);
//        } else if (0 == list.size()) {
//            EmptyTools.setEmptyView(baseContext, listView);
//            EmptyTools.setImg(R.drawable.mycar_icon);
//            EmptyTools.setMessage("当前列表没有新闻信息");
//        }
        ProgrosDialog.closeProgrosDialog();
    }

}
