package com.cheweishi.android.fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cheweishi.android.R;
import com.cheweishi.android.activity.CarShopActivity;
import com.cheweishi.android.adapter.ShopListAdapter;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.widget.XListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;


public class ShopPageFragment extends BaseFragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {

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
        for (int i = 0; i < 51; i++) {
            list.add("haha");
        }
        adapter = new ShopListAdapter(baseContext, list);
        gridView.setAdapter(adapter);
        gridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        gridView.setOnRefreshListener(this);
        gridView.setOnItemClickListener(this);
        gridView.setOnScrollListener(new XListView.OnXScrollListener() {
            @Override
            public void onXScrolling(View view) {
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    ((CarShopActivity) getActivity()).showTitle();
                } else if (firstVisibleItem > 2) {
                    ((CarShopActivity) getActivity()).hideTitle();
                }
            }
        });
        loading.sendEmptyMessageDelayed(0x10, 500);
    }

    @Override
    public void onDataLoading(int what) {

        if (0x10 == what && !isLoaded) {
            isLoaded = true;
//            sendPacket(0);
        } else if (0 == list.size()) {
            EmptyTools.setEmptyView(baseContext, gridView);
            EmptyTools.setImg(R.drawable.mycar_icon);
            EmptyTools.setMessage("当前列表没有商品信息");
        }
        ProgrosDialog.closeProgrosDialog();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }


}
