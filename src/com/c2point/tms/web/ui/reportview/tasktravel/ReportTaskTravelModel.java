package com.c2point.tms.web.ui.reportview.tasktravel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.TaskReportFacade;
import com.c2point.tms.datalayer.TravelReportFacade;
import com.c2point.tms.datalayer.UserFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.access.SecurityContext;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.taskandtravel.ProjectsReport;
import com.c2point.tms.web.reporting.taskandtravel.UsersReport;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.PeriodSelectionComponent;
import com.c2point.tms.web.ui.listeners.FilterChangedListener;

public class ReportTaskTravelModel extends AbstractModel implements PeriodSelectionComponent.DateModelIf {

	private static Logger logger = LogManager.getLogger( ReportTaskTravelModel.class.getName());

	public enum ReportType { PERSONNEL_VIEW, PROJECT_VIEW };
	

	private Organisation	reportedOrganisation;
	
	private Date startDate;
	private Date endDate;
	
	private ReportType reportType = ReportType.PERSONNEL_VIEW;
	
	// Personnel report flags
	private boolean date_flag;
	private boolean projects_flag;
	private boolean tasks_flag_1;
	private boolean travel_flag_1;

	// Projects report flags
	//	private boolean persons_flag;
	private Project	project;
	private boolean tasks_flag_2;
	private boolean travel_flag_2;
	
	private boolean	silentFire = true;
	
	// List of users and reports
	private List<TmsUser> 		listUsers;
	private List<TaskReport> 	listTaskReports;
	private List<TravelReport> 	listTravelReports;

	// Current AccessRights flag
 	private SupportedFunctionType reportsToShow;

 	private boolean changed = true;
	
	public ReportTaskTravelModel( TmsApplication app, SupportedFunctionType reportsToShow ) {
		this( app, null, reportsToShow );
	}

 	
 	public ReportTaskTravelModel( TmsApplication app, Organisation org, SupportedFunctionType reportsToShow ) {
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
	public boolean isDateFlag() {
		return date_flag;
	}
	public void setDateFlag( boolean date_flag ) {
		this.date_flag = date_flag;
		fireFilterChanged();
	}
	public boolean isProjectsFlag() {
		return projects_flag;
	}
	public void setProjectsFlag( boolean projects_flag ) {
		this.projects_flag = projects_flag;
		fireFilterChanged();
	}
	public boolean isTasksFlag_1() {
		return tasks_flag_1;
	}
	public void setTasksFlag_1( boolean tasks_flag_1 ) {
		this.tasks_flag_1 = tasks_flag_1;
		fireFilterChanged();
	}
	public boolean isTravelFlag_1() {
		return travel_flag_1;
	}
	public void setTravelFlag_1( boolean travel_flag_1 ) {
		this.travel_flag_1 = travel_flag_1;
		fireFilterChanged();
	}

	public void selectProject( Project project ) { 
		this.project = project; 
	}
	public Project getSelectedProject() { 
		return this.project; 
	}
	
	public boolean isTasksFlag_2() {
		return tasks_flag_2;
	}
	public void setTasksFlag_2( boolean tasks_flag_2 ) {
		this.tasks_flag_2 = tasks_flag_2;
		fireFilterChanged();
	}
	public boolean isTravelFlag_2() {
		return travel_flag_2;
	}
	public void setTravelFlag_2( boolean travel_flag_2 ) {
		this.travel_flag_2 = travel_flag_2;
		fireFilterChanged();
	}
/*	
	public boolean isPersonsFlag() {
		return persons_flag;
	}
	public void setPersonsFlag( boolean persons_flag ) {
		this.persons_flag = persons_flag;
		fireFilterChanged();
	}
*/
	
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
	
	public List<TmsUser> getUsersList() {
		return listUsers;
	}

	public List<TaskReport> getTaskReportsList() {
		return listTaskReports;
	}

	public List<TravelReport> getTravelReportsList() {
		return listTravelReports;
	}

	public void clearUserList() {
		listUsers.clear();
	}

	public void clearReportLists() {
		listTaskReports.clear();
		listTravelReports.clear();
	}

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
		
		reportType = ReportType.PERSONNEL_VIEW;
		
		date_flag = true;
		projects_flag = true;
		tasks_flag_1 = false;
		travel_flag_1 = false;

		tasks_flag_2 = true;
		travel_flag_2 = false;
		//		persons_flag = true;
		
		// For Users
		setReportsToShow( reportsToShow	);
		
	}
	
