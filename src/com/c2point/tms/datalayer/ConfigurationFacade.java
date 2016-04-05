package com.c2point.tms.datalayer;

import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.MeasurementUnit;

public class ConfigurationFacade {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ConfigurationFacade.class.getName()); 
	
	private static Collection<MeasurementUnit> cmu = null;
	/*	
	public static ConfigurationFacade getInstance() {
		if ( instance == null ) {
			ConfigurationFacade instance = new ConfigurationFacade();
		}
		return instance;
	}
*/
	private ConfigurationFacade() {}
	
	public static Collection<MeasurementUnit> getSupportedMeasurement() {
		
		if ( cmu == null ) {
			
			cmu = DataFacade.getInstance().list( MeasurementUnit.class );
		
		}
		
		return cmu;
			
	}

}
