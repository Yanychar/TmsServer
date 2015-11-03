package com.c2point.tms.datalayer;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.RollbackException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.stubs.travelreport.TravelReportStub;
import com.c2point.tms.entity.transactions.AddTravelReportTransaction;
import com.c2point.tms.entity.transactions.DeleteTravelReportTransaction;
import com.c2point.tms.entity.transactions.EditTravelReportTransaction;
import com.c2point.tms.entity.transactions.Transaction;
import com.c2point.tms.tools.RecordValidationIF;
import com.c2point.tms.tools.ReportStorageIf;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.exception.InvalidParametersException;
import com.c2point.tms.util.exception.ProjectNotFoundException;
import com.c2point.tms.util.exception.TravelNotFoundException;
import com.c2point.tms.util.exception.UserNotFoundException;

public class TravelReportFacade {
	private static Logger logger = LogManager.getLogger( TravelReportFacade.class.getName());

	public static TravelReportFacade getInstance() {
		return new TravelReportFacade();
	}

/// Travel Reporting methods	
	
	public boolean traverseTravelReports( Organisation org, RecordValidationIF processor ) {
		return traverseTravelReports( org, null, null, processor );
	}
	
	public boolean traverseTravelReports( Organisation org, Date startDate, Date endDate, RecordValidationIF processor ) {
		boolean bRes = true;

		if ( logger.isDebugEnabled()) 
			logger.debug( "Start to traverse Travel Reports for Organisation: '" + org.getName() + "'" );
		
		// Check period dates
		if ( startDate == null ) {
			startDate = new Date( 0 );
		}

		if ( endDate == null ) {
			endDate = DateUtil.getDate(); 
		}
		
		boolean toExit = false;
		// Setup number of records to read
		int max_records_to_read = 500;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		processor.setEntityManager( em );
		
		TypedQuery<TravelReport> query = null;

		try {
			query = em.createNamedQuery( "findAllTravelsByOrg&Period", TravelReport.class )
					.setParameter( "org", org )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endDate, TemporalType.DATE );
		} catch ( Exception e ) {
			logger.error( "Cannot create query for Travel Reports" );
			logger.error( e );
			em.close();
			return false;
		}
			
		query.setFirstResult( 0 );
		query.setMaxResults( max_records_to_read );
		List<TravelReport> reports = null;

		while ( !toExit ) {
			  //
			if ( logger.isDebugEnabled()) 
				logger.debug( "  Will read " + max_records_to_read + " records starting from " + query.getFirstResult());

			try {
				reports = query.getResultList();
				
				if ( logger.isDebugEnabled()) logger.debug( "  #" + reports.size() + " records were read.");
				
				em.getTransaction().begin();
				
				// Traverse through all read records
				for ( TravelReport report : reports ) {
					// Process the record
					if ( processor.preProcessRecord( report )) {
						if ( processor.processRecord( report )) {
							processor.postProcessRecord( report );
							
						}
					} else {
						// This is not error situation. Just skip the record
					}
				}
				
				em.flush();
				em.getTransaction().commit();
				
			} catch( OutOfMemoryError e ) { 
				max_records_to_read = max_records_to_read / 2;
				query.setMaxResults( max_records_to_read );
				if ( max_records_to_read < 10 ) {
					logger.error( "Not possible to read Reports. OutOf Memory \n" + e );
					bRes = false;
					break;
				} else {
					continue;
				}
			} catch (RollbackException e) {
				em.getTransaction().rollback();
				logger.error( "Cannot commit changes. \n" + e );
				bRes = false;
			} catch ( Exception e ) {
				logger.error( "Unpredicted exception\n" + e );
				bRes = false;
			}
			
			toExit = ( reports == null || reports.size() <= 0 );
			query.setFirstResult( query.getFirstResult() + max_records_to_read );
		}

		em.close();
		
		if ( logger.isDebugEnabled()) logger.debug( "... end of Travel traversing" );
		
