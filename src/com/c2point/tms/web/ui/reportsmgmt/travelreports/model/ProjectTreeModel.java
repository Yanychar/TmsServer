package com.c2point.tms.web.ui.reportsmgmt.travelreports.model;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.util.StringUtils;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

@SuppressWarnings("serial")
public class ProjectTreeModel extends HierarchicalContainer  {
	
	private static Logger logger = LogManager.getLogger( ProjectTreeModel.class.getName());

	private ReportsManagementModel	model; 
	
	public ProjectTreeModel( ReportsManagementModel model ) {
		super();
		
		this.model = model;
		
	}
	
	public void initModel() {
		setOrganisation( this.model.getSessionOwner().getOrganisation());
	}
	
	public void reInitModel() {

	}
	
	private void setOrganisation( Organisation org ) {
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

		Item itemPrj;
		for ( Project prj : projects ) {
			
			if ( prj != null && !prj.isDeleted()) {
				itemPrj =  this.addItem( prj );
				
				@SuppressWarnings("rawtypes")
				Property prop = itemPrj.getItemProperty("name");
				if ( prop != null ) {
					prop.setValue( StringUtils.padRightSpaces( prj.getCode(), 6 ) + prj.getName());
				}

				logger.debug( "Project '" + prj.getName() + "' was added to the ProjectTask tree" );
				
				this.setChildrenAllowed( prj, false );
					
			}
		}
		
		return res;
	}
	
}
