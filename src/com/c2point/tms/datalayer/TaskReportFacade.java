package com.c2point.tms.datalayer;

import java.text.ParseException;
import java.util.ArrayList;
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
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;

import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.stubs.taskreport.TaskReportStub;
import com.c2point.tms.entity.transactions.AddTaskReportTransaction;
import com.c2point.tms.entity.transactions.DeleteTaskReportTransaction;
import com.c2point.tms.entity.transactions.EditTaskReportTransaction;
import com.c2point.tms.entity.transactions.Transaction;
import com.c2point.tms.tools.RecordValidationIF;
import com.c2point.tms.tools.ReportStorageIf;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.exception.InvalidParametersException;
import com.c2point.tms.util.exception.ProjectNotFoundException;
import com.c2point.tms.util.exception.TaskNotFoundException;
import com.c2point.tms.util.exception.UserNotFoundException;

public class TaskReportFacade {
	private static Logger logger = LogManager.getLogger( TaskReportFacade.class.getName());

	public static TaskReportFacade getInstance() {
		return new TaskReportFacade();
	}

	public boolean traverseTaskReports( Organisation org, RecordValidationIF processor ) {
		return traverseTaskReports( org, null, null, processor );
	}

	public boolean traverseTaskReports( Organisation org, Date startDate, Date endDate, RecordValidationIF processor ) {
		boolean bRes = true;

		if ( logger.isDebugEnabled())
			logger.debug( "Start to traverse Task Reports for Organisation: '" + org.getName() + "'" );

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

		TypedQuery<TaskReport> query = null;

		try {
			query = em.createNamedQuery( "findAllByOrg&Period", TaskReport.class )
					.setParameter( "org", org )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endDate, TemporalType.DATE );
		} catch ( Exception e ) {
			logger.error( "Cannot create query for Task Reports" );
			logger.error( e );
			em.close();
			return false;
		}

		query.setFirstResult( 0 );
		query.setMaxResults( max_records_to_read );
		List<TaskReport> reports = null;

		while ( !toExit ) {
			  //
			if ( logger.isDebugEnabled())
				logger.debug( "  Will read " + max_records_to_read + " records starting from " + query.getFirstResult());

			try {
				reports = query.getResultList();

				if ( logger.isDebugEnabled()) logger.debug( "  #" + reports.size() + " records were read.");

				em.getTransaction().begin();

				// Traverse through all read records
				for ( TaskReport report : reports ) {
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
				em.getTransaction().rollback();
				logger.error( "Unpredicted exception\n" + e );
				bRes = false;
			}

			toExit = ( reports == null || reports.size() <= 0 );
			query.setFirstResult( query.getFirstResult() + max_records_to_read );
		}

		em.close();

		if ( logger.isDebugEnabled()) logger.debug( "... end of Task traversing" );

		return bRes;
	}

	// Task Reporting methods
	public TaskReport getTaskReport( String reportId ) {

		TaskReport report = findTaskReport( reportId );

		return report;
	}

	public List<TaskReport> getUserTaskReports( TmsUser user, Date date, String projectCode ) {
		List<TaskReport> resArray = null;


		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!
			TypedQuery<TaskReport> q = em.createNamedQuery( "findTasksByDate&Person&Project", TaskReport.class )
					.setParameter("date", date, TemporalType.DATE )
					.setParameter("user", user )
					.setParameter("projectCode", projectCode );
			resArray = q.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched TaskReports: " + resArray.size());
		} catch ( NoResultException e ) {
			resArray = null;
			if ( logger.isDebugEnabled()) logger.debug( "TaskReport Not Found for " + user );
		} catch ( Exception e ) {
			resArray = null;
			logger.error( e );
		} finally {
			em.close();
		}

