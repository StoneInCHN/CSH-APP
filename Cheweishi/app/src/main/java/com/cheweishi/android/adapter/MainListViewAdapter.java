package com.cheweishi.android.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheweishi.android.R;
import com.cheweishi.android.activity.WashcarDetailsActivity;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.MainSellerInfo;
import com.cheweishi.android.entity.ServiceListResponse;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.UnSlidingListView;

/**
 * 新版首页ListView适配器
 *
 * @author mingdasen
 */
public class MainListViewAdapter extends BaseAdapter implements OnClickListener {

    private Context mContext;
    private List<ServiceListResponse.MsgBean> list;
    private MianSellerServiceAdapater serviceAdapater;

    public MainListViewAdapter(Context mContext, List<ServiceListResponse.MsgBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void setData(List<ServiceListResponse.MsgBean> list) {
        this.list = list;
        notifyDataSetChanged();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler;
        if (convertView == null) {
            hodler = new ViewHodler();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_seller_list, null);
            hodler.rl_seller_info = (RelativeLayout) convertView
                    .findViewById(R.id.rl_seller_info);
            hodler.img_sellser = (ImageView) convertView
                    .findViewById(R.id.img_sellser);
            hodler.tv_seller_name = (TextView) convertView
                    .findViewById(R.id.tv_seller_name);
            hodler.tv_seller_evaluate = (TextView) convertView
                    .findViewById(R.id.tv_seller_evaluate);
            hodler.tv_seller_address = (TextView) convertView
                    .findViewById(R.id.tv_seller_address);
            hodler.tv_seller_distance = (TextView) convertView
                    .findViewById(R.id.tv_seller_distance);
            hodler.tv_appoint = (ImageView) convertView
                    .findViewById(R.id.tv_appoint);
            hodler.list_main_seller_service = (UnSlidingListView) convertView
                    .findViewById(R.id.list_main_seller_service);
            hodler.ll_seller_evaluate_content = (LinearLayout) convertView
                    .findViewById(R.id.ll_seller_evaluate_content);
            hodler.line = convertView.findViewById(R.id.line);
            hodler.rl_seller_info.setTag(position);
            hodler.rl_seller_info.setOnClickListener(this);
            convertView.setTag(hodler);

        } else {
            hodler = (ViewHodler) convertView.getTag();
            hodler.rl_seller_info.setTag(position);
        }

        if (position == 0) {
            hodler.line.setVisibility(View.GONE);
        } else {
            hodler.line.setVisibility(View.VISIBLE);
        }
        if (!StringUtil.isEmpty(list) && list.size() > position) {
            hodler.tv_seller_name.setText(list.get(position).getTenant_name());
//            hodler.tv_seller_evaluate.setText(list.get(position).getPraiseRate() * 100 + "%好评");
            hodler.tv_seller_address.setText(list.get(position).getAddress());
            hodler.tv_seller_distance.setText(list.get(position).getDistance() + "km");
//			if (!StringUtil.isEmpty(list.get(position).getAppoint())
//					&& StringUtil.isEquals("0",
//							list.get(position).getAppoint(), true)) {
//				hodler.tv_appoint.setImageResource(R.drawable.fujin);
//			} else {
//				hodler.tv_appoint.setVisibility(View.GONE);
//			}
            hodler.ll_seller_evaluate_content.removeAllViews();
            // TODO 添加钻石
            for (int i = 0; i < list.get(position).getPraiseRate(); i++) {
                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView.setPadding(0, 0, 2, 0);
                imageView.setScaleType(ScaleType.FIT_XY);
                imageView.setImageResource(R.drawable.haoping);
                hodler.ll_seller_evaluate_content.addView(imageView);
            }

            // ImageLoader imageLoader = ImageLoader.getInstance();
            // imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
            //
            // DisplayImageOptions options = new DisplayImageOptions.Builder()
            // .cacheInMemory(true).cacheOnDisk(true)
            // .showImageForEmptyUri(R.drawable.zhaochewei_img)
            // .showImageOnFail(R.drawable.zhaochewei_img)
            // .showImageOnLoading(R.drawable.zhaochewei_img)
            // .bitmapConfig(Bitmap.Config.RGB_565).build();
            //
            // // if (imgListStr != null && imgListStr.size() > 0) {
            // imageLoader.getInstance().displayImage(
            // API.CSH_GET_IMG_BASE_URL
            // + list.get(position).getImgUrl(), hodler.img_sellser, options);
            // }
            XUtilsImageLoader.getxUtilsImageLoader(mContext,
                    R.drawable.zhaochewei_img, hodler.img_sellser,
                    list.get(position).getPhoto());
            if (null == hodler.list_main_seller_service.getAdapter()) {
                serviceAdapater = new MianSellerServiceAdapater(mContext,
                        list.get(position));
                hodler.list_main_seller_service.setAdapter(serviceAdapater);
            }
        }
        return convertView;
    }


    private class ViewHodler {
        private RelativeLayout rl_seller_info;// 商家信息容器
        private ImageView img_sellser;// 商家图片
        private LinearLayout ll_seller_evaluate_content;// 好评图标容器
        private TextView tv_seller_name;// 商家名字
        private TextView tv_seller_evaluate;// 商家评价
        private TextView tv_seller_address;//a 商家地址
        private TextView tv_seller_distance;// 商家距离
        private ImageView tv_appoint;// 指定
        private View line;
        private UnSlidingListView list_main_seller_service;// 商家服务内容

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_seller_info:
                Intent intent = new Intent(mContext, WashcarDetailsActivity.class);
                intent.putExtra("id", list.get((Integer) v.getTag()).getId());
                mContext.startActivity(intent);
                break;

            default:
                break;
        }
    }
}
