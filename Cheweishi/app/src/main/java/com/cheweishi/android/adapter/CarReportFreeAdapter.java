package com.cheweishi.android.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.entity.CarReportFreeVO;

/**
 * 
 * 日常费用gridView
 * 
 * @author zhangq
 */
public class CarReportFreeAdapter extends MyBaseAdapter<CarReportFreeVO> {
	private Holder holder;
	private int[] bitmapRids = { R.drawable.feiyong_jiayou_selector,
			R.drawable.feiyong_tingche_selector,
			R.drawable.feiyong_xiche_selector,
			R.drawable.feiyong_luqiao_selector,
			R.drawable.feiyong_baoyang_selector,
			R.drawable.feiyong_baoxian_selector,
			R.drawable.feiyong_fakuang_selector, R.drawable.feiyong_tianjia2x };

	private int[] names = { R.string.report_oil_free,
			R.string.report_park_free, R.string.report_wash_free,
			R.string.report_road_free, R.string.report_maintenance_free,
			R.string.report_insure_free, R.string.report_fine_free,
			R.string.report_other_free };
	private Resources mRes;

	public CarReportFreeAdapter(List<CarReportFreeVO> mData, Context mContext) {
		super(mData, mContext);
		mRes = mContext.getResources();
	}

	public void addItem(CarReportFreeVO item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	public void removeItem() {
		mData.remove(mData.size() - 1);
	}

	public void removeItem(int position) {
		try {
			mData.remove(position);
			notifyDataSetChanged();
		} catch (Exception e) {
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup container) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.gridview_report_item,
					container, false);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		CarReportFreeVO vo = mData.get(position);
		holder.imgIcon.setImageResource(bitmapRids[vo.getType()]);
		holder.tvName.setText(mRes.getString(names[vo.getType()]));
		holder.tvMoney.setText(vo.getMoney());

		return convertView;
	}

	public class Holder {
		private ImageView imgIcon;
		private TextView tvName;
		private TextView tvMoney;

		public Holder(View v) {
			imgIcon = (ImageView) v.findViewById(R.id.img_icon);
			tvName = (TextView) v.findViewById(R.id.tv_name);
			tvMoney = (TextView) v.findViewById(R.id.tv_money);
		}
	}
}
