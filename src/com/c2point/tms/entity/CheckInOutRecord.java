package com.c2point.tms.entity;

import java.util.Date;

import javax.persistence.AttributeOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name="check_in_out")
@NamedQueries({
	@NamedQuery(name = "findCheckInOutByUser&Period",
					query = "SELECT checkIO "
							+ "FROM "
								+ "CheckInOutRecord checkIO, "
								+ "TmsUser user "
							+ "WHERE "
								+ "user = :user AND "
								+ "checkIO.userState = user.userState AND "
								+ "checkIO.dateCheckedIn BETWEEN :startDate AND :endDate"
							),
	@NamedQuery(name = "findCheckInOutByLineManager&Period",
	query = "SELECT checkIO "
			+ "FROM "
				+ "CheckInOutRecord checkIO, "
				+ "TmsUser user "
			+ "WHERE "
				+ "user.manager = :manager AND "
				+ "checkIO.userState = user.userState AND "
				+ "checkIO.dateCheckedIn BETWEEN :startDate AND :endDate"
			),
	@NamedQuery(name = "findCheckInOutByProjectManager&Period",
	query = "SELECT checkIO "
			+ "FROM "
				+ "CheckInOutRecord checkIO, "
				+ "TmsUser user "
			+ "WHERE "
				+ "checkIO.project.projectManager = :manager AND "
				+ "checkIO.userState = user.userState AND "
				+ "checkIO.dateCheckedIn BETWEEN :startDate AND :endDate"
			),
	@NamedQuery(name = "findCheckInOutByOrganisation&Period",
	query = "SELECT checkIO "
			+ "FROM "
				+ "CheckInOutRecord checkIO, "
				+ "TmsUser user "
			+ "WHERE "
				+ "user.organisation = :org AND "
				+ "checkIO.userState = user.userState AND "
				+ "checkIO.dateCheckedIn BETWEEN :startDate AND :endDate"
			),
	@NamedQuery(name = "countCheckInOutByUser&PrjMgr&Period",
	query = "SELECT count(checkIO.id) "
			+ "FROM "
				+ "CheckInOutRecord checkIO, "
				+ "TmsUserState state, "
				+ "TmsUser user, "
				+ "Project prj "
			+ "WHERE "
				+ "user = :user AND "
				+ "checkIO.project.projectManager = :prjmgr AND "
				+ "checkIO.dateCheckedIn BETWEEN :startDate AND :endDate AND "
				+ "checkIO.userState = user.userState"
			),
	@NamedQuery(name = "countCheckInOutByUser&Period",
	query = "SELECT count(checkIO.id) "
			+ "FROM "
				+ "CheckInOutRecord checkIO, "
				+ "TmsUser user "
			+ "WHERE "
				+ "user = :user AND "
				+ "checkIO.dateCheckedIn BETWEEN :startDate AND :endDate AND "
				+ "checkIO.userState = user.userState"
			),
})
public class CheckInOutRecord extends SimplePojo {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( CheckInOutRecord.class.getName());

	private TmsUserState	userState;
	private Project			project;

	@AttributeOverrides({
		@AttributeOverride(name="latitude", column= @Column(name="inlatitude")),
	    @AttributeOverride(name="longitude", column= @Column(name="inlongitude")),
	    @AttributeOverride(name="accuracy", column= @Column(name="inaccuracy"))
	})
	@Embedded
	private GeoCoordinates 	checkInGeo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date 			dateCheckedIn;
	
	@AttributeOverrides({
		@AttributeOverride(name="latitude", column= @Column(name="outlatitude")),
	    @AttributeOverride(name="longitude", column= @Column(name="outlongitude")),
	    @AttributeOverride(name="accuracy", column= @Column(name="outaccuracy"))
	})
	@Embedded
	private GeoCoordinates 	checkOutGeo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date 			dateCheckedOut;
	private boolean			checkOutByClient; 
	
	
	
	public CheckInOutRecord() {
		super();
	}

	public CheckInOutRecord( Date dateCheckedIn, TmsUserState userState, Project project ) {
		this( dateCheckedIn, userState, project, null );
	}

	public CheckInOutRecord( Date dateCheckedIn, TmsUserState userState, Project project, GeoCoordinates checkInGeo ) {
		super();
		this.dateCheckedIn = dateCheckedIn;
		this.dateCheckedOut = null;
		this.userState = userState;
		this.checkOutByClient = true;
		this.project = project;
		this.setCheckInGeo( checkInGeo );
	}



	public Date getDateCheckedIn() {
		return dateCheckedIn;
	}
	public void setDateCheckedIn(Date dateCheckedIn) {
		this.dateCheckedIn = dateCheckedIn;
	}
	public Date getDateCheckedOut() {
		return dateCheckedOut;
	}
	public void setDateCheckedOut( Date dateCheckedOut ) {
		setDateCheckedOut( dateCheckedOut, true );
	}
	public void setDateCheckedOut( Date dateCheckedOut, boolean didByClient ) {
		this.dateCheckedOut = dateCheckedOut;
		this.checkOutByClient = didByClient;
	}
	public TmsUserState getUserState() {
		return userState;
	}
	public void setUserState( TmsUserState userState ) {
		this.userState = userState;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	
	public GeoCoordinates getCheckInGeo() {
		return checkInGeo;
	}

	public void setCheckInGeo(GeoCoordinates checkInGeo) {
		this.checkInGeo = ( checkInGeo != null ? checkInGeo : new GeoCoordinates());
	}

	public GeoCoordinates getCheckOutGeo() {
		return checkOutGeo;
	}

	public void setCheckOutGeo( GeoCoordinates checkOutGeo ) {
		this.checkOutGeo = ( checkOutGeo != null ? checkOutGeo : new GeoCoordinates());
	}

	public boolean isCheckOutByClient() {
		return checkOutByClient;
	}

	public void setCheckOutByClient(boolean checkOutByClient) {
		this.checkOutByClient = checkOutByClient;
	}

	@Override
	public String toString() {
		return "CheckInOutRecord ["
				+ (dateCheckedIn != null ? "dateCheckedIn=" + dateCheckedIn
						+ ", " : "")
				+ (dateCheckedOut != null ? "dateCheckedOut=" + dateCheckedOut
						+ ", " : "")
				+ (userState != null ? "userState=" + userState + ", " : "")
				+ (project != null ? "project=" + project : "") + "]";
	}
	
	
}
