package com.c2point.tms.entity.transactions;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.stubs.transactions.ProjectStub;

@XmlRootElement(name = "info")
@XmlType(propOrder = { "dateTime", "project" })
public class CheckInOutTransactionData {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( CheckInOutTransactionData.class.getName()); 

	private Date		dateTime;
	private ProjectStub	project;
	
	
	/**
	 * @param tmsUser
	 * @param dateTime
	 * @param project
	 */
	protected CheckInOutTransactionData( Date dateTime, String projectCode, String name ) {
		super();
		this.dateTime = dateTime;
		this.project = new ProjectStub( projectCode, name );
	}
	public CheckInOutTransactionData( Date dateTime, Project project ) {
		super();
		this.dateTime = dateTime;
		this.project = ( project != null ? new ProjectStub( project ) : null );
	}

	protected CheckInOutTransactionData() {
		this( null, null );
	}

	
	
	/**
	 * @return the dateTime
	 */
	public Date getDateTime() {
		return dateTime;
	}
	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	/**
	 * @return the project
	 */
	public ProjectStub getProject() {
		return project;
	}
	/**
	 * @param project the project to set
	 */
	public void setProject( ProjectStub project ) {
		this.project = project;
	}
}
