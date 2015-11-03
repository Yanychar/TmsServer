package com.c2point.tms.entity;

public class GeoHolder {

	private float latitude;
	private float longitude;
	
	public GeoHolder() {
		super();
	}
	
	public GeoHolder(float latitude, float longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		return "GeoHolder [latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}

}
