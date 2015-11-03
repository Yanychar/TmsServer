package com.c2point.tms.entity.transactions;

import java.io.File;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.xml.XMLconverter;

@Entity
@DiscriminatorValue("fimport")
public class FileImportTransaction extends Transaction {

	public FileImportTransaction() throws JAXBException {
		this( null, null, null, null, null );
	}

	public FileImportTransaction( TmsUser user, String className, File inputFile, Organisation org, String errMsg ) throws JAXBException {
		super( user, DateUtil.getDate());
		createTransactionData( className, inputFile, org, errMsg );
	}
	
	protected void createTransactionData( String className, File inputFile, Organisation org, String errMsg ) throws JAXBException {
		FileImportTransactionData data = new FileImportTransactionData( className, inputFile, org, errMsg );
		
		this.setTransactionData( XMLconverter.convertToXML( data )); 
	}
	
}
