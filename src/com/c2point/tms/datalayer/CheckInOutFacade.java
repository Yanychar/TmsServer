package com.c2point.tms.datalayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.GeoCoordinates;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.exception.UserDisabledException;

public class CheckInOutFacade {
	private static Logger logger = LogManager.getLogger( CheckInOutFacade.class.getName()); 
	
//	private static CheckInOutFacade instance = null;

	public static CheckInOutFacade getInstance() {
	//	if ( instance == null ) {
		CheckInOutFacade instance = new CheckInOutFacade();
//		}
		return instance;
	}
	
	/**
	 * @param user
	 * @return 	Transaction if success
	 * 			null otherwise
	 */
	public boolean checkIn( TmsAccount account, String projectCode, GeoCoordinates checkInGeo ) {
		boolean retRes = false;
		
		if ( account == null ) {
			logger.error( "Account for Check-In cannot be null!" );
			return retRes;
		}
		if ( account.getUser() == null  ) {
			logger.error( account + " is not attached to the person. Not possible to Check-In!" );
			return retRes;
		}
		
		// Determine Date/Time
		Date date = DateUtil.getDate();

		Project project = null;

		// Check CheckedIn status of TmsUser. CheckOut if checked in
		if ( account.getUser().isCheckedIn()) {
			try {
				checkOutByServer( account );
				account = DataFacade.getInstance().find( TmsAccount.class, account.getId());
//				if ( logger.isDebugEnabled()) {
//				}
			} catch ( UserDisabledException e ) {
				logger.error( "Failed to make checkOut inside CheckIn (checkedIn already). Account: " + account );
			}
		}

		// Find out the Project by projectCode
		project = getProject( account, projectCode );
		
		if ( project == null ) {
			// Not found
			if ( logger.isDebugEnabled()) logger.debug( "Project: [code=" + projectCode + "] not found" );
			return retRes;
		}
		
		// Write CheckedIn status of TmsUser to TRUE
		CheckInOutRecord checkInOutRecord = null;
		DataFacade facade = DataFacade.getInstance();
		EntityManager em = facade.createEntityManager();
		try {
			em.getTransaction().begin();

			account = em.find( TmsAccount.class, account.getId());
			
			checkInOutRecord = account.getUser().getUserState().checkIn( date, account.getUser(), project );
			if ( checkInOutRecord != null ) {
				checkInOutRecord.setCheckInGeo( checkInGeo );
				em.persist( checkInOutRecord);
				account = em.merge( account );
			}

			if ( logger.isDebugEnabled()) {
				logger.debug( "  CheckIn data stored in DB:  "
					+ "Date='"+DateUtil.dateAndTimeToString( checkInOutRecord.getDateCheckedIn()) + "', "
					+ "Project='" + checkInOutRecord.getProject().getName() + "', "
					+ "Checked-In=" + checkInOutRecord.getCheckInGeo()
					);
			}
			
			
			em.flush();
			em.getTransaction().commit();
			retRes = true;
		} catch (RollbackException e) {
			throw e;
		} catch ( Exception e ) {
			logger.error( "Cannot set user session state as 'Checked-In'" );
			logger.error( e );
		} finally {
			em.close();
		}

		return retRes;
	}
	
	public TmsAccount checkOutByPerson( TmsAccount account, GeoCoordinates checkOutGeo ) throws UserDisabledException {
		return checkOut( account, true, checkOutGeo );
	}

	public TmsAccount checkOutByServer( TmsAccount account ) throws UserDisabledException {
		return checkOut( account, false, null );
	}

	public List<CheckInOutRecord> getList( TmsUser user, Date startDate, Date endDate ) { 

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to get CheckInOut records ..." );
		}
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		TypedQuery<CheckInOutRecord> query = null;

