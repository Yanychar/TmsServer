package com.c2point.tms.web.ui.approveview;


import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.AbstractReport;
import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.ui.approveview.edit.ModifyTaskDialog;
import com.c2point.tms.web.ui.approveview.edit.ModifyTravelDialog;
import com.c2point.tms.web.ui.approveview.model.ApproveModel;
import com.c2point.tms.web.ui.approveview.model.ProjectHolder;
import com.c2point.tms.web.ui.approveview.model.TmsUserHolder;
import com.c2point.tms.web.ui.listeners.ProjectReportChangedListener;
import com.c2point.tms.web.ui.listeners.ReportChangedListener;
import com.c2point.tms.web.ui.listeners.ReportsListChangedListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class ReportsListComponent extends VerticalLayout implements ReportsListChangedListener, ReportChangedListener, ProjectReportChangedListener {

	private static Logger logger = LogManager.getLogger( ReportsListComponent.class.getName());
	
	private ApproveModel 	model;
	
	private Label 			nameLabel;
	private Table 			table;

	private ModifyTaskDialog 	modifyTaskDialog;
	private ModifyTravelDialog 	modifyTravelDialog;
	
	public ReportsListComponent( ApproveModel model ) {
		
		this.model = model;
		
		initView();

		model.addChangedListener(( ReportsListChangedListener )this );
		model.addChangedListener(( ReportChangedListener )this );
		model.addChangedListener(( ProjectReportChangedListener )this );

	}

	private void initView() {

		setMargin( true );
		setSpacing( true );
		this.setSizeFull();
		
		nameLabel = new Label ( "", ContentMode.HTML );
		nameLabel.addStyleName( Runo.LABEL_H1);

		table = getTable();
		
		addComponent( nameLabel );
		addComponent( table );
		
		setExpandRatio( table, 1.0f );
		
	
	}

	private Table getTable() {
		
		final Table table = new Table() {
			@Override
			protected String formatPropertyValue( Object rowId, Object colId, Property<?> property )  {

				if ( property != null && property.getValue() != null && property.getValue() instanceof Date ) {
					 return DateUtil.dateToString(( Date )(property.getValue()));
				}
					
				return super.formatPropertyValue( rowId, colId, property );
			}
		};

		table.setSelectable( true );
		table.setMultiSelect( false );
		table.setNullSelectionAllowed( false );
		table.setColumnCollapsingAllowed( false );
		table.setColumnReorderingAllowed( false );
		table.setColumnHeaderMode( Table.ColumnHeaderMode.HIDDEN );
		table.setSortEnabled( false );
		table.setImmediate( true );
		table.setSizeFull();

		table.addContainerProperty( "date", 	Date.class, 	null );
		table.addContainerProperty( "project", 	String.class, 	null );
		table.addContainerProperty( "type",		Embedded.class, null );
		table.addContainerProperty( "task", 	String.class, 	null );
		table.addContainerProperty( "value", 	String.class, 	null );
		table.addContainerProperty( "status", 	Embedded.class, null );
		table.addContainerProperty( "approvereject", ApproveRejectButtonsComponent.class, null );
		table.addContainerProperty( "data", Object.class, null );

		/*		
		table.addContainerProperty( "hours", Float.class, null );
		table.addContainerProperty( "approvereject", ApproveRejectButtonsComponent.class, "");
		table.addContainerProperty( "userdata", TaskReport.class, "");
*/
/*
		table.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "general.table.header.date" ),
				model.getApp().getResourceStr( "general.table.header.project" ),
				model.getApp().getResourceStr( "general.table.header.type" ), 
				model.getApp().getResourceStr( "general.table.header.task" ), 
				
				model.getApp().getResourceStr( "general.table.header.employee" ), 
				model.getApp().getResourceStr( "general.table.header.hours" ), 
				model.getApp().getResourceStr( "general.table.header.status" ), 
				"", 
				"" 
		});
*/				

		
//		table.setVisibleColumns( new Object [] { "date", "person", "project", "task", "hours", "status", "approvereject" } );
		table.setVisibleColumns( new Object [] { "date", "project", "type", "task", "value", "status", "approvereject" } );
//		table.setColumnWidth( "approvereject",  160 );

		// New Report has been selected
		table.addValueChangeListener( new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "New Report was been selected! Selected value = " + table.getItem(table.getValue()));

				// Close Map and Travel dialogs if opened
				closeDialog( modifyTaskDialog );
				closeDialog( modifyTravelDialog );
				
				Item item = table.getItem( table.getValue());
				if ( item != null ) {
					model.reportItemSelected( item.getItemProperty( "data" ).getValue());
					enableNewApproveButton( item );
				}
			}
		});
			
		// Capture doubleclick
		table.addItemClickListener( new ItemClickListener() {

			@Override
			public void itemClick( ItemClickEvent event ) {
				
				if ( logger.isDebugEnabled()) logger.debug( "Item is clicked. ClicketItemId" );

				if ( event.isDoubleClick()) {
					if ( logger.isDebugEnabled()) logger.debug( "  Double clicked in Item" );
//				}
				
//				if ( event.getItemId() == table.getValue()) {
//					if ( logger.isDebugEnabled()) logger.debug( "Clicked on selected item. Edit it!" );
					
					Item item = table.getItem( table.getValue());
					if ( item != null ) {

						Object obj = item.getItemProperty( "data" ).getValue();

						if ( obj instanceof TaskReport ) {
							if ( logger.isDebugEnabled()) logger.debug( "This is TaskReport. Will be edited" );
						
							editReport(( TaskReport )obj );
						} else if ( obj instanceof TravelReport ) {
							if ( logger.isDebugEnabled()) logger.debug( "This is TravelReport. Will be edited" );
						
							editReport(( TravelReport )obj );
						} else {
							if ( logger.isDebugEnabled()) logger.debug( "Not Report. Will be missed" );
						}
					}
					
				}
				
			}
			
		});
		
