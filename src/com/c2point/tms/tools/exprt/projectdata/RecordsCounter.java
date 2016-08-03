package com.c2point.tms.tools.exprt.projectdata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecordsCounter {
	private static Logger logger = LogManager.getLogger( RecordsCounter.class.getName());

	private long processed;
	private long rejected;
	private long filteredOut;

	public RecordsCounter() {
		clearCounts();
	}
	
	public long getProcessed() {
		if ( logger.isDebugEnabled()) logger.debug( "Number of processed records: " + processed );
		return processed; 
	}
	public long getRejected() { 
		if ( logger.isDebugEnabled()) logger.debug( "Number of rejected records: " + rejected );
		return rejected; 
	}
	public long getFilteredOut() { 
		if ( logger.isDebugEnabled()) logger.debug( "Number of FilteredOut records: " + filteredOut );
		return filteredOut; 
	}
	public long getTotal() { 
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
	
}
