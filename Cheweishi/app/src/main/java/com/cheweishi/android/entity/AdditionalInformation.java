package com.cheweishi.android.entity;

import java.io.Serializable;

public class AdditionalInformation {
	private String name;// ": "(丰乐服务区)汽车维修",
	private String telephone;// ": "",
	private String address;// ": "肥西县丰乐服务区",
	private String tag;// ": "汽车维修"

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
