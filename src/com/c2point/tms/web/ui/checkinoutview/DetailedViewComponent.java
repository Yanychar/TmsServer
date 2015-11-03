package com.c2point.tms.web.ui.checkinoutview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.GeoCoordinates;
import com.c2point.tms.entity.Project;
import com.c2point.tms.util.location.GeoDistanceValidator;
import com.c2point.tms.web.ui.checkinoutview.model.CheckInOutModel;
import com.c2point.tms.web.ui.listeners.CheckInOutSelectionListener;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class DetailedViewComponent extends VerticalLayout implements CheckInOutSelectionListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( DetailedViewComponent.class.getName());

	private CheckInOutModel model;

	private Label 		projectDataHeader;

	private Label 		projectLabel;
	private Label 		projectName;

	private Label 		mngrLabel;
	private Label 		mngrName;

	private Label 		projectLocationLabel;
	private Label 		projectLocationInfo;
	
	private Label 		checkInOutHeader;

	private Label 		hoursLabel;
	private Label 		hoursValue;

	private Label 		checkInLabel;
	private Label 		checkInInfo;

	private Label 		checkOutLabel;
	private Label 		checkOutInfo;

	private Label separator1;
	private Label separator2;
	private Label glue 			= new Label( "" );

	public DetailedViewComponent( CheckInOutModel model ) {
		
		this.model = model;
				
		initView();

		model.addChangedListener( this );

	}

	private void initView() {

		setSizeFull();

		this.setMargin( new MarginInfo( false, true, true, true ));
		setSpacing( true );
		
		projectDataHeader = new Label( model.getApp().getResourceStr( "approve.project.label" ));
		projectDataHeader.addStyleName( Runo.LABEL_H2);
		projectDataHeader.setWidth( Sizeable.SIZE_UNDEFINED, Unit.PIXELS );
		
		projectLabel	= new Label( model.getApp().getResourceStr( "general.edit.project" ) + ": " );
		projectName  	= new Label( "", ContentMode.HTML );
		projectName.addStyleName( "h3");

		projectLocationLabel = new Label( model.getApp().getResourceStr( "presence.map.window.header" ) + ": " );
		projectLocationInfo	 = new Label( "", ContentMode.HTML );
		projectLocationInfo.addStyleName( "h3");
		
		mngrLabel		= new Label( model.getApp().getResourceStr( "general.edit.owner" ) + ": " );
		mngrName		= new Label( "", ContentMode.HTML );
		mngrName.addStyleName( "h3");


		checkInOutHeader	= new Label( model.getApp().getResourceStr( "checkin.hours.header" ), ContentMode.HTML );
		checkInOutHeader.addStyleName( Runo.LABEL_H2);
		checkInOutHeader.setWidth( Sizeable.SIZE_UNDEFINED, Unit.PIXELS );

		hoursLabel	= new Label( model.getApp().getResourceStr( "checkin.registered.label" ) + ": " );
		hoursValue	= new Label( "", ContentMode.HTML );
		hoursValue.addStyleName( "h3");
		
		checkInLabel	= new Label( model.getApp().getResourceStr( "general.table.header.checkin" ) + ": " );
		checkInInfo		= new Label( "", ContentMode.HTML );
		checkInInfo.addStyleName( "h3");
		
		checkOutLabel	= new Label( model.getApp().getResourceStr( "general.table.header.checkout" ) + ": " );
		checkOutInfo	= new Label( "", ContentMode.HTML );
		checkOutInfo.addStyleName( "h3");
		
		
		
		separator1 = new Label( "<hr/>", ContentMode.HTML );
		separator2 = new Label( "<hr/>", ContentMode.HTML );

		separator1.setWidth( "100%" );
		separator2.setWidth( "100%" );

		
		glue = new Label( "" );
		glue.setWidth("100%");
		
	}

	private void showProjectData( CheckInOutRecord record ) {
		
		GridLayout grid = new GridLayout( 2, 3 );
		grid.setSpacing( true );
		grid.setWidth( "100%" );
		
		Project project = record.getProject();
		
		projectName.setValue( 
				project != null 
				? "<b>" + project.getName() + "</b>"
				: model.getApp().getResourceStr( "unknown.value" )
		);

		projectLocationInfo.setValue(
				project != null && project.getGeo() != null
				? "<b>" + project.getAddress() + "</b>"
				:   "<font color=\"red\">"
				  + model.getApp().getResourceStr( "project.location.unknown" )
				  + "</font>"
					
		);

		mngrName.setValue( 
				project != null 
				? "<b>" + project.getProjectManager().getFirstAndLastNames() + "</b>"
				: model.getApp().getResourceStr( "unknown.value" )
		);

		grid.addComponent( projectLabel, 			0, 0 );
		grid.addComponent( projectName, 			1, 0 );
		grid.addComponent( projectLocationLabel, 	0, 1 );
		grid.addComponent( projectLocationInfo, 	1, 1 );
		grid.addComponent( mngrLabel, 				0, 2 );
		grid.addComponent( mngrName, 				1, 2 );

		
		
		addComponent( projectDataHeader );
		setComponentAlignment( projectDataHeader, Alignment.MIDDLE_CENTER );

		addComponent( grid );

		
		addComponent( separator1 );
		
	}

	private void showHoursData( CheckInOutRecord record ) {

		
		GridLayout grid = new GridLayout( 2, 3 );
		grid.setSpacing( true );
		grid.setWidth( "100%" );
		
		checkInLabel	= new Label( model.getApp().getResourceStr( "general.table.header.checkin" ) + ": " );
		checkInInfo		= new Label( "", ContentMode.HTML );
		checkInInfo.addStyleName( "h3");
		
		checkOutLabel	= new Label( model.getApp().getResourceStr( "general.table.header.checkout" ) + ": " );
		checkOutInfo	= new Label( "", ContentMode.HTML );
		checkOutInfo.addStyleName( "h3");

		// Show summarized amount of time
		String totalHMStr = model.getTotalHours( record );
		
		if ( totalHMStr != null ) {
			
			hoursValue.setValue( "<b>" + totalHMStr + "</b>" );
			
		} else {
			hoursValue.setValue( "<font color=\"red\">"
									+ model.getApp().getResourceStr( "checkin.registered.no.msg" )
									+ "</font>"
							);
		}
		
		checkInInfo.setValue( getGeoValidationString( model.getCheckInGeoStatus()));
		checkOutInfo.setValue( getGeoValidationString( model.getCheckOutGeoStatus()));
		
		grid.addComponent( hoursLabel, 		0, 0 );
		grid.addComponent( hoursValue, 		1, 0 );
		grid.addComponent( checkInLabel, 	0, 1 );
		grid.addComponent( checkInInfo, 	1, 1 );
		grid.addComponent( checkOutLabel, 	0, 2 );
		grid.addComponent( checkOutInfo, 	1, 2 );

		
		addComponent( checkInOutHeader );
		setComponentAlignment( checkInOutHeader, Alignment.MIDDLE_CENTER );

		
		addComponent( grid );

		
		addComponent( separator2 );
		
		
	}

	@Override
	public void selected( CheckInOutRecord record ) {

		this.removeAllComponents();

		if ( record != null ) {
			
			if ( logger.isDebugEnabled()) logger.debug( "CheckInOutRecord item has been selected" );

			showProjectData( record );
			showHoursData( record );
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Nothing to show. CheckInOut record == NULL" );
		}

		addComponent( glue );
		setExpandRatio( glue, 1.0f );
		
	}
	
	private String getGeoValidationString( GeoDistanceValidator.ValidationResult validationResult ) {

		String resStr = "";
		
		switch ( validationResult ) {

			case OK:
				resStr = "<b>" + model.getApp().getResourceStr( "general.button.ok" ) + "</b>"; 
				break;
			case BASE_INVALID:
				resStr = "<font style=\"color:DarkOrange\">"
					  	+ model.getApp().getResourceStr( "checkinout.location.missing" )
					  	+ "</font>";
				break;
			case TOO_FAR:				
				resStr = "<font style=\"color:red\">"
					  	+ "Too far from Workplace"
					  	+ "</font>";
				break;
			case OTHER_INVALID: 
			case BOTH_INVALID:
			default:
				resStr = "<font style=\"color:DarkOrange\">"
					  	+ model.getApp().getResourceStr( "checkinout.location.unavailable" )
					  	+ "</font>";
				break;
		
		}
		
		return resStr;
	}

}
