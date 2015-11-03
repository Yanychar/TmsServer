package com.c2point.tms.entity.transactions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.AbstractReport;

@Entity
@DiscriminatorValue("delete_travel")
public class DeleteTravelReportTransaction extends AbstractReportTransaction {

	public DeleteTravelReportTransaction( AbstractReport report ) throws JAXBException {
		super( report );
	}

	protected DeleteTravelReportTransaction() throws JAXBException {
		super();
	}
	
}
