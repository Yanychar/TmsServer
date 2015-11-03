package com.c2point.tms.web.ui.stuff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TmsUser;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PersonalProfileDialog extends Window {
	
	private static Logger logger = LogManager.getLogger( PersonalProfileDialog.class.getName());
	
	private PersonalProfileModel	model;
	
    private TmsUser 		user;

    private TextField 		usrnameField;
//    private Button 			newPwdButton;
    
    
    private TextField 		codeField;
    private TextField		fNameField;
    private TextField		mNameField;
    private TextField		lNameField;
    
    private TextField 		kelaField;
    private TextField 		taxField;
    private TextField 		addressField;

    private TextField 		emailField;
    private TextField 		mobileField;
    
	private Button 			saveButton;
//	private Button 			cancelButton;

	private Label			errorNote;

//	private boolean 		pwdWasChanged;
	
	public PersonalProfileDialog( PersonalProfileModel model ) {
		super();
		setModal( true );
		
		this.model = model;

		this.user = model.getSessionOwner();
		
		initView();
	}
	
	private void initView() {
		setWidth( "36em" );
//		setHeight( "80%" );
		center();

		this.setCaption( model.getApp().getResourceStr( "personnel.edit.caption" ));
		
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
		
		GridLayout layout = new GridLayout( 3, 15 );
		
		layout.setMargin( new MarginInfo( true, false, false, true ));
		layout.setSpacing( true );
		
		usrnameField = new TextField( model.getApp().getResourceStr( "login.username" ));
		usrnameField.setWidth("8em");
        usrnameField.setNullRepresentation( "" );
        usrnameField.setValidationVisible( true );
        
/*        
        newPwdButton = new Button( "New Password" );// model.getApp().getResourceStr( "general.edit.fname" ));
        newPwdButton.addListener( new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				creatrePassword();
			}
        });
*/        
        codeField = new TextField( model.getApp().getResourceStr( "general.edit.code" ) + ":" );
        codeField.setWidth("4em");
        codeField.setNullRepresentation( "" ); //"Enter unique Project Code ...");
        codeField.setDescription( model.getApp().getResourceStr( "personnel.edit.code.tooltip" ));
        codeField.setRequired( true );
        codeField.setValidationVisible( true );
        codeField.addValidator( new RegexpValidator( 
        								"[a-zA-Z_0-9]{4,}", 
        								model.getApp().getResourceStr( "personnel.edit.code.validator" ) 
        ));
        codeField.setImmediate(true);
        

        fNameField = new TextField( model.getApp().getResourceStr( "general.edit.fname" ));
        fNameField.setWidth("100%");
        fNameField.setNullRepresentation( "" ); 
        fNameField.setDescription( model.getApp().getResourceStr( "general.edit.fname.tooltip" ));
        fNameField.setValidationVisible( true );
        fNameField.setImmediate(true);

        mNameField = new TextField( model.getApp().getResourceStr( "general.edit.mname" ));
        mNameField.setWidth("100%");
        mNameField.setNullRepresentation( "" ); 
        mNameField.setDescription( model.getApp().getResourceStr( "general.edit.mname.tooltip" ));
        mNameField.setValidationVisible( true );
        mNameField.setImmediate(true);

        lNameField = new TextField( model.getApp().getResourceStr( "general.edit.lname" ));
        lNameField.setWidth("100%");
        lNameField.setNullRepresentation( "" ); 
        lNameField.setDescription( model.getApp().getResourceStr( "general.edit.lname.tooltip" ));
        lNameField.setValidationVisible( true );
        lNameField.setImmediate(true);

        kelaField = new TextField( model.getApp().getResourceStr( "personnel.kelacode" ));
        kelaField.setWidth("8em");
        kelaField.setNullRepresentation( "" ); //"Enter unique Project Code ...");
        kelaField.setDescription( model.getApp().getResourceStr( "personnel.kelacode.tooltip" ));
        kelaField.setRequired( false );
        kelaField.setValidationVisible( true );
        kelaField.setImmediate(true);

        taxField = new TextField( model.getApp().getResourceStr( "personnel.taxnumber" ));
        taxField.setWidth("8em");
        taxField.setNullRepresentation( "" ); //"Enter unique Tax Number ...");
        taxField.setDescription( model.getApp().getResourceStr( "personnel.taxnumber.tooltip" ));
        taxField.setRequired( false );
        taxField.setValidationVisible( true );
        taxField.setImmediate( true );
        
        addressField = new TextField( model.getApp().getResourceStr( "personnel.address" ));
        addressField.setWidth("100%");
        addressField.setNullRepresentation( "" ); 
        addressField.setDescription( model.getApp().getResourceStr( "personnel.address.tooltip" ));
        addressField.setRequired( false );
        addressField.setValidationVisible( true );
        addressField.setImmediate(true);
        
        emailField = new TextField( model.getApp().getResourceStr( "personnel.email" ));
        emailField.setWidth("100%");
        emailField.setNullRepresentation( "" ); //"Enter unique Email ...");
        emailField.setDescription( model.getApp().getResourceStr( "personnel.email.tooltip" ));
        emailField.setRequired( false );
        emailField.setValidationVisible( true );
        emailField.addValidator( new EmailValidator( model.getApp().getResourceStr( "personnel.errors.email.validation" )));
        emailField.setImmediate(true);

        mobileField = new TextField( model.getApp().getResourceStr( "personnel.mobile" ));
        mobileField.setWidth("10em");
        mobileField.setNullRepresentation( "" ); //"Enter unique Mobile Phone Number ...");
        mobileField.setDescription( model.getApp().getResourceStr( "personnel.mobile.tooltip" ));
        mobileField.setRequired( false );
        mobileField.setValidationVisible( true );
        mobileField.setImmediate( true );
        
        layout.addComponent( usrnameField, 0,  0 );
 //       layout.addComponent( newPwdButton, 1,  0 );
        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ), 0,  2, 2, 2 );

        layout.addComponent(  codeField, 0,  3 );
        layout.addComponent( fNameField, 0,  4 );
        layout.addComponent( mNameField, 1,  4 );
        layout.addComponent( lNameField, 2,  4 );

        layout.addComponent( addressField, 	0,  5, 2, 5 );
        layout.addComponent( kelaField, 	0,  6 );
        layout.addComponent( taxField, 		1,  6 );
        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ), 0,  7, 2, 7 );

        layout.addComponent( emailField, 	0,  8, 1, 8 );
        layout.addComponent( mobileField, 	2,  8 );
        
        layout.setColumnExpandRatio( 1, 1 );
        layout.setColumnExpandRatio( 2, 5 );
 //       layout.setComponentAlignment( newPwdButton, Alignment.BOTTOM_LEFT );
        
        dataToView();
        
		return layout;
	}
	
	private void dataToView() {
		
		if ( user != null ) {
			
		    usrnameField.setValue( user.getAccount().getUsrName());
//		    pwdWasChanged = false;
			
	        codeField.setValue( user.getCode());
	        fNameField.setValue( user.getFirstName());
	        mNameField.setValue( user.getMidName());
	        lNameField.setValue( user.getLastName());

		    kelaField.setValue( user.getKelaCode());
		    taxField.setValue( user.getTaxNumber());
		    addressField.setValue( user.getAddress());
		    
		    emailField.setValue( user.getEmail());
		    mobileField.setValue( user.getMobile());
	        
		} else {
			logger.error( "TmsUser is null. Shall be NOT null." );
		}

        usrnameField.setReadOnly( true );
		codeField.setReadOnly( true );
        fNameField.setReadOnly( true );
        mNameField.setReadOnly( true );
        lNameField.setReadOnly( true );
	    kelaField.setReadOnly( true );
	    taxField.setReadOnly( true );
	    addressField.setReadOnly( true );
	    
	    emailField.setReadOnly( true );
	    mobileField.setReadOnly( true );
	}
	
	private Component getBottomBar() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin( true );
		layout.setSpacing( true );