		try {
			Calendar endCal = Calendar.getInstance();
			endCal.setTime( endDate );
			endCal.add( Calendar.DAY_OF_MONTH, 1 );   

			query = em.createNamedQuery( "findCheckInOutByUser&Period", CheckInOutRecord.class )
					.setParameter( "user", user )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endCal.getTime(), TemporalType.DATE );
		} catch ( Exception e ) {
			logger.error( "Cannot create 'findCheckInOutByUser&Period' query for CheckInOutRecords" );
			logger.error( e );
			em.close();
			return null;
		}
			
		List<CheckInOutRecord> records = null;

		try {
			records = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched CheckInOutRecord-s: " + records.size());
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "CheckInOutRecord-s Not Found forTmsUser: " + user.getFirstAndLastNames());
		} catch ( Exception e ) {
			records = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return records;
	}

	/*
	 * Get all records for one organisation
	 */
	public List<CheckInOutRecord> getList( Organisation org, Date startDate, Date endDate ) { 

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to get CheckInOut records ..." );
		}
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		TypedQuery<CheckInOutRecord> query = null;

		try {
			Calendar endCal = Calendar.getInstance();
			endCal.setTime( endDate );
			endCal.add( Calendar.DAY_OF_MONTH, 1 );   

			query = em.createNamedQuery( "findCheckInOutByOrganisation&Period", CheckInOutRecord.class )
					.setParameter( "org", org )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endCal.getTime(), TemporalType.DATE );
		} catch ( Exception e ) {
			logger.error( "Cannot create 'findCheckInOutByOrganisation&Period' query for CheckInOutRecords" );
			logger.error( e );
			em.close();
			return null;
		}
			
		List<CheckInOutRecord> records = null;

		try {
			records = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched CheckInOutRecord-s: " + records.size());
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "CheckInOutRecord-s Not Found for Organisation:" + org );
		} catch ( Exception e ) {
			records = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return records;
	}

	
	/*
	 * Get all records for users where Line Manager specified
	 */
	public List<CheckInOutRecord> getListForLineManager( TmsUser mngr, Date startDate, Date endDate ) { 

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to get CheckInOut records ..." );
		}
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		TypedQuery<CheckInOutRecord> query = null;

		try {
			Calendar endCal = Calendar.getInstance();
			endCal.setTime( endDate );
			endCal.add( Calendar.DAY_OF_MONTH, 1 );   

			query = em.createNamedQuery( "findCheckInOutByLineManager&Period", CheckInOutRecord.class )
					.setParameter( "manager", mngr )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endCal.getTime(), TemporalType.DATE );
		} catch ( Exception e ) {
			logger.error( "Cannot create 'findCheckInOutByLineManager&Period' query for CheckInOutRecords" );
			logger.error( e );
			em.close();
			return null;
		}
			
		List<CheckInOutRecord> records = null;

		try {
			records = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched CheckInOutRecord-s: " + records.size());
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "CheckInOutRecord-s Not Found for LineManager: " + mngr.getFirstAndLastNames());
		} catch ( Exception e ) {
			records = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return records;
	}

	/*
	 * Get all records for users where Project Manager specified
	 */
	public List<CheckInOutRecord> getListForProjectManager( TmsUser mngr, Date startDate, Date endDate ) { 

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to get CheckInOut records ..." );
		}
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		TypedQuery<CheckInOutRecord> query = null;

		try {
			Calendar endCal = Calendar.getInstance();
			endCal.setTime( endDate );
			endCal.add( Calendar.DAY_OF_MONTH, 1 );   

			query = em.createNamedQuery( "findCheckInOutByProjectManager&Period", CheckInOutRecord.class )
					.setParameter( "manager", mngr )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endCal.getTime(), TemporalType.DATE );
		} catch ( Exception e ) {
			logger.error( "Cannot create 'findCheckInOutByLineManager&Period' query for CheckInOutRecords" );
			logger.error( e );
			em.close();
			return null;
		}
			
		List<CheckInOutRecord> records = null;

		try {
			records = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched CheckInOutRecord-s: " + records.size());
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "CheckInOutRecord-s Not Found for Project Manager: " + mngr.getFirstAndLastNames());
		} catch ( Exception e ) {
			records = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return records;
	}

	public int getCount( TmsUser user, TmsUser prjMgr, Date startDate, Date endDate ) { 

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to Count CheckIn/Outs ..." );
		}
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		int val = -1;

		try {
			Calendar endCal = Calendar.getInstance();
			endCal.setTime( endDate );
			endCal.add( Calendar.DAY_OF_MONTH, 1 );   

			Query query = em.createNamedQuery( "countCheckInOutByUser&PrjMgr&Period", CheckInOutRecord.class )
					.setParameter( "user", user )
					.setParameter( "prjmgr", prjMgr )
//					.setParameter( "prj", prj )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endCal.getTime(), TemporalType.DATE );
			val = (( Long )query.getSingleResult()).intValue();
		} catch ( Exception e ) {
			logger.error( "Cannot get count of CheckInOut Record" );
			logger.error( e );
			em.close();
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "...end to Count CheckIn/Outs ..." );
		return val;
	}

	public int getCount( TmsUser user, Date startDate, Date endDate ) { 

			if ( logger.isDebugEnabled()) {
				logger.debug( "Start to Count CheckIn/Outs ..." );
			}
			
			EntityManager em = DataFacade.getInstance().createEntityManager();
			
			int val = -1;

			try {
				Calendar endCal = Calendar.getInstance();
				endCal.setTime( endDate );
				endCal.add( Calendar.DAY_OF_MONTH, 1 );   

				Query query = em.createNamedQuery( "countCheckInOutByUser&Period", CheckInOutRecord.class )
						.setParameter( "user", user )
						.setParameter( "startDate", startDate, TemporalType.DATE )
						.setParameter( "endDate", endCal.getTime(), TemporalType.DATE );
				val = (( Long )query.getSingleResult()).intValue();
			} catch ( Exception e ) {
				logger.error( "Cannot get count of CheckInOut Record" );
				logger.error( e );
				em.close();
			}
			
			if ( logger.isDebugEnabled()) logger.debug( "...end to Count CheckIn/Outs ..." );
			return val;
		}

	
	private TmsAccount checkOut( TmsAccount account, boolean checkOutByClient, GeoCoordinates checkOutGeo ) throws UserDisabledException {
		TmsAccount retAccount = null;
		
		if ( account == null ) {
			logger.error( "Account for Check-Out cannot be null!" );
			return retAccount;
		}
		if ( account.getUser() == null  ) {
			logger.error( account + " is not attached to the person. Not possible to Check-Out!" );
			return retAccount;
		}
		
		// Determine Date/Time
		Date date = DateUtil.getDate();
		if ( logger.isDebugEnabled()) {
			logger.debug( "  Current date has been alocated for Check-Out" );
			logger.debug( "  Check-Out " + account );
			logger.debug( "  Check-Out " + account.getUser() );
		}

		// Check CheckedIn status of TmsUser
		// If the person CheckedIn ==>> Write into the log that person did not check out
		if ( account.getUser().isCheckedIn()) {
			
			DataFacade facade = DataFacade.getInstance();
			EntityManager em = facade.createEntityManager();
			CheckInOutRecord checkInOutRecord  = null;
			
			try {
				em.getTransaction().begin();

				account = em.find( TmsAccount.class, account.getId());
			
				checkInOutRecord  = account.getUser().getUserState().checkOut( date, checkOutByClient );
				
				if ( checkInOutRecord != null ) {
					checkInOutRecord.setCheckOutGeo( checkOutGeo );
					account = em.merge( account );

					if ( logger.isDebugEnabled()) {
						checkInOutRecord = account.getUser().getUserState().getCheckInOutRecord();
						logger.debug( "  CheckOut data stored in DB:  "
							+ "Date In='"+DateUtil.dateAndTimeToString( checkInOutRecord.getDateCheckedIn()) + "', "
							+ "Project='" + checkInOutRecord.getProject().getName() + "', "
							+ "Checked-In in =" + checkInOutRecord.getCheckInGeo()
							+ "Date Out='"+DateUtil.dateAndTimeToString( checkInOutRecord.getDateCheckedOut()) + "', "
							+ "Checked-Out =" + checkInOutRecord.getCheckOutGeo()
							);
					}
					
					retAccount = account;
				}

				em.flush();
				em.getTransaction().commit();
			} catch (RollbackException e) {
				throw e;
			} catch ( Exception e ) {
				logger.error( "Cannot set user session state as 'Checked-Out'" );
				logger.error( e );
			} finally {
				em.close();
			}
			
		} else {
			logger.info( account.getUser() + " Checked-Out already!" );
			throw new UserDisabledException( "" );
		}

		return retAccount;
	}

	private Project getProject( TmsAccount account, String projectCode ) {
		Project project = null;

		// projectCode == null if project info exists already in UserInfo (when Check-Out)
		if ( projectCode == null &&
			 account != null && account.getUser() != null && 
			 account.getUser().getUserState() != null &&
			 account.getUser().getUserState().getProject() != null 
		   ) {
			project = account.getUser().getUserState().getProject(); 
			projectCode = project.getCode(); 
		}
		
		// Project still not found
		if ( project == null ) {
			// Find Organisation the user belong to
			Organisation org = null;
			if ( account != null && account.getUser() != null ) { 
				org = account.getUser().getOrganisation();
				if ( org != null )
					if ( logger.isDebugEnabled()) logger.debug( "Organisation was found for " + account );
			} else {
				if ( logger.isDebugEnabled()) logger.debug( "Organisation is not assigned for " + account );
			}

			if ( org != null ) {
				// If Org found than find the Project in it
				project = org.getProject( projectCode ); 
			} else {
				Collection<Organisation> cl = OrganisationFacade.getInstance().getOrganisations();
				if ( cl.size() == 1 ) {
					org = cl.iterator().next();
				}
				if ( org != null ) {
					project = org.getProject( projectCode ); 
				}
			}
		}
		
		return project;
	}

//	public List< Project > getLatestCheckIns( TmsUser user, int days ) {
//		return getLatestCheckIns( user, DateUtil.getDate(), days );
//	}

	
	public List<Project> getLatestCheckIns( TmsUser user, Date date, int days ) {
		
		List< Project > retList = new ArrayList< Project >();
	
//		Date endDate = DateUtil.addDays( DateUtil.getDate(), -1 );
		Date endDate = date;
		Date startDate = DateUtil.addDays( endDate, - days );
		
		List<CheckInOutRecord> recordsList = getList( user, startDate, endDate );
		
		if ( recordsList == null || recordsList.size() == 0 ) {
			
			if ( days < 3 ) days = 3;
			
			startDate = DateUtil.addDays( endDate, -days );
			
			recordsList = getList( user, startDate, endDate );
		}

		if ( recordsList != null && recordsList.size() > 0 ) {
			
			for ( CheckInOutRecord record : recordsList ) {
				
				retList.add( record.getProject());
				
			}
			
		}
	
		return retList;
	}
	
	
}
