package com.c2point.tms.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.AuthenticationFacade;
import com.c2point.tms.entity.TmsAccount;

@Path("/logout")
public class LogoutResource {
	private static Logger logger = LogManager.getLogger( LogoutResource.class.getName());

	@Context HttpServletRequest req;		

	@GET
	public Response logout(
			@DefaultValue("NOT FOUND") @QueryParam("sessionId") String sessionId 
	) {
		if ( logger.isDebugEnabled()) logger.debug( "Start LogoutResource.logout() ..." );
		
		// Find out the user by session id
		if ( logger.isDebugEnabled()) logger.debug( " Find User for sessionId='" + sessionId + "'" );
		TmsAccount account = AuthenticationFacade.getInstance().findBySessionId( sessionId );
		// if not found return "NOT FOUND
		if ( account == null ) {
			if ( logger.isDebugEnabled()) logger.debug( " UNAUTHORIZED session with sessionId='" + sessionId + "'" );
			if ( logger.isDebugEnabled()) logger.debug( "... end LogoutResource.logout()" );
			return Response.status( Response.Status.UNAUTHORIZED ).build();
		}

		boolean bRes = AuthenticationFacade.getInstance().logout( account, false );
		if ( !bRes ) {
			if ( logger.isDebugEnabled()) logger.debug( "... INTERNAL_SERVER_ERROR. end LogoutResource.logout()" );
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
		}

		if ( logger.isDebugEnabled()) logger.debug( "... end LogoutResource.logout()" );

		return Response.ok().build();
	
	}
	
}
