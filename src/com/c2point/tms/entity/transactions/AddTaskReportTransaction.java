package com.c2point.tms.entity.transactions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.AbstractReport;

@Entity
@DiscriminatorValue("add_task")
public class AddTaskReportTransaction extends AbstractReportTransaction {

	public AddTaskReportTransaction( AbstractReport report ) throws JAXBException {
		super( report );
	}

	
	protected AddTaskReportTransaction() throws JAXBException {
		super();
	}
	
}
