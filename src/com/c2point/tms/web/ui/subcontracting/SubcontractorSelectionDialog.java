package com.c2point.tms.web.ui.subcontracting;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.web.ui.buttonbar.ButtonBar;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SubcontractorSelectionDialog extends Window {
	
	private static Logger logger = LogManager.getLogger( SubcontractorSelectionDialog.class.getName());
	
	private SubcontractingModel	model;

	private OrgsSelectionListComponent listComponent;
	
	
	public SubcontractorSelectionDialog( SubcontractingModel model ) {
		super();
		setModal(true);
		
		this.model = model;
		
		initView();
	}
	
	private void initView() {

		setHeight( "80%" );
		setWidth( "100ex" );


		this.setCaption( "???" ); //model.getApp().getResourceStr( "projects.tasks.edit.caption" ));
		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin( true );
		vl.setSpacing( true );
		vl.setSizeFull();

		listComponent = getOrgsListComponent();
		Component bBar = getBottomBar();

		vl.addComponent( listComponent );
		vl.addComponent( bBar );
		
		this.setContent( vl );
		
		vl.setExpandRatio( listComponent, 1 );
	
	}

	private OrgsSelectionListComponent getOrgsListComponent() {
		
		OrgsSelectionListComponent component = new OrgsSelectionListComponent( model );
		
		return component;
	}
	
	private Component getBottomBar() {

		ButtonBar btb = ButtonBar.getOkCancelBar();
		btb.setEnabled( ButtonBar.ButtonType.Ok, true );
		btb.addClickListener( ButtonBar.ButtonType.Ok, new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event ) {

				listComponent.storeChanges();
				
				close();
			}
			
		});
		
		btb.addClickListener( ButtonBar.ButtonType.Cancel, new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				close();
				
			}
			
		});
		
		
		
		return btb;
	}
	
	

}
