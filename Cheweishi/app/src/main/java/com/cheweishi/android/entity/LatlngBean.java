package com.cheweishi.android.entity;

import com.baidu.mapapi.model.LatLng;

public class LatlngBean {

	private LatLng  latLng;

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	@Override
	public String toString() {
		return this.latLng.latitude+","+latLng.longitude;
	}
}
