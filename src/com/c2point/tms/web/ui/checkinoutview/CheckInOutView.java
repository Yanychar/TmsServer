package com.c2point.tms.web.ui.checkinoutview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.AbstractMainView;
import com.c2point.tms.web.ui.checkinoutview.model.CheckInOutModel;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CheckInOutView extends AbstractMainView {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( CheckInOutView.class.getName());

	private CheckInOutModel model;

	public CheckInOutView( TmsApplication app, SupportedFunctionType visibility ) {
		super( app );

		if ( visibility == SupportedFunctionType.PRESENCE_TEAM || visibility == SupportedFunctionType.PRESENCE_COMPANY ) {

			model.setPresenceToShow( visibility );
		}

	}

	@Override
	public void initUI() {

		this.model = new CheckInOutModel( this.getTmsApplication());

		this.setSizeFull();
//		this.setSpacing( true );

		Component userList = getUserSelectionList();
		Component options = getOptionPanel();
		Component checkInOutList = getCheckInOutComponent();
		Component detailedComponent = getDetailedView();

		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setSpacing( true );

		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );

		vl.addComponent( options );
		vl.addComponent( separator );

		/////
		HorizontalSplitPanel recordsSplit  = new HorizontalSplitPanel();
		recordsSplit.setSplitPosition( 75, Unit.PERCENTAGE );
		recordsSplit.setSizeFull();
		recordsSplit.setLocked( false );
		
		recordsSplit.setFirstComponent( checkInOutList );
		recordsSplit.setSecondComponent( detailedComponent );		
		////
		vl.addComponent( recordsSplit );
		vl.setExpandRatio( recordsSplit, 1.0f );
		
		HorizontalSplitPanel horSplit = new HorizontalSplitPanel();
		horSplit.setSplitPosition( 25, Unit.PERCENTAGE );
		horSplit.setSizeFull();
		horSplit.setLocked( false );

		horSplit.setFirstComponent( userList );
		horSplit.setSecondComponent( vl );

		this.addComponent( horSplit );

	}

	private Component getOptionPanel() {

    	return new AdditionalOptionsComponent( model );
	}


	private Component getUserSelectionList() {

		UserListComponent comp = new UserListComponent( model );

		return comp;

	}

	private Component getCheckInOutComponent() {

		VerticalLayout vl = new VerticalLayout();

		vl.setSizeFull();
		vl.setMargin( true );
		vl.setSpacing( true );

		CheckInOutList comp =  new CheckInOutList( this.model );
		comp.setHeight( "100%" );
//        comp.setWidth( "100%" );

		this.model.addChangedListener( comp );

		vl.addComponent( comp );

		return vl;

	}
	
	private Component getDetailedView() {
		return new DetailedViewComponent( model );
	}
	
	
	
	@Override
	protected void initDataAtStart() {

		this.model.initModel();


	}
	@Override
	protected void initDataReturn() {
		// TODO Auto-generated method stub

	}


	private void printCurrentView() {
		logger.debug( "Print button has been pressed!" );
	}




}
