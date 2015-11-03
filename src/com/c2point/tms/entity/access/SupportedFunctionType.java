package com.c2point.tms.entity.access;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum SupportedFunctionType {
	PRESENCE_OWN( 0 ),		// Access rights to OWN Check-In/-Out information
	PRESENCE_TEAM( 1 ),		// Access rights to Check-In/-Out information of Team Members
	PRESENCE_COMPANY( 2 ),	// Access rights to Company wide  Check-In/-Out information of Team Members
	
	REPORTS_OWN( 3 ),				// Access rights to Time&Travel Reports of your own
	REPORTS_TEAM( 4 ),				// Access rights to Time&Travel Reports of your Team for Line Managers or participating Employees for Project Managers
	REPORTS_COMPANY( 5 ),			// Access rights to all Company wide Time&Travel Reports 
	
	CONSOLIDATE_OWN( 6 ),			// Consolidated reporting. TBD!!!
	CONSOLIDATE_TEAM( 7 ),			// Consolidated reporting. TBD!!!
	CONSOLIDATE_COMPANY( 8 ),		// Consolidated reporting. TBD!!!
	
	PROJECTS_OWN( 9 ),    		// 
	PROJECTS_TEAM( 10 ),    		// Including possibility to assign Tasks to the project
	PROJECTS_COMPANY( 11 ),   	// Including possibility to define Tasks used in the Company
	
	PERSONNEL_OWN( 12 ),
	PERSONNEL_TEAM( 13 ),
	PERSONNEL_COMPANY( 14 ),
	
	ACCESS_RIGHTS_COMPANY( 15 ),
	ACCESS_RIGHTS_TMS( 19 ),
	
	SETTINGS_COMPANY( 17 ),
	SETTINGS_TMS( 18 ),
	
	IMPORTEXPORT_COMPANY( 20 ),
	IMPORTEXPORT_TMS( 21 ),
	
	TMS_MANAGEMENT( 16 ),
	
	SUBCONTRACTING_COMPANY( 22 );
	
	private static Logger logger = LogManager.getLogger( SupportedFunctionType.class.getName());
	
	private final int value;  
	private static int size = values().length;
	
	private SupportedFunctionType( int value ) {  
		this.value = value;  
	}  
	public int value() {  
		return value;  
	}  

    public static SupportedFunctionType fromValue( int value ) {
        for( SupportedFunctionType item : values())   {
            if ( value == item.value )
                return item;
        }
        return null;
    }

    
    public static SupportedFunctionType fromValue( String strValue ) {
    	try {
    		int intValue = Integer.parseInt( strValue );
    		return fromValue( intValue );
    	} catch ( Exception e ) {
			logger.error( "Passed string: '" + strValue + "' cannot be converted into integer!" );
    	}

    	return null;
    }
	
	
	public static int size() { return size; }
}
