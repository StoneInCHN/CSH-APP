package com.cheweishi.android.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.cheweishi.android.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.activity.WebActivity;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.ADInfo;
import com.cheweishi.android.entity.AdvResponse;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.utils.LogHelper;

public class ImgAdapter extends BaseAdapter {

    private BaseActivity _context;
    private List<Integer> imgList;
    private AdvResponse adInfos;
    private int returnCount = -1;

    public ImgAdapter(BaseActivity context, List<Integer> imgList) {
        _context = context;
        this.imgList = imgList;
    }

    public ImgAdapter(BaseActivity context, AdvResponse adInfos,
                      int returnCount) {
        _context = context;
        this.adInfos = adInfos;
        this.returnCount = returnCount;
    }

    public int getCount() {
        if (returnCount == -1) {
            return Integer.MAX_VALUE;
        } else {
            return adInfos == null ? 0 : adInfos.getMsg().size();
        }

    }

    public Object getItem(int position) {

        return adInfos.getMsg().get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            ImageView imageView = new ImageView(_context);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ScaleType.FIT_XY);
            imageView.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            convertView = imageView;
            viewHolder.imageView = (ImageView) convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (imgList != null && imgList.size() > 0) {
            viewHolder.imageView.setImageResource(imgList.get(position
                    % imgList.size()));
        } else {

            if (adInfos != null && adInfos.getMsg().size() > 0) {
                ViewGroup.LayoutParams lp = viewHolder.imageView
                        .getLayoutParams();
                lp.width = ScreenTools.getScreentWidth(_context);
                lp.height = (int) (((float) ScreenTools
                        .getScreentWidth(_context)) * (((float) 80) / ((float) 320)));

                viewHolder.imageView.setLayoutParams(lp);
                XUtilsImageLoader.getxUtilsImageLoader(_context,
                        R.drawable.ad_default_back,
                        viewHolder.imageView, adInfos.getMsg().get(position % adInfos.getMsg().size()).getAdvImageUrl());
                // ImageLoader imageLoader = ImageLoader.getInstance();
                // imageLoader.init(ImageLoaderConfiguration.createDefault(_context));
                // imageLoader.displayImage(
                // API.CSH_GET_IMG_BASE_URL
                // + adInfos.get(position % adInfos.size()).getAdv_content(),
                // viewHolder.imageView, options);

                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(_context, WebActivity.class);
                        intent.putExtra("url", adInfos.getMsg().get(position % adInfos.getMsg().size()).getAdvContentLink());
                        _context.startActivity(intent);
                    }
                });
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }
}
