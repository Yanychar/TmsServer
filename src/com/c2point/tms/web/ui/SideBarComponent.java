package com.c2point.tms.web.ui;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SideBarComponent extends VerticalLayout {
	
	private static Logger logger = LogManager.getLogger( SideBarComponent.class.getName());
	
	private static int		DEFAULT_BUTTON_HEIGHT = 3; 
//	private static int		DEFAULT_BUTTON_WIDTH = 10;
	
	private static String	STYLE_SELECTED = "v-sidebar-selected";  
	
	private ArrayList<NativeButton>	buttons;
	private int						selectedButtonId;

	private Label glue;
	
	public SideBarComponent() {
		super();

		buttons = new ArrayList<NativeButton>();
		selectedButtonId = -1;
		
		initView();
	}
	
	public void addListener( Button.ClickListener listener ) {
		for ( Iterator<Component> iter = this.iterator(); iter.hasNext(); ) {
			Component c = iter.next();
			if ( c instanceof NativeButton ) {
				(( Button )c ).addClickListener( listener );
			}
		}
	}

	public int addItemButton( String caption ) {
		int res = -1;
		
		NativeButton button = new NativeButton( caption );
		button.setHeight( DEFAULT_BUTTON_HEIGHT + "em" );
		button.setWidth( "100%" );
		
		button.addClickListener( new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				logger.debug( "Button '" + event.getButton().getCaption() + "' was clicked" );
				setSelected( event.getButton()); 
				
			}
			
		});
		
		// Delete last Label component
		this.removeComponent( glue );
		// Add Button
		this.addComponent( button );

		// Add Label component to the end as glue component
		this.addComponent( glue );
		this.setExpandRatio( glue, 1.0f );
		
		res = addButton( button );
		button.setData( res );
		
		return res;
	}

	public void clickButton( int id ) {
		Button button = this.getButton( id );
		if ( button != null ) {
			button.click();
		}
	}
	
	private void setSelected( int id ) {
		if ( selectedButtonId != id && id > 0 ) {
			logger.debug( "Selection changed: " + selectedButtonId + " ==>> " + id );
			selectedButtonId = id;
			
			updateButtons();
			getButton( selectedButtonId ).addStyleName( STYLE_SELECTED );
			
		} else {
			logger.debug( "Selection CANNOT be changed from " + selectedButtonId + " to " + id );
			
		}
	}

	
	
	private void initView() {

		this.setHeightUndefined();
		this.setWidth( "100%" ); // Sizeable.SIZE_UNDEFINED, 0 );
		this.setImmediate( true );

		glue = new Label( "" );
		glue.setHeight("100%");
	}

	private void setSelected ( Button button ) {
		setSelected( getButtonId( button ));
	}
	
	private void updateButtons() {

		logger.debug( "  Clear selected style from all child components" );

		for ( Iterator<Component> iter = this.iterator(); iter.hasNext(); ) {
			Component c = iter.next();
			c.removeStyleName( STYLE_SELECTED );		
		}
		
	}
	
	/* 
	 * Methods to hide Implementation of buttons array
	 */
	// Get button using its ID
	private Button getButton( int id ) {
		try {
			return buttons.get( idToIndex( id ));
		} catch ( Exception e ) {
			logger.debug( "Wrong ID of Button: " + id );
		}
		
		return null;
	}

	  // Return -1 if not found
	private int getButtonId( Button button ) {
		return indexToId( buttons.indexOf( button ));
	}
	
	// Returns Button ID
	private int addButton( NativeButton button ) {
		int res = -1;
		
		if ( buttons.add( button )) {
			res = getButtonId( button );
		}
		
		return res;
	}
	
	private int idToIndex( int id ) { return id - 1; }
	private int indexToId( int index ) { return index + 1; }
	
}
