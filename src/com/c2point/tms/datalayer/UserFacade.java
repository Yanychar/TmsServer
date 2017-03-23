package com.c2point.tms.datalayer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.TmsUserState;

public class UserFacade {
	private static Logger logger = LogManager.getLogger( UserFacade.class.getName()); 

	public static UserFacade getInstance() {
		return new UserFacade();
	}
	
	public List<TmsUser> list( Organisation org ) {
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<TmsUser> query = null;
		List<TmsUser> results = null;
		
		try {
			query = em.createNamedQuery( "listUsers", TmsUser.class )
						.setParameter( "org", org );
			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of TmsUsers. Size = " + results.size());
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No users found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return results;
		
	}

	public TmsUser findUser( TmsUser user, Organisation org ) {
		
		List<TmsUser> userLst = findByUsrName( user.getLastName(), org );
		
		if ( userLst != null && userLst.size() > 0 ) {

			// For all in the list:
			for ( TmsUser tmpUser : userLst ) {
				// If user found return it
				if ( tmpUser.compareByName( user ) == 0 ) {
					return tmpUser;
				}
			}
		}
		
		return null;
	}
	
	public List<TmsUser> findByUsrName( String usrName, Organisation org ) {
		List<TmsUser> userLst;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<TmsUser> q = em.createNamedQuery( "findUserByUsrName", TmsUser.class )
				.setParameter("usrName", usrName )
				.setParameter( "org", org );
			userLst = q.getResultList();
		} catch ( NoResultException e ) {
			userLst = null;
			if ( logger.isDebugEnabled())
				logger.debug( "TmsUser Not Found for usrName: '" + usrName
								+ "' in Org: '" + org.getName() + "'");
		} catch ( Exception e ) {
			userLst = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return userLst;
	}
	
	public TmsUser findByCode( String code, Organisation org ) {
		TmsUser user = null;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<TmsUser> q = em.createNamedQuery( "findUserByCode", TmsUser.class )
					.setParameter( "usrCode", code )
					.setParameter( "org", org );
			user = q.getSingleResult();
		} catch ( NoResultException e ) {
			user = null;
			if ( logger.isDebugEnabled())
				logger.debug( "TmsUser Not Found for usrCode: '" + code
						+ "' in Org: '" + ((org != null && org.getName() != null ) ? org.getName() : "" ) + "'");
		} catch ( NonUniqueResultException e ) {
			logger.error( "More than one user with Code: '" + code + "' in Org: '" 
					+ ((org != null && org.getName() != null ) ? org.getName() : "" ) + "'");
		} catch ( Exception e ) {
			user = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return user;
	}

	public TmsUser findByState( Organisation org, TmsUserState state ) {

		TmsUser user = null;

		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			TypedQuery<TmsUser> query = em.createNamedQuery( "findUserByState", TmsUser.class )
						.setParameter( "state", state )
						.setParameter( "org", org );
			user = query.getSingleResult();
			if ( logger.isDebugEnabled()) logger.debug( "**** Found TmsUser" );
		} catch ( NoResultException e ) {
			user = null;
			if ( logger.isDebugEnabled())
				logger.debug( "TmsUser Not Found for specified TmsUserState"
						+ "' in Org: '" + ((org != null && org.getName() != null ) ? org.getName() : "" ) + "'");
		} catch ( NonUniqueResultException e ) {
			logger.error( "More than one user for specified TmsUserState" + "' in Org: '" 
					+ ((org != null && org.getName() != null ) ? org.getName() : "" ) + "'");
		} catch ( Exception e ) {
			user = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return user;
	}
	
	
}
