package com.c2point.tms.web.ui.approveview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;

@SuppressWarnings("serial")
public class ApproveRejectButtonsComponent extends CustomComponent {

	@SuppressWarnings("unused")
	private static Logger 	logger = LogManager.getLogger( ApproveRejectButtonsComponent.class.getName());
	
	private TmsApplication 	app;
	
	private AbstractReport 	report;

	private Button 	approveButton;
	private Button 	rejectButton;
	
	public ApproveRejectButtonsComponent( TmsApplication app ) {
		super();
		
		this.app = app;
		
		initView();

//		updateButtonView( report );
	}

	
	private void initView() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing( true );
		setCompositionRoot( layout );
	
		approveButton = new Button();
		approveButton.addStyleName( "smallroundicon" );
		approveButton.setIcon( new ThemeResource( "icons/16/selected16.png" ));
		approveButton.setDescription( app.getResourceStr( "general.approve.tooltip" ));
		approveButton.setImmediate( true );
		
		rejectButton = new Button();
		rejectButton.addStyleName( "smallroundicon" );
		rejectButton.setIcon( new ThemeResource( "icons/16/delete16.png" ));
		rejectButton.setDescription( app.getResourceStr( "general.reject.tooltip" ));
		rejectButton.setImmediate( true );
		
		
		layout.addComponent( approveButton );
		layout.addComponent( rejectButton );

		disable();
		
	}

	public void updateButtonView( AbstractReport report ) {
		
		this.report = report;

		enable();
	
	}
	
	public Object getData() {
		return this.report;
	}

	public AbstractReport getReport() {
		return this.report;
	}

	public void addApproveListener( Button.ClickListener listener ) {
		approveButton.addClickListener( listener );
	}
	
	public void addRejectListener( Button.ClickListener listener ) {
		rejectButton.addClickListener( listener );
	}

	public void enable() {
		
		if ( report != null ) {
			
			ApprovalFlagType type = report.getApprovalFlagType();

			approveButton.setEnabled( type != ApprovalFlagType.APPROVED && type != ApprovalFlagType.PROCESSED );
			rejectButton.setEnabled( type != ApprovalFlagType.REJECTED && type != ApprovalFlagType.PROCESSED );
		} else {
			disable();
		}
		
		
	}

	public void disable() {
		approveButton.setEnabled( false );
		rejectButton.setEnabled( false );
	}

}
