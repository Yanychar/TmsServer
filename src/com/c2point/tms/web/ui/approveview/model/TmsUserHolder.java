package com.c2point.tms.web.ui.approveview.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.TmsUser;

public class TmsUserHolder {
	
	private static Logger logger = LogManager.getLogger( TmsUserHolder.class.getName());
	
	private TmsUser	user;

	private int 	noValidated;
	private int		approved;
	private int		rejected;
	private int		processed;

	// Key = Date+PrjCode
	private SortedMap< String, ProjectHolder> projectHoldersMap = new TreeMap< String, ProjectHolder >();

	public TmsUserHolder( TmsUser user ) {
		this.user = user;
		
		noValidated = 0;
		approved = 0;
		rejected = 0;
		processed = 0;

	}
	
	public int getNoValidated() { return noValidated; }
	public int getApproved() 	{ return approved; }
	public int getRejected() 	{ return rejected; }
	public int getProcessed() 	{ return processed; }

	public TmsUser getTmsUser() { return user; }

	public Collection< ProjectHolder > values() {
		
		return projectHoldersMap.values();
	}
	public Collection< ProjectHolder > sortedValues() {
		return sortedValues( true );
	}
	public Collection< ProjectHolder > sortedValues( final boolean asc ) {
		
		List< ProjectHolder > list = new ArrayList< ProjectHolder >( values());
		
		if ( logger.isDebugEnabled()) logger.debug ( "  ProjectHolder sorting has been call!" );
		Collections.sort( list, new Comparator<ProjectHolder>() {
			
			@Override
			public int compare( ProjectHolder arg1, ProjectHolder arg2 ) {
				
				Date date1 = null;
				Date date2 = null;
				
				try {
					date1 = (( ProjectHolder )arg1 ).getDate();
					date2 = (( ProjectHolder )arg2 ).getDate();
				} catch ( Exception e ) {
					logger.error( "Something wrong in ProjectHolder. Cannot fetch date." );
				}

				if ( arg1 == null ) {
					return ( asc ? -1 : 1 );
				} else if ( arg2 == null ) {
					return ( asc ? 1 : -1 );
				} else if ( date1.after( date2 )) {
					return ( asc ? 1 : -1 );
				} else if ( date1.before( date2 )) {
					return ( asc ? -1 : 1 );
				}
		        
				return 0;
			}

		});

		return list;
	}

	public ProjectHolder getProjectHolder( AbstractReport report ) {
		
		return projectHoldersMap.get( ProjectHolder.getKey( report ));
	}
	
	public boolean addReport( AbstractReport report ) {
		boolean bRes = false;

		if ( report.getUser().getId() == this.user.getId()) {

			ProjectHolder holder = getProjectHolder( report );
			if ( holder == null ) {
				if ( logger.isDebugEnabled()) logger.debug ( "  New ProjectHolder for date: '" + report.getDate() + "'" );
				
				holder = new ProjectHolder( report );

				projectHoldersMap.put( holder.getKey(), holder );
				
			} else {
				if ( logger.isDebugEnabled()) logger.debug ( "  ProjectHolder for date: '" + report.getDate() + "' exists already!" );
			}

			bRes = holder.addReport( report );

		}
		
		return bRes;
	}

	public void updateCounters() {

		noValidated = 0;
		approved = 0;
		rejected = 0;
		processed = 0;
		
		for ( ProjectHolder ph : values()) {
			
			ph.updateCounters();
			
			addCounters( ph );
			
		}
	}
	
	
	public void clear() {

		for ( ProjectHolder ph : projectHoldersMap.values()) {
			ph.clear();
		}

		projectHoldersMap.clear();

		noValidated = 0;
		approved = 0;
		rejected = 0;
		processed = 0;
		
	}

	private void addCounters( ProjectHolder ph ) {
		this.noValidated = this.noValidated + ph.getNoValidated();
		this.approved	 = this.approved	+ ph.getApproved();
		this.rejected	 = this.rejected	+ ph.getRejected();
		this.processed	 = this.processed	+ ph.getProcessed();
	}
	
	/*	
	private void addCounter( AbstractReport report ) { 
		switch ( report.getApprovalFlagType()) {
			case TO_CHECK:
				this.noValidated++;
				break;
			case APPROVED:
				this.approved++;
				break;
			case REJECTED:
				this.rejected++;
				break;
			case PROCESSED:
				this.processed++;
				break;
		}
	}
*/
	public String toString() {
		
		return "noValidated = " + noValidated
			 + "approved = " + approved
			 + "rejected = " + rejected
			 + "processed = " + processed;
		
	}
}

