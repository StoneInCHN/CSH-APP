package com.cheweishi.android.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.Touch;
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
import com.cheweishi.android.activity.ProductDetailActivity;
import com.cheweishi.android.adapter.ShopListAdapter;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ShopListResponse;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.XListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private List<ShopListResponse.MsgBean> list = new ArrayList<>();

    private ShopListAdapter adapter;

    private int total;

    private boolean isHeadRefresh = false;

    private boolean isEmpty;

    private String mSortType;//当前排序类型

    private String mKeyWord;//当前关键字

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
        return inflater.inflate(R.layout.fragment_shop_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = (PullToRefreshGridView) view.findViewById(R.id.prg_shops);
        adapter = new ShopListAdapter(baseContext, list);
        gridView.setAdapter(adapter);
        gridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        gridView.setOnRefreshListener(this);
        gridView.setOnItemClickListener(this);
//        gridView.setOnScrollListener(new XListView.OnXScrollListener() {
//            @Override
//            public void onXScrolling(View view) {
//            }
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0) {
//                    View view2 = gridView.getChildAt(firstVisibleItem);
//                    if (view2 != null) {
//                        int[] location = new int[2];
//                        view2.getLocationOnScreen(location);
//                    }
//                    ((CarShopActivity) getActivity()).showTitle();
//                } else if (firstVisibleItem > 2) {
//                    ((CarShopActivity) getActivity()).hideTitle();
//                }
//            }
//        });
        isPrepared = true;
        onVisible();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if (!isPrepared || !isVisible) {//|| isLoaded
            return;
        }
        loading.sendEmptyMessageDelayed(0x10, 500);
    }

    @Override
    public void onDataLoading(int what) {

        if (0x10 == what && !isLoaded) {
            isLoaded = true;
            sendPacket(0, currentId, mSortType, mKeyWord);
        } else if (0 == list.size()) {
            EmptyTools.setEmptyView(baseContext, gridView);
            EmptyTools.setImg(R.drawable.mycar_icon);
            EmptyTools.setMessage("当前列表没有商品信息");
        }
        ProgrosDialog.closeProgrosDialog();
    }

    private void sendPacket(int type, int categoryId, String sortType, String keyWord) {
        if (0 == type)
            ProgrosDialog.openDialog(baseContext);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_SHOP + NetInterface.LIST + NetInterface.SUFFIX;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", loginResponse.getDesc());
        params.put("token", loginResponse.getToken());
        params.put("categoryId", categoryId);
        if (!StringUtil.isEmpty(sortType))
            params.put("sortType", sortType);
        if (!StringUtil.isEmpty(keyWord))
            params.put("keyWord", keyWord);
        params.put("pageNumber", currentPage);
        params.put("pageSize", 10);
        netWorkHelper.PostJson(url, params, this);
    }


    @Override
    public void receive(String data) {
        ShopListResponse response = (ShopListResponse) GsonUtil.getInstance().convertJsonStringToObject(data, ShopListResponse.class);

        if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
            ProgrosDialog.closeProgrosDialog();
            showToast(response.getDesc());
            gridView.onRefreshComplete();
            return;
        }
        List<ShopListResponse.MsgBean> temp = response.getMsg();

        if (null != temp && 0 < temp.size()) {
//            EmptyTools.hintEmpty();
            total = response.getPage().getTotal();
            if (isHeadRefresh) {
                list = temp;
            } else {
                list.addAll(temp);
            }
            isEmpty = false;
        } else if (!isEmpty) { // 已经添加了
            isEmpty = true;
            list = temp;
//            adapter.setData(list);
            EmptyTools.setEmptyView(baseContext, gridView);
            EmptyTools.setImg(R.drawable.mycar_icon);
            EmptyTools.setMessage("当前列表没有商品信息");
        }

        adapter.setData(list);
        loginResponse.setToken(response.getToken());
        ProgrosDialog.closeProgrosDialog();
        if (list.size() < total) {
            gridView.onRefreshComplete();
            gridView.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            gridView.onRefreshComplete();
            gridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogHelper.d("onItemClick:" + position);
        Intent detail = new Intent(baseContext, ProductDetailActivity.class);
        startActivity(detail);
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        currentPage = 1;
//        list.clear();
        isHeadRefresh = true;
        sendPacket(1, currentId, mSortType, mKeyWord);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        currentPage++;
        isHeadRefresh = false;
        sendPacket(1, currentId, mSortType, mKeyWord);
    }


}
