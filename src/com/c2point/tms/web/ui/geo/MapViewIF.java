package com.c2point.tms.web.ui.geo;

import com.c2point.tms.entity.GeoCoordinates;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public interface MapViewIF {

//	public void showMap( UI parent, int x, int y, GeoCoordinates site );
//	public void showMap( UI parent, int x, int y, GeoCoordinates site, GeoCoordinates current, boolean warning );

	
	public void setCaption( String caption ); 

	public void showMap( UI parent, GeoCoordinates site, String siteTooltip );
	public void showMapIn( UI parent, GeoCoordinates site, String siteTooltip, GeoCoordinates checkIn, String checkInTooltip );
	public void showMapOut( UI parent, GeoCoordinates site, String siteTooltip, GeoCoordinates checkOut, String checkOutTooltip );
	public void showMapAll( UI parent, 
						 GeoCoordinates site, String siteTooltip, 
						 GeoCoordinates checkIn, String checkInTooltip,
						 GeoCoordinates checkOut, String checkOutTooltip
	);
	

}
