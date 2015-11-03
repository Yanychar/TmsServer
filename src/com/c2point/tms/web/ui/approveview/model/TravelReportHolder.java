package com.c2point.tms.web.ui.approveview.model;

import com.c2point.tms.entity.TravelReport;

public class TravelReportHolder extends AbstractReportHolder {

	public TravelReportHolder( ProjectHolder projectHolder, TravelReport report) {
		super( projectHolder, report );
	}

	public TravelReport getReport() {
		
		return ( TravelReport )this.getAbstractReport();
		
	}
}
