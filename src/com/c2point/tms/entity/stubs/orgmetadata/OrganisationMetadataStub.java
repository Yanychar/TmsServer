package com.c2point.tms.entity.stubs.orgmetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;

@XmlRootElement(name = "organisation")
@XmlType(propOrder = { "code", "name", "projects" })
public class OrganisationMetadataStub {
	private String code;
	private String name;

	@XmlElement( name = "project" )
	List<ProjectStub> projects = new ArrayList<ProjectStub>();
	
	
	public OrganisationMetadataStub() {
	}
	
	public OrganisationMetadataStub( Organisation org ) {
		this( org, true );
	}

	public OrganisationMetadataStub( Organisation org, boolean bLongForm ) {
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
	
}
