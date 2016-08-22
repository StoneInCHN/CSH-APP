package com.cheweishi.android.thirdpart.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.cheweishi.android.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.entity.AdvResponse;

/**
 * Created by tangce on 8/19/2016.
 */
public class CommonNetWorkImgHolder implements Holder<String> {
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {

        XUtilsImageLoader.getxUtilsImageLoader(context, R.drawable.ad_default_back, imageView, data);
    }
}
