package com.c2point.tms.web.ui.reportsmgmt.travelreports.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.CheckInOutFacade;
import com.c2point.tms.datalayer.TravelReportFacade;
import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.tools.ReportStorageIf;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.approveview.ModifyTravelIf;
import com.c2point.tms.web.ui.listeners.ReportAddedListener;
import com.c2point.tms.web.ui.listeners.ReportChangedListener;
import com.c2point.tms.web.ui.listeners.ReportDeletedListener;
import com.c2point.tms.web.ui.listeners.TravelListChangedListener;

@SuppressWarnings("serial")
public class TravelReportsModel extends HashMap< String, TravelReport > 
								implements ModifyTravelIf, ReportStorageIf {
	private static Logger logger = LogManager.getLogger( TravelReportsModel.class.getName());

	private ReportsManagementModel	model; 
	
	public TravelReportsModel( ReportsManagementModel model ) {
		super();
		
		this.model = model;
	}
	
	
	public void removeAllItems() {
		clear();
	}
	
	public TravelReport getReport( String uniqueCode ) {
		return get( uniqueCode );
	}
	
	public TravelReport getReport( TravelReport report ) {
		return get( report.getUniqueReportId());
	}

	
	public TravelReport addReport( Project project ) {
		
		TravelReport report = new TravelReport();
		
		Date date = model.getDateModel().getDate().getTime();

		report.initReport( date, model.getSessionOwner(), project, TravelType.UNKNOWN, null, null, 0, "" ); 

		if ( logger.isDebugEnabled()) logger.debug( "New TravelReport has been created: " + report );
			
		put( report );

		fireReportAdded( report );
			
		return report;
	}

	@Override
	public boolean addReport( AbstractReport report ) {
		boolean res = false;

		put(( TravelReport )report );
		
		return res;
	}

	
	

	public void initData() {

		TravelReportFacade.getInstance().getUserTravelReports( 
												model.getSessionOwner(),

												model.getDateModel().getDate().getTime(),
												model.getDateModel().getDate().getTime(),
//												model.getDateModel().getStartOfWeek().getTime(),
//												model.getDateModel().getEndOfWeek().getTime(),

												null, this
											);
		
/*
		if ( listTrs != null && listTrs.size() > 0 ) {
			for ( TravelReport report : listTrs ) {
				if ( report != null ) {
					put( report );
					if ( logger.isDebugEnabled()) logger.debug( "Travel Report is added. ReportUnicId = " + report.getUniqueReportId());
				}
			}
		}
*/		
		fireTravelListChanged();
		
	}
	
	public void reInitData() {
		
		this.removeAllItems();
		initData();
	}

	private EventListenerList	listenerList = new EventListenerList(); 
	public void addListener( ReportAddedListener listener ) { listenerList.add( ReportAddedListener.class, listener );}
	public void addListener( ReportDeletedListener listener ) { listenerList.add( ReportDeletedListener.class, listener );}
	public void addListener( ReportChangedListener listener ) { listenerList.add( ReportChangedListener.class, listener );}
	public void addListener( TravelListChangedListener listener ) { listenerList.add( TravelListChangedListener.class, listener );}
	
	protected void fireReportAdded( TravelReport report ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ReportAddedListener.class) {
	    		(( ReportAddedListener )listeners[ i + 1 ] ).wasAdded( report );
	         }
	     }
	}
	protected void fireReportDeleted( TravelReport report ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ReportDeletedListener.class) {
	    		(( ReportDeletedListener )listeners[ i + 1 ] ).wasDeleted( report );
	         }
	     }
	}
	protected void fireReportChanged( TravelReport report ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ReportChangedListener.class) {
	    		(( ReportChangedListener )listeners[ i + 1 ] ).wasChanged( report );
	         }
	     }
	}
	protected void fireTravelListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == TravelListChangedListener.class) {
	    		(( TravelListChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }

	
	
	@Override
	public TravelReport updateTravelReport( TravelReport report ) {
//		TravelReport report = null;
		
		// Update DB
		if ( report != null ) {
	
			// Report shall be deleted or not saved. We need to handle this situation
			boolean bDeleted = ( report.getDistance() == 0 );
			
			// Save TravelReport
			report.setApprovalFlagType( ApprovalFlagType.TO_CHECK );
			report = TravelReportFacade.getInstance().saveTravelReport( report );
			if ( report != null ) {
				if ( !bDeleted ) {
					this.put( report );
					fireReportChanged( report );
				} else {
					// if deleted
					this.remove( report );
					fireReportDeleted( report );
				}
			} else {
				logger.error( "Failed to update TravelReport: " + report + " in DB!" );
			}

		}
		
		return report;
	}

	private void put( TravelReport report ) {
		this.put( report.getUniqueReportId(), report );
	}

	private void remove( TravelReport report ) {
		this.remove( report.getUniqueReportId());
	}
	
	@Override
	public List<CheckInOutRecord> getCheckInOutList( AbstractReport report ) {
    	List<CheckInOutRecord> resList = null;
		
    	resList = CheckInOutFacade.getInstance().getList(
    							report.getUser(), 
    							report.getDate(), 
    							report.getDate() 
    	);
    	
    	return resList;
	}

	public TmsApplication getApp() {
		return model.getApp();
	}
	
}
