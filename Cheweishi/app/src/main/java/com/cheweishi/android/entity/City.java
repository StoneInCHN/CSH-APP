package com.cheweishi.android.entity;

import java.io.Serializable;

public class City implements Serializable{
	private String cid;// ”: ”城市id”,
	private String name;// ”: ”城市名称”

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
