package com.c2point.tms.web.application;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.configuration.TmsConfiguration;
import com.c2point.tms.configuration.TmsDBUpdate;
import com.c2point.tms.entity.SessionData;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.web.ui.AbstractMainView;
import com.c2point.tms.web.ui.MainView;
import com.c2point.tms.web.ui.login.LoginView;
import com.c2point.tms.web.ui.login.LoginView.LoginListener;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.ui.UI;


@SuppressWarnings("serial")
@Theme("tms")
public class TmsApplication extends UI implements LoginListener {

	private static Logger logger = LogManager.getLogger( TmsApplication.class.getName());

	private Map<String, AbstractMainView> mapViews;

	private MainView mainView;
	
//	private transient HttpServletResponse response;
//	private transient HttpServletRequest request;

	private SessionData sessionData;
	
//	private VaadinRequest request;
	
	@Override
	public void init( VaadinRequest request ) {

//		this.request = request;		
		
		TmsConfiguration.readConfiguration(); // this );
		TmsDBUpdate.updateTmsDatabase();
		
		sessionData = new SessionData();
		
		mapViews = new HashMap<String, AbstractMainView>();		
		
		addStyleName( "main" );

/*		
		addListener( new Window.CloseListener() {
			   @Override
			    public void windowClose( CloseEvent e ) {
			       logger.debug( "Closing the application" );
			       getMainWindow().getApplication().close();
			    } 
			});		
*/
		// Gets current cookies
		Cookie[] cookies = request.getCookies();
		if ( cookies != null ) {
			getFromCookies( cookies );
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Cookies were read already!" );
		}
		
		LoginView loginView = new LoginView( this );

		putView( loginView );
		loginView.addLoginListener( this );

		setContent( loginView );
		
//		loginView.click();

	}

	@Override
	public void close() {
		// Close once only. super.close() sets running = false
//		if (isRunning()) {
/*
			getContext().removeTransactionListener(this);
			TeagleUser currentUser = (TeagleUser) getUser();
			if ((currentUser != null) && (currentUser.getId() != null) && (runningApplications.containsKey(currentUser.getId()))) {
				runningApplications.remove(currentUser.getId());
			}
*/			
			super.close();
//		}
	}

	public SessionData getSessionData() {
		if ( sessionData == null ) {
			sessionData = new SessionData();
		}

		return sessionData;
	}

	public void deleteCookies() {
		storeInCookies( null, null, false, "" );
	}
	
	public void storeInCookies( String name, String pwd, boolean toRemember, String language ) {

		Cookie nameCookie = new Cookie( "storedname", name );
		Cookie pwdCookie = new Cookie( "storedpwd", pwd );
		Cookie rememberCookie = new Cookie( "storedrememberflag", Boolean.toString( toRemember ));
		Cookie languageCookie = new Cookie( "storedlanguage", language );
		
		
		nameCookie.setPath( "/" );
		pwdCookie.setPath( "/" );
		rememberCookie.setPath( "/" );
		languageCookie.setPath( "/" );
		
		if ( name != null && toRemember ) {
			// Store cookies
			nameCookie.setMaxAge( 2592000 ); // 30 days
			pwdCookie.setMaxAge( 2592000 ); // 30 days
			rememberCookie.setMaxAge( 2592000 ); // 30 days
			languageCookie.setMaxAge( 2592000 ); // 30 days
			if ( logger.isDebugEnabled()) logger.debug( "Cookies will be stored" );
		} else {
			// Delete cookies
			nameCookie.setMaxAge( 0 );
			pwdCookie.setMaxAge( 0 ); // 30 days
			rememberCookie.setMaxAge( 0 ); // 30 days
			languageCookie.setMaxAge( 0 ); // 30 days
			if ( logger.isDebugEnabled()) logger.debug( "Cookies will be deleted" );
		}

		VaadinServletResponse response = 
				   (VaadinServletResponse) VaadinService.getCurrentResponse();
		
		response.addCookie( nameCookie );
		response.addCookie( pwdCookie );
		response.addCookie( rememberCookie );
		response.addCookie( languageCookie );

		if ( logger.isDebugEnabled()) logger.debug( "Cookies were added to response" );
	}

	private void getFromCookies( Cookie[] cookies ) {
		if ( cookies != null ) {
			String name;
			for ( int i=0; i < cookies.length; i++ ) {
				name = cookies[ i ].getName();
				if ("storedname".equals( name )) {
					// Log the user in automatically
					storedName = cookies[ i ].getValue();
					if ( logger.isDebugEnabled()) logger.debug( "StoredName found and = " + storedName );
				} else if ("storedpwd".equals( name )) {
					storedPwd = cookies[ i ].getValue();
					if ( logger.isDebugEnabled()) logger.debug( "StoredPwd found and = " + storedPwd );
				} else if ("storedrememberflag".equals( name )) {
					String str = cookies[ i ].getValue();
					storedRememberFlag = Boolean.parseBoolean( str );
					if ( logger.isDebugEnabled()) logger.debug( "StoredRememberFlag found and = " + storedRememberFlag );
				} else if ("storedlanguage".equals( name )) {
					storedLanguage = cookies[ i ].getValue();
					if ( logger.isDebugEnabled()) logger.debug( "StoredLanguage found and = " + storedLanguage );
				} else {
					if ( logger.isDebugEnabled()) logger.debug( "Wrong cookies were found!" );
				}
			}
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "There is no cookies stored!" );
		}
	}
	
	private String storedName = null;
	private String storedPwd = null;
	private boolean storedRememberFlag = false;
	private String storedLanguage = null;
	
	public String getNameFromCookies() { return storedName; }
	public String getPwdFromCookies() { return storedPwd; }
	public boolean getRememberFlagFromCookies() { return storedRememberFlag; }
	public String getLanguageFromCookies() { return storedLanguage; }

	private  void putView( AbstractMainView view ) {
		if ( !mapViews.containsKey( view.getClass().getSimpleName())) {
			mapViews.put( view.getClass().getSimpleName(), view );
		}
	}

	public String getResourceStr( String key ) {
		
		try {
			return this.getSessionData().getBundle().getString( key );
		} catch (Exception e) {
			logger.error(  "Could not find string resource '" + key + "'" );
		}
		return "";
	}

	@Override
	public void newUserLogged(TmsUser user) {

		if (mainView == null) {
			mainView = new MainView( this );
			mainView.initWindow();
		}

		setContent( mainView );
		
	}

	
}