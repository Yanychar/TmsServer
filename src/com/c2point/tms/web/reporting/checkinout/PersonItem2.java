package com.c2point.tms.web.reporting.checkinout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.TmsUser;

public class PersonItem2 extends AggregateItem {

	private static Logger logger = LogManager.getLogger( PersonItem2.class.getName());
	
	private TmsUser user;
	
	public PersonItem2( AggregateItem parent, TmsUser user ) {
		
		super( parent );
		
		setUser( user );
		
		setHasChilds();
	}

	@Override
	protected boolean isValid() {
		
		boolean bRes = false;
		
		if ( user != null &&
			 user.getCode() != null 
		) {
			bRes = true;
		} else {
			if ( logger.isDebugEnabled()) {
				
				logger.debug( "User is not valid because:" );
				logger.debug( user == null ? "User == Null" : "" );
				logger.debug( user != null && user.getCode() == null ? "User.Code == Null" : "" );
				
			}
			
		}
		
		return bRes;
	}

	@Override
	protected AggregateItem createChild( TmsUser user, CheckInOutRecord record) {

		return new ProjectItem2( this, record.getProject());
		
	}


	public TmsUser getUser() { return user; }
	public void setUser( TmsUser user ) { this.user = user; }

	@Override
	protected String getToCompare() {

		if ( user != null ) {
			
			return user.getLastAndFirstNames(); 
		}
		return null;
	}

	@Override
	protected String getKey() {

		return ( this.user != null ? this.user.getCode() : null );		
	}

	@Override
	protected String getKey( TmsUser user, CheckInOutRecord record ) {

		return ( user != null ? user.getCode() : null );		

	}

}
