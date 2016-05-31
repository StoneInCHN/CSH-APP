package com.cheweishi.android.entity;

import java.io.Serializable;

/**
 * 报告费用详情VO
 * 
 * @author zhangq
 * 
 */
public class CarReportFreeVO implements Serializable {
	/**
	 * {"data":{"total":"458.06","id":"12","percent":"81%","max":"400.0",
	 * "maxType"
	 * :"clean","list":[{"addr":"","fid":12,"id":4,"latlng":"","time":""
	 * ,"type":"clean"
	 * ,"value":200.0},{"addr":"\"\"","fid":12,"id":5,"latlng":"\"\""
	 * ,"time":"\"\""
	 * ,"type":"clean","value":200.0}],"oil":"58.06"},"operationState"
	 * :"SUCCESS","title":""}
	 */
	private String sType;
	private int type;// 费用类型
	private String money;// 费用金额 value
	private String addr;
	private int fid;
	private int id;
	private String latlng;
	private String time;

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public int getFid() {
		return fid;
	}

	public void setFid(int fid) {
		this.fid = fid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLatlng() {
		return latlng;
	}

	public void setLatlng(String latlng) {
		this.latlng = latlng;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getsType() {
		return sType;
	}

	public void setsType(String sType) {
		this.sType = sType;
		// (oil),park、clean、road、maintain、insurance、fine,(add)
		if ("oil".equalsIgnoreCase(sType)) {
			type = 0;
		}
		if ("park".equalsIgnoreCase(sType)) {
			type = 1;
		}
		if ("clean".equalsIgnoreCase(sType)) {
			type = 2;
		}
		if ("road".equalsIgnoreCase(sType)) {
			type = 3;
		}
		if ("maintain".equalsIgnoreCase(sType)) {
			type = 4;
		}
		if ("insurance".equalsIgnoreCase(sType)) {
			type = 5;
		}
		if ("fine".equalsIgnoreCase(sType)) {
			type = 6;
		}
		if ("add".equalsIgnoreCase(sType)) {
			type = 7;
		}
	}

}
