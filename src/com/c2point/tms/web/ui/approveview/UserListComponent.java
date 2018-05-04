package com.c2point.tms.web.ui.approveview;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.ApprovalFlagType;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.web.ui.ListWithSearchComponent;
import com.c2point.tms.web.ui.approveview.model.ApproveModel;
import com.c2point.tms.web.ui.approveview.model.TmsUserHolder;
import com.c2point.tms.web.ui.listeners.ReportChangedListener;
import com.c2point.tms.web.ui.listeners.UserChangedListener;
import com.c2point.tms.web.ui.listeners.UserListChangedListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class UserListComponent extends ListWithSearchComponent implements UserListChangedListener, UserChangedListener {
	
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( UserListComponent.class.getName());

	private ApproveModel model;
	
	private Table		usersTable;
	
	public UserListComponent( ApproveModel model ) {
		super();
		this.model = model;

//		model.addChangedListener( this );
		
		initView();

		model.addChangedListener(( UserListChangedListener ) this );
		model.addChangedListener(( UserChangedListener )this );

//		dataFromModel();
		
	}

	private void initView() {

		setSizeFull();

		setMargin( true );
		setSpacing( true );

//		setHeight( "100%" );
//		setWidth( "40ex" );
		
		usersTable = new Table();
//		usersTable.setHeight( "100%" );
//		usersTable.setWidth( "100%" );

		setContainerForSearch( usersTable );
		
		// Configure table
		usersTable.setSelectable( true );
		usersTable.setMultiSelect( false );
		usersTable.setNullSelectionAllowed( false );
		usersTable.setColumnCollapsingAllowed( false );
		usersTable.setColumnReorderingAllowed( false );
		usersTable.setImmediate( true );
		usersTable.setSizeFull();
		
		usersTable.addContainerProperty( "code",		String.class, 	null );
		usersTable.addContainerProperty( "fio", 		String.class, 	null );
/*
		usersTable.addContainerProperty( "checked",		Embedded.class, null );
		usersTable.addContainerProperty( "approved",	Embedded.class, null );
		usersTable.addContainerProperty( "rejected",	Embedded.class, null );
		usersTable.addContainerProperty( "processed",	Embedded.class, null );
*/
		usersTable.addContainerProperty( "status",	StatusPresenterComponent.class, null );

		usersTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "general.table.header.code" ), 
				model.getApp().getResourceStr( "general.table.header.employee" ), 
				"", 
//				"", 
//				"", 
//				"", 
		});
		
		// New User has been selected. Send event to model
		usersTable.addValueChangeListener( new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Property.valueChanged!" );
				
				TmsUserHolder holder = ( TmsUserHolder )usersTable.getValue();

				if ( holder != null ) {
					model.selectUser( holder );
				}
			}
		});
			
		
/*		
		usersTable.addItemClickListener( new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "ItemClickListener!" );
				
			}
			
		});
*/		
		
//		userGroupSelector = getUserGroupSelector();		
		
		// Add components
/*
		if ( userGroupSelector.size() > 1 ) {
			addComponent( userGroupSelector );
		}
*/
		this.addComponent( getSearchBar());
		this.addComponent( usersTable );
		
		this.setExpandRatio( usersTable, 1.0f );
		
		
		
//		userTable.setSizeFull();

		
	}
	
	private void dataFromModel() {
		
		Collection< TmsUserHolder > usersLst = model.getUsersList();
		
//		try {
			usersTable.removeAllItems();
//		} catch ( Exception e ) {
//			if ( logger.isDebugEnabled()) logger.debug( "UserList is empty. Nothing to remove" );
//		}
		
		if ( usersLst != null ) {
			for ( TmsUserHolder holder : usersLst ) {
				if ( holder != null ) {
					addItem( holder );
				}
			}
		}
		
		usersTable.setSortContainerPropertyId( "fio" );

		usersTable.sort();
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void addItem( TmsUserHolder usrHolder ) {
		
		Item item = usersTable.addItem( usrHolder );

		item.getItemProperty( "status" ).setValue( new StatusPresenterComponent( model.getApp(), usrHolder ));
		
		if ( logger.isDebugEnabled()) logger.debug( "Item will be added: " + usrHolder.getTmsUser());

		updateItem( item, usrHolder );
		
	}
	
	@SuppressWarnings("unchecked")
	private void updateItem( Item item, TmsUserHolder usrHolder ) {
		
		item.getItemProperty( "code" ).setValue( usrHolder.getTmsUser().getCode());
		item.getItemProperty( "fio" ).setValue( usrHolder.getTmsUser().getLastAndFirstNames());

		(( StatusPresenterComponent )item.getItemProperty( "status" ).getValue()).updateStatus();
		
		
	}
	
	@Override
	public void listWasChanged() {
//		usersTable.removeAllItems();
		
		Object selectedItemId = usersTable.getValue();
		
		dataFromModel();

		if ( selectedItemId != null && usersTable.containsId( selectedItemId ))
			usersTable.select( selectedItemId );
		else if ( usersTable.firstItemId() != null )
			usersTable.setValue( usersTable.firstItemId());
		else
			model.selectUser( null );
	}

/*	
	private Embedded getStatusIcon( ApprovalFlagType type, int count ) {
		
		Embedded icon = null;

		if ( count > 0 ) {
			String iconName = "";
			String tooltipStr = "";
			switch ( type ) {
				case TO_CHECK: {
					iconName = "icons/16/question16.png";
					tooltipStr = count + " records to check";
					break;
				}
				case REJECTED: {
					iconName = "icons/16/delete16.png";
					tooltipStr = count + " records rejected";
					break;
				}
				case APPROVED: {
					iconName = "icons/16/selected16.png";
					tooltipStr = count + " records approved";
					break;
				}
				case PROCESSED: {
					iconName = "icons/16/paid16.png";
					tooltipStr = count + " records processed";
					break;
				}
				default: {
					break;
				}
			}
				
			icon = new Embedded( "", new ThemeResource( iconName ));
			icon.setDescription( tooltipStr );
		}
		
		return icon;
	}
*/
	@Override
	public void wasChanged( TmsUser user ) {

		if ( logger.isDebugEnabled()) logger.debug( "UserWasChanged received: " + user );
		TmsUserHolder holder = model.getTmsUserHolder( user );
		if ( holder != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "TmsUserHolder found!" );

			Item item = usersTable.getItem( holder );
			if ( item != null )  {
				
				if ( logger.isDebugEnabled()) logger.debug( "User record will be updated" );
				updateItem( item, holder );

			}
			
		}
		
		
	}

	private String [] searchFields = { "fio", "code", };
	protected String [] getFieldsForSearch() {
		
		return this.searchFields;
		
	}
	
	
}
