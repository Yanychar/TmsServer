package com.c2point.tms.web.ui.reportsmgmt.timereports.model;

import java.util.HashMap;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.CheckInOutFacade;
import com.c2point.tms.datalayer.TaskReportFacade;
import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.tools.ReportStorageIf;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.approveview.ModifyTaskIf;
import com.c2point.tms.web.ui.listeners.TaskListChangedListener;
import com.c2point.tms.web.ui.listeners.WeekItemAddedListener;
import com.c2point.tms.web.ui.listeners.WeekItemChangedListener;

@SuppressWarnings("serial")
public class TimeReportsModel extends HashMap< WeekProjectTaskId, WeekItem > 
								implements ModifyTaskIf, ReportStorageIf {
	private static Logger logger = LogManager.getLogger( TimeReportsModel.class.getName());

	private ReportsManagementModel	model; 
	
	public TimeReportsModel( ReportsManagementModel model ) {
		super();
		
		this.model = model;
	}
	
	
	public void removeAllItems() {
		clear();
	}
	
	public WeekItem getWeekItem( WeekProjectTaskId id ) {
		return get( id );
	}
	
	public WeekItem getWeekItem( ProjectTask pt ) {
		return getWeekItem( new WeekProjectTaskId( pt ));
	}
	
	public WeekItem getWeekItem( TaskReport report ) {
		return getWeekItem( new WeekProjectTaskId( report ));
	}
	
	public WeekItem getOrAddWeekItem( ProjectTask projectTask ) {

		WeekItem item = getWeekItem( projectTask );
		
		if ( item == null ) {
			item = new WeekItem( projectTask, model.getDateModel());
			this.put( new WeekProjectTaskId( item ), item );
			fireItemAdded( item );
			
		}

		return item;
	}

	
	/*
	public WeekItem addWeekItem( WeekItem item ) {
		// Add new item or return null if exists already
		WeekProjectTaskId id = new WeekProjectTaskId( item );
		if ( id != null && !this.containsKey( id )) {
			return this.put( id, item );
		}
		
		return null;		
	}
*/	
	@Override
	public boolean addReport( AbstractReport report ) {
		boolean res = false;

		WeekItem item = getOrAddWeekItem((( TaskReport )report ).getProjectTask());
		
		res = item.putReport(( TaskReport )report );
		
		return res;
	}

	public void initData() {

		TaskReportFacade.getInstance().getUserTaskReports( 
											model.getSessionOwner(),
											model.getDateModel().getStartOfWeek().getTime(),
											model.getDateModel().getEndOfWeek().getTime(),
											null, this
										);
/*
		if ( listTrs != null && listTrs.size() > 0 ) {
			for ( TaskReport tr : listTrs ) {
				if ( tr != null ) {
					this.addReport( tr );
					if ( logger.isDebugEnabled()) logger.debug( "Task Report is added. ReportUnicId = " + tr.getUniqueReportId());
				}
			}
		}
*/		
		fireTaskListChanged();
		
	}
	
	public void reInitData() {
		
		this.removeAllItems();
		initData();
	}

	private EventListenerList	listenerList = new EventListenerList(); 
	public void addListener( WeekItemAddedListener listener ) { listenerList.add( WeekItemAddedListener.class, listener );}
	public void addListener( WeekItemChangedListener listener ) { listenerList.add( WeekItemChangedListener.class, listener );}
	public void addListener( TaskListChangedListener listener ) { listenerList.add( TaskListChangedListener.class, listener );}
	
	protected void fireItemAdded( WeekItem item ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == WeekItemAddedListener.class) {
	    		(( WeekItemAddedListener )listeners[ i + 1 ] ).wasAdded( item );
	         }
	     }
	}
	protected void fireItemChanged( WeekItem item ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == WeekItemChangedListener.class) {
	    		(( WeekItemChangedListener )listeners[ i + 1 ] ).wasChanged( item );
	         }
	     }
	}
	protected void fireTaskListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == TaskListChangedListener.class) {
	    		(( TaskListChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }

	
	
	@Override
	public TaskReport updateTaskReport( TaskReport report ) {
//		TaskReport report = null;
		
		// Update DB
		if ( report != null ) {
	
			// Report shall be deleted or not saved. We need to handle this situation
			boolean bDeleted = ( report.getHours() == 0 );
			
			// Save TaskReport
			report.setApprovalFlagType( ApprovalFlagType.TO_CHECK );
			report = TaskReportFacade.getInstance().saveTaskReport( report );
			if ( report != null ) {
				// If added or modified
				WeekItem item = getWeekItem( report );
				if ( item != null ) {
					if ( !bDeleted ) {
						item.putReport( report );
					} else {
						// if deleted
						item.removeReport( report );
					}
					fireItemChanged( item );
				} else {
					logger.error( "Did not find WeekItem for changed Report" );
				}
			} else {
				logger.error( "Failed to update TaskReport in DB!" );
			}

		}
		
		return report;
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
