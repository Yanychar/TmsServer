package com.c2point.tms.resources;

import java.util.Date;
import java.util.List;

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

import com.c2point.tms.datalayer.CheckInOutFacade;
import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.stubs.orgmetadata.OrganisationMetadataStub_2;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.xml.XMLconverter;


@Path("/getmetadata2")
public class GetMetadataResource_2 extends BaseResource {
	private static Logger logger = LogManager.getLogger( GetMetadataResource_2.class.getName());
	
	
	@GET
	@Produces( MediaType.TEXT_XML )
	public String getMetaData(
			@DefaultValue("0") @QueryParam("sessionId") String sessionId, 
			@DefaultValue("true") @QueryParam("longform") boolean bLongForm, 
			@DefaultValue("") @QueryParam("day") String particularDay, // in format "ddmmyyyy" 
			@DefaultValue("0.0") @QueryParam("lat") float latitude,
			@DefaultValue("0.0") @QueryParam("long") float longitude,
			@DefaultValue("0.0") @QueryParam("accuracy") float accuracy,
			@DefaultValue("unknown") @QueryParam("provider") String provider
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start 'GetMetadata_2'...");
			// Show received parameters
			logger.debug( "  Request parameters: " 
							+ "sessionId='" + sessionId + "', "
							+ "longForm='" + bLongForm + "', "
							+ "lat='" + latitude +  "', "
							+ "long='" + longitude + "', "
							+ "accuracy='" + accuracy + "', " 
							+ "provider='" + provider + "';" 
						); 
		}
		
		
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
//			resp = OrganisationFacade.getInstance().getOrganisation( code ).getXmlPresentation_2( bLongForm );
			
			OrganisationMetadataStub_2 stub = new OrganisationMetadataStub_2( 
					OrganisationFacade.getInstance().getOrganisation( code ), 
					bLongForm
			);

			// If the day has been specified than checkin at this day shall be fetch as latest project
			// Otherwise checkins during today and yesterday will be specified as latest
			boolean oneDayOnly = false;
			Date dayToGet = null; 
			if ( particularDay != null && particularDay.length() > 0 ) {
				try {
					
					dayToGet = DateUtil.stringNoDelimToDate( particularDay );
					
/* Restriction for 14 days to see info */
					// Calculate date 14 days before today in milliseconds
					long tdms = DateUtil.getDate().getTime() - 1000 * 60 * 60 * 24 * 14;  // Minus 14 days

					// Check that dayToGet is after calculated date
					if ( dayToGet.getTime() >= tdms ) {

						oneDayOnly = true;

					}
					
					// Commented because of previous
//					oneDayOnly = true;
					
/*          .... end ....              */					
					
				} catch ( Exception e ) {
					logger.error( "Date passed is wrong: '" + particularDay + "'" );
				}
			}
			
			if ( oneDayOnly ) {

				// Add project that the user checked in this day
				addLatestProjects( account.getUser(), stub, dayToGet );
				
			} else {

				// Add Latest Projects
				addLatestProjects( account.getUser(), stub, 1 );
				
				// Add closest projects
				
			}
			
			
			
			
			
			resp = XMLconverter.convertToXML( stub );
			
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

	private void addLatestProjects( TmsUser user, OrganisationMetadataStub_2 stub, int days ) {
		addLatestProjects( user, stub, DateUtil.getDate(), days );
	}
	
	private void addLatestProjects( TmsUser user, OrganisationMetadataStub_2 stub, Date date ) {
		addLatestProjects( user, stub, date, 0 );
	}
	
	private void addLatestProjects( TmsUser user, OrganisationMetadataStub_2 stub, Date date, int days ) {

		List<Project> prjs = CheckInOutFacade.getInstance().getLatestCheckIns( user, date, days );
		
		if ( prjs != null && prjs.size() > 0 ) {
			for ( Project project : prjs ) {
				stub.addLatest( project );
			}
		}

	}
	
}
