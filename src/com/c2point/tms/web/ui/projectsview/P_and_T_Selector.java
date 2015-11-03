package com.c2point.tms.web.ui.projectsview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.listeners.ProjectChangedListener;
import com.c2point.tms.web.ui.listeners.ProjectTaskChangedListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class P_and_T_Selector extends Window implements ProjectChangedListener, ProjectTaskChangedListener, ValueChangeListener  {

	private static Logger logger = LogManager.getLogger( P_and_T_Selector.class.getName());
	
	private TmsApplication			app;
	private boolean 				projectsOnly;
	private SupportedFunctionType 	type;
	
	private P_and_T_Component	selector;
	private Button				selectButton;
	private Button				cancelButton;
	
/*
  	public P_and_T_Selector( TmsApplication app  ) {
 		this( app, app.getSessionData().getUser().getOrganisation(), true );
	}
*/

	public P_and_T_Selector( TmsApplication app, boolean projectsOnly, SupportedFunctionType type ) {  
		this( app, app.getSessionData().getUser().getOrganisation(), projectsOnly, type );
	}

	public P_and_T_Selector( TmsApplication app, Project project, SupportedFunctionType type ) {
		this( app, project.getOrganisation(), true, type );

		selector.select( project );
	}

	public P_and_T_Selector( TmsApplication app, ProjectTask pTask, SupportedFunctionType type ) {
		this( app, pTask.getProject().getOrganisation(), false, type );
		
		selector.select( pTask );
	}
	
	private P_and_T_Selector( TmsApplication app, Organisation org, boolean projectsOnly, SupportedFunctionType type ) {
		super();
		setModal( true );
		
		this.app = app;
		this.projectsOnly = projectsOnly;
		this.type = type;
		
		initView( org );
	}

	public void initView( Organisation org ) {

		setWidth( "36em" );
		setHeight( "75%" );
		center();

		if ( projectsOnly ) {
			this.setCaption( app.getResourceStr( "projects.select.caption" ));
		} else {
			this.setCaption( app.getResourceStr( "projects.tasks.select.caption" ));
		}
		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin( true );
		vl.setSpacing( true );
		vl.setSizeFull();

		selector = new P_and_T_Component( app, org, projectsOnly, type );
//		selector.setWidth( "100%" );
		selector.setSizeFull();

		selector.addListener(( ValueChangeListener )this );
		selector.addListener(( ProjectChangedListener )this );
		selector.addListener(( ProjectTaskChangedListener )this );

		Component bBar = getBottomBar();
		bBar.setHeight(( float )( selectButton.getHeight() * 1.25), selectButton.getHeightUnits());

		vl.addComponent( selector );
		vl.addComponent( bBar );
		
		this.setContent( vl );
		
		vl.setExpandRatio( selector, 1 );
		
	}

	private Component getBottomBar() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin( true );
		layout.setSpacing( true );
//		layout.setSizeFull();

		selectButton = new Button( app.getResourceStr( "general.button.ok" ));
		cancelButton = new Button( app.getResourceStr( "general.button.cancel" ));
		
		layout.addComponent( selectButton );
		layout.addComponent( cancelButton );
		
		layout.setComponentAlignment( selectButton, Alignment.MIDDLE_LEFT );
		layout.setComponentAlignment( cancelButton, Alignment.MIDDLE_LEFT );
		
		
		final Window dlg = this;
		selectButton.addListener( new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				selector.fireSelection();
				
			}
			
		});
		
		cancelButton.addListener( new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				dlg.close();
			}
			
		});
		
		
		
		return layout;
	}

	@Override
	public void wasChanged( ProjectTask pt ) {
		this.close();
		
	}

	@Override
	public void wasChanged(Project project) {
		this.close();
		
	}

	
	public void addListener( ProjectChangedListener listener ) { selector.addListener( listener ); }
	public void addListener( ProjectTaskChangedListener listener )  { selector.addListener( listener ); }

	@Override
	public void valueChange( ValueChangeEvent event) {
//		( Organisation )event.getProperty().getValue()
		logger.debug( "Selector received item clicked!" );
		if ( event.getProperty().getValue() instanceof Project && projectsOnly
				||
			 event.getProperty().getValue() instanceof ProjectTask && !projectsOnly ) {
			
			selectButton.setEnabled( true );
		} else {
			selectButton.setEnabled( false );
		}
		
	}
	
}
