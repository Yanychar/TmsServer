package com.c2point.tms.tools.imprt.projectdata;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.UserFacade;
import com.c2point.tms.entity.GeoCoordinates;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.Task;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.exception.NotUniqueCode;

public class ProjectsDataImportProcessor extends DataImportProcessor_New {
	private static Logger logger = LogManager.getLogger( ProjectsDataImportProcessor.class.getName());

/*	Next line buffer:
	[ 0 ] - prj.getCode(), 
	[ 1 ] - prj.getName(),
	[ 2 ] - prj.getAddress(),
	[ 3 ] - StringUtils.defaultString( prj.getGeo().getLatitude().toString()),
	[ 4 ] - StringUtils.defaultString( prj.getGeo().getLongitude().toString()),
	[ 5 ] - prj.getProjectManager().getCode(), 
	[ 6 ] - prj.getProjectManager().getFirstName(), 
	[ 7 ] - prj.getProjectManager().getLastName(), 
	[ 8 ] - t.getCode(),
	[ 9 ] - t.getName(),
	[ 10] - pt.getCodeInProject()
*/
	
	// Fields length needs to be specified 
//	private int ORG_CODE_LENGTH = ???;
	private int MAX_LENGTH_PROJECT_CODE = 7; 
	
	// Map where all projects specified in the file will be stored
	// Will be used to delete (mark as deleted) all projects not specified
	private Map<String, Project> importedProjects = new HashMap<String, Project>();
	
	public ProjectsDataImportProcessor(Organisation org) {
		super(org);
		
	}

