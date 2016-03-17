package com.cheweishi.android.entity;

import java.io.Serializable;
import java.util.List;

public class WashCar implements Serializable{
	private String id;
	private String business_time;
	private String address;
	private String tel;
	private Double im_lng;
	private String image_1;
	private List<WashCarType> type;
	private String store_name;
	private Double im_lat;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getImage_1() {
		return image_1;
	}

	public void setImage_1(String image_1) {
		this.image_1 = image_1;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public List<WashCarType> getType() {
		return type;
	}

	public void setType(List<WashCarType> type) {
		this.type = type;
	}

	public String getBusiness_time() {
		return business_time;
	}

	public void setBusiness_time(String business_time) {
		this.business_time = business_time;
	}

	public Double getIm_lng() {
		return im_lng;
	}

	public void setIm_lng(Double im_lng) {
		this.im_lng = im_lng;
	}

	public Double getIm_lat() {
		return im_lat;
	}

	public void setIm_lat(Double im_lat) {
		this.im_lat = im_lat;
	}

}
