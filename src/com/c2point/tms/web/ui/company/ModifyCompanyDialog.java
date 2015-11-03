package com.c2point.tms.web.ui.company;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.datalayer.AuthenticationFacade;
import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.web.ui.company.model.CompaniesMgmtModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ModifyCompanyDialog extends Window implements TextChangeListener, ValueChangeListener {
	
	private static Logger logger = LogManager.getLogger( ModifyCompanyDialog.class.getName());

	private CompaniesMgmtModel	model;
    private Organisation 	organisation;

    private boolean			isNew;
    private boolean			ownerExists;
    
    private TextField 	companyCodeField;
    private TextField	nameField;
    private TextField	tunnusField;
    
    private TextField 	addressField;
    private TextField 	phoneField;
    private TextField 	companyEmailField;

	private TextArea	infoField;
    
	// Service owner data
	private ComboBox		ownerCombo;
    private TextField 		usrnameField;
    private TextField 		pwdField;
    private TextField 		codeField;
    private TextField		fNameField;
    private TextField		mNameField;
    private TextField		lNameField;
    private TextField 		emailField;
    private TextField 		mobileField;


    
    private TextArea	propsField;
    
   
	private Button saveButton;
	private Button cancelButton;

	private Label		errorNote;

	
	private boolean companyCodeChanged;
	private String  companyCodeOldValue;

	private boolean usrnameWasChanged;
	private String	usrnameOldValue;
	
	public ModifyCompanyDialog( CompaniesMgmtModel model ) {
		// Edit by default
		this( model, null );
	}

	protected ModifyCompanyDialog( CompaniesMgmtModel model, Organisation organisation ) {
		
		super();
		setModal( true );
		
		this.model = model;
		this.isNew = ( organisation == null );

		if ( isNew) {
			// New project
			// this.project = model.getNewProject();
			this.organisation = new Organisation();
		} else {
			this.organisation = organisation;
		}

        this.ownerExists = ( this.organisation.getUsersExisting() > 0 );
        	
		initView();
	}
	
	private void initView() {

		setWidth( "90%" );
		setHeight( "80%" );
//		setWidth( "36em" );
		center();

		if ( isNew ) {
			this.setCaption( model.getApp().getResourceStr( "company.add.caption" ));
		} else {
			this.setCaption( model.getApp().getResourceStr( "company.edit.caption" ));
		}
		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin( true );
		vl.setSpacing( true );
		vl.setSizeFull();

		HorizontalSplitPanel hrSplitPanel = new HorizontalSplitPanel();        
		hrSplitPanel.setSplitPosition( 60, Unit.PERCENTAGE );        
		hrSplitPanel.setSizeFull();        
		hrSplitPanel.setWidth( "100%" );        
		hrSplitPanel.setHeight( "100%" );        
		hrSplitPanel.setLocked( false );
		hrSplitPanel.addStyleName( Runo.SPLITPANEL_SMALL );
		
		Component form_1 = getOrganisationView();
		Component form_2 = getServiceOwnerView();
		Component errorBar = getErrorBar();
		Component bBar = getBottomBar();

		hrSplitPanel.addComponent( form_1 );
		hrSplitPanel.addComponent( form_2 );
		
		vl.addComponent( hrSplitPanel );
		vl.addComponent( errorBar );
		vl.addComponent( bBar );
		
		setContent( vl );
		
		form_1.setWidth( "95%" );
		form_2.setWidth( "95%" );
		bBar.setHeight( (float) (saveButton.getHeight() * 1.25), saveButton.getHeightUnits());
		
		vl.setExpandRatio( hrSplitPanel, 1 );

        dataToView();

//      nameField.focus();
        nameField.selectAll();
        
	}

	private Component getErrorBar() {
		errorNote = new Label();
		errorNote.setContentMode( ContentMode.HTML );
//		errorNote.setVisible( false );
		
		return errorNote;
	}
	
	private Component getOrganisationView() {
		
		GridLayout layout = new GridLayout( 3, 7 );
		
		layout.setMargin( new MarginInfo( true, false, false, true ));
		layout.setSpacing( true );
		
		companyCodeField = new TextField( model.getApp().getResourceStr( "general.edit.code" ) + ":" );
		companyCodeField.setTabIndex( 1 );
		companyCodeField.setWidth("8em");
		companyCodeField.setNullRepresentation( "" ); //"Enter unique Project Code ...");
		companyCodeField.setDescription( model.getApp().getResourceStr( "company.edit.code.tooltip" ));
		companyCodeField.setRequired( true );
		companyCodeField.setValidationVisible( true );
		companyCodeField.addValidator( new RegexpValidator( 
        								"[0-9]{4,}", 
        								model.getApp().getResourceStr( "company.edit.code.validator" ) 
        ));
		companyCodeField.setImmediate(true);
		companyCodeField.addTextChangeListener( new TextChangeListener() {

			@Override
			public void textChange( TextChangeEvent event ) {
		    	companyCodeChanged = true;
				
			}
        	
        });
		companyCodeField.addTextChangeListener(( TextChangeListener )this );
        

        nameField = new TextField( model.getApp().getResourceStr( "general.edit.name" ) + ":" );
        nameField.setTabIndex( 2 );
        nameField.setWidth("100%");
        nameField.setNullRepresentation( "" ); 
        nameField.setDescription( model.getApp().getResourceStr( "company.edit.name.tooltip" ));
//        nameField.setRequired( true );
        nameField.setValidationVisible( true );
        nameField.setImmediate(true);
		nameField.addTextChangeListener(( TextChangeListener )this );

        tunnusField = new TextField( model.getApp().getResourceStr( "company.edit.tunnus" ) + ":" );
        tunnusField.setTabIndex( 3 );
        tunnusField.setWidth("8em");
        tunnusField.setNullRepresentation( "" ); 
        tunnusField.setDescription( model.getApp().getResourceStr( "company.edit.tunnus.tooltip" ));
        tunnusField.setImmediate(true);
		tunnusField.addTextChangeListener(( TextChangeListener )this );

        addressField = new TextField( model.getApp().getResourceStr( "company.edit.address" ) + ":" );
        addressField.setTabIndex( 4 );
        addressField.setWidth("100%");
        addressField.setNullRepresentation( "" ); 
        addressField.setDescription( model.getApp().getResourceStr( "company.edit.address.tooltip" ));
        addressField.setRequired( false );
        addressField.setValidationVisible( true );
        addressField.setImmediate(true);
		addressField.addTextChangeListener(( TextChangeListener )this );
        
        phoneField = new TextField( model.getApp().getResourceStr( "company.edit.phone" ) + ":" );
        phoneField.setTabIndex( 5 );
        phoneField.setWidth("10em");
        phoneField.setNullRepresentation( "" ); //"Enter unique Mobile Phone Number ...");
        phoneField.setDescription( model.getApp().getResourceStr( "company.edit.phone.tooltip" ));
        phoneField.setRequired( false );
        phoneField.setValidationVisible( true );
        phoneField.setImmediate( true );
        phoneField.addTextChangeListener(( TextChangeListener )this );
        
        companyEmailField = new TextField( model.getApp().getResourceStr( "company.edit.email" ) + ":" );
        companyEmailField.setTabIndex( 6 );
        companyEmailField.setWidth("100%");
        companyEmailField.setNullRepresentation( "" ); //"Enter unique Email ...");
        companyEmailField.setDescription( model.getApp().getResourceStr( "company.edit.email.tooltip" ));
        companyEmailField.setRequired( false );
        companyEmailField.setValidationVisible( true );
        companyEmailField.addValidator( new EmailValidator( model.getApp().getResourceStr( "personnel.errors.email.validation" )));
        companyEmailField.setImmediate(true);
        companyEmailField.addTextChangeListener(( TextChangeListener )this );

        infoField = new TextArea( model.getApp().getResourceStr( "company.edit.info" ) + ":" );
        infoField.setTabIndex( 7 );
        infoField.setWordwrap( true );
        infoField.setRows( 3 );
        infoField.setWidth( "100%" );
        infoField.setHeight( "100%" );
        infoField.setNullRepresentation( "" ); 
        infoField.setImmediate( true );
        infoField.setDescription( model.getApp().getResourceStr( "company.edit.info.tooltip" ));
        infoField.addTextChangeListener(( TextChangeListener )this );
        
        propsField = new TextArea( model.getApp().getResourceStr( "company.edit.properties" ));
        propsField.setTabIndex( 8 );
        propsField.setWordwrap( true );
        propsField.setRows( 10 );
        propsField.setWidth( "100%" );
        propsField.setHeight( "100%" );
        propsField.setNullRepresentation( "" ); 
        propsField.setImmediate( true );
        propsField.setDescription( model.getApp().getResourceStr( "company.edit.properties.tooltip" ));
        propsField.addTextChangeListener(( TextChangeListener )this );
        
        
        layout.addComponent( companyCodeField,	0,	0 );
        layout.addComponent( nameField,			0,	1,	1,	1 );
        layout.addComponent( tunnusField,		2,	1 );
        layout.addComponent( addressField,		0,	2,	2, 2 );
        layout.addComponent( phoneField,		0,	3,	0,	3 );
        layout.addComponent( companyEmailField,	1,	3,	2,	3 );
        layout.addComponent( infoField,			0,	4,	2,	4 );
        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ), 0, 5, 2, 5 );
        layout.addComponent( propsField,		0,	6,	2,	6 );
        
    
        layout.setColumnExpandRatio( 1, 5 );
