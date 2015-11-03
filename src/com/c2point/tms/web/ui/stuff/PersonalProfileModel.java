package com.c2point.tms.web.ui.stuff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.listeners.UserChangedListener;

public class PersonalProfileModel extends AbstractModel {

	private static Logger logger = LogManager.getLogger( PersonalProfileModel.class.getName());

	
	public PersonalProfileModel( TmsApplication app ) {
		super( app );

		setToShowFilter( SupportedFunctionType.PERSONNEL_OWN );
		
	}

	public void initModel() {
	
	}

	public TmsUser getUser() {
		return getSessionOwner();
	}
	
	public void addChangedListener( UserChangedListener listener ) {
		listenerList.add( UserChangedListener.class, listener);
	}
	public void deleteListener( UserChangedListener listener ) { listenerList.remove( UserChangedListener.class, listener ); }

	private void fireUserChanged( TmsUser user ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == UserChangedListener.class) {
	    		(( UserChangedListener )listeners[ i + 1 ] ).wasChanged( user );
	         }
	     }
		
	}

	public TmsUser updateUser( TmsUser user ) {
		
		TmsUser result = null;

		// Update DB
		if ( user != null ) {

			try {
				Organisation org = user.getOrganisation();

				result = OrganisationFacade.getInstance().updateUser( org, user );
				
				fireUserChanged( result );
				
			} catch ( Exception e ) {
				logger.error( "Failed to update user: " + user );
				logger.error( e );
				
				return null;
			}
			
			
		}
		
		return result;
	}

}
