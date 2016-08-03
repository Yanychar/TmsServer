package com.c2point.tms.tools.imprt;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.StringUtils;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.tools.LoggerIF;

public class v10PersonsImportProcessor extends DataImportProcessor {
	private static Logger logger = LogManager.getLogger( v10PersonsImportProcessor.class.getName());

	private OrganisationFacade	orgFacade;
	

	public v10PersonsImportProcessor() {
		this( null );
	}
	
	public v10PersonsImportProcessor( LoggerIF importLogger ) {
		super( importLogger );
		
		orgFacade = OrganisationFacade.getInstance();
		
	}
	
	protected ValidationResult validateLine( String [] nextLine, int lineNum ) {
		
		ValidationResult vldRes = ValidationResult.VALIDATED;

		if ( logger.isDebugEnabled()) {
			logger.debug( "    Start String [] validation for conversion into TMSUser ..." );
		    // nextLine[] is an array of values from the line

			// Employee data shall have 4 or 5 fields. 5th is optional
			// 1st field: Calculation group. Not used yet
			// 2nd field: Person ID ==>> TmsUser.code
			// 3rd field: Person Last Name ==>> TmsUser.lastName
			// 4th field: Person First Name ==>> TmsUser.firstName
			// 5th field: Person Resource Code. We do not use it but keep ==>> TmsUser.resource
			logger.debug( "    Line # " + lineNum + " length=" + nextLine.length ); 
			String outstr = "      nextLine []: ( ";
			for ( int j = 0; j < nextLine.length; j++ )
				outstr = outstr.concat( "'" + (( nextLine[ j ] != null ) ? nextLine[ j ] : "NULL" ) + "'  " );
			outstr = outstr.concat( ")" );
			logger.debug( outstr ); 
		}

		if ( nextLine == null 
			|| nextLine[0] != null  && nextLine[0].trim().length() == 0
			|| nextLine[0] != null && StringUtils.startsWith( StringUtils.stripStart( nextLine[0], null ), "#" )  
		) {
			if ( logger.isDebugEnabled()) logger.debug( "    Line # " + lineNum + ".  This is empty or comment line" );
			vldRes = ValidationResult.COMMENT;
		} else if(   nextLine.length < 4 
				   ||nextLine[ 0 ] == null 
				   || nextLine[ 1 ] == null 
				   || nextLine [ 2 ] == null 
				   || nextLine [ 3 ] == null ) {
			error( "  ERROR: Line #" + lineNum + ". Mandatory elements cannot be empty." );
			logger.error( "          Elements are missing!!!" );
			vldRes = ValidationResult.FAILED;
		} else if ( nextLine [ 0 ].length() > 2 ) { 
				error( "  ERROR: Line #" + lineNum + ". Accounting Group code cannot be longer than 2!" );
				vldRes = ValidationResult.FAILED;
		} else if( nextLine[ 1 ].length() == 0 ) {
			error( "  ERROR: Line #" + lineNum + ". Personnel code cannot be empty!" );
			vldRes = ValidationResult.FAILED;
		} else {
			
			// Otherwise the record is valid!!!
			//    User can be created and added
			if ( logger.isDebugEnabled()) logger.debug( "    Line # " + lineNum + " is valid" );
			
		}

		if ( logger.isDebugEnabled()) {
			logger.debug( "    ... end of String [] validation" );
		}
		
		return vldRes;
	}

