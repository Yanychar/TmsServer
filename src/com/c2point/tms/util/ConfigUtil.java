package com.c2point.tms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;

public class ConfigUtil {
	private static Logger logger = LogManager.getLogger( ConfigUtil.class.getName());

	/**
	 * Returns the path to configuration directory. Specified by VM
	 * argument "teagle.configDir".
	 */
	public static String getConfigDir() {
		return System.getProperty("tms.config.dir", "config");
	}

	/**
	 * Returns the name of the file with locale related properties
	 * By default linux defined locales has been used
	 */
	public static String getLocalePropertiesFile() {
		String osName = System.getProperty( "os.name" ).toLowerCase();
		
		if ( osName.contains( "windows" )) {
			if ( logger.isDebugEnabled()) logger.debug( "Windows locale property file has been chosen" );
			return "locale.properties";
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "Linux locale  property file has been chosen" );
		return "locale_linux.properties";
	}
	
	public static Properties readPropertiesFromFile( String fileName ) {

		File file = new File( fileName );

		return readPropertiesFromFile( file );
	}

	public static Properties readPropertiesFromFile( File file ) {

		Properties readProperties = new Properties();
		
		FileInputStream fis = null;
		try {
			if ( logger.isDebugEnabled()) logger.debug( "Create File stream for: " + file.getName() );
			fis = new FileInputStream( file );
			if ( logger.isDebugEnabled()) logger.debug( "...created" );
			readProperties.load( fis );
			if ( logger.isDebugEnabled()) logger.debug( "Properties were loaded from file: " + file.getName() );
		} catch ( Exception e ) {
			if ( logger.isDebugEnabled()) logger.debug( "Failed to load property file: " + e.getMessage());
			readProperties = null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}

		if ( readProperties == null ) {
			logger.error( "Missing properties file -> " + file.getName());
			throw new IllegalArgumentException( "Missing properties file -> " + file.getName());
		}
		
		return readProperties;
	}

	public static int getOrganisationIntProperty( Organisation org, String propName, int defValue ) {
		int iRet = defValue;
		
		try {
			iRet = Integer.parseInt( org.getProperty( propName, null ));
		} catch ( Exception e ) {
			logger.debug( "Could not convert '...backward.period' property into the integer. Default " + defValue + " will be used" );
			iRet = defValue;
		}
		
		
		return iRet;
	}
	
}
