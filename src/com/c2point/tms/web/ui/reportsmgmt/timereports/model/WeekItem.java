package com.c2point.tms.web.ui.reportsmgmt.timereports.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.web.ui.DateModel;
import com.c2point.tms.web.ui.DaysOfWeek;

public class WeekItem {
	private static Logger logger = LogManager.getLogger( WeekItem.class.getName());

	private ProjectTask						projectTask;
	private Map<DaysOfWeek, TaskReport> 	reports;
//	private DateModel						dateModel;
	
	public  WeekItem( ProjectTask projectTask, DateModel dateModel ) {
		
		this.projectTask = projectTask;
		this.reports = new HashMap<DaysOfWeek, TaskReport>();
//		this.dateModel = dateModel;
	}
/*
	public WeekItem( ProjectTask projectTask ) {
		this( projectTask, null );
	}
*/	
	public ProjectTask getProjectTask() {
		return projectTask;
	}
	public void setProjectTask( ProjectTask projectTask ) {
		this.projectTask = projectTask;
	}
	public Map<DaysOfWeek, TaskReport> getReports() {
		return reports;
	}
	public void setReports( Map<DaysOfWeek, TaskReport> reports ) {
		this.reports = reports;
	}

	public TaskReport getReport( DaysOfWeek dayOfWeek ) {
		return reports.get( dayOfWeek );
	}
	
	public WeekProjectTaskId getId() {
		return new WeekProjectTaskId( this ); 
	}
	
	private Calendar reportCal = Calendar.getInstance();
	public boolean putReport( TaskReport report ) {
		
		reportCal.setTime( report.getDate());

		// Assign
		reports.put( DaysOfWeek.convertFromCalendarDOW( reportCal.get( Calendar.DAY_OF_WEEK )), report );
		if ( logger.isDebugEnabled()) logger.debug( "TaskReport was put into weekly reports Map: " + DaysOfWeek.convertFromCalendarDOW( reportCal.get( Calendar.DAY_OF_WEEK )) );
		
		return true;
	}
		
	public boolean removeReport( TaskReport report ) {
		
		reportCal.setTime( report.getDate());

		// Assign
		reports.put( DaysOfWeek.convertFromCalendarDOW( reportCal.get( Calendar.DAY_OF_WEEK )), null );
		if ( logger.isDebugEnabled()) logger.debug( "TaskReport was removed from weekly reports Map" );
		
		return true;
	}
		
	
	
	



	@Override
	public String toString() {
		return "WeekItem [Project.Task=" + projectTask.getProject().getName() + "."+ projectTask.getTask().getName() + "]";
	}

	
}
