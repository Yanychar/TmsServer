package com.c2point.tms.web.ui.subcontracting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.SubcontractFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.subcontracting.Contract;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.listeners.ProjectListChangedListener;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;

public class SubcontractingModel extends AbstractModel {

	private static Logger logger = LogManager.getLogger( SubcontractingModel.class.getName());

//	private Map<String, Project>	mapProjects;
	
	private Organisation 			org; 
	
	private Project					selectedProject;

 	// Filtering options
 	private boolean showClosed;		// Show if End Date defined and reached
 	private boolean showDeleted;	// Show deleted entities
	
	public SubcontractingModel( TmsApplication app ) {
		super( app );
		
		if ( getSessionOwner() == null ) {
			logger.error( "User defined in the Session was not found!!!" );
			return;
		}

		this.org = getSessionOwner().getOrganisation();
		
	 	setShowClosed( false );
	 	setShowDeleted( false );
	}

	public void initModel() {


		Organisation orgOld = DataFacade.getInstance().
				find( Organisation.class, getSessionOwner().getOrganisation().getId());
		
		if ( orgOld == null ) {
			logger.error( "No Organisation has been defined for the User: '" + getSessionOwner().getFirstAndLastNames() + "'!!!" );
			return;
		}
		
		this.org = orgOld;
		
		logger.debug( "  SibcontractingModel.initModel start..." );

		
		logger.debug( "  ... end projectModel.initModel" );

		fireProjectsListChanged();
		
	}

	public List<Project> getProjectsList() {

		ArrayList< Project > list = new ArrayList< Project >( this.org.getProjects().values()); 
		
		return list;
	}

	public Project getSelectedProject() {
		return this.selectedProject;
	}

	public void selectProject(Project project) {
		
		this.selectedProject = project;
		if ( logger.isDebugEnabled()) logger.debug( "project set as selected: " + this.selectedProject );

		
		fireSelectionChanged();
	}

	
	public Collection<Contract> getContracts( Project project ) {
	
		if ( getSelectedProject() != null ) {
			
			return SubcontractFacade.getInstance()
									.listContracts( project );
		}
		
		return null;
	};
	
	public Collection<Contract> getContracts() {
		
		return getContracts( getSelectedProject());
	};
	
	public boolean okToShow( Project project ) {
		boolean bRes = true;

	 	if ( !toShowClosed() && project.isClosed()) {
	 		bRes = false;
	 	}
	 	
	 	if ( !toShowDeleted() && project.isDeleted()) {
	 		bRes = false;
	 	}
	 	
		return bRes;
	}

 	public void setShowClosed( boolean toShow ) { showClosed = toShow; }
 	public boolean toShowClosed() { return showClosed; }
 	public void setShowDeleted( boolean toShow ) { showDeleted = toShow; }
 	public boolean toShowDeleted() { return showDeleted; }

	public void addChangedListener( ProjectListChangedListener listener ) {
		listenerList.add( ProjectListChangedListener.class, listener);
	}
	
	private void fireProjectsListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectListChangedListener.class) {
	    		(( ProjectListChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }

	public void addChangedListener( SelectionChangedListener listener ) {
		listenerList.add( SelectionChangedListener.class, listener);
	}
	
	private void fireSelectionChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == SelectionChangedListener.class) {
	    		(( SelectionChangedListener )listeners[ i + 1 ] ).selectionChanged();
	         }
	     }
	}

	
}
