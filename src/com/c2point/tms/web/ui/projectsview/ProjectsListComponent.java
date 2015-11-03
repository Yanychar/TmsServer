package com.c2point.tms.web.ui.projectsview;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tms.entity.Project;
import com.c2point.tms.web.ui.listeners.ProjectAddedListener;
import com.c2point.tms.web.ui.listeners.ProjectChangedListener;
import com.c2point.tms.web.ui.listeners.ProjectListChangedListener;
import com.vaadin.data.Item;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ProjectsListComponent extends VerticalLayout implements ProjectAddedListener, ProjectListChangedListener, ProjectChangedListener {

	private static Logger logger = LogManager.getLogger( ProjectsListComponent.class.getName());

	private static int BUTTON_WIDTH = 25;

	private ProjectsModel	model;

	private TextField		searchText;
	private Table 			projectsTable;

	private Button 			addButton;


	public ProjectsListComponent( ProjectsModel model ) {
		super();
		this.model = model;

		initView();

//		dataFromModel();

		model.addChangedListener(( ProjectListChangedListener )this );
		model.addChangedListener(( ProjectAddedListener )this );
		model.addChangedListener(( ProjectChangedListener )this );

	}

	private void initView() {

		setSizeFull();

		setMargin( true );
		setSpacing( true );

		projectsTable = new Table();

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
		projectsTable.addContainerProperty( "buttons", HorizontalLayout.class, null );

		projectsTable.setColumnHeaders( new String[] {
				model.getApp().getResourceStr( "general.table.header.code" ),
				model.getApp().getResourceStr( "general.table.header.projectname" ),
				""
		});


		projectsTable.setColumnWidth( "buttons", BUTTON_WIDTH * 3 );

//		model.addChangedListener(( ProjectListChangedListener )this );
//		model.addChangedListener(( ProjectAddedListener )this );
//		model.addChangedListener(( ProjectChangedListener )this );
		// Handles the click in the item. NOT USED  YET!
		projectsTable.addItemClickListener( new ItemClickListener() {

            public void itemClick( ItemClickEvent event) {

            	logger.debug( "Event: " + event.getClass().getName());
            	logger.debug( "Event.Source: " + event.getSource());
            	logger.debug( "Event.User: " + event.getItem());
            	logger.debug( "Event.UserId: " + event.getItemId());
            	logger.debug( "Cur.Selection: " + projectsTable.getValue());

            	if ( projectsTable.getValue() == event.getItemId() && event.getItemId() != null ) {
                	logger.debug( "Ready to edit Project. Event.ItemId: " + event.getItemId());
//    				Project  = ( TmsUser )(( Table )event.getSource()).getValue();
//                	editUser( user );
                } else {
                	logger.debug( "NOT Ready to edit Project. Event.ItemId: " + event.getItemId());
                }
            }
        });

		// Handle selection of item
		projectsTable.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange( ValueChangeEvent event ) {

				model.selectProject(( Project )event.getProperty().getValue());

			}

		});


        addButton = new Button();
