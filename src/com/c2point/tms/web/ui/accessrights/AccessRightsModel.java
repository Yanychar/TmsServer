package com.c2point.tms.web.ui.accessrights;

import java.util.Map;

import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.access.SecurityGroup;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.listeners.OrganisationChangedListener;
import com.c2point.tms.web.ui.listeners.SGSelectionChangedListener;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;

public class AccessRightsModel extends AbstractModel {

	private static Logger 	logger = LogManager.getLogger( AccessRightsModel.class.getName());

	private Organisation	selectedOrganisation;
	private SecurityGroup	selectedGroup;

	private boolean			updated;
	
	private boolean 		singleOrg;
	
	public AccessRightsModel( TmsApplication app ) {
		this( app, null );
	}
	
	public AccessRightsModel( TmsApplication app, Organisation org ) {
		super( app );

		if ( org != null ) {
			singleOrg =  true;
			this.selectedOrganisation = org;
		} else {
			singleOrg =  false;
			selectedOrganisation = this.getSessionOwner().getOrganisation();
		}
		
		
		initModel();
		
	}
	
	public boolean isSingleOrg() { return singleOrg; }
	
	public Map<String, SecurityGroup> getGroups() {
		return this.selectedOrganisation.getSecurityGroups();
	}

	public SecurityGroup getSelectedGroup() {
		return selectedGroup;
	}
	
	public Organisation getSelectedOrganisation() {
		return selectedOrganisation;
	}
	
	public SecurityGroup selectGroup() {
		// Select the first group from the list
		try {
			return selectGroup( getGroups().values().iterator().next().getCode());
		} catch ( Exception e ) {
			logger.debug( "SecurityGroup not found" );
		}
		
		return null;
	}
	
	public SecurityGroup selectGroup( SecurityGroup group ) {
		if ( group != null ) {
			return selectGroup( group.getCode());
		}
		
		return null;
	}
	public SecurityGroup selectGroup( String groupCode ) {
		
		SecurityGroup toSelect = getGroups().get( groupCode );
		if ( toSelect != null ) {
//			if ( selectedGroup != toSelect ) {
				selectedGroup = toSelect;
				logger.debug( "Was selected " + selectedGroup );

				if ( selectedGroup.normalize()) {
					updated = true;
					saveGroup();
				}
				
				
				fireGroupSelected();
//			}
		} else {
			logger.error( "Attempt to select wrong SecurityGroup with code = '" + groupCode + "'" );
		}
		
		return selectedGroup;
	}

	public Organisation selectOrganisation( Organisation org ) {
		if ( org != null ) {
			selectedOrganisation = org;
			this.updated = false;

			selectedGroup = null;
			selectGroup();

			fireOrganisationChanged( selectedOrganisation );
		} else {
			logger.error( "Attempt to select wrong Organisation: null" );
		}
		
		return selectedOrganisation;
	}
	private EventListenerList 	listenerList = new EventListenerList(); 

	public void addChangedListener( SGSelectionChangedListener listener ) {
		listenerList.add( SGSelectionChangedListener.class, listener);
	}

	public void addChangedListener( SelectionChangedListener listener ) {
		listenerList.add( SelectionChangedListener.class, listener);
	}

	public void addChangedListener( OrganisationChangedListener listener ) {
		listenerList.add( OrganisationChangedListener.class, listener);
	}
	
	private void fireGroupSelected() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == SGSelectionChangedListener.class) {
	    		(( SGSelectionChangedListener )listeners[ i + 1 ] ).selectionChanged( selectedGroup );
	         }
	     }
	 }

	private void fireOrganisationChanged( Organisation organisation ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == OrganisationChangedListener.class) {
	    		(( OrganisationChangedListener )listeners[ i + 1 ] ).wasChanged( organisation );
	         }
	     }
		
	}

	private void fireARSelected() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == SelectionChangedListener.class) {
	    		(( SelectionChangedListener )listeners[ i + 1 ] ).selectionChanged();
	         }
	     }
	 }

	public void groupWasChanged( SecurityGroup group ) {
		
		logger.debug( "Group has been changed. New Group: " + ( group != null ? group.getDefName() : "null"));
		
		selectGroup( group );			
		
	}
	
	public void organisationWasChanged( Organisation org ) {
		
		logger.debug( "Organisation has been changed. New Organisation: " + ( org != null ? org.getName() : "null"));
		
		selectOrganisation( org );			
		
	}

	public void updated() { 
		this.updated = true;
		fireARSelected();		
	}	
	public void clearUpdated() { this.updated = false; }	
	public boolean isUpdated() { return this.updated; }	


	public void saveGroup() {
		// Save selectedGroup
		if ( isUpdated()) {
			try {
				SecurityGroup updatedGroup = DataFacade.getInstance().merge( selectedGroup );
				selectedGroup = updatedGroup; 
	
				// Put selectedGroup into the groups
				getGroups().put( selectedGroup.getCode(), selectedGroup );
				// clear 'updated'
				clearUpdated();
				
				logger.debug( "Security Group '" + selectedGroup.getDefName() + "' had been updated!" );
				
			} catch ( Exception e ) {
				logger.error( "Cannot update Security Group because of:\n" + e );
			}
		} else {
			logger.debug( "Security Group '" + selectedGroup.getDefName() + "'. Nothing to update" );
		}
	}

	public void initModel() {

		this.selectedOrganisation = DataFacade.getInstance().find( Organisation.class, this.selectedOrganisation.getId());
		
		this.updated = false;
	}
}
