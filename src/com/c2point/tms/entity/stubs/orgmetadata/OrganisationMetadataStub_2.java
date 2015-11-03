package com.c2point.tms.entity.stubs.orgmetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;

@XmlRootElement(name = "organisation")
@XmlType(propOrder = { "code", "name", "projects", "latestCodes", "closestCodes" })
public class OrganisationMetadataStub_2 {
	private String code;
	private String name;

	@XmlElement( name = "project" )
	List<ProjectStub> projects = new ArrayList<ProjectStub>();
	
  // XmlElement sets the name of the entities
	@XmlElementWrapper(name = "latest", required=false)
	@XmlElement(name = "code", required=false)
	List<String> latestCodes = new ArrayList<String>();
	
	@XmlElementWrapper(name = "closest", required=false)
	@XmlElement(name = "code", required=false)
	List<String> closestCodes = new ArrayList<String>();
	
	
	
	public OrganisationMetadataStub_2() {
	}
	
	public OrganisationMetadataStub_2( Organisation org ) {
		this( org, true );
	}

	public OrganisationMetadataStub_2( Organisation org, boolean bLongForm ) {
		this.code = org.getCode();
		this.name = org.getName();
		
		init( org, bLongForm );
	}

	private void init( Organisation org, boolean bLongForm ) {
		ProjectStub stub;
		
		for ( Project project : org.getProjects().values()) {
			if ( project != null && !project.isDeleted() && !project.isClosed()) {
				stub = new ProjectStub( project );
				// If short form has been requested than Task List will be removed
				if ( !bLongForm ) {
					stub.makeShort();
				}
				this.projects.add( stub );
			}
		}
		
		if ( this.projects != null && this.projects.size() > 1 ) {
			Collections.sort( this.projects, new Comparator<ProjectStub>() {
			    public int compare( ProjectStub ar1, ProjectStub ar2 ) {
			    	
					if ( ar1.getName() == null ) return -1;
					if ( ar2.getName() == null ) return 1;
					
			        return ar1.getName().compareToIgnoreCase( ar2.getName());
			    }
			});
		}
	}
	
	public void addLatest( Project project ) {

		if ( project != null && project.getCode() != null && project.getCode().length() > 0 
				&& !this.latestCodes.contains( project.getCode()) ) {
			
			this.latestCodes.add( project.getCode() );
		}
		
	}
	
	public void addClosest( Project project ) {
		
		if ( project != null && project.getCode() != null && project.getCode().length() > 0 
				&& !this.closestCodes.contains( project.getCode()) ) {
			this.closestCodes.add( project.getCode() );
		}
		
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ProjectStub> getProjects() {
		return projects;
	}

	public List<String> getLatestCodes() {
		return latestCodes;
	}

	public List<String> getClosestCodes() {
		return closestCodes;
	}

	
}
