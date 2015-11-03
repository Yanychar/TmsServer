package com.c2point.tms.entity.transactions.subcontracting;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.subcontracting.Contract;
import com.c2point.tms.entity.transactions.OperationType;
import com.c2point.tms.entity.transactions.Transaction;
import com.c2point.tms.util.xml.XMLconverter;

@Entity
@DiscriminatorValue("personimport")
public class SubcontractingTransaction extends Transaction {

	public SubcontractingTransaction( OperationType operation, Contract contract, boolean success ) throws JAXBException {
		super();
		createTransactionData( operation, contract, success );
	}
	public SubcontractingTransaction() throws JAXBException {
		super();
	}


	protected void createTransactionData( OperationType operation, Contract contract, boolean success  ) throws JAXBException {
		SubcontractingTransactionData data = new SubcontractingTransactionData( operation, contract, success  );
		this.setTransactionData( XMLconverter.convertToXML( data )); 
	}

}
