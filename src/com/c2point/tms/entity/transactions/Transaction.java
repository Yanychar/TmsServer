package com.c2point.tms.entity.transactions;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.SimplePojo;
import com.c2point.tms.util.DateUtil;


@Entity
@Table(name="tmstransactions")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE", discriminatorType=DiscriminatorType.STRING,length=20)
@DiscriminatorValue("unknown")
//@Customizer( TransactionCustomizer.class )

public class Transaction extends SimplePojo {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Transaction.class.getName()); 

	/**
	 * Date and time of transaction
	 */
	@Temporal(TemporalType.TIMESTAMP)
	protected Date date;
	/**
	 * Type of transaction
	 */
	/**
	 * TmsUser initiated transaction
	 */
	protected TmsUser tmsUser;
	/**
	 * Transaction data (optional) in XML format
	 */
	@Column( length=2048 )
	protected String transactionData;

	
	public Transaction( TmsUser user, Date date, String transactionData ) {
		super();
		this.date = ( date != null ) ? date : DateUtil.getDate();
		this.tmsUser = user;
		this.transactionData = transactionData;
	}
	public Transaction( TmsUser user, Date date ) {
		this( user, date, null );
	}
/*
	public Transaction( TmsAccount account, Date date ) {
		this(
			  ( account != null ) ? account.getUser() : null,
			  date,
			  null
			);
	}
*/	
	public Transaction() {
		this( null, null, null );
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the tmsUser
	 */
	public TmsUser getPerson() {
		return tmsUser;
	}
	/**
	 * @param tmsUser the tmsUser to set
	 */
	public void setPerson( TmsUser tmsUser ) {
		this.tmsUser = tmsUser;
	}
	/**
	 * @return the transactionData
	 */
	public String getTransactionData() {
		return transactionData;
	}
	/**
	 * @param transactionData the transactionData to set
	 */
	public void setTransactionData(String transactionData) {
		this.transactionData = transactionData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return  this.getClass().getSimpleName() + "[ "
				+ "date=" + (date != null ? date : "NULL") + ", "
				+ "tmsUser='" + ( tmsUser != null ? tmsUser.getFirstAndLastNames() + "'" : "null") + ", "
				+ "transactionData=" + (transactionData != null ? transactionData : "null") + "]";
	}
	
	public String toStringShort() {
		return  this.getClass().getSimpleName() + "[ "
				+ "date=" + (date != null ? date : "NULL") + ", "
				+ "tmsUser='" + ( tmsUser != null ? tmsUser.getFirstAndLastNames() + "'" : "null") + " ]";
	}
	
}
