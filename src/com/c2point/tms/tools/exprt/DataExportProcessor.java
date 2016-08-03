package com.c2point.tms.tools.exprt;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.TaskReportFacade;
import com.c2point.tms.datalayer.TravelReportFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.transactions.FileExportTransaction;
import com.c2point.tms.entity.transactions.Transaction;
import com.c2point.tms.tools.LoggerIF;
import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.ui.UI;

public abstract class DataExportProcessor {
	private static Logger logger = LogManager.getLogger( DataExportProcessor.class.getName());

	private enum ProcessorType{ TIME_REPORTS, PROJECTS, PEOPLES }; 
	
	protected Organisation organisation;

	protected String 	exportDirectory = null;
	
	protected WritersSetIF 	writersSet = null;
	protected LoggerIF		processingLogger;
	
	public DataExportProcessor() {
		this( null );
	}
	
	public DataExportProcessor( LoggerIF exportLogger ) {
		super();
		
		this.processingLogger = exportLogger;
		
	}
	
	public boolean process( Organisation organisation, Date startDate, Date endDate ) {
		boolean result = false;

		String errMsg = null;

		if ( organisation == null ) {
			logger.error( "Organisation cannot be null for data export!" );

			errMsg = "ERROR: Organisation must be specified!";
			error( errMsg );

			return result;
		}
		
		this.organisation = organisation;
		
		// Read all properties		
		this.exportDirectory = getExportDir();

		// Validate that import/export directory exists
		File ff;
		ff = new File( exportDirectory );
		if ( !ff.exists()) {
			logger.error( "Directory '" + exportDirectory + "' does not exist! No data to process." );
			
			errMsg = "ERROR: No export directory found! No files to process";
			error( errMsg );
			
			return result;
		}
		
		writersSet = getWritersSet();
		
		ExportValidator recordValidator = getRecordsWriter();
		
		if ( recordValidator == null ) {
			logger.error( "Cannot instantiate Record Validator!" );
			
			errMsg = "ERROR: Export Data handler not found!";
			error( errMsg );

			return result;
		}
		
		recordValidator.setWritersSet( writersSet );
		recordValidator.setLogger( processingLogger );

		logger.info( "Writer for ExportFileWriterhas been set successfully" ); 

/* ... end of Possible usage of reflection */

		info( "*** Start data export ... ***" );
		
		
		result = TaskReportFacade.getInstance().traverseTaskReports( this.organisation, startDate, endDate, recordValidator );

		long taskProcessed = recordValidator.getProcessed();
		long taskRejected = recordValidator.getRejected();
		
		result = TravelReportFacade.getInstance().traverseTravelReports( this.organisation, startDate, endDate, recordValidator ) && result;
		
		total = recordValidator.getTotal(); 

		info( "*** End data export: ***" + ( result ? "Success" : "FAILED") );
		
		info( "  Task Reports exported: " + taskProcessed );
		info( "  Travel Reports exported: " + ( recordValidator.getProcessed() - taskProcessed ));
		
		info( "  Task Reports rejected: " + taskRejected );
		info( "  Travel Reports rejected: " + ( recordValidator.getRejected() - taskRejected ));
		
		info( "  Total Records handled: " + recordValidator.getTotal());

		info( "*** Report Files created:" );
		for ( String name : writersSet.getWriterNames()) {
			info( "  " + name );
		}
		

		writersSet.close();

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
		
		return result;
	}

	private long total = 0;
	public long getTotal() { return total; }
	
	protected abstract String getExportDir(); 
	protected abstract ExportValidator getRecordsWriter();
	protected abstract WritersSetIF getWritersSet();

	public static DataExportProcessor getTimingExportProcessor( Organisation org, LoggerIF processingLogger ){
		
		return getExportProcessor( ProcessorType.TIME_REPORTS, org, processingLogger );
	}
	
	public static DataExportProcessor getProjectsDataExportProcessor( Organisation org, LoggerIF processingLogger ){
		
		return getExportProcessor( ProcessorType.PROJECTS, org, processingLogger );
	}
	
	
	
	
	private static DataExportProcessor getExportProcessor( ProcessorType type, Organisation org, LoggerIF processingLogger ){
		Object obj = null;
		
		String processorClassName = DataExportProcessor.getProcessorClassName(type, org ); 
		
		try {
			obj = Class.forName( processorClassName ).newInstance();
			if ( obj == null ) {
				logger.error( "Instantiated class is NULL. Class: " + processorClassName );
				return null;
			}
			
			if ( !( obj instanceof DataExportProcessor )) {
				logger.error( "Wrong class was instantiated: " + obj.getClass().getName());
				return null;
			}
			
		} catch ( Exception e ) {
			logger.error( "Cannot instantiate class: " + processorClassName );
			return null;
		}

		(( DataExportProcessor )obj ).setLogger( processingLogger );
		
		
		return ( DataExportProcessor )obj;
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
				res = org.getProperties().getProperty( "company.projects.export.projectsdatahandler", "com.c2point.tms.tools.exprt.DefaultProjectsExportProcessor" );
				break;
			case TIME_REPORTS:
				res = org.getProperties().getProperty( "company.projects.export.datahandler", "com.c2point.tms.tools.exprt.DefaultTimingExportProcessor" );
				break;
			default:
				break;
		}
		return res;
	}
	
	
}
