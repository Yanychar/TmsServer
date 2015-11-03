package com.c2point.tms.web.ui.stuff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractMainView;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class StuffManagementView extends AbstractMainView {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( StuffManagementView.class.getName());
	
	private StuffMgmtModel model;

	
	public StuffManagementView( TmsApplication app, SupportedFunctionType visibility ) {
		super( app );
		
		model.setStuffToShow( visibility );
		
	}
	
	@Override
	public void initUI() {
	
		this.model = new StuffMgmtModel( this.getTmsApplication(), this.getTmsApplication().getSessionData().getUser().getOrganisation());

		this.setSizeFull();
		
		Component stuffsList = getStuffList();
		Component stuffPanel = getStuffView();

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
	
	private Component getStuffList() {

		StuffListComponent comp = new StuffListComponent( this.model );

		return comp;
	}

	private Component getStuffView() {
		
		VerticalLayout vl = new VerticalLayout();
		
		vl.addComponent( new StuffViewComponent( this.model ));
		Label glue = new Label( "" );
		vl.addComponent( glue );
//		vl.setExpandRatio( glue, 1.0f );

		return vl;
	}

}