//        layout.setColumnExpandRatio( 2, 5 );
        
		return layout;
	}

	private Component getServiceOwnerView() {
		
		GridLayout layout = new GridLayout( 3, 15 );
		
		layout.setMargin( new MarginInfo( true, false, false, true ));
		layout.setSpacing( true );
		
		Label soLabel = new Label( model.getApp().getResourceStr( "company.edit.owner" )); 
		soLabel.setStyleName( Runo.LABEL_H1 );
		
        if ( ownerExists ) {
        	// Personnel exists. Can be selected from existing
            ownerCombo = new ComboBox();
            ownerCombo.setTabIndex( 9 );
            ownerCombo.setDescription( model.getApp().getResourceStr( "company.edit.owner.tooltip" ));
            ownerCombo.setWidth( "100%" );        
            ownerCombo.setFilteringMode( FilteringMode.CONTAINS );
            ownerCombo.setImmediate( true );        
            ownerCombo.setNullSelectionAllowed( true );
            ownerCombo.addValueChangeListener(( ValueChangeListener )this );
        } else {
        	// No Personnel. Service Owner shall be entered here!

    		usrnameField = new TextField( model.getApp().getResourceStr( "login.username" ));
    		usrnameField.setTabIndex( 9 );
    		usrnameField.setWidth("8em");
            usrnameField.setNullRepresentation( "" );
            usrnameField.setRequired( true );
            usrnameField.setValidationVisible( true );
            usrnameField.addValidator( new RegexpValidator(
    				"[a-zA-Z_.0-9]{6,14}",
    				model.getApp().getResourceStr( "personnel.errors.usrname.validation" )
            ));
            usrnameField.setImmediate(true);
            usrnameField.addTextChangeListener(( TextChangeListener )this );
            usrnameField.addValueChangeListener( new ValueChangeListener() {

				@Override
				public void valueChange( ValueChangeEvent event ) {

					usrnameField.setEnabled( true );
					usrnameWasChanged = true;
					
				}
            });


    		pwdField = new TextField( model.getApp().getResourceStr( "login.password" ));
    		pwdField.setTabIndex( 10 );
    		pwdField.setWidth("8em");
    		pwdField.setNullRepresentation( "" );
            pwdField.setRequired( true );
    		pwdField.setImmediate( true );
            pwdField.addTextChangeListener(( TextChangeListener )this );
            pwdField.addValueChangeListener( new ValueChangeListener() {

				@Override
				public void valueChange( ValueChangeEvent event ) {

					pwdField.setEnabled( true );
					
				}
            });

            
            codeField = new TextField( model.getApp().getResourceStr( "general.edit.code" ) + ":" );
            codeField.setTabIndex( 11 );
            codeField.setWidth("4em");
            codeField.setNullRepresentation( "" ); //"Enter unique Stuff Code ...");
            codeField.setDescription( model.getApp().getResourceStr( "personnel.edit.code.tooltip" ));
            codeField.setRequired( true );
            codeField.setValidationVisible( true );
            codeField.addValidator( new RegexpValidator(
            								"[a-zA-Z_0-9]{4,}",
            								model.getApp().getResourceStr( "personnel.edit.code.validator" )
            ));
            codeField.setImmediate(true);
            codeField.addTextChangeListener(( TextChangeListener )this );

            fNameField = new TextField( model.getApp().getResourceStr( "general.edit.fname" ));
            fNameField.setTabIndex( 12 );
            fNameField.setWidth("100%");
            fNameField.setNullRepresentation( "" );
            fNameField.setDescription( model.getApp().getResourceStr( "general.edit.fname.tooltip" ));
            fNameField.setValidationVisible( true );
            fNameField.setImmediate(true);
            fNameField.addTextChangeListener(( TextChangeListener )this );

            mNameField = new TextField( model.getApp().getResourceStr( "general.edit.mname" ));
            mNameField.setTabIndex( 13 );
            mNameField.setWidth("100%");
            mNameField.setNullRepresentation( "" );
            mNameField.setDescription( model.getApp().getResourceStr( "general.edit.mname.tooltip" ));
            mNameField.setValidationVisible( true );
            mNameField.setImmediate(true);
            mNameField.addTextChangeListener(( TextChangeListener )this );

            lNameField = new TextField( model.getApp().getResourceStr( "general.edit.lname" ));
            lNameField.setTabIndex( 14 );
            lNameField.setWidth("100%");
            lNameField.setNullRepresentation( "" );
            lNameField.setDescription( model.getApp().getResourceStr( "general.edit.lname.tooltip" ));
            lNameField.setValidationVisible( true );
            lNameField.setImmediate(true);
            lNameField.addValueChangeListener( new ValueChangeListener() {

    			@Override
    			public void valueChange(ValueChangeEvent event) {
                	logger.debug( "Value was changed!" );

                	if ( isNew ) {
                    	logger.debug( "New user. Username shall be created!" );

                		createUsrname();

                    	logger.debug( "New Usrname has been created!" );
                	} else {
                    	logger.debug( "User not new. Not necessary to create NEW username" );
                	}
    			}
            });
            lNameField.setTextChangeEventMode( TextChangeEventMode.LAZY);
            lNameField.addTextChangeListener(( TextChangeListener )this );

            emailField = new TextField( model.getApp().getResourceStr( "personnel.email" ));
            emailField.setTabIndex( 15 );
            emailField.setWidth("100%");
            emailField.setNullRepresentation( "" ); //"Enter unique Email ...");
            emailField.setDescription( model.getApp().getResourceStr( "personnel.email.tooltip" ));
            emailField.setRequired( false );
            emailField.setValidationVisible( true );
            emailField.addValidator( new EmailValidator( model.getApp().getResourceStr( "personnel.errors.email.validation" )));
            emailField.setImmediate(true);
            emailField.addTextChangeListener(( TextChangeListener )this );

            mobileField = new TextField( model.getApp().getResourceStr( "personnel.mobile" ));
            mobileField.setTabIndex( 16 );
            mobileField.setWidth("10em");
            mobileField.setNullRepresentation( "" ); //"Enter unique Mobile Phone Number ...");
            mobileField.setDescription( model.getApp().getResourceStr( "personnel.mobile.tooltip" ));
            mobileField.setRequired( false );
            mobileField.setValidationVisible( true );
            mobileField.setImmediate( true );
            mobileField.addTextChangeListener(( TextChangeListener )this );
            
        	
        }

        layout.addComponent( soLabel,	0,	0,	1,	0 );
        if ( ownerExists ) {

        	layout.addComponent( ownerCombo,	0,	1,	1,	1 );

        } else {
            layout.addComponent( usrnameField, 	0,  1 );
            layout.addComponent( pwdField, 		2,  1 );

            layout.addComponent( codeField, 	0,  2 );
            layout.addComponent( fNameField, 	0,  3 );
            layout.addComponent( mNameField, 	1,  3 );
            layout.addComponent( lNameField, 	2,  3 );

            layout.addComponent( emailField, 	0,  4, 1, 4 );
            layout.addComponent( mobileField, 	2,  4 );
        	
        }
        
    
        layout.setColumnExpandRatio( 1, 5 );
//        layout.setColumnExpandRatio( 2, 5 );
        
        
		return layout;
	}
	
	
	
	private void dataToView() {
		
    	if ( isNew ) {

    		companyCodeField.setValue( model.generateCompanyCode());
    		
    	} else {
    		companyCodeField.setValue( organisation.getCode());
	        nameField.setValue( organisation.getName());
	        tunnusField.setValue( organisation.getTunnus());
	        addressField.setValue( organisation.getAddress());
	        phoneField.setValue( organisation.getPhone());
	        companyEmailField.setValue( organisation.getEmail());
	    	infoField.setValue( organisation.getInfo());
	        
	        propsField.setValue( organisation.getPropString());
    	}

    	if ( !ownerExists ) {

    		codeField.setValue( model.generateServiceOwnerCode( this.organisation ));

       		usrnameOldValue = ( String )usrnameField.getValue();

    		usrnameField.setEnabled( false );
    		pwdField.setEnabled( false );

        	usrnameWasChanged = false;
        	
    	} else {
    		
    		setupOwnerCombo();
    		
    	}
		
    	companyCodeChanged = false;
    	companyCodeOldValue = ( String )companyCodeField.getValue();

    	saveButton.setEnabled( isNew );
    	if ( isNew ) {
    		saveButton.setClickShortcut( KeyCode.ENTER );
    		saveButton.addStyleName( "primary" );    	
    	} else {
    		cancelButton.setClickShortcut( KeyCode.ENTER );
    		cancelButton.addStyleName( "primary" );    	
    	}
    	
    	
	}
	
	private void setupOwnerCombo() {
		ownerCombo.removeAllItems();
		if ( model.getSelectedOrganisation() != null ) {
			// Add an item with a generated ID
			for ( TmsUser user : model.getSelectedOrganisation().getUsers().values()) {
				if ( user != null && !user.isDeleted()) {
					ownerCombo.addItem( user );
					ownerCombo.setItemCaption( user, user.getFirstAndLastNames());
					if  ( model.getSelectedOrganisation().getServiceOwner() != null
							&& model.getSelectedOrganisation().getServiceOwner().getId() == user.getId()) {
						ownerCombo.setValue( user );
					}
				}
			}
		}
		
	}

	
	
	private void viewToData() {
		if ( organisation != null ) {
			
			organisation.setCode(( String )companyCodeField.getValue());
			organisation.setName(( String )nameField.getValue());

			organisation.setTunnus(( String )tunnusField.getValue());
			organisation.setAddress(( String )addressField.getValue());
			organisation.setPhone(( String )phoneField.getValue());
			organisation.setEmail(( String )companyEmailField.getValue());
			organisation.setInfo(( String )infoField.getValue());
	       

			organisation.setPropString(( String )propsField.getValue());
			
	    	if ( ownerExists ) {
	    
	    		organisation.setServiceOwner(( TmsUser )ownerCombo.getValue());
	    		
	    	} else {
	
	    		TmsUser serviceOwner = new TmsUser(
	    	    							( String )codeField.getValue(),
	    	    				    		( String )fNameField.getValue(),
	    	    				    		( String )mNameField.getValue(),
	    	    				    		( String )lNameField.getValue()
	    	    );
	    				
				TmsAccount account = new TmsAccount(
				        					( String )usrnameField.getValue(),
				        					( String )pwdField.getValue(),
											serviceOwner
				);
				serviceOwner.setAccount( account );
			

	    		serviceOwner.setEmail(( String )emailField.getValue());
	    		serviceOwner.setMobile(( String )mobileField.getValue());


	    		serviceOwner.setLineManager( true );
	            serviceOwner.setProjectManager( true );
	    		
	    		
	    		organisation.addUser( serviceOwner );
	    		organisation.setServiceOwner( serviceOwner );

   	}
			
			
		} else {
			logger.error( "Organisation is null. Shall be NOT null." );
		}
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
					if ( logger.isDebugEnabled()) logger.debug( "Modify Organisation fields are valid!" );
					// View to date
					viewToData();
					
					// model save
					if ( isNew ) {
						// Add new Organisation
						if ( model.addOrganisation( organisation ) != null ) {
							// update UI
							
							// Close Dialog window
							dlg.close();
						} else {
							// Notify about failure
							// Stay in dialog
							Notification.show(
									model.getApp().getResourceStr( "general.errors.add.header" ),
									model.getApp().getResourceStr( "company.errors.add.body" ),
									Notification.Type.ERROR_MESSAGE
							);
						}
					} else {
						// Save modified Organisation
						if ( model.updateOrganisation( organisation ) != null ) {
							// update UI
							
							// Close Dialog window
							dlg.close();
						} else {
							// Notify about failure
							// Stay in dialog
							Notification.show(
									model.getApp().getResourceStr( "general.errors.update.header" ),
									model.getApp().getResourceStr( "company.errors.update.body" ),
									Notification.Type.ERROR_MESSAGE
							);
						}
					}
				} else {
					logger.debug( "ModifyOrganisation fields are NOT valid!" );
				}
				
			}
			
		});
		
		cancelButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick( ClickEvent event ) {
				
				dlg.close();
			}
			
		});
		
		
		
		return layout;
	}
	
	private boolean isValid() {
		boolean bRes = false;

		if ( !companyCodeField.isValid() 
			|| !validateUniqueCompanyCode(( String )companyCodeField.getValue())) {
			if ( logger.isDebugEnabled()) logger.debug( "  companyCodeField is NOT valid" );
			showErrorField( companyCodeField );
		} else if ( !nameField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  nameField is valid: " + nameField.isValid());
			showErrorField( nameField );
		} else if ( !companyEmailField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  Company Email Field is NOT valid" );
			showErrorField( companyEmailField );
		} else if ( !ownerExists && (
						!usrnameField.isValid()
					 || !validateUniqueUsrname( usrnameField.getValue()))) {
			if ( logger.isDebugEnabled()) logger.debug( "  usrnameField is NOT valid" );
			showErrorField( usrnameField );
		} else if ( !ownerExists && !codeField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  User Code Field is NOT valid" );
			showErrorField( codeField );
		} else if ( !ownerExists && !emailField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  Email Field is NOT valid" );
			showErrorField( emailField );
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
	
	private void createUsrname() {

		logger.debug( "String to generate usrname: '" + ( String )lNameField.getValue() +  "'" );
		String newUsrname = AuthenticationFacade.getInstance().getFreeUserName(( String )lNameField.getValue());
    	logger.debug( "Generated Usrname = '" + newUsrname +  "'" );

//			account.setUsrName( newUsrname );
		usrnameField.setValue( newUsrname );
		
		if ( pwdField.getValue() == null
				||
			 pwdField.getValue() != null && (( String )pwdField.getValue()).length() < 4 ) {

			createPassword();

		}

	}

	private String createPassword() {

		String newPwd = null;

		newPwd = TmsAccount.generateNewPassword();

		pwdField.setValue( newPwd );

		return newPwd;

	}

	private boolean validateUniqueCompanyCode( String code ) {
		boolean res = true;
		
		if ( companyCodeChanged && companyCodeOldValue != null && companyCodeOldValue.compareToIgnoreCase( code ) != 0 ) {
		
			Organisation org = OrganisationFacade.getInstance().getOrganisation( code );
			
			if ( org != null && org.getId() != organisation.getId() ) {
				res = false;
			}
			
		}

		if ( logger.isDebugEnabled()) logger.debug( "  Company Code validation: CompanyCode is " + ( !res ? "NOT" : "" ) +" unique" );
		
		return res;
	}

	private boolean validateUniqueUsrname( Object usrname ) {

		boolean bRes =  true;

		if ( usrnameWasChanged && usrname != null && usrnameOldValue.compareToIgnoreCase(( String ) usrname ) != 0 ) {

			TmsAccount account = AuthenticationFacade.getInstance().findByUserName(( String ) usrname );
			
			if ( account != null ) {
				
				bRes = false;
				// Create a notification

				String template = model.getApp().getResourceStr( "notify.personnel.username.exist" );
				Object[] params = { ( String )usrname };
				template = MessageFormat.format( template, params );


				Notification notif = new Notification(
						model.getApp().getResourceStr( "general.errors.update.header" ), 
						template, 
						Notification.Type.ERROR_MESSAGE, 
						true ); 				

				notif.setPosition( Position.MIDDLE_CENTER );
				notif.setDelayMsec(-1);
						
				notif.show( Page.getCurrent());
			}
		}

		return bRes;
	}

	
	@Override
	public void textChange( TextChangeEvent event ) {
    	saveButton.setEnabled( true );
		saveButton.setClickShortcut( KeyCode.ENTER );
		saveButton.addStyleName( "primary" );    	
	}

	@Override
	public void valueChange( ValueChangeEvent event ) {
		textChange( null );    	
	}
	
	
}
