package com.cheweishi.android.tools;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.cheweishi.android.cheweishi.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class EmptyTools {
	/**
	 * 空数据布局
	 */
	public static View view;

	public static void setEmptyView(Context context, ListView listView) {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();// 调用Activity的getLayoutInflater()
		view = inflater.inflate(R.layout.no_data, null);
		((ViewGroup) listView.getParent()).addView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listView.setEmptyView(view);
		System.out.println("empty=====");
	}
	
	public static void setEmptyView(Context context, PullToRefreshListView listView) {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();// 调用Activity的getLayoutInflater()
		view = inflater.inflate(R.layout.no_data, null);
		((ViewGroup) listView.getParent()).addView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listView.setEmptyView(view);
		System.out.println("empty=====");
	}

	public static void setMessage(String message) {
		if (view != null) {
			TextView tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);
			tv_no_data.setText(message);
		}
	}

	public static void setImg(int res) {
		if (view != null) {
			ImageView img_no_data = (ImageView) view
					.findViewById(R.id.img_no_data);
			img_no_data.setImageResource(res);
		}
	}

	// /**
	// * 隐藏空数据布局
	// */
	// public static void hintEmpty() {
	// if (view != null) {
	// view.setVisibility(View.INVISIBLE);
	// }
	// }
	//
	// /**
	// * 显示空数据布局
	// */
	// public static void showEmpty() {
	// if (view != null) {
	// view.setVisibility(View.VISIBLE);
	// }
	// }
}
