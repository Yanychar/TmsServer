package com.c2point.tms.web.ui.reportsmgmt.timereports.model;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.Task;
import com.c2point.tms.entity.TaskReport;

public class WeekProjectTaskId {
	private long 	projId;
	private long	taskId;
	
	private int	hashValue = -1;
	
	public WeekProjectTaskId( long projectId, long taskId ) {
		this.projId = projectId;
		this.taskId = taskId;
	}

	public WeekProjectTaskId( Project project, Task task ) {
		this( project.getId(), task.getId());
		
	}

	public WeekProjectTaskId( ProjectTask projectTask ) {
		this( projectTask.getProject(), projectTask.getTask());
		
	}

	public WeekProjectTaskId( TaskReport report ) {
		this( report.getProjectTask());
		
	}

	public WeekProjectTaskId( WeekItem item ) {
		this( item.getProjectTask());
		
	}

	public boolean equals( Object obj ) {
		if ( obj instanceof WeekProjectTaskId ) {
			WeekProjectTaskId id = ( WeekProjectTaskId )obj;
			if ( this.projId == id.projId && this.taskId == id.taskId ) {
				return true;
			}
	
		}
		return false;
	}
	
	public int hashCode() {
		if ( hashValue < 0 ) {
			hashValue = 23*37 
					+ ( int )( projId ^ ( projId >>> 32 ))
					+ ( int )( taskId ^ ( projId >>> 32 ) );
		}
		
		return hashValue;
		
	}
		
}
