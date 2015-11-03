package com.c2point.tms.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("test")
public class TestTMSRestInterface {

	private static Logger logger = LogManager.getLogger( TestTMSRestInterface.class.getName());
	
  // This method is called if TEXT_PLAIN is request
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String sayPlainTextTest() {
    return "TMS is ready for REST requests (TEXT)";
  }

  // This method is called if TEXT_XML is request
  @GET
  @Produces(MediaType.TEXT_XML)
  public String sayXmlTest() {
    return "<?xml version=\"1.0\"?>" + "<test>" + " TMS is ready for REST requests!(XML) " + "</test>";
  }

  // This method is called if XML is request
  // This method is called if HTML is request
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String sayHtmlTest() {
	  
	  logger.debug( "TMS is ready for REST requests" );
	  
    return "<html> " + "<title>" + "TMS is ready for REST requests (HTML)" + "</title>"
        + "<body><h1>" + "TMS is ready for REST requests (HTML)" + "</body></h1>" + "</html> ";
  }

}