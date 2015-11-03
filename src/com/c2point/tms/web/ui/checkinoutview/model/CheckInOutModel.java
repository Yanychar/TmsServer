package com.c2point.tms.web.ui.checkinoutview.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.CheckInOutFacade;
import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.GeoCoordinates;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.access.SecurityContext;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.location.GeoDistanceValidator;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.PeriodSelectionComponent;
import com.c2point.tms.web.ui.checkinoutview.AdditionalOptionsComponent;
import com.c2point.tms.web.ui.listeners.CheckInOutListChangedListener;
import com.c2point.tms.web.ui.listeners.CheckInOutSelectionListener;
import com.c2point.tms.web.ui.listeners.FilterChangedListener;
import com.c2point.tms.web.ui.listeners.UserListChangedListener;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

@SuppressWarnings("serial")
public class CheckInOutModel extends AbstractModel implements PeriodSelectionComponent.DateModelIf, Property.ValueChangeListener {

	private static Logger logger = LogManager.getLogger( AdditionalOptionsComponent.class.getName());

	private List<TmsUser>		listUsers;
	
	private TmsUser 			selectedUser;
	
	private List<CheckInOutRecord>	listOfCheckInOuts;
	
	/* Filtering */
	private Date 			startDate;
	private Date 			endDate;

	private CheckInOutFacade ciof;

 	private SupportedFunctionType presenceToShow;

	private GeoDistanceValidator  checkInValidator;
	private GeoDistanceValidator  checkOutValidator;
 	
	public CheckInOutModel( TmsApplication app ) {
		super( app );
		presenceToShow = SupportedFunctionType.PRESENCE_OWN; 

		ciof = CheckInOutFacade.getInstance();

		this.checkInValidator = new GeoDistanceValidator();
		this.checkOutValidator = new GeoDistanceValidator();
		
		initFilter();
	}

	public void initModel() {

		TmsUser user = getSessionOwner();
		if ( user == null ) {
			logger.error( "User defined in the Session was not found!!!" );
			return;
		}
		
		if ( user.getOrganisation() == null ) {
			logger.error( "No Organisation has been defined for the User: '" + user.getFirstAndLastNames() + "'!!!" );
			return;
		}
		
		
		
		initUserList(); 

//		this.activeView = ViewReportsByType.EMPLOYEE_VIEW; 
		
//		this.listOfCheckInOuts = null;
		
//		updateCheckInOutList();
	}
	
	private void initFilter() {
		this.startDate 	= DateUtil.getFirstDayOfWeek( new Date(), -2 ).getTime(); // Monday of the week before last week (current - 2 weeks)
		this.endDate 	= new Date( DateUtil.getDate().getTime() + 1000*60*60*2 ); // Current + 2 hours
//		fireFilterChanged();		

	}

	private void initUserList() {
		
		if ( listUsers == null )
			listUsers = new ArrayList<TmsUser>();
		
		listUsers.clear();
		
		Organisation org = DataFacade.getInstance().find( Organisation.class, getSessionOwner().getOrganisation().getId());
		
		// Firstly validate Access Rights
		if ( logger.isDebugEnabled()) { 
			logger.debug( "**** UserList Filtering *** " );
			logger.debug( "Current user rights: " + this.getSecurityContext());
			
		}

		TmsUser sessionOwner = getSessionOwner();
		
		for ( TmsUser user : org.getUsers().values()) {
			if ( userListFiltering( user, sessionOwner )) {
				listUsers.add( user );
			}
		}
		if ( logger.isDebugEnabled()) logger.debug( "**** ... end UserList Filtering *** " );

		
		fireUserListChanged();
		
		if ( this.selectedUser != null ) {
			
		}
	}
	
	private boolean userListFiltering( TmsUser user, TmsUser sessionOwner ) {
		boolean bRes = false;
		
		if ( user != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "   Check for filtering user: " + user.getFirstAndLastNames() );
			
			// Allows to see own CheckInOut reports
			if ( presenceToShow == SupportedFunctionType.PRESENCE_OWN && user.getId() == sessionOwner.getId()) {
				if ( logger.isDebugEnabled()) logger.debug( "    This is own report" );
				bRes = true;

			} 
			
			// if allowed to see team reports...
			if ( !bRes && presenceToShow == SupportedFunctionType.PRESENCE_TEAM ) {
				if ( logger.isDebugEnabled()) logger.debug( "" );
				
				// If sessionOwner is Line manager of this user. He can see his presence data
				if ( sessionOwner.manages( user )) {
					if ( logger.isDebugEnabled()) logger.debug( "    User is subordinate" );
					bRes = true;
			
				} else {    // if sessionOwner is a Project Manager than he can see all data concerning his projects only
					bRes = ( ciof.getCount( user, sessionOwner, startDate, endDate) > 0 );
					if ( logger.isDebugEnabled()) logger.debug( "    User is subordinate" );
				}
			} 
			
			// If sessionOwner is Company wide viewer he can see all presence info
			if ( !bRes && presenceToShow == SupportedFunctionType.PRESENCE_COMPANY ) {
				if ( logger.isDebugEnabled()) logger.debug( "    All company workers allowed to show" );
				bRes = ( ciof.getCount( user, startDate, endDate) > 0 );
//				bRes = true;
			}
			
			if ( logger.isDebugEnabled()) logger.debug( "   ... filtered " + ( bRes ? "IN" : "OUT" ));
			
		}
		
