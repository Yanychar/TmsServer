package com.c2point.tms.entity.stubs.checkinout;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.util.DateUtil;

@XmlRootElement(name = "checkedin")
public class CheckInRespStub {
	private String 	date;

	protected CheckInRespStub() {
		
	}
	
	public CheckInRespStub( TmsAccount account ) {
		try {
			this.date = DateUtil.dateAndTimeToString( account.getUser().getUserState().getDateCheckedIn());
		} catch ( Exception e ) {
			this.date = null;
		}
	}
	
	@XmlElement
	public String getDate() { return date; }
	public void setDate( String date ) {this.date = date; }

	
}
