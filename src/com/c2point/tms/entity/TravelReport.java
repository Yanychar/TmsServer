package com.c2point.tms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;

@Entity
@Table(name="travelreport")
@NamedQueries({
	@NamedQuery(name = "findTravelReportByCode", query = "SELECT report FROM TravelReport report WHERE report.uniqueReportId = :reportId"),

	@NamedQuery(name = "findTravelsByDate&Person", 
			query = "SELECT report FROM TravelReport report WHERE " +
					"report.date = :date AND " +
					"report.user = :user AND " +
					"report.deleted = false"),
	@NamedQuery(name = "findTravelsByDate&Person&Project", 
					query = "SELECT report FROM TravelReport report WHERE " +
							"report.date = :date AND " +
							"report.user = :user AND " +
							"report.project.code = :projectCode AND " +
							"report.deleted = false"),
	
	@NamedQuery(name = "findAllTravelsByOrg&Period",
	query = "SELECT report FROM TravelReport report WHERE " +
			"report.org = :org AND " + 
			"report.date BETWEEN :startDate AND :endDate" ),

	@NamedQuery(name = "findTravelByPersonAndPeriod",
	query = "SELECT report FROM TravelReport report WHERE " +
			"report.user = :user AND " +
			"report.date BETWEEN :startDate AND :endDate" ),
			
	@NamedQuery(name = "findTravelByManager&Period",
					query = "SELECT report FROM TravelReport report WHERE " +
							"report.project.projectManager = :mngr AND " + 
							"report.date BETWEEN :startDate AND :endDate AND " +
							"report.deleted = false"),
})

public class TravelReport extends AbstractReport {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( TravelReport.class.getName());

	
    @Column(name="intTravelType")
	private TravelType	travelType;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date 		startDate;
	@Temporal(TemporalType.TIMESTAMP)
	protected Date 		endDate;
	
	private int			distance;
	
	@Column( length=512 )
	private String		route;	

	private Project 	project;
	
	public TravelReport() {
		super();
	}

	public TravelReport modifyReport( TravelReport otherReport ) {
		this.setApprovalFlagType( otherReport.getApprovalFlagType());
		this.travelType = otherReport.getTravelType();
//		this.date = otherReport.getDate();
		this.startDate = otherReport.getStartDate();
		this.endDate = otherReport.getEndDate();
		this.distance = otherReport.getDistance();
		this.route = otherReport.getRoute();
		if ( otherReport.getProject() != null ) {
			this.project = otherReport.getProject();
		}
	
		return this;
	}

	public TravelReport initReport( Date date, TmsUser user, 
									 Project project, 
									 TravelType travelType, 
									 Date startDate, Date endDate, int distance, String route ) {
		return initReport( null, date, user, project, travelType, startDate, endDate, distance, route );
	}
	
	public TravelReport initReport( String uniqueReportId, 
									 Date date, TmsUser user, 
									 Project project, 
									 TravelType travelType, 
									 Date startDate, Date endDate, int distance, String route ) {
		super.initReport( uniqueReportId, date, user);
		this.project = project;
		this.travelType = travelType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.distance = distance;
		this.route = route;	

		return this;
	}

	public TravelType getTravelType() {
		return travelType;
	}
	public void setTravelType(TravelType travelType) {
		this.travelType = travelType;
	}

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate( Date endDate ) {
		this.endDate = endDate;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}

	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public String toString() {
		return "TravelReport ["+(uniqueReportId != null ? "uniqueID=" + uniqueReportId + ", " : "")
				+ "date=" + (date != null ? date : "") + ", "
				+ (user != null ? "user=" + user.getFirstAndLastNames() + ", " : ", ")
				+ (getProject() != null ? "prj.code=" + getProject().getCode() + ", " : ", ")
				+ "travelType=" + travelType + ", "
				+ "startDate=" + (startDate != null ? DateUtil.dateAndTimeToString(startDate ) : "" ) + ", "
				+ "endDate=" + (endDate != null ? DateUtil.dateAndTimeToString( endDate ) : "" ) + ", "
				+ "distance=" + distance + ", "
				+ "route=" + (route != null ? route : "" ) 
				+ " ]";
	}

	public String toStringShort() {
		return "TravelReport ["+(uniqueReportId != null ? "uniqueID=" + uniqueReportId + ", " : "")
				+ (user != null ? "user=" + user.getLastName() + ", " : "")
				+ "travelType=" + travelType + ", "
				+ " ]";
	}


}
