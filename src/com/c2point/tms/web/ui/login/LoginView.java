package com.c2point.tms.web.ui.login;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.AuthenticationFacade;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractMainView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class LoginView  extends AbstractMainView {

	private static Logger logger = LogManager.getLogger( LoginView.class.getName());

	LoginComponent loginComponent;
	
	private final ArrayList<LoginListener> listeners;
	
	public LoginView( TmsApplication app ) {
		super( app );
		listeners = new ArrayList<LoginListener>();
	}
	
	public void initUI() {

		this.setSizeFull();
		this.setSpacing( true );
		
		loginComponent = new LoginComponent( this.getTmsApplication());
		loginComponent.setHeight( "400px" );
		loginComponent.setHeight( "400px" );
		
		loginComponent.setName( getTmsApplication().getNameFromCookies());
		loginComponent.setPwd( getTmsApplication().getPwdFromCookies());
		loginComponent.setRemember( getTmsApplication().getRememberFlagFromCookies());
		loginComponent.setLanguage( getTmsApplication().getLanguageFromCookies());
		
		addComponent(loginComponent);
		setComponentAlignment( loginComponent, Alignment.MIDDLE_CENTER );

		loginComponent.addLoginButtonListener( new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				if ( login()) {
					// Store credentials as cookies
					getTmsApplication().storeInCookies( 
							loginComponent.getName(),
							loginComponent.getPwd(),
							loginComponent.toRemember(),
							loginComponent.getLanguage()
					);
				} else {
					getTmsApplication().deleteCookies();
				}
				
			}
		}); 
	}

	private boolean login() {
		boolean bRes = false;
		if ( logger.isDebugEnabled()) logger.debug( "Started login..." );
		
		TmsAccount account = null;
		// Login
		account = AuthenticationFacade.getInstance()
					.authenticateTmsUser( loginComponent.getName(), loginComponent.getPwd());
		if ( account != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "  Logged-In (web): " + account.getUser().toStringShort());
			login( account.getUser());
			bRes = true;
		} else {
			loginComponent.invalid();
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "... login end. Succeeded? " + bRes );
		return bRes;
	}

	private boolean login( TmsUser user ) {
		boolean bRes = true;
		
		getTmsApplication().getSessionData().setUser( user );

		fireLoginEvent( user );
		
		return bRes;
	}
	
	private void fireLoginEvent( TmsUser user ) {
		for ( LoginListener l : listeners ) {
			l.newUserLogged( user );
		}
	}

	public void addLoginListener( LoginListener l ) {
		listeners.add( l );
	}

	public void removeLoginListener( LoginListener l ) {
		listeners.remove( l );
	}

	public interface LoginListener {
		public void newUserLogged( TmsUser user );
	}

	@Override
	protected void initDataAtStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initDataReturn() {
		// TODO Auto-generated method stub
		
	}

	public void click() {
		this.loginComponent.click();
	}
		
	
}
