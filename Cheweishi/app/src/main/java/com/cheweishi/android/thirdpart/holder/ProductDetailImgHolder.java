package com.cheweishi.android.thirdpart.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.cheweishi.android.R;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.entity.AdvResponse;
import com.cheweishi.android.entity.ProductDetailResponse;

import java.util.List;

/**
 * Created by tangce on 8/19/2016.
 */
public class ProductDetailImgHolder implements Holder<List<ProductDetailResponse.MsgBean.ProductImagesBean>> {
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, List<ProductDetailResponse.MsgBean.ProductImagesBean> data) {

        XUtilsImageLoader.getxUtilsImageLoader(context, R.drawable.udesk_defualt_failure, imageView, data.get(position).getMobileicon());
    }
}
