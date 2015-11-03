package com.c2point.tms.web.ui.stuff;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TmsAccount;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.access.SecurityGroup;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.UserError;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
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
public class ModifyStuffDialog extends Window implements ValueChangeListener {

	private static Logger logger = LogManager.getLogger( ModifyStuffDialog.class.getName());

	private StuffMgmtModel	model;
    private TmsUser 		user;
    private boolean			isNew;

    private TextField 		usrnameField;
    private TextField 		pwdField;
    private Button 			newPwdButton;


    private TextField 		codeField;
    private TextField		fNameField;
    private TextField		mNameField;
    private TextField		lNameField;

    private TextField 		kelaField;
    private TextField 		taxField;
    private TextField 		addressField;

    private TextField 		emailField;
    private TextField 		mobileField;

    private ComboBox		managerField;

    private CheckBox		isLineManager;
    private CheckBox		isProjectManager;

    private ComboBox		secGroupField;

	private Button 			saveButton;
	private Button 			cancelButton;

	private Label			errorNote;

	private boolean 		usrnameWasChanged;
	private boolean 		pwdWasChanged;
	private boolean 		codeWasChanged;

	public ModifyStuffDialog( StuffMgmtModel model ) {
		// Edit by default
		this( model, null );
	}

	protected ModifyStuffDialog( StuffMgmtModel model, TmsUser user ) {
		
		super();
		setModal( true );

		this.model = model;
		this.isNew = ( user == null );

		if ( isNew) {
			this.user = new TmsUser();
			TmsAccount account = new TmsAccount();
			this.user.setAccount( account );
		} else {
			this.user = user;
		}

		initView();
	}

	private void initView() {

		setWidth( "40em" );
		setHeight( "80%" );
//		this.setSizeUndefined();
		center();

		this.setCaption( model.getApp().getResourceStr( "personnel.edit.caption" ));
		if ( isNew ) {
			this.setCaption( model.getApp().getResourceStr( "personnel.add.caption" ));
		} else {
			this.setCaption( model.getApp().getResourceStr( "personnel.edit.caption" ));
		}


		VerticalLayout vl = new VerticalLayout();
		vl.setMargin( true );
		vl.setSpacing( true );
		vl.setSizeFull();

		Component form = getStuffView();
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

	private Component getStuffView() {

		GridLayout layout = new GridLayout( 3, 15 );

		layout.setMargin( new MarginInfo( true, false, false, true ));
		layout.setSpacing( true );

		usrnameField = new TextField( model.getApp().getResourceStr( "login.username" ));
		usrnameField.setWidth("8em");
        usrnameField.setNullRepresentation( "" );
        usrnameField.setValidationVisible( true );
        usrnameField.addValidator( new RegexpValidator(
				"[a-zA-Z_.0-9]{6,14}",
				model.getApp().getResourceStr( "personnel.errors.usrname.validation" )
        ));
        usrnameField.setImmediate(true);
        usrnameField.setReadOnly( isNew );
        usrnameField.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
            	logger.debug( "Username was changed!" );

            	if ( !isNew ) {
        			usrnameWasChanged = true;
            	}
			}
        });



		pwdField = new TextField( model.getApp().getResourceStr( "login.password" ));
		pwdField.setWidth("8em");
		pwdField.setNullRepresentation( "" );
		pwdField.setImmediate( true );
		pwdField.setReadOnly( true );

        newPwdButton = new Button( model.getApp().getResourceStr( "personnel.newpwd.button" ));
        newPwdButton.setDescription( model.getApp().getResourceStr( "personnel.newpwd.button.tooltip" ));
        newPwdButton.addClickListener( new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				creatrePassword();

			}
        });

        codeField = new TextField( model.getApp().getResourceStr( "general.edit.code" ) + ":" );
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
        codeField.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
            	logger.debug( "Code was changed!" );

       			codeWasChanged = true;

			}
        });


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

        kelaField = new TextField( model.getApp().getResourceStr( "personnel.kelacode" ));
        kelaField.setWidth("8em");
        kelaField.setNullRepresentation( "" ); //"Enter unique Social Security Code ...");
        kelaField.setDescription( model.getApp().getResourceStr( "personnel.kelacode.tooltip" ));
        kelaField.setRequired( false );
        kelaField.setValidationVisible( true );
