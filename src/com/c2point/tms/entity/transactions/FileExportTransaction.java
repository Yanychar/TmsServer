package com.c2point.tms.entity.transactions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.JAXBException;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.xml.XMLconverter;

@Entity
@DiscriminatorValue("fexport")
public class FileExportTransaction extends Transaction {

	public FileExportTransaction() throws JAXBException {
		this( null, null, null, null, null );
	}

	public FileExportTransaction( TmsUser user, String className, String exportFileNames, Organisation org, String errMsg ) throws JAXBException {
		super( user, DateUtil.getDate());
		createTransactionData( className, exportFileNames, org, errMsg );
	}
	
	protected void createTransactionData( String className, String exportFileNames, Organisation org, String errMsg ) throws JAXBException {
		FileExportTransactionData data = new FileExportTransactionData( className, exportFileNames, org, errMsg );
		
		this.setTransactionData( XMLconverter.convertToXML( data )); 
	}
	
}
