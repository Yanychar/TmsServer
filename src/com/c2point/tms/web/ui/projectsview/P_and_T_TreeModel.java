package com.c2point.tms.web.ui.projectsview;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.util.StringUtils;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.Item;

@SuppressWarnings("serial")
public class P_and_T_TreeModel extends HierarchicalContainer  {
	
	private static Logger logger = LogManager.getLogger( P_and_T_TreeModel.class.getName());

	private Organisation 	org;

	private boolean					projectsOnly;
	private SupportedFunctionType	type;
/*	
	public P_and_T_TreeModel( Organisation org ) {
		this( org, true );
	}
*/
	public P_and_T_TreeModel( Organisation org, boolean projectsOnly, SupportedFunctionType type  ) {
		super();
		
		this.org= org;
		this.projectsOnly = projectsOnly;
		this.type = type;
		
		init();
	}
	
	private boolean init() {
		boolean res = true;

		if ( org == null ){
			logger.error( "Organisation shall be specified!" );
			throw new IllegalArgumentException ( "Organisation cannot be null!" );
		}

		this.addContainerProperty( "name", String.class, "" );
			
		Item itemPrj, itemTask;
		for ( Project prj : org.getProjects().values()) {
			
			if ( prj != null && !prj.isDeleted()) {
				List<ProjectTask> lst = prj.getProjectTasksList();
				if ( lst != null && lst.size() > 0 ) {
					
					itemPrj =  this.addItem( prj );
					
					
					itemPrj.getItemProperty("name").setValue( StringUtils.padRightSpaces( prj.getCode(), 6 ) + prj.getName());

					logger.debug( "Project '" + prj.getName() + "' was added to the ProjectTask tree" );
					
					this.setChildrenAllowed( prj, !projectsOnly );
					
					if ( !projectsOnly ) {
						String codeStr;
						for ( ProjectTask pt : prj.getProjectTasksList()) {
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

		this.sort( new String [] { "name" }, new boolean [] { true } );
			
		
		return res;
	}

	public boolean isProjectsOnly() { return projectsOnly; }
	public SupportedFunctionType getType() { return type; }
	
}
