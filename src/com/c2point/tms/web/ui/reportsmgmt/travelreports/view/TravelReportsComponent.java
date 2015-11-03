package com.c2point.tms.web.ui.reportsmgmt.travelreports.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.web.ui.approveview.edit.ModifyTravelDialog;
import com.c2point.tms.web.ui.listeners.ReportAddedListener;
import com.c2point.tms.web.ui.listeners.ReportChangedListener;
import com.c2point.tms.web.ui.listeners.ReportDeletedListener;
import com.c2point.tms.web.ui.listeners.TravelListChangedListener;
import com.c2point.tms.web.ui.reportsmgmt.travelreports.model.ReportsManagementModel;
import com.c2point.tms.web.util.ToStringConverter;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TravelReportsComponent extends Table 
									implements ReportAddedListener, ReportDeletedListener, ReportChangedListener, TravelListChangedListener {
	private static Logger logger = LogManager.getLogger( TravelReportsComponent.class.getName());

	private ReportsManagementModel 	model;
	
	
	public TravelReportsComponent() {
		super();

		initView();

	}

	private void initView() {
		
		setSelectable( true );
		setMultiSelect( false );
		setNullSelectionAllowed( false );
		setColumnCollapsingAllowed( false );
		setColumnReorderingAllowed( false );
		setImmediate( true );
		setSizeFull();


		this.addItemClickListener( new ItemClickListener() {

            public void itemClick(ItemClickEvent event) {
				logger.debug( "Click event: " + event.getPropertyId().getClass().getName() + ": " + event.getPropertyId());
						
                if ( event.isDoubleClick()) {
            		TravelReport report = model.getTravelReportsModel().getReport(( String ) event.getItemId());
               		if ( report != null ) {
                		editReport( report );
            			
            		} else {
            			logger.error( "TravelReport is not found in Table. Wrong it's must be" );
            		}
                		
                }
				
            }
        });


		addContainerProperty( "code", 	String.class, null );
		addContainerProperty( "project", 	String.class, null );
		addContainerProperty( "type", 	TravelType.class, null );
		addContainerProperty( "distance", Integer.class, null );
		addContainerProperty( "route", 	String.class, null );
		addContainerProperty( "status", 	String.class, null );
		
		setColumnAlignments(new Table.Align[] { Align.LEFT, Align.LEFT,
												Align.RIGHT, Align.RIGHT, 
												Align.RIGHT, Align.RIGHT 
												 });		

	}

	public boolean setModel( ReportsManagementModel model ) {
		boolean res = false;
		
		this.model = model;

		model.getTravelReportsModel().addListener(( ReportAddedListener ) this );
		model.getTravelReportsModel().addListener(( ReportDeletedListener ) this );
		model.getTravelReportsModel().addListener(( ReportChangedListener ) this );
		model.getTravelReportsModel().addListener(( TravelListChangedListener ) this );

		listWasChanged();
		
		res = true;
		
		return res;
	}

	private void setHeaders() {
		String [] headers = new String[ 6 ]; 
		
		logger.debug( "Table header shall be changed!" );	            	
		
		headers[ 0 ] = model.getApp().getResourceStr( "general.table.header.code" );
		headers[ 1 ] = model.getApp().getResourceStr( "general.table.header.project" );
		headers[ 2 ] = model.getApp().getResourceStr( "general.table.header.type" );
		headers[ 3 ] = model.getApp().getResourceStr( "general.table.header.distance" ); 
		headers[ 4 ] = model.getApp().getResourceStr( "general.table.header.route" );
		headers[ 5 ] = model.getApp().getResourceStr( "general.table.header.status" );
		
		setColumnHeaders( headers );
	}

	private Item addOrUpdateTravelReportToModel( TravelReport report ) {
		
		if ( report != null ) {
			Item item = getItem( report.getUniqueReportId());
			if ( item == null ) {
				item = addItem( report.getUniqueReportId());
			}
			
			return updateItem( item, report );
		}
		
		return null;
	}

	private Item updateItem( Item item, TravelReport report ) {
		
		item.getItemProperty( "code" ).setValue( report.getProject().getCode());
		item.getItemProperty( "project" ).setValue( report.getProject().getName());
		item.getItemProperty( "type" ).setValue( report.getTravelType());
		item.getItemProperty( "distance" ).setValue( report.getDistance());
		item.getItemProperty( "route" ).setValue( report.getRoute());
		item.getItemProperty( "status" ).setValue( ToStringConverter.convertToString( model.getApp(), report.getApprovalFlagType()).toUpperCase()); 
				
		return item;
	}

	private boolean editReport( TravelReport report ) {
		boolean bRes = true;

		ModifyTravelDialog editWindow = new ModifyTravelDialog( model.getTravelReportsModel(), report, false );

		getUI().addWindow( editWindow );


		return bRes;
	}

	@Override
	public void wasAdded( AbstractReport report ) {
		
		Item item = addOrUpdateTravelReportToModel(( TravelReport ) report );
		setValue( item );
		if ( logger.isDebugEnabled()) logger.debug( "Table received 'added' event. Shall add and select (opt) : " + report );
		
	}

	@Override
	public void wasDeleted(AbstractReport report) {
		removeItem( report.getUniqueReportId());
	}

	@Override
	public void wasChanged( AbstractReport report ) {
		
		Item item = addOrUpdateTravelReportToModel(( TravelReport ) report );
		setValue( item );
		if ( logger.isDebugEnabled()) logger.debug( "Table received 'edit' event. Shall update and select (opt) : " + report );
	
	}

	@Override
	public void listWasChanged() {

//		dateFromModel();

		removeAllItems();
		setHeaders();
		
		if ( model.getTravelReportsModel() != null  ) {
			for ( TravelReport report : model.getTravelReportsModel().values()) {
				addOrUpdateTravelReportToModel( report );
			}
		}

		// Sort
		setSortContainerPropertyId( "code" );
		sort();
		
	}

	
}
