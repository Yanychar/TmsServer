package com.c2point.tms.tools.exprt.projectdata;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.Task;

public class CSV_ProjectFileWriter extends AbstractFileWriter {
	private static Logger logger = LogManager.getLogger( CSV_ProjectFileWriter.class.getName());

	private CSVWriter csvWriter;
	
	@Override
	public boolean open() {
		boolean res = false;
		
		try {
			csvWriter = new CSVWriter( new BufferedWriter( new FileWriter( getFile() )), ';', '"', CSVWriter.NO_ESCAPE_CHARACTER, EOL );
			res = true;
		} catch (IOException e) {
			logger.error( "ERROR: Cannot create CSVWriter. " + e.getMessage());
		}
		
		return res;
	}

	@Override
	public boolean close() {

		try {
			csvWriter.close();
		} catch (IOException e) {
			logger.error( "ERROR: Cannot close CSVWriter. " + e.getMessage());
		}		

		return false;
	}

	@Override
	public boolean write(Object object) {
		boolean res = false;

		if ( object instanceof ProjectTask ) {
			ProjectTask pt = ( ProjectTask )object;
			Project prj = pt.getProject();
			Task t = pt.getTask();

			String buffer [] = {
				prj.getCode(), 
				prj.getName(),
				prj.getProjectManager().getFirstAndLastNames(), 
				t.getCode(),
				t.getName(),
				pt.getCodeInProject()
			};
			
			csvWriter.writeNext( buffer );
			res = true;
			
		    if ( logger.isDebugEnabled()) 
		    	 logger.debug( pt.toString()); 
			
		}
		
		return res;
	}

}
