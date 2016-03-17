package com.cheweishi.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.CircleInformation;
import com.cheweishi.android.tools.ScreenTools;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 车资讯详情页面
 * 
 * @author Xiaojin
 * 
 */

public class CircleInformationDetailActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.tv_informationDetailTitle)
	private TextView tv_informationTitle;
	@ViewInject(R.id.tv_informationDetailContent)
	private TextView tv_informationContent;
	@ViewInject(R.id.img_informationDetailTitle)
	private ImageView img_informationTitle;
	private CircleInformation circleInformation;// = new CircleInformation();
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.left_action)
	private Button left_action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circle_information_detail);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		httpBiz = new HttpBiz(this);
		title.setText(R.string.detail);
		left_action.setText(R.string.back);
		circleInformation = new CircleInformation();
		circleInformation.setH(1);
		circleInformation.setW(1);
		setLayout();
		connectToServer();
	}

	@OnClick({ R.id.left_action })
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_action:
			CircleInformationDetailActivity.this.finish();
			break;
		}
	}

	private void connectToServer() {
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("circle", getIntent().getStringExtra("id"));
		httpBiz.httPostData(1, API.INFORMATION2_URL, rp, this);
		ProgrosDialog.openDialog(this);
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		switch (type) {
		case 1:
			parseJSON(data);
			break;
		}
	}

	private void parseJSON(String result) {
		ProgrosDialog.closeProgrosDialog();
		if (!StringUtil.isEmpty(result)) {
			System.out.println("chezixun============" + result);
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (StringUtil.isEquals(jsonObject.optString("operationState"),
						API.returnSuccess, true)) {
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<CircleInformation>() {
					}.getType();
					circleInformation = gson.fromJson(
							jsonObject.optJSONObject("data")
									.optString("circle"), type);
					setLayout();
					XUtilsImageLoader.getxUtilsImageLoader(
							CircleInformationDetailActivity.this,
							R.drawable.service_default,img_informationTitle,
							API.DOWN_IMAGE_URL + circleInformation.getPic());
					tv_informationTitle.setText(circleInformation.getTitle());
					tv_informationContent.setText(circleInformation
							.getContent());

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void setLayout() {
		ViewGroup.LayoutParams lp = img_informationTitle.getLayoutParams();
		lp.height = (ScreenTools
				.getScreentWidth(CircleInformationDetailActivity.this) - (int) CircleInformationDetailActivity.this
				.getResources().getDimension(R.dimen.small_padding))
				* circleInformation.getH() / circleInformation.getW();
		img_informationTitle.setLayoutParams(lp);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ProgrosDialog.closeProgrosDialog();
	}

}
