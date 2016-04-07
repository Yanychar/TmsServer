package com.c2point.tms.web.ui.approveview;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.ui.approveview.model.ApproveModel;
import com.c2point.tms.web.ui.approveview.model.ProjectHolder;
import com.c2point.tms.web.ui.checkinoutview.CheckTimeMapComponent;
import com.c2point.tms.web.ui.geo.MapFactory;
import com.c2point.tms.web.ui.geo.MapViewIF;
import com.c2point.tms.web.ui.geo.SupportedMapProviderType;
import com.c2point.tms.web.ui.listeners.ProjectReportChangedListener;
import com.c2point.tms.web.ui.listeners.ReportChangedListener;
import com.c2point.tms.web.ui.listeners.ReportItemSelectedListener;
import com.c2point.tms.web.ui.listeners.ReportsListChangedListener;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class DetailedViewComponent extends VerticalLayout implements ReportItemSelectedListener, 
																	 ReportsListChangedListener, 
																	 ReportChangedListener, 
																	 ProjectReportChangedListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( DetailedViewComponent.class.getName());

	// TODO:  Put into settings (per company)
	private static double 		WARNING_DISTANCE = 0.5;  // In kilometers
	
	private ApproveModel model;

	private Label 		header;

	private Label 		projectLabel;
	private Label 		projectName;
	private Label 		mngrLabel;
	private Label 		mngrName;

	private Label 		hoursName;
	private Label 		hoursValue;

	private Label 		numValue;

	private Label 		travelTypeName;
	private Label 		travelTypeValue;
	private Label 		startName;
	private Label 		startValue;
	private Label 		endName;
	private Label 		endValue;
	private Label 		distanceName;
	private Label 		distanceValue;

	private TextArea 	commentsValue;
	
	private Label 		checkInOutHeader;

	private Label separator1;
	private Label separator2;
	private Label glue 			= new Label( "" );

	public DetailedViewComponent( ApproveModel model ) {
		
		this.model = model;
				
		initView();

		model.addChangedListener(( ReportItemSelectedListener )this );
		model.addChangedListener(( ReportsListChangedListener )this );
		model.addChangedListener(( ReportChangedListener )this );
		model.addChangedListener(( ProjectReportChangedListener )this );

	}

	private void initView() {

		setSizeFull();

		this.setMargin( new MarginInfo( false, true, true, true ));
		setSpacing( true );
		
		header 			= new Label();
		header.addStyleName( Runo.LABEL_H2);
		header.setWidthUndefined();

		projectLabel	= new Label( model.getApp().getResourceStr( "general.edit.project" ) + ": ", ContentMode.HTML );
		
		projectName  	= new Label( "", ContentMode.HTML );
		projectName.addStyleName( "h3");

		mngrLabel		= new Label( model.getApp().getResourceStr( "general.edit.owner" ) + ": ", ContentMode.HTML );
		mngrName		= new Label( "", ContentMode.HTML );
		mngrName.addStyleName( "h3");

		hoursName	= new Label( "", ContentMode.HTML );
		hoursName.addStyleName( "h3");
		
		hoursValue	= new Label( "", ContentMode.HTML );
		hoursValue.addStyleName( "h3");

		numValue	= new Label( "", ContentMode.HTML );
		numValue.addStyleName( "h3");
		
		travelTypeName 	= new Label( "", ContentMode.HTML );
		startName		= new Label( "", ContentMode.HTML );
		endName			= new Label( "", ContentMode.HTML );
		distanceName	= new Label( "", ContentMode.HTML );

		travelTypeName.setValue( model.getApp().getResourceStr( "approve.edit.traveltype" ));
		startName.setValue( model.getApp().getResourceStr( "approve.edit.start" ));
		endName.setValue( model.getApp().getResourceStr( "approve.edit.end" ));
		distanceName.setValue( model.getApp().getResourceStr( "approve.edit.length" ));

		travelTypeValue = new Label( "", ContentMode.HTML );
		travelTypeValue.addStyleName( "h3");
		startValue		= new Label( "", ContentMode.HTML );
		startValue.addStyleName( "h3");
		endValue		= new Label( "", ContentMode.HTML );
		endValue.addStyleName( "h3");
		distanceValue	= new Label( "", ContentMode.HTML );
		distanceValue.addStyleName( "h3");
		
		commentsValue = new TextArea();
		commentsValue.setRows( 3 );
		commentsValue.setWidth( "100%" );
		commentsValue.setReadOnly( true );
		
		checkInOutHeader	= new Label( "", ContentMode.HTML );
		checkInOutHeader.addStyleName( Runo.LABEL_H2);
		checkInOutHeader.setWidth( Sizeable.SIZE_UNDEFINED, Unit.PIXELS );
		
		separator1 = new Label( "<hr/>", ContentMode.HTML );
		separator2 = new Label( "<hr/>", ContentMode.HTML );

		separator1.setWidth( "100%" );
		separator2.setWidth( "100%" );

		
		glue = new Label( "" );
		glue.setWidth("100%");
		
	}

	
	@Override
	public void selected( Object obj ) {

		this.removeAllComponents();

		if ( obj instanceof ProjectHolder ) {
			if ( logger.isDebugEnabled()) logger.debug( "Project item has been selected" );

			header.setValue( model.getApp().getResourceStr( "approve.project.label" ));
			addComponent( header );
			setComponentAlignment( header, Alignment.MIDDLE_CENTER );

			ProjectHolder holder = (( ProjectHolder )obj );
			
			showProjectData( holder.getProject());
			showHoursData( holder.getProject(), model.getSelectedUser().getTmsUser(), holder.getDate());
			
		} else if ( obj instanceof TaskReport ) {
			if ( logger.isDebugEnabled()) logger.debug( "TaskReport item has been selected" );

			header.setValue( model.getApp().getResourceStr( "approve.edit.task.caption" ));
			addComponent( header );
			setComponentAlignment( header, Alignment.MIDDLE_CENTER );

			TaskReport report = (( TaskReport )obj );
			
			showProjectData( report.getProject());
			showTaskReportData( report );
			showHoursData( report.getProject(), report.getUser(), report.getDate());

		} else if ( obj instanceof TravelReport ) {
			if ( logger.isDebugEnabled()) logger.debug( "TravelReport item has been selected" );

			header.setValue( model.getApp().getResourceStr( "approve.edit.travel.caption" ));
			addComponent( header );
			setComponentAlignment( header, Alignment.MIDDLE_CENTER );

			TravelReport report = (( TravelReport )obj );
			
			showProjectData( report.getProject());
			showTravelReportData( report );
			showHoursData( report.getProject(), report.getUser(), report.getDate());
			
		} else {
			logger.error( "Something unknown has been selected in Reports list" );
		}

		addComponent( glue );
		setExpandRatio( glue, 1.0f );
		
	}

	private void showProjectData( Project project ) {
		
		GridLayout grid = new GridLayout( 2, 2 );
		grid.setSpacing( true );
		grid.setWidth( "100%" );
		
		projectName.setValue( "<b>" + project.getName() + "</b>" );
		mngrName.setValue( "<b>" + project.getProjectManager().getFirstAndLastNames() + "</b>" );
		
		grid.addComponent( projectLabel, 0, 0 );
		grid.addComponent( projectName, 1, 0 );
		grid.addComponent( mngrLabel, 0, 1 );
		grid.addComponent( mngrName, 1, 1 );
		
		addComponent( grid );

		addComponent( separator1 );
		
	}

	private void showTaskReportData( TaskReport report ) {
		
		GridLayout grid = new GridLayout( 2, 3 );
		grid.setWidth( "100%" );
		
		hoursName.setValue( report.getTask().getName());
		hoursValue.setValue( "<b>" + Float.toString( report.getHours()) + " hours" + "</b>" );
		
		String unitName;
		try {
			unitName = report.getTask().getMeasurementUnit().getName();
			if ( !StringUtils.isBlank( unitName ) || report.getNumValue() > 0 ) {
				numValue.setValue( "<b>" + Float.toString( report.getNumValue()) + " " + unitName + "</b>" );
			}
		} catch ( Exception e ) {
//			unitName = "";
			numValue.setValue( null );
		}
		
		grid.addComponent( hoursName, 0, 0 );
		grid.addComponent( hoursValue, 1, 0 );
		grid.addComponent( numValue, 1, 1 );
		grid.setComponentAlignment( hoursName, Alignment.MIDDLE_LEFT );
		grid.setComponentAlignment( hoursValue, Alignment.MIDDLE_RIGHT );
		grid.setComponentAlignment( numValue, Alignment.MIDDLE_RIGHT );
		
		if ( report.getComment() != null && report.getComment().length() > 0 ) {
			commentsValue.setCaption( model.getApp().getResourceStr( "approve.edit.comment" ));
			commentsValue.setReadOnly( false );
			commentsValue.setValue( report.getComment());
			commentsValue.setReadOnly( true );
			grid.addComponent( commentsValue, 0, 1, 1, 1 );
		}
		
		
		addComponent( grid );

		addComponent( separator2 );
		
	}
	
	private void showTravelReportData( TravelReport report ) {
		
		GridLayout grid = new GridLayout( 2, 4 );
		grid.setSpacing( true );
		//hl.setMargin( true );
		grid.setWidth( "100%" );
		

		if ( report.getTravelType() == TravelType.WORK ) {
			travelTypeValue.setValue( "<b>" + model.getApp().getResourceStr( "approve.edit.traveltype.work" ) + "</b>");

			startValue.setValue( "<b>" + DateUtil.timeToString( report.getStartDate()) + "</b>" );
			endValue.setValue( "<b>" + DateUtil.timeToString( report.getEndDate()) + "</b>" );
		} else if ( report.getTravelType() == TravelType.HOME ) {
			travelTypeValue.setValue( "<b>" + model.getApp().getResourceStr( "approve.edit.traveltype.home" ) + "</b>");
		} else {
			travelTypeValue.setValue( "<b>" + model.getApp().getResourceStr( "approve.edit.traveltype.unknown" ) + "</b>");
		}
		
		distanceValue.setValue( "<b>" + Integer.toString( report.getDistance()) + " " 
									+ model.getApp().getResourceStr( "approve.edit.length.km" ) + "</b>" );

		grid.addComponent( travelTypeName, 0, 0, 0, 0 );
		grid.addComponent( travelTypeValue, 1, 0 );
		
		int row = 1;
		if ( report.getTravelType() == TravelType.WORK ) {
			
			VerticalLayout vl1 = new VerticalLayout(); 
			VerticalLayout vl2 = new VerticalLayout();
			
			vl1.addComponent( startName );
			vl1.addComponent( startValue );
			
			vl2.addComponent( endName );
			vl2.addComponent( endValue );

			grid.addComponent( vl1, 0, row );
			grid.addComponent( vl2, 1, row );
			row++;
			
		}
		
		grid.addComponent( distanceName, 0, row );
		grid.addComponent( distanceValue, 1, row );
		row++;

		if ( report.getRoute() != null && report.getRoute().length() > 0 ) {
			commentsValue.setCaption( model.getApp().getResourceStr( "approve.edit.route" ));
			commentsValue.setReadOnly( false );
			commentsValue.setValue( report.getRoute());
			commentsValue.setReadOnly( true );
			grid.addComponent( commentsValue, 0, row, 1, row );
		}
		
		
		addComponent( grid );

		addComponent( separator2 );
	}
	
	private void showHoursData( Project project, TmsUser user, Date date ) {

		checkInOutHeader.setValue( model.getApp().getResourceStr( "checkin.hours.header" ));
		addComponent( checkInOutHeader );
		setComponentAlignment( checkInOutHeader, Alignment.MIDDLE_CENTER );
		
		// Get CheckInOutt records
		ApproveModel.CheckInOutResults resultsHolder = model.getCheckInOutList( project,  user, date );

		// Show summarized amount of time
		if ( resultsHolder.getResult() == ApproveModel.ReturnedResult.PROJECT_EXISTS ) {
			
			addComponent( new Label( model.getApp().getResourceStr( "checkin.registered.label" ) + ": " 
									+ "<b>" 
									+ DateUtil.getHourMinsString( resultsHolder.getMinutes(),
													model.getApp().getResourceStr( "approve.edit.hours.short" ), 
													model.getApp().getResourceStr( "approve.edit.minutes.short" )
											)
									+ "</b>", 
									ContentMode.HTML ));
			
		} else {
			addComponent( new Label( "<b><font color=\"red\">"
									+ model.getApp().getResourceStr( "checkin.registered.no.msg" )
									+ "</font></b>", 
									ContentMode.HTML ));
		
		}
		
		// Show check-in/-out records
		showCheckInData( resultsHolder );
		
		
	}

	private void showCheckInData( ApproveModel.CheckInOutResults resultsHolder ) {

		GridLayout grid = new GridLayout( 3, resultsHolder.getRecordCount() + 1 );
		grid.setSpacing( true );
		
		Label headerLabel1	= new Label( "<b><u>"
									   + model.getApp().getResourceStr( "approve.edit.checkinout.text.checkin" )
									   + "</u></b>",
									   ContentMode.HTML );
		Label headerLabel2	= new Label( "<b><u>"
									   + model.getApp().getResourceStr( "approve.edit.checkinout.text.checkout" )
									   + "</u></b>",
									   ContentMode.HTML );
		
		grid.addComponent( headerLabel1, 0, 0 );
		grid.addComponent( headerLabel2, 1, 0 );
		grid.setComponentAlignment( headerLabel1, Alignment.MIDDLE_CENTER );
		grid.setComponentAlignment( headerLabel2, Alignment.MIDDLE_CENTER );
		
		int i = 1;
		for ( CheckInOutRecord record : resultsHolder.getRecords()) {
			
			grid.addComponent( getCheckInOutComponent( record, CheckTimeMapComponent.ShowType.IN ), 0, i );
			grid.addComponent( getCheckInOutComponent( record, CheckTimeMapComponent.ShowType.OUT ), 1, i );
			if ( resultsHolder.getResult() == ApproveModel.ReturnedResult.ALL_RECORDS ) {
				grid.addComponent( getProjectNameComponent( record ), 2, i );
			}
			
			i++;
		}
		
		
		addComponent( grid );
		
	
	}
