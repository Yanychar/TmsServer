package com.c2point.tms.web.ui.geo.code;

public class GeoResultParser {

    public String[] parseGeoCoderResult( String response ) {

    	// Look for location using brute force.
        // There are much nicer ways to do this, e.g. with Google's JSON library: Gson
        //     https://sites.google.com/site/gson/gson-user-guide

        String[] lines = response.split( "\n" );

        String lat = null;
        String lng = null;

        for ( int i = 0; i < lines.length; i++ )
        {
            if ( "\"location\" : {".equals(lines[i].trim())) {
            	
                lat = getOrdinate(lines[i+1]);
                lng = getOrdinate(lines[i+2]);
                
                break;
            }
        }

        return new String[] { lat, lng };
    }

    private String getOrdinate( String s ) {
    	
        String[] split = s.trim().split( " " );

        if ( split.length < 1 ) {
            return null;
        }

        String ord = split[ split.length - 1 ];

        if ( ord.endsWith(",")) {
        	
            ord = ord.substring( 0, ord.length() - 1 );
            
        }

        // Check that the result is a valid double
        Double.parseDouble( ord );

        return ord;
    }

}
