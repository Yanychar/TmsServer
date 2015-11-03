package com.c2point.tms.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Path("/tools")
public class ToolsConfigurationResource {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ToolsConfigurationResource.class.getName());
	
	@Context HttpServletRequest req;		
		
	@GET
	public Response provisionTool(
			@DefaultValue("") @QueryParam("op") String operation, 
			@DefaultValue("") @QueryParam("p1") String param_1, 
			@DefaultValue("") @QueryParam("p2") String param_2 
		) {
		/*
		 *  Operations supported by Tools through rest interface:
		 *  - Import all
		 *  - Import for company
		 *  - ???
		 *  
		 */
/*		
		if ( operation.compareToIgnoreCase( "importall" ) == 0 ) {
			// 1. Import ALL Companies
			if ( logger.isDebugEnabled()) logger.debug( "Start to load companies data..." );
			if ( !new CompaniesDataImportProcessor().process()) {
				logger.error( "Failed to load one or more Companies data" );
				return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
			}
			if ( logger.isDebugEnabled()) logger.debug( "... companies data were loaded successfully" );
			
			// 2. Import personnel and create accounts if necessary FOR ALL Companies
			if ( logger.isDebugEnabled()) logger.debug( "Start to load 'Personnel' data for ALL Organisations" );
			if ( !new PersonDataImportProcessor().process()) {
				logger.error( "Failed to load 'Personnel' for one or more Organisations" );
				return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
			}
			if ( logger.isDebugEnabled()) logger.debug( "... 'Personnel' data were loaded successfully" );

			// 3. Import projects FOR ALL Companies
			if ( logger.isDebugEnabled()) logger.debug( "Start to load 'Project' data for ALL Organisations..." );
			if ( !new ProjectDataImportProcessor().process()) {
				logger.error( "Failed to load 'Projects' for one or more Organisations" );
				return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
			}
			if ( logger.isDebugEnabled()) logger.debug( "... 'Project' data were loaded successfully" );

		} else  if ( operation.compareToIgnoreCase( "importcompany" ) == 0 ) {
			// param_1 - Company Code
			if ( TmsConfiguration.getOrganisationProperties().get( param_1 ) == null ) {
				logger.error( "Wrong parameters" );
				return Response.status( Response.Status.BAD_REQUEST ).build();
			}

			// 1. Import Company
			if ( logger.isDebugEnabled()) logger.debug( "Start to load companies data..." );
			if ( !new CompaniesDataImportProcessor().process()) {
				logger.error( "Failed to load one or more Companies data" );
				return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
			}
			if ( logger.isDebugEnabled()) logger.debug( "... companies data were loaded successfully" );
			
			// 2. Import personnel and create accounts if necessary
			if ( logger.isDebugEnabled()) logger.debug( "Start to load 'Personnel' data for the Organisation with orgCode: '" + param_1 + "'..." );
			if ( !new PersonDataImportProcessor().process( param_1 )) {
				logger.error( "Failed to load 'Personnel' for the Organisation with orgCode: '" + param_1 + "'" );
				return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
			}
			if ( logger.isDebugEnabled()) logger.debug( "... 'Personnel' data were loaded successfully" );
			

//			if ( !new Tools().createAccounts( param_1 )) {
			
			
			
			// 3. Import projects
			if ( logger.isDebugEnabled()) logger.debug( "Start to load 'Project' data for the Organisation with orgCode: '" + param_1 + "'..." );
			if ( !new ProjectDataImportProcessor().process( param_1 )) {
				logger.error( "Failed to load 'Projects' for the Organisation with orgCode: '" + param_1 + "'" );
				return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
			}
			if ( logger.isDebugEnabled()) logger.debug( "... 'Project' data were loaded successfully" );
			
		} else  if ( operation.compareToIgnoreCase( "exportall" ) == 0 ) {
			// 1. Import ALL Companies
			if ( logger.isDebugEnabled()) logger.debug( "Start to load companies data..." );
			if ( !new CompaniesDataImportProcessor().process()) {
				logger.error( "Failed to load one or more Companies data" );
				return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
			}
			if ( logger.isDebugEnabled()) logger.debug( "... companies data were loaded successfully" );
			
			// 2. Export data
			if ( logger.isDebugEnabled()) logger.debug( "Start to export data for ALL Organisations ..." );
			if ( !new DataExportProcessor().process()) {
				logger.error( "Failed to export for ALL Organisations " );
				return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
			}
			if ( logger.isDebugEnabled()) logger.debug( "... data were exported successfully" );
		} else  if ( operation.compareToIgnoreCase( "exportcompany" ) == 0 ) {
			// param_1 - Company Code
//			if ( TmsConfiguration.getOrganisationProperties().get( param_1 ) == null ) {
				// 1. Import Company
				if ( logger.isDebugEnabled()) logger.debug( "Start to load companies data..." );
				if ( !new CompaniesDataImportProcessor().process()) {
					logger.error( "Failed to load one or more Companies data" );
					return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
				}
//			}

			if ( TmsConfiguration.getOrganisationProperties().get( param_1 ) == null ) {
				logger.error( "Wrong parameters" );
				return Response.status( Response.Status.BAD_REQUEST ).build();
			}

			
			// 2. Export data
			if ( logger.isDebugEnabled()) logger.debug( "Start to export data for the Organisation with orgCode: '" + param_1 + "'..." );
			if ( !new DataExportProcessor().process( param_1 )) {
				logger.error( "Failed to export for the Organisation with orgCode: '" + param_1 + "'" );
				return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
			}
			if ( logger.isDebugEnabled()) logger.debug( "... data were exported successfully" );
			
		} else {
			return Response.status( Response.Status.BAD_REQUEST ).build();
		}

		return Response.ok().build();
*/		
		return Response.status( Response.Status.SERVICE_UNAVAILABLE ).build();

	}
	
}
