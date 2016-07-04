package com.c2point.tms.web.ui.projectsview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Runo;

public class ProjectViewToolbar extends HorizontalLayout {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ProjectViewToolbar.class.getName());

	private ClickListener 		impButtonListener;
	private ClickListener 		expButtonListener;
	
	private Button 		impButton;
	private Button 		expButton;
	
	
	public ProjectViewToolbar() {
		this( null, null );
	}

	public ProjectViewToolbar( ClickListener impButtonListener, ClickListener expButtonListener ) {
		super();
	
		initView();

	}

	public void setImpListener( ClickListener impButtonListener ) { 
		this.impButtonListener = impButtonListener; 

		if ( this.impButtonListener != null ) {
			
			impButton = new Button();
			impButton.setCaption( (( TmsApplication )UI.getCurrent()).getResourceStr( "tools.sidebar.item.import" ));
			impButton.addStyleName( Runo.BUTTON_BIG );
			impButton.addStyleName( Runo.BUTTON_DEFAULT );
			
			addComponent( impButton );

			impButton.addClickListener( impButtonListener );
			
		}
	}
	
	public void setExpListener( ClickListener expButtonListener ) { 
		this.expButtonListener = expButtonListener;
		
		if ( this.expButtonListener != null ) {
			
			expButton = new Button();
			expButton.setCaption( (( TmsApplication )UI.getCurrent()).getResourceStr( "tools.sidebar.item.export" ));
			expButton.addStyleName( Runo.BUTTON_BIG );
			expButton.addStyleName( Runo.BUTTON_DEFAULT );
			
			addComponent( expButton );

			expButton.addClickListener( expButtonListener );
			
		}
		
	}
	
	private void initView() {

		this.setWidth("100%");
		this.setMargin( true );
		
		Label glue = new Label( "" );
		glue.setWidth("100%");
		
		addComponent( glue );
		setExpandRatio( glue, 1.0f );

		setImpListener( impButtonListener );
		setExpListener( expButtonListener );
		
	}
	
}
