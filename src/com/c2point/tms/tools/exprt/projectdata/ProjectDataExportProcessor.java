package com.c2point.tms.tools.exprt.projectdata;

import java.io.File;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.transactions.Transaction;
import com.c2point.tms.tools.LoggerIF;

public abstract class ProjectDataExportProcessor {
	private static Logger logger = LogManager.getLogger( ProjectDataExportProcessor.class.getName());

	private enum ProcessorType{ TIME_REPORTS, PROJECTS, PEOPLES }; 

	public enum ScopeType { ALL, OPENED, CLOSED };
	public enum FormatType { CSV, EXCEL, XML };
	
	protected Organisation 		organisation;

	protected String 			exportDirectory = null;
	
	protected LoggerIF			processingLogger;

	protected RecordsCounter	counter;
	
	public ProjectDataExportProcessor() {
		this( null );
	}
	
	public ProjectDataExportProcessor( LoggerIF exportLogger ) {
		super();
		
		this.processingLogger = exportLogger;
		
	}
	
	public boolean process( Organisation organisation, ScopeType scopeType, FormatType format ) {
		boolean result = false;

		String errMsg = null;

		if ( organisation == null ) {
			logger.error( "Organisation cannot be null for data export!" );

			errMsg = "ERROR: Organisation must be specified!";
			error( errMsg );

			return result;
		}

		this.organisation = organisation;
		
		// Where to store 
		
		
		// Validate that import/export directory exists
		
		info( "*** Start project Data export ... ***" );
		
		counter = new RecordsCounter();
		
		for ( Project prj : organisation.getProjects().values()) {
			
			if ( isProjectScopeOk( prj, scopeType )) {
				for ( ProjectTask pt : prj.getProjectTasksList()) {
					
					handleOneProjectTask( pt );
				}
				
			}
		}
	
		
		info( "*** End data export: ***" + ( result ? "Success" : "FAILED") );
		
		info( "  Task Reports exported: " + counter.getProcessed());
		
		info( "  Task Reports rejected: " + counter.getRejected());
		
		info( "  Total Records handled: " + counter.getTotal());

		info( "*** Report Files created:" );
/*
		// Create Transaction
		try {
			String fileNames = "";
			
			List<String> lst = getWritersSet().getWriterNames();
			if ( lst != null && lst.size() > 0 ) {
				
				for ( String str : lst) {
					if ( str != null )
						fileNames = fileNames.concat( str + " " );
				}
			}
			
			Transaction transaction = new FileExportTransaction( 
			
					(( TmsApplication )UI.getCurrent()).getSessionData().getUser(),
					
					this.getClass().getSimpleName(), 
					
					fileNames,
					
					this.organisation, 
					errMsg );
			
			writeTransaction( transaction );
		} catch ( JAXBException e ) {
			logger.error( "Cannot convert to XML for transaction log (FileImport) " );
		}
*/
		return result;
	}

	
	protected String getExportDir() { return ""; }; 

	public static ProjectDataExportProcessor getTimingExportProcessor( Organisation org, LoggerIF processingLogger ){
		
		return getExportProcessor( ProcessorType.TIME_REPORTS, org, processingLogger );
	}
	
	public static ProjectDataExportProcessor getProjectsDataExportProcessor( Organisation org, LoggerIF processingLogger ){
		
		return getExportProcessor( ProcessorType.PROJECTS, org, processingLogger );
	}
	
	
	
	
	private static ProjectDataExportProcessor getExportProcessor( ProcessorType type, Organisation org, LoggerIF processingLogger ){
		Object obj = null;
		
		String processorClassName = ProjectDataExportProcessor.getProcessorClassName(type, org ); 
		
		try {
			obj = Class.forName( processorClassName ).newInstance();
			if ( obj == null ) {
				logger.error( "Instantiated class is NULL. Class: " + processorClassName );
				return null;
			}
			
			if ( !( obj instanceof ProjectDataExportProcessor )) {
				logger.error( "Wrong class was instantiated: " + obj.getClass().getName());
				return null;
			}
			
		} catch ( Exception e ) {
			logger.error( "Cannot instantiate class: " + processorClassName );
			return null;
		}

		(( ProjectDataExportProcessor )obj ).setLogger( processingLogger );
		
		
		return ( ProjectDataExportProcessor )obj;
	}

	public void setLogger( LoggerIF	processingLogger ) { this.processingLogger = processingLogger; }
	
	protected void info( String str ) {
		if ( processingLogger != null )
			processingLogger.info( str );
	}

	protected void error( String str ) {
		if ( processingLogger != null )
			processingLogger.error( str );
	}

	protected void writeTransaction( Transaction tr ) {
		tr = DataFacade.getInstance().insert( tr );

		if ( logger.isDebugEnabled()) logger.debug( tr );
	}
	

	private static String getProcessorClassName( ProcessorType type, Organisation org ){
		String res = null;
		
		switch ( type ) {
			case PEOPLES:
				res = org.getProperties().getProperty( "company.projects.export.peoplesdatahandler", "com.c2point.tms.tools.exprt.DefaultPersonnelExportProcessor" );
				break;
			case PROJECTS:
				res = org.getProperties().getProperty( "company.projects.export.projectsdatahandler", "com.c2point.tms.tools.exprt.projectdata.DefaultProjectsExportProcessor" );
				break;
			case TIME_REPORTS:
				res = org.getProperties().getProperty( "company.projects.export.datahandler", "com.c2point.tms.tools.exprt.DefaultTimingExportProcessor" );
				break;
			default:
				break;
		}
		return res;
	}
	
	private boolean isProjectScopeOk( Project prj, ScopeType scopeType ) {
		boolean res = false;

		if ( prj != null ) {
			switch ( scopeType ) {
				case ALL:
					// All projects are OK;
					res = true;
					break;
				case CLOSED:
					res = prj.isDeleted() || prj.getEndReal() != null && prj.getEndReal().before( new Date()); 
					break;
				case OPENED:
					res = !prj.isDeleted() && prj.getEndReal() == null; 
					break;
				default:
					break;
			
			}
			if ( !res ) counter.recordFilteredOut();
		} else {
			counter.recordRejected();
		}
		
		return res;
	}

	protected abstract void handleOneProjectTask( ProjectTask pt );

	public abstract File getResultFile();
}
