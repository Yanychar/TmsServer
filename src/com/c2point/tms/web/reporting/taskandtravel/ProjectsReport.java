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
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TravelReport;

public class ProjectsReport extends AggregateItem {
	private static Logger logger = LogManager.getLogger( ProjectsReport.class.getName());

	private Map<String, PrjItem> prjMap;

	public ProjectsReport() {
		super( null );
		
		prjMap = new HashMap<String, PrjItem>( 100, 1 );

	}

	public ProjectsReport prepareReport( 
			List<TaskReport> listTaskReports, 
			List<TravelReport> listTravelReports,
			Project project
	) {
		
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
			if ( reportAssignedToProject( report, project ))
				handleReport( report);
		}

		for ( AbstractReport report : listTravelReports ) {
			if ( reportAssignedToProject( report, project ))
		 	  handleReport( report);
		}
		return this;
	}
	
	private boolean reportAssignedToProject( AbstractReport report, Project project ) {
		
		boolean res = true;

		if ( project != null ) {
			
			// Project specified. We need to get reports related to this project only

			if ( report instanceof TaskReport ) {
				res = (( TaskReport )report ).getProjectTask().getProject().getId() == project.getId();
			} else if ( report instanceof TravelReport ) {
				res = (( TravelReport )report ).getProject().getId() == project.getId();
			} else {
				
				res = false;
			}
		
		
		
			
		}
		// project == null. Means all reports shall be counted in!
		return res;
	}
	
	
	private void handleReport( AbstractReport report ) {
		getItem( report ).handleReport( report );
		
	}
	
	private PrjItem getItem( AbstractReport report ) {
		
		Project project = null;

		if ( report instanceof TaskReport ) {
			project = (( TaskReport )report ).getProjectTask().getProject();
		} else if ( report instanceof TravelReport ) {
			project = (( TravelReport )report ).getProject();
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
	
	public String toStringFull() {
		
		String str = "********** Full Projects Report listing ************";
		
		for ( PrjItem item : values()) {
			str = str.concat( "\n" + item.toStringFull());
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
