package com.c2point.tms.web.ui.approveview.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.CheckInOutFacade;
import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.TaskReportFacade;
import com.c2point.tms.datalayer.TravelReportFacade;
import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.tools.ReportStorageIf;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.PeriodSelectionComponent;
import com.c2point.tms.web.ui.approveview.ModifyTaskIf;
import com.c2point.tms.web.ui.approveview.ModifyTravelIf;
import com.c2point.tms.web.ui.listeners.ProjectReportChangedListener;
import com.c2point.tms.web.ui.listeners.ReportChangedListener;
import com.c2point.tms.web.ui.listeners.ReportItemSelectedListener;
import com.c2point.tms.web.ui.listeners.ReportsListChangedListener;
import com.c2point.tms.web.ui.listeners.UserChangedListener;
import com.c2point.tms.web.ui.listeners.UserListChangedListener;

public class ApproveModel extends AbstractModel implements PeriodSelectionComponent.DateModelIf, ReportStorageIf, ModifyTaskIf, ModifyTravelIf {

	private static Logger logger = LogManager.getLogger( ApproveModel.class.getName());

	private Map<String, TmsUserHolder> 	usersHolder = new HashMap<String,TmsUserHolder>();
	private TmsUserHolder				selectedHolder;
	
	private ReportsFilter				filter;
	
	public ApproveModel( TmsApplication app ) {
		super( app );

		this.filter = new ReportsFilter();
		selectedHolder = null;
	}

	public void initModel() {

		TmsUser user = getSessionOwner();

		if ( user == null ) {
			logger.error( "User defined in the Session was not found!!!" );
			return;
		}

		fullReadReports();
	}

	
	public void fullReadReports() {

		TmsUser sessionOwner = getSessionOwner();

		// Remove old records
		clear();
		
		try {

			if ( logger.isDebugEnabled()) logger.debug( "Start to reload Task Reports List..." );

			if ( getReportsToShow() == SupportedFunctionType.REPORTS_COMPANY ) {

				if ( logger.isDebugEnabled()) logger.debug( "User has rights to see ALL COMPANY reports" );

					TaskReportFacade.getInstance().getCompanyTaskReports(
						sessionOwner.getOrganisation(),
						getFilter().getStartDate(), getFilter().getEndDate(),
//						reportValidator, this );
						null, this );

					TravelReportFacade.getInstance().getCompanyTravelReports(
						sessionOwner.getOrganisation(),
						getFilter().getStartDate(), getFilter().getEndDate(),
						null, this );

			} else if ( getReportsToShow() == SupportedFunctionType.REPORTS_TEAM ) {

				if ( logger.isDebugEnabled()) logger.debug( "User has rights to see his/her TEAM reports" );

					TaskReportFacade.getInstance().getManagerTaskReports(
						sessionOwner,
						getFilter().getStartDate(), getFilter().getEndDate(),
						null, this );

					TravelReportFacade.getInstance().getManagerTravelReports(
						sessionOwner,
						getFilter().getStartDate(), getFilter().getEndDate(),
						null, this );

			} else if ( getReportsToShow() == SupportedFunctionType.REPORTS_OWN ) {

				if ( logger.isDebugEnabled()) logger.debug( "User has rights to see his/her OWN reports only" );

					TaskReportFacade.getInstance().getUserTaskReports(
						sessionOwner,
						getFilter().getStartDate(), getFilter().getEndDate(),
						null, this );

					TravelReportFacade.getInstance().getUserTravelReports(
						sessionOwner,
						getFilter().getStartDate(), getFilter().getEndDate(),
						null, this );

			} else {

			}

			updateCounters();
			
			fireUserListChanged();
//			fireReportsListChanged();

		} catch ( Exception e ) {

			logger.error( "Cannot get a list of Task or Travel Reports for "+ sessionOwner + "\n" + e );
		}

	}

	public SupportedFunctionType getReportsToShow() {
		return getFilter().getReportsToShow();
	}

	public ReportsFilter getFilter() {
		if ( filter == null )
			this.filter = new ReportsFilter();

		return this.filter;
	}

	public boolean hasUser( TmsUser user ) {

		return usersHolder.containsKey( user.getCode());
		
	}

	public TmsUserHolder getTmsUserHolder( TmsUser user ) {

		return usersHolder.get( user.getCode());
		
	}

