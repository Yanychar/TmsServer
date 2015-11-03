package com.c2point.tms.web.ui.projectsview;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.web.ui.listeners.ProjectTaskChangedListener;
import com.c2point.tms.web.ui.listeners.ProjectTaskDeletedListener;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;
import com.vaadin.data.Item;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ProjectTasksComponent extends VerticalLayout implements SelectionChangedListener,
										ProjectTaskChangedListener, ProjectTaskDeletedListener {

	private static Logger logger = LogManager.getLogger( ProjectTasksComponent.class.getName());

	private static int CODE_WIDTH = 80;
	private static int BUTTON_WIDTH = 25;
	
	private ProjectsModel	model;

	private Table 			tasksTable;
	private Button 			assignButton;
	
	public ProjectTasksComponent( ProjectsModel model ) {
		super();
	
//		this.setCaption( model.getApp().getResourceStr( "projects.tasks.view.caption" ));
		this.model = model;

		initView();

//		dataFromModel();
		
	}

	private void initView() {
		
		setMargin( true );
		setSpacing( true );
//		setSizeFull();
		setHeight( "100%" );
//		setWidth( "55ex" );
		setWidth( "100%" );

		tasksTable = new Table();
		tasksTable.setHeight( "100%" );
		tasksTable.setWidth( "100%" );
		
		tasksTable.setSelectable( false );
		tasksTable.setMultiSelect( false );
		tasksTable.setNullSelectionAllowed( true );
		tasksTable.setColumnCollapsingAllowed( false );
		tasksTable.setColumnReorderingAllowed( false );
		tasksTable.setImmediate( true );
		
		tasksTable.addContainerProperty( "code", String.class, null );
		tasksTable.addContainerProperty( "name", String.class, null );
		tasksTable.addContainerProperty( "buttons", HorizontalLayout.class, null );

		tasksTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "general.table.header.code" ),
				model.getApp().getResourceStr( "general.table.header.taskname" ),
				"" 
		}); 
		
		tasksTable.setColumnWidth( "code", CODE_WIDTH );
		tasksTable.setColumnWidth( "buttons", BUTTON_WIDTH * 3 );
		tasksTable.setColumnExpandRatio( "name", 1 );
		
		
		model.addChangedListener(( SelectionChangedListener )this );
		model.addChangedListener(( ProjectTaskChangedListener )this );
		model.addChangedListener(( ProjectTaskDeletedListener )this );
		
		assignButton = new Button();
        assignButton.setIcon( new ThemeResource( "icons/24/assign24.png"));
        assignButton.setDescription( model.getApp().getResourceStr( "projects.tasks.assign.tooltip" ));
        assignButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick( ClickEvent event ) {
				// Add the project
				assignTask();
			}
        	
        });
		
		addComponent( assignButton );
		addComponent( tasksTable );
		
		setExpandRatio( tasksTable, 1.0f );
		
	}

	private void updateUI() {
		assignButton.setEnabled( model.getSelectedProject() != null );
	}

	private boolean assignTask() {
		boolean bRes = true;
		
		
//		TaskSelectionDialog assignWindow = new TaskSelectionDialog( model );
		TasksManagementDialog assignWindow = new TasksManagementDialog( model ); 
		
		getUI().addWindow( assignWindow );
		
		return bRes;
	}

	@Override
	public void selectionChanged() {
		
		logger.debug( "TaskList received Project SelectionChanged event" );
		
		dataFromModel();
		updateUI();
	}

	private void dataFromModel() {

		if ( logger.isDebugEnabled()) logger.debug( "List of Tasks will be reinitialized!" );

		// remove ald content
		tasksTable.removeAllItems();
		// Add items from model
		if ( model != null && model.getSelectedProject() != null ) {
			for ( ProjectTask pTask : model.getSelectedProject().getProjectTasksList()) {
				if ( pTask != null && !pTask.isDeleted()) {
					addOrUpdateItem( pTask ); 
				}
			}
	
			
			// Sort
			tasksTable.setSortContainerPropertyId( "name" );
			tasksTable.sort();
		}
	
	}
	
	private void addOrUpdateItem( ProjectTask pTask ) {
		
		Item item = tasksTable.getItem( pTask );
		
		if ( item == null ) {

			if ( logger.isDebugEnabled()) logger.debug( "Item will be added: " + pTask );
			item = tasksTable.addItem( pTask );
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Item exists already. Will be modified: " + pTask );
		}
		
		item.getItemProperty( "code" ).setValue( pTask.getCodeInProject());
		item.getItemProperty( "name" ).setValue( pTask.getTask().getName());

		// Add Edit button
		final NativeButton editButton = new NativeButton();
		editButton.setIcon( new ThemeResource( "icons/16/edit16.png"));
		editButton.setDescription( model.getApp().getResourceStr( "projects.tasks.edit.tooltip" ));
		
		editButton.setHeight("25px");
		editButton.setStyleName("v-nativebutton-deleteButton");
		editButton.addStyleName("v-nativebutton-link");
		editButton.setStyleName(Runo.BUTTON_LINK);
		
		
		editButton.setData( pTask );
		editButton.setImmediate( true );
		
		
		editButton.addListener( new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

//				tasksTable.setValue( editButton.getData());

				editTask(( ProjectTask )editButton.getData());
				
			}
		});
			
		// Add Delete button
		final NativeButton delButton = new NativeButton();
        delButton.setIcon( new ThemeResource( "icons/16/delete16.png"));
        delButton.setDescription( model.getApp().getResourceStr( "projects.tasks.delete.tooltip" ));
		
		delButton.setHeight("25px");
		delButton.setStyleName("v-nativebutton-deleteButton");
		delButton.addStyleName("v-nativebutton-link");
		delButton.setStyleName(Runo.BUTTON_LINK);
		
		
		delButton.setData( pTask );
		delButton.setImmediate( true );
		
		
        delButton.addListener( new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Delete button was been pressed" );
				// Select item
				final Object current = delButton.getData();
//				tasksTable.setValue( current);
				final Object future = tasksTable.prevItemId( current );
				
				// Confirm removal
				String template = model.getApp().getResourceStr( "confirm.project.task.delete" );
				Object[] params = { (( ProjectTask )current ).getTask().getName() };
				template = MessageFormat.format( template, params );
				
				ConfirmDialog.show( getUI(), 
						model.getApp().getResourceStr( "confirm.general.header" ), 
						template, 
						model.getApp().getResourceStr( "general.button.ok" ), 
						model.getApp().getResourceStr( "general.button.cancel" ), 
						new ConfirmDialog.Listener() {

							@Override
							public void onClose( ConfirmDialog dialog ) {
								if ( dialog.isConfirmed()) {                    
									// Confirmed to continue                    
									if ( deleteButtonPressed(( ProjectTask ) current )) {
										if ( future != null ) {
//											tasksTable.setValue( future );
										} else {
//											tasksTable.setValue( tasksTable.firstItemId());
										}
									}
								
								}										
							}
			
				});
				
			}
		
		});
        
        
        HorizontalLayout buttonsSet = new HorizontalLayout();
        
        buttonsSet.setSpacing( true );
        buttonsSet.addComponent(editButton);
        buttonsSet.addComponent(delButton);
        
        item.getItemProperty( "buttons" ).setValue( buttonsSet );
	
	}
	
	private boolean deleteButtonPressed( ProjectTask pTask ) {
		
		boolean bRes = false;
		if ( logger.isDebugEnabled()) logger.debug( "Task shall be removed from the list of assigned tasks!" );

		// Delete from model
		if ( model.deleteProjectTask( pTask ) != null ) {

			bRes = true;
		}
		
		return bRes;
		
	}

	private boolean editTask( ProjectTask pTask ) {
		
		boolean bRes = true;

		ModifyAssignedTaskDialog editWindow = new ModifyAssignedTaskDialog( model, pTask );
		
		getUI().addWindow( editWindow );
		
		return bRes;
	}

	@Override
	public void wasDeleted( ProjectTask pTask ) {

		// Update view
		if ( tasksTable.removeItem( pTask  )) {
			if ( logger.isDebugEnabled()) logger.debug( "ProjectTask Item has been removed from Table!" );
			
			String template = model.getApp().getResourceStr( "notify.project.task.delete" );
			Object[] params = { pTask.getTask().getName() };
			template = MessageFormat.format( template, params );
			
			Notification.show( template );

		}
		
	}

	@Override
	public void wasChanged( ProjectTask pTask) {
		// Edit item
		addOrUpdateItem( pTask );
		
		// Select item
		// Sort items
		tasksTable.sort();
		
	}

}
