package com.cheweishi.android.entity;

import java.io.Serializable;

import android.graphics.Bitmap;

public class SortModel implements Serializable{

	private String name;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	private String phone;
	private Bitmap bitmap;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
