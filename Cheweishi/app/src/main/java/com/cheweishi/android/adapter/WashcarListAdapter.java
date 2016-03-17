package com.cheweishi.android.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.WashCarPayActivity;
import com.cheweishi.android.activity.WashcarDetailsActivity;
import com.cheweishi.android.activity.WashcarListActivity;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.JSONCallback;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.dialog.WashCarDateDialog;
import com.cheweishi.android.dialog.WashCarDateDialog.Builder;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.entity.WashcarVO;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.utils.CustomProgressDialog;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.utils.mapUtils.NavigationUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;

public class WashcarListAdapter extends MyBaseAdapter<WashcarVO> {
	private ViewHolder mHolder;
	private HttpBiz httpBiz;
	private NavigationUtil mNavigationUtil;
	private LoginMessage loginMessage;
	private WashcarVO selectVO;
	private WashCarDateDialog dateDialog;
	private XUtilsImageLoader imageLoader;
	private CustomDialog.Builder builder;

	// private int count;

	public WashcarListAdapter(List<WashcarVO> mData, Context mContext,
			LoginMessage loginMessage) {
		super(mData, mContext);
		// this.count = count;
		httpBiz = new HttpBiz(mContext);
		this.loginMessage = loginMessage;
		mNavigationUtil = new NavigationUtil(mContext);
	}

