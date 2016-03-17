package com.cheweishi.android.adapter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.adapter.CarManagerAdapter.ViewHolder;
import com.cheweishi.android.adapter.CarManagerAdapter.onRightItemClickListener;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ListDialog;
import com.cheweishi.android.entity.CarManager;
import com.cheweishi.android.entity.InsuranceCalculation;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.widget.FontAwesomeView;

public class InsuranceBusinessAdapter extends BaseAdapter {
	private BaseActivity context;
	private ViewHolder viewHolder;
	private LayoutInflater mInflater;
	private int mRightWidth = 0;
	String[] array = { "10万", "20万", "30万", "40万", "50万" };
	List<InsuranceCalculation> listInsuranceCalculation;

	public InsuranceBusinessAdapter(BaseActivity context,
			List<InsuranceCalculation> listInsuranceCalculation) {
		this.context = context;
		this.listInsuranceCalculation = listInsuranceCalculation;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listInsuranceCalculation == null ? 0 : listInsuranceCalculation
				.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listInsuranceCalculation == null ? null
				: listInsuranceCalculation.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setData(List<InsuranceCalculation> listInsuranceCalculation) {
		this.listInsuranceCalculation = listInsuranceCalculation;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_insurance_buy, null);
			viewHolder = new ViewHolder();
			viewHolder.cb_bussiness_child_insurance = (CheckBox) convertView
					.findViewById(R.id.cb_bussiness_child_insurance);
			viewHolder.tv_bussiness_child_insurance_name = (TextView) convertView
					.findViewById(R.id.tv_bussiness_child_insurance_name);
			viewHolder.tv_bussiness_child_insurance_price = (TextView) convertView
					.findViewById(R.id.tv_bussiness_child_insurance_price);
			viewHolder.sp_bussiness_child_insurance = (TextView) convertView
					.findViewById(R.id.sp_bussiness_child_insurance);
			viewHolder.ll_bussiness_child_insurance = (LinearLayout) convertView
					.findViewById(R.id.ll_bussiness_child_insurance);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.sp_bussiness_child_insurance
				.setText(listInsuranceCalculation.get(position)
						.getInsuranceCurrentPrice());
		viewHolder.tv_bussiness_child_insurance_name
				.setText(listInsuranceCalculation.get(position)
						.getInsuranceName());
		viewHolder.tv_bussiness_child_insurance_price
				.setText(listInsuranceCalculation.get(position)
						.getInsurancePayPrice());
		if (listInsuranceCalculation.get(position).isChooseFlag() == false) {
			viewHolder.cb_bussiness_child_insurance.setChecked(false);
		} else {
			viewHolder.cb_bussiness_child_insurance.setChecked(true);
		}
		viewHolder.ll_bussiness_child_insurance
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (listInsuranceCalculation.get(position)
								.isChooseFlag() == false) {
							listInsuranceCalculation.get(position)
									.setChooseFlag(true);
							notifyDataSetChanged();
							context.dealCallBackFromAdapter(position,listInsuranceCalculation);
						} else {
							listInsuranceCalculation.get(position)
									.setChooseFlag(false);
							notifyDataSetChanged();
							context.dealCallBackFromAdapter(position,listInsuranceCalculation);
						}
					}
				});
		viewHolder.sp_bussiness_child_insurance
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						ListDialog.Builder builder = new ListDialog.Builder(
								context, array, new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										// TODO Auto-generated method stub
										// Toast.makeText(context, position +
										// "",
										// Toast.LENGTH_LONG).show();
										String hoddy = array[arg2];
										viewHolder.sp_bussiness_child_insurance
												.setText(hoddy);
										dialog.dismiss();
										listInsuranceCalculation
												.get(position)
												.setInsuranceCurrentPrice(hoddy);
										notifyDataSetChanged();
									}
								});
						// 设置对话框的标题
						builder.setTitle("title");
						dialog = builder.create();
						// 创建一个列表对话框

						dialog.show();
					}
				});
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
		// R.layout.item_oil, array);
		// viewHolder.sp_bussiness_child_insurance.setAdapter(adapter);
		return convertView;
	}

	class ViewHolder {
		TextView sp_bussiness_child_insurance;
		TextView tv_bussiness_child_insurance_name;
		TextView tv_bussiness_child_insurance_price;
		LinearLayout ll_bussiness_child_insurance;
		CheckBox cb_bussiness_child_insurance;

	}

	ListDialog dialog;

	private void showDialog(String titile, final String[] array,
			final TextView tv) {

	}
}
