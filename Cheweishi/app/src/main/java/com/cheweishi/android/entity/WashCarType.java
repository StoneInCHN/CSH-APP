package com.cheweishi.android.entity;

import java.io.Serializable;
import java.util.List;

public class WashCarType implements Serializable{
	private String typename;
	private List<WashCarTypeDetail> goodsList;

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	public List<WashCarTypeDetail> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<WashCarTypeDetail> goodsList) {
		this.goodsList = goodsList;
	}

}
