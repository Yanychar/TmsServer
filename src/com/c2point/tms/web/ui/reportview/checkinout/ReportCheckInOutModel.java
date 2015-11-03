package com.c2point.tms.web.ui.reportview.checkinout;


import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.access.SecurityContext;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.checkinout.ProjectsReport;
import com.c2point.tms.web.reporting.checkinout.UsersReport;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.PeriodSelectionComponent;
import com.c2point.tms.web.ui.listeners.FilterChangedListener;

public class ReportCheckInOutModel extends AbstractModel implements PeriodSelectionComponent.DateModelIf {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ReportCheckInOutModel.class.getName());

	public enum ReportType { PERSONNEL_VIEW, PROJECT_VIEW };
	

	private Organisation	reportedOrganisation;
	
	private Date startDate;
	private Date endDate;
	
	private ReportType reportType = ReportType.PERSONNEL_VIEW;
	
	// Projects report flags
	private boolean projectPersonelFlag;
	private boolean projectDateFlag;

	// Personnel report flags
	private boolean personnelDateFlag;
	private boolean personelProjectsFlag;
	
	private boolean	silentFire = true;
	

	// Current AccessRights flag
 	private SupportedFunctionType reportsToShow;

 	private boolean changed = true;
	
	public ReportCheckInOutModel( TmsApplication app, SupportedFunctionType reportsToShow ) {
		this( app, null, reportsToShow );
	}

 	
 	public ReportCheckInOutModel( TmsApplication app, Organisation org, SupportedFunctionType reportsToShow ) {
		super( app );
		
		if ( org != null ) {
			reportedOrganisation = org;
		} else {
			reportedOrganisation = getSessionOwner().getOrganisation();
		}
		
		initFilter( reportsToShow );
	}
 	
 	public Organisation getOrganisation() {
 		return reportedOrganisation;
	}
 	
	public ReportType getReportType() {
		return reportType;
	}
	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
		
		fireFilterChanged();
	}
	
	
	public boolean isProjectPersonnelFlag() { return this.projectPersonelFlag; }
	public void setProjectPersonnelFlag( boolean projectPersonelFlag ) { 
		this.projectPersonelFlag = projectPersonelFlag; fireFilterChanged(); 
	}
	
	public boolean isProjectDateFlag() { return this.projectDateFlag; }
	public void setProjectDateFlag( boolean projectDateFlag ) { 
		this.projectDateFlag = projectDateFlag; fireFilterChanged();
	}
	
	public boolean isPersonnelProjectsFlag() { return this.personelProjectsFlag; }
	public void setPersonnelProjectsFlag( boolean personelProjectsFlag ) { 
		this.personelProjectsFlag = personelProjectsFlag; fireFilterChanged(); 
	}
	
	public boolean isPersonnelDateFlag() { return this.personnelDateFlag; }
	public void setPersonnelDateFlag( boolean personnelDateFlag ) { 
		this.personnelDateFlag = personnelDateFlag; fireFilterChanged();
	}
	
	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	@Override
	public Date getStartDate() { return startDate; 	}
	@Override
	public void setStartDate( Date startDate ) { 
		this.startDate = startDate; 
		fireFilterChanged();
	}

	@Override
	public Date getEndDate() { return endDate; }
	@Override
	public void setEndDate( Date endDate ) { 
		this.endDate = endDate; 
		fireFilterChanged();
	}
	
	
	public void addChangedListener( FilterChangedListener listener ) {
		listenerList.add( FilterChangedListener.class, listener);
	}

	private void fireFilterChanged() {

		changed = true;
		
		if ( !silentFire ) {
			Object[] listeners = listenerList.getListenerList();
	
		    for ( int i = listeners.length-2; i >= 0; i -= 2) {
		    	if ( listeners[ i ] == FilterChangedListener.class) {
		    		(( FilterChangedListener )listeners[ i + 1 ] ).filterChanged();
		         }
		     }
		}
	}

	public void setSilentFlag( boolean flag ) { silentFire = flag; }
	
	/**** Methods to work with Users and Reports *****/
	
	public void setReportsToShow( SupportedFunctionType reportsToShow ) {
		if ( this.reportsToShow != reportsToShow ) { 

			SecurityContext context = getSessionOwner().getContext();
			
			if ( reportsToShow == SupportedFunctionType.CONSOLIDATE_TEAM 
				&& (
					context.isWrite( SupportedFunctionType.CONSOLIDATE_TEAM )
					|| 
					context.isWrite( SupportedFunctionType.CONSOLIDATE_COMPANY ))) {
				
				this.reportsToShow = reportsToShow;
			} else if ( reportsToShow == SupportedFunctionType.CONSOLIDATE_COMPANY 
					&& context.isWrite( SupportedFunctionType.CONSOLIDATE_COMPANY )) {

				this.reportsToShow = reportsToShow;
			} else {
				this.reportsToShow = SupportedFunctionType.CONSOLIDATE_OWN;
			}
		}
	}

	public SupportedFunctionType getReportsToShow() {
		return this.reportsToShow;
	}
	
	private void initFilter( SupportedFunctionType reportsToShow ) {

		Calendar cal = DateUtil.getFirstDayOfWeek( new Date(), -2 );
		this.startDate = cal.getTime();
		cal.add( Calendar.DATE, 13 );
		this.endDate 	= cal.getTime();
		
		reportType = ReportType.PROJECT_VIEW;
		
		// Project reporting flags
		setProjectPersonnelFlag( true );
		setProjectDateFlag( false );
		// Personnel Reporting flags
		setPersonnelProjectsFlag( true );
		setPersonnelDateFlag( false );
		
		// For Users
		setReportsToShow( reportsToShow	);
		
	}
	
	public ProjectsReport createProjectsReport() {
		
		ProjectsReport pr = new ProjectsReport( 
				getReportsToShow(), 
				reportedOrganisation, 
				getSessionOwner(),
				startDate, 
				endDate );
		
		pr.prepareReport();
	
		return pr;
	}

	public UsersReport createUsersReport() {
		
		UsersReport pr = new UsersReport( 
				getReportsToShow(), 
				reportedOrganisation, 
				getSessionOwner(),
				startDate, 
				endDate );
		
		pr.prepareReport();
	
		return pr;
	}

}
