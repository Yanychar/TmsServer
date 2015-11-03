package com.c2point.tms.tools.exprt;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UHR_DataExportProcessor extends DataExportProcessor {

	private static Logger logger = LogManager.getLogger( UHR_DataExportProcessor.class.getName());

	private static String fileNameBase = "erittely";
//	private BufferedWriter writerExport = null;

	private static String fileNameExt = "txt";

	@Override
	protected String getExportDir() { 
		return this.organisation.getProperties().getProperty( "company.reports.export" ); 
	} 
	
	@Override
	protected ExportValidator getRecordsWriter() {
		return new UHR_RecordsWriter();
	}

	@Override
	protected WritersSetIF getWritersSet() {
		
		File fDir = new File( getExportDir());
		if ( !fDir.exists()) {
			if ( fDir.mkdir()) {
				if ( logger.isDebugEnabled()) logger.debug( "Export directory had been created" );
			} else {
				logger.error( "Cannot create '" + exportDirectory + "' directory. Cannot proceed!" );
				error( "ERROR: Cannot create '" + exportDirectory + "' directory. Cannot proceed!" );
				return null;
			}
		}
		
		WritersSetIF writersSet = new FileWritersSet( getExportDir(), fileNameBase, fileNameExt );

		return writersSet;
	}


}
