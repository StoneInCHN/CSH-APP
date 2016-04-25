package com.cheweishi.android.adapter;

import java.util.List;

import com.cheweishi.android.R;
import com.cheweishi.android.entity.ProvinceListing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CarTypeProvinceListingAdapter extends BaseAdapter {

	private Context context;
	private List<ProvinceListing> list;
	private ProvinceListing provinceListing;
	private boolean flag = true;

	public CarTypeProvinceListingAdapter(Context context,
			List<ProvinceListing> list,boolean flag) {
		super();
		this.flag = flag;
		this.context = context;
		this.list = list;
	}

	public void setDataChanged(List<ProvinceListing> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0:list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.cartype_provincecitylisting_listitems, null);

			holder.title = (TextView) convertView.findViewById(R.id.tv_name);
			holder.arrows = (ImageView) convertView
					.findViewById(R.id.img_arrows);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		provinceListing = list.get(position);

		holder.title.setText(provinceListing.getName());
		if (provinceListing.getClassno().equals("-1")) {
			holder.arrows.setVisibility(View.INVISIBLE);
		}
		if(flag == false) {
			holder.arrows.setVisibility(View.VISIBLE);
		} else {
			holder.arrows.setVisibility(View.GONE);
		}
		return convertView;
	}

	private Holder holder;

	private class Holder {
		TextView title;
		ImageView arrows;
	}

}
