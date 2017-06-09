package com.c2point.tms.web.ui.approveview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.pdf.documents.ApprovalViewPdf;
import com.c2point.tms.web.ui.AbstractMainView;
import com.c2point.tms.web.ui.approveview.model.ApproveModel;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class ApproveView extends AbstractMainView {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( ApproveView.class.getName());

	private ApproveModel model;

	public ApproveView( TmsApplication app, SupportedFunctionType visibility ) {
		super( app );

		if ( visibility == SupportedFunctionType.REPORTS_TEAM || visibility == SupportedFunctionType.REPORTS_COMPANY ) {

			model.getFilter().setReportsToShow( visibility );
		}
	}

	public void initUI() {

		this.model = new ApproveModel( this.getTmsApplication());

		this.setSizeFull();

		Component usersList = getUserSelectionList();
		Component options = getOptionPanel();
		Component reportsList = getReportsList();
		Component detailedComponent = getDetailedView();

		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setSpacing( true );

		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );

		vl.addComponent( options );
		vl.addComponent( separator );

		/////
		HorizontalSplitPanel projectSplit  = new HorizontalSplitPanel();
		projectSplit.setSplitPosition( 75, Unit.PERCENTAGE );
		projectSplit.setSizeFull();
		projectSplit.setLocked( false );

		projectSplit.setFirstComponent( reportsList );
		projectSplit.setSecondComponent( detailedComponent );
		////
		vl.addComponent( projectSplit );
		vl.setExpandRatio( projectSplit, 1.0f );



		HorizontalSplitPanel horSplit = new HorizontalSplitPanel();
		horSplit.setSplitPosition( 25, Unit.PERCENTAGE );
		horSplit.setSizeFull();
		horSplit.setLocked( false );

		horSplit.setFirstComponent( usersList );
		horSplit.setSecondComponent( vl );

		this.addComponent( horSplit );

	}

	private Component getOptionPanel() {

    	Component comp = new AdditionalOptionsComponent( model, new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if ( logger.isDebugEnabled()) logger.debug( "Print! has been pressed" );

				printCurrentView();

			}

    	});


    	return comp;

	}

	private Component getReportsList() {


		return new ReportsListComponent( model );
	}

	private Component getDetailedView() {
		return new DetailedViewComponent( model );
	}


    private Component getUserSelectionList() {
    	UserListComponent comp = new UserListComponent( model );

		return comp;
    }


	@Override
	protected void initDataAtStart() {

		this.model.initModel();

		logger.debug( "AproveView.initDataAtStart" );
	}

	@Override
	protected void initDataReturn() {
		this.model.initModel();
//		logger.debug( "AproveView.initDataReturn" );
	}


	/*
	 *  Print currently selected reports
	 */

	private void printCurrentView() {


		if ( model.getSelectedUser() == null ) {

			// TODO: Add Warning Message

			return;
		}
		
		Window subwindow = new Window(
				model.getApp().getResourceStr( "menu.item.report.time" )
				+ " & "
				+ model.getApp().getResourceStr( "menu.item.report.travel" )
		);

		
		subwindow.setModal( true );
		subwindow.setWidth( "80%" );
		subwindow.setHeight( "90%" );
		subwindow.setResizable( true );
		subwindow.center();
		
		
		
		ApprovalViewPdf report = new ApprovalViewPdf( getTmsApplication(), model );
		report.printReport();
		
		StreamResource resource = report.getResource();
		resource.setCacheTime( 0 );
		// Set the right mime type
		resource.setMIMEType("application/pdf");

		BrowserFrame browser = new BrowserFrame( "Browser" );
		browser.setSource( resource );
		browser.setSizeFull();
		
		subwindow.setContent( browser );
		subwindow.addCloseListener( new CloseListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void windowClose(CloseEvent e) {

				report.deleteReport();
				
			}
			
		});

		
		UI.getCurrent().addWindow( subwindow );
		
	}

}
