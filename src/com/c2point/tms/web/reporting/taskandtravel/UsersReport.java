package com.c2point.tms.web.reporting.taskandtravel;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.TravelReport;

public class UsersReport extends AggregateItem {

	private static Logger logger = LogManager.getLogger( UsersReport.class.getName());

	private Map<String, UserItem> userMap;

	public UsersReport( List<TmsUser> listOfUsers ) {
		super( null );
		
		int initialCapasity = 40;
		if ( listOfUsers != null && listOfUsers.size() > initialCapasity  ) {
			initialCapasity = listOfUsers.size();
		}
		userMap = new HashMap<String, UserItem>( initialCapasity, 1 );

		fillByUserList( listOfUsers );
	
	}

	public UsersReport prepareReport( 
			List<TaskReport> listTaskReports, 
			List<TravelReport> listTravelReports ) {
		
		// Create Tree of Items ordered by User, Date
		//
		//								Hours   Työajo_km   Työmatka
		//   + User_1                    20        100         20
		//   + User_2
		//     + 21.01.2013
		//     - 22.01.2013
		//        + Peoject_1
		//        + Peoject_2                
		//           Task_1
		//           Task_2
		//           Task_3
		//        + Peoject_3
		//   + User_3
		//
		
		for ( AbstractReport report : listTaskReports ) {
			handleReport( report);
		}

		for ( AbstractReport report : listTravelReports ) {
			handleReport( report);
		}
		return this;
	}
	
	private void fillByUserList( List<TmsUser> listOfUsers ) {
		
		if ( listOfUsers != null ) {
			
			for ( TmsUser user : listOfUsers ) {
				getItem( user );
			}
		}
	}
	
	
	private void handleReport( AbstractReport report ) {
		getItem( report.getUser()).handleReport( report );
		
	}
	
	private UserItem getItem( TmsUser user ) {
		
		if ( user == null ) {
			logger.error( "NULL found in the List<TmsUser> passed for Reporting!" );
			return null;
		}
		// Find out existing aggregate record for user
		UserItem ui = userMap.get( user.getCode());
		
		// If record not found -> create one
		if ( ui == null ) {
			
			// Create record
			ui = new UserItem( this, user );
			// Add it to the map of users
			userMap.put( user.getCode(), ui ); 
			logger.debug( "Report user record was created: " + ui);
		} else {
			logger.debug( "Report user record was found: " + ui);
		}
		
		return ui;
	}

	public List<UserItem> values() {
		
		List< UserItem > list = new ArrayList< UserItem >( userMap.values());

		if ( list != null ) {
			
			Collections.sort( list, new UserItemComparator());
			
		}
		
		return list;
		
	}

	public String toStringFull() {
		
		String str = "********** Full Personnel Report listing ************";
		
		for ( UserItem item : values()) {
			str = str.concat( "\n" + item.toStringFull());
		}
		
		return str;
		
	}

	class UserItemComparator implements Comparator< UserItem >{

		private Collator standardComparator;
		
		public UserItemComparator() {
			standardComparator = Collator.getInstance(); 
		}
		
		@Override
		public int compare( UserItem arg1, UserItem arg2 ) {
			return standardComparator.compare( arg1.getUser().getLastName(), arg2.getUser().getLastName());
		}

	}
	

}
