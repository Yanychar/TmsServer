package com.c2point.tms.tools.imprt;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.UserFacade;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.Task;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.tools.LoggerIF;
import com.c2point.tms.util.exception.NotUniqueCode;

public class v10ProjectsImportProcessor extends DataImportProcessor {
	private static Logger logger = LogManager.getLogger( v10ProjectsImportProcessor.class.getName());

	// Fields length needs to be specified 
//	private int ORG_CODE_LENGTH = ???;
	private int MAX_LENGTH_PROJECT_CODE = 7; 
	
	// Map where all projects specified in the file will be stored
	// Will be used to delete (mark as deleted) all projects not specified
	private Map<String, Project> importedProjects;
	
	public v10ProjectsImportProcessor() {
		this( null );
	}
	
	public v10ProjectsImportProcessor( LoggerIF importLogger ) {
		super( importLogger );
	}
	
	@Override
	protected ValidationResult validateLine(String[] nextLine, int lineNumber) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "    Start String [] validation for conversion into Projects, Tasks and ProjectTasks ..." );
		    // nextLine[] is an array of values from the line

//			logger.debug( "    Line # " + lineNumber + " length=" + nextLine.length ); 
//			String outstr = "      nextLine []: ( ";
//			for ( int j = 0; j < nextLine.length; j++ )
//				outstr = outstr.concat( "'" + (( nextLine[ j ] != null ) ? nextLine[ j ] : "NULL" ) + "'  " );
//			outstr = outstr.concat( ")" );
//			logger.debug( outstr ); 
		}
		
		ValidationResult vldRes = ValidationResult.VALIDATED;
		
		
		if ( nextLine == null 
				|| nextLine[0] != null  && nextLine[0].trim().length() == 0
				|| nextLine[0] != null && StringUtils.startsWith( StringUtils.stripStart( nextLine[0], null ), "#" )  
			) {
				if ( logger.isDebugEnabled()) logger.debug( "    Line # " + lineNumber + ".  This is empty or comment line" );
			
				vldRes = ValidationResult.COMMENT;

				return vldRes;

		} else if ( nextLine != null && nextLine.length != 19 ) {
			logger.error( "Faileds to process line # "+ lineNumber + ". Parsing error. Number of fields less than expected" );
			error( "  ERROR: Line #" + lineNumber + ". Parsing error. Number of fields less than expected" );
		
			vldRes = ValidationResult.FAILED;

			return vldRes;
			
		}

		// Optional fields only can be null or 0 length
		for ( int i = 0; i < nextLine.length; i++ ) {
			if ( i != 9 && i != 13 && i != 15 && i != 16 && i != 5 && i != 6 && i != 7 &&
				 ( nextLine[ i ] == null || nextLine[ i ].length() == 0 )  
			) {
				logger.error( "Faileds to process line # "+ lineNumber + ". Mandatory field #" + i + " is NULL or the length is 0" );
				error( "  ERROR. Line # "+ lineNumber + ". Mandatory field #" + i + " is NULL or the length is 0" );

				vldRes = ValidationResult.FAILED;

				return vldRes;
			}
			
		}
		
		// User can be created and added!
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
		logger.debug( "Start to process Line # " + lineNumber );
		
		
		// 1. Project Manager
		TmsUser prMgr = new TmsUser( nextLine[ 5 ].trim(), nextLine[ 6 ].trim(), nextLine[ 7 ].trim());
		if ( logger.isDebugEnabled()) logger.debug( "  Created " + prMgr );

		// 1 Find ProjectManager
		prMgr = findProjectManager( prMgr );
		if ( prMgr != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "  projectManager was found. " + prMgr );
			
			// Change User code to one specified in the Project Data import file if different
			if ( prMgr.getCode().trim().compareToIgnoreCase( nextLine[ 5 ].trim()) != 0 ) {
				// Try to remove leading 0 and compare to avoid '205' != '00205'
				
				if ( StringUtils.stripStart( prMgr.getCode().trim(), "0" ).compareToIgnoreCase(
						StringUtils.stripStart( nextLine[ 5 ].trim(), "0" )) != 0 ) {
				
					String oldCode = prMgr.getCode(); 
					prMgr.setCode( nextLine[ 5 ].trim());
					
					this.organisation.getUsers().remove( oldCode );
					this.organisation.getUsers().put( prMgr.getCode(), prMgr );
					
					orgToMerge = true;
				
					logger.debug( "Project manager: '" + prMgr.getFirstAndLastNames() + "' code was changed " 
									+ "to '" + prMgr.getCode() + "'");
					prjMgrNewCode 	= true;
				}
			}
			
		} else {
			logger.debug( "Project Manager was not found for Project: '" + nextLine[ 3 ].trim() + "'" );
			logger.debug( "    Service owner will be set up as Project Manager: " + this.organisation.getServiceOwner());
			
			// Add Organisation Service Owner as manager and notify about that!!!
			prMgr = this.organisation.getServiceOwner();
			
			if ( prMgr == null ) {
				logger.error( "Project Manager was not found for Project: '" + nextLine[ 3 ].trim() + "' . Line #: " + lineNumber );
				error( "  ERROR: Line #: " + lineNumber + ". Cannot set up Project Manager for the Project" );
				return false;
			}

			newPrjMgr = true;
			
		}

		// 2. Project
		String projectCode = nextLine[ 2 ].trim();
		String projectName = nextLine[ 3 ].trim(); 
		if ( projectName.length() == 0 ) {
			logger.error( "Project Name shall be defined. ProjectCode: '" + projectCode + "'" );
			projectName = "noname"; 

			noPrjName = true;
		}
		
		
		// Find project
		Project project = this.organisation.getProject( projectCode );
		
		// If project not found than validate that addition of leaded '0' does not help
		int codeLength = projectCode.length();

		while ( codeLength < MAX_LENGTH_PROJECT_CODE && project == null ) {

			codeLength++;
			if ( logger.isDebugEnabled()) 
				logger.debug( "project Code '" + projectCode + "' less than necessary. Addition of leaded '0' will be tried" );
			
			String newCode = StringUtils.leftPad( projectCode, codeLength, '0' );
			
			project = this.organisation.getProject( newCode );
			if ( logger.isDebugEnabled())
				logger.debug( "Project with Code '" + newCode + "' was " + ( project != null ? "found" : "NOT found" ));
			
		}
		
		
		// Create new project if it is not in DB
		if ( project == null ) {
			if ( logger.isDebugEnabled()) logger.debug( "  Project with Code = " + projectCode + " was not found!" );
			project = new Project( projectCode, projectName );
			project.setProjectManager( prMgr );

			// Save project in the list of handled projects
			addImportedProject( project );

			if ( this.organisation.addProject( project )) {
//				if ( logger.isDebugEnabled()) logger.debug( "  Project added to the Organisation" );
//				org = DataFacade.getInstance().merge( org );
//				if ( logger.isDebugEnabled()) logger.debug( "  Organisation stored" );
				if ( logger.isDebugEnabled()) logger.debug( "  Project was added to Organisation (not persistant yet)" );
				orgToMerge = true;
			} else {
				logger.error( "Cannot add project to the organisation." );
				project = null;
				error( "  ERROR: Line #: " + lineNumber + ". Could not add the Project" );
				return false;
			}
		
			newPrj = true;
		
		} else {
			// If project was deleted earlier it must be activated again!
			if ( project.isDeleted()) {
				prjWasDeleted = true;
				logger.debug( "Project: " + project + "was deleted previously. Will be active again!" );
				
				project.setDeleted( false );
				
			}
			
			// Validate and change if necessary ProjectManager
			
			// Save project in the list of handled projects
			addImportedProject( project );
			
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
		String taskCode = nextLine[ 11 ];
		String codeInProject = nextLine[ 12 ];
		String taskName = nextLine[ 13 ].trim();
		if ( taskName.length() == 0 ) {
			logger.error( "Task Name shall be defined. TaskCode: '" + taskCode + "'" );
			taskName = "noname"; 

			noTaskName = true;
		}

		Task task; 
		ProjectTask projectTask;

		// Add Task if necessary
		task = this.organisation.getTask( taskCode );
		
//		if ( logger.isDebugEnabled()) logger.debug( "  Task created" );
//		if ( ProjectAndTaskFacade.getInstance().getTask( taskCode ) == null ) {
		if ( task == null ) {
			if ( logger.isDebugEnabled()) logger.debug( "  Task with code = " + taskCode + " does not exist. Will be added" );
			task = new Task( this.organisation, taskCode, taskName );
			task = DataFacade.getInstance().insert( task );
			if ( task != null ) {
				try {
					this.organisation.addTask( task );
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
			this.organisation = DataFacade.getInstance().merge( this.organisation );
		}

		if ( logger.isDebugEnabled()) logger.debug( "Number of projects in Org = " + this.organisation.getProjects().size());
		if ( logger.isDebugEnabled()) logger.debug( "Number of projects in DB = " + DataFacade.getInstance().list( Project.class ).size());
		
		if ( logger.isDebugEnabled()) logger.debug( "... end of process Line # " + lineNumber );
		

		info( "  Line # " + lineNumber + ": Import Project '" + project.getName() + "' ... OK" );
		if ( prjMgrNewCode )
			info( "       Warning! Project Manager'" + prMgr.getFirstAndLastNames() + "' got new Code" );
		if ( newPrjMgr )
			info( "       No Project Manager!!! Service Owner: '" + prMgr.getFirstAndLastNames() + "' will be set as PM");
		if ( noPrjName )
			info( "       Warning! Project has no Name. Project Code: '" + project.getCode() + "'" );
		if ( newPrj )
			info( "       New Project was added: '" + project.getName() + "'" );
		if ( prjWasDeleted )
			info( "       Project: [" + project.getCode() + ", " + project.getName() + "] was deleted earlier. It will be active again!" );
		if ( noTaskName )
			info( "       Warning! Task has no Name. Task Code: '" + taskCode + "'" );
		if ( newTask )
			info( "       New Task was added: '" + task.getName() + "'" );
		
		return true;
	}

	protected void processComment( String [] nextLine, int lineNumber ) {

		info( "  Line # " + lineNumber + ": empty line" );
		
	}
	
	
	private TmsUser findProjectManager( TmsUser tmsUser ) {

		// Find by Code. If found ==>> nothing to do. return found entity
		TmsUser oldUser = UserFacade.getInstance().findByCode( tmsUser.getCode(), this.organisation );
		if ( oldUser != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Project Manager found by Code: " + oldUser );
			return oldUser;
		}
		if ( logger.isDebugEnabled()) logger.debug( "Project Manager was NOT found by Code: " + tmsUser );
		
		// Find by User Name == >> List of TmsUser-s
		List<TmsUser> list= UserFacade.getInstance().findByUsrName( tmsUser.getLastName(), this.organisation );
		
		// if list.size() == 1, than this is the proj manager. Otherwise first name will be checked
		if ( list.size() == 1 ) {
			return list.get( 0 );
		}
		
		
		// For all in the list:
		for ( TmsUser user : list ) {
			//    If lastname and firstname equals than found ==>> nothing to do. return found entity
			if ( user != null && user.getLastName() != null 
				&& user.getLastName().trim().compareToIgnoreCase( tmsUser.getLastName().trim()) == 0
			) {
				if ( user.getFirstName().trim().compareToIgnoreCase( tmsUser.getFirstName().trim()) == 0
						|| user.getFirstName().trim().indexOf( tmsUser.getFirstName().trim()) >= 0 ) {
					if ( logger.isDebugEnabled()) logger.debug( "Project Manager exists: " + user );
					
					return user;
				}
			}
		}
		
		if ( logger.isDebugEnabled())
			logger.debug( "    Project manager '" + tmsUser.getFirstAndLastNames() + "' was not found in TMS User database!" );
		
		return null;
	}


	protected String getImportDir() { return this.organisation.getProperties().getProperty( "company.projects.import" ); } 
	protected String getArchiveDir() { return this.organisation.getProperties().getProperty( "company.projects.archive" ); }

	protected String getOriginalExt() { return this.organisation.getProperties().getProperty( "company.projects.original.ext", "" ); }
	protected String getProcessedExt() { return this.organisation.getProperties().getProperty( "company.projects.processed.ext", "" ); }
	protected String getErrorExt() { return this.organisation.getProperties().getProperty( "company.projects.error.ext", "" ); }

	protected boolean toRename() { 
		return Boolean.parseBoolean( this.organisation.getProperties().getProperty( "company.projects.rename", "true" ));
	}
	protected boolean toMove() {
		return Boolean.parseBoolean( this.organisation.getProperties().getProperty( "company.projects.move", "true" ));
	}
	protected boolean toDelete() {
		return Boolean.parseBoolean( this.organisation.getProperties().getProperty( "company.projects.delete", "true" ));
	}
	
	private void deleteMissedProjects() {
		
		if ( logger.isDebugEnabled()) {
			logger.debug( "*** Projects imported from the file. Size = " + importedProjects.size() + " ***" );
			
			for ( Project project : importedProjects.values()) {
				logger.debug( "  " + project );
			}
			
			logger.debug( "*** ... end of Project List ***" );
		}
		
		boolean orgToMerge = false;
		for ( Project project : this.organisation.getProjects().values()) {

			// Delete the project if it was NOT in the import file
			if ( project != null && !projectWasImported( project )) {
				logger.debug( "Project will be deleted: " + project );
				project.setDeleted();
				orgToMerge = true;
			}
		}
		
		// Save Organisation if the list of Projects has been changed
		if ( orgToMerge ) {
			this.organisation = DataFacade.getInstance().merge( this.organisation );
		}
		
	}

	private void addImportedProject( Project project ) {
		importedProjects.put( project.getCode(),project );
	}
	
	private boolean projectWasImported( Project project ) {
		
		return importedProjects.containsKey( project.getCode());
	}
	
	
	
	@Override
	protected boolean preProcessFile(File inputFile) {
		boolean res = super.preProcessFile(inputFile);
		
		importedProjects = new HashMap<String, Project>();
		
		return res;
	}

	@Override
	protected void postProcessFile(File inputFile, boolean res) {
		
		deleteMissedProjects();

		super.postProcessFile(inputFile, res);
	}
	
}
