package com.c2point.tms.web.ui.reportsmgmt.travelreports.view;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.data.Property.ValueChangeEvent;
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
	
	private static Logger logger = LogManager.getLogger( DateSelectionComponent.class.getName());
	
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
		
//		dateTime.setRangeEnd( DateUtil.getDate());
		Date td = DateUtil.getDate();
		dateTime.setRangeStart( new Date( td.getTime() - 1000 * 60 * 60 * 24 * 14 ));  // Minus 14 days
		dateTime.setRangeEnd( td );
		
		Button todayButton = new Button( app.getResourceStr( "general.button.today" ));
//		todayButton.setWidth( "100%" );
		todayButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				dateTime.setValue( DateUtil.getDate());
			}
			
		});

		if ( logger.isDebugEnabled()) {
			
			addListener( new ValueChangeListener () {

				@Override
				public void valueChange(ValueChangeEvent event) {

					final String valueString = String.valueOf(event.getProperty()                        .getValue());


					logger.debug( "Date selected: " +  valueString  + ". Max data to select:" + DateUtil.dateToString(dateTime.getRangeEnd()));
				}
				
			});
		}
		
		
		addComponent( dateTime );
		addComponent( todayButton );
		setSizeUndefined();
		setComponentAlignment(todayButton ,  Alignment.BOTTOM_CENTER );
		
		this.setSizeUndefined();
	
	}
	
	public void addListener( ValueChangeListener listener ) {
		dateTime.addValueChangeListener( listener );
		
	}
	
}
