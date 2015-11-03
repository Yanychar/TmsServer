package com.c2point.tms.web.ui.reportview.checkinout;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.reporting.checkinout.ProjectsReport;
import com.c2point.tms.web.reporting.checkinout.UsersReport;
import com.c2point.tms.web.reporting.pdf.PdfTemplate;
import com.c2point.tms.web.reporting.pdf.documents.PersonnelCheckInOutReportPdf;
import com.c2point.tms.web.reporting.pdf.documents.ProjectsCheckInOutReportPdf;
import com.c2point.tms.web.ui.AbstractMainView;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class ReportCheckInOutView extends AbstractMainView {

	private static Logger logger = LogManager.getLogger( ReportCheckInOutView.class.getName());
	
	private Panel filterPanel;

	private DateField startDF;
	private DateField endDF;
	
	private ComboBox reportType;

	// Project based Report visibility options
	private CheckBox projectPersonnel; 
	private CheckBox projectDate; 
	
	// Personnel based Report visibility options
	private CheckBox personnelDate; 
	private CheckBox personnelProjects; 

	private Button createButton;
	
	private ReportCheckInOutModel model;
	
	public ReportCheckInOutView( ReportCheckInOutModel model ) {
		super( model.getApp());
	
		this.model = model;
	}

	@Override
	protected void initUI() {
		this.setSizeFull();
		
		HorizontalLayout hl_1 = new HorizontalLayout();
		hl_1.setWidth( "100%" );
		hl_1.setMargin( true );
		hl_1.setSpacing( true );
		
		filterPanel = new Panel( getTmsApplication().getResourceStr( "reporting.tool.header" ));
		VerticalLayout vl_1 = new VerticalLayout();
		vl_1.setMargin( true );
		vl_1.setSpacing( true );
		
		startDF = new DateField();
		endDF = new DateField();
		
		startDF.setDateFormat((( SimpleDateFormat )DateUtil.getDateFormatYear4digit()).toPattern());
		endDF.setDateFormat((( SimpleDateFormat )DateUtil.getDateFormatYear4digit()).toPattern());
		
		startDF.setResolution( Resolution.DAY );
		endDF.setResolution( Resolution.DAY );
		startDF.setImmediate(true);
		endDF.setImmediate(true);

		HorizontalLayout hl_2 = new HorizontalLayout(); // Just for setting panel and adding
		
		hl_2.setMargin( true ); // we want a margin
		hl_2.setSpacing( true ); // and spacing between components		
		
		hl_2.addComponent( new Label( getTmsApplication().getResourceStr( "time.panel.from")));
		hl_2.addComponent( startDF );
		hl_2.addComponent( new Label( "  " + getTmsApplication().getResourceStr( "time.panel.to")));
		hl_2.addComponent( endDF );
		
		
		
		reportType = getReportTypeSelector();

		projectPersonnel = new CheckBox( getTmsApplication().getResourceStr( "reporting.checkbox.allpersonnel" ));
		projectDate = new CheckBox( getTmsApplication().getResourceStr( "reporting.checkbox.daily" )); 
		
		personnelDate = new CheckBox( getTmsApplication().getResourceStr( "reporting.checkbox.daily" ));
		personnelProjects = new CheckBox( getTmsApplication().getResourceStr( "reporting.checkbox.allprojects" ));
		
		createButton = new Button();
    	createButton.setCaption( getTmsApplication().getResourceStr( "general.button.create" ));
    	createButton.addStyleName( Runo.BUTTON_BIG );
    	createButton.addStyleName( Runo.BUTTON_DEFAULT );
		
		
		
    	vl_1.addComponent( hl_2 );		
    	vl_1.addComponent( reportType );

		filterPanel.setContent( vl_1 );
		
		hl_1.addComponent( filterPanel );

		this.addComponent( hl_1 );

	}

	@Override
	protected void initDataAtStart() {
		
		showFilter();

		startDF.setLocale( getTmsApplication().getSessionData().getLocale());
		endDF.setLocale( getTmsApplication().getSessionData().getLocale());
		
		startDF.addValueChangeListener( new ValueChangeListener() {
			public void valueChange( ValueChangeEvent event ) {
				// Get the new value and format it to the current locale
				if ( logger.isDebugEnabled()) logger.debug( "Start date has been changed!" );
				Object value = event.getProperty().getValue();        
				if ( value == null ||
					!( value instanceof Date )
				) {            
					Notification.show( getTmsApplication().getResourceStr( "time.error.invalidinput" ));        
				} else {
					model.setStartDate(( Date )value );
				}   			
			}
		});
		
		endDF.addValueChangeListener( new ValueChangeListener() {
			public void valueChange( ValueChangeEvent event ) {
				if ( logger.isDebugEnabled()) logger.debug( "End date has been changed!" );
				// Get the new value and format it to the current locale
				Object value = event.getProperty().getValue();        
				if ( value == null ||
					!( value instanceof Date )
				) {            
					Notification.show( getTmsApplication().getResourceStr( "time.error.invalidinput" ));        
				} else {
					model.setEndDate(( Date )value );
				}   			
			}
		});

		
		projectPersonnel.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setProjectPersonnelFlag(( Boolean )projectPersonnel.getValue());
				
			}
		});
		
		projectDate.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setProjectDateFlag(( Boolean )projectDate.getValue());
				
			}
		});

		personnelDate.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setPersonnelDateFlag(( Boolean )personnelDate.getValue());
				
			}
		});
		
		personnelProjects.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setPersonnelProjectsFlag(( Boolean )personnelProjects.getValue());
				
			}
		});

		
		
		reportType.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange( ValueChangeEvent event ) {

				model.setReportType(( ReportCheckInOutModel.ReportType )reportType.getValue());
				updateFieldsList();
			
			}
		});
	
		createButton.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event) {
				logger.debug( "Create button has been clicked" );
				createReport();
			}
		});
		
	}

	public ReportCheckInOutModel getModel() {
		return model;
	}
	
	@Override
	protected void initDataReturn() {
		
		showFilter();
	}

	private void showFilter() {
		
		reportType.setValue( model.getReportType());

		updateFieldsList();

		startDF.setValue( model.getStartDate());
		endDF.setValue( model.getEndDate());
		
		
		projectDate.setValue( model.isProjectDateFlag());
		projectPersonnel.setValue( model.isProjectPersonnelFlag()); 

		personnelDate.setValue( model.isPersonnelDateFlag());
		personnelProjects.setValue( model.isPersonnelProjectsFlag()); 
	
	}
	
	private ComboBox getReportTypeSelector() {
		reportType  = new ComboBox( getTmsApplication().getResourceStr( "reporting.report.type" ));

		reportType.setWidth( "16em" );
		reportType.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		reportType.setImmediate( true );        
		reportType.setNullSelectionAllowed( false );
		
		reportType.addItem( ReportCheckInOutModel.ReportType.PERSONNEL_VIEW );
		reportType.setItemCaption( ReportCheckInOutModel.ReportType.PERSONNEL_VIEW, getTmsApplication().getResourceStr( "reporting.report.type.personnel" ));
		reportType.addItem( ReportCheckInOutModel.ReportType.PROJECT_VIEW );
		reportType.setItemCaption( ReportCheckInOutModel.ReportType.PROJECT_VIEW, getTmsApplication().getResourceStr( "reporting.report.type.projects" ));
		
		return reportType;
	}

	private void updateFieldsList() {

		VerticalLayout vl = ( VerticalLayout )filterPanel.getContent();
		
		vl.removeComponent( projectDate );
		vl.removeComponent( projectPersonnel );
		vl.removeComponent( personnelProjects );
		vl.removeComponent( personnelDate );
		
		vl.removeComponent( createButton );

		if ( ReportCheckInOutModel.ReportType.PROJECT_VIEW == ( ReportCheckInOutModel.ReportType )reportType.getValue()) {

			//			filterPanel.addComponent( allPersons );
			vl.addComponent( projectPersonnel );
			vl.addComponent( projectDate );

		} else if ( ReportCheckInOutModel.ReportType.PERSONNEL_VIEW == ( ReportCheckInOutModel.ReportType )reportType.getValue()) {

			vl.addComponent( personnelProjects );
			vl.addComponent( personnelDate );

		} else {
			vl.removeAllComponents();
		}

		vl.addComponent( createButton );
		
//		createButton.setEnabled( model.isChanged());
	}

	private void createReport() {

		// Ask model to create update internal structures
//		model.updateTaskAndTravelLists();

		// Prepare Report
		UsersReport ur = null;
		ProjectsReport pr = null;
		PdfTemplate pdfDoc = null;
		if ( model.getReportType() == ReportCheckInOutModel.ReportType.PERSONNEL_VIEW ) {
			ur = model.createUsersReport();
			pdfDoc = new PersonnelCheckInOutReportPdf( getTmsApplication(), ur, model ).create();
		} else if ( model.getReportType() == ReportCheckInOutModel.ReportType.PROJECT_VIEW ) {
			pr = model.createProjectsReport();
			pdfDoc = new ProjectsCheckInOutReportPdf( getTmsApplication(), pr, model ).create();
		} else {
			return;
		}
		
		// Show PDF
		Window window = new Window();
		
		switch ( model.getReportType() ) {
			case PERSONNEL_VIEW:
				window.setCaption( model.getApp().getResourceStr( "reporting.report.type.personnel" ));
				break;
			case PROJECT_VIEW:
				window.setCaption( model.getApp().getResourceStr( "reporting.report.type.projects" ));
				break;
			default:
				window.setCaption( "" );
				break;
		}

		window.setResizable(true);
		
		if ( logger.isDebugEnabled()) {
			logger.debug( " getMainWindow().getWidth() = " + UI.getCurrent().getWidth());
			logger.debug( " getMainWindow().getHeight() = " + UI.getCurrent().getHeight());
		}

		window.setWidth( "80%" );
		window.setHeight( "90%" );
		
		window.center();
		
		StreamResource resource = new StreamResource( pdfDoc, PdfTemplate.getTmpName());
		resource.setMIMEType("application/pdf");        

		BrowserFrame browser = new BrowserFrame( "Browser", resource );
		browser.setSizeFull();        
		
		window.setContent( browser );
		
		UI.getCurrent().addWindow( window );
		
	}
}
