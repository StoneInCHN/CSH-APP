package com.cheweishi.android.entity;

import java.io.Serializable;

public class LoginMessage implements Serializable {
	private String uid ="";// : "ID",
	private String key="";// : "APPKey",
	private String no="";// : ”环信账号”,
	private String tel="";// : ”电话”,
	private String nick="";// : ”昵称”,
	private String photo="";// : ”头像URL”,
	private Account account;
	private Score score;
	private String scoreRanking="";
	private String mileRanking="";
	private int sign;
	private int isPush;
	private String note="";
	private int msg;
	private int type;
	/*===========================新版登录信息===================================*/
	/**
	 * "icon":null,"sex":null,"birth_date":null,"identity_card":null,"id":3,"nick_name":"bbbb","email":null,"qq":null,"signature":"bbb","mobile":"13647602913"
	 */

	private String icon = "";//头像
	private String sex = "";//性别
	private String birth_date = "";//出生年月日
	private String identity_card = "";//身份证
	private String id = "";//用户ID
	private String nick_name = "";//昵称
	private String email = "";//邮箱
	private String qq = "";//QQ
	private String signature = "";//签名
	private String mobile = "";//电话
	private CarManager carManager;//默认车辆信息
	
	
	public CarManager getCarManager() {
		return carManager;
	}

	public void setCarManager(CarManager carManager) {
		this.carManager = carManager;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirth_date() {
		return birth_date;
	}

	public void setBirth_date(String birth_date) {
		this.birth_date = birth_date;
	}

	public String getIdentity_card() {
		return identity_card;
	}

	public void setIdentity_card(String identity_card) {
		this.identity_card = identity_card;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	/*=====================================================================*/

	private Car car = new Car();

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public String getScoreRanking() {
		return scoreRanking;
	}

	public void setScoreRanking(String scoreRanking) {
		this.scoreRanking = scoreRanking;
	}

	public String getMileRanking() {
		return mileRanking;
	}

	public void setMileRanking(String mileRanking) {
		this.mileRanking = mileRanking;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public int getSign() {
		return sign;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}

	public int getIsPush() {
		return isPush;
	}

	public void setIsPush(int isPush) {
		this.isPush = isPush;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getMsg() {
		return msg;
	}

	public void setMsg(int msg) {
		this.msg = msg;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
