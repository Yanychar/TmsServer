package com.c2point.tms.web.ui.geo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapFactory {

	private static Logger logger = LogManager.getLogger( MapFactory.class.getName());
	
	public static MapViewIF getMapView( SupportedMapProviderType mapType ) {
		
		MapViewIF mapView = null;
		
		switch ( mapType ) {
			case GOOGLE_PROVIDER:
				mapView = new GoogleMapView();
				break;
			default:
				logger.error( "Specified rovider does not exist" );
				break;
		}
		
		return mapView;
		
	}

}
