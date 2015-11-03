package com.c2point.tms.entity.stubs;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.util.DateUtil;

@XmlRootElement(name = "authenticated")
@XmlType(propOrder = { "code", "name", "sessionId", "checkedIn", "date" })
public class AuthenticationStub {
	
	private String 	code;
	private String 	name;
	private String 	sessionId = "";
	private boolean checkedIn;
	private String 	date;
	
	protected AuthenticationStub() {
		
	}
	
	public AuthenticationStub( TmsAccount user, Date date ) {
		this.code = user.getUser().getCode();
		this.name = user.getUser().getFirstAndLastNames();
		this.sessionId = user.getUniqueSessionID();
		this.checkedIn = user.getUser().isCheckedIn();
		this.date = DateUtil.dateAndTimeToString( date );
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the uniqueSessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param uniqueSessionId the uniqueSessionId to set
	 */
	public void setSessionId( String sessionId ) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the checkedIn
	 */
	public boolean isCheckedIn() {
		return checkedIn;
	}

	/**
	 * @param checkedIn the checkedIn to set
	 */
	public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}

	/**
	 * @return the time
	 */
	public String getDate() {
		return date;
	}

	public void setDate( String date ) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "AuthenticationStub ["
				+ (code != null ? "code=" + code + ", " : "")
				+ (name != null ? "name=" + name + ", " : "")
				+ (sessionId != null ? "sessionId=" + sessionId + ", " : "")
				+ "checkedIn=" + checkedIn + ", "
				+ (date != null ? "date=" + date : "") + "]";
	}
	
	

}
