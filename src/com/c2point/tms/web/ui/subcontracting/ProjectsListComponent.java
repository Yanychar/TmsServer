package com.c2point.tms.web.ui.subcontracting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Project;
import com.c2point.tms.web.ui.listeners.ProjectListChangedListener;
import com.c2point.tms.web.ui.ListWithSearchComponent;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class ProjectsListComponent extends ListWithSearchComponent implements ProjectListChangedListener {
	
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ProjectsListComponent.class.getName());

	private SubcontractingModel model;
	
	private Table 				projectsTable;
	
	public ProjectsListComponent( SubcontractingModel model ) {
		
		super();
		this.model = model;

		initView();

		model.addChangedListener(( ProjectListChangedListener )this );

	}

	private void initView() {

		setSizeFull();

		setMargin( true );
		setSpacing( true );

		projectsTable = new Table();

		setContainerForSearch( projectsTable );
		
		// Configure table
		projectsTable.setSelectable( true );
		projectsTable.setNullSelectionAllowed(  false );
		projectsTable.setMultiSelect( false );
		projectsTable.setColumnCollapsingAllowed( false );
		projectsTable.setColumnReorderingAllowed( false );
		projectsTable.setImmediate( true );
		projectsTable.setSizeFull();
		
		projectsTable.addContainerProperty( "code", String.class, null );
		projectsTable.addContainerProperty( "name", String.class, null );

		projectsTable.setColumnHeaders( new String[] {
				model.getApp().getResourceStr( "general.table.header.code" ),
				model.getApp().getResourceStr( "general.table.header.projectname" ),
		});

		// Handle selection of item
		projectsTable.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange( ValueChangeEvent event ) {

				model.selectProject(( Project )event.getProperty().getValue());

			}

		});

		
		this.addComponent( getSearchBar() );
		this.addComponent( projectsTable );

		this.setExpandRatio( projectsTable, 1.0f );

		
	}
	
	private void dataFromModel() {
		
		// Store selection for recovery at the end of this method
		Project selectedProject = ( Project )projectsTable.getValue();
		Project newSelectedProject = null;
		boolean selected = ( selectedProject != null );

		// remove old content
		projectsTable.removeAllItems();

		if ( model.getProjectsList() != null ) {
			for ( Project project : model.getProjectsList()) {
				if ( project != null && model.okToShow( project )) {
					addItem( project );

					// Check that selection can be restored
					if ( selected && project.getId() == selectedProject.getId()) {
						newSelectedProject = project;
						selected = false;
					}
				}
			}


		}
		projectsTable.setValue( newSelectedProject );

		projectsTable.setSortContainerPropertyId( "name" );

		projectsTable.sort();


		// Select 1st item if exists
		projectsTable.setValue( projectsTable.firstItemId() );
		
	}
	
	
	private void addItem( Project project ) {
		
		Item item = projectsTable.addItem( project );

		item.getItemProperty( "code" ).setValue( project.getCode());
		item.getItemProperty( "name" ).setValue( project.getName());
		
		
	}
	
	@Override
	public void listWasChanged() {
		
		Object selectedItemId = projectsTable.getValue();
		
		dataFromModel();

		if ( selectedItemId != null && projectsTable.containsId( selectedItemId ))
			projectsTable.select( selectedItemId );
		else if ( projectsTable.firstItemId() != null )
			projectsTable.setValue( projectsTable.firstItemId());
		else
			model.selectProject( null );
	}

}
