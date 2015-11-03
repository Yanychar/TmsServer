package com.c2point.tms.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.TmsAccount;


@Path("/getmetadata")
public class GetMetadataResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( GetMetadataResource.class.getName());
	
	
	@GET
	@Produces( MediaType.TEXT_XML )
	public String getMetaData(
			@DefaultValue("0") @QueryParam("sessionId") String sessionId, 
			@DefaultValue("true") @QueryParam("longform") boolean bLongForm 
		) {

		if ( logger.isDebugEnabled()) logger.debug( "Start 'GetMetadata'...");
		
		String resp = null;
	
		// Find out the user by session id
		TmsAccount account = findAccount( sessionId );
		
		String code;
		if ( account.getUser() != null && account.getUser().getOrganisation() != null ) { 
			code = account.getUser().getOrganisation().getCode();
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Session found but " + account + " does not belong to any Organisation" );
			if ( logger.isDebugEnabled()) logger.debug( "...end 'GetMetadata'. Response:" + Response.Status.UNAUTHORIZED );
			throw new WebApplicationException( Response.Status.UNAUTHORIZED );
		}
		
		try {
			resp = OrganisationFacade.getInstance().getOrganisation( code ).getXmlPresentation( bLongForm );
		} catch (JAXBException e) {
			logger.error( "Failed to convert OrganisationMetadataStub into XML" );
			resp = null;
		}
		if ( resp != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Responce.size=" + resp.getBytes().length );
			if ( logger.isDebugEnabled()) logger.debug( "...end 'GetMetadata'. Response:" + resp );
			return resp;
		} else {
			logger.error( "Cannot get Metadata for Organisation with code: '" + code + "'" );
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}
	
	}
	
	
}
