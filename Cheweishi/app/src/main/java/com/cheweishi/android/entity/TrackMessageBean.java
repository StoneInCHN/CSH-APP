package com.cheweishi.android.entity;

public class TrackMessageBean {
	
	
	private String time;
	private String desId;
	private String device;
	private String lnglat;
	private String addr_start;
	private String addr_end;
	private String type;
	private String date_end;
	private String date_start;
	private String mile;
	private String minute;
	
	
	
	
	public TrackMessageBean(String time, String desId, String device,
			String lnglat, String addr_start, String addr_end, String type,
			String date_end, String date_start, String mile,String minute) {
		super();
		this.time = time;
		this.desId = desId;
		this.device = device;
		this.lnglat = lnglat;
		this.addr_start = addr_start;
		this.addr_end = addr_end;
		this.type = type;
		this.date_end = date_end;
		this.date_start = date_start;
		this.mile = mile;
		this.minute=minute;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDesId() {
		return desId;
	}
	public void setDesId(String desId) {
		this.desId = desId;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getLnglat() {
		return lnglat;
	}
	public void setLnglat(String lnglat) {
		this.lnglat = lnglat;
	}
	public String getAddr_start() {
		return addr_start;
	}
	public void setAddr_start(String addr_start) {
		this.addr_start = addr_start;
	}
	public String getAddr_end() {
		return addr_end;
	}
	public void setAddr_end(String addr_end) {
		this.addr_end = addr_end;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMile() {
		return mile;
	}
	public void setMile(String mile) {
		this.mile = mile;
	}
	public String getDate_end() {
		return date_end;
	}
	public void setDate_end(String date_end) {
		this.date_end = date_end;
	}
	public String getDate_start() {
		return date_start;
	}
	public void setDate_start(String date_start) {
		this.date_start = date_start;
	}
	public String getMinute() {
		return minute;
	}
	public void setMinute(String minute) {
		this.minute = minute;
	}
	
	
	

}
