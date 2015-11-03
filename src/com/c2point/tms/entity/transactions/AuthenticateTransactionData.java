package com.c2point.tms.entity.transactions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TmsUser;

@XmlRootElement(name = "info")
@XmlType(propOrder = { "usrName", "app_version", "hw_imei" })
public class AuthenticateTransactionData {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( AuthenticateTransactionData.class.getName()); 
	
	private String		usrName;
	private String		app_version;
	private String		hw_imei;

	/**
	 * @param info
	 */
	public AuthenticateTransactionData( TmsUser user, String version, String imei ) {
		super();
		this.usrName = ( user != null ? user.getFirstAndLastNames() : null );
		this.app_version = version;
		this.hw_imei = imei;
	}

	public AuthenticateTransactionData() {
		this( null, null, null );
	}

	/**
	 * @return the info
	 */
	@XmlElement(name = "username")
	public String getUsrName() {
		return usrName;
	}

	public void setUsrName(String usrName) {
		this.usrName = usrName;
	}

	@XmlElement(name = "app_version")
	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	@XmlElement(name = "imei")
	public String getHw_imei() {
		return hw_imei;
	}

	public void setHw_imei(String hw_imei) {
		this.hw_imei = hw_imei;
	}

}
