package com.c2point.tms.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.c2point.tms.datalayer.AccessRightsFacade;
import com.c2point.tms.datalayer.AuthenticationFacade;
import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.access.SecurityGroup;
import com.c2point.tms.entity.transactions.CheckInOutTransactionData;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.xml.XMLconverter;

public class TmsDBUpdate {
	private static Logger logger = LogManager.getLogger( TmsDBUpdate.class.getName());

	private String dbName = null;
	private String usrname = null;
	private String pwd = null;
	private Connection con = null;

	public TmsDBUpdate() {}

	public static void updateTmsDatabase() {

		logger.debug( "Start Update Database..." );
		
		TmsDBUpdate updater = new TmsDBUpdate();
		updater.readParameters();
		
		// get connection. exit if con == null
		if ( updater.initConnection() == null ) {
			logger.error( "Cannot open connection to DB! Exit." );
			System.exit( 0 );
		}
		
		// Create 'config' table if missing. Setup db ver 1
		updater.createConfigTableIfNecessary();
		
		// Change DB depending on current db version
		long db_version = updater.getDbVersion();
		
		boolean res = false;
		if ( db_version == 1 ) {
			res = updater.convert_from_1_to_2();
		} else if ( db_version == 2 ) {
			res = updater.convert_from_2_to_3();
		} else if ( db_version == 3 ) {
			res = updater.convert_from_3_to_4();
		} else if ( db_version == 4 ) {
			res = updater.convert_from_4_to_5();
		} else if ( db_version == 5 ) {
			res = updater.convert_from_5_to_6();
		} else if ( db_version == 6 ) {
			res = updater.convert_from_6_to_7();
		} else if ( db_version == 7 ) {
			res = updater.convert_from_7_to_8();
		} else if ( db_version == 8 ) {
			res = updater.convert_from_8_to_9();
		} else if ( db_version == 9 ) {
			res = updater.convert_from_9_to_10();
		} else if ( db_version == 10 ) {
			// Holder for the future
			res = updater.convert_from_10_to_11();
		} else if ( db_version == 11 ) {
			// Holder for the future
		} else if ( db_version == 12 ) {
			// Holder for the future
		} else if ( db_version == 13 ) {
			// Holder for the future
		} else if ( db_version == 14 ) {
			// Holder for the future
		} else if ( db_version == 15 ) {
			// Holder for the future
		} else {
			logger.debug( "  Update NOT necessary!" );
		}
		
		if ( res ) {
			updater.setupNextVersion( db_version );
		}
	
		updater.closeConnection();

		logger.debug( "... end Update Database" );
	}
	
	private void readParameters() {
		this.dbName = TmsConfiguration.getProperty( "javax.persistence.jdbc.url", "tms" );
		this.usrname = TmsConfiguration.getProperty( "javax.persistence.jdbc.user", "tms" );
		this.pwd = TmsConfiguration.getProperty( "javax.persistence.jdbc.password", "tms" );
		logger.debug( "Parameters were read successfully: " + this.dbName + ", " + this.usrname + ", " + this.pwd );
	}

	
	private void createConfigTableIfNecessary() {
		String stmtStr;
			
		stmtStr = "select * from configuration";
		if ( executeQuery( stmtStr ) == null ) {
			// Table must be created
			logger.debug( "Table 'configutration must be created" );
			
			stmtStr = "create table configuration ( " 
						+ "id bigint NOT NULL, "
						+ "db_version bigint, "
						+ "PRIMARY KEY (id) "
						+ " )";
		
			if ( executeUpdate( stmtStr )) {
				logger.debug( "'configuration' table has been created" );
			}

			// Initial version shall be provisioned
			logger.debug( "Initial version shall be provisioned" );
			
			stmtStr = "insert into configuration values ( "
		            	+ " 1, 1 "
		            	+ ")";
 
	
			if ( executeUpdate( stmtStr )) {
				logger.debug( "'configuration' table has been provisiioned to initial values" );
			}
			
		}
		
		
	}
	
