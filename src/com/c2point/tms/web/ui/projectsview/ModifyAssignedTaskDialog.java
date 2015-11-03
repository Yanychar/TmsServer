package com.c2point.tms.web.ui.projectsview;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.util.exception.NotUniqueCode;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ModifyAssignedTaskDialog extends Window implements Property.ValueChangeListener {
	
	private static Logger logger = LogManager.getLogger( ModifyAssignedTaskDialog.class.getName());
	
	private ProjectsModel	model;
    private ProjectTask		pTask;

    private TextField 	codeInProjectField;
    private Label	nameField;

	private Button saveButton;
	private Button cancelButton;

	private Label		errorNote;
	
	public ModifyAssignedTaskDialog( ProjectsModel model, ProjectTask pTask ) {
		super();
		setModal(true);
		
		this.model = model;
		this.pTask = pTask;
		
		initView();
	}
	
	private void initView() {
		setWidth( "24em" );
		setHeight( "20em" );
		center();

		this.setCaption( model.getApp().getResourceStr( "projects.tasks.edit.caption" ));
		
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
		errorNote.setContentMode( Label.CONTENT_XHTML );
//		errorNote.setVisible( false );
		
		return errorNote;
	}
	
	private Component getProjectView() {
		
		GridLayout layout = new GridLayout( 1, 2 );
		
		layout.setMargin( new MarginInfo( true, false, false, true ));
		layout.setSpacing( true );
		
        codeInProjectField = new TextField( model.getApp().getResourceStr( "general.edit.code" ) + ":" );
        codeInProjectField.setWidth("8em");
        codeInProjectField.setNullRepresentation( "" );
        codeInProjectField.setDescription( model.getApp().getResourceStr( "projects.tasks.edit.code.tooltip" ));
        codeInProjectField.setRequired( true );
        codeInProjectField.setValidationVisible( true );
        codeInProjectField.addValidator( new RegexpValidator( 
        								"[0-9]{6,}", 
        								model.getApp().getResourceStr( "projects.tasks.edit.code.validator" ) 
        ));
        codeInProjectField.setImmediate(true);
        

        nameField = new Label( model.getApp().getResourceStr( "general.edit.name" ) + ":" );
        nameField.setWidth("100%");
        nameField.setDescription( model.getApp().getResourceStr( "projects.tasks.edit.name.tooltip" ));
        nameField.setImmediate(true);
        nameField.setContentMode(Label.CONTENT_XHTML);
        
        layout.addComponent( codeInProjectField, 0, 0 );
        layout.addComponent( nameField, 0, 1 );
       
        dataToView();
        
        validateUI();

		return layout;
	}
	
	private void dataToView() {
		
		if ( pTask != null ) {
			
	        codeInProjectField.setValue( pTask.getCodeInProject());
	        nameField.setValue( model.getApp().getResourceStr( "general.edit.task" + ": " ) 
	        					+ "<br>" + "<b>" + pTask.getTask().getName() + "</b>" );

		} else {
			logger.error( "TaskInProject is null. Shall be NOT null." );
		}
	}

	private void viewToData() {
		if ( pTask != null ) {
			
	        pTask.setCodeInProject(( String )codeInProjectField.getValue());
		    
		} else {
			logger.error( "Project is null. Shall be NOT null." );
		}
	}
	
	
	
	
	private void validateUI() {
		nameField.setReadOnly( true );
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
		saveButton.addListener( new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				// Validate. if Validated()
				if ( isValid()) {
					logger.debug( "ModifyAssignedTask fields are valid!" );
					// View to date
					viewToData();
					
					try {
						model.updateProjectTask( pTask );

						dlg.close();
					
					} catch ( NotUniqueCode e ) {
						Notification.show(
								model.getApp().getResourceStr( "general.errors.update.header" ),
								model.getApp().getResourceStr( "projects.tasks.errors.update.body" ),
								Notification.TYPE_ERROR_MESSAGE
						);
						
					}
					
				} else {
					logger.debug( "Modify Task fields are NOT valid! Stayed in dialog" );
				}
				
			}
			
		});
		
		cancelButton.addListener( new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				dlg.close();
			}
			
		});
		
		
		
		return layout;
	}
	
	private boolean isValid() {
		boolean bRes = false;

		if ( !codeInProjectField.isValid() ) {
			if ( logger.isDebugEnabled()) logger.debug( "  codeField is valid: " + codeInProjectField.isValid());
			showErrorField( codeInProjectField );
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
	

}
