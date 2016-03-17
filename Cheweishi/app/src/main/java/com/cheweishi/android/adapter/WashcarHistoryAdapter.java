package com.cheweishi.android.adapter;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.activity.InformationNoteActivity;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.JSONCallback;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.LoginMessage;
import com.cheweishi.android.entity.WashcarHistoryVO;
import com.cheweishi.android.tools.DialogTool;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.CustomDialog;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;

/**
 * 洗车店历史记录
 * 
 * @author zhangq
 * 
 */
public class WashcarHistoryAdapter extends MyBaseAdapter<WashcarHistoryVO>
		implements JSONCallback {
	private ViewHolder mViewHolder;
	private XUtilsImageLoader imageLoader;
	private int colorGray;
	private int colorGrayNormal;
	private CustomDialog.Builder builder;
	private CustomDialog phoneDialog;
	private HttpBiz httpBiz;
	private BaseActivity context;
	private WashcarHistoryVO vo;
	private String userId, orderId;
	private int mPosition;

	public WashcarHistoryAdapter(List<WashcarHistoryVO> mData,
			BaseActivity mContext) {
		super(mData, mContext);
		this.context = mContext;

		colorGray = mContext.getResources().getColor(R.color.gray);
		colorGrayNormal = mContext.getResources().getColor(R.color.gray_normal);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.listview_item_washcar_history, null);
			mViewHolder = new ViewHolder(convertView);
			mViewHolder.btn_orderConfirm
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Log.i("washcarorder", "washcarorder");
							System.out.println("washcarorder===washcarorder");
							showPhoneDialog(position);

						}

					});
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		if (mData != null && mData.size() > position) {
			vo = mData.get(position);

			if (!StringUtil.isEmpty(vo)) {

				imageLoader.load(mViewHolder.imgIcon,
						API.DOWN_IMAGE_URL + vo.getImg());
				mViewHolder.tvName.setText(vo.getName());
				mViewHolder.tvOrderNumber.setText(getString("订单号：",
						vo.getUno(), colorGrayNormal));
				Log.i("result",
						"=position==" + position + "=Uno=" + vo.getUno()
								+ "==mData.size=" + mData.size());
				if (!StringUtil.isEmpty(vo.getPlate())) {
					mViewHolder.tvPlate.setText(getString("车牌号：",
							vo.getPlate(), colorGray));
				} else {
					mViewHolder.tvPlate.setText(getString("车牌号：", "--",
							colorGray));
				}

				mViewHolder.tvOrderTime.setText(getString("预约时间：",
						vo.getDate(), colorGray));

				// status-状态：0取消，1预约，2确认
				if ("2".equals(vo.getStatus())) {
					mViewHolder.imgIndex
							.setImageResource(R.drawable.dingdan_yiyong2x);
					mViewHolder.tvOutDate.setText(getString("失效时间：",
							vo.getDeal(), colorGray));
					mViewHolder.imgIndex.setVisibility(View.VISIBLE);
					mViewHolder.btn_orderConfirm.setVisibility(View.GONE);
					mViewHolder.btn_orderConfirm.setClickable(true);
				} else if ("0".equals(vo.getStatus())) {
					mViewHolder.imgIndex
							.setImageResource(R.drawable.dingdan_guoqi2x);
					mViewHolder.tvOutDate.setText(getString("失效时间：",
							vo.getDeal(), colorGray));
					mViewHolder.imgIndex.setVisibility(View.VISIBLE);
					mViewHolder.btn_orderConfirm.setVisibility(View.GONE);
					mViewHolder.btn_orderConfirm.setClickable(true);
				} else if ("4".equals(vo.getStatus())) {
					mViewHolder.imgIndex.setVisibility(View.GONE);
					mViewHolder.tvOutDate.setText(getString("失效时间：", "--",
							colorGray));
					mViewHolder.imgIndex.setVisibility(View.VISIBLE);
					mViewHolder.btn_orderConfirm.setVisibility(View.VISIBLE);
					mViewHolder.btn_orderConfirm.setText("争议订单");
					mViewHolder.btn_orderConfirm.setClickable(false);
					mViewHolder.btn_orderConfirm
							.setBackgroundResource(R.drawable.login_gray_reside);
				}

				else if ("1".equals(vo.getStatus())) {
					mViewHolder.imgIndex
							.setImageResource(R.drawable.yuyuezhong2x);
					mViewHolder.tvOutDate.setText(getString("失效时间：", "--",
							colorGray));
					mViewHolder.imgIndex.setVisibility(View.VISIBLE);
					mViewHolder.btn_orderConfirm.setVisibility(View.GONE);
					mViewHolder.btn_orderConfirm.setClickable(true);
				} else {
					mViewHolder.tvOutDate.setText(getString("失效时间：",
							vo.getDeal(), colorGray));
					mViewHolder.btn_orderConfirm.setVisibility(View.VISIBLE);
					mViewHolder.imgIndex.setVisibility(View.GONE);
					mViewHolder.btn_orderConfirm.setClickable(true);

				}
			}
		}
		return convertView;
	}

	private SpannableString getString(String front, String str, int color) {
		int len1 = front.length();
		int len2 = len1 + str.length();

		SpannableString builder = new SpannableString(front + str);
		builder.setSpan(new ForegroundColorSpan(color), len1, len2,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return builder;
	}

	class ViewHolder {
		private ImageView imgIcon;
		private ImageView imgIndex;
		private TextView tvName;
		private TextView tvOrderNumber;
		private TextView tvPlate;
		private TextView tvOrderTime;
		private TextView tvOutDate;
		private Button btn_orderConfirm;

		public ViewHolder(View v) {
			imgIcon = (ImageView) v.findViewById(R.id.imgIcon);
			imgIndex = (ImageView) v.findViewById(R.id.imgIndex);
			tvName = (TextView) v.findViewById(R.id.tvName);
			tvOrderNumber = (TextView) v.findViewById(R.id.tvOrderNumber);
			tvPlate = (TextView) v.findViewById(R.id.tvPlate);
			tvOrderTime = (TextView) v.findViewById(R.id.tvOrderTime);
			tvOutDate = (TextView) v.findViewById(R.id.tvOutDate);
			btn_orderConfirm = (Button) v.findViewById(R.id.btn_orderConfirm);
		}
	}

	/**
	 * 联系客服对话框
	 */
	private void showPhoneDialog(final int po) {
		mPosition = po;
		userId = mData.get(mPosition).getId();
		orderId = mData.get(mPosition).getUno();
		builder = new CustomDialog.Builder(mContext);
		builder.setMessage(R.string.order_itme_countersign);
		builder.setTitle(R.string.order_itme_title_countersign);

		builder.setPositiveButton(R.string.order_itme_yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
						Log.i("washcarorder", "result===messagefront");
						System.out.println("washcarorder===messagefront");
						LoginMessage loginMessage = LoginMessageUtils
								.getLoginMessage(context);

						if ((!(StringUtil.isEmpty(loginMessage)) && (!StringUtil
								.isEmpty(loginMessage.getUid())))) {
							Log.i("washcarorder", "result===requesttop");
							System.out.println("washcarorder===requesttop");
							httpBiz = new HttpBiz(context);
							RequestParams mRequestParams = new RequestParams();
							mRequestParams.addBodyParameter("uid",
									loginMessage.getUid());
							mRequestParams.addBodyParameter("key",
									loginMessage.getKey());
							mRequestParams.addBodyParameter("uno", mData
									.get(po).getUno());
							Log.i("washcarorder", "result===requestmiddele");
							System.out.println("washcarorder===requestmiddele");
							httpBiz.httPostData(10001, API.CONFIRM_AN_ORDER,
									mRequestParams, WashcarHistoryAdapter.this);
							Log.i("washcarorder", "result===requestSUCC");
							System.out.println("washcarorder===requestSUCC");
						} else {
							System.out.println("washcarorder===loginnull");
							Log.i("washcarorder", "result===loginnull");
						}
					}
				});

		builder.setNegativeButton(R.string.order_itme_no,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent();
						intent.setClass(mContext, InformationNoteActivity.class);
						intent.putExtra("userId", userId);
						intent.putExtra("orderId", orderId);
						mContext.startActivity(intent);

					}
				});
		phoneDialog = builder.create();
		phoneDialog.show();
	}

	@Override
	public void receive(int type, String data) {
		switch (type) {
		case 400:
			Log.i("washcarorder", "result===400");
			break;
		case 10001:
			parseWashCarOrderItemJSON(data);
			break;
		}
	}

	private void parseWashCarOrderItemJSON(String results) {
		if (StringUtil.isEmpty(results)) {
			Log.i("washcarorder", "result===null");
			return;
		}
		try {
			Log.i("washcarorder", results);
			JSONObject jsonObject = new JSONObject(results);

			if ("SUCCESS".equals(jsonObject.get("operationState"))) {
				// mViewHolder.btn_orderConfirm.setVisibility(View.GONE);
				mData.get(mPosition).setStatus("2");
				notifyDataSetChanged();
			} else if ("RELOGIN".equalsIgnoreCase(jsonObject
					.optString("operationState"))) {
				DialogTool.getInstance(mContext).showConflictDialog();
			} else if ("FAIL".equals(jsonObject.opt("operationState"))) {
				Toast.makeText(mContext,
						jsonObject.optJSONObject("data").optString("msg"),
						Toast.LENGTH_SHORT).show();
			} else if ("DEFAULT".equals(jsonObject.opt("operationState"))) {
				Toast.makeText(mContext,
						jsonObject.optJSONObject("data").optString("msg"),
						Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void downFile(int type, ResponseInfo<File> arg0) {
		// TODO Auto-generated method stub

	}
}
