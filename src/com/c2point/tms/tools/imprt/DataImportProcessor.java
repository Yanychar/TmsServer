package com.c2point.tms.tools.imprt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
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
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.util.DateUtil;
import com.vaadin.ui.UI;

public abstract class DataImportProcessor {
	private static Logger logger = LogManager.getLogger( DataImportProcessor.class.getName());

	protected enum ValidationResult {
		COMMENT,
		VALIDATED,
		FAILED
	};
	
	protected Organisation organisation;

	protected String importDirectory = null;
	protected String archiveDirectory = null;
	
	protected String origExt = null;
	protected String afterExt = null;
	protected String errExt = null;
	
	protected boolean renameAfter; 
	protected boolean moveAfter;
	protected boolean deleteAfter;
	
	protected LoggerIF	impLogger;
	
	public DataImportProcessor() {
		this( null );
	}
	
	public DataImportProcessor( LoggerIF importLogger ) {
		super();
		
		this.impLogger = importLogger;
		
	}
	
	public boolean process( Organisation organisation ) {
		boolean result = false;

		if ( organisation == null ) {
			logger.error( "Organisation cannot be null for data import!" );
			error( "ERROR: Organisation must be specified!" );
			return result;
		}
		this.organisation = organisation;
/*		
		Properties props = organisation.getProperties();
		if ( props == null ) {
			// use default values if properties were not found
			props = new Properties(); 
			logger.error( "Properties for Organisation '" + organisation.getName() + "' are undefined!" );
			error( "ERROR: Properties for Organisation '" + organisation.getName() + "' are undefined!" );
		}
*/
		// Read all properties		
		this.importDirectory = getImportDir();
		this.archiveDirectory = getArchiveDir();

		this.origExt = getOriginalExt();
		this.afterExt = getProcessedExt();
		this.errExt = getErrorExt();
		
		this.renameAfter = toRename(); 
		this.moveAfter = toMove();
		this.deleteAfter = toDelete(); // File processed successfully MUST be deleted anyway
		
		// Validate that import/export directory exists
		File ff;
		ff = new File( importDirectory );
		if ( !ff.exists()) {
			logger.error( "Directory '" + importDirectory + "' does not exist! No files to process." );
			error( "ERROR: No import directory found! No files to process" );
			return false;
		}
		
		// create archive directory if necessary
		if ( archiveDirectory != null && archiveDirectory.length() > 0 ) {
			if ( logger.isDebugEnabled()) logger.debug( "Validate that archive dir exist" );
			ff = new File( archiveDirectory );
			if ( !ff.exists()) {
				if ( ff.mkdirs()) {
					if ( logger.isDebugEnabled()) logger.debug( "Archive directory had been created" );
				} else {
					logger.error( "Cannot create '" + archiveDirectory + "' directory for archive. No archive files will be done" );
					error( "ERROR: Cannot create '" + archiveDirectory + "' directory for archive. No archive files will be done" );
					moveAfter = false;
				}
			} else {
				if ( logger.isDebugEnabled()) logger.debug( "Archive directory exists. Not necessary to create it" );
			}
		}
		
		// It is necessary to process all files from directory
		File dir = new File ( importDirectory );

		// This filter only returns original non processed files
		FilenameFilter fileFilter = new FilenameFilter() {
		    public boolean accept( File fl, String filename ){ 
		    	return filename != null && fl != null && filename.endsWith( "." + origExt );
		    }
		};

		File[] files = dir.listFiles( fileFilter );
		if ( files == null || files != null && files.length == 0 ) {
			if ( logger.isDebugEnabled()) logger.debug( "No files in Import directory! Nothing to process." );
			error( "Error: No files in Import directory! Nothing to process." );
			return false;
		}

		boolean tmpRes;
		
		result = true;
		for ( File inputFile : files ) {
			if ( logger.isDebugEnabled()) logger.debug( "Data file : '" + inputFile.getName() + "' ready for IMPORT. Will be processed");

			preProcessFile( inputFile );

			// Here file processor has been call
			tmpRes = processFile( inputFile );
			result = result && tmpRes;
					
			postProcessFile( inputFile, tmpRes );
				
		}
		
		
		return result;
	}

