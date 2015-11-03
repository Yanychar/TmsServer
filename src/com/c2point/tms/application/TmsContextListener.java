package com.c2point.tms.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TmsContextListener implements ServletContextListener {
	private static Logger logger = LogManager.getLogger( TmsContextListener.class.getName());
	
	private static EntityManagerFactory entityManagerFactory = null;

	public static EntityManagerFactory getEntityManagerFactory() {
		if ( entityManagerFactory == null ) {
			entityManagerFactory = Persistence.createEntityManagerFactory( "default", null );
		}
		if ( logger.isDebugEnabled()) logger.debug( "getEntityManagerFactory()" );
		return entityManagerFactory;
	}

	public static void setEntityManagerFactory( EntityManagerFactory emf ) {
		if ( entityManagerFactory != null && entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
			entityManagerFactory = null;
		}
		entityManagerFactory = emf;
		if ( logger.isDebugEnabled()) logger.debug( "setEntityManagerFactory(...)" );
	}

	@Override
	public void contextDestroyed( ServletContextEvent arg0 ) {
		if ( entityManagerFactory != null && entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
			entityManagerFactory = null;
		}
		logger.info( "Server closed" );
	}

	@Override
	public void contextInitialized( ServletContextEvent arg0 ) {
		logger.info( "TMS Server has been started!" );
	}

	
}
