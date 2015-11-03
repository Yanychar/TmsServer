package com.c2point.tms.web.ui.geo.code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class GoogleGeoCoder implements GeoCoderIF {

	private static final String GEO_CODE_SERVER = "http://maps.googleapis.com/maps/api/geocode/json?";

	private static volatile long lastRequest = 0L;
	
	@Override
	public String getLocation( String address ) {

        String requestAddress = buildUrl( address );

        synchronized (this) {
            try {
            	
                long elapsed = System.currentTimeMillis() - lastRequest;
                
                if ( elapsed < 100 ) {
                    try {
                        Thread.sleep(100 - elapsed);
                    } catch (InterruptedException e) {
                    }
                }
                
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                
                BufferedReader reader = new BufferedReader( new InputStreamReader( new URL( requestAddress ).openStream()));

                while (( line = reader.readLine()) != null ) {
                	
                	responseBuilder.append( line + '\n' );
                	
                }

                return responseBuilder.toString();
                
            } catch ( Exception e ) {
				e.printStackTrace();
				return "";
				
			} finally {
                lastRequest = System.currentTimeMillis();
            }
        }
        
        
    }
	
    private static String buildUrl(String code)
    {
        StringBuilder builder = new StringBuilder();

        builder.append( GEO_CODE_SERVER );

        builder.append( "address=" );
        builder.append(code.replaceAll( " ", "+" ));
        builder.append( "&sensor=false" );

        return builder.toString();
    }
	
}