/*		
		table.addListener( new ItemClickEvent.ItemClickListener() {

            public void itemClick(ItemClickEvent event) {
                if ( event.isDoubleClick()) {

                	itemDoubleClicked();

                }
            }
        });
*/		
		
		return table;
	}
	
	
	private void dataFromModel() {

		TmsUserHolder holder = model.getSelectedUser();
		
		if ( holder != null  ) {
			
			nameLabel.setValue( "<b>" + holder.getTmsUser().getFirstAndLastNames() + "</b>" );
			
			if ( holder != null && holder.values().size() > 0 ) {

				for ( ProjectHolder pHolder : holder.sortedValues()) {

					if ( pHolder != null ) {
						
						addOneProjectData( pHolder );
						
					}
				}
			}
			
		}
	}
	
	private void addOneProjectData( ProjectHolder pHolder ) {
		
		addProjectItem( pHolder );
		
		// Add Task Reports
		for ( TaskReport report : pHolder.getTaskReports()) {

			addReportItem( report );
		}
	
		// Add Travel Reports
		for ( TravelReport report : pHolder.getTravelReports()) {

			addReportItem( report );
			
		}

	}
	
	private void addProjectItem( ProjectHolder pHolder ) {

		if ( model.getFilter().isReportOk( pHolder )) {
			Item item = table.addItem( pHolder );
			
			if ( logger.isDebugEnabled()) logger.debug( "Item will be added: " + pHolder.getProject().getName());

			updateProjectItem( item, pHolder );			
		}

	}
	
	private void updateProjectItem( Item item, ProjectHolder pHolder ) {

		try {
			item.getItemProperty( "date" ).setValue( pHolder.getDate());
			item.getItemProperty( "project" ).setValue( pHolder.getProject().getName());
			item.getItemProperty( "value" ).setValue( hoursValueToString( pHolder.getHours()));
			item.getItemProperty( "data" ).setValue( pHolder );
			
		} catch ( Exception e ) {
			item.getItemProperty( "date" ).setValue( null );
			item.getItemProperty( "project" ).setValue( "*** ??? ***" );
		}
	}
		
	private void addReportItem( TaskReport report ) {

		if ( model.getFilter().isReportOk( report )) {
		
			Item item = table.addItem( report.getUniqueReportId());
			item.getItemProperty( "approvereject" ).setValue( createApproveRejectButton() );
			
			if ( logger.isDebugEnabled()) logger.debug( "Travel Report will be added: " + report );
			
			updateReportItem( item,  report );
	
			getApproveRejectButton( item ).disable();
		}
	}
		
	private void updateReportItem( Item item,  TaskReport report ) {

		try {
			item.getItemProperty( "type" ).setValue( getTaskIcon());
			item.getItemProperty( "task" ).setValue( report.getProjectTask().getTask().getName());
			item.getItemProperty( "value" ).setValue( hoursValueToString( report.getHours()));
			item.getItemProperty( "status" ).setValue( getStatusIcon( report ));
			
			getApproveRejectButton( item ).updateButtonView( report );

			item.getItemProperty( "data" ).setValue( report );
			
		} catch ( Exception e ) {
			item.getItemProperty( "task" ).setValue( "*** ??? ***" );
		}
	}
		
	private void addReportItem( TravelReport report ) {

		if ( model.getFilter().isReportOk( report )) {
			
			Item item = table.addItem( report.getUniqueReportId());
			item.getItemProperty( "approvereject" ).setValue( createApproveRejectButton());
			
			if ( logger.isDebugEnabled()) logger.debug( "Travel Report will be added: " + report );
		
			
			updateReportItem( item,  report );
			getApproveRejectButton( item ).disable();

		}
	}
		
	private void updateReportItem( Item item,  TravelReport report ) {

		try {
			item.getItemProperty( "type" ).setValue( getTypeIcon( report ));
			item.getItemProperty( "task" ).setValue( report.getRoute());
			item.getItemProperty( "value" ).setValue( distanceValueToString( report.getDistance()));
			item.getItemProperty( "status" ).setValue( getStatusIcon( report ));

			getApproveRejectButton( item ).updateButtonView( report );

			item.getItemProperty( "data" ).setValue( report );
			
		} catch ( Exception e ) {
			item.getItemProperty( "task" ).setValue( "*** ??? ***" );
		}
	}
		
	@Override
	public void wasChanged( AbstractReport report ) {
		if ( logger.isDebugEnabled()) logger.debug( "Event ReportWasChanged has been received from model: " + report );

		Item item = table.getItem( report.getUniqueReportId());
		if ( item != null ) {
			
			if ( report instanceof TaskReport ) {
				updateReportItem( item, ( TaskReport )report );
			} else if ( report instanceof TravelReport ) {
				updateReportItem( item, ( TravelReport )report );
			} 
		}
		
	}

	@Override
	public void wasChanged( ProjectHolder ph ) {
		if ( logger.isDebugEnabled()) logger.debug( "Event ProjectHolder has been received from model: " + ph );

		Item item = table.getItem( ph );
		if ( item != null ) {
			
			updateProjectItem( item, ph );
		}
		
	}

	@Override
	public void listWasChanged() {
		if ( logger.isDebugEnabled()) logger.debug( "Event ReportListWasChanged has been received from model" );
		
		removeAllItems();
		dataFromModel();
	}
	
	private void removeAllItems() {

//		VerticalLayout vl = ( VerticalLayout )this.getContent();
//		vl.removeAllComponents();
		table.removeAllItems();
		
	}

	private String hoursValueToString( float hours ) {
		
		return Float.toString( hours ) + " h";
		
	}

	private String distanceValueToString( int distance ) {
		
		return Integer.toString( distance ) + " km";
		
	}

	private Embedded getTypeIcon( TravelReport report ) {
		String iconName = "";
		String tooltipStr = "";
		if ( report.getTravelType() == TravelType.HOME ) {
			iconName = "icons/16/house16.png";
			tooltipStr = model.getApp().getResourceStr( "approve.home.trip.report.tooltip" );
		} else if ( report.getTravelType() == TravelType.WORK ) {
			iconName = "icons/16/van16.png";
			tooltipStr = model.getApp().getResourceStr( "approve.work.trip.report.tooltip" );
		}
			
		Embedded icon = new Embedded( "", new ThemeResource( iconName ));
		icon.setDescription( tooltipStr );
		
		return icon;
	}
	
	private Embedded getTaskIcon() {
		
		String iconName = "icons/16/helmet16.png";
		String tooltipStr = model.getApp().getResourceStr( "approve.task.report.tooltip" );
			
		Embedded icon = new Embedded( "", new ThemeResource( iconName ));
		icon.setDescription( tooltipStr );
		
		return icon;
	}
	
	private Embedded getStatusIcon( AbstractReport report ) {
		String iconName = "";
		String tooltipStr = "";
		switch ( report.getApprovalFlagType()) {
			case TO_CHECK: {
				iconName = "icons/16/question16.png";
				tooltipStr = model.getApp().getResourceStr( "approve.list.status.tocheck.tooltip" );
				break;
			}
			case REJECTED: {
				iconName = "icons/16/delete16.png";
				tooltipStr = model.getApp().getResourceStr( "approve.list.status.rejected.tooltip" ); 
				break;
			}
			case APPROVED: {
				iconName = "icons/16/selected16.png";
				tooltipStr = model.getApp().getResourceStr( "approve.list.status.approved.tooltip" );
				break;
			}
			case PROCESSED: {
				iconName = "icons/16/paid16.png";
				tooltipStr = model.getApp().getResourceStr( "approve.list.status.processed.tooltip" );
				break;
			}
			default: {
				break;
			}
		}
			
		Embedded icon = new Embedded( "", new ThemeResource( iconName ));
		icon.setDescription( tooltipStr );
		
		return icon;
	}

	private ApproveRejectButtonsComponent createApproveRejectButton() {

		final ApproveRejectButtonsComponent arButton = new ApproveRejectButtonsComponent( model.getApp());
	
		arButton.addApproveListener( new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Approve button pressed" );
	
				approve_reject_ButtonClicked( arButton.getReport(), ApprovalFlagType.APPROVED );
				
	
			}
		});
		arButton.addRejectListener( new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Reject button pressed" );
	
				approve_reject_ButtonClicked( arButton.getReport(), ApprovalFlagType.REJECTED );
	
			}
		});

		return arButton; 
	}

	private ApproveRejectButtonsComponent getApproveRejectButton( Item item ) {
		try {
			return (( ApproveRejectButtonsComponent )item.getItemProperty( "approvereject" ).getValue());
		} catch ( Exception e ) {
			logger.error( "Wrong data returned as 'approvereject' property. Should be 'ApproveRejectButtonsComponent' or NULL" );
		}
		
		return null;
	}
	
	private void approve_reject_ButtonClicked( Object obj, ApprovalFlagType type ) {

		if ( obj != null) {
			
			AbstractReport report = ( AbstractReport )obj;
			
			logger.debug( "Status of report was: " + report.getApprovalFlagType());
			
			AbstractReport newReport = model.changeReportState( report,  type );
		
			// TODO  Set referenced report to newReport 
			
			logger.debug( "Status of report now: " + report.getApprovalFlagType());
			
		} else {
			logger.error( "Failed to approve report.Reason unknown" );
			Notification.show(
					model.getApp().getResourceStr( "general.errors.update.header" ),
					model.getApp().getResourceStr( type == ApprovalFlagType.APPROVED ? "approve.errors.approve.body" : "approve.errors.reject.body" ),
					Notification.Type.ERROR_MESSAGE );
			
		}
		
	}

	private ApproveRejectButtonsComponent currentlySelected;
	private void enableNewApproveButton( Item item ) { 
		
		if ( currentlySelected != null ) {
			currentlySelected.disable();
		}

		currentlySelected = getApproveRejectButton( item );
		
		if ( currentlySelected != null ) {
			currentlySelected.enable();
		}
	}

	private void editReport( TaskReport report ) {
		
		closeDialog( modifyTaskDialog );
		
		modifyTaskDialog = new ModifyTaskDialog( model, report );

		UI.getCurrent().addWindow( modifyTaskDialog );

	}

	private void editReport( TravelReport report ) {
		
/*
*/
  		closeDialog( modifyTravelDialog );
 		
		modifyTravelDialog = new ModifyTravelDialog( model, report );

		UI.getCurrent().addWindow( modifyTravelDialog );

	}

	private void closeDialog( Window dlg ) {
		if ( dlg != null ) {
			try {
				dlg.close();
				dlg = null;
			} catch ( Exception e ) {
				
			}
		}
		
	}
}
