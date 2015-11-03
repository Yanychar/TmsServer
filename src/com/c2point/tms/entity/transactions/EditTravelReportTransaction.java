package com.c2point.tms.entity.transactions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.AbstractReport;

@Entity
@DiscriminatorValue("edit_travel")
public class EditTravelReportTransaction extends AbstractReportTransaction {

	public EditTravelReportTransaction( AbstractReport report ) throws JAXBException {
		super( report );
	}

	protected EditTravelReportTransaction() throws JAXBException {
		super();
	}
	
}