		return bRes;
	}
	
	private boolean presenceListFiltering( CheckInOutRecord presence, TmsUser sessionOwner, TmsUser user ) {
		boolean bRes = false;

		if ( presence != null ) {

			// Restriction must be set for TEAM visibility only.
			// User can see his own presence always. COMPANY visibility allows to see all data always too
			if ( presenceToShow == SupportedFunctionType.PRESENCE_TEAM ) {
				// If sessionOwner is Line manager than can see all subordinates
				if ( sessionOwner.manages( user )) {
					bRes = true;
			
				} else if ( presence.getProject().getProjectManager() != null 
						&& presence.getProject().getProjectManager().getId() == sessionOwner.getId() ) { 				
					bRes = true;
				}
			} else if ( presenceToShow == SupportedFunctionType.PRESENCE_COMPANY 
					 	|| 
					 	presenceToShow == SupportedFunctionType.PRESENCE_OWN ) {
				
				bRes = true;
				
			}
		}
		
		return bRes;
	}
	public List<TmsUser> getUsersList() {

		if ( listUsers == null )
			listUsers = new ArrayList<TmsUser>();

		return listUsers;
	}
	
	public List<CheckInOutRecord> getCheckInOutList() {

		if ( listOfCheckInOuts == null )
			listOfCheckInOuts = new ArrayList<CheckInOutRecord>();

		return listOfCheckInOuts;
	}

	private void updateCheckInOutList() {
		try {

			if ( logger.isDebugEnabled()) 
				logger.debug( "Start to reload CheckInOut List..." );
			
			if ( selectedUser != null ) {

				listOfCheckInOuts = new ArrayList<CheckInOutRecord>();
				TmsUser sessionOwner = getSessionOwner();

				List<CheckInOutRecord> checkInOutList = CheckInOutFacade.getInstance().getList( selectedUser, startDate, endDate );
					
				for ( CheckInOutRecord presence : checkInOutList ) {
					
					if ( presenceListFiltering( presence, sessionOwner, selectedUser )) {
						listOfCheckInOuts.add( presence );
					}
				}
				
				
			} else {
				listOfCheckInOuts = null;
			}
			
			if ( logger.isDebugEnabled()) { 
				if ( listOfCheckInOuts != null ) {
						logger.debug( " ... CheckInOuts List was loaded. Initial list size: " + listOfCheckInOuts.size());
					
				} else {
					if ( logger.isDebugEnabled()) 
						logger.debug( " ... CheckInOuts List is empty for "+ getSessionOwner());
				}
			}
			
			fireCheckInOutListChanged();
		} catch ( Exception e ) {
			listOfCheckInOuts = null;
			
			logger.error( "Cannot get CheckInOuts.\n" + e );
		}
		
	}

	public Date getStartDate() { return startDate; 	}
	public void setStartDate( Date startDate ) { 
		this.startDate = startDate;
		initUserList();
//		updateCheckInOutList();
	}

	public Date getEndDate() { return endDate; }
	public void setEndDate( Date endDate ) { 
		this.endDate = endDate; 
		initUserList();
//		updateCheckInOutList();
	}
