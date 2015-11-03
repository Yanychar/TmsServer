package com.c2point.tms.entity.transactions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.AbstractReport;

@XmlRootElement(name = "info")
@XmlType(propOrder = { "reportId", "reportOwner" })
public class TaskReportTransactionData {

	private String		reportId;
	private String		reportOwner;
	
	protected TaskReportTransactionData( AbstractReport report ) {
		super();
		if ( report != null ) {
			this.reportId = report.getUniqueReportId();
			this.reportOwner = ( report.getUser() != null ? report.getUser().getFirstAndLastNames() : null );
		} else {
			this.reportId = null;
			this.reportOwner = null;
		}
	}

	protected TaskReportTransactionData() {
		this( null );
	}

	@XmlElement(name = "reportid")
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	@XmlElement(name = "reportowner")
	public String getReportOwner() {
		return reportOwner;
	}
	public void setReportOwner(String reportOwner) {
		this.reportOwner = reportOwner;
	}

	
	
}
