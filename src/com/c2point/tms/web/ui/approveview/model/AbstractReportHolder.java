package com.c2point.tms.web.ui.approveview.model;

import com.c2point.tms.entity.AbstractReport;

public abstract class AbstractReportHolder {

	private AbstractReport	report;
	private ProjectHolder	projectHolder;
	
	public AbstractReportHolder( ProjectHolder projectHolder, AbstractReport report ) {
		this.projectHolder = projectHolder;
		this.report = report;
	}
	
	public ProjectHolder getProjectHolder() { return projectHolder; }
	
	public String toString() {
		
		return "ReportHolder ( "
			 + "reportApprovalType= " + ( report != null ? report.getApprovalFlagType() : "NULL REPORT" ) + ", "
			 + "reportUniquwId= '" + ( report != null ? report.getUniqueReportId() : "NULL REPORT" ) + "', "
			 + "project Name='" + ( projectHolder != null && projectHolder.getProject() != null ? projectHolder.getProject().getName() : "NULL PROJECT or HOLDER" ) +"'";
	}


	protected AbstractReport getAbstractReport() { return report; }

}
