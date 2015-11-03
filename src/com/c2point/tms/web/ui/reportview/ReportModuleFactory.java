package com.c2point.tms.web.ui.reportview;

import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractMainView;
import com.c2point.tms.web.ui.reportview.checkinout.ReportCheckInOutModel;
import com.c2point.tms.web.ui.reportview.checkinout.ReportCheckInOutView;
import com.c2point.tms.web.ui.reportview.tasktravel.ReportTaskTravelModel;
import com.c2point.tms.web.ui.reportview.tasktravel.ReportTaskTravelView;

public class ReportModuleFactory {

	public ReportModuleIF getReportModule( boolean checkInOutBased, 
			TmsApplication app, Organisation org, SupportedFunctionType reportsToShow ) {
		
		ReportModuleIF module;
		
		if ( checkInOutBased ) {
			
			module = createCheckInOutReportingModule( app, reportsToShow );
			
		} else {
			
			module = createTaskAndTravelReportingModule( app, reportsToShow );
			
		}
		
		
		
		return module;
	}
	
	private ReportModuleIF createTaskAndTravelReportingModule( final TmsApplication app, final SupportedFunctionType reportsToShow ) {
		
		ReportModuleIF module = new ReportModuleIF() {

			
			@Override
			public AbstractMainView getConfiguredReportView() {

				ReportTaskTravelModel model = new ReportTaskTravelModel( app, reportsToShow );
				
				return new ReportTaskTravelView( model );
			}
			
		};
		
		return module;
	}

	private ReportModuleIF createCheckInOutReportingModule( final TmsApplication app, final SupportedFunctionType reportsToShow ) {
		
		ReportModuleIF module = new ReportModuleIF() {

			@Override
			public AbstractMainView getConfiguredReportView() {

				ReportCheckInOutModel model = new ReportCheckInOutModel( app, reportsToShow );
				
				return new ReportCheckInOutView( model );
			}
			
		};
		
		return module;
	}

}
