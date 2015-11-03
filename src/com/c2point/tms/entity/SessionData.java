package com.c2point.tms.entity;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SecurityContext;

public class SessionData {
	private static Logger logger = LogManager.getLogger( SessionData.class.getName());

	private TmsUser sessionUser;
	
	private Locale	locale;
	
	private ResourceBundle	bundle;
	
	public SessionData() {
		this.sessionUser = null;
	}
	
	public void setUser( TmsUser user ) {
		logger.debug( "Save User in SessionData " + user );
		this.sessionUser = user;
	}

	public TmsUser getUser() {
		return sessionUser;
	}

	public SecurityContext getContext() {
		return ( sessionUser != null ? sessionUser.getContext() : null );
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale( Locale locale ) {
		this.locale = locale;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle( ResourceBundle bundle ) {
		this.bundle = bundle;
	}



}
