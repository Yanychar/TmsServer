package com.c2point.tms.util.location;

import com.c2point.tms.entity.GeoCoordinates;

public class GeoDistanceValidator {

	public enum ValidationResult {
		OK, BASE_INVALID, OTHER_INVALID, BOTH_INVALID, TOO_FAR, NO_RESULT;
	}
	
	private static final int 	EARTH_RADIUS = 6371; // km
	private static final double RADIANTS_IN_GRAD = 2 * Math.PI / 360;
	
	private static double 		DEFAULT_WARNING_DISTANCE = 0.5;  // In kilometers

	
	private GeoCoordinates	basePoint;
	private GeoCoordinates	otherPoint;
	
	private double			warningDistance;

	private ValidationResult	lastValidationResult;
	
	public GeoDistanceValidator() {
	
		this( null, null, DEFAULT_WARNING_DISTANCE );
		
	}
	
	public GeoDistanceValidator( double warningDistance ) {
		
		this( null, null, warningDistance );
		
	}
	
	public GeoDistanceValidator( 
			GeoCoordinates basePoint, GeoCoordinates otherPoint ) {
		
		this( basePoint, otherPoint, DEFAULT_WARNING_DISTANCE );
		
	}

	public GeoDistanceValidator( 
			GeoCoordinates basePoint, GeoCoordinates otherPoint, 
			double warningDistance ) {
		
		this.basePoint = basePoint;
		this.otherPoint = otherPoint;
		this.warningDistance = warningDistance;
		this.lastValidationResult = ValidationResult.NO_RESULT;
	}

	public GeoCoordinates getBasePoint() { return basePoint; }
	public void setBasePoint(GeoCoordinates basePoint) { this.basePoint = basePoint; }

	public GeoCoordinates getOtherPoint() { return otherPoint; }
	public void setOtherPoint(GeoCoordinates otherPoint) { this.otherPoint = otherPoint; }

	public void setGeoPoints( GeoCoordinates basePoint, GeoCoordinates otherPoint ) { 
		setBasePoint( basePoint ); 
		setOtherPoint( otherPoint ); 
	}
	
	
	public double getWarningDistance() { return warningDistance; }
	public void setWarningDistance(double warningDistance) { this.warningDistance = warningDistance; }
	
	/*
	 * Business methods
	 * 
	 */
	
	public ValidationResult validate( GeoCoordinates basePoint, GeoCoordinates otherPoint ) {

		setGeoPoints( basePoint, otherPoint ); 
		
		return validate();
	}
	
	public ValidationResult validate() {
		
		this.lastValidationResult = ValidationResult.OK;
		
		if ( !validate( basePoint )) {
			
			
			if ( !validate( otherPoint )) {
				
				this.lastValidationResult = ValidationResult.BOTH_INVALID;
			} else {

				this.lastValidationResult = ValidationResult.BASE_INVALID;
				
			}
		} else {
			if ( !validate( otherPoint )) {
				
				this.lastValidationResult = ValidationResult.OTHER_INVALID;
			}
		}
		
		if ( this.lastValidationResult == ValidationResult.OK && warningDistance > 0 ) {
			
			double warningDistanceWithAccuracy = 
					warningDistance 
				 + ( otherPoint.getAccuracy() + basePoint.getAccuracy()) / 100;
			
			// Now we can check distance between
			if ( calculateDistance( otherPoint, basePoint ) > warningDistanceWithAccuracy ) {

				this.lastValidationResult = ValidationResult.TOO_FAR;
			}
			
			
		}
		
		return this.lastValidationResult;
	}
	
	public ValidationResult getLastValidationResult() { 
		
		if ( this.lastValidationResult == ValidationResult.NO_RESULT ) {
			validate();
		}
		
		return this.lastValidationResult;
	}

	
	
	private boolean validate( GeoCoordinates	geoPoint ) {
		
		return geoPoint != null && geoPoint.isValid(); 
	}

	
	// Calculate distance in kilometers
	// Returns -1 if coordinates are not specified enough
	private float calculateDistance( GeoCoordinates coordinates_1, GeoCoordinates coordinates_2 ) {

		double lat1 = coordinates_1.getLatitude() * RADIANTS_IN_GRAD;
		double lat2 = coordinates_2.getLatitude() * RADIANTS_IN_GRAD;
		double lon1 = coordinates_1.getLongitude() * RADIANTS_IN_GRAD;
		double lon2 = coordinates_2.getLongitude() * RADIANTS_IN_GRAD;
	
		// d = acos(sin(lat1).sin(lat2)+cos(lat1).cos(lat2).cos(long2-long1)).R
		
		double d = Math.acos( Math.sin( lat1 ) * Math.sin( lat2 ) + 
		                  	  Math.cos( lat1 ) * Math.cos( lat2 ) * Math.cos( lon2 - lon1 )
		                  	) * EARTH_RADIUS;		
			
		return ( float )d;
	}

	
}
