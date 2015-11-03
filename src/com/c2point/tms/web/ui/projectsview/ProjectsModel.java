package com.c2point.tms.web.ui.projectsview;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.Task;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.access.SecurityContext;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.util.exception.NotUniqueCode;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractModel;
import com.c2point.tms.web.ui.listeners.ProjectAddedListener;
import com.c2point.tms.web.ui.listeners.ProjectChangedListener;
import com.c2point.tms.web.ui.listeners.ProjectDeletedListener;
import com.c2point.tms.web.ui.listeners.ProjectListChangedListener;
import com.c2point.tms.web.ui.listeners.ProjectTaskAddedListener;
import com.c2point.tms.web.ui.listeners.ProjectTaskChangedListener;
import com.c2point.tms.web.ui.listeners.ProjectTaskDeletedListener;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;
import com.c2point.tms.web.ui.listeners.TaskAddedListener;
import com.c2point.tms.web.ui.listeners.TaskChangedListener;
import com.c2point.tms.web.ui.listeners.TaskDeletedListener;

public class ProjectsModel extends AbstractModel {

	private static Logger logger = LogManager.getLogger( ProjectsModel.class.getName());

	private Organisation 			org;
	private Map<String, Project>	mapProjects;
	
	private Project					selectedProject;

 	// Filtering options
 	private boolean showClosed;		// Show if End Date defined and reached
 	private boolean showDeleted;	// Show deleted entities
 	
	public ProjectsModel( TmsApplication app, Organisation org ) {
		super( app );
		this.org = org;
		
		setToShowFilter( SupportedFunctionType.PROJECTS_OWN );
			
	 	setShowClosed( false );
	 	setShowDeleted( false );
		
	}

	public void initModel() {

		TmsUser sessionOwner = getSessionOwner();

		if ( sessionOwner == null ) {
			logger.error( "User defined in the Session was not found!!!" );
			return;
		}

		Organisation oldOrg = DataFacade.getInstance().find( Organisation.class, this.org.getId());
		
		if ( oldOrg == null ) {
			logger.error( "No Organisation has been defined for the User: '" + getSessionOwner().getFirstAndLastNames() + "'!!!" );
			return;
		} else {
			this.org = oldOrg;
		}
		
		logger.debug( "  projectModel.initModel start..." );

//		if ( mapProjects == null ) {
			mapProjects = new HashMap<String, Project>();
//		} else {
//			mapProjects.clear();
//		}
		
		for ( Project prj : org.getProjects().values()) {
			
			if (	getToShowFilter() == SupportedFunctionType.PROJECTS_COMPANY 
				||
				getToShowFilter() ==  SupportedFunctionType.PROJECTS_TEAM 
					&&
					prj.getProjectManager().getId() == sessionOwner.getId()
				) {
				
				if ( !prj.isClosed() && !prj.isDeleted() 
					|| 
					 toShowClosed() && prj.isClosed()
					||
					 toShowDeleted() && prj.isDeleted()) {
					
					mapProjects.put( prj.getCode(), prj );
					
				}
					 
			}
		}
		
		
		logger.debug( "  ... end projectModel.initModel" );

		fireProjectsListChanged();
		
	}
	
	public List<Project> getProjectsList() {
		ArrayList< Project > list = new ArrayList< Project >(); 
		if ( mapProjects != null && !mapProjects.isEmpty()) {
			
			for ( Project project : mapProjects.values()) {
				if ( isFilteredIn( project )) {
					list.add( project );
				}
			}
		}
		
		return list;
	}

	public Project getSelectedProject() {
		return this.selectedProject;
	}

	public void addChangedListener( ProjectAddedListener listener ) {
		listenerList.add( ProjectAddedListener.class, listener);
	}
	public void addChangedListener( ProjectChangedListener listener ) {
		listenerList.add( ProjectChangedListener.class, listener);
	}
	public void addChangedListener( ProjectDeletedListener listener ) {
		listenerList.add( ProjectDeletedListener.class, listener);
	}
	public void addChangedListener( SelectionChangedListener listener ) {
		listenerList.add( SelectionChangedListener.class, listener);
	}
	
