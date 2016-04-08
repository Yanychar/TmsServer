package com.c2point.tms.web.reporting.taskandtravel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Task;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.util.StringUtils;

public class TaskItem { //extends AggregateItem {
	
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( TaskItem.class.getName());
	
	private TaskReport	report;
	
	private AggregateItem	owner;
	
	public TaskItem( PrjItem pi, TaskReport report ) {
		this.owner	=  pi; 
		this.report	= report;
		handleReport();
	}

	public TaskReport getReport() { return report; }
	public Task getTask() { return report.getProjectTask().getTask(); }
	
	private void handleReport() {
		
		if ( this.report != null ) {
			owner.addHours( report.getApprovalFlagType(), this.report.getHours());
			owner.addNumValue( report.getApprovalFlagType(), this.report.getNumValue());
		}
		
	}
	
	public float getHours() {
		return report.getHours();
	}

	public float getNumValue() {
		return report.getNumValue();
	}

	public String getNumValueMeasure() {
		
		String str;
		
		try {
			str = report.getTask().getMeasurementUnit().getName();
		} catch ( Exception e ) {
			str = "";
		}
		
		return str;
	}

	
	@Override
	public String toString() {
		return "      Task Item ['" + StringUtils.padRightSpaces( getTask().getCode(), 8 ) 
									+ getTask().getName() + "', hours=" 
									+ report.getHours() + ", value="
									+ report.getNumValue()
									+ report.getApprovalFlagType() + " ]";
	}

}
