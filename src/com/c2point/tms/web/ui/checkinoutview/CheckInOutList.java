package com.c2point.tms.web.ui.checkinoutview;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.ui.checkinoutview.model.CheckInOutModel;
import com.c2point.tms.web.ui.geo.MapFactory;
import com.c2point.tms.web.ui.geo.MapViewIF;
import com.c2point.tms.web.ui.geo.SupportedMapProviderType;
import com.c2point.tms.web.ui.listeners.CheckInOutListChangedListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class CheckInOutList extends Table implements CheckInOutListChangedListener {

	private static Logger logger = LogManager.getLogger( CheckInOutList.class.getName());
	
	private static double 		WARNING_DISTANCE = 0.5;  // In kilometers
	
	private CheckInOutModel 	model;
	
	public CheckInOutList( CheckInOutModel model ) {
		super();
		this.model = model;

		initView();

		dateFromModel();
	}

	private void initView() {
		// Configure table
		setSelectable( true );
		setMultiSelect( false );
		setNullSelectionAllowed( false );
        setColumnCollapsingAllowed( false );
        setColumnReorderingAllowed( false );
		setImmediate( true );
		setSizeFull();

		addContainerProperty( "date", Date.class, null );
		addContainerProperty( "project", String.class, null );
		addContainerProperty( "checkin", TimeMapComponent.class, null );
		addContainerProperty( "checkout", TimeMapComponent.class, null );
		addContainerProperty( "total", String.class, null );
		
		setColumnHeaders( new String []  { 
				model.getApp().getResourceStr( "general.table.header.date" ), 
				model.getApp().getResourceStr( "general.table.header.project" ), 
				model.getApp().getResourceStr( "general.table.header.checkin" ), 
				model.getApp().getResourceStr( "general.table.header.checkout" ), 
				model.getApp().getResourceStr( "general.table.header.hours" ) 
		});
		
//		addGeneratedColumn( "total", new TotalColumnGenerator());		
		
		addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				
				CheckInOutRecord record = ( CheckInOutRecord ) CheckInOutList.this.getValue(); 
				CheckInOutList.this.model.selectRecord( record );
				
			}
			
		});
		
		
	}
	
	private void dateFromModel() {

		// remove ald content
		this.removeAllItems();
		
		if ( model.getCheckInOutList() != null ) {
			for ( CheckInOutRecord record : model.getCheckInOutList()) {
				if ( record != null ) {
					addRecordToView( record );
				}
			}
		}
		
		setSortContainerPropertyId( "date" );
		sort();

	}


	@Override
	protected String formatPropertyValue( Object rowId, Object colId, Property<?> property )  {

		if ( property != null && property.getValue() != null && property.getValue() instanceof Date ) {
			 return DateUtil.dateToString(( Date )(property.getValue()));
		}
			
		return super.formatPropertyValue( rowId, colId, property );
	}

	@SuppressWarnings("unchecked")
	private void addRecordToView( CheckInOutRecord record ) {
		final Item item = addItem( record );
		final TimeMapComponent mapCompIn, mapCompOut;
		
		item.getItemProperty( "date" ).setValue( 
				record.getDateCheckedIn());
		
		item.getItemProperty( "project" ).setValue( record.getProject().getName());	
		
		TimeMapStub tmStub = new TimeMapStub( record ); 
		tmStub.setupWarningDistance( WARNING_DISTANCE );
		
		mapCompIn = tmStub.getMapComponent( TimeMapStub.ShowType.IN, true );
		mapCompIn.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event ) {
				showLocationWindow( mapCompIn.getCheckInOutRecord(), true );
			}
		});		
		item.getItemProperty( "checkin" ).setValue( mapCompIn ); 

		mapCompOut = tmStub.getMapComponent( TimeMapStub.ShowType.OUT, true );
		mapCompOut.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event ) {
				showLocationWindow( mapCompOut.getCheckInOutRecord(), false );
			}
		});		
		item.getItemProperty( "checkout" ).setValue( mapCompOut ); 

		
		item.getItemProperty( "total" ).setValue( model.getTotalHours( record )); 
		
		if ( logger.isDebugEnabled()) logger.debug( "      Item was added to the table: " + item );

		
	}

	@Override
	public void listWasChanged() {
		if ( logger.isDebugEnabled()) logger.debug( "Event ReportListWasChanged has been received from model" );
		
		dateFromModel();		
	}

	private void showLocationWindow( CheckInOutRecord record, boolean isIn ) {

		MapViewIF mapView = MapFactory.getMapView( SupportedMapProviderType.GOOGLE_PROVIDER );
		
		mapView.setCaption( model.getApp().getResourceStr( "presence.map.window.header" ));
		
		if ( isIn )
			mapView.showMapIn( UI.getCurrent(),  
								record.getProject().getGeo(), model.getApp().getResourceStr( "approve.edit.project.tooltip" ), 
								record.getCheckInGeo(), model.getApp().getResourceStr( "approve.edit.checkinout.text.checkin" )
			);
		else
			mapView.showMapOut( UI.getCurrent(),  
					record.getProject().getGeo(), model.getApp().getResourceStr( "approve.edit.project.tooltip" ), 
					record.getCheckOutGeo(), model.getApp().getResourceStr( "approve.edit.checkinout.text.checkout" )
			);
		
	}

}

