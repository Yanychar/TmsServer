package com.c2point.tms.web.ui.checkinoutview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.web.ui.checkinoutview.model.CheckInOutModel;
import com.c2point.tms.web.ui.listeners.UserListChangedListener;
import com.c2point.tms.web.ui.ListWithSearchComponent;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Table;

public class UserListComponent extends ListWithSearchComponent implements UserListChangedListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( UserListComponent.class.getName());

	private CheckInOutModel	model;

	private Table		usersTable;
	
	public UserListComponent( CheckInOutModel model ) {
		super();
		this.model = model;

		initView();

		model.addChangedListener( this );
		
	}

	private void initView() {

		setSizeFull();

		setMargin( true );
//		setSpacing( true );

		usersTable = new Table();
		
		setContainerForSearch( usersTable );
		
		// Configure table
		usersTable.setSelectable( true );
		usersTable.setNullSelectionAllowed(  false );
		usersTable.setMultiSelect( false );
		usersTable.setColumnCollapsingAllowed( false );
		usersTable.setColumnReorderingAllowed( false );
		usersTable.setImmediate( true );
		usersTable.setSizeFull();
		
		usersTable.addContainerProperty( "code", String.class, null );
		usersTable.addContainerProperty( "fio", String.class, null );

		usersTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "general.table.header.code" ), 
				model.getApp().getResourceStr( "general.table.header.employee" ), 
		
		});

	
		// New User has been selected. Send event to model
		usersTable.addValueChangeListener( new  ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Property.valueChanged!" );
				
				TmsUser user = ( TmsUser )usersTable.getValue();

				model.selectUser( user );
			}
		});

		this.addComponent( getSearchBar());
		this.addComponent( usersTable );
		
		this.setExpandRatio( usersTable, 1.0f );
		
	}

	private void dataFromModel() {

		// Store selection for recovery at the end of this method
		TmsUser selectedUser = ( TmsUser )usersTable.getValue();
		TmsUser newSelectedUser = null;
		boolean selected = ( selectedUser != null );
		
		// remove old content
		usersTable.removeAllItems();
		
		if ( model.getUsersList() != null ) {
			for ( TmsUser user : model.getUsersList()) {
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
		
		usersTable.setSortContainerPropertyId( "fio" );

		usersTable.sort();
		
		if ( newSelectedUser != null ) {
			usersTable.setValue( newSelectedUser );
		} else {
			usersTable.setValue( usersTable.firstItemId());
		}
		
		
	}
	
	private void addOrUpdateItem( TmsUser user ) {
		
		Item item = usersTable.getItem( user );
		
		if ( item == null ) {

			if ( logger.isDebugEnabled()) logger.debug( "Item will be added: " + user );
			item = usersTable.addItem( user );
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Item exists already. Will be modified: " + user );
		}

		item.getItemProperty( "code" ).setValue( user.getCode());
		item.getItemProperty( "fio" ).setValue( user.getLastAndFirstNames());
		
		
	}
	
	@Override
	public void listWasChanged() {
		dataFromModel();
	}

	private String [] searchFields = { "fio", "code", };
	protected String [] getFieldsForSearch() {
		
		return this.searchFields;
		
	}
	
	
}