	@Override
	protected ValidationResult validateLine(String[] nextLine, int lineNumber) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "    Start String [] validation for conversion into Projects, Tasks and ProjectTasks ..." );
		}
		
		ValidationResult vldRes = ValidationResult.VALIDATED;
		
		// Check if line is commented out
		if ( nextLine == null 
				|| nextLine[0] != null  && nextLine[0].trim().length() == 0
				|| nextLine[0] != null && StringUtils.startsWith( StringUtils.stripStart( nextLine[0], null ), "#" )  
			) {
				if ( logger.isDebugEnabled()) logger.debug( "    Line # " + lineNumber + ".  This is empty or comment line" );
			
				vldRes = ValidationResult.COMMENT;

				return vldRes;

		} else if ( nextLine != null && nextLine.length != 11 ) {
			// Check that number if fields OK
			logger.error( "Faileds to process line # "+ lineNumber + ". Parsing error. Number of fields less than expected" );
			error( "  ERROR: Line #" + lineNumber + ". Parsing error. Number of fields less than expected" );
		
			vldRes = ValidationResult.FAILED;

			return vldRes;
			
		}

		// Optional fields only can be null or 0 length
		for ( int i = 0; i < nextLine.length; i++ ) {
			if ( i != 2 && i != 3 && i != 4 && StringUtils.isBlank( nextLine[ i ] )) {
				logger.error( "Faileds to process line # "+ lineNumber + ". Mandatory field #" + i + " is NULL or the length is 0" );
				error( "  ERROR. Line # "+ lineNumber + ". Mandatory field #" + i + " is NULL or the length is 0" );

				vldRes = ValidationResult.FAILED;

				return vldRes;
			}
			
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "    Line # " + lineNumber + " is valid" );


		if ( logger.isDebugEnabled()) {
			logger.debug( "    ... end of String [] validation" );
		}
		
		return vldRes;
	}

	@Override
	protected boolean processNextLine(String[] nextLine, int lineNumber) {

		boolean orgToMerge = false;

		boolean prjMgrNewCode 	= false;
		boolean newPrjMgr 		= false;
		boolean noPrjName 		= false;
		boolean newPrj 			= false;
		boolean prjWasDeleted	= false;
		boolean noTaskName 		= false;
		boolean newTask 		= false;
		
		// Objects to create and fill:
		//  Project, Task, ProjectTask, ProjectManager, TmsUser
		logger.debug( "Start to process Line # " + lineNumber + ": " + nextLine[0] );
		
		// 1. Project Manager
		TmsUser tmpMgr = new TmsUser( nextLine[ 5 ].trim(), nextLine[ 6 ].trim(), nextLine[ 7 ].trim());
		if ( logger.isDebugEnabled()) logger.debug( "  Created " + tmpMgr );

		// 1 Find ProjectManager
		TmsUser prMgr = findProjectManager( tmpMgr );
		
		if ( prMgr != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "  projectManager was found. " + prMgr );
			
			// Change User code to one specified in the Project Data import file if different
			if ( prMgr.getCode().trim().compareToIgnoreCase( tmpMgr.getCode().trim()) != 0 ) {
				// Try to remove leading 0 and compare to avoid '205' != '00205'
				
				if ( StringUtils.stripStart( prMgr.getCode().trim(), "0" ).compareToIgnoreCase(
						StringUtils.stripStart( tmpMgr.getCode().trim(), "0" )) != 0 ) {
				
					String oldCode = prMgr.getCode(); 
					prMgr.setCode( tmpMgr.getCode().trim());
					
					this.getOrg().getUsers().remove( oldCode );
					this.getOrg().getUsers().put( prMgr.getCode(), prMgr );
					
					orgToMerge = true;
				
					logger.debug( "Project manager: '" + prMgr.getFirstAndLastNames() + "' code was changed " 
									+ "to '" + prMgr.getCode() + "'");
					prjMgrNewCode 	= true;
				}
			}
			
		} else {
			logger.debug( "Project Manager was not found for Project: '" + nextLine[ 3 ].trim() + "'" );
			
			// Add Organisation newly created project manager
			prMgr = tmpMgr;
			// Set Project Manager flag
			prMgr.setProjectManager( true );
			
			// Add to the list of persons in the organisation
			this.getOrg().getUsers().put( prMgr.getCode(), prMgr );
			
			orgToMerge = true;
			newPrjMgr = true;
			
		}

		// 2. Project
		String projectCode = nextLine[ 0 ].trim();
		String projectName = nextLine[ 1 ].trim(); 
		if ( projectName.length() == 0 ) {
			logger.error( "Project Name shall be defined. ProjectCode: '" + projectCode + "'" );
			projectName = "noname"; 

			noPrjName = true;
		}
		
		// Find project
		Project project = findProject( projectCode );
		
		// If the project does not exist
		// Create it
		if ( project == null ) {

			if ( logger.isDebugEnabled()) logger.debug( "  Project with Code = " + projectCode + " was not found!" );
			project = new Project( projectCode, projectName );
			project.setProjectManager( prMgr );
			// set address
			if ( !StringUtils.isEmpty( nextLine[ 2 ] )) {
				project.setAddress( nextLine[ 2 ].trim());
			}
			
			// Set location
			try {
				GeoCoordinates geo = new GeoCoordinates();
				
				Double lat = Double.valueOf( nextLine[ 3 ]);
				Double lon = Double.valueOf( nextLine[ 4 ]);
				geo.setLatitude( lat );
				geo.setLongitude( lon );
				project.setGeo(geo);
			} catch ( Exception e ) {
			}
			

			if ( this.getOrg().addProject( project )) {
//				if ( logger.isDebugEnabled()) logger.debug( "  Project added to the Organisation" );
//				org = DataFacade.getInstance().merge( org );
//				if ( logger.isDebugEnabled()) logger.debug( "  Organisation stored" );
				if ( logger.isDebugEnabled()) logger.debug( "  Project was added to Organisation (not persistant yet)" );
				
				// Save project in the list of handled projects
				addImportedProject( project );
				
				orgToMerge = true;
			} else {
				logger.error( "Cannot add project to the organisation." );
				project = null;
				error( "  ERROR: Line #: " + lineNumber + ". Could not add the Project" );
				return false;
			}
		
			newPrj = true;
			
		} else {
			// If the project exists than its attributes shall be checked and changed if necessary
			
			// If project was deleted earlier it must be activated again!
			if ( project.isDeleted()) {
				prjWasDeleted = true;
				logger.debug( "Project: " + project + "was deleted previously. Will be active again!" );
				
				project.setDeleted( false );
				
				orgToMerge = true;
			}
			
			// Save project in the list of handled projects
			addImportedProject( project );
			
			// Validate and change if necessary ProjectManager
			if ( project.getProjectManager() == null || 
					prMgr != null && project.getProjectManager().getCode().compareTo( prMgr.getCode()) != 0 || 
					prMgr != null && project.getProjectManager().getFirstAndLastNames().compareTo( prMgr.getFirstAndLastNames()) != 0 
				 
			) {
				if ( logger.isDebugEnabled()) logger.debug( "  Update Project Manager in the Project (code !=)" );
				project.setProjectManager( prMgr );
				orgToMerge = true;
			}
			
			
			
		}
		
		// 3. Add Tasks and ProjectTasks
		String taskCode = nextLine[ 8 ];
		String taskName = nextLine[ 9 ].trim();
		String codeInProject = nextLine[ 10 ];
		if ( taskName.length() == 0 ) {
			logger.error( "Task Name shall be defined. TaskCode: '" + taskCode + "'" );
			taskName = "noname"; 

			noTaskName = true;
		}

		Task task; 
		ProjectTask projectTask;

		// Add Task if necessary
		task = this.getOrg().getTask( taskCode );
		
