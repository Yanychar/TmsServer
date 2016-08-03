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

import com.c2point.tms.datalayer.TaskReportFacade;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.stubs.taskreport.TaskReportStub;
import com.c2point.tms.entity.stubs.taskreport.TaskReportsListStub;
import com.c2point.tms.util.ConfigUtil;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.xml.XMLconverter;

@Path("/posttaskreports")
public class SaveTaskReportsResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( SaveTaskReportsResource.class.getName());

	@POST
	@Produces( MediaType.TEXT_XML )
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveTaskReports( 
			@DefaultValue("NOT FOUND") @QueryParam("sessionId") String sessionId, 
			JAXBElement<TaskReportsListStub> jaxbReport 
	) {

		if ( logger.isDebugEnabled()) logger.debug( "Start SaveTaskReportsResource.saveTaskReports()...");
		
		// Find out the user by session id
		TmsAccount account = findAccount( sessionId );
		
		TaskReportsListStub listStub = null;
		if ( jaxbReport.isNil()) {
			// JAXB failed to convert
			logger.error( "Received XML request body is nil (XML conversion problem)");
			return Response.status( Response.Status.BAD_REQUEST ).build();
		} else {
			listStub = jaxbReport.getValue();

			if ( listStub == null ) {
				logger.error( "Cannot convert XML request body into the TaskReport" );
				return Response.status( Response.Status.BAD_REQUEST ).build();
			}
		}
		if ( logger.isDebugEnabled()) {
			logger.debug( "*** TaskReport XML String:  ***" );
			try {
				logger.debug( XMLconverter.convertToXML( listStub ));
			} catch (JAXBException e ) {
				logger.error( e );
			}
			logger.debug( "*******************************" );
		}

		// Facade.saveReport
		TaskReport report = null;
		try {
			
			for ( TaskReportStub stub : listStub.getReports()) {
				if ( stub != null ) {

					report = TaskReportFacade.getInstance().
								convertStubToReport( account, listStub.getProject(), listStub.getDate(), stub );
					
					/* Restriction for 14 days to see info 
					 * 
					 *   If specified day is not so far than old way to calculqate
					 *   else pass empty List of reports and List of Tasks for conversion
					 * 
					 *    Here call to correctDate() method was added and method itself implemented to validate date and process valid date report only 
					 * 
					 * */
					
					if ( report != null && correctDate( report )) {

						report = TaskReportFacade.getInstance().saveTaskReport( report );
						
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
		if ( logger.isDebugEnabled()) logger.debug( "...end SaveTaskReportsResource.saveTaskReports()" );

		return Response.ok().build();
	}
	
	private boolean correctDate( TaskReport report ) {
		
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
