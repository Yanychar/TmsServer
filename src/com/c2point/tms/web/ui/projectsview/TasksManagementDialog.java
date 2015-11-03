package com.c2point.tms.web.ui.projectsview;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.Task;
import com.c2point.tms.web.ui.listeners.TaskAddedListener;
import com.c2point.tms.web.ui.listeners.TaskChangedListener;
import com.c2point.tms.web.ui.listeners.TaskDeletedListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class TasksManagementDialog extends Window 
				implements TaskAddedListener, TaskChangedListener, TaskDeletedListener {

	private static Logger logger = LogManager.getLogger( TasksManagementDialog.class.getName());

	private static int CODE_WIDTH = 70;
	private static int NAME_WIDTH = 200;
	private static int BUTTON_WIDTH = 25;
	
	private ProjectsModel	model;
    private Project 		project;

    private Table			tasksTable;
    
	private Button 			addButton;

	private Button 			assignButton;
	private Button 			closeButton;
	
	
	public TasksManagementDialog( ProjectsModel model ) {
		super();
		setModal(true);
		
		this.model = model;
		this.project = model.getSelectedProject();
		
		initView();

		dataFromModel();
		
	}
	
	private void initView() {
	
		setHeight( "100%" );
		setWidth( "70ex" );

		VerticalLayout vl = new VerticalLayout(); 
		
		vl.setMargin( true );
		vl.setSpacing( true );
		
		vl.setHeight( "100%" );
		vl.setWidth( "100%" );
		
		this.setCaption( model.getApp().getResourceStr( "projects.tasks.assign.caption" ));
		
		tasksTable = new Table();
		tasksTable.setHeight( "100%" );
		tasksTable.setWidth( "100%" );
		
		tasksTable.setSelectable( true );
		tasksTable.setMultiSelect( true );
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
		tasksTable.setColumnWidth( "name", NAME_WIDTH * 3 );
		tasksTable.setColumnWidth( "buttons", BUTTON_WIDTH * 3 );
		tasksTable.setColumnExpandRatio( "name", 1 );
		
		// Handle selection of item
		tasksTable.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled())
					logger.debug( "Selection in the Task Table was changed. Selection: " + tasksTable.getValue());
				updateAssignButton();				
			}

		});

		model.addChangedListener(( TaskAddedListener )this );
		model.addChangedListener(( TaskChangedListener )this );
		model.addChangedListener(( TaskDeletedListener )this );
		
		addButton = new Button();
		addButton.setIcon( new ThemeResource( "icons/16/add16.png"));
//		addButton.setDescription( model.getApp().getResourceStr( "projects.tasks.assign.tooltip" ));
		addButton.setImmediate( true );
		addButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				// Add the project
				addTask();
			}
        	
        });
		
		assignButton = new Button( model.getApp().getResourceStr( "general.button.assign" ));   
		assignButton.setDescription( model.getApp().getResourceStr( "projects.tasks.assign.tooltip" ));
		assignButton.setImmediate( true );
		assignButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				// Add the project
				assignTasks();
			}
        	
        });
		
		closeButton = new Button( model.getApp().getResourceStr( "general.button.cancel" ));   
