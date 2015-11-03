package com.c2point.tms.entity.stubs.orgmetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.stub.projectsandtasks.ProjectManagerStub;

@XmlType(propOrder = { "code", "name", "tasks", "pm" })
public class ProjectStub {
	private String code;
	private String name;

	private ProjectManagerStub pm;

	@XmlElement( name = "task" )
	List<ProjectTaskStub> tasks = null;
	
	
	protected ProjectStub() {
		
	}
	
	public ProjectStub( Project project ) {
		this.code = project.getCode();
		this.name = project.getName();
		this.pm = new ProjectManagerStub( project.getProjectManager()); 
		
		init( project );
	}
	
	private void init( Project project ) {
		for ( ProjectTask prTask : project.getProjectTasks().values()) {
			if ( prTask != null && !prTask.isDeleted()) {
				if ( this.tasks == null ) {
					this.tasks = new ArrayList<ProjectTaskStub>();
				}
				this.tasks.add( new ProjectTaskStub( prTask ));
			}
		}
		
		if ( this.tasks != null && this.tasks.size() > 1 ) {
			Collections.sort( this.tasks, new Comparator<ProjectTaskStub>() {
			    public int compare( ProjectTaskStub ar1, ProjectTaskStub ar2 ) {
			    	
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

	@XmlElement(name = "projectManager")
	public ProjectManagerStub getPm() {
		return pm;
	}

	public void setPm( ProjectManagerStub pm ) {
		this.pm = pm;
	}
	
	public List<ProjectTaskStub> getTasks() {
		return tasks;
	}

	public void makeShort() {
		this.tasks = null;
		this.pm = null;
	}
}
