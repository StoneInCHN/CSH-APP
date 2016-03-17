package com.cheweishi.android.activity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.CarTypeCarStyleExpandableListViewAdapter;
import com.cheweishi.android.biz.XUtilsImageLoader;
import com.cheweishi.android.config.API;
import com.cheweishi.android.entity.CarType;
import com.cheweishi.android.entity.Carobject;
import com.cheweishi.android.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnChildClick;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnGroupClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 车辆型号选择
 * 
 * @author Xiaojin
 * 
 */
public class CarTypeCarStyleActivity extends BaseActivity implements
		OnClickListener, OnChildClickListener, OnGroupClickListener {
	@ViewInject(R.id.tv_letter)
	private TextView tvBrandGroupName;
	@ViewInject(R.id.tv_carname)
	private TextView tvBrandName;
	@ViewInject(R.id.img_carlogo)
	private ImageView carLogo;
	@ViewInject(R.id.tv_cardetailname)
	private TextView tvCarStyleName;
	@ViewInject(R.id.elv_cartype_detail)
	private ExpandableListView expandableListView;
	private CarTypeCarStyleExpandableListViewAdapter carStyleExpandableListViewAdapter;
	private List<Carobject> styleList = new ArrayList<Carobject>();
	@ViewInject(R.id.left_action)
	private Button ibtnBack;
	@ViewInject(R.id.title)
	private TextView title;
	private String brandGroupName, modelGroupName, json, carModelName,
			carModelLogo;
	private String pinyinNum;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		styleList.clear();
		setContentView(R.layout.null_view);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cartype_carstyle_layout);
		ViewUtils.inject(this);
		init();
	}

	/**
	 * 初始化控件
	 */
	private void init() {
		ibtnBack.setText(R.string.back);
		title.setText(R.string.style_choose);
		expandableListView.setChildDivider(getResources().getDrawable(
				R.color.line_gray));
		Intent intent = getIntent();
		brandGroupName = intent.getStringExtra("brandGroupName");
		modelGroupName = intent.getStringExtra("modelGroupName");
		json = intent.getStringExtra("json");
		carModelName = intent.getStringExtra("carModelName");
		carModelLogo = intent.getStringExtra("url");
		pinyinNum = intent.getStringExtra("brandGroup");
		tvBrandGroupName.setText(pinyinNum);
		tvBrandName.setText(brandGroupName);
		XUtilsImageLoader.getxUtilsImageLoader(this, R.drawable.repaire_img,
				carLogo, API.DOWN_IMAGE_URL + carModelLogo);
		tvCarStyleName.setText(carModelName);
		tvCarStyleName.setText(modelGroupName);
		styleList = getCarStyleJsonData(json);
		carStyleExpandableListViewAdapter = new CarTypeCarStyleExpandableListViewAdapter(
				this, styleList);
		expandableListView.setAdapter(carStyleExpandableListViewAdapter);
		for (int i = 0; i < carStyleExpandableListViewAdapter.getGroupCount(); i++) {
			expandableListView.expandGroup(i);
		}
	}

	/**
	 * 解析车型具体型号的json数据
	 * 
	 * @param json
	 *            json格式的数据
	 */
	protected List<Carobject> getCarStyleJsonData(String json) {
		System.out.println("车型选择===================" + json);
		try {
			JSONObject object1 = new JSONObject(json);
			if (StringUtil.isEquals(object1.optString("state"),
					API.returnSuccess, true)) {
				Carobject Carobject = new Carobject();
				Carobject.setName(modelGroupName);
				List<CarType> list = new ArrayList<CarType>();
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<List<CarType>>() {
				}.getType();
				list = gson.fromJson((object1.optString("data")), type);
				Carobject.setList(list);
				styleList.add(Carobject);
				// }
			} else {
				JSONObject object2 = object1.optJSONObject("data");
				showToast(object2.getString("msg"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return styleList;
	}

	/**
	 * back键的返回事件
	 */
	@OnClick({ R.id.left_action })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_action:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 汽车品牌item，车型分类item事件 onchildclick
	 */
	@OnChildClick({ R.id.elv_cartype_detail })
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		String styleId = styleList.get(groupPosition).getList()
				.get(childPosition).getId();
		ImageView imgCheck = (ImageView) v.findViewById(R.id.img_ifchecked);
		imgCheck.setVisibility(View.VISIBLE);
		Intent intent = new Intent();
		intent.putExtra("styleId", styleId);
		intent.putExtra("carLogo", carModelLogo);
		setResult(RESULT_OK, intent);

		finish();
		return true;
	}

	@OnGroupClick({ R.id.elv_cartype_detail })
	@Override
	public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return true;
	}

}
