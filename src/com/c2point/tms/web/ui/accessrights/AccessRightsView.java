package com.c2point.tms.web.ui.accessrights;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tms.datalayer.OrganisationFacade;
import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.access.SecurityGroup;
import com.c2point.tms.entity.access.SupportedFunctionType;
import com.c2point.tms.web.ui.listeners.OrganisationChangedListener;
import com.c2point.tms.web.ui.listeners.SelectionChangedListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class AccessRightsView extends Panel implements SelectionChangedListener, OrganisationChangedListener,
														ValueChangeListener {

	private static Logger logger = LogManager.getLogger( AccessRightsView.class.getName());
	
	private AccessRightsModel 	model;
	
	private ComboBox 			orgSelector;
	private ComboBox 			groupSelector;
	private AccessRightsGrid	grid;
	
	private Button 				applyButton; 
	private Button 				restoreButton;
	
	public AccessRightsView( AccessRightsModel model ) {
		super();
		
		this.model = model;
		
		initUI();

	}

	public void initUI() {
	
		this.setCaption( model.getApp().getResourceStr( "access.panel.caption" ));
	
//		table = new AccessRightsTable( model );
		grid = new AccessRightsGrid( model );
		

		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing( true );
		hl.setImmediate( true );
		applyButton = new Button( model.getApp().getResourceStr( "general.button.apply" )); 
		applyButton.setImmediate( true );
		applyButton.addClickListener( new ClickListener() {
			@Override
			public void buttonClick( ClickEvent event) {
				logger.debug( "Save has been pressed to save Access Rights." );
				model.saveGroup();
				updateButtons();
			}
		});
		
		restoreButton = new Button( model.getApp().getResourceStr( "general.button.restore" ));
		restoreButton.setImmediate( true );
		restoreButton.addClickListener( new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				restoreSettings();
			}
		});
		
		
		Label glue = new Label( "" );
		glue.setWidth("100%");
		
		hl.addComponent( applyButton );
		hl.addComponent( restoreButton );
		hl.addComponent( glue );
		hl.setExpandRatio( glue, 1.0f );
		updateButtons();
		
		model.addChangedListener(( SelectionChangedListener )this );
		model.addChangedListener(( OrganisationChangedListener )this );
		
		VerticalLayout vl = new VerticalLayout();
		vl.addComponent( getOrganisationSelector());
		vl.addComponent( getSecGroupPanel());
		vl.addComponent( grid );
		vl.addComponent( hl );
		
		this.setContent( vl );
	}
	
	private HorizontalLayout getSecGroupPanel() {

		HorizontalLayout hl = new HorizontalLayout(); 
		Label glue = new Label( "" );
		glue.setWidth("100%");
		
		hl.setSpacing( true );
		
		hl.addComponent( getSecGroupSelector() );
		hl.addComponent( glue );
		hl.setExpandRatio( glue, 1.0f );
		
		return hl;
	}

	private ComboBox getSecGroupSelector() {
		groupSelector  = new ComboBox( model.getApp().getResourceStr( "general.edit.group" ));
		
		groupSelector.setWidth( "16em" );
		groupSelector.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		groupSelector.setFilteringMode( FilteringMode.STARTSWITH );
		groupSelector.setImmediate( true );        
		groupSelector.setNullSelectionAllowed( false );
		
		
		
		for ( SecurityGroup group : model.getGroups().values()) {
			if ( group != null ) {
				groupSelector.addItem( group );
				groupSelector.setItemCaption( group, group.getDefName());
			}
		}
		
//		groupSelector.addListener( model );
		groupSelector.addValueChangeListener( this );
		
		if ( groupSelector.size() > 0 ) {
			groupSelector.setValue( groupSelector.getItemIds().iterator().next());
		}
		
		
		return groupSelector;
	}

	
	private ComboBox getOrganisationSelector() {
		orgSelector  = new ComboBox( model.getApp().getResourceStr( "general.edit.company" ) + ":" );
		
		orgSelector.setWidth( "16em" );
		orgSelector.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		orgSelector.setFilteringMode( FilteringMode.STARTSWITH );
		orgSelector.setImmediate( true );        
		orgSelector.setNullSelectionAllowed( false );
		
		if ( model.isSingleOrg()) {

			Organisation org = model.getSelectedOrganisation();
			orgSelector.addItem( org );
			orgSelector.setItemCaption( org, org.getCode() + "  " + org.getName());

			orgSelector.setValue( org );
			
			orgSelector.setReadOnly( true );
			
		} else {

			for ( Organisation org : OrganisationFacade.getInstance().getOrganisations()) {
				if ( org != null && !org.isDeleted()) {
					orgSelector.addItem( org );
					orgSelector.setItemCaption( org, org.getCode() + "  " + org.getName());
				}
			}

			if ( orgSelector.size() > 0 ) {
				orgSelector.setValue( model.getSelectedOrganisation());
			}
			
		}
		
		
		if ( model.getSecurityContext().isRead( SupportedFunctionType.ACCESS_RIGHTS_TMS ) 
				&& 
			!model.getSecurityContext().isWrite( SupportedFunctionType.ACCESS_RIGHTS_TMS )) {
//				orgSelector.setReadOnly( true );
		}
		
		orgSelector.addValueChangeListener( this );
		
		return orgSelector;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.c2point.tms.web.ui.listeners.SelectionChangedListener#selectionChanged()
	 * 
	 * Handler of ComboBox (AccessRight selection). AccessRightsGrid issues event that activate this handler
	 */
	@Override
	public void selectionChanged() {
		updateButtons();
	}

	/*
	 *  Enable/Disable buttons depending on model edit status
	 */
	private void updateButtons() {
		applyButton.setEnabled( model.isUpdated()); 
		restoreButton.setEnabled( model.isUpdated());
		
		logger.debug( "Buttons shall be updated. Enabled: " + model.isUpdated() );
	}
	
	/*
	 *   restoreButton handler. Shall restore ARs from DB
	 */
	private void restoreSettings() {
		model.initModel();
//		Object tmpObj = groupSelector.getValue();
		//		groupSelector.select( groupSelector.getNullSelectionItemId());
		//groupSelector.select( tmpObj );
		model.selectGroup( 	model.getSelectedGroup());
		
		model.clearUpdated();
		updateButtons();
		logger.debug( "Access Rights were restored for Security Group: " + model.getSelectedGroup().getDefName());

	}

	@Override
	public void valueChange( final ValueChangeEvent event ) {
		if ( model.isUpdated()) {
			ConfirmDialog.show( this.getUI(), 
					model.getApp().getResourceStr( "confirm.general.header" ), 
					model.getApp().getResourceStr( "confirm.access.savechanges" ), 
					model.getApp().getResourceStr( "general.button.ok" ), 
					model.getApp().getResourceStr( "general.button.cancel" ), 
					new ConfirmDialog.Listener() {

						@Override
						public void onClose( ConfirmDialog dialog ) {
							if ( dialog.isConfirmed()) {                    
								// Confirmed to continue
								logger.debug( "Confirm.OK has been pressed to save Access Rights." );
								model.saveGroup();
								updateButtons();
							} else {
								logger.debug( "Confirm.Cancel has been pressed to drop latest changes." );
								restoreSettings();
							}

							changedModelAfterComboChanges( event );
						}
	
			});
		} else {
			changedModelAfterComboChanges( event );
		}
		
	}
	
	private void changedModelAfterComboChanges( ValueChangeEvent event ) {
		if ( event.getProperty().getValue() instanceof SecurityGroup ) {

			if ( logger.isDebugEnabled()) logger.debug( "SecurityGroup changed event" );
			
			model.groupWasChanged(( SecurityGroup )event.getProperty().getValue());
			
		} else if ( event.getProperty().getValue() instanceof Organisation ) {
			
			if ( logger.isDebugEnabled()) logger.debug( "Organisation changed event" );
			
			model.organisationWasChanged(( Organisation )event.getProperty().getValue());
			
		} else {
			logger.error( "Wrong object was passed here: " + event.getProperty().getValue().getClass().getName());
		}
	}

	@Override
	public void wasChanged( Organisation organisation ) {
		// TODO Auto-generated method stub
		
	}
	
}
