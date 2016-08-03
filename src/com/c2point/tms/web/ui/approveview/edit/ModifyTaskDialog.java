package com.c2point.tms.web.ui.approveview.edit;

import java.text.MessageFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.CheckInOutRecord;
import com.c2point.tms.entity.ProjectTask;
import com.c2point.tms.entity.TaskReport;
import com.c2point.tms.util.DateUtil;
import com.c2point.tms.util.StringUtils;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.ui.approveview.ModifyTaskIf;
import com.c2point.tms.web.ui.listeners.ProjectTaskChangedListener;
import com.c2point.tms.web.ui.projectsview.P_and_T_Selector;
import com.c2point.tms.web.util.ToStringConverter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class ModifyTaskDialog extends Window implements ValueChangeListener {
	

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( ModifyTaskDialog.class.getName());
	
	private ModifyTaskIf	model;
    private TaskReport		report;
    private boolean			editable;

    private Label		nameField;
    private Label		dateField;
    
    
    private Label 		projectName;
    private Label		taskName;
    
    private TextField	hoursField;
    private TextArea	commentField;
    private Label 		approvalTypeField;
    
    private Label		checkInOutText;	
    
	private Button 		saveButton;
	private Button 		cancelButton;

	private Label		errorNote;


	public ModifyTaskDialog( ModifyTaskIf model, TaskReport report ) {
		this( model, report, true );
	}

	public ModifyTaskDialog( ModifyTaskIf model, TaskReport report, boolean editable ) {
		super();
//		setModal(true);
		
		this.model = model;
		this.report = report;
		this.editable = editable;
		
		initView();
	}
	
	private void initView() {
		setWidth( "40em" );
//		setHeight( null ); //"40em" );
//		setSizeUndefined();
		center();

		this.setCaption( model.getApp().getResourceStr( "approve.edit.task.caption" ));
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin( true );
		layout.setSpacing( true );
//		layout.setSizeFull();

		Component form = getReportView();
		Component errorBar = getErrorBar();
		Component bBar = getBottomBar();

		layout.addComponent( form );
		layout.addComponent( errorBar );
		layout.addComponent( bBar );
		
//		form.setHeight( "26em" );
		form.setWidth( "100%" );
//		bBar.setHeight( "15em" ); 
		bBar.setHeight( (float) (saveButton.getHeight() * 1.25), saveButton.getHeightUnits());
//		bBar.setWidth( "100%" );
		
		layout.setExpandRatio( form, 1 );

//        hoursField.focus();
//        hoursField.selectAll();
		
		this.setContent( layout );

        if ( !editable || !report.getApprovalFlagType().allowToBeChanged()) {

        	// Items Approved & Processed cannot be changed by Employee
            hoursField.setEnabled( false );
            commentField.setEnabled( false );
			
			saveButton.setEnabled( false );
			cancelButton.focus();
			
        }
        
		
	}

	private Component getErrorBar() {
		errorNote = new Label( "" );
		errorNote.setContentMode( ContentMode.HTML );
//		errorNote.setVisible( false );
		
		return errorNote;
	}
	
	private boolean viewToData() {
		boolean wasChanged = false;
		
		if ( report != null ) {

			if ( report.getProjectTask().getId() != (( ProjectTask )projectName.getData()).getId()) {
				report.setProjectTask(( ProjectTask )projectName.getData());
				wasChanged = true;
			}
			
			float newHours = fieldToFloat( hoursField.getValue());
			if ( newHours != report.getHours()) {
				report.setHours( newHours );
				wasChanged = true;
			}
		
	        String newComment = ( String )commentField.getValue();
			if ( report.getComment() == null && newComment != null
	        		||
	        	 report.getComment() != null && newComment == null
	        	 	||
	        	 report.getComment() != null && newComment != null 
	        	 	&& report.getComment().compareTo( newComment ) != 0 ) {

	        	report.setComment( newComment );
				wasChanged = true;
	        }
	        		
			
		
		} else {
			logger.error( "TravelReport is null. Shall be NOT null." );
		}
		
		return wasChanged;
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
			public void buttonClick( ClickEvent event) {

				// Validate. if Validated()
				if ( isValid()) {
					if ( logger.isDebugEnabled()) logger.debug( "Modify User fields are valid!" );
					// Save modified Report
					if ( viewToData()) {
						if ( model.updateTaskReport( report ) != null ) {
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
		
		cancelButton.addClickListener( new ClickListener() {

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

		if ( !hoursField.isValid() ) {
			if ( logger.isDebugEnabled()) logger.debug( "  hoursField is NOT valid!" );
			showErrorField( hoursField );
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
	public void valueChange( ValueChangeEvent event) {

		
	}
	
    private float fieldToFloat( Object value ) {
    	
    	float fl = 0;
    	
    	try { 
    		if ( value == null ) {
    			fl = 0;
    		} else if ( value instanceof String ) {
	    		if ((( String )value ).length() != 0 ) {
		    		fl = Float.parseFloat(( String )value );
	    		}
	    	} else if ( value instanceof Number ) {
	    		fl = (( Number ) value ).floatValue();
	    	} else {
	    		logger.error( "float value has wrong class: " + value.getClass().getName());
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
		
		GridLayout gl = new GridLayout( 6, 2 );
		gl.setSpacing( true );
        gl.setWidth("100%");

		projectName = new Label( "", ContentMode.HTML );
        projectName.setDescription( model.getApp().getResourceStr( "approve.edit.project.tooltip" ));


		Label emptyLabel1 = new Label( "" ); 
		emptyLabel1.setWidth( "4ex" );

        taskName = new Label( "", ContentMode.HTML );
        taskName.setDescription( model.getApp().getResourceStr( "approve.edit.task.tooltip" ));
        
		
		Label emptyLabel2 = new Label( "" ); 
		emptyLabel2.setWidth( "4ex" );
		
		Validator hoursValidator = new RegexpValidator( "([0-9]{0,2}| )(\\.\\d)?$", "?" );

        hoursField = new TextField( "" );
        hoursField.setWidth("4em");
        hoursField.setNullSettingAllowed( true );
        hoursField.setNullRepresentation( "" );
        hoursField.setDescription( model.getApp().getResourceStr( "approve.edit.hours.tooltip" ));
        hoursField.setImmediate(true);
        hoursField.addValidator( hoursValidator );
		
		Label emptyLabel3 = new Label( "" ); 
		emptyLabel3.setWidth( "1ex" );

		Label hoursLabel = new Label( "hours" );

        Button editButton = null;
        if ( editable && report.getApprovalFlagType().allowToBeChanged()) {
        	
        	editButton = new Button( model.getApp().getResourceStr( "general.button.edit" ));
//    		searchButton.addStyleName( Runo.BUTTON_BIG );
        	editButton.addStyleName( Runo.BUTTON_DEFAULT );
    		
    		editButton.addClickListener( new ClickListener() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					changeProjectTask();
				}
			});
        }

        gl.addComponent( projectName, 0, 0, 4, 0 );
		gl.addComponent( emptyLabel1, 0, 1 );
		gl.addComponent( taskName, 1, 1, 1, 1 );
		gl.addComponent( emptyLabel2, 2, 1 );
		gl.addComponent( hoursField, 3, 1 );
		gl.addComponent( emptyLabel3, 4, 1 );
		gl.addComponent( hoursLabel, 5, 1 );
        if ( editButton != null ) {
        	gl.addComponent( editButton, 5, 0, 5, 0 );
        }
        
		gl.setComponentAlignment( taskName, Alignment.BOTTOM_LEFT );
		gl.setComponentAlignment( hoursField, Alignment.BOTTOM_LEFT );
		gl.setComponentAlignment( hoursLabel, Alignment.BOTTOM_LEFT );
		
		gl.setColumnExpandRatio(0, 0);
		gl.setColumnExpandRatio(1, 5);
		gl.setColumnExpandRatio(5, 1);
		
        commentField = new TextArea( model.getApp().getResourceStr( "approve.edit.comment" ));
        commentField.setRows( 4 );
        commentField.setColumns( 25 );
        commentField.setDescription( model.getApp().getResourceStr( "approve.edit.comment.tooltip" ));
        commentField.setImmediate(true);
        
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
        layout.addComponent( gl );
        layout.addComponent( commentField );
        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ));
        layout.addComponent( appVL );

        layout.setExpandRatio( appVL, 1.0f );
        
        layout.addComponent( new Label( "<hr/>", ContentMode.HTML ));
        
        Component comp = getCheckInOutComponent();
        layout.addComponent( comp );
        layout.setExpandRatio( comp, 1.0f );
    
        dataToView();
       
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
	        
        	projectName.setData( report.getProjectTask());
        	showProjectTask();
	        
	        hoursField.setValue( Float.toString( report.getHours()));
	        if ( !editable && !report.getApprovalFlagType().allowToBeChanged()) {
	        	// Items Approved & Processed cannot be changed by Employee
	        	hoursField.setEnabled( false );
	        }
	        
	        commentField.setValue( report.getComment());

	        approvalTypeField.setValue( "<b>" + ToStringConverter.convertToString( model.getApp(), report.getApprovalFlagType()).toUpperCase() + "</b>");

	        // Now add checkInOutText content
	        //   - Get List of CheckInOut records from model
	        //   - Format and all records to checkInOutText
	        List<CheckInOutRecord> list = model.getCheckInOutList( report );
	        updateCheckInOutComponent( list );
	        
	        hoursField.focus();
	        hoursField.selectAll();

		} else {
			logger.error( "Report is null. Shall be NOT null." );
		}
	}

	private boolean changeProjectTask() {
		boolean res = false;
		
		P_and_T_Selector selectorDlg = new P_and_T_Selector( model.getApp(), report.getProjectTask(), SupportedFunctionType.PROJECTS_COMPANY );
			
		selectorDlg.addListener( new ProjectTaskChangedListener() {
			public void wasChanged( ProjectTask pt ) {
				logger.debug( "ProjectTask has been selected: " + pt.getProject().getName() + "." + pt.getTask().getName());
				
	        	projectName.setData( pt );
				
	        	showProjectTask();

	            hoursField.focus();
	            hoursField.selectAll();

			}
		});
		
		UI.getCurrent().addWindow( selectorDlg );
		
		return res;
	}
	
	
	private void showProjectTask() {

		try {
			if ( projectName.getData() instanceof ProjectTask ) {

				ProjectTask pt = ( ProjectTask )projectName.getData();
	        	projectName.setValue( 
	        	          model.getApp().getResourceStr( "general.edit.project" ) + ": "
	        			+ "<b>" 
	        			+ StringUtils.nullToStr( pt.getProject().getName()) 
	        			+ "</b>" 
	        			+ " ( " // + model.getApp().getResourceStr( "general.edit.owner" ) + " " 
	        			+ StringUtils.nullToStr( pt.getProject().getProjectManager().getFirstAndLastNames()) + " )"
	        	);

	        	taskName.setValue( 
	        	          model.getApp().getResourceStr( "general.edit.task" ) + ": "
	        			+ "<b>" 
	        			+ StringUtils.nullToStr( pt.getTask().getName()) 
	        			+ "</b>" );
				
			} else {
				logger.error( "Wrong data has been saven in projectName field: " + projectName.getData().getClass().getName());
			}

			hoursField.focus();
	        hoursField.selectAll();
		
        	
        	
		} catch ( Exception e ) {}
	}

}
