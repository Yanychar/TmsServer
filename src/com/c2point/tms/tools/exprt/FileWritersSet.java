package com.c2point.tms.tools.exprt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("serial")
public class FileWritersSet extends HashMap< String, FileWritersSet.FileWriterHolder > 
								implements WritersSetIF {

	private static Logger logger = LogManager.getLogger( FileWritersSet.class.getName());
	
	private String exportDirectory = null;
	private String baseFileName = null;
	private String extension = null;
	
	private static long counter = new Date().getTime() /1000/60/60;  // Minutes from Jan 1, 1970;
	
	public FileWritersSet( String exportDirectory, String baseFileName, String extension ) {
		
		this.exportDirectory = exportDirectory;
		this.baseFileName = baseFileName;
		this.extension = extension;
		
	}
	
	public Writer getWriter( String code ) {
		
		if ( !this.containsKey( code )) {
			
			// Create writer
			File file = createFile( code );
			
			Writer writer = null;
			try {
				writer = new BufferedWriter( new FileWriter( file ));
			} catch ( Exception e ) {
				logger.error( "Cannot create Writer to export file '" + file.getName() + "'. Failed to create export file!" );
				return null;
			}
			// Put into the map
			
			this.put( code, new FileWriterHolder( writer, file ));
		}
		
		return this.get( code ).getWriter();
	}

	public Writer getWriter() {
		if ( this.size() > 1 ) {
			logger.error( "More than one writer exists. Use Writer code to fetch!" );
			return null;
		} else if ( this.size() == 0 ) {
			return getWriter( "" );
		}

		return this.values().iterator().next().getWriter();
	}

	public File getFile( String code ) {
		
		return ( this.get( code ) != null ? this.get( code ).getFile() : null );
	}

	public void close( String code ) {
		try {
			Writer writer = this.get( code ).getWriter();
			writer.close();
		} catch (IOException e) {
			logger.error( "Cannot close writer with code '"+code+"'\n" + e );
		}
	}
	
	public void close() {
		
		// Close all Writers
		for( FileWriterHolder writerHolder : this.values()) {
			if ( writerHolder != null && writerHolder.getWriter() != null ) {
				try {
					writerHolder.getWriter().close();
				} catch (IOException e) {
					logger.error( "Cannot close writer\n" + e );
				}
			}
		}
		this.clear();
		
	}
	
	public String getWriterName( String code ) {
		return this.get( code ).getFile().getName();
	}

	public List< String > getWriterNames() {

		List< String > list = new ArrayList< String >( this.size());
		
		for( FileWriterHolder writerHolder : this.values()) {
			if ( writerHolder != null && writerHolder.getFile() != null ) {
				list.add( writerHolder.getFile().getName());
			}
		}
		
		return list;
		
	}

	
	
	private String createFileName( String code ) {
		
		// Create file name
		// ... base of the name
		String fileName = exportDirectory + File.separator + baseFileName + code;
		// ... add date
//		fileName = fileName.concat( "_" + DateUtil.dateNoDelimToString());
		// ... add unique counter
		FileWritersSet.counter++;
//		fileName = fileName.concat( "_" + StringUtils.padLeftZero( FileWritersSet.counter, 5 ));  
		fileName = fileName.concat( "_" + counter );  

		// ... add extension
		fileName = fileName.concat( "." + extension );  
		
		return fileName;
	}
	
	private File createFile( String code ) {
		
		// Create file and falidate it's uniques
		File file;
		
		int i = 0;
		
		do {
			file = new File( createFileName( code ));
			i++;
			
			try {
				if ( !file.createNewFile()) {
					file = null;
				}
			} catch (IOException e) {
				logger.debug( "Cannot create file '" + file.getName() + "'" );
			}
			
        } while ( file == null && i < 1000 );

		if ( file != null ) {
			logger.debug( "Export will be written into export file: " + file.getName());
		} else {
			// Failed to create unique file
			logger.error( "Cannot create export file '" 
						+ exportDirectory + File.separator + baseFileName + code 
						+ "'. Failed to create export file!" );
		}
		
		return file;
	}

	class FileWriterHolder {
		
		private Writer 	writer; 
		private File 	file;
		
		FileWriterHolder( Writer writer, File file ) {
			this.writer = writer;
			this.file = file;
		}
		
		Writer 	getWriter() { return writer; }
		File 	getFile() { return file; }
		
	}

}

