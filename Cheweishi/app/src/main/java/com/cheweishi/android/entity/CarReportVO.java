package com.cheweishi.android.entity;

import android.widget.LinearLayout;

/**
 * 车辆报告
 * 
 * @author zhangq
 * 
 */
public class CarReportVO {
	public LinearLayout lLayoutMore;
	private String driverMile;
	private String avgoil;
	private String oil;
	private String oilFee;
	private String accCount;
	private String avgSpeed;
	private String feeTime;
	private String idleTime;
	private String skidCount;
	private String rapidCount;
	private String turnCount;
	private String fatigueDriver;

	public LinearLayout getlLayoutMore() {
		return lLayoutMore;
	}

	public void setlLayoutMore(LinearLayout lLayoutMore) {
		this.lLayoutMore = lLayoutMore;
	}

	public String getDriverMile() {
		return driverMile;
	}

	public void setDriverMile(String driverMile) {
		this.driverMile = driverMile;
	}

	public String getAvgoil() {
		return avgoil;
	}

	public void setAvgoil(String avgoil) {
		this.avgoil = avgoil;
	}

	public String getOil() {
		return oil;
	}

	public void setOil(String oil) {
		this.oil = oil;
	}

	public String getOilFee() {
		return oilFee;
	}

	public void setOilFee(String oilFee) {
		this.oilFee = oilFee;
	}

	public String getAccCount() {
		return accCount;
	}

	public void setAccCount(String accCount) {
		this.accCount = accCount;
	}

	public String getAvgSpeed() {
		return avgSpeed;
	}

	public void setAvgSpeed(String avgSpeed) {
		this.avgSpeed = avgSpeed;
	}

	public String getFeeTime() {
		return feeTime;
	}

	public void setFeeTime(String feeTime) {
		this.feeTime = feeTime;
	}

	public String getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(String idleTime) {
		this.idleTime = idleTime;
	}

	public String getSkidCount() {
		return skidCount;
	}

	public void setSkidCount(String skidCount) {
		this.skidCount = skidCount;
	}

	public String getRapidCount() {
		return rapidCount;
	}

	public void setRapidCount(String rapidCount) {
		this.rapidCount = rapidCount;
	}

	public String getTurnCount() {
		return turnCount;
	}

	public void setTurnCount(String turnCount) {
		this.turnCount = turnCount;
	}

	public String getFatigueDriver() {
		return fatigueDriver;
	}

	public void setFatigueDriver(String fatigueDriver) {
		this.fatigueDriver = fatigueDriver;
	}

}
