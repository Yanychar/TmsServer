package com.c2point.tms.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.swing.event.EventListenerList;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SecurityGroup;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.entity.stubs.orgmetadata.OrganisationMetadataStub;
import com.c2point.tms.entity.stubs.orgmetadata.OrganisationMetadataStub_2;
import com.c2point.tms.util.exception.NotUniqueCode;
import com.c2point.tms.util.xml.XMLconverter;

@Entity
@NamedQueries({
	@NamedQuery(name = "findOrganisationByCode", query = "SELECT org FROM Organisation org WHERE org.code = :code AND org.deleted = false"),
})
public class Organisation extends SimplePojo {

	private static Logger logger = LogManager.getLogger( Organisation.class.getName());

	private String code;
	private String name;
	
	
	@ManyToOne
	@OneToOne( cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch=FetchType.LAZY )
	private TmsUser 	serviceOwner;

	private String		propString;
	
	@OneToMany( mappedBy = "organisation", 
				cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH },
				fetch=FetchType.LAZY )
	@MapKey( name = "code" )
	private Map<String, TmsUser> users = new HashMap<String, TmsUser>();

	@OneToMany( mappedBy = "organisation", cascade = { CascadeType.ALL })
	@MapKey( name = "code" )
	private Map<String, Project> projects = new HashMap<String, Project>();

	@OneToMany( mappedBy = "organisation", 
				cascade = { CascadeType.ALL },
				fetch=FetchType.LAZY )
	@MapKey( name = "code" )
	private Map<String, Task> tasks = new HashMap<String, Task>();
	
	@OneToMany( mappedBy = "organisation", 
			cascade = { CascadeType.ALL },
			fetch=FetchType.LAZY )
	@MapKey( name = "code" )
	private Map< String, SecurityGroup > securityGroups = new HashMap< String, SecurityGroup >( SupportedFunctionType.size());
	
	private String address;
	private String tunnus;
	private String info;
	private String phone;
	private String email;

	
	@Transient
	protected EventListenerList	listenerList = new EventListenerList(); 
	
	public Organisation() {
		super();
		this.code = "";
		this.name = "";
	}
	
	public Organisation( String code, String name ) {
		super();
		this.code = code;
		this.name = name;
	}
	
	public void update( Organisation organisation ) {
		this.setCode( organisation.getCode());
		this.setName( organisation.getName());
		this.setServiceOwner( organisation.getServiceOwner());
		this.setPropString( organisation.getPropString());
		this.setAddress( organisation.getAddress());
		this.setTunnus( organisation.getTunnus());
		this.setInfo( organisation.getInfo());
		this.setPhone( organisation.getPhone());
		this.setEmail( organisation.getEmail());
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
	public void setCode( String code ) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the orgMap
	 */
	public Map<String, Project> getProjects() {
		return projects;
	}

	/**
	 * @param orgMap the orgMap to set
	 */
	public void setProjects( Map<String, Project> projects ) {
		this.projects = projects;
	}

	public Map<String, TmsUser> getUsers() {
		return users;
	}

	public void setUsers( Map<String, TmsUser> users ) {
		this.users = users;
	}

	public Map<String, Task> getTasks() {
		return tasks;
	}

	public void setTasks( Map<String, Task> tasks ) {
		this.tasks = tasks;
	}
	
	public Map<String, SecurityGroup> getSecurityGroups() {
		return this.securityGroups;
	}

	public void setSecurityGroups( Map<String, SecurityGroup> securityGroups ) {
		this.securityGroups = securityGroups;
	}


	public String getXmlPresentation() throws JAXBException {
		return getXmlPresentation( true );
	}
	
	public String getXmlPresentation( boolean bLongForm ) throws JAXBException {
		String str;
		
		OrganisationMetadataStub stub = new OrganisationMetadataStub( this, bLongForm );
		str = XMLconverter.convertToXML( stub );
		
		return str;
	}

	public String getXmlPresentation_2( OrganisationMetadataStub_2 stub, boolean bLongForm ) throws JAXBException {
		
		
		return XMLconverter.convertToXML( stub );
		
	}

	public TmsUser getServiceOwner() { return serviceOwner; }
	public void setServiceOwner(TmsUser serviceOwner) { this.serviceOwner = serviceOwner; }

	public String getPropString() { return propString; }
	public void setPropString( String propString ) { 
		this.propString = propString;

		this.propWereRead = false;
		getProperties();

		firePropertiesWereChanged();
		
	}

	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }

	public String getTunnus() { return tunnus; }
	public void setTunnus(String tunnus) { this.tunnus = tunnus; }

	public String getInfo() { return info; }
	public void setInfo(String info) { this.info = info; }

	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	@Transient
	private Properties props = new Properties();
	@Transient
	private boolean propWereRead = false;
	
	/**
	 * Convert stored XML string into the properties object
	 * @return Properties
	 */
	public Properties getProperties() {
//		Properties props = new Properties();

		if ( !propWereRead ) {
			try {
				props.load( new ByteArrayInputStream( getPropString().getBytes( "UTF-8" )));
				propWereRead = true;
		
				logger.debug( "XML String has been converted to Properties object successfully:" );
				
				String key;
				for ( Enumeration<Object> e = props.keys(); e.hasMoreElements();) {
				       key = (String) e.nextElement();
				       logger.debug( "[ " + key + ", " + props.get( key) + " ]" );
				       
				}
				
			} catch (InvalidPropertiesFormatException e) {
				logger.error( "Organisation " + this.getName() + " property string is not valid XML String" );
			} catch (UnsupportedEncodingException e) {
				logger.error( "Organisation " + this.getName() + " property string. Wrong encoding" );
			} catch (IOException e) {
				logger.error( "Organisation " + this.getName() + " property string.\n" + e );
			}

		}
		
		return props;
	}

	public void setProperties( Properties properties ) {
		
		ByteArrayOutputStream outByte = new ByteArrayOutputStream();
		try {
//			properties.storeToXML( outByte, "Comment to store properties" );
			properties.store( outByte, null );
			setPropString( outByte.toString( "UTF-8" ));

			
		} catch ( UnsupportedEncodingException e ) {
			logger.error( "Organisation " + this.getName() + " property string. Wrong encoding" );
		} catch ( IOException e ) {
			logger.error( "Organisation " + this.getName() + " property string.\n" + e );
		}

//		propWereRead = false;

	}

	/* Business methods
	 * 
	 */
	public Project getProject( String projectCode ) {
		
		try {
			return projects.get( projectCode );
		} catch ( Exception e ) {
			if ( logger.isDebugEnabled()) logger.debug( "   No Project with projectCode=" + ( projectCode != null ? projectCode : "null" ));
		}
		return null;
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
			
			project.setOrganisation( this );
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
					project.setOrganisation( null );
				}
				bRes = true;
			}
		} else {
			logger.error( "Project cannot be NULL to be removed from Organisation!" );
		}
		
		return bRes;
	}
	

	public boolean userExists( TmsUser user ) {
		return this.users.containsKey( user.getCode());
	}
	
	public TmsUser getUser( TmsUser user ) {
		return this.users.get( user.getCode());
	}
	
	public TmsUser getUser( String userCode ) {
		return this.users.get( userCode );
	}
	
	public boolean addUser( TmsUser user ) {
		boolean bRes = false;
		
		if ( userExists( user )) {
			if ( logger.isDebugEnabled()) logger.debug( "User: " + user.getFirstAndLastNames() + " exists already in the Organisation: " + this.getName()  );
		} else {
		
			user.setOrganisation( this );
			this.users.put( user.getCode(), user );
			if ( logger.isDebugEnabled()) logger.debug( "User: " + user.getFirstAndLastNames() + " was added to Organisation: " + this.getName());
			
			bRes = true;
		}
	
		return bRes;
	}
	
	public boolean updateUser( TmsUser user ) {
		boolean bRes = false;
		
		TmsUser oldUser = findUser( user );
		if ( oldUser != null ) {
	
			// User exists already. Shall be updated if necessary

			oldUser.update( user );
			if ( logger.isDebugEnabled()) 
				logger.debug( "User: " + user.getFirstAndLastNames() + " exists already in the Organisation: " + this.getName()
								+ " User be updated" );
			bRes = true;
		}
		
		return bRes;
	}

	public boolean removeUser( TmsUser user ) {
		boolean bRes = false;

		if ( userExists( user )) {

			this.users.get( user.getCode()).setDeleted();
			
			if ( logger.isDebugEnabled()) logger.debug( "User:" + user + " was set as DELETED in Organisation: " + this  );

			bRes = true;
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "User:" + user + " cannot be removed because it does NOT exist in the Organisation: " + this  );
		}

		return bRes;
	}

	public boolean taskExists( String taskCode ) {
		return this.tasks.containsKey( taskCode );
	}
	
	public Task getTask( String taskCode ) {
		return this.tasks.get( taskCode );
	}
	
	public Task getTask( long id ) {
		for ( Task task : this.tasks.values()) {
			if ( task != null && task.getId() == id ) {
				return task;
			}
		}
		
		return null;
	}
	
	public Task addTask( Task task ) throws NotUniqueCode {
		
		if ( taskExists( task.getCode())) {
			if ( logger.isDebugEnabled()) logger.debug( "Task:" + task + " exists already in the Organisation: " + this  );
			throw new NotUniqueCode();
		}
		
		this.tasks.put( task.getCode(), task );
		task.setOrganisation( this );
		if ( logger.isDebugEnabled()) logger.debug( "Task:" + task + " was added to Organisation: " + this  );
	
		return task;
	}

	public Task updateTask( Task task ) throws NotUniqueCode {
		
		// Check that the same code may belong to the same task only!
		Task oldTask = this.getTask( task.getCode());
		
		// If oldTask was not found it can be that Code was changet! Try to use id to find!
		if ( oldTask == null ) {
			oldTask = this.getTask( task.getId());
		}
		
		if ( oldTask != null && oldTask.getId() != task.getId()) {
			if ( logger.isDebugEnabled()) logger.debug( "Task with the same Code: " + oldTask + " exists in the Organisation: " + this  );
			throw new NotUniqueCode();
		}
		
		this.tasks.remove( oldTask.getCode());
		
		task = addTask( task );

		if ( logger.isDebugEnabled()) logger.debug( "Task:" + task + " was edited" );
		
		return task;
	}

	public Task deleteTask( Task task ) {
		if ( !taskExists( task.getCode())) {
			if ( logger.isDebugEnabled()) logger.debug( "Task:" + task + " cannot be removed because it does NOT exist in the Organisation: " + this  );
			return null;
		}
		
		// delete all assignment of task to ALL projects in the organisation
		for ( Project project : this.getProjects().values()) {
			if ( project != null ) {
				project.deleteAssignedTask( task );
			} else {
				logger.error( "There is NULL project in the list of projects! Wrong!" );
			}
		}
		
		// Set Task as deleted
		this.getTask( task.getCode()).setDeleted();
		// Remove reference to Task from Org
//		this.tasks.remove( task.getCode());
//		task.setOrganisation( null );
		if ( logger.isDebugEnabled()) logger.debug( "Task:" + task + " was removed from Organisation: " + this  );

		return task;
	}

	
	private TmsUser findUser( TmsUser user ) {
		
		TmsUser oldUser = this.getUser( user.getCode());
		if ( oldUser == null ) {
			if ( logger.isDebugEnabled()) 
				logger.debug( "User: " + user.getFirstAndLastNames() + " was not find by Code. Try to interate using Id" );
			
			for ( TmsUser tmp : this.getUsers().values()) {
				if ( tmp != null && tmp.getId() == user.getId()) {
					oldUser = tmp;
					if ( logger.isDebugEnabled()) 
						logger.debug( "User: " + user.getFirstAndLastNames() + " was found by Id!" );
					break;
				}
			}
			
		}
	
		return oldUser;
	}

	private enum UserType { EXISTING, DELETED, ALL };
	private int getUsersCount( UserType type ) {
		
		int count = 0;
		
		Collection<TmsUser> col = this.getUsers().values();
		
		if ( col != null && col.size() > 0 ) {
			if ( type == UserType.EXISTING ) {
				
				for ( TmsUser user : col ) {
					if ( user != null && !user.isDeleted()) count++;
				}
				
			} else if ( type == UserType.DELETED ) {
				
				for ( TmsUser user : col ) {
					if ( user != null && user.isDeleted()) count++;
				}
				
			} else if ( type == UserType.ALL ) {
				count = col.size();
			}
		}
		
		return count;
	}
	
	public int getUsersAll() { return getUsersCount( UserType.ALL ); }
	public int getUsersDeleted() { return getUsersCount( UserType.DELETED ); }
	public int getUsersExisting() { return getUsersCount( UserType.EXISTING ); }

		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Organisation [ " 
				+ "code=" + ( code != null ? code : "NULL") + ", "
				+ "name=" + ( name != null ? name : "NULL") + ", "
				+ "address=" + ( address != null ? address : "NULL") + ", "
				+ "tunnus=" + ( tunnus != null ? tunnus : "NULL") + ", "
				+ "info=" + ( info != null ? info : "NULL") + ", "
				+ "phone=" + ( phone != null ? phone : "NULL") + ", "
				+ "email=" + ( email != null ? email : "NULL") + " ]" +
				"\n" + projects;
	}
	public String toStringShort() {
		return "Organisation [ " 
				+ "code=" + (code != null ? code : "NULL") + ", "
				+ "name=" + (name != null ? name : "NULL") + " ]";

	
	}

	/*
	 *  Methods to work with Organisation properties
	 *  
	 */
	public String getProperty( String name ) {
		return getProperty( name, null );
	}
	public String getProperty( String name, String defValue ) {
		
		return getProperties().getProperty( name, defValue );
		
	}
	public void setProperty( String name, String value ) {
		
		getProperties().setProperty( name, value );

		ByteArrayOutputStream outByte = new ByteArrayOutputStream();
		try {
			getProperties().store( outByte, null );
			propString = outByte.toString( "UTF-8" );

			firePropertyWasChanged( name, value );
			
		} catch ( UnsupportedEncodingException e ) {
			logger.error( "Organisation " + this.getName() + " property string. Wrong encoding" );
		} catch ( IOException e ) {
			logger.error( "Organisation " + this.getName() + " property string.\n" + e );
		}
		
		
	}
	
	
	public interface PropertyChangedListener  extends EventListener {
		
		public void propertyWasChanged( String name, String value );
		public void propertiesWereChanged();
	}

	public void addChangedListener( PropertyChangedListener listener ) {
		
		listenerList.add( PropertyChangedListener.class, listener);
	
		
	}

	public void removeChangedListener( PropertyChangedListener listener ) {
		
		listenerList.remove( PropertyChangedListener.class, listener );
	
		
	}


	private void firePropertyWasChanged( String name, String value ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == PropertyChangedListener.class ) {
	    		
	    		(( PropertyChangedListener )listeners[ i + 1 ] ).propertyWasChanged( name, value );
	    		
	    		if ( logger.isDebugEnabled()) logger.debug( "PropertyWasChanged event has been sent. PropName: " + name );
	    	}
	    }
	}

	private void firePropertiesWereChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == PropertyChangedListener.class ) {
	    		
	    		(( PropertyChangedListener )listeners[ i + 1 ] ).propertiesWereChanged();
	    		
	    		if ( logger.isDebugEnabled()) logger.debug( "PropertiesWereChanged event has been sent" );
	    	}
	    }
	}

	
	
	
}
