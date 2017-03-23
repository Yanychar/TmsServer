package com.c2point.tms.tools.exprt.projectdata;

import java.io.File;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;

public class DefaultProjectsExportProcessor extends ProjectDataExportProcessor {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( DefaultProjectsExportProcessor.class.getName());

	private CSV_ProjectFileWriter	writer;

	public DefaultProjectsExportProcessor() {
		super();

		writer = new CSV_ProjectFileWriter();
		writer.open();
		
	}

	public boolean process( Organisation organisation, ScopeType scopeType, FormatType format ) {
		boolean res;
		
		writer = new CSV_ProjectFileWriter();
		writer.open();
		
		res = super.process( organisation, scopeType, format );

		writer.close(); 
//		writer.delete(); 
		
		return res;
	}
	
	public boolean process( Collection<Project> lstPrj, ScopeType scopeType, FormatType format ) {
		boolean res;
		
		
		res = super.process( lstPrj, scopeType, format );

		writer.close(); 
		
		return res;
	}
	
	
	
	protected void handleOneProjectTask( ProjectTask pt ) {
		
		writer.write( pt ); 

		counter.recordProcessed();
		
	}

	@Override
	public File getResultFile() {

		if ( writer != null ) {
			
			return writer.getFile();
		}
		
		return null;
	}

}
