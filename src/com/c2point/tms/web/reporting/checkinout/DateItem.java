package com.c2point.tms.web.reporting.checkinout;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.DateUtil;

public class DateItem extends AggregateItem {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( DateItem.class.getName());
	
	private Date date;

/*	
	public DateItem( AggregateItem parent ) {
		
		super( parent );
		
	}
*/
	public DateItem( AggregateItem parent, Date date ) {
		
		super( parent );
		
		setDate( date );
		
	}

	@Override
	protected boolean isValid() {
		
		
		return ( date != null );
	}

	@Override
	protected AggregateItem createChild( TmsUser user, CheckInOutRecord record) {

		return null;
	}

	public Date getDate() { return date; }
	private void setDate( Date date ) { this.date = date; }

	@Override
	protected String getToCompare() {

		if ( date != null ) {
			
			return Long.toString( date.getTime());
		}
		
		return "0";
	}

	@Override
	protected String getKey(TmsUser user, CheckInOutRecord record) {

		return DateUtil.dateNoDelimToString( record.getDateCheckedIn());		
	}

	@Override
	protected String getKey() {

		return DateUtil.dateNoDelimToString( date );		
	}

	
}
