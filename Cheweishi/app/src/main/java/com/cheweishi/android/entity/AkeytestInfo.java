package com.cheweishi.android.entity;

import java.io.Serializable;

/**
 * 一键检测集合信息
 * @author 胡健
 *  2015.7.3
 */
public class AkeytestInfo implements Serializable {
	private String unit;//千米
	private String name;//总里程数
	private String value;//检测项当前值
	private String fault;//状态
	private String  type;//标记
	
	public AkeytestInfo(String unit, String name, String value, String fault,
			String type) {
		super();
		this.unit = unit;
		this.name = name;
		this.value = value;
		this.fault = fault;
		this.type = type;
	}
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFault() {
		return fault;
	}

	public void setFault(String fault) {
		this.fault = fault;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
