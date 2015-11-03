package com.c2point.tms.web.ui.projectsview;

import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.application.TmsApplication;
import com.c2point.tms.web.ui.listeners.ProjectChangedListener;
import com.c2point.tms.web.ui.listeners.ProjectTaskChangedListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class P_and_T_Component extends VerticalLayout implements ItemClickListener {

	private static Logger logger = LogManager.getLogger( P_and_T_Component.class.getName());
	
	//
	private TmsApplication app;
	private P_and_T_TreeModel model;
	
	private TreeTable		tree;
	
/*	
	public P_and_T_Component( TmsApplication app ) {
		this( app, true );
	}
*/
	public P_and_T_Component( TmsApplication app, boolean projectsOnly, SupportedFunctionType type  ) {
		this( app, app.getSessionData().getUser().getOrganisation(), projectsOnly, type );
	}

	public P_and_T_Component( TmsApplication app, Organisation org, boolean projectsOnly, SupportedFunctionType type ) {
		super();
		
		this.app = app;
		
		initView();

		if ( org != null ) {
			initModel( org, projectsOnly, type );
		}
		
	}
	
	private void initView() {
	
		setMargin( true );
//		setSpacing( true );
//		setSizeFull();
		setHeight( "100%" );
		setWidth( "50ex" );
		
		tree = new TreeTable();
		tree.setHeight( "100%" );
		tree.setWidth( "100%" );
		
		// Configure table
		tree.setSelectable( true );
		tree.setMultiSelect( false );
		tree.setNullSelectionAllowed( false );
		tree.setImmediate( true );

		addComponent( tree );
		
		setExpandRatio( tree, 1.0f );
		
		tree.addListener( this );

	}

	public void addListener( ItemClickListener listener ) {
		tree.addListener( listener );
	}
	
	public void addListener( ValueChangeListener listener ) {
		tree.addListener( listener );
	}
	
	public void initModel( Organisation org, boolean projectsOnly, SupportedFunctionType type ) {
		
		this.model = new P_and_T_TreeModel( org, projectsOnly, type );

		tree.setContainerDataSource( model );
		
		String headerStr;
		if ( projectsOnly ) {
			headerStr = app.getResourceStr( "reporting.projects.caption" );
		} else {
			headerStr = app.getResourceStr( "reporting.projects.and.tasks.caption" );
		}

		tree.setColumnHeaders( new String [] { headerStr } );

		
	}

	public boolean select( Object obj ) {
		boolean res = false;
		
		Item item = model.getItem( obj );
		if ( item != null ) {
			tree.setValue( obj );
			
			Object parentId = tree.getParent( obj );
			if ( parentId != null && tree.isCollapsed( parentId )) {
				tree.setCollapsed( parentId, false );
			}
			tree.setCurrentPageFirstItemId(  obj );
			res = true;
			logger.debug( "Item " + obj.getClass().getName() + " has been found and selected in the tree" );
		} else {
			logger.debug( "Item " + obj.getClass().getName() + " has NOT been found and selected in the tree" );
		}
		
		return res;
	}

	public void fireSelection() {
		Object obj = tree.getValue();
		
		if ( obj != null ) {
			if ( obj instanceof ProjectTask && !model.isProjectsOnly()) {
				fireProjectTaskSelected(( ProjectTask )obj );
			} else if ( obj instanceof Project && model.isProjectsOnly()) {
				fireProjectSelected(( Project )obj );
			} 
			
		}
	}
	
	@Override
	public void itemClick(ItemClickEvent event) {
		if ( event.isDoubleClick()) {
//			logger.debug( "Item selected to be added: " + event.getItemId().getClass().getName());
			if ( event.getItemId() instanceof ProjectTask && !model.isProjectsOnly()) {
				ProjectTask pt = ( ProjectTask )event.getItemId();
				
				if ( pt != null ) {
					logger.debug( "ProjectTask has been selected: " + pt.getProject().getName() + "." + pt.getTask().getName());

					fireProjectTaskSelected( pt );
				}
			} else if ( event.getItemId() instanceof Project ) {
				if ( model.isProjectsOnly()) {
					Project p = ( Project )event.getItemId();
					if ( p != null ) {
						logger.debug( "Project has been selected: " + p.getName());
					
						fireProjectSelected( p );
					
					}
				} else {
					logger.debug( "Project was double clicked" );
					Object obj = event.getItemId();
					tree.setCollapsed( obj, !tree.isCollapsed( obj ));
					
				}
			}
		}
		
	}

	
	private EventListenerList	listenerList = new EventListenerList(); 
	public void addListener( ProjectChangedListener listener ) { listenerList.add( ProjectChangedListener.class, listener );}
	public void addListener( ProjectTaskChangedListener listener )  { listenerList.add( ProjectTaskChangedListener.class, listener );}
	
	protected void fireProjectSelected( Project project ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectChangedListener.class) {
	    		(( ProjectChangedListener )listeners[ i + 1 ] ).wasChanged( project );
	         }
	     }
	}
	protected void fireProjectTaskSelected( ProjectTask pt ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProjectTaskChangedListener.class) {
	    		(( ProjectTaskChangedListener )listeners[ i + 1 ] ).wasChanged( pt );
	         }
	     }
	}
	
	public P_and_T_TreeModel getModel() { return model; }
	
}
