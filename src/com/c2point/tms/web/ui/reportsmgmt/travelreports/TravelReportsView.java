package com.c2point.tms.web.ui.reportsmgmt.travelreports;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractMainView;
import com.c2point.tms.web.ui.reportsmgmt.travelreports.model.ReportsManagementModel;
import com.c2point.tms.web.ui.reportsmgmt.travelreports.view.DateSelectionComponent;
import com.c2point.tms.web.ui.reportsmgmt.travelreports.view.ProjectsSelectionComponent;
import com.c2point.tms.web.ui.reportsmgmt.travelreports.view.TravelReportsComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class TravelReportsView extends AbstractMainView {

	private static Logger logger = LogManager.getLogger( TravelReportsView.class.getName());
	
	private ReportsManagementModel 	model;
	
//	private SupportedFunctionType 	visibility;
	
	public TravelReportsView( TmsApplication app ) {

		super( app );
//		this.visibility = visibility;

	}
	
	@Override
	public void initUI() {
	
		this.setSizeFull();
		this.setSpacing( true );
	
		HorizontalSplitPanel hl = new HorizontalSplitPanel();        
		hl.setSplitPosition( 20, Unit.PERCENTAGE );        
		hl.setSizeFull();        
		hl.setLocked( false );
		hl.addStyleName( Runo.SPLITPANEL_SMALL );
		
		getProjectsList().setSizeFull();
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setSpacing( true );
		vl.setMargin( true );

		vl.addComponent( getDateSelector());
		vl.addComponent( getReportsComponent());
		vl.setExpandRatio( getReportsComponent(), 1.0f );

		hl.addComponent( getProjectsList());
		hl.addComponent( vl );

		this.addComponent( hl );

	}

	private ProjectsSelectionComponent	projects;
	private Component getProjectsList() {

		if ( projects == null ) {
			projects = new ProjectsSelectionComponent();
		}


		return projects;
	}

	private DateSelectionComponent	dateSelector;
	private Component getDateSelector() {

		if ( dateSelector == null ) {
			dateSelector = new DateSelectionComponent( this.getTmsApplication());
		}

		return dateSelector;
	}

	private TravelReportsComponent	reports;
	private Component getReportsComponent() {

		if ( reports == null ) {
			reports = new TravelReportsComponent();
		}

		return reports;
	}

		
	@Override
	protected void initDataAtStart() {
		
		logger.debug( "Data initialized 1st time" );
		
		model = new ReportsManagementModel( this.getTmsApplication());
//		model.setToShowFilter( visibility );

		model.initModel();

		projects.setModel( model );

		dateSelector.addListener( this.model );
		dateSelector.setEditableDateRange( 
				this.model.getEditableStartDate(), 
				this.model.getEditableEndDate()
			);

		reports.setModel( model );
	}
	@Override
	protected void initDataReturn() {
//		this.model = new ReportsManagementModel( this.getTmsApplication());
		logger.debug( "Data initialized again" );
	}

}
