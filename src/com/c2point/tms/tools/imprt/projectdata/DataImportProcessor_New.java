package com.c2point.tms.tools.imprt.projectdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.transactions.FileImportTransaction;
import com.c2point.tms.entity.transactions.Transaction;
import com.c2point.tms.tools.LoggerIF;
import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.ui.UI;

public abstract class DataImportProcessor_New {
	private static Logger logger = LogManager.getLogger( DataImportProcessor_New.class.getName());

	private Organisation organisation;
	private File file;

	protected LoggerIF	impLogger;
	
	protected enum ValidationResult {
		COMMENT,
		VALIDATED,
		FAILED
	};
	
	
	public DataImportProcessor_New( Organisation org ) {
		
		this.organisation = org;
		
	}

	public void setImportFile( File file ) {
		this.file = file;
	}
	
	public boolean process() {
		boolean bRes = false;
		
		if ( organisation == null ) {
			logger.error( "Organisation cannot be null for data import!" );
			return bRes;
		}
		
		if ( !file.exists()) {
			logger.error( "Import file does not exist! No files to process." );
			return bRes;
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "Data file : '" + file.getName() + "' ready for IMPORT. Will be processed");

		preProcessFile( file );

		// Here file processor has been call
		bRes = processFile( file );
				
		postProcessFile( file, bRes );
		
		
		
		return bRes;
	}
	
	protected boolean preProcessFile( File inputFile ) {
		boolean result = true;
		
		return result;
	}

	protected void postProcessFile( File inputFile, boolean res ) {
		if ( res && inputFile != null ) {
			// Processing was successful
			// Delete file file to archive if necessary
			inputFile.delete();
		}
	}

	protected abstract ValidationResult validateLine( String [] nextLine, int lineNumber );
	protected abstract boolean processNextLine( String [] nextLine, int lineNumber );
	protected abstract void processComment( String [] nextLine, int lineNumber );
	
	public boolean processFile( File inputFile ) {
		boolean result = true;

		logger.debug( "*** Start to process file: " + inputFile.getName() + " ***" );
		
		if ( logger.isDebugEnabled()) logger.debug( "Start to parse the file. Try to convert it into the List of TmsUser" );
		
		ImportProcessReader reader = new ImportProcessReader();
		if ( !reader.initReader( inputFile )) {
			error( "ERROR: Failed to process file: " + inputFile.getName());
			return false;
		}

		String [] nextLine;
		List< Integer > errLineLst = new ArrayList< Integer >();
		int i = 0;

		if ( logger.isDebugEnabled()) logger.debug( "  Start to traverse the file line by line......" );
		try {
			
			while (( nextLine = reader.readNextLine()) != null ) {
				i++;

				if ( logger.isDebugEnabled()) logger.debug( "    Line read and spilit into String [] ..." );
			    // nextLine[] is an array of values from the line

				// Firstly check that Line is not comment (start from #) and not empty)
				if ( nextLine[0].trim().length() != 0 && 
					 nextLine[0].trim().charAt( 0 ) == '#' ) {
					if ( logger.isDebugEnabled()) logger.debug( "Line #"+i+" is empty or commented out" );
					
					continue;
				}
				
				if ( logger.isDebugEnabled()) {
					logger.debug( "    Line # " + i + " length=" + nextLine.length ); 
					String outstr = "      nextLine []: ( ";
					for ( int j = 0; j < nextLine.length; j++ )
						outstr = outstr.concat( "'" + (( nextLine[ j ] != null ) ? nextLine[ j ] : "NULL" ) + "'  " );
					outstr = outstr.concat( ")" );
					logger.debug( outstr ); 
				}

				if ( validateLine( nextLine, i ) == ValidationResult.VALIDATED ) {
					// Line is valid. Process it
					if ( processNextLine( nextLine, i )) {
						// Line processed successully
					} else {
						// Cannot add to db
						errLineLst.add( i );
						result = false;
					}
				} else if ( validateLine( nextLine, i ) == ValidationResult.COMMENT ) {
					processComment( nextLine, i );
				} else {
					// Line is invalid. Save its number for error report
					errLineLst.add( i );
					result = false;
				}
				
			}
		} catch (IOException e) {
			if ( logger.isDebugEnabled()) logger.debug( "Faileds to read line="+i+" in import file: " + inputFile.getName() );

			error( "ERROR: I/O error. Line #" + i );
			
			errLineLst.add( i );
			result = false;
		}
		if ( logger.isDebugEnabled()) logger.debug( "  ... end file traversal" );
		
		reader.close();
		
		String errMsg = null;
		if ( result ) {
//			info( "*** Successfully processed without errors ***");
		} else {
			logger.error( "One or more lines were not imported from file: '" + inputFile.getName() + "'. Lines:" );
			errMsg = "";
			for ( Integer iRes : errLineLst ) {
				errMsg = errMsg.concat( "#" + iRes + " " );
			}
			logger.error( "  " + errMsg );

			error( "ERROR: *** Faileded to process lines: " + errMsg );
		}

		info( "*** ... File processing: " + ( result ? "SUCCEDED" :"FAILED" ) + " ***" );
		
		// Create Transaction
		try {
			Transaction transaction = new FileImportTransaction( 
			
					(( TmsApplication )UI.getCurrent()).getSessionData().getUser(),
					
					this.getClass().getSimpleName(), 
					inputFile, 
					this.organisation, 
					errMsg );
			
			writeTransaction( transaction );
		} catch ( JAXBException e ) {
			logger.error( "Cannot convert to XML for transaction log (FileImport) " );
		}
		
		return result;
	}

	class ImportProcessReader {

		private CSVReader csvReader = null;
		
		public boolean initReader( File inputFile, char delim ) {
			boolean result = false;

			try {
				if ( logger.isDebugEnabled()) logger.debug( "  Try to open file..." );
				csvReader = new CSVReader( new FileReader( inputFile ), delim );
				result = true;
			} catch (FileNotFoundException e) {
				logger.error( "Did not find specified file: " + inputFile.getName());
				return false;
			}
			
			return result;
		}
		public boolean initReader( File inputFile ) {
			return initReader( inputFile, ';' );
		}
		
	
		public String [] readNextLine() throws IOException {
			return csvReader.readNext();
		}

		public void close() {
			if ( csvReader != null ) {
				try {
					csvReader.close();
				} catch (IOException e) {
					logger.error( "Cannot close CSVReader properly" );
				}
				csvReader = null;
			}
		}
	}

	protected void info( String str ) {
		if ( impLogger != null )
			impLogger.info( str );
	}

	protected void error( String str ) {
		logger.error( str );
		if ( impLogger != null )
			impLogger.error( str );
	}
	
	protected void writeTransaction( Transaction tr ) {
		tr = DataFacade.getInstance().insert( tr );

		if ( logger.isDebugEnabled()) logger.debug( tr );
	}

	protected Organisation getOrg() { return this.organisation; }
	protected void setOrg( Organisation newOrg ) { this.organisation = newOrg; }
	
}
