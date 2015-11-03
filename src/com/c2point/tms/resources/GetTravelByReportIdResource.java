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

import com.c2point.tms.datalayer.TravelReportFacade;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.stubs.travelreport.TravelReportStub;
import com.c2point.tms.util.xml.XMLconverter;

@Path("/getonetravelreport")
public class GetTravelByReportIdResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( GetTravelByReportIdResource.class.getName());

	@Context HttpServletRequest req;		

	@GET
	@Produces( MediaType.TEXT_XML )
	public String getTravelReport(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionId") String sessionId, 
			@DefaultValue("NOT_SPECIFIED") @QueryParam("reportId") String reportId 
		) {
		if ( logger.isDebugEnabled()) logger.debug( "Start 'GetOneTravelResource.getTravelReport'...");

		String xmlStr = null;
		
		// Find out the user by session id
//		TmsAccount account = findAccount( sessionId );

		// Get Reports created by TmsUser at specified date and belonged to specified Project 
		TravelReport report = TravelReportFacade.getInstance().getTravelReport( reportId );
		
		// Convert to xml
		try {
			TravelReportStub stub = new TravelReportStub( report );
			
			xmlStr = XMLconverter.convertToXML( stub );
		} catch ( JAXBException e ) {
			logger.error( "Failed to convert Travel Reports to XML" );
			logger.error( e );
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}
		return xmlStr;
	}
	
}
