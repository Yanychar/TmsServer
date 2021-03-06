package com.c2point.tms.web.ui.reportsmgmt.timereports.view;

import java.util.Date;

import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class DateSelectionComponent extends VerticalLayout {
	
	private InlineDateField dateTime;
	
	private TmsApplication app;
	
	public DateSelectionComponent( TmsApplication app ) {
		this.app = app;
		init();
	}
	
	private void init() {

		dateTime = new InlineDateField();
		dateTime.setResolution( Resolution.DAY );        
		dateTime.setLocale( app.getSessionData().getLocale());
		dateTime.setImmediate( true );
		dateTime.setSizeUndefined();
		
		Button todayButton = new Button( app.getResourceStr( "general.button.today" ));
		todayButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				dateTime.setValue( DateUtil.getDate());
			}
			
		});
		
		addComponent( dateTime );
		addComponent( todayButton );
		setSizeUndefined();
		setComponentAlignment(todayButton ,  Alignment.BOTTOM_CENTER );
		
		this.setSizeUndefined();
	
	}
	
	public void addListener( ValueChangeListener listener ) {
		dateTime.addValueChangeListener( listener );
	}
	
	public void setEditableDateRange( Date startDate, Date endDate ) {

		dateTime.setRangeStart( startDate );  // Allow to select 'allowedDays' days
		dateTime.setRangeEnd( endDate );
		
	}
	
}
