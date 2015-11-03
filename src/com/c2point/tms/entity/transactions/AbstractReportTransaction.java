package com.c2point.tms.entity.transactions;

import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.xml.XMLconverter;

//@Entity
//@DiscriminatorValue("fimport")
public class AbstractReportTransaction extends Transaction {

	
	public AbstractReportTransaction() throws JAXBException {
		super( null, null );
	}
/*
	public AbstractReportTransaction( TmsAccount account, AbstractReport report ) throws JAXBException {
		super( account, DateUtil.getDate());
		createTransactionData( report );
	}
*/
	public AbstractReportTransaction( AbstractReport report ) throws JAXBException {
		super( report.getUser(), DateUtil.getDate());
		createTransactionData( report );
	}

	protected void createTransactionData( AbstractReport report ) throws JAXBException {
		
		TaskReportTransactionData data = new TaskReportTransactionData( report );
		
		this.setTransactionData( XMLconverter.convertToXML( data )); 
	}
	
}
