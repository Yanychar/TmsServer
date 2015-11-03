package com.c2point.tms.util;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsUser;

public class WorkHoursCalculator {
	private static Logger logger = LogManager.getLogger( WorkHoursCalculator.class.getName());
	
	public static float calculateWorkTime( TmsUser user, Date startTime, Date endTime ) {

		float time = ( endTime.getTime() - startTime.getTime()) / 3600000.0f;  // Converting milliseconds into hours
	
		return calculateWorkTime( user, time );
	}

	public static float calculateWorkTime( TmsUser user, float hours ) {

		Organisation org = user.getOrganisation();
		
		if ( org != null ) {
			if ( Boolean.parseBoolean( org.getProperty( "company.lunchbreak.count", "false" ))) {
				if ( logger.isDebugEnabled()) logger.debug( "Lunch break length shall be counted" );
				int deltaInMinutes = 0;
				try {
					deltaInMinutes = Integer.parseInt( org.getProperty( "company.lunchbreak.length", "0" ));
					if ( logger.isDebugEnabled()) logger.debug( "Length of lunch break = " + deltaInMinutes + " (min)" );
				} catch ( Exception e ) {
					logger.error( "Wrong value of property 'company.lunchbreak.length' for company: " + org.getName());
				}
				
				if ( logger.isDebugEnabled()) logger.debug( "Worktime before lunch break count = " + hours + " (hours)" );
				hours = hours - deltaInMinutes / 60.f; 
				if ( logger.isDebugEnabled()) logger.debug( "Worktime after lunch break count = " + hours + " (hours)" );
			} else {
				if ( logger.isDebugEnabled()) logger.debug( "No lunch breaks shall be counted" );
			}
			
		} else {
			logger.error( "Organisation is not available for user: " + user.getFirstAndLastNames());
		}
		
		return hours;
	}

}
