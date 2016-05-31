package com.cheweishi.android.entity;

import java.io.Serializable;

import android.graphics.Bitmap;

public class PhoneRecord implements Serializable {
	private String recordName;
	private String recordPhone;
	private Bitmap recordBitmap;
	private String recordAddress;
	private int billChannel;
	private String billEnd;
	private String billStart;

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public String getRecordPhone() {
		return recordPhone;
	}

	public void setRecordPhone(String recordPhone) {
		this.recordPhone = recordPhone;
	}

	public Bitmap getRecordBitmap() {
		return recordBitmap;
	}

	public void setRecordBitmap(Bitmap recordBitmap) {
		this.recordBitmap = recordBitmap;
	}

	public String getRecordAddress() {
		return recordAddress;
	}

	public void setRecordAddress(String recordAddress) {
		this.recordAddress = recordAddress;
	}

	public int getBillChannel() {
		return billChannel;
	}

	public void setBillChannel(int billChannel) {
		this.billChannel = billChannel;
	}

	public String getBillEnd() {
		return billEnd;
	}

	public void setBillEnd(String billEnd) {
		this.billEnd = billEnd;
	}

	public String getBillStart() {
		return billStart;
	}

	public void setBillStart(String billStart) {
		this.billStart = billStart;
	}
}
