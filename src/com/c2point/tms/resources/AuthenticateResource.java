package com.c2point.tms.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.AuthenticationFacade;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.stubs.AuthenticationStub;
import com.c2point.tms.util.xml.XMLconverter;


@Path("/authenticate")
public class AuthenticateResource {
	private static Logger logger = LogManager.getLogger( AuthenticateResource.class.getName());
	
	@Context HttpServletRequest req;		
		
	@GET
	@Produces( MediaType.TEXT_XML )
	public String authenticate(
			@DefaultValue("") @QueryParam("name") String usrname, 
			@DefaultValue("") @QueryParam("pwd") String pwd, 
			@DefaultValue("None") @QueryParam("hwc") String imei, 
			@DefaultValue("None") @QueryParam("av") String appVer
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start AuthenticateResource.authenticate()...");
			logger.debug( "  username: " + usrname );
			
		}

		TmsAccount account = AuthenticationFacade.getInstance().authenticateTmsUser( usrname, pwd, appVer, imei );

		if ( account == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because account not found");
				logger.debug( "... end AuthenticateResource.authenticate()");
			}
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}
		
		AuthResp resp = new AuthResp( account );
		if ( account != null && resp != null && resp.getAccount() != null ) {
			AuthenticationStub stub = new AuthenticationStub( resp.getAccount(), resp.getDate());
			String xmlStr;
			try {
				xmlStr = XMLconverter.convertToXML( stub );
				if ( logger.isDebugEnabled()) logger.debug( "***** Response authenticate: ****\n" + xmlStr );
			} catch (JAXBException e) {
				logger.error( "Failed to convert Authenticate: " + account + " Response to XML" );
				throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
			}

			if ( logger.isDebugEnabled()) logger.debug( "... end AuthenticateResource.authenticate()");
			
			return xmlStr;
		}
//		if ( logger.isDebugEnabled()) logger.debug( "Remote Host: " + ( req != null ? req.getRemoteHost() : "" ));
		logger.error( "Failed to convert Authenticate: " + account + " Response to XML" );
		logger.debug( "... end AuthenticateResource.authenticate()");
		
		throw new WebApplicationException( Response.Status.NOT_FOUND );
	}
	
}
