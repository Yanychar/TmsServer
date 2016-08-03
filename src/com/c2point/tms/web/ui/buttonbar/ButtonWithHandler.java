package com.c2point.tms.web.ui.buttonbar;

import com.vaadin.ui.Button;

public class ButtonWithHandler extends Button {
	private static final long serialVersionUID = 1L;
	
	
	private ButtonPressHandler handler = null;
	
	ButtonWithHandler() {
		this( null, null );
		
	}

	ButtonWithHandler( String header ) {
		this( header, null );
	}

	ButtonWithHandler( String header, ButtonPressHandler handler ) {
		super( header );
		
		setHandler( handler );
		
	}

	public void setHandler( ButtonPressHandler handler ) {
		this.handler = handler;
	}

	public void removeHandler() {
		setHandler( null );
	}
	
	public ButtonPressHandler getHandler() { return this.handler; }
	
}
