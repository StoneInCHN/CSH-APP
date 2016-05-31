package com.cheweishi.android.entity;

/**
 * 车辆报告中驾驶时间段信息
 * @author mingdasen
 *
 */
public class CarReportTimeInfo {
	private int start;// 开始时间
	private int end;// 结束时间
	private int feetime;// 驾驶时长

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getFeetime() {
		return feetime;
	}

	public void setFeetime(int feetime) {
		this.feetime = feetime;
	}
}
