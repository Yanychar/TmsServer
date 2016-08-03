package com.c2point.tms.web.ui.approveview.edit;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.Project;
import com.c2point.tms.entity.TravelReport;
import com.c2point.tms.entity.TravelType;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.StringUtils;
import com.c2point.tms.web.ui.approveview.ModifyTravelIf;
import com.c2point.tms.web.ui.listeners.ProjectChangedListener;
import com.c2point.tms.web.ui.projectsview.P_and_T_Selector;
import com.c2point.tms.web.util.ToStringConverter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class ModifyTravelDialog extends Window implements ValueChangeListener {
	
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( ModifyTravelDialog.class.getName());
	
	private ModifyTravelIf	model;
    private TravelReport	report;
    private boolean			editable;

    private Label		nameField;
    private Label		dateField;

    private Label 		projectName;
    
    private ComboBox	travelTypeField;
    
    private TextField 	startTrip;
    private TextField 	endTrip;
    
    private TextField	distance;
    private ComboBox	distanceSelection;
    
    private TextArea	routeAndComment;
    private Label 		approvalTypeField;
    
    private Label		checkInOutText;	
    
	private Button 		saveButton;
	private Button 		cancelButton;

	private Label		errorNote;
	
	public ModifyTravelDialog( ModifyTravelIf model, TravelReport report ) {
		this( model, report, true );
	}

	public ModifyTravelDialog( ModifyTravelIf model, TravelReport report, boolean editable ) {
		super();
//		setModal(true);
		
		this.model = model;
		this.report = report;
		this.editable = editable;
		
		initView();
	}
	
	private void initView() {
		setCaption( model.getApp().getResourceStr( "approve.edit.travel.caption" ));
		
		setWidth( "36em" );
		center();
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin( true );
		layout.setSpacing( true );
		
		setContent( layout );

		Component form = getReportView();
		Component errorBar = getErrorBar();
		Component bBar = getBottomBar();

		layout.addComponent( form );
		layout.addComponent( errorBar );
		layout.addComponent( bBar );
		
		layout.setExpandRatio( errorBar, 1 );
	
        if ( !editable || !report.getApprovalFlagType().allowToBeChanged()) {
        	// Items Approved & Processed cannot be changed by Employee
        	travelTypeField.setEnabled( false );
			startTrip.setEnabled( false );
			endTrip.setEnabled( false );
			distance.setEnabled( false );
			distanceSelection.setEnabled( false );
		    routeAndComment.setEnabled( false );
			
			saveButton.setEnabled( false );

			cancelButton.focus();
			
        }

		
	}

	private Component getErrorBar() {

		errorNote = new Label();
		errorNote.setContentMode( ContentMode.HTML );
		
		return errorNote;
	}

    private Component getCheckInOutComponent() {

    	VerticalLayout panel = new VerticalLayout();
		panel.addStyleName( Runo.PANEL_LIGHT );
		panel.setWidth( "100%" );
		panel.setHeight( "20ex" );
		
		
    	checkInOutText = new Label( "", ContentMode.HTML );
		checkInOutText.setWidth("100%");

		panel.addComponent( checkInOutText );
		
		return checkInOutText;
    }
    private void updateCheckInOutComponent( List<CheckInOutRecord> list ) {
    	String txt = "";
        if ( list != null && list.size() > 0 ) {
        	
    		txt = "<table border=\"0\">"
 					+ "<tr>"
						+ "<th>{0}</th>"
						+ "<th>{1}</th>"
						+ "<th>{2}</th>"
					+ "</tr>";

    		Object[] params = { 
    				model.getApp().getResourceStr( "approve.edit.checkinout.text.checkin" ),
    				model.getApp().getResourceStr( "approve.edit.checkinout.text.checkout" ),
    				model.getApp().getResourceStr( "approve.edit.checkinout.text.project" )
    		};
    		txt = MessageFormat.format( txt, params );
    		
        	for ( CheckInOutRecord record : list ) {

        		txt += 
           			  "<tr>"
	 	    			+ "<td>" + DateUtil.timeToString( record.getDateCheckedIn()) + "</td>"
		    			+ "<td>" + DateUtil.timeToString( record.getDateCheckedOut()) + "</td>"
		    			+ "<td>" + record.getProject().getCode() + "   " + record.getProject().getName() + "</td>"
	    			+ "</tr>";
	        				
        	}
        	
        	txt += "</table>";
        	
        } else {
        	// Add Note about 'No information!"
    		txt = model.getApp().getResourceStr( "approve.edit.checkinout.text.notfound" );    				
        }
    	checkInOutText.setValue( txt );
    }

	private Component getReportView() {
		
		VerticalLayout layout = new VerticalLayout();
		
		layout.setMargin( new MarginInfo( true, false, false, true ));
		layout.setSpacing( true );
		
        nameField = new Label( "", ContentMode.HTML );
        nameField.setDescription( model.getApp().getResourceStr( "approve.edit.name.tooltip" ));
        nameField.setImmediate(true);
        
        dateField = new Label( "", ContentMode.HTML );
        dateField.setWidth("6em");
        dateField.setDescription( model.getApp().getResourceStr( "approve.edit.date.tooltip" ));
        dateField.setImmediate(true);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth( "100%" );
		hl.setSpacing( true );
		hl.addComponent( nameField );
		hl.addComponent( dateField );
		hl.setComponentAlignment( dateField, Alignment.BOTTOM_RIGHT );

		projectName = new Label( "", ContentMode.HTML );
        projectName.setDescription( model.getApp().getResourceStr( "approve.edit.project.tooltip" ));

        HorizontalLayout hl_prj = new HorizontalLayout();
        hl_prj.setWidth( "100%" );
        hl_prj.setSpacing( true );

    	hl_prj.addComponent( projectName );
    	hl_prj.setExpandRatio( projectName, 1.0f );
        Button editButton = null;
        if ( editable && report.getApprovalFlagType().allowToBeChanged()) {
        	
        	editButton = new Button( model.getApp().getResourceStr( "general.button.edit" ));
        	editButton.addStyleName( Runo.BUTTON_DEFAULT );
    		
        	hl_prj.addComponent( editButton );
        	hl_prj.setComponentAlignment( editButton, Alignment.BOTTOM_RIGHT );

    		editButton.addClickListener( new ClickListener() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					changeProject();
				}
			});
        }

        
        travelTypeField = new ComboBox( model.getApp().getResourceStr( "approve.edit.traveltype" ));
        travelTypeField.setWidth( "16em" );
        travelTypeField.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
        travelTypeField.setFilteringMode( FilteringMode.STARTSWITH );
        travelTypeField.setImmediate( true );        
        travelTypeField.setNullSelectionAllowed( false );

		Validator timeValidator = new RegexpValidator( "([0-1][0-9]|[2][0-3]):([0-5][0-9])", "?" );

		HorizontalLayout hl_2 = new HorizontalLayout();
		hl_2.setSpacing( true );
		
		startTrip = new TextField( model.getApp().getResourceStr( "approve.edit.start" ));
		startTrip.setWidth("4em");
		startTrip.setImmediate(true);
		startTrip.setDescription( model.getApp().getResourceStr( "approve.edit.start.tooltip" ));
		startTrip.setNullSettingAllowed( false );
		startTrip.setNullRepresentation( "00:00" );
		startTrip.addValidator( timeValidator );
        
    	endTrip = new TextField( model.getApp().getResourceStr( "approve.edit.end" ));
		endTrip.setWidth("4em");
		endTrip.setImmediate(true);
		endTrip.setDescription( model.getApp().getResourceStr( "approve.edit.end.tooltip" ));
		endTrip.setNullSettingAllowed( false );
		endTrip.setNullRepresentation( "00:00" );
		endTrip.addValidator( timeValidator );
        
		Validator distanceValidator = new RegexpValidator( "\\d{1,3}", "?" );
        distance = new TextField( model.getApp().getResourceStr( "approve.edit.length" ));
        distance.setWidth("4em");
        distance.setImmediate(true);
        distance.setDescription( model.getApp().getResourceStr( "approve.edit.length.tooltip" ));
        distance.addValidator( distanceValidator );

        distanceSelection = new ComboBox( model.getApp().getResourceStr( "approve.edit.length" ));
        distanceSelection.setWidth( "100%" );
        distanceSelection.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
        distanceSelection.setFilteringMode( FilteringMode.CONTAINS );
        distanceSelection.setImmediate( true );        
        distanceSelection.setNullSelectionAllowed( false );
        
        routeAndComment = new TextArea( model.getApp().getResourceStr( "approve.edit.route" ));
        routeAndComment.setWordwrap( true );
        routeAndComment.setRows( 4 );
        routeAndComment.setWidth( "100%" );
        routeAndComment.setImmediate( true );
        routeAndComment.setDescription( model.getApp().getResourceStr( "approve.edit.route.tooltip" ));
        
        Label approvalTypeLabel = new Label( model.getApp().getResourceStr( "approve.edit.status" ));
        approvalTypeField = new Label( "", ContentMode.HTML );

        VerticalLayout appVL = new VerticalLayout();
        appVL.setWidth("100%");
        appVL.addComponent( approvalTypeLabel );
        appVL.addComponent( approvalTypeField );
        appVL.setComponentAlignment( approvalTypeLabel, Alignment.BOTTOM_LEFT );
        appVL.setComponentAlignment( approvalTypeField, Alignment.BOTTOM_LEFT );
        
        layout.addComponent( hl );
        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ));
        layout.addComponent( hl_prj );
        layout.addComponent( travelTypeField );
        hl_2.addComponent( startTrip );
        hl_2.addComponent( endTrip );

        layout.addComponent( hl_2 );
        layout.addComponent( distance );
        layout.addComponent( distanceSelection );
        layout.addComponent( routeAndComment );
        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ));
        layout.addComponent( appVL );

        layout.setExpandRatio( appVL, 1.0f );

        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ));
        
        Component comp = getCheckInOutComponent();
        layout.addComponent( comp );
        layout.setExpandRatio( comp, 1.0f );
    
        dataToView();
       
        validateUI();

		return layout;
	}
	
	private void dataToView() {
		
		if ( report != null ) {
			
	        try {
	        	nameField.setValue( "<b><u>" + report.getUser().getFirstAndLastNames() + "</u></b>" );
	        } catch ( Exception e ) {}
	        
	        try {
	        	dateField.setValue( "<b>" + StringUtils.nullToStr( DateUtil.dateToString( report.getDate())) + "</b>" );
			} catch ( Exception e ) {}
	        
        	projectName.setData( report.getProject());
	        showProject();	        
	        
	        setupTravelTypeCombo();
			travelTypeField.setValue( report.getTravelType());
	        
			startTrip.setValue( DateUtil.timeToString( report.getStartDate()));
			endTrip.setValue( DateUtil.timeToString( report.getEndDate()));
			
			distance.setValue( Integer.toString( report.getDistance()));
			
	        setupDistanceCombo();
			distanceSelection.setValue( Integer.valueOf( caltulateDistanceComboItemId( report.getDistance())));
			
			routeAndComment.setValue( report.getRoute());
			
	        approvalTypeField.setValue( "<b>" + ToStringConverter.convertToString( model.getApp(), report.getApprovalFlagType()).toUpperCase() + "</b>");

	        // Now add checkInOutText content
	        //   - Get List of CheckInOut records from model
	        //   - Format and all records to checkInOutText
	        List<CheckInOutRecord> list = model.getCheckInOutList( report );
	        updateCheckInOutComponent( list );
	        
		} else {
			logger.error( "TmsUser is null. Shall be NOT null." );
		}
	}
	
	private int caltulateDistanceComboItemId( int distance ) {
		int resp = 0;
		
		if ( distance <= 5 ) {
			resp = 5;
		} else if ( distance > 5 && distance <= 10 ) {
			resp = 10;
		} else if ( distance > 10 && distance <= 20 ) {
			resp = 20;
		} else if ( distance > 20 && distance <= 30 ) {
			resp = 30;
		} else if ( distance > 30 && distance <= 40 ) {
			resp = 40;
		} else if ( distance > 40 && distance <= 50 ) {
			resp = 50;
		} else if ( distance > 50 && distance <= 60 ) {
			resp = 60;
		} else if ( distance > 60 && distance <= 70 ) {
			resp = 70;
		} else if ( distance > 70 && distance <= 80 ) {
			resp = 80;
		} else if ( distance > 80 && distance <= 90 ) {
			resp = 90;
		} else if ( distance > 90 && distance <= 100 ) {
			resp = 100;
		} else if ( distance > 100 ) {
			resp = 110;
		}

		if ( logger.isDebugEnabled()) logger.debug( "Id determined as " + resp );
		
		return resp;
	}
	
	private void setupTravelTypeCombo() {

		// Add an item with a generated ID
		travelTypeField.addItem( TravelType.HOME );
		travelTypeField.setItemCaption( TravelType.HOME, model.getApp().getResourceStr( "approve.edit.traveltype.home" ));
		travelTypeField.addItem( TravelType.WORK );
		travelTypeField.setItemCaption( TravelType.WORK, model.getApp().getResourceStr( "approve.edit.traveltype.work" ));
		
		travelTypeField.addValueChangeListener( new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
		        validateUI();
			}
			
		});
		
	}

	private void setupDistanceCombo() {

		String km = model.getApp().getResourceStr( "approve.edit.length.km" );
		
		distanceSelection.addItem( 0 );
		distanceSelection.setItemCaption( 0, "0 " + km );
		distanceSelection.addItem( 5 );
		distanceSelection.setItemCaption( 5, "0 ... 5 " + km );
		distanceSelection.addItem( 10 );
		distanceSelection.setItemCaption( 10, "6 ... 10 " + km );

		int DELTA = 10;
		for ( int i = 20; i <= 100; i = i + DELTA ) {

			distanceSelection.addItem( i );
			distanceSelection.setItemCaption( i,  ( i - DELTA + 1 ) + " ... " + i + " " + km );
		
		}

		distanceSelection.addItem( 110 );
		distanceSelection.setItemCaption( 110, "101 ...  " + km );
		
	}

	private boolean viewToData() {
		boolean wasChanged = false;
		
		if ( report != null ) {

			if ( report.getProject().getId() != (( Project )projectName.getData()).getId()) {
				report.setProject(( Project )projectName.getData());
				wasChanged = true;
			}
			
			if ( travelTypeField.getValue() != report.getTravelType() ) {
				report.setTravelType((TravelType) travelTypeField.getValue());
				wasChanged = true;
			}

			if ( report.getTravelType() == TravelType.WORK ) {
				if ( areDifferentTimeValueAndTimeString( report.getStartDate(), ( String )startTrip.getValue())) {
					try {
						report.setStartDate( DateUtil.stringToTime( report.getDate(), ( String ) startTrip.getValue()));
						wasChanged = true;
					} catch (ParseException e) {
						logger.error( "Failed to convert Start String to Time: " + startTrip.getValue());
						logger.error( e );
					}
				}
					
				if ( areDifferentTimeValueAndTimeString( report.getEndDate(), ( String )endTrip.getValue())) {
					try {
						report.setEndDate( DateUtil.stringToTime( report.getDate(), ( String ) endTrip.getValue()));
						wasChanged = true;
					} catch (ParseException e) {
						logger.error( "Failed to convert End String to Time: " + endTrip.getValue());
						logger.error( e );
					}
				}

				int newDistance = fieldToInteger( distance.getValue());
				if ( newDistance != report.getDistance()) {
					report.setDistance( newDistance );
					wasChanged = true;
				}
			
			} else {   // HOME trip
				if ( report.getStartDate() != null ) {
					report.setStartDate( null );
					wasChanged = true;
				}
				if ( report.getEndDate() != null ) {
					report.setEndDate( null );
					wasChanged = true;
				}
				
				if (( Integer )distanceSelection.getValue() != report.getDistance()) {
					report.setDistance(( Integer )distanceSelection.getValue());
					wasChanged = true;
				}
			}

				
			
			if ( report.getRoute() != null && report.getRoute().compareTo(( String )routeAndComment.getValue()) != 0 ) {
				report.setRoute(( String )routeAndComment.getValue());
				wasChanged = true;
			}
			
		
		} else {
			logger.error( "TravelReport is null. Shall be NOT null." );
		}
		
		return wasChanged;
	}
	
	
	
	
	private void validateUI() {
		if ( travelTypeField.getValue() == TravelType.HOME ) {
	        startTrip.setVisible( false );
	        endTrip.setVisible( false );
	        distance.setVisible( false );
	        distanceSelection.setVisible( true );
	        routeAndComment.setVisible( true );
		} else if ( travelTypeField.getValue() == TravelType.WORK ) {
	        startTrip.setVisible( true );
	        endTrip.setVisible( true );
	        distance.setVisible( true );
	        distanceSelection.setVisible( false );
	        routeAndComment.setVisible( true );
		} else {
	        startTrip.setVisible( false );
	        endTrip.setVisible( false );
	        distance.setVisible( false );
	        distanceSelection.setVisible( false );
	        routeAndComment.setVisible( false );
		}
		
	
	}
	

	private Component getBottomBar() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin( true );
		layout.setSpacing( true );

		saveButton = new Button( model.getApp().getResourceStr( "general.button.ok" ));
		cancelButton = new Button( model.getApp().getResourceStr( "general.button.cancel" ));
		
		layout.addComponent( saveButton );
		layout.addComponent( cancelButton );
		
		layout.setComponentAlignment( saveButton, Alignment.MIDDLE_LEFT );
		layout.setComponentAlignment( cancelButton, Alignment.MIDDLE_LEFT );
		
		
		saveButton.addClickListener( new ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				// Validate. if Validated()
				if ( isValid()) {
					if ( logger.isDebugEnabled()) logger.debug( "Modify User fields are valid!" );
					// Save modified Report
					if ( viewToData()) {
						if ( model.updateTravelReport( report ) != null ) {
							// update UI
							
							// Close Dialog window
							close();
						} else {
							// Notify about failure
							// Stay in dialog
							Notification.show(
									model.getApp().getResourceStr( "general.errors.update.header" ),
									model.getApp().getResourceStr( "approve.errors.update.body" ),
									Notification.Type.ERROR_MESSAGE
							);
						}
					} else {
						// Close Dialog window
						close();
					}
				} else {
					logger.debug( "ModifyProject fields are NOT valid!" );
				}
				
			}
			
		});
		
		cancelButton.addClickListener( new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				close();
			}
			
		});
		
		
		
		return layout;
	}
	
	private boolean isValid() {
		boolean bRes = false;

		if ( travelTypeField.getValue() == null || ( TravelType ) travelTypeField.getValue() == TravelType.UNKNOWN ) {
			if ( logger.isDebugEnabled()) logger.debug( "  travelTypeField is NOT valid and == " + travelTypeField.getValue());
			showErrorField( travelTypeField );
		} else if ( startTrip.isVisible() && !startTrip.isValid() ) {
			if ( logger.isDebugEnabled()) logger.debug( "  startTrip is NOT valid: " + startTrip.isValid());
			showErrorField( startTrip );
		} else if ( endTrip.isVisible() && !endTrip.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  endTrip is NOT valid: " + endTrip.isValid());
			showErrorField( endTrip );
		} else if ( distance.isVisible() && !distance.isValid()) {
			if ( logger.isDebugEnabled()) logger.debug( "  distance is NOT valid: " + distance.isValid());
			showErrorField( distance );
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

	private void showErrorField( AbstractField<?> field ) {

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
	
    private int fieldToInteger( Object value ) {
    	
    	int fl = 0;
    	
    	try { 
    		if ( value instanceof String ) {
	    		if ((( String )value ).length() != 0 ) {
		    		fl = Integer.parseInt(( String )value );
	    		}
	    	} else if ( value instanceof Number ) {
	    		fl = (( Number ) value ).intValue();
	    	} else {
	    		logger.error( "int value has wrong class: " + value.getClass().getName());
	    		throw new NumberFormatException( "Wrong class passed!" );
	    	}
    	} catch( NullPointerException e ) {
    		// In case null has been passed for conversion. Error but handled here. If happened than check how this happened
    		logger.error( "Null had been passed but handled" );
    		fl = 0;
    	} catch( Exception e ) {
    		throw new NumberFormatException( "Wrong value passed!" );
	    }
    	
    	return fl;
    }

    
    private boolean areDifferentTimeValueAndTimeString( Date tm_d_1, String tm_str_2 ) {
    	if ( tm_d_1 == null && tm_str_2 == null 
    			||
    		 tm_d_1 != null && tm_str_2 != null &&
    		 DateUtil.timeToString( tm_d_1 ).compareToIgnoreCase( tm_str_2 ) == 0 ) {
    		return false;
    	}
    	
    	return true;
    }

	private boolean changeProject() {
		boolean res = false;
		
		P_and_T_Selector selectorDlg = new P_and_T_Selector( model.getApp(), report.getProject(), SupportedFunctionType.PROJECTS_COMPANY );
			
		selectorDlg.addListener( new ProjectChangedListener() {
			public void wasChanged( Project p ) {
				logger.debug( "Project has been selected: " + p.getName());
				
	        	projectName.setData( p );
				
	        	showProject();

			}

		});
		
		UI.getCurrent().addWindow( selectorDlg );
		
		return res;
	}
	
	private void showProject() {

		try {
			if ( projectName.getData() instanceof Project ) {

				Project p = ( Project )projectName.getData();
	        	projectName.setValue( 
	        	          model.getApp().getResourceStr( "general.edit.project" ) + ": "
	        			+ "<b>" 
	        			+ StringUtils.nullToStr( p.getName()) 
	        			+ "</b>" 
	        			+ " ( " // + model.getApp().getResourceStr( "general.edit.owner" ) + " " 
	        			+ StringUtils.nullToStr( p.getProjectManager().getFirstAndLastNames()) + " )"
	        	);

			} else {
				logger.error( "Wrong data has been saven in projectName field: " + projectName.getData().getClass().getName());
			}

        	
		} catch ( Exception e ) {}
	}

    
}
