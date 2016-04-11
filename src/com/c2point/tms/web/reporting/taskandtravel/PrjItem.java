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
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.util.StringUtils;

public class PrjItem extends AggregateItem {
	
	private static Logger logger = LogManager.getLogger( PrjItem.class.getName());
	
	private Project project;
	
	private Map<ProjectTask, TaskItem> 	taskMap;
	private List<TravelItem> travelList;
	
	public PrjItem( AggregateItem ai, Project project ) {
		super( ai );
		
		this.project = project;

		taskMap = new HashMap<ProjectTask,TaskItem>( 5 );
		travelList = new ArrayList<TravelItem>( 5 );
	
	}

	public Project getProject() { return project; }

	@SuppressWarnings("rawtypes")
	public List< TaskItem > getTaskItems() {
		
		@SuppressWarnings("unchecked")
		List< TaskItem> tl =  new ArrayList( taskMap.values());
		if ( tl != null ) {
			Collections.sort( tl, new TaskItemComparator());
		}

		return tl;
	}
	
	public List< TravelItem > getTravelItems() {
		
		return travelList;
	}
	
	
	public void handleReport( AbstractReport report ) {
		
		
		if ( report instanceof TaskReport ) {
			
			TaskItem taskItem = taskMap.get((( TaskReport )report).getProjectTask());
			
			if ( taskItem == null ) {
				
				taskItem = new TaskItem( this, ( TaskReport )report );
				taskMap.put((( TaskReport )report).getProjectTask(), taskItem );
				logger.debug( "TaskItem for TaskReport has been created " );
			}
			
			taskItem.handleReport(( TaskReport )report );
			logger.debug( "TaskItem for TaskReport has been created " );
			
			
		} else if ( report instanceof TravelReport ) {
			travelList.add( new TravelItem( this, ( TravelReport )report ));
			logger.debug( "TravelItem for TravelReport has been created " );
		} 
		
	}
	
	
	@Override
	public String toString() {
		return "    Project Item ['" + StringUtils.padRightSpaces( project.getCode(), 8 ) + project.getName() + "', " + super.toString() + "]";
	}

	public String toStringFull() {
		String str = toString();
		for ( TaskItem item : getTaskItems()) {
			str = str.concat( "\n" + item.toString());
		}
		for ( TravelItem item : getTravelItems()) {
			str = str.concat( "\n" + item.toString());
		}
		
		return str;
	}

	public class TaskItemComparator implements Comparator< TaskItem >{
		
		private Collator standardComparator;
		private int compareField = 0;   	//  0 - uninitialized. Shall be initialized in first comparison
											// 1 - to compare tasks names
											// other - to compare tasks codes
		
		public TaskItemComparator() {
			standardComparator = Collator.getInstance(); 
		}
		
		@Override
		public int compare( TaskItem arg1, TaskItem arg2 ) {
			
			if ( compareField == 0 ) {
				try {
					String str = arg1.getTask().getOrganisation().getProperties().getProperty( "company.tasks.order.name" );
					compareField = Integer.parseInt( str );
				} catch ( Exception e ) {
					logger.error( "'company.tasks.order.name' property was not found or wrong for : '" + arg1.getTask().getOrganisation().getName() + "'" );
					compareField = 2;
				}
			}
			
			
			if ( compareField == 1 ) {
				return standardComparator.compare( arg1.getTask().getName(), arg2.getTask().getName());
			}
			
			return standardComparator.compare( arg1.getTask().getCode(), arg2.getTask().getCode());
		}

	}

}
