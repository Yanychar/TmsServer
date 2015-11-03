package com.c2point.tms.web.ui.stuff;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.web.ui.listeners.UserAddedListener;
import com.c2point.tms.web.ui.listeners.UserChangedListener;
import com.c2point.tms.web.ui.listeners.UserListChangedListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class StuffListComponent extends VerticalLayout implements UserAddedListener, UserListChangedListener, UserChangedListener {

	private static Logger logger = LogManager.getLogger( StuffListComponent.class.getName());

	private static int BUTTON_WIDTH = 25;
	
	private StuffMgmtModel	model;

	private Table 			stuffTable;
	
	private Button 			addButton;

	public StuffListComponent( StuffMgmtModel model ) {

		super();
		this.model = model;

		initView();

		model.addChangedListener(( UserListChangedListener )this );
		model.addChangedListener(( UserAddedListener )this );
		model.addChangedListener(( UserChangedListener )this );
		
	}

	private void initView() {
		
		setSizeFull();

		setMargin( true );
		setSpacing( true );

		stuffTable = new Table();
		
		// Configure table
		stuffTable.setSelectable( true );
		stuffTable.setNullSelectionAllowed( false );
		stuffTable.setMultiSelect( false );
		stuffTable.setColumnCollapsingAllowed( false );
		stuffTable.setColumnReorderingAllowed( false );
		stuffTable.setImmediate( true );
		stuffTable.setSizeFull();
		
		stuffTable.addContainerProperty( "code", String.class, null );
		stuffTable.addContainerProperty( "name", String.class, null );
		stuffTable.addContainerProperty( "buttons", HorizontalLayout.class, null );

		stuffTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "general.table.header.code" ),
				model.getApp().getResourceStr( "general.table.header.fio" ),
				"" 
		}); 
		
		stuffTable.setColumnWidth( "buttons", BUTTON_WIDTH * 3 );		
		
		// Handles the click in the item. NOT USED  YET!
		stuffTable.addItemClickListener( new ItemClickListener() {

            public void itemClick(ItemClickEvent event) {

            	if ( stuffTable.getValue() == event.getItemId() && event.getItemId() != null ) {
                	logger.debug( "Ready to edit TmsUser. Event.ItemId: " + event.getItemId());
                } else {
                	logger.debug( "NOT Ready to edit TmsUser. Event.ItemId: " + event.getItemId());
                }
            }
        });

		// Handle selection of item
		stuffTable.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange( ValueChangeEvent event ) {
		
				model.selectUser(( TmsUser )event.getProperty().getValue());				
			}
		});

        addButton = new Button();
        addButton.setIcon( new ThemeResource( "icons/24/add24.png"));
        addButton.setDescription( model.getApp().getResourceStr( "personnel.list.add.tooltip" ));
        addButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

