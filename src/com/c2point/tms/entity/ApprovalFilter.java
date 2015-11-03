package com.c2point.tms.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.c2point.tms.util.DateUtil;

public class ApprovalFilter {

	private Map<String, TmsUser> 	mapUsers = null;
	private Map<String, Project> 	mapProjects = null;

	private TmsUser	projectManager;
	
	private Date startDate = new Date( 0 );
	private Date endDate = DateUtil.getDate();

	private boolean toCheck = true;
	private boolean rejected = true;
	private boolean approved = false;
	private boolean processed = false;
	
	
	public ApprovalFilter( 
			TmsUser	projectManager,
			Date startDate,
			Date endDate,
	
			boolean toCheck,
			boolean rejected,
			boolean approved,
			boolean processed ) {

		this.setProjectManager( projectManager );
		this.setStartDate( startDate );
		this.setEndDate( endDate );

		this.toCheck = toCheck;
		this.rejected = rejected;
		this.approved = approved;
		this.processed = processed;
	}

	public ApprovalFilter( TmsUser	projectManager ) {
		this( projectManager, null, null, true, true, false, false );
	}

	public ApprovalFilter( TmsAccount account ) {
		this( account.getUser());
	}

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		if ( startDate != null ) {
			this.startDate = startDate;
		} else {
			this.startDate = new Date( 0 ); // Smallest Date available
		}
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		if ( endDate != null ) {
			this.endDate = endDate;
		} else {
			this.endDate = new Date( DateUtil.getDate().getTime() + 1000*60*60*2 ); // +2 hours
		}
	}
	public boolean isToCheck() {
		return toCheck;
	}
	public void setToCheck(boolean toCheck) {
		this.toCheck = toCheck;
	}
	public boolean isRejected() {
		return rejected;
	}
	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}
	public boolean isApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	public Map<String, TmsUser> getMapUsers() {
		return mapUsers;
	}
	public void setMapUsers(Map<String, TmsUser> mapUsers) {
		this.mapUsers = mapUsers;
	}
	public Map<String, Project> getMapProjects() {
		return mapProjects;
	}
	public void setMapProjects(Map<String, Project> mapProjects) {
		this.mapProjects = mapProjects;
	}

	public TmsUser getProjectManager() {
		return projectManager;
	}
	public void setProjectManager(TmsUser projectManager) {
		this.projectManager = projectManager;
	}
	
	public void addUser( TmsUser user ) {
		if ( user != null ) {
			if ( getMapUsers() == null ) {
				setMapUsers( new HashMap<String, TmsUser>());
			}

			if ( !getMapUsers().containsKey( user.getCode())) {
				getMapUsers().put( user.getCode(), user );
			}
		}
	}

	public void addUser( List<TmsUser> userList ) {
		if ( userList != null ) {
			if ( getMapUsers() == null ) {
				setMapUsers( new HashMap<String, TmsUser>());
			}
		
			for ( TmsUser user : userList ) {
				addUser( user );
			}
		}
	}
	
	public void addProject( Project project ) {
		if ( project != null ) {
			if ( getMapProjects() == null ) {
				setMapProjects( new HashMap<String, Project>());
			}

			if ( !getMapProjects().containsKey( project.getCode())) {
				getMapProjects().put( project.getCode(), project );
			}
		}
	}

	public void addProject( List<Project> projectList ) {
		if ( projectList != null ) {
			if ( getMapProjects() == null ) {
				setMapProjects( new HashMap<String, Project>());
			}
	
			for ( Project project : projectList ) {
				addProject( project );
			}
		}
	}

	@Override
	public String toString() {
		return "ApprovalFilter ["
//				+ ( mapUsers != null ? "mapUsers=" + mapUsers + ", " : "")
				+ ( mapUsers != null ? "Num of Users = " + mapUsers.size() + ", " : "No users, " )
//				+ (mapProjects != null ? "mapProjects=" + mapProjects + ", " : "")
				+ ( mapProjects != null ? "Num of Projs=" + mapProjects.size() + ", " : "No projs., " )
				+ (projectManager != null ? "projectManager=" + projectManager + ", " : "")
				+ (startDate != null ? "startDate=" + startDate + ", " : "")
				+ (endDate != null ? "endDate=" + endDate + ", " : "")
				+ "toCheck=" + toCheck + ", rejected=" + rejected
				+ ", approved=" + approved + ", processed=" + processed + "]";
	}
	

}
