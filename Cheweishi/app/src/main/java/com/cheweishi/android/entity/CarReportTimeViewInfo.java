package com.cheweishi.android.entity;

/**
 * 车辆报告的驾驶时间详情数据
 * @author mingdasen
 *
 */
public class CarReportTimeViewInfo {
	private float left;// X轴起点坐标
	private float rigth;// X轴终点坐标
	private float width;//长方形宽度
	private String startTime;//起始时间
	private String endTime;//停止时间
	private String feeTime;//行驶时长

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getFeeTime() {
		return feeTime;
	}

	public void setFeeTime(String feeTime) {
		this.feeTime = feeTime;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getLeft() {
		return left;
	}

	public void setLeft(float left) {
		this.left = left;
	}

	public float getRigth() {
		return rigth;
	}

	public void setRigth(float rigth) {
		this.rigth = rigth;
	}

}
