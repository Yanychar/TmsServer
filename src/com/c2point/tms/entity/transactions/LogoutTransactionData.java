package com.c2point.tms.entity.transactions;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "info")
public class LogoutTransactionData {

	private String whoDid;
	
	public LogoutTransactionData( boolean bAutomaticFlag ) {
		super();
		this.whoDid = ( bAutomaticFlag ) ? "auto" : "client";
	}

	protected LogoutTransactionData() {
		this( true );
	}
	
	public String getWhoDid() {
		return whoDid;
	}

	public void setWhoDid( String whoDid ) {
		this.whoDid = whoDid;
	}


}
