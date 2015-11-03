package com.c2point.tms.web.reporting.checkinout;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.CheckInOutFacade;
import com.c2point.tms.datalayer.UserFacade;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.TmsUserState;


public abstract class AbstractReport extends AggregateItem {

	private static Logger logger = LogManager.getLogger( AbstractReport.class.getName());
	
	protected SupportedFunctionType 	accessType;
	protected Organisation 			organisation;
	protected TmsUser					user;

	protected Date					startDate;
	protected Date					endDate;

	private Map<Long,TmsUser> userCache = new HashMap<Long,TmsUser>();
	
	
	public AbstractReport( SupportedFunctionType accessType, Organisation organisation, TmsUser user,
			Date startDate, Date endDate ) {

		super();

		setHasChilds();
		
		setAccessType( accessType );
		setOrganisation( organisation );
		setUser( user );
		
		setStartDate( startDate );
		setEndDate( endDate );
	
	}

	public SupportedFunctionType getAccessType() {
		return accessType;
	}

	public void setAccessType(SupportedFunctionType accessType) {
		this.accessType = accessType;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public TmsUser getUser() {
		return user;
	}

	public void setUser(TmsUser user) {
		this.user = user;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public AbstractReport prepareReport() {
		

		try {

			if ( logger.isDebugEnabled()) 
				logger.debug( "Start to reload Task Reports List..." );

			initUserCache();
			
			if ( accessType == SupportedFunctionType.CONSOLIDATE_COMPANY ) {
				
				if ( logger.isDebugEnabled()) logger.debug( "User has rights to see ALL COMPANY reports" );

				List<CheckInOutRecord> 	listCheckInOutRecords = 
						CheckInOutFacade.getInstance().getList( 
								organisation, this.startDate, this.endDate
						);
					
				if ( listCheckInOutRecords != null && listCheckInOutRecords.size() > 0 ) {

					for ( CheckInOutRecord record : listCheckInOutRecords ) {
						
						handleRecord( findCachedUser( record ), record );
					}
					
				}
				
			} else if ( accessType == SupportedFunctionType.CONSOLIDATE_TEAM ) {
			
				if ( logger.isDebugEnabled()) logger.debug( "User has rights to see his/her TEAM reports" );

				List<CheckInOutRecord> 	listCheckInOutRecords = CheckInOutFacade.getInstance().getList(  
												organisation, this.startDate, this.endDate
				);
		
				if ( listCheckInOutRecords != null && listCheckInOutRecords.size() > 0 ) {
				
					TmsUser tmsUser;
					for ( CheckInOutRecord record : listCheckInOutRecords ) {

						tmsUser = findCachedUser( record );
						
						if ( tmsUser != null 
								&& 
							 ( user.isLineManager() && tmsUser.getManager().getId() == user.getId()
								||
							   user.isProjectManager() && record.getProject().getProjectManager().getId() == user.getId()
									 
							 )
								
						) {
							handleRecord( tmsUser, record );
						}
						
					}
				}				

				
				
			} else if ( accessType == SupportedFunctionType.CONSOLIDATE_OWN ) {
				
				if ( logger.isDebugEnabled()) logger.debug( "User has rights to see his/her OWN reports only" );

				List<CheckInOutRecord> 	listCheckInOutRecords = CheckInOutFacade.getInstance().getList(  
						user, this.startDate, this.endDate
				);

				if ( listCheckInOutRecords != null && listCheckInOutRecords.size() > 0 ) {
					for ( CheckInOutRecord record : listCheckInOutRecords ) {
						handleRecord( user, record );
					}
				}
				
			}
				
		} catch ( Exception e ) {
			
			
			logger.error( "Cannot get a lists of Users and/or CheckInOutRecords\n" + e );
			
			return null;
		}
		
		
		return this;
	}

	protected void initUserCache() {
		
		List<TmsUser> listUsers = UserFacade.getInstance().list( organisation );
		
		for ( TmsUser user : listUsers ) {
			if ( user != null && user.getUserState() != null ) {
			
				userCache.put( user.getUserState().getId(), user );
			}
			
		}
	}
	
	
	protected TmsUser findCachedUser( CheckInOutRecord record ) {

		TmsUser tmsUser = null;
		
		if ( record != null ) {
			
			TmsUserState state = record.getUserState();
			
			if ( state != null ) {
				
				if ( userCache.containsKey( state.getId())) {
					tmsUser = userCache.get( state.getId());
				} else {
					
					tmsUser = UserFacade.getInstance().findByState(  organisation, state );
					
					if ( tmsUser != null )
						
						userCache.put( state.getId(), tmsUser );
				}
			}
		
		}
		
		return tmsUser;
	}

	@Override
	protected boolean isValid() {
		
		boolean bRes = false;
		
		if ( getUser() != null &&
			 getOrganisation() != null &&
			 getStartDate() != null &&
			 getEndDate() != null 
		) {
			
			bRes = true;
		
		} else {
			
		}
		
		return bRes;
	}

	@Override
	protected String getToCompare() {
		logger.error( "Wrong call!" );
		return null;
	}

	@Override
	protected String getKey() {
		return null;
	}

	@Override
	protected String getKey( TmsUser user, CheckInOutRecord record ) {
		return null;
	}
	
}
