package com.c2point.tms.entity.transactions;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.xml.XMLconverter;

@Entity
@DiscriminatorValue("checkin")

@NamedQueries({
})


public class CheckInTransaction extends Transaction {
	

	public CheckInTransaction() throws JAXBException {
		this( null, null, null );
	}

	public CheckInTransaction( TmsUser user, Date time, Project project ) throws JAXBException {
		super( user, time );
		createTransactionData( project );
	}

	protected void createTransactionData( Project project ) throws JAXBException {
		CheckInOutTransactionData data = new CheckInOutTransactionData( this.getDate(), project );
		
		this.setTransactionData( XMLconverter.convertToXML( data )); 
	}
	
	
}
