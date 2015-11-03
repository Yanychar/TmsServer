package com.c2point.tms.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.CheckInOutFacade;
import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.entity.GeoCoordinates;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.stubs.checkinout.CheckInRespStub;
import com.c2point.tms.util.xml.XMLconverter;

@Path("/checkin")
public class CheckInResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( CheckInResource.class.getName()); 

	@POST
	@Produces( MediaType.TEXT_XML )
	public String checkin(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionId") String sessionId, 
			@DefaultValue("NOT_SPECIFIED") @QueryParam("projectId") String code,
			@DefaultValue("") @QueryParam("lat") String latitude,
			@DefaultValue("") @QueryParam("long") String longitude,
			@DefaultValue("0.0") @QueryParam("accuracy") String accuracy,
			@DefaultValue("unknown") @QueryParam("provider") String provider
			

	) {
		String xmlStr = null;
	
		if ( logger.isDebugEnabled()) {
			logger.debug( "Start 'CheckInresource.checkin'...");
			// Show received parameters
			logger.debug( "  Request parameters: " 
							+ "sessionId='" + sessionId + "', "
							+ "projectId='" + code + "', "
							+ "lat='" + latitude +  "', "
							+ "long='" + longitude + "', "
							+ "accuracy='" + accuracy + "', " 
							+ "provider='" + provider + "';" 
						); 
		}
		
		TmsAccount account = findAccount( sessionId );
	
		// if not found return "NOT FOUND
		if ( account == null ) {
			if ( logger.isDebugEnabled()) logger.debug( " NOT FOUND sessionId='" + sessionId + "'" );
			// If not than resp = FAILED
			if ( logger.isDebugEnabled()) logger.debug( "...end 'CheckInresource.checkin'. Response = UNAUTHORIZED" );
			throw new WebApplicationException( Response.Status.UNAUTHORIZED );
		}
		
		// Prepare Location information
		GeoCoordinates geoIn = convertToGeoFromStr( latitude, longitude, accuracy );
		
		// Perform real check-in
		boolean resp = CheckInOutFacade.getInstance().checkIn( account, code, geoIn );
		if ( resp ) {
			// Successfull. Prepare OK response!
			account = DataFacade.getInstance().find( TmsAccount.class, account.getId());
			CheckInRespStub stub = new CheckInRespStub( account );
			try {
				xmlStr = XMLconverter.convertToXML( stub );
			} catch (JAXBException e) {
				logger.error( "Failed to convert CheckIn Response to XML" );
				throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
			}
		} else {
			// If not than resp = FAILED
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		if ( logger.isDebugEnabled()) logger.debug( "...end 'CheckInresource.checkin'." );

		return xmlStr;
	}

	private GeoCoordinates convertToGeoFromStr( String latitude, String longitude, String accuracy ) {
		GeoCoordinates geoCoord = null; 
		
		if ( latitude == null || longitude == null || accuracy == null ) {
			// This is for guarantee only. Cannot be right now because @DefaultValue is used
			logger.error( "NULL Geo parameters passed" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		} else if ( latitude.length() == 0 && longitude.length() == 0 ) {
			// Location is empty. Is not used but this is valid
			// Nothing to do. Return NULL
			
		} else if ( latitude.length() == 0 || longitude.length() == 0 ) {
			// One parameter only is not valid combination
			logger.error( "One Geo parameter passed is null" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		} else {
			try {
				geoCoord = new GeoCoordinates( 
											Double.parseDouble( latitude ), 
											Double.parseDouble( longitude ),
											Double.parseDouble( accuracy ));

				if ( !geoCoord.isValid()) {
					logger.error( "Wrong Geo parameters passed" );
					throw new WebApplicationException( Response.Status.BAD_REQUEST );
				}
				
		   } catch ( NumberFormatException e ) {
				logger.error( "Wrong Geo parameters passed" );
				throw new WebApplicationException( Response.Status.BAD_REQUEST );
		   }
		}
		
		return geoCoord;
	}
	
}
