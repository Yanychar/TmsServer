package com.c2point.tms.tools;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;

public abstract class AbstractReportsValidator implements RecordValidationIF {
	private static Logger logger = LogManager.getLogger( AbstractReportsValidator.class.getName());

	private long processed = 0;
	private long rejected = 0;
	private long filteredOut = 0;
	
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
	
	@Override
	public boolean preProcessRecord( AbstractReport report) {
		return true;
	}

	@Override
	public boolean postProcessRecord( AbstractReport report) {
		return true;
	}

	@Override
	public EntityManager getEntityManager() {
		return null;
	}

	@Override
	public void setEntityManager( EntityManager eMgr ) {
	}
	
}
