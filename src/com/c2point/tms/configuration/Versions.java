/*
 *  Class to keep SW and DB versions
 *  
 *  
 *  	30.01.2015	ver. 2.0.1	First time version info available	
 */

package com.c2point.tms.configuration;

public class Versions {
	
	private static String SW_VERSION_MAJOR = "2";
	private static String SW_VERSION_MINOR = "0";
	private static String SW_VERSION_BUILD = "1";
	
	public static String getSwVersion() {
		
		return 
				SW_VERSION_MAJOR + "." 
			+ 	SW_VERSION_MINOR + "."
			+ 	SW_VERSION_BUILD;
	}
	
	public static String getDbVersion() {
		
		return Long.toString( new TmsDBUpdate().getDbVersion());
	}
	

}
