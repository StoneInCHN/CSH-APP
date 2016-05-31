package com.cheweishi.android.entity;

import java.io.Serializable;

public class CarManager implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cid;// ”: ”车辆id”,
	private String simNo;// ”: ”sim卡号”,
	private String color;// ”: ”颜色”,
	private String brandName;// ”: ”品牌”,
	private String seriesName;// ”: ”车系”,
	private String vinNo;// ”: ”车架号”,
	private String isDefault;// ”: ”是否为默认设备”,
	private String addTime;// ”: ”添加时间”,
	private String note;// ”: ”备注”,
	private String flag;// ”: ”小乂系统账户”,
	private String carId;// ”: ”小乂系统车辆ID”,
	private String boundJson;// ”,”小乂系统绑定数据”
	private int feed;
	private String serial;
	
	
	// private double MMile;
	// private double MMileage;
	// private double NMileage;
	// private String NInspect;
	// private String oil;
	// private String time;
	private String maintain;

	private String id;
	private String series;
	private String module;
	private String device;
	private String plate;
	private String driving_license;
	private String inspection;
	private Brand brand;
	
	private String mileage;

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getSimNo() {
		return simNo;
	}

	public void setSimNo(String simNo) {
		this.simNo = simNo;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getBoundJson() {
		return boundJson;
	}

	public void setBoundJson(String boundJson) {
		this.boundJson = boundJson;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	// public double getMMile() {
	// return MMile;
	// }
	//
	// public void setMMile(double mMile) {
	// MMile = mMile;
	// }
	//
	// public double getMMileage() {
	// return MMileage;
	// }
	//
	// public void setMMileage(double mMileage) {
	// MMileage = mMileage;
	// }
	//
	// public double getNMileage() {
	// return NMileage;
	// }
	//
	// public void setNMileage(double nMileage) {
	// NMileage = nMileage;
	// }
	//
	// public String getNInspect() {
	// return NInspect;
	// }
	//
	// public void setNInspect(String nInspect) {
	// NInspect = nInspect;
	// }
	//
	// public String getOil() {
	// return oil;
	// }
	//
	// public void setOil(String oil) {
	// this.oil = oil;
	// }
	//
	// public String getTime() {
	// return time;
	// }
	//
	// public void setTime(String time) {
	// this.time = time;
	// }

	public String getVinNo() {
		return vinNo;
	}

	public void setVinNo(String vinNo) {
		this.vinNo = vinNo;
	}

	public int getFeed() {
		return feed;
	}

	public void setFeed(int feed) {
		this.feed = feed;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDriving_license() {
		return driving_license;
	}

	public void setDriving_license(String driving_license) {
		this.driving_license = driving_license;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public String getInspection() {
		return inspection;
	}

	public void setInspection(String inspection) {
		this.inspection = inspection;
	}


	public String getMileage() {
		return mileage;
	}

	public void setMileage(String mileage) {
		this.mileage = mileage;
	}

	public String getMaintain() {
		return maintain;
	}

	public void setMaintain(String maintain) {
		this.maintain = maintain;
	}
}