//		closeButton.setDescription( model.getApp().getResourceStr( "projects.tasks.assign.tooltip" ));
		closeButton.setImmediate( true );
		closeButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				// Add the project
				cancelAssignment();
			}
        	
        });
	
		HorizontalLayout hl = new HorizontalLayout(); 
		hl.setMargin( true );
		hl.setSpacing( true );

		hl.addComponent( assignButton );
		hl.addComponent( closeButton );

		hl.setHeight( (float) ( closeButton.getHeight() * 1.25), closeButton.getHeightUnits());
		
		hl.setComponentAlignment( assignButton, Alignment.MIDDLE_LEFT );
		hl.setComponentAlignment( closeButton, Alignment.MIDDLE_LEFT );
		
		vl.addComponent( addButton );
		vl.addComponent( tasksTable );
		vl.addComponent( hl );
		
		vl.setExpandRatio( tasksTable, 1.0f );
		
		this.setContent( vl );

		updateAssignButton();				
		
	}

	private void dataFromModel() {

		Organisation org = model.getOrg();
		
		// remove old content
		tasksTable.removeAllItems();
		
        for ( Task task : org.getTasks().values()) {
        	
        	if ( task != null && !task.isDeleted() && project.getProjectTask( task.getCode()) == null ) {
				addOrUpdateItem( task );
			}
		}
		
		tasksTable.setSortContainerPropertyId( "name" );
		tasksTable.sort();

		// Select 1st item if exists
		tasksTable.setValue( tasksTable.firstItemId());

	}
        
	private void addOrUpdateItem( Task task ) {
    		
		Item item = tasksTable.getItem( task );
		
		if ( item == null ) {

			if ( logger.isDebugEnabled()) logger.debug( "Task Item will be added: " + task );
			item = tasksTable.addItem( task );
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Item exists already. Will be modified: " + task );
		}

		item.getItemProperty( "code" ).setValue( task.getCode());
		item.getItemProperty( "name" ).setValue( task.getName());

		// Add Edit button
		final NativeButton editButton = new NativeButton(); // "Edit" );
        editButton.setIcon( new ThemeResource( "icons/16/edit16.png"));
        editButton.setDescription( model.getApp().getResourceStr( "projects.list.edit.tooltip" ));

        editButton.setHeight( Integer.toString( BUTTON_WIDTH ) + "px" );
		editButton.setStyleName("v-nativebutton-deleteButton");
		editButton.addStyleName("v-nativebutton-link");
		editButton.setStyleName(Runo.BUTTON_LINK);

		editButton.setData( task );
		editButton.setImmediate( true );

        editButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				tasksTable.setValue( editButton.getData());
    			
				ModifyTaskDialog dlg = new ModifyTaskDialog( model, ( Task )editButton.getData());
				getUI().addWindow( dlg );

			}

        });


		// Add Delete button
		final NativeButton delButton = new NativeButton();
        delButton.setIcon( new ThemeResource( "icons/16/delete16.png"));
        delButton.setDescription( model.getApp().getResourceStr( "projects.list.delete.tooltip" ));

		delButton.setHeight( Integer.toString( BUTTON_WIDTH ) + "px" );
		delButton.setStyleName("v-nativebutton-deleteButton");
		delButton.addStyleName("v-nativebutton-link");
		delButton.setStyleName(Runo.BUTTON_LINK);

		delButton.setData( task );
		delButton.setImmediate( true );


        delButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Delete button was been pressed" );

				// Select item
				final Object current = delButton.getData();
				tasksTable.setValue( current );
				final Object future = tasksTable.prevItemId( current );

				// Confirm removal
				String template = model.getApp().getResourceStr( "confirm.task.delete" );
				Object[] params = { (( Task )current ).getName() };
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
								if ( deleteTask(( Task ) current )) {
									if ( future != null ) {
										tasksTable.setValue( future );
									} else {
										tasksTable.setValue( tasksTable.firstItemId());
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
    

	private void addTask() {
		
		getUI().addWindow( new ModifyTaskDialog( model ));
	}

	private boolean deleteTask( Task task ) {

		boolean bRes = false;
		
		if ( logger.isDebugEnabled()) logger.debug( "Task shall be deleted!" );

		// Delete from model
		model.deleteTask( task );
		
		bRes = true;

		return bRes;
		
	}
	
	private void assignTasks() {

		Object obj = tasksTable.getValue();
		
		if ( logger.isDebugEnabled()) {
			logger.debug( "Validate what was returned after multiselection. Return object class:" );
			logger.debug( "   Class: " + obj.getClass().getSimpleName());
			logger.debug( "   Is Collection? " + ( obj instanceof Collection< ? > ));
			logger.debug( "   Is Set? " + ( obj instanceof Set< ? > ));
			logger.debug( "   Is Task? " + ( obj instanceof Task ));
		
			
		}
		
		// Traverse all selected Tasks
		try {
			@SuppressWarnings("unchecked")
			Collection< Task > taskClctn = ( Collection< Task > )obj;

			if ( model.assignTasks( taskClctn )) {
				logger.debug( "Tasks were assigned successfully!" );

				this.close();
				
			} else {
				logger.debug( "Failed to assign all Tasks!" );
			}
		} catch ( Exception e ) {
			logger.error( "Collection does not include IDs == Task-s!" );
		
			// TODO
			// Show Error Dialog
			
		}
	}

	private void cancelAssignment() {
		this.close();
	}
	
	@Override
	public void wasAdded( Task task ) {

		// Add item
		addOrUpdateItem( task );
		
		// Select item
		// Sort items
		tasksTable.sort();
		
		// Select new item
		tasksTable.setValue( task );

	}

	@Override
	public void wasDeleted( Task task ) {

		// Update view
		if ( tasksTable.removeItem( task  )) {
			if ( logger.isDebugEnabled()) logger.debug( "Task Item has been removed from Table!" );
			
			String template = model.getApp().getResourceStr( "notify.task.delete" );
			Object[] params = { task.getName() };
			template = MessageFormat.format( template, params );
			
			Notification.show( template );

		}
	}

	@Override
	public void wasChanged(Task task) {

		// Edit item
		addOrUpdateItem( task );
		
		// Select item
		// Sort items
		tasksTable.sort();
		
		// Select new item
		tasksTable.setValue( task );
		
	}

	private void updateAssignButton() {
		Set<?> value = (Set<?>) tasksTable.getValue();
		assignButton.setEnabled( value != null && value.size() > 0 );
	}


}
