package com.cheweishi.android.entity;

import java.util.List;

public class OrderDetail {
	// private String id;
	// private String buyer_id;
	// private String seller_name;
	// private String buyer_email;
	// private String status;
	// private String buyer_name;
	// private String order_sn;
	// private String seller_id;
	// private String add_time;
	// private String type;
	//
	// public String getId() {
	// return id;
	// }
	//
	// public void setId(String id) {
	// this.id = id;
	// }
	//
	// public String getBuyer_id() {
	// return buyer_id;
	// }
	//
	// public void setBuyer_id(String buyer_id) {
	// this.buyer_id = buyer_id;
	// }
	//
	// public String getSeller_name() {
	// return seller_name;
	// }
	//
	// public void setSeller_name(String seller_name) {
	// this.seller_name = seller_name;
	// }
	//
	// public String getBuyer_email() {
	// return buyer_email;
	// }
	//
	// public void setBuyer_email(String buyer_email) {
	// this.buyer_email = buyer_email;
	// }
	//
	// public String getStatus() {
	// return status;
	// }
	//
	// public void setStatus(String status) {
	// this.status = status;
	// }
	//
	// public String getBuyer_name() {
	// return buyer_name;
	// }
	//
	// public void setBuyer_name(String buyer_name) {
	// this.buyer_name = buyer_name;
	// }
	//
	// public String getOrder_sn() {
	// return order_sn;
	// }
	//
	// public void setOrder_sn(String order_sn) {
	// this.order_sn = order_sn;
	// }
	//
	// public String getSeller_id() {
	// return seller_id;
	// }
	//
	// public void setSeller_id(String seller_id) {
	// this.seller_id = seller_id;
	// }
	//
	// public String getAdd_time() {
	// return add_time;
	// }
	//
	// public void setAdd_time(String add_time) {
	// this.add_time = add_time;
	// }
	//
	// public String getType() {
	// return type;
	// }
	//
	// public void setType(String type) {
	// this.type = type;
	// }

	private String owner_name;
	private String address;
	private String add_time;
	private String store_name;
	private String goodsName;
	private String mobile;
	private String orderId;
	private List<OrderGoods> orderGoodsList;
	private String reputation;
	private double im_lat;
	private double im_lng;
	private String effective_time;
	private String status;
	private String finished_time;
	private String image_1;
	private String order_sn;
	private String service_time;
	private String classification_name;
	private String pay_time;
	private String evaluation_time;
	private String barcodes;

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public List<OrderGoods> getOrderGoodsList() {
		return orderGoodsList;
	}

	public void setOrderGoodsList(List<OrderGoods> orderGoodsList) {
		this.orderGoodsList = orderGoodsList;
	}

	public String getReputation() {
		return reputation;
	}

	public void setReputation(String reputation) {
		this.reputation = reputation;
	}

	public double getIm_lat() {
		return im_lat;
	}

	public void setIm_lat(double im_lat) {
		this.im_lat = im_lat;
	}

	public double getIm_lng() {
		return im_lng;
	}

	public void setIm_lng(double im_lng) {
		this.im_lng = im_lng;
	}

	public String getEffective_time() {
		return effective_time;
	}

	public void setEffective_time(String effective_time) {
		this.effective_time = effective_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFinished_time() {
		return finished_time;
	}

	public void setFinished_time(String finished_time) {
		this.finished_time = finished_time;
	}

	public String getImage_1() {
		return image_1;
	}

	public void setImage_1(String image_1) {
		this.image_1 = image_1;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getService_time() {
		return service_time;
	}

	public void setService_time(String service_time) {
		this.service_time = service_time;
	}

	public String getClassification_name() {
		return classification_name;
	}

	public void setClassification_name(String classification_name) {
		this.classification_name = classification_name;
	}

	public String getPay_time() {
		return pay_time;
	}

	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}

	public String getEvaluation_time() {
		return evaluation_time;
	}

	public void setEvaluation_time(String evaluation_time) {
		this.evaluation_time = evaluation_time;
	}

	public String getBarcodes() {
		return barcodes;
	}

	public void setBarcodes(String barcodes) {
		this.barcodes = barcodes;
	}

}
