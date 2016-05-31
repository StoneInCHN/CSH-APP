package com.cheweishi.android.entity;

/**
 * 红包信息
 * @author mingdasen
 *
 */
public class RedPacketsInfo {
	
	private int id;
	private String effect;//过期时间
	private String status;//状态
	private String admin;//分配对象
	private double money;//金额
	private String add_time;//获取时间
	private String code;//功能描述
	private int type;//type: 0:正常：1：已使用；2：禁用；3：失效
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEffect() {
		return effect;
	}
	public void setEffect(String effect) {
		this.effect = effect;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
