package com.c2point.tms.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.c2point.tms.entity.Organisation;
import com.c2point.tms.util.ConfigUtil;
import com.vaadin.server.VaadinService;

public class TmsConfiguration {

	private static Logger logger = LogManager.getLogger( TmsConfiguration.class.getName());

	private static Properties properties = null;
//	private static TmsApplication application = null;

	private static Map<String, Properties> orgganisationsMap = new HashMap<String, Properties>();
	
	public static boolean readConfiguration() { // TmsApplication app ) {
		boolean bRes = true;
		Properties locProp;

		if ( logger.isDebugEnabled()) logger.debug( "Start to 'read TMS configuration' ..." );
		
//		TmsConfiguration.application = app;
		
		
		// 1. Read and setup if necessary config directory
		// Read tms.properties file
		locProp = new Properties();

		try {
			System.setProperty( "tms.config.dir", 
					VaadinService.getCurrent().getBaseDirectory()
				  + File.separator + "config" );

			logger.info( "TMS main config file: '" + System.getProperty( "tms.config.dir" ) + File.separator + "tms.properties" + "'" );
			
			locProp.load( new FileInputStream( System.getProperty( "tms.config.dir" ) + File.separator + "tms.properties" ));
			
			if ( logger.isDebugEnabled()) logger.debug( "tms.properties was read successfully" );

			addProperties( locProp );
			
		} catch ( Exception e) {
			logger.error( "tms.properties was NOT read successfully!!!\n" + e  );
		}
/*	
		System.setProperty( "tms.companies.dir",
				System.getProperty( "tms.config.dir", "config" ) 
				+ File.separator 
				+ locProp.getProperty( "tms.companies.dir" )
		);   
*/
//		logger.info( "Companies properties path: '" + System.getProperty( "tms.companies.dir" ) + "'" );
		
		// Read Local OS specific file
		locProp = new Properties();

		try {
			logger.info( "Locale property file: '" + System.getProperty( "tms.config.dir" ) + File.separator + ConfigUtil.getLocalePropertiesFile() + "'" );
		
			locProp.load( new FileInputStream( System.getProperty( "tms.config.dir" ) + File.separator + ConfigUtil.getLocalePropertiesFile()));
			
			if ( logger.isDebugEnabled()) logger.debug( "locale_???.properties was read successfully" );
		} catch ( Exception e) {
			logger.error( "locale_???.properties was NOT read successfully!!!" + "\n" + e );
		}

		addProperties( locProp );

		if ( logger.isDebugEnabled()) logger.debug( "... end of 'read TMS configuration'" );

		return bRes;
	}

	public static boolean readConfigurationTest() {
		boolean bRes = true;

		if ( logger.isDebugEnabled()) logger.debug( "Start to 'read TMS configuration' ..." );

		
		
		// 1. Read and setup if necessary config directory and company dir
		System.setProperty( "tms.config.dir", "C:\\Users\\sevastia\\workspace_tms\\TMS Vaadin 7\\config" );

		// Read tms.properties file
		addProperties( ConfigUtil.getConfigDir() + File.separator + "tms.properties" );

		// Read Local OS specific file
		addProperties( ConfigUtil.getConfigDir() + File.separator + ConfigUtil.getLocalePropertiesFile() );

		// Read test properties file
		addProperties( ConfigUtil.getConfigDir() + File.separator + "testdatabase.properties");
		
		
		
		if ( logger.isDebugEnabled()) logger.debug( "... end of 'read TMS configuration'" );

		return bRes;
	}

	public static String getProperty( String key, String defVal ) {
		return properties.getProperty( key, defVal );
	}
	
	public static String getProperty( String key ) {
		return getProperty( key, "" );
	}
	
	public static void setProperty( String key, String value ) {
		properties.setProperty( key, value );
	}
	
	public static void addOrganisationConfig( Organisation org, Properties props ) {
		
		orgganisationsMap.put( org.getCode(), props );
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
	public static Map<String, Properties> getOrganisationProperties() {
		return orgganisationsMap;
	}
	
	public static String getOrganisationProperty( String orgCode, String property ) {
		Properties props = orgganisationsMap.get( orgCode );
		if ( props != null ) {
			String res = props.getProperty( property );
			if ( res != null ) {
				return res;
			}
		} 
		
		logger.error( "Requested property '" + property + "' net found for Organisation: Code=" + orgCode );
		return null;

	}
	
	public static void setProperty( String orgCode, String key, String value ) {
		Properties props = orgganisationsMap.get( orgCode );
		if ( props == null ) {
			props = new Properties();
			orgganisationsMap.put( orgCode, props );
		} 
		if ( props != null ) {
			props.setProperty( key, value );
		} 
	}
	

	private static void addProperties( Properties properties ) {

		// Now add read properties
		if ( TmsConfiguration.properties == null ) {
			if ( logger.isDebugEnabled()) logger.debug( "TmsConfig.properties == null. Will be created!" );
			TmsConfiguration.properties = new Properties();
		}
		
		TmsConfiguration.properties.putAll( properties );
		if ( logger.isDebugEnabled()) logger.debug( "Properties were added to TmsConfig.properties!" );
	}

	private static void addProperties( String fileName ) {

		Properties readProperties = ConfigUtil.readPropertiesFromFile( fileName );
		
		// Now add read properties
		if ( TmsConfiguration.properties == null ) {
			if ( logger.isDebugEnabled()) logger.debug( "TmsConfig.properties == null. Will be created!" );
			TmsConfiguration.properties = new Properties();
		}
		
		TmsConfiguration.properties.putAll( readProperties );
		if ( logger.isDebugEnabled()) logger.debug( "Properties were added to TmsConfig.properties!" );
	}

}
