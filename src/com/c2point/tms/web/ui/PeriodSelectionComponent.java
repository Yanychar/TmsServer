package com.c2point.tms.web.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class PeriodSelectionComponent extends Panel {
	
	private static Logger logger = LogManager.getLogger( PeriodSelectionComponent.class.getName());

	protected TmsApplication	app;
	private DateModelIf			dateModel;
	private ClickListener 		printButtonListener;
	
	protected DateField startDF;
	protected DateField endDF;
	
	private Button		bToday;
	private Button		b2days;
	private Button		bThisWeek;
	
	private Button 		printButton;
	private boolean 	printButtonAdded;
	
	
	public interface DateModelIf {
		public Date getStartDate();
		public Date getEndDate();
		public void setStartDate( Date date );
		public void setEndDate( Date date );
		public TmsApplication getApp();
	}   			
	
	public PeriodSelectionComponent( TmsApplication app, DateModelIf dateModel ) {
		this( app, dateModel, null );
		
	}
		
	public PeriodSelectionComponent( TmsApplication app, DateModelIf dateModel, ClickListener printButtonListener ) {
		super();
		
		this.app = app;
		this.dateModel = dateModel;
		this.printButtonListener = printButtonListener;
		this.printButtonAdded = false;
		
		initView();
	}

	private void initView() {
		if ( logger.isDebugEnabled()) logger.debug( "PeriodSelection.InitView started... !" );

//		setCaption( dateModel.getApp().getResourceStr( "time.panel.caption" ));
		
		startDF = new DateField();
		startDF.setLocale( app.getSessionData().getLocale());
		endDF = new DateField();
		endDF.setLocale( app.getSessionData().getLocale());
		
		startDF.setDateFormat((( SimpleDateFormat )DateUtil.getDateFormatYear4digit()).toPattern());
		startDF.setValue( dateModel.getStartDate());
		endDF.setDateFormat((( SimpleDateFormat )DateUtil.getDateFormatYear4digit()).toPattern());
		endDF.setValue( dateModel.getEndDate());
		
		startDF.setResolution( Resolution.DAY );
		endDF.setResolution( Resolution.DAY );
		startDF.setImmediate(true);
		endDF.setImmediate(true);
		
		startDF.addValueChangeListener( new ValueChangeListener() {
			public void valueChange( ValueChangeEvent event ) {
				startDateChanged( event );
			}
		});
		
		endDF.addValueChangeListener( new ValueChangeListener() {
			public void valueChange( ValueChangeEvent event ) {
				endDateChanged( event );
			}
		});

		bToday = new Button( app.getResourceStr( "period.selection.today" ));
		b2days = new Button( app.getResourceStr( "period.selection.2todays" ));
		bThisWeek = new Button( app.getResourceStr( "period.selection.week" ));
		
        printButton = new Button();
    	printButton.setCaption( app.getResourceStr( "general.button.print" ));
    	printButton.addStyleName( Runo.BUTTON_BIG );
    	printButton.addStyleName( Runo.BUTTON_DEFAULT );
		
		
    	bToday.addClickListener( new ClickListener() {
    		@Override
			public void buttonClick(ClickEvent event) { setTodayPeriod();	}}
    	);
    	b2days.addClickListener( new ClickListener() {
    		@Override
			public void buttonClick(ClickEvent event) { set2daysPeriod();	}}
    	);
    	bThisWeek.addClickListener( new ClickListener() {
    		@Override
			public void buttonClick(ClickEvent event) { setThisWeekPeriod();	}}
    	);
		
		HorizontalLayout hl = new HorizontalLayout(); // Just for setting panel and adding
		
		hl.setMargin( true ); // we want a margin
		hl.setSpacing( true ); // and spacing between components		
		
		hl.addComponent( new Label( dateModel.getApp().getResourceStr( "time.panel.from")));
		hl.addComponent( startDF );
		hl.addComponent( new Label( "  " + dateModel.getApp().getResourceStr( "time.panel.to")));
		hl.addComponent( endDF );

		hl.addComponent( bToday );
		hl.addComponent( b2days );
		hl.addComponent( bThisWeek );
		
//		addPrintButton( hl , this.printButtonListener );
		if ( this.printButtonListener != null ) {
			
			if ( this.printButtonAdded ) {
				
				hl.removeComponent( printButton );
				this.printButtonAdded = false;
			}
			
			hl.addComponent( printButton );
			this.printButtonAdded = true;

			printButton.addClickListener( printButtonListener );
			
		}
		
		this.setContent( hl );
	}

	private void addPrintButton( HorizontalLayout hl, ClickListener printButtonListener ) {
		
		if ( printButtonListener != null ) {
			
			if ( this.printButtonAdded ) {
				
				hl.removeComponent( printButton );
				this.printButtonAdded = false;
			}
			
			hl.addComponent( printButton );

			printButton.addClickListener( printButtonListener );
			
			this.printButtonAdded = true;
		}
		
	}
	
	
	public void addPrintButton( ClickListener printButtonListener ) {
		if ( printButtonListener != null && !this.printButtonAdded ) {
		
			(( HorizontalLayout )this.getContent()).addComponent( printButton );
	    	printButton.addClickListener( this.printButtonListener );
			
			this.printButtonAdded = true;
		}
		
	}
	
	protected void startDateChanged( ValueChangeEvent event ) {
		
		// Get the new value and format it to the current locale
		if ( logger.isDebugEnabled()) logger.debug( "Start date has been changed!" );
		Object value = event.getProperty().getValue();        
		if ( value == null || !( value instanceof Date )) {            
			Notification.show( dateModel.getApp().getResourceStr( "time.error.invalidinput" ));        
		} else {
			dateModel.setStartDate(( Date )value );
		}   			
	}
	
	protected void endDateChanged( ValueChangeEvent event ) {
		if ( logger.isDebugEnabled()) logger.debug( "End date has been changed!" );
		// Get the new value and format it to the current locale
		Object value = event.getProperty().getValue();        
		if ( value == null ||
			!( value instanceof Date )
		) {            
			Notification.show( dateModel.getApp().getResourceStr( "time.error.invalidinput" ));        
		} else {
			dateModel.setEndDate(( Date )value );
		}   			
	}

	
	private void setTodayPeriod() {
		startDF.setValue( DateUtil.getStartOfDay());
		endDF.setValue( DateUtil.getEndOfDay());
	}

	private void set2daysPeriod() {
		Calendar cl = Calendar.getInstance();
		cl.add( Calendar.DAY_OF_YEAR, -1 );
		startDF.setValue( DateUtil.getStartOfDay( cl.getTime()));
		endDF.setValue( DateUtil.getEndOfDay());
	}

	private void setThisWeekPeriod() {
		startDF.setValue( DateUtil.getStartOfDay( DateUtil.getFirstDayOfWeek().getTime()));
		endDF.setValue( DateUtil.getEndOfDay());
	}
	
	
}
