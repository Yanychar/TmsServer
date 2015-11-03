package com.c2point.tms.web.ui.approveview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.approveview.model.TmsUserHolder;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;

public class StatusPresenterComponent extends HorizontalLayout { // extends CustomComponent { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -598558165344422777L;

	private static Logger 	logger = LogManager.getLogger( StatusPresenterComponent.class.getName());
	
	private TmsApplication 	app;

	private TmsUserHolder 	userHolder;
	
	private Embedded iconToCheck = null;
	private Embedded iconApproved = null;
	private Embedded iconRejected = null;
	private Embedded iconProcessed = null;

	private String	tooltipToCheck = null;
	private String	tooltipApproved = null;
	private String	tooltipRejected = null;
	private String	tooltipProcessed = null;
	
	public StatusPresenterComponent( TmsApplication app, TmsUserHolder userHolder ) {
		
		super();
		
		this.app = app;
		this.userHolder = userHolder;
		
		initView();

	}
	
	public void updateStatus() {

		if ( userHolder != null ) {
			updateStatus( iconToCheck, tooltipToCheck, userHolder.getNoValidated());
			updateStatus( iconApproved, tooltipApproved, userHolder.getApproved());
			updateStatus( iconRejected, tooltipRejected, userHolder.getRejected());
			updateStatus( iconProcessed, tooltipProcessed, userHolder.getProcessed());
		} else {
			updateStatus( iconToCheck, tooltipToCheck, 0 );
			updateStatus( iconApproved, tooltipApproved, 0 );
			updateStatus( iconRejected, tooltipRejected, 0 );
			updateStatus( iconProcessed, tooltipProcessed, 0 );
		}
	}
	
	private void updateStatus( Embedded icon, String tooltip, int count ) {
		
		if ( count > 0 ) {
			icon.setVisible( true );
//			icon.setEnabled( true );
			icon.setDescription( count + " " + tooltip );
		} else {
			icon.setVisible( false );
//			icon.setEnabled( false );
//			icon.setDescription( "" );
			
		}
		
	}
	
	private void initView() {
		setSpacing( false );
		this.setMargin( false );

		createStatusIcons();
		
		this.setHeight( iconToCheck.getHeight(), iconToCheck.getHeightUnits());
		
		addComponent( iconToCheck );
		addComponent( iconApproved );
		addComponent( iconRejected );
		addComponent( iconProcessed );
		
//		if ( logger.isDebugEnabled()) logger.debug( "Component created!" );
	}


	private void createStatusIcons() {

		if ( iconToCheck == null ) {
			iconToCheck = new Embedded( "", new ThemeResource( "icons/16/question16.png" ));
			tooltipToCheck = " " + app.getResourceStr( "approve.userlist.status.tocheck.tooltip" );
		}
		if ( iconApproved  == null ) {
			iconApproved = new Embedded( "", new ThemeResource( "icons/16/selected16.png" ));
			tooltipApproved = " " + app.getResourceStr( "approve.userlist.status.approved.tooltip" );
		}
		if ( iconRejected  == null ) {
			iconRejected = new Embedded( "", new ThemeResource( "icons/16/delete16.png" ));
			tooltipRejected = " " + app.getResourceStr( "approve.userlist.status.rejected.tooltip" );
		}
		if ( iconProcessed  == null ) {
			iconProcessed = new Embedded( "", new ThemeResource( "icons/16/paid16.png" ));
			tooltipProcessed = " " + app.getResourceStr( "approve.userlist.status.processed.tooltip" );
		}
	}
	
	
}