//        kelaField.addValidator( new RegexpValidator(
//        								"\\d{6}-\\d{3}\\w",
//        								"Social security code is unvalid. Enter it in right format ??????-????" //model.getApp().getResourceStr( "personnel.edit.code.validator" )
//        ));
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

        managerField = new ComboBox( model.getApp().getResourceStr( "personnel.edit.manager" ) + ":" );
        managerField.setDescription( model.getApp().getResourceStr( "personnel.edit.manager.tooltip" ));
        managerField.setWidth( "100%" );
        managerField.setFilteringMode( FilteringMode.CONTAINS );
        managerField.setImmediate( true );
        managerField.setNullSelectionAllowed( true );

        isLineManager = new CheckBox( model.getApp().getResourceStr( "personnel.edit.linemanager" ));
        isLineManager.setDescription( model.getApp().getResourceStr( "personnel.edit.linemanager.tooltip" ));
        isProjectManager = new CheckBox( model.getApp().getResourceStr( "personnel.edit.projectmanager" ));
        isProjectManager.setDescription( model.getApp().getResourceStr( "personnel.edit.projectmanager.tooltip" ));

        secGroupField = new ComboBox( model.getApp().getResourceStr( "general.edit.group" ));
        secGroupField.setDescription( model.getApp().getResourceStr( "personnel.edit.secgroup.tooltip" ));
        secGroupField.setWidth( "100%" );
        secGroupField.setFilteringMode( FilteringMode.CONTAINS );
        secGroupField.setImmediate( true );
        secGroupField.setNullSelectionAllowed( true );


        layout.addComponent( usrnameField, 	0,  0 );
        layout.addComponent( pwdField, 		1,  0 );
        layout.addComponent( newPwdButton, 	2,  0 );

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

        if ( model.getToShowFilter() != SupportedFunctionType.PERSONNEL_OWN ) {
	        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ), 0,  9, 2, 9 );

	        layout.addComponent( managerField,  	0, 10, 1, 10  );
	        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ), 0, 11, 2, 11 );
	        layout.addComponent( isLineManager,  	0, 12  );
	        layout.addComponent( isProjectManager,  1, 12  );
	        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ), 0, 13, 2, 13 );
	        layout.addComponent( secGroupField,  0, 14  );
        }

        layout.setColumnExpandRatio( 1, 1 );
        layout.setColumnExpandRatio( 2, 5 );
        layout.setComponentAlignment( newPwdButton, Alignment.BOTTOM_LEFT );

        dataToView();

        validateUI();

		return layout;
	}

	private void dataToView() {

		if ( user != null ) {

			if ( user.getAccount() != null ) {
			    usrnameField.setValue( user.getAccount().getUsrName());
			} else {
				logger.debug( "TmsAccount is null for " + user );
			}

		    kelaField.setValue( user.getKelaCode());
		    taxField.setValue( user.getTaxNumber());
		    addressField.setValue( user.getAddress());

		    emailField.setValue( user.getEmail());
		    mobileField.setValue( user.getMobile());

        	if ( isNew ) {
        		createCode();
        	} else {
    	        codeField.setValue( user.getCode());
        	}
	        fNameField.setValue( user.getFirstName());
	        mNameField.setValue( user.getMidName());
	        lNameField.setValue( user.getLastName());

	        if ( model.getToShowFilter() != SupportedFunctionType.PERSONNEL_OWN ) {
		        setupCombo();

		        isLineManager.setValue( user.isLineManager());
		        isProjectManager.setValue( user.isProjectManager());
	        }

			usrnameWasChanged = false;
		    pwdWasChanged = false;
		    codeWasChanged = false;

		} else {
			logger.error( "TmsUser is null. Shall be NOT null." );
		}
	}

	private void setupCombo() {

		// Setup list of managers
		managerField.addContainerProperty( "name", String.class, "");
		managerField.setItemCaptionMode( ItemCaptionMode.PROPERTY );
		managerField.setItemCaptionPropertyId( "name" );

		for ( TmsUser tmpUser : model.getUserList()) {
			if ( tmpUser.isLineManager() ||
					this.user.getManager() != null && tmpUser.getId() == this.user.getManager().getId()) {

				managerField.addItem( tmpUser ).getItemProperty( "name" ).setValue( tmpUser.getFirstAndLastNames());

				// Select current manager
				if ( user.getManager() != null && tmpUser.getId() == user.getManager().getId()) {
					managerField.setValue( tmpUser );
				}

			}
		}

		// Setup list of Security Groups
		secGroupField.addContainerProperty( "name", String.class, "");
		secGroupField.setItemCaptionMode( ItemCaptionMode.PROPERTY );
		secGroupField.setItemCaptionPropertyId( "name" );

		for ( SecurityGroup group : model.getAvailableSecurityGroups()) {
			if ( group != null && !group.isDeleted()) {

				secGroupField.addItem( group ).getItemProperty( "name" ).setValue( group.getDefName());

				// Select current SecurityGroup
				if ( user.getContext().getSecGroup() != null && user.getContext().getSecGroup().getId() == group.getId()) {
					secGroupField.setValue( group );
				}

			}
		}
	}

	private void viewToData() {
		if ( user != null ) {

			TmsAccount account = user.getAccount();
			if ( account == null ) {
				account = new TmsAccount();
				this.user.setAccount( account );
			}



			if ( usrnameWasChanged ) {
		        account.setUsrName(( String )usrnameField.getValue());
			}
			if ( pwdWasChanged  ) {
				account.setPwd( ( String )pwdField.getValue() );
			}

			user.setKelaCode(( String )kelaField.getValue());
		    user.setTaxNumber(( String )taxField.getValue());
		    user.setAddress(( String )addressField.getValue());

		    user.setEmail(( String )emailField.getValue());
		    user.setMobile(( String )mobileField.getValue());

			if ( codeWasChanged || isNew ) {
				user.setCode(( String )codeField.getValue());
			}
	        user.setFirstName(( String )fNameField.getValue());
	        user.setMidName(( String )mNameField.getValue());
	        user.setLastName(( String )lNameField.getValue());

	        if ( model.getToShowFilter() != SupportedFunctionType.PERSONNEL_OWN ) {

		        Object obj;

		        // Get who is the manager
		        obj = managerField.getValue();
		        if ( obj != null && obj instanceof TmsUser ) {
		        	if ( logger.isDebugEnabled()) logger.debug( "Line Manager was changed to: " + obj );
		        	(( TmsUser )obj ).addSubordinate( this.user );
		        } else {
		        	if ( logger.isDebugEnabled()) logger.debug( "Line Manager was changed to: NULL" );
		        	if ( this.user.getManager() != null ) {
		        		this.user.getManager().removeSubordinate( this.user );
		        	}
		        }

		        // Get what is the security group
		        obj = secGroupField.getValue();
		        if ( obj != null && obj instanceof SecurityGroup ) {
		        	if ( logger.isDebugEnabled()) logger.debug( "Security Group was changed to: " + obj );
		        	this.user.getContext().setSecGroup(( SecurityGroup )obj );
		        } else {
		        	if ( logger.isDebugEnabled()) logger.debug( "Security Group was changed to: NULL" );
		        	this.user.getContext().setSecGroup( null );
		        }

		        user.setLineManager(( Boolean )isLineManager.getValue());
		        user.setProjectManager(( Boolean )isProjectManager.getValue());
	        }

		} else {
			logger.error( "TmsUser is null. Shall be NOT null." );
		}
	}




	private void validateUI() {
		if ( !isNew ) {
//			codeField.setReadOnly( true );
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
					if ( logger.isDebugEnabled()) logger.debug( "Modify User fields are valid!" );
					// View to date
					viewToData();

					// model save
					if ( isNew ) {
						// Add new User
						if ( model.addUser( user ) != null ) {
							// update UI

							// Close Dialog window
							dlg.close();
						} else {
							// Notify about failure
							// Stay in dialog
							Notification.show(
									model.getApp().getResourceStr( "general.errors.add.header" ),
									model.getApp().getResourceStr( "personnel.errors.add.body" ),
									Type.ERROR_MESSAGE
							);
						}
					} else {
						// Save modified TmsUser
						if ( model.updateUser( user ) != null ) {
							// update UI

							// Close Dialog window
							dlg.close();
						} else {
							// Notify about failure
							// Stay in dialog
							Notification.show(
									model.getApp().getResourceStr( "general.errors.update.header" ),
									model.getApp().getResourceStr( "personnel.errors.update.body" ),
									Type.ERROR_MESSAGE
							);
						}
					}
				} else {
					logger.debug( "ModifyStuff fields are NOT valid!" );
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

		if ( !usrnameField.isValid() || !isUniqueUsrname()) {
			if ( logger.isDebugEnabled()) logger.debug( "  usrnameField is INvalid: " + usrnameField.isValid());
			showErrorField( usrnameField );
		} else if ( !codeField.isValid() || !isUniqueUserCode()) {
			if ( logger.isDebugEnabled()) logger.debug( "  codeField is valid: " + codeField.isValid());
			showErrorField( codeField );
		} else if ( !fNameField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  fNameField is valid: " + fNameField.isValid());
			showErrorField( fNameField );
		} else if ( !mNameField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  mNameField is valid: " + mNameField.isValid());
			showErrorField( mNameField );
		} else if ( !lNameField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  lNameField is valid: " + lNameField.isValid());
			showErrorField( lNameField );

		} else if ( !kelaField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  kelaField is valid: " + kelaField.isValid());
			showErrorField( kelaField );
		} else if ( !taxField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  taxField is valid: " + taxField.isValid());
			showErrorField( taxField );
		} else if ( !addressField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  addressField is valid: " + addressField.isValid());
			showErrorField( addressField );
		} else if ( !emailField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  emailField is valid: " + emailField.isValid());
			showErrorField( emailField );
		} else if ( !mobileField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  mobileField is valid: " + mobileField.isValid());
			showErrorField( mobileField );
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
		showErrorField( field, "" );
	}

	private void showErrorField( AbstractField field, String errMsg ) {

		field.setComponentError( new UserError( errMsg ));

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

	private String creatrePassword() {

		String newPwd = null;

		newPwd = TmsAccount.generateNewPassword();

		pwdField.setReadOnly( false );
		pwdField.setValue( newPwd );
		pwdField.setReadOnly( true );

		pwdWasChanged = true;

		return newPwd;

	}

	private void createUsrname() {

		logger.debug( "String to generate usrname: '" + ( String )lNameField.getValue() +  "'" );
		String newUsrname = model.generateUsrname(( String )lNameField.getValue());
    	logger.debug( "Generated Usrname = '" + newUsrname +  "'" );

//			account.setUsrName( newUsrname );
		usrnameField.setReadOnly( false );
		usrnameField.setValue( newUsrname );
		usrnameField.setReadOnly( true );

		usrnameWasChanged = true;

		if ( pwdField.getValue() == null
				||
			 pwdField.getValue() != null && (( String )pwdField.getValue()).length() < 4 ) {

			creatrePassword();

		}

	}

	private boolean isUniqueUsrname() {

		boolean bRes =  true;

		if ( usrnameWasChanged ) {
			bRes =  model.isUniqueUsrname(( String )usrnameField.getValue(), user );

			if ( !bRes ) {
				// Create a notification

				String template = model.getApp().getResourceStr( "notify.personnel.username.exist" );
				Object[] params = { ( String )usrnameField.getValue() };
				template = MessageFormat.format( template, params );


				Notification notif =
						new Notification(
								template,
								Type.ERROR_MESSAGE );

				notif.setPosition( Position.MIDDLE_CENTER );
				notif.setDelayMsec(-1);
				notif.setHtmlContentAllowed( true );

				Notification.show( model.getApp().getResourceStr( "general.errors.update.header" ));
						
			}
		}

		return bRes;
	}

	private void createCode() {

		String newCode = model.generateUserCode();
        logger.debug( "Generated User Code = '" + newCode +  "'" );

		codeField.setValue( newCode );
		codeWasChanged = true;

	}


	private boolean isUniqueUserCode() {

		boolean bRes =  true;

		if ( codeWasChanged ) {
			bRes =  model.isUniqueUserCode(( String )codeField.getValue(), user );

			if ( !bRes ) {
				// Create a notification
				Notification notif =
						new Notification(
								model.getApp().getResourceStr( "personnel.errors.add.body" ),
								Type.ERROR_MESSAGE );

				notif.setPosition( Position.MIDDLE_CENTER );
				notif.setDelayMsec(-1);
				notif.setHtmlContentAllowed( true );

				Notification.show( model.getApp().getResourceStr( "general.errors.update.header" ));
			}
		}

		return bRes;
	}

}
