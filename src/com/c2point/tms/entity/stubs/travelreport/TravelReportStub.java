package com.c2point.tms.entity.stubs.travelreport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.util.DateUtil;

@XmlRootElement(name = "travel")
@XmlType(propOrder = { "uniqueReportId", "travelType", "date", "startDate", "endDate", "distance", "route", "approvalFlagType" })
public class TravelReportStub {

//	@XmlElement( required=false )
	private String 		uniqueReportId;

//	@XmlElement
	private TravelType	travelType;
//	@XmlElement( required=false)
	protected String	date;

//	@XmlElement( required=false )
	protected String	startDate;
//	@XmlElement( required=false )
	protected String	endDate;
	
//	@XmlElement
	private Integer		distance;
	
//	@XmlElement( required=false )
	private String		route;	
	
//	@XmlElement( required=false )
	private ApprovalFlagType	approvalFlagType;
	
	public TravelReportStub() {
	}

	public TravelReportStub( TravelReport report ) {
		this.uniqueReportId = report.getUniqueReportId();
		this.travelType = report.getTravelType();
		this.date = DateUtil.dateNoDelimToString( report.getDate());
		this.startDate = DateUtil.dateAndTimeToString( report.getStartDate());
		this.endDate = DateUtil.dateAndTimeToString( report.getEndDate());
		this.distance = report.getDistance();
		this.route = report.getRoute();
		this.approvalFlagType = report.getApprovalFlagType();
	}

	public TravelReportStub( 	String uniqueReportId, TravelType travelType,
								String date, String	startDate, String endDate,
								int distance, String route, 
								ApprovalFlagType approvalFlagType ) {

		this.uniqueReportId = uniqueReportId;
		this.travelType = travelType;
		this.date = date;
		this.startDate = startDate;
		this.endDate = endDate;
		this.distance = distance;
		this.route = route;
		this.approvalFlagType = approvalFlagType;
	}

	public TravelReportStub( 	String uniqueReportId, TravelType travelType,
			String date, String	startDate, String endDate,
			int distance, String route ) {
		this( uniqueReportId, travelType, date, startDate, endDate, distance, route, ApprovalFlagType.TO_CHECK );
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

	@XmlElement( required=false )
	public String getDate() {
		return date;
	}

	public void setDate( String date ) {
		this.date = date;
	}

	@XmlElement( required=false )
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate ) {
		this.startDate = startDate;
	}

	@XmlElement( required=false )
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

	@XmlElement( required=false )
	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public ApprovalFlagType getApprovalFlagType() {
		return approvalFlagType;
	}

	public void setApprovalFlagType( ApprovalFlagType approvalFlagType ) {
		this.approvalFlagType = approvalFlagType;
	}


}
