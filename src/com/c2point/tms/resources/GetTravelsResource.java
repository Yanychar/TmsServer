package com.c2point.tms.resources;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.stubs.travelreport.TravelReportsListOutStub;
import com.c2point.tms.util.ConfigUtil;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.xml.XMLconverter;

@Path("/gettravelreports")
public class GetTravelsResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( GetTravelsResource.class.getName());

	@Context HttpServletRequest req;		

	@GET
	@Produces( MediaType.TEXT_XML )
	public String getTravelReports(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionId") String sessionId, 
			@DefaultValue("NOT_SPECIFIED") @QueryParam("date") String dateStr,
			@DefaultValue( "" ) @QueryParam("code") String projectCode 
		) {
		if ( logger.isDebugEnabled()) {
			logger.debug( "***Start 'GetTravelsResource.getTravels'( " + dateStr + ", " + projectCode + " )..." );
		}

		String xmlStr = null;
		
		// Find out the user by session id
		TmsAccount account = findAccount( sessionId );

		Date date;
		try {
			date = DateUtil.stringNoDelimToDate( dateStr );
		} catch (ParseException e1) {
			logger.error( "Wrong Date String parameter passed: '" + dateStr + "'" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		if ( projectCode == null ) { //|| projectCode.length() == 0 ) {
			logger.error( "Wrong projectCode parameter passed" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		/* Restriction for 14 days to see info 
		 * 
		 *   If specified day is not so far than old way to calculate
		 *   else pass empty List of Travel Reports for the conversion
		 * 
		 * 
		 * 
		 * */
		List<TravelReport> trList;
		
		/* Restriction for days to see info */
		// Check how many days it is allowed to edit backward
		int allowedDays = ConfigUtil.getOrganisationIntProperty(
				account.getUser().getOrganisation(), 
				"company.projects.backward.period", 
				14 );
		
		// Calculate date before today in milliseconds
		long tdms = DateUtil.getDate().getTime() - 1000 * 60 * 60 * 24 * ( allowedDays - 1 );  // Minus 'allowedDays' days
		
		// Validate that date is OK
		if ( date.getTime() >= tdms ) {
			
			// If date is OK
			
			// Get Reports created by TmsUser at specified date  
			trList = TravelReportFacade.getInstance().getUserTravelReports( account.getUser(), date, projectCode );

		} else {
			
			// If date is outside the range
			trList = new ArrayList<TravelReport>();
		
		}
		
		/*          .... end ....              */					
		
		try {
			xmlStr = convertToXml( trList );
		} catch ( JAXBException e ) {
			logger.error( "Failed to convert List of Reports to XML" );
			logger.error( e );
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}
		if ( logger.isDebugEnabled()) logger.debug( "**** List converted to XML:\n" + xmlStr );
		
		if ( logger.isDebugEnabled()) logger.debug( "...end 'GetTravelsResource.getTravels'." );
		return xmlStr;
	}

	private String convertToXml( List<TravelReport> trList ) throws JAXBException {
		String xmlString = null;
		
		TravelReportsListOutStub stub = new TravelReportsListOutStub( trList );
		
		xmlString = XMLconverter.convertToXML( stub );
		
		return xmlString;
	}
	
}

