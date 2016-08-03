package com.c2point.tms.tools.exprt;

import java.io.Writer;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.tools.LoggerIF;
import com.c2point.tms.tools.RecordValidationIF;

public abstract class ExportValidator implements RecordValidationIF {
	private static Logger logger = LogManager.getLogger( ExportValidator.class.getName());

	private EntityManager eMgr = null;	

	protected 	WritersSetIF 	writersSet;
	protected 	LoggerIF		impLogger;
	
	private long processed = 0;
	private long rejected = 0;
	private long filteredOut = 0;

	public ExportValidator( WritersSetIF writersSet, EntityManager eMgr, LoggerIF	impLogger ) {
		
		setWritersSet( writersSet );
		setEntityManager( eMgr );
		setLogger( impLogger );
	}
	
	public ExportValidator( WritersSetIF writersSet, EntityManager eMgr ) {
		
		this( writersSet, null, null );
//		setWriter( writer );
//		setEntityManager( eMgr );
	}
	
	public ExportValidator( WritersSetIF writersSet ) {
		this( writersSet, null );
	}
	
	public ExportValidator() {
		this( null, null );
	}
	
	public EntityManager  getEntityManager() { return this.eMgr; }
	public void setEntityManager( EntityManager eMgr ) { this.eMgr = eMgr; }

	@Override
	public long  getProcessed() {
		if ( logger.isDebugEnabled()) logger.debug( "Number of processed records: " + processed );
		return processed; 
	}
	public long  getRejected() { 
		if ( logger.isDebugEnabled()) logger.debug( "Number of rejected records: " + rejected );
		return rejected; 
	}
	public long  getFilteredOut() { 
		if ( logger.isDebugEnabled()) logger.debug( "Number of FilteredOut records: " + filteredOut );
		return filteredOut; 
	}
	public long  getTotal() { 
		if ( logger.isDebugEnabled()) logger.debug( "Number of Total processed records: " + ( processed + rejected ));
		return processed + rejected; 
	}
	
	protected void clearCounts() {
		processed = 0;
		rejected = 0;
		filteredOut = 0;
	}
	
	protected void recordProcessed() {
		processed++;
	}
	protected void recordRejected() {
		rejected++;
	}
	
	protected void recordFilteredOut() {
		filteredOut++;
	}
	
//	public WritersSetIF getWritersSet() { return this.writersSet; }
	public Writer getWriter( String code ) { 
		if ( this.writersSet != null ) {
			
			return this.writersSet.getWriter( code );
		}
		
		return null; 
	}
	
	public void setWritersSet( WritersSetIF writersSet ) { this.writersSet = writersSet; }
	public void setLogger( LoggerIF	impLogger ) { this.impLogger = impLogger; }

	protected void info( String str ) {
		if ( impLogger != null )
			impLogger.info( str );
	}

	protected void error( String str ) {
		if ( impLogger != null )
			impLogger.error( str );
	}
	
}
