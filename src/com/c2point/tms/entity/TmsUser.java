package com.c2point.tms.entity;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SecurityContext;

@Entity
@NamedQueries({
	@NamedQuery( name = "findUserByUsrName", 
	query = "SELECT user FROM TmsUser user " +
	 			"WHERE user.lastName = :usrName AND " +
	 			"user.organisation = :org " +
				"ORDER BY user.lastName ASC"
	),
	@NamedQuery( name = "findUserByCode", 
	query = "SELECT user FROM TmsUser user " +
	 			"WHERE user.code = :usrCode AND " +
	 			"user.organisation = :org"
	),
	@NamedQuery( name = "findUserByState", 
	query = "SELECT user FROM TmsUser user " +
				"WHERE user.userState = :state AND " +
				"user.organisation = :org"
	),
	@NamedQuery( name = "listUsers", 
	query = "SELECT user FROM TmsUser user " +
				"WHERE user.organisation = :org AND " +
				"user.deleted = false"
	),
})
public class TmsUser extends SimplePojo {
	private static Logger logger = LogManager.getLogger( TmsUser.class.getName());
	
	private String code;
	private String firstName;
	private String midName;
	private String lastName;
	private String resource;
	
	@OneToOne( cascade={CascadeType.ALL}, fetch=FetchType.EAGER )
	private TmsUserState userState;

	
	@ManyToOne
	private TmsUser manager;
	@OneToMany( mappedBy = "manager",
				cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH },
				fetch=FetchType.LAZY )
	@MapKey( name = "code" )
	private Map<String, TmsUser> subordinates = new HashMap<String, TmsUser>();
	
	@ManyToOne
	private Organisation organisation;
	
	@Column( length=10 )
    private String countgroup;

	// list of projects where this user is Project manager
	@OneToMany( mappedBy = "projectManager",
				cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH },
				fetch=FetchType.LAZY )
	@MapKey( name = "code" )
	private Map<String, Project> projects = new HashMap<String, Project>();

