package com.c2point.tms.web.ui.subcontracting;


import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.subcontracting.Contract;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;
import com.c2point.tms.web.ui.projectsview.ModifyAssignedTaskDialog;
import com.c2point.tms.web.ui.ListWithSearchComponent;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class SubcontractorsListComponent extends ListWithSearchComponent implements SelectionChangedListener { 

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( SubcontractorsListComponent.class.getName());
	
	private SubcontractingModel model;
	
	private Label 			nameLabel;
	private Button 			assignButton;
	private Table 			subcontractorsTable;
	
	public SubcontractorsListComponent( SubcontractingModel model ) {
		
		super();
		this.model = model;

		initView();

		model.addChangedListener( this );
	}

	private void initView() {

		setSizeFull();

		setMargin( true );
		setSpacing( true );

		nameLabel = new Label ( "", ContentMode.HTML );
		nameLabel.addStyleName( Runo.LABEL_H1);
		nameLabel.setValue( "<b>" + "Subcontractors" + "</b>" );
		
		subcontractorsTable = new Table();

		setContainerForSearch( subcontractorsTable );
		
		// Configure table
		subcontractorsTable.setSelectable( true );
		subcontractorsTable.setNullSelectionAllowed(  false );
		subcontractorsTable.setMultiSelect( false );
		subcontractorsTable.setColumnCollapsingAllowed( false );
		subcontractorsTable.setColumnReorderingAllowed( false );
		subcontractorsTable.setImmediate( true );
		subcontractorsTable.setSizeFull();
		
		subcontractorsTable.addContainerProperty( "code", String.class, null );
		subcontractorsTable.addContainerProperty( "name", String.class, null );

		subcontractorsTable.setColumnHeaders( new String[] {
				model.getApp().getResourceStr( "general.table.header.code" ),
				model.getApp().getResourceStr( "general.table.header.projectname" ),
		});

		// Handle selection of item
		subcontractorsTable.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange( ValueChangeEvent event ) {

//				model.selectContract(( Project )event.getProperty().getValue());

			}

		});

		assignButton = new Button();
        assignButton.setIcon( new ThemeResource( "icons/24/assign24.png"));
        assignButton.setDescription( "???????" ); // model.getApp().getResourceStr( "projects.tasks.assign.tooltip" ));
        assignButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick( ClickEvent event ) {

				addSubcontractor();
			}
        	
        });
		
		this.addComponent( nameLabel );
		this.addComponent( assignButton );
		this.addComponent( getSearchBar() );
		this.addComponent( subcontractorsTable );

		this.setExpandRatio( subcontractorsTable, 1.0f );

	}
	
	private void dataFromModel() {
		
		subcontractorsTable.removeAllItems();

		Collection<Contract> list = model.getContracts();
		
		if ( list != null ) {
			for ( Contract contract : list ) {
				if ( contract != null && contract.getSubcontractor() != null ) {

					addItem( contract );

				}
			}


		}

		subcontractorsTable.setSortContainerPropertyId( "name" );

		subcontractorsTable.sort();


		// Select 1st item if exists
		subcontractorsTable.setValue( subcontractorsTable.firstItemId() );
		
	}
	
	
	private void addItem( Contract contract ) {
		
		Item item = subcontractorsTable.addItem( contract );

		item.getItemProperty( "code" ).setValue( contract.getSubcontractor().getCode());
		item.getItemProperty( "name" ).setValue( contract.getSubcontractor().getName());
		
		
	}
	

	@Override
	public void selectionChanged() {
		
		dataFromModel();
		
	}

	private void addSubcontractor() {

		
		SubcontractorSelectionDialog selectWindow = new SubcontractorSelectionDialog( this.model );
		
		UI.getCurrent().addWindow( selectWindow );
		
		
	}
	
}
