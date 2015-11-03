package com.c2point.tms.datalayer;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUserState;
import com.c2point.tms.entity.transactions.AuthenticateTransaction;
import com.c2point.tms.entity.transactions.LogoutTransaction;
import com.c2point.tms.entity.transactions.Transaction;
import com.c2point.tms.util.DateUtil;

public class AuthenticationFacade {
	private static Logger logger = LogManager.getLogger( AuthenticationFacade.class.getName()); 

	private static AuthenticationFacade instance = null;

	public static AuthenticationFacade getInstance() {
		if ( instance == null ) {
			instance = new AuthenticationFacade();
		}
		return instance;
	}

	public TmsAccount authenticateTmsUser( String usrName, String pwd ) {
		return authenticateTmsUser( usrName, pwd, null, null );
	}
	
	public TmsAccount authenticateTmsUser( String usrName, String pwd, String version, String imei ) {
		TmsAccount account = null;
		
		// UserName and Pwd shall be not empty
		if ( usrName != null && pwd != null ) {
			
			// Finds account by username
			account = findByUserName( usrName.trim().toLowerCase()); 
			
			if ( account != null && account.getUser() != null ) {
				logger.debug( "Account and User found" );

				// if UserState is not created yet
				if ( account.getUser().getUserState() == null ) {
					logger.debug( "  UserState == null. Will be created" );

					account.getUser().setUserState( new TmsUserState());
					logger.debug( "  Created" );
				
					logger.debug( "  Try to save user with new UserState" );
					TmsUser user = DataFacade.getInstance().merge( account.getUser());
					logger.debug( "  Saved User = :" + user );
					
					logger.debug( "     and UserState: " + ( user != null ? user.getUserState() : "" ));

					account = DataFacade.getInstance().find( TmsAccount.class, account.getId());
				}
				
				if ( account.getPwd().compareTo( pwd ) == 0 ) {
					account.setUniqueSessionID();
					

					DataFacade.getInstance().merge( account.getUser().getUserState());
				} else {
					if ( logger.isDebugEnabled())
						logger.debug( account + " has different pwd than '" + pwd + "'" );
					account = null;
				}
					
			} else {
				if ( logger.isDebugEnabled())
					logger.debug( "No account or User with User Name: '" + usrName + "' found!" );
			}
		} else {
			logger.error( "TmsAccount cannot have NULL usrname or pwd. UsrName=" + usrName + ", pwd=" + pwd );
		}
		
		if ( account != null ) {
			try {
				TmsUser user = account.getUser();
				Date date = ( user != null ? user.getUserState().getDateSessionStarted() : null );
				writeTransaction( 
						new AuthenticateTransaction( user, date, version, imei ));
			} catch ( JAXBException e ) {
				logger.error( "Cannot convert to XML for transaction log (logIn): " + account );
			}
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "'" + usrName + "' not authenticated!" );
		}
		
