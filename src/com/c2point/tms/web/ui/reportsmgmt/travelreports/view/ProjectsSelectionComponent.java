package com.c2point.tms.web.ui.reportsmgmt.travelreports.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Project;
import com.c2point.tms.web.ui.reportsmgmt.travelreports.model.ReportsManagementModel;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ProjectsSelectionComponent extends VerticalLayout implements ItemClickListener {
	
	private static Logger logger = LogManager.getLogger( ProjectsSelectionComponent.class.getName());

	private Table 				tree;
	
	private ReportsManagementModel 	model;
	
	
	public ProjectsSelectionComponent() {
		super();

		initView();

	}
	
	private void initView() {

		this.setSizeFull();
		this.setSpacing( true );
		this.setMargin( true );

		tree = new Table();
		
		tree.setSelectable( true );
		tree.setMultiSelect( true );
		tree.setNullSelectionAllowed( false );
		tree.setImmediate( true );
		tree.setSizeFull();
		
		tree.addItemClickListener( this );
		
		addComponent( tree );

	}

	public void setModel( ReportsManagementModel model ) {
		
		this.model = model;

		if ( model != null ) {
			
			this.model = model;
			
			tree.setContainerDataSource( model.getProjectModel());
			
			tree.setColumnHeaders( new String[] { 
					model.getApp().getResourceStr( "reporting.projects.caption" ), 
			}); 
			
		}
	}

	@Override
	public void itemClick(ItemClickEvent event ) {
		if ( event.isDoubleClick()) {
//			logger.debug( "Item selected to be added: " + event.getItemId().getClass().getName());
			if ( event.getItemId() instanceof Project ) {
				Project project = ( Project )event.getItemId();
				
				if ( project != null ) {
					logger.debug( "Project selected to be added: " + project.getName());

					model.getTravelReportsModel().addReport( project );
					
				}
				
				
			}
		}
		
	}
	
}
