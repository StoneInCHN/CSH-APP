package com.cheweishi.android.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cheweishi.android.biz.JSONCallback;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.activity.OrderDetailsActivity;
import com.cheweishi.android.activity.WashCarPayActivity;
import com.cheweishi.android.config.NetInterface;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.ServiceDetailResponse;
import com.cheweishi.android.http.NetWorkHelper;
import com.cheweishi.android.response.BaseResponse;
import com.cheweishi.android.utils.GsonUtil;
import com.cheweishi.android.utils.LogHelper;
import com.cheweishi.android.widget.UnSlidingListView;
import com.lidroid.xutils.http.ResponseInfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
    private String type;
    private List<ServiceDetailResponse.MsgBean.CarServicesBean> washCar;
    LayoutInflater mInflater;
    Context context;
    private String tenantName;

    public ExpandableListViewAdapter(Context context,
                                     List<ServiceDetailResponse.MsgBean.CarServicesBean> washCar, String tenantName) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.washCar = washCar;
        this.tenantName = tenantName;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return washCar.get(groupPosition).getSubServices().get(childPosition);// child[groupPosition][childPosition];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            mViewChild = new ViewChild();
            convertView = mInflater.inflate(R.layout.expandablelistview_item,
                    null);
            // mViewChild.gridView = (UnSlidingListView) convertView
            // .findViewById(R.id.channel_item_child_listview);

            mViewChild.tv_item_child_name = (TextView) convertView
                    .findViewById(R.id.tv_item_child_name);
            mViewChild.tv_item_child_discount_showOrNot = (TextView) convertView
                    .findViewById(R.id.tv_item_child_discount_showOrNot);
            mViewChild.tv_discount_price_remind = (TextView) convertView
                    .findViewById(R.id.tv_discount_price_remind);
            mViewChild.tv_discount_price = (TextView) convertView
                    .findViewById(R.id.tv_discount_price);
            mViewChild.tv_original_price = (TextView) convertView
                    .findViewById(R.id.tv_original_price);
            mViewChild.btn_pay = (TextView) convertView
                    .findViewById(R.id.btn_pay);
            mViewChild.tv_original_price.getPaint().setFlags(
                    Paint.STRIKE_THRU_TEXT_FLAG);
            convertView.setTag(mViewChild);
        } else {
            mViewChild = (ViewChild) convertView.getTag();
        }
        mViewChild.tv_item_child_name.setText(washCar.get(groupPosition).getSubServices().get(childPosition).getServiceName());
        mViewChild.tv_original_price.setText("￥"
                + washCar.get(groupPosition).getSubServices().get(childPosition).getPrice());

        if (washCar.get(groupPosition).getSubServices().get(childPosition).getServiceName().contains("保养")) {
            mViewChild.btn_pay
                    .setBackgroundResource(R.drawable.maintain_click_selector);
            mViewChild.btn_pay.setTextColor(context.getResources().getColor(
                    R.color.main_orange));
            mViewChild.btn_pay.setText("预约");
            mViewChild.btn_pay.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO 预约,因为要展示,所以暂时注释,调用预约接口

                    subscript(washCar.get(groupPosition).getSubServices().get(childPosition).getId(), washCar.get(groupPosition).getSubServices().get(childPosition).getPrice());

//					Intent intent = new Intent(context,
//							OrderDetailsActivity.class);
//					Bundle bundle = new Bundle();
//					bundle.putSerializable("washCar", washCar.get(groupPosition));
//					bundle.putString("goods_id", type.get(groupPosition)
//							.getGoodsList().get(childPosition).getId());
//					bundle.putString("store_id", type.get(groupPosition)
//							.getGoodsList().get(childPosition).getStore_id());
//
//					if (type.get(groupPosition).getGoodsList()
//							.get(childPosition).getIs_discount_price()
//							.equals("0")) {
//						bundle.putString("price", type.get(groupPosition)
//								.getGoodsList().get(childPosition).getPrice());
//					} else {
//						bundle.putString("price", type.get(groupPosition)
//								.getGoodsList().get(childPosition)
//								.getDiscount_price());
//					}
//					intent.putExtra("bundle", bundle);
//					context.startActivity(intent);
                }
            });
        } else {
            mViewChild.btn_pay
                    .setBackgroundResource(R.drawable.pay_click_selector);
            mViewChild.btn_pay.setTextColor(context.getResources().getColor(
                    R.color.main_blue));
            mViewChild.btn_pay.setText("支付");
            mViewChild.btn_pay.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO 支付,因为要展示,所以暂时注释

                    Intent intent = new Intent(context, WashCarPayActivity.class);
                    intent.putExtra("seller", tenantName);
                    intent.putExtra("service", washCar.get(groupPosition).getCategoryName());
                    intent.putExtra("service_id", String.valueOf(washCar.get(groupPosition).getSubServices().get(childPosition).getId()));
                    intent.putExtra("price", String.valueOf(washCar.get(groupPosition).getSubServices().get(childPosition).getPrice()));
                    LogHelper.d("price:" + String.valueOf(washCar.get(groupPosition).getSubServices().get(childPosition).getPrice()));
                    context.startActivity(intent);
//					Intent intent = new Intent(context,
//							WashCarPayActivity.class);
//					intent.putExtra("seller_id", washCar.getId());
//					intent.putExtra("service_id", type.get(groupPosition)
//							.getGoodsList().get(childPosition).getId());
//					intent.putExtra("seller", washCar.getStore_name());
//					intent.putExtra("service", type.get(groupPosition)
//							.getGoodsList().get(childPosition).getGoods_name());
//					if (type.get(groupPosition).getGoodsList()
//							.get(childPosition).getIs_discount_price()
//							.equals("0")) {
//						intent.putExtra("price", type.get(groupPosition)
//								.getGoodsList().get(childPosition).getPrice());
//					} else {
//						intent.putExtra("price", type.get(groupPosition)
//								.getGoodsList().get(childPosition)
//								.getDiscount_price());
//					}
//					if (type.get(groupPosition).getGoodsList()
//							.get(childPosition).getGoods_name().contains("普洗")) {
//						intent.putExtra("type", "px");
//					}
//					context.startActivity(intent);

                }
            });
        }

        if (0 == washCar.get(groupPosition).getSubServices().get(childPosition).getPromotionPrice()) {
            mViewChild.tv_item_child_discount_showOrNot
                    .setVisibility(View.GONE);
            mViewChild.tv_discount_price_remind.setVisibility(View.VISIBLE);
            mViewChild.tv_original_price.setVisibility(View.VISIBLE);
            mViewChild.tv_discount_price.setText("￥"
                    + washCar.get(groupPosition).getSubServices().get(childPosition)
                    .getPrice());
            mViewChild.tv_original_price.setText("￥"
                    + washCar.get(groupPosition).getSubServices().get(childPosition).getPrice());
        } else {
            mViewChild.tv_discount_price.setText("￥"
                    + washCar.get(groupPosition).getSubServices().get(childPosition).getPromotionPrice());
            mViewChild.tv_item_child_discount_showOrNot
                    .setVisibility(View.GONE);
            mViewChild.tv_discount_price_remind.setVisibility(View.VISIBLE);
            mViewChild.tv_original_price.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    /**
     * 预约发包
     *
     * @param id
     * @param price
     */
    private void subscript(int id, int price) {
        ProgrosDialog.openDialog(context);
        String url = NetInterface.BASE_URL + NetInterface.TEMP_ORDER + NetInterface.SUBSCRIBE + NetInterface.SUFFIX;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", ((BaseActivity) context).getUserId());
        param.put("token", ((BaseActivity) context).getToken());
        param.put("serviceId", id);
        param.put("price", price);
        LogHelper.d(id + "----" + price);
        NetWorkHelper.getInstance(context).PostJson(url, param, new JSONCallback() {
            @Override
            public void receive(int type, String data) {

            }

            @Override
            public void downFile(int type, ResponseInfo<File> arg0) {

            }

            @Override
            public void receive(String TAG, String data) {

            }

            @Override
            public void receive(String data) {
                ProgrosDialog.closeProgrosDialog();
                BaseResponse response = (BaseResponse) GsonUtil.getInstance().convertJsonStringToObject(data, BaseResponse.class);
                if (!response.getCode().equals(NetInterface.RESPONSE_SUCCESS)) {
                    Toast.makeText(context, R.string.server_link_fault, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("recordId", response.getDesc());
                context.startActivity(intent);
                ((BaseActivity) context).setUserToken(response.getToken());
            }

            @Override
            public void error(String errorMsg) {
                ProgrosDialog.closeProgrosDialog();
            }
        });
    }

    /**
     * 设置gridview数据
     *
     * @param data
     * @return
     */
    private ArrayList<HashMap<String, Object>> setGridViewData(String[] data) {
        ArrayList<HashMap<String, Object>> gridItem = new ArrayList<HashMap<String, Object>>();
        Log.i("==============", data + "");
        for (int i = 0; i < data.length; i++) {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("channel_gridview_item", data[i]);
            gridItem.add(hashMap);
        }
        return gridItem;
    }

    /**
     * ����gridview����¼�����
     *
     * @param gridView
     */
    private void setGridViewListener(final ListView gridView) {
        gridView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (view instanceof TextView) {
                    TextView tv = (TextView) view;
                    Toast.makeText(context,
                            "position=" + position + "||" + tv.getText(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("hefeng", "gridView listaner position=" + position
                            + "||text=" + tv.getText());
                }
            }
        });
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return washCar.get(groupPosition).getSubServices() == null ? 0 : washCar.get(groupPosition).getSubServices().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return washCar.get(groupPosition);// [groupPosition];
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return washCar == null ? 0 : washCar.size();// .length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            mViewChild = new ViewChild();
            convertView = mInflater.inflate(R.layout.item_expandablellistview,
                    null);
            mViewChild.tv_project_name = (TextView) convertView
                    .findViewById(R.id.tv_project_name);
            mViewChild.tv_project_note = (TextView) convertView
                    .findViewById(R.id.tv_project_note);
            mViewChild.tv_project_money = (TextView) convertView
                    .findViewById(R.id.tv_project_money);
            mViewChild.img_project_right = (ImageView) convertView
                    .findViewById(R.id.img_project_right);
            convertView.setTag(mViewChild);
        } else {
            mViewChild = (ViewChild) convertView.getTag();
        }

        if (isExpanded) {
            mViewChild.img_project_right
                    .setImageResource(R.drawable.channel_expandablelistview_top_icon);
        } else {
            mViewChild.img_project_right
                    .setImageResource(R.drawable.channel_expandablelistview_bottom_icon);
        }
        mViewChild.tv_project_name.setText(washCar.get(groupPosition).getCategoryName());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

    ViewChild mViewChild;

    static class ViewChild {
        ImageView img_project_right;
        TextView tv_project_name;
        TextView tv_project_note;
        TextView tv_project_money;
        UnSlidingListView gridView;

        TextView tv_item_child_name;
        TextView tv_item_child_discount_showOrNot;
        TextView tv_discount_price_remind;
        TextView tv_discount_price;
        TextView tv_original_price;
        TextView btn_pay;
    }

}
