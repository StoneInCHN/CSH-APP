package com.cheweishi.android.entity;

import java.io.Serializable;

public class UserInfo implements Serializable {
	private String uid;// ": "用户ID",
	private String tel;// ”: ”电话”,
	private String nick;// ”: ”昵称”,
	private String photo;// ”: ”头像”,
	private String birth;// ”: ”生日”,
	private String sex;// ”: ”性别”,
	private String cid;// ”: ”城市id”,
	private String note;// ”: ”个人签名”,
	// private City city;
	private String age;
	private String carAge;// ：驾龄
	private String carNum;// ：我的车辆
	private String addr;// ：常出没地(为空，取city，均为空就没有数据)
	private String carMile;// ：里程
	private String city;// ：城市
	private String email;// 邮箱
	private String cityId;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	// public City getCity() {
	// return city;
	// }
	//
	// public void setCity(City city) {
	// this.city = city;
	// }

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getCarAge() {
		return carAge;
	}

	public void setCarAge(String carAge) {
		this.carAge = carAge;
	}

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getCarMile() {
		return carMile;
	}

	public void setCarMile(String carMile) {
		this.carMile = carMile;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
}
