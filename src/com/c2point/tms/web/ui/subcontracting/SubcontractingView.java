package com.c2point.tms.web.ui.subcontracting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractMainView;
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

public class SubcontractingView extends AbstractMainView {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( SubcontractingView.class.getName());

	private SubcontractingModel model;

	public SubcontractingView( TmsApplication app, SupportedFunctionType visibility ) {
		super( app );

		if ( visibility == SupportedFunctionType.SUBCONTRACTING_COMPANY ) {

		}
	}

	public void initUI() {

		this.model = new SubcontractingModel( this.getTmsApplication());

		this.setSizeFull();

		Component projectsView = getProjectsList();
		Component projectInfo = getProjectInfo();
		Component subContractsView = getSubsList();
		Component subContractsInfo = getSubInfo();

		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setSpacing( true );

		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );

		/////
		HorizontalSplitPanel subContractsSplit  = new HorizontalSplitPanel();
		subContractsSplit.setSplitPosition( 75, Unit.PERCENTAGE );
		subContractsSplit.setSizeFull();
		subContractsSplit.setLocked( false );

		subContractsSplit.setFirstComponent( subContractsView );
		subContractsSplit.setSecondComponent( subContractsInfo );
		////
		vl.addComponent( projectInfo );
		vl.addComponent( separator );
		vl.addComponent( subContractsSplit );

		vl.setExpandRatio( subContractsSplit, 1.0f );

		HorizontalSplitPanel horSplit = new HorizontalSplitPanel();
		horSplit.setSplitPosition( 25, Unit.PERCENTAGE );
		horSplit.setSizeFull();
		horSplit.setLocked( false );

		horSplit.setFirstComponent( projectsView );
		horSplit.setSecondComponent( vl );

		this.addComponent( horSplit );

	}

	private Component getProjectInfo() {

		Component comp = new ProjectView( this.model );


		return comp;
	}

	private Component getProjectsList() {

		Component comp = new ProjectsListComponent( model );
		
		return comp;
	}


	private Component getSubInfo() {

		Component comp = null;

		return comp;
	}


    private Component getSubsList() {
		
    	Component comp = new SubcontractorsListComponent( model );
		
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



}
