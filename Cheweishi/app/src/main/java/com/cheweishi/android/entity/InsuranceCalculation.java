package com.cheweishi.android.entity;

public class InsuranceCalculation {

	private String insuranceName;
	private String[] insuranceRange;
	private String insuranceCurrentPrice;
	private String insuranceType;
	private String insurancePayPrice;
	private boolean chooseFlag;

	public String getInsuranceName() {
		return insuranceName;
	}

	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}

	public String[] getInsuranceRange() {
		return insuranceRange;
	}

	public void setInsuranceRange(String[] insuranceRange) {
		this.insuranceRange = insuranceRange;
	}

	public String getInsuranceCurrentPrice() {
		return insuranceCurrentPrice;
	}

	public void setInsuranceCurrentPrice(String insuranceCurrentPrice) {
		this.insuranceCurrentPrice = insuranceCurrentPrice;
	}

	public String getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}

	public String getInsurancePayPrice() {
		return insurancePayPrice;
	}

	public void setInsurancePayPrice(String insurancePayPrice) {
		this.insurancePayPrice = insurancePayPrice;
	}

	public boolean isChooseFlag() {
		return chooseFlag;
	}

	public void setChooseFlag(boolean chooseFlag) {
		this.chooseFlag = chooseFlag;
	}

}