//        addButton.addStyleName( Runo.BUTTON_BIG );
//    	addButton.addStyleName( Runo.BUTTON_DEFAULT );
        addButton.setIcon( new ThemeResource( "icons/24/addproject24.png"));
        addButton.setDescription( model.getApp().getResourceStr( "projects.list.add.tooltip" ));
        addButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick( ClickEvent event) {

				// Add the project
				getUI().addWindow( new ModifyProjectDialog( model ));

			}

        });

        this.addComponent( addButton );
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
				if ( project != null ) {
					addOrUpdateItem( project );

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

	private void addOrUpdateItem( Project project ) {

		if ( logger.isDebugEnabled()) logger.debug( "AddOrUpdate Project: " + ( project != null ? project : "null" ));
		Item item = projectsTable.getItem( project );
		if ( logger.isDebugEnabled()) logger.debug( "Table Item (from project): " + ( item != null ? item : "null" ));

		if ( item == null ) {

			if ( logger.isDebugEnabled()) {
				logger.debug( "Item will be added for project: " + ( project != null ? project : "null" ));
			}

			item = projectsTable.addItem( project );

		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Item exists already. Will be modified: " + ( project != null ? project : "null" ));
		}

		item.getItemProperty( "code" ).setValue( project.getCode());
		item.getItemProperty( "name" ).setValue( project.getName());

		// Add Edit button
		final NativeButton editButton = new NativeButton(); // "Edit" );
        editButton.setIcon( new ThemeResource( "icons/16/edit16.png"));
        editButton.setDescription( model.getApp().getResourceStr( "projects.list.edit.tooltip" ));

        editButton.setHeight( Integer.toString( BUTTON_WIDTH ) + "px" );
		editButton.setStyleName("v-nativebutton-deleteButton");
		editButton.addStyleName("v-nativebutton-link");
		editButton.setStyleName(Runo.BUTTON_LINK);

		editButton.setData( project );
		editButton.setImmediate( true );

        editButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				projectsTable.setValue( editButton.getData());
				getUI().addWindow( new ModifyProjectDialog( model, ( Project )editButton.getData()));

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

		delButton.setData( project );
		delButton.setImmediate( true );


        delButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Delete button was been pressed" );
				// Select item
				final Object current = delButton.getData();
				projectsTable.setValue( current);
				final Object future = projectsTable.prevItemId( current );

				// Confirm removal
				String template = model.getApp().getResourceStr( "confirm.project.delete" );
				Object[] params = { (( Project )current ).getName() };
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
									if ( deleteButtonPressed(( Project ) current )) {
										if ( future != null ) {
											projectsTable.setValue( future );
										} else {
											projectsTable.setValue( projectsTable.firstItemId());
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

	private boolean deleteButtonPressed( Project project ) {
		boolean bRes = false;
		if ( logger.isDebugEnabled()) logger.debug( "Project shall be removed!" );

		// Delete from model
		if ( model.removeProject( project )) {
			// Update view
			if ( projectsTable.removeItem( project )) {

				String template = model.getApp().getResourceStr( "notify.project.delete" );
				Object[] params = { project.getName() };
				template = MessageFormat.format( template, params );

				Notification.show( template );

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
	public void wasAdded( Project project ) {

		// Add Table item
		addOrUpdateItem( project );

		// Sort items
		projectsTable.sort();

		// Select new item
		projectsTable.setValue( project );

	}

	@Override
	public void wasChanged(Project project) {

		// Add Table item
		addOrUpdateItem( project );

		// Sort items
		projectsTable.sort();

		// Select new item
		projectsTable.setValue( project );

	}

	private Component getSearchBar() {
		
		// Add search field
		HorizontalLayout searchLayout = new HorizontalLayout();
		
		searchLayout.setWidth("100%");
		searchLayout.setMargin( new MarginInfo( false, true, false, true ));

		Label searchIcon = new Label();
		searchIcon.setIcon(new ThemeResource("icons/16/search.png"));
		searchIcon.setWidth( "2em" );

		Button deleteIcon = new Button();
		deleteIcon.setStyleName( BaseTheme.BUTTON_LINK );
		deleteIcon.setIcon( new ThemeResource("icons/16/delete16.png"));
		
		deleteIcon.addClickListener( new ClickListener() {

			@Override
			public void buttonClick( ClickEvent event) {

				if ( logger.isDebugEnabled()) logger.debug( "DeleteIcon image had been pressed" );
				
				if ( searchText != null && searchText.getValue() != null && searchText.getValue().length() > 0 ) {

					if ( logger.isDebugEnabled()) logger.debug( "Search text shall be set to empty string" );
					
					searchText.setValue( "" );
					searchFieldUpdated( null );
					
				}
				
			}
			
		});
		
		searchText = new TextField();
		searchText.setWidth("100%");
		searchText.setNullSettingAllowed(true);
		searchText.setInputPrompt( "Search ...");
		searchText.setImmediate( true );
		
		searchText.addTextChangeListener( new TextChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void textChange( TextChangeEvent event ) {
				
				searchFieldUpdated( event.getText());
				
			}
			
		});
		

		searchLayout.addComponent( searchIcon );
		searchLayout.addComponent( searchText );
		searchLayout.addComponent( deleteIcon );

		searchLayout.setExpandRatio( searchText, 1.0f );
		
		return searchLayout;
	}

	private boolean searchFieldUpdated( String searchStr ) {
		
		boolean found = false;

		IndexedContainer container = ( IndexedContainer )projectsTable.getContainerDataSource();
		
		container.removeAllContainerFilters();
		if ( searchStr != null && searchStr.length() > 0 ) {
			Filter filter = new Or(
					new SimpleStringFilter( "name",	searchStr, true, false ),
					new SimpleStringFilter( "code",	searchStr, true, false )
					);
			
			container.addContainerFilter( filter );
			
			
		}
		
		found = container.size() > 0;
		
		projectsTable.setValue( found ? projectsTable.firstItemId() : null );
		
		return found;
	}

}