/*
	private Component getCheckInComponent( CheckInOutRecord record ) {
		return getCheckInOutComponent( record, true );
	}

	private Component getCheckOutComponent( CheckInOutRecord record ) {
		return getCheckInOutComponent( record, false );
	}
*/
	private Component getCheckInOutComponent( CheckInOutRecord record, CheckTimeMapComponent.ShowType showType ) {

		final CheckTimeMapComponent mapComp = new CheckTimeMapComponent( record, showType );

/*		
		mapComp.setField( isIn ? record.getDateCheckedIn() : record.getDateCheckedOut(),
						  isIn ? 0 : DateUtil.differenceInDays( record.getDateCheckedIn(), record.getDateCheckedOut()),
						  isIn ? record.getCheckInGeo()    : record.getCheckOutGeo());
		
		mapComp.setupBasePoint( record.getProject().getGeo());
*/		
		
		mapComp.setupWarningDistance( WARNING_DISTANCE );
	
		mapComp.addListener( new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event ) {
				showLocationWindow( mapComp.getCheckInOutRecord());
			}
		});		
		
		
		return mapComp;
	}

	private Component getProjectNameComponent( CheckInOutRecord record ) {
		
		return new Label( record.getProject().getName());
	}

	private void showLocationWindow( CheckInOutRecord record ) {

		MapViewIF mapView = MapFactory.getMapView( SupportedMapProviderType.GOOGLE_PROVIDER );
		
		mapView.setCaption( model.getApp().getResourceStr( "presence.map.window.header" ));
		
		mapView.showMapAll( UI.getCurrent(),  
							record.getProject().getGeo(), model.getApp().getResourceStr( "approve.edit.project.tooltip" ), 
							record.getCheckInGeo(), model.getApp().getResourceStr( "approve.edit.checkinout.text.checkin" ),
							record.getCheckOutGeo(), model.getApp().getResourceStr( "approve.edit.checkinout.text.checkout" )
		);
		
	}

	@Override
	public void wasChanged(ProjectHolder holder) {

		if ( logger.isDebugEnabled()) logger.debug( "ProjectReportWasChanged event received " );
//		selected( holder );
		
	}

	@Override
	public void wasChanged(AbstractReport report) {

		if ( logger.isDebugEnabled()) logger.debug( "AbstractReportWasChanged event received " );

		selected( report );
		
	}

	@Override
	public void listWasChanged() {

		if ( logger.isDebugEnabled()) logger.debug( "ReportsListWasChanged event received " );
		this.removeAllComponents();
		
	}

}
