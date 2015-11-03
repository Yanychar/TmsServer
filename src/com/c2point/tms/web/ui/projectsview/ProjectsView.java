package com.c2point.tms.web.ui.projectsview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractMainView;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ProjectsView extends AbstractMainView {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( ProjectsView.class.getName());

	private ProjectsModel model;

	public ProjectsView( TmsApplication app, SupportedFunctionType visibility ) {
		super( app );

		model.setProjectsToShow( visibility );

	}

	@Override
	public void initUI() {

		this.model = new ProjectsModel( this.getTmsApplication(), this.getTmsApplication().getSessionData().getUser().getOrganisation());

		this.setSizeFull();
//		this.setSpacing( true );

/*
		HorizontalSplitPanel horizontalSplit = new HorizontalSplitPanel();
		addComponent( horizontalSplit );
		setExpandRatio(horizontalSplit, 1);
		horizontalSplit.setSplitPosition(400, Sizeable.UNITS_PIXELS);

		horizontalSplit.setFirstComponent( getProjectsView());
		horizontalSplit.setSecondComponent( getTasksView());
*/

		Component projectPanel = getSingleProjectPanel();
		Component tasksList = getTasksList();
		Component projectsList = getProjectsList();

		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setSpacing( true );

		Label separator = new Label( "<hr/>", Label.CONTENT_XHTML );
		separator.setWidth( "100%" );

		vl.addComponent( projectPanel );
		vl.addComponent( separator );
		vl.addComponent( tasksList );

		vl.setExpandRatio( tasksList, 1.0f );

		HorizontalSplitPanel horSplit = new HorizontalSplitPanel();
		horSplit.setSplitPosition( 25, Unit.PERCENTAGE );
		horSplit.setSizeFull();
		horSplit.setLocked( false );


		horSplit.setFirstComponent( projectsList );
		horSplit.setSecondComponent( vl );

		this.addComponent( horSplit );

	}


	private Component getSingleProjectPanel() {

		Component comp = new ProjectView( this.model );


		return comp;
	}

	private Component getProjectsList() {

		ProjectsListComponent comp = new ProjectsListComponent( this.model );


		return comp;
	}

	private Component getTasksList() {

		Component comp = new ProjectTasksComponent( this.model );

		model.addChangedListener(( SelectionChangedListener )comp );


		return comp;
	}



	@Override
	protected void initDataAtStart() {

		logger.debug( "initDataStart..." );
		this.model.initModel();


	}



	@Override
	protected void initDataReturn() {

		logger.debug( "initDataReturn..." );
		this.model.initModel();

	}

}
