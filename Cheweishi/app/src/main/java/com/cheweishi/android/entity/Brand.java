package com.cheweishi.android.entity;

import java.io.Serializable;

public class Brand implements Serializable, Cloneable{
	private String series;
	private String brandIcon;
	private String module;
	private String seriesName;
	private String moduleName;
	private String brand;
	private String brandName;

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getBrandIcon() {
		return brandIcon;
	}

	public void setBrandIcon(String brandIcon) {
		this.brandIcon = brandIcon;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
}
