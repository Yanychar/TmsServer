package com.c2point.tms.entity.stubs.travelreport;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.c2point.tms.entity.TravelReport;

@XmlRootElement(name = "reports")
public class TravelReportsListOutStub {
	
    @XmlElement( name = "travel" )
	List<TravelReportOutStub> reports = new ArrayList<TravelReportOutStub>();
	
	public TravelReportsListOutStub() {
	}
	
	public TravelReportsListOutStub( List<TravelReport> list ) {
		init( list );
	}
	
	private void init( List<TravelReport> list ) {

		for ( TravelReport report : list ) {
			if ( report != null ) {
				this.reports.add( new TravelReportOutStub( report ));
			}
		}
		
	}
	
	public boolean addStub( TravelReportOutStub stub ) {
		if ( stub != null ) {
			return this.reports.add( stub );
		}
		return false;
	}
	
	public List<TravelReportOutStub> getReports() {
		return reports;
	}
	
}