	protected boolean processNextLine( String [] nextLine, int lineNumber ) {
		boolean bRes = true;

		TmsUser tmsUser;
		
		if ( logger.isDebugEnabled())
			logger.debug( "    Start creation of TmsUser from String [] and storing it in DB" );
		
		// Convert to 
		tmsUser = new TmsUser( nextLine[ 1 ], nextLine[ 3 ].trim(), nextLine[ 2 ].trim());
		// Add Counting Group
		tmsUser.setCountgroup( StringUtils.leftPad( nextLine[0], 2, '0' ));
		
		// Specific to UHR/SS and v10. Code '01' means worker, '02' means manager
		if ( nextLine[ 1 ] != null && nextLine[ 1 ].compareTo( "02" ) == 0 ) {
			// Setup as project owner
			tmsUser.setProjectManager( true );
			// Security group as manager
			tmsUser.getContext().setSecGroup( organisation.getSecurityGroups().get( "004" ));
		} else {
			// Setup as worker
			tmsUser.setProjectManager( false );
			// Security group as worker
			tmsUser.getContext().setSecGroup( organisation.getSecurityGroups().get( "003" ));
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "Created " + tmsUser );
		// Store user in the DB

		// Add User if necessary
		tmsUser = addOrUpdateUser( tmsUser );

		if ( tmsUser != null ) {
			// Set reference to the MERGED Organisation object
//			this.organisation = tmsUser.getOrganisation();

			// Not necessary to create account. It will be done manually!
/*			
			// Add TmsAccount if necessary
			// If user deleted than no account shall be created!!!
			if ( !tmsUser.isDeleted()) {
				if ( AuthenticationFacade.getInstance().addAccountDefault( tmsUser ) == null ) {
					error( "  ERROR: Line #: " + lineNumber + ". Cannot add user account" );
				}
			} else {
				logger.debug( "User " + tmsUser.getLastAndFirstNames() + " has been deleted. No account shall be created!" );
			}
*/			
		
		} else {
			error( "  ERROR: Line #: " + lineNumber + ". Cannot add/update user " );
			bRes = false;
		}
		
		if ( bRes ) {

			info( "  Line # " + lineNumber + ": Import '" + tmsUser.getLastAndFirstNames() + "' data... OK" );
			
		}
			
		if ( logger.isDebugEnabled())
			logger.debug( "    ... end of TmsUser creation and storing in DB. Res = " + bRes );
			
			
		return bRes;
	}

	protected void processComment( String [] nextLine, int lineNumber ) {

		info( "  Line # " + lineNumber + ": empty line" );
		
	}
	
	@Override
	protected String getImportDir() { return this.organisation.getProperties().getProperty( "company.persons.import" ); } 
	protected String getArchiveDir() { return this.organisation.getProperties().getProperty( "company.persons.archive" ); }

	protected String getOriginalExt() { return this.organisation.getProperties().getProperty( "company.persons.original.ext", "" ); }
	protected String getProcessedExt() { return this.organisation.getProperties().getProperty( "company.persons.processed.ext", "" ); }
	protected String getErrorExt() { return this.organisation.getProperties().getProperty( "company.persons.error.ext", "" ); }

	protected boolean toRename() { 
		return Boolean.parseBoolean( this.organisation.getProperties().getProperty( "company.persons.rename", "true" ));
	}
	protected boolean toMove() {
		return Boolean.parseBoolean( this.organisation.getProperties().getProperty( "company.persons.move", "true" ));
	}
	protected boolean toDelete() {
		return Boolean.parseBoolean( this.organisation.getProperties().getProperty( "company.persons.delete", "true" ));
	}

	
	private TmsUser addOrUpdateUser( TmsUser user ) {

		if ( user == null )
			throw new IllegalArgumentException( "Valid User cannot be null!" );
		if ( this.organisation == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		Organisation org =  
				DataFacade.getInstance().find( Organisation.class, this.organisation.getId());
		
		if ( org == null )
			throw new IllegalArgumentException( "Cannot find Organisation!" );

		// Determine that 
		TmsUser foundUser = findUser( org, user );
		if ( foundUser != null ) {

			return orgFacade.updateUser( org, foundUser );
			
		} else {

			return orgFacade.addUser( org, user );
			
		}
		
	}
	
	private TmsUser findUser( Organisation org, TmsUser user ) {
		
		TmsUser foundUser = null;
		
		foundUser = org.getUser( user.getCode());
		
		// If not found check the code without leading 0 and normalized in general
		if ( foundUser == null ) {
			
			String subCode = StringUtils.stripStart( user.getCode().trim(),"0" );
			
			for ( TmsUser tmpUser : org.getUsers().values()) {
				if ( tmpUser != null && tmpUser.getCode() != null 
					&&
					 tmpUser.getCode().matches( "^[0]*" + subCode + "$" )
				) {
					foundUser = user;
					foundUser.setCode( tmpUser.getCode());
					break;
				}
			}
			
			
		}
		
		return foundUser;
	}
	
		
}
