package com.c2point.tms.web.ui.approveview.model;

import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.util.DateUtil;

public class ReportsFilter {

	private static Logger logger = LogManager.getLogger( ReportsFilter.class.getName());
	
	private Date startDate;
	private Date endDate;

	private boolean toCheck;
	private boolean rejected;
	private boolean approved;
	private boolean processed;

 	private SupportedFunctionType reportsToShow;

 	public ReportsFilter() {

		reportsToShow = SupportedFunctionType.REPORTS_OWN; 
		
		Calendar cal = DateUtil.getFirstDayOfWeek( new Date(), -2 );
		this.startDate = cal.getTime();
		cal.add( Calendar.DATE, 13 );
		this.endDate 	= cal.getTime();
		
		toCheck = true;
		rejected = true;
		approved = false;
		processed = false;
 		
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

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isToCheck() {
		return toCheck;
	}

	public void setToCheck(boolean toCheck) {
		this.toCheck = toCheck;
	}

	public boolean isRejected() {
		return rejected;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public SupportedFunctionType getReportsToShow() {
		return reportsToShow;
	}

	public void setReportsToShow( SupportedFunctionType reportsToShow ) {
		this.reportsToShow = reportsToShow;
	}

	public boolean isReportOk( ProjectHolder pHolder ) {
		int count = 0;
		
		if ( toCheck ) count += pHolder.getNoValidated(); 
		if ( rejected ) count += pHolder.getRejected();
		if ( approved ) count += pHolder.getApproved();
		if ( processed ) count += pHolder.getProcessed();
			
		return ( count > 0 );
		
	}
	
	public boolean isReportOk( AbstractReport report ) {
//		boolean bRes = true;

		if ( logger.isDebugEnabled()) {
			logger.debug( "Filter:\n" + toString());
			logger.debug( "Report ( date, approvalStatus): " + DateUtil.dateAndTimeToString( report.getDate()) + ", " + report.getApprovalFlagType());
			logger.debug( "report.getDate().compareTo( startDate ) = " + report.getDate().compareTo( startDate ));
			logger.debug( "report.getDate().compareTo( endDate ) = " + report.getDate().compareTo( endDate ));
		}

		if ( startDate != null && report.getDate().compareTo( startDate ) < 0 ) return false; 
		if ( endDate != null && report.getDate().compareTo( endDate ) > 0 ) return false; 

		if ( !toCheck && report.getApprovalFlagType() == ApprovalFlagType.TO_CHECK ||
			 !rejected && report.getApprovalFlagType() == ApprovalFlagType.REJECTED ||
			 !approved && report.getApprovalFlagType() == ApprovalFlagType.APPROVED ||
			 !processed && report.getApprovalFlagType() == ApprovalFlagType.PROCESSED ) return false;
		
		return true;
	}
	
	public String toString() {
		
		return "< " + DateUtil.dateAndTimeToString( startDate ) + " --- " + DateUtil.dateAndTimeToString( endDate ) + " >"; 
	}
}
