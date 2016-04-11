package com.c2point.tms.web.reporting.taskandtravel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.Task;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.util.StringUtils;

public class TaskItem extends AggregateItem {
	
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( TaskItem.class.getName());
	
	private ProjectTask		projectTask;
	
//	private AggregateItem	own;
	
	public TaskItem( PrjItem pi, TaskReport report ) {
		super( pi );
		
		projectTask = report.getProjectTask(); 
//		handleReport( report );
	}

//	public TaskReport getReport() { return report; }
	public Task getTask() { return projectTask.getTask(); }
	
	public void handleReport( TaskReport report ) {
		
		if ( report != null ) {
			addHours( report.getApprovalFlagType(), report.getHours());
			addNumValue( report.getApprovalFlagType(), report.getNumValue());
			
		}
		
	}
	
	public String getNumValueMeasure() {
		
		String str;
		
		try {
			str = projectTask.getTask().getMeasurementUnit().getName();
		} catch ( Exception e ) {
			str = "";
		}
		
		return str;
	}

	
	@Override
	public String toString() {
		return "      Task Item ['" + StringUtils.padRightSpaces( getTask().getCode(), 8 ) 
									+ projectTask.getTask().getName() + "', hours=" 
									+ getHours() + ", value="
									+ getNumValue()
									/* + getApprovalFlagType()*/ + " ]";
	}

}
