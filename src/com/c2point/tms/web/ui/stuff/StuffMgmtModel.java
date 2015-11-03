package com.c2point.tms.web.ui.stuff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.AuthenticationFacade;
import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.access.SecurityContext;
import com.c2point.tms.entity.access.SecurityGroup;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.util.StringUtils;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;
import com.c2point.tms.web.ui.listeners.UserAddedListener;
import com.c2point.tms.web.ui.listeners.UserChangedListener;
import com.c2point.tms.web.ui.listeners.UserDeletedListener;
import com.c2point.tms.web.ui.listeners.UserListChangedListener;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

@SuppressWarnings("serial")
public class StuffMgmtModel extends AbstractModel implements Property.ValueChangeListener {

	private static Logger logger = LogManager.getLogger( StuffMgmtModel.class.getName());

	private Organisation 		organisation;

	private List<TmsUser>		listUsers;
	private TmsUser				selectedUser;
	
 	// Filtering options
 	private boolean showDeleted;	// Show deleted entities

	// TODO
	// Provide customization by Setting for code creation
	// Possible parameters
 	@SuppressWarnings("unused")
	private boolean CODE_DIGI_ONLY	= true;
 	private int 	CODE_LENGTH 		= 6;
 	private boolean	CODE_LEAD_ZERO 	= true;
 	
	public StuffMgmtModel( TmsApplication app, Organisation org ) {
		super( app );
		this.organisation = org;

		setToShowFilter( SupportedFunctionType.PERSONNEL_OWN );
		
	 	setShowDeleted( false );
	}

	public void initModel() {
	
		TmsUser sessionOwner = getSessionOwner();

		if ( sessionOwner == null ) {
			logger.error( "User defined in the Session was not found!!!" );
			return;
		}

		if ( organisation == null ) {
			organisation = sessionOwner.getOrganisation();
		}
		
		Organisation oldOrg = DataFacade.getInstance().find( Organisation.class, organisation.getId());
		
		if ( oldOrg == null ) {
			logger.error( "No Organisation has been defined for the User: '" + getSessionOwner().getFirstAndLastNames() + "'!!!" );
			return;
		} else {
			this.organisation = oldOrg;
		}

		logger.debug( "  StuffMgmtModel.initModel start..." );
		listUsers = new ArrayList<TmsUser>();

		for ( TmsUser user : organisation.getUsers().values()) {
			
			if ( getToShowFilter() == SupportedFunctionType.PERSONNEL_COMPANY 
				||
				getToShowFilter() ==  SupportedFunctionType.PERSONNEL_TEAM 
					&&
				user.getManager().getId() == sessionOwner.getId()
				|| 
				getToShowFilter() ==  SupportedFunctionType.PERSONNEL_OWN 
					&&
				user.getId() == sessionOwner.getId()
				) {
				
				if ( !user.isDeleted() || toShowDeleted()) {
					
					listUsers.add( user );

				}
					 
			}
		}
		
		
		logger.debug( "  ... end projectModel.initModel" );

		fireUserListChanged();
		
	}

	
	public List<TmsUser> getUserList() {
	
		if ( listUsers == null ) {
			listUsers = new ArrayList< TmsUser >(); 
		}
/*		
		ArrayList< TmsUser > list = new ArrayList< TmsUser >(); 
		if ( mapUsers != null && !mapUsers.isEmpty()) {
			
			for ( TmsUser user : mapUsers.values()) {
				if ( isFilteredIn( user )) {
					list.add( user );
				}
			}
		}
		
		return list;
*/		
		
		return listUsers;
	}

	public TmsUser getSelectedUser() {
		return this.selectedUser;
	}
	
	public void selectUser( TmsUser user ) {
		this.selectedUser = user;
		if ( logger.isDebugEnabled()) logger.debug( "User was set as selected: " + selectedUser );
		fireSelectionChanged();
	}

	
 	public void setShowDeleted( boolean toShow ) { showDeleted = toShow; }
 	public boolean toShowDeleted() { return showDeleted; }

