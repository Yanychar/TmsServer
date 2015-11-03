package com.c2point.tms.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringUtils {
	
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( StringUtils.class.getName());


	public static String padRightSpaces( String s, int n ) {
		if ( s == null ) { s = ""; }
		return String.format( "%1$-" + n + "s", s );   
	} 
	 
	public static String padLeftSpaces( String s, int n ) { 
		if ( s == null ) { s = ""; }
	    return String.format( "%1$" + n + "s", s );   
	}	
	public static String padLeftZero( float num, int length ) {
		return padLeftChar( num, length, '0' );
	}
	
	public static String padLeftZero( int num, int length ) {
		return padLeftChar( num, length, '0' );
	}
	
	public static String padLeftChar( float num, int length, char c ) {
		return padLeftChar(( int )num, length, c );
	}
	
	public static String padLeftChar( int num, int length, char c ) {
		// Create correct format string
		String format = String.format("%%%c%dd", c, length ); 
		
		return String.format( format, num );
	}
	
	public static String padLeftZero( String str, int length ) {
		return padLeftChar( str, length, '0' );
	}

	public static String padLeftChar( String str, int length, char c ) {
		String result;
		
		if ( str == null ) {
			str = "";
		}
		
		if ( str.length() == length ) {
			result = str;
		} else if ( str.length() > length ) {
			// Cut the string on the left
			result = str.substring( str.length() - length );
		} else { 
			// Add leading c
		    int curLength = str.length();   
		    StringBuffer buffer = new StringBuffer( curLength );   
		    while ( curLength++ < length )   
		      buffer.append( c );   
		    buffer.append( str );   
		    result = buffer.toString();   
		}  			

		return result;
	}
	
	public static String nullToStr( String str ) { return ( str != null ? str : "" ); }
	
	
/*	
	public static String convertToEng( String orig ) {
		String dest = "";
		
		if ( orig != null && orig.length() > 0 )
			dest = orig.replace( 'ö', 'o' ).replace( 'ä', 'a' ).replace( 'å', 'a' );
		
		
		return dest;
	}
*/	
}
