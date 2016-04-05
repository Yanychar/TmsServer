package com.c2point.tms.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@NamedQuery(name = "findTaskByCode", query = "SELECT task FROM Task task WHERE task.code = :code")
public class Task extends SimplePojo {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Task.class.getName());

	public static String DEFAULT_FAKE_TASK_CODE = "XXXX";
	
	private String code;
	private String name;
	
	@ManyToOne
	private Organisation organisation;

	@ManyToOne
	private MeasurementUnit measure;
	
	
	protected Task() {
		this( null, "", "" );
	}
	
	public Task( Organisation org, String code, String name ) {
		super();
		
		this.organisation = org;
		this.code = code.trim().toUpperCase();
		this.name = name;
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
	public void setName( String name ) {
		this.name = name;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation( Organisation organisation ) {
		this.organisation = organisation;
	}

	public MeasurementUnit getMeasurementUnit() { return measure; }
	public void setMeasurementUnit( MeasurementUnit measure ) { this.measure = measure; }

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Task [" 
				+ "code=" + (code != null ? code + ", " : "NULL, ")
				+ "name=" + (name != null ? name : "NULL") + "]";
	}

	
}
