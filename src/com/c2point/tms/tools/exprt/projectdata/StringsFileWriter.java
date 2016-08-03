package com.c2point.tms.tools.exprt.projectdata;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringsFileWriter extends AbstractFileWriter {
	private static Logger logger = LogManager.getLogger( StringsFileWriter.class.getName());

	protected Writer	writer;

	public boolean open() {
		boolean res = false;
		
		file = getFile();
		
		try {

			writer = new BufferedWriter( new FileWriter( file ));
			// TRUE is everything had been created OK
			res = true;
			
		} catch ( Exception e ) {
			logger.error( "Cannot create FileWriter for the file: '" + file.getName() + "'." );
		}
		
		return res;
	}
	
	public boolean close() {
		boolean res = false;
		if ( writer != null ) {
			
			// Close if necessary
			try {
				writer.flush();
				writer.close();
				
				res = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error( "Cannot close Writer for the file: '" + file.getName() + "'. " + e.getMessage());
			}
			writer = null;
			
		}
		
		return res;
	}

	public boolean write( Object obj ) {
		boolean res = false; 
		
		if ( obj != null ) {

			try {
				writer.write( obj.toString());
				// EOL  
				writer.write( EOL );

			} catch (IOException e) {
				
				logger.error( "Cannot write into the file '" + fileName + "': " + e.getMessage());
			}
			
		}

		return res;
	}


}
