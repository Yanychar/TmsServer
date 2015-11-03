package com.c2point.tms.tools;

import javax.persistence.EntityManager;

import com.c2point.tms.entity.AbstractReport;

public interface RecordValidationIF {

	public EntityManager  getEntityManager();
	public void setEntityManager( EntityManager eMgr );
	
	public boolean preProcessRecord( AbstractReport report );
	public boolean processRecord( AbstractReport report );
	public boolean postProcessRecord( AbstractReport report );
	
	public long  getProcessed();
	public long  getRejected();
	public long  getFilteredOut();   // not included into Total
	public long  getTotal();     // processed+rejected
	
}
