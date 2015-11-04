package com.c2point.tms.web.ui.reportview.tasktravel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Project;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.reporting.pdf.PdfTemplate;
import com.c2point.tms.web.reporting.pdf.documents.PersonnelReportPdf;
import com.c2point.tms.web.reporting.pdf.documents.ProjectsReportPdf;
import com.c2point.tms.web.reporting.taskandtravel.ProjectsReport;
import com.c2point.tms.web.reporting.taskandtravel.UsersReport;
import com.c2point.tms.web.ui.AbstractMainView;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class ReportTaskTravelView extends AbstractMainView {

	private static Logger logger = LogManager.getLogger( ReportTaskTravelView.class.getName());
	
	private Panel filterPanel;

	private DateField startDF;
	private DateField endDF;
	
	private ComboBox reportType;
	// Per Person options
	private CheckBox allDate; 
	private CheckBox allProjects; 
	private CheckBox allTasks_1; 
	private CheckBox allTravels_1; 
	
	// Per Project options
	private ComboBox projectList;
	private CheckBox allTasks_2; 
	private CheckBox allTravels_2; 
//	private CheckBox allPersons; 

	private Button createButton;
	
	private ReportTaskTravelModel model;
	
	public ReportTaskTravelView( ReportTaskTravelModel model ) {
		super( model.getApp());
	
		this.model = model;
		
		projectList = getProjectSelector();		
		
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
//		vl_1.setWidth( "100%" );
		vl_1.setMargin( true );
		vl_1.setSpacing( true );
		
//		filterPanel.addStyleName( Runo.PANEL_LIGHT );
//		filterPanel.setWidth( "60ex" );
		
//		filterPanel.setHeight( "40ex" );
		
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

		allDate = new CheckBox( getTmsApplication().getResourceStr( "reporting.checkbox.daily" ));
		allProjects = new CheckBox( getTmsApplication().getResourceStr( "reporting.checkbox.allprojects" ));
		allTasks_1 = new CheckBox( getTmsApplication().getResourceStr( "reporting.checkbox.alltasks" ));
		allTravels_1 = new CheckBox( getTmsApplication().getResourceStr( "reporting.checkbox.travels" ));

		
		allTasks_2 = new CheckBox( getTmsApplication().getResourceStr( "reporting.checkbox.alltasks" ));
		allTravels_2 = new CheckBox( getTmsApplication().getResourceStr( "reporting.checkbox.travels" ));
		
//		allPersons = new CheckBox( "Show personnel" );

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
		
		allDate.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setDateFlag(( Boolean )allDate.getValue());
				
			}
		});
		allProjects.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setProjectsFlag(( Boolean )allProjects.getValue());
				
			}
		}); 
		allTasks_1.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setTasksFlag_1(( Boolean )allTasks_1.getValue());
				
			}
		});
		allTravels_1.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setTravelFlag_1(( Boolean )allTasks_1.getValue());
				
			}
		});

		
		projectList.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {

				model.selectProject(( Project )projectList.getValue());
			
			}
		});
		
		allTasks_2.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setTasksFlag_2(( Boolean )allTasks_2.getValue());
				
			}
		});
		allTravels_2.addValueChangeListener( new ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setTravelFlag_2(( Boolean )allTravels_2.getValue());
				
			}
		});
		
