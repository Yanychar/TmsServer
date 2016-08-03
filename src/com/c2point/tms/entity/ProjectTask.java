package com.c2point.tms.entity;

import javax.persistence.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
public class ProjectTask extends SimplePojo {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ProjectTask.class.getName());
	
	private String codeInProject;
	
	private Task task;
	private Project project;
/*	
	protected ProjectTask() {
		this( "", null );
	}
	
	public ProjectTask( String codeInProject, Task task ) {
		super();
		this.codeInProject = codeInProject.trim().toUpperCase();
		this.task = task;
	}
*/
	protected ProjectTask() {
		this( null );
	}

	public ProjectTask( Task task ) {
		super();
		this.task = task;
		this.project = null;
		this.codeInProject = "";
	}
	
	public ProjectTask( Project project, Task task ) {
		this( task );
		setProject( project );
		setCodeInProject();
	}
	
	/**
	 * @return the code
	 */
	public String getCodeInProject() {
		return codeInProject;
	}

	/**
	 * @param code the code to set
	 */
	public void setCodeInProject( String codeInProject ) {
		this.codeInProject = codeInProject.trim().toUpperCase();
	}

	// Set Default codeInProject == Project.code + Task.code 
	public void setCodeInProject() {
		this.codeInProject = 
				( project != null ? project.getCode().trim().toUpperCase() : "" )
			  + ( task != null ? task.getCode().trim().toUpperCase() : "" );
	}
	
	
	/**
	 * @return the name
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * @param name the name to set
	 */
	public void setTask( Task task ) {
		this.task = task;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProjectTask [" 
				+ ( codeInProject != null ? codeInProject + ", " : "NULL, " )
				+ " for Project [" 
				+ "code=" + (project.getCode() != null ? project.getCode() + ", " : "NULL, ")
				+ "name=" + (project.getName() != null ? project.getName() : "NULL") + "]"
				+  "]"
				+ " and Task [" 
				+ "code=" + (task.getCode() != null ? task.getCode() + ", " : "NULL, ")
				+ "name=" + (task.getName() != null ? task.getName() : "NULL") + "]"
				+  "]";
	}

	
}
