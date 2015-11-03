package com.c2point.tms.entity.subcontracting;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.SimplePojo;

@Entity
@NamedQueries({
	// Query to fetch all Subcontractors of specified Contractor 
	@NamedQuery( name = "listContractsForContractor", 
	query = "SELECT contract FROM Contract contract " +
	 			"WHERE contract.contractor = :contractor " +
				"ORDER BY contract.subcontractor.name ASC"
	),
	// Query all Projects assigned by all Contractors to specified Subcontractor 
	@NamedQuery( name = "listContractsForSubcontractor", 
	query = "SELECT contract FROM Contract contract " +
	 			"WHERE contract.subcontractor = :subcontractor " +
				"ORDER BY contract.project.name ASC"
	),
	
	// Query all Subcontractors assigned to specified Project 
	@NamedQuery( name = "listContractsForProject", 
	query = "SELECT contract FROM Contract contract " +
	 			"WHERE contract.project = :project " +
				"ORDER BY contract.subcontractor.name ASC"
	),
	
	// Query particular Contract where all parameters specified 
	@NamedQuery( name = "findContract", 
	query = "SELECT contract FROM Contract contract " +
	 			"WHERE contract.contractor = :contractor " + "AND " + 
	 			"contract.subcontractor = :subcontractor " + "AND " + 
	 			"contract.project = :project " +

	 			"ORDER BY contract.subcontractor.name ASC"
	),
	
	
})
public class Contract extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Contract.class.getName());
	
	
	private Organisation	contractor;
	private Organisation	subcontractor;
	
	private Project 		project;

	protected Contract() {

		this( null, null, null );

	}

	public Contract( Organisation contractor, Organisation subContractor ) {
		
		this( contractor, subContractor, null );
		
	}

	public Contract( Organisation contractor, Organisation subcontractor, Project project ) {
		super();
		
		setContractor( contractor );
		setSubcontractor( subcontractor );
		setProject( project );
	}


	public Organisation getContractor() { return contractor; }
	public void setContractor( Organisation contractor ) { this.contractor = contractor; }

	public Organisation getSubcontractor() { return subcontractor; }
	public void setSubcontractor( Organisation subcontractor ) { this.subcontractor = subcontractor; }

	public Project getProject() { return project; }
	public void setProject( Project project ) { this.project = project; }

	
	/*
	 * Business methods
	 */
	
	
	/*
	 * Private methods
	 * 
	 */
	
	
}
