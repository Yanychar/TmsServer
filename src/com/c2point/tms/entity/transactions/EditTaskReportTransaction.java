package com.c2point.tms.entity.transactions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.AbstractReport;

@Entity
@DiscriminatorValue("edit_task")
public class EditTaskReportTransaction extends AbstractReportTransaction {

	public EditTaskReportTransaction( AbstractReport report ) throws JAXBException {
		super( report );
	}

	protected EditTaskReportTransaction() throws JAXBException {
		super();
	}
	
}
