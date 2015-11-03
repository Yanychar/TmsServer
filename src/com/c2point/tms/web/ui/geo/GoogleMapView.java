package com.c2point.tms.web.ui.geo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.GeoCoordinates;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class GoogleMapView extends Window implements MapViewIF {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( GoogleMapView.class.getName());

/*	
	private static final String CHECK_SITE_MARK = "T";
	private static final String CHECK_CURRENT_MARK = "C";
	private static final String SITE_MARK_COLOR = "markers=color:green";
	private static final String CURRENT_MARK_COLOR = "markers=color:green";
	private static final String WARNING_MARK_COLOR = "markers=color:red";
*/	
	private 		GoogleMap googleMap;
	
	GoogleMapMarker siteMarker = null;
	GoogleMapMarker checkInMarker = null;
	GoogleMapMarker checkOutMarker = null;
	
	public GoogleMapView() {

//		setModal( true );

		setWidth( "55%" );
		setHeight( "65%" );
		
		googleMap = new GoogleMap( null, null, null );
        googleMap.setSizeFull();
	
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();

		vl.addComponent(googleMap);
        vl.setExpandRatio(googleMap, 1.0f);
	
		this.setContent( vl );
		
	}
	
	public void setCaption( String caption ) {

		super.setCaption( caption );

	}
	
	public void showMap( UI parent, GeoCoordinates site, String siteTooltip ) {

		showMapInt( parent, site, siteTooltip, null, null, null, null );
		
	}

	public void showMapIn( UI parent, GeoCoordinates site, String siteTooltip, 
			 				GeoCoordinates checkIn, String checkInTooltip ) {
		
		showMapInt( parent, site, siteTooltip, checkIn, checkInTooltip, null, null );
	}

	public void showMapOut( UI parent, GeoCoordinates site, String siteTooltip, 
			 				GeoCoordinates checkOut, String checkOutTooltip ) {
		
		showMapInt( parent, site, siteTooltip, null, null, checkOut, checkOutTooltip );
	}


	
	public void showMapAll( UI parent, GeoCoordinates site, String siteTooltip, 
			 				GeoCoordinates checkIn, String checkInTooltip, 
			 				GeoCoordinates checkOut, String checkOutTooltip ) {

		showMapInt( parent, site, siteTooltip, checkIn, checkInTooltip, checkOut, checkOutTooltip );
		
	}
	
	private void showMapInt( UI parent, GeoCoordinates site, String siteTooltip, 
						 	GeoCoordinates checkIn, String checkInTooltip, 
						 	GeoCoordinates checkOut, String checkOutTooltip ) {

		logger.debug( "Show Map, Site, CheckIn and CheckOut places in the map: \n'" 
				+ siteTooltip + "' " + ( site != null ? site : "[NULL]" ) + "\n'"
				+ checkIn + "' " + ( checkIn != null ? checkIn : "[NULL]" ) + "'"
				+ checkOut + "' " + ( checkOut != null ? checkOut : "[NULL]" ) + "'"
				);

//	    googleMap.setZoom( 16.0 );
	//    googleMap.setMinZoom(4.0);
	//    googleMap.setMaxZoom(16.0);
	    
	    if ( site != null && site.isValid()) {

		    googleMap.setCenter( new LatLon( site.getLatitude(), site.getLongitude()));
		    
	    	if ( siteMarker != null ) {
	    		
	    		googleMap.removeMarker( siteMarker );
	    	}
		    
	    	siteMarker = new GoogleMapMarker( siteTooltip, new LatLon( site.getLatitude(), site.getLongitude()), false, "VAADIN/themes/tms/icons/32/helmet32.png" );
		    
		    googleMap.addMarker( siteMarker );
		    
		    
	    }
	    
	    if ( checkIn != null && checkIn.isValid()) {

		    googleMap.setCenter( new LatLon( checkIn.getLatitude(), checkIn.getLongitude()));
	    	
	    	if ( checkInMarker != null ) {
	    		
	    		googleMap.removeMarker( checkInMarker );
	    	}
		    
	    	checkInMarker = new GoogleMapMarker( checkInTooltip, new LatLon( checkIn.getLatitude(), checkIn.getLongitude()), false );
		    
		    googleMap.addMarker( checkInMarker );
	    }
	    
	    
	    if ( checkOut != null && checkOut.isValid()) {
		    
		    googleMap.setCenter( new LatLon( checkOut.getLatitude(), checkOut.getLongitude()));
	    	
	    	if ( checkOutMarker != null ) {
	    		
	    		googleMap.removeMarker( checkOutMarker );
	    	}
		    
	    	checkOutMarker = new GoogleMapMarker( checkOutTooltip, new LatLon( checkOut.getLatitude(), checkOut.getLongitude()), false );
		    
		    googleMap.addMarker( checkOutMarker );
	    }

		LatLon[] boundaries = calculateMinMaxBoundaries( site, checkIn, checkOut );
		
		if ( boundaries != null ) {
			googleMap.fitToBounds( boundaries[ 0 ], boundaries[ 1 ] );
		} else {
		    googleMap.setZoom( 12 );
		}
	    
	    
		parent.addWindow( this );
			
	}
		
	private LatLon[] calculateMinMaxBoundaries( GeoCoordinates site, GeoCoordinates checkIn, GeoCoordinates checkOut ) {
		
		LatLon[] boundaries = new LatLon[ 2 ];
		
		boundaries[ 0 ] = new LatLon();
		boundaries[ 1 ] = new LatLon();

		boundaries[ 0 ].setLat( 0 );
		boundaries[ 0 ].setLon( 0 );
		
		boundaries[ 1 ].setLat( 90 );
		boundaries[ 1 ].setLon( 180 );
		
		int count = 0;
		
		if ( site != null && site.isValid()) {
			if ( site.getLatitude() > boundaries[ 0 ].getLat())
				boundaries[ 0 ].setLat( site.getLatitude());
			if ( site.getLongitude() > boundaries[ 0 ].getLon())
				boundaries[ 0 ].setLon( site.getLongitude());
			
			if ( site.getLatitude() < boundaries[ 1 ].getLat())
				boundaries[ 1 ].setLat( site.getLatitude());
			if ( site.getLongitude() < boundaries[ 1 ].getLon())
				boundaries[ 1 ].setLon( site.getLongitude());
			
			count++;
		}
		
		if ( checkIn != null && checkIn.isValid()) {
			if ( checkIn.getLatitude() > boundaries[ 0 ].getLat())
				boundaries[ 0 ].setLat( checkIn.getLatitude());
			if ( checkIn.getLongitude() > boundaries[ 0 ].getLon())
				boundaries[ 0 ].setLon( checkIn.getLongitude());
			
			if ( checkIn.getLatitude() < boundaries[ 1 ].getLat())
				boundaries[ 1 ].setLat( checkIn.getLatitude());
			if ( checkIn.getLongitude() < boundaries[ 1 ].getLon())
				boundaries[ 1 ].setLon( checkIn.getLongitude());
			
			count++;
		}

		
		if ( checkOut != null && checkOut.isValid()) {
			if ( checkOut.getLatitude() > boundaries[ 0 ].getLat())
				boundaries[ 0 ].setLat( checkOut.getLatitude());
			if ( checkOut.getLongitude() > boundaries[ 0 ].getLon())
				boundaries[ 0 ].setLon( checkOut.getLongitude());
			
			if ( checkOut.getLatitude() < boundaries[ 1 ].getLat())
				boundaries[ 1 ].setLat( checkOut.getLatitude());
			if ( checkOut.getLongitude() < boundaries[ 1 ].getLon())
				boundaries[ 1 ].setLon( checkOut.getLongitude());
			
			count++;
		}
		
		if ( logger.isDebugEnabled()) {
			logger.debug( "Minimum coord: " + boundaries[ 1 ] );
			logger.debug( "Maximum coord: " + boundaries[ 0 ] );
		}
		
		if ( count < 2 ) 
			boundaries = null;
		
		return boundaries;
	}

}