	public long getDbVersion() {
		long lRes = -1;
		String stmtStr;
		Statement stmt = null;
		ResultSet rs = null;
			
		stmtStr = "select * from configuration";
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery( stmtStr);
			if ( rs != null && rs.next()) {
				lRes = rs.getLong( 2 );
			}
			logger.debug( "DB version was fetched. db_version = " + lRes );
			
		} catch ( SQLException e ) {
			logger.error( "Cannot fetch db version from the record!\n" + e );
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
			}
		}
		
		return lRes;
	}

	private void setupNextVersion( long db_version ) {
		String stmtStr;
		Statement stmt = null;
		ResultSet rs = null;
			
		stmtStr = "select * from configuration";
		
		try {
			stmt = con.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE );
			rs = stmt.executeQuery( stmtStr );
			if ( rs != null && rs.next()) {
				rs.updateLong( 2, db_version + 1 );
	            rs.updateRow();
				logger.debug( "DB version will be = " + (db_version + 1));
			}

			
		} catch ( SQLException e ) {
			logger.error( "Cannot fetch db version from the record!\n" + e );
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
			}
		}
		
	}
	
	private Connection initConnection() {
		
		try {
			Class.forName( "org.postgresql.Driver" );
			logger.debug( "Driver was loaded" );
		} catch (Exception x) {
			logger.error( x );
			logger.error( "Failed to load the driver" );
			return null;
		}
		
		try {
			con = DriverManager.getConnection( "jdbc:postgresql:" + dbName, usrname, pwd );
			logger.debug( "Connection has been obtained!" );
		} catch (SQLException e) {
			logger.error( dbName + " database does not exist!" );
			logger.error( e.toString());
			return null;
		}
		
		return con;
	}
	private void closeConnection() {
		if ( con != null ) {
			try {
				con.close();
				logger.debug( "Connection has been closed!" );
			} catch (SQLException e) {
				logger.error( e );
				logger.error( "Failed to close connection" );
			}
		}
		
	}
	private ResultSet executeQuery( String stmtStr ) {
		ResultSet rs = null;
		
		Statement stmt = null;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery( stmtStr);
			logger.debug( "executeQuery '" + stmtStr + "' succeeded" );
		} catch ( SQLException e ) {
			logger.debug( "executeQuery '" + stmtStr + "' failed:\n" + e );
		} finally {
			try {
				if ( stmt != null )
					stmt.close();
			} catch (SQLException e) {
			}
		}
		
		return rs;
	}

	private boolean executeUpdate( String stmtStr ) {
		boolean bRes = false;
		
		Statement stmt = null;

		try {
			stmt = con.createStatement();
			stmt.executeUpdate( stmtStr);
			bRes = true;
			logger.debug( "executeUpdate '" + stmtStr + "' succeeded" );
		} catch ( SQLException e ) {
			logger.error( "executeUpdate '" + stmtStr + "' failed:\n" + e );
		} finally {
			try {
				if ( stmt != null )
					stmt.close();
			} catch (SQLException e) {
			}
		}
		
		return bRes;
	}
	
	/*
	 * Conversion from version 1 (initial) to version 2:
	 *   - remove unnecessary columns: tmsuser.users_key, project.projects_key 
	 *   - TravelReport table needs to have reference to projects
	 *     
	 */
	private boolean convert_from_1_to_2() {

		String stmtStr;
		String stmtStr2;
/*		
		// Delete users_key from tmsuser table
		stmtStr = "ALTER TABLE tmsuser DROP COLUMN IF EXISTS users_key";
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "Delete users_key from tmsuser table succeeded" );
		} else {
			logger.error( "Delete users_key from tmsuser table failed!" );
			return false;
		}
		
		// Delete projects_key from project table
		stmtStr = "ALTER TABLE project DROP COLUMN IF EXISTS projects_key";
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "Delete projects_key from project table succeeded" );
		} else {
			logger.error( "Delete projects_key from project table failed!" );
			return false;
		}
*/
		// Add project_id column to TravelReport
		stmtStr = "ALTER TABLE travelreport "
					+ "ADD project_id bigint, "
					+ "ADD CONSTRAINT fk_travelreport_project_id FOREIGN KEY (project_id) "
					+ "REFERENCES project (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION";
			
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'travelreport' table has been updated successfully" );
		} else {
			logger.error( "'travelreport' table has NOT been updated successfully!" );
			return false;
		}
		

		// Create collection of Triples: (User, Date, Project).   Most of users worked at one object per day
			// To do that iterate all CheckIn transactions
		ResultSet rs = null;
		ResultSet rs2 = null;
		String xmlStr;
		long numS = 0;
		long numF = 0;
		CheckInOutTransactionData data;
		String	prjCode;
		Date date;
		long user_id;
		long prjId;
		String orgCode;
		ModiHolder holder;
		
		Statement stmt = null;
		PreparedStatement stmt2 = null;
				
		stmtStr = "SELECT * FROM tmstransactions where type = 'checkin'";
		stmtStr2 = "SELECT organisation.code, project.id "
					+ "FROM tmsuser, organisation, project "
					+ "WHERE "
						+ "tmsuser.id = ? AND tmsuser.organisation_id = organisation.id AND "
						+ "project.code=? and project.organisation_id= tmsuser.organisation_id";
		try {
			stmt = con.createStatement();
			stmt2 = con.prepareStatement( stmtStr2 );
			rs = stmt.executeQuery( stmtStr );
			logger.debug( "CheckIn transactions were read!" );
			while ( rs.next()) {
				xmlStr = rs.getString( "transactiondata" );
				if ( xmlStr != null && xmlStr.length() > 100 ) {
					try {
						data = XMLconverter.initFromXML( CheckInOutTransactionData.class, xmlStr );
						prjCode = data.getProject().getCode();
						date = data.getDateTime();	
						user_id = rs.getLong( "tmsuser_id" );
						
						stmt2.setLong( 1, user_id );
						stmt2.setString( 2, prjCode );
						rs2 = stmt2.executeQuery();
						if ( rs2.next()) {
							orgCode = rs2.getString( "code" );   
							prjId = rs2.getLong( "id" );
							holder = new ModiHolder( user_id, DateUtil.dateNoDelimToString( date ), prjId, prjCode, orgCode );
							add( holder );
							
							numS++;
						} else {
							logger.debug( "  Record not found for user_id=" + user_id + " and project=" + prjCode );
						numF++;
						}
					} catch (JAXBException e) {
						logger.error( "Failed to convert XML to TransactionData: " + xmlStr );
						numF++;
					}
				} else {
					numF++;
				}
				
			}
			
		} catch ( SQLException e ) {
			logger.error( e );
		} catch ( Exception e ) {
			logger.error( e );
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
			}
		}

		int count = 0;
		for ( MapByProjects mbp : mapHolders.values()) {
			count = count + mbp.values().size();
		}
		
		
		logger.info( "CheckIn events successfully served: " + numS + ", failed: " + numF 
					+ ". Num of saved entries: " + mapHolders.values().size());

		// for all TravelReports
			// Find Triple with the same Date+User. Use the project to set up TravelReport.project_id
		int inttraveltype;
		String key; 
		int size;

		stmtStr = "SELECT * FROM travelreport";

		logger.debug( "************  Start to update Travel Report *****************" );
		
		try {
			stmt = con.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE );
			rs = stmt.executeQuery( stmtStr );
			logger.debug( "All TravelReports were read!" );
			while ( rs.next()) {
				user_id = rs.getLong( "user_id" );
				date = rs.getDate( "date" );
				inttraveltype = rs.getInt( "inttraveltype" );
				
				prjId = -1;

				// Find ProjectsMap
				key = Long.toString( user_id ) + DateUtil.dateNoDelimToString( date ); 
				
				if ( mapHolders.containsKey( key )) {
					mbp = mapHolders.get( key );
					size = mbp.values().size();
					if ( size == 1 ) {
						// TaskReport can be updated easily
						holder = mbp.values().iterator().next();
						prjId = holder.project_id; 
						logger.debug( "Ok. One report - one project. Project Id = " + prjId );
					} else {
						logger.debug( "User worked in " + size + " projects!" );
						for ( ModiHolder mh : mbp.values()) {
							size = mbp.values().size();
							if ( inttraveltype == 0 ) {
								logger.debug( "Home travel was found." );
								// Tyo matka. The 1st Holder probably
								if ( mh.order == 1 || size  == 1 ) {
									prjId = mh.project_id;
									mbp.remove( mh.projectCode );
									break;
								}
							} else {
								if ( mh.order != 1 || size  == 1 ) {
									prjId = mh.project_id;
									mbp.remove( mh.projectCode );
									break;
								}
							}
						}
						
					}
					
				}
				
				if ( prjId == -1 ) {
					if ( user_id == 4338 ) {
						prjId = 4366; 
					} else if ( user_id == 3490 ) {
						prjId = 3565; 
					} else if ( user_id == 3458 ) {
						prjId = 3787; 
					} else if ( user_id == 3456 ) {
						prjId = 3787; 
					}
				}
						
				if ( prjId == -1 ) {
					logger.error( "ERROR!!! Project was not found for travel report: " + rs.getString( "id" ));
				}
				rs.updateLong( "project_id", prjId );
	            rs.updateRow();
				
			}
		} catch ( SQLException e ) {
			logger.error( e );
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
			}
		}

			
		
		return true;
	}

	Map<String, MapByProjects> mapHolders = new HashMap<String, MapByProjects>();
	MapByProjects mbp;
	
	public void add( TmsDBUpdate.ModiHolder holder) {
		if ( mapHolders.containsKey( holder.getKey())) {
			mbp = mapHolders.get( holder.getKey());
		} else {
			mbp = new MapByProjects();
			mapHolders.put( holder.getKey(), mbp); 
		}
		mbp.add( holder );
	}
	 
	@SuppressWarnings( "serial" )
	class MapByProjects extends HashMap<String, TmsDBUpdate.ModiHolder> {
		
		int order = 0;
		public void add( ModiHolder holder) {
			if ( this.containsKey( holder.projectCode )) {
//				logger.debug( "  Holder with ProjectCode: '" + holder.projectCode + "' exist already!" ); 
			} else {
//				logger.debug( "  Holder with ProjectCode: '" + holder.projectCode + "' will be added!" );
				this.order++;
				holder.order = this.order;
				this.put( holder.projectCode, holder );
//				logger.debug( "  Holder added: " + holder );
			}
		}
	}
	 
	class ModiHolder {
		long user_id;
		String dateStr;
		long project_id;
		String projectCode;
		String orgCode;
		int order;
		
		public ModiHolder( long user_id, String dateStr, long project_id, String projectCode, String orgCode ) {
			this.user_id = user_id;
			this.dateStr = dateStr;
			this.project_id = project_id;
			this.projectCode = projectCode;
			this.orgCode = orgCode;
			order = 0;
		}

		String getKey() {
			return Long.toString( user_id ) + dateStr; 
//			return Long.toString( user_id ) + dateStr + Long.toString( project_id ); 
		}		

		
		public String toString() {
			String res =  "  ( "
						+ user_id + ", "
						+ dateStr + ", "
						+ project_id + ", "
						+ projectCode + ", "
						+ orgCode + ", "
						+ "order=" + order
						+ " )";
		
			return res;
		}
	}		

	private boolean convert_from_2_to_3() {

		String stmtStr;

		// Create check_in_out table
		stmtStr = "CREATE TABLE check_in_out ( "
				+ "id bigint NOT NULL, "
				+ "consistencyversion bigint NOT NULL, "
				+ "checkoutbyclient boolean, "
				+ "deleted boolean NOT NULL, "
				+ "datecheckedin timestamp without time zone, "
				+ "datecheckedout timestamp without time zone, "
				+ "inlatitude double precision, "
				+ "inlongitude double precision, "
				+ "outlatitude double precision, "
				+ "outlongitude double precision, "
				+ "project_id bigint, "
				+ "userstate_id bigint, "
				+ "CONSTRAINT check_in_out_pkey PRIMARY KEY (id ), "
				+ "CONSTRAINT fk_check_in_out_project_id FOREIGN KEY ( project_id ) "
				+ 	"REFERENCES project (id) MATCH SIMPLE "
				+ 	"ON UPDATE NO ACTION ON DELETE NO ACTION, "
				+ "CONSTRAINT fk_check_in_out_userstate_id FOREIGN KEY ( userstate_id ) "
				+ 	"REFERENCES tmsuserstate (id) MATCH SIMPLE "
			    + 	"ON UPDATE NO ACTION ON DELETE NO ACTION "
			    + " )";
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'tmsuserstate' table has been updated successfully" );
		} else {
			logger.error( "'tmsuserstate' table has NOT been updated successfully!" );
			return false;
		}
		  
		// Alter tmsuserstate table to point out active CheckInOutRecord in check_in_out table
		stmtStr = "ALTER TABLE tmsuserstate "
				+ "ADD COLUMN check_in_out_id bigint, "
				+ "ADD CONSTRAINT fk_tmsuserstate_check_in_out_id FOREIGN KEY ( check_in_out_id )  "
				+ "REFERENCES check_in_out( id )";

		if ( executeUpdate( stmtStr )) {
			logger.debug( "'tmsuserstate' table has been updated successfully" );
		} else {
			logger.error( "'tmsuserstate' table has NOT been updated successfully!" );
			return false;
		}
		  
		return true;
	}

	private boolean convert_from_3_to_4() {

		String stmtStr;

		// 1. Create Accessrights table
		stmtStr = "CREATE TABLE accessrights ( "
				+ "id bigint NOT NULL, "
				+ "consistencyversion bigint NOT NULL, "
				+ "deleted boolean NOT NULL, "

				+ "user_id bigint, "
				+ "code character varying(255), "
				+ "read boolean NOT NULL, "
				+ "write boolean NOT NULL, "
				+ "add boolean NOT NULL, "
				+ "del boolean NOT NULL, "
				+ "CONSTRAINT accessrights_pkey PRIMARY KEY ( id ), "
				
				+ "CONSTRAINT fk_accessrights_user_id FOREIGN KEY (user_id) "
				+ "REFERENCES tmsuser (id) MATCH SIMPLE "
				+ "ON UPDATE NO ACTION ON DELETE NO ACTION "
			    + " )";
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'Accessrights' table has been updated successfully" );
		} else {
			logger.error( "'Accessrights' table has NOT been updated successfully!" );
			return false;
		}
		  
		// 2. Alter Project table. Add 3 date fields
		stmtStr = "ALTER TABLE project "
				+ "ADD COLUMN start date, "
				+ "ADD COLUMN endPlanned date, "
				+ "ADD COLUMN endReal date, "
				+ "ADD COLUMN address character varying(255), "
	  			+ "ADD COLUMN latitude double precision, "
	  			+ "ADD COLUMN longitude double precision";

		if ( executeUpdate( stmtStr )) {
			logger.debug( "'Project' table has been updated successfully" );
		} else {
			logger.error( "'Project' table has NOT been updated successfully!" );
			return false;
		}
		  
		// 3. Update Task table. Add field to support Tasks list per Organisation
		stmtStr = "ALTER TABLE task "
				+ "ADD COLUMN organisation_id bigint, "
				+ "ADD CONSTRAINT fk_task_organisation_id FOREIGN KEY (organisation_id) REFERENCES organisation(id)";

		if ( executeUpdate( stmtStr )) {
			logger.debug( "'Task' table has been updated successfully" );
		} else {
			logger.error( "'Task' table has NOT been updated successfully!" );
			return false;
		}

		// 4. Put reference to the Organisation into the Task records
		putRefToOrgIntoTasks();
		
		return true;
	}

	
	private boolean convert_from_4_to_5() {

		String stmtStr;

		// 1. Add manager_flag field to TmsUser table
		stmtStr = "ALTER TABLE tmsuser "
				+ "ADD COLUMN project_manager_flag boolean NOT NULL DEFAULT false, "
				+ "ADD COLUMN line_manager_flag boolean NOT NULL DEFAULT false, "
				+ "DROP COLUMN substitutedby_id, "
			  	+ "ADD COLUMN secgroup_id bigint";
				
				;
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'TmsUser' table has been updated successfully" );
		} else {
			logger.error( "'TmsUser' table has NOT been updated successfully!" );
			return false;
		}
		
		// 2. Create securitygroup table
		stmtStr = "CREATE TABLE securitygroup ( "
				+ "id bigint NOT NULL, "
				+ "consistencyversion bigint NOT NULL, "
				+ "deleted boolean NOT NULL, "
				+ "organisation_id bigint, "
				+ "code character varying(255), "
				+ "defname character varying(255), " 
			    + "CONSTRAINT securitygroup_pkey PRIMARY KEY ( id ), "
				+ "CONSTRAINT fk_securitygroup_organisation_id FOREIGN KEY ( organisation_id ) "
			    + "REFERENCES organisation (id) MATCH SIMPLE "
				+ "ON UPDATE NO ACTION ON DELETE NO ACTION "    
				+ ")";

		if ( executeUpdate( stmtStr )) {
			logger.debug( "'SecurityGroup' table has been created successfully" );
		} else {
			logger.error( "'SecurityGroup' table has NOT been created successfully!" );
			return false;
		}
		
		// 3. Update AccessRights table
		
		stmtStr = "ALTER TABLE accessrights "
				+ "DROP COLUMN code, "
				+ "DROP COLUMN user_id, "
				+ "ADD COLUMN owneruser_id bigint, "
				+ "ADD COLUMN ownergroup_id bigint, "
				+ "ADD COLUMN function int, "
				+ "ADD CONSTRAINT fk_accessrights_owneruser_id FOREIGN KEY ( owneruser_id ) "
				+ "REFERENCES tmsuser (id) MATCH SIMPLE "
				+ "ON UPDATE NO ACTION ON DELETE NO ACTION, "
				+ "ADD CONSTRAINT fk_accessrights_ownergroup_id FOREIGN KEY ( ownergroup_id ) "
				+ "REFERENCES securitygroup (id) MATCH SIMPLE "
				+ "ON UPDATE NO ACTION ON DELETE NO ACTION";    

		if ( executeUpdate( stmtStr )) {
			logger.debug( "'AccessRights' table has been updated successfully" );
		} else {
			logger.error( "'AccessRights' table has NOT been updated successfully!" );
			return false;
		}

		// 4. Update TmsUser table to refer to SecurityGroups
		
		stmtStr = "ALTER TABLE tmsuser "
				+ "ADD CONSTRAINT fk_tmsuser_secgroup_id FOREIGN KEY (secgroup_id) "
				+ "REFERENCES securitygroup (id) MATCH SIMPLE "
				+ "ON UPDATE NO ACTION ON DELETE NO ACTION";
				
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'TmsUser' table has been updated successfully" );
		} else {
			logger.error( "'TmsUser' table has NOT been updated successfully!" );
			return false;
		}
		
		// 5. Update Check_in_out table to add location accuracy fields (for check-in and check-out separately)
		
		stmtStr = "ALTER TABLE check_in_out "
				+ "ADD COLUMN inaccuracy integer, "
				+ "ADD COLUMN outaccuracy integer";
				
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'TmsUser' table has been updated successfully" );
		} else {
			logger.error( "'TmsUser' table has NOT been updated successfully!" );
			return false;
		}

		// 5. Update Project table to add location accuracy field (not used but for simplicity. Can be removed later)
		
		stmtStr = "ALTER TABLE project "
				+ "ADD COLUMN accuracy integer";
				
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'TmsUser' table has been updated successfully" );
		} else {
			logger.error( "'TmsUser' table has NOT been updated successfully!" );
			return false;
		}

		// 6. Update tmsuserstate table. Removes unnecessary fields
	    
		stmtStr = "ALTER TABLE tmsuserstate "
				+ "DROP COLUMN datecheckedin, "
				+ "DROP COLUMN datecheckedout, "
				+ "DROP COLUMN project_id";
				
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'TmsUser' table has been updated successfully" );
		} else {
			logger.error( "'TmsUser' table has NOT been updated successfully!" );
			return false;
		}
		
		return true;
	}

	private boolean convert_from_5_to_6() {
		boolean res = false;

		String stmtStr;

		// 1. Update Organisation table
		//		- Add reference to Organisationn's Service Owner
		//		- Add column to save properties
	    
		stmtStr = "ALTER TABLE organisation "
				+ "ADD COLUMN serviceowner_id bigint, "
				+ "ADD COLUMN propstring character varying(4096), "
				+ "ADD CONSTRAINT fk_serviceowner_id FOREIGN KEY ( serviceowner_id ) REFERENCES tmsuser( id )";
		 
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'Organisation' table has been updated successfully" );
			res = true;
		} else {
			logger.error( "'Organisation' table has NOT been updated successfully!" );
			return false;
		}
		
		
		// 2. Add Fake Task and assign it to all Projects in All Organisations
		for( Organisation org : OrganisationFacade.getInstance().getOrganisations()) {
			TmsConfiguration.setProperty( org.getCode(), "company.projects.fakeusage", Boolean.TRUE.toString());
//			res = OrganisationFacade.getInstance().fakeAssignTasks( org.getCode());
			if ( !res ) break;
		}

		// 3. Fill Default Securitry groups and assign 'sevastianov" full access

		for ( Organisation org : OrganisationFacade.getInstance().getOrganisations()) {
			AccessRightsFacade.getInstance().updateSecurityGroups( org );
		}
		
		TmsAccount account = AuthenticationFacade.getInstance().findByUserName( "sevastia" );
		if ( account != null && account.getUser() != null ) {
			SecurityGroup sg = account.getUser().getOrganisation().getSecurityGroups().get( "008" );
			if ( sg != null ) {
				account.getUser().getContext().setSecGroup( sg );
			}
		}
		
		return res;
	}
	
	private boolean convert_from_6_to_7() {
		boolean res = false;

		logger.debug( "  Update from ver. 6 to ver. 7 started..." );
		
		String stmtStr;

		// 1. Add "pwdMustBeChanged" field to the tmsaccount table
		//		- Will be used to force password changes
	    
		stmtStr = "ALTER TABLE tmsaccount "
				+ "ADD COLUMN pwdMustBeChanged boolean NOT NULL DEFAULT false";
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'TmsAccount' table has been updated successfully" );
			res = true;
		} else {
			logger.error( "'TmsAccount' table has NOT been updated successfully!" );
			return false;
		}
		
		
		// 2. Add new fields to the TmsUser table
		//	-
		//	-
		//	-
		//	-
		//	-

		stmtStr = "ALTER TABLE tmsuser "
				+ "ADD COLUMN address character varying(255), "
				+ "ADD COLUMN kelaCode character varying(255), "
				+ "ADD COLUMN taxNumber character varying(255), "
				+ "ADD COLUMN email character varying(255), "
				+ "ADD COLUMN mobile character varying(255)";

		if ( executeUpdate( stmtStr )) {
			logger.debug( "'TmsUser' table has been updated successfully" );
			res = true;
		} else {
			logger.error( "'TmsUser' table has NOT been updated successfully!" );
			return false;
		}
		
		logger.debug( "  ... update from ver. 6 to ver. 7 ended!" );
		return res;
	}
	
	private boolean convert_from_7_to_8() {
		boolean res = false;

		logger.info( "  Update from ver. 7 to ver. 8 started..." );
		
		String stmtStr;

		// 1. Add new fields:
		// 		String address;
		// 		String tunnus;
		// 		String info;
		// 		String phone;
		// 		String email;
		//    to the Organisation table
	    
		stmtStr = "ALTER TABLE organisation "
				+ "ADD COLUMN address character varying(255), "
				+ "ADD COLUMN tunnus character varying(255), "
				+ "ADD COLUMN info character varying(4096), "
				+ "ADD COLUMN phone character varying(255), "
				+ "ADD COLUMN email character varying(255)";

		if ( executeUpdate( stmtStr )) {
			logger.debug( "'Organisation' table has been updated successfully" );
			res = true;
		} else {
			logger.error( "'Organisation' table has NOT been updated successfully!" );
			return false;
		}
		
		logger.debug( "  ... update from ver. 7 to ver. 8 ended!" );
		return res;
	}
	
	
	Map< Long, Long> mapTasks = new HashMap< Long, Long>();
	private void putRefToOrgIntoTasks() {
		
		Statement stmt = null;
		ResultSet rs = null;
		long orgId, taskId;

		String stmtStr = 
				"SELECT t.id, o.id  "
				+ "from organisation o, task t, projecttask pt, project p "
				+ "where pt.project_id = p.id AND p.organisation_id = o.id AND pt.task_id=t.id;";

		logger.debug( "************  Start to read Tasks and fill the map by Org reference *****************" );
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery( stmtStr );
			logger.debug( "All Tasks and Org-s were read!" );
			
			
			while ( rs.next()) {
				taskId = rs.getLong( 1 );
				orgId = rs.getLong( 2 );
				mapTasks.put( Long.valueOf( taskId ), Long.valueOf( orgId ));
				
			}
		} catch ( SQLException e ) {
			logger.error( e );
			return;
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
			}
		}
		logger.debug( "************  ... map has been filled *****************" );

		stmtStr = 
				"SELECT * "
				+ "from task";

		logger.debug( "************  Start to update Tasks by Org references *****************" );
		
		try {
			stmt = con.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE );
			rs = stmt.executeQuery( stmtStr );
			
			while ( rs.next()) {
				taskId = rs.getLong( "id" );
				orgId = mapTasks.get( Long.valueOf( taskId ));
				
				rs.updateLong( "organisation_id", orgId );
	            rs.updateRow();
				
			}
		} catch ( SQLException e ) {
			logger.error( e );
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
			}
		}
		
		logger.debug( "************  ... Tasks were updated! *****************" );
		
		
	}

	private boolean convert_from_8_to_9() {

		String stmtStr;

		// Create check_in_out table
		
		stmtStr = "CREATE TABLE TAXREPORT ( "
				+ "id BIGINT NOT NULL, "
				+ "CODE VARCHAR(1048576), "
				+ "CONSISTENCYVERSION BIGINT NOT NULL, "
				+ "DATE DATE, "
				+ "deleted BOOLEAN NOT NULL, "
				+ "LASTMODIDATE DATE, "
				+ "STATUS INTEGER, "
				+ "xml_data VARCHAR(1048576), "
				+ "TYPE INTEGER, "
				+ "ORGANISATION_id BIGINT, "
				+ "CONSTRAINT taxreport_pkey PRIMARY KEY (id ), "
				+ "CONSTRAINT fk_taxreport_org_id FOREIGN KEY ( organisation_id ) "
				+ 	"REFERENCES organisation (id) MATCH SIMPLE "
			    + 	"ON UPDATE NO ACTION ON DELETE NO ACTION "
			    + " )";
		
				
		
		
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'taxreport' table has been created successfully" );
		} else {
			logger.error( "'taxreport' table has NOT been created successfully!" );
			return false;
		}
		  
		return true;
	}
	
	private boolean convert_from_9_to_10() {

		String stmtStr;

		// Create CONTRACT table
		
		stmtStr = "CREATE TABLE CONTRACT ( "
				+ "id BIGINT NOT NULL, "
				+ "CONSISTENCYVERSION BIGINT NOT NULL, " 
				+ "deleted BOOLEAN NOT NULL, "
				+ "CONTRACTOR_id BIGINT, "
				+ "PROJECT_id BIGINT, "
				+ "SUBCONTRACTOR_id BIGINT, "
				+ "CONSTRAINT contract_pkey PRIMARY KEY (id), "
				+ "CONSTRAINT FK_CONTRACT_CONTRACTOR_id FOREIGN KEY ( CONTRACTOR_id ) "
				+ 	"REFERENCES ORGANISATION (id) MATCH SIMPLE "
			    + 	"ON UPDATE NO ACTION ON DELETE NO ACTION, "
				+ "CONSTRAINT FK_CONTRACT_PROJECT_id FOREIGN KEY (PROJECT_id) "
				+   "REFERENCES PROJECT (id) MATCH SIMPLE "
			    + 	"ON UPDATE NO ACTION ON DELETE NO ACTION, "
				+ "CONSTRAINT FK_CONTRACT_SUBCONTRACTOR_id FOREIGN KEY (SUBCONTRACTOR_id) "
				+   "REFERENCES ORGANISATION (id) MATCH SIMPLE "
			    + 	"ON UPDATE NO ACTION ON DELETE NO ACTION "
			    + " )";
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'contract' table has been created successfully" );
		} else {
			logger.error( "'contract' table has NOT been created successfully!" );
			return false;
		}
		  
		return true;
	}

	private boolean convert_from_10_to_11() {

		logger.debug( "  Update from ver. 10 to ver. 11 started..." );
		logger.debug( "    1. Create 'measurementunit' table" );
		logger.debug( "    2. Fill 'measurementunit' table" );
		logger.debug( "    2. Update 'task' table. Add ref to 'measurement_unit' record" );
		
		String stmtStr;

		// 1. Create 'measurementunit' table
		//
		
		stmtStr = "CREATE TABLE MEASUREMENTUNIT ( "
				+ "id BIGINT NOT NULL, "
				+ "CONSISTENCYVERSION BIGINT NOT NULL, " 
				+ "deleted BOOLEAN NOT NULL, "
				+ "defname character varying(255), "
				+ "resourcename character varying(255), "
				+ "description character varying(255), "
				+ "CONSTRAINT measurement_pkey PRIMARY KEY (id) "
			    + " )";
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'MeasurementUnit' table has been updated successfully" );
		} else {
			logger.error( "'MeasurementUnit' table has NOT been updated successfully!" );
			return false;
		}
		
		// 2. Fill in 'measurementunit' table
		//
		
		stmtStr = "INSERT INTO MEASUREMENTUNIT VALUES "
            	+ "(  1, 1, false, 'm',   'measure.meter',   'meter/running meter'), "
            	+ "(  2, 1, false, 'm2',  'measure.2meter',  'square meter'), "
            	+ "(  3, 1, false, 'm3',  'measure.3meter',  'cubical meter'), "
            	+ "(  4, 1, false, 'kg',  'measure.kg',      'kilogram'), "
            	+ "(  5, 1, false, 't',   'measure.tn',      'ton'), "
            	+ "(  8, 1, false, 'pcs', 'measure.pcs',     'pieces')";
            	
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'MeasurementUnit' table has been fulfilled successfully" );
		} else {
			logger.error( "'MeasurementUnit' table has NOT been fulfilled successfully!" );
			return false;
		}
		
		
		// 3. Update 'task' table. Add ref to 'measurement_unit' record
		//		- Will be used to force password changes
	    
		stmtStr = "ALTER TABLE TASK "
				+ "ADD COLUMN measure_id bigint, "
				+ "ADD CONSTRAINT fk_task_measure_id FOREIGN KEY (measure_id) "
				+ "REFERENCES measurementunit (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION";
		
		if ( executeUpdate( stmtStr )) {
			logger.debug( "'Task' table has been updated successfully" );
		} else {
			logger.error( "'Task' table has NOT been updated successfully!" );
			return false;
		}
		
		
		logger.debug( "  ... update from ver. 10 to ver. 11 ended!" );
		return true;
	}
	
	
}