		return account;
	}

	public boolean logout( TmsAccount account, boolean bAutomatic ) {
		boolean bRes = false;
		
		// Set status logged = OFF
		account.closeSession();
		DataFacade.getInstance().merge( account.getUser().getUserState());
		if ( logger.isDebugEnabled()) logger.debug( "Session for " + account.getUser() + " closed!" );
		
		// Create and store LogOutTransaction
		try {
			writeTransaction( new LogoutTransaction( account.getUser(), DateUtil.getDate(), bAutomatic ));
		} catch ( JAXBException e ) {
			logger.error( "Cannot convert to XML for transaction log (logOut): " + account );
		}
		
		bRes = true;
		
		return bRes;
	}
	
	public TmsAccount findBySessionId( String sessionId ) {
		TmsAccount account;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<TmsAccount> q = em.createNamedQuery( "findAccountBySessionId", TmsAccount.class )
					.setParameter("sessionId", sessionId );
			account = q.getSingleResult();
		} catch ( NoResultException e ) {
			account = null;
			logger.debug( "Not found: NoResultException for sessionId: '" + sessionId + "'" );
		} catch ( NonUniqueResultException e ) {
			account = null;
			logger.error( "It should be one account only for sessionId: '" + sessionId + "'" );
			
			TypedQuery<TmsAccount> q2 = em.createNamedQuery( "findAccountBySessionId", TmsAccount.class )
					.setParameter("sessionId", sessionId );
			List<TmsAccount> lst = q2.getResultList();
			logger.debug( "Find by sessionID size = " + lst.size());
			for ( TmsAccount a : lst ) {
				logger.debug( "Account[ id, name, pwd ]: " + a.getId() + ", " + a.getUsrName() + ", " + a.getPwd() );
			}
			
	
			
			
			
		} catch ( Exception e ) {
			account = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return account;
	}
	
	public TmsAccount addAccount( String usrName, String pwd, TmsUser tmsUser ) {
		

		if ( usrName == null || usrName.length() == 0 )
			throw new IllegalArgumentException ( "User Name cannot be null or emptyl!" );		if ( pwd == null || pwd.length() == 0 )
			throw new IllegalArgumentException( "Password cannot be null or empty!" );
		if ( tmsUser == null )
			throw new IllegalArgumentException( "Valid TmsUser cannot be null!" );

		// Convert to Lower Case usrName to be case insensitive
		if ( usrName != null )
			usrName = usrName.trim().toLowerCase();
		
		TmsAccount account = new TmsAccount( usrName, pwd, tmsUser ); 
		
		
		try {
			account = DataFacade.getInstance().insert( account );
		} catch ( Exception e) {
			logger.error( "Cannot add account\n" + e );
		}
		
		if ( logger.isDebugEnabled())
				logger.debug( "New Tms Account was added: " + account );

		return account;
		
	}

	public TmsAccount deleteAccount( TmsUser tmsUser ) {
		
		TmsAccount existingAccount = null;

		if ( tmsUser == null )
			throw new IllegalArgumentException( "Valid TmsUser cannot be null!" );

		existingAccount = findByUserId( tmsUser ); 
		
		try {
			DataFacade.getInstance().remove( existingAccount );
		} catch ( Exception e) {
			logger.error( "Cannot remove account\n" + e );
			
			return null;
		}
		
		if ( logger.isDebugEnabled())
				logger.debug( "Tms Account has been deleted: " + existingAccount );

		return existingAccount;
		
	}

	private void writeTransaction( Transaction tr ) {
		tr = DataFacade.getInstance().insert( tr );

		if ( logger.isDebugEnabled()) logger.debug( tr );
	}
	
	public TmsAccount findByUserId( TmsUser user ) {
		TmsAccount account;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<TmsAccount> q = em.createNamedQuery( "findAccountByUsrId", TmsAccount.class )
					.setParameter("userId", user.getId() );
			account = q.getSingleResult();
		} catch ( NoResultException e ) {
			account = null;
			if ( logger.isDebugEnabled())
				logger.debug( "Account Not Found for TmsUser: '" + user.getFirstAndLastNames() + "'" );
		} catch ( NonUniqueResultException e ) {
			account = null;
			logger.error( "It should be one account only for TmsUser: '" + user.getFirstAndLastNames() + "'" );
		} catch ( Exception e ) {
			account = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return account;
	}

	public TmsAccount findByUserName( String usrName ) {
		TmsAccount account;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<TmsAccount> q = em.createNamedQuery( "findAccountByUsrName", TmsAccount.class )
					.setParameter("usrName", usrName );
			account = q.getSingleResult();
		} catch ( NoResultException e ) {
			account = null;
			if ( logger.isDebugEnabled())
				logger.debug( "Account Not Found for usrName: '" + usrName + "'" );
		} catch ( NonUniqueResultException e ) {
			account = null;
			logger.error( "It should be one account only for usrName: '" + usrName + "'" );
		} catch ( Exception e ) {
			account = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return account;
	}

	public TmsAccount addAccountDefault( TmsUser tmsUser ) {
		if ( tmsUser == null )
			throw new IllegalArgumentException( "Valid TmsUser cannot be null!" );

		TmsAccount account; 

		// Find account record for the TmsUser
		if ( logger.isDebugEnabled()) logger.debug( "Try to find Account for TmsUser: '" + tmsUser.getFirstAndLastNames() + "'" );
		account = findByUserId( tmsUser );
		if ( account != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Account exists already. Not necessary to create" );
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Account does not exist. Must be created" );
			// Create FREE name and Default password password
			String accName = getFreeUserName( tmsUser.getLastName());
			if ( accName != null ) {
				
				// Necessary to add account
				String accPwd = TmsAccount.generateNewPassword();
				
				account = addAccount( accName, accPwd, tmsUser );
				if ( logger.isDebugEnabled()) logger.debug( "User account will be added: '" + accName + "'" );
			} else {
				if ( logger.isDebugEnabled()) logger.debug( "Cannot create new UserName!" );
			}
		
		}
		
		return account;
	}
	
	// TODO
	// Minimum and maxlength of User Name should be in company settings
	int min_num = 8;
	int max_num = 11;
	// TODO
	// Put prefix into the company settings
	final static String USRNAME_PREFIX = "fi";
	
	public String getFreeUserName( String usrName ) {
		String retName = null;
		
		String tmpName;
		if ( usrName != null ) {
			tmpName = USRNAME_PREFIX + usrName;
			tmpName = tmpName.toLowerCase();
			
			tmpName = StringUtils.replaceChars( tmpName, "ˆ÷‰ƒÂ≈", "ooaaaa" );
//			tmpName = StringUtils.replaceChars( tmpName, "‰ƒ", "a" );
//			tmpName = StringUtils.replaceChars( tmpName, "Â≈", "a" );
			
			if ( tmpName.length() < min_num ) {
				tmpName = tmpName.concat( "123456789" ).substring( 0, min_num );
			} else if ( tmpName.length() > max_num ) {
				tmpName = tmpName.substring( 0, max_num );
			}

			int i = 1;
			String searchName = new String ( tmpName );
			EntityManager em = DataFacade.getInstance().createEntityManager();
			TypedQuery<TmsAccount> q = em.createNamedQuery( "findAccountByUsrName", TmsAccount.class );
			while ( true ) {
				try {
					q.setParameter("usrName", searchName );
					// Fetched Account with specify UserName. Should be one account only!!!  
					q.getSingleResult();
				} catch ( NoResultException e ) {
					if ( logger.isDebugEnabled()) logger.debug( "Name '" + searchName + "' is free! Will be used" );
					retName = searchName;
					break;
				} catch ( NonUniqueResultException e ) {
					logger.error( "It should be one account only for usrName: '" + searchName + "'" );
				} catch ( Exception e ) {
					logger.error( e );
				}

				searchName = tmpName.concat( "." + i );
				i++;
				
				if ( i > 500 ) {
					logger.error( "Cannot create unique username. " + i + " attempts were made!" );
					searchName = null;
					break;
				}
				
			}
			em.close();
			
		}
		
		return retName;
	}
	
	
	
	
}