 	public void addChangedListener( UserAddedListener listener ) {
		listenerList.add( UserAddedListener.class, listener);
	}
	public void addChangedListener( UserChangedListener listener ) {
		listenerList.add( UserChangedListener.class, listener);
	}
	public void addChangedListener( UserDeletedListener listener ) {
		listenerList.add( UserDeletedListener.class, listener);
	}
	public void addChangedListener( SelectionChangedListener listener ) {
		listenerList.add( SelectionChangedListener.class, listener);
	}
	public void addChangedListener( UserListChangedListener listener ) {
		listenerList.add( UserListChangedListener.class, listener);
	}

	public void deleteListener( UserAddedListener listener ) { listenerList.remove( UserAddedListener.class, listener ); }
	public void deleteListener( UserChangedListener listener ) { listenerList.remove( UserChangedListener.class, listener ); }
	public void deleteListener( UserDeletedListener listener ) { listenerList.remove( UserDeletedListener.class, listener ); }
	public void deleteListener( SelectionChangedListener listener ) { listenerList.remove( SelectionChangedListener.class, listener ); }
	public void deleteListener( UserListChangedListener listener ) { listenerList.remove( UserListChangedListener.class, listener ); }

	@SuppressWarnings("unused")
	private void fireUserAdded( TmsUser user ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == UserAddedListener.class) {
	    		(( UserAddedListener )listeners[ i + 1 ] ).wasAdded( user );
	         }
	     }
		
	}

	private void fireUserChanged( TmsUser user ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == UserChangedListener.class) {
	    		(( UserChangedListener )listeners[ i + 1 ] ).wasChanged( user );
	         }
	     }
		
	}

	private void fireUserDeleted( TmsUser user ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == UserDeletedListener.class) {
	    		(( UserDeletedListener )listeners[ i + 1 ] ).wasDeleted( user );
	         }
	     }
		
	}

	protected void fireUserListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == UserListChangedListener.class) {
	    		(( UserListChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }
	
	private void fireSelectionChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == SelectionChangedListener.class) {
	    		(( SelectionChangedListener )listeners[ i + 1 ] ).selectionChanged();
	         }
	     }
	}


	@Override
	public void valueChange( ValueChangeEvent event ) {
		this.selectedUser = ( TmsUser )event.getProperty().getValue();
		if ( logger.isDebugEnabled()) logger.debug( "TmsUser was set as selected: " + this.selectedUser );
		fireSelectionChanged();
	}	

	@SuppressWarnings("unused")
	private boolean isFilteredIn( TmsUser user ) {
		boolean bRes = true;
		
		return bRes;
	}

	public boolean codeExists( TmsUser user ) {
		return codeExists( user.getCode());
	}
	public boolean codeExists( String code ) {
		
		if ( code != null && code .length() > 0 && listUsers != null && listUsers.size() > 0 ) {
			for ( TmsUser  user : listUsers ) {
				if ( user != null && user.getCode() != null && user.getCode().compareToIgnoreCase( code ) == 0 ) {
					return true;
				}
			}
		}
		
		return false;
	}

	
	public TmsUser addUser( TmsUser user ) {
		TmsUser result = null;

		// Cannot add the User with code existed already
		if ( !codeExists( user.getCode())) {
			// Set up active user as Project Manager if it is missing (last chance)
			if ( user.getManager() == null ) {
				getSessionOwner().addSubordinate( user );
			}
			
			try {
				user = OrganisationFacade.getInstance().addUser( this.organisation, user );
				if ( user != null ) {
					// Add TmsAccount if necessary
					if ( AuthenticationFacade.getInstance().addAccountDefault( user ) == null ) {
						logger.error( "Account for the new user '"+ user.getFirstAndLastNames() + "' was not added!" );
					}
				}
				
				listUsers.add( user ); 
				fireUserListChanged();
				selectUser( user );				
				
				//				fireUserAdded( newUser );
				
				result = user;
								
			} catch ( Exception e ) {
				logger.error( "Failed to add user: " + user );
				logger.error( e );
			}
				
		} else {
			logger.error( "Cannot add the User because the User with the same Code already exists already!" );
		}

		return result;
	}
	
	public TmsUser updateUser( TmsUser user ) {
		TmsUser result = null;

		// Update DB
		if ( user != null ) {

			try {
				Organisation org = ( user.getOrganisation() != null ? user.getOrganisation() : this.organisation );

				user = OrganisationFacade.getInstance().updateUser( org, user );
				
				fireUserChanged( user );
				
				result = user;
								
			} catch ( Exception e ) {
				logger.error( "Failed to update user: " + user );
				logger.error( e );
			}
			
			
		}
		
		return result;
	}

	public boolean removeUser( TmsUser user ) {
		boolean bRes = false;

		// Delete here means mark as deleted but leave in DB
		// Deleted user shall not be visible in UserLists
		if ( user != null ) {
			// Mark as deleted
			user.setDeleted();
			
			

			try {
				//Store in DB

				user = OrganisationFacade.getInstance().removeUser( user );
				
				if ( user != null ) {
					// Delete from model
					if ( listUsers.remove( user )) {				
						// Fire 'deleted' event
						fireUserDeleted( user );
					} else {
						logger.error( "User mas not found in the model to be deleted!" );
					}
					
					bRes = true;
				} else {
					logger.error( "DB failed to update deletion status!" );
				}
								
			} catch ( Exception e ) {
				logger.error( "Failed to update user: " + user );
				logger.error( e );
			}
			
			
		} else {
			logger.error( "User cannot be null dor deletion!" );
		}
		
		
		// 
		
		return bRes;
	}

	public Collection<SecurityGroup> getAvailableSecurityGroups() {
		Collection<SecurityGroup> sgCol = null;

		Organisation org = ( this.organisation != null ? this.organisation : getSessionOwner().getOrganisation());

		if ( org != null ) {
			
//			sgCol = AccessRightsFacade.getInstance().getSecGroupList( org );
			sgCol = org.getSecurityGroups().values();
			
		}
		
		return sgCol;
	}


	public void setStuffToShow( SupportedFunctionType fromMenu ) {

		if ( getToShowFilter() != fromMenu ) { 

			SecurityContext context = getSessionOwner().getContext();
			
			if ( fromMenu == SupportedFunctionType.PERSONNEL_TEAM 
				&& 
				 context.isAccessible( SupportedFunctionType.PERSONNEL_TEAM )
			||   fromMenu == SupportedFunctionType.PERSONNEL_COMPANY 
				&& 
				 context.isAccessible( SupportedFunctionType.PERSONNEL_COMPANY )) {
				
				setToShowFilter( fromMenu );
			} else {
				setToShowFilter( SupportedFunctionType.PERSONNEL_OWN );
			}
		}
			
	}

	public String generateUsrname( String usrname ) {
		
		return AuthenticationFacade.getInstance().getFreeUserName( usrname );
	}

	public boolean isUniqueUsrname( String usrname, TmsUser user ) {
		
		TmsAccount  account = AuthenticationFacade.getInstance().findByUserName( usrname );
		
		if ( account != null && account.getUser() != null && account.getUser().getId() != user.getId()) {
			return false;
		}
		
		return true;
	}

	// Default implementation of user code
	// TODO
	// Provide customization by Setting
	
	public String generateUserCode() {
		
		int tmpCount = this.organisation.getUsers().size() + 1;
		String generatedCode = createStrCode( tmpCount++ );
		
		while ( codeExists( generatedCode )) {
			generatedCode = createStrCode( tmpCount++ );
		}
		
		return generatedCode;
		
	}

	public boolean isUniqueUserCode( String code, TmsUser user ) { 
		
		boolean unique = !codeExists( code );
		
		if ( !unique ) {
			TmsUser existingUser = organisation.getUser( code );
			if ( existingUser != null && user != null && existingUser.getId() == user.getId()) {
				unique = true;
			}
		}
		return unique;

	}

	private String createStrCode( int i ) {

		// Parameters
	 	// boolean CODE_DIGI_ONLY	= true;
	 	// int 	CODE_LENGTH 		= 6;
	 	// boolean	CODE_LEAD_ZERO 	= true;
		
		String code;
		
		if ( CODE_LEAD_ZERO ) {
			code = StringUtils.padLeftZero( i, CODE_LENGTH );
		} else {
			code = Integer.toString( i );
		}
		
		return code;
		
	}
	

}
