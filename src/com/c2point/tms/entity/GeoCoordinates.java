package com.c2point.tms.entity;

import javax.persistence.Embeddable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Embeddable
public class GeoCoordinates {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( GeoCoordinates.class.getName());

	private Double	latitude;
	private Double	longitude;
	private Double	accuracy;  // Accuracy of location measured in meters
	
	/**
	 * 
	 */
	public GeoCoordinates() {
		super();
		this.latitude = null;
		this.longitude = null;
		this.accuracy = null;
	}
	/**
	 * @param latitude
	 * @param longitude
	 */
	public GeoCoordinates( Double latitude, Double longitude, Double accuracy ) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
		
	}
/*	
	public GeoCoordinates( double latitude, double longitude, double accuracy ) {
		this( new Double( latitude ), new Double( longitude ), new Double( accuracy ));
	}
*/	
	public GeoCoordinates( GeoCoordinates coordinates ) {
		this( coordinates.latitude, coordinates.longitude, coordinates.accuracy );
	}
/*
	public GeoCoordinates( double latitude, double longitude ) {
		this( latitude, longitude, 0 );
	}
*/
	public GeoCoordinates( Double latitude, Double longitude ) {
		this( latitude, longitude, new Double( 0 ));
	}

	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude( Double latitude ) {
		this.latitude = latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude( Double longitude ) {
		this.longitude = longitude;
	}

	public Double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy( Double accuracy ) {
		this.accuracy = accuracy;
	}
		
	public boolean isValid() {
		if ( this.latitude != null && this.longitude != null ) {
			if ( this.latitude >= -90 && this.latitude <= 90 && 
				 this.longitude >= -180 && this.longitude <= 180 ) {

				if ( this.accuracy == null || this.accuracy < 0 )
					this.accuracy = new Double( 0 );
				
				return true;
			}
		}
		
		return false;
	}

	public GeoCoordinates setInvalid() {
		this.latitude = null;
		this.longitude = null;
		this.accuracy = null;
		
		return this;
	}
	
	@Override
	public String toString() {
		return "Geo ["
				+ "lat=" + ( latitude != null ? latitude : "null" ) + ", "
				+ "long="+ ( longitude != null ? longitude : "null" ) + ", "
				+ "accur=" + ( accuracy != null ? accuracy : "null" ) + "]";
	}
	
	
}
