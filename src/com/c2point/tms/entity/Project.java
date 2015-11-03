package com.c2point.tms.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.exception.NotUniqueCode;

@Entity
public class Project extends SimplePojo {

	private static Logger logger = LogManager.getLogger( Project.class.getName());

    private String code;
	private String name;

	@Temporal(TemporalType.DATE)
	private Date start;
	@Temporal(TemporalType.DATE)
	private Date endPlanned;
	@Temporal(TemporalType.DATE)
	private Date endReal;
	
	@ManyToOne
	private Organisation organisation;
	
	//@OneToMany (fetch = EAGER, targetEntity = ProjectTask.class)
	@OneToMany( mappedBy = "project", cascade = { CascadeType.ALL })
	private Map<String, ProjectTask> projectTasks = new HashMap<String, ProjectTask>();
		
	@ManyToOne
	@OneToOne( cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch=FetchType.LAZY )
	private TmsUser 	projectManager;
	
	private String address;
	@AttributeOverrides({
		@AttributeOverride(name="latitude", column= @Column(name="latitude")),
	    @AttributeOverride(name="longitude", column= @Column(name="longitude")),
	    @AttributeOverride(name="accuracy", column= @Column(name="accuracy"))   // Is not used in "Project"
	})
	@Embedded
	private GeoCoordinates 	geo;
	
	private boolean 	closed;
	
	protected Project() {
		this( "", "" );
	}
	
	public Project(String code, String name) {
		super();
		this.code = ( code != null ? code.trim().toUpperCase() : code );
		this.name = name;
	}
	public Project( Project project ) {
		this( project.getCode(), project.getName());
	}

	public void update( Project project ) {
		this.name = project.getName();

		this.start = project.getStart();
		this.endPlanned = project.getEndPlanned();
		this.endReal = project.getEndReal();
		
//		this.organisation = project.;
		
//		this.projectTasks = new HashMap<String, ProjectTask>();
			
		this.projectManager = project.getProjectManager();
		
		this.address = project.getAddress();
		this.geo = project.getGeo();
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
		this.code = code.trim().toUpperCase();
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
	 * @return the projectManager
	 */
	public TmsUser getProjectManager() {
		return projectManager;
	}

	/**
	 * @param projectManager the projectManager to set
	 */
	public void setProjectManager( TmsUser projectManager ) {
		this.projectManager = projectManager;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation( Organisation organisation ) {
		this.organisation = organisation;
	}

	public Map<String, ProjectTask> getProjectTasks() {
		return projectTasks;
	}

	public void setProjectTasks(Map<String, ProjectTask> projectTasks) {
		this.projectTasks = projectTasks;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEndPlanned() {
		return endPlanned;
	}

	public void setEndPlanned(Date endPlanned) {
		this.endPlanned = endPlanned;
	}

	public Date getEndReal() {
		return endReal;
	}

	public void setEndReal(Date endReal) {
		this.endReal = endReal;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public GeoCoordinates getGeo() {
		return geo;
	}

	public void setGeo(GeoCoordinates geo) {
		this.geo = geo;
	}

	/**** Business Methods  ***/
	public ProjectTask assignTask( Task task ) {
		ProjectTask pt = null;

		if ( task != null && task.getCode() != null) {

			// If the same item exist than return false
			if ( !getProjectTasks().containsKey( task.getCode() )) {
				pt = new ProjectTask( this, task );
				getProjectTasks().put( pt.getTask().getCode(), pt );
			
			} else {
				logger.error( "Cannot assign " + task + ". It is assigned to the " + this + " already!" );
			}
		} else {
			logger.error( "Task cannot be NULL when assign Task to Project!" );
		}
		
		return pt;
	}

	public ProjectTask updateAssignedTask( ProjectTask pTask ) throws NotUniqueCode {

		// Check that the same codeInProject may belong to one ProjectTask only!
		ProjectTask oldPTask = getProjectTaskByCodeInProject( pTask.getCodeInProject());
		
		if ( oldPTask != null && oldPTask.getId() != pTask.getId()) {
			if ( logger.isDebugEnabled()) logger.debug( "Cannot update ProjectTask because ProjectTask with different id passed"  );
			throw new NotUniqueCode();
		}
		
//		oldPTask.setDeleted( pTask.isDeleted());
//		oldPTask.setCodeInProject( pTask.getCodeInProject());
		
		this.getProjectTasks().put( pTask.getTask().getCode(), pTask );
		

		if ( logger.isDebugEnabled()) logger.debug( "ProjectTask:" + pTask + " was edited" );
		
		return pTask;
	}
	
	public ProjectTask deleteAssignedTask( Task task ) {
		return deleteAssignedTask( this.getProjectTask( task.getCode()));
	}
	public ProjectTask deleteAssignedTask( ProjectTask pTask ) {
		
		ProjectTask oldPTask = this.getProjectTask( pTask );
		
		if ( oldPTask == null ) {
			if ( logger.isDebugEnabled()) logger.debug( "The Project does not contain this:" + pTask + ". Cannot be removed"  );
			return null;
		}

		// Set ProjectTask as deleted
		oldPTask=this.getProjectTasks().remove( pTask.getTask().getCode());
		oldPTask.setDeleted();
		
		if ( logger.isDebugEnabled()) logger.debug( "ProjectTask:" + pTask + " was removed from Project: " + this  );

		return oldPTask;
			
	}
	
	
	public Task getTask( String code ) {
		
		return getProjectTask( code ).getTask();
	}
	
	public ProjectTask getProjectTask( String taskCode ) {
		
		return getProjectTasks().get( taskCode );
	}

	public ProjectTask getProjectTask( ProjectTask pTask ) {
		try {
			return getProjectTask( pTask.getTask().getCode());
		} catch ( Exception e ) {
		}
		
		logger.error( "ProjectTask or ref to Task in it is null!" );
		return null;
	}

	public ProjectTask getProjectTaskByCodeInProject( String codeInProject ) {
		
		for( ProjectTask pt : getProjectTasks().values()) {
			if ( pt != null && pt.getCodeInProject().compareTo( codeInProject ) == 0 ) {
				return pt;
			}
		}
		
		return null;
	}

	public List<ProjectTask> getProjectTasksList() {
		ArrayList< ProjectTask > list;
		if ( projectTasks != null && !projectTasks.isEmpty()) {
			list = new ArrayList< ProjectTask >( projectTasks.values()); 
		} else {
			list = new ArrayList< ProjectTask >(); 
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		String str = "";
		try {
			str = "Project [";
			str = str.concat( "code=" + (code != null ? code + ", " : "NULL"));
			str = str.concat( "name=" + (name != null ? name + ", " : "NULL"));
			str = str.concat( "projectManager=" + (projectManager != null ? projectManager : "NULL") + " " );
//			str = str.concat( "tasks=" + ( tasks != null ? tasks : "NULL"));
			str = str.concat( "]" );
		} catch (Exception e) {
			logger.error( "Created NOT COMPLETED string str: " + str );
		}
		 
		return str;
	}
	
	public String detailed() {
		return toString() + "\n" + toStringTaskList();
		
	}
	
	public String toStringTaskList() {
		String str = "  Assigned Tasks ( " + getProjectTasks().size() + " ):";
		
		for( ProjectTask pt : getProjectTasks().values()) {
			str = str.concat( "\n    " + pt );
		}
		
		return str;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public boolean isEnded() {
		
 		return isClosed(); 
	}
	
}