//				getUI();
				// Add the project
				UI.getCurrent().addWindow( new ModifyStuffDialog( model ));

			}
        	
        });
		
		addComponent( addButton );
		addComponent( stuffTable );
		
		setExpandRatio( stuffTable, 1.0f );
		
	}

	private void dataFromModel() {

		// Store selection for recovery at the end of this method
		TmsUser selectedUser = ( TmsUser )stuffTable.getValue();
		TmsUser newSelectedUser = null;
		boolean selected = ( selectedUser != null );
		
		// remove old content
		stuffTable.removeAllItems();
		
		if ( model.getUserList() != null ) {
			for ( TmsUser user : model.getUserList()) {
				if ( user != null ) {
					addOrUpdateItem( user );
					
					// Check that selection can be restored
					if ( selected && user.getId() == selectedUser.getId()) {
						newSelectedUser = user;
						selected = false;
					}
				}
			}
			
			
		}
		stuffTable.setValue( newSelectedUser );
		
		
		stuffTable.setSortContainerPropertyId( "name" );

		stuffTable.sort();
		

		// Select 1st item if exists
		stuffTable.setValue( stuffTable.firstItemId() );
	
	}
	
	private void addOrUpdateItem( TmsUser user ) {
		
		Item item = stuffTable.getItem( user );
		
		if ( item == null ) {

			if ( logger.isDebugEnabled()) logger.debug( "Item will be added: " + user );
			item = stuffTable.addItem( user );
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Item exists already. Will be modified: " + user );
		}

		item.getItemProperty( "code" ).setValue( user.getCode());
		item.getItemProperty( "name" ).setValue( user.getLastAndFirstNames());

		// Add&Edit buttons
		// If session owner has WRITE access rights
		if ( model.getSecurityContext().isWrite( model.getToShowFilter())) {
			final NativeButton editButton = new NativeButton(); // "Edit" );
	        editButton.setIcon( new ThemeResource( "icons/16/edit16.png"));
	        editButton.setDescription( model.getApp().getResourceStr( "personnel.edit.tooltip" ));
	
	        editButton.setHeight( Integer.toString( BUTTON_WIDTH ) + "px" );
			editButton.setStyleName("v-nativebutton-deleteButton");
			editButton.addStyleName("v-nativebutton-link");
			editButton.setStyleName(Runo.BUTTON_LINK);
	
			editButton.setData( user );
			editButton.setImmediate( true );
	
	        editButton.addClickListener( new ClickListener() {
	
				@Override
				public void buttonClick(ClickEvent event) {
	
					stuffTable.setValue( editButton.getData());
					UI.getCurrent().addWindow( new ModifyStuffDialog( model, ( TmsUser )editButton.getData()));
	
				}
	
	        });
	
	
			// Add Delete button
			final NativeButton delButton = new NativeButton();
	        delButton.setIcon( new ThemeResource( "icons/16/delete16.png"));
	        delButton.setDescription( model.getApp().getResourceStr( "personnel.delete.tooltip" ));
	
			delButton.setHeight( Integer.toString( BUTTON_WIDTH ) + "px" );
			delButton.setStyleName("v-nativebutton-deleteButton");
			delButton.addStyleName("v-nativebutton-link");
			delButton.setStyleName(Runo.BUTTON_LINK);
	
			delButton.setData( user );
			delButton.setImmediate( true );
	
	        delButton.addClickListener( new ClickListener() {
	
				@Override
				public void buttonClick(ClickEvent event) {
					if ( logger.isDebugEnabled()) logger.debug( "Delete button was been pressed" );
					// Select item
					final Object current = delButton.getData();
					stuffTable.setValue( current);
					final Object future = stuffTable.prevItemId( current );
	
					// Confirm removal
					String template = model.getApp().getResourceStr( "confirm.personnel.delete" );
					Object[] params = { (( TmsUser )current ).getFirstAndLastNames() };
					template = MessageFormat.format( template, params );
					
					ConfirmDialog.show( UI.getCurrent(), 
							model.getApp().getResourceStr( "confirm.general.header" ), 
							template, 
							model.getApp().getResourceStr( "general.button.ok" ), 
							model.getApp().getResourceStr( "general.button.cancel" ), 
							new ConfirmDialog.Listener() {
		
								@Override
								public void onClose( ConfirmDialog dialog ) {
									if ( dialog.isConfirmed()) {
										// Confirmed to continue
										if ( deleteButtonPressed(( TmsUser ) current )) {
											if ( future != null ) {
												stuffTable.setValue( future );
											} else {
												stuffTable.setValue( stuffTable.firstItemId());
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
	}
	
	private boolean deleteButtonPressed( TmsUser user ) {
		boolean bRes = false;
		if ( logger.isDebugEnabled()) logger.debug( "TmsUser shall be removed!" );

		// Delete from model
		if ( model.removeUser( user )) {
			// Update view
			if ( stuffTable.removeItem( user )) {
				
				String template = model.getApp().getResourceStr( "notify.personnel.delete" );
				Object[] params = { user.getFirstAndLastNames() };
				template = MessageFormat.format( template, params );
				
				UI.getCurrent().showNotification( template );

				bRes = true;
			}
		}
		return bRes;
	}

	@Override
	public void listWasChanged() {
//		usersTable.removeAllItems();
		dataFromModel();
	}

	@Override
	public void wasAdded( TmsUser user ) {

		// Add Table item
		addOrUpdateItem( user );
		
		// Sort items
		stuffTable.sort();
		
		// Select new item
		stuffTable.setValue( user );

	}

	@Override
	public void wasChanged( TmsUser user ) {

		// Add Table item
		addOrUpdateItem( user );
		
		// Sort items
		stuffTable.sort();
		
		// Select new item
		stuffTable.setValue( user );
		
	}


}