		return resArray;
	}

	public boolean getUserTaskReports( TmsUser user, Date startDate, Date endDate, RecordValidationIF filterProcessor, ReportStorageIf storage ) {

		boolean bRes = false;

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to getUserTaskReports( RecordValidationIF ) ..." );
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

		TypedQuery<TaskReport> query = null;
		try {
			query = em.createNamedQuery( "findTasksByPersonAndPeriod", TaskReport.class )
					.setParameter( "user", user )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endDate, TemporalType.DATE );

			bRes = filterReports( query, filterProcessor, storage );

		} catch ( NoResultException e ) {
			bRes = false;
			if ( logger.isDebugEnabled()) logger.debug( "TaskReports were Not Found for user: " + user );
		} catch ( Exception e ) {
			bRes = false;
			logger.error( "Cannot create query for Task Reports" );
			logger.error( e );
		} finally {
			em.close();
		}

		return bRes;
	}

	public boolean getManagerTaskReports( TmsUser mngr, Date startDate, Date endDate, RecordValidationIF filterProcessor, ReportStorageIf storage ) {

		boolean bRes = false;

		if ( logger.isDebugEnabled()) logger.debug( "Start to getManagerTaskReports( RecordValidationIF ) ..." );

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

		TypedQuery<TaskReport> query = null;

		try {
			query = em.createNamedQuery( "findByManager&Period", TaskReport.class )
					.setParameter( "mngr", mngr )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endDate, TemporalType.DATE );
			
			if ( logger.isDebugEnabled()) {
/*				
				Session session = em.unwrap(JpaEntityManager.class).getActiveSession(); 
				DatabaseQuery databaseQuery = query.getDatabaseQuery();
				query.
				databaseQuery.prepareCall(session, new DatabaseRecord());

				String sqlString = databaseQuery.getSQLString();
*/								
			}

			bRes = filterReports( query, filterProcessor, storage );

		} catch ( NoResultException e ) {
			bRes = false;
			if ( logger.isDebugEnabled()) logger.debug( "TaskReports were Not Found for Manager: " + mngr );
		} catch ( Exception e ) {
			bRes = false;
			logger.error( "Cannot create query for Task Reports" );
			logger.error( e );
		} finally {
			em.close();
		}

		return bRes;
	}


	public List<TaskReport> getTaskReportsForReporting( Organisation org, Date startDate, Date endDate ) {

		ApprovalFlagType [] flags = new ApprovalFlagType[2];
		flags[ 0 ] = ApprovalFlagType.APPROVED;
		flags[ 1 ] = ApprovalFlagType.PROCESSED;

		return getAllTaskReports( org, null, null, startDate, endDate, flags );

	}

	public List<TaskReport> getTaskReportsForReporting( TmsUser mngr, Date startDate, Date endDate ) {

		ApprovalFlagType [] flags = new ApprovalFlagType[2];
		flags[ 0 ] = ApprovalFlagType.APPROVED;
		flags[ 1 ] = ApprovalFlagType.PROCESSED;

		return getAllTaskReports( null, mngr, null, startDate, endDate, flags );

	}

	public List<TaskReport> getAllTaskReports( Organisation org, TmsUser mngr, TmsUser user, Date startDate, Date endDate, ApprovalFlagType [] flags ) {
		List<TaskReport> resArray = null;

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to getManagerAllValidTaskReports( mngr, startDate, endDate, APPROVED or PROCESSED ) ..." );
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
			TypedQuery<TaskReport> query = buildQuery( em, org, mngr, user, startDate, endDate, flags );

			resArray = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched TaskReports: " + resArray.size());
		} catch ( NoResultException e ) {
			resArray = null;
			if ( logger.isDebugEnabled()) logger.debug( "No TaskReport Found" );
		} catch ( Exception e ) {
			resArray = null;
			logger.error( e );
		} finally {
			em.close();
		}

		return resArray;
	}

	public boolean getCompanyTaskReports( Organisation org, Date startDate, Date endDate, RecordValidationIF filterProcessor, ReportStorageIf storage ) {

		boolean bRes = false;

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to getTaskReports( RecordValidationIF ) ..." );
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

		TypedQuery<TaskReport> query = null;

		try {
			query = em.createNamedQuery( "findAllByOrg&Period", TaskReport.class )
					.setParameter( "org", org )
					.setParameter( "startDate", startDate, TemporalType.DATE )
					.setParameter( "endDate", endDate, TemporalType.DATE );

			bRes = filterReports( query, filterProcessor, storage );

		} catch ( NoResultException e ) {
			bRes = false;
			if ( logger.isDebugEnabled()) logger.debug( "TaskReports were Not Found for Organisation: " + org );
		} catch ( Exception e ) {
			bRes = false;
			logger.error( "Cannot create query for Task Reports" );
			logger.error( e );
		} finally {
			em.close();
		}

		return bRes;
	}

	private boolean filterReports( TypedQuery<TaskReport> query, RecordValidationIF filterProcessor, ReportStorageIf storage ) {

		boolean bRes = false;

		int max_records_to_read = 1000;
		boolean toExit = false;

		if ( query != null ) {

			query.setFirstResult( 0 );
			query.setMaxResults( max_records_to_read );
			List<TaskReport> reports = null;

			while ( !toExit ) {
				  //
				if ( logger.isDebugEnabled())
					logger.debug( "  Will read " + max_records_to_read + " records starting from " + query.getFirstResult());

				try {
					reports = query.getResultList();

					if ( logger.isDebugEnabled()) logger.debug( "  #" + reports.size() + " records were read.");

						// Traverse through all read records
						for ( TaskReport report : reports ) {
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

	public List<ProjectTask> getPossibleTasks( TmsAccount account, String projectCode ) {
		List<ProjectTask> resArray = null;

		try {
			resArray = new ArrayList<ProjectTask>( account.getUser().getOrganisation()
													.getProject( projectCode ).getProjectTasks()
													.values());
		} catch ( Exception e ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "*** Cannot fetch list of Tasks assigned to the Project!" );
				logger.debug( "ProjectCode=" + projectCode + "  " + account );
			}
		}

		return resArray;
	}

	// Convert and Save methods
	public TaskReport saveTaskReport( TaskReport nuReport ) {

		TaskReport resReport = null;

		if ( nuReport == null ) {
			logger.error( "TaskReport == null. Nothing to save!" );
			return null;
		}


		if ( logger.isDebugEnabled()) {
			logger.debug( "*****  SaveTaskReport( Report ) starting ... " );
			logger.debug( nuReport );
		}


		if ( logger.isDebugEnabled()) logger.debug( "  Search Task Report using uniqueId" );
		TaskReport oldReport = findTaskReport( nuReport.getUniqueReportId());

		if ( oldReport != null ) {

			// TaskReport exists. Report update is necessary

			if ( logger.isDebugEnabled()) logger.debug( "  TaskReport with code=" + nuReport.getUniqueReportId() + " was found!" );

 			if ( nuReport.getHours() != 0 ) {

				if ( logger.isDebugEnabled()) logger.debug( "  Hours != 0. Report must be edited" );
				// ... otherwise should be edited
				oldReport.modifyReport( nuReport );
				// merge old report
				resReport = DataFacade.getInstance().merge( oldReport );
				if ( logger.isDebugEnabled()) logger.debug( "  Report has been modified!" );
				if ( resReport != null ) {
					// Write AddReport transaction
					try {
						writeTransaction( new EditTaskReportTransaction( resReport ));
					} catch ( JAXBException e ) {
						logger.error( "Cannot create and write 'EditTaskReport' transaction for report with uniqueId = " + resReport.getUniqueReportId());
					}
				}
			} else {

				// If hours == 0 than report must be deleted

				if ( logger.isDebugEnabled()) logger.debug( "  Hours == 0. Task Report must be deleted" );

				DataFacade.getInstance().remove( oldReport );
				resReport = oldReport;

				if ( logger.isDebugEnabled()) logger.debug( "  Report has been deleted!" );
				// Write AddReport transaction
				try {
					writeTransaction( new DeleteTaskReportTransaction( oldReport ));
				} catch ( JAXBException e ) {
					logger.error( "Cannot create and write 'DeleteTaskReport' transaction for report with uniqueId = "
									+ ( oldReport != null ? oldReport.getUniqueReportId() : "" ));
				}
			}
		} else {

			// Report is new. Necessary to add it if Hours != null

			if ( nuReport.getHours() == 0 ) {
				logger.debug( "New report does not include hours. Will be skipped!" );
				return null;
			}

			if ( logger.isDebugEnabled())
				logger.debug( "TaskReport with user " + nuReport.getUser().getFirstAndLastNames()
						     + " and Task " + nuReport.getProjectTask().getTask().getName() + " will be added" );

			// insert new report
			resReport = DataFacade.getInstance().insert( nuReport );

			if ( resReport != null ) {
				// Write AddReport transaction
				try {
					writeTransaction( new AddTaskReportTransaction( resReport ));
				} catch ( JAXBException e ) {
					logger.error( "Cannot create and write 'AddTaskReport' transaction for report with uniqueId = " + resReport.getUniqueReportId());
				}
			}
		}


		return resReport;
	}

	public TaskReport convertStubToReport( TmsAccount account, String projectCode, String dateStr, TaskReportStub stub )
			throws UserNotFoundException, ProjectNotFoundException, InvalidParametersException, TaskNotFoundException {

		TaskReport resReport = null;

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start to convert ... " );
		}


		resReport = new TaskReport();

		// fill new report
		Date date = null;
		TmsUser user = null;
		Project project = null;
		ProjectTask prTask = null;

		// Determine Date
		try {
			date = DateUtil.stringNoDelimToDate( dateStr );
		} catch ( ParseException e) {
			if ( logger.isDebugEnabled()) logger.debug( "Wrong date passed: '" + dateStr + "'" );
			throw new InvalidParametersException( "Wrong date parameter passed" );
		} catch ( Exception e) {
			if ( logger.isDebugEnabled()) logger.debug( "Wrong date passed: '" + dateStr + "'" );
			throw new InvalidParametersException( "Wrong date parameter passed" );
		}

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
				logger.error( "Cannot find project specified in rquest. Project Code = " + projectCode );
				throw new ProjectNotFoundException( "Cannot find project in the user Organisation to set up TaskReport!" );
			}
		} catch ( Exception e ) {
			logger.error( "Cannot find project specified in rquest. Project id = " + projectCode );
			throw new ProjectNotFoundException( "Cannot set up project to the TaskReport!" );
		}

		// Find ProjectTask
		try {
			prTask = project.getProjectTask( stub.getCode());
			if ( prTask == null ) {
				logger.error( "Specified ProjectTask does not exist in the Project to set up TaskReport!" );
				throw new TaskNotFoundException( "Specified ProjectTask does not exist in the Project to set up TaskReport!" );
			}
		} catch ( Exception e ) {
			logger.error( "Specified ProjectTask does not exist in the Project to set up TaskReport!" );
			throw new TaskNotFoundException( "Cannot set up task to the TaskReport!" );
		}


		// Now initialize new Report
		try {
			resReport.initReport( stub.getUniqueReportId(), date, user, prTask, stub.getHours(), stub.getNumValue(), stub.getComment());
		} catch ( Exception e ) {
			logger.error( "Cannot get parameters from stub!\n" + e );
			resReport = null;
			throw new InvalidParametersException( "Cannot get parameters from stub!" );
		}

		return resReport;
	}

	// Private  methods
	private TaskReport findTaskReport( String reportCode ) {
		TaskReport report = null;

		if ( reportCode == null ||  reportCode != null &&  reportCode.length() == 0 ) {
			if ( logger.isDebugEnabled())
				logger.debug( "TaskReport Not Found for reportCode: '" + reportCode + "'" );
			return null;
		}

		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!
			TypedQuery<TaskReport> q = em.createNamedQuery( "findTaskReportByCode", TaskReport.class )
					.setParameter("reportId", reportCode );
			report = q.getSingleResult();
		} catch ( NoResultException e ) {
			report = null;
			if ( logger.isDebugEnabled())
				logger.debug( "TaskReport Not Found for reportCode: '" + reportCode + "'" );
		} catch ( NonUniqueResultException e ) {
			report = null;
			logger.error( "It should be one TaskReport only for reportCode: '" + reportCode + "'" );
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



	private TypedQuery<TaskReport> buildQuery( EntityManager em,
									Organisation org,
									TmsUser mngr,
									TmsUser user,
									Date startDate, Date endDate,
									ApprovalFlagType [] flags
		) {

		TypedQuery<TaskReport> query = null;
		Date sDate;
		Date eDate;

		StringBuilder queryBuilder = new StringBuilder( "SELECT report FROM TaskReport report" );
		clearPrefixFlag();

		// Create query string
		if ( org!= null ) {
			queryBuilder.append( getPrefix());
			queryBuilder.append( "report.org = :org" );
		}
		if ( mngr!= null ) {
			queryBuilder.append( getPrefix());
			queryBuilder.append( "report.projectTask.project.projectManager = :mngr" );
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
		query = em.createQuery( queryBuilder.toString(), TaskReport.class );

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
/*
*/

		//			query = "SELECT report FROM TaskReport report WHERE " +
//					"report.projectTask.project.projectManager = :mngr AND " +
//					"report.date BETWEEN :startDate AND :endDate AND " +
//					"( report.approvalFlagType = ApprovalFlagType.APPROVED OR " +
//					"  report.approvalFlagType = ApprovalFlagType.PROCESSED )"






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




