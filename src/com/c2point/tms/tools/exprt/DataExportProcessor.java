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
import com.c2point.tms.tools.imprt.LoggerIF;
import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.ui.UI;

public abstract class DataExportProcessor {
	private static Logger logger = LogManager.getLogger( DataExportProcessor.class.getName());

	protected Organisation organisation;

	protected String 	exportDirectory = null;
	
	protected WritersSetIF 	writersSet = null;
	protected LoggerIF		impLogger;
	
	public DataExportProcessor() {
		this( null );
	}
	
	public DataExportProcessor( LoggerIF importLogger ) {
		super();
		
		this.impLogger = importLogger;
		
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
		recordValidator.setLogger( impLogger );

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

	public static DataExportProcessor getExportProcessor( Organisation org ){
		return getExportProcessor( org, null );
	}
	
	public static DataExportProcessor getExportProcessor( Organisation org, LoggerIF importLogger ){
		
		return getExportProcessor( org.getProperties().getProperty( "company.projects.export.datahandler", "" ), importLogger );
	}
	
	private static DataExportProcessor getExportProcessor( String processorClassName, LoggerIF importLogger ){
		Object obj = null;
		
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

		(( DataExportProcessor )obj ).setLogger( importLogger );
		
		
		return ( DataExportProcessor )obj;
	}

	public void setLogger( LoggerIF	impLogger ) { this.impLogger = impLogger; }
	
	protected void info( String str ) {
		if ( impLogger != null )
			impLogger.info( str );
	}

	protected void error( String str ) {
		if ( impLogger != null )
			impLogger.error( str );
	}

	protected void writeTransaction( Transaction tr ) {
		tr = DataFacade.getInstance().insert( tr );

		if ( logger.isDebugEnabled()) logger.debug( tr );
	}
	

}
