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

import com.c2point.tms.datalayer.TaskReportFacade;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.stubs.taskreport.TaskReportStub;
import com.c2point.tms.util.xml.XMLconverter;

@Path("/getonetaskreport")
public class GetTaskByReportIdResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( GetTaskByReportIdResource.class.getName());

	@Context HttpServletRequest req;		

	@GET
	@Produces( MediaType.TEXT_XML )
	public String getTaskReport(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionId") String sessionId, 
			@DefaultValue("NOT_SPECIFIED") @QueryParam("reportId") String reportId 
		) {
		if ( logger.isDebugEnabled()) logger.debug( "Start 'GetOneTaskResource.getTaskReport'...");

		String xmlStr = null;
		
		// Find out the user by session id
//		TmsAccount account = findAccount( sessionId );

		// Get Reports created by TmsUser at specified date and belonged to specified Project 
		TaskReport report = TaskReportFacade.getInstance().getTaskReport( reportId );
		
		// Convert to xml
		try {
			TaskReportStub stub = new TaskReportStub( report );
			
			xmlStr = XMLconverter.convertToXML( stub );
		} catch ( JAXBException e ) {
			logger.error( "Failed to convert Task Reports to XML" );
			logger.error( e );
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}
		return xmlStr;
	}
	
}