//		layout.setSizeFull();

		saveButton = new Button( model.getApp().getResourceStr( "general.button.ok" ));
//		cancelButton = new Button( model.getApp().getResourceStr( "general.button.cancel" ));
		
		layout.addComponent( saveButton );
//		layout.addComponent( cancelButton );
		
		layout.setComponentAlignment( saveButton, Alignment.MIDDLE_LEFT );
//		layout.setComponentAlignment( cancelButton, Alignment.MIDDLE_LEFT );
		
		
		final Window dlg = this;
		saveButton.addClickListener( new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				dlg.close();

/*
				// Validate. if Validated()
				if ( pwdWasChanged ) {		
					if ( logger.isDebugEnabled()) logger.debug( "Modify User fields are valid!" );
					// View to date
					user.getAccount().setPwd( tmpPwd );
					
					// model save
					// Save modified Project
					if ( model.updateUser( user ) != null ) {
						// update UI
						
						// Close Dialog window
						dlg.getParent().removeWindow( dlg );
					} else {
						// Notify about failure
						// Stay in dialog
						getWindow().showNotification(
								model.getApp().getResourceStr( "general.errors.update.header" ),
								model.getApp().getResourceStr( "personnel.errors.update.body" ),
								Notification.TYPE_ERROR_MESSAGE
						);
					}
				} else {
					dlg.getParent().removeWindow( dlg );
				}
*/
			}
			
		});
/*		
		cancelButton.addListener( new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				dlg.getParent().removeWindow( dlg );
			}
			
		});
*/		
		
		
		return layout;
	}
	
}
