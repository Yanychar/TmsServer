package com.c2point.tms.web.ui.company;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.web.ui.company.model.CompaniesMgmtModel;
import com.c2point.tms.web.ui.listeners.OrganisationAddedListener;
import com.c2point.tms.web.ui.listeners.OrganisationChangedListener;
import com.c2point.tms.web.ui.listeners.OrganisationListChangedListener;
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
public class CompaniesListComponent extends VerticalLayout implements OrganisationAddedListener, OrganisationListChangedListener, OrganisationChangedListener {

	private static Logger logger = LogManager.getLogger( CompaniesListComponent.class.getName());

	private static int BUTTON_WIDTH = 25;
	
	private CompaniesMgmtModel	model;

	private Table			companiesTable;
	
	private Button 			addButton;

//	private ComboBox 		validitySelector;


	public CompaniesListComponent( CompaniesMgmtModel model ) {
		super();
		this.model = model;

		initView();

		model.addChangedListener(( OrganisationListChangedListener )this );
		model.addChangedListener(( OrganisationAddedListener )this );
		model.addChangedListener(( OrganisationChangedListener )this );
	}

	private void initView() {

		setSizeFull();

		setMargin( true );
		setSpacing( true );

		companiesTable = new Table();

		// Configure table
		companiesTable.setSelectable( true );
		companiesTable.setMultiSelect( false );
		companiesTable.setNullSelectionAllowed( false );
		companiesTable.setColumnCollapsingAllowed( false );
		companiesTable.setColumnReorderingAllowed( false );
		companiesTable.setImmediate( true );
		companiesTable.setSizeFull();
		
		companiesTable.addContainerProperty( "code", String.class, null );
		companiesTable.addContainerProperty( "name", String.class, null );
		companiesTable.addContainerProperty( "buttons", HorizontalLayout.class, null );

		companiesTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "general.table.header.code" ),
				model.getApp().getResourceStr( "general.table.header.name" ),
				"" 
		}); 
		
		companiesTable.setColumnWidth( "buttons", BUTTON_WIDTH * 3 );		
		
		// Handles the click in the item. NOT USED  YET!
		companiesTable.addItemClickListener( new ItemClickListener() {

            public void itemClick(ItemClickEvent event) {

            	if ( companiesTable.getValue() == event.getItemId() && event.getItemId() != null ) {
                	logger.debug( "Ready to edit Organisation. Event.ItemId: " + event.getItemId());
                } else {
                	logger.debug( "NOT Ready to edit Organisation. Event.ItemId: " + event.getItemId());
                }
            }
        });

		// Handle selection of item
		companiesTable.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange( ValueChangeEvent event ) {
		
				model.selectOrganisation(( Organisation )event.getProperty().getValue());				
			}
		});

        addButton = new Button();
        addButton.setIcon( new ThemeResource( "icons/24/add24.png"));
        addButton.setDescription( model.getApp().getResourceStr( "company.list.add.tooltip" ));
        addButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				UI.getCurrent().addWindow( new ModifyCompanyDialog( model ));

			}
        	
        });

		addComponent( addButton );
		addComponent( companiesTable );
		
		setExpandRatio( companiesTable, 1.0f );

	}

	private void dataFromModel() {

		// Store selection for recovery at the end of this method
		Organisation selectedOrganisation = ( Organisation )companiesTable.getValue();
		Organisation newSelectedOrganisation = null;
		boolean selected = ( selectedOrganisation != null );
		
		// remove old content
		companiesTable.removeAllItems();
		
		if ( model.getOrganisationList() != null ) {
			for ( Organisation organisation : model.getOrganisationList()) {
				if ( organisation != null ) {
					addOrUpdateItem( organisation );
					
					// Check that selection can be restored
					if ( selected && organisation.getId() == selectedOrganisation.getId()) {
						newSelectedOrganisation = organisation;
						selected = false;
					}
				}
			}
			
			
		}
		companiesTable.setValue( newSelectedOrganisation );
		
		
		companiesTable.setSortContainerPropertyId( "name" );

		companiesTable.sort();
		

		// Select 1st item if exists
		companiesTable.setValue( companiesTable.firstItemId() );
	
	}
	
	private void addOrUpdateItem( Organisation organisation ) {
		
		Item item = companiesTable.getItem( organisation );
		
		if ( item == null ) {

			if ( logger.isDebugEnabled()) logger.debug( "Item will be added: " + organisation );
			item = companiesTable.addItem( organisation );
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Item exists already. Will be modified: " + organisation );
		}

		item.getItemProperty( "code" ).setValue( organisation.getCode());
		item.getItemProperty( "name" ).setValue( organisation.getName());

		// Add&Edit buttons
		// If session owner has WRITE access rights
		if ( model.getSecurityContext().isWrite( model.getToShowFilter())) {
			final NativeButton editButton = new NativeButton(); // "Edit" );
	        editButton.setIcon( new ThemeResource( "icons/16/edit16.png"));
	        editButton.setDescription( model.getApp().getResourceStr( "company.list.edit.tooltip" ));
	
	        editButton.setHeight( Integer.toString( BUTTON_WIDTH ) + "px" );
			editButton.setStyleName("v-nativebutton-deleteButton");
			editButton.addStyleName("v-nativebutton-link");
			editButton.setStyleName(Runo.BUTTON_LINK);
	
			editButton.setData( organisation );
			editButton.setImmediate( true );
	
	        editButton.addClickListener( new ClickListener() {
	
				@Override
				public void buttonClick(ClickEvent event) {
	
					companiesTable.setValue( editButton.getData());
					getUI().addWindow( new ModifyCompanyDialog( model, ( Organisation )editButton.getData()));
	
				}
	
	        });
	
	
			// Add Delete button
			final NativeButton delButton = new NativeButton();
	        delButton.setIcon( new ThemeResource( "icons/16/delete16.png"));
	        delButton.setDescription( model.getApp().getResourceStr( "company.list.delete.tooltip" ));
	
			delButton.setHeight( Integer.toString( BUTTON_WIDTH ) + "px" );
			delButton.setStyleName("v-nativebutton-deleteButton");
			delButton.addStyleName("v-nativebutton-link");
			delButton.setStyleName(Runo.BUTTON_LINK);
	
			delButton.setData( organisation );
			delButton.setImmediate( true );
	
	        delButton.addClickListener( new ClickListener() {
	
				@Override
				public void buttonClick(ClickEvent event) {
					if ( logger.isDebugEnabled()) logger.debug( "Delete button was been pressed" );
					// Select item
					final Object current = delButton.getData();
					companiesTable.setValue( current);
					final Object future = companiesTable.prevItemId( current );
	
					// Confirm removal
					String template = model.getApp().getResourceStr( "confirm.company.delete" );
					Object[] params = { (( Organisation )current ).getName() };
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
										if ( deleteButtonPressed(( Organisation ) current )) {
											if ( future != null ) {
												companiesTable.setValue( future );
											} else {
												companiesTable.setValue( companiesTable.firstItemId());
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
	
	private boolean deleteButtonPressed( Organisation organisation ) {
		boolean bRes = false;
		if ( logger.isDebugEnabled()) logger.debug( "Organisation shall be removed!" );

		// Delete from model
		if ( model.removeOrganisation( organisation )) {
			// Update view
			if ( companiesTable.removeItem( organisation )) {
				if ( logger.isDebugEnabled()) logger.debug( "Organisation Item has been removed from Table!" );
				
				String template = model.getApp().getResourceStr( "notify.company.delete" );
				Object[] params = { organisation.getName() };
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
	public void wasAdded( Organisation organisation ) {

		// Add Table item
		addOrUpdateItem( organisation );
		
		// Sort items
		companiesTable.sort();
		
		// Select new item
		companiesTable.setValue( organisation );

	}

	@Override
	public void wasChanged( Organisation organisation ) {

		// Add Table item
		addOrUpdateItem( organisation );
		
		// Sort items
		companiesTable.sort();
		
		// Select new item
		companiesTable.setValue( organisation );
		
	}
	
}
