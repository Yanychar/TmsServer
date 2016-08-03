package com.c2point.tms.web.ui.reportsmgmt.timereports.model;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.util.StringUtils;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

@SuppressWarnings("serial")
public class ProjectsTasksTreeModel extends HierarchicalContainer  {
	
	private static Logger logger = LogManager.getLogger( ProjectsTasksTreeModel.class.getName());

	private ReportsManagementModel	model; 
	
	public ProjectsTasksTreeModel( ReportsManagementModel model ) {
		super();
		
		this.model = model;
		
	}

	public ProjectsTasksTreeModel() {

		this( null );
		
	}

	public void initModel() {
		
		if ( this.model != null )
			setOrganisation( this.model.getSessionOwner().getOrganisation());
	}
	
	public void reInitModel() {

	}
	
	public void setOrganisation( Organisation org ) {
		if ( org != null ){
			this.addContainerProperty( "name", String.class, "" );
			
			init( org.getProjects().values());
			this.sort( new String [] { "name" }, new boolean [] { true } );
			
		} else {
			logger.error( "Organisation shall be specified!" );
			throw new IllegalArgumentException ( "Organisation cannot be null!" );
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean init( Collection<Project> projects ) {
		boolean res = true;

		Item itemPrj, itemTask;
		for ( Project prj : projects ) {
			
			if ( prj != null && !prj.isDeleted()) {
				List<ProjectTask> lst = prj.getProjectTasksList();
				if ( lst != null && lst.size() > 0 ) {
					
					itemPrj =  this.addItem( prj );
					
					
//					itemPrj.getItemProperty("name").setValue( StringUtils.padRightSpaces( prj.getCode(), 6 ) + prj.getName());
					try {
						Property prop = itemPrj.getItemProperty("name");
						prop.setValue( StringUtils.padRightSpaces( prj.getCode(), 6 ) + prj.getName());
					} catch( Exception e ) {
						logger.error( "***" );
					}

					logger.debug( "Project '" + prj.getName() + "' was added to the ProjectTask tree" );
					
					this.setChildrenAllowed( prj, true );
					
					String codeStr;
					for ( ProjectTask pt : prj.getProjectTasksList()) {
						if ( pt != null && !pt.isDeleted()) {
							itemTask =  this.addItem( pt );
							
							codeStr = ( pt.getCodeInProject() != null 
									&& pt.getCodeInProject().length() > 0 ? pt.getCodeInProject() : pt.getTask().getCode());
	
							itemTask.getItemProperty("name").setValue( StringUtils.padRightSpaces( codeStr, 10 ) + pt.getTask().getName());
							
							logger.debug( "   Task '" + pt.getTask().getName() + "' was added to the ProjectTask tree" );
	
							this.setParent( pt, prj );
							this.setChildrenAllowed( pt, false );
						}
					}
					
				}
			}
		}
		
		return res;
	}
	
}
