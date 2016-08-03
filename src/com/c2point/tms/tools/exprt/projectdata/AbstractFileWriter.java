package com.c2point.tms.tools.exprt.projectdata;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractFileWriter implements FileWriterIF {
	private static Logger logger = LogManager.getLogger( AbstractFileWriter.class.getName());

	protected String	fileName;
	protected File		file;

	protected String 	EOL = "\r\n";
	
	protected AbstractFileWriter() {
		this( null );
		
	}
	
	protected AbstractFileWriter( String fileName ) {
		
		this.fileName = fileName;
		
	}
	
	public abstract boolean open();
	public abstract boolean close();
	public abstract boolean write( Object object );

	public boolean delete() {
		boolean res = false;
		try {
			res = getFile().delete();
			if ( !res ) {
				logger.error( "No file found: " + this.fileName );
			}
			
		} catch (Exception e ) {
			logger.error( "Cannot delete file: " + e.getMessage());
		}
		
		return res;
	};
	
	/* Implementation helpers */
	public File getFile() {

		if ( this.file == null ) {
			// Create file
			try {
				if ( StringUtils.isBlank( fileName )) {
					
					// Create temporal file if  no name was given 
					this.file = File.createTempFile( "tmp_", ".csv" );
				} else {
					this.file = new File( fileName );
					// Create new file if old one exist
					if ( this.file.exists()) {
						if ( this.file.delete()) {
							this.file.createNewFile();
						}
					}
						
				}
				
				
				// Put full path with filename
				this.fileName = this.file.getCanonicalPath();
				logger.debug( "Export file: '" + this.file.getName() + "'" );
				
			} catch ( Exception e ) {
				logger.error( "Cannot create File : '" + this.fileName + "'." );
				this.file = null;
				this.fileName = null;
			}
		}
		
		return this.file;
	}
	
}
