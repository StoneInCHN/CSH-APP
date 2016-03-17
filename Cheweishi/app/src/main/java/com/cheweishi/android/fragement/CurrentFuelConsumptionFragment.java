package com.cheweishi.android.fragement;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.CarReportActivity;
import com.cheweishi.android.config.API;
import com.cheweishi.android.config.Config;
import com.cheweishi.android.http.MyHttpUtils;
import com.cheweishi.android.http.SimpleHttpUtils;
import com.cheweishi.android.tools.LoginMessageUtils;
import com.cheweishi.android.tools.ShareTools;
import com.cheweishi.android.tools.TextViewTools;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.YouhaoView;
import com.lidroid.xutils.http.RequestParams;

public class CurrentFuelConsumptionFragment extends BaseFragment {

	private YouhaoView mYouhaoView;
	private RelativeLayout linearlayout_fragment_CurrentFuelConsumptionFragment;
	private TextView tv_speedFlag;
	private TextView tv_speed;
	private TextView tv_app;
	private Button btnShare;
	private TextView tv_congestionTime;
	private TextView tv_noSpeedData;
	private View view;
	private String time;
	private int rid;
	private String title = "嘿~你的车联网了么";
	private String content = "";
	private String titleUrl;
	private String imgUrl;
	private String speedFlag = "您当日油耗为:";
	private String percent = "";
	private String oil = "";
	private String slogan = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		time = getArguments().getString("time");
		System.out.println(time);
		rid = getArguments().getInt("rid");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.activity_current_fuel_consumption, container, false);
		this.view = view;
		init(view);
		return view;
	}

	private void init(View view) {
		linearlayout_fragment_CurrentFuelConsumptionFragment = (RelativeLayout) view
				.findViewById(R.id.linearlayout_fragment_CurrentFuelConsumptionFragment);
		btnShare = (Button) view.findViewById(R.id.btnShare);
		tv_speed = (TextView) view.findViewById(R.id.tv_speed);
		tv_app = (TextView) view.findViewById(R.id.tv_app);
		linearlayout_fragment_CurrentFuelConsumptionFragment = (RelativeLayout) view
				.findViewById(R.id.linearlayout_fragment_CurrentFuelConsumptionFragment);
		tv_speedFlag = (TextView) view.findViewById(R.id.tv_speedFlag);
		tv_congestionTime = (TextView) view
				.findViewById(R.id.tv_congestionTime);
		tv_speedFlag.setVisibility(View.VISIBLE);
		btnShare.setOnClickListener(listener);
		tv_speedFlag.setText(speedFlag);
		tv_noSpeedData=(TextView) view.findViewById(R.id.tv_noSpeedData);

		updateData(time);
		// int w = View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED);
		// int h = View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED);
		// view.measure(w, h);
		// height = view.getMeasuredHeight();
		// int width = view.getMeasuredWidth();
		// System.out.println(height + "------------" + width);
		// width =
		// getActivity().getWindowManager().getDefaultDisplay().getWidth();
		// height = getActivity().getWindowManager().getDefaultDisplay()
		// .getHeight();

	}

	@Override
	public void updateData(String time) {
		RequestParams params = new RequestParams(HTTP.UTF_8);
		if (LoginMessageUtils.getLoginMessage(mContext) != null) {
			params.addBodyParameter("uid",
					LoginMessageUtils.getLoginMessage(mContext).getUid());
			params.addBodyParameter("key",
					LoginMessageUtils.getLoginMessage(mContext).getKey());
			params.addBodyParameter("cid",
					LoginMessageUtils.getLoginMessage(mContext).getCar()
							.getCid());
			params.addBodyParameter("type", "oil");
			params.addBodyParameter("time", time);
			params.addBodyParameter("rid", rid + "");
			String utlString = API.ONTHATDAY_URL;
			SimpleHttpUtils myHttpUtils = new SimpleHttpUtils(mContext,
					params, utlString, handler);
			myHttpUtils.PostHttpUtils();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			((CarReportActivity) mContext).disMissCustomDialog();
			if (msg.what == 400) {
				showToast(Config.ERROR);
				initView(view);
				mYouhaoView.setVisibility(View.GONE);
				linearlayout_fragment_CurrentFuelConsumptionFragment
						.setVisibility(View.VISIBLE);
				tv_speed.setText(0 + "");
				tv_app.setVisibility(View.GONE);
				tv_congestionTime.setVisibility(View.GONE);
				btnShare.setBackgroundResource(R.drawable.shaiyishai_nodata);
				btnShare.setTextColor(mContext.getResources().getColor(R.color.huise));
				btnShare.setClickable(false);
			} else {

				String result = (String) msg.obj;
				if (result != null && !result.equals("")) {
					analysis(result);
				} else {
					showToast("服务器无数据");
					initView(view);
					mYouhaoView.setVisibility(View.GONE);
					linearlayout_fragment_CurrentFuelConsumptionFragment
							.setVisibility(View.VISIBLE);
					tv_speed.setText(0 + "");
					tv_app.setVisibility(View.GONE);
					tv_congestionTime.setVisibility(View.GONE);
					btnShare.setBackgroundResource(R.drawable.shaiyishai_nodata);
					btnShare.setTextColor(getResources()
							.getColor(R.color.huise));
					btnShare.setClickable(false);
				}
			}
		};
	};


	@SuppressWarnings("deprecation")
	protected void analysis(String result) {

		try {
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.optString("operationState").equals("SUCCESS")) {
				JSONObject jsonObject2 = jsonObject.optJSONObject("data");
				if (jsonObject2.optString("status").equals("1")) {
					percent = jsonObject2.optString("percent");
					titleUrl = jsonObject2.optString("shareUrl");
					imgUrl = jsonObject2.optString("shareIcon");
					String surplus = jsonObject2.optString("surplus");
					oil = jsonObject2.optString("oil");
					if (StringUtil.getDouble(oil) < 4) {
						tv_app.setText("节能减排，我做贡献！");
						slogan = "节能减排，我做贡献！";
						TextViewTools.setTextViewFontsStyle(mContext,
								tv_app);
					} else if (4 < StringUtil.getDouble(oil)
							&& StringUtil.getDouble(oil) < 16) {
						slogan = "晒晒太阳，我的生活在路上！";
						tv_app.setText("晒晒太阳兜兜风，我的生活在路上！");
						TextViewTools.setTextViewFontsStyle(mContext,
								tv_app);
					} else {
						slogan = "两桶油算啥，我养活的！";
						tv_app.setText("两桶油算啥，我养活的！");
						TextViewTools.setTextViewFontsStyle(mContext,
								tv_app);
					}
					if (percent != null && !percent.equals("")) {
						SpannableStringBuilder style = new SpannableStringBuilder(
								"击败了全国" + percent + "的车友");
						style.setSpan(new ForegroundColorSpan(mContext.getResources()
								.getColor(R.color.orange)), 5, 8,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						tv_congestionTime.setText(style);

						if (oil != null && !oil.equals("")
								&& !oil.equals("无法获取") && surplus != null
								&& surplus.equals("")
								&& !surplus.equals("无法获取")) {
							initView(view);
							linearlayout_fragment_CurrentFuelConsumptionFragment
									.setVisibility(View.GONE);
							mYouhaoView.setVisibility(View.VISIBLE);
							tv_app.setVisibility(View.VISIBLE);
							tv_congestionTime.setVisibility(View.VISIBLE);
							btnShare.setClickable(true);
							btnShare.setTextColor(mContext.getResources().getColor(
									R.color.orange_text_color));
							btnShare.setBackgroundDrawable(mContext.getResources()
									.getDrawable(R.drawable.shaiyishai));
							mYouhaoView.initDate(mContext, 0.5f, 0.3f,
									oil, surplus);
							tv_speed.setText(oil + "L");
						} else if (oil != null && !oil.equals("")
								&& !oil.equals("无法获取")) {
							initView(view);
							tv_app.setVisibility(View.VISIBLE);
							tv_congestionTime.setVisibility(View.VISIBLE);
							linearlayout_fragment_CurrentFuelConsumptionFragment
									.setVisibility(View.GONE);
							btnShare.setClickable(true);
							btnShare.setBackgroundDrawable(mContext.getResources()
									.getDrawable(R.drawable.shaiyishai));
							btnShare.setTextColor(mContext.getResources().getColor(
									R.color.orange_text_color));
							mYouhaoView.setVisibility(View.VISIBLE);
							mYouhaoView.initDate(mContext, 0.5f, 0.3f,
									oil, surplus);
							tv_speed.setText(oil + "L");
						} else {
							initView(view);
							mYouhaoView.setVisibility(View.GONE);
							linearlayout_fragment_CurrentFuelConsumptionFragment
									.setVisibility(View.VISIBLE);
							tv_speed.setText(0 + "");
							tv_app.setVisibility(View.GONE);
							tv_congestionTime.setVisibility(View.GONE);
							btnShare.setBackgroundResource(R.drawable.shaiyishai_nodata);
							btnShare.setTextColor(mContext.getResources().getColor(
									R.color.huise));
							btnShare.setClickable(false);
						}
					}
				} else {
					initView(view);
					mYouhaoView.setVisibility(View.GONE);
					linearlayout_fragment_CurrentFuelConsumptionFragment
							.setVisibility(View.VISIBLE);
					tv_speed.setText(0 + "");
					tv_app.setVisibility(View.GONE);
					tv_congestionTime.setVisibility(View.GONE);
					btnShare.setBackgroundResource(R.drawable.shaiyishai_nodata);
					btnShare.setTextColor(mContext.getResources()
							.getColor(R.color.huise));
					btnShare.setClickable(false);
				}
			} else {
				showToast(jsonObject.optString("title"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void initView(View view) {
		mYouhaoView = (YouhaoView) view.findViewById(R.id.youhaoview);
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btnShare:
				content = speedFlag + oil + "L,击败了全国" + percent + "的车友,"
						+ slogan;
				System.out.println(content);
				System.out.println(imgUrl);
				ShareTools.showShare(mContext, title, content, titleUrl,
						imgUrl);
				break;
			default:
				break;
			}

		}
	};

}
