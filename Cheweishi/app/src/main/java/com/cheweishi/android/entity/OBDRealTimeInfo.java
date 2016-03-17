package com.cheweishi.android.entity;
/**
 * 实时车况数据
 * @author mingdasen
 *
 */
public class OBDRealTimeInfo {
	private String mile;// 里程
	private String obdafr;// 　空燃比系数
	private String obdbv;// 　蓄电池电压
	private String obdcta;// 　节气门开度
	private String obddate;// 　obd时间
	private String obdengload;// 　发动机负荷
	private String obdengruntime;// 　发动机运行时间
	private String obdifc;// 　百公里油耗
	private String obdremaininggas;// 　剩余油量　
	private String obdrpm;// 　转速
	private String obdspeed;// 　车速
	private String obdtemp; // 　环境温度
	private String obdwatertemp;// 　水温

	public String getMile() {
		return mile;
	}

	public void setMile(String mile) {
		this.mile = mile;
	}

	public String getObdafr() {
		return obdafr;
	}

	public void setObdafr(String obdafr) {
		this.obdafr = obdafr;
	}

	public String getObdbv() {
		return obdbv;
	}

	public void setObdbv(String obdbv) {
		this.obdbv = obdbv;
	}

	public String getObdcta() {
		return obdcta;
	}

	public void setObdcta(String obdcta) {
		this.obdcta = obdcta;
	}

	public String getObddate() {
		return obddate;
	}

	public void setObddate(String obddate) {
		this.obddate = obddate;
	}

	public String getObdengload() {
		return obdengload;
	}

	public void setObdengload(String obdengload) {
		this.obdengload = obdengload;
	}

	public String getObdengruntime() {
		return obdengruntime;
	}

	public void setObdengruntime(String obdengruntime) {
		this.obdengruntime = obdengruntime;
	}

	public String getObdifc() {
		return obdifc;
	}

	public void setObdifc(String obdifc) {
		this.obdifc = obdifc;
	}

	public String getObdremaininggas() {
		return obdremaininggas;
	}

	public void setObdremaininggas(String obdremaininggas) {
		this.obdremaininggas = obdremaininggas;
	}

	public String getObdrpm() {
		return obdrpm;
	}

	public void setObdrpm(String obdrpm) {
		this.obdrpm = obdrpm;
	}

	public String getObdspeed() {
		return obdspeed;
	}

	public void setObdspeed(String obdspeed) {
		this.obdspeed = obdspeed;
	}

	public String getObdtemp() {
		return obdtemp;
	}

	public void setObdtemp(String obdtemp) {
		this.obdtemp = obdtemp;
	}

	public String getObdwatertemp() {
		return obdwatertemp;
	}

	public void setObdwatertemp(String obdwatertemp) {
		this.obdwatertemp = obdwatertemp;
	}
}