/*	
	// Access Rights
	@OneToMany( mappedBy = "ownerUser",
			cascade = { CascadeType.ALL },
			fetch=FetchType.LAZY )
	@MapKey( name = "function" )
	private Map< SupportedFunctionType, AccessRight > mapOfAR = 
			new HashMap< SupportedFunctionType, AccessRight >( SupportedFunctionType.size());
*/	
	@Column(name="project_manager_flag")
	private boolean isProjectManager = false;

	@Column(name="line_manager_flag")
	private boolean isLineManager = false;

	
	private SecurityContext context;
	
	@OneToOne( mappedBy = "tmsUser", cascade = { CascadeType.ALL }, fetch=FetchType.EAGER )
	private TmsAccount account;	
	

	private String address;
	private String kelaCode;
	private String taxNumber;
	private String email;
	private String mobile;
	
	/**
	 * 
	 */
	public TmsUser() {
		this( "", "", "", "" );
	}

	/**
	 * @param code
	 * @param firstName
	 * @param midName
	 * @param lastName
	 */
	public TmsUser( String code, String firstName, String midName, String lastName ) {
		super();
		this.code = code;
		this.firstName = firstName;
		this.midName = midName;
		this.lastName = lastName;
		this.organisation = null;

		this.account = null;
		this.address = "";
		this.kelaCode = "";
		this.taxNumber = "";
		this.email = "";
		this.mobile = "";
		
		userState = new TmsUserState();
		
		this.isProjectManager = false;
		this.isLineManager = false;
		
		context = new SecurityContext();
	}
	public TmsUser( String code, String firstName, String lastName ) {
		this( code, firstName, "", lastName);
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the midName
	 */
	public String getMidName() {
		return midName;
	}

	/**
	 * @param midName the midName to set
	 */
	public void setMidName(String midName) {
		this.midName = midName;
	}

	/**
	 * @return the Last Name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the Last Name to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * @return First Name + Last Name
	 */
	public String getFirstAndLastNames() {
		return ( this.getFirstName() != null ? this.getFirstName() : "" )
			   + " " 
			   + ( this.getLastName() != null ? this.getLastName() : "" );
	}

	public String getLastAndFirstNames() {
		return ( this.getLastName() != null ? this.getLastName() : "" )
			   + " " 
			   + ( this.getFirstName() != null ? this.getFirstName() : "" );
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public TmsAccount getAccount() {
		return account;
	}

	public void setAccount( TmsAccount account ) {
		
		if ( account != null ) {
			account.setUser( this );
		}
		
		this.account = account;
		
	}

	
	public boolean isCheckedIn() {
		return userState.isCheckedIn();
	}
	public void setCheckedIn( boolean checkedIn ) {
		userState.setCheckedIn( checkedIn );
	}

	/**
	 * @return the userState
	 */
	public TmsUserState getUserState() {
		return userState;
	}

	/**
	 * @param userState the userState to set
	 */
	public void setUserState(TmsUserState userState) {
		this.userState = userState;
	}

	public TmsUser getManager() {
		return manager;
	}

	// Cannot be used outside. Use business method addSubordinate
	protected void setManager( TmsUser manager ) {
		this.manager = manager;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation( Organisation organisation ) {
		this.organisation = organisation;
	}

	public Map<String, TmsUser> getSubordinates() {
		return subordinates;
	}
	public void setSubordinates( Map<String, TmsUser> subordinates ) {
		this.subordinates = subordinates;
	}
	
	/**
	 * @return the organisation
	 */
	public String getCountgroup() {
		return countgroup;
	}

	public void setCountgroup(String countgroup) {
		this.countgroup = countgroup;
	}

	public Map<String, Project> getProjects() {
		return projects;
	}

	public void setProjects(Map<String, Project> projects) {
		this.projects = projects;
	}
/*
	public Map< SupportedFunctionType, AccessRight > getRights() {
		return mapOfAR;
	}

	public void setRights( Map< SupportedFunctionType, AccessRight > mapOfAR ) {
		this.mapOfAR = mapOfAR;
	}

	public AccessRight getRights( SupportedFunctionType function ) {
		
		return mapOfAR.get( function );
	}
*/	
	public boolean isProjectManager() {
		return isProjectManager;
	}

	public void setProjectManager( boolean isProjectManager ) {
		this.isProjectManager = isProjectManager;
	}

	public boolean isLineManager() {
		return isLineManager;
	}

	public void setLineManager( boolean isLineManager ) {
		this.isLineManager = isLineManager;
	}

	public SecurityContext getContext() {
		return context;
	}

	public void setContext(SecurityContext context) {
		this.context = context;
	}

	
	
	
	
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getKelaCode() {
		return kelaCode;
	}

	public void setKelaCode(String kelaCode) {
		this.kelaCode = kelaCode;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber( String taxNumber ) {
		this.taxNumber = taxNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @param organisation the organisation to set
	 */
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TmsUser [" + "code=" + (code != null ? code : "NULL" ) + ", "
				+ "firstName=" + (firstName != null ? firstName : "NULL" ) + ", "
				+ "midName=" + (midName != null ? midName : "NULL" ) + ", "
				+ "lastName=" + (lastName != null ? lastName : "NULL" ) + ", " 
				+ "checked-In=" + userState.isCheckedIn() + ", " 
				+ "organisation=" + ( organisation != null ? organisation.getName() : "NULL" ) + ", " 
				+ "manager=" +  ( manager != null ? manager.getFirstAndLastNames() : "NULL" )
				+ "]";
	}

	public String toStringShort() {
		return "TmsUser [ " 
				+ "code=" + (code != null ? code : "NULL" ) + ", "
				+ "Fio=" + this.getFirstAndLastNames() + " from "
				+ this.getOrganisation().getName()
				+ " ]";
	}

	public boolean addSubordinate( TmsUser user ) {
		boolean bRes = false;

		if ( subordinates == null ) {
			logger.error( "List of Subordinates must be != NULL!" );
		}
		
		if ( user == null ) {
			logger.error( "User must be !== NULL!" );
		}
	
		// Validate that does not "belong" to other manager and remove it if necessary from that
		if ( user.getManager() != null && user.getManager().getCode().compareToIgnoreCase( this.getCode()) != 0 ) {
			user.getManager().removeSubordinate( user );
		}
		// Add user as subordinate
		user.setManager( this );
		subordinates.put( user.getCode(), user );
		bRes = true;
		if ( logger.isDebugEnabled()) logger.debug( "User " + user.getFirstAndLastNames() + " was added as subordinate" );
		
				
		return bRes;
	}
	
	public boolean removeSubordinate( TmsUser user ) {
		boolean bRes = false;

		if ( subordinates == null ) {
			logger.error( "List of Subordinates must be != NULL!" );
		}
		
		if ( user == null ) {
			logger.error( "User must be != NULL!" );
		}
		
		// Remove user if exist
		if ( subordinates.containsKey( user.getCode())) {
			TmsUser usr = this.subordinates.remove( user.getCode() );
			
			if ( logger.isDebugEnabled()) 
				if ( usr != null ) {
					logger.debug( "User '" + usr.getFirstAndLastNames() +
							"' was removed from the list of subordinates of Manager:" + this );
				} else {
					logger.debug( "User is not subordinate of Manager:" + this );
				}
			bRes = true;
		}
		user.setManager( null );
		
		return bRes;
	}

	public void update( TmsUser resUser ) {
		this.setCode( resUser.getCode());
		this.setFirstName( resUser.getFirstName());
		this.setMidName( resUser.getMidName());
		this.setLastName( resUser.getLastName());
		this.setResource( resUser.getResource());
		this.setManager( resUser.getManager());
	    this.setCountgroup( resUser.getCountgroup());
		this.setProjectManager( resUser.isProjectManager( ));
		this.setLineManager( resUser.isLineManager( ));
		this.getContext().setSecGroup( resUser.getContext().getSecGroup());

		this.setAccount( resUser.getAccount());
		this.setAddress( resUser.getAddress());
		this.setKelaCode( resUser.getKelaCode());
		this.setTaxNumber( resUser.getTaxNumber());
		this.setEmail( resUser.getEmail());
		this.setMobile( resUser.getMobile());
	}

	public boolean addProject( Project project ) {
		boolean bRes = false;
		if ( logger.isDebugEnabled()) logger.debug( "Try to add " + project + " to " + this );
		if ( project != null ) {
			// If the same item exist than return false
			if ( projects.containsKey( project.getCode())) {
				if ( logger.isDebugEnabled()) logger.debug( "   ... Project exist already" );
				return false;
			}
			
			project.setProjectManager( this );
			projects.put( project.getCode(), project );
			if ( logger.isDebugEnabled()) logger.debug( "   ... Project added" );
			
			bRes = true;
			
		} else {
			logger.error( "Task cannot be NULL when add to Project!" );
		}
		
		return bRes;
	}

	public boolean removeProject( Project project ) {
		boolean bRes = false;
		if ( project != null ) {

			
			if ( projects.containsKey( project.getCode())) {
				Project tmp = projects.remove( project.getCode());
				if ( tmp != null ) {
					project.setProjectManager( null );
				}
				bRes = true;
			}
		} else {
			logger.error( "Project cannot be NULL to be removed from TmsUser!" );
		}
		
		return bRes;
	}

	public class LastNameComparator implements Comparator< TmsUser >{

		private Collator standardComparator;
		
		public LastNameComparator() {
			standardComparator = Collator.getInstance(); 
		}
		
		@Override
		public int compare( TmsUser arg1, TmsUser arg2 ) {
			return standardComparator.compare( arg1.getLastAndFirstNames(), arg2.getLastAndFirstNames());
		}

	}

	public boolean manages( TmsUser user ) {
		
		return ( user != null && user.getManager() != null && user.getManager().getId() == this.getId());
	}

	private int compareNamePart( String namePart1, String namePart2 ) {

		if ( namePart1 == null ) {
			return 1;
		}
		
		if ( namePart2 == null ) {
			return -1;
		}
		
		return namePart1.trim().compareToIgnoreCase( namePart2.trim());			
		
	}
	
	public int compareByName( TmsUser user ) {
		int res = 1;
		
		if ( user != null ) {
			
			// Compare last name firstly
			res = compareNamePart( user.getLastName(), this.getLastName());			
			if ( res != 0 ) return res;
				
			// Compare first name 
			res = compareNamePart( user.getFirstName(), this.getFirstName());			
			if ( res != 0 ) return res;
				
			// Compare middle name
			res = compareNamePart( user.getMidName(), this.getMidName());			
			if ( res != 0 ) return res;
			
		}
		
		return res;
	}
	
}
