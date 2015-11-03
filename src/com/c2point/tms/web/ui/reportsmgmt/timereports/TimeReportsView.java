package com.c2point.tms.web.ui.reportsmgmt.timereports;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractMainView;
import com.c2point.tms.web.ui.reportsmgmt.timereports.model.ReportsManagementModel;
import com.c2point.tms.web.ui.reportsmgmt.timereports.view.DateSelectionComponent;
import com.c2point.tms.web.ui.reportsmgmt.timereports.view.TasksSelectionComponent;
import com.c2point.tms.web.ui.reportsmgmt.timereports.view.TimeReportsComponent;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class TimeReportsView extends AbstractMainView {

	private static Logger logger = LogManager.getLogger( TimeReportsView.class.getName());

	private ReportsManagementModel 	model;

	private SupportedFunctionType 	visibility;

	public TimeReportsView( TmsApplication app, SupportedFunctionType visibility ) {
		super( app );
		this.visibility = visibility;

	}
	@Override
	public void initUI() {

//		this.model = new ReportsManagementModel( this.getTmsApplication());

		this.setSizeFull();
		this.setSpacing( true );

		HorizontalSplitPanel hl = new HorizontalSplitPanel();
		hl.setSplitPosition( 25, Sizeable.UNITS_PERCENTAGE );
		hl.setSizeFull();
		hl.setLocked( false );
		hl.addStyleName( Runo.SPLITPANEL_SMALL );
//		hl.setMargin( true );

		getProjectTasksList().setSizeFull();

		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setSpacing( true );
		vl.setMargin( true );

		vl.addComponent( getDateSelector());
		vl.addComponent( getReportsComponent());
		vl.setExpandRatio( getReportsComponent(), 1.0f );

		hl.addComponent( getProjectTasksList());
		hl.addComponent( vl );

		this.addComponent( hl );

	}

	private TasksSelectionComponent	projectsAndTasks;
	private Component getProjectTasksList() {

		if ( projectsAndTasks == null ) {
			projectsAndTasks = new TasksSelectionComponent();
		}


		return projectsAndTasks;
	}

	private DateSelectionComponent	dateSelector;
	private Component getDateSelector() {

		if ( dateSelector == null ) {
			dateSelector = new DateSelectionComponent( this.getTmsApplication());
		}

		return dateSelector;
	}

	private TimeReportsComponent	reports;
	private Component getReportsComponent() {

		if ( reports == null ) {
			reports = new TimeReportsComponent();
		}

		return reports;
	}

	@Override
	protected void initDataAtStart() {
		logger.debug( "Data initialized 1st time" );
		model = new ReportsManagementModel( this.getTmsApplication());
		model.setToShowFilter( visibility );

		model.initModel();

		projectsAndTasks.setModel( model );

		dateSelector.addListener( this.model );

		reports.setModel( model );

	}
	@Override
	protected void initDataReturn() {
//		this.model.reInitModel();
//		this.model = new ReportsManagementModel( this.getTmsApplication());
//		this.model.initModel();
//		projectsAndTasks.setModel( model );





		logger.debug( "Data initialized again" );
	}



}
