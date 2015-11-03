package com.c2point.tms.entity.transactions;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.xml.XMLconverter;

@Entity
@DiscriminatorValue("authenticated")
public class AuthenticateTransaction extends Transaction {
	

	public AuthenticateTransaction() throws JAXBException {
		this( null, null, null, null );
	}
/*	
	public AuthenticateTransaction( TmsAccount account, Date time ) throws JAXBException {
		this( account, time, null, null );
	}
*/	
	public AuthenticateTransaction( TmsUser user, Date date, String version, String imei ) throws JAXBException {
		super( user, date );
		
		createTransactionData( user, version, imei );
	}

	protected void createTransactionData( TmsUser user, String version, String imei ) throws JAXBException {
		AuthenticateTransactionData data = new AuthenticateTransactionData( user, version, imei );
		
		this.setTransactionData( XMLconverter.convertToXML( data )); 
	}
	
}
