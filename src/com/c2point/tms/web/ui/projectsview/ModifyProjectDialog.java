package com.c2point.tms.web.ui.projectsview;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.GeoCoordinates;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TmsUser;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.web.ui.geo.MapFactory;
import com.c2point.tms.web.ui.geo.MapViewIF;
import com.c2point.tms.web.ui.geo.SupportedMapProviderType;
import com.c2point.tms.web.ui.geo.code.GeoResultParser;
import com.c2point.tms.web.ui.geo.code.GoogleGeoCoder;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ModifyProjectDialog extends Window implements Property.ValueChangeListener {

	private static Logger logger = LogManager.getLogger( ModifyProjectDialog.class.getName());

	private ProjectsModel	model;
    private Project 		project;
    private boolean			isNew;

    private TextField 	codeField;
    private TextField	nameField;

    private ComboBox	pmCombo;

    private DateField 	startPrj;
    private DateField 	endPrjPlan;
    private DateField 	endPrjReal;

    private TextField 	addrField;

    private TextField 	latitudeField;
    private TextField 	longitudeField;
    private Button 	  	calculateButton;
    private Button 	  	previewButton;

	private Button saveButton;
	private Button cancelButton;

	private Label		errorNote;

	public ModifyProjectDialog( ProjectsModel model ) {
		// Edit by default
		this( model, null );
	}

	protected ModifyProjectDialog( ProjectsModel model, Project project ) {

  	super();
		setModal(true);

		this.model = model;
		this.isNew = ( project == null );

		if ( !isNew) {
			this.project = project;
		} else {
			// New project
			// this.project = model.getNewProject();
			this.project = new Project( null, null );
		}

		initView();
	}

	private void initView() {
		setWidth( "36em" );
		setHeight( "36em" );
		center();

		if ( isNew ) {
			this.setCaption( model.getApp().getResourceStr( "projects.add.caption" ));
		} else {
			this.setCaption( model.getApp().getResourceStr( "projects.edit.caption" ));
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

		form.setWidth( "100%" );
		bBar.setHeight(( float )( saveButton.getHeight() * 1.25 ), saveButton.getHeightUnits());

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

		GridLayout layout = new GridLayout( 4, 6 );

		layout.setMargin( new MarginInfo( true, false, false, true ));
		layout.setSpacing( true );

        codeField = new TextField( model.getApp().getResourceStr( "general.edit.code" ) + ":" );
        codeField.setWidth("8em");
        codeField.setNullRepresentation( "" ); //"Enter unique Project Code ...");
        codeField.setDescription( model.getApp().getResourceStr( "projects.edit.code.tooltip" ));
        codeField.setRequired( true );
        codeField.setValidationVisible( true );
        codeField.addValidator( new RegexpValidator(
        								"[0-9]{4,}",
        								model.getApp().getResourceStr( "projects.edit.code.validator" )
        ));
        codeField.setImmediate(true);


        nameField = new TextField( model.getApp().getResourceStr( "general.edit.name" ) + ":" );
        nameField.setWidth("100%");
        nameField.setNullRepresentation( "" ); //"Enter the Name of the Project ...");
        nameField.setDescription( model.getApp().getResourceStr( "projects.edit.name.tooltip" ));
        nameField.setRequired( true );
        nameField.setValidationVisible( true );
        nameField.addValidator( new StringLengthValidator(
									model.getApp().getResourceStr( "projects.edit.name.validator" ),
									4, 200,
									false
        ));
        nameField.setImmediate(true);

    	pmCombo = new ComboBox( model.getApp().getResourceStr( "general.edit.owner" ));
    	pmCombo.setWidth( "100%" );
    	pmCombo.setFilteringMode( Filtering.FILTERINGMODE_CONTAINS );
    	pmCombo.setNewItemsAllowed( false );
    	pmCombo.setNullSelectionAllowed( false );
    	pmCombo.setImmediate( true );
    	pmCombo.setDescription( model.getApp().getResourceStr( "projects.edit.owner.tooltip" ));

        startPrj = new DateField( model.getApp().getResourceStr( "projects.edit.start" ));
        startPrj.setLocale( model.getApp().getSessionData().getLocale());
		startPrj.setWidth("8em");
    	startPrj.setDateFormat((( SimpleDateFormat )DateUtil.getDateFormatYear4digit()).toPattern());
		startPrj.setResolution( DateField.RESOLUTION_DAY );
		startPrj.setImmediate(true);
		startPrj.setDescription( model.getApp().getResourceStr( "projects.edit.start.tooltip" ));

    	endPrjPlan = new DateField( model.getApp().getResourceStr( "projects.edit.end.plan" ));
    	endPrjPlan.setLocale( model.getApp().getSessionData().getLocale());
		endPrjPlan.setWidth("8em");
    	endPrjPlan.setDateFormat((( SimpleDateFormat )DateUtil.getDateFormatYear4digit()).toPattern());
		endPrjPlan.setResolution( DateField.RESOLUTION_DAY );
		endPrjPlan.setImmediate(true);
		endPrjPlan.setDescription( model.getApp().getResourceStr( "projects.edit.end.plan.tooltip" ));

    	endPrjReal = new DateField( model.getApp().getResourceStr( "projects.edit.end.real" ));
    	endPrjReal.setLocale( model.getApp().getSessionData().getLocale());
		endPrjReal.setWidth("8em");
    	endPrjReal.setDateFormat((( SimpleDateFormat )DateUtil.getDateFormatYear4digit()).toPattern());
		endPrjReal.setResolution( DateField.RESOLUTION_DAY );
		endPrjReal.setImmediate(true);
		endPrjReal.setDescription( model.getApp().getResourceStr( "projects.edit.end.real.tooltip" ));



        addrField = new TextField( model.getApp().getResourceStr( "projects.edit.address" ));
        addrField.setWidth("100%");
        addrField.setNullRepresentation( "" ); //"Enter the Address where the Project takes place ...");
        addrField.setImmediate(true);

        latitudeField = new TextField( model.getApp().getResourceStr( "projects.edit.loc.lt" ));
    	latitudeField.setWidth( "100%" );
    	latitudeField.setValidationVisible( true );
    	latitudeField.addValidator( new LatitudeValidator( model.getApp().getResourceStr( "projects.edit.loc.lt.validator" )));
    	latitudeField.setImmediate( true );

        longitudeField = new TextField( model.getApp().getResourceStr( "projects.edit.loc.lg" ));
    	longitudeField.setWidth( "100%" );
    	longitudeField.setValidationVisible( true );
    	longitudeField.addValidator( new LongitudeValidator( model.getApp().getResourceStr( "projects.edit.loc.lg.validator" )));
    	latitudeField.setImmediate( true );

        calculateButton = new Button( model.getApp().getResourceStr( "general.button.get" ));
        calculateButton.setDescription( model.getApp().getResourceStr( "projects.edit.calculate.tooltip" ));

        previewButton = new Button( model.getApp().getResourceStr( "general.button.preview" ));
        previewButton.setDescription( model.getApp().getResourceStr( "projects.edit.preview.tooltip" ));

		calculateButton.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event ) { calculateGeo(); }
		});

		previewButton.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event ) { previewGeo(); }
		});

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent( calculateButton );
        hl.addComponent( previewButton );


        layout.addComponent( codeField, 0, 0 );
        layout.addComponent( nameField, 1, 0, 3, 0 );
        layout.addComponent( pmCombo, 0, 1, 2, 1 );
        layout.addComponent( startPrj, 0, 2 );
        layout.addComponent( endPrjPlan, 1, 2 );
        layout.addComponent( endPrjReal, 2, 2 );

        layout.addComponent( addrField, 0, 3, 3, 3 );

        layout.addComponent( latitudeField, 1, 4, 1, 4 );
        layout.addComponent( longitudeField, 1, 5, 1, 5 );
        layout.addComponent( hl, 2, 4, 3, 4 );

        hl.setComponentAlignment( calculateButton, Alignment.BOTTOM_LEFT );
        hl.setComponentAlignment( calculateButton, Alignment.BOTTOM_LEFT );
        layout.setComponentAlignment( hl, Alignment.BOTTOM_LEFT );

