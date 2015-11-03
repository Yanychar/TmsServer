package com.c2point.tms.datalayer;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.access.SecurityGroup;
import com.c2point.tms.entity.access.SupportedFunctionType;

public class AccessRightsFacade {
	private static Logger logger = LogManager.getLogger( AccessRightsFacade.class.getName());
	
	/*
	 *  Default Groups:
	 *	1) Check-In/-Out only
	 *  2) Externals. Can Check-In/-Out and reports but no View result, no Web interface. Phone Client Only
	 *  3) Projects participant. Check-In/-Out, reporting. View and Edit own reports through the Web
	 *  4) Projects owner. Own + Team reports    
	 *  5) Company Service Owner
	 *  6) Company TMS admin
	 *  7) TMS owner (C2Point)
	 *  8) No name 3. All false
	 *  9) No name 3. All false
	 *  10)  No name 3. All false
	 *  
	 *    Default Settings.
	 *    One setting: array of strings identified on group Access Rights for one function:
	 *    2 type of records:
	 *    - Starting with "***" defines Group settings:
	 *       Group Code, Group Name
	 *    - Starting from digit defines   
	 *    Group Code, Function, R, W, A, D
	 *      
	 */
	
	public static String SERVICE_OWNER_GROUP_ID = "006";
	
	private static String [][] defGroups = {
		// Fields: code, name
//		{ "001", "Check-in/-Out only" },
//		{ "002", "Externals" },
		{ "003", "Employee" },
		{ "004", "Project Owner" },
		{ "005", "Accountant" },
		{ SERVICE_OWNER_GROUP_ID, "Service Owner" },
		{ "007", "Administrator" },
//		{ "008", "Full Access" },
//		{ "009", "-- Not used --" }
	};
	
