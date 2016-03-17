package com.cheweishi.android.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 一键检测所有信息
 * @author Administrator
 *2015.7.3
 */
@SuppressWarnings("serial")
public class AkeyTextAllInfo implements Serializable {
    private  String carState;//综合评分
    private  String logo;//图片地址
    private  String maintainState;//状态
    private String  plate;//车牌号
    private String dtc;// 故障码
    private String synthesis;//优秀
    private ArrayList<AkeytestInfo> Akeyone;//第一个
    private ArrayList<AkeytestInfo> Akeytwo;//第二个
    private ArrayList<AkeytestInfo> Akeythere;//第三个
	public AkeyTextAllInfo(String carState, String logo, String maintainState,
			String plate, String dtc, String synthesis,
			ArrayList<AkeytestInfo> akeyone, ArrayList<AkeytestInfo> akeytwo,
			ArrayList<AkeytestInfo> akeythere) {
		super();
		this.carState = carState;
		this.logo = logo;
		this.maintainState = maintainState;
		this.plate = plate;
		this.dtc = dtc;
		this.synthesis = synthesis;
		Akeyone = akeyone;
		Akeytwo = akeytwo;
		Akeythere = akeythere;
	}
	
	
	public String getCarState() {
		return carState;
	}
	public void setCarState(String carState) {
		this.carState = carState;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getMaintainState() {
		return maintainState;
	}
	public void setMaintainState(String maintainState) {
		this.maintainState = maintainState;
	}
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
	}
	public String getDtc() {
		return dtc;
	}
	public void setDtc(String dtc) {
		this.dtc = dtc;
	}
	public String getSynthesis() {
		return synthesis;
	}
	public void setSynthesis(String synthesis) {
		this.synthesis = synthesis;
	}
	public ArrayList<AkeytestInfo> getAkeyone() {
		return Akeyone;
	}
	public void setAkeyone(ArrayList<AkeytestInfo> akeyone) {
		Akeyone = akeyone;
	}
	public ArrayList<AkeytestInfo> getAkeytwo() {
		return Akeytwo;
	}
	public void setAkeytwo(ArrayList<AkeytestInfo> akeytwo) {
		Akeytwo = akeytwo;
	}
	public ArrayList<AkeytestInfo> getAkeythere() {
		return Akeythere;
	}
	public void setAkeythere(ArrayList<AkeytestInfo> akeythere) {
		Akeythere = akeythere;
	}
	
	
    
    
    
}
