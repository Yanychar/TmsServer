package com.c2point.tms.web.ui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class ValueLabel extends Label {

	public ValueLabel() {
		this( null );
	}

	public ValueLabel( String str ) {
		super();
		setContentMode( ContentMode.HTML );
		setValue( str );
	}

	public void setCaption( String str ) {
		super.setCaption( "<b>" + str + "</b>" );
	}
	
	public void setValue( String str ) {
		if ( str != null ) {
			super.setValue( "<b>" + str + "</b>" );
		} else {
			super.setValue( "" );
		}
	}
	
}

