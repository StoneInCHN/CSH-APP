package com.cheweishi.android.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cheweishi.android.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.entity.MyCarManagerResponse;
import com.cheweishi.android.entity.ServiceDetailResponse;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.widget.FontAwesomeView;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CarManagerAdapter extends PagerAdapter {
    private Context context;

    private List<View> views = new ArrayList<>();

    private List<MyCarManagerResponse.MsgBean> list;

    public CarManagerAdapter(Context context, List<MyCarManagerResponse.MsgBean> list) {
        this.context = context;
        this.list = list;

//        init();
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.car_manager_item, null);
        container.addView(view);
        views.add(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }
}
