package com.c2point.tms.entity.stubs.orgmetadata;

import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.ProjectTask;

@XmlType(propOrder = { "code", "name" })
public class ProjectTaskStub {

	private String code;
	private String name;

	protected ProjectTaskStub() {
		
	}
	
	public ProjectTaskStub( ProjectTask prTask ) {
		this.code = prTask.getTask().getCode();
		this.name = prTask.getTask().getName();
		
//		init( project );
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
	
}
