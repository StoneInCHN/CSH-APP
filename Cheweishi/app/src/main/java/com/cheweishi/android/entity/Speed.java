package com.cheweishi.android.entity;

import java.util.ArrayList;
import java.util.List;

public class Speed {
	private long start;
	private long end;
	private float avgCurrent;
	private float maxCurrent;
	private String startTime;
	private String endTime;
	private List<SubSpeed> listSubSpeed = new ArrayList<SubSpeed>();
	private String blankTime;
	private long maxTime;

	public String getBlankTime() {
		return blankTime;
	}

	public void setBlankTime(String blankTime) {
		this.blankTime = blankTime;
	}

	public List<SubSpeed> getListSubSpeed() {
		return listSubSpeed;
	}

	public void setListSubSpeed(List<SubSpeed> listSubSpeed) {
		this.listSubSpeed = listSubSpeed;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public float getAvgCurrent() {
		return avgCurrent;
	}

	public void setAvgCurrent(float avgCurrent) {
		this.avgCurrent = avgCurrent;
	}

	public float getMaxCurrent() {
		return maxCurrent;
	}

	public void setMaxCurrent(float maxCurrent) {
		this.maxCurrent = maxCurrent;
	}

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

	public long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}

}
