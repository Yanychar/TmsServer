package com.c2point.tms.web.reporting.taskandtravel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;

public class TravelItem {
	
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( TravelItem.class.getName());
	
	private TravelReport	report;
	
	private AggregateItem	owner;
	
	public TravelItem( PrjItem pi, TravelReport report ) {
		this.owner	=  pi; 
		this.report	= report;
		handleReport( report );
	}

	public TravelReport getReport() { return report; }
	
	private void handleReport( TravelReport report ) {
		
		if ( report != null ) {
			if ( this.report.getTravelType() == TravelType.HOME ) {
				owner.addMatka( report.getApprovalFlagType(), report.getDistance());
			} else {
				owner.addAjo( report.getApprovalFlagType(), report.getDistance());
			}
		}
		
	}
	
	public TravelType getTravelType() {
		return report.getTravelType();
	}
	
	public int getDistance() {
		return report.getDistance();
	}
	
	
	@Override
	public String toString() {
		return "        Travel Item ['" + getTravelType() + "', distance=" + getDistance() + " km, " + report.getApprovalFlagType() + " ]";
	}

}
