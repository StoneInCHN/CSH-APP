package com.cheweishi.android.entity;

public class PushListInfo {

	private String device;// 设备号
	private int id;
	private int isGain;//
	private int carId;
	private String plate;//车牌
	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	private String time;// 时间
	private String type;// 消息类型

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIsGain() {
		return isGain;
	}

	public void setIsGain(int isGain) {
		this.isGain = isGain;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
