package com.c2point.tms.web.ui.approveview.model;

import com.c2point.tms.entity.TaskReport;

public class TaskReportHolder extends AbstractReportHolder {

	public TaskReportHolder( ProjectHolder projectHolder, TaskReport report) {
		super( projectHolder, report );
	}

	public TaskReport getReport() {
		
		return ( TaskReport )this.getAbstractReport();
		
	}
}
