package com.cheweishi.android.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.jpush.android.helpers.h;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.MainGridInfo;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.utils.DisplayUtil;
import com.cheweishi.android.utils.StringUtil;

/**
 * 商城版首页GridView适配器
 *
 * @author mingdasen
 */
public class MainGridViewAdapter extends BaseAdapter {

    private List<MainGridInfo> list;
    private Context mContext;

    public MainGridViewAdapter(Context mContext, List<MainGridInfo> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return StringUtil.isEmpty(list) ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_main_gridview, null);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.img_icon);
            convertView.setBackgroundResource(R.drawable.more_back);
            if (position == 5) {
                float f = 1;
//				float p = 12.5f;
                Log.i("result", "=====屏幕宽度====" + ScreenTools.getScreentWidth((Activity) mContext));
                if (ScreenTools.getScreentWidth((Activity) mContext) <= 480) {
                    f = 0.5f;
//					p = 13f;
                } else {
                    f = 1;
//					p = 12.5f;
                }
                convertView.setLayoutParams(new LayoutParams(ScreenTools.getScreentWidth((Activity) mContext) / 2 - DisplayUtil.dip2px(mContext, f), LayoutParams.WRAP_CONTENT));
                holder.textView.setVisibility(View.GONE);
                holder.imageView.setImageResource(R.drawable.main_logo);
                convertView.setBackgroundResource(R.color.white);
//				holder.imageView.setPadding(0, DisplayUtil.dip2px(mContext, 12.5f), 0, DisplayUtil.dip2px(mContext, p));
            }
            if (position == 6) {
                convertView.setVisibility(View.GONE);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!StringUtil.isEmpty(list) && list.size() > position) {
            if (position != 5 && position != 6) {
//				holder.imageView.setImageResource(R.drawable.main_logo);
//				holder.textView.setVisibility(View.GONE);
//			}else {
                holder.textView.setText(list.get(position).getName());
                holder.imageView.setImageResource(list.get(position).getImgId());
//			}

//			DisplayImageOptions options = new DisplayImageOptions.Builder()
//					.cacheInMemory(true).cacheOnDisk(true)
//					.showImageForEmptyUri(R.drawable.ic_launcher)
//					.showImageOnFail(R.drawable.ic_launcher)
//					.showImageOnLoading(R.drawable.ic_launcher)
//					.bitmapConfig(Bitmap.Config.RGB_565).build();
//
//			// if (imgListStr != null && imgListStr.size() > 0) {
//			ImageLoader.getInstance().displayImage(
//					"http://115.28.161.11:8080/XAI/appDownLoad/downLoadPhoto?path="
//							+ list.get(position), holder.imageView, options);
            }
        }
        return convertView;
    }


    class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

}
