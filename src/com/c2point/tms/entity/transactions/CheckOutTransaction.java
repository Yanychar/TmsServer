package com.c2point.tms.entity.transactions;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.xml.XMLconverter;

@Entity
@DiscriminatorValue("checkout")
public class CheckOutTransaction extends Transaction {

	public CheckOutTransaction( TmsUser user, Date time, Project project ) throws JAXBException {
		super( user, time );
		createTransactionData( project );
	}
	public CheckOutTransaction() throws JAXBException {
		this( null, null, null );
	}


	protected void createTransactionData( Project project ) throws JAXBException {
		CheckInOutTransactionData data = new CheckInOutTransactionData( this.getDate(), project );
		this.setTransactionData( XMLconverter.convertToXML( data )); 
	}

}
