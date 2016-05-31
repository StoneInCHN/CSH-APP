package com.cheweishi.android.entity;

import java.io.Serializable;

public class WashCarTypeDetail implements Serializable{
	private String id;
	private String is_discount_price;
	private String discount_price;
	private String goods_name;
	private String store_id;
	private String support_red;
	private String classification_name;
	private String price;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIs_discount_price() {
		return is_discount_price;
	}

	public void setIs_discount_price(String is_discount_price) {
		this.is_discount_price = is_discount_price;
	}

	public String getDiscount_price() {
		return discount_price;
	}

	public void setDiscount_price(String discount_price) {
		this.discount_price = discount_price;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getSupport_red() {
		return support_red;
	}

	public void setSupport_red(String support_red) {
		this.support_red = support_red;
	}

	public String getClassification_name() {
		return classification_name;
	}

	public void setClassification_name(String classification_name) {
		this.classification_name = classification_name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
}
