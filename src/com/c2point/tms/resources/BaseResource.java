package com.c2point.tms.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.AuthenticationFacade;
import com.c2point.tms.entity.TmsAccount;

public class BaseResource {

	private static Logger logger = LogManager.getLogger( BaseResource.class.getName());

	@Context HttpServletRequest req;		

	protected TmsAccount findAccount( String sessionId ) throws WebApplicationException {
	
		if ( logger.isDebugEnabled()) logger.debug( " Find User Session for sessionId='" + sessionId + "'" );
		TmsAccount account = AuthenticationFacade.getInstance().findBySessionId( sessionId );
		
		// if not found return "NOT FOUND
		if ( account == null ) {
			if ( logger.isDebugEnabled()) logger.debug( " NOT FOUND sessionId='" + sessionId + "'" );
			// If not than resp = FAILED
			if ( logger.isDebugEnabled()) logger.debug( "...end 'GetReportsResource.getReports'. Response = UNAUTHORIZED" );
			throw new WebApplicationException( Response.Status.UNAUTHORIZED );
		}
		return account;
	}
}
