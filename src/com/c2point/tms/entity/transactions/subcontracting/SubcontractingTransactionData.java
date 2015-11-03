package com.c2point.tms.entity.transactions.subcontracting;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.subcontracting.Contract;
import com.c2point.tms.entity.transactions.OperationType;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;

@XmlRootElement(name = "info")
@XmlType(propOrder = { "operation", "result", "contr", "subcontr", "project" })
@SuppressWarnings("unused")
public class SubcontractingTransactionData {

	private static Logger logger = LogManager.getLogger( SubcontractingTransactionData.class.getName()); 

	

	private String operation;
	private String result;

	private String contr;
	private String subcontr;
	private String project;
	
	/**
	 * @param org
	 */
	protected SubcontractingTransactionData( OperationType operation, 
					Organisation contractor, Organisation subcontractor, Project project, boolean success  ) {
		super();
		
		setOperation( operation.toString());
		setContr( contractor != null ? contractor.getName() : "" );
		setSubcontr( subcontractor != null ? subcontractor.getName() : "" );
		setProject( project != null ? project.getName() : "" );
		setResult( success );
		
	}

	protected SubcontractingTransactionData( OperationType operation, Contract contract, boolean success  ) {
		
		this( 
				operation, 
				contract != null ? contract.getContractor() : null, 
				contract != null ? contract.getSubcontractor() : null,
				contract != null ? contract.getProject() : null,
				success );
	}

	protected SubcontractingTransactionData() {
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getResult() { return result; }
	public void setResult( String result ) { this.result = result; }
	public void setResult( boolean result ) { this.result = ( result ? "OK" : "Failed" ); }

	public String getContr() {
		return contr;
	}

	public void setContr(String contr) {
		this.contr = contr;
	}

	public String getSubcontr() {
		return subcontr;
	}

	public void setSubcontr(String subcontr) {
		this.subcontr = subcontr;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}


}
