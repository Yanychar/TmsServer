package com.c2point.tms.web.util;

import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.web.application.TmsApplication;

public class ToStringConverter {

	public static String convertToString( TmsApplication app, ApprovalFlagType type ) {
			
		switch ( type ) {
			case TO_CHECK:
				return app.getResourceStr( "approve.options.tocheck" ); //"Must be checked!";
			case REJECTED:
				return app.getResourceStr( "approve.options.rejected" ); //"Rejected";
			case APPROVED:
				return app.getResourceStr( "approve.options.approved" ); //"Approved";
			case PROCESSED:
				return app.getResourceStr( "approve.options.processed" ); //"Processed";
		}

		return app.getResourceStr( "approve.edit.traveltype.unknown" ); //"Unknown";
		
	}

	public static String convertToString( TmsApplication app, TravelType type ) {
		
		switch ( type ) {
			case HOME:
				return app.getResourceStr( "approve.edit.traveltype.home" ); 
			case WORK:
				return app.getResourceStr( "approve.edit.traveltype.work" );
			default:
				app.getResourceStr( "approve.edit.traveltype.unknown" ); //"Unknown";
		}
		
		return app.getResourceStr( "approve.edit.traveltype.unknown" ); //"Unknown";
	}
	
	
}
