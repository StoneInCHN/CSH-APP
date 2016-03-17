package com.cheweishi.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.CarReportTimeStrInfo;

public class CarReportTimeAdapter extends BaseAdapter {
	private Context context;
	private List<CarReportTimeStrInfo> list;
	private int pp;
	Viewholder holder;
	private int selectIndex;

	public CarReportTimeAdapter(Context context, List<CarReportTimeStrInfo> list) {
		this.context = context;
		this.list = list;
	}

	public void setList(List<CarReportTimeStrInfo> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list == null || list.size() == 0) {
			return 0;
		} else if(list.size() == 1 && list.get(0).getStart().equals("")&&list.get(0).getEnd().equals("")){
			return list.size();
		}else {
			return list.size() + 1;
		}
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

		this.pp = position;
		if (convertView == null) {
			holder = new Viewholder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.carreport_timeslot_item, null);
			holder.textView = (TextView) convertView
					.findViewById(R.id.tv_carReport_item);
			holder.ll_text = (LinearLayout) convertView
					.findViewById(R.id.ll_text);
			holder.ll_item = (LinearLayout) convertView
					.findViewById(R.id.ll_item);
			holder.setLl_item(holder.ll_item);
			holder.tv_start = (TextView) convertView
					.findViewById(R.id.tv_start);
			holder.tv_end = (TextView) convertView.findViewById(R.id.tv_end);
			holder.firstItem = (TextView) convertView
					.findViewById(R.id.firstItem);
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();

		}
		if (list != null && list.size() > 0) {
			if (position == 0) {
				holder.ll_text.setVisibility(View.INVISIBLE);
				holder.textView.setText("");
				holder.firstItem.setVisibility(View.VISIBLE);
				// holder.tv_start.setText("");
				// holder.tv_end.setText("");

			} else {
				holder.firstItem.setVisibility(View.GONE);
				holder.textView.setText("至");
				holder.ll_text.setVisibility(View.VISIBLE);
				holder.tv_start.setText(list.get(position - 1).getStart());
				holder.tv_end.setText(list.get(position - 1).getEnd());
			}
			if (position == selectIndex) {
				// if (position == 0) {
				holder.ll_item.setBackgroundResource(R.color.item_click);
				// } else {
				// holder.ll_item.setBackgroundResource(R.color.item_default);
				// }

			} else {
				// if (position == 0) {
				holder.ll_item.setBackgroundResource(R.color.item_default);
				// } else {
				// holder.ll_item
				// .setBackgroundResource(R.color.gray_backgroud);
				// }
			}
		}
		return convertView;
	}

	private static class Viewholder {
		private TextView textView;
		private LinearLayout ll_text;
		private TextView tv_start;
		private TextView tv_end;
		private LinearLayout ll_item;
		private TextView firstItem;

		public LinearLayout getLl_item() {
			return ll_item;
		}

		public void setLl_item(LinearLayout ll_item) {
			this.ll_item = ll_item;
		}
	}

	public void setSelectItem(int selectIndex) {
		this.selectIndex = selectIndex;
		this.notifyDataSetChanged();
		// setItem
	}

}
