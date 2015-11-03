package com.c2point.tms.datalayer;

import java.util.Collection;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.Task;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.exception.NotUniqueCode;

public class OrganisationFacade {
	private static Logger logger = LogManager.getLogger(OrganisationFacade.class.getName());

	public static OrganisationFacade getInstance() {

		return new OrganisationFacade();
	}
	
	private OrganisationFacade() {}

	/* Fetch metadata methods */
	public Organisation getOrganisation( String code ) {
		if ( logger.isDebugEnabled()) logger.debug( "getOrganication with code ==" + code );
		Organisation org = null;
		// Try to fetch it from DB
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Organisation with specified code. Should be one Organisation only!!!  
			TypedQuery<Organisation> q = em.createNamedQuery( "findOrganisationByCode", Organisation.class )
					.setParameter("code", code );
			org = q.getSingleResult();
		} catch ( NoResultException e ) {
			org = null;
			logger.debug( "Not found: NoResultException for Organisation.code: '" + code + "'" );
		} catch ( NonUniqueResultException e ) {
			org = null;
			logger.error( "It should be one Organisation only for Organisation.code: '" + code + "'" );
		} catch ( Exception e ) {
			org = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
//		org = validateRootDepartment( org );
		
		return org;

	}
	public Organisation addOrganisation( Organisation org ) {
		
		if ( org == null ) {
			logger.error( "org cannot be null to add Organisation!" );
			return null;
		}
		// 	Find in DB
		Organisation oldOrg = getOrganisation( org.getCode());
		// If not found than
		if ( oldOrg == null ) {
			if ( logger.isDebugEnabled()) logger.debug( org + " does not exist in Db. Will be added!" );
			
			// Add Organisation and its ServiceOwner
			try {
				org = DataFacade.getInstance().insert( org );

				if ( logger.isDebugEnabled()) logger.debug( org + " was added to db successfully" );
			} catch ( Exception e ) {
				logger.error("Cannot add " + org );
				logger.error( e );
				return null;
			}
			
			// Now setup proper Security Groups
			Organisation secOrg = AccessRightsFacade.getInstance().updateSecurityGroups( org );
			if ( secOrg != null ) {
/*				
				secOrg.getServiceOwner().getContext().setSecGroup( secOrg.getSecurityGroups().get( AccessRightsFacade.SERVICE_OWNER_GROUP_ID ));
				
				// Save if Security Group has been assigned successfully
				if ( secOrg.getServiceOwner().getContext().getSecGroup() != null ) {
					secOrg = DataFacade.getInstance().merge( secOrg );
				}
*/
				
				
			} else {
				logger.error( "Security Group was not set for company: " + org.toStringShort());
			}

			org = DataFacade.getInstance().find( Organisation.class, org.getId());
			
		} else {
			logger.error( org + " exists in DB already! Cannot be added" );
			return null;
		}
		
		return org;
	}

	public Organisation updateOrganisation( Organisation organisation ) {
		if ( organisation == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );
		
		// Find Organisation
		Organisation oldOrganisation = DataFacade.getInstance().find( Organisation.class, organisation.getId());
		
		if ( oldOrganisation == null ) {
			// No such Organisation!!!
			logger.error( "Organisation " + organisation + " was not found during Organisation update!" );
			return null;
		}
		
		// update Organisation
		oldOrganisation.update( organisation );
		
		// Store Organisation
		
		try {
			organisation = DataFacade.getInstance().merge( oldOrganisation );
		} catch ( Exception e ) {
			logger.error( "Failed to update Organisation: " + oldOrganisation );
			logger.error( e );
			return null;
		}
		

		if ( logger.isDebugEnabled() && organisation != null ) 
			logger.debug( "Organisation has been updated: " + organisation );
		
		
		return organisation;
		
	}
	
