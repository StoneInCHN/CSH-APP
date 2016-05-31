package com.cheweishi.android.entity;

import java.io.Serializable;

public class Score implements Serializable{
	private String cid;// : ”积分账户ID”,
//	private String total;// : ”历史总积分”,
//	private String used;// : ”已使用积分”,
	private String now;// ”: ”当前剩余积分”

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}


	public String getNow() {
		return now;
	}

	public void setNow(String now) {
		this.now = now;
	}
}
