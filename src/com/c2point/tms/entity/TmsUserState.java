package com.c2point.tms.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;

@Entity
@NamedQueries({
	@NamedQuery(name = "findOldSessions", 
					query = "SELECT state FROM TmsUserState state WHERE "
								+ "state.uniqueSessionID is not null AND LENGTH( state.uniqueSessionID ) > 0 AND "
								+ "state.dateSessionTouched < :dateNow AND "
								+ "state.deleted = false")
})
public class TmsUserState extends SimplePojo {
	private static Logger logger = LogManager.getLogger( TmsUserState.class.getName());

    private String 				uniqueSessionID;
	@Temporal(TemporalType.TIMESTAMP)
	private Date 				dateSessionStarted;
	@Temporal(TemporalType.TIMESTAMP)
	private Date 				dateSessionTouched;
	@Temporal(TemporalType.TIMESTAMP)
	private Date 				dateSessionEnded;
/*
	@Temporal(TemporalType.TIMESTAMP)
	private Date 				dateCheckedIn;			// Will be removed
	@Temporal(TemporalType.TIMESTAMP)
	private Date 				dateCheckedOut;		// Will be removed
*/
	
	private boolean 			checkedIn = false;
//	private Project 			project;
	
	@JoinColumn(name="check_in_out_id")
	@OneToOne( cascade={CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, fetch=FetchType.EAGER )
	private CheckInOutRecord 	checkInOutRecord;
	
	public TmsUserState( ) {
		this.uniqueSessionID = null;
		this.dateSessionStarted = null;
		this.dateSessionTouched = null;
		this.dateSessionEnded = null;
//		this.dateCheckedIn = null;
//		this.dateCheckedOut = null;
		this.checkedIn = false;
//		this.project = null;
		this.checkInOutRecord = null;
	}
	
	public String getUniqueSessionID() {
		return uniqueSessionID;
	}
	protected void setUniqueSessionID(String uniqueSessionID) {
		this.uniqueSessionID = uniqueSessionID;
	}
	
	public Date getDateCheckedIn() {
		if ( checkInOutRecord != null ) {
			return checkInOutRecord.getDateCheckedIn();
		}
		logger.error( "ERROR! Do not have reference to the new CheckInOutRecord object. Return null!" );
//		return dateCheckedIn;
		return null;
	}
	
	public Date getDateCheckedOut() {
		if ( checkInOutRecord != null ) {
			return checkInOutRecord.getDateCheckedOut();
		}
		logger.error( "ERROR! Do not have reference to the new CheckInOutRecord object. Return null!" );
//		return dateCheckedOut;
		return null;
	}
	public Project getProject() {
		if ( checkInOutRecord != null ) {
			return checkInOutRecord.getProject();
		}
		logger.debug( "Project is missing! Project shall be found from Organisation using Project Code" );
		return null;
	}
	
	public boolean isCheckedIn() {
		return checkedIn;
	}
	protected void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}
	public Date getDateSessionStarted() {
		return dateSessionStarted;
	}
	protected void setDateSessionStarted(Date dateSessionStarted) {
		this.dateSessionStarted = dateSessionStarted;
	}
	public Date getDateSessionTouched() {
		return dateSessionTouched;
	}
	protected void setDateSessionTouched(Date dateSessionTouched) {
		this.dateSessionTouched = dateSessionTouched;
	}
	public Date getDateSessionEnded() {
		return dateSessionEnded;
	}
	protected void setDateSessionEnded(Date dateSessionEnded) {
		this.dateSessionEnded = dateSessionEnded;
	}
	

	public CheckInOutRecord getCheckInOutRecord() {
		return checkInOutRecord;
	}

	public void setCheckInOutRecord(CheckInOutRecord checkInOutRecord) {
		this.checkInOutRecord = checkInOutRecord;
	}

	/*
	 * Business methods
	 */
	public String setNewSession() {
		setUniqueSessionID( UUID.randomUUID().toString());
		Date date = DateUtil.getDate();
		setDateSessionStarted( date );
		setDateSessionTouched( date );
		setDateSessionEnded( null );
	
		return uniqueSessionID;
	}
	
	public void touchSession() {
		setDateSessionTouched( DateUtil.getDate());
	}

	public String closeSession() {
		return closeSession( DateUtil.getDate());
	}
	public String closeSession( Date date ) {
		String oldId = uniqueSessionID;
/*
		if ( this.isCheckedIn()) {
			checkOut( date );
		}
*/		
		setUniqueSessionID( null );
		setDateSessionEnded( date );
		
		return oldId;
	}

	
	
	// Just duplicate persistence method getUniqueSessionID() for convenience
	public String getSessionId() {
		return getUniqueSessionID();
	}

	public CheckInOutRecord checkIn( Date date, TmsUser person, Project project ) {
		touchSession();
		
		setCheckInOutRecord( new CheckInOutRecord( date, this, project )); 
				
		setCheckedIn( true );

		return checkInOutRecord;
	}
	
	public CheckInOutRecord checkOut( Date date, boolean checkOutByClient ) {

		touchSession();
		if ( checkInOutRecord != null ) {
			checkInOutRecord.setDateCheckedOut( date );
			checkInOutRecord.setCheckOutByClient( checkOutByClient );
		}
		
		setCheckedIn( false );
		
		return checkInOutRecord ;
	}
	
	
	
	
}
