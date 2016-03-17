package com.cheweishi.android.fragement;

import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.adapter.MyStallAdapter;
import com.cheweishi.android.config.Constant;
import com.cheweishi.android.entity.ParkInfo;
import com.cheweishi.android.fragement.FindParkingSpaceMapFragment.FindParkBroadcastReceiver;
import com.cheweishi.android.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 找车位列表
 * 
 * @author 大森
 * 
 */

public class FindParkingSpaceListFragment extends BaseFragment {

	@ViewInject(R.id.mycar_xlistview)
	private ListView mycar_xlistview;
	private MyStallAdapter adapter;

	private Bundle bundle;
	private List<ParkInfo> parkInfos;
	
	private FindParkBroadcastReceiver broad;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_parking_space, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (!StringUtil.isEmpty(getArguments())) {
			parkInfos = getArguments().getParcelableArrayList("data");
//			latLng = new LatLng(getArguments().getDouble("lat"),getArguments().getDouble("lng"));
		}
//		Log.i("result", "=============list=onstart======" + parkInfos.size());
		 adapter = new MyStallAdapter(mContext, parkInfos);
		 mycar_xlistview.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("result", "=============list=onResume======");
		// 注册刷新广播
		if (broad == null) {
			broad = new FindParkBroadcastReceiver();
		}
		IntentFilter intentFilter = new IntentFilter(Constant.REFRESH_FLAG);
		mContext.registerReceiver(broad, intentFilter);
	}

	public class FindParkBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (StringUtil.isEquals(Constant.CURRENT_REFRESH,
					Constant.FIND_PARK_REFRESH, true)) {
				Log.i("result", "=================找车位更新===============");
				parkInfos = intent.getParcelableArrayListExtra("data");
				// latLng = new LatLng(intent.getDoubleExtra("lat", 0),
				// intent.getDoubleExtra("lng", 0));
				// moveTolocation(latLng);
				adapter = new MyStallAdapter(mContext, parkInfos);
				mycar_xlistview.setAdapter(adapter);
			}
		}
	}
}