	public boolean addReport( AbstractReport report ) {
		boolean bRes = false;
		
		TmsUserHolder holder = usersHolder.get( report.getUser().getCode());
		if ( holder == null ) {
			if ( logger.isDebugEnabled()) logger.debug ( "New UserHolder for '" + report.getUser().getFirstAndLastNames() + "'" );
			
			holder = new TmsUserHolder( report.getUser());
			usersHolder.put( report.getUser().getCode(), holder );
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug ( "UserHolder for '" + report.getUser().getFirstAndLastNames() + "' exists already!" );
		}

		bRes = holder.addReport( report );
			
		return bRes;
	}

	private void clear() {
		for ( TmsUserHolder uh : values()) {
			uh.clear();
		}
		
		usersHolder.clear();

		
	}

	public void addChangedListener( UserListChangedListener listener ) {
		listenerList.add( UserListChangedListener.class, listener);
	}

	public void addChangedListener( UserChangedListener listener ) {
		listenerList.add( UserChangedListener.class, listener);
	}

	public void addChangedListener( ReportsListChangedListener listener ) {
		listenerList.add( ReportsListChangedListener.class, listener);
	}

	public void addChangedListener( ReportChangedListener listener ) {
		listenerList.add( ReportChangedListener.class, listener);
	}
	
	public void addChangedListener( ReportItemSelectedListener listener ) {
		listenerList.add( ReportItemSelectedListener.class, listener);
	}

	public void addChangedListener( ProjectReportChangedListener listener ) {
		listenerList.add( ProjectReportChangedListener.class, listener);
	}
	
	
	protected void fireUserListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == UserListChangedListener.class) {
	    		(( UserListChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }

	protected void fireUserChanged( TmsUser user ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == UserChangedListener.class) {
	    		(( UserChangedListener )listeners[ i + 1 ] ).wasChanged( user );
	         }
	     }
	 }

	protected void fireReportsListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ReportsListChangedListener.class) {
	    		(( ReportsListChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }

	protected void fireReportChanged( AbstractReport report ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ReportChangedListener.class) {
	    		(( ReportChangedListener )listeners[ i + 1 ] ).wasChanged( report );
	         }
	     }
	 }

	protected void fireReportItemSelected( Object obj ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ReportItemSelectedListener.class) {
	    		(( ReportItemSelectedListener )listeners[ i + 1 ] ).selected( obj );
	         }
	     }
	 }

	protected void fireProjectReportChanged( ProjectHolder ph ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectReportChangedListener.class) {
	    		(( ProjectReportChangedListener )listeners[ i + 1 ] ).wasChanged( ph );
	         }
	     }
	 }


	public void clearReportLists() {

		clear();

		fireReportsListChanged();
		
	}
	
	public Collection< TmsUserHolder > getUsersList() {
		
		return sortedValues( true );
		//		return values();
	}
	
	public void selectUser( TmsUserHolder holder ) {
		
		if ( selectedHolder != holder ) {
			
			this.selectedHolder = holder; 
			
			if ( logger.isDebugEnabled()) logger.debug( "User has been selected in the User table: " + this.selectedHolder.getTmsUser().getLastAndFirstNames());
			
			fireReportsListChanged();
			
		} else {

			if ( logger.isDebugEnabled()) logger.debug( "User has been selected already. Not necessary to change selection" );
			
		}
		
	}

	public void resetReportList() {
		
		fireReportsListChanged();
		
	}

	public TmsUserHolder getSelectedUser() { return this.selectedHolder; }
	
	public void reportItemSelected( Object itemToShowInDetailsPanel ) {

		fireReportItemSelected( itemToShowInDetailsPanel );
	}
	
	
	
	public Collection<TmsUserHolder> values() { return usersHolder.values(); }
	public Collection<TmsUserHolder> sortedValues( final boolean asc ) {
		
		List<TmsUserHolder> list = new LinkedList<TmsUserHolder>( values());
		 
		// sort list based on comparator
		Collections.sort( list, new Comparator< TmsUserHolder>() {

			Collator standardComparator = Collator.getInstance(); 
			
			
			@Override
			public int compare(TmsUserHolder arg1, TmsUserHolder arg2) {

				return ( asc ? 1 : -1 ) * standardComparator.compare( arg1.getTmsUser().getLastAndFirstNames(), arg2.getTmsUser().getLastAndFirstNames());

			}
		});
		
		
		
		return list;
		
	}
	
