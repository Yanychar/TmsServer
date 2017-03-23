package com.c2point.tms.datalayer;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.transactions.subcontracting.SubcontractingTransaction;
import com.c2point.tms.entity.transactions.Transaction;
import com.c2point.tms.entity.subcontracting.Contract;
import com.c2point.tms.entity.transactions.OperationType;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;

public class SubcontractFacade {
	
	private static Logger logger = LogManager.getLogger( SubcontractFacade.class.getName());

	private static SubcontractFacade instance = null;
	
	public static SubcontractFacade getInstance() {

		if ( SubcontractFacade.instance == null ) {
			
			SubcontractFacade.instance = new SubcontractFacade();
			
		}
		return SubcontractFacade.instance;
	}
	
	private SubcontractFacade() {
		
	}

	public List<Contract> listContractsWithSubcontractors( Organisation org ) {

		return listContracts( org, true );
	}

	public List<Contract> listContractsWithContractors( Organisation org ) {

		return listContracts( org, false );
	}

	/* 
	 * Fetch all Contracts with specified Project
	 * 
	 */
	public List<Contract> listContracts( Project project ) {

		List<Contract> contractLst = null;
		
		if ( project == null )
			throw new IllegalArgumentException( "Valid Project shall be specified!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			TypedQuery<Contract> query = em.createNamedQuery( "listContractsForProject", Contract.class )
						.setParameter( "project", project );
			contractLst = query.getResultList();
			
		} catch ( NoResultException e ) {
			// Means that no subcontracors exist
			contractLst = new ArrayList<Contract>();
			if ( logger.isDebugEnabled())
				logger.debug( "No SubContractors found for Project: '" + project.getName() + "'");
		} catch ( Exception e ) {
			// Here is some bug occured!
			contractLst = null;
			logger.error( e );
			
		} finally {
			em.close();
		}

		return contractLst;
	}

	public Contract findContract( Contract contract ) {
		
		Contract retContract = null;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		try {
			
			// Fetched Contract with specified Contractor, Subcontractor and Project. Should be one or zero Contract only!!!  
			TypedQuery<Contract> q = em.createNamedQuery( "findContract", Contract.class )
					.setParameter( "contractor", contract.getContractor())
					.setParameter( "subcontractor", contract.getSubcontractor())
					.setParameter( "project", contract.getProject());
			
			retContract = q.getSingleResult();
			
		} catch ( NoResultException e ) {
			
			retContract = null;
			if ( logger.isDebugEnabled())
				logger.debug( "Contract Not Found. " 
						+ ( contract.getContractor() != null ? "Contractor: " + contract.getContractor().getName() : "No Contractor specified" )
						+ ( contract.getSubcontractor() != null ? "SubContractor: " + contract.getSubcontractor().getName() : "No SubContractor specified" )
						+ ( contract.getProject() != null ? "Project: " + contract.getProject().getName() : "No Project specified" ));
			
		} catch ( NonUniqueResultException e ) {
			logger.error( "More than one Contract found for: "
					+ ( contract.getContractor() != null ? "Contractor: " + contract.getContractor().getName() : "No Contractor specified" )
					+ ( contract.getSubcontractor() != null ? "SubContractor: " + contract.getSubcontractor().getName() : "No SubContractor specified" )
					+ ( contract.getProject() != null ? "Project: " + contract.getProject().getName() : "No Project specified" ));
					
		} catch ( Exception e ) {
			retContract = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return retContract;
	}

	
	
	
	public Contract addContract( Organisation contractor, Organisation subcontractor, Project project ) {
	
		if ( contractor == null )
			throw new IllegalArgumentException( "Valid Contractor shall be specified!" );
		if ( subcontractor == null )
			throw new IllegalArgumentException( "Valid SubContractor shall be specified!" );
		
		Contract contract = new Contract( contractor, subcontractor, project );
		
		contract = DataFacade.getInstance().insert( contract );


		writeTransaction( OperationType.Add, contract );
			
		return contract;
	}
		
	public Contract deleteContract( Organisation contractor, Organisation subcontractor, Project project ) {
		
		if ( contractor == null )
			throw new IllegalArgumentException( "Valid Contractor shall be specified!" );
		if ( subcontractor == null )
			throw new IllegalArgumentException( "Valid SubContractor shall be specified!" );
		
		Contract contract = findContract( new Contract( contractor, subcontractor, project ));		

		if ( contract != null ) {
			
			DataFacade.getInstance().remove( contract );

			logger.debug( "Contract has been deleted" );
		} else {
			
			contract = null;
			logger.debug( "Cannot delete Contract" );
		}


		writeTransaction( OperationType.Delete, contract );
			
		return contract;
	}
		
	private List<Contract> listContracts( Organisation org, boolean isContractor ) {

		List<Contract> contractLst = null;
		
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation shall be specified!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			TypedQuery<Contract> query;
			
			if ( isContractor ) {
				// Find all Contracts for Contractor
				query = em.createNamedQuery( "listContractsForContractor", Contract.class )
							.setParameter( "contractor", org );
			} else {
				query = em.createNamedQuery( "listContractsForSubContractor", Contract.class )
						.setParameter( "subcontractor", org );
			}
			contractLst = query.getResultList();
			
		} catch ( NoResultException e ) {
			// Means that no contracts exist
			contractLst = new ArrayList<Contract>();
			if ( logger.isDebugEnabled())
				logger.debug( "No Contracts found for Organisation( "
								+ ( isContractor ? "Contractor" : "Subcontractor" )
								+ " ) "
								+ "'" + org.getName() + "'" );
		} catch ( Exception e ) {
			// Here is some bug occured!
			contractLst = null;
			logger.error( e );
			
		} finally {
			em.close();
		}

		return contractLst;
	}
	
	private void writeTransaction( OperationType type, Contract contract ) {
		
		writeTransaction( type, contract, contract != null );

	}
	
	private void writeTransaction( OperationType type, Contract contract, boolean success ) {
		
		Transaction transaction;
		try {

			transaction = new SubcontractingTransaction( type, contract, success );
			
			transaction = DataFacade.getInstance().insert( transaction );

			if ( logger.isDebugEnabled()) logger.debug( transaction );
			
		} catch (JAXBException e) {
			logger.error( "Cannot write SubcontractingTransaction\n" + e );
			e.printStackTrace();
		}

	
	}
		
}