	public void setFilter(
					List<TmsUser> listUsers,
					List<TaskReport> listTaskReports, 
					List<TravelReport> listTravelReports,
					Date startDate,
					Date endDate ) {

		if ( listUsers != null ) { this.listUsers = listUsers; } 
		if ( listTaskReports != null ) { this.listTaskReports = listTaskReports; } 
		if ( listTravelReports != null ) { this.listTravelReports = listTravelReports; }
		if ( startDate != null ) { this.startDate = startDate; }
		if ( endDate != null ) { this.endDate = endDate; }
	}

	public void updateTaskAndTravelLists() {

		TmsUser sessionOwner = getSessionOwner();
		
		try {

			if ( logger.isDebugEnabled()) 
				logger.debug( "Start to reload Task Reports List..." );

			if ( getReportsToShow() == SupportedFunctionType.CONSOLIDATE_COMPANY ) {
				
				if ( logger.isDebugEnabled()) logger.debug( "User has rights to see ALL COMPANY reports" );
				
				listTaskReports = TaskReportFacade.getInstance().getTaskReportsForReporting( 
						reportedOrganisation, 
						this.startDate, this.endDate
				);

				listTravelReports = TravelReportFacade.getInstance().getTravelReportsForReporting( 
						reportedOrganisation, 
						this.startDate, this.endDate
				);

				listUsers = UserFacade.getInstance().list( reportedOrganisation );
				
			} else if ( getReportsToShow() == SupportedFunctionType.CONSOLIDATE_TEAM ) {
			
				if ( logger.isDebugEnabled()) logger.debug( "User has rights to see his/her TEAM reports" );

				listTaskReports = TaskReportFacade.getInstance().getTaskReportsForReporting( 
						sessionOwner, 
						this.startDate, this.endDate
				);

				listTravelReports = TravelReportFacade.getInstance().getTravelReportsForReporting( 
						sessionOwner, 
						this.startDate, this.endDate
				);

				listUsers = null;
				
			} else if ( getReportsToShow() == SupportedFunctionType.CONSOLIDATE_OWN ) {
				
				if ( logger.isDebugEnabled()) logger.debug( "User has rights to see his/her OWN reports only" );
/*
				listTaskReports = TaskReportFacade.getInstance().getUserTaskReports( 
						sessionOwner, 
						this.startDate, this.endDate,
						taskValidator );

				listTravelReports = TravelReportFacade.getInstance().getUserTravelReports( 
						sessionOwner, 
						this.startDate, this.endDate,
						travelValidator );
*/				
			}
			
			if ( this.listUsers != null ) {
				if ( logger.isDebugEnabled()) 
					logger.debug( " ... Users List was loaded. Initial list size: " + listUsers.size());
				
			} else {
				logger.error( " ... Users List was NOT loaded" );
			}

			
			if ( listTaskReports != null ) {
				if ( logger.isDebugEnabled()) 
					logger.debug( " ... Tasks List was loaded. Initial list size: " + listTaskReports.size());
				
			} else {
				logger.error( " ... Tasks List was NOT loaded" );
			}

			if ( listTravelReports != null ) {
				if ( logger.isDebugEnabled()) 
					logger.debug( " ... Travels List was loaded. Initial list size: " + listTravelReports.size());
				
			} else {
				logger.error( " ... Travels List was NOT loaded" );
			}
			
			changed = false;
			
		} catch ( Exception e ) {
			listTaskReports = null;
			listTravelReports = null;
			
			logger.error( "Cannot get a list of Task or Travel Reports" );
		}
		
	}

	public List<Project> getProjectsList() {

		List<Project> listProjects = new ArrayList<Project>();
		
		if ( reportedOrganisation != null && reportedOrganisation.getProjects() != null ) {
			
			for ( Project prj : reportedOrganisation.getProjects().values()) {
				
				if ( prj != null && !prj.isDeleted()) {
					listProjects.add( prj );
				}
			}
			
		} 
		
		Collections.sort( listProjects, new Comparator<Project>() {

			@Override
			public int compare( Project prj1, Project prj2 ) {
				
				int res = 0;
				
				if ( prj1 == null || StringUtils.isBlank( prj1.getName())) res = -1;
				else if ( prj2 == null || StringUtils.isBlank( prj2.getName())) res = -1;
				else {

					res = prj1.getName().compareToIgnoreCase( prj2.getName());
				}

				return res;
			}
			
		}); 
						
		return listProjects;
	}
	
	public UsersReport createUsersReport() {
		
		UsersReport ur = new UsersReport( getUsersList())
								.prepareReport( getTaskReportsList(), getTravelReportsList());
	
		return ur;
	}

	public ProjectsReport createProjectsReport() {
		
		ProjectsReport pr = new ProjectsReport()
								.prepareReport( getTaskReportsList(), getTravelReportsList(), this.getSelectedProject());
	
		return pr;
	}

}
