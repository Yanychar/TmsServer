package com.c2point.tms.entity.stubs.travelreport;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.c2point.tms.entity.TravelReport;

@XmlRootElement(name = "reports")
public class TravelReportsListStub {
	
	// This is project code. It is equal for all Travel Reports
	private String project;
	// Date of report (OPTIONAL)
	//   Date is the same for all Travel Reports saved
//    @XmlElement( name = "date", required=false)
//    private String	date;

    @XmlElement( name = "travel" )
	List<TravelReportStub> reports = new ArrayList<TravelReportStub>();
	
	public TravelReportsListStub() {
	}
	
	public TravelReportsListStub( List<TravelReport> list ) {
		init( list );
	}
	
	private void init( List<TravelReport> list ) {

		boolean initialised = false;
		
		for ( TravelReport report : list ) {
			if ( report != null ) {
				// Take Project information from TravelReport itself if it was not done already
				if ( !initialised ) {
					this.project = ( report.getProject() != null ? report.getProject().getCode() : null );
//					this.date = ( report.getDate() != null ? DateUtil.dateNoDelimToString( report.getDate()) : null );
					initialised = true;
				}
				
				this.reports.add( new TravelReportStub( report ));
			}
		}
		
	}
	
	public boolean addStub( TravelReportStub stub ) {
		if ( stub != null ) {
			return this.reports.add( stub );
		}
		return false;
	}
	
	public List<TravelReportStub> getReports() {
		return reports;
	}

	public String getProject() {
		return project;
	}

	public void setProject( String project ) {
		this.project = project;
	}
/*
	public String getDate() {
		return date;
	}

	public void setDate( String date ) {
		this.date = date;
	}
*/	
	
}
