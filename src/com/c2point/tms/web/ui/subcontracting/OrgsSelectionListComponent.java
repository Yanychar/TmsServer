package com.c2point.tms.web.ui.subcontracting;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.subcontracting.Contract;
import com.c2point.tms.web.ui.ListWithSearchComponent;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;

public class OrgsSelectionListComponent extends ListWithSearchComponent {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( OrgsSelectionListComponent.class.getName());

	private SubcontractingModel model;
	
	private Table 				orgsTable;
	
	public OrgsSelectionListComponent( SubcontractingModel model ) {
		
		super();
		this.model = model;

		initView();

		dataFromModel();
	}

	private void initView() {

		setSizeFull();

		setMargin( true );
		setSpacing( true );

		orgsTable = new Table();

		setContainerForSearch( orgsTable );
		
		// Configure table
		orgsTable.setSelectable( true );
		orgsTable.setNullSelectionAllowed(  false );
		orgsTable.setMultiSelect( false );
		orgsTable.setColumnCollapsingAllowed( false );
		orgsTable.setColumnReorderingAllowed( false );
		orgsTable.setImmediate( true );
		orgsTable.setSizeFull();
		
		orgsTable.addContainerProperty( "selected", CheckBox.class, null );
		orgsTable.addContainerProperty( "code", String.class, null );
		orgsTable.addContainerProperty( "name", String.class, null );

		orgsTable.setColumnHeaders( new String[] {
				"",
				model.getApp().getResourceStr( "general.table.header.code" ),
				model.getApp().getResourceStr( "general.table.header.name" ),
		});

		// Handle selection of item
		orgsTable.addValueChangeListener( new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {


			}

		});

		
		this.addComponent( getSearchBar() );
		this.addComponent( orgsTable );

		this.setExpandRatio( orgsTable, 1.0f );

		
	}
	
	private void dataFromModel() {
		

		// remove old content
		orgsTable.removeAllItems();

		Collection<Organisation> listOrganisations = OrganisationFacade.getInstance().getOrganisations();
		
		Collection<Contract> listContracts = model.getContracts();
		
		if ( listOrganisations != null ) {
			for ( Organisation org : listOrganisations ) {
				if ( org != null ) {
					addItem( org, listContracts );

				}
			}


		}

		orgsTable.setSortContainerPropertyId( "name" );

		orgsTable.sort();

		// Select 1st item if exists
		orgsTable.setValue( orgsTable.firstItemId() );
		
	}
	
	
	private void addItem( Organisation org, Collection<Contract> listContracts ) {
		
		Item item = orgsTable.addItem( org );

		CheckBox cb = new CheckBox( "" );
		
		Contract contract = getSubcontract( org, listContracts );
		if ( contract != null ) {

			// Contract exists. Org shall be checked and contract stored in CheckBox
			cb.setData( contract );
			cb.setValue( true );
			
		} else {
			// No contract yet. Data == null
			cb.setData( null );
			cb.setValue( false );
		}
		
		item.getItemProperty( "selected" ).setValue( cb );
		item.getItemProperty( "code" ).setValue( org.getCode());
		item.getItemProperty( "name" ).setValue( org.getName());
		
		
	}
	
	private Contract getSubcontract( Organisation org, Collection<Contract> listContracts ) {
		
		Contract res = null;
		
		
		return res;
	}

	public void storeChanges() {
		
		Item item;
		boolean changedFlag = false;
		
		for ( Object itemId : orgsTable.getItemIds()) {
			
			item = orgsTable.getItem( itemId );
			
			if ( item != null && itemWasChanged( item )) {
				
				
				
			}
		}
		
	}

	private boolean itemWasChanged( Item item ) {
	
		boolean bRes;
		
		CheckBox cb = ( CheckBox )item.getItemProperty( "selected" );

		bRes = 
			// Selected (contract exist) but ref to contract == null
			cb.getValue() && cb.getData() == null
		||
			// Not Selected (no contract ) but ref to contract != null
			!cb.getValue() && cb.getData() != null;
		
		
		return bRes;
	}
}