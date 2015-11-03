package com.c2point.tms.web.reporting.checkinout;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.entity.TmsUser;

public class UsersReport extends AbstractReport {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( UsersReport.class.getName());

	public UsersReport( SupportedFunctionType accessType, Organisation organisation, TmsUser user,
						Date startDate, Date endDate ) {
		
		super( accessType, organisation, user, startDate, endDate );
		
		setHasChilds();
		
	}

	@Override
	protected AggregateItem createChild( TmsUser user, CheckInOutRecord record ) {

		return new PersonItem2( this, user );

	}


	
}
