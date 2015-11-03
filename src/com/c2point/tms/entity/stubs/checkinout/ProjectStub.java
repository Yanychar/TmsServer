package com.c2point.tms.entity.stubs.checkinout;

import javax.xml.bind.annotation.XmlElement;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.stub.projectsandtasks.ProjectManagerStub;

public class ProjectStub {
	private String code;
	private String name;

	private ProjectManagerStub pm;

	protected ProjectStub() {
		
	}
	
	public ProjectStub( Project project ) {
		this.code = project.getCode();
		this.name = project.getName();
		this.pm = new ProjectManagerStub( project.getProjectManager()); 
		
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
	
}
