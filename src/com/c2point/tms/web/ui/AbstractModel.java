package com.c2point.tms.web.ui;

import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.access.SecurityContext;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;


public class AbstractModel  {
	
	private static Logger logger = LogManager.getLogger( AbstractModel.class.getName());

	protected TmsApplication 	app;
	
	protected EventListenerList	listenerList; 
	
	private SupportedFunctionType 	visibility;
	
	
	public AbstractModel() {
		listenerList = new EventListenerList();
		
	}

	public AbstractModel( TmsApplication app ) {
		this();
		this.app = app;
		
	}

	public TmsApplication getApp() {
		return app;
	}

	public void setApp( TmsApplication app ) {
		logger.debug( "setApp( app ). app = " + app  );
		this.app = app;
	}
	
	public TmsUser getSessionOwner() { return app.getSessionData().getUser(); }
	public SecurityContext getSecurityContext() { return app.getSessionData().getContext(); }
			
	public SupportedFunctionType getToShowFilter() {
		return visibility;
	}

	public void setToShowFilter( SupportedFunctionType visibility ) {
		this.visibility = visibility;
	}

}
