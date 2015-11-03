package com.c2point.tms.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@NamedQueries({
	@NamedQuery(name = "findAccountByUsrName", query = 
			"SELECT account FROM TmsAccount account " +
				"WHERE account.usrName = :usrName AND account.deleted = false ORDER BY account.usrName ASC"),
	@NamedQuery(name = "findAccountBySessionId", query = 
			"SELECT account FROM TmsAccount account " +
				"WHERE account.tmsUser.userState.uniqueSessionID = :sessionId AND account.deleted = false"),
	@NamedQuery(name = "findAccountByUsrId", query = 
			"SELECT account FROM TmsAccount account " +
				"WHERE account.tmsUser.id = :userId"),
})
public class TmsAccount extends SimplePojo {
	private static Logger logger = LogManager.getLogger( TmsAccount.class.getName()); 

	private static final int 		PSW_LENGTH = 10;
	private static final char [] 	CHAR_TO_DELETE = { '0', 'o', 'O', 'l', 'I', '1' };
	
	private String 	usrName;
	private String 	pwd;
	
	@Transient
	private boolean pwdMustBeChanged;
	
	@OneToOne(optional=false, fetch=FetchType.LAZY)
	private TmsUser	tmsUser;

	public TmsAccount( String usrName, String pwd, TmsUser tmsUser ) {
		setUsrName( usrName );
		setPwd( pwd );
		setUser( tmsUser );
		setPwdMustBeChanged( true );
	}
	public TmsAccount() {
		this( "", "", null );
	}

	public String getUsrName() {
		return usrName;
	}
	public void setUsrName( String usrName ) {
		this.usrName = ( usrName != null ? usrName : "" );
	}
	
	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}
	/**
	 * @param pwd the pwd to set
	 */
	public void setPwd( String pwd ) {
		this.pwd = ( pwd != null ? pwd : "" );
	}

	
	public TmsUser getUser() {
		return tmsUser;
	}
	public void setUser(TmsUser tmsUser) {
		this.tmsUser = tmsUser;
	}
	
	/**
	 * @return the uniqueSessionID
	 */
	public String getUniqueSessionID() {
		if ( this.tmsUser != null && tmsUser.getUserState() != null ) {
			return tmsUser.getUserState().getSessionId();
		}
		return null;
	}
	/**
	 * @param uniqueSessionID the uniqueSessionID to set
	 */
	public String setUniqueSessionID() {
		if ( this.tmsUser != null && tmsUser.getUserState() != null ) {
			return tmsUser.getUserState().setNewSession();
		}
		return null;
	}
	public boolean isPwdMustBeChanged() {
		return pwdMustBeChanged;
	}
	public void setPwdMustBeChanged(boolean pwdMustBeChanged) {
		this.pwdMustBeChanged = pwdMustBeChanged;
	}
	public String closeSession() {
		if ( this.tmsUser != null && tmsUser.getUserState() != null ) {
			String res = tmsUser.getUserState().closeSession();
			if ( res == null ) res = "";
			return res;
		}
		return null;
	}

	public static String generateNewPassword() {
		
		String password = "";
		boolean generate = true;
		
		while( generate ) {
			password = RandomStringUtils.randomAlphanumeric( PSW_LENGTH * 2 );
			
			// what shall be removed: 0oOlI1
			if ( StringUtils.containsAny( password, CHAR_TO_DELETE )) {
				// Delete chars
				for ( char c : CHAR_TO_DELETE ) {
					password = StringUtils.remove( password, c );
				}
			}
			
			// Check that length is required. Cut or select next passord
			if ( password.length() >= PSW_LENGTH ) {
				password = StringUtils.left( password, PSW_LENGTH );
				
				generate = false;
			}

			logger.debug( "Generated password: '" + password + "'" );
			
		}
		
		
		
		return password;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tms Account [usrName=" + usrName + ", pwd=??? ]";
	}
	/**
	 * @return the address
	 */
}
