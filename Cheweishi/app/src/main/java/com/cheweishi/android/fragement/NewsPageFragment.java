package com.cheweishi.android.fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheweishi.android.R;
import com.cheweishi.android.adapter.NewsListAdapter;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.NewsListResponse;
import com.cheweishi.android.entity.StoreListResponse;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.LogHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewsPageFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    public static final String ARG_PAGE = "ARG_PAGE";

    public static final String ARG_ID = "ARG_ID";

    private int mPage;

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    private boolean isLoaded;//是否已经联网加载过数据了

    private PullToRefreshListView listView;

    private int currentPage = 1;

    private int currentId;

    private List<NewsListResponse.MsgBean> list = new ArrayList<>();

    private NewsListAdapter adapter;

    private int total;

    public static NewsPageFragment newInstance(int page, int id) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putInt(ARG_ID, id);
        NewsPageFragment pageFragment = new NewsPageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        currentId = getArguments().getInt(ARG_ID);
//        LogHelper.d("onCreate:" + mPage);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
//        LogHelper.d("onCreateView:" + mPage);
        listView = (PullToRefreshListView) view.findViewById(R.id.prl_news);
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
//        isLoaded = true;
        LogHelper.d("onVisible:" + mPage);
        adapter = new NewsListAdapter(baseContext, list);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setOnRefreshListener(this);
        loading.sendEmptyMessageDelayed(0x10, 500);
    }

    @Override
    public void onDataLoading(int what) {
        if (isLoaded) {
            if (0 == list.size()) {
                EmptyTools.setEmptyView(baseContext, listView);
                EmptyTools.setImg(R.drawable.mycar_icon);
                EmptyTools.setMessage("当前列表没有新闻信息");
            }
            return;
        }

        if (0x10 == what && !isLoaded) {
            isLoaded = true;
            sendPacket(0);
        }
    }

    private void sendPacket(int type) {
        if (0 == type)
            ProgrosDialog.openDialog(baseContext);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_NEWS + NetInterface.GET_NEWS_LIST + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginResponse.getDesc());
        param.put("token", loginResponse.getToken());
        param.put("pageNumber", currentPage);
        param.put("pageSize", 5);
        param.put("categoryId", currentId);
        netWorkHelper.PostJson(url, param, this);
    }

    @Override
    public void receive(String data) {
        ProgrosDialog.closeProgrosDialog();
        NewsListResponse response = (NewsListResponse) GsonUtil.getInstance().convertJsonStringToObject(data, NewsListResponse.class);

        if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
            showToast(response.getDesc());
            listView.onRefreshComplete();
            return;
        }
        List<NewsListResponse.MsgBean> temp = response.getMsg();

        if (null != temp && 0 < temp.size()) {
            total = response.getPage().getTotal();
            list.addAll(temp);
            if (list.size() < total)
                listView.setMode(PullToRefreshBase.Mode.BOTH);
            else if (5 > list.size())
                listView.setMode(PullToRefreshBase.Mode.DISABLED);
            else
                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            adapter.setData(list);
        } else {
            EmptyTools.setEmptyView(baseContext, listView);
            EmptyTools.setImg(R.drawable.mycar_icon);
            EmptyTools.setMessage("当前列表没有新闻信息");
        }

        loginResponse.setToken(response.getToken());
        listView.onRefreshComplete();
    }

    @Override
    public void error(String errorMsg) {
        ProgrosDialog.closeProgrosDialog();
        listView.onRefreshComplete();
        showToast(R.string.server_link_fault);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}