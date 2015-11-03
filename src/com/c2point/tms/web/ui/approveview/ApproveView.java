package com.c2point.tms.web.ui.approveview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.reporting.pdf.PdfTemplate;
import com.c2point.tms.web.reporting.pdf.documents.ApprovalViewPdf;
import com.c2point.tms.web.ui.AbstractMainView;
import com.c2point.tms.web.ui.approveview.model.ApproveModel;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

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


		PdfTemplate pdfDoc = new ApprovalViewPdf( getTmsApplication(), model).create(); //.getTaskReportsList(), model.getStartDate(), model.getEndDate());

		Window subwindow = new Window(
				model.getApp().getResourceStr( "menu.item.report.time" )
				+ " & "
				+ model.getApp().getResourceStr( "menu.item.report.travel" )
		);



		subwindow.setModal( true );
//		subwindow.setSizeFull();
		subwindow.setWidth( "80%" );
		subwindow.setHeight( "90%" );
		subwindow.setResizable( true );
		subwindow.center();

		Embedded e = new Embedded();
		e.setType(Embedded.TYPE_BROWSER);
		// Here we create a new StreamResource which downloads our StreamSource,
		// which is our pdf.
		StreamResource resource = new StreamResource( pdfDoc, PdfTemplate.getTmpName());
		// Set the right mime type
		resource.setMIMEType("application/pdf");
		e.setSource(resource);
		e.setSizeFull();

		subwindow.setContent( e );

		UI.getCurrent().addWindow( subwindow );

/*
		((VerticalLayout) window.getContent()).setSizeFull();
		window.setResizable(true);
		window.setWidth("800");
		window.setHeight("600");
		window.center();
		Embedded e = new Embedded();
		e.setSizeFull();
		e.setType(Embedded.TYPE_BROWSER);
		// Here we create a new StreamResource which downloads our StreamSource,
		// which is our pdf.
//		StreamResource resource = new StreamResource(new PdfTemplate(), "test.pdf?" + System.currentTimeMillis(), getTmsApplication());
//		StreamResource resource = new StreamResource(new PdfTemplate(), "test.pdf", getTmsApplication());
		StreamResource resource = new StreamResource( pdfDoc, PdfTemplate.getTmpName(), getTmsApplication());
		// Set the right mime type
		resource.setMIMEType("application/pdf");
		e.setSource(resource);

		window.addComponent(e);
		getTmsApplication().getMainWindow().addWindow(window);
//		getTmsApplication().getMainWindow().addComponent(e);
 *
 */

	}

}
