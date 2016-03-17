package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.MainListViewAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.MainSellerInfo;
import com.cheweishi.android.entity.MainSellerServiceInfo;
import com.cheweishi.android.tools.EmptyTools;
import com.cheweishi.android.tools.ReLoginDialog;
import com.cheweishi.android.utils.MyMapUtils;
import com.cheweishi.android.utils.StringUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 美容列表
 * @author mingdasen
 *
 */
public class BeautyListActivity extends BaseActivity implements OnRefreshListener2<ListView>{

	@ViewInject(R.id.title)
	private TextView tvTitle;
	@ViewInject(R.id.left_action)
	private Button btnLeft;
	@ViewInject(R.id.listview)
	private PullToRefreshListView mListView;
	@ViewInject(R.id.lay_wash_beauty)
	private RelativeLayout lay_wash_beauty;
	private List<MainSellerInfo> washcarList;
//	private List<LatLng> positionList;
	private MainListViewAdapter mListViewAdapter;
//	private List<WashcarVO> mListViewLists;
	@ViewInject(R.id.tv_maintain_myCar)
	private TextView tv_maintain_myCar;
//	@ViewInject(R.id.lay_wash_beauty)
//	private RelativeLayout lay_wash_beauty;
//	@ViewInject(R.id.layout_nodata)
//	private LinearLayout layout_nodata;
	private int page = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maintain_beauty_list);
		ViewUtils.inject(this);

		/**/
		initView();
		getDataFromIntent();
	}

	private void getDataFromIntent() {
		httpBiz = new HttpBiz(this);
		RequestParams mRequestParams = new RequestParams();
		mRequestParams.addBodyParameter("uid", loginMessage.getUid());//
		mRequestParams.addBodyParameter("mobile", loginMessage.getMobile());
		mRequestParams.addBodyParameter("pageNumber",page + "");
		mRequestParams.addBodyParameter("pageSize", 20+"");
		if (hasDevice()) {
			mRequestParams.addBodyParameter("device", loginMessage
					.getCarManager().getDevice());
		} else {
			mRequestParams.addBodyParameter("device", "");
		}
		mRequestParams.addBodyParameter("lat", MyMapUtils.getLatitude(this)
				+ "");
		mRequestParams.addBodyParameter("lon",
				MyMapUtils.getLongitude(this) + "");
		// Log.i("zqtest", "uid:" + loginMessage.getUid() + "--key--:"
		// + loginMessage.getKey());
		ProgrosDialog.openDialog(this);
		httpBiz.httPostData(1001, API.CSH_BEAUTY_LIST_URL, mRequestParams,
				this);
	}

	@Override
	public void receive(int type, String data) {
		ProgrosDialog.closeProgrosDialog();
		mListView.onRefreshComplete();
		switch (type) {
		case 1001:
			doAboutBeauty(data);
			break;
		default:
			break;
		}
	}

	/**
	 * 解析洗车店列表数据
	 * 
	 * @param data
	 */
	private void doAboutBeauty(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(R.string.FAIL);
			return;
		}
		try {
			JSONObject json = new JSONObject(data);
			if (StringUtil.isEquals(API.returnSuccess, json.optString("state"), true)) {
				JSONArray array = json.optJSONArray("data");
				JSONObject jsonObject = null;
				MainSellerInfo sellerInfo = null;
				for (int i = 0; i < array.length(); i++) {
					jsonObject = array.optJSONObject(i);
					sellerInfo = new MainSellerInfo();
					sellerInfo.setName(jsonObject.optString("store_name"));
					sellerInfo.setAddress(jsonObject.optString("address"));
					sellerInfo.setImgUrl(jsonObject.optString("image_1"));//
					sellerInfo.setEvaluateImg(jsonObject.optInt("evaluateImg"));//?
					sellerInfo.setEvaluate(jsonObject.optString("evaluate"));//?
					String distance = String.format("%.2f", jsonObject.optDouble("distance")/1000);
					sellerInfo.setDistance(distance + "km");
					sellerInfo.setId(jsonObject.optString("id"));
					sellerInfo.setAppoint(jsonObject.optString("is_appoint"));
//					sellerInfo.setLon(jsonObject.getDouble("lon"));
					
					JSONArray array2 = jsonObject.optJSONArray("commodity");
					JSONObject object = null;
					List<MainSellerServiceInfo> serviceInfos = new ArrayList<MainSellerServiceInfo>();
					MainSellerServiceInfo serviceInfo = null;
					for (int j = 0; j < array2.length(); j++) {
						serviceInfo = new MainSellerServiceInfo();
						object = array2.optJSONObject(j);
						serviceInfo.setName(object.optString("goods_name"));
						serviceInfo.setfPrice(object.optString("discount_price"));
						serviceInfo.setPrice(object.optString("price"));
						serviceInfo.setId(object.optString("id"));
						serviceInfo.setIsFPrice(object.optInt("is_discount__price"));
						serviceInfo.setIsRed(object.optInt("support_red") + "");
						serviceInfo.setCate_id_2(object.optString("cate_id_2"));
						serviceInfos.add(serviceInfo);
					}
//					Log.i("result", "===serviceInfos==" + serviceInfos.size());
					sellerInfo.setServices(serviceInfos);
					washcarList.add(sellerInfo);
				}
				if (StringUtil.isEmpty(washcarList) || washcarList.size() <= 0) {
					EmptyTools.setEmptyView(this, mListView);
					EmptyTools.setImg(R.drawable.dingdanwu_icon);
					EmptyTools.setMessage("亲，暂无相关数据");
				} else {
					mListViewAdapter.setData(washcarList);
				}
			} else if (StringUtil.isEquals(API.returnRelogin, json.optString("state"), true)) {
				ReLoginDialog.getInstance(this).showDialog(json.optString("message"));
			} else {
				showToast(json.optString("message"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		lay_wash_beauty.setVisibility(View.GONE);
		tvTitle.setText("汽车美容");
		btnLeft.setText(R.string.back);
		
		mListView.setOnRefreshListener(this);
		mListView.setMode(Mode.BOTH);
		washcarList = new ArrayList<MainSellerInfo>();
		mListViewAdapter = new MainListViewAdapter(this, washcarList);
		mListView.setAdapter(mListViewAdapter);
	}

	@OnClick({ R.id.left_action, R.id.tvHistory })
	public void finishActivity(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			onBackPressed();
			break;
		case R.id.tvHistory:
			Intent intent = new Intent(this, WashcarHistoryActivity.class);
			// intent.putExtra("count", count);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

//	@OnCompoundButtonCheckedChange(R.id.cbox_map)
//	public void turnToMap(View v, boolean isChecked) {
//		Intent intent = new Intent(this, WashCarActivity.class);
//		intent.putExtra("index", WashCarActivity.INDEX_FROM_LIST);
//		intent.putParcelableArrayListExtra("list",
//				(ArrayList<? extends Parcelable>) mListViewLists);
//		startActivity(intent);
//		finish();
//	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

@Override
public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
	washcarList.clear();
//	serviceInfos.clear();
	page = 1;
	getDataFromIntent();
}

@Override
public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	page++;
	getDataFromIntent();	
}
}
