package com.cheweishi.android.entity;

public class ScoreDetail {
	private String stid;// ”: ”积分收支类型id”,
	private String total;// ”: ”积分”,
	private String time;// ”: ”时间”,
	private String note;// ”: ”备注”
	private String rule;// ”: ”收支类型：0(收取)/1(支出)”,

	public String getStid() {
		return stid;
	}

	public void setStid(String stid) {
		this.stid = stid;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
}