	public void setData(List<WashcarVO> mData) {
		this.mData = mData;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_listview_washcarlist,
					null);
			mHolder = new ViewHolder(convertView);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		final WashcarVO vo = mData.get(position);
		convertView.setId(5000 + position);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clickParent(vo);
			}
		});

		imageLoader.load(mHolder.imgIcon, API.DOWN_IMAGE_URL + vo.getPic());
		mHolder.tvName.setText(vo.getName());
		mHolder.tvTime.setText(vo.getDate());
		mHolder.tvAddress.setText(vo.getAddress());
		// mHolder.layoutOrder.setId(6000 + position);
		// mHolder.layoutOrder.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // if (count == 4) {
		// // showNoDialog();
		// // } else {
		// // showDateDiaglog(vo);
		// // }
		// clickParent(vo);
		// }
		// });
		// mHolder.layoutGo.setId(7000 + position);
		// mHolder.layoutGo.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// clickNavi(vo);
		// }
		// });
		mHolder.tv_wash_car_pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, WashCarPayActivity.class);
				// tv_wash_money.getText();
				intent.putExtra("pay_num", "0.01");
				mContext.startActivity(intent);
			}
		});
		mHolder.tv_old_wash_money.getPaint().setFlags(
				Paint.STRIKE_THRU_TEXT_FLAG);
		return convertView;
	}

	/**
	 * 不能进行预约提示
	 */
	private void showNoDialog() {
		builder = new CustomDialog.Builder(mContext);
		builder.setMessage("您本月免费次数已用完。亲~下个月再来吧!");
		builder.setTitle("提示");
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private void showDateDiaglog(final WashcarVO vo) {
		if (dateDialog == null) {
			final Builder builder = new WashCarDateDialog.Builder(mContext);
			builder.setPositiveButton("",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String date = builder.getSelectPosition();
							String selectedDate = date.substring(1,
									date.length() - 1);
							clickOrder(vo, selectedDate);
							dateDialog.dismiss();
						}
					});
			builder.setNegativeButton("",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dateDialog.dismiss();

						}
					});
			builder.setDate(Calendar.getInstance().getTime());

			dateDialog = builder.create();
			builder.setCount("本月剩余次数为" + (4 - Constant.WASHCAR_COUNT) + "次");
		}
		if (!dateDialog.isShowing()) {
			dateDialog.show();
		}
	}

	private void clickOrder(WashcarVO vo, String time) {
		if (loginMessage == null || loginMessage.getUid() == null) {
			return;
		}
		selectVO = vo;
		showProgressDialog();
		RequestParams mRequestParams = new RequestParams();
		mRequestParams.addBodyParameter("uid", loginMessage.getUid());
		mRequestParams.addBodyParameter("key", loginMessage.getKey());
		mRequestParams.addBodyParameter("cid", loginMessage.getCar().getCid());
		mRequestParams.addBodyParameter("cw_id", vo.getCwId());
		mRequestParams.addBodyParameter("time", time);
		httpBiz.httPostData(1001, API.WASHCAR_ORDER_SHOP, mRequestParams,
				callBack);
	}

	private void clickNavi(WashcarVO vo) {
		mNavigationUtil.startNavigation(MyMapUtils.getLatitude(mContext),
				MyMapUtils.getLongitude(mContext),
				MyMapUtils.getAddress(mContext),
				StringUtil.getDouble(vo.getLat()),
				StringUtil.getDouble(vo.getLon()), vo.getAddress());
	}

	private void clickParent(WashcarVO vo) {
		Intent intent = new Intent(mContext, WashcarDetailsActivity.class);
		intent.putExtra("index", WashcarDetailsActivity.INDEX_DETAIL);
		intent.putExtra("return", WashcarDetailsActivity.INDEX_FROM_LIST);
		intent.putParcelableArrayListExtra("list",
				(ArrayList<? extends Parcelable>) mData);
		intent.putExtra("vo", vo);
		// intent.putExtra("count", count);
		mContext.startActivity(intent);
		// ((Activity) mContext).finish();
	}

	JSONCallback callBack = new JSONCallback() {

		@Override
		public void receive(int type, String data) {
			disMissProgressDialog();
			try {
				JSONObject json = new JSONObject(data);
				if ("SUCCESS".equals(json.get("operationState"))) {
					json = json.getJSONObject("data");
					json.getString("status");

					Intent intent = new Intent(mContext,
							WashcarDetailsActivity.class);
					intent.putExtra("index",
							WashcarDetailsActivity.INDEX_ORDER_DETAIL);
					selectVO.setUno(json.getString("uno"));
					selectVO.setTime(json.getString("time"));
					intent.putExtra("vo", selectVO);
					mContext.startActivity(intent);
					((Activity) mContext).finish();
				} else if ("RELOGIN".equalsIgnoreCase(json
						.getString("operationState"))) {
					DialogTool.getInstance(mContext).showConflictDialog();
				} else {
					json = json.getJSONObject("data");
					Toast.makeText(mContext, json.getString("msg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void downFile(int type, ResponseInfo<File> arg0) {

		}
	};

	private CustomProgressDialog progressDialog;

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.getInstance(mContext);
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}

	private void disMissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	class ViewHolder {
		private ImageView imgIcon;
		private TextView tvName;
		// private TextView tvMile;
		private TextView tvAddress;
		private TextView tvTime;
		private LinearLayout layoutOrder;
		private LinearLayout layoutGo;
		private TextView tv_wash_car_pay;
		private TextView tv_wash_money;
		private TextView tv_old_wash_money;

		public ViewHolder(View v) {
			imgIcon = (ImageView) v.findViewById(R.id.car_iv_location);
			tvName = (TextView) v.findViewById(R.id.car_tv_car_iv_location);
			tvAddress = (TextView) v.findViewById(R.id.car_xlocation);
			// layoutOrder = (LinearLayout) v.findViewById(R.id.car_lin_yuyue);
			// layoutGo = (LinearLayout) v.findViewById(R.id.car_lin_daozhequ);
			tvTime = (TextView) v.findViewById(R.id.tv_time);
			tv_wash_car_pay = (TextView) v.findViewById(R.id.tv_wash_car_pay);
			tv_wash_money = (TextView) v.findViewById(R.id.tv_wash_money);
			tv_old_wash_money = (TextView) v
					.findViewById(R.id.tv_old_wash_money);
		}
	}

}
