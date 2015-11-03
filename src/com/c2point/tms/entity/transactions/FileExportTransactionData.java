package com.c2point.tms.entity.transactions;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.stubs.transactions.OrganisationStub;

@XmlRootElement(name = "info")
@XmlType(propOrder = { "className", "fileNames", "stub", "errMsg" })
public class FileExportTransactionData {

	private String 				className;

	private String 				fileNames;
	private OrganisationStub	stub;
	private String 				errMsg;
	
	public FileExportTransactionData( String className, String inputFileNames, Organisation org, String errMsg ) throws JAXBException {
		this.className = className;
		this.fileNames = inputFileNames;
		this.stub = ( org != null ? new OrganisationStub( org ) : null );
		this.errMsg = ( errMsg != null ? "Errors!!! Lines: " + errMsg : null );
	}

	protected FileExportTransactionData() throws JAXBException {
		this( null, null, null, null );
	}
	
	
	@XmlElement(name = "processor")
	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	@XmlElement(name = "file")
	public String getFileNames() {
		return fileNames;
	}


	public void setFileNames( String fileNames ) {
		this.fileNames = fileNames;
	}


	@XmlElement(name = "organisation")
	public OrganisationStub getStub() {
		return stub;
	}


	public void setStub(OrganisationStub stub) {
		this.stub = stub;
	}

	@XmlElement(name = "errors")
	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	
}
