package com.c2point.tms.web.ui.company;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractMainView;
import com.c2point.tms.web.ui.company.model.CompaniesMgmtModel;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CompaniesManagementView extends AbstractMainView {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( CompaniesManagementView.class.getName());
	
	private CompaniesMgmtModel model;

	
	public CompaniesManagementView( TmsApplication app, SupportedFunctionType visibility ) {
		super( app );
		
		model.setCompanyToShow( visibility );
	}
	

	
	@Override
	public void initUI() {
	
		this.model = new CompaniesMgmtModel( this.getTmsApplication());

		this.setSizeFull();
//		this.setSpacing( true );
		
		Component stuffsList = getCompaniesList();
		Component stuffPanel = getCompanyView();
		
		HorizontalSplitPanel horSplit = new HorizontalSplitPanel();
		horSplit.setSplitPosition( 25, Unit.PERCENTAGE );
		horSplit.setSizeFull();
		horSplit.setLocked( false );
		
		horSplit.setFirstComponent( stuffsList );
		horSplit.setSecondComponent( stuffPanel );

		this.addComponent( horSplit );

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
	
	private Component getCompaniesList() {
		CompaniesListComponent comp = new CompaniesListComponent( this.model );

		return comp;
	}

	private Component getCompanyView() {
		
		VerticalLayout vl = new VerticalLayout();
		
		vl.addComponent( new CompanyViewComponent( this.model ));
		Label glue = new Label( "" );
		vl.addComponent( glue );
//		vl.setExpandRatio( glue, 1.0f );

		return vl;
	}


}