		return bRes;
	}
		
	public TravelReport getTravelReport( String reportId ) {
		
		TravelReport report = findTravelReport( reportId );
		
		return report;
	}
	
	
	
	
	public List<TravelReport> getUserTravelReports( TmsUser user, Date date, String projectCode ) {
		List<TravelReport> resArray = null;

		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			TypedQuery<TravelReport> q;
			// Fetched Account with specify UserName. Should be one account only!!!
			if ( projectCode != null && projectCode.length() > 0 ) {
				q = em.createNamedQuery( "findTravelsByDate&Person&Project", TravelReport.class )
						.setParameter( "date", date, TemporalType.DATE )
						.setParameter( "user", user )
						.setParameter( "projectCode", projectCode );
			} else {
				q = em.createNamedQuery( "findTravelsByDate&Person", TravelReport.class )
						.setParameter( "date", date, TemporalType.DATE )
						.setParameter( "user", user);
			}
			
			resArray = q.getResultList();
			
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched TravelReports: " + resArray.size());
		} catch ( NoResultException e ) {
			resArray = null;
			if ( logger.isDebugEnabled()) logger.debug( "TravelReport Not Found for " + user );
		} catch ( Exception e ) {
			resArray = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return resArray;
	}
	
	public boolean getUserTravelReports( TmsUser user, Date startDate, Date endDate, RecordValidationIF filterProcessor, ReportStorageIf storage ) {   

		boolean bRes = false;

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to getUserTravelReports( RecordValidationIF ) ..." );
		}
		
		if ( startDate == null ) {
			startDate = new Date( 0 ); // Smallest Date available
		}
		if ( endDate == null ) {
			endDate = new Date( DateUtil.getDate().getTime() + 1000*60*60*2 ); // +2 hours
		}

		EntityManager em = DataFacade.getInstance().createEntityManager();

		if ( filterProcessor != null ) {
			filterProcessor.setEntityManager( em );
		}
		
		TypedQuery<TravelReport> query = null;
		try {
			query = em.createNamedQuery( "findTravelByPersonAndPeriod", TravelReport.class )
					.setParameter( "user", user )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endDate, TemporalType.DATE );
			
			bRes = filterReports( query, filterProcessor, storage );
			
		} catch ( NoResultException e ) {
			bRes = false;
			if ( logger.isDebugEnabled()) logger.debug( "TravelReports were Not Found for user: " + user );
		} catch ( Exception e ) {
			bRes = false;
			logger.error( "Cannot create query for Travel Reports" );
			logger.error( e );
		} finally {
			em.close();
		}

		return bRes;
	}
	
	public boolean getManagerTravelReports( TmsUser mngr, Date startDate, Date endDate, RecordValidationIF filterProcessor, ReportStorageIf storage ) { 

		boolean bRes = false;
		
		if ( logger.isDebugEnabled()) logger.debug( "Start to getManagerTravelReports( RecordValidationIF ) ..." );
		
		if ( startDate == null ) {
			startDate = new Date( 0 ); // Smallest Date available
		}
		if ( endDate == null ) {
			endDate = new Date( DateUtil.getDate().getTime() + 1000*60*60*2 ); // +2 hours
		}
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		if ( filterProcessor != null ) {
			filterProcessor.setEntityManager( em );
		}
		
		TypedQuery<TravelReport> query = null;

		try {
			query = em.createNamedQuery( "findTravelByManager&Period", TravelReport.class )
					.setParameter( "mngr", mngr )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endDate, TemporalType.DATE );

			bRes = filterReports( query, filterProcessor, storage );
			
		} catch ( NoResultException e ) {
			bRes = false;
			if ( logger.isDebugEnabled()) logger.debug( "TravelReports were Not Found for manager: " + mngr );
		} catch ( Exception e ) {
			bRes = false;
			logger.error( "Cannot create query for Travel Reports" );
			logger.error( e );
		} finally {
			em.close();
		}

		return bRes;
	}

	public boolean getCompanyTravelReports( Organisation org, Date startDate, Date endDate, RecordValidationIF filterProcessor, ReportStorageIf storage ) { 

		boolean bRes = false;
		
		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to getTravelReports( RecordValidationIF ) ..." );
		}

		if ( startDate == null ) {
			startDate = new Date( 0 ); // Smallest Date available
		}
		if ( endDate == null ) {
			endDate = new Date( DateUtil.getDate().getTime() + 1000*60*60*2 ); // +2 hours
		}
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		if ( filterProcessor != null ) {
			filterProcessor.setEntityManager( em );
		}
		
		TypedQuery<TravelReport> query = null;

		try {
			query = em.createNamedQuery( "findAllTravelsByOrg&Period", TravelReport.class )
					.setParameter( "org", org )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endDate, TemporalType.DATE );

			bRes = filterReports( query, filterProcessor, storage );
			
		} catch ( NoResultException e ) {
			bRes = false;
			if ( logger.isDebugEnabled()) logger.debug( "TravelReports were Not Found for Organisation: " + org );
		} catch ( Exception e ) {
			bRes = false;
			logger.error( "Cannot create query for Travel Reports" );
			logger.error( e );
		} finally {
			em.close();
		}

		return bRes;
	}

	public TravelReport saveTravelReport( TravelReport nuReport ) {

		TravelReport resReport = null;
		
		if ( nuReport == null ) {
			logger.error( "TravelReport == null. Nothing to save!" );
			return null;
		}
		
		
		if ( logger.isDebugEnabled()) {
			logger.debug( "*****  SaveTravelReport( Report ) starting ... " );
			logger.debug( nuReport );
		}
		
		
		if ( logger.isDebugEnabled()) logger.debug( "  Search Travel Report using uniqueId" );
		TravelReport oldReport = findTravelReport( nuReport.getUniqueReportId());
		
		if ( oldReport != null ) { 

			// TravelReport exists. Report update is necessary
			
			if ( logger.isDebugEnabled()) logger.debug( "  TravelReport with code=" + nuReport.getUniqueReportId() + " was found!" );
			
			if ( nuReport.getDistance() != 0 ) {

				if ( logger.isDebugEnabled()) logger.debug( "  Distance != 0. Report must be edited" );
				// ... otherwise should be edited
				oldReport.modifyReport( nuReport );
				// merge old report
				resReport = DataFacade.getInstance().merge( oldReport );
				if ( logger.isDebugEnabled()) logger.debug( "  Report has been modified!" );
				if ( resReport != null ) {
					// Write AddReport transaction
					try {
						writeTransaction( new EditTravelReportTransaction( resReport ));
					} catch ( JAXBException e ) {
						logger.error( "Cannot create and write 'EditTravelReport' transaction for report with uniqueId = " + resReport.getUniqueReportId());
					}
				}
			} else {

				if ( logger.isDebugEnabled()) logger.debug( "  Distance == 0. Travel Report must be deleted" );

				DataFacade.getInstance().remove( oldReport );
				resReport = oldReport;
				
				if ( logger.isDebugEnabled()) logger.debug( "  Report has been deleted!" );
				// Write AddReport transaction
				try {
					writeTransaction( new DeleteTravelReportTransaction( oldReport ));
				} catch ( JAXBException e ) {
					logger.error( "Cannot create and write 'DeleteTravelReport' transaction for report with uniqueId = " 
									+ ( oldReport != null ? oldReport.getUniqueReportId() : "" ));
				}
			}
		} else {

			// Report is new. Necessary to add it if Distance != null
			
			if ( nuReport.getDistance() == 0 ) {
				logger.debug( "New report does not include Distance. Will be skipped!" );
				return nuReport;
			}
				
			if ( logger.isDebugEnabled())
				logger.debug( "TravelReport with for user " + nuReport.getUser().getFirstAndLastNames() 
						     + " and Date " + nuReport.getDate() + " will be added" );
			
			// insert new report
			resReport = DataFacade.getInstance().insert( nuReport );
			
			if ( resReport != null ) {
				// Write AddReport transaction
				try {
					writeTransaction( new AddTravelReportTransaction( resReport ));
				} catch ( JAXBException e ) {
					logger.error( "Cannot create and write 'AddTravelReport' transaction for report with uniqueId = " + resReport.getUniqueReportId());
				}
			}
		}

	
		return resReport;
	}

	public TravelReport convertStubToReport( TmsAccount account, String projectCode, /*String dateStr, */TravelReportStub stub )
			throws UserNotFoundException, InvalidParametersException, TravelNotFoundException, ParseException, ProjectNotFoundException {
		
		TravelReport resReport = null;
		
		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to convert ... " );
		}
		
		
		resReport = new TravelReport();
			
		// fill new report
		Date date = null;
		TmsUser user = null;
		Project project = null;
		Date startDate = null;
		Date endDate = null;

		// Determine user. Account has been determined earlier from REST request from client
		try {
			user = account.getUser();
			if ( user == null ) {
				throw new UserNotFoundException( "Cannot set user because account.person == null!" );
			}
		} catch ( Exception e ) {
			if ( logger.isDebugEnabled()) logger.debug( "Cannot set user because account or account.person == null: " + account );
			throw new UserNotFoundException( "Cannot set user because account == null!" );
		}
			
		// Determine the  Project from projectCode
		try {
			project = user.getOrganisation().getProject( projectCode );
			if ( project == null ) {
				// Try to find project from userstate
				project = user.getUserState().getProject();
				if ( project == null ) {
					logger.error( "Cannot find project specified in request. Project Code = " + projectCode );
					throw new ProjectNotFoundException( "Cannot find project in the user Organisation to set up TravelReport!" );
				}
			}
		} catch ( Exception e ) {
			logger.error( "Cannot find project specified in rquest. Project id = " + projectCode );
			throw new ProjectNotFoundException( "Cannot set up project to the TravelReport!" );
		}

		// Convert Travel Start DateTime
		try {
			startDate = DateUtil.stringToDateAndTime( stub.getStartDate());
		} catch ( ParseException e) {
			startDate = null;
			if ( logger.isDebugEnabled()) logger.debug( "Wrong start date passed: '" + startDate + "'. Empty date will be used" );
		}

		// Convert Travel End DateTime
		try {
			endDate = DateUtil.stringToDateAndTime( stub.getEndDate());
		} catch ( ParseException e) {
			endDate = null;
			if ( logger.isDebugEnabled()) logger.debug( "Wrong start date passed: '" + endDate + "'. Empty date will be used" );
		}

		// Determine Report Date
		//   1. Try to use startDate as Report date
		if ( startDate != null ) {
			
			Calendar cal = Calendar.getInstance();
			cal.setTime( startDate );
			cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
			date = cal.getTime();

		} else {
		//   2. If no startDate defined than use dateStr
			//   Otherwise is an error!
			if ( logger.isDebugEnabled()) logger.debug( "Start Date = NULL  ==>>" );
			try {
				if ( logger.isDebugEnabled()) logger.debug( "    Report Date will be taken from TravelReport stub: " + stub.getDate());
				date = DateUtil.stringNoDelimToDate( stub.getDate());
			} catch ( Exception e) {
				if ( logger.isDebugEnabled()) logger.debug( "       No success. Report Date will be set to today: " + new Date());
//					throw new InvalidParametersException( "Cannot get correct parameter for Date/DateStart from stub!" );

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
		        cal.set(Calendar.MINUTE, 0);
		        cal.set(Calendar.SECOND, 0);
		        cal.set(Calendar.MILLISECOND, 0);
				date = cal.getTime();
			}

		}
			
		// Now initialize new Report
		try {
			resReport.initReport( stub.getUniqueReportId(), date, user, project, stub.getTravelType(), startDate, endDate, stub.getDistance(), stub.getRoute());
		
		} catch ( Exception e ) {
			logger.error( "Cannot get parameters from stub!" );
			resReport = null;
			throw new InvalidParametersException( "Cannot get parameters from stub!" );
		}
			
		return resReport;
	}

	public List<TravelReport> getTravelReportsForReporting( Organisation org, Date startDate, Date endDate ) { 

		ApprovalFlagType [] flags = new ApprovalFlagType[2];
		flags[ 0 ] = ApprovalFlagType.APPROVED;
		flags[ 1 ] = ApprovalFlagType.PROCESSED;

		return getAllTravelReports( org, null, null, startDate, endDate, flags ); 
	
	}
	
	public List<TravelReport> getTravelReportsForReporting( TmsUser mngr, Date startDate, Date endDate ) { 

		ApprovalFlagType [] flags = new ApprovalFlagType[2];
		flags[ 0 ] = ApprovalFlagType.APPROVED;
		flags[ 1 ] = ApprovalFlagType.PROCESSED;

		return getAllTravelReports( null, mngr, null, startDate, endDate, flags ); 
	
	}
	
	public List<TravelReport> getAllTravelReports( Organisation org, TmsUser mngr, TmsUser user, Date startDate, Date endDate, ApprovalFlagType [] flags ) { 
		List<TravelReport> resArray = null;

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to getManagerAllValidTravelReports( mngr, startDate, endDate, APPROVED or PROCESSED ) ..." );
		}

		if ( startDate == null ) {
			logger.error( "Start date shall be defined!" );
			return null;
		}
		if ( endDate == null ) {
			logger.error( "End date shall be defined!" );
			return null;
		}
		if ( org == null && mngr == null && user == null ) {
			logger.error( "Organisation, manager or user shall be specified!" );
			return null;
		} else if ( org == null ) {
			// Just additional prevention to fetch users from wrong organisation
			org = ( mngr != null ) ? mngr.getOrganisation() : user.getOrganisation(); 
		}
		
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<TravelReport> query = buildQuery( em, org, mngr, user, startDate, endDate, flags ); 
			
			resArray = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched TravelReports: " + resArray.size());
		} catch ( NoResultException e ) {
			resArray = null;
			if ( logger.isDebugEnabled()) logger.debug( "No TravelReport Found" );
		} catch ( Exception e ) {
			resArray = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return resArray;
	}
	
	// Private  methods	
	private boolean filterReports( TypedQuery<TravelReport> query, RecordValidationIF filterProcessor, ReportStorageIf storage ) {
		
		boolean bRes = false;
		
		int max_records_to_read = 1000;
		boolean toExit = false;
		
		if ( query != null ) {

			query.setFirstResult( 0 );
			query.setMaxResults( max_records_to_read );
			List<TravelReport> reports = null;

			while ( !toExit ) {
				  //
				if ( logger.isDebugEnabled()) 
					logger.debug( "  Will read " + max_records_to_read + " records starting from " + query.getFirstResult());

				try {
					reports = query.getResultList();
					
					if ( logger.isDebugEnabled()) logger.debug( "  #" + reports.size() + " records were read.");
					
					// Traverse through all read records
					for ( TravelReport report : reports ) {
						// Process the record
						if ( filterProcessor != null ) {
							if ( filterProcessor.preProcessRecord( report )) {
								if ( filterProcessor.processRecord( report )) {
									bRes = storage.addReport( report ) || bRes; 
									filterProcessor.postProcessRecord( report );
									
								}
							} else {
								// This is not error situation. Just skip the record
							}
						} else {
							bRes = storage.addReport( report ) || bRes;
						}
					}
					
				} catch( OutOfMemoryError e ) { 
					max_records_to_read = max_records_to_read / 2;
					query.setMaxResults( max_records_to_read );
					if ( max_records_to_read < 10 ) {
						logger.error( "Not possible to read Reports. OutOf Memory \n" + e );
						bRes = false;
						break;
					} else {
						continue;
					}
				} catch ( Exception e ) {
					logger.error( "Unpredicted exception\n" + e );
					bRes = false;
				}
				
				toExit = ( reports == null || reports.size() <= 0 );
				query.setFirstResult( query.getFirstResult() + max_records_to_read );
			}
			
		}
		
		return bRes;
	}
	
	private TravelReport findTravelReport( String reportCode ) {
		TravelReport report = null;
		
		if ( reportCode == null ||  reportCode != null &&  reportCode.length() == 0 ) {
			if ( logger.isDebugEnabled())
				logger.debug( "TravelReport Not Found for reportCode: '" + reportCode + "'" );
			return null;
		}
				
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<TravelReport> q = em.createNamedQuery( "findTravelReportByCode", TravelReport.class )
					.setParameter("reportId", reportCode );
			report = q.getSingleResult();
		} catch ( NoResultException e ) {
			report = null;
			if ( logger.isDebugEnabled())
				logger.debug( "TravelReport Not Found for reportCode: '" + reportCode + "'" );
		} catch ( NonUniqueResultException e ) {
			report = null;
			logger.error( "It should be one TravelReport only for reportCode: '" + reportCode + "'" );
		} catch ( Exception e ) {
			report = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return report;
	}

	private void writeTransaction( Transaction tr ) {
		tr = DataFacade.getInstance().insert( tr );

		if ( logger.isDebugEnabled()) logger.debug( tr );
	}


	private TypedQuery<TravelReport> buildQuery( EntityManager em,
									Organisation org, 
									TmsUser mngr, 
									TmsUser user, 
									Date startDate, Date endDate, 
									ApprovalFlagType [] flags 
			) {
	
		TypedQuery<TravelReport> query = null;
		Date sDate;
		Date eDate;
		
		StringBuilder queryBuilder = new StringBuilder( "SELECT report FROM TravelReport report" );
		clearPrefixFlag();
		
		// Create query string
		if ( org!= null ) {
			queryBuilder.append( getPrefix());
			queryBuilder.append( "report.org = :org" );
		}
		if ( mngr!= null ) {
			queryBuilder.append( getPrefix());
			queryBuilder.append( "report.project.projectManager = :mngr" );
		}
		if ( user!= null ) {
			queryBuilder.append( getPrefix());
			queryBuilder.append( "report.user = :user" );
		}
		
		sDate = ( startDate != null ) ? startDate : new Date( 0 ); // Smallest Date available
		eDate = ( endDate != null ) ? endDate : new Date( DateUtil.getDate().getTime());
		queryBuilder.append( getPrefix());
		queryBuilder.append( "report.date BETWEEN :startDate and :endDate" );
		
		/*		
		flags = new ApprovalFlagType[2];
		flags[ 0 ] = ApprovalFlagType.TO_CHECK;
		flags[ 1 ] = ApprovalFlagType.APPROVED;
		*/		
		String tmpStr = "( ";
		if ( flags != null && flags.length > 0 ) {
		
			boolean isFirst = true;
			for ( ApprovalFlagType flag:  flags ) {
				if ( isFirst ) {
					isFirst = false;
				} else {
					tmpStr = tmpStr.concat( orQueryToken );
				}
				tmpStr = tmpStr.concat( "report.approvalFlagType = " + 
										 ApprovalFlagType.class.getCanonicalName() + "." + flag );
			}
			tmpStr = tmpStr.concat( " )" );
			logger.debug( "Flags str = '" + tmpStr + "'" );
			
			queryBuilder.append( getPrefix());
			queryBuilder.append( tmpStr );
		
		}
		
		// Create query itself
		query = em.createQuery( queryBuilder.toString(), TravelReport.class );
		
		// Put parameter values into the query before query execution
		if ( org!= null ) {
			query.setParameter( "org", org );
		}
		if ( mngr!= null ) {
			query.setParameter( "mngr", mngr );
		}
		if ( user!= null ) {
			query.setParameter( "user", user );
		}
		query.setParameter( "startDate", sDate, TemporalType.DATE );
		query.setParameter( "endDate", eDate, TemporalType.DATE );
		
		return query;
	}
	
	private boolean isFirst = true;
	private static String firstQueryToken = " where ";
	private static String andQueryToken = " and ";
	private static String orQueryToken = " or ";
	
	private void clearPrefixFlag() {
		isFirst = true;
	}
	
	private String getPrefix() {
		if ( isFirst ) {
			isFirst = false;
			return firstQueryToken;
		} 
		
		return andQueryToken;
	}

}
