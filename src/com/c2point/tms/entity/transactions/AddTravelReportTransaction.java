package com.c2point.tms.entity.transactions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.AbstractReport;

@Entity
@DiscriminatorValue("add_travel")
public class AddTravelReportTransaction extends AbstractReportTransaction {

	public AddTravelReportTransaction( AbstractReport report ) throws JAXBException {
		super( report );
	}

	protected AddTravelReportTransaction() throws JAXBException {
		super();
	}
	
}