	private static String [][] defAccessRights = {
		// Group 1. Check-In/-Out only
		{ "001",  "0",  "true",  "false", "true", "false" },		// PRESENCE_OWN( 0 )
		{ "001",  "1", "false", "false", "false", "false" },		// PRESENCE_TEAM( 1 )	
		{ "001",  "2", "false", "false", "false", "false" },		// PRESENCE_COMPANY( 2 ) 
		{ "001",  "3", "false", "false", "false", "false" },		// REPORTS_OWN( 3 )
		{ "001",  "4", "false", "false", "false", "false" },		// REPORTS_TEAM( 4 )
		{ "001",  "5", "false", "false", "false", "false" },		// REPORTS_COMPANY( 5 )
		{ "001",  "6", "false", "false", "false", "false" },		// CONSOLIDATE_OWN( 6 )
		{ "001",  "7", "false", "false", "false", "false" },		// CONSOLIDATE_TEAM( 7 )
		{ "001",  "8", "false", "false", "false", "false" },		// CONSOLIDATE_COMPANY( 8 )
		{ "001",  "9", "false", "false", "false", "false" },		// PROJECTS_OWN( 9 )
		{ "001", "10", "false", "false", "false", "false" },		// PROJECTS_TEAM( 10 )
		{ "001", "11", "false", "false", "false", "false" },		// PROJECTS_COMPANY( 11 )
		{ "001", "12", "false", "false", "false", "false" },		// PERSONNEL_OWN( 12 )
		{ "001", "13", "false", "false", "false", "false" },		// PERSONNEL_TEAM( 13 )
		{ "001", "14", "false", "false", "false", "false" },		// PERSONNEL_COMPANY( 14 )
		{ "001", "15", "false", "false", "false", "false" },		// ACCESS_RIGHTS_COMPANY( 15 ) 
		{ "001", "16", "false", "false", "false", "false" },		// TMS_MANAGEMENT( 16 )
		{ "001", "17", "false", "false", "false", "false" },		// SETTINGS_COMPANY( 17 )
		{ "001", "18", "false", "false", "false", "false" },		// SETTINGS_TMS( 18 )
		{ "001", "19", "false", "false", "false", "false" },		// ACCESS_RIGHTS_TMS( 19 )
		{ "001", "20", "false", "false", "false", "false" },		// IMPORTEXPORT_COMPANY( 20 )
		{ "001", "21", "false", "false", "false", "false" },		// IMPORTEXPORT_TMS( 21 )
		
		// Group 2. Externals. Can Check-In/-Out and reports but no View result, no Web interface. Phone Client Only
		{ "002",  "0",  "true",  "false", "true", "false" },		// PRESENCE_OWN( 0 )
		{ "002",  "1", "false", "false", "false", "false" },		// PRESENCE_TEAM( 1 )
		{ "002",  "2", "false", "false", "false", "false" },		// PRESENCE_COMPANY( 2 )
		{ "002",  "3",  "true",  "true",  "true",  "true" },		// REPORTS_OWN( 3 )
		{ "002",  "4", "false", "false", "false", "false" },		// REPORTS_TEAM( 4 )
		{ "002",  "5", "false", "false", "false", "false" },		// REPORTS_COMPANY( 5 )
		{ "002",  "6", "false", "false", "false", "false" },		// CONSOLIDATE_OWN( 6 )
		{ "002",  "7", "false", "false", "false", "false" },		// CONSOLIDATE_TEAM( 7 )
		{ "002",  "8", "false", "false", "false", "false" },		// CONSOLIDATE_COMPANY( 8 )
		{ "002",  "9", "false", "false", "false", "false" },		// PROJECTS_OWN( 9 )
		{ "002", "10", "false", "false", "false", "false" },		// PROJECTS_TEAM( 10 )
		{ "002", "11", "false", "false", "false", "false" },		// PROJECTS_COMPANY( 11 )
		{ "002", "12", "false", "false", "false", "false" },		// PERSONNEL_OWN( 12 )
		{ "002", "13", "false", "false", "false", "false" },		// PERSONNEL_TEAM( 13 )
		{ "002", "14", "false", "false", "false", "false" },		// PERSONNEL_COMPANY( 14 )
		{ "002", "15", "false", "false", "false", "false" },		// ACCESS_RIGHTS_COMPANY( 15 ) 
		{ "002", "16", "false", "false", "false", "false" },		// TMS_MANAGEMENT( 16 )
		{ "002", "17", "false", "false", "false", "false" },		// SETTINGS_COMPANY( 17 )
		{ "002", "18", "false", "false", "false", "false" },		// SETTINGS_TMS( 18 )
		{ "002", "19", "false", "false", "false", "false" },		// ACCESS_RIGHTS_TMS( 19 )
		{ "002", "20", "false", "false", "false", "false" },		// IMPORTEXPORT_COMPANY( 20 )
		{ "002", "21", "false", "false", "false", "false" },		// IMPORTEXPORT_TMS( 21 )
		// Group 3. Projects participant. Check-In/-Out, reporting. View and Edit own reports through the Web
		{ "003",  "0",  "true",  "false", "true", "false" },		// PRESENCE_OWN( 0 )
		{ "003",  "1", "false", "false", "false", "false" },		// PRESENCE_TEAM( 1 )	
		{ "003",  "2", "false", "false", "false", "false" },		// PRESENCE_COMPANY( 2 ) 
		{ "003",  "3",  "true",  "true",  "true",  "true" },		// REPORTS_OWN( 3 )
		{ "003",  "4", "false", "false", "false", "false" },		// REPORTS_TEAM( 4 )
		{ "003",  "5", "false", "false", "false", "false" },		// REPORTS_COMPANY( 5 )
		{ "003",  "6",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_OWN( 6 )
		{ "003",  "7", "false", "false", "false", "false" },		// CONSOLIDATE_TEAM( 7 )
		{ "003",  "8", "false", "false", "false", "false" },		// CONSOLIDATE_COMPANY( 8 )
		{ "003",  "9", "false", "false", "false", "false" },		// PROJECTS_OWN( 9 )
		{ "003", "10", "false", "false", "false", "false" },		// PROJECTS_TEAM( 10 )
		{ "003", "11", "false", "false", "false", "false" },		// PROJECTS_COMPANY( 11 )
		{ "003", "12", "false", "false", "false", "false" },		// PERSONNEL_OWN( 12 )
		{ "003", "13", "false", "false", "false", "false" },		// PERSONNEL_TEAM( 13 )
		{ "003", "14", "false", "false", "false", "false" },		// PERSONNEL_COMPANY( 14 )
		{ "003", "15", "false", "false", "false", "false" },		// ACCESS_RIGHTS_COMPANY( 15 ) 
		{ "003", "16", "false", "false", "false", "false" },		// TMS_MANAGEMENT( 16 )
		{ "003", "17", "false", "false", "false", "false" },		// SETTINGS_COMPANY( 17 )
		{ "003", "18", "false", "false", "false", "false" },		// SETTINGS_TMS( 18 )
		{ "003", "19", "false", "false", "false", "false" },		// ACCESS_RIGHTS_TMS( 19 )
		{ "003", "20", "false", "false", "false", "false" },		// IMPORTEXPORT_COMPANY( 20 )
		{ "003", "21", "false", "false", "false", "false" },		// IMPORTEXPORT_TMS( 21 )
		// Group 4. Projects owner. Own + Team reports    
		{ "004",  "0",  "true",  "false", "true", "false" },		// PRESENCE_OWN( 0 )
		{ "004",  "1",  "true",  "true",  "true",  "true" },		// PRESENCE_TEAM( 1 )	
		{ "004",  "2", "false", "false", "false", "false" },		// PRESENCE_COMPANY( 2 ) 
		{ "004",  "3",  "true",  "true",  "true",  "true" },		// REPORTS_OWN( 3 )
		{ "004",  "4",  "true",  "true",  "true",  "true" },		// REPORTS_TEAM( 4 )
		{ "004",  "5", "false", "false", "false", "false" },		// REPORTS_COMPANY( 5 )
		{ "004",  "6",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_OWN( 6 )
		{ "004",  "7",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_TEAM( 7 )
		{ "004",  "8", "false", "false", "false", "false" },		// CONSOLIDATE_COMPANY( 8 )
		{ "004",  "9",  "true",  "true", "false", "false" },		// PROJECTS_OWN( 9 )
		{ "004", "10", "false", "false", "false", "false" },		// PROJECTS_TEAM( 10 )
		{ "004", "11", "false", "false", "false", "false" },		// PROJECTS_COMPANY( 11 )
		{ "004", "12", "false", "false", "false", "false" },		// PERSONNEL_OWN( 12 )
		{ "004", "13", "false", "false", "false", "false" },		// PERSONNEL_TEAM( 13 )
		{ "004", "14", "false", "false", "false", "false" },		// PERSONNEL_COMPANY( 14 )
		{ "004", "15", "false", "false", "false", "false" },		// ACCESS_RIGHTS_COMPANY( 15 ) 
		{ "004", "16", "false", "false", "false", "false" },		// TMS_MANAGEMENT( 16 )
		{ "004", "17", "false", "false", "false", "false" },		// SETTINGS_COMPANY( 17 )
		{ "004", "18", "false", "false", "false", "false" },		// SETTINGS_TMS( 18 )
		{ "004", "19", "false", "false", "false", "false" },		// ACCESS_RIGHTS_TMS( 19 )
		{ "004", "20", "false", "false", "false", "false" },		// IMPORTEXPORT_COMPANY( 20 )
		{ "004", "21", "false", "false", "false", "false" },		// IMPORTEXPORT_TMS( 21 )
		// Group 5. Accountant
		{ "005",  "0",  "true",  "true",  "true", "false" },		// PRESENCE_OWN( 0 )
		{ "005",  "1", "false", "false", "false", "false" },		// PRESENCE_TEAM( 1 )	
		{ "005",  "2", "false", "false", "false", "false" },		// PRESENCE_COMPANY( 2 ) 
		{ "005",  "3",  "true",  "true",  "true",  "true" },		// REPORTS_OWN( 3 )
		{ "005",  "4",  "true", "false", "false", "false" },		// REPORTS_TEAM( 4 )
		{ "005",  "5",  "true", "false", "false", "false" },		// REPORTS_COMPANY( 5 )
		{ "005",  "6",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_OWN( 6 )
		{ "005",  "7",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_TEAM( 7 )
		{ "005",  "8",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_COMPANY( 8 )
		{ "005",  "9",  "true",  "true",  "true",  "true" },		// PROJECTS_OWN( 9 )
		{ "005", "10",  "truee", "true",  "true",  "true" },		// PROJECTS_TEAM( 10 )
		{ "005", "11",  "true", "false",  "true",  "true" },		// PROJECTS_COMPANY( 11 )
		{ "005", "12",  "true", "false", "false", "false" },		// PERSONNEL_OWN( 12 )
		{ "005", "13", "false", "false", "false", "false" },		// PERSONNEL_TEAM( 13 )
		{ "005", "14", "false", "false", "false", "false" },		// PERSONNEL_COMPANY( 14 )
		{ "005", "15", "false", "false", "false", "false" },		// ACCESS_RIGHTS_COMPANY( 15 ) 
		{ "005", "16", "false", "false", "false", "false" },		// TMS_MANAGEMENT( 16 )
		{ "005", "17", "false", "false", "false", "false" },		// SETTINGS_COMPANY( 17 )
		{ "005", "18", "false", "false", "false", "false" },		// SETTINGS_TMS( 18 )
		{ "005", "19", "false", "false", "false", "false" },		// ACCESS_RIGHTS_TMS( 19 )
		{ "005", "20", "false", "false", "false", "false" },		// IMPORTEXPORT_COMPANY( 20 )
		{ "005", "21", "false", "false", "false", "false" },		// IMPORTEXPORT_TMS( 21 )
		// Group 6. Company Service Owner
		{ SERVICE_OWNER_GROUP_ID,  "0",  "true",  "true",  "true",  "true" },		// PRESENCE_OWN( 0 )
		{ SERVICE_OWNER_GROUP_ID,  "1",  "true",  "true",  "true",  "true" },		// PRESENCE_TEAM( 1 )	
		{ SERVICE_OWNER_GROUP_ID,  "2",  "true",  "true",  "true",  "true" },		// PRESENCE_COMPANY( 2 ) 
		{ SERVICE_OWNER_GROUP_ID,  "3",  "true",  "true",  "true",  "true" },		// REPORTS_OWN( 3 )
		{ SERVICE_OWNER_GROUP_ID,  "4",  "true",  "true",  "true",  "true" },		// REPORTS_TEAM( 4 )
		{ SERVICE_OWNER_GROUP_ID,  "5",  "true",  "true",  "true",  "true" },		// REPORTS_COMPANY( 5 )
		{ SERVICE_OWNER_GROUP_ID,  "6",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_OWN( 6 )
		{ SERVICE_OWNER_GROUP_ID,  "7",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_TEAM( 7 )
		{ SERVICE_OWNER_GROUP_ID,  "8",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_COMPANY( 8 )
		{ SERVICE_OWNER_GROUP_ID,  "9",  "true",  "true",  "true",  "true" },		// PROJECTS_OWN( 9 )
		{ SERVICE_OWNER_GROUP_ID, "10",  "true",  "true",  "true",  "true" },		// PROJECTS_TEAM( 10 )
		{ SERVICE_OWNER_GROUP_ID, "11",  "true",  "true",  "true",  "true" },		// PROJECTS_COMPANY( 11 )
		{ SERVICE_OWNER_GROUP_ID, "12",  "true",  "true",  "true",  "true" },		// PERSONNEL_OWN( 12 )
		{ SERVICE_OWNER_GROUP_ID, "13",  "true",  "true",  "true",  "true" },		// PERSONNEL_TEAM( 13 )
		{ SERVICE_OWNER_GROUP_ID, "14",  "true",  "true",  "true",  "true" },		// PERSONNEL_COMPANY( 14 )
		{ SERVICE_OWNER_GROUP_ID, "15",  "true",  "true",  "true",  "true" },		// ACCESS_RIGHTS_COMPANY( 15 ) 
		{ SERVICE_OWNER_GROUP_ID, "16", "false", "false", "false", "false" },		// TMS_MANAGEMENT( 16 )
		{ SERVICE_OWNER_GROUP_ID, "17",  "true",  "true",  "true",  "true" },		// SETTINGS_COMPANY( 17 )
		{ SERVICE_OWNER_GROUP_ID, "18", "false", "false", "false", "false" },		// SETTINGS_TMS( 18 )
		{ SERVICE_OWNER_GROUP_ID, "19", "false", "false", "false", "false" },		// ACCESS_RIGHTS_TMS( 19 )
		{ SERVICE_OWNER_GROUP_ID, "20",  "true",  "true",  "true",  "true" },		// IMPORTEXPORT_COMPANY( 20 )
		{ SERVICE_OWNER_GROUP_ID, "21", "false", "false", "false", "false" },		// IMPORTEXPORT_TMS( 21 )
		// Group 7. Company Service admin
		{ "007",  "0",  "true",  "false", "true", "false" },		// PRESENCE_OWN( 0 )
		{ "007",  "1", "false", "false", "false", "false" },		// PRESENCE_TEAM( 1 )	
		{ "007",  "2", "false", "false", "false", "false" },		// PRESENCE_COMPANY( 2 ) 
		{ "007",  "3", "false", "false", "false", "false" },		// REPORTS_OWN( 3 )
		{ "007",  "4", "false", "false", "false", "false" },		// REPORTS_TEAM( 4 )
		{ "007",  "5", "false", "false", "false", "false" },		// REPORTS_COMPANY( 5 )
		{ "007",  "6", "false", "false", "false", "false" },		// CONSOLIDATE_OWN( 6 )
		{ "007",  "7", "false", "false", "false", "false" },		// CONSOLIDATE_TEAM( 7 )
		{ "007",  "8", "false", "false", "false", "false" },		// CONSOLIDATE_COMPANY( 8 )
		{ "007",  "9", "false", "false", "false", "false" },		// PROJECTS_OWN( 9 )
		{ "007", "10", "false", "false", "false", "false" },		// PROJECTS_TEAM( 10 )
		{ "007", "11",  "true",  "true",  "true",  "true" },		// PROJECTS_COMPANY( 11 )
		{ "007", "12",  "true",  "true",  "true",  "true" },		// PERSONNEL_OWN( 12 )
		{ "007", "13",  "true",  "true",  "true",  "true" },		// PERSONNEL_TEAM( 13 )
		{ "007", "14", "false", "false", "false", "false" },		// PERSONNEL_COMPANY( 14 )
		{ "007", "15",  "true",  "true",  "true",  "true" },		// ACCESS_RIGHTS_COMPANY( 15 ) 
		{ "007", "16", "false", "false", "false", "false" },		// TMS_MANAGEMENT( 16 )
		{ "007", "17",  "true",  "true",  "true",  "true" },		// SETTINGS_COMPANY( 17 )
		{ "007", "18", "false", "false", "false", "false" },		// SETTINGS_TMS( 18 )
		{ "007", "19", "false", "false", "false", "false" },		// ACCESS_RIGHTS_TMS( 19 )
		{ "007", "20",  "true",  "true",  "true",  "true" },		// IMPORTEXPORT_COMPANY( 20 )
		{ "007", "21", "false", "false", "false", "false" },		// IMPORTEXPORT_TMS( 21 )
		// Group 8. Full Access
		{ "008",  "0",  "true",  "true", "true",  "true" },			// PRESENCE_OWN( 0 )
		{ "008",  "1",  "true",  "true",  "true",  "true" },		// PRESENCE_TEAM( 1 )	
		{ "008",  "2",  "true",  "true",  "true",  "true" },		// PRESENCE_COMPANY( 2 ) 
		{ "008",  "3",  "true",  "true",  "true",  "true" },		// REPORTS_OWN( 3 )
		{ "008",  "4",  "true",  "true",  "true",  "true" },		// REPORTS_TEAM( 4 )
		{ "008",  "5",  "true",  "true",  "true",  "true" },		// REPORTS_COMPANY( 5 )
		{ "008",  "6",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_OWN( 6 )
		{ "008",  "7",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_TEAM( 7 )
		{ "008",  "8",  "true",  "true",  "true",  "true" },		// CONSOLIDATE_COMPANY( 8 )
		{ "008",  "9",  "true",  "true",  "true",  "true" },		// PROJECTS_OWN( 9 )
		{ "008", "10",  "true",  "true",  "true",  "true" },		// PROJECTS_TEAM( 10 )
		{ "008", "11",  "true",  "true",  "true",  "true" },		// PROJECTS_COMPANY( 11 )
		{ "008", "12",  "true",  "true",  "true",  "true" },		// PERSONNEL_OWN( 12 )
		{ "008", "13",  "true",  "true",  "true",  "true" },		// PERSONNEL_TEAM( 13 )
		{ "008", "14",  "true",  "true",  "true",  "true" },		// PERSONNEL_COMPANY( 14 )
		{ "008", "15",  "true",  "true",  "true",  "true" },		// ACCESS_RIGHTS_COMPANY( 15 ) 
		{ "008", "16",  "true",  "true",  "true",  "true" },		// TMS_MANAGEMENT( 16 )
		{ "008", "17",  "true",  "true",  "true",  "true" },		// SETTINGS_COMPANY( 17 )
		{ "008", "18",  "true",  "true",  "true",  "true" },		// SETTINGS_TMS( 18 )
		{ "008", "19",  "true",  "true",  "true",  "true" },		// ACCESS_RIGHTS_TMS( 19 )
		{ "008", "20",  "true",  "true",  "true",  "true" },		// IMPORTEXPORT_COMPANY( 20 )
		{ "008", "21",  "true",  "true",  "true",  "true" },		// IMPORTEXPORT_TMS( 21 )
	};

	public static AccessRightsFacade getInstance() {
		return new AccessRightsFacade();
	}
	
	public Organisation updateSecurityGroups( Organisation org ) {
		
		Organisation newOrg = null;

		if ( org == null ) {
			logger.error( "Organisation must NOT be NULL here!" );
			return null;
		}
		
		if ( validateAndFix( org )) {

			// Store Organisation with men SGs & ARs
			if ( org.getServiceOwner() != null ) {
				org.getServiceOwner().getContext().setSecGroup( org.getSecurityGroups().get( AccessRightsFacade.SERVICE_OWNER_GROUP_ID ));
			}
			
			
			newOrg = DataFacade.getInstance().merge( org );
			if ( logger.isDebugEnabled()) logger.debug( "Organisation Security has been updated!" );
		} else {
			newOrg = org;
		}

		return newOrg;
	}


	public String [][] getDefSecurityGroupsDescription() {
		return defGroups;
	}
	public String [][] getDefAccessRightsDescription() {
		return defAccessRights;
	}

	// Validate existence of Default Security Groups. Fix if necessary
	public boolean validateAndFix( Organisation org ) {
		boolean bRes = false;  // Return TRUE if changes 
		
		SecurityGroup sg;
		Map<String, SecurityGroup> groupsMap = org.getSecurityGroups();
		
		if ( groupsMap == null ) {
			groupsMap = new HashMap<String, SecurityGroup>();
		}

		// Create Security groups
		for( String [] row : defGroups ) {
			if ( row[0] != null   ) {
				// New Group description
				if ( !groupsMap.containsKey( row[ 0 ])) {
					groupsMap.put( row[0] , new SecurityGroup( org, row[0], row[1] ));
					bRes = bRes || true;
					if ( logger.isDebugEnabled()) logger.debug( " Security group was added: " + groupsMap.get( row[0] ));
				} else {
					if ( logger.isDebugEnabled()) logger.debug( " Security group '" + groupsMap.get( row[1] ) + "' exist!" );
				}
			}
			
		}

		// Add Access Rights to Security groups
		for( String [] row : defAccessRights ) {
			if ( row[0] != null  ) {
				
				// Set of rights description
				if ( logger.isDebugEnabled()) logger.debug( "   Search group with code: " + row[0] + "..." );
				sg = groupsMap.get( row[0] );
				if ( logger.isDebugEnabled()) logger.debug( "   Security group shall be updated: " + sg );
				if ( sg != null && sg.getRights( SupportedFunctionType.fromValue( row[1] )) == null ) {
					sg.setRights( SupportedFunctionType.fromValue( row[1] ), Boolean.parseBoolean( row[2]), Boolean.parseBoolean( row[3]), Boolean.parseBoolean( row[4]), Boolean.parseBoolean( row[5]));
					bRes = bRes || true;
				} else {
					if ( logger.isDebugEnabled()) logger.debug( "   Access Rights cannot be set (Group,AR): [ " 
							+ ( sg != null ? sg.getDefName() : "No Group" ) + ", " 
							+ ( SupportedFunctionType.fromValue( row[1] ) != null ? SupportedFunctionType.fromValue( row[1] ) : "No AR" )
							+ " ]" );
				}
			}
		}
		
		if ( bRes ) { 
			org.setSecurityGroups( groupsMap );
			if ( logger.isDebugEnabled()) logger.debug( "   Security Groups for Organisation were changed. Org shall me updated!" );
		}
		
		return bRes;
	}
	

}
