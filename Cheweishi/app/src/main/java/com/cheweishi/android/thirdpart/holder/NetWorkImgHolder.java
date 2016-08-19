package com.cheweishi.android.thirdpart.holder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cheweishi.android.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.entity.AdvResponse;
import com.cheweishi.android.tools.ScreenTools;

import java.util.List;

/**
 * Created by tangce on 8/19/2016.
 */
public class NetWorkImgHolder implements Holder<AdvResponse.MsgBean> {
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
//        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        ViewGroup.LayoutParams layout = imageView.getLayoutParams();
//        layout.height = (int) (((float) ScreenTools.getScreentWidth((Activity) context)) * (((float) 80) / ((float) 320)));
//        layout.width = ScreenTools.getScreentWidth((Activity) context);
//        imageView.setLayoutParams(layout);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position,AdvResponse.MsgBean data) {

        XUtilsImageLoader.getxUtilsImageLoader(context, R.drawable.ad_default_back, imageView, data.getAdvImageUrl());
    }
}
