package com.c2point.tms.web.ui.projectsview;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.ui.listeners.ProjectChangedListener;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class ProjectView extends Panel implements SelectionChangedListener, ProjectChangedListener {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ProjectView.class.getName());
	
	private ProjectsModel	model;

	private Label 			projectContent;
	private Label 			timeContent;
	private Label 			addressContent;
	
	public ProjectView( ProjectsModel model ) {
		super();
		this.model = model;
		
		initView();
	}
	
	private void initView() {

		setCaption( this.model.getApp().getResourceStr( "general.table.header.project" ));
		addStyleName( Runo.PANEL_LIGHT );		
		
		
		
		VerticalLayout hl = new VerticalLayout();
		hl.setMargin( true ); // we want a margin
		hl.setSpacing( true ); // and spacing between components		
//		hl.setHeight( "25ex" );
		
		projectContent = new Label();
		projectContent.setContentMode( ContentMode.HTML );
		timeContent = new Label();
		timeContent.setContentMode( ContentMode.HTML );
		addressContent = new Label();
		addressContent.setContentMode( ContentMode.HTML );
		
		hl.addComponent( projectContent );
		hl.addComponent( timeContent );
		hl.addComponent( addressContent );

		this.setContent( hl );
		
		model.addChangedListener(( SelectionChangedListener )this );
		model.addChangedListener(( ProjectChangedListener )this );
		
	}

	public void selectionChanged() {
		showProject( model.getSelectedProject() );
	}
	
	public void wasChanged( Project project ) {
		showProject( project );
	}
	
	public void showProject( Project project ) {
		if ( project != null ) {
			TmsUser mgr = project.getProjectManager();
			projectContent.setValue( 
					  model.getApp().getResourceStr( "general.edit.code" ) + ":" + "&nbsp;" 
					+ "<b><big>" + out( project.getCode()) + "</big></b>"   
					+ "<br />"
					+ model.getApp().getResourceStr( "general.edit.name" ) + ":" + "&nbsp;" 
					+ "<b>" + out( project.getName()) + "</b>" 
					+ "<br />"
					+ model.getApp().getResourceStr( "general.edit.owner" ) + "&nbsp;"
					+ "<b>" + out( mgr != null ? mgr.getFirstAndLastNames() : null ) + "</b>" 
					+ ( project.isClosed() ? "<b>" + "Closed" + "</b>" + "<br />" : "" )
			);
			
			timeContent.setValue( 
					  out( project.getStart(), model.getApp().getResourceStr( "projects.edit.start" ))   
					+ out( project.getEndPlanned(), model.getApp().getResourceStr( "projects.edit.end.plan" ))   
					+ out( project.getEndReal(), model.getApp().getResourceStr( "projects.edit.end.real" ), true )
			);
					 
			addressContent.setValue(
					 ( project.getAddress() != null ?
						  model.getApp().getResourceStr( "projects.edit.address" ) + "&nbsp;"
						+ "<b>" + project.getAddress() + "</b>"
						+ "<br />"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;"
						: "" )
					+ ( project.getGeo() != null && project.getGeo().getLatitude() != null ?	
						  model.getApp().getResourceStr( "projects.edit.loc.lt" ) + "&nbsp;"
						+ "<b>" + project.getGeo().getLatitude() + "</b>"
						+ "<br />"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;"
						: "" )
					+ ( project.getGeo() != null && project.getGeo().getLongitude() != null ?
						  model.getApp().getResourceStr( "projects.edit.loc.lg" ) + "&nbsp;"
						+ "<b>" + project.getGeo().getLongitude() + "</b>"
					    : "" )
			);
		} else {
			projectContent.setValue( "" );
			timeContent.setValue( "" );
			addressContent.setValue( "" );
		}
	}
	
	private String out( String str ) {
		if ( str != null ) return str;
		
		return "";
	}
	
	private String out( Date date ) {
		if ( date != null ) {
			 return DateUtil.dateToString( date );
		}
		
		return "";
	}

	private String out( Date date, String caption ) {
		
		return out( date, caption, false );
	}
	
	private String out( Date date, String caption, boolean isLast ) {
		
		String str = out( date );
		
		if ( str != null && str.trim().length() > 0 ) {
			
			str = caption + "&nbsp;" + "<b>" + str.trim() + "</b>" 
						  + ( !isLast ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "" );
			
		} else{
			str = "";
		}
		
		return str;
	}
	
}
