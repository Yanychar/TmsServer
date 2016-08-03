package com.c2point.tms.web.ui.buttonbar;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class ButtonBar extends HorizontalLayout {

	private static final long serialVersionUID = 1L;

	public enum ButtonType { Cancel, Ok, Close, Export, Import };
	
	private static Logger logger = LogManager.getLogger( ButtonBar.class.getName());
	
	private Button okButton; // = new Button();
	private Button cancelButton; 
	private Button closeButton; 

	private Button expButton;
	private Button impButton;

	private List<Button> btList = new ArrayList<Button>(); 
	
	
	protected ButtonBar() {
		super();
	}
	
	public static ButtonBar getOkCancelBar() {
		
		ButtonBar bar = new ButtonBar();
		
		Button [] buttons = { bar.okButton, bar.cancelButton }; 
		
		bar.initUI( buttons );
		
		bar.setEnabled( ButtonType.Ok, false );
		
		return bar;
	}
	
	public static ButtonBar getCloseBar () {

		ButtonBar bar = new ButtonBar();
		
		Button [] buttons = { bar.closeButton }; 
		
		bar.initUI( buttons );
		
		return bar;
		
	}
	
	
	protected void initUI( Button [] buttons ) {
		
		this.setWidth( "100%" );
		
		this.setMargin( true );
		this.setSpacing( true );
		
		initResources();
		
		Label glue1 = new Label( "" );
		
		for ( Button button : buttons ) {
			addComponent( button );
		}
		addComponent( glue1 );
		
		this.setExpandRatio( glue1,  1f );
	}
	
	private void initResources() {

		okButton.setCaption( "OK" );
		cancelButton.setCaption( "Cancel" );
		closeButton.setCaption( "Close" );
		
	}

	public void setEnabled( ButtonType type, boolean value ) {
		
		Button button = getButton( type );
		
		if ( button != null ) 
			button.setEnabled( value );
		else {
			okButton.setEnabled( false );
			cancelButton.setEnabled( false );
		}
	}

	public Button getButton( ButtonType type ) {
		
		switch ( type ) {
			case Ok: {
				return okButton;
			}
			case Cancel: {
				return cancelButton;
			}
			case Close: {
				return closeButton;
			}
			
			
		}
		
		logger.error( "WRONG Button Type!!!" );
		return null;
	}

	public void removeButton( ButtonType type ) {
		
		this.removeComponent( getButton( type ));
	}
	
	public void addClickListener( ButtonType type, ClickListener listener ) {
		
		if  ( listener != null )
			getButton( type ).addClickListener( listener );
		
	}	
	
	
	
/*	
	public boolean addButton( ButtonType type, int pos, boolean rebuild ) {
		boolean bRes = false;
		
		if ( getButton( type ) == null ) {
			// No button created yet
			Button button = createButton( type );
			if ( pos < 0 ) {
				// Add to the end of the array
				btList.add( button );
				
				bRes = true;
			} else if ( pos < btList.size()){
				// Position correct
				btList.add( pos, button );
				
				bRes = true;
			} else {
				logger.error( "Button " + type + " has wrong position specified!" );
			}
			
			
		} else {
			logger.error( "Button " + type + " exists already!" );
		}
			
		if ( rebuild )
			rebuildBar();
	
		return bRes;
	}
	
	private Button createButton( ButtonType type ) {
		
		return null;
	}
	
	private void rebuildBar() {

		for ( ButtonType type: ButtonType.values() ) {
			
		}
		
	}
	
	
	public static ButtonBar getCloseBar () {

		ButtonBar bar = new ButtonBar();
		
		
		bar.initUI( buttons );
		
		return bar;
		
	}
	
	
	protected void initUI( Button [] buttons ) {
		
		this.setWidth( "100%" );
		
		this.setMargin( true );
		this.setSpacing( true );
		
		initResources();
		
		Label glue1 = new Label( "" );
		
		for ( Button button : buttons ) {
			addComponent( button );
		}
		addComponent( glue1 );
		
		this.setExpandRatio( glue1,  1f );
	}
	
	private void initResources() {

		okButton.setCaption( "OK" );
		cancelButton.setCaption( "Cancel" );
		closeButton.setCaption( "Close" );
		
	}

	public void setEnabled( ButtonType type, boolean value ) {
		
		Button button = getButton( type );
		
		if ( button != null ) 
			button.setEnabled( value );
		else {
			okButton.setEnabled( false );
			cancelButton.setEnabled( false );
		}
	}

	public Button getButton( ButtonType type ) {
		
		switch ( type ) {
			case Ok: {
				return okButton;
			}
			case Cancel: {
				return cancelButton;
			}
			case Close: {
				return closeButton;
			}
			
			
		}
		
		logger.error( "WRONG Button Type!!!" );
		return null;
	}

	public void addButton() {
		
	}
	
	public void removeButton( ButtonType type ) {
		
		this.removeComponent( getButton( type ));
	}
	
	public void addClickListener( ButtonType type, ClickListener listener ) {
		
		if  ( listener != null )
			getButton( type ).addClickListener( listener );
		
	}
*/
}
