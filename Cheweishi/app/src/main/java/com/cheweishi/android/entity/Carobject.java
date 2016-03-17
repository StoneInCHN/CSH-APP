package com.cheweishi.android.entity;

import java.util.List;

public class Carobject {

	private String name;
	private List<CarType> list;
	private String type;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CarType> getList() {
		return list;
	}
	public void setList(List<CarType> list) {
		this.list = list;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