/*
		allPersons.addListener( new Property.ValueChangeListener() {
			@Override
			public void valueChange( ValueChangeEvent event ) {
				model.setPersonsFlag(( Boolean )allPersons.getValue());
				
			}
		});
*/
		reportType.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange( ValueChangeEvent event ) {

				model.setReportType(( ReportTaskTravelModel.ReportType )reportType.getValue());
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

	public ReportTaskTravelModel getModel() {
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
		
		
		allDate.setValue( model.isDateFlag());
		allProjects.setValue( model.isProjectsFlag()); 
		allTasks_1.setValue(  model.isTasksFlag_1());
		allTravels_1.setValue( model.isTravelFlag_1());

		allTasks_2.setValue(  model.isTasksFlag_2());
		allTravels_2.setValue( model.isTravelFlag_2());
//		allPersons.setValue( model.isPersonsFlag());
	
	}
	
	private ComboBox getReportTypeSelector() {
		reportType  = new ComboBox( getTmsApplication().getResourceStr( "reporting.report.type" ));

		reportType.setWidth( "16em" );
		reportType.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		reportType.setImmediate( true );        
		reportType.setNullSelectionAllowed( false );
		
		reportType.addItem( ReportTaskTravelModel.ReportType.PERSONNEL_VIEW );
		reportType.setItemCaption( ReportTaskTravelModel.ReportType.PERSONNEL_VIEW, getTmsApplication().getResourceStr( "reporting.report.type.personnel" ));
		reportType.addItem( ReportTaskTravelModel.ReportType.PROJECT_VIEW );
		reportType.setItemCaption( ReportTaskTravelModel.ReportType.PROJECT_VIEW, getTmsApplication().getResourceStr( "reporting.report.type.projects" ));
		
		return reportType;
	}

	private ComboBox getProjectSelector() {
		
		projectList  = new ComboBox();

		projectList.setWidth( "12em" );
		projectList.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		projectList.setImmediate( true );        
		projectList.setNullSelectionAllowed( true );
		projectList.setInputPrompt( "Show all Projects");
		
//		projectList.setNullSelectionItemId( Long.MIN_VALUE );
//		projectList.addItem( Long.MIN_VALUE );
//		projectList.setItemCaption( Long.MIN_VALUE, "" );
		
		for ( Project prj : model.getProjectsList()) {
			
			projectList.addItem( prj );
			projectList.setItemCaption( prj, prj.getName());
			
		}

		return projectList;
	}

	private void updateFieldsList() {

		VerticalLayout vl = ( VerticalLayout )filterPanel.getContent();
		
		vl.removeComponent( allDate );
		vl.removeComponent( allProjects );
		vl.removeComponent( allTasks_1 );
		vl.removeComponent( allTravels_1 );
		
		vl.removeComponent( projectList );
		vl.removeComponent( allTasks_2 );
		vl.removeComponent( allTravels_2 );
		vl.removeComponent( createButton );

		if ( ReportTaskTravelModel.ReportType.PERSONNEL_VIEW == ( ReportTaskTravelModel.ReportType )reportType.getValue()) {

			vl.addComponent( allDate );
			vl.addComponent( allProjects );
			vl.addComponent( allTasks_1 );
			vl.addComponent( allTravels_1 );
		
		} else if ( ReportTaskTravelModel.ReportType.PROJECT_VIEW == ( ReportTaskTravelModel.ReportType )reportType.getValue()) {

			//			filterPanel.addComponent( allPersons );
			vl.addComponent( projectList );
			vl.addComponent( allTasks_2 );
			vl.addComponent( allTravels_2 );

		} else {
			vl.removeAllComponents();
		}

		vl.addComponent( createButton );
		
//		createButton.setEnabled( model.isChanged());
	}

	private void createReport() {

		// Ask model to create update internal structures
		model.updateTaskAndTravelLists();

		// Prepare Report
		UsersReport ur = null;
		ProjectsReport pr = null;
		PdfTemplate pdfDoc = null;
		if ( model.getReportType() == ReportTaskTravelModel.ReportType.PERSONNEL_VIEW ) {
			ur = model.createUsersReport();
			pdfDoc = new PersonnelReportPdf( getTmsApplication(), ur, model ).create();
		} else if ( model.getReportType() == ReportTaskTravelModel.ReportType.PROJECT_VIEW ) {
			pr = model.createProjectsReport();
			pdfDoc = new ProjectsReportPdf( getTmsApplication(), pr, model ).create();
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

//		((VerticalLayout) window.getContent()).setSizeFull();        
		window.setResizable(true);
		
		if ( logger.isDebugEnabled()) {
			logger.debug( " getMainWindow().getWidth() = " + UI.getCurrent().getWidth());
			logger.debug( " getMainWindow().getHeight() = " + UI.getCurrent().getHeight());
		}
//		window.setWidth( mainWindow.getWidth() - 50, mainWindow.getWidthUnits()); //.setWidth("800");        
//		window.setHeight( mainWindow.getHeight() - 50, mainWindow.getHeightUnits()); //.setHeight("600");

		window.setWidth( "80%" );
		window.setHeight( "90%" );
		
		window.center();        
		Embedded e = new Embedded();        
		e.setSizeFull();        
		e.setType(Embedded.TYPE_BROWSER);        
		// Here we create a new StreamResource which downloads our StreamSource,        
		// which is our pdf.        
		StreamResource resource = new StreamResource( pdfDoc, PdfTemplate.getTmpName());
		// Set the right mime type        
		resource.setMIMEType("application/pdf");        
		e.setSource(resource);        

		window.setContent( e );
		
		UI.getCurrent().addWindow( window );		

		
		
/*		
		if ( ur != null ) {
			
			// Show PDF
			reportWindow = getApplication().getWindow( "reportWindow" );
			if ( reportWindow == null ) {
	    		reportWindow = new Window();
	    		reportWindow.setName( "reportWindow" );
	    		
	    		label = new Label( "", Label.CONTENT_PREFORMATTED );
	    		label.setSizeFull();
	    		reportWindow.addComponent( label );
	    		
	    		
	    		
	        	// Add the window to the application
	        	getApplication().addWindow( reportWindow );

			
			}
		
			Iterator<Component> iter = reportWindow.getComponentIterator();
			Component fl;
			while (iter.hasNext()) {
				fl = iter.next();
				if ( fl != null && ( fl instanceof Label )) {
					(( Label )fl ).setValue( ur.toStringFull());
					break;
				}
			}
					
					
					

	    	// Get the URL for the window, and open that in a new
	    	// browser window, in this case in a small window.
	    	getWindow().open( new ExternalResource( reportWindow.getURL()), // URL
	    			"reportWindow" //, // window name                                
	//	    			500, // width                                
	//	    			200, // weight                                
	//	    			Window.BORDER_NONE // decorations                                
	    	    	);    	
			
			
			reportWindow.focus();
	    	
	    	
		} else {
			logger.error( "Cannot create report!" );
			getWindow().showNotification(
					"Error", //getTmsApplication().getResourceStr( "general.errors.update.header" ),
					"Report cannot be created  because unknown reason!", //getTmsApplication().getResourceStr( "approve.errors.update.body" ),
					Notification.TYPE_ERROR_MESSAGE
			);
		}
*/		
	}
}
