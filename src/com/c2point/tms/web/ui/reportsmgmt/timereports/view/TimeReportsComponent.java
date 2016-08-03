package com.c2point.tms.web.ui.reportsmgmt.timereports.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.web.ui.DaysOfWeek;
import com.c2point.tms.web.ui.approveview.edit.ModifyTaskDialog;
import com.c2point.tms.web.ui.listeners.TaskListChangedListener;
import com.c2point.tms.web.ui.listeners.WeekItemChangedListener;
import com.c2point.tms.web.ui.listeners.WeekItemAddedListener;
import com.c2point.tms.web.ui.reportsmgmt.timereports.model.ReportsManagementModel;
import com.c2point.tms.web.ui.reportsmgmt.timereports.model.WeekItem;
import com.c2point.tms.web.ui.reportsmgmt.timereports.model.WeekProjectTaskId;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TimeReportsComponent extends Table
									implements WeekItemAddedListener, WeekItemChangedListener, TaskListChangedListener {
	private static Logger logger = LogManager.getLogger( TimeReportsComponent.class.getName());

	private ReportsManagementModel 	model;

	public TimeReportsComponent() {
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


		addItemClickListener( new ItemClickListener() {

            public void itemClick(ItemClickEvent event) {
				logger.debug( "Click event: " + event.getPropertyId().getClass().getName() + ": " + event.getPropertyId());

                if ( event.isDoubleClick()) {
                	if ( event.getPropertyId() instanceof DaysOfWeek ) {

                		WeekItem item = model.getTimeReportsModel().getWeekItem(( WeekProjectTaskId ) event.getItemId());
                		if ( item != null ) {
                    		TaskReport report = null;
        					Date date = null;
                			DaysOfWeek dayOfWeek = ( DaysOfWeek ) event.getPropertyId();
							report = item.getReport( dayOfWeek );
                    		if ( report == null ) {

                    			// No report in this cell. New one will be created and added
            					report = new TaskReport();

            					try {
            						if ( logger.isDebugEnabled()) logger.debug( "Date conversion: " + dayOfWeek + " ==>> " + dayOfWeek.convertToCalendarDOW() );
//									date = model.getDateModel().getDateOfWeekDay( dayOfWeek.convertToCalendarDOW());
									date = model.getDateModel().getDateOfWeekDay( dayOfWeek );

									report.initReport( date, model.getSessionOwner(), item.getProjectTask(), 0, "" );
	                    			if ( logger.isDebugEnabled()) logger.debug( "New TaskReport has been created: " + report );

	                    			item.putReport( report );

            					} catch (IndexOutOfBoundsException e) {
									logger.error( e );
									return;
								}
                    		}

                    		editReport( report );

                		} else {
                			logger.error( "WeekItem is not found in Table. Wrong it's must be" );
                		}

                	}
                }


            }
        });


		addContainerProperty( "code", 	String.class, null );
		addContainerProperty( "project&task", 	String.class, null );
		addContainerProperty( DaysOfWeek.MON, Float.class, null );
		addContainerProperty( DaysOfWeek.TUE, Float.class, null );
		addContainerProperty( DaysOfWeek.WED, Float.class, null );
		addContainerProperty( DaysOfWeek.THU, Float.class, null );
		addContainerProperty( DaysOfWeek.FRI, Float.class, null );
		addContainerProperty( DaysOfWeek.SAT, Float.class, null );
		addContainerProperty( DaysOfWeek.SUN, Float.class, null );


		setColumnAlignments(new Table.Align[] { Align.LEFT, Align.LEFT,
												Align.RIGHT, Align.RIGHT, 
												Align.RIGHT, Align.RIGHT,
												Align.RIGHT, Align.RIGHT, 
												Align.RIGHT
												 });

	}

	public boolean setModel( ReportsManagementModel model ) {
		boolean res = false;

		this.model = model;

		model.getTimeReportsModel().addListener(( WeekItemAddedListener ) this );
		model.getTimeReportsModel().addListener(( WeekItemChangedListener ) this );
		model.getTimeReportsModel().addListener(( TaskListChangedListener ) this );

		listWasChanged();

		res = true;

		return res;
	}

	private void setHeaders() {
		String [] headers = new String[ 2 + 7 ];

	    SimpleDateFormat dateFormatter = new SimpleDateFormat( "EEE dd.MM", model.getApp().getSessionData().getLocale());
		Calendar cal = (Calendar) model.getDateModel().getStartOfWeek().clone();

		headers[ 0 ] = model.getApp().getResourceStr( "general.table.header.code" );
		headers[ 1 ] = model.getApp().getResourceStr( "general.table.header.project.task" );
		for ( int i = 2; i < 9; i++  ) {
			headers[ i ] = dateFormatter.format( cal.getTime());
			cal.add( Calendar.DAY_OF_WEEK, 1 );
		}
		setColumnHeaders( headers );
	}

	private Item addOrUpdateTaskReportToModel( WeekItem weekItem ) {

		if ( weekItem != null ) {
			Item item = getItem( weekItem.getId());
			if ( item == null ) {
				item = addItem( weekItem.getId());
			}

			return updateItem( item, weekItem );
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Item updateItem( Item item, WeekItem weekItem ) {

		item.getItemProperty( "code" ).setValue( weekItem.getProjectTask().getCodeInProject());
		item.getItemProperty( "project&task" ).setValue( weekItem.getProjectTask().getProject().getName() + " - " + weekItem.getProjectTask().getTask().getName());
		item.getItemProperty( DaysOfWeek.MON ).setValue( getDayHours( weekItem, DaysOfWeek.MON ));
		item.getItemProperty( DaysOfWeek.TUE ).setValue( getDayHours( weekItem, DaysOfWeek.TUE ));
		item.getItemProperty( DaysOfWeek.WED ).setValue( getDayHours( weekItem, DaysOfWeek.WED ));
		item.getItemProperty( DaysOfWeek.THU ).setValue( getDayHours( weekItem, DaysOfWeek.THU ));
		item.getItemProperty( DaysOfWeek.FRI ).setValue( getDayHours( weekItem, DaysOfWeek.FRI ));
		item.getItemProperty( DaysOfWeek.SAT ).setValue( getDayHours( weekItem, DaysOfWeek.SAT ));
		item.getItemProperty( DaysOfWeek.SUN ).setValue( getDayHours( weekItem, DaysOfWeek.SUN ));

		return item;
	}

	private float getDayHours( WeekItem weekItem, DaysOfWeek day ) {
		TaskReport report = weekItem.getReport( day );
		if ( report != null ) {
			return report.getHours();
		}

		return 0;
	}

	private boolean editReport( TaskReport report ) {
		boolean bRes = true;
		
		boolean isEditable = model.isDateEditable( report.getDate());
		boolean isManager = false;
		logger.debug( "Is report editable? " + isEditable );

		ModifyTaskDialog editWindow = new ModifyTaskDialog( model.getTimeReportsModel(), report, isManager || isEditable );

		getUI().addWindow( editWindow );


		return bRes;
	}

	@Override
	public void wasAdded( WeekItem weekItem ) {
		Item item = addOrUpdateTaskReportToModel( weekItem );
		setValue( item );
		if ( logger.isDebugEnabled()) logger.debug( "Table received 'added' event. Shall add and select (opt) : " + weekItem );
	}

	@Override
	public void wasChanged( WeekItem weekItem ) {

		Item item = addOrUpdateTaskReportToModel( weekItem );
		setValue( item );
		if ( logger.isDebugEnabled()) logger.debug( "Table received 'edit' event. Shall update and select (opt) : " + weekItem );

	}

	@Override
	public void listWasChanged() {

//		dateFromModel();

		removeAllItems();
		setHeaders();

		if ( model.getTimeReportsModel() != null  ) {
			for ( WeekItem record : model.getTimeReportsModel().values()) {
				addOrUpdateTaskReportToModel( record );
			}
		}

		// Sort
		setSortContainerPropertyId( "date" );
		sort();

	}


}
