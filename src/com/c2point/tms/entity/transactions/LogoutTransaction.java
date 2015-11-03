package com.c2point.tms.entity.transactions;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.xml.XMLconverter;

@Entity
@DiscriminatorValue("logout")
public class LogoutTransaction extends Transaction {
	
	public LogoutTransaction() throws JAXBException {
		this( null, null, true );
	}
	public LogoutTransaction( TmsUser user, Date time, boolean bAutomaticFlag  ) throws JAXBException {
		super( user, time );
		createTransactionData( bAutomaticFlag );
	}

	protected void createTransactionData( boolean bAutomaticFlag ) throws JAXBException {
		LogoutTransactionData data = new LogoutTransactionData( bAutomaticFlag );
		
		this.setTransactionData( XMLconverter.convertToXML( data )); 
	}

}
