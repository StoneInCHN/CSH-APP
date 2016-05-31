package com.cheweishi.android.entity;

import java.io.Serializable;

public class CarMaintain implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long aid;
	private long cid;
	private int MMile = 0; // 上次保养后行驶里程
	private int MMileage = 0; // 当前总里程
	private int NMileage = 0; // 下次保养里程值
	private String NInspect = ""; // 下次年检时间
	private String oil = ""; // 常用油品
	private String time = "";

	public long getAid() {
		return aid;
	}

	public void setAid(long aid) {
		this.aid = aid;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}


	

	public String getNInspect() {
		return NInspect;
	}

	public void setNInspect(String nInspect) {
		NInspect = nInspect;
	}

	public String getOil() {
		return oil;
	}

	public void setOil(String oil) {
		this.oil = oil;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getMMileage() {
		return MMileage;
	}

	public void setMMileage(int mMileage) {
		MMileage = mMileage;
	}

	public int getNMileage() {
		return NMileage;
	}

	public void setNMileage(int nMileage) {
		NMileage = nMileage;
	}

	public int getMMile() {
		return MMile;
	}

	public void setMMile(int mMile) {
		MMile = mMile;
	}

}
