package com.c2point.tms.web.ui.reportsmgmt.timereports.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.web.ui.reportsmgmt.timereports.model.ReportsManagementModel;
import com.c2point.tms.web.ui.reportsmgmt.timereports.model.WeekItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TasksSelectionComponent extends VerticalLayout implements ItemClickListener {
	
	private static Logger logger = LogManager.getLogger( TasksSelectionComponent.class.getName());

	private TreeTable 		tree;
	
	private ReportsManagementModel 	model;
	
	
	public TasksSelectionComponent() { // ReportsManagementModel 	model ) {
		super();
//		this.model = model;

		initView();

	}
	
	private void initView() {

		this.setSizeFull();
		this.setSpacing( true );
		this.setMargin( true );
		
		tree = new TreeTable();
		
		tree.setSelectable( true );
		tree.setMultiSelect( true );
		tree.setNullSelectionAllowed( false );
		tree.setImmediate( true );
		tree.setSizeFull();
		
		tree.addItemClickListener( this );
		
//		setModel( this.model );

		addComponent( tree );
		
	}

	@Override
	public void itemClick(ItemClickEvent event ) {
		if ( event.isDoubleClick()) {
			logger.debug( "Item selected to be added: " + event.getItemId().getClass().getName());
			if ( event.getItemId() instanceof ProjectTask ) {
				ProjectTask pt = ( ProjectTask )event.getItemId();
				
				if ( pt != null ) {
					logger.debug( "ProjectTask selected to be added: " + pt.getProject().getName() + "." + pt.getTask().getName());

					@SuppressWarnings("unused")
					WeekItem  item = model.getTimeReportsModel().getOrAddWeekItem( pt );
					
				}
			} else if ( event.getItemId() instanceof Project ) {
				logger.debug( "Project was double clicked" );
				Object obj = event.getItemId();

				tree.setCollapsed( obj, !tree.isCollapsed( obj ));
			}
		}
		
	}
	
	public void setModel( ReportsManagementModel model ) {

		if ( model != null ) {
			
			this.model = model;
			
			tree.setContainerDataSource( model.getProjectModel());
			
			tree.setColumnHeaders( new String[] { 
					model.getApp().getResourceStr( "reporting.projects.and.tasks.caption" ), 
			}); 
			
		}
		
	}

}