	private void fireProjectAdded( Project  project ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectAddedListener.class) {
	    		(( ProjectAddedListener )listeners[ i + 1 ] ).wasAdded( project );
	         }
	     }
		
	}
	private void fireProjectChanged( Project  project ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectChangedListener.class) {
	    		(( ProjectChangedListener )listeners[ i + 1 ] ).wasChanged( project );
	         }
	     }
		
	}
	@SuppressWarnings("unused")
	private void fireProjectDeleted( Project  project ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectDeletedListener.class) {
	    		(( ProjectDeletedListener )listeners[ i + 1 ] ).wasDeleted( project );
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



	public void addChangedListener( TaskAddedListener listener ) {
		listenerList.add( TaskAddedListener.class, listener);
	}
	private void fireTaskAdded( Task task ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == TaskAddedListener.class) {
	    		(( TaskAddedListener )listeners[ i + 1 ] ).wasAdded( task );
	         }
	     }
		
	}
	public void addChangedListener( TaskChangedListener listener ) {
		listenerList.add( TaskChangedListener.class, listener);
	}
	private void fireTaskChanged( Task task ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == TaskChangedListener.class) {
	    		(( TaskChangedListener )listeners[ i + 1 ] ).wasChanged( task );
	         }
	     }
		
	}
	public void addChangedListener( TaskDeletedListener listener ) {
		listenerList.add( TaskDeletedListener.class, listener);
	}
	private void fireTaskDeleted( Task task ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == TaskDeletedListener.class) {
	    		(( TaskDeletedListener )listeners[ i + 1 ] ).wasDeleted( task );
	         }
	     }
		
	}
	
	public void addChangedListener( ProjectTaskAddedListener listener ) {
		listenerList.add( ProjectTaskAddedListener.class, listener);
	}
	@SuppressWarnings("unused")
	private void fireProjectTaskAdded( ProjectTask pTask ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectTaskAddedListener.class) {
	    		(( ProjectTaskAddedListener )listeners[ i + 1 ] ).wasAdded( pTask );
	         }
	     }
		
	}
	public void addChangedListener( ProjectTaskChangedListener listener ) {
		listenerList.add( ProjectTaskChangedListener.class, listener);
	}
	private void fireProjectTaskChanged( ProjectTask pTask ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectTaskChangedListener.class) {
	    		(( ProjectTaskChangedListener )listeners[ i + 1 ] ).wasChanged( pTask );
	         }
	     }
		
	}
	public void addChangedListener( ProjectTaskDeletedListener listener ) {
		listenerList.add( ProjectTaskDeletedListener.class, listener);
	}
	private void fireProjectTaskDeleted( ProjectTask pTask ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectTaskDeletedListener.class) {
	    		(( ProjectTaskDeletedListener )listeners[ i + 1 ] ).wasDeleted( pTask );
	         }
	     }
		
	}
	


	
	public void selectProject(Project project) {
		this.selectedProject = project;
		if ( logger.isDebugEnabled()) logger.debug( "project set as selected: " + this.selectedProject );
		fireSelectionChanged();
	}


	private boolean isFilteredIn( Project project ) {
		boolean bRes = true;
		
		return bRes;
	}

	public boolean codeExists( Project project ) {
		return codeExists( project.getCode());
	}
	public boolean codeExists( String code ) {
		
		return mapProjects.containsKey( code );
	}
	
	public Project addProject( Project project ) {
		Project result = null;

		// Cannot add the Project with code existed already
		if ( !mapProjects.containsKey( project.getCode())) {

			// Set up active user as Project Manager if it is missing (last chance)
			if ( project.getProjectManager() == null ) {
				project.setProjectManager( getSessionOwner());
			}
			
			org = DataFacade.getInstance().find( Organisation.class, org.getId());
			if ( org != null ) {
				// Add new Project to the Organisation
				if ( org.addProject( project )) {
					org = DataFacade.getInstance().merge( org );
					if ( org != null ) {
						mapProjects = org.getProjects();

						result = mapProjects.get( project.getCode()); 

						// Fire Project changed event to select added project
						fireProjectAdded( result );
						
					} else {
						logger.error( "Organisation was not 'merged' in DB after new project was added" );
					}
				}
				
				
				
			}
			
		} else {
			logger.error( "Cannot add the Project because the project with the same Code already exists already!" );
		}

		return result;
	}

	public Project updateProject( Project project ) {
		return updateProject( project, false );
	}
	
	public Project updateProject( Project project, boolean changeTaskList ) {
		Project result = null;

		// Update DB
		if ( project != null ) {
			// Save Project
			Project oldProject = DataFacade.getInstance().find( Project.class, project.getId());
			if ( oldProject != null ) {
				oldProject.update( project );
				if ( changeTaskList ) {
					oldProject.setProjectTasks( project.getProjectTasks());
				}
				
				project = DataFacade.getInstance().merge( oldProject );
				if ( project == null ) {
					logger.error( "Failed to update Project: [" + oldProject.getCode() + ", " + oldProject.getCode() 
										+ "], Org: [" 
										+ org.getCode() + ", " 
										+ org.getName() + "]" 
										+ " from DB!" );
				} else {
					result = project;
					
					mapProjects.put( project.getCode(), project );
					// Fire model changed event!
					fireProjectChanged( project );
					
				}
			} else {
				logger.error( "Edited Project was not found: [" 
								+ project.getCode() + ", " + project.getCode() 
								+ "], Org: [" 
								+ org.getCode() + ", " 
								+ org.getName() + "]" 
								+ " from DB!" );
			}
		}
		
		return result;
	}

	public boolean removeProject( Project project ) {
		boolean bRes = false;


		// Update DB
		if ( project != null ) {
			// Save Project
			Project oldProject = DataFacade.getInstance().find( Project.class, project.getId());
			if ( oldProject != null ) {
				oldProject.setDeleted();
				
				project = DataFacade.getInstance().merge( oldProject );
				if ( project != null ) {
					
					mapProjects.put( project.getCode(), project );
					// Fire model changed event!
					fireProjectChanged( project );

					if ( selectedProject != null && selectedProject.getCode().compareToIgnoreCase( project.getCode()) == 0 ) {
						selectedProject = null;
						fireSelectionChanged();
						 
					}
					if ( logger.isDebugEnabled()) { logger.debug( " Project has been removed from Project Model" ); }
					
					bRes = true;
					
				} else {
					logger.error( "Failed to delete the Project: [" + oldProject.getCode() + ", " + oldProject.getCode() 
							+ "], Org: [" 
							+ org.getCode() + ", " 
							+ org.getName() + "]" 
							+ " from DB!" );
				}
			} else {
				logger.error( "Project to be deleted was not found: [" 
								+ project.getCode() + ", " + project.getCode() 
								+ "], Org: [" 
								+ org.getCode() + ", " 
								+ org.getName() + "]" 
								+ " from DB!" );
			}
		}
		
		return bRes;
	}

	public boolean assignTasks( Collection< Task > tasksCollection ) {
		boolean bRes = true;
		boolean assignTmpRes;
		
		// Traverse all selected Tasks
		for ( Task task : tasksCollection ) {
			// Ask model to assign
			if ( logger.isDebugEnabled()) {
				logger.debug( "   Task selected and need to be assigned: " + task.getName());
			}
			assignTmpRes = assignTask( task );
			
			bRes = bRes && assignTmpRes;
		}

		// Save Project if changes were done successfully 
		if ( bRes && tasksCollection.size() > 0 ) {
			Project tmpProject = updateProject( selectedProject, true );
			if ( tmpProject != null ) {
				selectedProject = tmpProject;
//				this.fireProjectChanged( selectedProject );
				this.fireSelectionChanged();
				bRes = true;
			} else {
				bRes = false;
				logger.error( "Failed to update Project in DB" );
			}
			
		}
		
		return bRes;
	}

	private boolean assignTask( Task task ) {
		boolean bRes = false;

		if ( task == null ) {
			logger.error( "Passed parameter 'task' must be  != null!" );
			return bRes;
		}

		// Check that Task was not assigned already
    	if ( selectedProject.getProjectTask( task.getCode()) != null ) {
			logger.error( "Task [" + task.getCode() + ", " + task.getName() + "] has been assigned already!" );
			return bRes;
		}
		
		// Assign to project and setup default and CodeInProject code
   		selectedProject.assignTask( task ).setCodeInProject();

   		bRes = true;
   		
		if ( logger.isDebugEnabled()) { 
			logger.debug( "Task [" + task.getCode() + ", " + task.getName() + "]  has been assigned to the project" );
	    }
		
		return bRes;
	}
	
	public ProjectTask updateProjectTask( ProjectTask pTask ) throws NotUniqueCode {

		ProjectTask result = null;

		if ( pTask == null ) {
			logger.error( "ProjectTask == null. Cannot be updated!" );
			return null;
		}

		OrganisationFacade of = OrganisationFacade.getInstance(); 
		
		result = of.updateAssignedTask( pTask );
		
		if ( result != null ) {

			fireProjectTaskChanged( result );
			
		} else {
			logger.error( "ProjectTask was not updated!" );
		}
		
		return result;
		
	}

	public ProjectTask deleteProjectTask( ProjectTask pTask ) {
		ProjectTask result = null;

		

		/*		
		result = of.deleteTaskAssignment( selectedProject, pTask );
		
		if ( result != null ) {

			fireProjectTaskDeleted( pTask );
			
		} else {
			logger.error( "Task was not deleted!" );
		}
*/

		if ( !OrganisationFacade.getInstance().deleteTaskAssignment( pTask )) {
			logger.error( "Failed to delete ProjectTask: " + pTask );
			return null;
		}
		
		
//		org.getProjects().put( result.getProject().getCode(), result.getProject() );
		
//		initModel();
		
//		Project project = org.getProject( pTask.getProject().getCode());
		
//		this.fireProjectChanged( project );

//		this.selectProject( project );
		
/*	
		private Organisation 			org;
		private Map<String, Project>	mapProjects;
		private Project					selectedProject;
*/		
		
		Project project = mapProjects.get( pTask.getProject().getCode());
		project.getProjectTasks().remove( pTask.getTask().getCode());
		
		this.selectProject( project );
		
		result = pTask;
		 
		fireProjectTaskDeleted( pTask );


		return result;
		
	}

	public List<TmsUser> getProjectManagers() {
		List<TmsUser> list = new ArrayList<TmsUser>();
		
		for ( TmsUser user : org.getUsers().values()) {
			if ( user.isProjectManager()) {
				list.add( user );
			}
		}
		
		if ( list != null ) {
			Collections.sort( list, new LastNameComparator());
		}
		
		return list;
	}

	public void setProjectsToShow( SupportedFunctionType presenceToShow ) {

		if ( getToShowFilter() != presenceToShow ) { 

			SecurityContext context = getSessionOwner().getContext();
			
			if ( presenceToShow == SupportedFunctionType.PROJECTS_TEAM 
				&& (
					context.isRead( SupportedFunctionType.PROJECTS_TEAM )
					|| 
					context.isRead( SupportedFunctionType.PROJECTS_COMPANY ))) {
				
				setToShowFilter( presenceToShow );
			} else if ( presenceToShow == SupportedFunctionType.PROJECTS_COMPANY 
					&& context.isRead( SupportedFunctionType.PROJECTS_COMPANY )) {

				setToShowFilter( presenceToShow );
			} else {
				setToShowFilter( SupportedFunctionType.PROJECTS_OWN );
			}
		}
			
	}

 	public void setShowClosed( boolean toShow ) { showClosed = toShow; }
 	public boolean toShowClosed() { return showClosed; }
 	public void setShowDeleted( boolean toShow ) { showDeleted = toShow; }
 	public boolean toShowDeleted() { return showDeleted; }

 	
	public class LastNameComparator implements Comparator< TmsUser >{

		private Collator standardComparator;
		
		public LastNameComparator() {
			standardComparator = Collator.getInstance(); 
		}
		
		@Override
		public int compare( TmsUser arg1, TmsUser arg2 ) {
			return standardComparator.compare( arg1.getLastAndFirstNames(), arg2.getLastAndFirstNames());
		}

	}

	public void addChangedListener( ProjectListChangedListener listener ) {
		listenerList.add( ProjectListChangedListener.class, listener);
	}
	
	protected void fireProjectsListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectListChangedListener.class) {
	    		(( ProjectListChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }
	 	
	public Organisation getOrg() {
		org = DataFacade.getInstance().find( Organisation.class, org.getId());
		return org;
	}

	public void refreshOrg() {
		org = DataFacade.getInstance().find( Organisation.class, org.getId());
	}

	public Task addTask( Task task ) throws NotUniqueCode {
		
		Task result = null;

		OrganisationFacade of = OrganisationFacade.getInstance(); 
		
		result = of.addTask( org, task );
		
		if ( result != null ) {

			fireTaskAdded( result );
			
		} else {
			logger.error( "Task was not added!" );
		}
		
		return result;
	}
	
	public Task updateTask( Task task ) throws NotUniqueCode {
		
		Task result = null;

		OrganisationFacade of = OrganisationFacade.getInstance(); 
		
		result = of.updateTask( org, task );
		
		if ( result != null ) {

			fireTaskChanged( result );
			
		} else {
			logger.error( "Task was not updated!" );
		}
		
		return result;
	}
	
	public Task deleteTask( Task task ) {
		
		Task result = null;

		OrganisationFacade of = OrganisationFacade.getInstance(); 
		
		result = of.deleteTask( org, task );
		
		if ( result != null ) {

			fireTaskDeleted( result );
			
		} else {
			logger.error( "Task was not deleted!" );
		}
		
		return result;
	}
	
}