	public boolean deleteOrganisation( Organisation organisation ) {
		boolean bRes = false;
		
		if ( organisation == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );
		
		// Find Organisation
		Organisation oldOrganisation = DataFacade.getInstance().find( Organisation.class, organisation.getId());
		
		if ( oldOrganisation == null ) {
			// No such Organisation!!!
			logger.error( "Organisation " + organisation + " was not found during Organisation delete!" );
			return bRes;
		}
		
		// update Organisation
		oldOrganisation.setDeleted();
		
		// Store Organisation
		
		try {
			organisation = DataFacade.getInstance().merge( oldOrganisation );
			bRes = true;
		} catch ( Exception e ) {
			logger.error( "Failed to update Organisation (during deletion!): " + oldOrganisation );
			logger.error( e );
			return bRes;
		}
		

		if ( logger.isDebugEnabled() && organisation != null ) 
			logger.debug( "Organisation has been deleted: " + organisation );
		
		
		return bRes;
		
	}
	
	
	public Collection<Organisation> getOrganisations() {
		return getOrganisations( ShowType.CURRENT );
	}

	public Collection<Organisation> getOrganisations( ShowType type ) {
		Collection<Organisation> lst = DataFacade.getInstance().list( Organisation.class );
		
		Organisation org;
		for ( Iterator<Organisation> it = lst.iterator(); it.hasNext(); ) {
			org = it.next();
			if ( org != null ) {
				if ( type == ShowType.CURRENT && org.isDeleted()
						||
					 type == ShowType.DELETED && !org.isDeleted()) {
		            it.remove();
				}
			}
		}
		
		return lst;
	}

	/**
	 * @author sevastia
	 * 
	 * Analyse Organisation and add fake Task and assign fake Task to every Project in the system
	 *
	 */
	public boolean fakeAssignTasks( String orgCode ) throws NotUniqueCode {
		
		boolean res = false;
		
		if ( orgCode == null ) {
			logger.error( "Org Code cannot be null to process!" );
			return false;
		}

		// 	Find in DB
		Organisation org = getOrganisation( orgCode );
		// If not found than
		if ( org == null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Did not find Organisation with Code: " + orgCode );
			return false;
		}

		// Check that fake shall be used
//		String propStr = TmsConfiguration.getOrganisationProperty( orgCode, "company.projects.fakeusage" );
		String propStr = "true";
		if ( !Boolean.parseBoolean( propStr )) {
			logger.debug( "Not necessary to add 'fake' project and task for Organisation: " + org.getName() );
			return true;
		}
		
//		String fakeTaskCode = TmsConfiguration.getOrganisationProperty( orgCode, "company.projects.faketaskcode" );
		String fakeTaskCode = null;	
		if ( fakeTaskCode == null ) {
			fakeTaskCode = Task.DEFAULT_FAKE_TASK_CODE;
		}
		
		// Validate and add Fake Task if necessary
		Task task; 
		boolean needToBeSaved = false;
		if ( org.taskExists( fakeTaskCode )) {
			task = org.getTask( fakeTaskCode );
		} else {
			task = new Task( org, fakeTaskCode, "Other tasks" );
			
			org.addTask( task );
			needToBeSaved = true;
		}
		
		// Validate and assign Fake Task to Fake Project if necessary
		if ( task != null ) {
			for ( Project project : org.getProjects().values()) {
				if ( project.getProjectTask( fakeTaskCode ) == null ) {
					project.assignTask( task );
					needToBeSaved = true;
				}
			}
			
		}
		
		// Store Org data!!!
		if ( needToBeSaved ) {
			try {
				org = DataFacade.getInstance().merge( org );
				res = ( org != null );
			} catch (Exception e) {
				logger.error( e );
				res = false;
			}
		} else {
			logger.debug( "No projects, Tasks, TaskInProject-s were added. Noting to save! " );
			res = true;
		}
		
		return res;
	}
	
	public TmsUser addUser( Organisation org, TmsUser user ) {
		TmsUser result = null;
		
		if ( user == null )
			throw new IllegalArgumentException( "Valid User cannot be null!" );
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );
		
