package com.c2point.tms.web.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.web.resources.tools.LangPacks;

public class ReadResource {

	private static Logger logger = LogManager.getLogger( ReadResource.class.getName());
	
	public static void readPropertyFile( LangPacks allPacks, Locale locale  ) {

		String fileName = ResourceTools.propertyFileName( locale ); 
		
		InputStream stream;
		
		BufferedReader in = null;
		String str, splitStr;
		try {

			stream = ReadResource.class.getResourceAsStream ( fileName );
			
			in = new BufferedReader( new InputStreamReader (stream) );
	    	splitStr = "";
			while ((str = in.readLine()) != null) {
			    str = StringUtils.stripEnd( str, null );

			    logger.debug( "String to handle: '" + str + "'" );
			    
			    if ( StringUtils.endsWith( str, "\\" )) {
			    	splitStr = splitStr.concat( StringUtils.stripEnd( str, "\\" ));
			    } else {

			    	splitStr = splitStr.concat( str );
			    	
			    	handleString( locale, splitStr, allPacks  );
			    	
			    	splitStr = "";
			    }
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}

	}

	@SuppressWarnings("unused")
	public static void readCSVfiles( LangPacks allPacks ) {
		
//		ImportTool.allPacks = allPacks;
//		Locale locale;
		
		InputStream stream = null;
		
		BufferedReader in = null;
		String str;
		
		for ( Locale locale : ResourceTools.SupportedLocales ) {
			if ( locale != null ) {
				
				String fileName = ResourceTools.csvFileName( locale );

				int count = 0;
				try {

					stream = new FileInputStream( fileName );
							
//							ReadResource.class.getResourceAsStream( fileName );
					if ( stream == null) {
						logger.error( "File: " + fileName + " NOT found!");
						logger.error( "AbsPath = " + new File(".").getAbsolutePath());
						continue;
					}
					
					in = new BufferedReader( new InputStreamReader (stream) );
					while ((str = in.readLine()) != null) {
					    
				    	handleCsvString( allPacks, str, locale );
				    	count++;
					    	
					}
					logger.info( "Number of handled strings from file " + fileName + " is "+ count );
					in.close();
				} catch ( FileNotFoundException e ) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
						
			}
			
		
		}
		
	}
	
	@SuppressWarnings("unused")
	public static void readCsvMainFile( LangPacks allPacks ) {
		InputStream stream = null;
		
		BufferedReader in = null;
		String str;
		
				
		String fileName = ResourceTools.csvFileName();

/*		
		try {
			stream = new FileInputStream( fileName );
			
			in = new BufferedReader( new InputStreamReader (stream) );
			while ((str = in.readLine()) != null) {
			    
				System.out.println( "Str = '" + str + "'" );
			    	
			}
			in.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println( e );
		} catch (IOException e) {
			System.out.println( e );
		}
*/		
		
		int count = 0;
		try {

			stream = new FileInputStream( fileName );
					
			if ( stream == null) {
				logger.error( "File: " + fileName + " NOT found!");
				logger.error( "AbsPath = " + new File(".").getAbsolutePath());

				return;
			}
			
			in = new BufferedReader( new InputStreamReader( stream ) );
			
			// Read first header line. Take Locale sequence
			str = in.readLine();
			Locale [] localeSequence = getLocaleSequence( str );
			
			// Read Second header line and pass it through without handling
			str = in.readLine();
			
			while ((str = in.readLine()) != null) {
			    
		    	handleCsvString( allPacks, str, localeSequence );
		    	count++;
			    	
			}
			logger.info( "Number of handled strings from file " + fileName + " is "+ count );
			in.close();
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
						
		
	}
	
	
	private static void handleString( Locale locale, String str, LangPacks allPacks ) {
	    
		String value, key;
	    
		if ( str == null || StringUtils.strip( str ).length() == 0 ) {
			// Empty
			if ( locale.equals( LangPacks.LOCALE_EN )) {
//				logger.info( "" );
				allPacks.add();
			}
		} else if ( StringUtils.startsWith( StringUtils.stripStart( str, null ), "#" )) {
	    	// Comment
			if ( locale.equals( LangPacks.LOCALE_EN )) {
//				logger.info( StringUtils.stripStart( str, null ));
				allPacks.add( StringUtils.stripStart( str, null ));
				
			}
	    } else {
	    	String [] array = StringUtils.split( str, "=", 2 );
	    	
	    	if ( array.length == 0 ) {
				// Empty
				if ( locale.equals( LangPacks.LOCALE_EN )) {
//					logger.info( "" );
					allPacks.add();
				}
	    	} else {
	    		// Property
//				logger.info( array[0] + " = " + array[1] );
	    		key = array[0];
	    		value = ( array != null && array.length > 1 ) ? array[1] : "";
	    		if ( key != null ) {
	    			
	    			allPacks.add( key.trim(), locale, value.trim());
	    		}
	    	}
	    }
		
	}
	
	private static void handleCsvString( LangPacks allPacks, String str, Locale locale ) {

		String key, value;
		
		if ( str != null ) {
	    	String [] array = StringUtils.split( str, ResourceTools.csvDelimiter, 2 );

	    	if ( array == null || array.length < 2 ) {
	    		// This is not property. Can be empty or comment
	    		// If noAdd than do nothing!
	    		// TODO
	    	} else {
	    		key = array[0];
	    		value = array[1];
	    		
	    		allPacks.add( key, locale, value );
//	    		logger.info( "!!! " + key + "=" + value );
	    	}
		
		
		} else {
			// Create empty string in output 
			// TODO
		}
	}

	private static Locale [] getLocaleSequence( String headerStr ) {
		// SupportedLocales
		Locale [] localeSequence = ResourceTools.SupportedLocales;
		
		if ( headerStr != null ) {
	    	String [] array = StringUtils.split( headerStr, ResourceTools.csvDelimiter );

	    	if ( array == null || array.length != ResourceTools.SupportedLocales.length + 1 ) {
	    		logger.error( "Number of locales defined by header string wrong ( =" + array.length 
	    					  + " ). Error!!! ... but ResourceTools.SupportedLocales will be used" );
	    	} else {
				localeSequence = ResourceTools.SupportedLocales;
				
	    		for ( int i = 1; i < array.length; i++ ) {
	    			if ( StringUtils.remove( array[ i ], '#' ).trim().compareToIgnoreCase( ResourceTools.SupportedLocales[ i - 1 ].toString()) != 0 ) {
	    	    		logger.error( "Sequence of Locales defined by header string is wrong. "
	    	    					+ "Error!!! ... but ResourceTools.SupportedLocales will be used" );
	    			}
	    		}
	    	}
		
		
		} else {
			// Header string == null. Error but suppose that ResourceTools.SupportedLocales shall be used
    		logger.error( "Header string == null. Error!!! ... but ResourceTools.SupportedLocales will be used" );
		}
		
		
		return localeSequence;

	}

	private static void handleCsvString( LangPacks allPacks, String str, Locale [] localeSequence ) {

		String key, value;
		
		if ( str != null ) {
	    	String [] array = StringUtils.split( str, ResourceTools.csvDelimiter );

    		logger.debug( "  CSV string to convert: '" + str + "'" );
/*
    		try {
				PrintStream ps = new PrintStream( System.out, true );
				ps.println(str);
				
				String strEnd2 = new String( str.getBytes(),  "UTF-16LE" );
	    		logger.debug( "  New str: '" + strEnd2 + "'" );
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    	
*/	    	
    		logger.debug( "Number of fields in csv string == " + array.length + "." );
    		
	    	if ( array == null || array.length < 1 ) {
	    		allPacks.add();
	    		return;
	    	}
    		
	    	for ( int i = 1; i < array.length; i++ ) {
	    		key = array[0];
	    		value = array[i];
	    		logger.debug( "  Key/value to add: '" + key + "'/'" + value + "'. Locale: " + ResourceTools.SupportedLocales[ i - 1 ] );

	    		allPacks.add( key, ResourceTools.SupportedLocales[ i - 1 ], value );
	    	}
		
		
		} else {
    		logger.error( "CSV String == null! Error!" );
    		return;
		}
	}

	
}
