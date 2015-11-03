package com.c2point.tms.entity.stubs.taskreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.util.DateUtil;

@XmlRootElement(name = "reports")
@XmlType(propOrder = { "project", "date", "reports" })
public class TaskReportsListStub {
	// This is project code. It is equal for all Task Reports
	private String project;
	// Date of report (OPTIONAL)
	//   Date is the same for all TaskReports saved
    private String	date;
	
	
	@XmlElement( name = "task" )
	List<TaskReportStub> reports = new ArrayList<TaskReportStub>();
	
	Map<String,String> hm = new HashMap<String,String>();
	
	public TaskReportsListStub() {
	}
	
	public TaskReportsListStub( TaskReportStub stub ) {
		this( stub, null, null );
	}
	
	public TaskReportsListStub( TaskReportStub stub, String projectCode, String dateStr ) {
		this.project = projectCode;
		this.date = dateStr;

		if ( stub != null ) {
			// Take Project information from TaskReport itself if it was not done already
			this.reports.add( stub );
			
		}
	}
	
	public TaskReportsListStub( List<TaskReport> list ) {
		init( list );
	}
	
	private void init( List<TaskReport> list ) {
		
		boolean initialised = false;
		
		for ( TaskReport report : list ) {
			if ( report != null ) {
				// Take Project information from TaskReport itself if it was not done already
				if ( !initialised && report.getProjectTask() != null ) {
					this.project = report.getProjectTask().getProject().getCode();
					this.date = DateUtil.dateNoDelimToString( report.getDate());
					initialised = true;
				}
				
				this.reports.add( new TaskReportStub( report ));
				
				// Add Task unique code to the map to save tasks reported by user
				hm.put( report.getProjectTask().getTask().getCode(), "" );
			}
		}
		
		
	}
	
	public void addAvailableProjectTasks( List<ProjectTask> list ) {
		if ( list != null ) {
			for ( ProjectTask prjTask : list ) {
				if ( prjTask != null && !prjTask.isDeleted()) {
					if ( !hm.containsKey( prjTask.getTask().getCode())) {
						this.reports.add( new TaskReportStub( prjTask ));
						// Add Task unique code to the map to save tasks reported by user
						hm.put( prjTask.getTask().getCode(), "" );
					}
				}
			}
		}
	}

	public TaskReportsListStub addStub( TaskReportStub stub ) {
		if ( stub != null ) {
			this.reports.add( stub );
		}
		
		return this;
	}
	
	
	public String getProject() {
		return project;
	}

	public void setProject( String project ) {
		this.project = project;
	}

	public String getDate() {
		return date;
	}

	public void setDate( String date ) {
		this.date = date;
	}
	
	public List<TaskReportStub> getReports() {
		return reports;
	}

}