/*
        codeField.addListener( this );
        nameField.addListener( this );
        startPrj.addListener( this );
        endPrjPlan.addListener( this );
    	endPrjReal.addListener( this );
*/
     	addrField.addListener( this );
    	latitudeField.addListener( this );
        longitudeField.addListener( this );

        dataToView();

        validateUI();

		return layout;
	}

	private void dataToView() {

		if ( project != null ) {

	        codeField.setValue( project.getCode());
	        nameField.setValue( project.getName());

	        initPMcombo();

	        startPrj.setValue( project.getStart());
	        endPrjPlan.setValue( project.getEndPlanned());
	    	endPrjReal.setValue( project.getEndReal());

		    addrField.setValue( project.getAddress());
		    if ( project.getGeo() != null ) {
			    if ( project.getGeo().getLatitude() != null ) {
			    	latitudeField.setValue( Double.toString( project.getGeo().getLatitude()));
			    }
			    if ( project.getGeo().getLongitude() != null ) {
				    longitudeField.setValue( Double.toString( project.getGeo().getLongitude()));
			    }
		    }
		} else {
			logger.error( "Project is null. Shall be NOT null." );
		}
	}

	private void viewToData() {
		if ( project != null ) {

	        project.setCode(( String )codeField.getValue());
	        project.setName(( String )nameField.getValue());

	        project.setProjectManager(( TmsUser )pmCombo.getValue());

	        project.setStart(( Date ) startPrj.getValue());
	        project.setEndPlanned(( Date )endPrjPlan.getValue());
	    	project.setEndReal(( Date )endPrjReal.getValue());

		    project.setAddress( (String) addrField.getValue());

	    	try {
	    		project.setGeo( new GeoCoordinates(
	    								geoFieldToDouble( latitudeField.getValue()),
	    								geoFieldToDouble( longitudeField.getValue())
	    		));

	    	} catch ( NumberFormatException e ) {
	    		project.setGeo( new GeoCoordinates());
	    	}
/*
		    if( project.getGeo() != null ) {
			    project.getGeo().setLatitude( latitudeField.getValue());
			    project.getGeo().setLongitude( longitudeField.getValue());
		    } else {
		    	GeoCoordinates geo = new GeoCoordinates();
			    project.getGeo().setLatitude( latitudeField.getValue());
			    project.getGeo().setLongitude( longitudeField.getValue());

		    }
*/

		} else {
			logger.error( "Project is null. Shall be NOT null." );
		}
	}




	private void validateUI() {
		if ( !isNew ) {
			codeField.setReadOnly( true );
		}

		validateGeoUI();
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
					logger.debug( "ModifyProject fields are valid!" );
					// View to date
					viewToData();

					// model save
					if ( isNew ) {
						// Add new Project
						if ( model.addProject( project ) != null ) {
							//§ update UI

							// Close Dialog window
							dlg.close();
						} else {
							// Notify about failure
							// Stay in dialog
							Notification.show(
									model.getApp().getResourceStr( "general.errors.add.header" ),
									model.getApp().getResourceStr( "projects.errors.add.body" ),
									Notification.TYPE_ERROR_MESSAGE
							);
						}
					} else {
						// Save modified Project
						if ( model.updateProject( project ) != null ) {
							// update UI

							// Close Dialog window
							dlg.close();
						} else {
							// Notify about failure
							// Stay in dialog
							Notification.show(
									model.getApp().getResourceStr( "general.errors.update.header" ),
									model.getApp().getResourceStr( "projects.errors.update.body" ),
									Notification.TYPE_ERROR_MESSAGE
							);
						}
					}
				} else {
					logger.debug( "ModifyProject fields are NOT valid!" );
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

		if ( !codeField.isValid() ) {
			if ( logger.isDebugEnabled()) logger.debug( "  codeField is valid: " + codeField.isValid());
			showErrorField( codeField );
		} else if ( !nameField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  nameField is valid: " + nameField.isValid());
			showErrorField( nameField );
		} else if ( !startPrj.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  startPrj is valid: " + startPrj.isValid());
			showErrorField( startPrj );
		} else if ( !endPrjPlan.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  endPrjPlan is valid: " + endPrjPlan.isValid());
			showErrorField( endPrjPlan );
		} else if ( !endPrjReal.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  endPrjReal is valid: " + endPrjReal.isValid());
			showErrorField( endPrjReal );
		} else if ( !addrField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  addrField is valid: " + addrField.isValid());
			showErrorField( addrField );
		} else if ( !latitudeField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  latitudeField is valid: " + latitudeField.isValid());
			showErrorField( latitudeField );
		} else if ( !longitudeField.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  longitudeField is valid: " + longitudeField.isValid());
			showErrorField( longitudeField );
		} else {
			// Can do without "try-catch" because it was validated in previous 2 else-if
			Double dLat = geoFieldToDouble( latitudeField.getValue());
			Double dLong = geoFieldToDouble( longitudeField.getValue());

			// means No Location information!!!
			if ( dLat == null && dLong == null ) {
				clearErrorField();
				bRes = true;
			} else if ( dLat == null ) {
				showErrorField( latitudeField );
			} else if ( dLong == null) {
				showErrorField( longitudeField );
			} else if ( new GeoCoordinates( dLat, dLong ).isValid()) {
				clearErrorField();
				bRes = true;
			} else {
				Notification.show(
						model.getApp().getResourceStr( "general.errors.validation.header" ),
						model.getApp().getResourceStr( "projects.errors.validation.body" ),
						Notification.TYPE_ERROR_MESSAGE
				);
			}


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

		validateGeoUI();

	}

	private class LatLongValidator extends RegexpValidator {

		private int borderAbsValue;

		public LatLongValidator( String errorMessage, int borderAbsValue ) {

			super( "-?[0-9]{1,3}.?[0-9]{0,}", errorMessage );
			this.borderAbsValue = Math.abs( borderAbsValue );

		}


		public boolean isValidValue( String value ) {
           boolean bRes = super.isValidValue( value );

           if ( bRes ) {

        	   Double dbl;// = null;
        	   try {
        		   dbl = geoFieldToDouble( value );
        	   } catch ( Exception e ) {
        		   return false;
        	   }

        	   if ( dbl != null && ( dbl.doubleValue() < -borderAbsValue || dbl.doubleValue() > borderAbsValue )) {
        		   bRes = false;
        	   }
           }

           return bRes;
		}
 /*
		public void validate( Object value ) {
			if ( !isValid( value )) {
				throw new Validator.InvalidValueException( getErrorMessage());
			}
		}
*/
	}
	public class LatitudeValidator extends LatLongValidator {

		public LatitudeValidator( String errorMessage ) {
			super( errorMessage, 90 );
		}
	}
	public class LongitudeValidator extends LatLongValidator {

		public LongitudeValidator( String errorMessage ) {
			super( errorMessage, 180 );
		}
	}

    private void initPMcombo() {

    	// Get people list
    	List<TmsUser> list = model.getProjectManagers();

    	if ( model.getToShowFilter() == SupportedFunctionType.PROJECTS_COMPANY ) {
	    	if ( list != null ) {
		    	// Fill Combo
		    	for ( TmsUser user : list ) {
		    		pmCombo.addItem( user );
		    		pmCombo.setItemCaption( user, user.getFirstAndLastNames());
		    		if ( project.getProjectManager() != null &&
			    		 user.getCode().compareToIgnoreCase( project.getProjectManager().getCode()) == 0 ) {
			    	    	pmCombo.setValue( user );
			    	}
		    	}
	    	}
    	} else {
    		TmsUser user = ( isNew ? model.getSessionOwner() : project.getProjectManager());

    		pmCombo.addItem( user );
    		pmCombo.setItemCaption( user, user.getFirstAndLastNames());
	    	pmCombo.setValue( user );
    		pmCombo.setReadOnly( true );
    	}

    }


    private Double geoFieldToDouble( Object value ) {

    	Double dbl = null;

    	try {
	    	if ( value instanceof Double ) {
	    		dbl = ( Double )value;
	    	} else if ( value instanceof String ) {
	    		if ((( String )value ).length() != 0 ) {
		    		dbl = Double.valueOf(( String )value );
	    		}
	    	} else {
	    		logger.error( "GEO value has wrong class: " + value.getClass().getName());
	    		throw new NumberFormatException( "Wrong class passed!" );
	    	}
    	} catch( NullPointerException e ) {
    		// In case null has been passed for conversion. Error but handled here. If happened than check how this happened
    		logger.debug( "Null had been passed but handled" );
    		dbl = null;
    	} catch( Exception e ) {
    		throw new NumberFormatException( "Wrong value passed!" );
	    }

    	return dbl;
    }


	private void calculateGeo() {

		logger.debug( "GEO determination starts. Address: " + addrField.getValue());

		try {
			String[] result = new GeoResultParser().parseGeoCoderResult(
					new GoogleGeoCoder().getLocation(( String )addrField.getValue()));

			logger.debug( "Latitude: " + result[0] + ", Longitude: " + result[1]);

//			latitudeField.setValue( Double.parseDouble( result[0] ));
//			longitudeField.setValue( Double.parseDouble( result[1] ));
			latitudeField.setValue( result[0] != null ? result[0] : "" );
			longitudeField.setValue( result[1] != null ? result[1] : "" );

		} catch ( Exception e ) {
			logger.error( "Failed to determine location \n" + e );
		}
	}

	private void previewGeo() {

		MapViewIF mapView = MapFactory.getMapView( SupportedMapProviderType.GOOGLE_PROVIDER );

		logger.debug( "Before Preview ( Lattd, Longtd ): " + latitudeField.getValue() + ", " + longitudeField.getValue() );

		mapView.setCaption( model.getApp().getResourceStr( "presence.map.window.header" ));

		mapView.showMap( UI.getCurrent(),
							new GeoCoordinates(
								geoFieldToDouble( latitudeField.getValue()),
								geoFieldToDouble( longitudeField.getValue())
						),
						"Workplace"
		);

	}


	private void validateGeoUI() {

		boolean previewTrue = false;
		boolean calcTrue = false;

		try {
			Double lattd = geoFieldToDouble( latitudeField.getValue());
			Double longtd = geoFieldToDouble( longitudeField.getValue());
			if ( lattd != null && longtd != null ) {
				previewTrue = true;
				previewButton.setEnabled( true );
			}
		} catch ( Exception e ) {
		}
		previewButton.setEnabled( previewTrue );

		if ( addrField != null && addrField.getValue() != null && (( String )addrField.getValue()).length() > 0 ) {
			calcTrue = true;
		}
		calculateButton.setEnabled( calcTrue );
	}

}