	protected abstract String getImportDir();
	protected abstract String getArchiveDir();

	protected String getOriginalExt() { return "txt"; }
	protected String getProcessedExt() { return "old"; }
	protected String getErrorExt() { return "err" ; }

	protected boolean toRename() { return true; } 
	protected boolean toMove() { return true; }
	protected boolean toDelete() { return false; }

	protected void writeTransaction( Transaction tr ) {
		tr = DataFacade.getInstance().insert( tr );

		if ( logger.isDebugEnabled()) logger.debug( tr );
	}
	
	protected File renameExt( File inputFile ) { return renameExt( inputFile, true ); }
	protected File renameExt( File inputFile, boolean withoutError ) {

		int dotPos = inputFile.getAbsolutePath().lastIndexOf(".");
		String ext = ( withoutError ? this.afterExt : this.errExt );

		String newName;
		if ( dotPos > 0 ) {
			newName = inputFile.getAbsolutePath().substring( 0, dotPos ) 
//					+ "_"
//					+ DateUtil.dateAndTimeToString()
					+ "." + ext;
		} else if ( dotPos == 0 ) {
			newName = "noname_"
					+ DateUtil.dateAndTimeToString()
					+ "." + ext;
		} else {
			newName = inputFile.getAbsolutePath() 
//					+ "_"
//					+ DateUtil.dateAndTimeToString()
					+ "." + ext;
		}
		
		File newFile = new File( newName );
		
		if ( inputFile.renameTo( newFile )) {
			if ( logger.isDebugEnabled()) logger.debug( "File '" + inputFile.getName() + "' was renamed to '" 
																 + newFile.getName() + "'" );
			return newFile;
		}
		
		logger.error( "Failed to rename file '" + inputFile.getName() + "' to '" + newFile.getName() + "'"); 
		return null;
	}
	protected File moveFile( File inputFile ) {

		// Destination directory
		File destDir = new File( this.archiveDirectory ); 
		// Move file to new directory
		File newFile = new File( destDir, inputFile.getName());
		if ( newFile.exists()) {
			deleteFile( newFile );
		}
		if ( inputFile.renameTo( newFile )) {
			if ( logger.isDebugEnabled()) logger.debug( "File '" + inputFile.getName() + "' was moved to the '" + this.archiveDirectory  + "'" );
			return newFile;
		}
		
		logger.error( "Failed to move file '" + inputFile.getName() + "' into the '" + this.archiveDirectory  + "'" ); 
		return null;
	}
	protected boolean deleteFile( File inputFile ) {
		if ( inputFile.delete()) {
			if ( logger.isDebugEnabled()) 
				logger.debug( "File '" + inputFile.getName() + "' was deleted" );
			return true;
		}
		logger.error( "Failed to delete file '" + inputFile.getName() + "'" );
		return false;
	}
	
	protected abstract ValidationResult validateLine( String [] nextLine, int lineNumber );
	
	protected abstract boolean processNextLine( String [] nextLine, int lineNumber );
	
	protected abstract void processComment( String [] nextLine, int lineNumber );
	
	protected boolean preProcessFile( File inputFile ) {
		boolean result = true;
		
		return result;
	}

	protected void postProcessFile( File inputFile, boolean res ) {
		if ( res ) {
			// Processing was successful
			// Rename and move file to archive if necessary
			File tmpFile;
			// rename if necessary
			if ( renameAfter ) {
				tmpFile = renameExt( inputFile );
				if ( tmpFile != null ) {
					inputFile = tmpFile;
				}
			}
			// move if necessary
			if ( moveAfter ) {
				moveFile( inputFile );
			} else {
				// delete if necessary
				if ( deleteAfter ) {
					deleteFile( inputFile );
				}
			}
		} else {
			// Processing failed
			// No delete
			// No move/archive
			// Add err extension
			if ( renameAfter ) {
				renameExt( inputFile, false );
			}
		}
	}
	
	public boolean processFile( File inputFile ) {
		boolean result = true;

		info( "*** Start to process file: " + inputFile.getName() + " ***" );
		
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
	
}
