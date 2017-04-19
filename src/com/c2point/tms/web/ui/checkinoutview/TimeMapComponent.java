package com.c2point.tms.web.ui.checkinoutview;

import com.c2point.tms.entity.CheckInOutRecord;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

public class TimeMapComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;

	private HorizontalLayout 	layout;

	private CheckInOutRecord record;
	
	private Button 				mapButton;
	
	public TimeMapComponent( CheckInOutRecord record ) {
		super();
		
		this.record = record;
		
		initView();
	}

	private void initView() {

		layout = new HorizontalLayout();
		layout.setSpacing( true );
		setCompositionRoot( layout );

	}

	public void addTime( Component timeL ) {
		
		layout.addComponent( timeL );
		layout.setComponentAlignment( timeL, Alignment.MIDDLE_LEFT );
	}
	
	public void addMap() {
		
		mapButton = new Button();
		mapButton.setStyleName( BaseTheme.BUTTON_LINK );
		
		mapButton.setIcon( new ThemeResource( "icons/16/map16.png" ));
		
		layout.addComponent( mapButton );
		
	}
	
	public void addClickListener( ClickListener listener ) {
		
		if ( mapButton != null ) {
			mapButton.addClickListener( listener );
		}
	}

	public CheckInOutRecord getCheckInOutRecord() { return record; }

}