		org = DataFacade.getInstance().find( Organisation.class, org.getId());
		if ( org == null )
			throw new IllegalArgumentException( "Cannot find Organisation!" );
		
		
		// Add user to the organisation
		if ( org.addUser( user )) {
			// Merge organisation
			Organisation newOrg = null;
			try {
				newOrg = DataFacade.getInstance().merge( org );
				
				// Find User in MERGED org
				result = newOrg.getUser( user.getCode());
			} catch (Exception e) {
				logger.error( "Cannot add user: " + user.getFirstAndLastNames() + "\n" + e );
			}
			
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "  User was NOT added to DB" );
		}
		
		return result;
	}
	
	public TmsUser updateUser( Organisation org, TmsUser user ) {
		TmsUser result = null;
		
		if ( user == null )
			throw new IllegalArgumentException( "Valid User cannot be null!" );
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		org = DataFacade.getInstance().find( Organisation.class, org.getId());
		if ( org == null )
			throw new IllegalArgumentException( "Cannot find Organisation!" );

		// Update user 
		if ( org.updateUser( user )) {
			// Merge organisation
			Organisation newOrg = null;
			try {
				newOrg = DataFacade.getInstance().merge( org );
				
				// Find User in MERGED org
//				newOrg = DataFacade.getInstance().find( Organisation.class, org.getId());
				result = newOrg.getUser( user.getCode());
			} catch (Exception e) {
				logger.error( "Cannot update user: " + user.getFirstAndLastNames() + "\n" + e );
			}
			
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "  User was NOT updated to DB" );
		}
		
		return result;
	}
	
	public TmsUser removeUser( TmsUser user ) {
		TmsUser result = null;
		
		if ( user == null )
			throw new IllegalArgumentException( "Valid User cannot be null!" );
		if ( user.getOrganisation() == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		// Delete Account firstly!
		TmsAccount account = AuthenticationFacade.getInstance().deleteAccount( user );
		if ( account == null ) {
			logger.error( "Cannot remove account for user: " + user.getFirstAndLastNames() + ". Continue to remove TmsUser");
		}

		Organisation org = DataFacade.getInstance().find( Organisation.class, user.getOrganisation().getId());
		if ( org == null )
			throw new IllegalArgumentException( "Cannot find Organisation!" );

		// Delete user 
		if ( org.removeUser( user )) {
			// Merge organisation
			Organisation newOrg = null;
			try {
				newOrg = DataFacade.getInstance().merge( org );
				
				// Find User in MERGED org
				result = newOrg.getUser( user.getCode());
			} catch (Exception e) {
				logger.error( "Cannot set user: " + user.getFirstAndLastNames() + "  as deleted \n" + e );
			}
			
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "  User was NOT marked as DELETED in the DB" );
		}
		
		return result;
	}
	
	public Task addTask( Organisation org, Task task ) throws NotUniqueCode {
		
		DataFacade df = DataFacade.getInstance();
		
		if ( org != null && task != null ) {

			Organisation lOrg = df.find( Organisation.class, org.getId()); 
			
			// Add to task to Organisation and check that code is unique
			if ( lOrg != null && lOrg.addTask( task ) != null ) {

				// Store Organisation in the DB (merge)
				try {

					lOrg = df.merge( lOrg );
					logger.debug( "Task [" + task.getName() + "] was addded to Organisation [" + org.getName() + "] successfully" );

					return lOrg.getTask( task.getCode());

				} catch ( Exception e ) {
					logger.error( "Failed to update Organisation in DB!" );
				}
			} else {
				logger.error( "Failed to add Task " + task  );
			}
		} else {
			logger.error( "One of parameters is null!" );
		}
		
		return null;
		
	}
	
	public Task deleteTask( Organisation org, Task task ) {

		task.setDeleted();
		try {
			return updateTask( org, task );
		} catch ( Exception e ) {
			logger.error( "Failed to store deleted Task in DB!" );
			return null;
		}
	}
	
	public Task updateTask( Organisation org, Task task ) throws NotUniqueCode {

		DataFacade df = DataFacade.getInstance();
		
		if ( org != null && task != null ) {

			Organisation lOrg = df.find( Organisation.class, org.getId()); 

			// Add to task to Organisation and check that code is unique
			if ( lOrg != null && lOrg.updateTask( task ) != null ) { 

				// Store Organisation in the DB ( merge)
				try {
					
					lOrg = df.merge( lOrg );
					
					task = lOrg.getTask( task.getCode());
					logger.debug( task + " was updated in Organisation [" + org.getName() + "] successfully" );
					
					return task;
					
				} catch ( Exception e ) {
					logger.error( "Failed to update Organisation in DB!" );
				}
			} else {
				logger.error( "Failed to update Task " + task  );
			}
		} else {
			logger.error( "One of parameters is null!" );
		}
		
		return null;
		
	}
	
	public ProjectTask assignTask( Project project, Task task ) {

		DataFacade df = DataFacade.getInstance();
		
		if ( project != null && task != null ) {
			
			Project lProject = df.find( Project.class, project.getId());
		
			// Add to task to Organisation and check that code is unique
			if ( lProject != null && lProject.assignTask( task) != null ) { 

				try {
					// Store Organisation in the DB (merge)
					lProject =  DataFacade.getInstance().merge( lProject );
					
					if ( lProject != null ) {
	
					logger.debug( "Task [" + task.getName() + "] was assigned to the Project[" + project.getName() + "] successfully" );

					return lProject.getProjectTask( task.getCode());
								
					} else {
						logger.error( "Failed to update Project in DB!" );
					}
				} catch ( Exception e ) {

					logger.error( "Failed to update Project in DB!" );
				}
			}
		} else {
			logger.error( "One of parameters is null!" );
		}
		
		return null;
		
	}
	
	public ProjectTask updateAssignedTask( ProjectTask pTask ) throws NotUniqueCode {

		DataFacade df = DataFacade.getInstance();
		
		if ( pTask != null && pTask.getProject() != null && pTask.getTask() != null ) {

			Project lPrj = df.find( Project.class, pTask.getProject().getId()); 

			// Add to task to Organisation and check that code is unique
			if ( lPrj != null && lPrj.updateAssignedTask( pTask ) != null ) { 

				// Store Organisation in the DB ( merge)
				try {
					
					lPrj = df.merge( lPrj );
					logger.debug( "ProjectTask: " + pTask + " from Project: " + lPrj + " was updated successfully" );
					
					return lPrj.getProjectTask( pTask );
					
				} catch ( Exception e ) {
					logger.error( "Failed to update Project in DB!" );
				}
			} else {
				logger.error( "Failed to update ProjectTask " + pTask  );
			}
		} else {
			logger.error( "One of parameters is null!" );
		}
		
		return null;
		
	}
	
	public boolean deleteTaskAssignment( ProjectTask pTask ) {

		DataFacade df = DataFacade.getInstance();
		Organisation org = null; 
		Project project = null;

		
		// Remove from DB
		boolean bRes = false;
		
		try {
			df.remove( pTask );

		} catch ( PersistenceException pe ) {
			// Seems that df cannot be removed because it is referenced by probably TaskReports
			// This is not an error! Just save the entity!
			try {
				pTask.setDeleted();
				pTask = df.merge( pTask );
				logger.debug( "ProjectTask: " + pTask + " was updated successfully" );
				
			} catch ( Exception ex ) {
				logger.error( "Failed to update ProjectTask in DB!" );
			}
			
		} catch ( Exception e ) {
			logger.error( "Failed to update ProjectTask in DB!" );
		}

		try {
			org = df.find( Organisation.class, pTask.getProject().getOrganisation().getId());
			project = org.getProject( pTask.getProject().getCode());
			project.deleteAssignedTask( pTask );
			
			df.merge( org );

			
			bRes = true;
			
		} catch ( Exception e ) {
			logger.error( "Failed to update Organisation in DB!" );
		}
		
		
		
		return bRes;
	}
	
	
	
	
}

