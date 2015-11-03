package com.c2point.tms.web.ui.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.tools.exprt.DataExportProcessor;
import com.c2point.tms.tools.imprt.LoggerIF;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ExportToolView extends Panel implements ValueChangeListener, LoggerIF {

	private static Logger logger = LogManager.getLogger( ExportToolView.class.getName());
	
	
	private ComboBox 		orgSelector;

	private DateField 		startDF;
	private DateField 		endDF;
	
	private TextArea		outputArea;
	
	private Button 			exportReportsButton;

	private TmsApplication 	app;

	private Organisation  	selectedOrganisation;

	private Date 			startDate;
	private Date 			endDate;
	
	public ExportToolView( TmsApplication app ) {
		this( app, null );
	}
		
	public ExportToolView( TmsApplication app, Organisation org ) {
		super();
		
		this.app = app;
		this.selectedOrganisation = org;
		
		initUI();

	}

	public void initUI() {
	
		this.setCaption( app.getResourceStr( "tools.panel.export.caption" ));
	
		VerticalLayout vl = new VerticalLayout();
		
		vl.setSpacing( true );
		vl.setWidth( "100%" );
		vl.setHeight( "100%" );
		
		orgSelector = getOrgSelector();
	
		HorizontalLayout hl = new HorizontalLayout(); // Just for setting panel and adding
		
//		hl.setMargin( true );
		hl.setSpacing( true );		
		
		startDF = new DateField();
		startDF.setLocale( app.getSessionData().getLocale());
		endDF = new DateField();
		endDF.setLocale( app.getSessionData().getLocale());
		
		startDF.setDateFormat((( SimpleDateFormat )DateUtil.getDateFormatYear4digit()).toPattern());
		endDF.setDateFormat((( SimpleDateFormat )DateUtil.getDateFormatYear4digit()).toPattern());

		initDatePeriod();
		
		startDF.setResolution( DateField.RESOLUTION_DAY );
		endDF.setResolution( DateField.RESOLUTION_DAY );
		startDF.setImmediate(true);
		endDF.setImmediate(true);
		
		startDF.addListener( new Property.ValueChangeListener() {
			public void valueChange( ValueChangeEvent event ) {
				// Get the new value and format it to the current locale
				if ( logger.isDebugEnabled()) logger.debug( "Start date has been changed!" );
				Object value = event.getProperty().getValue();        
				if ( value == null ||
					!( value instanceof Date )
				) {            
					Notification.show( app.getResourceStr( "tools.export.date.error" ));        
				} else {
					startDate = ( Date )value;
				}   			
			}
		});
		
		endDF.addListener( new Property.ValueChangeListener() {
			public void valueChange( ValueChangeEvent event ) {
				if ( logger.isDebugEnabled()) logger.debug( "End date has been changed!" );
				// Get the new value and format it to the current locale
				Object value = event.getProperty().getValue();        
				if ( value == null ||
					!( value instanceof Date )
				) {            
					Notification.show( app.getResourceStr( "tools.export.date.error" ));        
				} else {
					endDate = ( Date )value;
				}   			
			}
		});
		
		
		hl.addComponent( startDF );
		hl.addComponent( new Label( " " + app.getResourceStr( "tools.export.date.separator" ) + "  " ));
		hl.addComponent( endDF );
		
		
		
		
		outputArea = new TextArea( app.getResourceStr( "tools.export.output" ));
		outputArea.setRows( 20 );
		outputArea.setColumns( 80 );        
		outputArea.setWordwrap( false );
		outputArea.setImmediate( true );
		outputArea.setReadOnly( true );
		
		
		exportReportsButton = new Button( app.getResourceStr( "tools.export.start.button" ));
		exportReportsButton.setImmediate( true );
		exportReportsButton.addListener( new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				logger.debug( "exportReports has been pressed" );
				exportReports();
			}
		});
		
		
		
		
		Label glue = new Label( "" );
		glue.setHeight("100%");
		
		vl.addComponent( orgSelector );
		vl.addComponent( hl );
		vl.addComponent( outputArea );
		vl.addComponent( exportReportsButton  );
		vl.addComponent( glue );
		vl.setExpandRatio( glue, 1.0f );
		
		this.setContent( vl );
		
	}
	
	private ComboBox getOrgSelector() {
		
		orgSelector  = new ComboBox( app.getResourceStr( "general.edit.company" ) + ":" );
		
		orgSelector.setWidth( "16em" );
		orgSelector.setItemCaptionMode( Select.ITEM_CAPTION_MODE_EXPLICIT );
		orgSelector.setFilteringMode( Filtering.FILTERINGMODE_STARTSWITH );
		orgSelector.setImmediate( true );        
		orgSelector.setNullSelectionAllowed( false );
		
		if ( selectedOrganisation != null ) {
			orgSelector.addItem( selectedOrganisation );
			orgSelector.setItemCaption( 
					selectedOrganisation, 
					selectedOrganisation.getCode() 
					+ "  " + selectedOrganisation.getName());

			orgSelector.setValue( selectedOrganisation );
			
			orgSelector.setReadOnly( true );
		} else {

			for ( Organisation org : OrganisationFacade.getInstance().getOrganisations()) {
				if ( org != null && !org.isDeleted()) {
					orgSelector.addItem( org );
					orgSelector.setItemCaption( org, org.getCode() + "  " + org.getName());
				}
			}

			if ( orgSelector.size() > 0 ) {
				orgSelector.setValue( orgSelector.getItemIds().iterator().next());
			}
			
		}
		
		orgSelector.addListener( this );
		
		return orgSelector;
	}

	@Override
	public void valueChange( final ValueChangeEvent event) {

	}
	

	public boolean exportReports() {
		boolean res = false;
		
		Organisation selectedOrg = ( Organisation )orgSelector.getValue();
		selectedOrg = DataFacade.getInstance().find( Organisation.class, selectedOrg.getId());
		
		if ( selectedOrg != null ) {

			DataExportProcessor processor;

			logger.debug( "Start Reports Export for Organisation: '" + selectedOrg.getName() + "'" );
			
			outputArea.setReadOnly( false );
			outputArea.setValue( "" );
			outputArea.setReadOnly( true );

			// Export data
			processor = DataExportProcessor.getExportProcessor( selectedOrg, this );
			if ( processor != null ) {
				res = processor.process( selectedOrg, startDate, endDate );
			} else {
				error( "ERROR: Could not find data processor for export" );
			}
				
		} else {
			logger.error( "No Organisation selected!" );
		}
		
		return res;

	}

	private void initDatePeriod() {
		Calendar cal = DateUtil.getFirstDayOfWeek( new Date(), -2 );
		startDate = cal.getTime();
		cal.add( Calendar.DATE, 13 );
		endDate 	= cal.getTime();

		startDF.setValue( startDate );
		endDF.setValue( endDate );
	}
	
	
	@Override
	public void info( String str ) {
		
		if ( str != null && str.length() > 0 ) {
			
			String areaStr = ( String )outputArea.getValue();
			
			areaStr = areaStr.concat( "\n" );
			areaStr = areaStr.concat( str );
		
			outputArea.setReadOnly( false );
			outputArea.setValue( areaStr );
			outputArea.setCursorPosition( areaStr.length() - 1 );			
			outputArea.setReadOnly( true );
			
		}
	}

	@Override
	public void error( String str ) {
		
		info( str );
	}
	
	
}
