package com.c2point.tms.entity.stubs.transactions;

import com.c2point.tms.entity.Project;

public class ProjectStub {
	String code;
	String name;
	/**
	 * @param id
	 * @param name
	 */
	public ProjectStub( String code, String name ) {
		super();
		this.code = code;
		this.name = name;
	}
	public ProjectStub() {
		this( "", "" );
	}
	public ProjectStub( Project project ) {
		super();
		this.code = project.getCode();
		this.name = project.getName();
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
}