//	@Override
    public List<CheckInOutRecord> getCheckInOutList( AbstractReport report ) {
		
    	return getCheckInOutList( report.getUser(), report.getDate());
    }

    public List<CheckInOutRecord> getCheckInOutList( TmsUser user, Date date ) {
    	List<CheckInOutRecord> resList = null;
		
    	resList = CheckInOutFacade.getInstance().getList( user, date, date ); 
    	
    	return resList;
    }

	// 
	// Returns TRUE if threre is records for specified project. Otherwise FALSE
	// In 'records' project records or all records will be returned
	// !!! 'records' shall be allocated in caller
	
	public enum ReturnedResult { PROJECT_EXISTS, ALL_RECORDS };
	public class CheckInOutResults { //implements Collection<CheckInOutRecord>{
		
		ReturnedResult			result;
		List<CheckInOutRecord>  records;
		long					registeredInMinutes;
		
		CheckInOutResults() {
			result 				= ReturnedResult.ALL_RECORDS;
			records 			= new ArrayList<CheckInOutRecord>();
			registeredInMinutes = 0;
		}
		
		public ReturnedResult getResult() { return result; }
		public long getMinutes() { return registeredInMinutes; }
		public int getRecordCount() { return records.size(); } 
		public Collection<CheckInOutRecord> getRecords() { return records; } 
		
	}
    
    
    public CheckInOutResults getCheckInOutList( Project project,  TmsUser user, Date date ) {
		
    	CheckInOutResults result = new CheckInOutResults();
		
    	List<CheckInOutRecord> resList = getCheckInOutList( user, date );
    	
    	if ( resList != null && resList.size() > 0 ) {
    		
    		long totalMins = 0;
    		long projectMins = 0;
    		long tmpMins;
    		
    		for ( CheckInOutRecord record : resList ) {
    			
    			if ( record != null ) {
    				long startTime, endTime;
    				try {
    					startTime = record.getDateCheckedIn().getTime();
    				} catch ( Exception e ) {
    					if ( logger.isDebugEnabled()) logger.debug( "Missing Checked-In Date or wrong formatted" );
    					startTime = -1;
    				}
    				try {
    					endTime = record.getDateCheckedOut().getTime();
    				} catch ( Exception e ) {
    					if ( logger.isDebugEnabled()) logger.debug( "Missing Checked-Ou Date or wrong formatted. Will be == Check-In" );
    					endTime = startTime;
    				}
    				
	    			if ( startTime > 0 ) {
	    				tmpMins = ( endTime - startTime) / ( 1000 * 60 ); 
		    			if ( record.getProject().getId() == project.getId()) {
		    				result.result = ReturnedResult.PROJECT_EXISTS;
		    				result.records.add( record );
		    	    		projectMins = projectMins + tmpMins;
		    			}
			    		totalMins = totalMins + tmpMins;
	    			}
    			}
    		}

        	if ( result.result == ReturnedResult.PROJECT_EXISTS ) {
    			if ( logger.isDebugEnabled()) logger.debug( "There are registered time records in selected project!" );
    			result.registeredInMinutes = projectMins; 
        	} else {
    			if ( logger.isDebugEnabled()) logger.debug( "No registered time in reported project! All records will be return" );
    			result.records.addAll( resList );
    			result.registeredInMinutes = totalMins;
        	}
    	
    	}
    	
    	return result;
    }


	@Override
	public Date getStartDate() {
		return this.getFilter().getStartDate();
	}

	@Override
	public Date getEndDate() {
		return this.getFilter().getEndDate();
	}

	@Override
	public void setStartDate(Date date) {
		this.getFilter().setStartDate( date );
	}

	@Override
	public void setEndDate(Date date) {
		this.getFilter().setEndDate( date );
	}

	public AbstractReport changeReportState( AbstractReport report,  ApprovalFlagType newState ) {
		
		AbstractReport newReport = null;
	
		if ( report != null ) {

			report.setApprovalFlagType( newState );
			
			if ( report instanceof TaskReport ) {
				newReport = updateTaskReport(( TaskReport )report ); //ApprovalFlagType.APPROVED );
			} else if ( report instanceof TravelReport ) {
				newReport = updateTravelReport(( TravelReport )report ); //ApprovalFlagType.APPROVED );
			} 
			
			if ( newReport != null ) {
				if ( logger.isDebugEnabled()) logger.debug( "Report state changed successfully to " + newState );
			} else {
				logger.error( "Cannot approve/reject " + report + ".\n" );
			}
			
		} else {
			logger.error( "Report  passed == null" );
		}
			
		return newReport;
	}
	
	public TaskReport updateTaskReport( TaskReport newReport ) {
		TaskReport report = null;
		
		// Update DB
		if ( newReport != null ) {
			// Save TaskReport
			TaskReport oldReport = DataFacade.getInstance().find( TaskReport.class, newReport.getId());
			if ( oldReport != null ) {

				ApprovalFlagType oldFlag = oldReport.getApprovalFlagType();
				float oldHours = oldReport.getHours();
				
				oldReport.modifyReport( newReport );
				
				report = DataFacade.getInstance().merge( oldReport );
				if ( report != null ) {
					
					updateReportInternal( report, oldFlag, oldHours );
				
				} else {
					logger.error( "Failed to update TaskReport: " + newReport + " in DB!" );
				}
			} else {
				logger.error( "Edited TaskReport was not found: " + newReport + " in DB!" );
			}
		}
		
		return report;
	}
	
	public TravelReport updateTravelReport( TravelReport newReport ) {
		TravelReport report = null;
		
		// Update DB
		if ( newReport != null ) {
			// Save TravelReport
			TravelReport oldReport = DataFacade.getInstance().find( TravelReport.class, newReport.getId());
			if ( oldReport != null ) {
				
				ApprovalFlagType oldFlag = oldReport.getApprovalFlagType();
				
				oldReport.modifyReport( newReport );
				
				report = DataFacade.getInstance().merge( oldReport );
				if ( report != null ) {

					updateReportInternal( report, oldFlag );
				
				} else {
					logger.error( "Failed to update TravelReport: " + newReport + " in DB!" );
				}
			} else {
				logger.error( "Edited TravelReport was not found: " + newReport + " in DB!" );
			}
		}
		
		return report;
	}

	private void updateCounters() {

		for ( TmsUserHolder uh : values()) {
			uh.updateCounters();
		}
	}

	// Used to save TravelReports
	private boolean  updateReportInternal( TravelReport newReport, ApprovalFlagType oldType ) {
		return updateReportInternal( newReport, oldType, -1 );
	}
	
	// Used to save TaskReportsReports
	private boolean updateReportInternal( AbstractReport newReport, ApprovalFlagType oldType, float oldHours ) {

		boolean bRes = false;
		
		ProjectHolder ph = null;
		TmsUserHolder uh = getTmsUserHolder( newReport.getUser());
		
		if ( uh != null ) {
			
			ph = uh.getProjectHolder( newReport );
			
			if ( ph != null ) {

				bRes = ph.updateReport( newReport );
				
			} else {
				
			}
		}
		
		if ( bRes ) {

			fireReportChanged( newReport );

			boolean counterUpdated = false;
			
			if ( oldType != newReport.getApprovalFlagType()) {

				uh.updateCounters();
				counterUpdated = true;
	
				fireUserChanged( uh.getTmsUser());
				
				bRes = false;
				
			}
			
			if ( ph != null && oldHours >= 0 && (( TaskReport )newReport ).getHours() != oldHours ) {
				
				if ( !counterUpdated )
					ph.updateCounters();
				
				fireProjectReportChanged( ph );
			}
		}
					
		return bRes;
	}

	
	public List<TaskReport> getTaskReportsList() {
		
		List<TaskReport> reportsList = new ArrayList<TaskReport>();
		
		for ( TmsUserHolder uh : values()) {
			
			for ( ProjectHolder ph : uh.values()) {

				for ( TaskReport report : ph.getTaskReports()) {

					if ( report != null ) {
						
						reportsList.add( report );
						
					}
					
				}
			}
			
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "Number of TaskReports for printing is: " + reportsList.size());
		
		return reportsList;
	}

	public List<TravelReport> getTravelReportsList() {

		List<TravelReport> reportsList = new ArrayList<TravelReport>();
		
		for ( TmsUserHolder uh : values()) {
			
			for ( ProjectHolder ph : uh.values()) {

				for ( TravelReport report : ph.getTravelReports()) {

					if ( report != null ) {
						
						reportsList.add( report );
						
					}
					
				}
			}
			
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "Number of TravelReports for printing is: " + reportsList.size());
		
		return reportsList;
	}

	
}
