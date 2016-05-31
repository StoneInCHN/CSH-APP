package com.cheweishi.android.entity;

import java.util.ArrayList;

public class GasStationInfo {

	public String name;
	public String cityName;
	public ArrayList<Location> location;
	public String address;
	public String distance;
	public String district;

	public class Location {
		public String lat;
		public String lng;
	}
}
