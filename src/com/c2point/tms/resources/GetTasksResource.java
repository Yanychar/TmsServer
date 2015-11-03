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

import com.c2point.tms.datalayer.TaskReportFacade;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.stubs.taskreport.TaskReportsListStub;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.xml.XMLconverter;

@Path("/gettaskreports")
public class GetTasksResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( GetTasksResource.class.getName());

	@Context HttpServletRequest req;		

	@GET
	@Produces( MediaType.TEXT_XML )
	public String getTaskReports(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionId") String sessionId, 
			@DefaultValue("NOT_SPECIFIED") @QueryParam("date") String dateStr ,
			/*@DefaultValue("") */@QueryParam("code") String projectCode 
		) {
		if ( logger.isDebugEnabled()) logger.debug( "Start 'GetTasksResource.getTasks'...");

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

		if ( projectCode == null || projectCode.length() == 0 ) {
			logger.error( "Wrong projectCode parameter passed" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		/* Restriction for 14 days to see info 
		 * 
		 *   If specified day is not so far than old way to calculqate
		 *   else pass empty List of reports and List of Tasks for conversion
		 * 
		 * 
		 * 
		 * */
		List<TaskReport> trList;
		List<ProjectTask> ptList;

		// Calculate date
		// Calculate date 14 days before today in milliseconds
		long tdms = DateUtil.getDate().getTime() - 1000 * 60 * 60 * 24 * 14;  // Minus 14 days
		
		// Validate that date is OK
		if ( date.getTime() >= tdms ) {
			
			// If date is OK
			
			// Get Reports created by TmsUser at specified date and belonged to specified Project 
			trList = TaskReportFacade.getInstance().getUserTaskReports( account.getUser(), date, projectCode );
		
			// Add other Tasks assigned to the project but not reported by user
			ptList = TaskReportFacade.getInstance().getPossibleTasks( account, projectCode );

		} else {
			// If date is outside the range
			trList = new ArrayList<TaskReport>();
		
			// Add other Tasks assigned to the project but not reported by user
			ptList = new ArrayList<ProjectTask>();
			
		}
		
		/*          .... end ....              */					
		
		try {
			xmlStr = convertToXml( dateStr , projectCode, trList, ptList );
		} catch ( JAXBException e ) {
			logger.error( "Failed to convert List of Reports to XML" );
			logger.error( e );
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		if ( logger.isDebugEnabled()) logger.debug( xmlStr );
		
		if ( logger.isDebugEnabled()) logger.debug( "...end 'GetTasksResource.getTasks'." );
		return xmlStr;
	}

	private String convertToXml( String dateStr , String projectCode, List<TaskReport> trList, List<ProjectTask> ptList ) throws JAXBException {
		String xmlString = null;
		
		TaskReportsListStub stub = new TaskReportsListStub( trList );
		// Add ProjCode and date from incoming request (asked by Alexei)
		stub.setProject( projectCode );
		stub.setDate( dateStr );
		stub.addAvailableProjectTasks( ptList );
		
		xmlString = XMLconverter.convertToXML( stub );
		
		return xmlString;
	}
	
}
