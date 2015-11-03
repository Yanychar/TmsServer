package com.c2point.tms.entity.stubs.checkinout;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.util.DateUtil;

@XmlRootElement(name = "checkedout")
@XmlType(propOrder = { "dateCheckedIn", "dateCheckedOut", "project" })
public class CheckOutRespStub {
	 
	private String dateCheckedIn;
	private String dateCheckedOut;

	private ProjectStub project;
	
	public CheckOutRespStub( TmsAccount account ) {
		super();
		try {
			this.dateCheckedIn = DateUtil.dateAndTimeToString( account.getUser().getUserState().getDateCheckedIn());
		} catch ( Exception e ) {
			this.dateCheckedIn = null;
		}
		try {
			this.dateCheckedOut = DateUtil.dateAndTimeToString( account.getUser().getUserState().getDateCheckedOut());
		} catch ( Exception e ) {
			this.dateCheckedOut = null;
		}
		
		copy( account.getUser().getUserState().getProject());
	}

	public CheckOutRespStub() {
		super();
	}

	private void copy( Project project ) {
		if ( project != null ) {
			this.project = new ProjectStub( project );
		}
	}

	@XmlElement(name = "datein")
	public String getDateCheckedIn() {
		return dateCheckedIn;
	}

	public void setDateCheckedIn( String dateCheckedIn ) {
		this.dateCheckedIn = dateCheckedIn;
	}

	@XmlElement(name = "dateout")
	public String getDateCheckedOut() {
		return dateCheckedOut;
	}

	public void setDateCheckedOut( String dateCheckedOut ) {
		this.dateCheckedOut = dateCheckedOut;
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
