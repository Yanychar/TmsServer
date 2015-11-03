package com.c2point.tms.web.reporting.taskandtravel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.DateUtil;

public class UserItem extends AggregateItem {
	
	private static Logger logger = LogManager.getLogger( UserItem.class.getName());
	
	private TmsUser user;
	
	private Map<String, DateItem> dateMap;
	
	
	public UserItem( UsersReport fullReport, TmsUser user ) {
		super( fullReport );
		this.user = user;

		dateMap = new HashMap<String, DateItem>( 40, 0.8f );
	
	}
	
	public TmsUser getUser() { return user; }

	public void handleReport( AbstractReport report ) {
		getItem( report ).handleReport( report );
		
	}
	
	
	
	private DateItem getItem( AbstractReport report ) {
		
		// Find out existing aggregate record for user
		String strDate = DateUtil.dateToString( report.getDate());
		DateItem di = dateMap.get( strDate );
		
		// If record not found -> create one
		if ( di == null ) {
			
			// Create record
			di = new DateItem( this, report.getDate() );
			// Add it to the map of users
			dateMap.put( strDate, di ); 
			logger.debug( "Report date record was created: " + di);
		} else {
			logger.debug( "Report date record was found: " + di);
		}
		
		return di;
	}

	public List<DateItem> values() {
		
		List< DateItem > list = new ArrayList< DateItem >( dateMap.values());

		if ( list != null ) {
			
			Collections.sort( list, new DateItemComparator());
			
		}
		
		return list;
	}
	
	
	@Override
	public String toString() {
		return "UserItem [" + user.getLastAndFirstNames() + ", " + super.toString() + "]";
	}
	public String toStringFull() {
		String str = toString();
		for ( DateItem item : values()) {
			str = str.concat( "\n" + item.toStringFull());
		}
		
		return str;
	}

	class DateItemComparator implements Comparator< DateItem >{

		@Override
		public int compare( DateItem arg1, DateItem arg2 ) {
			if ( arg1.getDate() == null ) return -1;
			if ( arg2.getDate() == null ) return 1;
			
			return arg1.getDate().compareTo( arg2.getDate());
		}

	}
	

}
