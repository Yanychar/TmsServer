package com.c2point.tms.web.ui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.DataFacade;
import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.tools.LoggerIF;
import com.c2point.tms.tools.imprt.DataImportProcessor;
import com.c2point.tms.tools.imprt.v10PersonsImportProcessor;
import com.c2point.tms.tools.imprt.v10ProjectsImportProcessor;
import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ImportToolView extends Panel implements ValueChangeListener, LoggerIF {

	private static Logger logger = LogManager.getLogger( ImportToolView.class.getName());
	
	
	private ComboBox 			orgSelector;
	
	private TextArea			outputArea;
	
	private Button 				importPersonnelButton; 
	private Button 				importProjectsButton;

	private TmsApplication 		app;
	
	private Organisation  		selectedOrganisation;
	
	public ImportToolView( TmsApplication app ) {
		this( app, null );
	}
		
	public ImportToolView( TmsApplication app, Organisation org ) {
		super();
		
		this.app 					= app;
		this.selectedOrganisation 	= org;
		
		initUI();

	}

	public void initUI() {
	
		this.setCaption( app.getResourceStr( "tools.panel.import.caption" ));
	
		VerticalLayout vl = new VerticalLayout();
		
		vl.setSpacing( true );
		vl.setWidth( "100%" );
		vl.setHeight( "100%" );
		
		orgSelector = getOrgSelector();
		
//		outputArea = new TextArea( null, "initial text");
		outputArea = new TextArea( app.getResourceStr( "tools.import.output" ));
		outputArea.setRows( 20 );
		outputArea.setColumns( 80 );        
		outputArea.setWordwrap( false );
		outputArea.setImmediate( true );
		outputArea.setReadOnly( true );
		
		
		importPersonnelButton = new Button( app.getResourceStr( "tools.import.personnel.button" ));
		importPersonnelButton.setImmediate( true );
		importPersonnelButton.addListener( new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				logger.debug( "importPersonnel has been pressed" );
				importPersonnel();
			}
		});
		
		importProjectsButton = new Button( app.getResourceStr( "tools.import.projects.button" ));
		importProjectsButton.setImmediate( true );
		importProjectsButton.addListener( new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				logger.debug( "importProjects has been pressed" );
				importProjects();
			}
		});
		
		
		
		
		Label glue = new Label( "" );
		glue.setHeight("100%");
		
		vl.addComponent( orgSelector );
		vl.addComponent( outputArea );
		vl.addComponent( importPersonnelButton  );
		vl.addComponent( importProjectsButton  );
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
	

	private boolean importPersonnel() {
		boolean res = false;
		
		Organisation selectedOrg = ( Organisation )orgSelector.getValue();
		selectedOrg = DataFacade.getInstance().find( Organisation.class, selectedOrg.getId());
		
		if ( selectedOrg != null ) {

			DataImportProcessor processor;

			logger.debug( "Start Personnel Data Import for Organisation: '" + selectedOrg.getName() + "'" );
			
			outputArea.setReadOnly( false );
			outputArea.setValue( "" );
			outputArea.setReadOnly( true );
			// Import Personnel
			processor = new v10PersonsImportProcessor( this );
			res = processor.process( selectedOrg );
				
		} else {
			logger.error( "No Organisation selected!" );
		}
		
		return res;
	}

	private boolean importProjects() {
		boolean res = false;
		
		Organisation selectedOrg = ( Organisation )orgSelector.getValue();
		selectedOrg = DataFacade.getInstance().find( Organisation.class, selectedOrg.getId());
		
		if ( selectedOrg != null ) {

			DataImportProcessor processor;

			logger.debug( "Start Projects Data Import for Organisation: '" + selectedOrg.getName() + "'" );
			
			outputArea.setReadOnly( false );
			outputArea.setValue( "" );
			outputArea.setReadOnly( true );
			// Import Personnel
			processor = new v10ProjectsImportProcessor( this );
			res = processor.process( selectedOrg );
				
		} else {
			logger.error( "No Organisation selected!" );
		}
		
		return res;
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
