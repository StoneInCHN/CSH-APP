package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.CarTypeCarBrandExpandableListViewAdapter;
import com.cheweishi.android.adapter.CarTypeCarModelExpandableListViewAdapter;
import com.cheweishi.android.biz.HttpBiz;
import com.cheweishi.android.config.API;
import com.cheweishi.android.dialog.ProgrosDialog;
import com.cheweishi.android.entity.CarType;
import com.cheweishi.android.entity.Carobject;
import com.cheweishi.android.utils.StringUtil;
import com.cheweishi.android.widget.AssortView;
import com.cheweishi.android.widget.AssortView.OnTouchAssortListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.RequestParams;

public class CarTypeCarBrandModelActivity extends BaseActivity implements
		OnChildClickListener, OnClickListener {

	private static final int CODE_BRAND = 1001;
	private static final int CODE_MODEL = 1002;
	private static final int CODE_STYLE = 1003;
	private List<Carobject> brandList = new ArrayList<Carobject>();
	private List<Carobject> mbrandList = new ArrayList<Carobject>();
	private List<Carobject> modelList = new ArrayList<Carobject>();
	private List<Carobject> mmodelList = new ArrayList<Carobject>();
	private CarTypeCarBrandExpandableListViewAdapter brandExpandableListViewAdapter;
	private CarTypeCarModelExpandableListViewAdapter modelExpandableListViewAdapter;
	private ExpandableListView brandExpandableListView;
	private ExpandableListView modelExpandableListView;
	private AssortView assortView;
	private DrawerLayout mDrawerLayout;
	private TextView title;
	private Button left_action;
	private TextView right_action;
	private String carModelName, carBrandName;
	private String mResultFirstId;
	private String mResultLastId;
	public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	boolean falg = false;
	private String url;
	private String mResultFirstName;
	private String mResultLastName;
	private String brandUrl = "";
	/**
	 * 汽车品牌item，车型分类item事件
	 */
	private String pinyinNum = "";

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		brandList.clear();
		mbrandList.clear();
		modelList.clear();
		mmodelList.clear();
		setContentView(R.layout.null_view);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cartype_carbrandmodel_layout);
		init();
	}

	/**
	 * 初始化控件
	 */
	public void init() {
		httpBiz = new HttpBiz(this);
		title = (TextView) findViewById(R.id.title);
		left_action = (Button) findViewById(R.id.left_action);
		left_action.setText(R.string.back);
		left_action.setOnClickListener(this);
		right_action = (TextView) findViewById(R.id.right_action);
		right_action.setVisibility(View.GONE);
		title.setText(R.string.model_choose);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		mDrawerLayout.setDrawerListener(onDrawerListener);
		httpBiz = new HttpBiz(this);
		brandExpandableListView = (ExpandableListView) findViewById(R.id.elv_carbrand);
		brandExpandableListView.setChildDivider(getResources().getDrawable(
				R.color.line_gray));
		brandExpandableListView.setOnGroupClickListener(onGroupClickListener);
		assortView = (AssortView) findViewById(R.id.assort);
		assortView.setOnTouchAssortListener(letterListener);
		brandExpandableListView.setOnChildClickListener(this);
		modelExpandableListView = (ExpandableListView) findViewById(R.id.elv_carmodel);
		modelExpandableListView.setChildDivider(getResources().getDrawable(
				R.color.line_gray));
		modelExpandableListView.setOnGroupClickListener(onGroupClickListener);
		modelExpandableListView.setOnChildClickListener(childListener);
		connectToServerModel();
	}

	// 设置品牌group点击无事件，让expandableListView可以保持一直打开的状态
	private OnGroupClickListener onGroupClickListener = new OnGroupClickListener() {
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			return true;
		}
	};

	/**
	 * 对抽屉进行监听
	 */
	private DrawerListener onDrawerListener = new DrawerListener() {

		@Override
		public void onDrawerStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onDrawerSlide(View arg0, float arg1) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onDrawerOpened(View arg0) {
			// TODO Auto-generated method stub
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		}

		@Override
		public void onDrawerClosed(View arg0) {
			// TODO Auto-generated method stub
			mDrawerLayout
					.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			CarTypeCarBrandModelActivity.this.modelList.clear();
			setModelDataAdapter();
		}
	};

	private void parseBrandJSON(String result) {
		ProgrosDialog.closeProgrosDialog();
		this.brandList.clear();
		if (StringUtil.isEmpty(result)) {
			showToast(R.string.data_fail);
		} else {
			try {
				JSONObject object1 = new JSONObject(result);
				if (StringUtil.isEquals(object1.optString("state"),
						API.returnSuccess, true)) {
					JSONObject object2 = object1.optJSONObject("data");
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<List<Carobject>>() {
					}.getType();
					this.brandList = gson.fromJson(object1.optString("data"),
							type);
					this.handler.sendEmptyMessage(CODE_BRAND);
				} else {
					showToast(object1.optJSONObject("data").getString("msg"));
				}

				//
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void parseModelJSON(String result) {
		ProgrosDialog.closeProgrosDialog();
		this.modelList.clear();
		if (StringUtil.isEmpty(result)) {
			showToast(R.string.data_fail);
		} else {
			try {
				JSONObject object1 = new JSONObject(result);
				if (StringUtil.isEquals(object1.optString("state"),
						API.returnSuccess, true)) {
					JSONObject object2 = object1.optJSONObject("data");
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<List<CarType>>() {
					}.getType();
					Carobject co = new Carobject();
					co.setName(carBrandName);
					co.setType(carBrandName);
					List<CarType> listCarType = gson.fromJson(object1.optString("data"),
							type);
					co.setList(listCarType);
					if(this.modelList!=null) {
					this.modelList.clear();
					}
					this.modelList.add(co);
//					this.modelList = gson.fromJson(object1.optString("data"),
//							type);
					this.handler.sendEmptyMessage(CODE_MODEL);
				} else {
					showToast(object1.optJSONObject("data").getString("msg"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void parseStyleJSON(String result) {
		ProgrosDialog.closeProgrosDialog();
		if (!StringUtil.isEmpty(result)) {
			try {
				JSONObject object1 = new JSONObject(result);
				if (StringUtil.isEquals(object1.optString("state"),
						API.returnSuccess, true)) {

					JSONArray jsonArray = object1.optJSONArray("data");
					if (jsonArray.length() >= 1) {
						Intent data = new Intent(this,
								CarTypeCarStyleActivity.class);

						data.putExtra("json", result);
						data.putExtra("brandGroup", this.pinyinNum);
						data.putExtra("brandGroupName", this.mResultFirstName);
						data.putExtra("modelGroupName", this.mResultLastName);
						data.putExtra("url", this.url);
						this.startActivityForResult(data, 1000);
					} else {
						showToast(R.string.no_infoForModel);
					}
				} else {
					showToast(object1.optJSONObject("data").getString("msg"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			showToast(R.string.data_fail);
		}
	}

	@Override
	public void receive(int type, String data) {
		// TODO Auto-generated method stub
		System.out.println("品牌=======" + data);
		switch (type) {
		case 10001:
			parseBrandJSON(data);
			break;
		case 10002:
			parseModelJSON(data);
			break;
		case 10003:
			parseStyleJSON(data);
			break;
		}
	}

	/**
	 * 请求车系
	 * 
	 * @param id
	 */
	private void connectToServerStyle(String id) {
		ProgrosDialog.openDialog(this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("grade", "2");
		rp.addBodyParameter("parent", id);
		httpBiz.httPostData(10002, API.GET_MODEL_URL, rp, this);

	}

	/**
	 * 请求车型
	 * 
	 * @param parentId
	 */
	private void commitToServer(String parentId) {
		ProgrosDialog.openDialog(this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("grade", "3");
		rp.addBodyParameter("parent", parentId);
		httpBiz.httPostData(10003, API.GET_MODEL_URL, rp, this);

	}

	/**
	 * 请求车辆品牌
	 */
	private void connectToServerModel() {
		ProgrosDialog.openDialog(this);
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("grade", "1");
		rp.addBodyParameter("parent", "0");
		httpBiz.httPostData(10001, API.GET_MODEL_URL, rp, this);

	}

	/**
	 * handler 更新ui
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_BRAND:
				if (brandList.size() >= 0) {
					brandExpandableListViewAdapter = new CarTypeCarBrandExpandableListViewAdapter(
							CarTypeCarBrandModelActivity.this, brandList);
					brandExpandableListView
							.setAdapter(brandExpandableListViewAdapter);
					// 展开某组的列表
					for (int i = 0; i < brandExpandableListViewAdapter
							.getGroupCount(); i++) {
						brandExpandableListView.expandGroup(i);
					}
				}

				break;
			case CODE_MODEL:
				setModelDataAdapter();
				break;

			case CODE_STYLE:
				break;
			default:
				break;
			}
		};
	};

	private void setModelDataAdapter() {
		modelExpandableListViewAdapter = new CarTypeCarModelExpandableListViewAdapter(
				CarTypeCarBrandModelActivity.this, modelList);
		modelExpandableListView.setAdapter(modelExpandableListViewAdapter);
		// 展开某组的列表
		for (int i = 0; i < modelExpandableListViewAdapter.getGroupCount(); i++) {
			modelExpandableListView.expandGroup(i);
		}
	}

	/**
	 * 导航首字母事件监听
	 */
	OnTouchAssortListener letterListener = new OnTouchAssortListener() {

		@Override
		public void onTouchAssortListener(int index) {
			if (brandExpandableListView != null && brandList.size() > index)
				brandExpandableListView.setSelectedGroup(index);
		}

		@Override
		public void onTouchAssortUP() {
		}
	};

	/**
	 * 返回按钮，收回盒子效果按钮事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			CarTypeCarBrandModelActivity.this.finish();
			break;
		case R.id.ibtn_retract:
			this.modelList.clear();

			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			break;

		default:
			break;
		}
	}

	private OnChildClickListener childListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView arg0, View arg1,
				int groupPosition, int childPosition, long arg4) {
			// TODO Auto-generated method stub
			mResultLastId = modelList.get(groupPosition).getList()
					.get(childPosition).getId();
			mResultLastName = modelList.get(groupPosition).getList()
					.get(childPosition).getName();
			url = modelList.get(groupPosition).getList().get(childPosition)
					.getLogo();
			commitToServer(mResultLastId);
			return true;
		}

	};

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		switch (parent.getId()) {
		case R.id.elv_carbrand:
			carBrandName = brandList.get(groupPosition).getList()
					.get(childPosition).getName();
			brandUrl = brandList.get(groupPosition).getList()
					.get(childPosition).getLogo();
			mResultFirstName = carBrandName;
			pinyinNum = brandList.get(groupPosition).getName();

			// 按钮按下，将抽屉打开
			mDrawerLayout.openDrawer(Gravity.RIGHT);
			connectToServerStyle(brandList.get(groupPosition).getList()
					.get(childPosition).getId());
			mResultFirstId = brandList.get(groupPosition).getList()
					.get(childPosition).getId();
			mResultFirstName = brandList.get(groupPosition).getList()
					.get(childPosition).getName();
			break;
		case R.id.elv_carmodel:
			carModelName = modelList.get(groupPosition).getList()
					.get(childPosition).getName();
			mResultLastId = modelList.get(groupPosition).getList()
					.get(childPosition).getId();
			handler.sendEmptyMessage(CODE_STYLE);
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			Intent intent = new Intent();
			switch (requestCode) {
			case 1000:
				String styleId = data.getStringExtra("styleId");
				intent.putExtra("mResultFirstName", mResultFirstName);
				intent.putExtra("mResultLastName", mResultLastName);
				intent.putExtra("mResultFirstId", mResultFirstId);
				intent.putExtra("mResultLastId", mResultLastId);
				intent.putExtra("carName", carBrandName + carModelName);
				intent.putExtra("carLogoUrl", brandUrl);
				intent.putExtra("styleId", styleId);
				setResult(RESULT_OK, intent);
				CarTypeCarBrandModelActivity.this.finish();
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}
	}

	public String getmResultFirstName() {
		return mResultFirstName;
	}

	public void setmResultFirstName(String mResultFirstName) {
		this.mResultFirstName = mResultFirstName;
	}

	public String getmResultLastName() {
		return mResultLastName;
	}

	public void setmResultLastName(String mResultLastName) {
		this.mResultLastName = mResultLastName;
	}

}
