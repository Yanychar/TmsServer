package com.c2point.tms.entity.stubs.travelreport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.util.DateUtil;

@XmlRootElement(name = "travel")
@XmlType(propOrder = { "uniqueReportId", "travelType", "startDate", "endDate", "distance", "route" })
public class TravelReportOutStub {

	private String 		uniqueReportId;

	private TravelType	travelType;

	protected String	startDate;
	protected String	endDate;
	
	private Integer		distance;
	
	private String		route;	
	
	public TravelReportOutStub() {
	}

	public TravelReportOutStub( TravelReport report ) {
		this.uniqueReportId = report.getUniqueReportId();
		this.travelType = report.getTravelType();
		this.startDate = DateUtil.dateAndTimeToString( report.getStartDate());
		this.endDate = DateUtil.dateAndTimeToString( report.getEndDate());
		this.distance = report.getDistance();
		this.route = report.getRoute();
	}

	public TravelReportOutStub( 	String uniqueReportId, TravelType travelType,
								String	startDate, String endDate,
								int distance, String route, 
								ApprovalFlagType approvalFlagType ) {

		this.uniqueReportId = uniqueReportId;
		this.travelType = travelType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.distance = distance;
		this.route = route;
	}

	public TravelReportOutStub( 	String uniqueReportId, TravelType travelType,
			String	startDate, String endDate,
			int distance, String route ) {
		this( uniqueReportId, travelType, startDate, endDate, distance, route, ApprovalFlagType.TO_CHECK );
	}
	
	
	@XmlElement( name = "reportId" )
	public String getUniqueReportId() {
		return uniqueReportId;
	}

	public void setUniqueReportId( String uniqueReportId ) {
		this.uniqueReportId = uniqueReportId;
	}

	@XmlElement(name = "type")
	public TravelType getTravelType() {
		return travelType;
	}

	public void setTravelType(TravelType travelType) {
		this.travelType = travelType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate ) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate( String endDate ) {
		this.endDate = endDate;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

}
