package com.c2point.tms.web.ui.projectsview;

import java.text.MessageFormat;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.ConfigurationFacade;
import com.c2point.tms.entity.MeasurementUnit;
import com.c2point.tms.entity.Task;
import com.c2point.tms.util.StringUtils;
import com.c2point.tms.util.exception.NotUniqueCode;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ModifyTaskDialog extends Window implements Property.ValueChangeListener {
	
	private static Logger logger = LogManager.getLogger( ModifyTaskDialog.class.getName());
	
	private ProjectsModel	model;
    private Task			task;
    private boolean 		isNew;

    private TextField 		codeField;
    private TextField		nameField;
    
    private ComboBox		measure;

	private Button 			saveButton;
	private Button 			cancelButton;

	private Label			errorNote;
	
	public ModifyTaskDialog( ProjectsModel model, Task task ) {
		super();
		setModal(true);
		
		this.model = model;
		this.task = task;
		this.isNew = false;
		
		initView();
	}

	public ModifyTaskDialog( ProjectsModel model  ) {
		this( model, new Task( model.getOrg(), "", "" ));
		this.isNew = true;
		
	}
	
	private void initView() {

		setWidth( "24em" );
		setHeight( "25em" );

		center();

		if ( isNew ) {
			this.setCaption( model.getApp().getResourceStr( "projects.tasks.add.caption" ));
		} else {
			this.setCaption( model.getApp().getResourceStr( "projects.tasks.edit.caption" ));
		}
		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin( true );
		vl.setSpacing( true );
		vl.setSizeFull();

		Component form = getProjectView();
		Component errorBar = getErrorBar();
		Component bBar = getBottomBar();

		vl.addComponent( form );
		vl.addComponent( errorBar );
		vl.addComponent( bBar );
		
//		form.setHeight( "26em" );
		form.setWidth( "100%" );
//		bBar.setHeight( "15em" ); 
		bBar.setHeight( (float) (saveButton.getHeight() * 1.25), saveButton.getHeightUnits());
//		bBar.setWidth( "100%" );
		
		this.setContent( vl );
		
		vl.setExpandRatio( form, 1 );
	
	}

	private Component getErrorBar() {
		errorNote = new Label();
		errorNote.setContentMode( ContentMode.HTML );
//		errorNote.setVisible( false );
		
		return errorNote;
	}
	
	private Component getProjectView() {
		
		GridLayout layout = new GridLayout( 1, 3 );
		
		layout.setMargin( new MarginInfo( true, false, false, true ));
		layout.setSpacing( true );
		
        codeField = new TextField( model.getApp().getResourceStr( "general.edit.code" ) + ":" );
        codeField.setWidth("8em");
        codeField.setNullRepresentation( "" );
        codeField.setDescription( model.getApp().getResourceStr( "projects.tasks.edit.code.tooltip" ));
        codeField.setRequired( true );
        codeField.setValidationVisible( true );
        codeField.addValidator( new RegexpValidator( 
        								"[0-9]{3,}", 
        								model.getApp().getResourceStr( "projects.tasks.edit.code.validator" ) 
        ));
        codeField.setImmediate(true);
        

        nameField = new TextField( model.getApp().getResourceStr( "general.edit.name" ) + ":" );
        nameField.setWidth("100%");
        nameField.setNullRepresentation( "" );
        nameField.setDescription( model.getApp().getResourceStr( "projects.tasks.edit.name.tooltip" ));
        nameField.setRequired( true );
        nameField.setImmediate(true);
        
//        measure = new ComboBox( model.getApp().getResourceStr( "general.label.measure" ) + ":" );
        measure = new ComboBox( "Unit" + ":" );
        measure.setWidth( "5em" );
        measure.setNullSelectionAllowed( true );
//        measure.setDescription( model.getApp().getResourceStr( "general.label.measure.tooltip" ));
        measure.setDescription( "Select measurement unit" );
        measure.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
        
        measure.setRequired( true );
        measure.setImmediate(true);
        
        layout.addComponent( codeField, 0, 0 );
        layout.addComponent( nameField, 0, 1 );
        layout.addComponent( measure,   0, 2 );
       
        dataToView();
        
        validateUI();

		return layout;
	}
	
	private void dataToView() {
		
		if ( task != null ) {
			
			if ( isNew ) {
		        codeField.setValue( uniqueCode());
		        nameField.setValue( "" );

		        initMeasurementCombo( null );
		        
			} else {
		        codeField.setValue( task.getCode());
		        nameField.setValue( task.getName());

		        initMeasurementCombo( task.getMeasurementUnit());
			}
			
			

		} else {
			logger.error( "Task is null. Shall be NOT null." );
		}
	}

	private void viewToData() {
		if ( task != null ) {
			
	        task.setCode(( String )codeField.getValue());
	        task.setName(( String )nameField.getValue());
		    task.setMeasurementUnit(( MeasurementUnit )measure.getValue());
		} else {
			logger.error( "Project is null. Shall be NOT null." );
		}
	}
	
    private void initMeasurementCombo( MeasurementUnit unit ) {
    	
    	Collection<MeasurementUnit> cmu = ConfigurationFacade.getSupportedMeasurement();

		measure.addItems( cmu );

    	for ( MeasurementUnit mu : cmu ) {
    		
    		measure.setItemCaption( mu, mu.getName());
    		
    	}
    	
    	if ( unit != null ) {
    		measure.setValue( unit );
    	}
    		
    	
    }
	
	
	
	private void validateUI() {
//		nameField.setReadOnly( true );
	}
	
	private Component getBottomBar() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin( true );
		layout.setSpacing( true );
//		layout.setSizeFull();

		saveButton = new Button( model.getApp().getResourceStr( "general.button.ok" ));
		cancelButton = new Button( model.getApp().getResourceStr( "general.button.cancel" ));
		
		layout.addComponent( saveButton );
		layout.addComponent( cancelButton );
		
		layout.setComponentAlignment( saveButton, Alignment.MIDDLE_LEFT );
		layout.setComponentAlignment( cancelButton, Alignment.MIDDLE_LEFT );
		
		
		final Window dlg = this;
		saveButton.addClickListener( new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				// Validate. if Validated()
				if ( isValid()) {
					logger.debug( "ModifyTask fields are valid!" );
					// View to date
					viewToData();

					try {
						if ( isNew ) {
							model.addTask( task );
						} else {
							model.updateTask( task );
						}

						dlg.close();
					
					} catch ( NotUniqueCode e ) {
						Notification.show(
								model.getApp().getResourceStr( "general.errors.update.header" ),
								model.getApp().getResourceStr( "projects.tasks.errors.update.body_2" ),
								Type.ERROR_MESSAGE
						);
						
					}
					
				} else {
					logger.debug( "Modify Task fields are NOT valid! Stayed in dialog" );
				}
				
			}
			
		});
		
		cancelButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				dlg.close();
			}
			
		});
		
		
		
		return layout;
	}
	
	private boolean isValid() {
		boolean bRes = false;

		if ( !codeField.isValid() ) {
			if ( logger.isDebugEnabled()) logger.debug( "  codeField is valid: " + codeField.isValid());
			showErrorField( codeField );
		} else {
			
			clearErrorField();

			bRes = true;
		}

		return bRes;
	}
	
	private void clearErrorField() {
		errorNote.setVisible( false );
		errorNote.setValue( "" );
	}

	@SuppressWarnings("rawtypes")
	private void showErrorField( AbstractField field ) {
		
		String errorMsg = model.getApp().getResourceStr( "general.error.validation.msg" ); 

		Object[] params = { field.getCaption() };
		errorMsg = MessageFormat.format( errorMsg, params );
		
		errorNote.setVisible( true );
		errorNote.setValue( errorMsg );

		if ( field != null ) {
			field.focus();
			if ( field instanceof AbstractTextField ) {
				((AbstractTextField) field).selectAll();
			}
		}
	}

	@Override
	public void valueChange(ValueChangeEvent event) {

		
	}

	private String uniqueCode() {
		
		long current;
		
		try {
			current = model.getOrg().getTasks().size() + 1;
		} catch ( Exception e ) {
			current = 1;
		}
		
		String currentStr = StringUtils.padLeftZero( current, 4 );
		
		while ( model.getOrg().getTask( currentStr ) != null ) {
			currentStr = StringUtils.padLeftZero( current++, 4 );
		}
		
		return currentStr;
	}

}
