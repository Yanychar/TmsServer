/**
 * 
 */
package com.c2point.tms.application;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author sevastia
 *
 */
@ApplicationPath("rest")
public class TmsRestApplication extends ResourceConfig {

	public TmsRestApplication() {
		packages( "com.c2point.tms.resources" );
	}

}
