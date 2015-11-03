package com.c2point.tms.web.reporting.taskandtravel;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.util.DateUtil;

public class DateItem extends AggregateItem {

	private static Logger logger = LogManager.getLogger( DateItem.class.getName());
	
	private Date 	date;
	
	private Map<String, PrjItem> prjMap;
	
	public DateItem( UserItem ui, Date date ) {
		super ( ui );
		this.date = date;

		prjMap = new HashMap<String, PrjItem>( 5, 0.75f );
	}

	public Date getDate() { return date; }
	
	public void handleReport( AbstractReport report ) {
		if ( report != null ) {
			getItem( report ).handleReport( report );
		} else {
			logger.error( "NULL has been passed as AbstractReport for Reporting!" );
		}
		
	}
	
	private PrjItem getItem( AbstractReport report ) {
		
		Project project = null;

		if ( report instanceof TaskReport ) {
			project = (( TaskReport )report ).getProjectTask().getProject();
		} else if ( report instanceof TravelReport ) {
			project = (( TravelReport )report ).getProject();
		}
		
		// If Project == null (can be in old records by mistake
		if ( project == null ) {
			project = new Project( "???", "*** ??? ***" );
			project.setOrganisation( report.getUser().getOrganisation());
		}
		// Find out existing aggregate record for user
		
		PrjItem pi = prjMap.get( project.getCode());
		
		// If record not found -> create one
		if ( pi == null ) {
			
			// Create record
			pi = new PrjItem( this, project );
			// Add it to the map of users
			prjMap.put( project.getCode(), pi ); 
			logger.debug( "Report project record was created: " + pi);
		} else {
			logger.debug( "Report project record was found: " + pi);
		}
		
		return pi;
	}
	
	public List<PrjItem> values() {
		
		List< PrjItem > list = new ArrayList< PrjItem >( prjMap.values());

		if ( list != null ) {
			
			Collections.sort( list, new ProjectItemComparator());
			
		}
		
		return list;
	}
	
	@Override
	public String toString() {
		return "  DateItem [" + DateUtil.dateToString( date ) + ", " + super.toString() + "]";
	}

	public String toStringFull() {
		String str = toString();
		for ( PrjItem item : values()) {
			str = str.concat( "\n" + item.toStringFull() );
		}
		
		return str;
	}

	class ProjectItemComparator implements Comparator< PrjItem >{
		
		private Collator standardComparator;
		private int compareField = 0;   	// 1 - to compare project names
											// other - to compare project codes
		
		public ProjectItemComparator() {
			standardComparator = Collator.getInstance(); 
		}
		
		@Override
		public int compare( PrjItem arg1, PrjItem arg2 ) {
			
			if ( compareField == 0 ) {
				try {
					String str = arg1.getProject().getOrganisation().getProperties().getProperty( "company.projects.order.name" );
					compareField = Integer.parseInt( str );
				} catch ( Exception e ) {
					logger.error( "'company.projects.order.name' property was not found or wrong for : '" + arg1.getProject().getOrganisation().getName() + "'" );
					compareField = 2;
				}
			}
			
			
			if ( compareField == 1 ) {
				return standardComparator.compare( arg1.getProject().getName(), arg2.getProject().getName());
			}
			
			return standardComparator.compare( arg1.getProject().getCode(), arg2.getProject().getCode());
		}

	}
	
}
