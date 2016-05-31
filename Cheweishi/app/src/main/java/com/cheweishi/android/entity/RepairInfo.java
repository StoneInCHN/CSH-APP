package com.cheweishi.android.entity;

import java.io.Serializable;

public class RepairInfo  {

	// private String id;// ": 3,
	// private String title;// ": "渝成代驾3",
	// private String tel;// ": "13983690946",
	// private double lng;// ": 106.542852,
	// private String addr;// ": "重庆市洋河中路42号",
	// private String photo;//
	// ": "service/20150412/20150412_2a0eef9865f7482d9da184ea617e8f66.jpg",
	// private double lat;// ": 29.581928
	//
	// public String getId() {
	// return id;
	// }
	//
	// public void setId(String id) {
	// this.id = id;
	// }
	//
	// public String getTitle() {
	// return title;
	// }
	//
	// public void setTitle(String title) {
	// this.title = title;
	// }
	//
	// public String getTel() {
	// return tel;
	// }
	//
	// public void setTel(String tel) {
	// this.tel = tel;
	// }
	//
	// public double getLng() {
	// return lng;
	// }
	//
	// public void setLng(double lng) {
	// this.lng = lng;
	// }
	//
	// public String getAddr() {
	// return addr;
	// }
	//
	// public void setAddr(String addr) {
	// this.addr = addr;
	// }
	//
	// public String getPhoto() {
	// return photo;
	// }
	//
	// public void setPhoto(String photo) {
	// this.photo = photo;
	// }
	//
	// public double getLat() {
	// return lat;
	// }
	//
	// public void setLat(double lat) {
	// this.lat = lat;
	// }
	private String name;// ": "(丰乐服务区)汽车维修",
	private String cityName;// ": "合肥市"," +
	private String photo;
	private LC location;
	private String address;// ": "肥西县丰乐服务区",
	private String district;// ": "肥西县",
	private AdditionalInformation additionalInformation;
	private String type;// ": "汽车服务汽车维修/养护/洗车"

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public AdditionalInformation getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(
			AdditionalInformation additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LC getLocation() {
		return location;
	}

	public void setLocation(LC location) {
		this.location = location;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
}
