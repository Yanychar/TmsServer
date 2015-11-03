package com.c2point.tms.web.resources;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.web.resources.tools.LangPacks;
import com.c2point.tms.web.resources.tools.LocalizedProperty;

public class CreateResourcesTool {

	private static Logger logger = LogManager.getLogger( CreateResourcesTool.class.getName());

	protected static String 	EOL = "\r\n";
	
	public static void createResources( LangPacks allPacks ) {
		for ( Locale locale : ResourceTools.SupportedLocales ) {
			if ( locale != null ) {
				createResource( allPacks, locale );
			}
		}
	}

	private static boolean createResource( LangPacks allPacks, Locale locale ) {
		
		// Create Lang Resource property file
//		String filename = ResourceTools.propertyFileName( locale, ".new" );
		String filename = ResourceTools.propertyFileName( locale );
	
		try {
			
			FileWriter fstream = new FileWriter( filename );
			BufferedWriter out = new BufferedWriter( fstream );			
			
			Iterator<String> iter = allPacks.keys().iterator();
			String key, value;
			while( iter.hasNext()) {
				key = iter.next();
				
				LocalizedProperty lp =  allPacks.get( key );
	
				if ( lp.getType() == LocalizedProperty.RecType.EMPTY ) {
//					logger.debug( "" );
					out.write( "" );
					out.write( EOL );
					
				} else if ( lp.getType() == LocalizedProperty.RecType.COMMENT ) {
	// 				logger.debug( lp.getComment());
					out.write( lp.getComment() );
					out.write( EOL );
				} else if ( lp.getType() == LocalizedProperty.RecType.PROPERTY ) {
/*
					logger.debug( key + ":" );
					logger.debug( "    =" + lp.get( LangPacks.LOCALE_EN ));
					logger.debug( "      =" + lp.get( LangPacks.LOCALE_FI ));
					logger.debug( "        =" + lp.get( LangPacks.LOCALE_ES ));
					logger.debug( "          =" + lp.get( LangPacks.LOCALE_RU ));
					logger.debug( "            =" + lp.get( LangPacks.LOCALE_SV ));
*/
					value = lp.get( locale );
					out.write( key + " = " + value );
					out.write( EOL );

				}
			}
			
			out.close();
			fstream.close();
		} catch ( Exception e ) {
			logger.error( "Error: " + e );
		}
			
		
		return true;
	}

	public static boolean createCsvFile( LangPacks allPacks ) {
		
		// Create Lang Resource property file
	
		try {
			
			FileWriter fstream = new FileWriter( ResourceTools.csvFileName());
			BufferedWriter out = new BufferedWriter( fstream );			
			
			Iterator<String> iter = allPacks.keys().iterator();
			
			String key, header_1, header_2;
			LocalizedProperty lp;

			// Create Header line
			header_1 = "## Key Value";
			header_2 = "##";
			
			for ( Locale locale : ResourceTools.SupportedLocales ) {
				header_1 = header_1.concat( ResourceTools.csvDelimiter + "## " + ( locale != null ? locale : "" ));
				header_2 = header_2.concat( ResourceTools.csvDelimiter + "## " + ( locale != null ? locale.getDisplayLanguage() : "" ));
			}
			
			out.write( header_1 );
			out.write( EOL );
			out.write( header_2 );
			out.write( EOL );
			
			
			// Fill the file
			
			while( iter.hasNext()) {
				key = iter.next();
				
				lp =  allPacks.get( key );
				
				if ( lp != null ) {
	
					out.write( lp.exportAsCsv( key, ResourceTools.SupportedLocales, ResourceTools.csvDelimiter ));
					out.write( EOL );

				}
			}
			
			out.close();
			fstream.close();
		} catch ( Exception e ) {
			logger.error( "Error: " + e );
		}
			
		
		return true;
	}

}