/*
	public ViewReportsByType getActiveView() {
		return activeView;
	}

	public void setActiveView( ViewReportsByType activeView ) {
		this.activeView = activeView;
	}
*/

	public void addChangedListener( UserListChangedListener listener ) {
		listenerList.add( UserListChangedListener.class, listener);
	}
	public void addChangedListener( CheckInOutListChangedListener listener ) {
		listenerList.add( CheckInOutListChangedListener.class, listener);
	}
		
	public void addChangedListener( FilterChangedListener listener ) {
		listenerList.add( FilterChangedListener.class, listener);
	}

	public void addChangedListener( CheckInOutSelectionListener listener ) {
		listenerList.add( CheckInOutSelectionListener.class, listener);
	}

	private void fireCheckInOutListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CheckInOutListChangedListener.class) {
	    		(( CheckInOutListChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }

	private void fireUserListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == UserListChangedListener.class) {
	    		(( UserListChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }
	
	private void fireCheckInOutRecordSelected( CheckInOutRecord record ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CheckInOutSelectionListener.class) {
	    		(( CheckInOutSelectionListener )listeners[ i + 1 ] ).selected( record );
	         }
	     }
	 }

	
	public void selectRecord( CheckInOutRecord record ) {

		if ( record != null ) {
			this.checkInValidator.validate( record.getProject().getGeo(), record.getCheckInGeo());
			this.checkOutValidator.validate( record.getProject().getGeo(), record.getCheckOutGeo());
		}
		
		fireCheckInOutRecordSelected( record );
	}

	public GeoDistanceValidator.ValidationResult getCheckInGeoStatus() { 
		return this.checkInValidator.getLastValidationResult(); 
	}
	public GeoDistanceValidator.ValidationResult getCheckOutGeoStatus() { 
		return this.checkOutValidator.getLastValidationResult(); 
	}
	
	
/*
	private void fireFilterChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == FilterChangedListener.class) {
	    		(( FilterChangedListener )listeners[ i + 1 ] ).filterChanged();
	         }
	     }
	 }
*/
	
	@Override
	public void valueChange(ValueChangeEvent event) {
		if ( logger.isDebugEnabled()) logger.debug( "Model received ValueChanged event (expected as selection in the UserList only)" );
		Object obj = event.getProperty().getValue();
		
		if ( obj != null && obj instanceof TmsUser ) {
			if ( logger.isDebugEnabled()) logger.debug( "User selected: " + ( TmsUser )obj);
			
			selectUser(( TmsUser )obj );
			
		} else {
			selectUser( null );
			if ( logger.isDebugEnabled()) {
				if ( obj == null ) {
					if ( logger.isDebugEnabled()) logger.debug( "Event holds NULL object (TmsUser???)" );
				} else {
					if ( logger.isDebugEnabled()) logger.debug( "Event holds object of wrong type: " + obj.getClass().getName());
				}
			}
		}
		

//		initCheckInOutLists();

//		fireCheckInOutListChanged();
		
	}

	public void selectUser( TmsUser item ) {
		if ( selectedUser == null && item != null 
				|| 
			 selectedUser != null && item == null
				||
			 selectedUser != null && item != null && item.getId() != selectedUser.getId() 
	 	 ) {
			
			selectedUser = item;
			updateCheckInOutList();
		} else {
			
		}

	}

	public void setPresenceToShow( SupportedFunctionType presenceToShow ) {

		if ( this.presenceToShow != presenceToShow ) { 

			SecurityContext context = getSessionOwner().getContext();
			
			if ( presenceToShow == SupportedFunctionType.PRESENCE_TEAM 
				&& (
					context.isRead( SupportedFunctionType.PRESENCE_TEAM )
					|| 
					context.isRead( SupportedFunctionType.PRESENCE_COMPANY ))) {
				
				this.presenceToShow = presenceToShow;
			} else if ( presenceToShow == SupportedFunctionType.PRESENCE_COMPANY 
					&& context.isRead( SupportedFunctionType.PRESENCE_COMPANY )) {

				this.presenceToShow = presenceToShow;
			} else {
				presenceToShow = SupportedFunctionType.PRESENCE_OWN;
			}
		}
			
	}

	public SupportedFunctionType getPresenceToShow() {
		return this.presenceToShow;
	}
	
	public String getTotalHours( CheckInOutRecord record ) {
		
		long totalMins = -1;
		
		if ( record != null ) {
			long startTime, endTime;
			try {
				startTime = record.getDateCheckedIn().getTime();
			} catch ( Exception e ) {
				if ( logger.isDebugEnabled()) logger.debug( "Missing Checked-In Date or wrong formatted" );
				startTime = -1;
			}
			try {
				if ( record.isCheckOutByClient()) {
					endTime = record.getDateCheckedOut().getTime();
				} else {
					endTime = -1;
				}
			} catch ( Exception e ) {
				if ( logger.isDebugEnabled()) logger.debug( "Missing Checked-Out Date or wrong formatted. Will be UNKNOWN" );
				endTime = -1;
			}
			
			if ( startTime > 0 && endTime > 0 ) {
	    		totalMins = ( endTime - startTime) / ( 1000 * 60 );
			}
		}
		
		if ( totalMins < 0 ) {
			
			return null;
		}

		return DateUtil.getHourMinsString( totalMins,
						getApp().getResourceStr( "approve.edit.hours.short" ), 
						getApp().getResourceStr( "approve.edit.minutes.short" )
				);
	}
	

}
