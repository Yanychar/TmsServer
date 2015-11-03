package com.c2point.tms.entity.transactions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.AbstractReport;

@Entity
@DiscriminatorValue("delete_task")
public class DeleteTaskReportTransaction extends AbstractReportTransaction {

	public DeleteTaskReportTransaction( AbstractReport report ) throws JAXBException {
		super( report );
	}

	protected DeleteTaskReportTransaction() throws JAXBException {
		super();
	}
	
}
