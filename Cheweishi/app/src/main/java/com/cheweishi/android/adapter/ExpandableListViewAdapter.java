package com.cheweishi.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.activity.BaskOrderActivity;
import com.cheweishi.android.activity.OrderDetailsActivity;
import com.cheweishi.android.activity.WashCarPayActivity;
import com.cheweishi.android.entity.WashCar;
import com.cheweishi.android.entity.WashCarType;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.UnSlidingListView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
	private List<WashCarType> type;
	private WashCar washCar;
	LayoutInflater mInflater;
	Context context;

	public ExpandableListViewAdapter(Context context, List<WashCarType> type,
			WashCar washCar) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.type = type;
		this.washCar = washCar;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return type.get(groupPosition).getGoodsList().get(childPosition);// child[groupPosition][childPosition];
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
		mViewChild.tv_item_child_name.setText(type.get(groupPosition)
				.getGoodsList().get(childPosition).getGoods_name());
		mViewChild.tv_original_price.setText("￥"
				+ type.get(groupPosition).getGoodsList().get(childPosition)
						.getDiscount_price());

		if (type.get(groupPosition).getGoodsList().get(childPosition)
				.getGoods_name().contains("保养")) {
			mViewChild.btn_pay
					.setBackgroundResource(R.drawable.maintain_click_selector);
			mViewChild.btn_pay.setTextColor(context.getResources().getColor(
					R.color.main_orange));
			mViewChild.btn_pay.setText("预约");
			mViewChild.btn_pay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					System.out.println("预约===good");
					Intent intent = new Intent(context,
							OrderDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("washCar", washCar);
					bundle.putString("goods_id", type.get(groupPosition)
							.getGoodsList().get(childPosition).getId());
					bundle.putString("store_id", type.get(groupPosition)
							.getGoodsList().get(childPosition).getStore_id());

					if (type.get(groupPosition).getGoodsList()
							.get(childPosition).getIs_discount_price()
							.equals("0")) {
						bundle.putString("price", type.get(groupPosition)
								.getGoodsList().get(childPosition).getPrice());
					} else {
						bundle.putString("price", type.get(groupPosition)
								.getGoodsList().get(childPosition)
								.getDiscount_price());
					}
					intent.putExtra("bundle", bundle);
					context.startActivity(intent);
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
					System.out.println("预约===bad");
					Intent intent = new Intent(context,
							WashCarPayActivity.class);
					intent.putExtra("seller_id", washCar.getId());
					intent.putExtra("service_id", type.get(groupPosition)
							.getGoodsList().get(childPosition).getId());
					intent.putExtra("seller", washCar.getStore_name());
					intent.putExtra("service", type.get(groupPosition)
							.getGoodsList().get(childPosition).getGoods_name());
					if (type.get(groupPosition).getGoodsList()
							.get(childPosition).getIs_discount_price()
							.equals("0")) {
						intent.putExtra("price", type.get(groupPosition)
								.getGoodsList().get(childPosition).getPrice());
					} else {
						intent.putExtra("price", type.get(groupPosition)
								.getGoodsList().get(childPosition)
								.getDiscount_price());
					}
					if (type.get(groupPosition).getGoodsList()
							.get(childPosition).getGoods_name().contains("普洗")) {
						intent.putExtra("type", "px");
					}
					context.startActivity(intent);

				}
			});
		}

		if (type.get(groupPosition).getGoodsList().get(childPosition)
				.getIs_discount_price().equals("0")) {
			mViewChild.tv_item_child_discount_showOrNot
					.setVisibility(View.VISIBLE);
			mViewChild.tv_discount_price_remind.setVisibility(View.VISIBLE);
			mViewChild.tv_original_price.setVisibility(View.VISIBLE);
			mViewChild.tv_discount_price.setText("￥"
					+ type.get(groupPosition).getGoodsList().get(childPosition)
							.getPrice());
			mViewChild.tv_original_price.setText("￥"
					+ type.get(groupPosition).getGoodsList().get(childPosition)
							.getPrice());
		} else {
			mViewChild.tv_discount_price.setText("￥"
					+ type.get(groupPosition).getGoodsList().get(childPosition)
							.getDiscount_price());
			mViewChild.tv_item_child_discount_showOrNot
					.setVisibility(View.VISIBLE);
			mViewChild.tv_discount_price_remind.setVisibility(View.VISIBLE);
			mViewChild.tv_original_price.setVisibility(View.VISIBLE);
		}
		return convertView;
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
		// TODO Auto-generated method stub
		return type.get(groupPosition).getGoodsList() == null ? 0 : type
				.get(groupPosition).getGoodsList().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return type.get(groupPosition);// [groupPosition];
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return type == null ? 0 : type.size();// .length;
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
		mViewChild.tv_project_name.setText(type.get(groupPosition)
				.getTypename());
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
