package com.c2point.tms.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;

@MappedSuperclass
public abstract class AbstractReport extends SimplePojo {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( AbstractReport.class.getName());

    protected String	uniqueReportId;

	@Temporal(TemporalType.DATE)
	protected Date		date;
	
	protected TmsUser		user;
	protected Organisation	org;

    @Column(name="intapprovalType")
	private ApprovalFlagType	approvalFlagType;
	
	
	/**
	 * 
	 */
	public AbstractReport() {
		super();
		setUniqueReportId();
		setApprovalFlagType( ApprovalFlagType.TO_CHECK );
		setDate( DateUtil.getDate());
	}

	public AbstractReport initReport( String uniqueReportId, Date date, TmsUser user ) {
		setUniqueReportId( uniqueReportId );
		setDate( date );
		setUser( user );

		return this;
	}

//	public AbstractReport initReport( Date date, TmsUser user ) {
	
	public String getUniqueReportId() {
		return uniqueReportId;
	}
	public void setUniqueReportId( String uniqueReportId ) {
		if ( uniqueReportId != null && uniqueReportId.length() > 0 ) {
			this.uniqueReportId = uniqueReportId;
		}
	}
	
	public String setUniqueReportId() {
		setUniqueReportId( UUID.randomUUID().toString());
		
		return uniqueReportId;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public TmsUser getUser() {
		return user;
	}

	public void setUser(TmsUser user) {
		this.user = user;
		try {
			setOrg( user.getOrganisation());
		} catch ( NullPointerException e ) {
//			logger.error( "User without linkage to the Organisation was assigned to Report" );
		}
	}


	public ApprovalFlagType getApprovalFlagType() {
		return this.approvalFlagType;
	}

	public void setApprovalFlagType( ApprovalFlagType approvalFlagType ) {
		this.approvalFlagType = approvalFlagType;
	}

	public Organisation getOrg() {
		return org;
	}

	protected void setOrg(Organisation org) {
		this.org = org;
	}

	public abstract Project getProject();
	
}
