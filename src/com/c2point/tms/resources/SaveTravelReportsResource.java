package com.c2point.tms.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.TravelReportFacade;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.stubs.travelreport.TravelReportStub;
import com.c2point.tms.entity.stubs.travelreport.TravelReportsListStub;
import com.c2point.tms.util.ConfigUtil;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.xml.XMLconverter;

@Path("/posttravelreports")
public class SaveTravelReportsResource extends BaseResource {
	
	private static Logger logger = LogManager.getLogger( SaveTravelReportsResource.class.getName());
	
	@POST
	@Produces( MediaType.TEXT_XML )
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveTravelReports( 
			@DefaultValue("NOT FOUND") @QueryParam("sessionId") String sessionId, 
			JAXBElement<TravelReportsListStub> jaxbReport 
	) {

		if ( logger.isDebugEnabled()) logger.debug( "Start SaveTravelReportsResource.saveTravelReports()...");
		
		// Find out the user by session id
		TmsAccount account = findAccount( sessionId );
		if ( logger.isDebugEnabled()) logger.debug( "  Account found: " + account.getUser().getFirstAndLastNames());
		
		TravelReportsListStub listStub = null;
		if ( jaxbReport.isNil()) {
			// JAXB failed to convert
			logger.error( "  Received XML request body is nil (XML conversion problem)");
			return Response.status( Response.Status.BAD_REQUEST ).build();
		} else {
			listStub = jaxbReport.getValue();
			if ( logger.isDebugEnabled()) {
				try {
					logger.debug( "****  Request body received from client:\n" + XMLconverter.convertToXML( listStub ));
					logger.debug( "****  ... end of request body" );
				} catch (JAXBException e) {
					logger.error( e );
				}
			}
			if ( listStub == null ) {
				logger.error( "Cannot convert XML request body into the TravelReport" );
				return Response.status( Response.Status.BAD_REQUEST ).build();
			}
		}

		// Validate Project Code != null. New!
		if ( listStub.getProject() == null || listStub.getProject().length() == 0 ) {
			logger.error( "Project Code is not specified! Travel Reports cannot be saved for User: " 
							+ account.getUser().getFirstAndLastNames());
			
			return Response.status( Response.Status.BAD_REQUEST ).build();
		}
		
		// Facade.saveReport
		TravelReport report = null;

		if ( logger.isDebugEnabled()) {
			try {
				logger.debug( "GetStub from list...");
				listStub.getReports().get(0);
			} catch ( Exception e ) {
				logger.error( "Cannot getStub from list\n" + e.getMessage());
			}
			
		}
		
		
		try {
			
			
			for ( TravelReportStub stub : listStub.getReports()) {
				if ( stub != null ) {

					if ( logger.isDebugEnabled()) {
						logger.debug( "Convert TravelReportStub ..." );
					}
					report = TravelReportFacade.getInstance().
								convertStubToReport( account, listStub.getProject(), /* listStub.getDate(), */stub );
					if ( logger.isDebugEnabled()) {
						logger.debug( "TravelReportStub converted!" );
					}
					
					/* Restriction for 14 days to see info 
					 * 
					 *   If specified day is not so far than old way to calculqate
					 *   else pass empty List of reports and List of Tasks for conversion
					 * 
					 *    Here call to correctDate() method was added and method itself implemented to validate date and process valid date report only 
					 * 
					 * */
					
					if ( report != null && correctDate( report )) {

						report = TravelReportFacade.getInstance().saveTravelReport( report );
						
					}
					
					/*          .... end ....              */					
					
				}
			}
			
		} catch (Exception e) {
			logger.error( e.getMessage());
			return Response.status( Response.Status.BAD_REQUEST ).build();
		}
/*
		if ( report == null ) {
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
		}
*/
		if ( logger.isDebugEnabled()) logger.debug( "...end SaveTravelReportsResource.saveTravelReports() successfully" );

		return Response.ok().build();
	}
	
	private boolean correctDate( TravelReport report ) {
		
		boolean bRes = false;
		
		if ( report != null ) {
			
			/* Restriction for days to see info */
			// Check how many days it is allowed to edit backward
			int allowedDays = ConfigUtil.getOrganisationIntProperty(
					report.getUser().getOrganisation(), 
					"company.projects.backward.period", 
					14 );
			
			// Calculate date before today in milliseconds
			long tdms = DateUtil.getDate().getTime() - 1000 * 60 * 60 * 24 * ( allowedDays - 1 );  // Minus 'allowedDays' days

			// Validate that date is OK
			bRes = ( report.getDate().getTime() >= tdms );
			
			
		}
		
		return bRes;
	}
	
}