//		if ( logger.isDebugEnabled()) logger.debug( "  Task created" );
//		if ( ProjectAndTaskFacade.getInstance().getTask( taskCode ) == null ) {
		if ( task == null ) {
			if ( logger.isDebugEnabled()) logger.debug( "  Task with code = " + taskCode + " does not exist. Will be added" );
			task = new Task( this.getOrg(), taskCode, taskName );
			task = DataFacade.getInstance().insert( task );
			if ( task != null ) {
				try {
					this.getOrg().addTask( task );
				} catch ( NotUniqueCode e ) {
					error( "  ERROR: Line #: " + lineNumber + ". Cannot add new Task. Task Code exists" );
				}
				orgToMerge = true;
				
				newTask = true;
				
			} else {
				logger.error( "Cannot add new Task. Line #: " + lineNumber + "\n" );
				error( "  ERROR: Line #: " + lineNumber + ". Cannot add new Task" );
			}
		}
		
		// Check if Project has assigned this Task already
		if ( project.getProjectTask( taskCode ) != null ) {
			// ProjectTask has been assigned already
			if ( logger.isDebugEnabled()) 
				logger.debug( "  Task with code = " + taskCode + " assigned already to project " + project.getCode());
		} else {
			// ProjectTask has NOT been assigned. Must be assigned
			projectTask = project.assignTask( task );
			projectTask.setCodeInProject( codeInProject );
			orgToMerge = true;
		}
		
		
		if ( orgToMerge ) {
			this.setOrg( DataFacade.getInstance().merge( this.getOrg()));
		}


		
		return true;
	}

	@Override
	protected void processComment(String[] nextLine, int lineNumber) {

		info( "  Line # " + lineNumber + ": Comment" );
		
	}

	private TmsUser findProjectManager( TmsUser tmsUser ) {

/*		
		// Find by Code. If found ==>> nothing to do. return found entity
		TmsUser oldUser = UserFacade.getInstance().findByCode( tmsUser.getCode(), getOrg());
		if ( oldUser != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Project Manager found by Code: " + oldUser );
			return oldUser;
		}
		if ( logger.isDebugEnabled()) logger.debug( "Project Manager was NOT found by Code: " + tmsUser );
*/		
		// Find by User Name == >> List of TmsUser-s
		TmsUser prjMgr = UserFacade.getInstance().findUser( tmsUser, this.getOrg());
		
		
		if ( logger.isDebugEnabled()) {
			
			if ( prjMgr != null )
				logger.debug( "    Project manager '" + prjMgr.getFirstAndLastNames() + "' was found in TMS User database!" );
			else
				logger.debug( "    Project manager '" + tmsUser.getFirstAndLastNames() + "' was not found in TMS User database!" );
		}
		
		return prjMgr;
	}

	private Project findProject( String projectCode ) {
		
		Project project = this.getOrg().getProject( projectCode );
	
		// If project not found than validate that addition of leaded '0' does not help
		int codeLength = projectCode.length();
	
		while ( codeLength < MAX_LENGTH_PROJECT_CODE && project == null ) {
	
			codeLength++;
			if ( logger.isDebugEnabled()) 
				logger.debug( "project Code '" + projectCode + "' less than necessary. Addition of leaded '0' will be tried" );
			
			String newCode = StringUtils.leftPad( projectCode, codeLength, '0' );
			
			project = this.getOrg().getProject( newCode );
			if ( logger.isDebugEnabled())
				logger.debug( "Project with Code '" + newCode + "' was " + ( project != null ? "found" : "NOT found" ));
			
		}
		
		return project;
	}

	private void addImportedProject( Project project ) {
		importedProjects.put( project.getCode(),project );
	}
	
	private boolean projectWasImported( Project project ) {
		
		return importedProjects.containsKey( project.getCode());
	}

	
}
